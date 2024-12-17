package com.example.common.enums;

public enum PayEnum {
    YES("已缴费"),
    NO("未缴费"),
    REFUND("已退款"),
    CHECK ("待审核"),
    ;
    public String pay;

    PayEnum(String pay) {
        this.pay = pay;
    }
}
