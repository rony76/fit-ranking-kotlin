package org.nalda.tennis

import org.nalda.tennis.Outcome.LOSS_BY_WALK_OVER
import org.nalda.tennis.Tier.*

enum class Tier {
    IV, III, II, I
}

enum class Ranking(val tier: Tier, val basePoints: Int,
                   val promotionThreshold: Int, val demotionThreshold: Int,
                   private val baseMatchCount: Int) {
    IV_NC(IV, 0, 50, Int.MIN_VALUE, 5),
    IV_6(IV, 2, 100, 25, 5),
    IV_5(IV, 5, 165, 55, 5),
    IV_4(IV, 10, 240, 95, 5),
    IV_3(IV, 20, 340, 140, 6),
    IV_2(IV, 30, 410, 205, 6),
    IV_1(IV, 50, 480, 245, 6),
    III_5(III, 80, 510, 290, 7),
    III_4(III, 120, 560, 325, 7),
    III_3(III, 160, 660, 370, 7),
    III_2(III, 200, 730, 465, 8),
    III_1(III, 240, 780, 525, 8),
    II_8(II, 290, 910, 570, 9);

    fun delta(other: Ranking): Int {
        return other.ordinal - this.ordinal
    }

    fun matchesCount(results: Collection<Result>): Int {
        val additionalMatches = when (tier) {
            IV ->
                fourthTierAdditionalMatches(additionalMatchesFactor(results))
            III ->
                thirdTierAdditionalMatches(additionalMatchesFactor(results))
            II -> secondTierAdditionalMatches(additionalMatchesFactor(results))
            else -> 0
        }

        return baseMatchCount + additionalMatches
    }

    private fun secondTierAdditionalMatches(additionalMatchesFactor: Int): Int {
        return when (additionalMatchesFactor) {
            in 0..7 -> 1
            in 8..14 -> 2
            in 15..21 -> 3
            in 22..28 -> 4
            else -> 5
        }
    }

    private fun thirdTierAdditionalMatches(additionalMatchesFactor: Int): Int {
        return when (additionalMatchesFactor) {
            in 0..6 -> 1
            in 7..12 -> 2
            in 13..18 -> 3
            in 19..24 -> 4
            else -> 5
        }
    }

    private fun fourthTierAdditionalMatches(additionalMatchesFactor: Int): Int {
        return when (additionalMatchesFactor) {
            in 0..5 -> 1
            in 6..10 -> 2
            in 11..15 -> 3
            in 16..20 -> 4
            else ->
                if (additionalMatchesFactor < 0) 0 else 5
        }
    }

    private fun additionalMatchesFactor(results: Collection<Result>): Int {
        val wins = results.filter { it.outcome.isWin }
        val lossesAfterMatchStartByDelta = results
                .filter { !it.outcome.isWin && it.outcome != Outcome.LOSS_BY_WALK_OVER }
                .groupBy { delta(it.opponent.ranking) }

        val lossesByWalkOverCount = results
                .filter { it.outcome == LOSS_BY_WALK_OVER && delta(it.opponent.ranking) <= 0 }
                .count()

        val v = wins.count()
        var e = lossesAfterMatchStartByDelta[0].orEmpty().count()
        var i = lossesAfterMatchStartByDelta[-1].orEmpty().count()
        var g = lossesAfterMatchStartByDelta.filterKeys { it < -1 }.values.flatten().count()

        if (lossesByWalkOverCount >= 3) e++
        if (lossesByWalkOverCount >= 4) i++
        if (lossesByWalkOverCount > 4) g += lossesByWalkOverCount - 4;

        return v - e - (2 * i) - (3 * g)
    }

    fun demote(): Ranking {
        return if (this == worst()) this
        else Ranking.values()[this.ordinal - 1]
    }

    fun promote(): Ranking {
        return if (this == best()) this
        else Ranking.values()[this.ordinal + 1]
    }

    companion object Util {
        fun best(): Ranking {
            return Ranking.values()[Ranking.values().size - 1]
        }

        fun worst(): Ranking {
            return Ranking.values()[0]
        }
    }

    fun worseThanOrEqual(other: Ranking): Boolean {
        return ordinal <= other.ordinal
    }

    fun bonusForNoLossesWithPeerOrWorse(): Int {
        return if (this == IV_NC) 0 else
            when (tier) {
                IV -> 50
                III, II -> 100
                else -> 0
            }

    }
}