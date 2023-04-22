package com.xuqm.openaijava.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.xuqm.openaijava.config.OpenAiConfig;
import com.xuqm.openaijava.entity.BalanceDTO;

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

        // 获取总额
        String totalAmountResp = invokeApi(GET_SUBSCRIPTION_URL, apikey, null);
        int total = JSONUtil.parseObj(totalAmountResp).getInt("hard_limit_usd");

        // 获取使用量
        // 组装参数，开始日期和结束日期为必填参数
        Map<String, Object> params = MapUtil.ofEntries(
            // 开始日期选择90天前
            MapUtil.entry("start_date", DateUtil.offsetDay(new DateTime(), -90).toDateStr()),
            // 结束日期选择明天
            MapUtil.entry("end_date", DateUtil.tomorrow().toDateStr()));
        String usageResponse = invokeApi(GET_USAGE_URL, apikey, params);
        BigDecimal used = JSONUtil.parseObj(usageResponse).getBigDecimal("total_usage")
            // 响应中的此单位是美分，故除以100进行转换
            .divide(BigDecimal.valueOf(100), 3, RoundingMode.HALF_UP);

        // 余额 = 总额 - 已使用
        BigDecimal balance = BigDecimal.valueOf(total).subtract(used);

        // 返回结果
        return new BalanceDTO(total, used, balance);
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
            .setHttpProxy("127.0.0.1", 7890)
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
