package server.handle;


import utils.CloseUtils;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/14 17:34
 */
public class ClientHandler {


    private final Socket socket;
    private final ClientReadHandler readHandler;
    private final ClientWriteHandler writeHandler;
    private final ClientHandlerCallback clientHandlerCallback;
    private final String clientInfo;



    public ClientHandler(Socket socket, ClientHandlerCallback closeNotify) throws IOException {
        this.socket = socket;
        this.readHandler = new ClientReadHandler(socket.getInputStream());
        this.writeHandler = new ClientWriteHandler(socket.getOutputStream());
        this.clientHandlerCallback = closeNotify;
        this.clientInfo = "A[" + socket.getInetAddress().getHostAddress() + "]P[" + socket.getPort() + "]";

        System.out.println("新客户端连接：" + clientInfo);
    }

    public String getClientInfo(){
        return clientInfo;
    }


    /**
     * 退出
     */
    public void exit() {
        readHandler.exit();
        writeHandler.exit();
        CloseUtils.close(socket);
        System.out.println("客户端已退出：" + socket.getInetAddress() + "\tPortL:" + socket.getPort());
    }

    /**
     * 发送信息
     * @param str
     */
    public void send(String str) {
        writeHandler.send(str);
    }

    /**
     * 读取信息并打印
     */
    public void readToPrint() {
        readHandler.start();
    }

    private void exitBuSelf(){
        exit();
        clientHandlerCallback.onSelfClosed(this);
    }

    public interface ClientHandlerCallback{
        //自身关闭通知
        void onSelfClosed(ClientHandler handler);

        //收到消息时通知
        void onMessageArrived(ClientHandler handler,String msg);
    }

    class ClientReadHandler extends Thread {
        private boolean done = false;
        private final InputStream inputStream;

        ClientReadHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public void run() {
            super.run();

            try {
                //得到输入流，用于接收数据
                BufferedReader socketInput = new BufferedReader(new InputStreamReader(inputStream));

                do {
                    //客户端拿到一条数据
                    String str = socketInput.readLine();
                    if (str == null) {
                        System.out.println("客户端已无法读取数据！");
                        //退出当前客户端
                        ClientHandler.this.exitBuSelf();
                        break;
                    }
                    //通知到TCPServer
                    clientHandlerCallback.onMessageArrived(ClientHandler.this,str);

                } while (!done);
            } catch (IOException e) {
                if (!done) {
                    System.out.println("连接异常断开");
                    ClientHandler.this.exitBuSelf();
                }
            } finally {
                //连接关闭
                CloseUtils.close(inputStream);
            }
        }
        void exit(){
            done = true;
            CloseUtils.close(inputStream);
        }
    }

    class ClientWriteHandler {

        private boolean done = false;
        private final PrintStream printStream;
        private final ExecutorService executorService;

        ClientWriteHandler(OutputStream outputStream) {
            this.printStream = new PrintStream(outputStream);
            this.executorService = Executors.newSingleThreadExecutor();
        }

        void exit(){
            done = true;
            CloseUtils.close(printStream);
            executorService.shutdownNow();
        }

        void send(String str) {
            if (done){
                return;
            }
            executorService.execute(new WriteRunnable(str));
        }

        class WriteRunnable implements Runnable{
            private final String msg;

            WriteRunnable(String msg) {
                this.msg = msg;
            }

            public void run() {
                if (ClientWriteHandler.this.done){
                    return;
                }
                try {
                    ClientWriteHandler.this.printStream.println(msg);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}
