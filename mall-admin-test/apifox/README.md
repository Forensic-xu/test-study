# 已迁移

Postman 导出请使用：**`mall-admin-test/postman/`**

见 `postman/README.md` 与 `doc/learning/stage-03-postman/lesson-10-export.md`。

## 文件说明

| 文件 | 说明 |
|------|------|
| `mall-admin-test-api.postman_collection.json` | 完整 API 集合（01-Auth … 08-Smoke-Flow） |
| `mall-admin-test-local.postman_environment.json` | 本地环境变量（**勿提交真实 Token**） |

## 导出步骤

见学习笔记：`doc/learning/stage-03-postman/lesson-10-export.md`

## 环境变量

| 变量 | 说明 |
|------|------|
| `base_url` | `http://127.0.0.1:8080` |
| `token` | admin JWT（运行时由登录脚本写入） |
| `user_token` | user01 JWT |
| `order_id` | 订单关联 |

## 冒烟

在 Apifox 中运行 **`08-Smoke-Flow`** 文件夹批量测试，期望 7/7 通过。
