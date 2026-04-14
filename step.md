## 2026-04-14

- Added standalone login and register pages inside `ManualFront`, wired them to the existing user API layer and Pinia login state, and supported redirect-based return navigation plus account prefill after registration.
- Rebuilt `ManualFront/src/views/HomeView.vue` as a handmade creative shop landing page while keeping the warm premium visual style and driving the page from mock category, product, artisan, and recent-order data.
- Aligned the front-end user contract with the WatchMall backend style by changing registration to return an id, switching the current-user endpoint to `/api/user/get/login`, and replacing `userName` with `username`.
- Installed `ant-design-vue` and `@ant-design/icons-vue`, enabled the library globally in `ManualFront/src/main.ts`, converted the home/auth experience to Ant Design Vue components, and upgraded the storefront cards to use real image-based content.
- Re-skinned all visible Ant Design Vue buttons so they keep the original warm rounded house style instead of the default Ant button appearance.
- Fixed the vertical centering of the Ant Design Vue hero buttons so their text sits visually centered inside the custom rounded button skin.
