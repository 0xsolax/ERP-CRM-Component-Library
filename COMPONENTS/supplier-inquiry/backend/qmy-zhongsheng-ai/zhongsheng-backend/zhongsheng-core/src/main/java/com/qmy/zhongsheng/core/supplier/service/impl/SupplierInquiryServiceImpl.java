package com.qmy.zhongsheng.core.supplier.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryHistoryQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquiryListQueryDTO;
import com.qmy.zhongsheng.api.dto.supplier.SupplierInquirySaveDTO;
import com.qmy.zhongsheng.common.context.LoginUserInfoContext;
import com.qmy.zhongsheng.common.error.SupplierErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.login.LoginUserInfo;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.core.supplier.manager.SupplierInquiryManager;
import com.qmy.zhongsheng.core.supplier.manager.SupplierManager;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierDO;
import com.qmy.zhongsheng.core.supplier.model.entity.SupplierInquiryDO;
import com.qmy.zhongsheng.core.supplier.model.vo.SupplierInquiryVO;
import com.qmy.zhongsheng.core.supplier.service.SupplierInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * 供应商询价台账服务实现。
 *
 * @author AI Coding
 */
@Service
@RequiredArgsConstructor
public class SupplierInquiryServiceImpl implements SupplierInquiryService {

    private static final String DEFAULT_CURRENCY = "RMB";

    private static final DateTimeFormatter LOG_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final SupplierInquiryManager supplierInquiryManager;

    private final SupplierManager supplierManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(SupplierInquirySaveDTO dto) {
        normalize(dto);
        validate(dto);
        SupplierInquiryDO existing = null;
        if (dto.getId() != null) {
            existing = supplierInquiryManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_NOT_FOUND);
            }
        }
        SupplierDO supplier = supplierManager.getById(dto.getSupplierId());
        if (supplier == null) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_NOT_FOUND);
        }
        SupplierInquiryDO row = BeanUtils.toBean(dto, SupplierInquiryDO.class);
        row.setSupplierCode(supplier.getCode());
        row.setSupplierName(supplier.getName());
        if (!StringUtils.hasText(row.getContactName())) {
            row.setContactName(supplier.getContact());
        }
        if (!StringUtils.hasText(row.getContactPhone())) {
            row.setContactPhone(supplier.getPhone());
        }
        fillOwner(row, existing);
        row.setModificationLogJson(buildModificationLogJson(existing, row));
        return supplierInquiryManager.saveOrUpdate(row);
    }

    @Override
    public Page<SupplierInquiryVO> page(SupplierInquiryListQueryDTO query) {
        Page<SupplierInquiryDO> doPage = supplierInquiryManager.page(query);
        Page<SupplierInquiryVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        voPage.setRecords(doPage.getRecords().stream().map(this::toVO).toList());
        return voPage;
    }

    @Override
    public SupplierInquiryVO detail(Long id) {
        SupplierInquiryDO row = supplierInquiryManager.getById(id);
        if (row == null) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_NOT_FOUND);
        }
        return toVO(row);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(Long id) {
        if (supplierInquiryManager.getById(id) == null) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_NOT_FOUND);
        }
        return supplierInquiryManager.delete(id);
    }

    @Override
    public Page<SupplierInquiryVO> history(SupplierInquiryHistoryQueryDTO query) {
        return page(query);
    }

    private SupplierInquiryVO toVO(SupplierInquiryDO row) {
        SupplierInquiryVO vo = BeanUtils.toBean(row, SupplierInquiryVO.class);
        vo.setChangeLogs(parseChangeLogs(row.getModificationLogJson()));
        return vo;
    }

    private void normalize(SupplierInquirySaveDTO dto) {
        if (dto == null) {
            return;
        }
        dto.setTargetType(trimToNull(dto.getTargetType()));
        dto.setTargetCode(trimToNull(dto.getTargetCode()));
        dto.setTargetName(trimToNull(dto.getTargetName()));
        dto.setSpecification(trimToNull(dto.getSpecification()));
        dto.setUnit(trimToNull(dto.getUnit()));
        dto.setCurrency(StringUtils.hasText(dto.getCurrency()) ? dto.getCurrency().trim().toUpperCase() : DEFAULT_CURRENCY);
        dto.setDeliveryDays(trimToNull(dto.getDeliveryDays()));
        dto.setContactName(trimToNull(dto.getContactName()));
        dto.setContactPhone(trimToNull(dto.getContactPhone()));
        dto.setRemark(trimToNull(dto.getRemark()));
        if (dto.getQuoteDate() == null) {
            dto.setQuoteDate(LocalDate.now());
        }
        if (dto.getTaxRate() == null) {
            dto.setTaxRate(BigDecimal.ZERO);
        }
    }

    private void validate(SupplierInquirySaveDTO dto) {
        if (dto == null || dto.getSupplierId() == null) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_SUPPLIER_REQUIRED);
        }
        if (!StringUtils.hasText(dto.getTargetType())) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_TARGET_TYPE_REQUIRED);
        }
        if (!StringUtils.hasText(dto.getTargetName())) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_TARGET_NAME_REQUIRED);
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw ServiceExceptionUtil.exception(SupplierErrorCodeConstants.SUPPLIER_INQUIRY_PRICE_INVALID);
        }
    }

    private void fillOwner(SupplierInquiryDO row, SupplierInquiryDO existing) {
        if (existing != null) {
            row.setOwnerId(existing.getOwnerId());
            row.setOwnerName(existing.getOwnerName());
            return;
        }
        LoginUserInfo loginUser = LoginUserInfoContext.getLoginUserInfo();
        if (loginUser != null) {
            row.setOwnerId(loginUser.getUserId());
            row.setOwnerName(loginUser.getUserName());
        }
    }

    private String buildModificationLogJson(SupplierInquiryDO before, SupplierInquiryDO after) {
        if (before == null) {
            return null;
        }
        List<SupplierInquiryVO.ChangeLogVO> logs = new ArrayList<>(parseChangeLogs(before.getModificationLogJson()));
        List<SupplierInquiryVO.FieldChangeVO> changes = new ArrayList<>();
        addChange(changes, "供应商", before.getSupplierName(), after.getSupplierName());
        addChange(changes, "询价对象类型", before.getTargetType(), after.getTargetType());
        addChange(changes, "询价对象编号", before.getTargetCode(), after.getTargetCode());
        addChange(changes, "询价对象", before.getTargetName(), after.getTargetName());
        addChange(changes, "规格", before.getSpecification(), after.getSpecification());
        addChange(changes, "单位", before.getUnit(), after.getUnit());
        addChange(changes, "单价", decimalText(before.getPrice()), decimalText(after.getPrice()));
        addChange(changes, "币种", before.getCurrency(), after.getCurrency());
        addChange(changes, "税率", decimalText(before.getTaxRate()), decimalText(after.getTaxRate()));
        addChange(changes, "起订量", decimalText(before.getMoq()), decimalText(after.getMoq()));
        addChange(changes, "交期", before.getDeliveryDays(), after.getDeliveryDays());
        addChange(changes, "报价日期", dateText(before.getQuoteDate()), dateText(after.getQuoteDate()));
        addChange(changes, "有效期", dateText(before.getValidUntil()), dateText(after.getValidUntil()));
        addChange(changes, "联系人", before.getContactName(), after.getContactName());
        addChange(changes, "联系方式", before.getContactPhone(), after.getContactPhone());
        addChange(changes, "备注", before.getRemark(), after.getRemark());
        if (changes.isEmpty()) {
            return before.getModificationLogJson();
        }
        SupplierInquiryVO.ChangeLogVO log = new SupplierInquiryVO.ChangeLogVO();
        LoginUserInfo loginUser = LoginUserInfoContext.getLoginUserInfo();
        log.setTime(LocalDateTime.now().format(LOG_TIME_FORMATTER));
        log.setOperatorId(loginUser == null ? null : loginUser.getUserId());
        log.setOperatorName(operatorName(loginUser));
        log.setChanges(changes);
        logs.add(0, log);
        return JSONUtil.toJsonStr(logs);
    }

    private List<SupplierInquiryVO.ChangeLogVO> parseChangeLogs(String json) {
        if (!StringUtils.hasText(json)) {
            return Collections.emptyList();
        }
        try {
            JSONArray array = JSONUtil.parseArray(json);
            List<SupplierInquiryVO.ChangeLogVO> logs = new ArrayList<>();
            for (Object item : array) {
                JSONObject obj = JSONUtil.parseObj(item);
                SupplierInquiryVO.ChangeLogVO log = new SupplierInquiryVO.ChangeLogVO();
                log.setTime(obj.getStr("time"));
                log.setOperatorId(obj.getLong("operatorId"));
                log.setOperatorName(obj.getStr("operatorName"));
                log.setChanges(parseFieldChanges(obj.getJSONArray("changes")));
                logs.add(log);
            }
            return logs;
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    private List<SupplierInquiryVO.FieldChangeVO> parseFieldChanges(JSONArray array) {
        if (array == null || array.isEmpty()) {
            return Collections.emptyList();
        }
        List<SupplierInquiryVO.FieldChangeVO> changes = new ArrayList<>();
        for (Object item : array) {
            JSONObject obj = JSONUtil.parseObj(item);
            SupplierInquiryVO.FieldChangeVO change = new SupplierInquiryVO.FieldChangeVO();
            change.setField(obj.getStr("field"));
            change.setBefore(obj.getStr("before"));
            change.setAfter(obj.getStr("after"));
            changes.add(change);
        }
        return changes;
    }

    private void addChange(List<SupplierInquiryVO.FieldChangeVO> changes, String field, String before, String after) {
        String oldValue = trimToNull(before);
        String newValue = trimToNull(after);
        if (Objects.equals(oldValue, newValue)) {
            return;
        }
        SupplierInquiryVO.FieldChangeVO change = new SupplierInquiryVO.FieldChangeVO();
        change.setField(field);
        change.setBefore(oldValue);
        change.setAfter(newValue);
        changes.add(change);
    }

    private String operatorName(LoginUserInfo loginUser) {
        if (loginUser == null) {
            return "系统";
        }
        if (StringUtils.hasText(loginUser.getNickname())) {
            return loginUser.getNickname();
        }
        return StringUtils.hasText(loginUser.getUserName()) ? loginUser.getUserName() : "系统";
    }

    private String decimalText(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.stripTrailingZeros().toPlainString();
    }

    private String dateText(LocalDate value) {
        return value == null ? null : value.toString();
    }

    private String trimToNull(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }

}
