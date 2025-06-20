import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryManager {
    private final String historyFile;

    public HistoryManager(String fileName) {
        this.historyFile = fileName;
    }

    // 保存历史记录
    public void saveHistory(String expression, double result) {
        // 创建时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timestamp = sdf.format(new Date());

        // 格式化结果
        String resultStr = String.format("%.6f", result);

        // 创建历史记录条目
        String historyEntry = timestamp + " | " + expression + " = " + resultStr;

        // 写入文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile, true))) {
            writer.write(historyEntry);
            writer.newLine();
        } catch (IOException e) {
            System.err.println("无法保存历史记录: " + e.getMessage());
        }
    }

    // 获取历史记录
    public String getHistory() {
        Path path = Paths.get(historyFile);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return "暂无历史记录";
        }

        try {
            List<String> lines = Files.readAllLines(path);
            if (lines.isEmpty()) {
                return "暂无历史记录";
            }

            StringBuilder history = new StringBuilder();
            history.append("===== 历史记录 =====\n");

            // 显示最近的10条记录
            int startIndex = Math.max(0, lines.size() - 10);
            for (int i = startIndex; i < lines.size(); i++) {
                history.append(lines.get(i)).append("\n");
            }

            history.append("\n共 ").append(lines.size()).append(" 条记录");
            return history.toString();
        } catch (IOException e) {
            return "无法读取历史记录: " + e.getMessage();
        }
    }

    public void clearHistory() {
        try {
            Path path = Paths.get(historyFile);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            System.err.println("无法清除历史记录: " + e.getMessage());
        }
    }
}