package udp_search.client;

import udp_search.client.bean.ServerInfo;

import java.io.IOException;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/13 17:33
 */
public class Client {
    public static void main(String[] args) {

        ServerInfo info = ClientSearch.searcherServer(10000);
        System.out.println("Server:" + info);

        if (info != null){
            try {
                TCPClient.linkWith(info);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
