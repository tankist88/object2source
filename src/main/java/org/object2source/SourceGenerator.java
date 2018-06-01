package org.object2source;

import org.clapper.util.classutil.ClassFinder;
import org.clapper.util.classutil.ClassInfo;
import org.clapper.util.classutil.SubclassClassFilter;
import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;
import org.object2source.dto.ProviderResult;
import org.object2source.exception.ObjectDepthExceededException;
import org.object2source.extension.EmbeddedExtension;
import org.object2source.extension.Extension;
import org.object2source.extension.arrays.ArraysExtension;
import org.object2source.extension.collections.*;
import org.object2source.extension.maps.BaseMapsExtension;
import org.object2source.extension.maps.EmptyMapExtension;
import org.object2source.extension.maps.UnmodMapExtension;
import org.object2source.extension.maps.UnmodSortedMapExtension;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.lang.reflect.Modifier.*;
import static org.object2source.util.AssigmentUtil.*;
import static org.object2source.util.GenerationUtil.*;

public class SourceGenerator implements TypeGenerator {
    static final int DEFAULT_MAX_DEPTH = 10;

    private Set<String> packageExclusions;
    private int maxObjectDepth;
    private String commonMethodsClassName;
    private Set<ProviderInfo> commonMethods;
    private String tabSymb;
    private boolean exceptionWhenMaxODepth;

    private List<Extension> extensions;
    private Set<String> extensionClasses;

    public SourceGenerator() {
        this("    ", new HashSet<String>());
    }
    public SourceGenerator(String tabSymb) {
        this(tabSymb, new HashSet<String>());
    }
    public SourceGenerator(String tabSymb, Set<String> packageExclusions) {
        this(tabSymb, packageExclusions, null);
    }
    public SourceGenerator(String tabSymb, Set<String> packageExclusions, String commonMethodsClassName) {
        this(tabSymb, packageExclusions, commonMethodsClassName, true);
    }
    public SourceGenerator(String tabSymb, Set<String> packageExclusions, String commonMethodsClassName, boolean exceptionWhenMaxODepth) {
        this.tabSymb = tabSymb;
        this.packageExclusions = packageExclusions;
        this.maxObjectDepth = DEFAULT_MAX_DEPTH;
        this.exceptionWhenMaxODepth = exceptionWhenMaxODepth;
        this.commonMethodsClassName = commonMethodsClassName;
        this.commonMethods = getCommonMethods(tabSymb);
        this.extensions =  new ArrayList<>();
        this.extensionClasses = new HashSet<>();
        initEmbeddedExtensions();
        initClassPathExtensions();
    }

    private void initEmbeddedExtensions() {
        registerExtension(new ArraysExtension());
        registerExtension(new ArraysArrayListExtension());
        registerExtension(new EmptyListExtension());
        registerExtension(new EmptySetExtension());
        registerExtension(new UnmodCollectionExtension());
        registerExtension(new SingletonListExtension());
        registerExtension(new BaseCollectionsExtension());
        registerExtension(new EmptyMapExtension());
        registerExtension(new UnmodSortedMapExtension());
        registerExtension(new UnmodMapExtension());
        registerExtension(new BaseMapsExtension());
    }

    private void initClassPathExtensions() {
        Set<ClassInfo> classes = new HashSet<>();
        ClassFinder cf = new ClassFinder();
        cf.addClassPath();
        cf.findClasses(classes, new SubclassClassFilter(Extension.class));
        for (ClassInfo ci : classes) {
            if (isAbstract(ci.getModifier()) || isInterface(ci.getModifier())) continue;
            try {
                Extension ext = (Extension) Class.forName(ci.getClassName()).newInstance();
                registerExtension(ext);
            } catch (ReflectiveOperationException roe) {
                System.err.println("Cant't register extension " + ci.getClassName() + ". " + roe.getMessage());
                roe.printStackTrace();
            }
        }
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
        } else if (objectDepth <= 0 || obj == null || exclusionType(obj.getClass())) {
            return new InstanceCreateData(tabSymb + "return null;\n");
        }

        InstanceCreateData result = new InstanceCreateData();
        StringBuilder instBuilder = new StringBuilder();

        Class<?> clazz = obj.getClass();

        InstanceCreateData simpleInstance = getInstanceCreateData(obj, true, objectDepth);
        if (simpleInstance != null) {
            instBuilder.append(tabSymb).append("return ").append(simpleInstance.getInstanceCreator()).append(";\n");
        } else {
            instBuilder.append(tabSymb).append(createInstStr(clazz, commonMethodsClassName)).append("\n");
            List<Method> allMethods = getAllMethodsOfClass(classHierarchy);
            for (Field field : getAllFieldsOfClass(classHierarchy)) {
                boolean deniedModifier = isStatic(field.getModifiers()) || isNative(field.getModifiers());
                if (deniedModifier || exclusionType(field.getType()) || (!field.getType().isPrimitive() && getFieldValue(field, obj) == null)) continue;
                InstanceCreateData instData = getInstanceCreateData(getFieldValue(field, obj), false, objectDepth);
                if (instData == null) continue;
                String fieldName = field.getName();
                if (!isPublic(clazz.getModifiers()) || setterNotExists(fieldName, field, allMethods)) {
                    if (isPublic(field.getModifiers()) && !isFinal(field.getModifiers())) {
                        instBuilder.append(getFieldAssignment(tabSymb, obj, fieldName, instData.getInstanceCreator()));
                    } else {
                        instBuilder .append(tabSymb).append(tabSymb)
                                    .append(getFieldNotPublicAssignment(obj, fieldName, instData.getInstanceCreator(), commonMethodsClassName))
                                    .append(";\n");
                    }
                } else {
                    instBuilder.append(getFieldSetter(tabSymb, obj, fieldName, instData.getInstanceCreator()));
                }
                result.getDataProviderMethods().addAll(instData.getDataProviderMethods());
            }
            instBuilder.append(tabSymb).append(tabSymb).append("return ").append(getInstName(clazz)).append(";\n");
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
                            .replaceAll("([\\\\])", ESCAPE_STRING_REPLACE)
                            .replaceAll("\r", "\\\\r")
                            .replaceAll("\n", "\\\\n")
                            .replaceAll("\t", "\\\\t")
                    + "'");
        } else if (clazz.equals(short.class) || clazz.equals(Short.class)) {
            result = new InstanceCreateData(Short.valueOf(obj.toString()).toString());
        } else if (clazz.equals(byte.class) || clazz.equals(Byte.class)) {
            result = new InstanceCreateData(Byte.valueOf(obj.toString()).toString());
        } else if (clazz.equals(String.class)) {
            String escapedVal = obj.toString()
                    .replaceAll(ESCAPE_STRING_REGEX, ESCAPE_STRING_REPLACE)
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
        } else if (getClassHierarchyStr(clazz).contains(java.util.Calendar.class.getName())) {
            java.util.Calendar val = (java.util.Calendar) obj;
            String methodCall = getCalendarInstanceMethod(
                    "\"" + val.getTimeZone().getID() + "\"",
                    val.getTimeInMillis() + "L",
                    commonMethodsClassName
            );
            result = new InstanceCreateData(methodCall);
        } else if (clazz.isEnum()) {
            Enum val = (Enum) obj;
            String enumType = clazz.getName().replaceAll("\\$", ".");
            result = new InstanceCreateData(enumType + "." + val.name());
        } else if (!onlySimple && !exclusionType(clazz)) {
            String fieldName = clazz.isArray() ? "array" : getInstName(clazz.getName(), false);
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
        if (obj == null || exclusionType(obj.getClass())) return null;
        boolean anonymousClass = getLastClassShort(obj.getClass().getName()).matches("\\d+");
        if(anonymousClass) return null;
        Class<?> clazz = obj.getClass();
        String fieldName = clazz.isArray() ? "array" : getInstName(clazz.getName(), false);
        int objectDepth = maxObjectDepth;
        try {
            return createDataProviderMethod(obj, fieldName, objectDepth);
        } catch (Exception ex) {
            return null;
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
            fillMethodBody(obj, bodyBuilder, providers, getClassHierarchy(clazz), objectDepth);
        }

        Class<?> actClass = !isPublic(clazz.getModifiers()) ? getFirstPublicType(clazz) : clazz;
        String typeName = extension != null ? extension.getActualType(obj) : actClass.getName();
        String methodBody = bodyBuilder.toString();
        String providerMethodName = getDataProviderMethodName(fieldName, methodBody.hashCode());

        String method = tabSymb + "public static " + typeName.replaceAll("\\$", ".") + " " +
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

    public Set<String> getPackageExclusions() {
        if (packageExclusions == null) packageExclusions = new HashSet<>();
        return packageExclusions;
    }

    private boolean exclusionType(Class<?> clazz) {
        for (String p : packageExclusions) {
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
            extensions.add(extension);
            extensionClasses.add(extension.getClass().getName());
        }
    }

    @Override
    public Extension findExtension(Class clazz) {
        // Search in registered extensions
        for (Extension ext : extensions) {
            if (ext.isTypeSupported(clazz)) return ext;
        }
        return null;
    }
}
