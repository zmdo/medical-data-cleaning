package cn.zmdo.mdc.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class AbstractStandardDataConverter<A,B> implements StandardDataConverter<A,B>{

    /**
     * 转换失败最大尝试次数
     */
    @Setter
    @Getter
    protected int maxTryCount = 3;

    /**
     * 临时数据存储地址
     */
    @Setter
    @Getter
    protected String tempSavePath;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    public AbstractStandardDataConverter() {

        // 获取 dataId
        Converter converter = getClass().getAnnotation(Converter.class);
        if (converter == null) {
            throw new IllegalArgumentException("Internal error: MedicalDataConverter constructed without actual type information");
        }

        // 获取当前时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");

        // 构建临时文件存储地址
        String filename = String.format("%s-%s.json",converter.dataId(),simpleDateFormat.format(new Date()));
        tempSavePath = System.getProperty("user.dir") + "/temp/" + filename;

    }

    @Override
    public List<B> convert(List<A> list, List<IntelliChat> intelliChats) {
        List<B> convertedDataList = new ArrayList<>();
        int tryCount;
        boolean success;
        for (A data : list) {

            tryCount = 0;
            B b = null;

            // 会尝试进行数据转换，如果数据转换错误超过 maxTryCount 次，就跳过本次转换
            do {
                try {
                    b = convert(data,intelliChats);
                    if (b != null) {
                        convertedDataList.add(b);
                    }
                    success = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    success = false;
                }
                tryCount ++;
            } while (!success && tryCount < maxTryCount);

            // 每次转换后都会保存数据
            if (b != null && success) {

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
     * 保存临时数据
     *
     * @param data 待保存的数据
     */
    protected void save(List<B> data) throws IOException {
        objectMapper.writeValue(new File(tempSavePath),data);
    }
}
