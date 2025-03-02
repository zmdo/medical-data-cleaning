# HealthAI 2025 医学数据清洗工具

该项目的作用是清洗医学数据，并将其转换为固定的格式以备训练大模型。 运行该项目需要 **JDK 11** 及以上版本的支持。

## 快速开始

1. 请将下载好的数据解压到 `data` 目录下，确保你的数据格式为 json 格式，且存储的内容为数据数组。如：

```json
[
  {"question": "什么是肩周炎？","answer": "肩周炎是一种以肩关节疼痛和活动受限为主要特征的慢性炎症性疾病，多因肩部软组织损伤、退变或长期劳损引起。"},
  {"question": "怎么治疗肩周炎？","answer": "肩周炎的治疗需综合多种手段，包括药物治疗（如非甾体抗炎药缓解疼痛和炎症）、物理治疗（如热敷、冷敷、电疗等）、功能锻炼（如爬墙法、钟摆运动等）、中医治疗（如针灸、推拿、中药外敷）以及局部封闭或手术治疗（针对严重病例），具体方案应根据病情和医生建议选择。"},
  {"question": "怎么诊断感冒？","answer": "感冒通常通过病史、症状（如发热、咳嗽、流涕、咽痛等）和体格检查进行初步诊断，必要时可结合实验室检查（如血常规、咽拭子检测）以排除其他疾病。"}
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
                "test/test-data-01.json",      // 在 data 目录下对应的数据地址，指定地址的数据会转换为 TestObject 对象传入该类的 convert(TestObject,List<IntelliChat>) 方法中。
                "line:test/test-data-02.json", // 路径前加上 line: 表示按行读取
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
5. 最后的输出文件会在 `out` 目录下。为防止程序意外中断导致数据丢失，程序运行时会保存一份临时数据，临时数据保存在 `temp` 目录下。

## 输出数据的结构

输出的 json 文件中的单一数据结构如下表所示：

| 字段 | 类型 | 描述      |
| --- | --- |---------|
| condition | Object | 患者的基本情况和检查结果 |
| condition.gender | String | 性别      |
| condition.age | Number | 年龄      |
| condition.chiefComplaint | String | 主诉      |
| condition.historyOfPresentIllness | String | 现病史     |
| condition.pastHistory | String | 既往史     |
| condition.personalHistory | String | 个人史     |
| condition.allergicHistory | String | 过敏史     |
| condition.reproductiveHistory | String | 生育史（ TPAL 数据 ） |
| condition.pregnancyAndDelivery | String | 婚育史     |
| condition.epidemicHistory | String | 流行病史    |
| condition.physicalExamination | String | 体格检查结果  |
| condition.auxiliaryExamination | String | 辅助检查结果  |
| diseases | Array | 诊断的疾病列表 |
| analyses | Array | 病例分析列表  |
| analyses.description | String | 分析描述    |
| analyses.score | Number | 分析评分（ 1 - 3 分 ） |
| diagnoses | Array | 最终诊断结果  |
| diagnoses.reason | String | 诊断依据    |
| diagnoses.conclusion | String | 诊断结论    |

## 医疗数据来源

| 数据  | 描述                                                       | 数据来源                                     |
|-----|----------------------------------------------------------|------------------------------------------|
| CMB | CMB（Comprehensive Medical Benchmark in Chinese）是由香港中文大学（深圳）的研究团队在2023年推出的一个全面的中文医学问答评测基准。                                                 | https://github.com/FreedomIntelligence/CMB |
| MedQA | MedQA 是一种采用多项选择题格式的医学文本问答数据集，其问题选自美国、中国大陆及中国台湾医学委员会的考试。  | https://github.com/jind11/MedQA          |

## 关于 HealthAI 2025 健康智能挑战赛

**随着科技的快速进步和数字经济的蓬勃发展，我国医疗健康产业正面临着前所未有的机遇与挑战。**

从数字经济的角度来看，以人工智能为代表的新一代信息技术正在帮助临床诊疗、药物研发等加速进步，展现出巨大的发展潜力和应用前景。这些技术不仅能够提升医疗服务的效率和质量，还能优化医疗资源的配置，为医疗行业的创新提供了广阔的空间。

从社会效益的视角来看，人口老龄化和慢性病增加等问题对医疗资源和服务提出了更高的要求。前沿技术的应用，不仅能为医疗服务的转型升级注入新的活力，还能加速健康医疗体系的发展，满足社会对优质医疗服务的需求。

在这样的背景下，医疗大模型应用挑战赛应运而生，旨在激发民生领域的创新活力，成为连接科技前沿与民生福祉的桥梁，推动数字经济发展和社会效益提升，激励社会各界共同为数字经济与医疗健康事业的发展贡献力量。


本次比赛聚焦于医疗大模型领域的应用创新项目。我们诚挚地邀请全国各地的高校、科研、医疗机构和产业单位参与其中，共同探索医疗健康产业的未来发展方向。本次比赛不仅是一次技术和创意的竞技场，更是一个学习、交流和合作的平台。我们将通过线上应用场景以及线下分赛区路演等多种形式，为参赛者提供与行业专家、合作伙伴深入交流的机会，帮助他们在医疗健康领域取得更大的突破。我们相信，通过本次比赛的举办，将有力推动医疗大模型的创新发展，为构建健康中国贡献力量。让我们携手共进，共创美好未来！

官网链接：[https://competition.pkucxpl.com](https://competition.pkucxpl.com)