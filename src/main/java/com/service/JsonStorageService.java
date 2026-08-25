/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.openide.util.Exceptions;
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
        // File config cũ có thể chứa field đã bị xóa/đổi tên ở phiên bản mới.
        // Không tắt cờ này thì cả file config sẽ không đọc được -> mất sạch cấu hình.
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    /**
     * Ghi Object Java ra file JSON.
     *
     * Ghi ra file tạm rồi mới đổi tên đè lên file đích, tránh trường hợp crash
     * giữa chừng để lại file JSON cụt (lần sau đọc lên sẽ lỗi).
     *
     * synchronized: trên Windows, 2 luồng cùng move vào một file đích sẽ ném
     * AccessDeniedException / FileAlreadyExistsException. Ghi config rất thưa
     * nên một khóa chung là đủ, không cần khóa theo từng file.
     *
     * @return true nếu ghi thành công
     */
    public static synchronized <T> boolean writeToJsonFile(File file, T data) {
        File wTempFile = null;
        try {
            // Tự động tạo thư mục cha nếu chưa tồn tại
            File wParent = file.getParentFile();
            if (wParent != null && !wParent.exists()) {
                wParent.mkdirs();
            }

            // Tên file tạm phải là duy nhất, tránh 2 luồng cùng ghi đè lên nhau
            wTempFile = (wParent != null)
                    ? File.createTempFile(file.getName(), ".tmp", wParent)
                    : new File(file.getAbsolutePath() + ".tmp");
            mapper.writeValue(wTempFile, data);

            try {
                Files.move(wTempFile.toPath(), file.toPath(),
                        StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
            } catch (IOException | UnsupportedOperationException e) {
                // Một số filesystem không hỗ trợ ATOMIC_MOVE -> fallback move thường
                Files.move(wTempFile.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            return true;
        } catch (IOException | RuntimeException e) {
            Exceptions.printStackTrace(e);
            return false;
        } finally {
            if (wTempFile != null && wTempFile.exists()) {
                wTempFile.delete();
            }
        }
    }

    /**
     * Đọc file JSON và ánh xạ thành Object Java.
     *
     * @return null nếu file không tồn tại hoặc đọc lỗi - phía gọi BẮT BUỘC phải
     * xử lý trường hợp null (xem PluginConfigFileService.getObjectConfig).
     */
    public static <T> T readFromJsonFile(File file, Class<T> clazz) {
        if (!file.exists()) {
            return null;
        }
        try {
            return mapper.readValue(file, clazz);
        } catch (IOException | RuntimeException e) {
            Exceptions.printStackTrace(e);
            return null;
        }
    }
}
