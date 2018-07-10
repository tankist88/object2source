package org.object2source.util;

import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.*;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.replace;
import static org.apache.commons.lang3.StringUtils.replaceEach;
import static org.object2source.util.AssigmentUtil.getConstructorCall;

public class GenerationUtil {
    public static final String ESCAPE_STRING_REGEX = "([\"\\\\])";
    public static final String ESCAPE_STRING_REPLACE = "\\\\$1";

    public static Object getFieldValue(Field field, Object fieldOwner) throws IllegalAccessException {
        boolean currentAccessible = field.isAccessible();
        field.setAccessible(true);
        Object fieldValue = field.get(fieldOwner);
        field.setAccessible(currentAccessible);
        return fieldValue;
    }

    public static List<String> getClassHierarchyStr(Class clazz){
        return getClassHierarchyStr(getClassHierarchy(clazz));
    }

    public static List<String> getClassHierarchyStr(List<Class> classHierarchy){
        List<String> result = new ArrayList<>();
        for(Class c : classHierarchy) result.add(c.getName());
        return result;
    }

    public static List<Class> getClassHierarchy(Class clazz){
        List<Class> classes = new ArrayList<>();
        for (Class c : ClassUtils.hierarchy(clazz)) {
            if(c.equals(Object.class)) continue;
            classes.add(c);
        }
        return classes;
    }

    public static List<String> getInterfacesHierarchyStr(Class clazz){
        List<String> result = new ArrayList<>();
        for(Class c : getInterfacesHierarchy(clazz)) {
            result.add(c.getName());
        }
        return result;
    }

    public static List<Class> getInterfacesHierarchy(Class<?> clazz) {
        List<Class> interfaces = new ArrayList<>();
        for (Class i : ClassUtils.hierarchy(clazz, ClassUtils.Interfaces.INCLUDE)) {
            if(i.isInterface()) {
                interfaces.add(i);
            }
        }
        return interfaces;
    }

    public static boolean setterNotExists(String fieldName, Field field, List<Method> allMethods) {
        List<Class> fieldClassHierarchy = getClassHierarchy(field.getType());
        for (Method m : allMethods) {
            if (Modifier.isPublic(m.getModifiers()) && m.getName().equals("set" + upFirst(fieldName))) {
                List<Class<?>> parameters = Arrays.asList(m.getParameterTypes());
                if (parameters.size() == 1 && fieldClassHierarchy.contains(parameters.get(0))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static String upFirst(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String downFirst(String str) {
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public static String getClearedClassName(String fullClassName) {
        return replaceEach(fullClassName, new String[] {"$", ";"}, new String[] {".", ""});
    }

    static String getFirstClassName(String fullClassName) {
        String[] arr = getClearedClassName(fullClassName).split("\\.");
        String className = arr[arr.length - 1];
        for (String part : arr) {
            if (part.length() > 0 && Character.isUpperCase(part.charAt(0))) {
                className = part;
                break;
            }
        }
        return className;
    }

    public static String getLastClassShort(String fullClassName) {
        String[] arr = getClearedClassName(fullClassName).split("\\.");
        return arr[arr.length - 1];
    }

    public static String getOwnerParentClass(String fullClassName) {
        if(!fullClassName.contains("$")) return fullClassName;
        String lastClassName = getLastClassShort(fullClassName);
        return fullClassName.substring(0, fullClassName.indexOf(lastClassName) - 1);
    }

    public static String getClassShort(String fullClassName) {
        String clearedClassName = getClearedClassName(fullClassName);
        String firstClassName = getFirstClassName(fullClassName);
        return clearedClassName.substring(clearedClassName.indexOf(firstClassName));
    }

    public static String getPackage(String fullClassName) {
        String clearedClassName = getClearedClassName(fullClassName);
        String firstClassName = getFirstClassName(fullClassName);
        return clearedClassName.substring(0, clearedClassName.indexOf(firstClassName) - 1);
    }

    public static Class getFirstPublicType(Class clazz) {
        for(Class c : getClassHierarchy(clazz)) {
            if(Modifier.isPublic(c.getModifiers())) return c;
        }
        List<Class> interfaces = getInterfacesHierarchy(clazz);
        if(interfaces.size() > 0) return getInterfacesHierarchy(clazz).get(0);
        else return Object.class;
    }

    public static String createInstStr(Class clazz, String commonMethodsClassName) {
        if(Modifier.isPublic(clazz.getModifiers())) {
            return createInstStr(clazz, clazz, commonMethodsClassName);
        } else {
            return createInstStr(getFirstPublicType(clazz), clazz, commonMethodsClassName);
        }
    }

    public static String createInstStr(Class clazz, Class instType, String commonMethodsClassName) {
        return  getClearedClassName(clazz.getName()) + " " +
                getInstName(instType) + " = " + getConstructorCall(instType, clazz, commonMethodsClassName);
    }

    public static String getInstName(Class clazz) {
        return getInstName(clazz.getName());
    }

    public static String getInstName(String className) {
        return getInstName(className, true);
    }

    public static String getInstName(String className, boolean underscore) {
        String shortClassName = getClassShort(className);
        StringBuilder instNameBuilder = new StringBuilder();
        if(underscore) instNameBuilder.append("_");
        instNameBuilder.append(replace(downFirst(shortClassName), ".", ""));
        return instNameBuilder.toString();
    }

    public static String getDataProviderMethodName(String fieldName, int code) {
        return "get" + replace(upFirst(fieldName), "$", "_") + "_" + replace(Integer.toString(code), "-", "_") + "()";
    }

    public static String createArrayElementString(String fieldName, String val, int index, String tabSymb) {
        return tabSymb + tabSymb + downFirst(fieldName) + "[" + index + "] = " + val + ";\n";
    }

    public static List<Field> getAllFieldsOfClass(List<Class> classHierarchy) {
        List<Field> allFields = new ArrayList<>();
        for(Class c : classHierarchy) {
            allFields.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return allFields;
    }

    public static List<Method> getAllMethodsOfClass(List<Class> classHierarchy) {
        List<Method> allMethods = new ArrayList<>();
        for(Class c : classHierarchy) {
            allMethods.addAll(Arrays.asList(c.getDeclaredMethods()));
        }
        return allMethods;
    }

    public static boolean isPrimitive(String className) {
        switch(className) {
            case "boolean" :
            case "int" :
            case "long" :
            case "double" :
            case "float" :
            case "char" :
            case "short" :
            case "byte" :
                return true;
            default: return false;
        }
    }

    public static boolean isWrapper(String className) {
        switch(className) {
            case "java.lang.Boolean" :
            case "java.lang.Integer" :
            case "java.lang.Long" :
            case "java.lang.Double" :
            case "java.lang.Float" :
            case "java.lang.Character" :
            case "java.lang.Short" :
            case "java.lang.Byte" :
                return true;
            default: return false;
        }
    }

    public static Class convertPrimitiveToWrapper(Class clazz) {
        if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            return Boolean.class;
        } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            return Integer.class;
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            return Long.class;
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            return Double.class;
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            return Float.class;
        } else if (clazz.equals(char.class) || clazz.equals(Character.class)) {
            return Character.class;
        } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
            return Short.class;
        } else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
            return Byte.class;
        } else {
            return clazz;
        }
    }

    public static String convertPrimitiveToWrapper(String classname) {
        if (boolean.class.getName().equals(classname)) {
            return Boolean.class.getName();
        } else if (int.class.getName().equals(classname)) {
            return Integer.class.getName();
        } else if (long.class.getName().equals(classname)) {
            return Long.class.getName();
        } else if (double.class.getName().equals(classname)) {
            return Double.class.getName();
        } else if (float.class.getName().equals(classname)) {
            return Float.class.getName();
        } else if (char.class.getName().equals(classname)) {
            return Character.class.getName();
        } else if (short.class.getName().equals(classname)) {
            return Short.class.getName();
        } else if (byte.class.getName().equals(classname)) {
            return Byte.class.getName();
        } else {
            return classname;
        }
    }

    public static Method getMethodByNameAndArgs(List<Class> classHierarchy, String methodName, Class<?>... args) throws NoSuchMethodException {
        for(Method m : getAllMethodsOfClass(classHierarchy)) {
            if(m.getName().equals(methodName)) {
                Class[] pTypes = m.getParameterTypes();
                if(pTypes.length != args.length) continue;
                if(pTypes.length == 0) return m;
                int countEquals = 0;
                for(int i = 0; i < pTypes.length; i++) {
                    Class argType = args[i].isPrimitive() ? convertPrimitiveToWrapper(args[i]) : args[i];
                    Class decType = pTypes[i].isPrimitive() ? convertPrimitiveToWrapper(pTypes[i]) : pTypes[i];

                    Set<String> argTypeHierarchy = new HashSet<>();
                    argTypeHierarchy.addAll(getClassHierarchyStr(argType));
                    argTypeHierarchy.add(Object.class.getName());
                    argTypeHierarchy.addAll(getInterfacesHierarchyStr(argType));

                    if(argTypeHierarchy.contains(decType.getName())) {
                        countEquals++;
                    }
                }
                if(countEquals == pTypes.length) return m;
            }
        }
        throw new NoSuchMethodException("Method " + methodName + " does not exists");
    }

    public static Class getMethodArgType(List<Class> classHierarchy, String methodName, int argNum, Class<?>... args) throws NoSuchMethodException {
        return getMethodByNameAndArgs(classHierarchy, methodName, args).getParameterTypes()[argNum];
    }

    public static List<Class> getMethodArgGenericTypes(List<Class> classHierarchy, String methodName, int argNum, Class<?>... args) throws NoSuchMethodException {
        return getMethodArgGenericTypes(getMethodByNameAndArgs(classHierarchy, methodName, args), argNum);
    }

    public static List<Class> getMethodArgGenericTypes(Method method, int argNum) {
        Type[] types = method.getGenericParameterTypes();
        if(types.length <= argNum) return new ArrayList<>();
        Type type = types[argNum];
        if(type instanceof ParameterizedType) {
            List<Class> result = getParameterizedTypes(type);
            if(result.size() > 0) {
                return result;
            } else {
                return getParameterizedTypes(method.getGenericReturnType());
            }
        } else {
            return new ArrayList<>();
        }
    }

    public static List<Class> getParameterizedTypes(Type type) {
        List<Class> result = new ArrayList<>();
        if(type instanceof ParameterizedType) {
            for (Type gt : ((ParameterizedType) type).getActualTypeArguments()) {
                if (gt instanceof Class) result.add((Class) gt);
            }
        }
        return result;
    }
}
