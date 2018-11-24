package app.com.bledemo.Format;

/**
 * Created by kxyu on 2018/11/21
 */

public interface FastModeCallback {

    void sendData(byte[] data);
    void revice(byte[] data, int len);
    void write(byte[] data, int len);

}
