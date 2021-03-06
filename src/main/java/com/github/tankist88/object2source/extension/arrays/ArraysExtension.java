package com.github.tankist88.object2source.extension.arrays;

import com.github.tankist88.object2source.dto.InstanceCreateData;
import com.github.tankist88.object2source.dto.ProviderInfo;
import com.github.tankist88.object2source.extension.AbstractEmbeddedExtension;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static com.github.tankist88.object2source.util.ExtensionUtil.getActualClassName;
import static com.github.tankist88.object2source.util.ExtensionUtil.getCanonicalClass;
import static com.github.tankist88.object2source.util.GenerationUtil.*;

public class ArraysExtension extends AbstractEmbeddedExtension {
    private int byteArrayMaxLength;

    public ArraysExtension() {
        this(-1);
    }

    public ArraysExtension(int byteArrayMaxLength) {
        this.byteArrayMaxLength = byteArrayMaxLength;
    }

    @Override
    public boolean isTypeSupported(Class clazz) {
        return clazz.isArray();
    }

    @Override
    public String getActualType(Object obj) {
        return getClearedClassName(getActualClassName(obj.getClass()));
    }

    @Override
    public String getMethodBody(Set<ProviderInfo> providers, int objectDepth, Object obj, boolean fillObj) throws Exception {
        StringBuilder bb = new StringBuilder();
        String fieldName = "array";
        StringBuilder arrayValues = new StringBuilder();
        List<Integer> arraySizeList = new ArrayList<Integer>();
        if (obj instanceof boolean[]) {
            boolean[] arr = ((boolean[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (!arr[i]) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof byte[]) {
            byte[] arr = ((byte[]) obj);
            arraySizeList.add(arr.length);
            int length = byteArrayMaxLength >= 0 ? byteArrayMaxLength : arr.length;
            for (int i = 0; i < length; i++) {
                if (arr[i] == (byte) 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof char[]) {
            char[] arr = ((char[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == '\u0000') continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof short[]) {
            short[] arr = ((short[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == (short) 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof int[]) {
            int[] arr = ((int[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 0) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof long[]) {
            long[] arr = ((long[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 0L) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof float[]) {
            float[] arr = ((float[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 0.0f) continue;
                String instVal = sourceGenerator.getInstanceCreateData(arr[i], objectDepth).getInstanceCreator();
                arrayValues.append(createArrayElementString(fieldName, instVal, i, getTabSymb()));
            }
        } else if (obj instanceof double[]) {
            double[] arr = ((double[]) obj);
            arraySizeList.add(arr.length);
            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == 0.0d) continue;
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
                if (arr[i] == null) continue;
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
            String elementType = getCanonicalClass(obj.getClass()).getName();
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

        return bb.toString();
    }
}
