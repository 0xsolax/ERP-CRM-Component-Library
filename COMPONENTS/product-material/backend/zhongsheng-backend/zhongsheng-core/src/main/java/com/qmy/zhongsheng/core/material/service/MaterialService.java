package com.qmy.zhongsheng.core.material.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.material.MaterialListQueryDTO;
import com.qmy.zhongsheng.api.dto.material.MaterialSaveDTO;
import com.qmy.zhongsheng.api.request.IdRequestParam;
import com.qmy.zhongsheng.core.material.model.vo.MaterialSimpleVO;
import com.qmy.zhongsheng.core.material.model.vo.MaterialVO;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * @author AI Coding
 */
public interface MaterialService {

    Long saveOrUpdate(MaterialSaveDTO dto);

    Page<MaterialVO> page(MaterialListQueryDTO query);

    List<MaterialSimpleVO> listByCategoryId(IdRequestParam idRequestParam);

    Boolean delete(Long id);
}