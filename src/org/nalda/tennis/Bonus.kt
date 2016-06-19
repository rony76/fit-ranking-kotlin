package org.nalda.tennis

interface Bonus {
    fun getPoints(from: Ranking): Int

    companion object Bonuses {
        fun tournamentWinWithBestRank(bestRanking: Ranking): Bonus {
            return object: Bonus {
                override fun getPoints(from: Ranking): Int {
                    // additional points for winning a tournament are half of the points
                    // you'd get by winning with the best ranked player in the tournament
                    return Result(Outcome.WIN, Player(bestRanking)).getPoints(from) / 2
                }
            }
        }
    }
}

