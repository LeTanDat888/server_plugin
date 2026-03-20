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
        id = "com.datlt.datlt_plugin.Lowercase"
)
@ActionRegistration(
        displayName = "#CTL_Lowercase"
)
@ActionReferences({
@ActionReference(path = "Menu/Tools/DatltPlugin", position = 40),
    // Alt + L
    @ActionReference(path = "Shortcuts", name = "A-l") 
})
@Messages("CTL_Lowercase=lowercase")
public final class Lowercase implements ActionListener {

    private final EditCookie context;

    public Lowercase(EditCookie context) {
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

                    // 0. Uppercase
                    String wNewText = Common.lowercase(selectedText);

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
                    Common.showNoti(wInfoIcon, "Lowercase Success", selectedText + " -> " + wNewText);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng bôi đen một đoạn văn bản!");
                }
            }
        }
    }
}
