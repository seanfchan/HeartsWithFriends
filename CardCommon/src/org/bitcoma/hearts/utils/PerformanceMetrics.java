package org.bitcoma.hearts.utils;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

public class PerformanceMetrics {

    private static PerformanceMetrics instance;
    private Sigar sigar = new Sigar();

    public static PerformanceMetrics instance() {
        if (instance == null) {
            instance = new PerformanceMetrics();
        }
        return instance;
    }

    // ********* Memory Statistics **********
    public RamUsageInfo getSystemRamInfo() {
        RamUsageInfo info = new RamUsageInfo();
        try {
            Mem mem = sigar.getMem();

            info.ramUsed = mem.getUsed();
            info.ramFree = mem.getFree();
            info.ramMax = mem.getTotal();

        } catch (SigarException e) {
            e.printStackTrace();
        }

        return info;
    }

    public RamUsageInfo getJVMRamInfo() {
        RamUsageInfo info = new RamUsageInfo();
        Runtime runTime = Runtime.getRuntime();
        info.ramFree = runTime.freeMemory();
        info.ramUsed = runTime.totalMemory() - runTime.freeMemory();
        info.ramMax = runTime.totalMemory();
        return info;
    }

    // ************* CPU Statistics *************
    public int getNumCpuSockets() {

        int numCpuSockets = 0;

        try {
            CpuInfo[] infos = sigar.getCpuInfoList();

            for (CpuInfo info : infos) {
                numCpuSockets += info.getTotalSockets();
            }

        } catch (SigarException e) {
            e.printStackTrace();
        }

        return numCpuSockets;
    }

    public int getNumCpuCores() {
        int numCpuCores = 0;

        try {
            CpuInfo[] infos = sigar.getCpuInfoList();

            for (CpuInfo info : infos) {
                numCpuCores += info.getTotalCores();
            }

        } catch (SigarException e) {
            e.printStackTrace();
        }

        return numCpuCores;
    }

    public CpuUsageInfo getCpuUsageInfo() {
        CpuUsageInfo info = new CpuUsageInfo();
        try {
            CpuPerc cpuPerc = sigar.getCpuPerc();

            info.userPerc = cpuPerc.getUser();
            info.sysPerc = cpuPerc.getSys();
            info.totalPerc = cpuPerc.getCombined();
        } catch (SigarException e) {
            e.printStackTrace();
        }

        return info;
    }

    public class RamUsageInfo {
        public long ramUsed;
        public long ramFree;
        public long ramMax;
    }

    public class CpuUsageInfo {
        public double userPerc;
        public double sysPerc;
        public double totalPerc;
    }

    public static void main(String[] args) {
        RamUsageInfo jvmRam = PerformanceMetrics.instance().getJVMRamInfo();
        System.out.println("JVM Free: " + jvmRam.ramFree / 1024.0);
        System.out.println("JVM Used: " + jvmRam.ramFree / 1024.0);
        System.out.println("JVM Max : " + jvmRam.ramFree / 1024.0);

        RamUsageInfo systemRam = PerformanceMetrics.instance().getSystemRamInfo();
        System.out.println("System Free: " + systemRam.ramFree / 1024.0);
        System.out.println("System Used: " + systemRam.ramUsed / 1024.0);
        System.out.println("System Max : " + systemRam.ramMax / 1024.0);

        System.out.println("Total # CPU Sockets: " + PerformanceMetrics.instance().getNumCpuSockets());
        System.out.println("Total # CPU Cores: " + PerformanceMetrics.instance().getNumCpuCores());

        CpuUsageInfo cpuInfo = PerformanceMetrics.instance().getCpuUsageInfo();
        System.out.println("Total CPU Usage: " + cpuInfo.totalPerc);
        System.out.println("User CPU Usage: " + cpuInfo.userPerc);
        System.out.println("Sys CPU Usage: " + cpuInfo.sysPerc);
    }

}
