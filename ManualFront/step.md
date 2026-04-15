## 2026-04-14

- Rebuilt `src/views/HomeView.vue` into a warm restaurant landing page with vibrant food photography, menu preview cards, an interactive reservation form, a chef story section, and a location map.
- Simplified `src/App.vue` to a router-only shell and reset `src/assets/base.css` plus `src/assets/main.css` for warm editorial typography, color tokens, responsive layout rules, and accessible interaction states.
- Rebuilt `src/views/HomeView.vue` into a heartwarming pet adoption landing page with motion-driven pet cards, search filter previews, success stories, shelter partnership sections, and warm adoption CTAs; updated `src/App.vue`, `src/assets/base.css`, and `src/assets/main.css` to support the new inviting visual system and responsive motion behavior.
- Extracted the landing page navbar into `src/components/layout/LandingNav.vue` and kept the existing style while restructuring it as a single full-width left-to-right component; updated `src/views/HomeView.vue` to consume the new component.
- Reworked the front-end toward a handmade creative shop system: added standalone login and register pages, introduced store-backed guest route handling, aligned user API types with the WatchMall backend contract, and replaced the pet-themed home page with a handmade retail landing page powered by mock `HomePageData`.
- Migrated the visible storefront and auth flow to Ant Design Vue, installed `ant-design-vue` plus `@ant-design/icons-vue`, and replaced text-only cards with image-backed category, product, artisan, and order cards.
- Re-skinned the Ant Design Vue buttons so they keep the original warm rounded gradient / soft secondary visual language instead of the library defaults.
- Fixed the hero button text alignment so the custom-styled Ant buttons render with centered labels.
- Switched the front-end request layer to an axios wrapper aligned with the WatchMall style, removed hard-coded `/api` duplication from the API modules, and added centralized 401 login-expiration handling with store cleanup and redirect support.
- Connected the login/register pages to the new `Manual` backend endpoints so registration, login, logout, and current-user fetch now follow the real session-based backend contract.
- Fixed the Ant auth form submit wiring by changing the primary login/register buttons to call `handleSubmit` directly and binding the form model explicitly, avoiding the previous click-with-no-request issue.
- Updated `src/components/layout/LandingNav.vue` to use Chinese navigation text, keep only a login button for guests, and show a logged-in dropdown menu with user settings and logout actions after sign-in.
- Translated `src/views/LoginView.vue` and `src/views/RegisterView.vue` to Chinese so the auth experience matches the rest of the storefront navigation and product language.
- Added independent public pages for `精选作品`、`匠人故事`、`定制服务` and introduced a separate `ProfileLayout` with user-side pages for `个人中心`、`我的订单`、`我的收藏`、`收货地址`、`账号设置`.
- Updated the router and navigation so the current project is clearly positioned as a storefront plus personal center: `/profile/**` now requires login, guest-only routes redirect to `/profile`, and the top nav dropdown routes into the user pages instead of placeholder actions.

- Simplified the logged-in header dropdown so it no longer repeats every personal-center subpage, and added clear return-to-storefront actions inside ProfileLayout for going back to the home page or products page.

- Reworked ProfileSettingsView to use Ant Design Vue descriptions for account information display, making the settings page read more like a polished profile summary while keeping action buttons for later editing flows.

- Replaced the front-end LoginUser type with the full backend LoginUserVO field set and updated ProfileSettingsView to render all returned account fields through the descriptions-based profile summary.

- Refined the ProfileSettingsView role field to render colored tags for backend roles, mapping admin to ����Ա, user to ��ͨ�û�, and artisan to ���� for a clearer account summary.
