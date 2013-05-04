/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.zombielabs.paingame;

//import com.pi4j.io.gpio.GpioPinDigitalOutput;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;


/**
 * The Player class is used to wrap all operations to identify a player in a 
 * convenient class.
 * @author steps
 */
public class Player {
    /**
     * The name of the player. Usually "player 1" or similar.
     */
    private String name;
    
    /**
     * The output pin to toggle in order to shock the player.
     */
    private GpioPinDigitalOutput shockPin;
    
    /**
     * The output pin that is connected to an LED indicating the player.
     */
    private GpioPinDigitalOutput ledPin;
    
    /**
     * The input pin that is listening to the player's buzzer.
     */
    private GpioPinDigitalInput buzzerPin;

    /**
     * Gets the name of the player.
     * @return A String containing the player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the player.
     * @param name The new name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the pin that needs to be toggled in order to shock this player.
     * @return The GpioPinDigitalOutput to toggle in order to shock this player
     */
    public GpioPinDigitalOutput getShockPin() {
        return shockPin;
    }

    /**
     * Sets the pin to toggle in order to shock this player.
     * @param pin The GpioPinDigitalOutput to associate with the player.
     */
    public void setShockPin(GpioPinDigitalOutput pin) {
        this.shockPin = pin;
    }

    /**
     * Gets the pin that is connected to the LED that is the player
     * @return A GpioPinDigitalOutput that is connected to an LED indicating the player
     */
    public GpioPinDigitalOutput getLEDPin() {
        return ledPin;
    }

    /**
     * Sets the pin that is connected to the LED that is the player
     * @param playerPin The GpioPinDigitalOutput that is connected to the LED that is the player
     */
    public void setLEDPin(GpioPinDigitalOutput playerPin) {
        this.ledPin = playerPin;
    }

    /**
     * Gets the input pin that is the player's buzzer.
     * @return The GpioPinDigitalInput that is the player's buzzer pin.
     */
    public GpioPinDigitalInput getBuzzerPin() {
        return buzzerPin;
    }

    /**
     * Sets the player's buzzer pin
     * @param buzzerPin The GpioPinDigitalInput that is the new player's buzzer pin.
     */
    public void setBuzzerPin(GpioPinDigitalInput buzzerPin) {
        this.buzzerPin = buzzerPin;
    }
    

    /**
     * Initializes a new instance of the Player class.
     * @param name The player's name
     * @param shockPin The pin to toggle in order to shock the player
     * @param ledPin The pin that is the player's LED
     * @throws IllegalArgumentException if either name or any pin is null or empty
     */
    public Player(String name, GpioPinDigitalOutput shockPin, GpioPinDigitalOutput ledPin, GpioPinDigitalInput buzzerPin) {
        if(name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Parameter 'name' must neither be "
                    + "null, nor empty");
        }
        
        if(shockPin == null) {
            throw new IllegalArgumentException("Parameter 'shockPin' must not be null");
        }
        
        if(ledPin == null) {
            throw new IllegalArgumentException("Parameter 'ledPin' must not be null");
        }
        
        if(buzzerPin == null) {
            throw new IllegalArgumentException("Parameter 'buzzerPin' must not be null");
        }
        
        this.name = name;
        this.shockPin = shockPin;
        this.ledPin = ledPin;
        this.buzzerPin = buzzerPin;
    }
    
    /**
     * Resets this player's output pins, i.e. sets them to LOW.
     */
    public void resetOutput() {
        this.ledPin.low();
        this.shockPin.low();
    }
    
    /**
     * Gets a String that represents this Player.
     * @return The player's name.
     */
    @Override
    public String toString() {
        return this.name;
    }
    
}
