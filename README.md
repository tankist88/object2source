# object2source #

[![Build Status](https://travis-ci.org/tankist88/object2source.svg?branch=master)](https://travis-ci.org/tankist88/object2source)
[![Codecov](https://img.shields.io/codecov/c/github/tankist88/object2source.svg)](https://codecov.io/gh/tankist88/object2source)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/49996a1d61e1430fa8b51d1fd7a17b66)](https://www.codacy.com/project/tankist88/object2source/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=tankist88/object2source&amp;utm_campaign=Badge_Grade_Dashboard)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.tankist88/object2source.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.github.tankist88%22%20a%3A%22object2source%22)


A library for generating the source code that creates an instance of the object that is submitted to the input. 
This means that using this library, you can reverse engineer objects from the JVM memory. The object2source can generate data provider methods for a variety of standard Java types. If you want to generate a method to get an instance of an arbitrary custom class, you can use the extension system.
Now you can use extensions to generate instances of classes from the libraries of Google guava and YodaTime.

Links to the repository with extensions:

1. https://github.com/tankist88/guava-extension
2. https://github.com/tankist88/jodatime-extension

### Example of usage ###

```java
TestObj testObj = new TestObj();
ProviderResult pr = (new SourceGenerator()).createDataProviderMethod(testObj);
```

Print result:
```java
for (ProviderInfo pi : pr.getProviders()) {
    System.out.println(pi.getMethodBody());
}
```

Example output:

```java
private static TestObj getTestObj_240650537() {
    TestObj testObj = new TestObj();
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

private static TestObj[] getArray_1277181601() {
    TestObj[] array = new TestObj[10];
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

### Register extensions ###

```java
SourceGenerator sg = new SourceGenerator();
Extension ext = (Extension) Class.forName("org.example.MyCustomTypeExtension").newInstance();
sg.registerExtension(ext);
```

### Create custom extension ###

To create custom extension you need create class that implements [Extension](https://github.com/tankist88/object2source/blob/master/src/main/java/com/github/tankist88/object2source/extension/Extension.java) interface.

### Installation ###

```text
mvn clean install
```

### Contacts ###

* Repo owner - Alexey Ustinov (tankist88@gmail.com)
