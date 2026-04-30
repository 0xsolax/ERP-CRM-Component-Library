package com.qmy.zhongsheng.core.product.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.product.ProductListQueryDTO;
import com.qmy.zhongsheng.api.dto.product.ProductSaveDTO;
import com.qmy.zhongsheng.core.product.model.vo.*;

import java.util.List;

/**
 * 产品服务。
 *
 * @author 单漪甜
 */
public interface ProductService {

    /**
     * 保存或更新产品。
     *
     * @param dto 保存请求 DTO
     * @return 产品 ID
     */
    Long saveOrUpdate(ProductSaveDTO dto);

    /**
     * 分页查询产品列表。
     *
     * @param query 分页查询请求，支持多维度筛选与关键词
     * @return 分页结果
     */
    Page<ProductVO> page(ProductListQueryDTO query);

    /**
     * 删除产品。
     *
     * @param id 产品 ID
     * @return 是否删除成功
     */
    Boolean delete(Long id);

    /**
     * 查询产品详情。
     *
     * @param id 产品 ID
     * @return 产品详情
     */
    ProductDetailVO detail(Long id);
}
