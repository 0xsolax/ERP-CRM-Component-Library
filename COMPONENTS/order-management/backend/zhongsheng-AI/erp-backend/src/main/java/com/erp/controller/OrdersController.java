package com.erp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.Result;
import com.erp.entity.Orders;
import com.erp.service.OrdersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {
    @Autowired
    private OrdersService ordersService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword) {
        return Result.success(ordersService.page(new Page<>(current, size), keyword));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(ordersService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Orders orders) {
        ordersService.save(orders);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Orders orders) {
        ordersService.updateById(orders);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        ordersService.removeById(id);
        return Result.success();
    }
}
