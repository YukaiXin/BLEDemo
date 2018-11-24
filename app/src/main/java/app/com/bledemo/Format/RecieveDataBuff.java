package app.com.bledemo.Format;

import java.nio.ByteBuffer;

import static app.com.bledemo.Format.PackageType.PACK_TRANSFER_SIZE;


/**
 * Created by kxyu on 2018/11/21
 */

public class RecieveDataBuff {
    public int len;
    public int head;
    public int tail;
    public ByteBuffer buffer;

    public RecieveDataBuff() {
        buffer = ByteBuffer.allocate(PACK_TRANSFER_SIZE); //2048
    }
}
