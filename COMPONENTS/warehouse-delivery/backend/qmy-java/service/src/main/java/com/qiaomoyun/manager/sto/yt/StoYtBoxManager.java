/*
 * @author java_deng
 * @date 2024/12/15 10:10
 * @description 箱规管理服务类
 */
package com.qiaomoyun.manager.sto.yt;

import com.github.pagehelper.PageHelper;
import com.qiaomoyun.entity.sto.yt.StoYtBox;
import com.qiaomoyun.info.PageResultInfo;
import com.qiaomoyun.mapper.sto.yt.StoYtBoxMapper;
import com.qiaomoyun.util.EntityCodeGenerateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 箱规管理服务类
 */
@Component
public class StoYtBoxManager {

    @Autowired
    private StoYtBoxMapper stoYtBoxMapper;

    /**
     * 添加或编辑箱规
     */
    public void saveOrUpdate(StoYtBox stoYtBox) {
        // 新增时生成箱规代码
        if (stoYtBox.getId() == null) {
            stoYtBox.setCode(EntityCodeGenerateUtil.generateUniqueId("C"));
            stoYtBoxMapper.insert(stoYtBox);
        } else {
            stoYtBoxMapper.updateById(stoYtBox);
        }
    }

    /**
     * 获取箱规列表
     */
    public Object list(StoYtBox stoYtBox, Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        List<StoYtBox> list = stoYtBoxMapper.list(stoYtBox);
        return new PageResultInfo<>(list);
    }

    /**
     * 获取箱规详情
     */
    public StoYtBox detail(Long id) {
        StoYtBox box = stoYtBoxMapper.selectById(id);
        if (box != null && box.getIsDeleted() != 1) {
            return box;
        }
        return null;
    }

    /**
     * 删除箱规
     */
    public void delete(Long id) {
        StoYtBox box = new StoYtBox();
        box.setId(id);
        box.setIsDeleted(1);
        stoYtBoxMapper.updateById(box);
    }

    /**
     * 获取箱规列表，用于下拉选择，返回id和长宽高
     */
    public List<StoYtBox> listForSelect() {
        StoYtBox stoYtBox = new StoYtBox();
        return stoYtBoxMapper.list(stoYtBox);
    }
}
