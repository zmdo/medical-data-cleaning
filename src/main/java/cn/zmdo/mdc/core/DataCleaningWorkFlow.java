package cn.zmdo.mdc.core;

import cn.zmdo.mdc.third.DeepSeekIntelliChat;
import cn.zmdo.mdc.util.DataReader;
import cn.zmdo.mdc.util.PromptReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 数据清洗工作流
 */
@Slf4j
public class DataCleaningWorkFlow {

    /**
     * 执行任务
     *
     * @param config 任务配置
     */
    public <T> void start(Config config) throws Exception {

        log.info("开始工作流");

        // 扫描转换器
        List<Class<?>> converterClasses = scanConverter(config.getScanPackage());

        log.info("共扫描到 {} 个转换器：{}",converterClasses.size(), converterClasses);

        List<T> allData = new ArrayList<>();

        // 创建线程池
        ExecutorService executor = Executors.newFixedThreadPool(config.getThreads());

        // 构建任务
        List<Callable<List<? extends T>>> tasks = new ArrayList<>();
        for (Class<?> converterClass : converterClasses) {
            tasks.add(() -> convert(
                    config,
                    converterClass
            ));
        }

        try {

            log.info("开始执行任务");

            // 提交并执行任务
            List<Future<List<? extends T>>> futures = executor.invokeAll(tasks);

            // 合并保存
            if (config.isMergeSave()) {

                log.info("合并保存结果");

                for (Future<List<? extends T>> future : futures) {
                    allData.addAll(future.get());
                }
                save(allData,"all");
            }

        } finally {
            executor.shutdown();
        }

        log.info("结束工作流");

    }

    /**
     * 扫描所有被 {@link cn.zmdo.mdc.core.Converter} 注解的类
     *
     * @param packageName 包名
     * @return 在指定包下被 {@link cn.zmdo.mdc.core.Converter} 注解的类的列表
     */
    public static List<Class<?>> scanConverter(String packageName) {

        List<Class<?>> converterClasses = new ArrayList<>();

        try {
            // 创建一个类路径扫描器
            ClassPathScanningCandidateComponentProvider scanner =
                    new ClassPathScanningCandidateComponentProvider(false);

            // 添加过滤器，只选择带有 @Converter 注解的类
            scanner.addIncludeFilter(new AnnotationTypeFilter(Converter.class));

            // 扫描指定的包
            scanner.findCandidateComponents(packageName)
                    .forEach(beanDefinition -> {
                        try {
                            // 获取类的完整名称
                            String className = beanDefinition.getBeanClassName();

                            // 加载类
                            Class<?> clazz = Class.forName(className);

                            // 添加到列表
                            converterClasses.add(clazz);
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

        return converterClasses;
    }

    /**
     * 使用转换器类转换数据
     *
     * @param config         配置
     * @param converterClass 转换器类型，要确保转换器类可以转换泛型 {@code <T>} 对应的类型
     * @return 泛型 {@code <T>} 对应的数据列表
     * @param <T> 被转换的类型
     */
    public <T> List<T> convert(
            Config config,
            Class<?> converterClass
    ) throws Exception {

        List<T> data = new ArrayList<>();

        // 先获取注解信息
        Converter converterAnno = converterClass.getAnnotation(Converter.class);
        if (!converterAnno.enable()) {
            return data;
        }

        // 读取并注入 prompt 到 AI 会话器中
        List<IntelliChat> intelliChats = new ArrayList<>();
        if (converterAnno.prompts() != null) {
            for (String prompt : converterAnno.prompts()) {
                DeepSeekIntelliChat intelliChat = new DeepSeekIntelliChat(
                        config.getAiChatUrl(),
                        config.getAiChatKey(),
                        config.getAiChatModel()
                );
                String promptContent = PromptReader.readPrompt(prompt);
                intelliChat.setSystemPrompt(promptContent);
                intelliChats.add(intelliChat);
            }
        }

        // 创建一个对象
        Object obj = converterClass.getConstructor().newInstance();
        Method originalTypeMethod = converterClass.getMethod("originalType");

        // 读取数据
        String[] sources = converterAnno.sources();
        for (String source : sources) {

            // 读取数据
            List<?> objects = DataReader.readList(source,(Class<?>)originalTypeMethod.invoke(obj));

            if (objects == null) {
                continue;
            }

            // 转换数据
            List<? extends T> convertedObjects = ((StandardDataConverter)obj).convert(objects,intelliChats);

            // 加入数据列表
            data.addAll(convertedObjects);

        }

        // 保存数据
        save(data,converterAnno.dataId());
        return data;
    }

    /**
     * 保存数据为 json，该数据会被保存到项目目录下的 {@code /out} 中。
     *
     * @param data 需要保存的数据
     * @param name 数据名
     * @param <T> 数据类型
     */
    public <T> void save(List<T> data,String name) throws IOException {
        String savePath = System.getProperty("user.dir") + "/out/" + name + ".json";

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(savePath),data);
    }

}
