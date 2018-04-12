package org.magenta.random;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.junit.Test;

import com.google.common.collect.Range;

public class RandomLongTest {


  @Test
  public void testAnyInRange(){
    Random r = new Random(1234);
    for(int i = 0 ; i< 1000 ; i++){
    RandomLong rnd = new RandomLong(r);

    long value = rnd.resolution(2).any(Range.closed(-1000L,1000L));

    assertThat(value).isBetween(-1000L, 1000L);
    }
  }
}
