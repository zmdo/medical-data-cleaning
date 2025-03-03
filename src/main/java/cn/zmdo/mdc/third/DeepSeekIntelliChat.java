package cn.zmdo.mdc.third;

import cn.zmdo.mdc.core.IntelliChat;
import cn.zmdo.mdc.third.model.component.ChatMessage;
import cn.zmdo.mdc.third.model.constant.ChatRoleEnum;
import cn.zmdo.mdc.third.model.request.ChatCompletionRequest;
import cn.zmdo.mdc.third.model.response.ChatCompletionResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.message.Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
public class DeepSeekIntelliChat implements IntelliChat {

    private String systemPrompt;

    private String model;

    private final DeepSeekService deepSeekService;

    public DeepSeekIntelliChat(String url,String key,String model) {
        this.deepSeekService = new DeepSeekService(url,key);
        this.model = model;
    }

    @Override
    public String jsonCompletions(String content) {
        return completions(content,true,false);
    }

    @Override
    public String completions(String content,boolean exportReasoningContent) {
        return completions(content,false,exportReasoningContent);
    }

    @Override
    public String getSystemPrompt() {
        return systemPrompt;
    }


    /**
     * AI 对话补全
     *
     * @param content                 输入内容
     * @param outputJson              是否输出为 json 格式
     * @param exportReasoningContent  是否输出深度思考内容
     * @return 补全的内容
     */
    public String completions(String content,boolean outputJson,boolean exportReasoningContent) {

        // 构建聊天列表
        List<ChatMessage> chatMessageList = new ArrayList<>();
        if (systemPrompt != null && !systemPrompt.isEmpty()) {
            ChatMessage systemChatMessage = new ChatMessage(ChatRoleEnum.SYSTEM,systemPrompt);
            chatMessageList.add(systemChatMessage);
        }
        ChatMessage userChatMessage = new ChatMessage(ChatRoleEnum.USER,content);
        chatMessageList.add(userChatMessage);

        // 构建请求
        ChatCompletionRequest request = new ChatCompletionRequest();
        request.setModel(model);
        request.setMessages(chatMessageList);
        if (outputJson) {
            request.setResponseFormat(ChatCompletionRequest.ResponseFormat.JSON_RESPONSE_FORMAT);
        }

        try {
            // 请求
            ChatCompletionResponse response = deepSeekService.chatCompletion(request);

            // 获取 AI 回应的消息
            ChatMessage responseMessage = response.getChoices().get(0).getMessage();

            String aiContent = responseMessage.getContent();

            // 组装消息
            if (!outputJson && exportReasoningContent) {
                String aiReasoningContent = responseMessage.getReasoningContent();
                return String.format("<think>\n%s\n</think>\n%s",aiReasoningContent,aiContent);
            }
            return aiContent;

        } catch (Exception e) {
            log.error("Error in DeepSeekIntelliChat",e);
            return null;
        }

    }
}
