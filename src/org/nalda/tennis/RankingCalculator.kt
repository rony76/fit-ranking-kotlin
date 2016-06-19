package org.nalda.tennis;

interface RankingCalculator {
    data class CalculationResult(val newRanking: Ranking, val calculationBreakdown: List<Pair<Ranking, Int>>)

    fun calculate(from: Ranking, results: Collection<Result>, bonuses: Collection<Bonus>): CalculationResult

}

