package com.github.bchapuis.markovchain4j;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NOrderMarkovChainTest {

    @Test
    public void test() {
        List<Integer> list = Arrays.asList(new Integer[] {
                1,2,3,4,5,6,7,8,9,
                1,2,3,4,5,6,7,8,9,
                1,2,3,4,5,6,7,8,9,
        });
        NOrderMarkovChain<Integer> mc = NOrderMarkovChain.create(list, 2);
        Assert.assertTrue(mc.next(Arrays.asList(new Integer[] {1, 2})).get(0).state == 3);
        Assert.assertTrue(mc.next(Arrays.asList(new Integer[] {2, 3})).get(0).state == 4);
        Assert.assertTrue(mc.next(Arrays.asList(new Integer[] {8, 9})).get(0).state == 1);
    }

}
