package org.nalda.tennis

enum class Outcome(val isWin: Boolean) {
    WIN(true),
    LOSS(false),
    WIN_BY_WALK_OVER(true),
    LOSS_BY_WALK_OVER(false),
    WIN_BY_RETIREMENT(true),
    LOSS_BY_RETIREMENT(false);
}

data class Player(val ranking: Ranking)

data class Result(val outcome: Outcome, val opponent: Player) {
    private fun deltaToPoints(delta: Int): Int {
        return when (delta) {
            1 -> 90
            0 -> 60
            -1 -> 30
            -2 -> 20
            -3 -> 15
            else -> if (delta > 1) 120 else 0
        }
    }

    fun getPoints(from: Ranking): Int {
        val delta = from.delta(opponent.ranking);

        return deltaToPoints(delta);
    }
}