import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @Auther kejiefu
 * @Date 2018/9/14 0014
 */
public class Text {
    public static void main(String[] args) {
        List<String> commands = new ArrayList<>();
        File file = new File("G:/text/1.mp4");
        if (!file.exists()) {
            System.out.println("文件不存在");
        }
        commands.add("ffmpeg -i G:/text/1.mp4 -c copy -map 0 -f segment -segment_list G:/text/playlist.m3u8 -segment_time 5 G:/text/abc%03d.ts");
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
        } catch (Exception e) {
            throw new RuntimeException("ffmpeg执行异常" + e.getMessage());
        }
    }
}
