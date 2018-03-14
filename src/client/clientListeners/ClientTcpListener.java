package client.clientListeners;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.locks.Lock;

public class ClientTcpListener implements Runnable {

    private BufferedReader is = null;
    private Boolean close = false;
    private DatagramSocket datagramSocket;
    private InetAddress address;
    private int port;
    private String userName = null;
    //private final Lock lock;


    public ClientTcpListener(BufferedReader is, DatagramSocket datagramSocket, InetAddress address, int port, Lock lock){
        this.is = is;
        this.datagramSocket = datagramSocket;
        this.address = address;
        this.port = port;
        //this.lock = lock;
    }

    public void endWork() {
        this.close = true;
    }

    public String getUserName() {
        return userName;
    }

    public void run() {

        String response;

        try{
            while(!close) {
                response = is.readLine();

                if (response != null) {
                    if(response.substring(0,7).equals("/config")){
                        userName = response.substring(7).trim();

                        byte[] registerBuff = ("/register" + userName).getBytes();
                        DatagramPacket registerUdpPacket = new DatagramPacket(registerBuff, registerBuff.length, address, port);
                        datagramSocket.send(registerUdpPacket);
                    }
                    else {
                        System.out.println(response);
                    }
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
