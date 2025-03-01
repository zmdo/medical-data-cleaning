package cn.zmdo.mdc.third.model.config;

import lombok.Data;

@Data
public class AuthenticationConfig {

    /**
     * 组织ID
     */
    private String organizationId;

    /**
     * 密钥
     */
    private String key;

}
