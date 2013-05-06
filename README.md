The Pain Game
========
Welcome to my git repository for The Pain Game.

The what?
========
The Pain Game is a device I am currently developing. Basically, it is device that can deliver electric shocks to up to four people in a fun way. Sound disturbing at first, I know.

The project is composed of two main components: the software (which you are looking at) and the hardware (might be a git repository as well in the future).

Requirements
====
The Pain Game requires a [Raspberry Pi](http://www.raspberrypi.org) (use Debian "wheezy", Raspian won't work) with Oracle's JDK 7u10 running on it. It uses [pi4j](https://github.com/Pi4J/pi4j/) to communicate with the RasPi.
This will most likely not run on your PC! Clean-Build on your machine, then SCP the generated ZIP to your RasPi, unzip and execute it (sudo java -jar paingame.jar ...). Have fun.

Setting up Debian Wheezy on a Raspberry Pi: [here](http://dersteps.wordpress.com/2013/05/04/setting-up-a-raspberry-pi-with-debian-wheezy/)
Installing Oracle's JDK 7u10 on a Raspberry Pi: [here](http://dersteps.wordpress.com/2013/05/03/oracle-jdk-7u10-on-raspberry-pi/)


Can I...?
===
Of course. This code is public, do with it whatever you want. I'd love to hear about it, though...
