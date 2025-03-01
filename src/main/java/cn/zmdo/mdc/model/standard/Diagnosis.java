package cn.zmdo.mdc.model.standard;

import lombok.Data;

@Data
public class Diagnosis {

    /**
     * 原因
     */
    private String reason;

    /**
     * 结论
     */
    private String conclusion;

}
