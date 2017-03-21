package com.juno.gameofwar;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Joe Teibel.
 */
public class CardTest {

    private static final int CARD_SUIT = 1;
    private static final int CARD_RANK = 0;


    @Test
    public void testCardCreateSuit() {

        Card c = new Card(CARD_SUIT, CARD_RANK);
        assertEquals(c.getSuit(), CARD_SUIT);
    }

    @Test
    public void testCardCreateRank() {

        Card c = new Card(CARD_SUIT, CARD_RANK);
        assertEquals(c.getRank(), CARD_RANK);
    }
}
