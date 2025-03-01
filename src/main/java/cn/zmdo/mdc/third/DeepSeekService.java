package cn.zmdo.mdc.third;

import cn.zmdo.mdc.third.model.request.ChatCompletionRequest;
import cn.zmdo.mdc.third.model.response.ChatCompletionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class DeepSeekService {

    private final String url;

    private final String key;

    private final RestTemplate restTemplate = new RestTemplate();

    public DeepSeekService(String url,String key) {
        this.url = url;
        this.key = key;
    }

    /**
     * 对话补全
     *
     * @param request 对话请求
     * @return 对话补全对象
     */
    public ChatCompletionResponse chatCompletion(ChatCompletionRequest request) {

        // (1) 设置密钥
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("Bearer %s",key));

        // (2) 创建HttpEntity对象，包含headers和body（如果有）
        HttpEntity<ChatCompletionRequest> entity = new HttpEntity<>(request, headers);

        // (3) 请求服务
        return restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                ChatCompletionResponse.class
        ).getBody();

    }

}
