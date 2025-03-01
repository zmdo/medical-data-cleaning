package cn.zmdo.mdc;

import cn.zmdo.mdc.core.Config;
import cn.zmdo.mdc.core.DataCleaningWorkFlow;

public class Main {

    public static void main(String[] args) throws Exception {

        DataCleaningWorkFlow dataCleaningWorkFlow = new DataCleaningWorkFlow();

        Config config = new Config();

        config.setAiChatUrl("https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions");
        config.setAiChatKey("sk-821094c075ed451088b874da1487f748");
        config.setAiChatModel("deepseek-r1");
        config.setThreads(3);

        dataCleaningWorkFlow.start(config);

    }

}