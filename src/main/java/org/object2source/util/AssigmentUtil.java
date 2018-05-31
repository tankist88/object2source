package org.object2source.util;

import org.object2source.dto.ProviderInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;

import static org.object2source.util.GenerationUtil.ESCAPE_STRING_REGEX;
import static org.object2source.util.GenerationUtil.ESCAPE_STRING_REPLACE;

public class AssigmentUtil {
    private static final String COMMON_METHODS_FILE = "common-methods.txt";
    private static final String COMMON_METHODS_SPLITTER = "-----------------";
    private static final String TAB_PLACEHOLDER = "<tab>";

    public static String getCalendarInstanceMethod(String timeZoneId, String mTime, String className) {
        String classAppend = className != null ? className + "." : "";
        return classAppend + "getCalendarInstance(<tzId>, <mTime>)"
                .replaceAll("<tzId>", timeZoneId)
                .replaceAll("<mTime>", mTime);
    }

    public static String getFieldNotPublicAssignment(Object obj, String fieldName, String value, String className) {
        String classAppend = className != null ? className + "." : "";
        try {
            return classAppend + "notPublicAssignment(<obj>, <fName>, <value>)"
                    .replaceAll("<obj>", GenerationUtil.getInstName(obj.getClass()))
                    .replaceAll("<fName>", "\"" + fieldName + "\"")
                    .replaceAll("<value>", value.replaceAll(ESCAPE_STRING_REGEX, ESCAPE_STRING_REPLACE));
        } catch (Exception ex) {
            return classAppend + "notPublicAssignment(<obj>, <fName>, <value>)"
                    .replaceAll("<obj>", GenerationUtil.getInstName(obj.getClass()))
                    .replaceAll("<fName>", "\"" + fieldName + "\"")
                    .replaceAll("<value>", "null");
        }
    }

    public static String getConstructorCall(Class instType, String commonClassName) {
        return getConstructorCall(instType, instType, commonClassName);
    }

    public static String getConstructorCall(Class instType, Class retType, String commonClassName) {
        boolean noParamConstructorFound = false;
        if(instType.getDeclaredConstructors().length > 0) {
            for (Constructor c : instType.getDeclaredConstructors()) {
                if(!Modifier.isPublic(c.getModifiers())) continue;
                if (c.getParameterTypes().length == 0) {
                    noParamConstructorFound = true;
                    break;
                }
            }
        } else {
            noParamConstructorFound = true;
        }
        String result;
        if(noParamConstructorFound && Modifier.isPublic(instType.getModifiers())) {
            result = "new " + instType.getName().replaceAll("\\$", ".") + "();";
        } else {
            String classAppend = commonClassName != null ? commonClassName + "." : "";
            String retTypeName = retType.getName().replaceAll("\\$", ".");
            result = "(" + retTypeName + ") " + classAppend + "newInstanceHard(<type>);".replace("<type>", "Class.forName(\"" + instType.getName() + "\")");
        }
        return result;
    }

    public static String getFieldAssignment(String tabSymb, Object obj, String fieldName, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(tabSymb).append(tabSymb)
                .append(GenerationUtil.getInstName(obj.getClass()))
                .append(".")
                .append(fieldName)
                .append(" = ")
                .append(value)
                .append(";\n");
        return sb.toString();
    }

    public static String getFieldSetter(String tabSymb, Object obj, String fieldName, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(tabSymb).append(tabSymb)
                .append(GenerationUtil.getInstName(obj.getClass()))
                .append(".set")
                .append(GenerationUtil.upFirst(fieldName))
                .append("(")
                .append(value)
                .append(");\n");
        return sb.toString();
    }

    public static Set<ProviderInfo> getCommonMethods(String tabSymb) {
        Set<ProviderInfo> result = new HashSet<>();
        BufferedReader br = null;
        try {
            InputStream inputStream = GenerationUtil.class.getClassLoader().getResourceAsStream(COMMON_METHODS_FILE);

            boolean methodName = false;
            boolean methodBody = false;
            StringBuilder sb = new StringBuilder();
            br = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
            String line;
            ProviderInfo info = new ProviderInfo();
            while ((line = br.readLine()) != null) {
                if (line.equals(COMMON_METHODS_SPLITTER) && !methodName && !methodBody) {
                    methodName = true;
                } else if (line.equals(COMMON_METHODS_SPLITTER) && methodName && !methodBody) {
                    methodName = false;
                    methodBody = true;
                } else if (line.equals(COMMON_METHODS_SPLITTER) && !methodName && methodBody) {
                    methodName = true;
                    methodBody = false;
                    info.setMethodBody(sb.toString());
                    result.add(info);
                } else if (!line.equals(COMMON_METHODS_SPLITTER) && methodName && !methodBody) {
                    info = new ProviderInfo();
                    sb = new StringBuilder();
                    info.setMethodName(line.trim());
                } else if (!line.equals(COMMON_METHODS_SPLITTER) && !methodName && methodBody) {
                    sb.append(line.replaceAll(TAB_PLACEHOLDER, tabSymb)).append("\n");
                }
            }
        } catch (IOException ioex) {
            ioex.printStackTrace();
        } finally {
            if(br != null) {
                try {
                    br.close();
                } catch (IOException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }

        return result;
    }
}
