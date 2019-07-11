package com.wlx.proxy_core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;


public class Utils {

    /**
     * 读取文件
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static byte[] getBytes(File file) throws Exception {
        RandomAccessFile r = new RandomAccessFile(file, "r");
        byte[] buffer = new byte[(int) r.length()];
        r.readFully(buffer);
        r.close();
        return buffer;
    }

    /**
     * 反射获得 指定对象(当前-》父类-》父类...)中的 成员属性
     *
     * @param instance
     * @param name
     * @return
     * @throws NoSuchFieldException
     */
    public static Field findField(Object instance, String name) throws NoSuchFieldException {
        Class clazz = instance.getClass();
        //反射获得
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(name);
                //如果无法访问 设置为可访问
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                //如果找不到往父类找
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }


    /**
     * 反射获得 指定对象(当前-》父类-》父类...)中的 函数
     *
     * @param instance
     * @param name
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    public static Method findMethod(Object instance, String name, Class... parameterTypes)
            throws NoSuchMethodException {
        Class clazz = instance.getClass();
        while (clazz != null) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            } catch (NoSuchMethodException e) {
                //如果找不到往父类找
                clazz = clazz.getSuperclass();
            }
        }
        throw new NoSuchMethodException("Method " + name + " with parameters " + Arrays.asList
                (parameterTypes) + " not found in " + instance.getClass());
    }

    // 所有文件md5总和
    private static String fileSum = "";

    /**
     *
     * @param file
     * @param suffix
     * @return
     */
    public static String traverseFolder(File file, String suffix) {

        if (file == null) {
            throw new NullPointerException("遍历路径为空路径或非法路径");
        }

        if (file.exists()) { //判断文件或目录是否存在

            File[] files = file.listFiles();

            if (files.length == 0) { // 文件夹为空
                return null;
            } else {
                for (File f : files) { // 遍历文件夹

                    if (f.isDirectory()) { // 判断是否是目录

                        if ((f.getName().endsWith(suffix))) { // 只小羊.dex 结尾的目录 则计算该目录下的文件的md5值

                            // 递归遍历
                            traverseFolder(f, suffix);
                        }

                    } else {
                        // 得到文件的md5值
                        String string = checkMd5(f);
                        // 将每个文件的md5值相加
                        fileSum += string;
                    }
                }
            }

        } else {
            return null; // 目录不存在
        }

        return fileSum; // 返回所有文件md5值字符串之和
    }

    /**
     * 计算文件md5值
     * 检验文件生成唯一的md5值 作用：检验文件是否已被修改
     *
     * @param file 需要检验的文件
     * @return 该文件的md5值
     */
    private static String checkMd5(File file) {

        // 若输入的参数不是一个文件 则抛出异常
        if (!file.isFile()) {
            throw new NumberFormatException("参数错误！请输入校准文件。");
        }

        // 定义相关变量
        FileInputStream fis = null;
        byte[] rb = null;
        DigestInputStream digestInputStream = null;
        try {
            fis = new FileInputStream(file);
            MessageDigest md5 = MessageDigest.getInstance("md5");
            digestInputStream = new DigestInputStream(fis, md5);
            byte[] buffer = new byte[4096];

            while (digestInputStream.read(buffer) > 0) ;

            md5 = digestInputStream.getMessageDigest();
            rb = md5.digest();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rb.length; i++) {
            String a = Integer.toHexString(0XFF & rb[i]);
            if (a.length() < 2) {
                a = '0' + a;
            }
            sb.append(a);
        }
        return sb.toString(); //得到md5值
    }
}
