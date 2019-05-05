package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * UDP搜索者，用于搜索服务支持方
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/04 15:58
 */
public class UDPSearcher {
    private static final int LISTEN_PORT = 30000;


    public static void main(String[] args) throws IOException, InterruptedException {

        System.out.println("UDPSearcher started.");

        Listener listener = listen();
        sendBroadcast();

        //读取任意键盘信息后可以退出
        System.in.read();

        List<Device> devices = listener.getDevicesAndClose();

        for (Device device : devices){
            System.out.println("Device:" + device.toString());
        }

        //完成
        System.out.println("UDPSearcher Finished.");
    }

    private static Listener listen() throws InterruptedException {


        System.out.println("UDPSearcher start listen.");
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Listener listener = new Listener(LISTEN_PORT,countDownLatch);
        listener.start();
        countDownLatch.await();
        return listener;
    }


    /**
     * 广播发送方法
     */
    private static void sendBroadcast() throws IOException {

        System.out.println("UDPSearcher sendBroadcast started.");

        //作为搜索方，无需指定端口，系统自动分配端口
        DatagramSocket datagramSocket = new DatagramSocket();

        //构建一份请求数据
        String requestData = MessageCreate.buildWithPort(LISTEN_PORT);
        byte[] requestDataBytes = requestData.getBytes();

        //直接构建packet
        DatagramPacket requestPacket = new DatagramPacket(requestDataBytes,
                requestDataBytes.length);

        //端口20000,广播地址255.255.255.255
        requestPacket.setAddress(InetAddress.getByName("255.255.255.255"));
        requestPacket.setPort(20000);
        //发送
        datagramSocket.send(requestPacket);
        datagramSocket.close();

        //完成
        System.out.println("UDPSearcher sendBroadcast Finished.");
    }

    private static class Device{
        final int port;
        final String ip;
        final String sn;

        private Device(int port, String ip, String sn) {
            this.port = port;
            this.ip = ip;
            this.sn = sn;
        }

        @Override
        public String toString() {
            return "Device{" +
                    "port=" + port +
                    ", ip='" + ip + '\'' +
                    ", sn='" + sn + '\'' +
                    '}';
        }
    }



    private static class Listener extends Thread{

        private final int listenPort;
        private final CountDownLatch countDownLatch;
        private final List<Device> devices = new ArrayList<Device>();
        private boolean done = false;
        private DatagramSocket datagramSocket = null;

        public Listener(int listenPort, CountDownLatch countDownLatch){
            super();
            this.listenPort = listenPort;
            this.countDownLatch = countDownLatch;
        }
        @Override
        public void run() {
            super.run();

            //通知启动
            countDownLatch.countDown();
            try {

                // 监听回送端口
                datagramSocket = new DatagramSocket(listenPort);

                while (!done){
                    //构建接收实体
                    final byte[] buf = new byte[512];
                    DatagramPacket receivePack = new DatagramPacket(buf,buf.length);

                    //接收
                    datagramSocket.receive(receivePack);

                    //打印接收到的信息与发送者的信息
                    //发送者的IP地址
                    String ip = receivePack.getAddress().getHostAddress();
                    int port = receivePack.getPort();
                    int dataLen = receivePack.getLength();
                    String data = new String(receivePack.getData(),0,dataLen);
                    System.out.println("UDPSearcher receive form ip:" + ip
                            +"\tport:" + port + "\tdata:" + data);

                    String sn = MessageCreate.parseSn(data);
                    if (sn != null){
                        Device device = new Device(port,ip,sn);
                        devices.add(device);
                    }
                }

            }catch (Exception e){

            }finally {
                close();
            }
            System.out.println("UDPSearcher listener finished");
        }


        private void close(){
            if (datagramSocket != null){
                datagramSocket.close();
                datagramSocket = null;
            }
        }
        List<Device> getDevicesAndClose(){
            done = true;
            close();
            return devices;
        }
    }
}
