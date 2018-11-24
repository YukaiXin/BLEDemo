package app.com.bledemo.Format;



import java.nio.ByteBuffer;

import app.com.bledemo.Utils.Convert;
import app.com.bledemo.Utils.MLog;

import static app.com.bledemo.Format.PackageType.ACK_TYPE_GET_MAP;
import static app.com.bledemo.Format.PackageType.ACK_TYPE_HEAD;
import static app.com.bledemo.Format.PackageType.ACK_TYPE_TAIL;
import static app.com.bledemo.Format.PackageType.ACK_TYPE_WITH_MAP;
import static app.com.bledemo.Format.PackageType.DATA;
import static app.com.bledemo.Format.PackageType.HEAD;
import static app.com.bledemo.Format.PackageType.PACK_CONTENT_LENGTH;
import static app.com.bledemo.Format.PackageType.PACK_TRANSFER_SIZE;
import static app.com.bledemo.Format.PackageType.RACK;
import static app.com.bledemo.Format.PackageType.SACK;


/**
 * Created by kxyu on 2018/11/21
 */

public class FastMode extends  BaseFastmode {

    private static String TAG = "FastMode";
    static boolean isGetAck = false;
    static int recvBlockLen = 0;
    public static boolean isSendFlag = false;
    static boolean isRecvFlag = false;
    static boolean isSendExitSuc = false;

    static PackData sendBlockMap = new PackData();
    static PackData recvBlockMap = new PackData();

    private FastModeCallback fastModeCallback;
    private ByteBuffer revBuff;

    public FastMode(FastModeCallback fastModeCallback) {
        this.fastModeCallback = fastModeCallback;
    }


    public byte[] fillHeadPackData(int leng){

        byte[] tmpData = new byte[20];
        tmpData[0] = (byte) ((2 << 3 )& 0xFF);
        tmpData[1] = (byte) 0x00;
        tmpData[2] = (byte) (255 & 0xFF);
        tmpData[3] = (byte) ((255 >> 8) & 0xFF);

        for (int i = 4; i < 18; i ++){
            tmpData[i] = (byte) ( 0 & 0xFF);
        }
        return tmpData;
    }

    //make ack data package
    public static PackData fillAckPackData(byte dataType,byte ackType,byte[] data){
        PackData packData = new PackData();

        packData.tinyAndMsgIdAndMsgLen = (byte) (dataType << 1);
        packData.msgIdex = ackType;

        return packData;
    }



    @Override
    boolean setBlock(byte[] block, int blockLen) {


        return false;
    }

    @Override
    boolean sendBlock(PackData packData) {

        return false;
    }

    @Override
    public boolean onSendBlock(byte[] bytes, int sendLength) {

        if(isSendFlag) return false;
        isSendFlag = true;
        return sendPackage(bytes, (short) sendLength);

    }


    private  boolean sendPackage(byte[] bytes, int sendLength) {

        ByteBuffer writeData = ByteBuffer.allocate(PACK_TRANSFER_SIZE);
        writeData.put(bytes);
        writeData.rewind();

        boolean isSendSuccess = true;
        int tlen;
        int retry = 0;
        int count = 0;

        byte[] headData = fillHeadPackData(sendLength);
        MLog.d(TAG, " send head package  "+sendLength);
        isGetAck = false;
        retry = 0;
        while (true){
            if (count++ % 2500 == 0) {

                if (retry++ > 3) {
                    MLog.i(TAG, "  send head package failed !!!!  ");
                    isSendFlag = false;
                    return false;
                }
                if (retry > 1) {
                    MLog.d(TAG, "  send head data in cycle  ");
                }

                MLog.i(TAG,"Send HEAD Data :  "+ Convert.bytesToHexString(headData));
                fastModeCallback.sendData(headData);
            } else {
                sleep(400);
            }

            if (isGetAck) {
                MLog.d(TAG, "  send Head success !!! ");
                count = 0;

                isGetAck = false;
                break;
            }

        }


        while (true){

            //Send Data package
            MLog.d(TAG," send data lenght  "+ sendLength);

            /////////////////// 组宝 》》》》》》》》
            byte[] dataPackage = new byte[20];


            //////////////////
            count = 0;
            dataPackage[0] = (byte) ((DATA << 1) & 0xFF);

            for (int i = 0; i < sendLength ; i += PACK_CONTENT_LENGTH){
                int pos = i / PACK_CONTENT_LENGTH;
                MLog.i(TAG, "  send data pack");

                if(i + PACK_CONTENT_LENGTH < sendLength){
                    tlen = PACK_CONTENT_LENGTH;
                }else {
                    tlen = (char) (sendLength - i);
                }

                if((sendBlockMap.data[pos/8] & (1 << (pos % 8))) == 0x00){
                    dataPackage[0] = (byte) (((DATA << 1 ) & 0xFF | (tlen << 3) & 0xFF));
                    dataPackage[1] = (byte) pos;
                    writeData.get(dataPackage, 2, tlen - 1);
                    MLog.i(TAG,"  数据位置 "+ pos +"  数据长度 :    "+ tlen  + "   发送数据     ：  "+Convert.bytesToHexString(dataPackage));
                    fastModeCallback.sendData(dataPackage);

                    sleep(2000);
                }else {
                    MLog.i(TAG,"  map  "+ pos/8 + " =  "+ sendBlockMap.data[pos/8]+"    ");
                }

            }

            MLog.i(TAG," data 数据发送完，send ACK package   ");
            //Send Ack package
            isGetAck = false;
            count = 0;
            retry = 0;
//            PackData ackData = fillAckPackData(SACK, ACK_TYPE_GET_MAP, null);

            byte[] ackDataTMp = new byte[20];
            ackDataTMp[0] = SACK << 1 ;
            ackDataTMp[1] = ACK_TYPE_GET_MAP & 0xFF;
            sleep(1000);

            while (true){
                if(count ++ % 2500 == 0){
                    if (retry++ > 5){
                        isSendFlag = false;
                        return false;
                    }
                    //TODO:  send

                    MLog.i(TAG, " send ack ACK_TYPE_GET_MAP    "+Convert.bytesToHexString(ackDataTMp));
                    fastModeCallback.sendData(ackDataTMp);
                }else {
                    sleep(400);
                }

                if(isGetAck){
                    isSendSuccess = true;
                    for (int i = 0; i < sendLength ; i += PACK_CONTENT_LENGTH){
                        int pos = i/PACK_CONTENT_LENGTH;
                        if((sendBlockMap.data[pos/8] & (1 << (pos % 8))) == 0x00){
                            isSendSuccess = false;
                            break;
                        }
                    }
                }

                if(isSendSuccess){
                    ackDataTMp[1] = ACK_TYPE_TAIL & 0xFF;

                    MLog.i(TAG, " send ack tail data    "+Convert.bytesToHexString(ackDataTMp));
                    sleep(1000);
                    fastModeCallback.sendData(ackDataTMp);
                    sleep(1000);
                    fastModeCallback.sendData(ackDataTMp);
                    isSendFlag = false;
                    return true;
                }else {
                    MLog.d(TAG, "  send package lost data ");
                    break;
                }
            }
        }

    }

    private boolean onRecieve(byte[] bytes, int len) {

        revBuff = ByteBuffer.allocate(PACK_TRANSFER_SIZE);

        MLog.d(TAG, " onRecieve  index "+   "    hex  string  "+ Convert.bytesToHexString(bytes));
        PackData packData = new PackData();
        packData.tinyAndMsgIdAndMsgLen = bytes[0];
        packData.msgIdex = bytes[1];

        byte[] dataPack = new byte[20];


        byte pos = 0;

        byte msgId = (byte) ((bytes[0] & 0x06)  >> 1);  // 00000110
        switch (msgId){
            case HEAD:


                recvBlockLen = packData.data[2] + packData.data[3] << 8 ;
                MLog.d(TAG," mRecvive: head data    data  2  :  " + packData.data[2]);
                MLog.d(TAG," mRecvive: head data    data 3 :  " + (packData.data[3] << 8));
                MLog.d(TAG," mRecvive: head data    data lenght :  " + recvBlockLen);

                if(isSendFlag){
                    if(recvBlockLen > 0){
                        isSendFlag = false;
                        MLog.d(TAG," mRecvive: Force to stop send ? ");
                        sleep(5000);
                    }
                    return false;
                }
                if(recvBlockLen == 0){
                  return false;
                }

                recvBlockMap.tinyAndMsgIdAndMsgLen = ((RACK << 1) & 0xFFF);
                recvBlockMap.msgIdex = ACK_TYPE_HEAD;
                MLog.i(TAG, " mRecvive: send Head ack ");
                fastModeCallback.sendData(recvBlockMap.getBuffer());
                break;
            case DATA:
                pos = packData.msgIdex;

                recvBlockMap.data[pos/8] |= (1 << (pos % 8));
                revBuff.put(packData.data);

                break;
            case SACK:

                if(recvBlockLen == 0){
                    return true;
                }

                if(packData.msgIdex == ACK_TYPE_GET_MAP){
                    isGetAck = true;
                    recvBlockMap.tinyAndMsgIdAndMsgLen = ((RACK << 3) & 0xFFF);
                    recvBlockMap.msgIdex = ACK_TYPE_WITH_MAP;
                    sleep(5000);
                    fastModeCallback.sendData(recvBlockMap.getBuffer());

                }else if(packData.msgIdex == ACK_TYPE_TAIL) {
                    //TODO:  rev callback
                    fastModeCallback.revice(revBuff.array(), recvBlockLen);
                    recvBlockLen = 0;
                    recvBlockMap.msgIdex = 0;
                    recvBlockMap.tinyAndMsgIdAndMsgLen = 0;
                    return true;
                }else {
                    MLog.i(TAG,"  SACK unkown state  "+packData.msgIdex);
                }
                break;
            case RACK:
                MLog.d(TAG," Rack   ");
                if(isSendFlag && !isGetAck){
                    if(packData.msgIdex == ACK_TYPE_HEAD){
                        MLog.i(TAG," RACK and ACK_HEAD ");
                        isGetAck = true;
                        return true;
                    }else if(packData.msgIdex == ACK_TYPE_WITH_MAP){
                        sendBlockMap.data = packData.data;
                        isGetAck = true;
                        MLog.i(TAG, "RACK and  ACK_WITH_MAP ");
                        return true;
                    }else {
                        MLog.i(TAG,"  RACK unkown state  "+packData.msgIdex);
                    }
                }else {
                    MLog.i(TAG,"  isSendFlag : "+isSendFlag+" isGetAck : n "+isSendFlag);
                }
                break;

        }
        return true;
    }

    @Override
    public boolean onMRecieve(byte[] bytes, int len) {

        if(len < 20 ){
            MLog.d(TAG,"Revice data length is not 20 byte");
            return false;
        }
        return onRecieve(bytes, len);
    }

    @Override
    void sentCallbackFlag() {
        isSendExitSuc = true;
    }


    private void sleep(long m){
        try {
            Thread.sleep(m);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}
