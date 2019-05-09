package tcp;

/**
 * @program SocketDemo
 * @author: sunxiaozhe
 * @create: 2019/05/09 14:30
 */
public class Tools {


    /**
     * byte类型的值转换为int类型的值
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b){
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * int类型的值转换为byte类型的值
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a){
        return new byte[] {
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}
