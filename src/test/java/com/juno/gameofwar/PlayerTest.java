package com.juno.gameofwar;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Joe Teibel.
 */
public class PlayerTest {

    /**
     * Test that null is returned when a player's hand is empty
     */
    @Test
    public void emptyHandTest() {
        Player player = new Player(0);
        Card cardFromHand = player.playCardFromHand();
        assertEquals(cardFromHand, null);
        assertFalse(player.hasCards());
    }

    /**
     * Validate that the id we use to create a player is persistent
     */
    @Test
    public void validateCreateWithId() {
        final int id = 123;
        Player p = new Player(id);
        assertEquals(p.getId(), id);
    }

    /**
     * Test that player knows they have cards and that we get the cards back in the order added
     */
    @Test
    public void testGettingCorrectCardBack() {
        Card c1 = new Card(5, 3);
        Card c2 = new Card(6, 7);
        Player p = new Player(0);
        p.addCardToHand(c1);
        p.addCardToHand(c2);

        assertTrue(p.hasCards());
        assertEquals(p.numberOfCards(), 2);
        assertEquals(p.playCardFromHand(), c1);
        assertEquals(p.playCardFromHand(), c2);
    }
}
