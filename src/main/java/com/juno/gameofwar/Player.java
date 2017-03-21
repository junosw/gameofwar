package com.juno.gameofwar;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe Teibel.
 */
public class Player {

    // list of cards this player is holding
    private List<Card> hand = new ArrayList<>(0);
    // an integer id for the player
    private final int id;

    /**
     * Create a new player with id
     * @param id - identifier for the player
     */
    public Player(final int id) {
        this.id = id;
    }

    /**
     * Return the player id
     * @return
     */
    public int getId() {
        return id;
    }


    /**
     * Add a card to this players hand
     * @param card
     */
    public void addCardToHand(final Card card) {
        hand.add(card);
    }

    /**
     * Play the "top" card from this players hand.
     * Returns null if this player has no cards in their hand.
     */
    public Card playCardFromHand() {
        // do we have a card to return?
        if (hand.size() == 0) {
            return null;
        }

        Card returnCard = hand.get(0);
        hand.remove(0);
        return returnCard;
    }

    /**
     * Returns true if their are cards in this players hand
     * @return
     */
    public boolean hasCards() {
        return hand.size() != 0;
    }

    /**
     * Returns the number of cards in the players hand
     * @return
     */
    public int numberOfCards() {
        return hand.size();
    }
}
