package org.magenta.core.data.supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.magenta.Sequence;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;

public class LazyGeneratedCollectionDataSupplierTest {

	@Test
	public void testConstruction(){
		
		//setup fixture

	    Supplier<List<Integer>> integers = Suppliers.ofInstance(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)); 
		LazyGeneratedCollectionDataSupplier<Integer> sut = new LazyGeneratedCollectionDataSupplier<>(integers, TypeToken.of(Integer.class));
		
		//exercise sut and verify outcome

	    assertThat(sut.getType()).isEqualTo(TypeToken.of(Integer.class));
	    assertThat(sut.isConstant()).isEqualTo(true);
	    assertThat(sut.isGenerated()).isEqualTo(true);
		assertThat(sut.getSize()).isEqualTo(10);
	    assertThat(sut.iterator()).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		
		
	}
}
