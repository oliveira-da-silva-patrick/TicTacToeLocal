package tictactoe;
import java.net.*;
import java.io.*;

public class Server {
    private ServerSocket serverSocket;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private int turnsMade;
    private int maxTurns;
    private boolean isWon;
    private int [][] values;
    private int player1ButtonNum;
    private int player2ButtonNum;

    public Server(){
        System.out.println("----Game Server----");
        numPlayers = 0;
        turnsMade = 0;
        maxTurns = 9;
        isWon = false;
        values = new int[3][3];
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                values[i][j] = 0;
            }
        }
        try{
            serverSocket = new ServerSocket(5000);
        } catch (IOException ex){
            //System.out.println("IOException from Gameserver Constructor");
        }
    }

    /**
     * waits for both players to be connected and sets the connections to the players
     * player numbers are also set correctly
     */
    public void acceptConnections(){
        try{
            System.out.println("Waiting for connections...");
            while(numPlayers < 2){
                Socket s = serverSocket.accept();
                numPlayers++;
                System.out.println("Player #" + numPlayers + " has connected.");
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                if(numPlayers == 1){
                    player1 = ssc;
                } else{
                    player2 = ssc;
                }
                Thread t = new Thread(ssc);
                t.start();
            }
            System.out.println("No longer accepting connections.");
        } catch (IOException ex){
            //System.out.println("IOException from acceptConnections");
        }
    }

    /**
     * Inner class to allow server-client communication
     */
    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        public ServerSideConnection(Socket s, int id){
            socket = s;
            playerID = id;
            try{
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch(IOException ex){
                //System.out.println("IOException from SSC constructor");
            }
        }

        /**
         * runs the game by getting the sent button, and sending it to the correct player, to keep track of turns
         * also checks for wins and draws (by checking the turnsMade)
         */
        public void run(){
            try{
                dataOut.writeInt(playerID);
                dataOut.flush();
                while(turnsMade != maxTurns || !isWon){
                    if(playerID == 1){
                        player1ButtonNum = dataIn.readInt();
                        if(player1ButtonNum == -10) {
                            isWon = true;
                            continue;
                        }
                        System.out.println("Player 1 clicked Button #" + player1ButtonNum);
                        player2.sendButtonNum(player1ButtonNum);
                    } else{
                        player2ButtonNum = dataIn.readInt();
                        if(player1ButtonNum == -10) {
                            isWon = true;
                            continue;
                        }
                        System.out.println("Player 2 clicked Button #" + player2ButtonNum);
                        player1.sendButtonNum(player2ButtonNum);
                    }
                    turnsMade++;
                }
            } catch (IOException ex){
                //System.out.println("IOException from run() SSC");
            }
            System.out.println("Player #"+ playerID+ " finished the game.");
        }

        public void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            } catch(IOException ex){
                //System.out.println("IOException from sendButtonNum() SSC");
            }
        }
    }

    public static void main(String[] args) {
        Server gs = new Server();
        gs.acceptConnections();
    }

}
