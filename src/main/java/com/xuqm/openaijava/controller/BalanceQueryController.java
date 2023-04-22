package com.xuqm.openaijava.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.xuqm.openaijava.entity.BalanceDTO;
import com.xuqm.openaijava.entity.Result;
import com.xuqm.openaijava.service.BalanceQueryService;

import lombok.RequiredArgsConstructor;

/**
 * 余额查询Controller
 *
 * @author xuqiming
 * @date 2023-04-22
 **/
@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class BalanceQueryController {

    private final BalanceQueryService balanceQueryService;

    @GetMapping("/balanceQuery")
    public Result<BalanceDTO> balanceQuery(@RequestParam String apikey) {
        try {
            return Result.success(balanceQueryService.balanceQuery(apikey));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

}
