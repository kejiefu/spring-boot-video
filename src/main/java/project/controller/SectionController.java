package project.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther kejiefu
 * @Date 2018/9/10 0010
 */
@Controller
public class SectionController {

    private static final Logger logger = LoggerFactory.getLogger(SectionController.class);

    @Value("${file.save.path}")
    private String fileSavePath;

    @RequestMapping("/section")
    public void section(HttpServletRequest request, HttpServletResponse response) {
        logger.info("section...");
        //1.创建文件对象
        File file = new File(this.getClass().getClassLoader().getResource("").getPath() + "static/video/1.mp4");
        if (!file.exists()) {
            logger.error("文件不存在!");
            return;
        }
        String filePath = file.getAbsolutePath();
        List<String> commands = new ArrayList<>();
        commands.add("ffmpeg");
        commands.add("-i");
        commands.add(filePath);
        commands.add("-c");
        commands.add("copy");
        commands.add("-map");
        commands.add("0");
        commands.add("-f");
        commands.add("segment");
        commands.add("-segment_list");
        commands.add(fileSavePath + "/playlist.m3u8");
        commands.add("-segment_time");
        commands.add("5");
        commands.add(fileSavePath + "/abc%03d.ts");

        ProcessBuilder builder = new ProcessBuilder(commands);
        builder.redirectErrorStream(true);
        try {
            Process process = builder.start();
            StringBuilder stringBuilder = new StringBuilder();
            InputStream inputStream = process.getInputStream();
            new Thread(new Runnable() {//启动新线程为异步读取缓冲器，防止线程阻塞
                @Override
                public void run() {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    try {
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                        }
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            process.waitFor();
            System.out.println(stringBuilder.toString());
        } catch (Exception e) {
            logger.error("ffmpeg执行异常:", e);
        }
    }


}
