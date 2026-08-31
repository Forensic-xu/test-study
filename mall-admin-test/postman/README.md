# Postman 导出备份

本目录存放从 **Postman** 导出的集合与环境 JSON，供版本备份、简历展示、MeterSphere 导入。

## 文件说明

| 文件 | 说明 |
|------|------|
| `mall-admin-test-api.postman_collection.json` | 完整 Collection（01-Auth … 08-Smoke-Flow） |
| `mall-admin-test-local.postman_environment.json` | 本地 Environment（**勿提交真实 Token**） |

## Postman 导出步骤

### 导出 Collection

1. 左侧选中集合 **`mall-admin-test API`**
2. 右侧 `···` → **Export**
3. 选 **Collection v2.1**
4. 保存为本目录下的 `mall-admin-test-api.postman_collection.json`

### 导出 Environment

1. 左侧 **Environments** → 选中 **`mall-admin-test-local`**
2. `···` → **Export**
3. 保存为 `mall-admin-test-local.postman_environment.json`
4. 导出前把 `token`、`user_token` 的 **Current Value** 清空

## 冒烟

Collection Runner 运行 **`08-Smoke-Flow`**，期望 7/7 Tests 通过。

详细笔记：`doc/learning/stage-03-postman/lesson-10-export.md`
