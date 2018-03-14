package client.clientListeners;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Arrays;


public class ClientMulticastListener extends UdpListener implements Runnable {

    private MulticastSocket multicastSocket;
    private String userName;
    private Boolean close = false;


    private String pathToSave = ".\\Photos\\Multicast\\";


    public ClientMulticastListener(MulticastSocket multicastSocket) {
        this.multicastSocket = multicastSocket;
    }

    public void endWork() {
        this.close = true;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void run(){


        try {
            while(!close){


                byte[] receiveBuffer = new byte[1500];
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);
                multicastSocket.receive(receivePacket);

                receiveBuffer = receivePacket.getData();
                String msg = new String(receiveBuffer);

                byte[] imageBuffer = new byte[1475];

                int senderNameLen = stringToInt(msg.substring(0,1));

                String senderName = msg.substring(1, senderNameLen + 1);
                if(!senderName.equals(userName)) {
                    System.out.print("Image from " + senderName);
                    imageBuffer = Arrays.copyOfRange(receiveBuffer, 1 + senderNameLen, receiveBuffer.length);
                    decodeImage(imageBuffer, pathToSave);
                }

            }
        }
        catch(SocketException e){
            //System.out.println("Connection lost!");
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }
}
