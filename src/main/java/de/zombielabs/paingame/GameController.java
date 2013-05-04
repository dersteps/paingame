/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zombielabs.paingame;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import static de.zombielabs.paingame.Paingame.DEVELOPMENT;
import de.zombielabs.paingame.games.Game;
import de.zombielabs.paingame.games.GameListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The GameController class is used to conveniently control the whole game flow.
 * @author steps
 */
public class GameController implements GameListener {
    /**
     * The log.
     */
    private static final Logger log = LogManager.getLogger(GameController.class);
    
    /**
     * The list of all Players.
     */
    private List<Player> players;
    
    /**
     * The Game that is currently controlled.
     */
    private Game game;
    
    /**
     * The GpioController to actually trigger pins etc.
     */
    private final GpioController gpio = GpioFactory.getInstance();
    
    /**
     * The amount of time to switch on the winner's LED.
     */
    private final int WIN_LED_TIME = 2000;
    
    /**
     * Initializes a new instance of the GameController class.
     * @param players 
     */
    public GameController(List<Player> players) {
        this.players = players;
    }
    
    /**
     * Hands control over to this controller.
     * @param game The game to play.
     */
    public void play(Game game) {
        this.game = game;
        this.game.setup(players.toArray(new Player[0]));
        this.game.addListener(this);
        
        Thread gameThread = new Thread(this.game, "game");
        gameThread.start();
    }

    /**
     * Is called by the currently played game once it ends.
     * Will determine the winner(s) and switch on the LEDs accordingly. After
     * that, will pause the thread for a given amount of time, so the LEDs can
     * return to their original state.
     * @param game The game that has ended
     * @param score The final score
     */
    @Override
    public void onGameEnded(Game game, HashMap<Player, Integer> score) {
        log.info(game.getGameName() + " has ended");
        final List<Player> winners = this.findWinners(score);
        for(final Player winner : winners) {
            log.info("Winner: " + winner);
            if(!DEVELOPMENT) {
                log.error("Switching on LED of " + winner.getName());
                winner.getLEDPin().pulse(WIN_LED_TIME, false);
            } else {
                log.warn("Development mode, no actual pin triggering");
            }
        }
        
        // Delay the game for WIN_LED_TIME
        try {
            Thread.sleep(WIN_LED_TIME);
        } catch (InterruptedException ex) {
            log.error("Unable to delay, interrupted!");
        }
        
        
        // Reset all LEDs and other pins
        for(final Player player : this.players) {
            log.error("Switching off LEDs etc for " + player.getName());
            player.resetOutput();
        }
    }

    /**
     * Is called when the currently played game enters a new round.
     * @param game The game that raised the event
     * @param current The no. of the round currently starting
     * @param total The total amount of rounds to play.
     */
    @Override
    public void onRoundStarted(Game game, int current, int total) {
        log.info("Round " + current + " of " + total + " of " + game.getGameName() + " is about to start");
        
        // Reset all LEDs and other pins
        for(final Player player : this.players) {
            player.resetOutput();
        }
    }

    /**
     * Is called when the currently played game has finished a round.
     * @param game The game that raised the event
     * @param current The no. of the round that ended
     * @param total The total amount of rounds to play.
     */
    @Override
    public void onRoundEnded(Game game, int current, int total) {
        log.info(current + " of " + total + " rounds of " + game.getGameName() + " have been played");
    }
    
    /**
     * Simple finder method.
     * Iterates twice over the map of scores. First to find highest score of all
     * and second to find everybody with that score. Those are returned
     * @param scoreMap The map that is the current score
     * @return Only the players that have the most points.
     */
    private List<Player> findWinners(HashMap<Player, Integer> scoreMap) {
        List<Player> winners = new ArrayList<Player>();
        int max = Integer.MIN_VALUE;
        
        for(Map.Entry<Player, Integer> entry : scoreMap.entrySet()) {
            max = Math.max(max, entry.getValue());
        }
        
        for(Map.Entry<Player, Integer> entry : scoreMap.entrySet()) {
            if(entry.getValue() == max) {
                winners.add(entry.getKey());
            }
        }
        
        return winners;
    }
}
