package org.magenta.random;

import java.util.Locale;
import java.util.Random;

import org.junit.Test;
import org.magenta.random.RandomDouble;

import com.google.common.collect.Range;

import static org.assertj.core.api.Assertions.assertThat;

public class RandomDoubleTest {

	@Test
	public void testAny(){
		
		//setup fixtures
		RandomDouble sut=new RandomDouble(new Random(),3);
		
		//exercise SUT
		for(int x=0;x<100;x++){
			double actual=sut.any(Range.closed(0D, 10D));
			System.out.println(actual);
		}
		
	}
}
