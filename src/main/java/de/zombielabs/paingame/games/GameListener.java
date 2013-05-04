/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zombielabs.paingame.games;

import de.zombielabs.paingame.Player;
import java.util.HashMap;

/**
 * The GameListener interfaces allows other instances to listen to changes in a
 * game's state.
 * @author steps
 */
public interface GameListener {
    /**
     * Called when a game ends.
     * @param game The Game that has ended.
     * @param score The final score
     */
    void onGameEnded(Game game, HashMap<Player, Integer> score);
    
    /**
     * Called whenever a new round of a Game starts.
     * @param game The Game that raised the event
     * @param current The current round's number
     * @param total The total amount of rounds that will be played
     */
    void onRoundStarted(Game game, int current, int total);
    
    /**
     * Called whenever a round of a Game ends.
     * @param game The Game that raised the event
     * @param current The current round's number
     * @param total The total amount of rounds that will be played.
     */
    void onRoundEnded(Game game, int current, int total);
}
