# TicTacToeLocal
## Description
This group project was created by Patrick Silva and Matteo Vitellaro in java as part of the Networks 2 cours in the Bachelor of applied information technology.
The project is a simple TicTacToe application, which is allows 2 players to play by communicating with a server.
It is set up to be run on one machine over localhost on port 5000, but can be easily modified to run on an actual server online.

## Communication
The communication works in a few simple steps.
Firstly, one player sends the integer position of the clicked tile to the server.
The server then sends that integer to the second player, who can draw a circle or an X where the position corresponds to.
The second player can then register an input themselves and send it to the server.
The server uses a ServerSideConnection inner class, which uses a socket, as well as DataInput and output streams to send packets between players.
Each player uses a ClientSideConnection inner class to accomplish the same.
Buttons are disabled when it is not the players turn, which is accomplished by correctly disabling and enabling the mousePressed() method that checks the input.

## How to run the game
Firstly, the server has to be started, which waits for both players to connect. This can be done by running the main method of the Server class.
The players can be launched by running their respective main classes. Player 1, or the first to connect, has always the first move. 
Please only start playing the game as soon as both players are connected. Additional information is displayed in the terminal to let the player know which buttons are pressed.
