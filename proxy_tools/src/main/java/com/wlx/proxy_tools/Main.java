package com.wlx.proxy_tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;

public class Main {

    public static void main(String[] args) throws Exception {
        /**
         * 1.制作只包含解密代码的dex文件
         */
        File aarFile = new File("proxy_core/build/outputs/aar/proxy_core-debug.aar");
        File aarTemp = new File("proxy_tools/temp");
        Zip.unZip(aarFile,aarTemp);
        File classesJar = new File(aarTemp, "classes.jar");
        File classesDex = new File(aarTemp, "classes.dex");
//
//        //dx --dex --output out.dex in.jar
        Process process = Runtime.getRuntime().exec("cmd /c dx --dex --output " + classesDex.getAbsolutePath()
                + " " + classesJar.getAbsolutePath());
        process.waitFor();
        if (process.exitValue() != 0) {
            throw new RuntimeException("dex error");
        }

        /**
         * 2.加密APK中所有的dex文件
         */
        File apkFile=new File("app/build/outputs/apk/debug/app-debug.apk");
        File apkTemp = new File("app/build/outputs/apk/debug/temp");
        //解压
        Zip.unZip(apkFile, apkTemp);
        //只要dex文件拿出来加密
        File[] dexFiles = apkTemp.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File file, String s) {
                return s.endsWith(".dex");
            }
        });
        //AES加密了
        AES.init(AES.DEFAULT_PWD);
        for (File dexFile : dexFiles) {
            byte[] bytes = Utils.getBytes(dexFile);
            byte[] encrypt = AES.encrypt(bytes);
            FileOutputStream fos = new FileOutputStream(new File(apkTemp,
                    "secret-" + dexFile.getName()));
            fos.write(encrypt);
            fos.flush();
            fos.close();
            dexFile.delete();
        }

        /**
         * 3.把dex放入apk解压目录，重新压成apk文件
         */
        classesDex.renameTo(new File(apkTemp,"classes.dex"));
        File unSignedApk = new File("app/build/outputs/apk/debug/app-unsigned.apk");
        Zip.zip(apkTemp,unSignedApk);
//
//
//        /**
//         * 4.对齐和签名
//         */
//        zipalign -v -p 4 my-app-unsigned.apk my-app-unsigned-aligned.apk
        File alignedApk = new File("app/build/outputs/apk/debug/app-unsigned-aligned.apk");
        process = Runtime.getRuntime().exec("cmd /c zipalign -v -p 4 " + unSignedApk.getAbsolutePath()
                + " " + alignedApk.getAbsolutePath());
        process.waitFor();
//        if(process.exitValue()!=0){
//            throw new RuntimeException("dex error");
//        }
//
//
////        apksigner sign --ks my-release-key.jks --out my-app-release.apk my-app-unsigned-aligned.apk
////        apksigner sign  --ks jks文件地址 --ks-key-alias 别名 --ks-pass pass:jsk密码 --key-pass pass:别名密码 --out  out.apk in.apk
        File signedApk = new File("app/build/outputs/apk/debug/app-signed-aligned.apk");
        File jks = new File("proxy_tools/proxy.jks");
        process = Runtime.getRuntime().exec("cmd /c apksigner sign --ks " + jks.getAbsolutePath()
                + " --ks-key-alias 123 --ks-pass pass:123456 --key-pass pass:123456 --out "
                + signedApk.getAbsolutePath() + " " + alignedApk.getAbsolutePath());
        process.waitFor();
//        if(process.exitValue()!=0){
//            throw new RuntimeException("dex error");
//        }
        System.out.println("执行成功");

    }
}
