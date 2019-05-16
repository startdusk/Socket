package client;

import client.bean.ServerInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program chat
 * @author: sunxiaozhe
 * @create: 2019/05/16 13:26
 */
public class ClientTest {

    private static boolean done;


    public static void main(String[] args) throws InterruptedException, IOException {
        ServerInfo info = ClientSearch.searcherServer(10000);
        System.out.println("Server:" + info);

        if (info == null){
            return;
        }

        //当前连接数量
        int size = 0;
        final List<TCPClient> tcpClients = new ArrayList<TCPClient>();
        for (int i = 0; i < 1000; i++) {
            try {
                TCPClient tcpClient = TCPClient.startWith(info);
                if (tcpClient == null){
                    System.out.println("连接异常");
                    continue;
                }

                tcpClients.add(tcpClient);
                System.out.println("连接成功：" + (++size));

            } catch (IOException e) {
                System.out.println("连接异常");
            }
            Thread.sleep(20);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                while (!done){
                    for (TCPClient tcpClient : tcpClients){
                        tcpClient.send("Hello~");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        System.in.read();

        //等待线程完成
        done = true;
        thread.join();

        //结束所有客户端
        for (TCPClient tcpClient : tcpClients){
            tcpClient.exit();
        }
    }
}
