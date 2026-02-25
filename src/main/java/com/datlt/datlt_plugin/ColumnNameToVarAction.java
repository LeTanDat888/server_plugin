/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package com.datlt.datlt_plugin;

import com.common.Common;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.apache.commons.lang3.StringUtils;
import org.openide.cookies.EditCookie;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.EditorCookie;
import org.openide.nodes.Node;
import org.openide.text.NbDocument;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;

@ActionID(
        category = "Tools",
        id = "com.datlt.datlt_plugin.ColumnNameToVarAction"
)
@ActionRegistration(
        displayName = "#CTL_ColumnNameToVarAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools/DatltPlugin", position = 0),
    // Alt + 1
    @ActionReference(path = "Shortcuts", name = "A-1") 
})
@Messages("CTL_ColumnNameToVarAction=ColumnName To Var")
public final class ColumnNameToVarAction implements ActionListener {

    private final EditCookie context;

    public ColumnNameToVarAction(EditCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        // 1. Lấy Node đang được kích hoạt (cửa sổ đang focus)
        Node[] activatedNodes = TopComponent.getRegistry().getActivatedNodes();

        if (activatedNodes.length > 0) {
            // 2. Tìm EditorCookie từ Node đó
            EditorCookie cookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);

            if (cookie != null) {
                // 3. Truy cập vào văn bản trong Editor
                JTextComponent editor = cookie.getOpenedPanes()[0];
                String selectedText = editor.getSelectedText(); // Đây là dữ liệu bạn cần

                if (selectedText != null && !selectedText.isEmpty()) {

                    // 0. Format
                    String wNewText = formatNameDBToVar(selectedText);

                    // 1. Lấy StyledDocument từ cookie
                    StyledDocument wDoc = cookie.getDocument();
                    if (wDoc != null) {
                        try {
                            // 2. Thực hiện thay thế một cách "Atomic" (để có thể Ctrl + Z)
                            NbDocument.runAtomicAsUser(wDoc, () -> {
                                try {
                                    int wStart = editor.getSelectionStart();
                                    int wEnd = editor.getSelectionEnd();
                                    
                                    // Xóa văn bản cũ tại vị trí bôi đen
                                    wDoc.remove(wStart, wEnd - wStart);
                                    
                                    // Chèn văn bản mới vào đúng vị trí đó
                                    wDoc.insertString(wStart, wNewText, null);
                                } catch (BadLocationException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            });
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }

                    // 3. Hiển thị thông báo như cũ
                    Icon wInfoIcon = ImageUtilities.loadImageIcon(Common.getPathImageIcon("Point"), true);
                    Common.showNoti(wInfoIcon, "Thông báo từ plugin datlt: ", "Đã chuyển " + selectedText + " thành " + wNewText);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng bôi đen một đoạn văn bản!");
                }
            }
        }
    }

    /**
     * Hàm chuyển tên column lấy từ DB thành tên biến dùng trong code (ky_no -> wKyNo)
     * 
     * @param pInput
     * @return 
     */
    private String formatNameDBToVar(String pInput) {
        String wResult = StringUtils.EMPTY;

        if (StringUtils.isNotBlank(pInput)) {
            String[] wTemp = StringUtils.split(pInput, '_');
            wResult = StringUtils.join(wResult, "w", StringUtils.capitalize(wTemp[0]));

            if (wTemp.length > 1) {
                for (int i = 1; i < wTemp.length; i++) {
                    wResult = StringUtils.join(wResult, StringUtils.capitalize(wTemp[i]));
                }
            }
        }

        return wResult;
    }
}
