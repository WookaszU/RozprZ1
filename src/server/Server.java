package server;

import server.clientConnectors.ClientData;
import server.clientConnectors.ClientTcpConnect;
import server.clientConnectors.ClientUdpConnect;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;


public class Server {


    private static ServerSocket serverSocket = null;
    private static DatagramSocket datagramSocket = null;

    private static ExecutorService pool = Executors.newFixedThreadPool(100);
    private static ConcurrentLinkedQueue<ClientData> clients = new ConcurrentLinkedQueue<>();
    private static int maxClientNumber = 1;

    public static void main(String args[]){
        System.out.println("JAVA TCP SERVER");
        int portNumber = 12345;


        try {
            serverSocket = new ServerSocket(portNumber);
            datagramSocket = new DatagramSocket(12345);
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(serverSocket != null && datagramSocket != null) {
            while (true) {

                try {
                    Socket clientSocket = serverSocket.accept();
                    if(ConcurrentCounter.counter < maxClientNumber) {

                        pool.submit(new ClientTcpConnect(clients, clientSocket));
                        pool.submit(new ClientUdpConnect(clients, datagramSocket));

                        ConcurrentCounter.incrementCounter();
                    }

                    else {
                        PrintStream os = new PrintStream(clientSocket.getOutputStream());
                        os.println("Server too busy. Try later.");
                        os.close();
                        clientSocket.close();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        datagramSocket.close();
        pool.shutdown();
    }

}

