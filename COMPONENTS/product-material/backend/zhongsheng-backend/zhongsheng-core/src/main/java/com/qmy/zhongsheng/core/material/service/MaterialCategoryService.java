package com.qmy.zhongsheng.core.material.service;

import com.qmy.zhongsheng.api.dto.material.MaterialCategoryListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialCategorySaveDTO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialCategoryVO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author AI Coding
 */
public interface MaterialCategoryService {

    Long saveOrUpdate(MaterialCategorySaveDTO dto);

    List<MaterialCategoryVO> listByLikeName(MaterialCategoryListQueryDTO query);

    Boolean delete(Long id);
}