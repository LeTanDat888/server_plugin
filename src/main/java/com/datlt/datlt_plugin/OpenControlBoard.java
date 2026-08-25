/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/actionListener.java to edit this template
 */
package com.datlt.datlt_plugin;

import com.common.Common;
import com.common.DatLTPluginControlBoardTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "OpenControlBoard",
        id = "com.datlt.datlt_plugin.OpenControlBoard"
)
@ActionRegistration(
        displayName = "#CTL_OpenControlBoard"
)
@ActionReference(path = "Menu/Tools/DatltPlugin", position = 0)
@Messages("CTL_OpenControlBoard=Open Control Board")
public final class OpenControlBoard implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent wTC = WindowManager.getDefault().findTopComponent("DatLTPluginControlBoardTopComponent");

        // Chỉ báo lỗi khi thực sự không tìm thấy cửa sổ.
        if (!(wTC instanceof DatLTPluginControlBoardTopComponent)) {
            String wContentNoti = "Vui lòng liên hệ người phát triển để sữa lỗi!";
            Common.showNoti(null, "Mở control board thất bại!", wContentNoti);
            return;
        }

        DatLTPluginControlBoardTopComponent wBoard = (DatLTPluginControlBoardTopComponent) wTC;

        // Đang mở sẵn thì chỉ cần đưa ra trước, không phải lỗi
        if (!wBoard.isOpened()) {
            wBoard.open();
        }
        wBoard.requestActive();
    }
}
