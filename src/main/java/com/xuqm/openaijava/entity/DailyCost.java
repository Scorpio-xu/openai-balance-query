package com.xuqm.openaijava.entity;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;

/**
 * 金额消耗列表
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@Data
public class DailyCost {

    /**
     * 时间戳
     */
    private long timestamp;

    /**
     * 模型消耗金额详情
     */
    @JSONField(name = "line_items")
    private List<LineItem> lineItems;

}
