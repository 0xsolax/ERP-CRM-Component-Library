package com.qiaomoyun.service.sal.yt;

import com.qiaomoyun.manager.sal.yt.SalYtOrderManager;
import com.qiaomoyun.param.sal.yt.SalYtOrderExportDeliveryParams;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SalYtOrderService {

    @Autowired
    private SalYtOrderManager salYtOrderManager;

    /**
     * 订单详情里面的物流导出
     * @param params
     * @param response
     */
    public void exportDelivery(SalYtOrderExportDeliveryParams params, HttpServletResponse response) {
        salYtOrderManager.exportDelivery(params, response);
    }
}
