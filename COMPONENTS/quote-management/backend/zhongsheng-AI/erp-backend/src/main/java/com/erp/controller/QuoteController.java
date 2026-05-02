package com.erp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.Result;
import com.erp.entity.Quote;
import com.erp.service.QuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quote")
public class QuoteController {
    @Autowired
    private QuoteService quoteService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.success(quoteService.page(new Page<>(current, size), keyword));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(quoteService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Quote quote) {
        quoteService.save(quote);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Quote quote) {
        quoteService.updateById(quote);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        quoteService.removeById(id);
        return Result.success();
    }
}
