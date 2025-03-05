package com.zmdo.mdc.tools;

import cn.zmdo.mdc.model.standard.Diagnosis;
import cn.zmdo.mdc.model.standard.DiseaseAnalysis;
import cn.zmdo.mdc.model.standard.PatientCondition;
import cn.zmdo.mdc.model.standard.StandardTrainData;
import cn.zmdo.mdc.third.DeepSeekIntelliChat;
import cn.zmdo.mdc.util.JsonExtractHelper;
import cn.zmdo.mdc.util.PromptReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataMergingTool {

    private ObjectMapper objectMapper = new ObjectMapper();

    private DeepSeekIntelliChat deepSeekIntelliChat = new DeepSeekIntelliChat(
            "https://api.deepseek.com/chat/completions",
            "sk-9992f172aa934aaab9b4d11aa6363337",
            // "deepseek-reasoner"
            "deepseek-chat"
    );

    @Test
    public void merge() throws IOException {

        String cmbPath = System.getProperty("user.dir") + "/out/std-cmb-clin-qa.json";
        List<StandardTrainData> cmbList = objectMapper.readValue(
                new File(cmbPath),
                new TypeReference<>() {}
        );
        System.out.println("CMB: " + cmbList.size());

        String medqaPath = System.getProperty("user.dir") + "/out/medqa-mainland.json";
        List<StandardTrainData> medqaList = objectMapper.readValue(
                new File(medqaPath),
                new TypeReference<>() {}
        );
        System.out.println("MedQA: " + medqaList.size());

        List<StandardTrainData> allList = new ArrayList<>();
        allList.addAll(cmbList);
        allList.addAll(medqaList);
        System.out.println("ALL: " + allList.size());

        objectMapper.writeValue(
                new File(System.getProperty("user.dir") + "/out/all.json"),
                allList
        );

    }

    @Test
    public void check() throws Exception {
        String allDataPath = System.getProperty("user.dir") + "/out/all.json";

        List<StandardTrainData> list = objectMapper.readValue(
                new File(allDataPath),
                new TypeReference<>() {}
        );

        splitDiseases(list);
    }

    @Test
    public void fix() throws IOException {

        String dataPath = System.getProperty("user.dir") + "/out/medqa-mainland.json";
        String newDataPath = System.getProperty("user.dir") + "/out/medqa-mainland-new.json";

        List<StandardTrainData> list = objectMapper.readValue(
                new File(dataPath),
                new TypeReference<>() {}
        );

        List<StandardTrainData> newList = splitDiseases(list);

        objectMapper.writeValue(
                new File(newDataPath), newList
        );
    }

    private List<StandardTrainData> splitDiseases(List<StandardTrainData> list) throws JsonProcessingException {
        int i = 0;
        List<StandardTrainData> newList = new ArrayList<>();
        for (StandardTrainData data : list) {
            boolean flag = false;
            List<String> diseases = new ArrayList<>();
            for (String disease : data.getDiseases()) {
                if (disease.contains("，") || disease.contains(",")) {
                    diseases.addAll(Arrays.asList(disease.split("[,，]")));
                    flag = true;
                } else {
                    diseases.add(disease);
                }
            }
            if (flag) {
                i ++;
                newList.add(regenerate(data,diseases));
            } else {
                newList.add(data);
            }
        }
        System.out.println(i);
        return newList;
    }

    private StandardTrainData regenerate(StandardTrainData data,List<String> diseases) throws JsonProcessingException {

        StandardTrainData newData = new StandardTrainData();

        PatientCondition patientCondition = data.getCondition();
        newData.setCondition(patientCondition);
        newData.setDiseases(diseases);

        // +-----------------+
        //    转换病情分析
        // +-----------------+

        String diseasesText = String.join(",", diseases);

        // 将患者报告和患者的疾病包装为一个字符串
        String reportPrompt = String.format(
                "<report>\n%s\n</report>\n<disease>\n%s\n</disease>",
                patientCondition.report(),
                diseasesText
        );

        // 通过 AI 分析具体诊断步骤和评分
        deepSeekIntelliChat.setSystemPrompt(
                PromptReader.readPrompt("MedQA/medqa-mainland-analysis.prompt")
        );
        String analysesJson = deepSeekIntelliChat.jsonCompletions(reportPrompt);
        List<DiseaseAnalysis> analyses = objectMapper.convertValue(
                JsonExtractHelper.extract(analysesJson,"analysis_steps"),
                new TypeReference<>() {}
        );
        newData.setAnalyses(analyses);

        // +-----------------+
        //    转换诊断结果
        // +-----------------+

        deepSeekIntelliChat.setSystemPrompt(
                PromptReader.readPrompt("CMB/cmb-clin-qa-diagnosis.prompt")
        );
        String diagnosesJson = deepSeekIntelliChat.jsonCompletions(reportPrompt);
        List<Diagnosis> diagnoses = objectMapper.convertValue(
                JsonExtractHelper.extract(diagnosesJson,"diagnoses"),
                new TypeReference<>() {}
        );
        newData.setDiagnoses(diagnoses);

        return newData;
    }

}
