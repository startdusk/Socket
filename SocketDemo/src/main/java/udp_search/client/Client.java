package udp_search.client;

import udp_search.client.bean.ServerInfo;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/13 17:33
 */
public class Client {
    public static void main(String[] args) {

        ServerInfo info = ClientSearch.searcherServer(10000);
        System.out.println("Server:" + info);
    }
}
