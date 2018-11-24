package app.com.bledemo.Format;


import java.nio.ByteBuffer;

public class PackData{


    byte tinyAndMsgIdAndMsgLen ;
    byte msgIdex;
    byte[] data;
    private ByteBuffer buffer;

    public PackData() {
        this.data = new byte[18];
        buffer = ByteBuffer.allocate(20);
    }

    public byte[] getBuffer() {
        if(buffer.hasRemaining()){
            buffer.clear();
        }
        buffer.put(tinyAndMsgIdAndMsgLen);
        buffer.put(msgIdex);
        buffer.put(data);

        buffer.rewind();

        return buffer.array();
    }
}
