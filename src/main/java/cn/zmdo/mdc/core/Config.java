package cn.zmdo.mdc.core;

import lombok.Data;

@Data
public class Config {

    public static final String DEFAULT_PACKAGE_NAME = "cn.zmdo.mdc.converter";

    public static final boolean DEFAULT_MERGE_SAVE = true;

    /**
     * AI 对话 URL
     */
    private String aiChatUrl;

    /**
     * AI 对话 Key
     */
    private String aiChatKey;

    /**
     * AI 对话模型
     */
    private String aiChatModel;

    /**
     * 转换器对应的包名，默认为 {@link cn.zmdo.mdc.converter}
     * @see cn.zmdo.mdc.core.StandardDataConverter
     */
    private String scanPackage = DEFAULT_PACKAGE_NAME;

    /**
     * 是否将扫描的记过合并存储，默认为 {@code true}
     */
    private boolean mergeSave = DEFAULT_MERGE_SAVE;

    /**
     * 执行线程数，默认为 {@code 1}
     */
    private int threads = 1;

}
