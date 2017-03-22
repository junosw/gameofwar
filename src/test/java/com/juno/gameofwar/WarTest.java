package com.juno.gameofwar;

import org.junit.Before;
import org.junit.Test;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Joe Teibel.
 */
public class WarTest {

    // helper constants
    private static final int NO_WAR_STARTING_SCORE = 0;

    // war object to use across multiple tests
    War war = new War();

    // players for testing elements of the war game
    // create 2 players for the test
    Player p1 = new Player(1);
    Player p2 = new Player(2);
    Player p3 = new Player(3);
    Player p4 = new Player(4);

    List<Player> players = new ArrayList<>(2);

    /**
     * Init some objects used in the following tests
     */
    @Before
    public void setup() {

        p1.addCardToHand(new Card(1, 1));
        p2.addCardToHand(new Card(1, 2));
        p3.addCardToHand(new Card(1, 3));
        p4.addCardToHand(new Card(1, 0));

        players.add(p1);
        players.add(p2);
        players.add(p3);
        players.add(p4);
    }

    /**
     * Test dealing with a known deck and players
     */
    @Test
    public void testDealing() {
        WarDeck testDeck = new WarDeck();

        final int numSuits = 2;
        final int numRanks = 4;

        // a deck with 8 cards
        testDeck.create(numSuits, numRanks);

        assertEquals(testDeck.numberOfUndealtCards(), numSuits * numRanks);

        List<Player> playersCopy = new ArrayList<>(players);

        war.dealCards(playersCopy, testDeck);

        // 4 players in test player list each with a card to start
        // the we dealt them two more each so each should have 3 cards
        final int expectedNumCards = 3;

        assertEquals(playersCopy.get(0).numberOfCards(), expectedNumCards);
        assertEquals(playersCopy.get(1).numberOfCards(), expectedNumCards);
        assertEquals(playersCopy.get(2).numberOfCards(), expectedNumCards);
        assertEquals(playersCopy.get(3).numberOfCards(), expectedNumCards);
    }

    /**
     * Test that War knows when players have no cards left
     */
    @Test
    public void testPlayersHaveCards() {
        List<Player> playersCopy = new ArrayList<>(players);
        // get rid of all players cards
        playersCopy.forEach(player -> {
            player.playCardFromHand();
        });

        assertFalse(war.onePlayerHasCards(playersCopy));

        playersCopy.get(0).addCardToHand(new Card(1, 1));

        assertTrue(war.onePlayerHasCards(playersCopy));
    }

    /**
     * We require each player to have at least one card - validate
     */
    @Test (expected = InvalidParameterException.class)
    public void validPlayParamsTest() {
        war.play(1, 1, 10);
    }

    /**
     * Game policy is to return the first player as winner if all cards are gone with a score of zero
     */
    @Test
    public void testFirstPlayerReturnedWhenNoCardsLeft() {
        List<Player> playersCopy = new ArrayList<>(players);
        // get rid of all players cards - round by definition should then tell us
        // that the first player in the list won
        playersCopy.forEach(player -> {
            player.playCardFromHand();
        });

        RoundResult rr = war.doWar(players, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p1);
        // score should be zero
        final int expectedScore = 0;
        assertEquals(rr.getScore(), 0);
    }

    /**
     * Test a basic one winner scenario
     */
    @Test
    public void testPlayRoundBasic() {
        RoundResult rr = war.playRound(players, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p3);
    }

    /**
     * Test a tie between two players
     */
    @Test
    public void testTie() {
        List<Player> tiedPlayers = new ArrayList<>(players);
        Player p5 = new Player(5);
        p5.addCardToHand(new Card(2, 3));
        tiedPlayers.add(p5);

        final int numberOfExpectedTies = 2;

        RoundResult rr = war.playRound(tiedPlayers, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getTiedPlayers().size(), numberOfExpectedTies);
        assertEquals(rr.getTiedPlayers().get(0), p3);
        assertEquals(rr.getTiedPlayers().get(1), p5);
    }

    /**
     * Test a scenario where multiple players tie
     */
    @Test
    public void testMultiTie() {
        List<Player> tiedPlayers = new ArrayList<>(players);
        Player p5 = new Player(5);
        p5.addCardToHand(new Card(2, 3));
        tiedPlayers.add(p5);

        Player p6 = new Player(6);
        p6.addCardToHand(new Card(3, 3));
        tiedPlayers.add(p6);

        RoundResult rr = war.playRound(tiedPlayers, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getTiedPlayers().get(0), p3);
        assertEquals(rr.getTiedPlayers().get(1), p5);
        assertEquals(rr.getTiedPlayers().get(2), p6);
    }

    /**
     * Set up a player array that has a tie then the tie is broken by one winner
     */
    @Test
    public void testSwitchFromTieToWinner() {
        List<Player> p6Winner = new ArrayList<>(players);
        Player p5 = new Player(5);
        p5.addCardToHand(new Card(2, 3));
        p6Winner.add(p5);

        Player p6 = new Player(6);
        p6.addCardToHand(new Card(3, 4));
        p6Winner.add(p6);

        RoundResult rr = war.playRound(p6Winner, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p6);
    }

    /**
     * Setup player array so p3 has no cards so p2 should be winner
     */
    @Test
    public void testPlayerOutOfCards() {
        List<Player> playersCopy = new ArrayList<>(players);
        // get rid of player 3's card so another player will win
        playersCopy.get(2).playCardFromHand();

        RoundResult rr = war.playRound(players, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p2);
    }

    /**
     * playRound may take a starting score (in support of "war rounds") so test that it does what we expect
     */
    @Test
    public void testStartingScoreDuringRound() {
        final int testStartScore = 10;
        // expected result is equal to the number of players in the round (they play one card each)
        // PLUS the starting score
        final int expectedResult = testStartScore + players.size();

        RoundResult rr = war.playRound(players, testStartScore);
        assertEquals(rr.getScore(), expectedResult);
    }

    /**
     * Test that when a "war round" is played, if no players have cards remaining, the first player to "play"
     * the previous round is declared the winner
     */
    @Test
    public void testWarRoundNoPlayersWithCards() {
        List<Player> playersCopy = new ArrayList<>(players);
        // get rid of all players cards - war round by definition should then tell us
        // that the first player in the list won
        playersCopy.forEach(player -> {
            player.playCardFromHand();
        });

        RoundResult rr = war.doWar(players, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p1);
    }

    /**
     * Test that when a "war round" is played, if players have ONE card remaining, the first player to "play"
     * the is declared the winner
     */
    @Test
    public void testWarWhenPlayersHaveOneCardLeft() {
        RoundResult rr = war.doWar(players, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p1);
    }

    /**
     * Basic two card war test
     */
    @Test
    public void testWarExpectedResult() {
        List<Player> playersCopy = new ArrayList<>(players);
        // add a card to every players hand with rank == index
        // this means then that with two cards in every hand, we expect the
        // highest indexed player (p4) to win war (the first card is the set in the parent class)
        for (int i = 0; i < playersCopy.size(); i++) {
            playersCopy.get(i).addCardToHand(new Card(4, i));
        }

        RoundResult rr = war.doWar(playersCopy, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p4);
        // starting with a zero score, we also expect the score to be 8 - total cards played during war
        // number of players * number of cards played
        final int expectedScore = 8;

        assertEquals(rr.getScore(), expectedScore);
    }

    /**
     * Expect two rounds of war; validate winner and score
     */
    @Test
    public void recursiveWarTest() {
        List<Player> playersCopy = new ArrayList<>(players);
        // first card in players hand is defined above - in war round, this card is discard first
        // second card in players hand decides war round 1 - we want a tie so set rank same for everyone
        // third card is the discard for the second round of war
        // fourth card is the decider: rank == players.size() - i so that p1 should win
        // the war with 16 points

        for (int i = 0; i < playersCopy.size(); i++) {
            playersCopy.get(i).addCardToHand(new Card(4, 4));
            playersCopy.get(i).addCardToHand(new Card(4, i));
            playersCopy.get(i).addCardToHand(new Card(4, (playersCopy.size() - i)));
        }
        RoundResult rr = war.doWar(playersCopy, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p1);

        // starting with a zero score, we also expect the score to be 16 - total cards played during
        // two rounds of war:  number of players * number of cards played
        final int expectedScore = 16;

        assertEquals(rr.getScore(), expectedScore);
    }

    /**
     * This test expects two rounds of war but only two players play the second round
     */
    @Test
    public void recursiveWarTestVariedNumSecondRound() {
        List<Player> playersCopy = new ArrayList<>(players);
        // first card in players hand is defined above - in war round, this card is discard first
        // second card in players hand decides war round 1 - we want a tie BETWEEN TWO PLAYERS - other two are out
        // third card is the discard for the second round of war
        // fourth card is the decider: rank == players.size() - i so that p1 should win
        // the war with 16 points
        playersCopy.get(0).addCardToHand(new Card(4, 1));
        // players at index 1 & 2 (p2 & 3) tie in first war
        playersCopy.get(1).addCardToHand(new Card(4, 4));
        playersCopy.get(2).addCardToHand(new Card(3, 4));
        playersCopy.get(3).addCardToHand(new Card(4, 0));
        // discard for second war
        for (int i = 0; i < playersCopy.size(); i++) {
            playersCopy.get(i).addCardToHand(new Card(4, i));
        }
        // set up index 1 (p2) to win
        playersCopy.get(1).addCardToHand(new Card(3, 5));
        playersCopy.get(2).addCardToHand(new Card(3, 2));

        RoundResult rr = war.doWar(playersCopy, NO_WAR_STARTING_SCORE);
        assertEquals(rr.getWinner(), p2);

        // starting with a zero score, we also expect the score to be 12
        // 2 cards per player round 1, 4 total round 2 (2 for each of two players)
        final int expectedScore = 12;

        assertEquals(rr.getScore(), expectedScore);
    }

    /**
     * Now actually play a game with a stacked deck and look for the expected result
     */
    @Test
    public void playWar() {
        TestDeck testDeck = new TestDeck();
        testDeck.create(2, 3);

        War war = new War(testDeck);

        // because we initialized the War object with a deck, the SUITS and RANKS values
        // won't actually be used - our deck that we've already created will
        // the NUM_PLAYERS value WILL be used
        // So we have 4 cards and two players
        List<Player> winningPlayers =
                war.play(GameOfWar.DEFAULT_NUM_SUITS, GameOfWar.DEFAULT_NUM_RANKS, GameOfWar.DEFAULT_NUM_PLAYERS);

        // we know that the test deck is built like this:
        // card1(0, 0), card2(0, 1), card3(0, 2), card4(1, 0), card5(1, 1), card6(1, 2)
        // so then when it is dealt to two players in circular fashion, the player hands should look like:
        // p1Hand = card1, card3, card5
        // p2Hand = card2, card4, card6
        // so playing this out, p2 wins 2 points in round 1, p1 wins 2 in round 2, p2 wins 2 in round 3
        // p2 should win with 4 points
        final int expectedNumberOfWinners = 1;
        final int expectedWinnerId = 2;

        assertEquals(winningPlayers.size(), expectedNumberOfWinners);
        assertEquals(winningPlayers.get(0).getId(), expectedWinnerId);
    }
}
