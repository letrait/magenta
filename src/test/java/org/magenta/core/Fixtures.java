package org.magenta.core;

import java.util.Arrays;
import java.util.Random;

import org.magenta.core.GenericDataSet;
import org.magenta.random.Randoms;

import com.google.common.base.Suppliers;

public class Fixtures {
	
	public static Randoms randomizer(){
		return Randoms.singleton();
	}
	
  public static GenericDataSet<String> createAnonymousDataSet(int size) {
    String[] elements = new String[size];
    for (int i = 0; i < size; i++) {
      elements[i] = randomizer().strings().charabia(12);
    }
    return createDataSetOf(elements);
  }

  public static <D> GenericDataSet<D> createDataSetOf(D... elements) {
    return new GenericDataSet<D>(Suppliers.ofInstance(Arrays.asList(elements)), (Class<D>) elements[0].getClass(),randomizer());
  }
}
