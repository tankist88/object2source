package com.github.tankist88.object2source;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.dto.ProviderResult;
import com.github.tankist88.object2source.exception.ObjectDepthExceededException;
import com.github.tankist88.object2source.extension.EmbeddedExtension;
import com.github.tankist88.object2source.extension.Extension;
import com.github.tankist88.object2source.extension.arrays.ArraysExtension;
import com.github.tankist88.object2source.extension.collections.*;
import com.github.tankist88.object2source.extension.maps.BaseMapsExtension;
import com.github.tankist88.object2source.extension.maps.EmptyMapExtension;
import com.github.tankist88.object2source.extension.maps.UnmodMapExtension;
import com.github.tankist88.object2source.extension.maps.UnmodSortedMapExtension;
import com.github.tankist88.object2source.util.AssigmentUtil;
import com.github.tankist88.object2source.util.GenerationUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.reflect.Modifier.*;

public class SourceGenerator implements TypeGenerator {
    static final int DEFAULT_MAX_DEPTH = 10;

    private Set<String> allowedPackages;
    private int maxObjectDepth;
    private String commonMethodsClassName;
    private Set<ProviderInfo> commonMethods;
    private String tabSymb;
    private boolean exceptionWhenMaxODepth;

    private ArrayList<Extension> extensions;
    private Set<String> extensionClasses;

    public SourceGenerator() {
        this("    ", new HashSet<String>());
    }
    public SourceGenerator(String tabSymb) {
        this(tabSymb, new HashSet<String>());
    }
    public SourceGenerator(String tabSymb, Set<String> allowedPackages) {
        this(tabSymb, allowedPackages, null);
    }
    public SourceGenerator(String tabSymb, Set<String> allowedPackages, String commonMethodsClassName) {
        this(tabSymb, allowedPackages, commonMethodsClassName, true);
    }
    public SourceGenerator(String tabSymb, Set<String> allowedPackages, String commonMethodsClassName, boolean exceptionWhenMaxODepth) {
        this.tabSymb = tabSymb;
        this.allowedPackages = allowedPackages;
        this.maxObjectDepth = DEFAULT_MAX_DEPTH;
        this.exceptionWhenMaxODepth = exceptionWhenMaxODepth;
        this.commonMethodsClassName = commonMethodsClassName;
        this.commonMethods = AssigmentUtil.getCommonMethods(tabSymb);
        this.extensions =  new ArrayList<>();
        this.extensionClasses = new HashSet<>();
        initEmbeddedExtensions();
    }

    private void initEmbeddedExtensions() {
        registerExtension(new BaseMapsExtension());
        registerExtension(new UnmodMapExtension());
        registerExtension(new UnmodSortedMapExtension());
        registerExtension(new EmptyMapExtension());
        registerExtension(new BaseCollectionsExtension());
        registerExtension(new SingletonListExtension());
        registerExtension(new UnmodCollectionExtension());
        registerExtension(new EmptySetExtension());
        registerExtension(new EmptyListExtension());
        registerExtension(new ArraysArrayListExtension());
        registerExtension(new ArraysExtension());
    }

    public boolean isExceptionWhenMaxODepth() {
        return exceptionWhenMaxODepth;
    }

    public void setExceptionWhenMaxODepth(boolean exceptionWhenMaxODepth) {
        this.exceptionWhenMaxODepth = exceptionWhenMaxODepth;
    }

    public String getCommonMethodsClassName() {
        return commonMethodsClassName;
    }

    public String getTabSymb() {
        return tabSymb;
    }

    public int getMaxObjectDepth() {
        return maxObjectDepth;
    }

    public void setMaxObjectDepth(int maxObjectDepth) {
        this.maxObjectDepth = maxObjectDepth;
    }

    private InstanceCreateData generateObjInstance(Object obj, List<Class> classHierarchy, int objectDepth) throws Exception {
        if (objectDepth <= 0 && exceptionWhenMaxODepth) {
            throw new ObjectDepthExceededException("Object depth exceeded. " + obj.getClass());
        } else if (objectDepth <= 0 || obj == null || !allowedType(obj.getClass())) {
            return new InstanceCreateData(tabSymb + "return null;\n");
        }

        InstanceCreateData result = new InstanceCreateData();
        StringBuilder instBuilder = new StringBuilder();

        Class<?> clazz = obj.getClass();

        InstanceCreateData simpleInstance = getInstanceCreateData(obj, true, objectDepth);
        if (simpleInstance != null) {
            instBuilder.append(tabSymb).append("return ").append(simpleInstance.getInstanceCreator()).append(";\n");
        } else {
            instBuilder.append(tabSymb).append(GenerationUtil.createInstStr(clazz, commonMethodsClassName)).append("\n");
            List<Method> allMethods = GenerationUtil.getAllMethodsOfClass(classHierarchy);
            for (Field field : GenerationUtil.getAllFieldsOfClass(classHierarchy)) {
                int fieldModifiers = field.getModifiers();
                boolean deniedModifier = isStatic(fieldModifiers) || isNative(fieldModifiers);
                Object fieldValue = null;
                if (    deniedModifier || !allowedType(field.getType()) ||
                        (!field.getType().isPrimitive() && (fieldValue = GenerationUtil.getFieldValue(field, obj)) == null))
                {
                    continue;
                }
                if (fieldValue == null) fieldValue = GenerationUtil.getFieldValue(field, obj);
                InstanceCreateData instData = getInstanceCreateData(fieldValue, false, objectDepth);
                if (instData == null) continue;
                String fieldName = field.getName();
                if (!isPublic(clazz.getModifiers()) || GenerationUtil.setterNotExists(fieldName, field, allMethods)) {
                    if (isPublic(fieldModifiers) && !isFinal(fieldModifiers)) {
                        instBuilder.append(AssigmentUtil.getFieldAssignment(tabSymb, obj, fieldName, instData.getInstanceCreator()));
                    } else {
                        instBuilder .append(tabSymb).append(tabSymb)
                                    .append(AssigmentUtil.getFieldNotPublicAssignment(obj, fieldName, instData.getInstanceCreator(), commonMethodsClassName))
                                    .append(";\n");
                    }
                } else {
                    instBuilder.append(AssigmentUtil.getFieldSetter(tabSymb, obj, fieldName, instData.getInstanceCreator()));
                }
                result.getDataProviderMethods().addAll(instData.getDataProviderMethods());
            }
            instBuilder.append(tabSymb).append(tabSymb).append("return ").append(GenerationUtil.getInstName(clazz)).append(";\n");
        }
        result.setInstanceCreator(instBuilder.toString());
        return result;
    }

    public InstanceCreateData getInstanceCreateData(Object obj, int objectDepth) throws Exception {
        return getInstanceCreateData(obj, false, objectDepth);
    }
    private InstanceCreateData getInstanceCreateData(Object obj, boolean onlySimple, int objectDepth) throws Exception {
        if (obj == null) return new InstanceCreateData("null");
        InstanceCreateData result = null;
        Class<?> clazz = obj.getClass();
        if (clazz.equals(boolean.class) || clazz.equals(Boolean.class)) {
            result = new InstanceCreateData(Boolean.valueOf(obj.toString()).toString());
        } else if (clazz.equals(int.class) || clazz.equals(Integer.class)) {
            result = new InstanceCreateData(Integer.valueOf(obj.toString()).toString());
        } else if (clazz.equals(long.class) || clazz.equals(Long.class)) {
            result = new InstanceCreateData(Long.valueOf(obj.toString()).toString() + "L");
        } else if (clazz.equals(double.class) || clazz.equals(Double.class)) {
            result = new InstanceCreateData(Double.valueOf(obj.toString()).toString() + "d");
        } else if (clazz.equals(float.class) || clazz.equals(Float.class)) {
            result = new InstanceCreateData(Float.valueOf(obj.toString()).toString() + "f");
        } else if (clazz.equals(char.class) || clazz.equals(Character.class)) {
            result = new InstanceCreateData("'" +
                    Character.toString(obj.toString().charAt(0))
                            .replaceAll("([\\\\])", GenerationUtil.ESCAPE_STRING_REPLACE)
                            .replaceAll("\r", "\\\\r")
                            .replaceAll("\n", "\\\\n")
                            .replaceAll("\t", "\\\\t")
                    + "'");
        } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
            result = new InstanceCreateData("(short) " + Short.valueOf(obj.toString()).toString());
        } else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
            result = new InstanceCreateData("(byte) " + Byte.valueOf(obj.toString()).toString());
        } else if (clazz.equals(String.class)) {
            String escapedVal = obj.toString()
                    .replaceAll(GenerationUtil.ESCAPE_STRING_REGEX, GenerationUtil.ESCAPE_STRING_REPLACE)
                    .replaceAll("\r", "\\\\r")
                    .replaceAll("\n", "\\\\n")
                    .replaceAll("\t", "\\\\t");
            result = new InstanceCreateData("\"" + escapedVal + "\"");
        } else if (clazz.equals(java.math.BigDecimal.class)) {
            java.math.BigDecimal val = (java.math.BigDecimal) obj;
            result = new InstanceCreateData("new java.math.BigDecimal(" + val + "d)");
        } else if (clazz.equals(java.math.BigInteger.class)) {
            java.math.BigInteger val = (java.math.BigInteger) obj;
            result = new InstanceCreateData("new java.math.BigInteger(\"" + val + "\")");
        } else if (clazz.equals(java.sql.Timestamp.class)) {
            java.sql.Timestamp val = (java.sql.Timestamp) obj;
            result = new InstanceCreateData("new java.sql.Timestamp(" + val.getTime() + "L)");
        } else if (clazz.equals(java.sql.Time.class)) {
            java.sql.Time val = (java.sql.Time) obj;
            result = new InstanceCreateData("new java.sql.Time(" + val.getTime() + "L)");
        } else if (clazz.equals(java.sql.Date.class)) {
            java.sql.Date val = (java.sql.Date) obj;
            result = new InstanceCreateData("new java.sql.Date(" + val.getTime() + "L)");
        } else if (clazz.equals(java.util.Date.class)) {
            java.util.Date val = (java.util.Date) obj;
            result = new InstanceCreateData("new java.util.Date(" + val.getTime() + "L)");
        } else if (clazz.equals(java.util.UUID.class)) {
            java.util.UUID val = (java.util.UUID) obj;
            result = new InstanceCreateData("new java.util.UUID(" +
                    val.getMostSignificantBits() + "L" +
                    ", " +
                    val.getLeastSignificantBits() + "L)");
        } else if (GenerationUtil.getClassHierarchyStr(clazz).contains(java.util.Calendar.class.getName())) {
            java.util.Calendar val = (java.util.Calendar) obj;
            String methodCall = AssigmentUtil.getCalendarInstanceMethod(
                    "\"" + val.getTimeZone().getID() + "\"",
                    val.getTimeInMillis() + "L",
                    commonMethodsClassName
            );
            result = new InstanceCreateData(methodCall);
        } else if (GenerationUtil.getClassHierarchyStr(clazz).contains(java.util.TimeZone.class.getName())) {
            java.util.TimeZone val = (java.util.TimeZone) obj;
            result = new InstanceCreateData("(" + clazz.getName() + ") java.util.TimeZone.getTimeZone(\"" + val.getID() + "\")");
        } else if (clazz.isEnum()) {
            Enum val = (Enum) obj;
            String enumType = GenerationUtil.getClearedClassName(clazz.getName());
            result = new InstanceCreateData(enumType + "." + val.name());
        } else if (!onlySimple && allowedType(clazz)) {
            String fieldName = clazz.isArray() ? "array" : GenerationUtil.getInstName(clazz.getName(), false);
            ProviderResult providerResult = createDataProviderMethod(obj, fieldName, objectDepth);
            result = new InstanceCreateData(providerResult.getEndPoint().getMethodName());
            result.getDataProviderMethods().addAll(providerResult.getProviders());
        } else if (!onlySimple) {
            result = new InstanceCreateData("null");
        }
        return result;
    }

    @Override
    public ProviderResult createDataProviderMethod(Object obj) {
        if (obj == null || !allowedType(obj.getClass())) return null;
        boolean anonymousClass = GenerationUtil.getLastClassShort(obj.getClass().getName()).matches("\\d+");
        if(anonymousClass) return null;
        Class<?> clazz = obj.getClass();
        String fieldName = clazz.isArray() ? "array" : GenerationUtil.getInstName(clazz.getName(), false);
        int objectDepth = maxObjectDepth;
        try {
            return createDataProviderMethod(obj, fieldName, objectDepth);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private ProviderResult createDataProviderMethod(Object obj, String fieldName, int objectDepth) throws Exception {
        Set<ProviderInfo> providers = new HashSet<>();

        objectDepth--;

        StringBuilder bodyBuilder = new StringBuilder();

        Class<?> clazz = obj.getClass();

        Extension extension = findExtension(clazz);
        if (extension != null) {
            extension.fillMethodBody(bodyBuilder, providers, objectDepth, obj);
        } else {
            fillMethodBody(obj, bodyBuilder, providers, GenerationUtil.getClassHierarchy(clazz), objectDepth);
        }

        Class<?> actClass = !isPublic(clazz.getModifiers()) ? GenerationUtil.getFirstPublicType(clazz) : clazz;
        String typeName = extension != null ? extension.getActualType(obj) : actClass.getName();
        String methodBody = bodyBuilder.toString();
        String providerMethodName = GenerationUtil.getDataProviderMethodName(fieldName, methodBody.hashCode());


        String method = tabSymb + "public static " + GenerationUtil.getClearedClassName(typeName) + " " +
                        providerMethodName + " throws Exception {\n" + methodBody + tabSymb + "}\n";

        ProviderResult result = new ProviderResult();
        result.setEndPoint(new ProviderInfo(providerMethodName, method));
        result.setProviders(providers);
        result.getProviders().add(result.getEndPoint());
        if (commonMethodsClassName == null) {
            result.getProviders().addAll(commonMethods);
        }

        return result;
    }

    public Set<String> getAllowedPackages() {
        if (allowedPackages == null) allowedPackages = new HashSet<>();
        return allowedPackages;
    }

    boolean allowedType(Class<?> clazz) {
        if(allowedPackages == null || allowedPackages.size() == 0 || clazz.isPrimitive()) return true;
        for (String p : allowedPackages) {
            if (clazz.getName().startsWith(p)) return true;
        }
        return false;
    }

    private void fillMethodBody(Object obj, StringBuilder bb, Set<ProviderInfo> result, List<Class> classHierarchy, int objectDepth) throws Exception {
        InstanceCreateData objGenerateResult = generateObjInstance(obj, classHierarchy, objectDepth);
        bb.append(tabSymb).append(objGenerateResult.getInstanceCreator());
        result.addAll(objGenerateResult.getDataProviderMethods());
    }

    @Override
    public void registerExtension(Extension extension) {
        if (extension instanceof EmbeddedExtension) {
            ((EmbeddedExtension) extension).setSourceGenerator(this);
        }
        if (!extensionClasses.contains(extension.getClass().getName())) {
            extensions.add(0, extension);
            extensionClasses.add(extension.getClass().getName());
        }
    }

    @Override
    public Extension findExtension(Class clazz) {
        for (Extension ext : extensions) {
            if (ext.isTypeSupported(clazz)) return ext;
        }
        return null;
    }
}
