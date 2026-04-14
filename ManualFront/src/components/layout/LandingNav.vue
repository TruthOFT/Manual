<script setup lang="ts">
const navItems = [
    { label: 'pets', href: '#pets' },
    { label: 'filters', href: '#filters' },
    { label: 'stories', href: '#stories' },
    { label: 'shelters', href: '#partners' },
]
</script>

<template>
    <div class="nav">
        <a class="brand" href="#top" aria-label="Kindred Tails home">
            <span class="mark">kt</span>
            <span class="brand-copy">
                <strong>Kindred Tails</strong>
                <small>adopt with heart</small>
            </span>
        </a>

        <nav class="links" aria-label="Section navigation">
            <a v-for="item in navItems" :key="item.href" :href="item.href">
                {{ item.label }}
            </a>
        </nav>

        <a class="btn btn-ghost" href="#cta">start matching</a>
    </div>
</template>

<style scoped>
.nav {
    display: grid;
    grid-template-columns: auto 1fr auto;
    align-items: center;
    gap: 20px;
    width: 100%;
    padding: 16px 20px;
    border: 1px solid var(--line);
    border-radius: 999px;
    background: var(--surface);
    box-shadow: var(--shadow);
    backdrop-filter: blur(20px);
}

.brand {
    display: inline-flex;
    align-items: center;
    gap: 12px;
}

.mark {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    width: 48px;
    height: 48px;
    border-radius: 16px;
    background: linear-gradient(135deg, #ffbf8f, #f28a67);
    box-shadow: 0 12px 24px rgba(226, 122, 82, 0.28);
    font-family: var(--font-display);
    font-size: 1.25rem;
    color: var(--text-strong);
}

.brand-copy {
    display: grid;
}

.brand-copy strong {
    color: var(--text-strong);
}

.brand-copy small {
    color: var(--text-muted);
    letter-spacing: 0.08em;
    text-transform: uppercase;
    font-size: 0.78rem;
}

.links {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 20px;
    min-width: 0;
    font-weight: 700;
}

.links a {
    color: var(--text);
    transition: color 0.24s ease;
}

.links a:hover,
.links a:focus-visible {
    color: var(--text-strong);
}

.btn {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-height: 52px;
    padding: 0 24px;
    border: 1px solid transparent;
    border-radius: 999px;
    font-weight: 800;
    transition:
        transform 0.24s ease,
        box-shadow 0.24s ease,
        background-color 0.24s ease,
        border-color 0.24s ease;
}

.btn:hover,
.btn:focus-visible {
    transform: translateY(-2px);
}

.btn-ghost {
    background: transparent;
    border-color: var(--line-strong);
    color: var(--text-strong);
}

@media (max-width: 1120px) {
    .nav {
        grid-template-columns: 1fr;
        justify-items: center;
        border-radius: 30px;
    }

    .links {
        flex-wrap: wrap;
    }
}

@media (max-width: 760px) {
    .nav {
        padding: 18px;
    }

    .links,
    .btn {
        width: 100%;
    }

    .links {
        justify-content: center;
    }
}
</style>
