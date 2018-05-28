package org.object2source.test;

public class PrivateStaticClassTest {
    public static TestClass getTestClass() {
        return new TestClass(1, "ggg");
    }

    private static class TestClass implements TestClassInt {
        private int id;
        private String name;

        TestClass(int id, String name) {
            this.id = id;
            this.name = name;
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

        public void setName(String name) {
            this.name = name;
        }
    }
}
