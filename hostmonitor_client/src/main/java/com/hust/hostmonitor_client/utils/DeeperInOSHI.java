package com.hust.hostmonitor_client.utils;

import com.hust.hostmonitor_client.utils.KylinEntity.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeeperInOSHI {
    private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
    public static String getHostName(){
        List<String> hostName=runCommand("hostname");
        return  hostName.get(0).trim();
    }
    public static String getOSName(){
        List<String> OSName=runCommand("cat /proc/version");
        return  OSName.get(0).trim();
    }
    public static String getCPUName(){
        List<String> CPUInfo=runCommand("cat /proc/cpuinfo |grep cpu");
        return CPUInfo.get(0).split(":")[1];
    }
    public static List<KylinDiskStore> getDiskStores(){
        ArrayList<KylinDiskStore> Result=new ArrayList<>();
        List<String> devs=runCommand("lsblk -bnd");
        for(String string:devs){
            String[] tokens=string.split("\\s+");
            String devsName=tokens[0];
            System.out.println(devsName);
            String Model="unknown",Serial="unknown";
            long size=Long.parseLong(tokens[3]);
            List<String> diskInfo=runCommand("hdparm -i /dev/"+devsName);
            for(String info:diskInfo){
                System.out.println(info);
                if(info.contains("Model")){
                    tokens=info.split(",");
                    for(String token:tokens){
                        if(token.contains("Model")){
                            int index=token.indexOf("=");
                            Model=token.substring(index+1);
                        }
                        if(token.contains("Serial")){
                            int index=token.indexOf("=");
                            Serial=token.substring(index+1);
                        }
                    }
                    break;
                }
            }
            KylinDiskStore ds=new KylinDiskStore(devsName,Serial,Model,size);
            Result.add(ds);
        }

        return Result;
    }
    public static int getDiskStoreSize() {
        ArrayList<KylinDiskStore> Result=new ArrayList<>();
        List<String> devs=runCommand("lsblk -bnd");
        return  devs.size();
    }
    public static List<KylinGPU> getGraphicsCardsFromLspci() {
        List<KylinGPU> cardList = new ArrayList();
        List<String> lspci = runCommand("lspci -vnnm");
        String name = "unknown";
        String deviceId = "unknown";
        String vendor = "unknown";
        List<String> versionInfoList = new ArrayList();
        boolean found = false;
        String lookupDevice = null;
        Iterator var8 = lspci.iterator();

        while(var8.hasNext()) {
            String line = (String)var8.next();
            String[] split = line.trim().split(":", 2);
            String prefix = split[0];
            if (prefix.equals("Class") && line.contains("VGA")) {
                found = true;
            } else if (prefix.equals("Device") && !found && split.length > 1) {
                lookupDevice = split[1].trim();
            }

            if (found) {
                if (split.length < 2) {
                    cardList.add(new KylinGPU(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), queryLspciMemorySize(lookupDevice)));
                    versionInfoList.clear();
                    found = false;
                } else {
                    Pair pair;
                    if (prefix.equals("Device")) {
                        pair = parseLspciMachineReadable(split[1].trim());
                        if (pair != null) {
                            name = (String)pair.getA();
                            deviceId = "0x" + (String)pair.getB();
                        }
                    } else if (prefix.equals("Vendor")) {
                        pair = parseLspciMachineReadable(split[1].trim());
                        if (pair != null) {
                            vendor = (String)pair.getA() + " (0x" + (String)pair.getB() + ")";
                        } else {
                            vendor = split[1].trim();
                        }
                    } else if (prefix.equals("Rev:")) {
                        versionInfoList.add(line.trim());
                    }
                }
            }
        }

        if (found) {
            cardList.add(new KylinGPU(name, deviceId, vendor, versionInfoList.isEmpty() ? "unknown" : String.join(", ", versionInfoList), queryLspciMemorySize(lookupDevice)));
        }

        return cardList;
    }
    public static int getGraphicsCardSize() {
        return getGraphicsCardsFromLspci().size();
    }
    private static long queryLspciMemorySize(String lookupDevice) {
        long vram = 0L;
        List<String> lspciMem = runCommand("lspci -v -s " + lookupDevice);
        Iterator var4 = lspciMem.iterator();

        while(var4.hasNext()) {
            String mem = (String)var4.next();
            if (mem.contains(" prefetchable")) {
                vram += parseLspciMemorySize(mem);
            }
        }

        return vram;
    }
    public static final Pattern whitespaces = Pattern.compile("\\s+");
    private static final Pattern BYTES_PATTERN = Pattern.compile("(\\d+) ?([kMGT]?B).*");
    private static long parseLspciMemorySize(String line) {

            Matcher matcher = LSPCI_MEMORY_SIZE.matcher(line);
            return matcher.matches() ? parseDecimalMemorySizeToBinary(matcher.group(1) + " " + matcher.group(2) + "B") : 0L;

    }
    private static long parseDecimalMemorySizeToBinary(String size) {
        String[] mem = whitespaces.split(size);
        if (mem.length < 2) {
            Matcher matcher = BYTES_PATTERN.matcher(size.trim());
            if (matcher.find() && matcher.groupCount() == 2) {
                mem = new String[]{matcher.group(1), matcher.group(2)};
            }
        }

        long capacity = parseLongOrDefault(mem[0], 0L);
        if (mem.length == 2 && mem[1].length() > 1) {
            switch(mem[1].charAt(0)) {
                case 'G':
                    capacity <<= 30;
                    break;
                case 'K':
                case 'k':
                    capacity <<= 10;
                    break;
                case 'M':
                    capacity <<= 20;
                    break;
                case 'T':
                    capacity <<= 40;
            }
        }

        return capacity;
    }
    //Run Command
    private static long parseLongOrDefault(String s, long defaultLong) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException var4) {
            System.err.println("{"+s+"} didn't parse. Returning default. {"+var4+"}");
            return defaultLong;
        }
    }
    public static List<KylinNetworkIF> getNetworkIFs(){
        ArrayList<KylinNetworkIF> result=new ArrayList<>();
        List<String> NetworkInfo = runCommand("lspci | grep -i net");
        for(String string:NetworkInfo){
            KylinNetworkIF networkIF=new KylinNetworkIF(string);
            result.add(networkIF);
        }
        return  result;
    }


    public static int getNetworkIFSize() {
        return getNetworkIFs().size();
    }

    public static List<KylinProcess> getProcesses() {
        ArrayList<KylinProcess> result=new ArrayList<>();
        List<String> ProcessInfo = runCommand("top -b -n 1 ");
        boolean reachProcesses=false;
        for(String string:ProcessInfo){
            if(!reachProcesses){
                continue;
            }
            if(string.contains("PID")){
                reachProcesses=true;
                continue;
            }
            String[] tokens=string.split("\\s+");
            int PID=Integer.parseInt(tokens[0]);
            String Name=tokens[11];
            if(Name.equals("top")){
                continue;
            }
            double cpuUsage=Double.parseDouble(tokens[8]);
            double memoryUsage=Double.parseDouble(tokens[9]);
            //时间算法有问题，以后再debug
            String[] times=tokens[10].split(":.");
            long time=new Date().getTime();
            long useTime=Integer.parseInt(times[0])*60*1000+Integer.parseInt(times[1])*1000+Integer.parseInt(times[2])*10;
            KylinProcess kylinProcess=new KylinProcess(PID,Name,time-useTime,cpuUsage,memoryUsage,0.0f,0.0f);
            result.add(kylinProcess);
        }
        return  result;
    }

    public static KylinGlobalMemory getGlobalMemory() {
        KylinGlobalMemory result=null;
        List<String> ProcessInfo = runCommand("free");
        for(String string:ProcessInfo){

            if(string.contains("Mem")){
                String[] tokens=string.split("\\s+");
                result=new KylinGlobalMemory(Long.parseLong(tokens[1]),Long.parseLong(tokens[6]));
                break;
            }
        }
        return  result;
    }
    public static KylinPeriodRecord getPeriodRecord(){
        KylinPeriodRecord record=new KylinPeriodRecord();
        //CPU
        List<String> CPUInfo = runCommand("cat /proc/stat | grep cpu");
        for(String string:CPUInfo){
            String[] tokens=string.split("\\s+");
            if(tokens[0].equals("cpu")){
                long total=Long.parseLong(tokens[1])+Long.parseLong(tokens[2])+Long.parseLong(tokens[3])+Long.parseLong(tokens[4])
                        +Long.parseLong(tokens[5])+Long.parseLong(tokens[6])+Long.parseLong(tokens[7]);
                long used=Long.parseLong(tokens[1])+Long.parseLong(tokens[2])+Long.parseLong(tokens[3])+
                        Long.parseLong(tokens[6])+Long.parseLong(tokens[7]);
                record.setCPUused(used);
                System.out.println(used);
                record.setCPUtotal(total);
                System.out.println(total);
            }
        }
        String scriptPath=System.getProperty("user.dir")+"/ConfigData/Client/SampleCommand.sh";
        List<String> sampleInfo=runCommand(scriptPath);
        Iterator<String> itr=sampleInfo.iterator();
        while(itr.hasNext()){
            String currentString=itr.next();
            if(currentString.contains(scriptPath)){
                continue;
            }
            String[] tokens=currentString.split(":");
            if(tokens[0].equals("MemTotal")){
                record.setMemTotal(Long.parseLong(tokens[1]));
            }
            else if(tokens[0].equals("MemFree")){
                record.setMemFree(Long.parseLong(tokens[1]));
            }
            else if(tokens[0].equals("MemAvailable")){
                record.setMemAvailable(Long.parseLong(tokens[1]));
            }
            else if(tokens[0].equals("NetSend")){
                record.setNetSend(0l);
            }
            else if(tokens[0].equals("NetReceive")){
                record.setNetReceive(0l);
            }
            else if(tokens[0].equals("Power")){
                record.setCPUTemperature(40.0);
            }
            else if(tokens[0].contains("Disk_Iops")){
                DiskInfo tempDiskInfo=new DiskInfo();
                String diskName=tokens[0].split("_")[2];
                tempDiskInfo.diskName=diskName;
                tempDiskInfo.diskIOPS=Double.parseDouble(tokens[1]);
                currentString= itr.next();
                double readSpeed=Double.parseDouble(currentString.split(":")[1]);
                currentString= itr.next();
                double writeSpeed=Double.parseDouble(currentString.split(":")[1]);
                currentString= itr.next();
                double util=Double.parseDouble(currentString.split(":")[1]);
                tempDiskInfo.diskReadSpeed=readSpeed;
                tempDiskInfo.diskWriteSpeed=writeSpeed;
                tempDiskInfo.diskUsed=util;
                record.getDisks().add(tempDiskInfo);
            }
        }
        return record;
    }
    public static class Pair<A, B> {
        private final A a;
        private final B b;

        public Pair(A a, B b) {
            this.a = a;
            this.b = b;
        }

        public final A getA() {
            return this.a;
        }

        public final B getB() {
            return this.b;
        }
    }
    private static final Pattern LSPCI_MACHINE_READABLE = Pattern.compile("(.+)\\s\\[(.*?)\\]");
    public static Pair<String, String> parseLspciMachineReadable(String line) {
        Matcher matcher = LSPCI_MACHINE_READABLE.matcher(line);
        return matcher.matches() ? new Pair(matcher.group(1), matcher.group(2)) : null;
    }
    public static List<String> runCommand(String string){
        try {
            Runtime rt = Runtime.getRuntime();
            Process process=rt.exec(string);
            BufferedReader in =new BufferedReader(new InputStreamReader(process.getInputStream()));
            ArrayList<String> result=new ArrayList<>();
            String tempStr;
            while((tempStr=in.readLine())!=null){
                result.add(tempStr);
            }
            in.close();
            return  result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
