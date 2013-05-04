package de.zombielabs.paingame.games;

import de.zombielabs.paingame.Player;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map.Entry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Shocky is the most basic game there is for the Paingame.
 * 
 * The rules are extremely simple:
 * 
 *  During the game loop, the game will determine 0 to n players (n being the 
 *  maximum amount of players possible) and shock them. For each shock, a player
 *  is rewarded with a single point. The game ends after ten rounds. The player
 *  who was shocked the most, wins.
 * 
 * @author steps
 */
public class Shocky extends Game {
    
    /**
     * Log instance.
     */
    private static final Logger log = LogManager.getLogger(Shocky.class);
    
    /**
     * The amount of rounds to play, currently not configurable.
     */
    private int totalRounds = 10;
    
    /**
     * The probability of getting shocked.
     * The principle behind this is fairly simple: the higher this value gets,
     * the harder it is to get shocked.
     * The game will in each loop generate a random number between 0 and 
     * the probability to get shocked. A global random number is the magic number.
     * Should any player have the same random number as the global number, he or
     * she will get shocked. Easy as pi(e).
     */
    private int probability = 10;
    
    /**
     * The amount of time, in milliseconds, to pause between rounds.
     */
    private long pauseBetweenRounds = 5000;
    
    /**
     * Initializes a new instance of the Shocky class.
     * @param rounds The amount of rounds to play.
     * @param pause The amount of time to pause between rounds.
     * @param probability The probability to get shocked.
     */
    public Shocky(int rounds, long pause, int probability) {
        this.totalRounds = rounds;
        this.pauseBetweenRounds = pause;
        this.probability = probability;
        this.setGameName("Shocky");
    }
    
    /**
     * Sets up Shocky.
     * @param players The array of players playing the game.
     * @return Boolean.TRUE if setup succeeded, Boolean.FALSE otherwise and
     * null if an error was encountered.
     */
    @Override
    public Boolean setup(Player[] players) {
        this.setPlayers(players);
        
        for(final Player player : players) {
            log.info(player.getName() + " dares to play " + this.getGameName());
        }
        
        return Boolean.TRUE;
    }

    /**
     * Tears down shocky. There's actually not a lot going on here, simply all
     * the pins of the players will be reset.
     * @return Boolean.TRUE, always
     */
    @Override
    public Boolean teardown() {
        
        // Clear up all LEDs and pins
        for(Player player : this.getPlayers()) {
            player.getLEDPin().low();
            player.getShockPin().low();
        }
        return Boolean.TRUE;
    }

    /**
     * The main game loop.
     * 
     * Here's how the loop works:
     * 
     * In the beginning, a random number is generated. This is the magic number 
     * and is any random value between 0 and the probability to get shocked.
     * 
     * The game now loops n times, n being the amount of rounds to play. In each
     * round, a random number is generated for each player. Should that number
     * be the same as the magic number, the player will get shocked and rewarded
     * with one score point.
     * 
     * After all rounds have finished, the game will check whether or not anybody
     * got shocked at all. If not, everybody will get shocked, one after the other.
     * 
     * @return The score as a HashMap
     * @throws InterruptedException If the thread gets interrupted.
     */
    @Override
    public HashMap<Player, Integer> loop() throws InterruptedException {
        HashMap<Player, Integer> score = new HashMap<Player, Integer>();
        
        for(int i=0; i<this.getPlayers().length; i++) {
            score.put(this.getPlayers()[i], 0);
        }
        
        SecureRandom rand = new SecureRandom();
        final int shockingNumber = rand.nextInt(this.probability);
        log.info("Magic number is " + shockingNumber);
        
        boolean anybodyWasShocked = false;
        
        int round = 0;
        
        while(round++ < this.totalRounds) {
            
            this.raiseOnRoundStarted(round, this.totalRounds);
            
            HashMap<Player, Boolean> shockThem = new HashMap<Player, Boolean>();
            
            for(int i=0; i<this.getPlayers().length; i++) {
                shockThem.put(this.getPlayers()[i], Boolean.FALSE);
            }
            
            // Get a random number for each player
            for(int i=0; i<this.getPlayers().length; i++) {
                // Chance to get shocked is 1 in 10
                final int random = rand.nextInt(this.probability);
                if(random == shockingNumber) {
                    log.info("Player '" + this.getPlayers()[i] + "' hit the magic number");
                    shockThem.put(this.getPlayers()[i], Boolean.TRUE);
                    score.put(this.getPlayers()[i], score.get(this.getPlayers()[i]) + 1);
                    anybodyWasShocked = true;
                } else {
                    log.debug("Player '" + this.getPlayers()[i] + "' was lucky this time (" + random + ")");
                }
            }
            
            // Now shock everybody that hit the magic number
            for(Entry<Player, Boolean> entry : shockThem.entrySet()) {
                if(entry.getValue().equals(Boolean.TRUE)) {
                    log.info("Shocking '" + entry.getKey().getName() + "' for 300 ms...");
                    this.shockPlayer(entry.getKey(), 300, false);
                }
            }
            
            log.info("Next round will start in " + (this.pauseBetweenRounds/1000) + " seconds...");
            this.raiseOnRoundEnded(round, this.totalRounds);
            Thread.sleep(this.pauseBetweenRounds);
        }
        
        // If nobody was shocked, just be evil and shock everybody
        if(!anybodyWasShocked) {
            for(final Player player : this.getPlayers()) {
                // We use blocking calls here to avoid the game from ending too soon
                this.shockPlayer(player, 300, true);
            }
        }
        
        return score;
    }
}