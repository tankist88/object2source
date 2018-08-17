package com.github.tankist88.object2source.extension.arrays;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.extension.AbstractEmbeddedExtension;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.tankist88.object2source.util.GenerationUtil.*;
import static java.lang.reflect.Modifier.isPublic;

public class ArraysExtension extends AbstractEmbeddedExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return clazz.isArray();
    }

    @Override
    public String getActualType(Object obj) {
        return getClearedClassName(getActualClassName(obj));
    }

    private String getActualClassName(Object obj) {
        Class canonicalClazz = getCanonicalClass(obj);
        String canonicalName = obj.getClass().getCanonicalName();
        if (!isPublic(canonicalClazz.getModifiers())) {
            return Object.class.getName() + canonicalName.substring(canonicalName.indexOf("["));
        } else {
            return canonicalName;
        }
    }

    private Class getCanonicalClass(Object obj) {
        String canonicalName = obj.getClass().getCanonicalName();
        String canonicalType = canonicalName.replace("[", "").replace("]", "");
        try {
            return ClassUtils.getClass(obj.getClass().getClassLoader(), canonicalType);
        } catch (ClassNotFoundException cnf1) {
            throw new IllegalStateException(cnf1);
        }
    }

    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        String fieldName = "array";

        StringBuilder arrayValues = new StringBuilder();
        List<Integer> arraySizeList = new ArrayList<>();
        if (obj instanceof boolean[]) {
            boolean[] arr = ((boolean[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof byte[]) {
            byte[] arr = ((byte[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof char[]) {
            char[] arr = ((char[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof short[]) {
            short[] arr = ((short[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof int[]) {
            int[] arr = ((int[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof long[]) {
            long[] arr = ((long[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof float[]) {
            float[] arr = ((float[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof double[]) {
            double[] arr = ((double[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof String[]) {
            String[] arr = ((String[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.math.BigDecimal[]) {
            java.math.BigDecimal[] arr = ((java.math.BigDecimal[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.math.BigInteger[]) {
            java.math.BigInteger[] arr = ((java.math.BigInteger[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.sql.Timestamp[]) {
            java.sql.Timestamp[] arr = ((java.sql.Timestamp[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.sql.Time[]) {
            java.sql.Time[] arr = ((java.sql.Time[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.sql.Date[]) {
            java.sql.Date[] arr = ((java.sql.Date[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.util.Date[]) {
            java.util.Date[] arr = ((java.util.Date[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.util.UUID[]) {
            java.util.UUID[] arr = ((java.util.UUID[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.util.Calendar[]) {
            java.util.Calendar[] arr = ((java.util.Calendar[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else {
            Object[] arr = ((Object[]) obj);
            int dimension = obj.getClass().getCanonicalName().split("\\[").length - 1;
            Object inArr = arr;
            for (int i = 0; i < dimension; i++) {
                arraySizeList.add(Array.getLength(inArr));
                if (i < dimension - 1) {
                    inArr = Array.get(inArr, 0);
                }
            }
            for (int i = 0; i < arr.length; i++) {
                InstanceCreateData data = sourceGenerator.getInstanceCreateData(arr[i], objectDepth);
                arrayValues.append(createArrayElementString(fieldName, data.getInstanceCreator(), i, getTabSymb()));
                providers.addAll(data.getDataProviderMethods());
            }
        }

        String typeName = getActualType(obj);

        String typeWithSize = typeName;
        for (Integer dim : arraySizeList) {
            typeWithSize = typeWithSize.replaceFirst("\\[]", "[" + dim + "]");
        }

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append(typeName)
          .append(" ")
          .append(downFirst(fieldName))
          .append(" = ");

        if(typeName.equals(obj.getClass().getCanonicalName())) {
            bb.append("new ")
              .append(typeWithSize)
              .append(";\n");
        } else {
            String elementType = getCanonicalClass(obj).getName();
            bb.append("(")
              .append(typeName)
              .append(") java.lang.reflect.Array.newInstance(Class.forName(\"")
              .append(elementType)
              .append("\"), ");
            Iterator<Integer> iterator = arraySizeList.iterator();
            while (iterator.hasNext()) {
                bb.append(iterator.next());
                if (iterator.hasNext()) bb.append(", ");
            }
            bb.append(");\n");
        }

        bb.append(arrayValues.toString())
          .append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(downFirst(fieldName))
          .append(";\n");
    }
}
