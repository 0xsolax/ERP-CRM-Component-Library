package com.qmy.zhongsheng.core.product.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qmy.zhongsheng.api.dto.product.ProductFabricSaveDTO;
import com.qmy.zhongsheng.api.dto.product.ProductListQueryDTO;
import com.qmy.zhongsheng.api.dto.product.ProductMaterialSaveDTO;
import com.qmy.zhongsheng.api.dto.product.ProductPackagingSaveDTO;
import com.qmy.zhongsheng.api.dto.product.ProductPrintingSaveDTO;
import com.qmy.zhongsheng.api.dto.product.ProductProcessPriceSaveDTO;
import com.qmy.zhongsheng.api.dto.product.ProductSaveDTO;
import com.qmy.zhongsheng.api.dto.product.ProductUmbrellaFrameSaveDTO;
import com.qmy.zhongsheng.common.error.ProductErrorCodeConstants;
import com.qmy.zhongsheng.common.exception.ServiceExceptionUtil;
import com.qmy.zhongsheng.common.utils.BeanUtils;
import com.qmy.zhongsheng.common.utils.StrUtils;
import com.qmy.zhongsheng.core.base.manager.BaseDataManager;
import com.qmy.zhongsheng.core.base.model.entity.BaseDataDO;
import com.qmy.zhongsheng.core.file.enums.SystemFileMainTypeEnum;
import com.qmy.zhongsheng.core.file.enums.SystemFileSubTypeEnum;
import com.qmy.zhongsheng.core.file.manager.SystemFileManager;
import com.qmy.zhongsheng.core.file.model.entity.SystemFileDO;
import com.qmy.zhongsheng.core.file.model.vo.FileVO;
import com.qmy.zhongsheng.core.file.service.SystemFileService;
import com.qmy.zhongsheng.core.material.manager.UmbrellaFrameManager;
import com.qmy.zhongsheng.core.material.model.entity.UmbrellaFrameDO;
import com.qmy.zhongsheng.core.product.manager.ProductFabricManager;
import com.qmy.zhongsheng.core.product.manager.ProductManager;
import com.qmy.zhongsheng.core.product.manager.ProductMaterialManager;
import com.qmy.zhongsheng.core.product.manager.ProductPackagingManager;
import com.qmy.zhongsheng.core.product.manager.ProductPrintingManager;
import com.qmy.zhongsheng.core.product.manager.ProductProcessPriceManager;
import com.qmy.zhongsheng.core.product.manager.ProductTypeManager;
import com.qmy.zhongsheng.core.product.manager.ProductUmbrellaFrameManager;
import com.qmy.zhongsheng.core.product.model.condition.ProductQueryCondition;
import com.qmy.zhongsheng.core.product.model.entity.ProductDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductFabricDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductMaterialDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductPackagingDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductPrintingDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductProcessPriceDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductTypeDO;
import com.qmy.zhongsheng.core.product.model.entity.ProductUmbrellaFrameDO;
import com.qmy.zhongsheng.core.product.model.vo.ProductDetailVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductFabricVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductMaterialVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductPackagingVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductPrintingVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductProcessPriceVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductTypeVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductUmbrellaFrameVO;
import com.qmy.zhongsheng.core.product.model.vo.ProductVO;
import com.qmy.zhongsheng.core.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotBlank;
import static com.qmy.zhongsheng.common.utils.ValidityUtils.isNotEmpty;
import static com.qmy.zhongsheng.common.utils.ValidityUtils.nonNull;

/**
 * 产品服务实现类。
 *
 * @author 单漪甜
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductManager productManager;

    private final ProductTypeManager productTypeManager;

    private final ProductUmbrellaFrameManager productUmbrellaFrameManager;

    private final ProductMaterialManager productMaterialManager;

    private final ProductFabricManager productFabricManager;

    private final ProductPackagingManager productPackagingManager;

    private final ProductPrintingManager productPrintingManager;

    private final ProductProcessPriceManager productProcessPriceManager;

    private final BaseDataManager baseDataManager;

    private final SystemFileManager systemFileManager;

    private final SystemFileService systemFileService;

    private final UmbrellaFrameManager umbrellaFrameManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrUpdate(ProductSaveDTO dto) {
        if (dto.getId() != null) {
            ProductDO existing = productManager.getById(dto.getId());
            if (existing == null) {
                throw ServiceExceptionUtil.exception(ProductErrorCodeConstants.PRODUCT_NOT_FOUND);
            }
        }

        ProductDO product = BeanUtils.toBean(dto, ProductDO.class);
        Long productId = productManager.saveOrUpdate(product);
        boolean hasFabrics = isNotEmpty(dto.getFabrics());
        boolean hasPrinting = isNotEmpty(dto.getPrintingList());
        if (hasFabrics != hasPrinting || (hasFabrics && dto.getFabrics().size() != dto.getPrintingList().size())) {
            throw ServiceExceptionUtil.exception(ProductErrorCodeConstants.PRODUCT_FABRIC_PRINTING_SIZE_NOT_EQUAL);
        }

        // 保存产品类型关联
        if (isNotEmpty(dto.getProductTypeIdList())) {
            saveProductTypes(productId, dto.getProductTypeIdList());
        }

        if (dto.getUmbrellaFrame() != null) {
            saveProductUmbrellaFrame(productId, dto.getUmbrellaFrame());
        }
        if (isNotEmpty(dto.getMaterials())) {
            saveProductMaterials(productId, dto.getMaterials());
        }
        if (isNotEmpty(dto.getFabrics())) {
            saveProductFabrics(productId, dto.getFabrics());
        }
        if (isNotEmpty(dto.getPackagingList())) {
            saveProductPackaging(productId, dto.getPackagingList());
        }
        if (isNotEmpty(dto.getPrintingList())) {
            saveProductPrinting(productId, dto.getPrintingList());
        }
        if (isNotEmpty(dto.getProcessPriceList())) {
            saveProductProcessPrices(productId, dto.getProcessPriceList());
        }
        if (isNotEmpty(dto.getImages())) {
            systemFileService.saveFiles(productId, SystemFileMainTypeEnum.PRODUCT, SystemFileSubTypeEnum.PRODUCT_IMAGE, dto.getImages());
        }

        return productId;
    }


    @Override
    public Page<ProductVO> page(ProductListQueryDTO query) {
        ProductQueryCondition condition = buildPageCondition(query);
        Page<ProductDO> doPage = productManager.page(condition);
        Page<ProductVO> voPage = new Page<>(doPage.getCurrent(), doPage.getSize(), doPage.getTotal());
        List<ProductVO> voList = BeanUtils.toBean(doPage.getRecords(), ProductVO.class);
        // 填充额外信息，进行业务处理
        if (!voList.isEmpty()) {
            // 产品类型
            fillProductTypes(voList);
            // 产品图片
            fillProductImages(voList);
            // 产品包菜尺寸展示逻辑
            fillLargestPackaging(voList);
        }
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 将 API 查询 DTO 转为 Manager 入参（多表筛选在 Service 侧解析为产品 ID 集合）。
     */
    private ProductQueryCondition buildPageCondition(ProductListQueryDTO query) {
        Map<Long, String> baseDataNameMap = getBaseDataNameMap(query);
        Set<Long> filteredProductIds = buildFilteredProductIds(query, baseDataNameMap);
        ProductQueryCondition condition = new ProductQueryCondition();
        condition.setPageNum(query.getPageNum());
        condition.setPageSize(query.getPageSize());
        condition.setIds(filteredProductIds);
        if (isNotBlank(query.getKeywords())) {
            condition.setKeywords(query.getKeywords().trim());
        }
        return condition;
    }

    /**
     * 获取基础数据名称映射
     */
    private Map<Long, String> getBaseDataNameMap(ProductListQueryDTO query) {
        List<Long> baseDataIds = Stream.of(
                        query.getFrameTypeId(),
                        query.getFrameFunctionId(),
                        query.getFrameMaterialId(),
                        query.getFabricTypeId(),
                        query.getPrintTypeId(),
                        query.getAlignmentTypeId())
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (baseDataIds.isEmpty()) {
            return Collections.emptyMap();
        }

        return baseDataManager.listByIds(baseDataIds).stream()
                .collect(Collectors.toMap(BaseDataDO::getId, BaseDataDO::getValue1, (a, b) -> a));
    }

    /**
     * 构建筛选的产品ID集合
     */
    private Set<Long> buildFilteredProductIds(ProductListQueryDTO query, Map<Long, String> baseDataNameMap) {
        // --- 1. 有效性校验：如果传了ID但查不到名称，说明数据不一致，直接返回空结果 ---
        if (hasInvalidBaseData(query, baseDataNameMap)) {
            return Collections.emptySet();
        }

        Set<Long> filteredProductIds = null;

        // --- 2. 组装各个维度的筛选ID ---

        // 产品类型
        if (nonNull(query.getProductTypeId())) {
            Set<Long> ids = productTypeManager.getProductIdsByTypeId(query.getProductTypeId());
            filteredProductIds = mergeProductIds(filteredProductIds, ids);
        }

        // 伞架尺寸 (通过长度ID反查三要素)
        if (nonNull(query.getFrameLengthId())) {
            UmbrellaFrameDO frame = umbrellaFrameManager.getById(query.getFrameLengthId());
            if (frame == null) {
                return Collections.emptySet();
            }
            Set<Long> ids = productUmbrellaFrameManager.getProductIdsByFrameSize(
                    frame.getLengthName(), frame.getDiameterName(), frame.getRibCountName());
            filteredProductIds = mergeProductIds(filteredProductIds, ids);
        }

        // 伞架属性 (类型、功能、材料)
        String frameTypeName = baseDataNameMap.get(query.getFrameTypeId());
        String frameFunctionName = baseDataNameMap.get(query.getFrameFunctionId());
        String frameMaterialName = baseDataNameMap.get(query.getFrameMaterialId());

        if (nonNull(frameTypeName) || nonNull(frameFunctionName) || nonNull(frameMaterialName)) {
            Set<Long> ids = productUmbrellaFrameManager.getProductIdsByNames(
                    frameTypeName, null, frameFunctionName, frameMaterialName);
            filteredProductIds = mergeProductIds(filteredProductIds, ids);
        }

        // 面料
        String fabricTypeName = baseDataNameMap.get(query.getFabricTypeId());
        if (nonNull(fabricTypeName)) {
            Set<Long> ids = productFabricManager.getProductIdsByTypeName(fabricTypeName);
            filteredProductIds = mergeProductIds(filteredProductIds, ids);
        }

        // 印刷
        String printTypeName = baseDataNameMap.get(query.getPrintTypeId());
        String alignmentTypeName = baseDataNameMap.get(query.getAlignmentTypeId());
        if (nonNull(printTypeName) || nonNull(alignmentTypeName)) {
            Set<Long> ids = productPrintingManager.getProductIdsByPrintingNames(printTypeName, alignmentTypeName);
            filteredProductIds = mergeProductIds(filteredProductIds, ids);
        }

        return filteredProductIds;
    }

    /**
     * 校验基础数据有效性
     */
    private boolean hasInvalidBaseData(ProductListQueryDTO query, Map<Long, String> nameMap) {
        return (nonNull(query.getFrameTypeId()) && !nameMap.containsKey(query.getFrameTypeId())) ||
                (nonNull(query.getFrameFunctionId()) && !nameMap.containsKey(query.getFrameFunctionId())) ||
                (nonNull(query.getFrameMaterialId()) && !nameMap.containsKey(query.getFrameMaterialId())) ||
                (nonNull(query.getFabricTypeId()) && !nameMap.containsKey(query.getFabricTypeId())) ||
                (nonNull(query.getPrintTypeId()) && !nameMap.containsKey(query.getPrintTypeId())) ||
                (nonNull(query.getAlignmentTypeId()) && !nameMap.containsKey(query.getAlignmentTypeId()));
    }


    @Override
    public Boolean delete(Long id) {
        return productManager.deleted(id);
    }

    @Override
    public ProductDetailVO detail(Long id) {
        ProductDO product = productManager.getById(id);
        if (product == null) {
            throw ServiceExceptionUtil.exception(ProductErrorCodeConstants.PRODUCT_NOT_FOUND);
        }

        ProductDetailVO detailVO = new ProductDetailVO();
        ProductVO productVO = BeanUtils.toBean(product, ProductVO.class);
        fillProductTypes(Collections.singletonList(productVO));
        fillProductImages(Collections.singletonList(productVO));
        fillLargestPackaging(Collections.singletonList(productVO));
        detailVO.setProduct(productVO);

        ProductUmbrellaFrameDO umbrellaFrameDO = productUmbrellaFrameManager.getByProductId(id);
        if (umbrellaFrameDO != null) {
            detailVO.setUmbrellaFrame(BeanUtils.toBean(umbrellaFrameDO, ProductUmbrellaFrameVO.class));
        }

        List<ProductMaterialDO> materialDOList = productMaterialManager.listByProductIds(Collections.singletonList(id));
        detailVO.setMaterials(BeanUtils.toBean(materialDOList, ProductMaterialVO.class));

        List<ProductFabricDO> fabricDOList = productFabricManager.listByProductIds(Collections.singletonList(id));
        detailVO.setFabrics(BeanUtils.toBean(fabricDOList, ProductFabricVO.class));

        List<ProductPackagingDO> packagingDOList = productPackagingManager.listByProductIds(Collections.singletonList(id));
        detailVO.setPackagingList(BeanUtils.toBean(packagingDOList, ProductPackagingVO.class));

        List<ProductPrintingDO> printingDOList = productPrintingManager.listByProductIds(Collections.singletonList(id));
        detailVO.setPrintingList(BeanUtils.toBean(printingDOList, ProductPrintingVO.class));

        List<ProductProcessPriceDO> processPriceDOList = productProcessPriceManager.listByProductIds(Collections.singletonList(id));
        detailVO.setProcessPriceList(BeanUtils.toBean(processPriceDOList, ProductProcessPriceVO.class));

        return detailVO;
    }

    // ===================== private methods =====================

    // newIds 为空表示该筛选在关联表中无匹配，须得到空交集，不得当作「跳过该条件」而保留 existingIds
    private Set<Long> mergeProductIds(Set<Long> existingIds, Set<Long> newIds) {
        if (newIds == null) {
            return existingIds;
        }
        if (newIds.isEmpty()) {
            return new HashSet<>();
        }
        if (existingIds != null && existingIds.isEmpty()) {
            return existingIds;
        }
        if (existingIds == null) {
            return new HashSet<>(newIds);
        }
        existingIds.retainAll(newIds);
        return existingIds;
    }

    /**
     * 保存产品类型关联：先逻辑删除该产品下已有类型，再按入参从 base_data 查名称后写入 product_type 表。
     */
    private void saveProductTypes(Long productId, List<Long> typeIdList) {
        productTypeManager.deleteByProductId(productId);
        List<BaseDataDO> baseDataList = baseDataManager.listByIds(typeIdList);
        Map<Long, String> nameMap = baseDataList.stream()
                .collect(Collectors.toMap(BaseDataDO::getId, BaseDataDO::getValue1, (a, b) -> a));

        for (Long typeId : typeIdList) {
            ProductTypeDO typeDO = new ProductTypeDO();
            typeDO.setProductId(productId);
            typeDO.setTypeId(typeId);
            typeDO.setTypeName(nameMap.getOrDefault(typeId, ""));
            productTypeManager.saveOrUpdate(typeDO);
        }
    }

    /**
     * 保存产品伞框关联
     * @param productId 产品 ID
     * @param dto 伞框信息
     */
    private void saveProductUmbrellaFrame(Long productId, ProductUmbrellaFrameSaveDTO dto) {
        // 删除原来的伞架
        productUmbrellaFrameManager.deleteByProductId(productId);
        ProductUmbrellaFrameDO umbrellaFrameDO = BeanUtils.toBean(dto, ProductUmbrellaFrameDO.class);
        umbrellaFrameDO.setId(null);
        umbrellaFrameDO.setProductId(productId);
        productUmbrellaFrameManager.saveOrUpdate(umbrellaFrameDO);
    }

    private void saveProductMaterials(Long productId, List<ProductMaterialSaveDTO> dtoList) {
        for (ProductMaterialSaveDTO dto : dtoList) {
            ProductMaterialDO materialDO = BeanUtils.toBean(dto, ProductMaterialDO.class);
            materialDO.setProductId(productId);
            productMaterialManager.saveOrUpdate(materialDO);
        }
    }

    private void saveProductFabrics(Long productId, List<ProductFabricSaveDTO> dtoList) {
        for (ProductFabricSaveDTO dto : dtoList) {
            ProductFabricDO fabricDO = BeanUtils.toBean(dto, ProductFabricDO.class);
            fabricDO.setProductId(productId);
            productFabricManager.saveOrUpdate(fabricDO);
        }
    }

    private void saveProductPackaging(Long productId, List<ProductPackagingSaveDTO> dtoList) {
        for (ProductPackagingSaveDTO dto : dtoList) {
            ProductPackagingDO packagingDO = BeanUtils.toBean(dto, ProductPackagingDO.class);
            packagingDO.setProductId(productId);
            productPackagingManager.saveOrUpdate(packagingDO);
        }
    }

    private void saveProductPrinting(Long productId, List<ProductPrintingSaveDTO> dtoList) {
        for (ProductPrintingSaveDTO dto : dtoList) {
            ProductPrintingDO printingDO = BeanUtils.toBean(dto, ProductPrintingDO.class);
            printingDO.setProductId(productId);
            productPrintingManager.saveOrUpdate(printingDO);
        }
    }

    private void saveProductProcessPrices(Long productId, List<ProductProcessPriceSaveDTO> dtoList) {
        for (ProductProcessPriceSaveDTO dto : dtoList) {
            ProductProcessPriceDO processPriceDO = BeanUtils.toBean(dto, ProductProcessPriceDO.class);
            processPriceDO.setProductId(productId);
            productProcessPriceManager.saveOrUpdate(processPriceDO);
        }
    }

    /**
     * 批量填充产品类型列表（从 product_type 关联表查询，分页与详情共用）
     */
    private void fillProductTypes(List<ProductVO> voList) {
        if (voList.isEmpty()) {
            return;
        }
        List<Long> productIds = voList.stream()
                .map(ProductVO::getId)
                .toList();

        List<ProductTypeDO> typeDOList = productTypeManager.listByProductIds(productIds);
        Map<Long, List<ProductTypeVO>> typeMap = typeDOList.stream()
                .collect(Collectors.groupingBy(ProductTypeDO::getProductId,
                        Collectors.mapping(t -> {
                            ProductTypeVO vo = new ProductTypeVO();
                            vo.setTypeId(t.getTypeId());
                            vo.setTypeName(t.getTypeName());
                            return vo;
                        }, Collectors.toList())));

        for (ProductVO vo : voList) {
            vo.setProductTypes(typeMap.getOrDefault(vo.getId(), new ArrayList<>()));
        }
    }

    private void fillProductImages(List<ProductVO> voList) {
        if (voList.isEmpty()) {
            return;
        }
        List<Long> productIds = voList.stream()
                .map(ProductVO::getId)
                .filter(Objects::nonNull)
                .toList();
        List<SystemFileDO> fileList = systemFileManager.listByMainSubAndMasterIds(
                SystemFileMainTypeEnum.PRODUCT,
                SystemFileSubTypeEnum.PRODUCT_IMAGE,
                productIds);
        Map<Long, List<FileVO>> imageMap = new HashMap<>();
        for (SystemFileDO file : fileList) {
            Long masterId = file.getMasterId();
            if (masterId != null) {
                imageMap.computeIfAbsent(masterId, k -> new ArrayList<>())
                        .add(BeanUtils.toBean(file, FileVO.class));
            }
        }
        for (ProductVO vo : voList) {
            if (vo.getId() != null) {
                vo.setImages(imageMap.getOrDefault(vo.getId(), new ArrayList<>()));
            }
        }
    }

    /**
     * 按包材尺寸（解析后比较体积代理）取最大一条，填充箱规与装箱数（分页列表与详情共用）。
     */
    private void fillLargestPackaging(List<ProductVO> voList) {
        if (voList.isEmpty()) {
            return;
        }
        List<Long> productIds = voList.stream()
                .map(ProductVO::getId)
                .filter(Objects::nonNull)
                .toList();
        if (productIds.isEmpty()) {
            return;
        }
        List<ProductPackagingDO> packagingList = productPackagingManager.listByProductIds(productIds);
        Map<Long, List<ProductPackagingDO>> byProduct = packagingList.stream()
                .filter(p -> p.getProductId() != null)
                .collect(Collectors.groupingBy(ProductPackagingDO::getProductId));
        for (ProductVO vo : voList) {
            if (vo.getId() == null) {
                continue;
            }
            List<ProductPackagingDO> rows = byProduct.getOrDefault(vo.getId(), List.of());
            ProductPackagingDO largest = null;
            for (ProductPackagingDO row : rows) {
                if (row == null) {
                    continue;
                }
                if (largest == null
                        || StrUtils.compareByPackagingSize(row.getSize(), largest.getSize()) > 0) {
                    largest = row;
                }
            }
            if (largest != null) {
                vo.setBoxSpec(largest.getSize());
                vo.setBoxCount(largest.getBoxCount());
            }
        }
    }
}
