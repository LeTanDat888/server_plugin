/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/template_mypluginOptionsPanelController.java to edit this template
 */
package com.datlt.datlt_plugin;

import com.common.DatLTPluginControlBoardTopComponent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.prefs.Preferences;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

@OptionsPanelController.SubRegistration(
        displayName = "#AdvancedOption_DisplayName_DatLTPlugin",
        keywords = "#AdvancedOption_Keywords_DatLTPlugin",
        keywordsCategory = "Advanced/DatLTPlugin"
)
@org.openide.util.NbBundle.Messages({"AdvancedOption_DisplayName_DatLTPlugin=DatLT Plugin", "AdvancedOption_Keywords_DatLTPlugin=datlt, sync, tool, sroll, sqlcode, align"})
public final class DatLTPluginOptionsPanelController extends OptionsPanelController {

    private DatLTPluginPanel panel;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private boolean changed;

    public void update() {
        getPanel().load();
        changed = false;
    }

    @Override
    public void applyChanges() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // 1. Lưu cấu hình CheckBox vào Preferences trước
                getPanel().store();
                changed = false;

                // 2. Tìm đúng cửa sổ Control Board đang hiển thị ở phía sau bằng ID
                DatLTPluginControlBoardTopComponent topComp = (DatLTPluginControlBoardTopComponent) WindowManager.getDefault().findTopComponent("DatLTPluginControlBoardTopComponent");

                // 3. Nếu tìm thấy cửa sổ, ép nó chạy hàm đọc lại cấu hình để ẩn/hiện lblNews ngay lập tức
                if (topComp != null) {
                    topComp.refreshNewsVisibility();
                }
            }
        });

        Preferences prefs = NbPreferences.forModule(DatLTPluginControlBoardTopComponent.class);
        prefs.putBoolean("chb_displaynewsbar", panel.getChbDisplayNewsBar().isSelected());
    }

    public void cancel() {
        // need not do anything special, if no changes have been persisted yet
    }

    public boolean isValid() {
        return getPanel().valid();
    }

    public boolean isChanged() {
        return changed;
    }

    public HelpCtx getHelpCtx() {
        return null; // new HelpCtx("...ID") if you have a help set
    }

    public JComponent getComponent(Lookup masterLookup) {
        return getPanel();
    }

    public void addPropertyChangeListener(PropertyChangeListener l) {
        pcs.addPropertyChangeListener(l);
    }

    public void removePropertyChangeListener(PropertyChangeListener l) {
        pcs.removePropertyChangeListener(l);
    }

    private DatLTPluginPanel getPanel() {
        if (panel == null) {
            panel = new DatLTPluginPanel(this);
        }
        return panel;
    }

    void changed() {
        if (!changed) {
            changed = true;
            pcs.firePropertyChange(OptionsPanelController.PROP_CHANGED, false, true);
        }
        pcs.firePropertyChange(OptionsPanelController.PROP_VALID, null, null);
    }

}
