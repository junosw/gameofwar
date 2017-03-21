package com.juno.gameofwar;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;


/**
 * Created by Joe Teibel.
 */
public class WarDeck implements Deck {

    // our list of undealt cards, init to size 0
    // call create to fill the deck with cards
    private List<Card> undealtCards = new ArrayList<>(0);

    // our list of dealt cards.  Cards move from the undealt list to this least via the deal method
    private List<Card> dealtCards = new ArrayList<>(0);

    /**
     * Create a new deck of cards. Throws an exception if parameter constraints are not met.
     *
     * @param numberOfSuits - a value greater than zero and less than max int
     * @param numberOfRanks - a value greater than zero and less than max int.
     */
    @Override
    public void create(final int numberOfSuits, final int numberOfRanks) {

        // validate input and throw appropriate exception if expectations not met
        if (numberOfSuits < 1) {
            throw new InvalidParameterException("numberOfSuits must be great than zero.");
        }

        if (numberOfRanks < 1) {
            throw new InvalidParameterException("numberOfRanks must be great than zero.");
        }

        // init the undealt cards
        undealtCards = new ArrayList<>(numberOfSuits * numberOfRanks);

        for (int suit = 0; suit < numberOfSuits; suit++) {

            for (int rank = 0; rank < numberOfRanks; rank++) {
                undealtCards.add(new Card(suit, rank));
            }
        }
    }

    /**
     * Randomize the list of cards in the deck
     */
    @Override
    public void shuffle() {
        long seed = System.nanoTime();
        Collections.shuffle(undealtCards, new Random(seed));
    }

    /**
     * Deal a card out of the deck.  Returns null if the deck has not been created yet or all the cards have been dealt.
     * To make more cards available, call create a new deck or call shuffle to reset all cards to undealt.
     * @return
     */
    @Override
    public Card deal() {
        // return null if we have no undealt cards
        if (undealtCards.size() == 0) {
            return null;
        }

        // grab the "top" card from the undealt list, add it to dealt list, remove from it from undealt
        // then return it
        Card c = undealtCards.get(0);
        dealtCards.add(c);
        undealtCards.remove(0);
        return c;
    }

    /**
     * Get the number of cards in the deck that have not been dealt
     * @return
     */
    public int numberOfUndealtCards() {
        return undealtCards.size();
    }
}
