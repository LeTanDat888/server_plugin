/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.StringUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.text.NbDocument;

/**
 *
 * @author datlt
 */
public class Common {

    public static void showNoti(Icon pIcon, String pTitle, String pContent) {
        NotificationDisplayer.getDefault().notify(
                pTitle, // Tiêu đề
                pIcon, // Icon hiển thị
                pContent, // Nội dung thông báo
                null // Hành động khi click vào thông báo (để null nếu chỉ muốn xem)
        );
    }
    
    public static String getPathImageIcon(String pNameIcon) {
        if (StringUtils.isNotBlank(pNameIcon)) {
            return StringUtils.join("com/common/images/", pNameIcon, ".png");
        }

        return StringUtils.EMPTY;
    }

    /**
     * Hiện Popup lấy tham số người dùng nhập
     * 
     * @param pTitlePopUp
     * @param pTitleInput
     * @return 
     */
    public static String showPopUpReceiveVar(String pTitlePopUp, String pTitleInput) {
        String wResult = StringUtils.EMPTY;

        // 1. Tạo một hộp thoại nhập liệu (Input Dialog)
        NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                pTitleInput, // Thông điệp
                pTitlePopUp // Tiêu đề popup
        );
        input.setInputText("-1"); // Giá trị mặc định

        // 2. Hiển thị popup và kiểm tra xem người dùng nhấn OK hay Cancel
        if (DialogDisplayer.getDefault().notify(input) == NotifyDescriptor.OK_OPTION) {

            // 3. Lấy giá trị người dùng vừa nhập
            wResult = input.getInputText();
        }

        return wResult;
    }

    /**
     * Lấy chỉ số dòng bắt đầu và kết thúc của vùng văn bản được chọn trong editor
     * 
     * @param pCookie
     * @return Map chứa chỉ số dòng bắt đầu (key "sta") và kết thúc (key "end")
     */
    public static Map<String, Object> getIndexStaEndOfSelectedText(EditorCookie pCookie) {
        Map<String, Object> wResult = new HashMap<>();

        JTextComponent editor = pCookie.getOpenedPanes()[0];
        StyledDocument doc = pCookie.getDocument();

        // 1. Lấy vị trí ký tự (Offset) bắt đầu và kết thúc của vùng chọn
        int startOffset = editor.getSelectionStart();
        int endOffset = editor.getSelectionEnd();

        // 2. Chuyển đổi Offset sang số dòng (Dòng đầu tiên là dòng 0)
        int startLine = NbDocument.findLineNumber(doc, startOffset);
        int endLine = NbDocument.findLineNumber(doc, endOffset);

        // 3. Gán vào kết quả vị trí start end
        wResult.put("sta", startLine+1);
        wResult.put("end", endLine+1);

        return wResult;
    }

    /**
     * Uppercase
     * 
     * @param pCookie
     * @return 
     */
    public static String uppercase(String pText) {
        return StringUtils.upperCase(pText);
    }

    /**
     * Lowercase
     *
     * @param pCookie
     * @return
     */
    public static String lowercase(String pText) {
        return StringUtils.lowerCase(pText);
    }
}
