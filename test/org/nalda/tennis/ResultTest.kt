package org.nalda.tennis

import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import org.nalda.tennis.Outcome.WIN
import org.nalda.tennis.Ranking.IV_4
import org.nalda.tennis.Ranking.IV_NC

class ResultTest {
    @Test
    fun ncWinsOverNc() {
        assertThat(Result(WIN, Player(IV_NC)).getPoints(IV_NC), equalTo(60))
    }

    @Test
    fun ncWinsOverIV4() {
        assertThat(Result(WIN, Player(IV_NC)).getPoints(IV_4), equalTo(15))
    }

    @Test
    fun iv4WinsOverNC() {
        assertThat(Result(WIN, Player(IV_4)).getPoints(IV_NC), equalTo(120))
    }

}