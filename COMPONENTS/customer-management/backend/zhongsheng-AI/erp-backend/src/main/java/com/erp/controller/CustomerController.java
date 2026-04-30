package com.erp.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.erp.common.Result;
import com.erp.entity.Customer;
import com.erp.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @GetMapping("/page")
    public Result<?> page(@RequestParam(defaultValue = "1") long current,
                          @RequestParam(defaultValue = "10") long size,
                          @RequestParam(required = false) String keyword,
                          HttpServletRequest request) {
        boolean isAdmin = Boolean.TRUE.equals(request.getAttribute("isAdmin"));
        String username = (String) request.getAttribute("username");
        String ownerFilter = isAdmin ? null : username;
        return Result.success(customerService.page(new Page<>(current, size), keyword, ownerFilter));
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        return Result.success(customerService.getById(id));
    }

    @PostMapping
    public Result<?> save(@RequestBody Customer customer) {
        customerService.save(customer);
        return Result.success();
    }

    @PutMapping
    public Result<?> update(@RequestBody Customer customer) {
        customerService.updateById(customer);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        customerService.removeById(id);
        return Result.success();
    }
}
