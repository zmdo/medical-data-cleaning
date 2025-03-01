# HealthAI 2025 医学数据清洗工具

该项目的作用是清洗医学数据，并将其转换为固定的格式以备训练大模型。

## 快速开始

1. 请将下载好的数据解压到 `data` 目录下，确保你的数据格式为 json 格式，且存储的内容为数据数组。如：

```json
[
  {"question": "什么是肩周炎？","answer": "肩周炎是一种以肩关节疼痛和活动受限为主要特征的慢性炎症性疾病，多因肩部软组织损伤、退变或长期劳损引起。"},
  {"question": "怎么治疗肩周炎？","answer": "肩周炎的治疗需综合多种手段，包括药物治疗（如非甾体抗炎药缓解疼痛和炎症）、物理治疗（如热敷、冷敷、电疗等）、功能锻炼（如爬墙法、钟摆运动等）、中医治疗（如针灸、推拿、中药外敷）以及局部封闭或手术治疗（针对严重病例），具体方案应根据病情和医生建议选择。"},
  {"question": "怎么诊断感冒？","answer": "感冒通常通过病史、症状（如发热、咳嗽、流涕、咽痛等）和体格检查进行初步诊断，必要时可结合实验室检查（如血常规、咽拭子检测）以排除其他疾病。"},
]
```
2. 在 `prompt` 目录下创建提示词文件，你可以参考已经提交的提示词来写你自己的提示词。
3. 在 `cn.zmdo.mdc.converter` 包下创建一个继承 `MedicalDataConverter<T>` 的类，并为其添加 `@Converter` 注解，如下所示：

```java
@Converter(
        dataId = "test-data", // 该数据的唯一标识符，最终会在 out 目录下保存该文件的 {dataId}.json 数据
        prompts = {
                "test/test-01.prompt",    // 在 prompt 目录下对应的系统提示词，该提示词会自动加载入 AI 对话器 IntelliChat 对象中。
                "test/test-02.prompt",      
        },
        sources = {
                "test/test-data-01.json", // 在 data 目录下对应的数据地址，指定地址的数据会转换为 TestObject 对象传入该类的 convert(TestObject,List<IntelliChat>) 方法中。
                "test/test-data-02.json",
        }
)
public class TestDataConverter extends MedicalDataConverter<TestObject> {
    
    @Override
    public StandardTrainData convert(TestObject originalData, List<IntelliChat> intelliChats) throws Exception {
        
        /*
         * 实现如何将 originalData 转换为 StandardTrainData 
         */

        // intelliChats 的列表顺序对应 @Converter 注解中加载 prompts 的顺序
        IntelliChat testO1PromptIntelliChat = intelliChats.get(0);
        
        ...
        
    }
    
}
```
4. 启动，直接运行 `Main` 类即可，其中你可以通过修改 `config.setThreads(int n)` 中的线程数来多线程请求文件。
5. 最后的输出文件会在 `out` 目录下。

## 关于 HealthAI 2025 健康智能挑战赛

随着人工智能技术的飞速发展，医疗健康产业正站在数字化转型的风口浪尖。在此背景下，由蚂蚁集团主办，中国计算机学会计算经济学专业组、北京大学全球健康发展研究院进行学术指导，北大创新评论、上海市数字医学创新中心联合发起的HealthAI 2024健康智能挑战赛报名开启。



大赛聚焦于医疗大模型领域的应用创新，邀请全国高校、科研机构、医疗机构及科技企业共同参与，通过线上应用场景展示和线下分赛区路演等多种方式，为参赛者提供与行业专家深入交流的平台。



HealthAI健康智能挑战赛不仅是一场技术和创意的竞技场，更是一个学习、交流和合作的盛会。它推动数字经济的发展和社会效益的提升。通过本次大赛激发社会创新活力，本次比赛将为满足社会对优质医疗服务的需求，同时为参赛者提供实现技术突破和个人成长的机遇。这不仅是对参赛者技术实力的考验，更是对医疗健康领域创新思维的一次大检阅。



通过本次大赛，希望挖掘和培养一批具有前瞻性和创新性的医疗健康项目，推动医疗大模型技术在实际应用中的落地，加速健康医疗体系的发展。我们期待通过比赛的举办，能够促进医疗健康领域的技术进步和服务质量的提升，为社会带来更高效、更智能的医疗服务，最终实现科技赋能医疗，智慧守护健康的愿景。



本次大赛需在官网报名参与比赛，官网链接：[https://competition.pkucxpl.com](https://competition.pkucxpl.com)