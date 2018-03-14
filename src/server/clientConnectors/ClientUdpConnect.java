package server.clientConnectors;

import java.lang.reflect.Array;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ConcurrentLinkedQueue;


public class ClientUdpConnect implements Runnable {

    private ConcurrentLinkedQueue<ClientData> clients;

    private DatagramSocket socket;


    public ClientUdpConnect(ConcurrentLinkedQueue<ClientData> clients, DatagramSocket datagramSocket){
        this.socket = datagramSocket;
        this.clients = clients;
    }



    private void registerClient(DatagramPacket receivePacket, String clientName){

        InetAddress inetAddress = receivePacket.getAddress();
        Integer port = receivePacket.getPort();

        for(ClientData clientData: clients){
            if(clientData.getClientName().equals(clientName)) {
                clientData.addUdpConnect(inetAddress, port);
            }
        }
    }

    private String searchClient(DatagramPacket receivePacket){
        InetAddress inetAddress = receivePacket.getAddress();
        Integer port = receivePacket.getPort();
        for(ClientData clientData: clients){
            if(clientData.getInetAddress().equals(inetAddress) && clientData.getPort().equals(port)) {
                return clientData.getClientName();
            }
        }
        return null;
    }


    private byte[] concatenate(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;

        byte[] c = (byte[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }

    public void run(){
        try {


            while(true) {

                byte[] receiveBuffer = new byte[1500];
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                receiveBuffer = receivePacket.getData();

                String msg = new String(receiveBuffer);
                if(msg.substring(0,9).equals("/register")) {
                    String clientName = msg.substring(9).trim();
                    registerClient(receivePacket, clientName);
                }
                else {
                    byte[] nameBuffer = new byte[25];
                    DatagramPacket sendPacket;
                    DatagramPacket sendName;
                    String senderName = searchClient(receivePacket);

                    Integer nameLength = senderName.length();
                    String name = nameLength.toString() + senderName;
                    nameBuffer = name.getBytes();
                    byte[] sendBuffer = concatenate(nameBuffer, receiveBuffer);

                    for (ClientData clientData : clients) {
                        if (!clientData.getClientName().equals(senderName)) {
                            InetAddress clientAddress = clientData.getInetAddress();
                            int clientPort = clientData.getPort();
                            sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, clientAddress, clientPort);
                            socket.send(sendPacket);
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }
}
