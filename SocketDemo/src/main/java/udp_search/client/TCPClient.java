package udp_search.client;

import udp_search.client.bean.ServerInfo;

import java.io.*;
import java.net.*;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/14 16:32
 */
public class TCPClient {
    public static void linkWith(ServerInfo info) throws IOException {
        Socket socket = new Socket();
        //超时时间3秒
        socket.setSoTimeout(3000);

        //连接本地，端口2000；超时时间3s
        socket.connect(new InetSocketAddress(InetAddress.getByName(info.getAddress()),info.getPort()),3000);

        System.out.println("已经发起服务器连接，并进入后续流程～");
        System.out.println("客户端信息：" + socket.getLocalAddress() + "\tPort:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress() + " \tPort" + socket.getPort());

        try {
            //发送数据
            todo(socket);
        }catch (IOException e){
            System.out.println("异常关闭");
        }

        //关闭资源
        socket.close();
        System.out.println("客户端已退出～");
    }

    private static void todo(Socket client) throws IOException {
        //构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //得到Socket输出流，并转换为打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream socketPrintStream = new PrintStream(outputStream);


        //得到Socket输入流，并转换为BufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketBufferReader = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;

        do {
            //键盘读取一行
            String str = input.readLine();
            //发送到服务器
            socketPrintStream.println(str);

            //从服务器读取一行
            String echo = socketBufferReader.readLine();
            if ("bye".equalsIgnoreCase(echo)){
                flag = false;
            }else {
                System.out.println(echo);
            }
        }while (flag);

        //关闭资源
        socketBufferReader.close();
        socketPrintStream.close();
    }
}
