# 来源材料说明

## 已复制

| 路径 | 来源 | 说明 |
| :--- | :--- | :--- |
| `zhongsheng-AI/PRD_Detailed_V2.md` | `RAW/PROJECTs/zhongsheng-AI/PRD_Detailed_V2.md` | 报价 CRUD、API、成本公式和业务章节 |
| `zhongsheng/第一次调研（2026.02.11）/第一次会议.md` | `RAW/docs/zhongsheng/...` | 客户、销售、报价相关调研背景 |
| `zhongsheng/第一次调研（2026.02.11）/第二次会议.md` | `RAW/docs/zhongsheng/...` | 业务流程和需求补充 |
| `zhongsheng/第二次调研/会议记录.md` | `RAW/docs/zhongsheng/...` | 后续调研记录 |
| `zhongsheng/第二次调研/需求点：.md` | `RAW/docs/zhongsheng/...` | 需求点整理 |

## 关键事实

- `PRD_Detailed_V2.md` 的报价管理章节只定义基础 CRUD。
- 同一 PRD 后续成本公式章节提供产品成本、面料换算、纸箱成本、体积和装柜公式参考。
- qmy-admin 的 SED 报价页面已经明显超过基础 CRUD，包含成本、历史报价、审核和转订单。
- qmy-java 中存在与 qmy-admin API 匹配的 `/api/sal/sed/quotation` 后端实现，是复杂流的主要后端证据。

## 未复制

- `.DS_Store` 未复制。
- RAW 下完整项目目录未整包复制，只复制报价组件直接相关文件。
