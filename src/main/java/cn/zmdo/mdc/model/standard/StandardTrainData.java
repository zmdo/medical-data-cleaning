package cn.zmdo.mdc.model.standard;

import lombok.Data;

import java.util.List;

/**
 * 标准训练数据
 */
@Data
public class StandardTrainData {

    /**
     * 病人情况
     */
    private PatientCondition condition;

    /**
     * 疾病列表
     */
    private List<String> diseases;

    /**
     * 病情分析
     */
    private List<DiseaseAnalysis> analyses;

    /**
     * 诊断结果
     */
    private List<Diagnosis> diagnoses;

}
