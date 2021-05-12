package com.hust.hostmonitor_client.utils;


import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;


import java.text.DecimalFormat;
import java.util.List;


public class DataSampler {
    SystemInfo systemInfo = new SystemInfo();
    public DataSampler(){
        OperatingSystem os = systemInfo.getOperatingSystem();
        System.out.println(os);

    }

    public void sample(){
        GlobalMemory globalMemory = systemInfo.getHardware().getMemory();

        long totalByte = globalMemory.getTotal();
        long availableByte = globalMemory.getAvailable();
        //formatUnits(,(1024*1024*1024),"GB");
        double memoryUsage = (totalByte-availableByte)*1.0/totalByte;
        System.out.println("MemoryUsage:" + new DecimalFormat("#.##%").format(memoryUsage));

        CentralProcessor centralProcessor = systemInfo.getHardware().getProcessor();
        System.out.println("LogicalProcessorCount:"+centralProcessor.getLogicalProcessorCount());


        long[] prevTicks = centralProcessor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        long[] ticks = centralProcessor.getSystemCpuLoadTicks();

        long[] value = new long[prevTicks.length];
        long totalCpu = 0;
        for(int i=0;i<value.length;i++){
            value[i] = ticks[i] - prevTicks[i];
            totalCpu += value[i];
        }

        double cpuUsage = 1.0-(value[CentralProcessor.TickType.IDLE.getIndex()] * 1.0 / totalCpu);
        System.out.println("cpuUsage:" + new DecimalFormat("#.##%").format(cpuUsage));


        Sensors sensors = systemInfo.getHardware().getSensors();
        System.out.format("CPU Temperature: %.1f°C \n", sensors.getCpuTemperature());

        systemInfo.getHardware().getPowerSources();

        List<GraphicsCard> graphicsCards =  systemInfo.getHardware().getGraphicsCards();
        for (GraphicsCard graphicsCard:graphicsCards){
            System.out.println(graphicsCard.getName());
            System.out.println(FormatUtil.formatBytes(graphicsCard.getVRam()));
        }
    }



    private static String formatUnits(long value, long prefix, String unit) {
        return value % prefix == 0L ? String.format("%d %s", value / prefix, unit) : String.format("%.2f %s", (double)value / (double)prefix, unit);
    }

    public static void main(String[] args) {
        DataSampler dataSampler = new DataSampler();
        dataSampler.sample();
    }
}
