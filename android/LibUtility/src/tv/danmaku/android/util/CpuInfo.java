package tv.danmaku.android.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.TreeMap;
import java.util.TreeSet;

import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;

/* -
 * Qualcomm CPU part list:
 * 
 * 0x00f
 *  MSM8255, glacier, HTC
 *  MSM8255, zeus, Sony Ericsson Xperia PLAY
 *  MSM8255, mogami, Sony Ericsson Xperia neo V
 *  
 * 0x02d
 *  QCT_MSM8X60_SURF
 *  MSM8260, pyramid, HTC Sensation
 *  MSM8X60, mione
 *  MSM8260, doubleshot, HTC DoubleShot
 */
public class CpuInfo {

    // #define ARM_CPUID_ARM1026 0x4106a262
    // #define ARM_CPUID_ARM926 0x41069265
    // #define ARM_CPUID_ARM946 0x41059461
    // #define ARM_CPUID_TI915T 0x54029152
    // #define ARM_CPUID_TI925T 0x54029252
    // #define ARM_CPUID_SA1100 0x4401A11B
    // #define ARM_CPUID_SA1110 0x6901B119
    // #define ARM_CPUID_PXA250 0x69052100
    // #define ARM_CPUID_PXA255 0x69052d00
    // #define ARM_CPUID_PXA260 0x69052903
    // #define ARM_CPUID_PXA261 0x69052d05
    // #define ARM_CPUID_PXA262 0x69052d06
    // #define ARM_CPUID_PXA270 0x69054110
    // #define ARM_CPUID_PXA270_A0 0x69054110
    // #define ARM_CPUID_PXA270_A1 0x69054111
    // #define ARM_CPUID_PXA270_B0 0x69054112
    // #define ARM_CPUID_PXA270_B1 0x69054113
    // #define ARM_CPUID_PXA270_C0 0x69054114
    // #define ARM_CPUID_PXA270_C5 0x69054117
    // #define ARM_CPUID_ARM1136 0x4117b363
    // #define ARM_CPUID_ARM1136_R2 0x4107b362
    // #define ARM_CPUID_ARM1176 0x410fb767
    // #define ARM_CPUID_ARM11MPCORE 0x410fb022
    // #define ARM_CPUID_CORTEXA8 0x410fc080
    // #define ARM_CPUID_CORTEXA8_R2 0x412fc083
    // #define ARM_CPUID_CORTEXA9 0x410fc090
    // #define ARM_CPUID_CORTEXM3 0x410fc231

    // -#define ARM_CPUID_ARM1026 0x4106a262
    // -#define ARM_CPUID_ARM926 0x41069265
    // -#define ARM_CPUID_ARM946 0x41059461
    // #define ARM_CPUID_TI915T 0x54029152
    // #define ARM_CPUID_TI925T 0x54029252
    // -#define ARM_CPUID_SA1100 0x4401A11B
    // -#define ARM_CPUID_SA1110 0x6901B119
    // -#define ARM_CPUID_PXA250 0x69052100
    // -#define ARM_CPUID_PXA255 0x69052d00
    // -#define ARM_CPUID_PXA260 0x69052903
    // -#define ARM_CPUID_PXA261 0x69052d05
    // -#define ARM_CPUID_PXA262 0x69052d06
    // -#define ARM_CPUID_PXA270 0x69054110
    // -#define ARM_CPUID_PXA270_A0 0x69054110
    // -#define ARM_CPUID_PXA270_A1 0x69054111
    // -#define ARM_CPUID_PXA270_B0 0x69054112
    // -#define ARM_CPUID_PXA270_B1 0x69054113
    // -#define ARM_CPUID_PXA270_C0 0x69054114
    // -#define ARM_CPUID_PXA270_C5 0x69054117
    // -#define ARM_CPUID_ARM1136 0x4117b363
    // -#define ARM_CPUID_ARM1136_R2 0x4107b362
    // -#define ARM_CPUID_ARM1176 0x410fb767
    // -#define ARM_CPUID_ARM11MPCORE 0x410fb022
    // -#define ARM_CPUID_CORTEXA8 0x410fc080
    // -#define ARM_CPUID_CORTEXA9 0x410fc090
    // -#define ARM_CPUID_CORTEXA15 0x412fc0f1
    // -#define ARM_CPUID_CORTEXM3 0x410fc231
    // -#define ARM_CPUID_ANY 0xffffffff

    // CPU implementer
    public static final int CPU_IMPL_ARM_LIMITED = 0x41; // 'A'
    public static final int CPU_IMPL_DEC = 0x44; // 'D'
    public static final int CPU_IMPL_MOTO = 0x4d; // 'M'
    public static final int CPU_IMPL_QUALCOMM = 0x51; // 'Q'
    public static final int CPU_IMPL_MARVELL = 0x56; // 'V'
    public static final int CPU_IMPL_INTEL = 0x69; // 'i'

    public static LongSparseArray<String> sCpuImplementerMap = new LongSparseArray<String>() {
        {
            put(CPU_IMPL_ARM_LIMITED, "ARM");
            put(CPU_IMPL_DEC, "DEC");
            put(CPU_IMPL_MOTO, "Moto");
            put(CPU_IMPL_QUALCOMM, "Qualcomm");
            put(CPU_IMPL_MARVELL, "Marvell");
            put(CPU_IMPL_INTEL, "Intel");
        }
    };

    // CPU part:

    // ARMv6 xScale
    public static final int CPU_PART_PXA910 = 0x840;

    // ARMv6
    public static final int CPU_PART_ARM926 = 0x926;
    public static final int CPU_PART_ARM946 = 0x946;
    public static final int CPU_PART_ARM1026 = 0xa26;
    public static final int CPU_PART_ARM1136 = 0xb36;
    public static final int CPU_PART_ARM1156 = 0xb56;
    public static final int CPU_PART_ARM1176 = 0xb76;
    public static final int CPU_PART_ARM11_MPCORE = 0xb02;

    // ARMv7 Cortex-A
    public static final int CPU_PART_CORTEX_A5 = 0xc05;
    public static final int CPU_PART_CORTEX_A7 = 0xc07;
    public static final int CPU_PART_CORTEX_A8 = 0xc08;
    public static final int CPU_PART_CORTEX_A9 = 0xc09;
    public static final int CPU_PART_CORTEX_A15 = 0xc0f;

    public static final int CPU_PART_CORTEX_R4 = 0xc14;
    public static final int CPU_PART_CORTEX_R5 = 0xc15;

    public static final int CPU_PART_CORTEX_M3 = 0xc23;

    // Qualcomm CPU part: (Maybe)
    public static final int CPU_PART_QUALCOMM_S1_S2 = 0x00f;
    public static final int CPU_PART_QUALCOMM_S3 = 0x02d;
    public static final int CPU_PART_QUALCOMM_S4 = 0x04d;

    // CPU architecture
    public static final String CPU_ARCHITECTURE_7 = "7";
    public static final String CPU_ARCHITECTURE_6TEJ = "6TEJ";
    public static final String CPU_ARCHITECTURE_5TE = "5TE";

    private boolean mHasARMv7;
    private boolean mHasARMv6;
    private boolean mHasARMv5;
    private String mProcess;

    private static CpuInfo sInstance;

    public static LongSparseArray<String> sCpuPartMap = new LongSparseArray<String>() {
        {
            put(CPU_PART_PXA910, "PXA910");

            put(CPU_PART_ARM1136, "ARM1136");

            put(CPU_PART_CORTEX_A5, "Cortex-A5");
            put(CPU_PART_CORTEX_A7, "Cortex-A7");
            put(CPU_PART_CORTEX_A8, "Cortex-A8");
            put(CPU_PART_CORTEX_A9, "Cortex-A9");
            put(CPU_PART_CORTEX_A15, "Cortex-A15");

            put(CPU_PART_ARM926, "ARM926");
            put(CPU_PART_ARM946, "ARM946");
            put(CPU_PART_ARM1026, "ARM1026");
            put(CPU_PART_ARM1156, "ARM1156");
            put(CPU_PART_ARM1176, "ARM1176");
            put(CPU_PART_ARM11_MPCORE, "ARM11-MPCore");

            put(CPU_PART_QUALCOMM_S1_S2, "Qualcomm S1/S2");
            put(CPU_PART_QUALCOMM_S3, "Qualcomm S3");
            put(CPU_PART_QUALCOMM_S4, "Qualcomm S4");
        }
    };

    public String mRawCpuInfo = new String();
    public TreeMap<String, String> mRawInfoMap = new TreeMap<String, String>();
    public int mCpuPart;
    public int mCpuImplementer;
    public TreeSet<String> mFeatureSet = new TreeSet<String>();

    public static String getRawCpuInfo() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/cpuinfo");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        String text = StreamHelper.readStreamLineByLine(inputStream);
        StreamHelper.closeStream(inputStream);

        return text;
    }

    public String getParsedCpuInfo() {
        StringBuilder parsedCpuInfo = new StringBuilder();

        parsedCpuInfo.append("CPU implementer : ");
        parsedCpuInfo.append(getCpuImplementerText());
        parsedCpuInfo.append("\n");

        parsedCpuInfo.append("CPU part : ");
        parsedCpuInfo.append(getCpuPartText());
        parsedCpuInfo.append("\n");

        parsedCpuInfo.append("NEON : ");
        parsedCpuInfo.append(supportNeon() ? "Yes" : "No");
        parsedCpuInfo.append("\n");

        return parsedCpuInfo.toString();
    }

    public static CpuInfo parseCpuInfo() {
        if (sInstance != null)
            return sInstance;

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream("/proc/cpuinfo");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader buffReader = new BufferedReader(reader);

        try {
            inputStream = new FileInputStream("/proc/cpuinfo");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        CpuInfo cpuInfo = new CpuInfo();

        try {
            StringBuilder infoBuilder = new StringBuilder();
            String line;
            while ((line = buffReader.readLine()) != null) {
                cpuInfo.addCpuInfo(line);

                infoBuilder.append(line);
                infoBuilder.append('\n');
            }

            cpuInfo.mRawCpuInfo = infoBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            cpuInfo = null;
        } finally {
            try {
                buffReader.close();
                reader.close();
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        sInstance = cpuInfo;
        return cpuInfo;
    }

    public void addCpuInfo(String line) {
        String[] lineInfo = line.split(":", 2);
        if (lineInfo.length >= 2) {
            addCpuInfo(lineInfo[0], lineInfo[1]);
        }
    }

    public void addCpuInfo(String key, String value) {
        key = key.toLowerCase().trim();
        value = value.trim();

        mRawInfoMap.put(key, value);

        if (key.equals("processor") && TextUtils.isEmpty(mProcess)) {
            mProcess = value;
            value = value.toLowerCase();
            if (value.startsWith("armv7")) {
                mHasARMv7 = true;
            } else if (value.startsWith("armv6")) {
                mHasARMv6 = true;
            } else if (value.startsWith("arm926ej-s")) {
                mHasARMv5 = true;
            } else if (value.startsWith("marvell 88sv331x")) {
                mHasARMv5 = true;
            }
        } else if (key.equals("cpu part")) {
            value = value.toLowerCase();
            int pos = value.toLowerCase().indexOf('x');
            if (-1 == pos) {
                mCpuPart = Integer.parseInt(value);
            } else {
                mCpuPart = Integer.parseInt(value.substring(pos + 1), 16);
            }
        } else if (key.equals("cpu implementer")) {
            value = value.toLowerCase();
            int pos = value.toLowerCase().indexOf('x');
            if (-1 == pos) {
                mCpuImplementer = Integer.parseInt(value);
            } else {
                mCpuImplementer = Integer
                        .parseInt(value.substring(pos + 1), 16);
            }
        } else if (key.equals("features")) {
            value = value.toLowerCase();

            String[] features = value.split(" ");
            for (String featureItem : features) {
                mFeatureSet.add(featureItem.trim());
            }
        }
    }

    public String getCpuImplementerText() {
        String cpuPartText = sCpuImplementerMap.get(mCpuImplementer);
        if (TextUtils.isEmpty(cpuPartText)) {
            return String.format("Unknown:0x%x", mCpuImplementer);
        }

        return cpuPartText;
    }

    public String getCpuPartText() {
        String cpuPartText = sCpuPartMap.get(mCpuPart);
        if (TextUtils.isEmpty(cpuPartText)) {
            return String.format("Unknown:0x%x", mCpuPart);
        }

        return cpuPartText;
    }

    public boolean isKnownCpuId() {
        return sCpuPartMap.get(mCpuPart) != null;
    }

    private String optCpuRawInfoItem(String key) {
        if (TextUtils.isEmpty(key))
            return "";

        String value = mRawInfoMap.get(key);
        if (TextUtils.isEmpty(value))
            return "";

        return value;
    }

    public String getCpuIdString() {
        String hardware = optCpuRawInfoItem("hardware").trim()
                .replace(' ', '_');
        String implementer = optCpuRawInfoItem("cpu implementer");
        String architecture = optCpuRawInfoItem("cpu architecture");
        String variant = optCpuRawInfoItem("cpu variant");
        String part = optCpuRawInfoItem("cpu part");
        String revision = optCpuRawInfoItem("cpu revision");
        String featureList = optCpuRawInfoItem("features").replace(' ', '_');

        StringBuilder sb = new StringBuilder();
        sb.append(hardware);
        sb.append('.');
        sb.append(implementer);
        sb.append('.');
        sb.append(architecture);
        sb.append('.');
        sb.append(variant);
        sb.append('.');
        sb.append(part);
        sb.append('.');
        sb.append(revision);
        sb.append('.');
        sb.append(featureList);
        sb.append('.');
        sb.append(mProcess == null ? "" : mProcess);

        return sb.toString();
    }

    public boolean isCortexA5() {
        return CPU_PART_CORTEX_A5 == mCpuPart;
    }

    public boolean isCortexA7() {
        return CPU_PART_CORTEX_A7 == mCpuPart;
    }

    public boolean isCortexA8() {
        return CPU_PART_CORTEX_A8 == mCpuPart;
    }

    public boolean isCortexA9() {
        return CPU_PART_CORTEX_A9 == mCpuPart;
    }

    public boolean isCortexA15() {
        return CPU_PART_CORTEX_A15 == mCpuPart;
    }

    public boolean isSnapdragon_S1_or_S2() {
        if (mCpuImplementer != CPU_IMPL_QUALCOMM)
            return false;

        return CPU_PART_QUALCOMM_S1_S2 == mCpuPart;
    }

    public boolean isSnapdragon_S3() {
        if (mCpuImplementer != CPU_IMPL_QUALCOMM)
            return false;

        return CPU_PART_QUALCOMM_S3 == mCpuPart;
    }

    public boolean isSnapdragon_S4() {
        if (mCpuImplementer != CPU_IMPL_QUALCOMM)
            return false;

        return CPU_PART_QUALCOMM_S4 == mCpuPart;
    }

    public String getCpuArchitecture() {
        return mRawInfoMap.get("cpu architecture");
    }

    public boolean hasArmv7() {
        return mHasARMv7;
    }

    public boolean hasArmv6() {
        return mHasARMv6;
    }

    public boolean hasArmv5() {
        return mHasARMv5;
    }

    public boolean supportNeon() {
        return mFeatureSet.contains("neon");
    }

    public boolean supportVfpv3D16() {
        if (mFeatureSet.contains("vfpv3-d16"))
            return true;

        if (mFeatureSet.contains("vfpv3d16"))
            return true;

        return false;
    }

    public boolean supportVfp() {
        return mFeatureSet.contains("vfp");
    }
}
