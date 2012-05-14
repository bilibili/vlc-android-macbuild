package tv.danmaku.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

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
public class AppCpuInfo {

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

    // CPU implementer
    public static final int CPU_IMPL_ARM_LIMITED = 0x41; // 'A'
    public static final int CPU_IMPL_DEC = 0x44; // 'D'
    public static final int CPU_IMPL_MOTO = 0x4d; // 'M'
    public static final int CPU_IMPL_QUALCOMM = 0x51; // 'Q'
    public static final int CPU_IMPL_MARVELL = 0x56; // 'V'
    public static final int CPU_IMPL_INTEL = 0x69; // 'i'

    // CPU part:
    public static final int CPU_PART_NONE = 0x000;
    public static final int CPU_PART_CORTEX_A5 = 0xc05;
    public static final int CPU_PART_CORTEX_A8 = 0xc08;
    public static final int CPU_PART_CORTEX_A9 = 0xc09;
    public static final int CPU_PART_CORTEX_A15 = 0xc0f;

    public static final int CPU_PART_CORTEX_R4 = 0xc14;
    public static final int CPU_PART_CORTEX_R5 = 0xc15;

    public static final int CPU_PART_CORTEX_M3 = 0xc23;

    public static final int CPU_PART_ARM926 = 0x926;
    public static final int CPU_PART_ARM946 = 0x946;
    public static final int CPU_PART_ARM1026 = 0xa26;
    public static final int CPU_PART_ARM1136 = 0xb36;
    public static final int CPU_PART_ARM1156 = 0xb56;
    public static final int CPU_PART_ARM1176 = 0xb76;
    public static final int CPU_PART_ARM11_MPCORE = 0xb02;

    // Qualcomm CPU part: (Maybe)
    public static final int CPU_PART_QUALCOMM_8x50 = 0x002;
    public static final int CPU_PART_QUALCOMM_8x60 = 0x02d;

    public static Map<Integer, String> sCpuPartMap = Collections
            .unmodifiableMap(new HashMap<Integer, String>() {
                private static final long serialVersionUID = 4195997904984256276L;

                {
                    put(CPU_PART_CORTEX_A5, "Cortex-A5");
                    put(CPU_PART_CORTEX_A8, "Cortex-A8");
                    put(CPU_PART_CORTEX_A9, "Cortex-A9");
                    put(CPU_PART_CORTEX_A15, "Cortex-A15");
                    put(CPU_PART_CORTEX_R4, "Cortex-R4");
                    put(CPU_PART_CORTEX_R5, "Cortex-R5");
                    put(CPU_PART_CORTEX_M3, "Cortex-M3");
                    put(CPU_PART_ARM926, "ARM926");
                    put(CPU_PART_ARM946, "ARM946");
                    put(CPU_PART_ARM1026, "ARM1026");
                    put(CPU_PART_ARM1136, "ARM1136");
                    put(CPU_PART_ARM1156, "ARM1156");
                    put(CPU_PART_ARM1176, "ARM1176");
                    put(CPU_PART_ARM11_MPCORE, "ARM11-MPCore");

                    put(CPU_PART_QUALCOMM_8x60, "Qualcomm 8x50/8x55");
                    put(CPU_PART_QUALCOMM_8x60, "Qualcomm 8x60");
                }
            });

    public String mRawCpuInfo = new String();
    public TreeMap<String, String> mRawInfoMap = new TreeMap<String, String>();
    public int mCpuPart;
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

        parsedCpuInfo.append("CPU part : ");
        parsedCpuInfo.append(getCpuPartText());
        parsedCpuInfo.append("\n");

        parsedCpuInfo.append("NEON : ");
        parsedCpuInfo.append(supportNeon() ? "Yes" : "No");
        parsedCpuInfo.append("\n");

        return parsedCpuInfo.toString();
    }

    public static AppCpuInfo parseCpuInfo() {
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

        AppCpuInfo cpuInfo = new AppCpuInfo();

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

        if (key.equals("cpu part")) {
            value = value.toLowerCase();
            int pos = value.toLowerCase().indexOf('x');
            if (-1 == pos) {
                mCpuPart = Integer.parseInt(value);
            } else {
                mCpuPart = Integer.parseInt(value.substring(pos + 1), 16);
            }
        } else if (key.equals("features")) {
            value = value.toLowerCase();

            String[] features = value.split(" ");
            for (String featureItem : features) {
                mFeatureSet.add(featureItem.trim());
            }
        }
    }

    public String getCpuPartText() {
        return parseCpuPartText(mCpuPart);
    }

    public boolean isKnownCpuId() {
        return sCpuPartMap.containsKey(mCpuPart);
    }

    public String getCpuIdString() {
        String hardware = mRawInfoMap.get("hardware");
        String implementer = mRawInfoMap.get("cpu implementer");
        String architecture = mRawInfoMap.get("cpu architecture");
        String variant = mRawInfoMap.get("cpu variant");
        String part = mRawInfoMap.get("cpu part");
        String revision = mRawInfoMap.get("cpu revision");
        String featureList = mRawInfoMap.get("features").replace(' ', '_');

        StringBuilder sb = new StringBuilder();
        sb.append(hardware.trim().replace(' ', '_'));
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

        return sb.toString();
    }

    public boolean isCortexA8() {
        return CPU_PART_CORTEX_A8 == mCpuPart;
    }

    public boolean isCortexA9() {
        return CPU_PART_CORTEX_A9 == mCpuPart;
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

    public static String parseCpuPartText(int cpuPart) {
        String cpuPartText = sCpuPartMap.get(cpuPart);
        if (TextUtils.isEmpty(cpuPartText)) {
            return String.format("Unknown:0x%x", cpuPart);
        }

        return cpuPartText;
    }
}
