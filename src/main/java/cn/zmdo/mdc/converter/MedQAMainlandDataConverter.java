package cn.zmdo.mdc.converter;

import cn.zmdo.mdc.core.Converter;
import cn.zmdo.mdc.core.IntelliChat;
import cn.zmdo.mdc.core.MedicalDataConverter;
import cn.zmdo.mdc.model.original.MedQAJson;
import cn.zmdo.mdc.model.standard.Diagnosis;
import cn.zmdo.mdc.model.standard.DiseaseAnalysis;
import cn.zmdo.mdc.model.standard.PatientCondition;
import cn.zmdo.mdc.model.standard.StandardTrainData;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;

@Converter(
        dataId = "medqa-mainland",
        prompts = {
                "MedQA/medqa-mainland-description.prompt",
                "MedQA/medqa-mainland-analysis.prompt",
                "MedQA/medqa-mainland-diagnosis.prompt"
        },
        sources = {
                "line:MedQA/questions/Mainland/dev.jsonl",
                "line:MedQA/questions/Mainland/test.jsonl",
                "line:MedQA/questions/Mainland/train.jsonl"
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
        standardTrainData.setDiseases(List.of(disease));

        // +-----------------+
        //    转换病历信息
        // +-----------------+

        String patientConditionJson = intelliChats.get(0).completions(
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
                disease
        );

        // 通过 AI 分析具体诊断步骤和评分
        String analysesText = intelliChats.get(1).completions(reportPrompt);
        List<DiseaseAnalysis> analyses = objectMapper.readValue(
                analysesText, new TypeReference<>() {}
        );
        standardTrainData.setAnalyses(analyses);

        // +-----------------+
        //    转换诊断结果
        // +-----------------+

        String diagnosesText = intelliChats.get(2).completions(reportPrompt);
        List<Diagnosis> diagnoses = objectMapper.readValue(
                diagnosesText, new TypeReference<>() {}
        );
        standardTrainData.setDiagnoses(diagnoses);

        return standardTrainData;
    }

}
