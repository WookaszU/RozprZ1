package server.clientConnectors;

import java.io.BufferedReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;

public class ClientData {

    private Boolean udpOn = false;
    private InetAddress inetAddress;
    private Integer port;
    private String clientName;

    private Socket clientSocket;
    private BufferedReader is;
    private PrintStream os;

    public ClientData(String clientName, Socket clientSocket, BufferedReader is, PrintStream os) {
        this.clientName = clientName;
        this.clientSocket = clientSocket;
        this.is = is;
        this.os = os;
    }

    public void addUdpConnect(InetAddress inetAddress, Integer port){
        this.inetAddress = inetAddress;
        this.port = port;
        this.udpOn = true;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public Boolean getUdpOn() {
        return udpOn;
    }

    public BufferedReader getIs() {
        return is;
    }

    public PrintStream getOs() {
        return os;
    }

    public InetAddress getInetAddress() {
        return inetAddress;
    }

    public Integer getPort() {
        return port;
    }

    public String getClientName() {
        return clientName;
    }
}
