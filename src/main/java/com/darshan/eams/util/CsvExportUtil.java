package com.darshan.eams.util;

import java.util.List;

public final class CsvExportUtil {

    private CsvExportUtil() {
    }

    public static String toCsv(List<String> headers, List<List<String>> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.join(",", headers.stream().map(CsvExportUtil::escape).toList())).append("\n");

        for (List<String> row : rows) {
            sb.append(String.join(",", row.stream().map(CsvExportUtil::escape).toList())).append("\n");
        }

        return sb.toString();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        boolean needsQuoting = value.contains(",") || value.contains("\"") || value.contains("\n");
        String escaped = value.replace("\"", "\"\"");
        return needsQuoting ? "\"" + escaped + "\"" : escaped;
    }
}