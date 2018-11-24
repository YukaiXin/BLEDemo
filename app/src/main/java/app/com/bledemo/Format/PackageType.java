package app.com.bledemo.Format;

/**
 * Created by kxyu on 2018/11/20
 */

public class PackageType {

    public static final int PACK_CONTENT_LENGTH = 18;
    public static final int PACK_TRANSFER_SIZE   = 2048;

    public static final byte HEAD =  0;
    public static final byte DATA = 1;
    public static final byte SACK = 2;
    public static final byte RACK = 3;


    public static final byte ACK_TYPE_HEAD = 0;
    public static final byte ACK_TYPE_GET_MAP = 1;
    public static final byte ACK_TYPE_WITH_MAP = 2;
    public static final byte ACK_TYPE_TAIL = 3;





}
