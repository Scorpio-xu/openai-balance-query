package com.xuqm.openaijava.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xuqm.openaijava.config.OpenAiConfig;
import com.xuqm.openaijava.entity.*;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 余额查询Service
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceQueryService {

    private final OpenAiConfig openAiConfig;

    /**
     * 查询使用量URL
     */
    private static final String GET_USAGE_URL = "/v1/dashboard/billing/usage";

    /**
     * 查询订阅信息URL
     */
    private static final String GET_SUBSCRIPTION_URL = "/v1/dashboard/billing/subscription";

    /**
     * 余额查询
     *
     * @param apikey OPENAI_API_KEY
     * @return 调用结果
     */
    public BalanceDTO balanceQuery(String apikey) {
        if (StrUtil.isBlank(apikey)) {
            apikey = openAiConfig.getKey();
        }

        // 获取订阅信息
        String subscriptionResp = invokeApi(GET_SUBSCRIPTION_URL, apikey, null);
        Subscription subscription = JSON.parseObject(subscriptionResp, new TypeReference<Subscription>() {});
        BigDecimal total = subscription.getHardLimitUsd();

        String startDate;
        boolean expired = false;
        if (subscription.isHasPaymentMethod()) {
            // 付费账户从本月1号开始查
            startDate = DateUtil.beginOfMonth(new DateTime()).toDateStr();
        } else {
            // 非付费账户从90天前起查
            startDate = DateUtil.offsetDay(new DateTime(), -90).toDateStr();
            // 判断此账户是否过期
            if (new Date().getTime() > subscription.getAccessUntil() * 1000) {
                expired = true;
            }
        }

        // 获取使用量
        // 组装参数，开始日期和结束日期为必填参数
        Map<String, Object> params = MapUtil.ofEntries(MapUtil.entry("start_date", startDate),
            MapUtil.entry("end_date", DateUtil.tomorrow().toDateStr()));
        String usageResponse = invokeApi(GET_USAGE_URL, apikey, params);

        // 每日使用详情
        BillingUsage billingUsage = JSON.parseObject(usageResponse, new TypeReference<BillingUsage>() {});
        Map<String, BigDecimal> dailyCosts = new LinkedHashMap<>();
        for (DailyCost dailyCost : billingUsage.getDailyCosts()) {
            String date = DateUtil.date(dailyCost.getTimestamp() * 1000).toDateStr();
            BigDecimal dailyTotal = dailyCost.getLineItems().stream().map(LineItem::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);
            if (!BigDecimal.valueOf(0, 3).equals(dailyTotal)) {
                dailyCosts.put(date, dailyTotal);
            }
        }
        BigDecimal used = billingUsage.getTotalUsage()
            // 响应中的此单位是美分，故除以100进行转换
            .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);

        // 余额 = 总额 - 已使用
        BigDecimal balance = total.subtract(used);

        // 返回结果
        return new BalanceDTO(total, used, balance, dailyCosts, expired);
    }

    /**
     * 调用接口
     *
     * @param url 接口地址
     * @param apikey OPENAI_API_KEY
     * @param formMap 请求参数
     * @return 原始请求结果
     */
    private String invokeApi(String url, String apikey, Map<String, Object> formMap) {
        String result = HttpRequest.get(openAiConfig.getHost() + url)
            // 本地开发时用，上生产时记得去掉
            // .setHttpProxy("127.0.0.1", 7890)
            // 设置表头
            .header(Header.CONTENT_TYPE, "application/json").header(Header.AUTHORIZATION, "Bearer " + apikey)
            // 设置待传的表单数据并传输
            .form(formMap).execute().body();
        // 判断是否请求出错
        if (result.contains("error")) {
            log.error("请求{}出错，响应：{}", url, result);
            throw new RuntimeException("请求出错——" + JSONUtil.parseObj(result).getByPath("$.error.message", String.class));
        }
        return result;
    }
}
