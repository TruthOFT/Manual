import type { ProductCard } from '@/types/home'

export type RecommendationProduct = ProductCard & {
    recommendScore?: number | string | null
    recommendReason?: string | null
    rankNo?: number | null
    similarityScore?: number | string | null
    coBehaviorCount?: number | null
    algorithmVersion?: string | null
    calculatedTime?: string | null
}
