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
}
