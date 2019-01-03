#!/bin/sh

set -e

javac src/MyBot.java src/hlt/*.java
./halite --replay-directory replays/ -vvv --width 32 --height 32 "java -classpath bin MyBot" "java -classpath 41/src MyBot"
