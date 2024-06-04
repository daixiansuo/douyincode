package com.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: ADMIN
 * @Date: 2022/6/17 16:37
 */
public class FileUtils {


    /**
     *  <h1>获取指定文件夹下所有文件，不含文件夹</h1>
     * @param dirFilePath 文件夹路径
     * @return
     */
    public static List<File> getAllFile(String dirFilePath){
        if(StringUtils.isBlank(dirFilePath))
            return null;
        return getAllFile(new File(dirFilePath));
    }

    /**
     *  <h1>获取指定文件夹下所有文件，不含文件夹</h1>
     * @param dirFile 文件夹
     * @return
     */
    public static List<File> getAllFile(File dirFile){
        // 如果文件夹不存在或着不是文件夹，则返回 null
        if(Objects.isNull(dirFile) || !dirFile.exists() || dirFile.isFile())
            return null;

        File[] childrenFiles =  dirFile.listFiles();
        if(Objects.isNull(childrenFiles) || childrenFiles.length == 0)
            return null;

        List<File> files = new ArrayList<>();
        for(File childFile : childrenFiles) {

            // 如果时文件，直接添加到结果集合
            if(childFile.isFile()) {
                files.add(childFile);
            }else {
                // 如果是文件夹，则将其内部文件添加进结果集合
                List<File> cFiles =  getAllFile(childFile);
                if(Objects.isNull(cFiles) || cFiles.isEmpty()) continue;
                files.addAll(cFiles);
            }

        }

        return files;
    }


    /**
     * 取两个文本之间的文本值
     * @param text 源文本 比如：欲取全文本为 12345
     * @param left 文本前面
     * @param right 后面文本
     * @return 返回 String
     */
    public static String getSubString(String text, String left, String right) {
        String result = "";
        int zLen;
        if (left == null || left.isEmpty()) {
            zLen = 0;
        } else {
            zLen = text.indexOf(left);
            if (zLen > -1) {
                zLen += left.length();
            } else {
                zLen = 0;
            }
        }
        int yLen = text.indexOf(right, zLen);
        if (yLen < 0 || right == null || right.isEmpty()) {
            yLen = text.length();
        }
        result = text.substring(zLen, yLen);
        return result;
    }




}
