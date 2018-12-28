package com.github.bchapuis.markovchain4j;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class MarkovChainTest {

    @Test
    public void test() {
        List<Integer> list = Arrays.asList(new Integer[] {
                1,2,3,4,5,6,7,8,9,
                1,2,3,4,5,6,7,8,9,
                1,2,3,4,5,6,7,8,9,
        });
        MarkovChain<Integer> mc = MarkovChain.create(list);
        Assert.assertTrue(mc.next(1).get(0).state == 2);
        Assert.assertTrue(mc.next(2).get(0).state == 3);
        Assert.assertTrue(mc.next(9).get(0).state == 1);
    }

}
