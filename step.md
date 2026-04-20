## 2026-04-14

- Added standalone login and register pages inside `ManualFront`, wired them to the existing user API layer and Pinia login state, and supported redirect-based return navigation plus account prefill after registration.
- Rebuilt `ManualFront/src/views/HomeView.vue` as a handmade creative shop landing page while keeping the warm premium visual style and driving the page from mock category, product, artisan, and recent-order data.
- Aligned the front-end user contract with the WatchMall backend style by changing registration to return an id, switching the current-user endpoint to `/api/user/get/login`, and replacing `userName` with `username`.
- Installed `ant-design-vue` and `@ant-design/icons-vue`, enabled the library globally in `ManualFront/src/main.ts`, converted the home/auth experience to Ant Design Vue components, and upgraded the storefront cards to use real image-based content.
- Re-skinned all visible Ant Design Vue buttons so they keep the original warm rounded house style instead of the default Ant button appearance.
- Fixed the vertical centering of the Ant Design Vue hero buttons so their text sits visually centered inside the custom rounded button skin.
- Added a WatchMall-style `users` login/register backend in `src/main/java/com/manual/manual`, including DTOs, entity, mapper, service, controller, session login state, and `/user/register`, `/user/login`, `/user/logout`, `/user/get/login` endpoints mapped to the existing `manual_mall.users` table.
- Switched `ManualFront/src/api/request.ts` to an axios-based request wrapper with `/api` base path, cookie credentials, login-expiration handling, and aligned `src/api/user.ts`, `src/api/home.ts`, and `src/stores/user.ts` to the real backend login/register flow.
- Fixed the auth page submit trigger by binding the Ant Design Vue login/register buttons directly to `handleSubmit` and setting the form model explicitly, so clicking the primary button now actually sends the request.
- Updated the landing navigation to Chinese copy, removed the separate register button from the header, and replaced the logged-in header pill with a dropdown menu that includes user settings and logout actions.
- Translated the login and register pages into Chinese, including titles, helper copy, validation feedback, field labels, placeholders, and action text, while keeping the existing layout and API wiring unchanged.
- Expanded `ManualFront` into a real front-of-house route structure with `/products`, `/artisans`, `/custom`, and authenticated `/profile` pages, and added a dedicated profile layout plus mock user-side pages for orders, favorites, addresses, and account settings.
- Reworked the storefront navigation to match the new user-side information architecture: guest users only see a login button, logged-in users get a profile dropdown, and login/guest-only redirect behavior now defaults into `/profile` instead of returning to the landing page.

- Simplified the logged-in storefront dropdown so it only keeps the necessary personal-center entry points, and added explicit storefront return actions inside the profile layout to make going back to the public home page obvious.

- Refined the personal-center settings page to use an Ant Design Vue descriptions-based account summary instead of a raw form, keeping the page more suitable for profile display while preserving edit/change-password/logout action slots.

- Aligned the ManualFront login user type with the backend LoginUserVO shape and updated the profile settings page to display the full user payload, including phone, email, gender, status, balance, and time fields.

- Updated the profile settings role display to use color-coded tags mapped from backend roles, showing admin as 管理员, user as 普通用户, and artisan as 匠人.

- Added a real /api/home aggregation module on the Spring Boot side with explicit SQL queries for homepage stats, categories, products, artisans, and recent orders, then switched ManualFront home/products/artisans pages from mock data to shared real API loading with loading/error empty states and repaired the related Chinese storefront copy.

- Removed the public homepage business stats from the storefront and from the /api/home response, keeping those metrics out of the front-end while preserving category, product, artisan, and recent-order display data.

- Added public product and artisan modules with independent /api/products and /api/artisans endpoints, including product filtering by category/origin/material plus product detail and artisan detail aggregations, then rewired the storefront to use dedicated listing APIs, added /products/:id and /artisans/:id pages, and linked home/product/artisan cards into those new detail views.

- 修复前台商品与匠人详情长整型 id 精度问题：前端商品/匠人/分类主键统一改为 string 传递，后端首页与作品/匠人公开 VO 的 Long id 使用字符串序列化，避免 3100000000000004004 这类主键在浏览器中被 number 截断

- 首页精选分类卡片改为可点击并支持键盘触发，点击后会跳转到 /products 并携带 categoryId query，商品页也会读取并同步该分类筛选到地址栏，实现按分类直达作品列表。
- 商品列表页筛选区改为由“过滤”按钮控制展开与收起，默认先隐藏分类、产地、材料类型三个筛选项，点击按钮后展示，再次点击按钮后隐藏，原有筛选请求逻辑保持不变。

- 新增匠人工作台 MVP：后端补齐 /api/artisan-center 下的店铺资料、工作台概览、商品管理、订单履约、定制需求接口与状态流转，前端新增独立 /artisan 路由组、匠人侧布局及概览/资料/商品/订单/定制页面，并调整登录与导航让 artisan 角色默认进入工作台。

- 新建 ManualAdmin 管理端项目：在项目根目录下新增基于 Vue 3 + Vite + TypeScript + Ant Design Vue 的管理员前端骨架，包含独立路由、后台布局以及控制台、用户、商品、订单、分类、系统设置等基础页面。

- 为 ManualAdmin 增加用户管理真实 CRUD：后端新增 /api/admin/users 管理接口，支持管理员按关键字/角色/状态查询用户、查看详情、新增、编辑、删除；前端用户页改为接真实接口，补齐筛选、表格、弹窗表单和删除确认。

- 放宽后端 WebMvc 跨域配置，允许 localhost 和 127.0.0.1 任意本地开发端口携带凭证访问，修复 ManualAdmin 请求 /api/admin/users 出现 403 的问题

- 为 ManualAdmin 增加独立管理员登录页、Pinia 登录态、全局路由守卫与退出登录能力，管理端页面现在必须使用 admin 账号登录后才能访问

- 修复 ManualAdmin 登录页提交无响应问题，改为显式表单模型校验与按钮点击提交，并清理管理员登录相关提示文案编码

- 新增管理员分类管理模块：后端补充 /api/admin/categories 分类 CRUD、层级与父分类校验、删除前商品/子分类约束；前端将 ManualAdmin 分类页改为真实接口表格与弹窗管理

- 修复匠人工作台新增商品分类为空问题：新增独立商品分类接口，商品编辑页单独拉取分类并在无分类时给出管理员端启用分类提示
- 新增管理员商品审核中心：后端补充 /api/admin/products 列表、详情、通过、驳回接口，前端将 ManualAdmin 商品页改为真实审核中心并支持查看详情与执行审核

- 拆分前台与后台登录态：新增 admin_login 独立 session、补充 /api/admin/auth 登录接口并将 ManualAdmin 改为走后台专用鉴权，修复管理员登录覆盖前台用户/匠人会话的问题

- 调整匠人商品编辑规则：商品更新后自动退回草稿并重置为需重新提交审核，前端保存成功提示同步说明需再次提交审核

- 调整匠人商品编辑规则为更新后直接进入待审核状态，不再将审核状态重置为草稿，同时前端保存提示同步更新

- 新增管理员首页真实概览接口：后端补充 /api/admin/dashboard/overview，按数据库聚合待审核商品、待处理订单、活跃匠人、风险提醒及近 7 日经营快照；前端将 ManualAdmin 控制台首页改为实时加载这些统计，并把待发货、地址异常、低库存、退款申请、低分评价等明细说明同步展示

- 修复管理员首页风险统计 SQL 字段错误：将 product_review 表中不存在的 rating 改为实际字段 score，恢复 /api/admin/dashboard/overview 查询

- 新增匠人申请、模拟充值、上传接口与管理员审核流程：后端补充 artisan_profile 申请审核逻辑、/api/artisan-application、/api/user/recharge、/api/upload/{biz} 与静态上传映射；前端新增个人中心匠人申请页、商品图片上传式编辑页和 ManualAdmin 匠人审核页，并同步清理相关 SQL 建表/种子脚本

- 修复匠人申请页提交流程禁用条件：区分普通用户、管理员、已是匠人、待审核状态，避免将非普通用户误提示为审核中，并在提交前弹出明确原因
- 调整匠人申请页访问权限：前台个人中心仅普通用户显示“申请成为匠人”入口，并在路由层限制 /profile/artisan-application 仅 user 角色可访问，管理员访问将自动回到 /profile