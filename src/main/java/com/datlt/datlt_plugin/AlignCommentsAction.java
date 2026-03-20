/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/NetBeansModuleDevelopment-files/contextAction.java to edit this template
 */
package com.datlt.datlt_plugin;

import com.common.Common;
import com.common.DatLTPluginControlBoardTopComponent;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;
import org.apache.commons.collections4.MapUtils;
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
        id = "com.datlt.datlt_plugin.AlignCommentsAction"
)
@ActionRegistration(
        displayName = "#CTL_AlignCommentsAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools/DatltPlugin", position = 20),
    // Alt + 2
    @ActionReference(path = "Shortcuts", name = "A-2") 
})
@Messages("CTL_AlignCommentsAction=Align Comments")
public final class AlignCommentsAction implements ActionListener {

    /*
     *
     */
    private static final String TITLE_POPUP = "Align Comments";

    /*
     *
     */
    private static final String TITLE_INPUT = "Số Tab giữa comment và code dài nhất \n (Vui lòng nhập -1 để sử dụng Auto format): ";

    /*
     *
     */
    private static final String INIT_VALUE = "-1";

    private final EditCookie context;

    public AlignCommentsAction(EditCookie context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        DatLTPluginControlBoardTopComponent wBoard = Common.getControllBoard();
        Node[] activatedNodes = TopComponent.getRegistry().getActivatedNodes();
        if (activatedNodes.length > 0) {
            EditorCookie cookie = activatedNodes[0].getLookup().lookup(EditorCookie.class);
            if (cookie != null) {
                Map<String, Object> wIndexStaEnd = Common.getIndexStaEndOfSelectedText(cookie);
                JTextComponent editor = Common.getFullSelectedLinesEditor(cookie);
                String wSelectedText = editor.getSelectedText();

                if (StringUtils.isNotBlank(wSelectedText)) {
                    Map<String, Object> wMapVar = wBoard == null ? new HashMap<>() : wBoard.getVar(1);
                    String wTabQuantiy;
                    boolean wIsAutoFormat;
                    boolean wChbAutoAlign = MapUtils.getBoolean(wMapVar, "chbAutoAlign", false);
                    if (wBoard == null || !wChbAutoAlign) {
                        wTabQuantiy = Common.showPopUpReceiveVar(TITLE_POPUP, TITLE_INPUT, INIT_VALUE);

                        // Dừng xử lí nếu người dùng nhấn cancel
                        if (StringUtils.isBlank(wTabQuantiy)) return;

                        wIsAutoFormat = "-1".equals(wTabQuantiy);

                    } else {
                        wTabQuantiy = (String) wMapVar.get("txtTabQuantity");
                        wIsAutoFormat = wChbAutoAlign;
                    }

                    // Chia đoạn văn bản thành danh sách các dòng
                    String[] lines = wSelectedText.split("\\R", -1);
                    int maxCodeLength = 0;

                    // Tìm vị trí (cột) xa nhất của phần code trước dấu "//"
                    for (String line : lines) {
                        if (line.contains("//") && !StringUtils.equals(StringUtils.left(StringUtils.trimToEmpty(line), 2), "//")) {
                            int commentIndex = line.indexOf("//");
                            // Lấy phần code trước comment và cắt bỏ khoảng trắng thừa ở cuối
                            String codePart = line.substring(0, commentIndex).stripTrailing();
                            maxCodeLength = Math.max(maxCodeLength, codePart.length());
                        }
                    }

                    int mostFrequentPosition = maxCodeLength;
                    int wTab = Integer.parseInt(wTabQuantiy);
                    int wSpaceQ = 0;

                    // Trường hợp tự động format
                    if (wIsAutoFormat) {
                        List<Integer> wCommentPositions = new ArrayList<>();
                        Map<Integer, Integer> wCommentPositionMap = new HashMap<>();

                        // Thống kê số comment có cùng vị trí
                        for (String wLine : lines) {
                            int wCommentIndex = wLine.indexOf("//");
                            if (wCommentIndex > maxCodeLength && wLine.contains("//") && !StringUtils.equals(StringUtils.left(StringUtils.trimToEmpty(wLine), 2), "//")) {
                                if (wCommentPositions.contains(wCommentIndex)) {
                                    int wExistingCount = wCommentPositionMap.get(wCommentIndex);
                                    wCommentPositionMap.put(wCommentIndex, wExistingCount + 1);
                                } else {
                                    wCommentPositions.add(wCommentIndex);
                                    wCommentPositionMap.put(wCommentIndex, 1);
                                    
                                }
                            }
                        }

                        // Tìm vị trí comment có số lượng nhiều nhất
                        int highestCount = 0;
                        for (Map.Entry<Integer, Integer> entry : wCommentPositionMap.entrySet()) {
                            if (entry.getValue() > highestCount) {
                                highestCount = entry.getValue();
                                mostFrequentPosition = entry.getKey();
                            }
                        }   
                    } else {
                        wSpaceQ = wTab * 4; // Quy đổi tab ra space (1 tab = 4 space)
                    }

                    // Xây dựng lại các dòng đã được căn lề
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        if (line.contains("//") && !StringUtils.equals(StringUtils.left(StringUtils.trimToEmpty(line), 2), "//")) {
                            int commentIndex = line.indexOf("//");
                            String codePart = line.substring(0, commentIndex).stripTrailing();
                            String commentPart = line.substring(commentIndex);

                            // Trường hợp tự động format thì tính toán lại khoảng cách
                            if (wIsAutoFormat) {
                                // Thêm khoảng trắng để đẩy comment ra vị trí chỉ định
                                sb.append(StringUtils.rightPad(codePart, mostFrequentPosition));
                            } else {
                                // Thêm khoảng trắng để đẩy comment ra vị trí chỉ định
                                sb.append(StringUtils.rightPad(codePart, maxCodeLength + wSpaceQ));
                            }

                            sb.append(commentPart);
                        } else if (!StringUtils.isBlank(StringUtils.trimToEmpty(line))) {
                            sb.append(line);
                        }

                        // Thêm dấu xuống dòng nếu không phải dòng cuối cùng
                        if (i < lines.length - 1) {
                            sb.append("\n");
                        }
                    }

                    String newText = sb.toString();

                    // Gán lại vào Editor (Atomic để có thể Undo)
                    StyledDocument doc = cookie.getDocument();
                    if (doc != null) {
                        try {
                            NbDocument.runAtomicAsUser(doc, () -> {
                                try {
                                    int start = editor.getSelectionStart();
                                    int end = editor.getSelectionEnd();
                                    doc.remove(start, end - start);
                                    doc.insertString(start, newText, null);
                                } catch (BadLocationException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            });
                        } catch (BadLocationException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }

                    // Hiển thị thông báo thành công
                    Icon infoIcon = ImageUtilities.loadImageIcon(Common.getPathImageIcon("Point"), true);

                    String wContentNoti = StringUtils.EMPTY;
                    if (MapUtils.isNotEmpty(wIndexStaEnd)) {
                        int wIdxSta = (int) wIndexStaEnd.get("sta");
                        int wIdxEnd = (int) wIndexStaEnd.get("end");

                        if (wIdxSta == wIdxEnd) {
                            wContentNoti = StringUtils.join("Đã căn thẳng hàng các comment dòng ", wIdxSta);
                        } else if (wIdxSta > wIdxEnd) {
                            wContentNoti = StringUtils.join("Đã căn thẳng hàng các comment từ dòng ", wIdxEnd, " -> ", wIdxSta);
                        } else {
                            wContentNoti = StringUtils.join("Đã căn thẳng hàng các comment từ dòng ", wIdxSta, " -> ", wIdxEnd);
                        }
                    }

                    Common.showNoti(infoIcon, "Format thành công", wContentNoti);
                } else {
                    JOptionPane.showMessageDialog(null, "Vui lòng bôi đen đoạn code có chứa comment!");
                }
            }
        }
    }
}
