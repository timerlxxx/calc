package com.calculator.model;

import java.io.*;
import java.nio.file.*;
import java.text.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class HistoryManager {
    private final String historyFile;
    private List<String> historyCache = new ArrayList<>();
    private boolean cacheDirty = true;
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final Pattern HISTORY_PATTERN = Pattern.compile("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}) \\| (.+) = (\\d+\\.?\\d*)");

    public HistoryManager(String fileName) {
        this.historyFile = fileName;
        loadHistoryToCache();
    }

    // 加载历史记录到缓存
    private void loadHistoryToCache() {
        Path path = Paths.get(historyFile);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            historyCache.clear();
            cacheDirty = false;
            return;
        }

        try {
            historyCache = Files.readAllLines(path);
            cacheDirty = false;
        } catch (IOException e) {
            System.err.println("无法加载历史记录: " + e.getMessage());
            historyCache.clear();
        }
    }

    // 保存缓存到文件
    private void saveCacheToFile() {
        if (!cacheDirty) return;

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(historyFile))) {
            for (String entry : historyCache) {
                writer.write(entry);
                writer.newLine();
            }
            cacheDirty = false;
        } catch (IOException e) {
            System.err.println("无法保存历史记录: " + e.getMessage());
        }
    }

    // 保存历史记录
    public void saveHistory(String expression, double result) {
        String timestamp = SDF.format(new Date());
        String resultStr = String.format("%.6f", result);
        String historyEntry = timestamp + " | " + expression + " = " + resultStr;

        historyCache.add(historyEntry);
        cacheDirty = true;
        saveCacheToFile();
    }

    // 获取历史记录
    public String getHistory() {
        return getHistory(10, null, false);
    }

    // 增强版获取历史记录
    public String getHistory(int maxEntries, String filter, boolean reverseOrder) {
        if (cacheDirty) loadHistoryToCache();

        if (historyCache.isEmpty()) {
            return "暂无历史记录";
        }

        Stream<String> stream = historyCache.stream();

        // 应用过滤
        if (filter != null && !filter.trim().isEmpty()) {
            String searchTerm = filter.toLowerCase();
            stream = stream.filter(entry -> entry.toLowerCase().contains(searchTerm));
        }

        // 应用排序
        if (reverseOrder) {
            stream = stream.sorted(Comparator.reverseOrder());
        }

        // 限制条目数量
        List<String> displayList = stream
                .limit(maxEntries)
                .collect(Collectors.toList());

        if (displayList.isEmpty()) {
            return "未找到匹配的历史记录";
        }

        StringBuilder history = new StringBuilder();
        history.append("===== 历史记录 (显示 ").append(displayList.size())
                .append("/").append(historyCache.size()).append(") =====\n");

        for (String entry : displayList) {
            history.append(entry).append("\n");
        }

        return history.toString();
    }

    // 清除历史记录
    public void clearHistory() {
        historyCache.clear();
        cacheDirty = true;
        saveCacheToFile();
    }

    // 从文件导入表达式并计算
    public int processExpressionFile(String inputFilePath, ExpressionParser parser) throws IOException {
        Path path = Paths.get(inputFilePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException("文件不存在: " + inputFilePath);
        }

        List<String> expressions = Files.readAllLines(path);
        for (String expression : expressions) {
            String expr = expression.trim();
            if (expr.isEmpty()) continue;

            try {
                double result = parser.evaluate(expr);
                saveHistory(expr, result);
            } catch (Exception e) {
                System.err.println("计算表达式 '" + expr + "' 时出错: " + e.getMessage());
            }
        }
        return 0;
    }

    // 导出历史记录到文件
    public void exportHistory(String outputFilePath, String format) throws IOException {
        if (cacheDirty) loadHistoryToCache();

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
            for (String entry : historyCache) {
                if ("csv".equalsIgnoreCase(format)) {
                    // CSV格式: 时间戳,表达式,结果
                    Matcher m = HISTORY_PATTERN.matcher(entry);
                    if (m.find()) {
                        writer.write(String.format("\"%s\",\"%s\",%s\n",
                                m.group(1), m.group(2), m.group(3)));
                    }
                } else {
                    // 默认文本格式
                    writer.write(entry);
                    writer.newLine();
                }
            }
        }
    }

    // 导入历史记录
    public void importHistory(String inputFilePath) throws IOException {
        Path path = Paths.get(inputFilePath);
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            throw new IOException("文件不存在: " + inputFilePath);
        }

        List<String> importedEntries = Files.readAllLines(path);
        for (String entry : importedEntries) {
            if (entry.trim().isEmpty()) continue;

            // 尝试解析不同格式
            Matcher m = HISTORY_PATTERN.matcher(entry);
            if (m.find()) {
                // 标准格式: 直接添加
                historyCache.add(entry);
            } else if (entry.contains(",")) {
                // CSV格式: 时间戳,表达式,结果
                String[] parts = entry.split(",", 3);
                if (parts.length == 3) {
                    String timestamp = parts[0].replaceAll("^\"|\"$", "");
                    String expression = parts[1].replaceAll("^\"|\"$", "");
                    String result = parts[2].replaceAll("^\"|\"$", "");
                    historyCache.add(timestamp + " | " + expression + " = " + result);
                }
            }
        }

        cacheDirty = true;
        saveCacheToFile();
    }

    // 获取历史记录统计信息
    public Map<String, Object> getHistoryStats() {
        if (cacheDirty) loadHistoryToCache();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("总记录数", historyCache.size());

        if (historyCache.isEmpty()) {
            return stats;
        }

        // 时间范围
        String first = historyCache.get(0);
        String last = historyCache.get(historyCache.size() - 1);
        stats.put("最早记录", first.split(" \\| ")[0]);
        stats.put("最新记录", last.split(" \\| ")[0]);

        // 常见操作统计
        Map<String, Integer> operatorCount = new HashMap<>();
        for (String entry : historyCache) {
            Matcher m = HISTORY_PATTERN.matcher(entry);
            if (m.find()) {
                String expr = m.group(2);
                if (expr.contains("+")) operatorCount.put("+", operatorCount.getOrDefault("+", 0) + 1);
                if (expr.contains("-")) operatorCount.put("-", operatorCount.getOrDefault("-", 0) + 1);
                if (expr.contains("*")) operatorCount.put("*", operatorCount.getOrDefault("*", 0) + 1);
                if (expr.contains("/")) operatorCount.put("/", operatorCount.getOrDefault("/", 0) + 1);
                if (expr.contains("^")) operatorCount.put("^", operatorCount.getOrDefault("^", 0) + 1);
                if (expr.toLowerCase().contains("sin")) operatorCount.put("sin", operatorCount.getOrDefault("sin", 0) + 1);
                if (expr.toLowerCase().contains("cos")) operatorCount.put("cos", operatorCount.getOrDefault("cos", 0) + 1);
                if (expr.toLowerCase().contains("sqrt")) operatorCount.put("sqrt", operatorCount.getOrDefault("sqrt", 0) + 1);
            }
        }
        stats.put("操作统计", operatorCount);

        return stats;
    }
}