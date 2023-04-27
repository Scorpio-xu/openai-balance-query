package com.xuqm.openaijava.entity;

import java.math.BigDecimal;

import lombok.Data;

/**
 * 每日模型金额消耗详情
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@Data
public class LineItem {

    /**
     * 模型名称
     */
    private String name;

    /**
     * 消耗金额
     */
    private BigDecimal cost;

}
