package com.juno.gameofwar;

import org.junit.Test;

import java.security.InvalidParameterException;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Joe Teibel.
 */
public class WarDeckTest {

    // testing constants
    private static final int NUMBER_OF_SUITS = 4;
    private static final int NUMBER_OF_RANKS = 13;
    private static final int TOTAL_CARDS = NUMBER_OF_SUITS * NUMBER_OF_RANKS;

    // deck object used in multiple tests
    WarDeck deck = new WarDeck();

    /**
     * Test for bad create input (suits)
     */
    @Test (expected = InvalidParameterException.class)
    public void testCreateParamNumSuits() {

        deck.create(0, NUMBER_OF_RANKS);
    }

    /**
     * Test for bad deck create input (ranks)
     */
    @Test (expected = InvalidParameterException.class)
    public void testCreateParamNumRanks() {

        deck.create(NUMBER_OF_SUITS, 0);
    }

    /**
     * Test for null returned when deal is called with an empty deck
     */
    @Test
    public void validateThatWeCantDealFromEmptyDeck() {
        Card c = deck.deal();
        assertEquals(c, null);
    }

    /**
     * Validate that a deck size we expect is created
     */
    @Test
    public void validateDeckCreate() {
        deck.create(NUMBER_OF_SUITS, NUMBER_OF_RANKS);
        assertEquals(deck.numberOfUndealtCards(), TOTAL_CARDS);
    }

}
