package cn.zmdo.mdc.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 转换器注解
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Converter {

    /**
     * 数据唯一 ID
     *
     * @return 数据唯一ID
     */
    String dataId();

    /**
     * 系统提示词文件路径，如果为空则不加载系统提示词
     *
     * @return 系统提示词文件的路径
     * @see cn.zmdo.mdc.core.IntelliChat
     */
    String[] prompts() default {};

    /**
     * 数据源
     *
     * @return 数据源列表
     */
    String[] sources();

    /**
     * 转换器是否可用
     *
     * @return 返回 {@code true} 表示转换器可用，反之表示转换器不可用
     */
    boolean enable() default true;

}
