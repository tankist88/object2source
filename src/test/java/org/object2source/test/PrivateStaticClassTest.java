package org.object2source.test;

public class PrivateStaticClassTest {
    public static TestClass getTestClass() {
        return new TestClass(1, "ggg");
    }

    private static class TestClass implements TestClassInt {
        private int id;
        private String name;
        private NotPublic notPublic;
        private ExamplePackagePrivateList<Integer> examplePackagePrivateList;
        private NotPublic[] notPublicArray;
        private PrivateConstructor pc;

        TestClass(int id, String name) {
            this.id = id;
            this.name = name;
            this.notPublic = new NotPublic();
            this.examplePackagePrivateList = new ExamplePackagePrivateList<>();
            this.examplePackagePrivateList.add(123);
            this.examplePackagePrivateList.add(321);
            this.notPublicArray = new NotPublic[10];
            this.notPublicArray[0] = new NotPublic();
            this.notPublicArray[5] = new NotPublic();
            this.pc = new PrivateConstructor();
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        @Override
        public NotPublic getNotPublic() {
            return notPublic;
        }

        public void setNotPublic(NotPublic notPublic) {
            this.notPublic = notPublic;
        }

        public void setName(String name) {
            this.name = name;
        }

        public ExamplePackagePrivateList getExamplePackagePrivateList() {
            return examplePackagePrivateList;
        }

        public void setExamplePackagePrivateList(ExamplePackagePrivateList examplePackagePrivateList) {
            this.examplePackagePrivateList = examplePackagePrivateList;
        }
    }

    public static class PrivateConstructor {
        private int id;

        private PrivateConstructor() {
            this.id = 111;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}
