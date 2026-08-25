/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.service;

import com.model.PluginConfig;
import java.io.File;
import org.openide.modules.Places;

/**
 *
 * @author datlt
 */
public class PluginConfigFileService {

    private static final String FOLDER_NAME = "config/DatLTPlugin";
    private static final String FILE_NAME = "DatLTPluginConfig.txt";

    public static boolean SaveConfig(PluginConfig pObject) {
        if (pObject == null) {
            return false;
        }
        return JsonStorageService.writeToJsonFile(getFileConfig(), pObject);
    }

    /**
     * Đọc cấu hình đã lưu.
     *
     * KHÔNG BAO GIỜ trả về null: file chưa có / rỗng / hỏng đều trả về cấu hình
     * mặc định. Trả null sẽ làm loadUserConfig() văng NPE, kéo theo cờ isLoaded
     * không bao giờ được bật và saveUserConfig() chết im lặng.
     */
    public static PluginConfig getObjectConfig() {
        File wFileConfig = getFileConfig();

        if (wFileConfig.exists() && wFileConfig.length() > 0) {
            PluginConfig wConfig = JsonStorageService.readFromJsonFile(wFileConfig, PluginConfig.class);
            if (wConfig != null) {
                return wConfig;
            }
            // File hỏng: đổi tên để giữ lại cho việc điều tra, lần sau ghi lại từ đầu
            File wBroken = new File(wFileConfig.getAbsolutePath() + ".bad");
            if (wBroken.exists()) {
                wBroken.delete();
            }
            wFileConfig.renameTo(wBroken);
        }

        return new PluginConfig();
    }

    private static File getFileConfig() {
        // Lấy thư mục Userdir chuẩn của NetBeans
        File wNbUserDir = Places.getUserDirectory();

        // Chạy với --userdir none thì Places trả về null -> fallback về thư mục user
        if (wNbUserDir == null) {
            wNbUserDir = new File(System.getProperty("user.home"), ".netbeans");
        }

        // Tạo thư mục plugin riêng trong Userdir
        File wPluginDir = new File(wNbUserDir, FOLDER_NAME);
        if (!wPluginDir.exists()) {
            wPluginDir.mkdirs();
        }

        return new File(wPluginDir, FILE_NAME);
    }
}
