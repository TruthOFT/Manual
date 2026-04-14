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
