package udp.talk1v1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/06 21:08
 */
public class EchoServer {
    private DatagramSocket datagramSocket;
    private String ipAddress;
    private int port;

    public EchoServer(DatagramSocket datagramSocket, String ipAddress, int port) {
        this.datagramSocket = datagramSocket;
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public void service() throws IOException {
        System.out.println("--------------服务端启动--------------");
        while (true){
            try {
                System.out.println("服务端：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                Scanner in = new Scanner(System.in);
                String msg = in.next();
                byte[] bytes = msg.getBytes();
                InetAddress ip = InetAddress.getByName(ipAddress);
                DatagramPacket packet = new DatagramPacket(bytes,bytes.length,ip,port);
                datagramSocket.send(packet);
                receiver(datagramSocket);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println("错误");
            }
        }
    }

    private void receiver(DatagramSocket datagramSocket) throws IOException {
        byte[] byres1 = new byte[1024];
        DatagramPacket data = new DatagramPacket(byres1,byres1.length);
        datagramSocket.receive(data);
        String reMsg = new String(data.getData()).trim();
        System.out.println("客户端：" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        System.out.println(reMsg);
    }

    public static void main(String[] args) throws IOException {
        new EchoServer(new DatagramSocket(8888),"localhost",9999).service();
    }
}
