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
            System.out.println("IOException from Gameserver Constructor");
        }
    }

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
            System.out.println("IOException from acceptConnections");
        }
    }

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
                System.out.println("IOException from SSC constructor");
            }
        }

        public void run(){
            try{
                dataOut.writeInt(playerID);
                dataOut.flush();
                while(true){
                    	if(playerID == 1){
                            player1ButtonNum = dataIn.readInt();
                            System.out.println("Player 1 clicked Button #" + player1ButtonNum);
                            player2.sendButtonNum(player1ButtonNum);
                        } else{
                            player2ButtonNum = dataIn.readInt();
                            System.out.println("Player 2 clicked Button #" + player2ButtonNum);
                            player1.sendButtonNum(player2ButtonNum);
                        }
                }
            } catch (IOException ex){
                System.out.println("IOException from run() SSC");
            }
        }

        public void sendButtonNum(int n){
            try{
                dataOut.writeInt(n);
                dataOut.flush();
            } catch(IOException ex){
                System.out.println("IOException from sendButtonNum() SSC");
            }
        }
    }



    public static void main(String[] args) {
        Server gs = new Server();
        gs.acceptConnections();
    }

}
