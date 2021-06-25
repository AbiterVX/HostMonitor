import oshi.SystemInfo;
import oshi.hardware.HWDiskStore;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class oshiTest {
    public static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();
    public static void main(String[] args){
//        SystemInfo systemInfo=new SystemInfo();
//        List<HWDiskStore> list=systemInfo.getHardware().getDiskStores();
//        for(HWDiskStore hwDiskStore:list){
//            System.out.println("Model:"+hwDiskStore.getModel());
//            System.out.println("Name:"+hwDiskStore.getName());
//            System.out.println("Serial:"+hwDiskStore.getSerial());
//            System.out.println("Serial:"+toHexString(hwDiskStore.getSerial().getBytes(StandardCharsets.UTF_8)));
//            System.out.println("Serial trim:"+hwDiskStore.getSerial().trim());
//            System.out.println();
//        }
        String original="CW3C7FAKA79EF";
        System.out.println(stringReorder(original));
    }
    public static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        sb.append("[");
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(',');
        }
        return sb.substring(0,sb.length()-1)+"]";
    }
    public static String stringReorder(String original){
        StringBuffer stringBuffer=new StringBuffer();
        int groupNumber=original.length()/2;
        int i=0;
        for(i=0;i<groupNumber;i++){
            stringBuffer.append(original.charAt(i*2+1));
            stringBuffer.append(original.charAt(i*2));
        }
        if(i*2==original.length()-1);
        stringBuffer.append(original.charAt(i*2));
        return stringBuffer.toString();
    }
}
