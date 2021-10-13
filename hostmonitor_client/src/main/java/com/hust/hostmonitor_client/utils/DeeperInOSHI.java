package com.hust.hostmonitor_client.utils;

import com.hust.hostmonitor_client.utils.KylinEntity.KylinGPU;
import oshi.util.ParseUtil;
import oshi.util.tuples.Pair;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeeperInOSHI {
    private static final Pattern LSPCI_MEMORY_SIZE = Pattern.compile(".+\\s\\[size=(\\d+)([kKMGT])\\]");
    public static List<KylinGPU> getGraphicsCardsFromLspci() {
        List<KylinGPU> cardList = new ArrayList();
        List<String> lspci = ExecutingCommand.runNative("lspci -vnnm");
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
    private static long queryLspciMemorySize(String lookupDevice) {
        long vram = 0L;
        List<String> lspciMem = ExecutingCommand.runNative("lspci -v -s " + lookupDevice);
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
}
