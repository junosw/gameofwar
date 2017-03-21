package com.juno.gameofwar;

import java.security.InvalidParameterException;
import java.util.*;

import org.apache.commons.cli.*;

/**
 * Created by Joe Teibel.
 */
public class War {

    // defaults
    public static final int DEFAULT_NUM_SUITS = 4;
    public static final int DEFAULT_NUM_RANKS = 13;
    public static final int DEFAULT_NUM_PLAYERS = 2;

    public static final String PROGRAM_CMD = "java -jar build/libs/war-1.0.jar";
    public static final String SUITS_OPT = "s";
    public static final String SUITS_OPT_LONG = "suits";
    public static final String SUITS_OPT_NAME = "Suits";
    public static final String RANKS_OPT = "r";
    public static final String RANKS_OPT_LONG = "ranks";
    public static final String RANKS_OPT_NAME = "Ranks";
    public static final String PLAYERS_OPT = "p";
    public static final String PLAYERS_OPT_LONG = "players";
    public static final String PLAYERS_OPT_NAME = "Players";
    public static final String HELP_OPT = "h";
    public static final String HELP_OPT_LONG = "help";
    public static final String HELP_OPT_NAME = "Help";

    // some reasonable defaults just to put some gaurd rails around memory usage
    public static final Integer MAX_SUITS = 100;
    public static final Integer MIN_SUITS = 1;
    public static final Integer MAX_RANKS = 10000;
    public static final Integer MIN_RANKS = 1;
    // according to wikipedia this is actually the max players :)
    public static final Integer MAX_PLAYERS = 600;
    public static final Integer MIN_PLAYERS = 2;

    // map for keeping score of the game
    private HashMap<Player, Integer> playerScoreMap = new HashMap<Player, Integer>();

    /**
     * Main entry point
     * @param args
     */
    public static void main(final String[] args) {

        // set up options parsing
        Options options = new Options();

        Option suitsOption = Option.builder(SUITS_OPT)
                .argName(SUITS_OPT_NAME)
                .longOpt(SUITS_OPT_LONG)
                .hasArg()
                .desc("Number of suits to use for the deck of cards. Minimum value is "
                        + MIN_SUITS + ", max value is " + MAX_SUITS)
                .build();

        Option ranksOption = Option.builder(RANKS_OPT)
                .argName(RANKS_OPT_NAME)
                .longOpt(RANKS_OPT_LONG)
                .hasArg()
                .desc("Number of ranks to use for the deck of cards. Minimum value is "
                        + MIN_RANKS + ", max value is " + MAX_RANKS)
                .build();

        Option playersOption = Option.builder(PLAYERS_OPT)
                .argName(PLAYERS_OPT_NAME)
                .longOpt(PLAYERS_OPT_LONG)
                .hasArg()
                .desc("Number of players to use for the game. Minimum value is "
                        + MIN_PLAYERS + ", max is " + MAX_PLAYERS)
                .build();

        Option helpOption = Option.builder(HELP_OPT)
                .argName(HELP_OPT_NAME)
                .longOpt(HELP_OPT_LONG)
                .desc("Print this message.")
                .build();

        options.addOption(suitsOption);
        options.addOption(ranksOption);
        options.addOption(playersOption);
        options.addOption(helpOption);

        CommandLineParser parser = new DefaultParser();

        Integer numSuits = DEFAULT_NUM_SUITS;
        int numRanks = DEFAULT_NUM_RANKS;
        int numPlayers = DEFAULT_NUM_PLAYERS;

        try {
            CommandLine cmd = parser.parse(options, args);

            if (cmd.hasOption(HELP_OPT)) {
                HelpFormatter formatter = new HelpFormatter();
                System.out.println("");
                formatter.printHelp(PROGRAM_CMD, options);
                System.out.println("");
                return;
            }

            if (cmd.hasOption(SUITS_OPT)) {
                numSuits = Integer.parseInt(cmd.getOptionValue(SUITS_OPT));

                if (numSuits < MIN_SUITS || numSuits > MAX_SUITS) {
                    displayParamError(SUITS_OPT_NAME, MIN_SUITS, MAX_SUITS);
                    return;
                }
            }
            if (cmd.hasOption(RANKS_OPT)) {
                numRanks = Integer.parseInt(cmd.getOptionValue(RANKS_OPT));

                if (numRanks < MIN_RANKS || numRanks > MAX_RANKS) {
                    displayParamError(RANKS_OPT_NAME, MIN_RANKS, MAX_RANKS);
                    return;
                }
            }
            if (cmd.hasOption(PLAYERS_OPT)) {
                numPlayers = Integer.parseInt(cmd.getOptionValue(PLAYERS_OPT));

                if (numPlayers < MIN_PLAYERS || numPlayers > MAX_PLAYERS) {
                    displayParamError(PLAYERS_OPT_NAME, MIN_PLAYERS, MAX_PLAYERS);
                    return;
                }
            }

        } catch (ParseException pe) {
            System.out.println("Error parsing options.  Use '-h' or '-help' for a description of the arguments.");
        } catch (NumberFormatException nfe) {
            System.out.println("Invalid integer argument found.");
            return;
        }

        final War war = new War();

        System.out.println("\n" + numPlayers + " players playing war with "
                + numSuits + " suits and " + numRanks + " ranks");

        try {
            war.play(numSuits, numRanks, numPlayers);

        } catch (InvalidParameterException ipe) {
            System.out.println("Invalid input detected: " + ipe.getMessage());
        }
    }

    /**
     * Display a message that an integer parameter to the program is out of bounds
     * @param name
     * @param min
     * @param max
     */
    private static void displayParamError(final String name, final Integer min, final Integer max) {
        System.out.println("");
        System.out.println(name + " parameter out of bounds. Minimum "
                + min + ", max " + max);
        System.out.println("");
    }
    /**
     * Play the game of war with the specified number of players, suits and ranks
     * @param numberOfSuits
     * @param numberOfRanks
     * @param numberOfPlayers
     */
    public void play(final int numberOfSuits, final int numberOfRanks, final int numberOfPlayers) {

        // each player should at least have 1 card to play the game - validate
        if ((numberOfSuits * numberOfRanks) < numberOfPlayers) {
            throw new InvalidParameterException("numberOfSuits time numberOfRanks must be great than numberOfPlayers");
        }

        // create a deck for the game
        WarDeck deck = new WarDeck();
        deck.create(numberOfSuits, numberOfRanks);
        deck.shuffle();

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

    }

    /**
     * Deal cards in circular fashion to all the players
     * @param players
     * @param deck
     */
    public void dealCards(final List<Player> players, final Deck deck) {
        Card dealCard = deck.deal();
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

            dealCard = deck.deal();
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
