package com.juno.gameofwar;

/**
 * Created by Joe Teibel.
 */
public class Card {

    private final int suit;
    private final int rank;

    /**
     * Instantiate a new Card
     * @param suit
     * @param rank
     */
    public Card(final int suit, final int rank) {
        this.suit = suit;
        this.rank = rank;
    }

    /**
     * Get the suit of the card
     * @return
     */
    public int getSuit() {
        return suit;
    }

    /**
     * Get the rank of the card
     * @return
     */
    public int getRank() {
        return rank;
    }
}
