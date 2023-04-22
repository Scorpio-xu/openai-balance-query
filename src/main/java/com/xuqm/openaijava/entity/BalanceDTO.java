package com.xuqm.openaijava.entity;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 余额详情
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BalanceDTO {

    /**
     * 总额（美元）
     */
    private Integer total;

    /**
     * 已使用（美元）
     */
    private BigDecimal used;

    /**
     * 余额（美元）
     */
    private BigDecimal balance;

}
