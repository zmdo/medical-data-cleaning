package cn.zmdo.mdc.converter;

import cn.zmdo.mdc.core.IntelliChat;
import cn.zmdo.mdc.core.Converter;
import cn.zmdo.mdc.core.MedicalDataConverter;
import cn.zmdo.mdc.model.original.CMBClinQAJson;
import cn.zmdo.mdc.model.standard.Diagnosis;
import cn.zmdo.mdc.model.standard.DiseaseAnalysis;
import cn.zmdo.mdc.model.standard.PatientCondition;
import cn.zmdo.mdc.model.standard.StandardTrainData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Arrays;
import java.util.List;

@Converter(
        dataId = "std-cmb-clin-qa",
        chatModel = "deepseek-reasoner", // 由于数据量少，并且该数据具有参考性，所以采用深度思考模式
        prompts = {
                "CMB/cmb-clin-qa-description.prompt",
                "CMB/cmb-clin-qa-disease.prompt",
                "CMB/cmb-clin-qa-analysis.prompt",
                "CMB/cmb-clin-qa-diagnosis.prompt",
        },
        sources = "CMB/CMB-Clin/CMB-Clin-qa.json"
)
public class CMBClinQADataConverter extends MedicalDataConverter<CMBClinQAJson> {

    /**
     * 将 CMB-Clin-QA 数据转换为标准数据
     *
     * @param originalData 原始的 CMB-Clin-QA 数据对象
     * @param intelliChats 人工智能对话补全器
     * @return 标准数据
     */
    @Override
    public StandardTrainData convert(CMBClinQAJson originalData, List<IntelliChat> intelliChats) throws Exception {

        // +---------------+
        //     数据准备
        // +---------------+

        StandardTrainData standardTrainData = new StandardTrainData();
        String originalJson = null;
        try {
            originalJson = objectMapper.writeValueAsString(originalData);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // +---------------+
        //   转换病情描述
        // +---------------+

        // 获取病情描述信息
        String description = originalData.getDescription();

        // AI 自动转换用户病情描述
        String patientConditionJson = intelliChats.get(0).completions(description);
        PatientCondition patientCondition =
                objectMapper.readValue(patientConditionJson, PatientCondition.class);

        standardTrainData.setCondition(patientCondition);

        // +---------------+
        //    转换疾病名
        // +---------------+

        String diseasesText = intelliChats.get(1).completions(originalJson);
        String[] diseases = diseasesText.split(",");
        standardTrainData.setDiseases(Arrays.asList(diseases));

        // +---------------+
        //   转换病情分析
        // +---------------+

        // 将患者报告和患者的疾病包装为一个字符串
        String reportPrompt = String.format(
                "<report>\n%s\n</report>\n<disease>\n%s\n</disease>",
                patientCondition.report(),
                diseasesText
        );

        // 通过 AI 分析具体诊断步骤和评分
        String analysesJson = intelliChats.get(2).completions(reportPrompt);
        List<DiseaseAnalysis> analyses = objectMapper.readValue(
                analysesJson, new TypeReference<>() {}
        );
        standardTrainData.setAnalyses(analyses);

        // +---------------+
        //   转换诊断结果
        // +---------------+

        String diagnosesJson = intelliChats.get(3).completions(originalJson);
        List<Diagnosis> diagnoses = objectMapper.readValue(
                diagnosesJson, new TypeReference<>() {}
        );
        standardTrainData.setDiagnoses(diagnoses);

        return standardTrainData;
    }

}