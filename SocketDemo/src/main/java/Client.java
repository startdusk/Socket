import java.io.*;
import java.net.*;

/**
 * 简单客户端
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/04 14:07
 */
public class Client {

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket();

        //超时时间
        socket.setSoTimeout(3000);

        //连接本地，端口2000；超时时间为3000ms
        socket.connect(new InetSocketAddress(Inet4Address.getLocalHost(),2000),3000);

        System.out.println("已发起服务器连接，并进入后续流程");
        System.out.println("客户端信息：" + socket.getLocalAddress()+ "       Port:" + socket.getLocalPort());
        System.out.println("服务器信息：" + socket.getInetAddress()+ "        Port:" + socket.getPort());

        try {
            // 发送接收数据 阻塞的
            todo(socket);
        }catch (Exception e){
            System.out.println("异常关闭！！！");
        }
        socket.close();
        System.out.println("客户端已退出");
    }

    private static void todo(Socket client) throws IOException {
        //构建键盘输入流
        InputStream in = System.in;
        BufferedReader input = new BufferedReader(new InputStreamReader(in));

        //得到Socket输出流，并转换成打印流
        OutputStream outputStream = client.getOutputStream();
        PrintStream printStream = new PrintStream(outputStream);

        //得到Socket输入流,并转换为BufferedReader
        InputStream inputStream = client.getInputStream();
        BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

        boolean flag = true;
        do {

            //键盘读取一行
            String str = input.readLine();
            //发送到服务器
            printStream.println(str);

            //从服务器读取一行
            String echo = socketInput.readLine();
            if ("bye".equalsIgnoreCase(echo)) {
                flag = false;
            }else{
                System.out.println(echo);
            }
        }while (flag);

        //释放资源
        printStream.close();
        socketInput.close();

    }
}
