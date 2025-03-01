package cn.zmdo.mdc;

import cn.zmdo.mdc.core.Config;
import cn.zmdo.mdc.core.DataCleaningWorkFlow;

public class Main {

    public static void main(String[] args) throws Exception {

        DataCleaningWorkFlow dataCleaningWorkFlow = new DataCleaningWorkFlow();

        Config config = new Config();

        // 阿里 DeepSeek-R1 API
//        config.setAiChatUrl("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
//        config.setAiChatKey("sk-821094c075ed451088b874da1487f748");
//        config.setAiChatModel("deepseek-r1");

        // 官方 DeepSeek-R1 API
        config.setAiChatUrl("https://api.deepseek.com/chat/completions");
        config.setAiChatKey("sk-9992f172aa934aaab9b4d11aa6363337");
        config.setAiChatModel("deepseek-reasoner");

        config.setThreads(3);

        dataCleaningWorkFlow.start(config);

    }

}