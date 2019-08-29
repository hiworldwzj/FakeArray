package code;

import java.util.Random;

import static org.junit.Assert.*;

public class GoodFakeArrayTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @org.junit.Test
    public void length() {
        GoodFakeArray arrayList = new GoodFakeArray();
        assertTrue(arrayList.length() == 0);
        arrayList.addFirst(100);
        arrayList.addFirst(100);
        assertTrue(arrayList.length() == 2);
        arrayList.addLast(199);
        arrayList.addLast(199);
        assertTrue(arrayList.length() == 4);
        for(int i = 0; i < 200; i++) {
            arrayList.add(this.randInt(arrayList.length()), this.randInt(1000));
            assertTrue(arrayList.length() == 4 + i + 1);
        }
        assertTrue(arrayList.getDepth() < 10);
    }

    @org.junit.Test
    public void get() {
        GoodFakeArray fakeArray = new GoodFakeArray();
        int[] testArray = new int[100];
        for(int i = 0; i < testArray.length; i++) {
            testArray[i] = this.randInt(1000);
            fakeArray.addLast(testArray[i]);
        }

        for(int i = 0; i < testArray.length; i++) {
            assertTrue((Integer)fakeArray.get(i) == testArray[i]);
        }

    }

    @org.junit.Test
    public void addorDelete() {
        GoodFakeArray alist = new GoodFakeArray();
        for(int i = 0; i < 1000; i++) {
            alist.addLast(666);
        }
        for(int i = 0; i < 10000000; i++) {
            if(i % 100000 == 0) {
                System.out.println(i);
            }
            if(Math.random() < 0.7) {
                int loc = this.randInt(alist.length());
                int value = this.randInt(1000);
                alist.add(loc, value);
                assertTrue((Integer)alist.get(loc) == value);
            } else {
                int loc = this.randInt(alist.length());
                alist.delete(loc);
            }
        }
        assertTrue(alist.length() < 10000000);
        assertTrue(alist.getDepth() < 64);
    }


    static Random random = new Random();

    public int randInt(int size) {
        return random.nextInt(size);
    }
}