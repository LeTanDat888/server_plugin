/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.common;

import java.util.HashMap;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.StringUtils;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.NotificationDisplayer;
import org.openide.cookies.EditorCookie;
import org.openide.text.NbDocument;
import javax.swing.text.BadLocationException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author datlt
 */
public class Common {

    public static void showNoti(Icon pIcon, String pTitle, String pContent) {
        Icon wInfoIcon = ImageUtilities.loadImageIcon(Common.getPathImageIcon("Point"), true);
        NotificationDisplayer.getDefault().notify(
                pTitle,                            // Tiêu đề
                pIcon == null ? wInfoIcon : pIcon, // Icon hiển thị
                pContent,                          // Nội dung thông báo
                null                               // Hành động khi click vào thông báo (để null nếu chỉ muốn xem)
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
    public static String showPopUpReceiveVar(String pTitlePopUp, String pTitleInput, String pInitValue) {
        String wResult = StringUtils.EMPTY;

        // 1. Tạo một hộp thoại nhập liệu (Input Dialog)
        NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                pTitleInput, // Thông điệp
                pTitlePopUp // Tiêu đề popup
        );
        input.setInputText(pInitValue); // Giá trị mặc định

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

    /**
     * Lấy toàn bộ nội dung các dòng có chứa đoạn bôi đen.
     * Tương đương getSelectedText() nhưng mở rộng ra đầu và cuối dòng.
     */
    public static String getFullSelectedLinesText(EditorCookie cookie) {
        try {
            JTextComponent editor = cookie.getOpenedPanes()[0];
            StyledDocument doc = cookie.getDocument();

            // Lấy vị trí bôi đen hiện tại (offset)
            int selectionStart = editor.getSelectionStart();
            int selectionEnd = editor.getSelectionEnd();

            // Sử dụng Element để tìm vị trí bắt đầu dòng đầu và kết thúc dòng cuối
            // Cách này an toàn và chính xác hơn trên mọi phiên bản NetBeans
            int fullStart = NbDocument.findLineRootElement(doc)
                    .getElement(NbDocument.findLineRootElement(doc).getElementIndex(selectionStart))
                    .getStartOffset();

            int fullEnd = NbDocument.findLineRootElement(doc)
                    .getElement(NbDocument.findLineRootElement(doc).getElementIndex(selectionEnd))
                    .getEndOffset();

            // Trả về chuỗi văn bản trong phạm vi đã mở rộng hoàn toàn các dòng
            return doc.getText(fullStart, fullEnd - fullStart);

        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     * Mở rộng vùng chọn ra toàn bộ các dòng chứa đoạn bôi đen và trả về chính Editor đó để tiếp tục thao tác.
     */
    public static JTextComponent getFullSelectedLinesEditor(EditorCookie cookie) {
        try {
            JTextComponent editor = cookie.getOpenedPanes()[0];
            StyledDocument doc = cookie.getDocument();

            // Lấy vị trí bôi đen hiện tại
            int selectionStart = editor.getSelectionStart();
            int selectionEnd = editor.getSelectionEnd();

            // Tìm vị trí bắt đầu dòng đầu và kết thúc dòng cuối bằng Element
            int fullStart = NbDocument.findLineRootElement(doc)
                    .getElement(NbDocument.findLineRootElement(doc).getElementIndex(selectionStart))
                    .getStartOffset();

            int fullEnd = NbDocument.findLineRootElement(doc)
                    .getElement(NbDocument.findLineRootElement(doc).getElementIndex(selectionEnd))
                    .getEndOffset();

            // Thực hiện bôi đen lại toàn bộ các dòng trên giao diện
            editor.setSelectionStart(fullStart);
            editor.setSelectionEnd(fullEnd);

            // Trả về editor đã được cập nhật vùng chọn
            return editor;

        } catch (Exception ex) {
            // Sử dụng Exception chung để bắt cả lỗi index nếu không có Pane nào mở
            Exceptions.printStackTrace(ex);
            return null;
        }
    }

    /**
     * 
     * @return 
     */
    public static DatLTPluginControlBoardTopComponent getControllBoard () {
        TopComponent wTC = WindowManager.getDefault().findTopComponent("DatLTPluginControlBoardTopComponent");

        if (wTC instanceof DatLTPluginControlBoardTopComponent) {
            DatLTPluginControlBoardTopComponent wBoard = (DatLTPluginControlBoardTopComponent) wTC;
//            wBoard.open();
//            wBoard.requestActive();
//
//            // display Align Comments Tab
//            wBoard.setSelectedIndex(1);
            return wBoard;
        }
        return null;
    }

    public static void scrollEditor(boolean up, int valueScroll) {
        // Luôn bọc trong invokeLater khi thay đổi UI
        java.awt.EventQueue.invokeLater(() -> {
            java.util.Set<TopComponent> opened = TopComponent.getRegistry().getOpened();

            for (TopComponent tc : opened) {
                EditorCookie ec = tc.getLookup().lookup(EditorCookie.class);

                // Chỉ tác động nếu file đang hiện trên màn hình
                if (ec != null && tc.isShowing()) {
                    JTextComponent[] panes = ec.getOpenedPanes();
                    if (panes != null && panes.length > 0) {
                        JTextComponent editor = panes[0];
                        javax.swing.JScrollPane scrollPane = (javax.swing.JScrollPane) javax.swing.SwingUtilities.getAncestorOfClass(javax.swing.JScrollPane.class, editor);

                        if (scrollPane != null) {
                            javax.swing.JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

                            if (verticalBar != null && verticalBar.isEnabled()) {
                                int scrollAmount = up ? -valueScroll : valueScroll;
                                int newValue = verticalBar.getValue() + scrollAmount;

                                // Giới hạn giá trị để không cuộn quá phạm vi
                                newValue = Math.max(verticalBar.getMinimum(),
                                        Math.min(newValue, verticalBar.getMaximum()));

                                verticalBar.setValue(newValue);
                                return; // Thoát sau khi cuộn Editor đang hiển thị đầu tiên
                            }
                        }
                    }
                }
            }
        });
    }

    public static void scrollWatches(boolean up, int valueScroll) {
        // Nên chạy trên luồng giao diện để đảm bảo an toàn
        java.awt.EventQueue.invokeLater(() -> {
            TopComponent watchesTC = WindowManager.getDefault().findTopComponent("watchesView");
            if (watchesTC != null) {
                // Đảm bảo cửa sổ đang mở/hiện hữu
                JScrollPane scrollPane = findScrollPane(watchesTC);
                if (scrollPane != null) {
                    javax.swing.JScrollBar verticalBar = scrollPane.getVerticalScrollBar();

                    // Kiểm tra xem thanh cuộn có đang hoạt động không
                    if (verticalBar != null && verticalBar.isEnabled()) {
                        int currentVal = verticalBar.getValue();
                        int scrollAmount = up ? -valueScroll : valueScroll;

                        // Tính toán giá trị mới
                        int newValue = currentVal + scrollAmount;

                        // Chặn giới hạn để không cuộn quá lố
                        if (newValue < verticalBar.getMinimum()) {
                            newValue = verticalBar.getMinimum();
                        }
                        if (newValue > verticalBar.getMaximum()) {
                            newValue = verticalBar.getMaximum();
                        }

                        verticalBar.setValue(newValue);
                    }
                }
            }
        });
    }

    /**
     * Hàm bổ trợ để tìm JScrollPane bên trong một Container
     * 
     * @param container
     * @return 
     */
    private static JScrollPane findScrollPane(java.awt.Container container) {
        for (java.awt.Component child : container.getComponents()) {
            if (child instanceof JScrollPane) {
                return (JScrollPane) child;
            } else if (child instanceof java.awt.Container) {
                JScrollPane found = findScrollPane((java.awt.Container) child);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }
}
