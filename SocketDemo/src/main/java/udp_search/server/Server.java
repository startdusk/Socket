package udp_search.server;

import udp_search.constants.TCPConstants;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/13 16:48
 */
public class Server {

    public static void main(String[] args) {
        ServerProvider.start(TCPConstants.PORT_SERVER);
    }
}
