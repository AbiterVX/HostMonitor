package DP;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.List;

/** 内存文件映射
 * @author 62786
 * @date: 2020年12月7日 下午7:19:39
 */
public class MappedBigFileReader {
    private MappedByteBuffer[] mappedBufArray;
    private int number; // 内存文件映射数组的大小
    private int count = 0; // 当前的内存映射区
    private int data_count = 0; // 当前的数据块
    private FileInputStream fileIn;
    private long fileLength; // 文件长度
    private long readLength = 0; // 已读取的文件长度
    private int arraySize; // 单次读取的文件长度
    private byte[] array;
    private byte[] last_byte = {};
    private int convert = 1024 * 1024;

    public MappedBigFileReader(String fileName, int arraySize) throws IOException {
        this.fileIn = new FileInputStream(fileName);
        FileChannel fileChannel = fileIn.getChannel();
        this.fileLength = fileChannel.size();
        this.number = (int) Math.ceil((double) fileLength / (double) Integer.MAX_VALUE);
        this.mappedBufArray = new MappedByteBuffer[number];// 内存文件映射数组
        long curLength = 0;
        long regionSize = (long) Integer.MAX_VALUE;// 映射区域的大小
        for (int i = 0; i < number; i++) {// 将文件的连续区域映射到内存文件映射数组中
            if (fileLength - curLength < (long) Integer.MAX_VALUE) {
                regionSize = fileLength - curLength;// 最后一片区域的大小
            }
            mappedBufArray[i] = fileChannel.map(FileChannel.MapMode.READ_ONLY, curLength, regionSize);
            curLength += regionSize;// 下一片区域的开始
        }
        this.arraySize = arraySize;
    }

    public int read() throws IOException {
        if (count >= number) {
            return -1;
        }
        int limit = mappedBufArray[count].limit();
        int position = mappedBufArray[count].position();
        if (limit - position > arraySize) {
            array = new byte[arraySize];
            mappedBufArray[count].get(array);
            return arraySize;
        } else {// 本内存文件映射最后一次读取数据
            array = new byte[limit - position];
            mappedBufArray[count].get(array);
            if (count < number) {
                count++;// 转换到下一个内存文件映射
            }
            return limit - position;
        }
    }

    public void close() throws IOException {
        fileIn.close();
        array = null;
    }

    public byte[] getArray() {
        return array;
    }
    
    public int getDatacount() {
        return data_count;
    }
    
    public double getReadLength() {
        return 1.0 * readLength / convert;
    }
    
    public double getFileLength() {
        return 1.0 * fileLength / convert;
    }
    
    public List<String> readArray() {
    	readLength += array.length;
        byte[] new_array = new byte[array.length + last_byte.length]; 
        System.arraycopy(last_byte, 0, new_array, 0, last_byte.length);  
        System.arraycopy(array, 0, new_array, last_byte.length, array.length);  
        
    	String s = new String(new_array);
    	List<String> data = Arrays.asList(s.split("\\n"));
//    	System.out.printf("process %d %d%n %s %n %s %n %s %n", data.size(), fileLength,
//    			data.get(0), data.get(data.size() - 2), data.get(data.size() - 1));
    	last_byte = data.get(data.size() - 1).getBytes();
    	data_count ++;
    	return data.subList(0, data.size() - 1);
    }
    
//
//    public static void main(String[] args) throws IOException {
//    	List<String> data = null;
//    	int num = 0;
//        MappedBigFileReader reader = new MappedBigFileReader("./file/input/test.csv", 65536000);
//        long start = System.nanoTime();
//        while (reader.read() != -1) {
//        	System.out.println(num);
//        	data = reader.readArray();
//        	System.out.println(data.size());
//        	System.out.println(data.get(0));
//        	System.out.println(data.get(data.size() - 1));
//        	num ++;
//        };
//        long end = System.nanoTime();
//        reader.close();
//        System.out.println("MappedBiggerFileReader: " + (end - start));
//    }
}