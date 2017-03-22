package com.juno.gameofwar;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joe Teibel
 */
public class TestDeck implements Deck {
    // our list of undealt cards, init to size 0
    // call create to fill the deck with cards
    private List<Card> undealtCards = new ArrayList<>(0);

    @Override
    public void create(int numberOfSuits, int numberOfRanks) {
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

    @Override
    public void shuffle() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Card deal() {
        // return null if we have no undealt cards
        if (undealtCards.size() == 0) {
            return null;
        }

        // grab the "top" card from the undealt list, remove from it from undealt
        // then return it
        Card c = undealtCards.get(0);
        undealtCards.remove(0);
        return c;
    }
}
