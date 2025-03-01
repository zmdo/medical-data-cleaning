package cn.zmdo.mdc.model.original;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class CMBClinQAJson {

    /**
     * ID
     */
    private Long id;

    /**
     * 题目
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 问答对
     */
    @JsonProperty("QA_pairs")
    private List<QAPair> QAPairs;

    @Data
    public static class QAPair {

        /**
         * 问题
         */
        private String question;

        /**
         * 答案
         */
        private String answer;

    }

}
