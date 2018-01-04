package org.magenta.random;

import java.util.Random;

import org.junit.Test;

import com.google.common.collect.Range;

public class RandomDoubleTest {

	@Test
	public void testAny(){

		//setup fixtures
		RandomDouble sut=new RandomDouble(new Random(),4,Range.closedOpen(-100d, 100d));

		//exercise SUT
		for(int x=0;x<50;x++){
			double actual=sut.any();

		}

	}
}
