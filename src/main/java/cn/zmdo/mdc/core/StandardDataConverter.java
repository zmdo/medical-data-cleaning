package cn.zmdo.mdc.core;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 标准数据转换器
 *
 * @param <A> 转换前的原始类型
 * @param <B> 转换后的类型
 */
public interface StandardDataConverter<A,B> {

    /**
     * 将原始的数据转换为标准数据
     *
     * @param originalData 原始数据对象
     * @param intelliChats 人工智能对话补全器列表
     * @return 标准数据对象
     */
    B convert(A originalData, List<IntelliChat> intelliChats) throws Exception;

    /**
     * 将原始的数据列表转换为标准的数据列表
     *
     * @param list         原始数据列表
     * @param intelliChats 人工智能对话补全器列表
     * @return 标准数据列表
     */
    default List<B> convert(List<A> list, List<IntelliChat> intelliChats) {
        List<B> result = new ArrayList<>();
        for (A item : list) {
            try {
                result.add(convert(item,intelliChats));
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        return result;
    }

    /**
     * 转换前的原始类型
     *
     * @return 转换前的原始类型
     */
    Type originalType();

    /**
     * 转换后的类型
     *
     * @return 转换后的类型
     */
    Type convertedType();


}
