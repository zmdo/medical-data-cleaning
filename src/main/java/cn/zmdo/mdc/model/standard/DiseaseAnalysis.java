package cn.zmdo.mdc.model.standard;

import lombok.Data;

import java.util.Map;

@Data
public class DiseaseAnalysis {

    /**
     * 描述
     */
    private String description;

    /**
     * 分数
     */
    private Double score;

}
