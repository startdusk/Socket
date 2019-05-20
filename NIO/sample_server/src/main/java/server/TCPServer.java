package server;

import server.handle.ClientHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/14 15:59
 */
public class TCPServer implements ClientHandler.ClientHandlerCallback{
    private final int port;
    private ClientListener mListener;
    private List<ClientHandler>   clientHandlerList = new ArrayList<ClientHandler>();
    private final ExecutorService forwardingThreadPoolExecutor;

    public TCPServer(int port){
        this.port = port;
        //转发线程池
        this.forwardingThreadPoolExecutor = Executors.newSingleThreadExecutor();
    }

    public boolean start(){
        try {
            ClientListener listener = new ClientListener(port);
            mListener = listener;
            listener.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void stop(){
        if (mListener != null){
            mListener.exit();
        }

        //遍历所有客户端同步处理
        synchronized (TCPServer.this) {
            for (ClientHandler clientHandler : clientHandlerList) {
                clientHandler.exit();
            }
            clientHandlerList.clear();
        }

        //停止线程池
        forwardingThreadPoolExecutor.shutdownNow();

    }

    /**
     * 向客户端广播发送消息
     * 保证线程安全
     * @param str
     */
    public synchronized void broadcast(String str) {
        for (ClientHandler clientHandler : clientHandlerList){
            clientHandler.send(str);
        }
    }

    //保证同步安全
    @Override
    public synchronized void onSelfClosed(ClientHandler handler) {
        clientHandlerList.remove(handler);
    }

    //保证同步安全
    @Override
    public void onMessageArrived(final ClientHandler handler, String msg) {
        //打印消息到屏幕
        System.out.println("Recived：" + handler.getClientInfo() + ":" + msg);

        //异步提交转发任务
        forwardingThreadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (TCPServer.this) {
                    for (ClientHandler clientHandler : clientHandlerList) {
                        if (clientHandler.equals(handler)){
                            //跳过自己
                            continue;
                        }
                        //对其他客户端发送消息
                        clientHandler.send(msg);

                    }
                }
            }
        });
    }

    private class ClientListener extends Thread{
        private ServerSocket server;
        private boolean done = false;

        private ClientListener(int port) throws IOException {
            server = new ServerSocket(port);
            System.out.println("服务器信息：" + server.getInetAddress() + "\tport：" + server.getLocalPort());
        }

        @Override
        public void run() {
            super.run();

            System.out.println("服务器准备就绪～");
            //等待客户端连接
            do {
                Socket client;
                try {
                    client = server.accept();
                } catch (IOException e) {
                    continue;
                }
                try {
                    //客户端构建异步线程
                    ClientHandler clientHandler = new ClientHandler(client,TCPServer.this);
                    //读取数据并打印
                    clientHandler.readToPrint();
                    //添加同步处理
                    synchronized (TCPServer.this) {
                        clientHandlerList.add(clientHandler);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                    System.out.println("客户端连接异常：" + e.getMessage());
                }
            }while (!done);

            System.out.println("服务器已关闭～");
        }

        void exit(){
            done = true;
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
