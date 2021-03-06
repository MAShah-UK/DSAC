package cbox.sorting;

import org.junit.Assert;
import org.junit.Test;

public class FrequencySortTest_exec {
    public void checkEqual(int[] expected, int[] array) {
        FrequencySort.exec(array);
        Assert.assertArrayEquals(expected, array);
    }

    @Test
    public void checkNull() {
        checkEqual(null, null);
    }

    @Test
    public void emptyArray() {
        checkEqual(new int[]{}, new int[]{});
    }

    @Test
    public void manyRepeatingElements() {
        checkEqual(new int[]{1, 1, 0, 0, -2, -2, 2, -1}, new int[]{-2, -2, -1, 0, 0, 1, 1, 2});
    }

    @Test
    public void manyRepeatingElements2() {
        checkEqual(new int[]{4, 4, 4, 5, 5, 6}, new int[]{4, 5, 6, 4, 5, 4});
    }


    @Test
    public void negativeToPositiveElements() {
        checkEqual(new int[]{1, 0, -1}, new int[]{-1, 0, 1});
    }

    @Test
    public void oneElement() {
        checkEqual(new int[]{5}, new int[]{5});
    }

    @Test
    public void oneRepeatingElement() {
        checkEqual(new int[]{2, 2, 1}, new int[]{1, 2, 2});
    }

    @Test
    public void sameElements() {
        checkEqual(new int[]{1, 1, 1}, new int[]{1, 1, 1});
    }

    @Test
    public void twoRepeatingElements() {
        checkEqual(new int[]{2, 2, 1, 1, 3}, new int[]{3, 2, 1, 1, 2});
    }

    @Test
    public void uniqueElementsOrdered() {
        checkEqual(new int[]{3, 2, 1}, new int[]{3, 2, 1});
    }

    @Test
    public void uniqueElementsUnordered() {
        checkEqual(new int[]{3, 2, 1}, new int[]{2, 3, 1});
    }
}
