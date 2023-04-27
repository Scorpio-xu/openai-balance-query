package com.xuqm.openaijava.entity;

import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 金额消耗信息
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@Data
public class BillingUsage {

    private String object;

    /**
     * 账号金额消耗明细
     */
    @JSONField(name = "daily_costs")
    private List<DailyCost> dailyCosts;

    /**
     * 总使用金额：美分
     */
    @JSONField(name = "total_usage")
    private BigDecimal totalUsage;

}
