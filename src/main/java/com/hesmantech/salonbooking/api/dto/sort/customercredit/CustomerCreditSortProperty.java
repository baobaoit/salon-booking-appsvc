package com.hesmantech.salonbooking.api.dto.sort.customercredit;

import lombok.Getter;

@Getter
public enum CustomerCreditSortProperty {
    CREATED_DATE("createdDate", "createdDate"),
    CUSTOMER_NAME("customerName", "customer.firstName,customer.lastName"),
    AVAILABLE_CREDIT("availableCredit", "availableCredit"),
    TOTAL_CREDIT("totalCredit", "totalCredit"),
    AVAILABLE_NO_GIFT_CARD("availableNoGiftCard", "availableNoGiftCard"),
    REDEEM_NO_GIFT_CARD("redeemNoGiftCard", "redeemNoGiftCard");

    private final String property;
    private final String dbColumn;

    CustomerCreditSortProperty(String property, String dbColumn) {
        this.property = property;
        this.dbColumn = dbColumn;
    }

    public static CustomerCreditSortProperty fromDbColumn(String dbColumn) {
        for (CustomerCreditSortProperty property : CustomerCreditSortProperty.values()) {
            if (property.getDbColumn().equals(dbColumn)) {
                return property;
            }
        }
        return null;
    }
}
