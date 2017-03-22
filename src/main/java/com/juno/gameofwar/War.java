package com.juno.gameofwar;

import java.security.InvalidParameterException;
import java.util.*;

/**
 * Created by Joe Teibel.
 */
public class War {

    // map for keeping score of the game
    private HashMap<Player, Integer> playerScoreMap = new HashMap<Player, Integer>();
    // Deck to use to play a game of war
    private Deck deck = null;

    /**
     * No-arg c'tor - this should be used for "normal" play
     */
    public War() { }

    /**
     * Init with a deck; dependency injection for testing.
     * @param deck
     */
    public War(final Deck deck) {
        this.deck = deck;
    }

    /**
     * Play the game of war with the specified number of players, suits and ranks
     *
     * @param numberOfSuits
     * @param numberOfRanks
     * @param numberOfPlayers
     * @return A list of the winning players
     */
    public List<Player> play(final int numberOfSuits, final int numberOfRanks, final int numberOfPlayers) {

        // each player should at least have 1 card to play the game - validate
        if ((numberOfSuits * numberOfRanks) < numberOfPlayers) {
            throw new InvalidParameterException("numberOfSuits time numberOfRanks must be great than numberOfPlayers");
        }

        if (deck == null) {
            // create a deck for the game
            deck = new WarDeck();
            deck.create(numberOfSuits, numberOfRanks);
            deck.shuffle();
        }

        // for each player, create a player object
        List<Player> players = new ArrayList<>(numberOfPlayers);

        for (int i = 0; i < numberOfPlayers; i++) {
            players.add(new Player(i + 1));
        }

        // now deal the cards
        dealCards(players, deck);

        // play the game
        while (onePlayerHasCards(players)) {

            // Play a "Round"
            // A round is where each player plays ONE card and either there is a clear winner
            // and we get the "score" that winner should get OR
            // there is a tie between one or more players.
            // In this case the result gives us the score from the round AND
            // a list of the tied players

            RoundResult rr = playRound(players, 0);

            if (rr.isTie()) { // WAR!
                // this is a recursive function that will resolve all subsequent ties and
                // return a final score and winner
                rr = doWar(rr.getTiedPlayers(), rr.getScore());
            }

            // there should now be one "round result" winner with all wars resolved and
            // rr has the "final" score for the winning player
            // save that score and associate with the winning player
            final Player winningPlayer = rr.getWinner();

            // has this player won before?
            if (playerScoreMap.containsKey(winningPlayer)) {
                // add it to their score
                Integer newScore = playerScoreMap.get(winningPlayer) + rr.getScore();
                playerScoreMap.put(winningPlayer, newScore);

            } else { // first time winner
                playerScoreMap.put(winningPlayer, rr.getScore());
            }
        }

        // find the high score
        Integer highScore = 0;
        // there may be a tie so keep track of who has high score
        List<Player> winningPlayers = new ArrayList<>();
        // iterate through player scores to find high score(s)
        Iterator<Map.Entry<Player, Integer>> iter = playerScoreMap.entrySet().iterator();

        while (iter.hasNext()) {
            Map.Entry<Player, Integer> playerScore = iter.next();

            // if they equaled it, just add them to the map
            if (playerScore.getValue().equals(highScore)) {
                winningPlayers.add(playerScore.getKey());

            } else if (playerScore.getValue() > highScore) {
                // else if the current player beat the high score, all previous winners are invalid
                winningPlayers.clear();
                winningPlayers.add(playerScore.getKey());
                highScore = playerScore.getValue();
            }
        }

        System.out.println("\nThe winning score was " + highScore);
        System.out.println("\nThere were " + winningPlayers.size() + " winning players!");
        System.out.println("\n----- WINNERS -----");

        winningPlayers.forEach(player -> {
            System.out.println("\t" + player.getId());
        });

        return winningPlayers;
    }

    /**
     * Deal cards in circular fashion to all the players
     *
     * @param players
     * @param deckParam
     */
    public void dealCards(final List<Player> players, final Deck deckParam) {
        Card dealCard = deckParam.deal();
        int curPlayer = 0;

        // while we have a valid card
        while (dealCard != null) {
            // get the next player and give them the card
            players.get(curPlayer).addCardToHand(dealCard);

            curPlayer++;

            // loop through player list
            if (curPlayer == players.size()) {
                curPlayer = 0;
            }

            dealCard = deckParam.deal();
        }
    }

    /**
     * Play one "basic" round of war and return id of the winning player
     *
     * A round is where each player plays ONE card and either there is a clear winner and we get the "score" that
     * winner should get OR
     * there is a tie between one or more players.  In this case the result gives us the score from the round AND
     * a list of the tied players
     *
     * @param players
     */
    public RoundResult playRound(final List<Player> players, final int startingScore) {

        Iterator<Player> playerIter = players.iterator();
        // need to init these in the loop below to find a player that actually has cards
        Player currentWinningPlayer = null;
        Card currentWinningCard = null;

        // initialize by finding the first player with cards left and persisting their data
        // as the current "winning player"
        while (playerIter.hasNext()) {
            currentWinningPlayer = playerIter.next();
            currentWinningCard = currentWinningPlayer.playCardFromHand();
            if (currentWinningCard != null) {
                break;
            }
        }

        // game policy: if no one has cards left, the first player wins
        if (currentWinningCard == null) { // round is over
            return new RoundResult(startingScore, players.get(0));
        }

        // now we have at least one player with a card,
        // score equals the number of cards played.
        // we just played 1 card to init so score starts at startingScore + 1
        int score = startingScore + 1;

        // keep track of ties players to sent back in the result
        List<Player> tiedPlayers = null;

        while (playerIter.hasNext()) {

            Player currentPlayer = playerIter.next();
            Card currentCard = currentPlayer.playCardFromHand();

            if (currentCard == null) {
                // the player is out of cards and cannot be considered
                continue;
            }

            // increment score for every card played
            score++;

            // did current card beat winning card outright?
            if (currentCard.getRank() > currentWinningCard.getRank()) {
                // set this guy as winner for now
                currentWinningPlayer = currentPlayer;
                // which means we have no ties
                tiedPlayers = null;
                // save current winning card
                currentWinningCard = currentCard;

            } else if (currentCard.getRank() == currentWinningCard.getRank()) {
                // tied, set up for war result

                // if tied players is null, then we have one winner currently
                // switch it now to a tie
                if (tiedPlayers == null) {
                    tiedPlayers = new ArrayList<>();
                    tiedPlayers.add(currentWinningPlayer);
                    currentWinningPlayer = null;
                }

                tiedPlayers.add(currentPlayer);

            } // else - current card is lower than current winning card, no-op
        }

        // we either have a winner or a tie with at least two players. In the case of a tie, war will
        // be initialized by the calling function

        if (currentWinningPlayer != null) {
            return new RoundResult(score, currentWinningPlayer);
        } else {
            return new RoundResult(score, tiedPlayers);
        }
    }

    /**
     *  Play a standard round of war with a set of players
     *  This function is recursive - every time there is a tie between two or more players another round
     *  will be played until all players are out of cards or there is one winner.
     *  Score is persisted through each round of war and a total score for all wars is returned.
     *
     * @param players
     * @return
     */
    public RoundResult doWar(final List<Player> players, final int startingScore) {

        int score = startingScore;

        // each player discards one card now because that's the rules of war
        // score is increased equal to number of cards discarded
        // players may run out of cards during war - they auto-lose
        List<Player> playersStillIn = new ArrayList<>();

        for (int i = 0; i < players.size(); i++) {
            // loop through, get a player, grab a card, make sure it is valid, add that player to "still playing" list
            // then increment score
            Player p = players.get(i);
            Card c = p.playCardFromHand();
            if (c != null) {
                playersStillIn.add(p);
                score++;
            }
        }

        // if all players are out of cards, the first player to play the previous round wins
        if (playersStillIn.size() == 0) {
            return new RoundResult(score, players.get(0));
        }

        // now we play a round!
        RoundResult warResult = playRound(playersStillIn, score);

        if (warResult.isTie()) {
            // do it again!
            return doWar(warResult.getTiedPlayers(), warResult.getScore());
        }

        return warResult;
    }

    /**
     * Returns true if one player in the list has cards.  False if no player has cards.
     * @param players
     * @return
     */
    public boolean onePlayerHasCards(final List<Player> players) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).hasCards()) {
                return true;
            }
        }
        return false;
    }
}
