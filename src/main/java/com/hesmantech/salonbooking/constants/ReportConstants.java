package com.hesmantech.salonbooking.constants;

public final class ReportConstants {
    // File name
    public static final String EMPLOYEE_REPORT_FILE_NAME = "employee_report";
    public static final String TECHNICIAN_REPORT_FILE_NAME = "technician_report";
    public static final String CUSTOMER_REPORT_FILE_NAME = "customer_report";
    public static final String CHECK_IN_MANAGEMENT_REPORT_FILE_NAME = "check_in_management_report";

    // Sheet name
    public static final String EMPLOYEES_SHEET_NAME = "Employees";
    public static final String TECHNICIANS_SHEET_NAME = "Technicians";
    public static final String CUSTOMERS_SHEET_NAME = "Customers";
    public static final String CHECK_IN_SHEET_NAME = "Check in";

    // Column header
    public static final String FIRST_NAME_COLUMN_HEADER = "First name";
    public static final String LAST_NAME_COLUMN_HEADER = "Last name";
    public static final String PHONE_NUMBER_COLUMN_HEADER = "Phone number";
    public static final String DATE_OF_BIRTH_COLUMN_HEADER = "Date of Birth";
    public static final String EMAIL_COLUMN_HEADER = "Email";
    public static final String GENDER_COLUMN_HEADER = "Gender";
    public static final String STATUS_COLUMN_HEADER = "Status";
    public static final String NO_COLUMN_HEADER = "No";
    public static final String CUSTOMER_NAME_COLUMN_HEADER = "Customer name";
    public static final String NAIL_TECHNICIAN_COLUMN_HEADER = "Nail technician";
    public static final String SUB_TOTAL_COLUMN_HEADER = "Sub total";
    public static final String CHECK_IN_TIME_COLUMN_HEADER = "Check in time";
    public static final String CHECK_OUT_TIME_COLUMN_HEADER = "Check out time";

    // Font name
    public static final String ARIAL_FONT_NAME = "Arial";
    public static final String TIMES_NEW_ROMAN_FONT_NAME = "Times New Roman";

    // File extension
    public static final String EXCEL_EXTENSION = ".xlsx";

    private ReportConstants() {
        throw new IllegalStateException("Constant class");
    }
}
