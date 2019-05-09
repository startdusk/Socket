package tcp;

import sun.jvm.hotspot.tools.Tool;

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.ByteBuffer;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/09 13:28
 */
public class Server {

    private static final int PORT = 20000;

    public static void main(String[] args) throws IOException {

        ServerSocket server = createServerSocket();

        initServerSocket(server);

        //绑定到本地端口
        server.bind(new InetSocketAddress(Inet4Address.getLocalHost(), PORT), 50);

        System.out.println("服务器准备就绪～");
        System.out.println("服务器信息：" + server.getInetAddress() + "Port" + server.getLocalPort());

        //等待客户端连接
        for (; ; ) {
            //得到客户端连接
            Socket client = server.accept();

            //客户端构建异步线程
            ClientHandler clientHandler = new ClientHandler(client);

            //启动线程
            clientHandler.start();
        }

    }


    private static ServerSocket createServerSocket() throws IOException {

        //创建基础的ServerSocket
        ServerSocket serverSocket = new ServerSocket();

        //绑定到本地端口上
        //serverSocket = new ServerSocket(PORT);

        //绑定到本地端口20000上，并且设置当前可允许等待连接的队列为50个

        //等效于上面的方案，队列设置为50个
        //serverSocket = new ServerSocket(PORT,50);

        //与上面相同
        //serverSocket = new ServerSocket(PORT, 50, Inet4Address.getLocalHost());

        return serverSocket;

    }

    private static void initServerSocket(ServerSocket serverSocket) throws SocketException {

        //是否复用未完全关闭的端口
        serverSocket.setReuseAddress(true);

        //等效于Socket#setReceiveBufferSize
        serverSocket.setReceiveBufferSize(64 * 1024 * 1024);

        //设置serverSocket#accept超时时间
        //serverSocket.setSoTimeout(2000);

        //设置性能参数：短连接，延迟，带宽的相对重要性
        serverSocket.setPerformancePreferences(1, 1, 1);
    }


    private static class ClientHandler extends Thread {

        private Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            System.out.println("新客户端连接：" + socket.getInetAddress() + "Port:" + socket.getPort());
            try {

                //得到套接字流
                OutputStream outputStream = socket.getOutputStream();
                InputStream inputStream = socket.getInputStream();

                byte[] buff = new byte[1024];

                int readCount = inputStream.read(buff);
                ByteBuffer byteBuffer = ByteBuffer.wrap(buff,0,readCount);

                //byte
                byte be = byteBuffer.get();

                //char
                char c = byteBuffer.getChar();

                //int
                int i = byteBuffer.getInt();

                //boolean
                boolean b = byteBuffer.get() == 1;

                //Long
                long l = byteBuffer.getLong();

                //float
                float f = byteBuffer.getFloat();

                //double
                double d = byteBuffer.getDouble();

                //String
                int pos = byteBuffer.position();
                String str = new String(buff,pos,readCount-pos-1);


                System.out.println("收到数量：" + readCount);
                System.out.println("数据内容：");
                System.out.println("数据byte：" + be);
                System.out.println("数据char：" + c);
                System.out.println("数据int：" + i);
                System.out.println("数据boolean：" + b);
                System.out.println("数据long：" + l);
                System.out.println("数据float：" + f);
                System.out.println("数据double；" + d);
                System.out.println("数据String：" + str);

                outputStream.write(buff,0,readCount);


                //资源关闭操作
                outputStream.close();
                inputStream.close();

            } catch (Exception e) {
                System.out.println("连接异常断开");
            } finally {
                //连接关闭
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("客户端已退出：" + socket.getInetAddress() + "Port:" + socket.getPort());
        }
    }
}
