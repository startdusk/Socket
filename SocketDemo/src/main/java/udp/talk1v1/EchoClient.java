package udp.talk1v1;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/06 21:12
 */
public class EchoClient {

    private DatagramSocket datagramSocket = null;

    public EchoClient(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public void talk(){
        System.out.println("--------------客户端启动--------------");
        while (true){
            try {
                byte[] bytes = new byte[1024];
                DatagramPacket packet = new DatagramPacket(bytes,bytes.length);
                datagramSocket.receive(packet);
                String reMsg = new String(packet.getData()).trim();
                System.out.println("服务器端" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                System.out.println(reMsg);
                System.out.println("客户端：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                Scanner scanner = new Scanner(System.in);
                String in = scanner.next();
                byte[] bytes1 = in.getBytes();
                DatagramPacket data = new DatagramPacket(bytes1,bytes1.length,packet.getSocketAddress());
                datagramSocket.send(data);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("错误");
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new EchoClient(new DatagramSocket(9999, InetAddress.getByName("localhost"))).talk();
    }

}
