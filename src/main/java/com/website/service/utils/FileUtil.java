package com.website.service.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class FileUtil {
    public static void writeFile(String path,String fileName,String content){
        String filePath = path.concat(fileName);
        File file = new File(filePath);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new FileWriter(filePath, true);
            writer.write(content + "\t\n");
            try {
                if(writer != null){
                    writer.flush();
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static List<String> readFile(String path, String fileName) throws Exception{
        String filePath = path.concat(fileName);
        File file = new File(filePath);
        if(!file.exists()){
            throw new FileNotFoundException("文件不存在");
        }
        List<String> list = new ArrayList<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                list.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        return list;
    }
}

