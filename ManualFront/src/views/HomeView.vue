<script setup lang="ts">
import LandingNav from '@/components/layout/LandingNav.vue'
const filters = [
    { label: 'good with kids', count: '128', active: true },
    { label: 'apartment friendly', count: '76', active: true },
    { label: 'senior pets', count: '34', active: false },
    { label: 'low shedding', count: '49', active: true },
]

const pets = [
    {
        name: 'Maple',
        breed: 'golden retriever mix',
        age: '2 years old',
        energy: 'gentle zoomies',
        status: 'ready for meet-and-greet',
        tone: 'apricot',
        story: 'Maple loves soft blankets, easy walks, and leaning into every new friend she meets.',
        traits: ['house trained', 'kid approved', 'snuggle expert'],
    },
    {
        name: 'Milo',
        breed: 'tabby explorer',
        age: '10 months old',
        energy: 'playful and curious',
        status: 'foster-to-adopt open',
        tone: 'mint',
        story: 'Milo turns windowsills into adventure decks and settles down the second a lap appears.',
        traits: ['litter trained', 'sweet with cats', 'sunbeam collector'],
    },
    {
        name: 'Poppy',
        breed: 'terrier sweetheart',
        age: '4 years old',
        energy: 'balanced companion',
        status: 'weekend adoption event',
        tone: 'gold',
        story: 'Poppy thrives on routine, happy tail wags, and greeting neighbors like old friends.',
        traits: ['leash confident', 'calm indoors', 'loves car rides'],
    },
]

const stories = [
    {
        family: 'The Bennett family',
        pet: 'Luna',
        quote: 'The profile told us exactly who Luna was. By the time we met, it felt like we already knew her rhythm.',
        result: 'Matched in 6 days with a home prep checklist and post-adoption support.',
    },
    {
        family: 'Mia and Jordan',
        pet: 'Ollie',
        quote: 'The filter preview helped us find a dog who loved hikes but could still settle in a small apartment.',
        result: 'Adoption completed after one video intro and one in-person visit.',
    },
]

const partners = [
    {
        name: 'sunrise rescue network',
        city: 'portland, or',
        focus: 'family-ready dogs and shy companion cats',
        support: ['verified medical records', 'same-day review', 'new adopter orientation'],
    },
    {
        name: 'harbor paws collective',
        city: 'austin, tx',
        focus: 'foster-based placements with behavior notes',
        support: ['virtual meetups', 'transport coordination', 'settling guidance'],
    },
    {
        name: 'meadow street shelter',
        city: 'nashville, tn',
        focus: 'senior pets and bonded pair adoptions',
        support: ['fee-waived senior events', 'care plan templates', 'partner vet discounts'],
    },
]
</script>

<template>
    <main class="page">
        <section class="hero shell" id="top">
            <LandingNav />


            <div class="hero-grid">
                <div>
                    <p class="eyebrow">warm welcomes, trusted shelters, happier homes</p>
                    <h1>Find the pet who already feels like part of your family.</h1>
                    <p class="lead">
                        Explore motion-rich pet cards, thoughtful filter previews, and real adoption journeys designed to
                        make every match feel personal from the first click.
                    </p>
                    <div class="actions">
                        <a class="btn btn-primary" href="#pets">meet featured pets</a>
                        <a class="btn btn-soft" href="#partners">partner with a shelter</a>
                    </div>
                    <dl class="stats">
                        <div><dt>1,240+</dt><dd>successful matches this year</dd></div>
                        <div><dt>92%</dt><dd>applications answered in under 48 hours</dd></div>
                        <div><dt>38</dt><dd>verified rescue partners nationwide</dd></div>
                    </dl>
                </div>

                <div class="hero-card">
                    <div class="glow glow-coral"></div>
                    <div class="glow glow-gold"></div>
                    <article class="match-card">
                        <span class="label">today's warm matches</span>
                        <h2>3 pets align with your vibe</h2>
                        <div class="chips">
                            <span>calm at home</span>
                            <span>first-time adopter friendly</span>
                            <span>weekend visits available</span>
                        </div>
                        <div class="mini-list">
                            <article v-for="(pet, index) in pets.slice(0, 2)" :key="pet.name" :class="['mini', pet.tone]" :style="{ '--delay': `${index * 140}ms` }">
                                <div class="avatar">{{ pet.name.slice(0, 1) }}</div>
                                <div>
                                    <strong>{{ pet.name }}</strong>
                                    <small>{{ pet.breed }}</small>
                                </div>
                                <span>{{ pet.energy }}</span>
                            </article>
                        </div>
                    </article>
                </div>
            </div>
        </section>

        <section class="shell section" id="pets">
            <p class="eyebrow">motion-driven pet cards</p>
            <h2>Profiles that feel lively before you even schedule a visit.</h2>
            <p class="intro">Each card surfaces personality, readiness, and lifestyle fit so adopters can move from curiosity to confidence without guesswork.</p>
            <div class="pet-grid">
                <article v-for="(pet, index) in pets" :key="pet.name" :class="['pet', pet.tone]" :style="{ '--card-delay': `${index * 120}ms` }">
                    <div class="pet-visual">
                        <span class="pulse"></span>
                        <div class="monogram">{{ pet.name.slice(0, 1) }}</div>
                        <span class="status">{{ pet.status }}</span>
                    </div>
                    <div class="pet-head">
                        <div>
                            <h3>{{ pet.name }}</h3>
                            <p>{{ pet.breed }}</p>
                        </div>
                        <strong>{{ pet.age }}</strong>
                    </div>
                    <p>{{ pet.story }}</p>
                    <ul class="tags"><li v-for="trait in pet.traits" :key="trait">{{ trait }}</li></ul>
                    <div class="pet-foot">
                        <span>{{ pet.energy }}</span>
                        <a href="#cta">adopt {{ pet.name.toLowerCase() }}</a>
                    </div>
                </article>
            </div>
        </section>

        <section class="shell section" id="filters">
            <p class="eyebrow">search filters preview</p>
            <h2>Layer practical details with emotional fit.</h2>
            <p class="intro">Preview filters help adopters narrow by home style, energy, and care needs while keeping the experience warm and approachable.</p>
            <div class="two-col">
                <article class="panel">
                    <div class="row-head">
                        <div>
                            <span class="label">adoption finder</span>
                            <h3>Refine your future sidekick</h3>
                        </div>
                        <span class="pill coral-pill">live preview</span>
                    </div>
                    <div class="filter-list">
                        <button v-for="filter in filters" :key="filter.label" class="filter" type="button" :class="{ active: filter.active }" :aria-pressed="filter.active">
                            <span>{{ filter.label }}</span>
                            <strong>{{ filter.count }}</strong>
                        </button>
                    </div>
                    <div class="selector-list">
                        <div><span>home pace</span><strong>calm evenings + weekend walks</strong></div>
                        <div><span>care experience</span><strong>first-time adopter ready</strong></div>
                        <div><span>preferred visit</span><strong>virtual intro before in-person</strong></div>
                    </div>
                </article>
                <article class="panel">
                    <span class="label">top results</span>
                    <p class="results-copy">Matched using routine, household size, and companionship style.</p>
                    <div class="result-list">
                        <article v-for="pet in pets" :key="`${pet.name}-result`" class="result">
                            <div class="avatar small">{{ pet.name.slice(0, 1) }}</div>
                            <div>
                                <div class="result-title"><strong>{{ pet.name }}</strong><span>{{ pet.age }}</span></div>
                                <p>{{ pet.breed }} • {{ pet.energy }}</p>
                            </div>
                            <span class="pill mint-pill">96% match</span>
                        </article>
                    </div>
                </article>
            </div>
        </section>

        <section class="shell section" id="stories">
            <p class="eyebrow">success stories</p>
            <h2>Every adoption comes with a beginning worth retelling.</h2>
            <p class="intro">From first inquiry to the ride home, these stories show how thoughtful matching and shelter support turn a search into a lasting bond.</p>
            <div class="two-col stories">
                <article v-for="story in stories" :key="story.pet" class="panel story">
                    <span class="pill mint-pill">adopted: {{ story.pet }}</span>
                    <h3>{{ story.family }}</h3>
                    <blockquote>{{ story.quote }}</blockquote>
                    <p>{{ story.result }}</p>
                </article>
                <aside class="panel highlight">
                    <span class="label">what adopters value most</span>
                    <ul class="tags stacked">
                        <li>temperament notes from foster homes</li>
                        <li>clear timelines from application to pickup</li>
                        <li>follow-up care tips after adoption day</li>
                    </ul>
                    <a class="btn btn-soft" href="#cta">read more journeys</a>
                </aside>
            </div>
        </section>

        <section class="shell section" id="partners">
            <p class="eyebrow">shelter partnerships</p>
            <h2>Built to help rescue teams spotlight care, trust, and readiness.</h2>
            <p class="intro">Partner shelters get polished profiles, transparent readiness signals, and adoption tools that feel personal instead of transactional.</p>
            <div class="partner-grid">
                <article v-for="partner in partners" :key="partner.name" class="panel partner">
                    <div class="row-head">
                        <div>
                            <h3>{{ partner.name }}</h3>
                            <p>{{ partner.city }}</p>
                        </div>
                        <span class="focus">{{ partner.focus }}</span>
                    </div>
                    <ul class="tags"><li v-for="item in partner.support" :key="item">{{ item }}</li></ul>
                </article>
            </div>
        </section>

        <section class="shell cta" id="cta">
            <div>
                <p class="eyebrow">adoption cta</p>
                <h2>Open your home to the pet already waiting for your routine.</h2>
                <p>Start with a quick profile, preview pets that fit your pace, and let verified shelters guide the next steps with warmth and clarity.</p>
            </div>
            <div class="actions">
                <a class="btn btn-cream" href="#filters">start your adoption plan</a>
                <a class="btn btn-ghost-light" href="#partners">join as a shelter partner</a>
            </div>
        </section>
    </main>
</template>

<style scoped>
.page { padding: 24px 24px 72px; overflow: hidden; }
.shell, .cta { width: min(1180px, 100%); margin: 0 auto; }
.section { padding: 36px 0 72px; }
.hero { padding: 24px 0 88px; }
.panel, .stats div { background: var(--surface); border: 1px solid var(--line); box-shadow: var(--shadow); }
.avatar, .monogram { display: inline-flex; align-items: center; justify-content: center; font-family: var(--font-display); color: var(--text-strong); }
h1, h2, h3 { color: var(--text-strong); }
.label, .status { color: var(--text-muted); letter-spacing: 0.08em; text-transform: uppercase; font-size: 0.78rem; }
.actions { display: flex; align-items: center; gap: 14px; flex-wrap: wrap; }
.eyebrow { display: inline-flex; align-items: center; gap: 10px; color: var(--coral-deep); font-weight: 800; letter-spacing: 0.08em; text-transform: uppercase; }
.eyebrow::before { content: ''; width: 34px; height: 10px; border-radius: 999px; background: linear-gradient(90deg, var(--gold), var(--coral)); }
.hero-grid, .two-col, .cta { display: grid; gap: 24px; }
.hero-grid { grid-template-columns: minmax(0, 1.05fr) minmax(320px, 0.95fr); align-items: center; padding-top: 52px; }
.hero-card { position: relative; min-height: 520px; display: flex; align-items: center; justify-content: center; }
.glow { position: absolute; border-radius: 999px; filter: blur(4px); }
.glow-coral { inset: 24px auto auto 18px; width: 180px; height: 180px; background: rgba(244, 155, 122, 0.34); }
.glow-gold { right: 8px; bottom: 52px; width: 220px; height: 220px; background: rgba(255, 195, 109, 0.28); }
.match-card { position: relative; z-index: 1; width: min(100%, 480px); padding: 28px; border-radius: 36px; background: linear-gradient(180deg, rgba(255, 252, 247, 0.96), rgba(255, 243, 235, 0.94)); border: 1px solid rgba(223, 157, 121, 0.22); box-shadow: 0 32px 70px rgba(160, 103, 77, 0.22); }
.lead, .intro, .results-copy, .story p, .cta p, .pet p { color: var(--text); font-size: 1.05rem; }
h1, h2 { font-family: var(--font-display); line-height: 0.96; }
h1 { max-width: 12ch; font-size: clamp(3.4rem, 6vw, 6.1rem); margin: 18px 0 20px; }
h2 { font-size: clamp(2.4rem, 4vw, 4.3rem); margin: 14px 0 12px; }
h3 { margin: 0; font-size: 1.45rem; }
.btn { display: inline-flex; align-items: center; justify-content: center; min-height: 52px; padding: 0 24px; border: 1px solid transparent; border-radius: 999px; font-weight: 800; transition: transform 0.24s ease, box-shadow 0.24s ease, background-color 0.24s ease, border-color 0.24s ease; }
.btn:hover, .btn:focus-visible, .filter:hover, .filter:focus-visible, .pet:hover, .story:hover, .partner:hover { transform: translateY(-2px); }
.btn-primary { background: linear-gradient(135deg, var(--coral), #ef8a5f); color: #fff7f1; box-shadow: 0 16px 34px rgba(228, 123, 93, 0.26); }
.btn-soft { background: rgba(255, 255, 255, 0.7); border-color: rgba(211, 138, 105, 0.26); color: var(--text-strong); }
.btn-ghost { background: transparent; border-color: var(--line-strong); color: var(--text-strong); }
.btn-cream { background: #fff8f1; color: var(--coral-deep); }
.btn-ghost-light { background: transparent; border-color: rgba(255, 246, 236, 0.28); color: #fffaf4; }
.stats { display: grid; grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 16px; margin-top: 34px; }
.stats div { padding: 18px 20px; border-radius: 26px; }
.stats dt { font-family: var(--font-display); font-size: 2rem; color: var(--text-strong); }
.stats dd { margin-top: 8px; color: var(--text-muted); }
.chips, .mini-list, .pet-grid, .filter-list, .selector-list, .result-list, .partner-grid { display: grid; gap: 14px; }
.chips { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 20px; }
.chips span, .tags li, .focus, .selector-list div, .result, .pill, .filter { border-radius: 18px; }
.chips span, .tags li, .result, .selector-list div { background: var(--surface-soft); }
.chips span, .tags li { padding: 10px 14px; font-weight: 700; }
.mini-list { margin-top: 24px; }
.mini { display: grid; grid-template-columns: auto 1fr auto; align-items: center; gap: 16px; padding: 16px 18px; border-radius: 24px; background: rgba(255, 255, 255, 0.82); animation: drift 4.6s ease-in-out infinite; animation-delay: var(--delay); }
.avatar { width: 58px; height: 58px; border-radius: 20px; font-size: 1.5rem; }
.avatar.small { width: 52px; height: 52px; border-radius: 18px; font-size: 1.35rem; }
.apricot .avatar, .apricot .pet-visual { background: linear-gradient(145deg, #ffe1cf, #ffbc9d); }
.mint .avatar, .mint .pet-visual { background: linear-gradient(145deg, #dff6e9, #abd8bd); }
.gold .avatar, .gold .pet-visual { background: linear-gradient(145deg, #ffe7bb, #ffc773); }
.pet-grid, .partner-grid { grid-template-columns: repeat(3, minmax(0, 1fr)); gap: 22px; margin-top: 28px; }
.pet, .panel { padding: 24px; border-radius: 30px; }
.pet { background: var(--surface); border: 1px solid var(--line); box-shadow: var(--shadow); animation: rise 0.8s ease both; animation-delay: var(--card-delay); transition: transform 0.24s ease, box-shadow 0.24s ease; }
.pet-visual { position: relative; min-height: 220px; padding: 22px; display: flex; align-items: end; justify-content: space-between; border-radius: 28px; overflow: hidden; }
.pulse { position: absolute; top: 18px; right: 18px; width: 86px; height: 86px; border-radius: 999px; background: rgba(255, 255, 255, 0.34); animation: pulse 3.6s ease-in-out infinite; }
.monogram { position: relative; z-index: 1; width: 88px; height: 88px; border-radius: 30px; background: rgba(255, 251, 245, 0.72); backdrop-filter: blur(12px); font-size: 2.4rem; }
.status { position: relative; z-index: 1; max-width: 12ch; padding: 12px 14px; background: rgba(255, 255, 255, 0.74); color: var(--text); font-weight: 800; }
.pet-head, .row-head, .result-title, .pet-foot { display: flex; justify-content: space-between; gap: 16px; }
.pet-head { align-items: start; margin-top: 18px; }
.pet-head p, .result p, .row-head p { color: var(--text-muted); }
.pet-head strong { padding: 10px 12px; border-radius: 16px; background: rgba(250, 240, 229, 0.9); color: var(--text); white-space: nowrap; }
.tags { display: flex; flex-wrap: wrap; gap: 10px; list-style: none; }
.tags.stacked { align-content: start; }
.pet-foot { align-items: center; }
.pet-foot a { color: var(--coral-deep); font-weight: 800; }
.two-col { grid-template-columns: minmax(0, 1.1fr) minmax(300px, 0.9fr); align-items: start; }
.row-head { align-items: start; }
.filter-list { margin-top: 22px; grid-template-columns: repeat(2, minmax(0, 1fr)); }
.filter { display: inline-flex; align-items: center; justify-content: space-between; gap: 10px; padding: 14px 16px; border: 1px solid transparent; background: #fff7f0; color: var(--text); cursor: pointer; transition: transform 0.2s ease, border-color 0.2s ease; }
.filter.active { background: rgba(235, 139, 105, 0.14); border-color: rgba(229, 132, 96, 0.3); }
.selector-list { margin-top: 22px; }
.selector-list div { padding: 18px 20px; }
.selector-list span { display: block; margin-bottom: 6px; color: var(--text-muted); font-size: 0.92rem; }
.result-list { margin-top: 18px; }
.result { display: grid; grid-template-columns: auto 1fr auto; gap: 16px; align-items: center; padding: 14px 16px; }
.pill { padding: 10px 14px; font-size: 0.82rem; font-weight: 800; letter-spacing: 0.03em; }
.coral-pill { background: rgba(232, 122, 86, 0.12); color: var(--coral-deep); }
.mint-pill { background: rgba(151, 193, 169, 0.2); color: #46745a; }
.story, .partner, .highlight { transition: transform 0.24s ease, box-shadow 0.24s ease; }
.story blockquote { color: var(--text-strong); font-size: 1.15rem; font-weight: 700; line-height: 1.6; margin: 16px 0 0; }
.highlight { background: linear-gradient(180deg, rgba(255, 248, 239, 0.95), rgba(255, 237, 217, 0.95)); }
.focus { max-width: 18ch; padding: 10px 12px; background: rgba(255, 240, 226, 0.92); color: var(--text); font-weight: 700; text-align: right; }
.cta { grid-template-columns: minmax(0, 1fr) auto; align-items: center; padding: 36px; border-radius: 34px; background: linear-gradient(135deg, #d16f51, #ed9766); color: #fffaf6; box-shadow: 0 30px 70px rgba(194, 104, 72, 0.24); }
.cta .eyebrow, .cta p, .cta h2 { color: inherit; }
.cta .eyebrow::before { background: linear-gradient(90deg, rgba(255, 242, 225, 0.95), rgba(255, 208, 160, 0.88)); }
@keyframes drift { 0%, 100% { transform: translateY(0); } 50% { transform: translateY(-6px); } }
@keyframes rise { from { opacity: 0; transform: translateY(24px); } to { opacity: 1; transform: translateY(0); } }
@keyframes pulse { 0%, 100% { transform: scale(1); opacity: 0.72; } 50% { transform: scale(1.12); opacity: 0.35; } }
@media (max-width: 1120px) {
    .hero-grid, .two-col, .cta, .pet-grid, .partner-grid { grid-template-columns: 1fr; }
    .stats { grid-template-columns: repeat(2, minmax(0, 1fr)); }
}
@media (max-width: 760px) {
    .page { padding: 16px 16px 56px; }
    .hero { padding-top: 8px; padding-bottom: 64px; }
    .hero-grid { gap: 32px; padding-top: 34px; }
    h1 { max-width: none; }
    .stats, .pet-grid, .partner-grid, .filter-list { grid-template-columns: 1fr; }
    .pet-head, .row-head, .result-title, .pet-foot, .actions { flex-direction: column; align-items: flex-start; }
    .result { grid-template-columns: 1fr; }
    .focus { text-align: left; }
    .btn { width: 100%; }
    .cta { padding: 28px 22px; }
}
</style>


