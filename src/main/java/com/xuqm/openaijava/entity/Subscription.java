package com.xuqm.openaijava.entity;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 账户信息
 *
 * @author xuqiming
 * @since 2023-06-01
 */
@Data
public class Subscription {

    private String object;

    @JSONField(name = "has_payment_method")
    private boolean hasPaymentMethod;

    private boolean canceled;

    @JSONField(name = "canceled_at")
    private Object canceledAt;

    private Object delinquent;

    @JSONField(name = "access_until")
    private long accessUntil;

    @JSONField(name = "soft_limit")
    private long softLimit;

    @JSONField(name = "hard_limit")
    private long hardLimit;

    @JSONField(name = "system_hard_limit")
    private long systemHardLimit;

    @JSONField(name = "soft_limit_usd")
    private BigDecimal softLimitUsd;

    @JSONField(name = "hard_limit_usd")
    private BigDecimal hardLimitUsd;

    @JSONField(name = "system_hard_limit_usd")
    private BigDecimal systemHardLimitUsd;

    private Plan plan;

    @JSONField(name = "account_name")
    private String accountName;

    @JSONField(name = "po_number")
    private Object poNumber;

    @JSONField(name = "billing_email")
    private Object billingEmail;

    @JSONField(name = "tax_ids")
    private Object taxIds;

    @JSONField(name = "billing_address")
    private Object billingAddress;

    @JSONField(name = "business_address")
    private Object businessAddress;

    @Data
    public static class Plan {
        private String title;
        private String id;
    }
}
