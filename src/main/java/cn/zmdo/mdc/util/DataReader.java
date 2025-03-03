package cn.zmdo.mdc.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据读取器
 */
@Slf4j
public class DataReader {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 根据数据存放路径读取数据列表。
     *
     * @param dataPath 项目目录下 {@code /data/} 的原始数据路径
     * @return 数据对象列表
     */
    public static <T> List<T> readList(String dataPath,Class<T> clazz) {

        List<T> data;
        if (dataPath.startsWith("line:") || dataPath.toLowerCase().endsWith(".jsonl")) {

            String realDataPath;
            if (dataPath.startsWith("line:")) {
                realDataPath = dataPath.substring(5);
            } else {
                realDataPath = dataPath;
            }

            // 如果是按行读取
            String path = System.getProperty("user.dir") + "/data/" + realDataPath;

            data = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(new File(path)))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.add(objectMapper.readValue(line,clazz));
                }
            } catch (IOException e) {
                log.error("读取{}时发生错误！",dataPath);
                log.error(e.getMessage());
                return null;
            }
        } else {
            String path = System.getProperty("user.dir") + "/data/" + dataPath;

            try {
                data = objectMapper.readValue(new File(path), new TypeReference<List<T>>() {
                    @Override
                    public Type getType() {
                        return new ObjectListType(clazz);
                    }
                });
            } catch (IOException e) {
                log.error("读取{}时发生错误！",dataPath);
                log.error(e.getMessage());
                return null;
            }
        }

        return data;
    }

    /**
     * List 泛型类型
     */
    static class ObjectListType implements ParameterizedType {

        private final Type rawType = List.class;
        private final Type[] typeArguments;

        public ObjectListType(Type objectType) {
            this.typeArguments = new Type[]{objectType};
        }

        @Override
        public Type[] getActualTypeArguments() {
            return typeArguments;
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return null;
        }
    }

}
