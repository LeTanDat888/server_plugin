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

    public static void SaveConfig(PluginConfig pObject) {
        JsonStorageService.writeToJsonFile(getFileConfig(), pObject);
    }

    public static PluginConfig getObjectConfig() {
        File wFileConfig = getFileConfig();
        PluginConfig wConfig = new PluginConfig();

        if (wFileConfig.length() != 0) {
            wConfig = JsonStorageService.readFromJsonFile(wFileConfig, PluginConfig.class);
        }
        return wConfig;
    }

    private static File getFileConfig() {
        // Lấy thư mục Userdir chuẩn của NetBeans
        File wNbUserDir = Places.getUserDirectory();

        // Tạo thư mục plugin riêng trong Userdir
        File wPluginDir = new File(wNbUserDir, FOLDER_NAME);
        if (!wPluginDir.exists()) {
            wPluginDir.mkdirs();
        }

        return new File(wPluginDir, FILE_NAME);
    }
}
