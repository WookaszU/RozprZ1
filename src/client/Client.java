package client;

import client.clientListeners.ClientMulticastListener;
import client.clientListeners.ClientTcpListener;
import client.clientListeners.ClientUdpListener;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class Client {


    // The client socket
    private static Socket clientSocket = null;
    // The input stream
    private static BufferedReader is = null;
    // The output stream
    private static PrintStream os = null;
    private static ExecutorService pool;
    private static DatagramSocket datagramSocket = null;

    private static boolean close = false;

    public final static Lock lock = new ReentrantLock();

    private static byte[] concatenate(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;

        byte[] c = (byte[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);

        return c;
    }


    public static void main(String[] args) throws IOException {

        System.out.println("JAVA CLIENT");
        String hostName = "localhost";
        int portNumber = 12345;


        pool = Executors.newFixedThreadPool(3);

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));


        InetAddress group = InetAddress.getByName("228.5.6.7");
        int multicastPort = 5555;

        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);
        multicastSocket.joinGroup(group);


        try {

            InetAddress address = InetAddress.getByName("localhost");


            clientSocket = new Socket(hostName, portNumber);
            os = new PrintStream(clientSocket.getOutputStream());
            is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            datagramSocket = new DatagramSocket(clientSocket.getLocalPort());

            ClientTcpListener readerTcp = new ClientTcpListener(is, datagramSocket, address, portNumber, lock);
            ClientUdpListener readerUdp = new ClientUdpListener(datagramSocket);
            ClientMulticastListener readerMulticast = new ClientMulticastListener(multicastSocket);

            pool.submit(readerTcp);
            pool.submit(readerUdp);
            pool.submit(readerMulticast);


            String line;
            String option;


            while (!close) {
                line = input.readLine().trim();

                if(line.length() > 1)
                    option = line.substring(0,2);
                else
                    option = "default";

                if(option.equals("/q")){
                    close = true;
                    os.println("/q");
                }

                else if(option.equals("/U") || option.equals("/u")){

                    BufferedImage img = ImageIO.read(new File("src/test.jpg"));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", baos);
                    baos.flush();
                    byte[] buffer = baos.toByteArray();
                    baos.close();

                    DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, address, portNumber);

                    datagramSocket.send(sendPacket);

                }

                else if(option.equals("/M") || option.equals("/m")){


                    BufferedImage img = ImageIO.read(new File("src/test.jpg"));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "jpg", baos);
                    baos.flush();
                    byte[] buffer = baos.toByteArray();
                    baos.close();


                    String userName = readerTcp.getUserName();
                    readerMulticast.setUserName(userName);

                    byte[] nameBuffer = (((Integer)userName.length()).toString() + userName).getBytes();

                    byte[] toSend = concatenate(nameBuffer, buffer);

                    DatagramPacket sendPacket = new DatagramPacket(toSend, toSend.length, group, multicastPort);

                    multicastSocket.send(sendPacket);

                }

                else {
                    os.println(line);
                }
            }

            readerUdp.endWork();
            readerMulticast.endWork();
            readerTcp.endWork();

        }

        catch(ConnectException e){
            System.out.println("Server not available!");
        }
        finally{

            clientSocket.close();
            datagramSocket.close();
            multicastSocket.leaveGroup(group);
            multicastSocket.close();
            os.close();
            is.close();
            pool.shutdownNow();
            System.out.println("exited!");
        }

    }

}