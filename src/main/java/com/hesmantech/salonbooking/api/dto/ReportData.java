package com.hesmantech.salonbooking.api.dto;

import java.util.Arrays;

public record ReportData(
        String fileName,
        byte[] content
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReportData reportData = (ReportData) o;
        return fileName.equals(reportData.fileName) && Arrays.equals(content, reportData.content);
    }

    @Override
    public int hashCode() {
        int result = fileName.hashCode();
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "ReportData{" +
                "fileName=" + fileName +
                ", content=" + Arrays.toString(content) +
                '}';
    }
}
