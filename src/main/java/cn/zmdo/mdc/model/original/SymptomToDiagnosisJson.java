package cn.zmdo.mdc.model.original;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SymptomToDiagnosisJson {

    /**
     * 病情描述
     */
    @JsonProperty("input_text")
    private String inputText;

    /**
     * 诊断信息
     */
    @JsonProperty("output_text")
    private String outputText;

}
