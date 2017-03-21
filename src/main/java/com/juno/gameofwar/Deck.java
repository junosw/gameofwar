package com.juno.gameofwar;

/**
 * Created by Joe Teibel.
 */
public interface Deck {

    void create(int numberOfSuits, int numberOfRanks);
    void shuffle();
    Card deal();
}
