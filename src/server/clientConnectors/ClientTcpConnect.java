package server.clientConnectors;

import server.ConcurrentCounter;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ClientTcpConnect implements Runnable {

    private ConcurrentLinkedQueue<ClientData> clients;
    private Socket clientSocket;
    private String clientName;
    private BufferedReader is;
    private PrintStream os;

    private ClientData currentClient;


    public ClientTcpConnect(ConcurrentLinkedQueue<ClientData> clients, Socket clientSocket){
        this.clients = clients;
        this.clientSocket = clientSocket;

    }


    private boolean checkUsername(String name){

        for(ClientData clientData: clients){
            if(clientData.getClientName().equals(name))
                return false;
        }

        return true;
    }


    public void run(){

        try {
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            os = new PrintStream(clientSocket.getOutputStream());

            String name;

            do{
                os.println("Enter your name.");
                name = is.readLine().trim();
            }while(!checkUsername(name));
            clientName = name;
            currentClient = new ClientData(clientName, clientSocket, is, os);
            clients.add(currentClient);


            os.println("/config" + clientName);
            os.println("Dolaczono do chatu!");


            try {
                while(true){
                    String line = is.readLine();

                    if(line.equals("/q")) {
                        System.out.println("trtrtrtrtr");
                        this.os.println("/unregistered");
                        ConcurrentCounter.decrementCounter();
                        break;
                    }

                    Iterator<ClientData> it = clients.iterator();

                    while(it.hasNext()){
                        ClientData clientData = it.next();
                        if(!this.clientName.equals(clientData.getClientName()))
                            clientData.getOs().println(clientName + " : " + line);
                    }

                }
            } catch (SocketException e) {

            }
            finally {
                this.is.close();
                this.os.close();
                clients.remove(currentClient);
            }


        }
        catch(SocketException e){
            e.printStackTrace();
            System.out.print("socket exception");
        }
        catch (IOException e) {
            e.printStackTrace();
        }



    }

}
