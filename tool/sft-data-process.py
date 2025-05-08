import json

# 该代码的作用是将病历标准格式转换为 SFT 训练集格式

def convert_dataset(input_file, system_txt_path, output_jsonl_path):
    # 读取system内容
    with open(system_txt_path, 'r', encoding='utf-8') as f:
        system_content = f.read()

    # 字段映射关系（字段名: 显示名称）
    field_map = [
        ('gender', '性别'),
        ('age', '年龄'),
        ('chiefComplaint', '主诉'),
        ('historyOfPresentIllness', '现病史'),
        ('pastHistory', '既往史'),
        ('personalHistory', '个人史'),
        ('allergicHistory', '过敏史'),
        ('reproductiveHistory', '生育史'),
        ('pregnancyAndDelivery', '婚育史'),
        ('epidemicHistory', '流行病史'),
        ('physicalExamination', '体格检查'),
        ('auxiliaryExamination', '辅助检查')
    ]

    # 加载原始数据
    with open(input_file, 'r', encoding='utf-8') as f:
        original_data = json.load(f)

    output_lines = []

    for item in original_data:
        # 构建user content
        user_content = []
        for key, display_name in field_map:
            value = item['condition'].get(key, '')
            if value is None:  # 处理空值
                value = ''
            user_content.append(f"{display_name}：{value}")

        # 构建assistant content
        # 疾病部分
        diseases = '<diseases>\n' + ';'.join(item['diseases']) + ';\n</diseases>'

        # 分析部分
        analyses = []
        for idx, analysis in enumerate(item['analyses'], 1):
            analyses.append(f"{idx}. {analysis['description']}")

        assistant_content = diseases + '\n' + '\n'.join(analyses)

        # 构建完整消息结构
        messages = [
            {"role": "system", "content": system_content},
            {"role": "user", "content": '\n'.join(user_content)},
            {"role": "assistant", "content": assistant_content}
        ]

        # 写入JSONL
        output_lines.append(json.dumps({"messages": messages}, ensure_ascii=False))

    # 保存结果
    with open(output_jsonl_path, 'w', encoding='utf-8') as f:
        f.write('\n'.join(output_lines))


if __name__ == "__main__":
    # 使用示例（需要替换实际路径）
    convert_dataset(
        input_file='/path/to/dataset.json',
        system_txt_path='/path/to/system-prompt.txt',
        output_jsonl_path='/path/to/output.jsonl'
    )