package com.github.tankist88.object2source.util;

import com.github.tankist88.object2source.dto.ProviderInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.*;
import static java.lang.reflect.Modifier.isPublic;

public class AssigmentUtil {
    private static final String COMMON_METHODS_FILE = "common-methods.txt";
    private static final String COMMON_METHODS_SPLITTER = "-----------------";
    private static final String TAB_PLACEHOLDER = "<tab>";

    public static final String VAR_NAME_PLACEHOLDER = "<var_name>";

    public static String getCalendarInstanceMethod(String timeZoneId, String mTime, String className) {
        String classAppend = className != null ? className + "." : "";
        return classAppend + "getCalendarInstance(<tzId>, <mTime>)"
                .replace("<tzId>", timeZoneId)
                .replace("<mTime>", mTime);
    }

    public static String getFieldNotPublicAssignment(Object obj, String fieldName, String value, String className) {
        String classAppend = className != null ? className + "." : "";
        return classAppend + "notPublicAssignment(<obj>, <fName>, <value>)"
                .replace("<obj>", getInstName(obj.getClass()))
                .replace("<fName>", "\"" + fieldName + "\"")
                .replace("<value>", value);
    }

    public static boolean hasZeroArgConstructor(Class clazz, boolean onlyPublic) {
        if(clazz.getDeclaredConstructors().length == 0) return true;
        for (Constructor c : clazz.getDeclaredConstructors()) {
            if (c.getParameterTypes().length == 0) {
                return !onlyPublic || isPublic(c.getModifiers());
            }
        }
        return false;
    }

    public static String getConstructorCall(Class instType, String commonClassName) {
        return getConstructorCall(instType, instType, commonClassName);
    }

    public static String getConstructorCall(Class instType, Class retType, String commonClassName) {
        boolean noParamConstructorFound = hasZeroArgConstructor(instType, false);
        boolean publicConstructorFound = hasZeroArgConstructor(instType, true);

        String classAppend = commonClassName != null ? commonClassName + "." : "";
        String retTypeName = getClearedClassName(retType.getName());
        String instClearName = getClearedClassName(instType.getName());

        String result;
        if (noParamConstructorFound && publicConstructorFound && isPublic(instType.getModifiers())) {
            result = "new " + instClearName + "();";
        } else if (noParamConstructorFound && isPublic(instType.getModifiers())) {
            result = "(" + retTypeName + ") " + classAppend + "callConstructorReflection(<type>);".replace("<type>", instClearName + ".class");
        } else if (noParamConstructorFound) {
            result = "(" + retTypeName + ") " + classAppend + "callConstructorReflection(<type>);".replace("<type>", "Class.forName(\"" + instType.getName() + "\")");
        } else {
            result = "(" + retTypeName + ") " + classAppend + "newInstanceHard(<type>);".replace("<type>", "Class.forName(\"" + instType.getName() + "\")");
        }
        return result;
    }

    public static String getFieldAssignment(String tabSymb, Object obj, String fieldName, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(tabSymb).append(tabSymb)
                .append(getInstName(obj.getClass()))
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
                .append(getInstName(obj.getClass()))
                .append(".set")
                .append(upFirst(fieldName))
                .append("(")
                .append(value)
                .append(");\n");
        return sb.toString();
    }

    public static Set<ProviderInfo> getCommonMethods(String tabSymb) {
        Set<ProviderInfo> result = new HashSet<>();
        BufferedReader br = null;
        try {
            InputStream inputStream = AssigmentUtil.class.getClassLoader().getResourceAsStream(COMMON_METHODS_FILE);

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
