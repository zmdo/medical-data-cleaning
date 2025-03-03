package com.zmdo.mdc.third;

import cn.zmdo.mdc.model.original.CMBClinQAJson;
import cn.zmdo.mdc.model.standard.PatientCondition;
import cn.zmdo.mdc.third.DeepSeekIntelliChat;
import cn.zmdo.mdc.util.DataReader;
import cn.zmdo.mdc.util.PromptReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

public class DeepSeekIntelliChatTest {

    private final ObjectMapper mapper = new ObjectMapper();
    private DeepSeekIntelliChat deepSeekIntelliChat = new DeepSeekIntelliChat(
            "https://api.deepseek.com/chat/completions",
            "sk-9992f172aa934aaab9b4d11aa6363337",
            // "deepseek-reasoner"
            "deepseek-chat"
    );

    @Test
    public void testChat() throws JsonProcessingException {

        var list = DataReader.readList("CMB/CMB-Clin/CMB-Clin-qa.json", CMBClinQAJson.class);

        String systemPrompt = PromptReader.readPrompt("CMB/cmb-clin-qa-description.prompt");
        deepSeekIntelliChat.setSystemPrompt(systemPrompt);

        String inputData = list.get(5).getDescription();

        String res = deepSeekIntelliChat.completions(
                inputData,false
        );

        PatientCondition condition = mapper.readValue(res, PatientCondition.class);

        System.out.println(condition.report());
    }

    @Test
    public void testChat2() throws JsonProcessingException {

        String systemPrompt = PromptReader.readPrompt("CMB/cmb-clin-qa-disease.prompt");
        deepSeekIntelliChat.setSystemPrompt(systemPrompt);

        var list = DataReader.readList("CMB/CMB-Clin/CMB-Clin-qa.json", CMBClinQAJson.class);
        String inputData = mapper.writeValueAsString(list.get(5));
        String res = deepSeekIntelliChat.completions(
                inputData,
                true
        );
        System.out.println(res);
    }

    @Test
    public void testChat3() throws JsonProcessingException {

        String systemPrompt = PromptReader.readPrompt("CMB/cmb-clin-qa-analysis.prompt");
        deepSeekIntelliChat.setSystemPrompt(systemPrompt);

        String inputData = String.format("<report>%s</report>\n<disease>%s</disease>",
                "性别：男\n" +
                "年龄：3\n" +
                "主诉：发现右侧上腹部无痛性肿块4个月\n" +
                "现病史：4个月前发现右侧上腹部无痛性肿块，如苹果大小，活动性好，可上下移动。近几天肿块较前增大，无疼痛，偶有低热，食量减少，大小便正常。\n" +
                "既往史：\n" +
                "个人史：\n" +
                "过敏史：\n" +
                "生育史：\n" +
                "婚育史：\n" +
                "流行病史：\n" +
                "体格检查：生命体征：体温37.4℃，脉搏102次/分，呼吸26次/分，血压110/70mmHg。腹部查体：右上腹部略膨隆，触及8cm×7cm肿块，无压痛，表面光滑，可上下移动。\n" +
                "辅助检查：实验室检查：WBC 13×10^9/L，尿常规、肾功能、肝功能未见异常。多普勒超声：右肾中极8.2cm×7.8cm囊实性占位，肾静脉及腔静脉无癌栓。CT：右肾中极8.5cm×7.8cm囊实性占位，平扫低密度，增强轻中度强化伴片状未强化影。\n",
                "肾母细胞瘤"
                );

        String res = deepSeekIntelliChat.jsonCompletions(
                inputData
        );
        System.out.println(res);
    }

    @Test
    public void test() {
        // System.out.println("性别: 女\n年龄: 29\n主诉: 阴道不规则出血10天.\n现病史: 既往月经规律，末次月经：2022-12-20，月经量中等，经期无特殊不适。生育史： 0-0-1-0\n既往史: 否认肝炎、结核、疟疾等传染病史，否认肝脏肾脏疾病 ，否认高血压、心脏病、糖尿病等慢性疾病病 史，否认输血、外伤史，否认食物、药物过敏史，预防接种史不详。 流行病学史：\n个人史: \n过敏史: \n婚育史: \n流行病史: \n体格检查: PV:外阴：已婚式 \n阴道：畅，可见暗红色血迹 \n宫颈：光，常大，触血(-) \n宫体：前位，常大，质中,活动可,无压痛 \n\n双附件：未及明显异常\n体重：60kg，身高：165cm\n辅助检查: 尿妊娠试验-，B超未见明显异常");
        String text = "急性化脓性腹膜炎 案例分析-膈下脓肿";
        int index = text.indexOf("案例分析");
        String title = text.substring(index + 5);
        System.out.println(title);
    }

}
