WAR! (the card game)
===============

This is a Java Gradle project that simulates the card game War with up to 600 players using up to 1,000,000 cards.

Build and Test
--------------
<code>./gradlew clean build</code>

This will build the code and run all unit tests and output pass or fail for each test. A successful build will generate
a jar file at <code>./build/lib/war-1.0.jar</code> that can then be executed via the command line to simulate a game of War.

Run
--------------
To execute using the default number of players and cards, from the command line:

<code>java -jar build/libs/war-1.0.jar</code>

To see the simulation run with a specified number of players and cards, you can use command line arguments. See the help for details:

<code>java -jar build/libs/war-1.0.jar -help</code>