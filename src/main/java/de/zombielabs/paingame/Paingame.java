package de.zombielabs.paingame;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import de.zombielabs.paingame.games.Game;
import de.zombielabs.paingame.games.GameListener;
import de.zombielabs.paingame.games.GameMode;
import de.zombielabs.paingame.games.Shocky;
import gnu.getopt.Getopt;
import gnu.getopt.LongOpt;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * The main class of the Paingame.
 * Controls the whole game flow.
 * @author steps
 */
public class Paingame {
    
    /**
     * If this is true, no actual pin interaction will happen.
     */
    public static final boolean DEVELOPMENT = true;
    
    /**
     * The log.
     */
    private static final Logger log = LogManager.getLogger(Paingame.class);
    
    /**
     * The mode of the Paingame. Is controlled by passing command line arguments.
     */
    private static GameMode mode;
    
    /**
     * The game modes available. 
     */
    private static final HashMap<GameMode, Game> availableGames = new HashMap<GameMode, Game>() {{
        put(GameMode.SHOCKY, new Shocky(10, 2000, 10));
    }};
    
    /**
     * The name of the first Player.
     */
    private static String namePlayer1 = "Player 1";
    
    /**
     * The name of the second Player.
     */
    private static String namePlayer2 = "Player 2";
    
    /**
     * The name of the third Player.
     */
    private static String namePlayer3 = "Player 3";
    
    /**
     * The name of the fourth Player.
     */
    private static String namePlayer4 = "Player 4";
    
    /**
     * Parses the command line arguments.
     * @param args 
     */
    private static void parseCommandLine(String[] args) {
        
        final LongOpt[] options = new LongOpt[] {
            new LongOpt("game", LongOpt.REQUIRED_ARGUMENT, null, 'g'),
            new LongOpt("player1", LongOpt.OPTIONAL_ARGUMENT, null, 'a'),
            new LongOpt("player2", LongOpt.OPTIONAL_ARGUMENT, null, 'b'),
            new LongOpt("player3", LongOpt.OPTIONAL_ARGUMENT, null, 'c'),
            new LongOpt("player4", LongOpt.OPTIONAL_ARGUMENT, null, 'd')
        };
        
        Getopt g = new Getopt("paingame", args, "gabcd:", options);
        g.setOpterr(true);
        
        int c = -1;
        
        while((c = g.getopt()) != -1) {
            switch(c) {
                case 'g': {
                    final String gameMode = g.getOptarg();
                    mode = GameMode.valueOf(gameMode.toUpperCase());
                    log.info("Game mode set: " + mode);
                    break;
                } case 'a': {
                    namePlayer1 = g.getOptarg();
                    break;
                } case 'b': {
                    namePlayer2 = g.getOptarg();
                    break;
                } case 'c': {
                    namePlayer3 = g.getOptarg();
                    break;
                } case 'd': {
                    namePlayer4 = g.getOptarg();
                    break;
                } default: {
                    log.warn("Unrecognized command line argument: " + g.getOptarg());
                    break;
                }
            }
        }
    }
    
    private static List<Player> initPlayers() {
        // Get a GPIO controller
        final GpioController gpio = GpioFactory.getInstance();
        
        // Setup the shock pins for the players
        final GpioPinDigitalOutput p1_shockPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_01, PinState.LOW);
        final GpioPinDigitalOutput p2_shockPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_02, PinState.LOW);
        final GpioPinDigitalOutput p3_shockPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_03, PinState.LOW);
        final GpioPinDigitalOutput p4_shockPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, PinState.LOW);
        
        // Setup the status pins for the players
        final GpioPinDigitalOutput p1_ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05, PinState.LOW);
        final GpioPinDigitalOutput p2_ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_06, PinState.LOW);
        final GpioPinDigitalOutput p3_ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_07, PinState.LOW);
        final GpioPinDigitalOutput p4_ledPin = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_08, PinState.LOW);
        
        // Setup the buzzer pins for the players
        final GpioPinDigitalInput p1_buzzerPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_09, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput p2_buzzerPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_10, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput p3_buzzerPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_11, PinPullResistance.PULL_DOWN);
        final GpioPinDigitalInput p4_buzzerPin = gpio.provisionDigitalInputPin(RaspiPin.GPIO_12, PinPullResistance.PULL_DOWN);
        
        // Make sure everything is handled correctly on shutdown
        p1_shockPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p2_shockPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p3_shockPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p4_shockPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p1_ledPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p2_ledPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p3_ledPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        p4_ledPin.setShutdownOptions(true, PinState.LOW, PinPullResistance.OFF);
        
        // Initialize players
        final Player player1 = new Player(namePlayer1, p1_shockPin, p1_ledPin, p1_buzzerPin);
        final Player player2 = new Player(namePlayer2, p2_shockPin, p2_ledPin, p2_buzzerPin);
        final Player player3 = new Player(namePlayer3, p3_shockPin, p3_ledPin, p3_buzzerPin);
        final Player player4 = new Player(namePlayer4, p4_shockPin, p4_ledPin, p4_buzzerPin);
        
        return new ArrayList<Player>() {{
            add(player1);
            add(player2);
            add(player3);
            add(player4);
        }};
    }
    
    /**
     * The Paingame's main entry point.
     * @param args Command line arguments
     * @throws InterruptedException If the game was interrupted
     */
    public static void main(String[] args) throws InterruptedException {
        // First of all, load the logging configuration from the resources
        InputStream in = Class.class.getResourceAsStream("/de/zombielabs/paingame/config/log4j.properties");
        PropertyConfigurator.configure(in);
        
        log.info("Paingame is up and running");
        
        // Parse command line arguments
        parseCommandLine(args);
        
        GameController controller = new GameController(initPlayers());
        
        // Find game
        Game game = availableGames.get(GameMode.SHOCKY);
        
        if(availableGames.containsKey(mode)) {
            log.info("Loading game '" + mode + "'...");
            game = availableGames.get(mode);
        }

        controller.play(game);
    }
}
