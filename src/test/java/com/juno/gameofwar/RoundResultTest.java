package com.juno.gameofwar;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by Joe Teibel.
 */
public class RoundResultTest {


    /**
     * If we have a winner in a round, the tiedPlayers list should be null
     */
    @Test
    public void testRoundResultWinnerPolicy(){
        RoundResult rr = new RoundResult(3, new Player(0));
        assertEquals(rr.getTiedPlayers(), null);
    }


    /**
     * If we have tied players in a round, the tiedPlayers list should be null
     * Validate the isTie helper as well
     */
    @Test
    public void testRoundResultTiedPlayersPolicy(){
        List<Player> players = new ArrayList<>(2);
        Player p1 = new Player(1);
        Player p2 = new Player(2);
        players.add(p1);
        players.add(p2);

        RoundResult rr = new RoundResult(3, players);
        assertEquals(rr.getWinner(), null);
        assertTrue(rr.isTie());
    }
}
