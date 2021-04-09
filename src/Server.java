
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final static int SERVER_PORT = 5000;

    public static void main(String args[]){
    	ServerSocket serverSocket = null;
        List<ObjectOutputStream> listPlayers = new ArrayList<ObjectOutputStream>();
        List<ObjectInputStream> listPlayers_in = new ArrayList<ObjectInputStream>();
        try {
            serverSocket = new ServerSocket();

            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            serverSocket.bind( new InetSocketAddress(hostAddress, SERVER_PORT) );
            System.out.println("Waiting- " + hostAddress + ":" + SERVER_PORT);

            while(true) {
                Socket socket = serverSocket.accept();
                new ServerProcessThread(socket, listPlayers, listPlayers_in).start();
                if(listPlayers.size()==1) {
                	System.out.println("move to game");
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                if( serverSocket != null && !serverSocket.isClosed() ) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}