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
public class SQLToJavaNew {
    public static String convert(String sqlQuery, String varNameSB) {
        List<String> javaCode = new ArrayList<>();
        String wVarNameSB = StringUtils.isNotBlank(varNameSB) ? varNameSB : "wSqlstr";

        javaCode.add("StringBuilder " + wVarNameSB + " = new StringBuilder();");
        javaCode.add("");
        String[] sql = StringUtils.split(sqlQuery, "\n");

        List<String> wSQLLines = Arrays.asList(sql).stream().filter((x) -> StringUtils.isNotBlank(x)).collect(Collectors.toList());

        for (String line : wSQLLines) {
            StringBuilder buildNewLine = new StringBuilder();

            if (line.indexOf("--") > 0) {
                buildNewLine.append(wVarNameSB).append(".append(\" ");
                buildNewLine.append(StringUtils.stripEnd(StringUtils.stripEnd(StringUtils.substringBefore(line, "--"), "\t"), " "));
                buildNewLine.append(" \");");
                buildNewLine.append("// ");
                buildNewLine.append(StringUtils.trim(StringUtils.substringAfter(line, "--").replaceAll("\t", "")));
            } else {
                buildNewLine.append(wVarNameSB).append(".append(\" ");
                buildNewLine.append(StringUtils.stripEnd(StringUtils.stripEnd(line, "\t"), " "));
                buildNewLine.append(" \");");
            }

            javaCode.add(buildNewLine.toString());
        }
        javaCode.add("");
        javaCode.add("return " + wVarNameSB + ".toString();");

        return javaCode.stream().collect(Collectors.joining("\r\n"));
    }
}
