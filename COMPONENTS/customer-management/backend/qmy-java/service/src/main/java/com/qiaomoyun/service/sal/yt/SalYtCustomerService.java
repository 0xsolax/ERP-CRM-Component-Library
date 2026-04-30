/*
 * @author java_deng
 * @date 2026/1/2 09:37
 * @description
 */
package com.qiaomoyun.service.sal.yt;

import com.qiaomoyun.entity.sal.yt.SalYtCustomerStore;
import com.qiaomoyun.manager.sal.yt.SalYtCustomerStoreManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreManager;
import com.qiaomoyun.manager.sto.yt.StoYtStoreRecordManager;
import com.qiaomoyun.param.sal.yt.SalYtCustomerQueryParams;
import org.springframework.stereotype.Service;

@Service
public class SalYtCustomerService {

    private final SalYtCustomerStoreManager salYtCustomerStoreManager;
    private final StoYtStoreManager stoYtStoreManager;
    private final StoYtStoreRecordManager stoYtStoreRecordManager;

    public SalYtCustomerService(SalYtCustomerStoreManager salYtCustomerStoreManager, StoYtStoreManager stoYtStoreManager, StoYtStoreRecordManager stoYtStoreRecordManager) {
        this.salYtCustomerStoreManager = salYtCustomerStoreManager;
        this.stoYtStoreManager = stoYtStoreManager;
        this.stoYtStoreRecordManager = stoYtStoreRecordManager;
    }

    public Object storeRecord(SalYtCustomerQueryParams params) {
        return salYtCustomerStoreManager.getRecord(params);
    }
}
