package com.hesmantech.salonbooking.service.report;

import java.util.List;

public interface PrepareReportData<T, R> {
    List<T> prepareReportData(R request);

    default List<T> prepareReportData() {
        return prepareReportData(null);
    }
}
