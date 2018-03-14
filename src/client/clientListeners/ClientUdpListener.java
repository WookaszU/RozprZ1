package client.clientListeners;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

public class ClientUdpListener extends UdpListener implements Runnable {

    private DatagramSocket datagramSocket;
    private Boolean close = false;


    private String pathToSave = ".\\Photos\\UDP\\";

    public ClientUdpListener(DatagramSocket datagramSocket){
        this.datagramSocket = datagramSocket;
    }

    public void endWork() {
        this.close = true;
    }



    public void run() {


        try{

            while(!close) {
                byte[] receiveBuffer = new byte[1500];
                DatagramPacket receivePacket =
                        new DatagramPacket(receiveBuffer, receiveBuffer.length);
                datagramSocket.receive(receivePacket);

                receiveBuffer = receivePacket.getData();
                String msg = new String(receiveBuffer);

                byte[] imageBuffer = new byte[1475];

                int senderNameLen = stringToInt(msg.substring(0,1));

                String senderName = msg.substring(1, senderNameLen + 1);
                System.out.print("Image from " + senderName);
                imageBuffer = Arrays.copyOfRange(receiveBuffer,1 + senderNameLen, receiveBuffer.length);
                decodeImage(imageBuffer, pathToSave);

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
