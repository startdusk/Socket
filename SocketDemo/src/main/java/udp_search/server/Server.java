package udp_search.server;

import udp_search.constants.TCPConstants;

import java.io.IOException;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/13 16:48
 */
public class Server {

    public static void main(String[] args) {
        TCPServer tcpServer = new TCPServer(TCPConstants.PORT_SERVER);
        boolean isSucceed = tcpServer.start();

        if (!isSucceed){
            System.out.println("Start TCP server failed!");
            return;
        }

        UDPProvider.start(TCPConstants.PORT_SERVER);

        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        UDPProvider.stop();
        tcpServer.stop();
    }
}
