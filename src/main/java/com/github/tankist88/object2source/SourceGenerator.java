package com.github.tankist88.object2source;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.dto.ProviderResult;
import com.github.tankist88.object2source.exception.FillingNotSupportedException;
import com.github.tankist88.object2source.exception.ObjectDepthExceededException;
import com.github.tankist88.object2source.extension.DynamicProxyExtension;
import com.github.tankist88.object2source.extension.EmbeddedExtension;
import com.github.tankist88.object2source.extension.Extension;
import com.github.tankist88.object2source.extension.arrays.ArraysExtension;
import com.github.tankist88.object2source.extension.collections.*;
import com.github.tankist88.object2source.extension.maps.BaseMapsExtension;
import com.github.tankist88.object2source.extension.maps.EmptyMapExtension;
import com.github.tankist88.object2source.extension.maps.UnmodMapExtension;
import com.github.tankist88.object2source.extension.maps.UnmodSortedMapExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.github.tankist88.object2source.util.AssigmentUtil.*;
import static com.github.tankist88.object2source.util.ExtensionUtil.getCanonicalTypeName;
import static com.github.tankist88.object2source.util.GenerationUtil.*;
import static java.lang.reflect.Modifier.*;
import static org.apache.commons.lang3.StringUtils.isBlank;

public class SourceGenerator implements CreateTypeGenerator, FillTypeGenerator {
    static final int DEFAULT_MAX_DEPTH = 10;
    static final int DEFAULT_BYTE_ARRAY_MAX_LENGTH = -1;

    private Set<String> allowedPackages;
    private int maxObjectDepth;
    private String commonMethodsClassName;
    private Set<ProviderInfo> commonMethods;
    private String tabSymb;
    private boolean exceptionWhenMaxODepth;
    private ArrayList<Extension> extensions;
    private Set<String> extensionClasses;
    private int byteArrayMaxLength;

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
        this.commonMethods = getCommonMethods(tabSymb);
        this.extensions =  new ArrayList<Extension>();
        this.extensionClasses = new HashSet<String>();
        this.byteArrayMaxLength = DEFAULT_BYTE_ARRAY_MAX_LENGTH;
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
        registerExtension(new ArraysExtension(byteArrayMaxLength));
        registerExtension(new DynamicProxyExtension());
    }

    @Override
    public ProviderResult createDataProviderMethod(Object obj) {
        try {
            return createDataProviderMethod(obj, false);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public ProviderResult createFillObjectMethod(Object obj) throws FillingNotSupportedException {
        return createDataProviderMethod(obj, true);
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
            if (ext.isTypeSupported(clazz) && ext.isFillingSupported()) return ext;
        }
        for (Extension ext : extensions) {
            if (ext.isTypeSupported(clazz)) return ext;
        }
        return null;
    }

    private InstanceCreateData generateObjInstance(Object obj, List<Class> classHierarchy, int objectDepth, boolean createInst) throws Exception {
        if (objectDepth <= 0 && exceptionWhenMaxODepth) {
            throw new ObjectDepthExceededException("Object depth exceeded. " + obj.getClass());
        } else if (objectDepth <= 0 || obj == null || !allowedType(obj.getClass())) {
            return new InstanceCreateData(tabSymb + tabSymb + "return null;\n");
        }
        InstanceCreateData result = new InstanceCreateData();
        StringBuilder instBuilder = new StringBuilder();
        Class<?> clazz = obj.getClass();
        InstanceCreateData simpleInstance = getInstanceCreateData(obj, true, objectDepth);
        if (simpleInstance != null) {
            fillSimpleInstance(simpleInstance, createInst, instBuilder);
        } else if (createInst) {
            instBuilder.append(tabSymb).append(tabSymb).append(createInstStr(clazz, commonMethodsClassName)).append("\n");
            instBuilder.append(getFieldAssigment(result, obj, classHierarchy, objectDepth));
            instBuilder.append(tabSymb).append(tabSymb).append("return ").append(getInstName(clazz)).append(";\n");
        } else {
            instBuilder.append(getFieldAssigment(result, obj, classHierarchy, objectDepth));
        }
        result.setInstanceCreator(instBuilder.toString());
        return result;
    }

    private void fillSimpleInstance(InstanceCreateData simpleInstance, boolean createInst, StringBuilder instBuilder) {
        instBuilder.append(tabSymb).append(tabSymb).append("return");
        if (createInst) {
            instBuilder.append(" ").append(simpleInstance.getInstanceCreator()).append(";\n");
        } else {
            instBuilder.append(";\n");
        }
    }

    private String getFieldAssigment(InstanceCreateData result, Object obj, List<Class> classHierarchy, int objectDepth) throws Exception {
        Class<?> clazz = obj.getClass();
        StringBuilder assignBuilder = new StringBuilder();
        List<Method> allMethods = getAllMethodsOfClass(classHierarchy);
        for (Field field : getAllFieldsOfClass(classHierarchy)) {
            int fieldModifiers = field.getModifiers();
            boolean deniedModifier = isStatic(fieldModifiers) || isNative(fieldModifiers);
            Object fieldValue = null;
            if (deniedModifier || !allowedType(field.getType()) ||
                    (!field.getType().isPrimitive() && (fieldValue = getFieldValue(field, obj)) == null)
                    ) {
                continue;
            }
            if (fieldValue == null) fieldValue = getFieldValue(field, obj);
            InstanceCreateData instData = getInstanceCreateData(fieldValue, false, objectDepth);
            if (instData == null) continue;
            String fieldName = field.getName();
            if (!isPublic(clazz.getModifiers()) || setterNotExists(fieldName, field, allMethods)) {
                if (isPublic(fieldModifiers) && !isFinal(fieldModifiers)) {
                    assignBuilder.append(getFieldAssignment(tabSymb, obj, fieldName, instData.getInstanceCreator()));
                } else {
                    assignBuilder .append(tabSymb).append(tabSymb)
                            .append(getFieldNotPublicAssignment(obj, fieldName, instData.getInstanceCreator(), commonMethodsClassName))
                            .append(";\n");
                }
            } else {
                assignBuilder.append(getFieldSetter(tabSymb, obj, fieldName, instData.getInstanceCreator()));
            }
            result.getDataProviderMethods().addAll(instData.getDataProviderMethods());
        }
        return assignBuilder.toString();
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
                            .replaceAll("([\\\\])", ESCAPE_STRING_REPLACE)
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
                    .replaceAll(ESCAPE_STRING_REGEX, ESCAPE_STRING_REPLACE)
                    .replaceAll("\r", "\\\\r")
                    .replaceAll("\n", "\\\\n")
                    .replaceAll("\t", "\\\\t");
            result = new InstanceCreateData("\"" + escapedVal + "\"");
        } else if (clazz.equals(java.math.BigDecimal.class)) {
            java.math.BigDecimal val = (java.math.BigDecimal) obj;
            result = new InstanceCreateData("new java.math.BigDecimal(\"" + val + "\")");
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
        } else if (getClassHierarchyStr(clazz).contains(java.util.Calendar.class.getName())) {
            java.util.Calendar val = (java.util.Calendar) obj;
            String methodCall = getCalendarInstanceMethod(
                    "\"" + val.getTimeZone().getID() + "\"",
                    val.getTimeInMillis() + "L",
                    commonMethodsClassName
            );
            result = new InstanceCreateData(methodCall);
        } else if (getClassHierarchyStr(clazz).contains(java.util.TimeZone.class.getName())) {
            java.util.TimeZone val = (java.util.TimeZone) obj;
            result = new InstanceCreateData("(" + clazz.getName() + ") java.util.TimeZone.getTimeZone(\"" + val.getID() + "\")");
        } else if (clazz.isEnum()) {
            Enum val = (Enum) obj;
            String enumType = getClearedClassName(clazz.getName());
            result = new InstanceCreateData(enumType + "." + val.name());
        } else if (!onlySimple && allowedType(clazz)) {
            String fieldName = clazz.isArray() ? "array" : getInstName(clazz.getName(), false);
            ProviderResult providerResult = createDataProviderMethod(obj, fieldName, objectDepth);
            result = new InstanceCreateData(providerResult.getEndPoint().getMethodName());
            result.getDataProviderMethods().addAll(providerResult.getProviders());
        } else if (!onlySimple) {
            result = new InstanceCreateData("null");
        }
        return result;
    }

    private ProviderResult createDataProviderMethod(Object obj, boolean fillObj) throws FillingNotSupportedException {
        if (obj == null || !allowedType(obj.getClass())) return null;
        boolean anonymousClass = getLastClassShort(obj.getClass().getName()).matches("\\d+");
        if (anonymousClass) return null;
        Class<?> clazz = obj.getClass();
        String fieldName = clazz.isArray() ? "array" : getInstName(clazz.getName(), false);
        int objectDepth = maxObjectDepth;
        try {
            return createDataProviderMethod(obj, fieldName, fillObj, objectDepth);
        } catch (FillingNotSupportedException fillEx) {
            throw fillEx;
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private ProviderResult createDataProviderMethod(Object obj, String fieldName, int objectDepth) throws Exception {
        return createDataProviderMethod(obj, fieldName, false, objectDepth);
    }

    private ProviderResult createDataProviderMethod(Object obj, String fieldName, boolean fillObj, int objectDepth) throws Exception {
        Set<ProviderInfo> providers = new HashSet<ProviderInfo>();

        int nextObjectDepth = objectDepth - 1;

        StringBuilder bodyBuilder = new StringBuilder();

        Class<?> clazz = obj.getClass();
        Class<?> actClass = !isPublic(clazz.getModifiers()) ? getFirstPublicType(clazz) : clazz;
        String typeName;

        Extension extension = findExtension(clazz);
        if (extension != null) {
            if (fillObj && !extension.isFillingSupported()) {
                throw new FillingNotSupportedException(
                        "Extension " + extension.getClass().getName() + " not supported filling objects"
                );
            }
            typeName = extension.getActualType(obj);
            bodyBuilder.append(extension.getMethodBody(providers, nextObjectDepth, obj, fillObj));
        } else {
            typeName = actClass.getName();
            bodyBuilder.append(getMethodBody(obj, providers, getClassHierarchy(clazz), nextObjectDepth, fillObj));
        }
        String args = fillObj ? "(" + getClearedClassName(typeName) + " " + getInstName(clazz) + ")" : "()";
        String stubArgs = fillObj ? "(" + VAR_NAME_PLACEHOLDER + ")" : args;
        String retType = fillObj ? "void" : getClearedClassName(typeName);
        String methodBody = bodyBuilder.toString();
        String providerMethodName = getDataProviderMethodName(fieldName, methodBody.hashCode(), fillObj);
        String method = tabSymb + "public static " + retType + " " +
                        providerMethodName + args + " throws Exception {\n" + methodBody + tabSymb + "}\n";
        ProviderResult result = new ProviderResult();
        result.setEndPoint(new ProviderInfo(providerMethodName + stubArgs, method, isBlank(methodBody)));
        result.setProviders(providers);
        result.getProviders().add(result.getEndPoint());
        if (commonMethodsClassName == null) {
            result.getProviders().addAll(commonMethods);
        }
        return result;
    }

    public Set<String> getAllowedPackages() {
        if (allowedPackages == null) allowedPackages = new HashSet<String>();
        return allowedPackages;
    }

    boolean allowedType(Class<?> clazz) {
        if (allowedPackages == null || allowedPackages.size() == 0 || clazz.isPrimitive()) return true;
        String className = clazz.isArray() ? getCanonicalTypeName(clazz) : clazz.getName();
        if (isPrimitive(className)) return true;
        for (String p : allowedPackages) {
            if (className.startsWith(p)) return true;
        }
        return false;
    }

    private String getMethodBody(Object obj, Set<ProviderInfo> result, List<Class> classHierarchy, int objectDepth, boolean fillObj) throws Exception {
        InstanceCreateData objGenerateResult = generateObjInstance(obj, classHierarchy, objectDepth, !fillObj);
        result.addAll(objGenerateResult.getDataProviderMethods());
        return objGenerateResult.getInstanceCreator();
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

    public int getByteArrayMaxLength() {
        return byteArrayMaxLength;
    }

    public void setByteArrayMaxLength(int byteArrayMaxLength) {
        this.byteArrayMaxLength = byteArrayMaxLength;
    }
}
