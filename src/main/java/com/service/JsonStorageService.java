/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
/**
 *
 * @author datlt
 */
public class JsonStorageService {

    // Reuse ObjectMapper để tối ưu hiệu năng
    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        // Cấu hình ghi file JSON có định dạng đẹp (xuống dòng, thụt lề)
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    /**
     * Ghi Object Java ra file JSON
     */
    public static <T> void writeToJsonFile(File file, T data) {
        try {
            // Tự động tạo thư mục cha nếu chưa tồn tại
            if (file.getParentFile() != null && !file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            mapper.writeValue(file, data);
            System.out.println("Lưu file JSON thành công: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi file JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Đọc file JSON và ánh xạ thành Object Java
     */
    public static <T> T readFromJsonFile(File file, Class<T> clazz) {
        if (!file.exists()) {
            System.out.println("File không tồn tại: " + file.getAbsolutePath());
            return null;
        }
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file JSON: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
