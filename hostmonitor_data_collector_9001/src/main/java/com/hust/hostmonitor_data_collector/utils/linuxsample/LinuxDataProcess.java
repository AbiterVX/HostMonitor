package com.hust.hostmonitor_data_collector.utils.linuxsample;

import com.hust.hostmonitor_data_collector.utils.CmdExecutor;
import com.hust.hostmonitor_data_collector.utils.SSHConnect.HostConfigData;
import com.hust.hostmonitor_data_collector.utils.linuxsample.Entity.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LinuxDataProcess {
    private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
//    public static int getDiskStoreSize() {
//        ArrayList<KylinDiskStore> Result=new ArrayList<>();
//        List<String> devs=runCommand("lsblk -bnd");
//        return  devs.size();
//    }
//    public static int getGraphicsCardSize() {
//        return getGraphicsCardsFromLspci().size();
//    }
    private static CmdExecutor cmdExecutor=new CmdExecutor();
    public static long queryLspciMemorySize(String lookupDevice, HostConfigData hostConfigData) {
        long vram = 0L;
        List<String> lspciMem = cmdExecutor.runCommand("lspci -v -s " + lookupDevice,hostConfigData,false);
        Iterator var4 = lspciMem.iterator();

        while(var4.hasNext()) {
            String mem = (String)var4.next();
            if (mem.contains(" prefetchable")) {
                vram += parseLspciMemorySize(mem);
            }
        }

        return vram;
    }
    private static final Pattern whitespaces = Pattern.compile("\\s+");
    private static final Pattern BYTES_PATTERN = Pattern.compile("(\\d+) ?([kMGT]?B).*");
    public static long parseLspciMemorySize(String line) {

            Matcher matcher = LSPCI_MEMORY_SIZE.matcher(line);
            return matcher.matches() ? parseDecimalMemorySizeToBinary(matcher.group(1) + " " + matcher.group(2) + "B") : 0L;

    }
    public static long parseDecimalMemorySizeToBinary(String size) {
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
    public static long parseLongOrDefault(String s, long defaultLong) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException var4) {
            System.err.println("{"+s+"} didn't parse. Returning default. {"+var4+"}");
            return defaultLong;
        }
    }
    public static double doubleTo2bits_double(double original){
        BigDecimal b=new BigDecimal(original);
        return b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    private static final Pattern LSPCI_MACHINE_READABLE = Pattern.compile("(.+)\\s\\[(.*?)\\]");
    public static Pair<String, String> parseLspciMachineReadable(String line) {
        Matcher matcher = LSPCI_MACHINE_READABLE.matcher(line);
        return matcher.matches() ? new Pair(matcher.group(1), matcher.group(2)) : null;
    }

}
