package com.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author datlt
 */
public class SQLToJava {
    private static final String INSERT_KEY = "INSERT";
    private static final String UPDATE_KEY = "UPDATE";

    private static final List<String> WHERE_KEYWORDS = Arrays.asList("=", "BETWEEN", "AND", "OR", "LIKE", "IN");

    private static final List<String> EQUAL_LIKE_IN = Arrays.asList("=", "LIKE", "IN");

    /**
     * BETWEEN
     */
    private static final String BETWEEN = "BETWEEN";

    public static String convert(String sqlQuery) {
        List<String> javaCode = new ArrayList<>();

        javaCode.add("StringBuilder wSqlstr = new StringBuilder();");
        String[] sql = StringUtils.split(sqlQuery, "\n");
        boolean isSelectQuery = true;

        if (sql[0].indexOf(INSERT_KEY) > 0 || sql[0].contains(UPDATE_KEY)) {
            isSelectQuery = false;
        }

        List<String> wSQLLines = Arrays.asList(sql).stream().filter((x) -> StringUtils.isNotBlank(x))
                .map((x) -> StringUtils.stripEnd(x, ""))
                .collect(Collectors.toList());

        List<String> params = new ArrayList<>();

        for (String line : wSQLLines) {
            String lineLowerCase = line.toLowerCase();

            StringBuilder buildNewLine = new StringBuilder();

            // T/h dieu kien where cua SQL duoc viet: = ?prm_cd
            if (lineLowerCase.indexOf("=") > 0 && lineLowerCase.indexOf("?") > 0) {
                String[] lineSplitSpace = StringUtils.split(line, " ");

                int idx = 0;
                for (String charInLine : lineSplitSpace) {
                    if (StringUtils.isBlank(charInLine)) {
                        buildNewLine.append(charInLine);
                        buildNewLine.append(" ");
                        idx++;
                        continue;
                    }
                    
                    if (charInLine.contains("?")) {
                        String pField = " ";

                        // t/h chi co dat ? lam parameter
                        if (charInLine.length() == 1 && (idx + 1) == lineSplitSpace.length) {
                            int check = idx - 1;
                            while (true) {
                                String pCheckKey = lineSplitSpace[check];

                                if (!EQUAL_LIKE_IN.contains(pCheckKey) && StringUtils.isNotBlank(pCheckKey)) {
                                    pField = pCheckKey;
                                    break;
                                }
                                check--;
                            }

                            if (pField.lastIndexOf(".") >= 0) {
                                pField = StringUtils.substring(pField, pField.lastIndexOf("."), pField.length() - pField.lastIndexOf("."));
                                buildNewLine.append(String.format(" ?%s", pField));
                            }

                            // t/h chi dat ? lam param va sau co co them dieu kien where khac nua
                        } else if (charInLine.length() == 1 && WHERE_KEYWORDS.contains(lineSplitSpace[idx + 1])) {
                            int check = idx - 1;
                            while (true) {
                                String pCheckKey = lineSplitSpace[check];

                                if (!EQUAL_LIKE_IN.contains(pCheckKey) && StringUtils.isNotBlank(pCheckKey)) {
                                    pField = pCheckKey;
                                    break;
                                }
                                check--;
                            }

                            if (pField.lastIndexOf(".") >= 0) {
                                pField = StringUtils.substring(pField, pField.lastIndexOf("."), pField.length() - pField.lastIndexOf("."));
                                buildNewLine.append(String.format(" ?%s", pField));
                            }

                        } else if (charInLine.length() == 2 && charInLine.contains(",")) {
                            int check = idx - 1;
                            while (true) {
                                String pCheckKey = lineSplitSpace[check];

                                if (!EQUAL_LIKE_IN.contains(pCheckKey) && StringUtils.isNotBlank(pCheckKey)) {
                                    pField = pCheckKey;
                                    break;
                                }
                                check--;
                            }

                            if (pField.lastIndexOf(".") >= 0) {
                                pField = StringUtils.substring(pField, pField.lastIndexOf("."), pField.length() - pField.lastIndexOf("."));
                                buildNewLine.append(String.format(" ?%s,", pField));
                            }
                        } else if (charInLine.length() > 1) {
                            pField = StringUtils.trim(StringUtils.replace(charInLine, "?", ""));
                            buildNewLine.append(StringUtils.stripEnd(StringUtils.substringBefore(charInLine, "--").replaceAll("\t", "    "), " "));
                        }
                        params.add(StringUtils.stripEnd(StringUtils.substringBefore(pField, "--"), "\t"));
                    } else {
                        buildNewLine.append(charInLine);
                    }

                    idx++;
                }
               
            // T/h dieu kien where cua SQL duoc viet: {0}
            } else if (lineLowerCase.indexOf("{") > 0 && lineLowerCase.indexOf("}") > 0) {

                String[] lineSplitSpace = StringUtils.split(line, "{");
                StringBuilder sqlInline = new StringBuilder().append(StringUtils.stripEnd(lineSplitSpace[0], " "));

                int edWhere = lineLowerCase.indexOf("}");
                String afterCondition = StringUtils.trim(StringUtils.substring(line, edWhere + 1));

                if (afterCondition.contains(BETWEEN) && !(afterCondition.indexOf("{") > 0 && lineLowerCase.indexOf("{") > 0)) {

                    afterCondition = StringUtils.substringBefore(afterCondition, "--");
                    buildNewLine.append(sqlInline).append(" ?xxxxxxxxx ").append(afterCondition.replaceAll("\t", ""));
                    params.add("xxxxxxxxx");

                } else {
                    String[] lineSplitSpace2 = StringUtils.split(lineSplitSpace[0], " ");

                    int idx = 0;
                    String pField = " ";

                    for (String charInLine : lineSplitSpace2) {
                        if (StringUtils.isBlank(charInLine)) {
                            idx++;
                            continue;
                        }
                        if (EQUAL_LIKE_IN.contains(charInLine)) {
                            int check = idx - 1;
                            while (true) {
                                String pCheckKey = lineSplitSpace2[check];

                                if (!EQUAL_LIKE_IN.contains(pCheckKey) && StringUtils.isNotBlank(pCheckKey)) {
                                    pField = pCheckKey;
                                    break;
                                }
                                check--;
                            }
                            pField = StringUtils.substring(pField, pField.lastIndexOf(".") + 1);
                        }
                        idx++;
                    }

                    buildNewLine.append(sqlInline).append(" ?").append(pField);
                    params.add(pField);
                }

            } else {
                String sqlStr = line;
                if (line.indexOf("--") > 0) {
                    sqlStr = StringUtils.substring(line, 0, line.lastIndexOf("--"));
                }
                buildNewLine.append(sqlStr.replaceAll("\\s+$", "").replaceAll("\t", "    "));
            }

            // Them comment (neu co)
            String lineComment = "";
            if (line.indexOf("--") > 0) {
                lineComment = StringUtils.substringAfter(line, "--");
                if (StringUtils.isNotBlank(lineComment)) {
                    lineComment = "// " + StringUtils.trim(lineComment.replaceAll("\t", ""));
                }
            }

            javaCode.add(String.format("wSqlstr.append(\" %s \"); %s", buildNewLine.toString(), StringUtils.trim(lineComment)));
        }

        javaCode.add("");
        javaCode.add("Query wQuery = getEntityManager().createNativeQuery(wSqlstr.toString());");
        if (isSelectQuery) {
            javaCode.add("wQuery.setHint(QueryHints.RESULT_TYPE, ResultType.Map);");
        }

        javaCode.add("");

        List<String> paramFilter = params.stream().distinct().collect(Collectors.toList());

        for (String param : paramFilter) {
            param = StringUtils.trim(param);
            javaCode.add(String.format("wQuery.setParameter(\"%s\", null);", param));
        }

        javaCode.add("");
        javaCode.add("// **********************************************");
        javaCode.add("// ログ出力処理(L4)  SQL出力");
        javaCode.add("// **********************************************");
        javaCode.add("Cmn.InsertLog(getEntityManager(), pUser, Cmn.Log_Syusyu_LV.L4, Cmn.Kino_LV.L3, pKinoCd, wQuery.toString());");
        javaCode.add("");
        javaCode.add("// **********************************************");
        javaCode.add("// SQL実行");
        javaCode.add("// **********************************************");

        if (isSelectQuery) {
            javaCode.add("return wQuery.getResultList();");
        } else {
            javaCode.add("return wQuery.executeUpdate();");
        }

        return javaCode.stream().collect(Collectors.joining("\r\n"));
    }
}
