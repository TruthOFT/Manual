import type { ArtisanItem, ProductCard } from '@/types/home'

export type ArtisanDetail = ArtisanItem & {
    artisanStory: string | null
    products: ProductCard[]
}
