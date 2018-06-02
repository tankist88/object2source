package org.object2source.extension.arrays;

import org.object2source.dto.InstanceCreateData;
import org.object2source.dto.ProviderInfo;
import org.object2source.extension.AbstractEmbeddedExtension;

import java.util.Set;

import static java.lang.reflect.Modifier.isPublic;
import static org.object2source.util.GenerationUtil.createArrayElementString;
import static org.object2source.util.GenerationUtil.downFirst;

public class ArraysExtension extends AbstractEmbeddedExtension {
    @Override
    public boolean isTypeSupported(Class clazz) {
        return clazz.isArray();
    }

    @Override
    public String getActualType(Object obj) {
        String canonicalName = obj.getClass().getCanonicalName();
        try {
            Class canonicalClazz = Class.forName(canonicalName.substring(0, canonicalName.length() - 2));
            Class actClass = !isPublic(canonicalClazz.getModifiers()) ? Object.class : canonicalClazz;
            return actClass.getName() + "[]";
        } catch (ClassNotFoundException e) {
            return canonicalName;
        }
    }

    @Override
    public void fillMethodBody(StringBuilder bb, Set<ProviderInfo> providers, int objectDepth, Object obj) throws Exception {
        String fieldName = "array";

        StringBuilder arrayValues = new StringBuilder();
        int arraySize;
        if (obj instanceof boolean[]) {
            boolean[] arr = ((boolean[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof byte[]) {
            byte[] arr = ((byte[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof char[]) {
            char[] arr = ((char[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof short[]) {
            short[] arr = ((short[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof int[]) {
            int[] arr = ((int[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof long[]) {
            long[] arr = ((long[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof float[]) {
            float[] arr = ((float[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == 0.0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof double[]) {
            double[] arr = ((double[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == 0.0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof String[]) {
            String[] arr = ((String[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.math.BigDecimal[]) {
            java.math.BigDecimal[] arr = ((java.math.BigDecimal[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.math.BigInteger[]) {
            java.math.BigInteger[] arr = ((java.math.BigInteger[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.sql.Timestamp[]) {
            java.sql.Timestamp[] arr = ((java.sql.Timestamp[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.sql.Time[]) {
            java.sql.Time[] arr = ((java.sql.Time[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.sql.Date[]) {
            java.sql.Date[] arr = ((java.sql.Date[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.util.Date[]) {
            java.util.Date[] arr = ((java.util.Date[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.util.UUID[]) {
            java.util.UUID[] arr = ((java.util.UUID[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof java.util.Calendar[]) {
            java.util.Calendar[] arr = ((java.util.Calendar[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else {
            Object[] arr = ((Object[]) obj);
            arraySize = arr.length;
            for (int i = 0; i < arraySize; i++) {
                if (arr[i] == null) continue;
                InstanceCreateData data = sourceGenerator.getInstanceCreateData(arr[i], objectDepth);
                arrayValues.append(createArrayElementString(fieldName, data.getInstanceCreator(), i, getTabSymb()));
                providers.addAll(data.getDataProviderMethods());
            }
        }

        String typeName = getActualType(obj);
        String typeWithSize = new StringBuilder(typeName).insert(typeName.length() - 1, arraySize).toString();
        String sourceCanonicalType = obj.getClass().getCanonicalName();

        bb.append(getTabSymb())
          .append(getTabSymb())
          .append(typeName)
          .append(" ")
          .append(downFirst(fieldName))
          .append(" = ");

        if(typeName.equals(sourceCanonicalType)) {
            bb.append("new ")
              .append(typeWithSize)
              .append(";\n");
        } else {
            String elementType = sourceCanonicalType.substring(0, sourceCanonicalType.length() - 2);
            bb.append("(")
              .append(typeName)
              .append(") java.lang.reflect.Array.newInstance(Class.forName(\"")
              .append(elementType)
              .append("\"), ")
              .append(arraySize)
              .append(");\n");
        }

        bb.append(arrayValues.toString())
          .append(getTabSymb())
          .append(getTabSymb())
          .append("return ")
          .append(downFirst(fieldName))
          .append(";\n");
    }
}
