package org.magenta.random;

import java.util.Random;

import org.junit.Test;

public class RandomDoubleTest {

	@Test
	public void testAny(){

		//setup fixtures
		RandomDouble sut=new RandomDouble(new Random(),3);

		//exercise SUT
		for(int x=0;x<100;x++){
			double actual=sut.any();
			System.out.println(actual);
		}

	}
}
