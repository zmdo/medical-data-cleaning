package cn.zmdo.mdc.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonExtractHelper {

    public static final ObjectMapper objectMapper = new ObjectMapper();

    public static JsonNode extract(String json,String field) throws JsonProcessingException {

        // 解析 JSON 为 JsonNode
        JsonNode rootNode = objectMapper.readTree(json);

        // 提取字段
        return rootNode.get(field);

    }

}
