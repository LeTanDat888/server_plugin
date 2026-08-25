/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author datlt
 */
@Getter
@Setter
public class PluginConfig {
    private int selected_tab_index = 0;

    // --- Sync Tab (Tab 0) ---
    private boolean chb_moelacmn = true;
    private boolean chb_moelasql = true;
    private boolean chb_moelagym = true;
    private boolean chb_moelacheck = true;
    private boolean chb_batbase = false;
    private boolean chb_jmsysbase = false;
    private String txt_filepath = StringUtils.EMPTY;

    // --- Align Cmt Tab (Tab 1) ---
    private boolean chb_autoalign = true;
    private String txt_tabquantity = "3";

    // --- SQL↔Code Tab (Tab 2) ---
    private String txt_namevarsb = "wSqlStr";

    // --- Scroll Tab (Tab 3) ---
    private int spn_editorscroll = 50;
    private int spn_watchesscroll = 20;

    public PluginConfig() {
    }

    /**
     * Copy constructor.
     *
     * Dùng để chụp lại trạng thái trên EDT rồi đưa xuống thread ghi file, tránh
     * việc Jackson đang serialize thì EDT sửa object.
     */
    public PluginConfig(PluginConfig pSource) {
        if (pSource == null) {
            return;
        }
        this.selected_tab_index = pSource.selected_tab_index;
        this.chb_moelacmn = pSource.chb_moelacmn;
        this.chb_moelasql = pSource.chb_moelasql;
        this.chb_moelagym = pSource.chb_moelagym;
        this.chb_moelacheck = pSource.chb_moelacheck;
        this.chb_batbase = pSource.chb_batbase;
        this.chb_jmsysbase = pSource.chb_jmsysbase;
        this.txt_filepath = pSource.txt_filepath;
        this.chb_autoalign = pSource.chb_autoalign;
        this.txt_tabquantity = pSource.txt_tabquantity;
        this.txt_namevarsb = pSource.txt_namevarsb;
        this.spn_editorscroll = pSource.spn_editorscroll;
        this.spn_watchesscroll = pSource.spn_watchesscroll;
    }
}
