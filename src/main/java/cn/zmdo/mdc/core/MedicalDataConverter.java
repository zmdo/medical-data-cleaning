package cn.zmdo.mdc.core;

import cn.zmdo.mdc.model.standard.StandardTrainData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 医疗数据转换器
 *
 * @param <T> 原始医疗数据类型
 */
@Slf4j
public abstract class MedicalDataConverter<T> extends AbstractStandardDataConverter<T, StandardTrainData> {

    protected final Type _originalType;
    public MedicalDataConverter() {
        Type superClass = this.getClass().getGenericSuperclass();
        if (superClass instanceof Class) {
            throw new IllegalArgumentException("Internal error: MedicalDataConverter constructed without actual type information");
        } else {
            this._originalType = ((ParameterizedType)superClass).getActualTypeArguments()[0];
        }
    }

    @Override
    public Type originalType() {
        return _originalType;
    }

    @Override
    public Type convertedType() {
        return StandardTrainData.class;
    }

}
