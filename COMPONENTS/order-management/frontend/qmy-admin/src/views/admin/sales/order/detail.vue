<template>
  <div class="order-detail-container">
    <div class="detail-content">
      <div class="content-left">
        <el-card class="info-card" shadow="never">
          <div class="detail-header">
            <div class="header-left">
              <span class="order-label">订单号：</span>
              <span v-copy="orderInfo.orderCode" class="order-number order-number--copyable" title="点击复制">
                {{ orderInfo.orderCode }}
              </span>
              <el-tag type="info" size="large" class="status-tag status-primary">
                <span class="tag-content">
                  {{ orderInfo.shippingMethod }}
                  <el-icon
                    class="edit-icon"
                    v-permission="'sal:yt:order:updateShippingMethod'"
                    @click="handleEditShippingMethod"
                  >
                    <Edit />
                  </el-icon>
                </span>
              </el-tag>
              <!-- <el-tag type="success" size="large" class="status-tag status-success">
                {{ orderInfo.orderTypeLabel }}
              </el-tag> -->
              <el-tag type="warning" size="large" class="status-tag status-warning">
                {{ orderInfo.isInboundDeliveryLabel }}
              </el-tag>
            </div>
            <div class="header-right">
              <el-button
                v-if="showConfirmButtons"
                type="primary"
                v-permission="'sal:yt:order:confirmComplete'"
                @click="handleConfirmComplete"
              >
                确认完成
              </el-button>
              <el-button
                v-if="showConfirmButtons"
                type="primary"
                v-permission="'sal:yt:order:confirmDelivery'"
                @click="handleConfirmShipment"
              >
                确认发货
              </el-button>
              <!-- prettier-ignore -->
              <el-button v-if="orderInfo.subStatus < 4" type="danger" :disabled="!canClickCloseOrder" @click="handleCloseOrder">
                关闭订单
              </el-button>
              <el-button type="primary" @click="handleExport">导出</el-button>
            </div>
          </div>

          <div class="info-section">
            <div class="info-left">
              <div class="info-row">
                <div class="info-item">
                  <span class="label">平台名称：</span>
                  <span class="value">{{ orderInfo.sourcePlatform }}</span>
                </div>
                <div class="info-item">
                  <span class="label">平台单号：</span>
                  <span class="value">{{ orderInfo.platformOrderCode }}</span>
                </div>
                <div class="info-item">
                  <span class="label">币种：</span>
                  <span class="value">{{ orderInfo.currencyLabel }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">下单时间：</span>
                  <span class="value">{{ formatDate(orderInfo.orderTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">交货时间：</span>
                  <span class="value">{{ formatDate(orderInfo.deliveryTime) }}</span>
                </div>
                <div class="info-item">
                  <span class="label">完成时间：</span>
                  <span class="value">{{ getCompletionTime(orderInfo) }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item">
                  <span class="label">优惠金额：</span>
                  <span class="value">{{ currencySymbol }}{{ orderInfo.discountAmount }}</span>
                </div>
                <div class="info-item">
                  <span class="label">运费金额：</span>
                  <span class="value">{{ currencySymbol }}{{ orderInfo.shippingCost }}</span>
                </div>
                <div class="info-item">
                  <span class="label">运费：</span>
                  <span class="value status-badge">{{ orderInfo.isCollectedShippingCost }}</span>
                </div>
              </div>
              <div class="info-row">
                <div class="info-item full-width">
                  <span class="label">订单备注：</span>
                  <span class="value status-badge">{{ orderInfo.remark }}</span>
                </div>
              </div>
            </div>
            <div class="info-right">
              <div class="status-item">
                <div class="status-label">状态</div>
                <div class="status-value">{{ orderInfo.subStatusText }}</div>
              </div>
              <div class="amount-item">
                <div class="amount-label">订单金额</div>
                <div class="amount-value">{{ currencySymbol }}{{ orderInfo.amount }}</div>
              </div>
              <div v-if="showCloseAmount" class="amount-item">
                <div class="amount-label">关闭金额</div>
                <div class="amount-value amount-value--close">{{ currencySymbol }}{{ orderInfo.endAmount }}</div>
                <div class="amount-extra">其他金额 {{ currencySymbol }}{{ orderInfo.endOtherAmount }}</div>
              </div>
            </div>
          </div>

          <div class="progress-section">
            <div class="section-title">流程进度</div>
            <el-steps :active="currentStep" align-center>
              <el-step
                v-for="(step, index) in progressSteps"
                :key="index"
                :title="step"
                :description="getProgressStepTime(step)"
              />
            </el-steps>
          </div>
        </el-card>
      </div>

      <div class="content-right">
        <div class="section-card">
          <div class="section-title">客户信息</div>
          <div class="section-content">
            <div class="info-item">
              <span class="label">客户：</span>
              <span class="value">{{ orderInfo.customerName }}</span>
            </div>
            <div class="info-item">
              <span class="label">收货地址：</span>
              <span class="value">{{ orderInfo.customerAddress }}</span>
            </div>
            <div class="info-item">
              <span class="label">收货人：</span>
              <span class="value">{{ orderInfo.receiver }}</span>
            </div>
            <div class="info-item">
              <span class="label">联系方式：</span>
              <span class="value">{{ orderInfo.receiverPhone }}</span>
            </div>
          </div>
        </div>

        <div class="section-card" style="padding-right: 5px">
          <div class="section-title">销售信息</div>
          <div class="section-content">
            <div class="info-row">
              <div class="info-item">
                <span class="label">业务员：</span>
                <span class="value">{{ orderInfo.salesEmployeeName }}</span>
              </div>
              <div class="info-item">
                <span class="label">业绩占比：</span>
                <span class="value">{{ orderInfo.saleRatio }}</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-item">
                <span class="label">跟进员：</span>
                <span class="value">{{ orderInfo.followEmployeeName }}</span>
              </div>
              <div class="info-item">
                <span class="label">业绩占比：</span>
                <span class="value">{{ orderInfo.followRatio }}</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-item">
                <span class="label">预计成本：</span>
                <span class="value">¥{{ orderInfo.costAmount }}</span>
              </div>
              <div class="info-item">
                <span class="label">预计毛利：</span>
                <span class="value">¥{{ orderInfo.profitAmount }}</span>
              </div>
            </div>
            <div class="info-row">
              <div class="info-item">
                <span class="label">订单回款：</span>
                <span class="value">{{ orderInfo.orderPayment }}</span>
                <el-popover placement="top" trigger="click" :width="180">
                  <div>时间：{{ formatDate(orderInfo.receiveFinishTime) }}</div>
                  <template #reference>
                    <el-icon class="detail-time-trigger"><InfoFilled /></el-icon>
                  </template>
                </el-popover>
              </div>
              <div class="info-item">
                <span class="label">客户运费：</span>
                <span class="value">{{ orderInfo.customerShipping }}</span>
                <el-popover placement="top" trigger="click" :width="180">
                  <div>时间：{{ formatDate(orderInfo.shippingReceiveFinishTime) }}</div>
                  <template #reference>
                    <el-icon class="detail-time-trigger"><InfoFilled /></el-icon>
                  </template>
                </el-popover>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <el-card class="tabs-card" shadow="never">
      <el-tabs v-model="activeTab">
        <el-tab-pane v-if="showSemiFinishedTab" label="半成品" name="semiFinished" />
        <el-tab-pane label="产品" name="product" />
        <el-tab-pane v-if="showLogisticsTab" label="物流" name="logistics" />
        <el-tab-pane label="退货" name="refund" />
      </el-tabs>

      <!-- 半成品内容 -->
      <div v-if="activeTab === 'semiFinished'" class="tab-content">
        <div class="tab-header">
          <div class="search-group">
            <el-input v-model="semiFinishedSearch.productCode" placeholder="产品ID" style="width: 180px" />
            <el-input v-model="semiFinishedSearch.specName" placeholder="规格名称" style="width: 180px" />
            <el-input v-model="semiFinishedSearch.pic" placeholder="图片" style="width: 180px" />
            <el-button type="primary" @click="handleSemiFinishedSearch">搜索</el-button>
            <el-button style="margin-left: 0" @click="handleSemiFinishedReset">重置</el-button>
          </div>
        </div>

        <el-table :data="semiFinishedList" border style="margin-top: 15px">
          <el-table-column label="产品ID" prop="productCode" align="center" width="170" />
          <el-table-column label="产品图片" align="center" width="200">
            <template #default="{ row }">
              <el-image
                v-if="row.productImage"
                :src="row.productImage"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleImagePreview(row.productImage)"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="已确认/总数量" align="center" width="220">
            <template #default="{ row }">
              <div style="display: flex; align-items: center; gap: 10px; padding: 0 10px">
                <el-progress :percentage="row.progressPercentage" :show-text="false" style="flex: 1" />
                <span style="font-size: 13px; color: #606266; white-space: nowrap">{{ row.progressText }}</span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="定制化属性" prop="customAttribute" align="center" />
          <el-table-column label="单价" prop="price" align="center" />
          <el-table-column label="总价" prop="totalPrice" align="center" />
          <el-table-column label="成本" prop="supplierPrice" align="center" />
          <el-table-column label="状态" prop="confirmStatusName" align="center" />
          <el-table-column label="操作" fixed="right" align="center" width="150">
            <template #default="{ row }">
              <el-button type="primary" v-if="row.confirmStatus != 1" link @click="handleSemiConfirm(row)">
                确认
              </el-button>
              <!-- <el-button
                type="primary"
                v-if="row.confirmStatus != 1"
                link
                v-permission="'sal:yt:order:returnItem'"
                @click="handleSemiRefund(row)"
              >
                退货
              </el-button> -->
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 产品内容 -->
      <div v-if="activeTab === 'product'" class="tab-content">
        <div class="tab-header">
          <div class="filter-group">
            <el-button :class="{ active: productFilter === 'all' }" @click="handleFilterChange('all')">
              全部 ({{ statusCount.all }})
            </el-button>
            <!-- 成品单显示待采购 -->
            <el-button
              v-if="orderInfo.hasHalfProductOrder == 0"
              :class="{ active: productFilter === 'pending' }"
              @click="handleFilterChange('pending')"
            >
              待采购 ({{ statusCount.pending }})
            </el-button>
            <!-- 入库发货显示待入库（成品单+入库发货 或 半成品单+入库发货） -->
            <el-button
              v-if="orderInfo.isInboundDelivery === '1'"
              :class="{ active: productFilter === 'warehouse' }"
              @click="handleFilterChange('warehouse')"
            >
              待入库 ({{ statusCount.warehouse }})
            </el-button>
            <!-- 入库发货显示待打包 -->
            <el-button
              v-if="orderInfo.isInboundDelivery === '1'"
              :class="{ active: productFilter === 'packing' }"
              @click="handleFilterChange('packing')"
            >
              待打包 ({{ statusCount.packing }})
            </el-button>
            <el-button :class="{ active: productFilter === 'shipping' }" @click="handleFilterChange('shipping')">
              待发货 ({{ statusCount.shipping }})
            </el-button>
            <el-button :class="{ active: productFilter === 'shipped' }" @click="handleFilterChange('shipped')">
              已发货 ({{ statusCount.shipped }})
            </el-button>
            <el-button
              v-if="statusCount.closed > 0"
              :class="{ active: productFilter === 'closed' }"
              @click="handleFilterChange('closed')"
            >
              已关闭 ({{ statusCount.closed }})
            </el-button>
            <!-- 供应商发货显示已完成 -->
            <el-button
              v-if="orderInfo.isInboundDelivery === '0'"
              :class="{ active: productFilter === 'completed' }"
              @click="handleFilterChange('completed')"
            >
              已完成 ({{ statusCount.completed }})
            </el-button>
          </div>
          <div class="search-group">
            <el-input v-model="searchProduct" placeholder="产品ID" style="width: 180px" />
            <el-input v-model="searchSpec" placeholder="规格名称" style="width: 180px" />
            <el-input v-model="searchPic" placeholder="图片" style="width: 180px" />
            <el-button type="primary" @click="handleSearch">搜索</el-button>
            <el-button style="margin-left: 0" @click="handleReset">重置</el-button>
          </div>
        </div>

        <el-table :data="displayProductList" border style="margin-top: 15px" :span-method="spanMethod">
          <el-table-column label="产品ID" prop="productCode" align="center" width="170" />
          <el-table-column label="总数" prop="number" align="center" />
          <el-table-column label="总价" prop="totalPrice" align="center" />
          <el-table-column label="规格图片" align="center" width="100">
            <template #default="{ row }">
              <el-image
                v-if="row.specImage"
                :src="row.specImage"
                fit="cover"
                style="width: 40px; height: 40px"
                @click="handleImagePreview(row.specImage)"
              />
            </template>
          </el-table-column>
          <el-table-column label="规格名称" align="center" width="120">
            <template #default="{ row }">
              <div class="spec-name-content">
                <template v-for="(segment, index) in row.specSegments" :key="index">
                  <span
                    class="spec-name-segment"
                    :class="{ 'spec-name-segment--colored': !!segment.color }"
                    :style="{
                      backgroundColor: segment.color || undefined,
                      color: segment.color ? getContrastTextColor(segment.color) : undefined
                    }"
                  >
                    {{ segment.text }}
                  </span>
                  <span v-if="index < row.specSegments.length - 1" class="spec-name-separator">+</span>
                </template>
              </div>
            </template>
          </el-table-column>
          <el-table-column
            :label="orderInfo.isInboundDelivery === '1' ? '已发货/已入库/总数量' : '已发货/总数量'"
            align="center"
            width="200"
          >
            <template #default="{ row }">
              <div style="display: flex; align-items: center; gap: 10px; padding: 0 10px">
                <el-progress :percentage="row.progressPercentage" :show-text="false" style="flex: 1" />
                <span style="font-size: 13px; white-space: nowrap">
                  <span style="color: #67c23a">{{ row.deliveryNumber }}</span>
                  <template v-if="orderInfo.isInboundDelivery === '1'">
                    <span>/</span>
                    <span style="color: #409eff">{{ row.enterNumber }}</span>
                  </template>
                  <span>/</span>
                  <span style="color: #606266">{{ row.totalNumber }}</span>
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="定制化属性" prop="labelName" align="center" />
          <el-table-column label="单价" prop="priceValue" align="center" />
          <el-table-column label="总价" prop="totalUnitPrice" align="center" />
          <el-table-column label="成本" prop="supplierPriceValue" align="center" />
          <el-table-column label="订单规格备注" prop="remark" align="center" width="200" />
          <el-table-column label="状态" prop="status" align="center" />
          <el-table-column label="操作" fixed="right" align="center" min-width="150">
            <template #default="{ row }">
              <!-- <el-button
                v-if="false"
                type="primary"
                link
                v-permission="'sal:yt:order:returnItem'"
                @click="handleRefund(row)"
              >
                退货
              </el-button> -->
              <el-button type="primary" link @click="handleProductProgress(row)">产品进度</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 物流内容 -->
      <div v-if="activeTab === 'logistics'" class="tab-content">
        <div class="tab-header">
          <div class="search-group">
            <el-input v-model="logisticsSearch.deliveryCode" placeholder="发货单号" style="width: 180px" />
            <el-date-picker
              v-model="logisticsSearch.deliveryTime"
              type="date"
              placeholder="发货时间"
              style="width: 180px"
              value-format="YYYY-MM-DD HH:mm:ss"
            />
            <el-input v-model="logisticsSearch.productCode" placeholder="产品ID" style="width: 180px" />
            <el-button type="primary" @click="handleLogisticsSearch">搜索</el-button>
            <el-button @click="handleLogisticsReset" style="margin-left: 0">重置</el-button>
          </div>
        </div>

        <el-table :data="logisticsList" border style="margin-top: 15px" :span-method="logisticsSpanMethod">
          <el-table-column label="发货单号" prop="deliveryCode" align="center" width="160" />
          <el-table-column label="发货时间" prop="deliveryTime" align="center" />
          <el-table-column label="收货地址" prop="address" align="center" />
          <el-table-column label="物流公司" prop="transportCompanyName" align="center" />
          <el-table-column label="面单/物流单号" prop="packageCode" align="center">
            <template #default="{ row }">
              <el-image
                v-if="row.waybillUrl"
                :src="row.waybillUrl"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleViewWaybill(row)"
              />
              <span v-else-if="row.packageCode">{{ row.packageCode }}</span>
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="打包箱箱号" prop="boxCode" align="center" />
          <el-table-column label="打包箱尺寸" prop="boxSize" align="center" />
          <el-table-column label="打包箱重量" prop="boxWeight" align="center" />
          <el-table-column label="操作" fixed="right" align="center" width="120">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleLogisticsDetail(row)">详情</el-button>
              <el-button type="primary" link @click="handleLogisticsExport(row)">导出</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <!-- 退货内容 -->
      <div v-if="activeTab === 'refund'" class="tab-content">
        <div class="tab-header">
          <div class="search-group">
            <el-input v-model="refundSearch.productCode" placeholder="产品ID" style="width: 180px" />
            <el-input v-model="refundSearch.specificationName" placeholder="规格名称" style="width: 180px" />
            <el-date-picker
              v-if="refundViewType === 'list'"
              v-model="refundSearch.dateRange"
              type="daterange"
              range-separator="至"
              start-placeholder="开始时间"
              end-placeholder="结束时间"
              style="width: 330px"
              value-format="YYYY-MM-DD"
            />
            <el-button type="primary" @click="handleRefundSearch">搜索</el-button>
            <el-button @click="handleRefundReset" style="margin-left: 0">重置</el-button>
          </div>
          <el-button-group>
            <el-button :type="refundViewType === 'list' ? 'primary' : ''" @click="refundViewType = 'list'">
              <el-icon><Operation /></el-icon>
            </el-button>
            <el-button :type="refundViewType === 'detail' ? 'primary' : ''" @click="refundViewType = 'detail'">
              <el-icon><Grid /></el-icon>
            </el-button>
          </el-button-group>
        </div>

        <!-- 详情视图 -->
        <el-table
          v-if="refundViewType === 'detail'"
          :data="refundDetailList"
          border
          style="margin-top: 15px; width: 100%"
        >
          <el-table-column label="产品ID" prop="productCode" align="center" width="200" />
          <el-table-column label="规格图片" align="center">
            <template #default="{ row }">
              <el-image
                v-if="row.specImage"
                :src="row.specImage"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleImagePreview(row.specImage)"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="规格名称" prop="specName" align="center" />
          <el-table-column label="订单退货信息" align="center">
            <template #default="{ row }">
              <div style="text-align: center">
                <div>初始数量：{{ row.initialNumber }}</div>
                <div>总退货数量：{{ row.totalReturnNumber }}</div>
                <div>当前数量：{{ row.remainingNumber }}</div>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" fixed="right" align="center" width="150">
            <template #default="{ row }">
              <el-button type="primary" link @click="handleRefundDetailDialog(row)">详情</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 列表视图（默认） -->
        <el-table v-else :data="refundList" border style="margin-top: 15px; width: 100%">
          <el-table-column label="产品ID" prop="productCode" align="center" width="170" />
          <el-table-column label="规格图片" align="center">
            <template #default="{ row }">
              <el-image
                v-if="row.specImage"
                :src="row.specImage"
                fit="cover"
                style="width: 40px; height: 40px; cursor: pointer"
                @click="handleImagePreview(row.specImage)"
              />
              <span v-else>-</span>
            </template>
          </el-table-column>
          <el-table-column label="规格名称" prop="specName" align="center" />
          <el-table-column label="订单退货数量" prop="refundCount" align="center" />
          <el-table-column label="退货人" prop="refundUser" align="center" />
          <el-table-column label="退货时间" prop="refundTime" align="center" />
          <el-table-column label="订单退货原因" prop="returnReason" align="center" min-width="150" />
        </el-table>
      </div>
    </el-card>
  </div>
  <!-- 图片预览 -->
  <el-image-viewer
    v-if="showViewer"
    :url-list="viewerImageList"
    :initial-index="0"
    hide-on-click-modal
    @close="showViewer = false"
  />

  <!-- 关闭订单弹窗 -->
  <el-dialog v-model="closeOrderVisible" title="关闭订单" width="1200px" :close-on-click-modal="false">
    <el-table :data="closeOrderList" border>
      <el-table-column label="产品ID" prop="productCode" align="center" width="120" />
      <el-table-column label="规格图片" align="center" width="90">
        <template #default="{ row }">
          <el-image
            v-if="row.specImage"
            :src="row.specImage"
            fit="cover"
            style="width: 50px; height: 50px; cursor: pointer"
            :preview-src-list="[row.specImage]"
            preview-teleported
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="规格名称" prop="specName" align="center" min-width="160" />
      <el-table-column label="下单数量" prop="totalNumber" align="center" width="90" />
      <el-table-column label="待采购" prop="pendingPurchaseQty" align="center" width="90" />
      <el-table-column label="待入库" prop="waitEnterQty" align="center" width="90" />
      <el-table-column label="待打包" prop="waitPackageQty" align="center" width="90" />
      <el-table-column label="待发货" prop="waitDeliveryQty" align="center" width="90" />
      <el-table-column label="已发货" prop="deliveredQty" align="center" width="90" />
      <el-table-column label="可关闭数量" align="center" width="110">
        <template #default="{ row }">
          <span style="color: #f56c6c">{{ row.refundQty || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="单价" align="center" width="100">
        <template #default="{ row }">{{ currencySymbol }}{{ row.price }}</template>
      </el-table-column>
      <el-table-column label="总价" align="center" width="110">
        <template #default="{ row }">
          {{ currencySymbol }}{{ ((row.price || 0) * (row.refundQty || 0)).toFixed(2) }}
        </template>
      </el-table-column>
    </el-table>
    <div class="close-order-footer">
      <div class="close-order-extra">
        <span class="extra-label">其他金额：</span>
        <!-- prettier-ignore -->
        <el-input v-model="closeOrderExtraAmount" placeholder="请输入" style="width: 150px" />
      </div>
      <span class="close-order-total">
        退款总价：
        <strong>{{ currencySymbol }}{{ closeOrderTotalAmount }}</strong>
      </span>
    </div>
    <template #footer>
      <el-button @click="closeOrderVisible = false">取消</el-button>
      <el-button
        type="primary"
        :loading="closeOrderSubmitting"
        :disabled="closeOrderSubmitting"
        @click="handleCloseOrderConfirm"
      >
        确定
      </el-button>
    </template>
  </el-dialog>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Grid, Operation, Edit, InfoFilled } from '@element-plus/icons-vue'
import { dynamic } from '@bzlab/bz-core'
import dayjs from 'dayjs'
import {
  getOrderDetailNew,
  getOrderDetailProductList,
  getOrderInCompleteList,
  getReturnOrderList,
  getReturnOrderStats,
  confirmComplete,
  closeOrder,
  getCloseOrderPreview,
  getDeliveryInfo,
  getExchangeRate
} from '@/api/admin/sales/order'
import {
  getPlatformLabel,
  getCurrencyLabel,
  getOrderStatusLabel,
  getShippingMethodLabel,
  getDeliveryTypeLabel,
  getIsWarehouseDeliveryLabel
} from '@/constant/yitang/sales'
import { getReceivableStatusLabel } from '@/constant/yitang/finance'
import refundDialog from './components/refund-dialog.vue'
import productProgress from './components/product-progress.vue'
import exportDialog from './components/export-dialog.vue'
import editShippingMethodDialog from './components/edit-shipping-method-dialog.vue'
import confirmShipmentDialog from './components/confirm-shipment-dialog.vue'
import confirmDialog from './components/confirm-dialog.vue'
import refundDetailDialog from './components/refund-detail-dialog.vue'
import logisticsDetailDialog from './components/logistics-detail-dialog.vue'
import { downloadAxiosBlobFile } from '@/utils/download'
import { getYitangAdminToken, getYitangAdminTenantInfo } from '@/utils/auth'

const formatDate = (date: string) => (date ? dayjs(date).format('YYYY-MM-DD') : '-')
const getContrastTextColor = (backgroundColor?: string) => {
  if (!backgroundColor) return '#303133'
  const normalized = backgroundColor.trim().replace('#', '')
  if (!/^[0-9a-fA-F]{6}$/.test(normalized)) return '#303133'

  const red = parseInt(normalized.slice(0, 2), 16)
  const green = parseInt(normalized.slice(2, 4), 16)
  const blue = parseInt(normalized.slice(4, 6), 16)
  const brightness = red * 0.299 + green * 0.587 + blue * 0.114
  return brightness > 186 ? '#303133' : '#ffffff'
}
const getProgressStepTime = (step: string) => {
  if (step === '创建') return formatDate(orderInfo.value.submitOrderTime)
  if (step === '发货') return formatDate(orderInfo.value.orderFinishTime)
  return ''
}
const getCompletionTime = (info: any) => {
  const finishTimeList = [info.orderFinishTime, info.receiveFinishTime, info.shippingReceiveFinishTime].filter(
    (time: string | null | undefined) => Boolean(time)
  )
  if (finishTimeList.length !== 3) return '-'
  return finishTimeList
    .map((time: string) => dayjs(time))
    .sort((prev, next) => next.valueOf() - prev.valueOf())[0]
    .format('YYYY-MM-DD')
}

const route = useRoute()

const orderInfo = ref<any>({
  orderCode: '',
  sourcePlatform: '',
  platformOrderCode: '',
  invoiceNo: '',
  currency: '',
  currencyLabel: '',
  orderTime: '',
  deliveryTime: '',
  submitOrderTime: '',
  orderFinishTime: '',
  discountAmount: '',
  shippingCost: '',
  isCollectedShippingCost: '',
  remark: '',
  hasHalfProductOrder: 0,
  shippingMethod: '',
  isInboundDelivery: '',
  subStatusText: '',
  subStatus: '',
  createTime: '',
  amount: '',
  customerName: '',
  customerAddress: '',
  salesEmployeeName: '',
  saleRatio: '',
  followEmployeeName: '',
  followRatio: '',
  costAmount: '',
  profitAmount: '',
  orderPayment: '',
  customerShipping: '',
  receiveFinishTime: '',
  shippingReceiveFinishTime: '',
  statusCountMap: {}
})

// 人民币显示¥，美元显示$
const currencySymbol = computed(() => {
  return orderInfo.value.currency === '0' ? '¥' : '$'
})

// 汇率配置（美元兑人民币）
const exchangeRate = ref(1)

const loadExchangeRate = async () => {
  const { code, data } = await getExchangeRate({ code: 'exchangeRate' })
  if (code === 200 && Array.isArray(data) && data.length > 0) {
    exchangeRate.value = Number(data[0].value) || 1
  }
}

// 根据订单类型和发货方式生成流程步骤
const progressSteps = computed(() => {
  const hasHalfProductOrder = Number(orderInfo.value.hasHalfProductOrder) || 0 // 0:没有半成品, 1:有半成品
  const isInboundDelivery = orderInfo.value.isInboundDelivery // 1:入库发货, 0:供应商发货

  if (hasHalfProductOrder === 0) {
    // 成品单-入库发货
    if (isInboundDelivery === '1') {
      return ['创建', '采购', '入库', '打包', '发货']
    }
    // 成品单-供应商发货
    return ['创建', '采购', '发货', '完成']
  }
  // 半成品单
  if (isInboundDelivery === '1') {
    // 半成品单-入库发货
    return ['创建', '采购', '确认', '入库', '打包', '发货']
  }
  // 半成品单-供应商发货
  return ['创建', '采购', '确认', '发货', '完成']
})

// 根据订单状态计算当前流程步骤
// status: 0:待采购, 1:待入库, 2:待打包, 3:待发货, 4:已发货, 5:已完成, 6:待确认, 7:已关闭
const currentStep = computed(() => {
  const status = Number(orderInfo.value.subStatus) || 0
  const hasHalfProductOrder = Number(orderInfo.value.hasHalfProductOrder) || 0
  const isInboundDelivery = orderInfo.value.isInboundDelivery

  if (hasHalfProductOrder === 0) {
    // 成品单-入库发货
    if (isInboundDelivery === '1') {
      return Math.min(status + 1, 5)
    }
    // 成品单-供应商发货: ['创建', '采购', '发货', '完成']
    if (status === 0) return 1 // 待采购
    if (status === 3) return 2 // 待发货
    if (status === 4) return 3 // 已发货
    if (status === 5) return 4 // 已完成
    if (status === 7) return 4 // 已关闭
    return 1
  }
  // 半成品单
  if (isInboundDelivery === '1') {
    // 半成品单-入库发货: ['创建', '采购', '确认', '入库', '打包', '发货']
    if (status === 0) return 1 // 待采购
    if (status === 6) return 2 // 待确认
    if (status === 1) return 3 // 待入库
    if (status === 2) return 4 // 待打包
    if (status === 3) return 5 // 待发货
    if (status === 4) return 6 // 已发货
    if (status === 7) return 6 // 已关闭
    return 1
  }
  // 半成品单-供应商发货: ['创建', '采购', '确认', '发货', '完成']
  if (status === 0) return 1 // 待采购
  if (status === 6) return 2 // 待确认
  if (status === 3) return 3 // 待发货
  if (status === 4) return 4 // 已发货
  if (status === 5) return 5 // 已完成
  if (status === 7) return 5 // 已关闭
  return 1
})
const activeTab = ref('')

const loadedTabs = ref<Set<string>>(new Set())

const productFilter = ref('all')
const searchProduct = ref('')
const searchSpec = ref('')
const searchPic = ref('')

const productList = ref<any[]>([])
const displayProductList = ref<any[]>([])
const orderId = ref<string>('')
const createEmptyCloseOrderPreview = () => ({
  canClose: false,
  message: '',
  pendingPurchaseQty: 0,
  waitEnterQty: 0,
  waitPackageQty: 0,
  waitDeliveryQty: 0,
  deliveredQty: 0,
  closableQty: 0,
  itemList: []
})
const closeOrderPreview = ref<any>(createEmptyCloseOrderPreview())

const statusCount = computed(() => {
  const countMap = orderInfo.value.statusCountMap || {}
  const pending = Number(countMap['0']) || 0
  const warehouse = Number(countMap['1']) || 0
  const packing = Number(countMap['2']) || 0
  const shipping = Number(countMap['3']) || 0
  const shipped = Number(countMap['4']) || 0
  const completed = Number(countMap['5']) || 0
  const closed = Number(countMap['7']) || 0
  const all = pending + warehouse + packing + shipping + shipped + completed + closed

  return {
    all,
    pending,
    warehouse,
    packing,
    shipping,
    shipped,
    completed,
    closed
  }
})

const showSemiFinishedTab = computed(() => {
  return Number(orderInfo.value.hasHalfProductOrder) === 1
})

const showLogisticsTab = computed(() => {
  return orderInfo.value.isInboundDelivery === '1'
})

const showCloseAmount = computed(() => {
  return (
    Number(orderInfo.value.subStatus) === 7 ||
    Number(orderInfo.value.endAmount || 0) > 0 ||
    Number(orderInfo.value.endOtherAmount || 0) > 0
  )
})

const showConfirmButtons = computed(() => {
  return orderInfo.value.isInboundDelivery === '0'
})

// 物流相关
const logisticsSearch = ref({
  deliveryCode: '',
  address: '',
  deliveryTime: '',
  productCode: ''
})
const logisticsList = ref<any[]>([])

const loadLogisticsList = async () => {
  const orderId = route.query.id
  const { code, data, message } = await getDeliveryInfo({
    orderId: orderId,
    deliveryCode: logisticsSearch.value.deliveryCode || null,
    deliveryTime: logisticsSearch.value.deliveryTime || null,
    productCode: logisticsSearch.value.productCode || null
  })
  if (code !== 200) return ElMessage.warning(message)
  const result: any[] = []
  ;(data || []).forEach((item: any) => {
    const boxList = item.boxList || []
    const boxes = boxList.length ? boxList : [null]
    const baseInfo = {
      ...item,
      deliveryCode: item.code || '-',
      deliveryTime: item.deliveryTime ? dayjs(item.deliveryTime).format('YYYY-MM-DD HH:mm') : '-',
      address: item.address || '-',
      transportCompanyName: item.transportCompanyName || '-',
      waybillUrl: item.transportOrderFileUrl || ''
    }

    boxes.forEach((box: any, index: number) => {
      result.push({
        ...baseInfo,
        boxSize: box?.boxSize ?? '-',
        boxCode: box?.boxCode ?? '-',
        boxWeight: box?.boxWeight ? `${box.boxWeight}kg` : '-',
        boxId: box?.id || null,
        isFirstRow: index === 0,
        rowSpan: index === 0 ? boxes.length : 0
      })
    })
  })

  logisticsList.value = result
}

const logisticsSpanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex <= 4 || columnIndex === 8) {
    if (row.isFirstRow) {
      return { rowspan: row.rowSpan, colspan: 1 }
    }
    return { rowspan: 0, colspan: 0 }
  }
}

const handleLogisticsSearch = () => {
  loadLogisticsList()
}

const handleLogisticsReset = () => {
  logisticsSearch.value = {
    deliveryCode: '',
    address: '',
    deliveryTime: '',
    productCode: ''
  }
  loadLogisticsList()
}

const handleViewWaybill = (row: any) => {
  if (row.waybillUrl) {
    viewerImageList.value = [row.waybillUrl]
    showViewer.value = true
  } else {
    ElMessage.warning('暂无面单信息')
  }
}

const handleLogisticsDetail = (row: any) => {
  const params = {
    id: 'logisticsDetailDialog',
    el: '#app',
    data: {
      rowData: row
    },
    render: logisticsDetailDialog
  }
  dynamic.show(params)
}

const handleLogisticsExport = async (row: any) => {
  console.log('handleLogisticsExport', row)
  const { status, message } = await downloadAxiosBlobFile({
    url: `${import.meta.env.VITE_APP_YITANG_BASE_API}/sal/yt/order/exportDelivery`,
    headers: {
      'qiaomoyun-tenant': getYitangAdminTenantInfo()?.id,
      'qiaomoyun-token': getYitangAdminToken()
    },
    data: {
      orderId: orderId.value,
      deliveryId: row.id
    },
    method: 'post'
  })
  if (status !== 200) return ElMessage.warning(message)
  ElMessage.success('导出成功')
}

// 半成品相关
const semiFinishedSearch = ref({
  productCode: '',
  specName: '',
  pic: ''
})
const semiFinishedList = ref<any[]>([])
const showViewer = ref(false)
const viewerImageList = ref<string[]>([])

const handleImagePreview = (imageUrl: string) => {
  if (!imageUrl) return
  viewerImageList.value = [imageUrl]
  showViewer.value = true
}

// 加载半成品列表
const loadSemiFinishedList = async () => {
  const orderId = route.query.id
  const { code, data, message } = await getOrderInCompleteList({
    productCodeOrOrderSubCode: semiFinishedSearch.value.productCode || null,
    confirmStatus: null,
    orderId: orderId
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  const list = data || []
  semiFinishedList.value = list.map((item: any) => {
    const confirmNumber = item.confirmNumber || 0
    const totalNumber = item.number || 0
    const progressPercentage = totalNumber > 0 ? Math.round((confirmNumber / totalNumber) * 100) : 0

    return {
      ...item,
      id: item.id,
      productId: item.productId,
      productCode: item.productCode,
      productImage: item.imageList?.[0]?.url || '',
      progressPercentage,
      progressText: `${confirmNumber}/${totalNumber}`,
      customAttribute: item.labelName || '/',
      price: item.price || 0,
      totalPrice: (item.price || 0) * (item.number || 0),
      supplierPrice: item.supplierPrice || 0,
      confirmStatus: item.confirmStatus,
      confirmStatusName: item.confirmStatus == '0' ? '待确认' : '已确认',
      confirmNumber,
      number: totalNumber
    }
  })
}

const handleSemiFinishedSearch = () => {
  loadSemiFinishedList()
}

const handleSemiFinishedReset = () => {
  semiFinishedSearch.value = {
    productCode: '',
    specName: '',
    pic: ''
  }
  loadSemiFinishedList()
}

const handleSemiConfirm = (row: any) => {
  const params = {
    id: 'confirmDialog',
    el: '#app',
    data: {
      productData: row,
      onSuccess: () => {
        loadSemiFinishedList()
      }
    },
    render: confirmDialog
  }
  dynamic.show(params)
}

const handleSemiRefund = (row: any) => {
  console.log('handleSemiRefund', row)

  const params = {
    id: 'refundDialog',
    el: '#app',
    data: {
      rowData: {
        orderId: orderId.value,
        productId: row.productId,
        price: row.price,
        labelId: row.labelId,
        remark: row.remark || '',
        specificationId: row.specificationId,
        supplierPrice: row.supplierPrice
      },
      onSuccess: () => {
        loadOrderDetail()
      }
    },
    render: refundDialog
  }
  dynamic.show(params)
}
handleSemiRefund

// 退货相关
const refundViewType = ref('list')
const refundSearch = ref({
  productCode: '',
  specificationName: '',
  dateRange: [] as string[]
})
const refundList = ref<any[]>([])
const refundDetailList = ref<any[]>([])

const loadRefundList = async () => {
  const orderId = route.query.id
  let startTime: string | undefined = undefined
  let endTime: string | undefined = undefined
  if (refundSearch.value.dateRange && refundSearch.value.dateRange.length === 2) {
    startTime = dayjs(refundSearch.value.dateRange[0]).format('YYYY-MM-DD HH:mm:ss')
    endTime = dayjs(refundSearch.value.dateRange[1]).format('YYYY-MM-DD HH:mm:ss')
  }
  const { code, data, message } = await getReturnOrderList({
    orderId: orderId,
    productCode: refundSearch.value.productCode || undefined,
    specificationName: refundSearch.value.specificationName || undefined,
    startTime,
    endTime
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  refundList.value = (data || []).map((item: any) => {
    const specName = item.itemList?.map((spec: any) => spec.categorySpecificationItemValue || '').join('-') || '-'

    return {
      id: item.id,
      productCode: item.productCode || '',
      specImage: item.imageList?.[0]?.url || '',
      specName,
      refundCount: item.returnNumber || 0,
      refundUser: item.returnUserName || '-',
      refundTime: item.createTime || '-',
      returnReason: item.reason || '-'
    }
  })
}

// 加载退货统计数据
const loadRefundDetailList = async () => {
  const orderId = route.query.id
  const { code, data, message } = await getReturnOrderStats({
    orderId: orderId,
    productCode: refundSearch.value.productCode || undefined,
    specificationName: refundSearch.value.specificationName || undefined
  })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }
  refundDetailList.value = (data || []).map((item: any) => {
    const specName = item.itemList?.map((spec: any) => spec.categorySpecificationItemValue || '').join('-') || '-'

    const orderSubItemId = item.returnOrderList?.[0]?.orderSubItemId || ''

    return {
      id: item.specificationId || item.productCode,
      orderSubItemId,
      productCode: item.productCode || '',
      specImage: item.imageList?.[0]?.url || '',
      specName,
      initialNumber: item.initialNumber || 0,
      totalReturnNumber: item.totalReturnNumber || 0,
      remainingNumber: item.remainingNumber || 0
    }
  })
}

const handleRefundSearch = () => {
  if (refundViewType.value === 'detail') {
    loadRefundDetailList()
  } else {
    loadRefundList()
  }
}

const handleRefundReset = () => {
  refundSearch.value = {
    productCode: '',
    specificationName: '',
    dateRange: []
  }
  if (refundViewType.value === 'detail') {
    loadRefundDetailList()
  } else {
    loadRefundList()
  }
}

const handleRefundDetailDialog = (row: any) => {
  const params = {
    id: 'refundDetailDialog',
    el: '#app',
    data: {
      refundData: row
    },
    render: refundDetailDialog
  }
  dynamic.show(params)
}

const handleFilterChange = async (filterType: string) => {
  productFilter.value = filterType
  await loadProductList()
}

onMounted(() => {
  if (route.query.id) {
    orderId.value = route.query.id as string
  }
  loadExchangeRate()
  loadOrderDetail()
})

watch(
  activeTab,
  async newTab => {
    if (newTab === 'semiFinished') {
      await loadSemiFinishedList()
      loadedTabs.value.add('semiFinished')
    } else if (newTab === 'product') {
      await loadProductList()
      loadedTabs.value.add('product')
    } else if (newTab === 'logistics') {
      await loadLogisticsList()
      loadedTabs.value.add('logistics')
    } else if (newTab === 'refund') {
      if (refundViewType.value === 'detail') {
        await loadRefundDetailList()
      } else {
        await loadRefundList()
      }
      loadedTabs.value.add('refund')
    }
  },
  { immediate: true }
)

watch(refundViewType, async newViewType => {
  if (activeTab.value === 'refund') {
    if (newViewType === 'detail') {
      await loadRefundDetailList()
    } else {
      await loadRefundList()
    }
  }
})

const loadOrderDetail = async () => {
  const { code, data, message } = await getOrderDetailNew({ orderId: orderId.value })
  if (code !== 200) return ElMessage.warning(message)

  if (Number(data.hasHalfProductOrder) === 1) {
    activeTab.value = 'semiFinished'
  } else {
    activeTab.value = 'product'
  }

  orderInfo.value = {
    orderCode: data.code || '',
    sourcePlatform: getPlatformLabel(data.sourcePlatform),
    platformOrderCode: data.platformOrderCode || '',
    invoiceNo: data.code || '',
    currency: data.currency,
    currencyLabel: getCurrencyLabel(data.currency),
    orderTime: data.orderTime || '',
    deliveryTime: data.deliveryTime || '',
    submitOrderTime: data.submitOrderTime || '',
    orderFinishTime: data.orderFinishTime || '',
    discountAmount: data.discountAmount || '0.00',
    shippingCost: data.shippingCost || '0.00',
    isCollectedShippingCost: getShippingMethodLabel(data.isCollectedShippingCost),
    amount: data.orderAmount || '0.00',
    endAmount: data.endAmount || '0.00',
    endOtherAmount: data.endOtherAmount || '0.00',
    remark: data.remark || '',
    subStatusText: getOrderStatusLabel(data.orderStatus),
    subStatus: data.orderStatus,
    hasHalfProductOrder: data.hasHalfProductOrder || 0,
    shippingMethod: getDeliveryTypeLabel(data.shippingMethod),
    shippingMethodValue: data.shippingMethod,
    isInboundDelivery: data.isInboundDelivery ? '1' : '0',
    isInboundDeliveryLabel: getIsWarehouseDeliveryLabel(data.isInboundDelivery ? '1' : '0'),
    createTime: data.createTime || '',
    customerName: data.customerName || '',
    customerAddress: data.customerAddress,
    receiver: data.receiver,
    receiverPhone: data.receiverPhone,
    salesEmployeeName: data.salesEmployeeName || '',
    saleRatio: `${data.saleRatio ?? 0}%`,
    followEmployeeName: data.followEmployeeName || '',
    followRatio: `${data.followRatio ?? 0}%`,
    costAmount: data.orderCostAmount || '0.00',
    profitAmount: data.orderProfitAmount || '0.00',
    orderPayment: getReceivableStatusLabel(data.receiveStatus),
    customerShipping: getReceivableStatusLabel(data.shippingReceiveStatus),
    receiveFinishTime: data.receiveFinishTime || '',
    shippingReceiveFinishTime: data.shippingReceiveFinishTime || '',
    statusCountMap: data.statusCountMap || {}
  }

  // if (data.currency == '1') {
  //   orderInfo.value.profitAmount = (data.orderProfitAmount * exchangeRate.value).toFixed(2)
  // }

  await loadCloseOrderPreview()
}

const formatProductTableData = (itemList: any[], currency: string) => {
  const currencySymbol = currency == '0' ? '¥' : '$'
  const productGroups: any = {}
  itemList.forEach((item: any) => {
    const productCode = item.productCode || ''
    if (!productGroups[productCode]) {
      productGroups[productCode] = []
    }
    productGroups[productCode].push(item)
  })

  const tableData: any[] = []
  Object.keys(productGroups).forEach(productCode => {
    const group = productGroups[productCode]
    const totalNumber = group.reduce((sum: number, item: any) => sum + (Number(item.number) || 0), 0)
    const totalAmount = group.reduce(
      (sum: number, item: any) => sum + (Number(item.price) || 0) * (Number(item.number) || 0),
      0
    )

    group.forEach((item: any, index: number) => {
      const specName = item.itemList?.map((spec: any) => spec.categorySpecificationItemValue || '').join('-') || ''
      const specSegments =
        item.itemList?.length > 0
          ? item.itemList.map((spec: any) => ({
              text: spec.categorySpecificationItemValue || '',
              color: spec.color || ''
            }))
          : [{ text: specName, color: '' }]
      const specImage = item.imageList?.[0]?.url || ''
      const totalPrice = (Number(item.price) || 0) * (Number(item.number) || 0)

      tableData.push({
        ...item,
        id: item.id || '',
        productCode,
        number: totalNumber,
        totalPrice: `${currencySymbol}${totalAmount.toFixed(2)}`,
        specImage,
        specName,
        specSegments,
        progressPercentage: item.number > 0 ? Math.round(((item.deliveryNumber || 0) / item.number) * 100) : 0,
        deliveryNumber: item.deliveryNumber || 0,
        enterNumber: item.enterNumber || 0,
        totalNumber: item.number || 0,
        labelName: item.labelName || '/',
        price: item.price || 0,
        priceValue: `${currencySymbol}${item.price || 0}`,
        totalUnitPrice: `${currencySymbol}${totalPrice.toFixed(2)}`,
        supplierPrice: item.supplierPrice || 0,
        supplierPriceValue: `¥${item.supplierPrice || 0}`,
        remark: item.remark || '',
        rawStatus: String(item.status || '0'),
        status: getOrderStatusLabel(item.status),
        isFirstInGroup: index === 0,
        groupRowSpan: group.length
      })
    })
  })

  return tableData
}

const loadProductList = async () => {
  const params: any = {
    orderId: orderId.value,
    productCode: searchProduct.value,
    specificationName: searchSpec.value
  }

  if (productFilter.value !== 'all') {
    const statusMap: Record<string, string> = {
      pending: '0',
      warehouse: '1',
      packing: '2',
      shipping: '3',
      shipped: '4',
      completed: '5',
      closed: '7'
    }
    params.status = statusMap[productFilter.value]
  }

  const { code, data, message } = await getOrderDetailProductList(params)
  if (code !== 200) return ElMessage.warning(message)
  const subOrder = data.subOrder || {}

  if (subOrder.statusCountMap) {
    orderInfo.value.statusCountMap = subOrder.statusCountMap
  }

  if (subOrder.itemList && Array.isArray(subOrder.itemList)) {
    const tableData = formatProductTableData(subOrder.itemList, data.currency)
    productList.value = tableData
    displayProductList.value = tableData
  }
}

const loadCloseOrderPreview = async () => {
  closeOrderPreview.value = createEmptyCloseOrderPreview()
  if (Number(orderInfo.value.subStatus) >= 4) {
    return
  }

  const { code, data, message } = await getCloseOrderPreview({ orderId: orderId.value })
  if (code !== 200) {
    ElMessage.warning(message)
    return
  }

  closeOrderPreview.value = {
    ...createEmptyCloseOrderPreview(),
    ...(data || {}),
    itemList: Array.isArray(data?.itemList)
      ? data.itemList.map((item: any) => ({
          ...item,
          id: item.orderSubItemId,
          refundQty: Number(item.closableQty) || 0
        }))
      : []
  }
}

const handleSearch = () => {
  loadProductList()
}

const handleReset = () => {
  searchProduct.value = ''
  searchSpec.value = ''
  loadProductList()
}

// 单元格合并
const spanMethod = ({ row, columnIndex }: any) => {
  if (columnIndex === 0 || columnIndex === 1 || columnIndex === 2) {
    if (row.isFirstInGroup) {
      return {
        rowspan: row.groupRowSpan,
        colspan: 1
      }
    }
    return {
      rowspan: 0,
      colspan: 0
    }
  }
}

const handleConfirmComplete = async () => {
  ElMessageBox.confirm('订单确认完成', '确认完成', {
    confirmButtonText: '确定',
    type: 'warning',
    center: false
  })
    .then(async () => {
      const { code, message } = await confirmComplete({ orderId: orderId.value })
      if (code !== 200) return ElMessage.warning(message)
      ElMessage.success('确认完成')
      loadOrderDetail()
    })
    .catch(() => {
      // 用户取消
    })
}

const handleConfirmShipment = () => {
  const params = {
    id: 'confirmShipmentDialog',
    el: '#app',
    data: {
      orderId: orderId.value,
      onSuccess: () => {
        loadOrderDetail()
      }
    },
    render: confirmShipmentDialog
  }
  dynamic.show(params)
}

// 关闭订单弹窗
const closeOrderVisible = ref(false)
const closeOrderList = ref<any[]>([])
const closeOrderExtraAmount = ref('')
const closeOrderSubmitting = ref(false)

const closeOrderItemAmount = computed(() => {
  return closeOrderList.value.reduce((sum, item) => sum + (item.price || 0) * (item.refundQty || 0), 0)
})

const closeOrderTotalAmount = computed(() => {
  const otherAmount = closeOrderExtraAmount.value === '' ? 0 : Number(closeOrderExtraAmount.value)
  const validOtherAmount = Number.isNaN(otherAmount) ? 0 : otherAmount
  return (closeOrderItemAmount.value + validOtherAmount).toFixed(2)
})

const canClickCloseOrder = computed(() => {
  if (Number(orderInfo.value.subStatus) >= 4) return false
  return Boolean(closeOrderPreview.value.canClose)
})

const handleCloseOrder = async () => {
  await loadCloseOrderPreview()
  if (!closeOrderPreview.value.canClose) {
    return ElMessage.warning(closeOrderPreview.value.message || '当前订单不满足关闭条件')
  }

  closeOrderList.value = closeOrderPreview.value.itemList
    .filter((item: any) => (Number(item.refundQty) || 0) > 0)
    .map((item: any) => ({
      ...item,
      refundQty: Number(item.refundQty) || 0
    }))
  if (!closeOrderList.value.length) {
    return ElMessage.warning('当前订单暂无可关闭数量')
  }
  closeOrderExtraAmount.value = ''
  closeOrderVisible.value = true
}

const handleCloseOrderConfirm = async () => {
  if (closeOrderSubmitting.value) return

  const otherAmount = closeOrderExtraAmount.value === '' ? 0 : Number(closeOrderExtraAmount.value)
  if (Number.isNaN(otherAmount)) {
    return ElMessage.warning('其他金额请输入有效数字')
  }

  const invalidItem = closeOrderList.value.find((item: any) => {
    const refundQty = item.refundQty === '' ? 0 : Number(item.refundQty)
    const max = Number(item.closableQty) || 0
    return Number.isNaN(refundQty) || refundQty < 0 || refundQty > max
  })
  if (invalidItem) {
    return ElMessage.warning('存在无效的退款数量，请检查后重试')
  }

  const orderAmount = Number(orderInfo.value.amount || 0)
  // const discountAmount = Number(orderInfo.value.discountAmount || 0)
  const totalRefundAmount = closeOrderItemAmount.value + otherAmount
  if (totalRefundAmount > orderAmount) {
    return ElMessage.warning('退款总价不能大于订单金额')
  }

  closeOrderSubmitting.value = true
  try {
    const { code, message } = await closeOrder({
      orderId: orderId.value,
      otherAmount: otherAmount.toFixed(3),
      amount: Number(closeOrderTotalAmount.value).toFixed(3),
      itemList: closeOrderList.value.map((item: any) => ({
        orderSubItemId: item.id,
        refundQty: item.refundQty === '' ? 0 : Number(item.refundQty)
      }))
    })
    if (code !== 200) return ElMessage.warning(message)

    ElMessage.success('关闭订单成功')
    closeOrderVisible.value = false
    await loadOrderDetail()
    if (loadedTabs.value.has('product') || activeTab.value === 'product') {
      await loadProductList()
    }
    if (loadedTabs.value.has('semiFinished') || activeTab.value === 'semiFinished') {
      await loadSemiFinishedList()
    }
    if (loadedTabs.value.has('logistics') || activeTab.value === 'logistics') {
      await loadLogisticsList()
    }
  } finally {
    closeOrderSubmitting.value = false
  }
}

const handleExport = () => {
  const params = {
    id: 'exportDialog',
    el: '#app',
    data: {
      orderId: orderId.value
    },
    render: exportDialog
  }
  dynamic.show(params)
}

// 打开修改发货方式弹窗
const handleEditShippingMethod = () => {
  const params = {
    id: 'editShippingMethodDialog',
    el: '#app',
    data: {
      currentValue: orderInfo.value.shippingMethodValue,
      orderId: orderId.value,
      onSuccess: () => {
        loadOrderDetail()
      }
    },
    render: editShippingMethodDialog
  }
  dynamic.show(params)
}

const handleRefund = (row: any) => {
  console.log('handleRefund', row)

  const params = {
    id: 'refundDialog',
    el: '#app',
    data: {
      rowData: {
        orderId: orderId.value,
        productId: row.productId,
        price: row.price,
        labelId: row.labelId,
        remark: row.remark || '',
        specificationId: row.specificationId,
        supplierPrice: row.supplierPrice
      },
      onSuccess: () => {
        loadOrderDetail()
      }
    },
    render: refundDialog
  }
  dynamic.show(params)
}
handleRefund

const handleProductProgress = (row: any) => {
  const itemIds =
    Array.isArray(row.salYtOrderSubItemIds) && row.salYtOrderSubItemIds.length ? row.salYtOrderSubItemIds : [row.id]
  const params = {
    id: 'productProgress',
    el: '#app',
    data: {
      itemId: row.id,
      itemIds,
      currencySymbol: currencySymbol.value
    },
    render: productProgress
  }
  dynamic.show(params)
}
</script>

<style scoped lang="scss">
.order-detail-container {
  background: #f5f7fa;
  min-height: calc(100vh - 60px);
  .detail-content {
    display: flex;
    gap: 10px;
    margin-bottom: 10px;

    .content-left {
      flex: 1;

      .info-card {
        border: 0;
        min-height: 415px;

        .detail-header {
          display: flex;
          justify-content: space-between;
          align-items: center;
          padding-bottom: 15px;
          margin-bottom: 15px;
          border-bottom: 1px solid #f0f0f0;

          .header-left {
            display: flex;
            align-items: center;

            .order-label {
              font-weight: 600;
              color: #333;
              font-size: 14px;
            }

            .order-number {
              font-size: 16px;
              font-weight: 600;
              margin-right: 10px;
              color: #333;

              &.order-number--copyable {
                cursor: pointer;

                &:hover {
                  color: #409eff;
                }
              }
            }

            .status-tag {
              margin: 0 5px;

              &.status-primary {
                background: #e3f2fd;
                color: #2196f3;
                border-color: #90caf9;
              }

              &.status-success {
                background: #e8f5e9;
                color: #4caf50;
                border-color: #81c784;
              }

              .tag-content {
                display: inline-flex;
                align-items: center;
              }

              .edit-icon {
                margin-left: 3px;
                margin-bottom: -1px;
                cursor: pointer;
              }

              &.status-warning {
                background: #fff3e0;
                color: #ff9800;
                border-color: #ffb74d;
              }
            }
          }

          .header-right {
            display: flex;
          }
        }

        .info-section {
          display: flex;
          gap: 30px;
          margin-bottom: 20px;

          .info-left {
            flex: 1;
            border-radius: 4px;

            .info-row {
              display: flex;
              gap: 30px;
              margin-bottom: 12px;

              &:last-child {
                margin-bottom: 0;
              }

              .info-item {
                display: flex;
                font-size: 14px;
                flex: 1;

                &.full-width {
                  flex: 100%;
                }

                &.empty {
                  visibility: hidden;
                }

                .label {
                  color: #909399;
                  white-space: nowrap;
                }

                .value {
                  color: #333;

                  &.status-badge {
                    color: #e6a23c;
                  }
                }
              }
            }
          }

          .info-right {
            border-radius: 4px;
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
            justify-content: flex-end;

            .status-item {
              font-size: 14px;
              text-align: right;

              .status-label {
                color: #909399;
                margin-bottom: 8px;
              }

              .status-value {
                font-size: 22px;
                font-weight: 600;
                color: #333;
              }
            }

            .amount-item {
              text-align: right;
              font-size: 14px;

              .amount-label {
                color: #909399;
                margin-bottom: 8px;
              }

              .amount-value {
                font-size: 24px;
                font-weight: 600;
                color: #333;
              }

              .amount-value--close {
                color: #e6a23c;
              }

              .amount-extra {
                margin-top: 6px;
                color: #909399;
                font-size: 12px;
              }
            }
          }
        }

        .progress-section {
          padding-top: 20px;
          border-top: 1px solid #f0f0f0;

          .section-title {
            font-weight: 600;
            font-size: 14px;
            color: #333;
            margin-bottom: 20px;
          }
        }
      }
    }

    .content-right {
      width: 330px;

      .section-card {
        background: #fff;
        border-radius: 4px;
        padding: 15px;
        margin-bottom: 10px;
        &:last-child {
          margin-bottom: 0;
        }

        .section-title {
          font-weight: 600;
          font-size: 14px;
          color: #333;
          padding-bottom: 10px;
          border-bottom: 1px solid #f0f0f0;
          margin-bottom: 15px;
        }

        .section-content {
          .info-row {
            display: flex;
            gap: 15px;
            margin-bottom: 12px;

            .info-item {
              flex: 1;
              font-size: 14px;

              .label {
                color: #909399;
              }

              .value {
                color: #333;
              }

              .detail-time-trigger {
                margin-left: 6px;
                color: #909399;
                cursor: pointer;
                vertical-align: middle;
              }
            }
          }

          .info-item {
            margin-bottom: 12px;
            font-size: 14px;

            &:last-child {
              margin-bottom: 0;
            }

            .label {
              color: #909399;
              display: inline-block;
            }

            .value {
              color: #333;
            }
          }
        }
      }
    }
  }

  .tabs-card {
    background: #fff;
    border: 0;

    .tab-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
      flex-wrap: wrap;
      gap: 15px;

      .filter-group {
        display: flex;
        gap: 10px;
        flex-wrap: wrap;

        .el-button {
          padding: 5px 15px;
          border: 1px solid #dcdfe6;
          background: #fff;
          color: #606266;

          &.active {
            color: #409eff;
            border-color: #409eff;
          }

          &:hover {
            color: #409eff;
            border-color: #409eff;
          }
        }
      }

      .search-group {
        display: flex;
        gap: 10px;
        align-items: center;
        flex-wrap: wrap;
      }

      @media (max-width: 1400px) {
        flex-direction: column;
        align-items: flex-start;

        .filter-group,
        .search-group {
          width: 100%;
        }
      }
    }

    .empty-content {
      text-align: center;
      padding: 50px 0;
      color: #999;
    }
  }

  .spec-name-content {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    gap: 2px;
  }

  .spec-name-segment {
    color: #303133;
  }

  .spec-name-segment--colored {
    display: inline-block;
    padding: 1px 2px;
    border-radius: 4px;
  }

  .spec-name-separator {
    color: #303133;
  }
}

.close-order-footer {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 24px;
  margin-top: 16px;
  padding-right: 4px;

  .close-order-extra {
    display: flex;
    align-items: center;

    .extra-label {
      font-size: 14px;
      color: #606266;
      white-space: nowrap;
    }
  }

  .close-order-total {
    font-size: 14px;
    color: #606266;

    strong {
      color: #f56c6c;
      font-size: 16px;
    }
  }
}
</style>
