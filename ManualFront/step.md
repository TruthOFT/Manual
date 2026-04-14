## 2026-04-14

- Rebuilt `src/views/HomeView.vue` into a warm restaurant landing page with vibrant food photography, menu preview cards, an interactive reservation form, a chef story section, and a location map.
- Simplified `src/App.vue` to a router-only shell and reset `src/assets/base.css` plus `src/assets/main.css` for warm editorial typography, color tokens, responsive layout rules, and accessible interaction states.
- Rebuilt `src/views/HomeView.vue` into a heartwarming pet adoption landing page with motion-driven pet cards, search filter previews, success stories, shelter partnership sections, and warm adoption CTAs; updated `src/App.vue`, `src/assets/base.css`, and `src/assets/main.css` to support the new inviting visual system and responsive motion behavior.
- Extracted the landing page navbar into `src/components/layout/LandingNav.vue` and kept the existing style while restructuring it as a single full-width left-to-right component; updated `src/views/HomeView.vue` to consume the new component.
- Reworked the front-end toward a handmade creative shop system: added standalone login and register pages, introduced store-backed guest route handling, aligned user API types with the WatchMall backend contract, and replaced the pet-themed home page with a handmade retail landing page powered by mock `HomePageData`.
- Migrated the visible storefront and auth flow to Ant Design Vue, installed `ant-design-vue` plus `@ant-design/icons-vue`, and replaced text-only cards with image-backed category, product, artisan, and order cards.
- Re-skinned the Ant Design Vue buttons so they keep the original warm rounded gradient / soft secondary visual language instead of the library defaults.
- Fixed the hero button text alignment so the custom-styled Ant buttons render with centered labels.
