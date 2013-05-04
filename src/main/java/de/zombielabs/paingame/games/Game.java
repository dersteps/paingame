/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zombielabs.paingame.games;

import de.zombielabs.paingame.Paingame;
import de.zombielabs.paingame.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * The abstract Game class is the base class for all game types.
 * @author steps
 */
public abstract class Game implements Runnable {
    
    private static final Logger log = LogManager.getLogger(Game.class);
    
    /**
     * Each game has a name.
     */
    private String name;
    
    /**
     * The Players that play the game.
     */
    private Player[] players;
    
    /**
     * All GameListener instances that listen in on this Game.
     */
    private List<GameListener> listeners = new ArrayList<GameListener>();
    
    /**
     * Adds a new GameListener to this Game.
     * @param listener The listener to add
     */
    public void addListener(GameListener listener) {
        if(this.listeners == null) {
            this.listeners = new ArrayList<GameListener>();
        }
        
        this.listeners.add(listener);
    }

    /**
     * Raises the onGameEnded event on all listening instances.
     * @param score The final score after the game has ended.
     */
    protected void raiseOnGameEnded(HashMap<Player, Integer> score) {
        for(GameListener listener : this.listeners) {
            listener.onGameEnded(this, score);
        }
    }
    
    /**
     * Raises the onRoundStarted event on all listening instances.
     * @param current The round that is starting
     * @param total The total amount of rounds that will be played
     */
    protected void raiseOnRoundStarted(int current, int total) {
        for(final GameListener listener : this.listeners) {
            listener.onRoundStarted(this, current, total);
        }
    }
    
    /**
     * Raises the onRoundEnded event on all listening instances.
     * @param current The round that ended
     * @param total The total amount of rounds that will be played.
     */
    protected void raiseOnRoundEnded(int current, int total) {
        for(final GameListener listener : this.listeners) {
            listener.onRoundEnded(this, current, total);
        }
    }
    
    /**
     * Gets this Game's name.
     * @return A String containing the Game's name.
     */
    public String getGameName() {
        return name;
    }

    /**
     * Sets this Game's name. Is protected, so only classes in the same
     * package or extending classes can actually do this.
     * @param name The new name of this Game
     */
    protected void setGameName(String name) {
        this.name = name;
    }

    /**
     * Gets the players that play this game.
     * @return An array of all Players playing the game.
     */
    public Player[] getPlayers() {
        return players;
    }
    
    /**
     * Sets this Game's players.
     * @param players The players playing the game.
     */
    protected void setPlayers(Player[] players) {
        this.players = players;
    }
    
    /**
     * This is the first method that is called when a game is about to be played.
     * A Game has to setup all it needs to work during this method.
     * @param players The array of all Players that play the game. Will contain 
     * 1 to 4 entries.
     * @return A Boolean value that indicates the success of the setup process.
     * A Game should return null if errors have been encountered. TRUE should be
     * returned if the Game has been properly set up, FALSE otherwise.
     * The Game will only be started if the setup method returned TRUE.
     */
    public abstract Boolean setup(Player[] players);
    
    /**
     * This is the method that will be called right after a game has finished.
     * All games must make sure that they properly release all resources they 
     * might have loaded when this method is called.
     * @return A Boolean value that indicates the success of the teardown process.
     * A Game should return null if errors have been encountered. TRUE should be
     * returned if the Game has been properly torn down, FALSE otherwise.
     */
    public abstract Boolean teardown();
    
    /**
     * This is the main method of every game. An implementing class must put all
     * code that makes up the game itself inside this method (it can, of course,
     * call others). The method will not only execute the game, but must also
     * determine the winner (0 to 4 Players).
     * @return A Hashmap that maps players to their rank. 
     */
    public abstract HashMap<Player, Integer> loop() throws InterruptedException;
    
    /**
     * Starts the game asynchronously.
     */
    @Override
    public void run() {
        try {
            final HashMap<Player, Integer> score = this.loop();
            this.raiseOnGameEnded(score);
            
        } catch (InterruptedException ex) {
            log.error("Error while in game loop: " + ex.getMessage(), ex);
        }
    }
    
    /**
     * Simple helper method that shocks a player while switching on his/her LED.
     * @param player The player to shock
     * @param duration The amount of time to shock the player
     * @param blocking If true, calls are blocking, should be false most of the time
     */
    protected void shockPlayer(Player player, int duration, boolean blocking) {
        log.info("Shocking '" + player.getName() + "' for " + duration + " milliseconds");
        if(!Paingame.DEVELOPMENT) {
            player.getLEDPin().pulse(duration, blocking);
            player.getShockPin().pulse(duration, blocking);
        } else {
            log.warn("In development mode, no actual pin triggering");
        }
    }
}
