# object2source #

Библиотека для генерации исходного кода, создающего экземпляр объекта, который подается на вход.

Пример вызова:

```java
TestObj testObj = new TestObj();
(new SourceGenerator()).createDataProviderMethod(testObj);
```

Результат:
```java
private static org.object2source.TestObj getTestObj_240650537() {
    org.object2source.TestObj testObj = new org.object2source.TestObj();
    testObj.setNum(0);
    testObj.setCh(' ');
    testObj.setArr(getArray_483422889());
    testObj.setTestObjArr(getArray_1277181601());
    return testObj;
}

private static int[] getArray_483422889() {
    int[] array = new int[10];
    array[0] = 0;
    array[1] = 0;
    array[2] = 0;
    array[3] = 0;
    array[4] = 0;
    array[5] = 0;
    array[6] = 0;
    array[7] = 0;
    array[8] = 0;
    array[9] = 0;
    return array;
}

private static org.object2source.TestObj[] getArray_1277181601() {
    org.object2source.TestObj[] array = new org.object2source.TestObj[10];
    array[0] = null;
    array[1] = null;
    array[2] = null;
    array[3] = null;
    array[4] = null;
    array[5] = null;
    array[6] = null;
    array[7] = null;
    array[8] = null;
    array[9] = null;
    return array;
}
```

### Контакты ###

* Repo owner - Alexey Ustinov (tankist88@gmail.com)
