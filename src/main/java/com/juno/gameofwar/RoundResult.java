package com.juno.gameofwar;

import java.util.List;

/**
 * Created by Joe Teibel.
 */
public class RoundResult {

    // the score
    // in a war scenario, this may be the aggregate score of many rounds
    private final int score;
    // this list contains the players that tied in this round
    private final List<Player> tiedPlayers;
    // this is the winner.  Note that a winner means there are no ties and vice versa
    private final Player winner;

    /**
     * Instantiate a new round where the players tied
     * @param score
     * @param tiedPlayers
     */
    public RoundResult(final int score, final List<Player> tiedPlayers) {
        this.score = score;
        this.tiedPlayers = tiedPlayers;
        this.winner = null;
    }

    /**
     * Instantiate a new round where one player won
     * @param score
     * @param winnner
     */
    public RoundResult(final int score, final Player winnner) {
        this.score = score;
        this.tiedPlayers = null;
        this.winner = winnner;
    }

    /**
     * Get the score from this round
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * Get the list of tied players.  Returns null if there was a winner i.e. no ties
     * @return
     */
    public List<Player> getTiedPlayers() {
        return tiedPlayers;
    }

    /**
     * Returns the winner if there was one.  In case a tie, this will be null and getTiedPlayers can be called
     * to get a list of those that tied.
     * @return
     */
    public Player getWinner() {
        return winner;
    }

    /**
     * Returns true if this result is a tie
     * @return
     */
    public boolean isTie() {
        // in case of a tie, winner should always be null
        return winner == null;
    }
}
