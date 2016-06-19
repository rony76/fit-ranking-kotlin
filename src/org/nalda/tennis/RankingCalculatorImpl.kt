package org.nalda.tennis

class RankingCalculatorImpl : RankingCalculator {
    override fun calculate(from: Ranking, results: Collection<Result>, bonuses: Collection<Bonus>): Ranking {
        return calculate(from.basePoints, from, results, bonuses)
    }

    private fun calculate(basePoints: Int, from: Ranking, results: Collection<Result>, bonuses: Collection<Bonus>): Ranking {
        val relevantMatchesCount = from.matchesCount(results)
        val relevantMatches =
                results
                        .filter { it.outcome.isWin }
                        .sortedByDescending { it.opponent.ranking }
                        .take(relevantMatchesCount)

        val totalPoints = basePoints +
                relevantMatches.sumBy { it.getPoints(from) } +
                bonuses.sumBy { it.getPoints(from) } +
                noLossBonus(from, results)


        if (totalPoints <= from.demotionThreshold)
            return from.demote()

        if (totalPoints < from.promotionThreshold)
            return from

        val promotedRanking = from.promote()
        if (promotedRanking == Ranking.best())
            return promotedRanking

        return calculate(basePoints, promotedRanking, results, bonuses)
    }

    private fun noLossBonus(from: Ranking, results: Collection<Result>): Int {
        return if (noLossesWithPeerOrWorseBonusApplies(from, results))
            from.bonusForNoLossesWithPeerOrWorse()
        else 0
    }

    private fun noLossesWithPeerOrWorseBonusApplies(from: Ranking, results: Collection<Result>): Boolean {
        val matchesWithPeerOrWorse = results.filter { it.opponent.ranking.worseThanOrEqual(from) }

        if (matchesWithPeerOrWorse.count() < 5)
            return false

        return matchesWithPeerOrWorse.all { it.outcome.isWin }
    }

}
