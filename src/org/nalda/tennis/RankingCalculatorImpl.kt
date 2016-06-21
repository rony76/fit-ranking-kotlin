package org.nalda.tennis

import org.nalda.tennis.RankingCalculator.CalculationResult
import java.util.*

class RankingCalculatorImpl : RankingCalculator {
    override fun calculate(from: Ranking, results: Collection<Result>, bonuses: Collection<Bonus>): CalculationResult {
        val breakdown = calculate(from.basePoints, from, results, bonuses, emptyList())
        return CalculationResult(breakdown.last().first, breakdown)
    }

    private fun calculate(basePoints: Int, from: Ranking, matchResults: Collection<Result>,
                          bonuses: Collection<Bonus>, resultBreakdown: List<Pair<Ranking, Int>>): List<Pair<Ranking, Int>> {
        val relevantMatchesCount = from.matchesCount(matchResults)
        val relevantMatches =
                matchResults
                        .filter { it.outcome.isWin }
                        .sortedByDescending { it.opponent.ranking }
                        .take(relevantMatchesCount)

        val totalPoints = basePoints +
                relevantMatches.sumBy { it.getPoints(from) } +
                bonuses.sumBy { it.getPoints(from) } +
                noLossBonus(from, matchResults)

        if (resultBreakdown.isEmpty() && (totalPoints <= from.demotionThreshold)) {
            return Arrays.asList(Pair(from, totalPoints), Pair(from.demote(), 0))
        }

        val newResult = resultBreakdown + Pair(from, totalPoints)

        if (totalPoints < from.promotionThreshold)
            return newResult

        if (from == Ranking.best())
            return newResult

        return calculate(basePoints, from.promote(), matchResults, bonuses, newResult)
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
