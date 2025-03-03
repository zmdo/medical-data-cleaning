package cn.zmdo.mdc.core;

/**
 * AI 对话
 */
public interface IntelliChat {

    /**
     * 获取系统提示词
     *
     * @return 系统提示此
     */
    String getSystemPrompt();

    /**
     * json 补全，注意此情况下不能使用思考模式
     *
     * @param content 输入的内容
     * @return json 数据
     */
    String jsonCompletions(String content);

    /**
     * AI 对话补全
     *
     * @param content                输入的内容
     * @param exportReasoningContent 是否输出深度思考内容，深度思考内容会用 {@code <think></think>} 标签包裹
     * @return AI 会话补全结果
     */
    String completions(String content,boolean exportReasoningContent);

    /**
     * AI 对话补全，且不返回深度思考内容
     *
     * @param content 输入的内容
     * @return AI 会话补全结果
     */
    default String completions(String content) {
        return completions(content, false);
    }

}
