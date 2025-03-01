package cn.zmdo.mdc.core;

import cn.zmdo.mdc.model.standard.StandardTrainData;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 医疗数据转换器
 *
 * @param <T> 原始医疗数据类型
 */
@Slf4j
public abstract class MedicalDataConverter<T> implements StandardDataConverter<T, StandardTrainData> {

    protected int maxTryCount = 3;
    protected String tempSavePath = System.getProperty("user.dir") + "/temp/";

    protected final Type _originalType;

    protected ObjectMapper objectMapper = new ObjectMapper();

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

    @Override
    public List<StandardTrainData> convert(List<T> list, List<IntelliChat> intelliChats) {
        List<StandardTrainData> convertedDataList = new ArrayList<>();
        int tryCount;
        boolean success;
        for (T data : list) {

            tryCount = 0;

            // 会尝试进行数据转换，如果数据转换错误超过 maxTryCount 次，就跳过本次转换
            do {
                try {
                    convertedDataList.add(convert(data,intelliChats));
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                tryCount ++;
            } while (!success && tryCount < maxTryCount);

            // 每次转换后都会保存数据
            if (success) {

                try {
                    save(convertedDataList);
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("保存失败：{}",e.getMessage());
                }

            }
        }
        return convertedDataList;
    }

    /**
     * 保存数据
     *
     * @param data 待保存的数据
     */
    protected void save(List<StandardTrainData> data) throws IOException {

        Converter converter = getClass().getAnnotation(Converter.class);
        if (converter == null) {
            throw new IllegalArgumentException("Internal error: MedicalDataConverter constructed without actual type information");
        }

        String savePath = tempSavePath + converter.dataId() + ".json";
        objectMapper.writeValue(new File(savePath),data);

    }

}
