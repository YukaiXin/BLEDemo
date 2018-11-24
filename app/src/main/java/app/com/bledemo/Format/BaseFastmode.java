package app.com.bledemo.Format;

/**
 * Created by kxyu on 2018/11/20
 */

public abstract class BaseFastmode {


   abstract boolean setBlock(byte[] block, int blockLen);
   abstract boolean sendBlock(PackData packData);
   abstract boolean onSendBlock(byte[] bytes, int sendLength);
   abstract boolean onMRecieve(byte[] bytes, int len);
   abstract  void sentCallbackFlag();

}
