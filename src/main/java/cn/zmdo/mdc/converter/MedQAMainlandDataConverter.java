package cn.zmdo.mdc.converter;

import cn.zmdo.mdc.core.Converter;
import cn.zmdo.mdc.core.IntelliChat;
import cn.zmdo.mdc.core.MedicalDataConverter;
import cn.zmdo.mdc.model.original.MedQAJson;
import cn.zmdo.mdc.model.standard.Diagnosis;
import cn.zmdo.mdc.model.standard.DiseaseAnalysis;
import cn.zmdo.mdc.model.standard.PatientCondition;
import cn.zmdo.mdc.model.standard.StandardTrainData;
import cn.zmdo.mdc.util.JsonExtractHelper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;

@Converter(
        dataId = "medqa-mainland",
        chatModel = "deepseek-chat", // 由于数据很多，所以这里不采用深度思考模式
        prompts = {
                "MedQA/medqa-mainland-description.prompt",
                "MedQA/medqa-mainland-analysis.prompt",
                "MedQA/medqa-mainland-diagnosis.prompt"
        },
        sources = {
                "MedQA/questions/Mainland/dev.jsonl",
                "MedQA/questions/Mainland/test.jsonl",
                "MedQA/questions/Mainland/train.jsonl"
        }
)
public class MedQAMainlandDataConverter extends MedicalDataConverter<MedQAJson> {

    /**
     * 将 MedQA 中文数据转换为标准数据
     *
     * @param originalData MedQA 数据对象
     * @param intelliChats 人工智能对话补全器列表
     * @return 标准数据对象
     */
    @Override
    public StandardTrainData convert(MedQAJson originalData, List<IntelliChat> intelliChats) throws Exception {

        // 判断问题是否为诊断问题
        String question = originalData.getQuestion();
        if (!question.contains("最可能的诊断是（　　）。")) {
            return null;
        }

        StandardTrainData standardTrainData = new StandardTrainData();

        // 直接设置疾病名
        String disease = originalData.getAnswer();
        String[] diseases = disease.split("[,，]");
        standardTrainData.setDiseases(Arrays.asList(diseases));

        // +-----------------+
        //    转换病历信息
        // +-----------------+

        String patientConditionJson = intelliChats.get(0).jsonCompletions(
                question.replace("最可能的诊断是（　　）。","病历介绍完毕。")
        );
        PatientCondition patientCondition =
                objectMapper.readValue(patientConditionJson, PatientCondition.class);

        standardTrainData.setCondition(patientCondition);

        // +-----------------+
        //    转换病情分析
        // +-----------------+

        // 将患者报告和患者的疾病包装为一个字符串
        String reportPrompt = String.format(
                "<report>\n%s\n</report>\n<disease>\n%s\n</disease>",
                patientCondition.report(),
                disease.replaceAll("，",",")
        );

        // 通过 AI 分析具体诊断步骤和评分
        String analysesJson = intelliChats.get(1).jsonCompletions(reportPrompt);
        List<DiseaseAnalysis> analyses = objectMapper.convertValue(
                JsonExtractHelper.extract(analysesJson,"analysis_steps"),
                new TypeReference<>() {}
        );
        standardTrainData.setAnalyses(analyses);

        // +-----------------+
        //    转换诊断结果
        // +-----------------+

        String diagnosesJson = intelliChats.get(2).jsonCompletions(reportPrompt);
        List<Diagnosis> diagnoses = objectMapper.convertValue(
                JsonExtractHelper.extract(diagnosesJson,"diagnoses"),
                new TypeReference<>() {}
        );
        standardTrainData.setDiagnoses(diagnoses);

        return standardTrainData;
    }

}
