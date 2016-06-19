package org.nalda.tennis;

interface RankingCalculator {

    fun calculate(from: Ranking, results: Collection<Result>, bonuses: Collection<Bonus>): Ranking
}

