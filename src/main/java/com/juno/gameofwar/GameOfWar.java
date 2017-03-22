package com.juno.gameofwar;

import org.apache.commons.cli.*;

import java.security.InvalidParameterException;

/**
 * Holds the main entry point for the application
 * Also handles command line args and specific game policies
 */
public final class GameOfWar {

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

    /**
     * Private constructor
     */
    private GameOfWar() { }

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
}
