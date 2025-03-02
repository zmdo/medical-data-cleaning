package cn.zmdo.mdc.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * 提示词读取器
 */
@Slf4j
public class PromptReader {

    /**
     * 读取指定文件的提示词内容。
     *
     * @param promptPath 项目目录下 {@code /prompt/} 下的提示词文件路径
     * @return 提示词文件内容
     */
    public static String readPrompt(String promptPath) {
        String path = System.getProperty("user.dir") + "/prompt/" + promptPath;
        return readTextFile(path);
    }

    /**
     * 读取指定路径的文本文件
     *
     * @param filePath 文件路径
     * @return 文件内容
     */
    private static String readTextFile(String filePath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(new File(filePath)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n"); // 追加每行内容
            }
        } catch (IOException e) {
            log.error("读取{}时发生错误！",filePath);
            log.error(e.getMessage());
            return null;
        }
        return content.toString();
    }
}
