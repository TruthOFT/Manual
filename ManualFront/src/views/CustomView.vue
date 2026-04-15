<template>
    <main class="page">
        <section class="shell top-section">
            <LandingNav />
        </section>

        <section class="shell hero-section">
            <div class="hero-copy">
                <p class="eyebrow">定制服务</p>
                <h1>把品牌礼盒、空间陈列和节日伴手礼，做成可被咨询和持续复用的服务页面。</h1>
                <p class="lead">
                    这一页先承载流程、材质方向和合作入口，后续可以接入 `custom_requirement` 的真实提交能力。
                </p>
            </div>
        </section>

        <section class="shell section two-col">
            <a-card class="soft-card" :bordered="false">
                <template #title>
                    <span class="card-title">定制流程</span>
                </template>

                <div class="step-list">
                    <div class="step-item">
                        <strong>01 提交需求</strong>
                        <p>说明用途、数量、预算区间和期望交付时间。</p>
                    </div>
                    <div class="step-item">
                        <strong>02 确认材质</strong>
                        <p>根据陶艺、木作、织物或香氛方向匹配工坊。</p>
                    </div>
                    <div class="step-item">
                        <strong>03 打样确认</strong>
                        <p>确认包装、配色、Logo 呈现和成品细节。</p>
                    </div>
                    <div class="step-item">
                        <strong>04 生产交付</strong>
                        <p>进入小批量制作、物流发货和售后跟进阶段。</p>
                    </div>
                </div>
            </a-card>

            <a-card class="soft-card inquiry-card" :bordered="false">
                <template #title>
                    <span class="card-title">咨询方向</span>
                </template>

                <a-space wrap>
                    <a-tag>品牌礼盒</a-tag>
                    <a-tag>婚礼伴手礼</a-tag>
                    <a-tag>节日员工礼</a-tag>
                    <a-tag>陈列搭配</a-tag>
                </a-space>

                <p class="inquiry-copy">当前先保留咨询入口样式，后续接真实接口时可直接改为提交表单。</p>

                <a-button class="manual-ant-btn manual-ant-btn-primary" size="large">提交定制意向</a-button>
            </a-card>
        </section>
    </main>
</template>

<script setup lang="ts">
import LandingNav from '@/components/layout/LandingNav.vue'
</script>

<style scoped>
.page {
    padding: 24px 24px 72px;
}

.shell {
    width: min(1180px, 100%);
    margin: 0 auto;
}

.top-section {
    padding-top: 24px;
}

.hero-section {
    padding: 48px 0 28px;
}

.section {
    padding-bottom: 48px;
}

.two-col {
    display: grid;
    grid-template-columns: minmax(0, 1.12fr) minmax(320px, 0.88fr);
    gap: 24px;
}

.hero-copy {
    display: grid;
    gap: 18px;
    max-width: 760px;
}

.eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 10px;
    color: var(--coral-deep);
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
}

.eyebrow::before {
    content: '';
    width: 34px;
    height: 10px;
    border-radius: 999px;
    background: linear-gradient(90deg, var(--gold), var(--coral));
}

h1 {
    margin: 0;
    color: var(--text-strong);
    font-family: var(--font-display);
    font-size: clamp(2.8rem, 5vw, 4.8rem);
    line-height: 0.98;
}

.lead,
.step-item p,
.inquiry-copy {
    color: var(--text);
}

.soft-card {
    border-radius: 28px;
    background: rgba(255, 253, 248, 0.92);
    box-shadow: var(--shadow);
}

.card-title {
    color: var(--text-muted);
    letter-spacing: 0.08em;
    text-transform: uppercase;
    font-size: 0.78rem;
    font-weight: 800;
}

.step-list {
    display: grid;
    gap: 16px;
}

.step-item {
    padding: 18px;
    border-radius: 20px;
    background: rgba(255, 247, 238, 0.92);
}

.step-item strong {
    display: block;
    margin-bottom: 8px;
    color: var(--text-strong);
}

.inquiry-card :deep(.ant-card-body) {
    display: grid;
    gap: 18px;
}

@media (max-width: 980px) {
    .two-col {
        grid-template-columns: 1fr;
    }
}

@media (max-width: 760px) {
    .page {
        padding: 16px 16px 56px;
    }
}
</style>
