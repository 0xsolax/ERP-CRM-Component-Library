/*
 * @author java_deng
 * @date 2025/12/2 10:07
 * @description
 */
package com.qiaomoyun.job;

import com.qiaomoyun.eunm.sys.TenantConfigEnum;
import com.qiaomoyun.info.TenantInfo;
import com.qiaomoyun.manager.pur.yt.PurYtStoreWarningManager;
import com.qiaomoyun.util.TenantInfoContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class YtStoreWarningJob {
    @Autowired
    private PurYtStoreWarningManager purYtStoreWarningManager;

    @Scheduled(cron = "0 0 0 * * ?")
//@Scheduled(cron = "0 51 10 * * ?")
    public void StoreWarning() {
    //因为是一唐的定时任务，所有直接设置一唐的租户id
    TenantInfo tenantInfo = new TenantInfo();
    tenantInfo.setTenantId(Integer.parseInt(TenantConfigEnum.YiTangTenantId.getKey()));
    TenantInfoContext.setCurrentTenant(tenantInfo);
        // 删除前一天的库存预警数据
        purYtStoreWarningManager.deleteYesterdayWarningData();

        // 遍历sal_yt_customer_store数据,增加独立仓预警
        purYtStoreWarningManager.processCustomerStoreWarning();

        // 遍历sto_yt_store，增加公共仓预警
        purYtStoreWarningManager.processPublicStoreWarning();
    }
}
