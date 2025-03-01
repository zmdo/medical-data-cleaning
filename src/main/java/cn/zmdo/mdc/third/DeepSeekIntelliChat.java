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
    public String completions(String content,boolean exportReasoningContent) {

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

        try {
            // 请求
            ChatCompletionResponse response = deepSeekService.chatCompletion(request);

            // 获取 AI 回应的消息
            ChatMessage responseMessage = response.getChoices().get(0).getMessage();
            String aiContent = responseMessage.getContent();
            String aiReasoningContent = responseMessage.getReasoningContent();;

            // 组装消息
            if (exportReasoningContent) {
                return String.format("<think>\n%s\n</think>\n%s",aiReasoningContent,aiContent);
            }
            return aiContent;
        } catch (Exception e) {
            log.error("Error in DeepSeekIntelliChat",e);
            return null;
        }

    }

    @Override
    public String getSystemPrompt() {
        return systemPrompt;
    }

}
