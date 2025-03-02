package cn.zmdo.mdc.model.original;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class MedQAJson {

    /**
     * 问题
     */
    private String question;

    /**
     * 选项
     */
    private Map<String,String> options;

    /**
     * 正确答案内容
     */
    private String answer;

    /**
     * 元数据信息
     */
    @JsonProperty("meta_info")
    private String metaInfo;

    /**
     * 正确选项
     */
    @JsonProperty("answer_idx")
    private String answerIdx;

}
