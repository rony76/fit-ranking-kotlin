package org.nalda.tennis

import org.hamcrest.core.IsEqual
import org.hamcrest.core.IsEqual.equalTo
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThat
import org.junit.Test
import org.nalda.tennis.Outcome.LOSS
import org.nalda.tennis.Outcome.WIN
import org.nalda.tennis.Ranking.*
import java.util.*

class RankingCalculatorImplTest {
    private val calculator = RankingCalculatorImpl();

    @Test fun ncStaysSuchIfNoMatchIsPlayed() {
        val newRanking = calculator.calculate(IV_NC, Collections.emptySet(), emptySet())

        assertEquals(IV_NC, newRanking)
    }


    /* Il suo punteggio sarà di 935 punti calcolati nel modo seguente.
    * Come 1° gruppo di terza cat., somma le migliori 8 vittorie, cioè le 3
    * contro giocatori di un gradino superiore e 5 di quelle con i pari classifica per cui:
    * (3 x 90 = 270 punti per le tre vittorie sui giocatori II cat. 8° gr.) +
    * (5 x 60 = 300 punti per le cin- que vittorie sui giocatori III cat. 1° gr.)
    * + 240 di capitale di partenza, + 100 punti per assenza di sconfitte con giocatori di
    * classifica pari o inferiore, + 30 punti per la vittoria del torneo il cui miglior giocatore
    * era un pari classifica. Si aggiungono poi le vittorie supplementari date dalla formula
    * V-E-(2*I)-(3*G), che resti- tuisce il valore di 9 e quindi due vittorie supplementari.
    * Nel computo ne utilizza una sola (la sesta vittoria contro il 3’1) con 60 punti.
    * Poiché il punteggio minimo per la promozione al gradino superiore di un 3.1 è uguale a 780
    * il giocatore passa II categoria 8° gruppo.
    * A questo punto si calcola nuovamente il punteggio, sulla base della nuova classifica, nel
    * modo seguente. Essendo diventato 8° gruppo di seconda cat., somma le migliori 9 vittorie,
    * vale a dire le 3 contro giocatori, a questo punto dello stesso gruppo, e 6 di quelle con i
    * giocatori III cat. 1° gr., a questo punto di un gruppo inferiore, per cui:
    * (3 x 60 = 180 punti per le tre vittorie sui giocatori II cat. 8° gr.) +
    * (6 x 30 = 180 punti per le sei vittorie sui giocatori III cat. 1° gr.) +
    * 240 di capitale di partenza, che come detto è l’unico parametro che non varia,
    * + 15 per la vittoria del torneo il cui miglior giocatore era un giocatore di un gruppo
    * inferiore di classifica. Per le vittorie supplementari il coefficiente ora è di 6
    * (V=9, E=3, I=o, G=0) e quindi si considererà una vittoria contro il 3.1 (30 punti).
    * Non spettando più il bonus per assenza di sconfitte con giocatori di classifica pari od
    * inferiore avremo un totale di 645. Poiché il punteggio minimo per la promozione al gradino
    * superiore di un II categoria 8° gruppo è uguale a 910 il giocatore rimane II categoria
    * 8° gruppo con coefficiente di rendimento 645.
    * */
    @Test fun documentationExample() {
        // Consideriamo il caso di un 1° gruppo di III categoria
        val baseRanking = III_1

        val results = LinkedList<Result>()
        // che ha battuto 3 giocatori classificati 8° gruppo di II cat.
        results.add(Result(WIN, Player(II_8)))
        results.add(Result(WIN, Player(II_8)))
        results.add(Result(WIN, Player(II_8)))

        // e 6 giocatori classificati come lui,
        results.add(Result(WIN, Player(III_1)))
        results.add(Result(WIN, Player(III_1)))
        results.add(Result(WIN, Player(III_1)))
        results.add(Result(WIN, Player(III_1)))
        results.add(Result(WIN, Player(III_1)))
        results.add(Result(WIN, Player(III_1)))

        // ha subito sconfitte con 3 giocatori classificati 8° gruppo di II cat.,
        results.add(Result(LOSS, Player(II_8)))
        results.add(Result(LOSS, Player(II_8)))
        results.add(Result(LOSS, Player(II_8)))

        // ed ha vinto un torneo nel quale il giocatore di più alta
        // classifica era un 1° gruppo di III categoria.
        val bonuses = Collections.singleton(Bonus.tournamentWinWithBestRank(III_1))

        val finalRanking = calculator.calculate(baseRanking, results, bonuses)

        System.out.println(finalRanking)
    }

    @Test fun ronyGenMag2016() {
        val results = ArrayList<Result>(5)

        results.add(Result(LOSS, Player(IV_4)))
        results.add(Result(LOSS, Player(IV_4)))
        results.add(Result(WIN, Player(IV_NC)))
        results.add(Result(WIN, Player(IV_6)))
        results.add(Result(LOSS, Player(IV_5)))

        assertThat(calculator.calculate(IV_NC, results, emptySet()), equalTo(IV_6))
    }

    @Test fun ronyGiuNov2016() {
        val results = ArrayList<Result>(5)

        results.add(Result(LOSS, Player(IV_NC)))
        results.add(Result(WIN, Player(IV_NC)))
        results.add(Result(WIN, Player(IV_NC)))
        results.add(Result(LOSS, Player(IV_5)))
        results.add(Result(WIN, Player(IV_NC)))

        assertThat(calculator.calculate(IV_6, results, emptySet()), equalTo(IV_6))
    }
}

