package channel.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/14 17:52
 */
public class CloseUtils {
    public static void close(Closeable...closeables){
        if (closeables == null){
            return;
        }
        for (Closeable closeable : closeables){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
