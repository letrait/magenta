package org.magenta.core.data.supplier;

import java.util.Iterator;
import java.util.List;

import org.magenta.DataSupplier;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.reflect.TypeToken;
import com.google.inject.internal.Lists;

public class LazyGeneratedCollectionDataSupplier<D> implements DataSupplier<D> {

	private final Supplier<List<D>> generator;
	private final TypeToken<D> type;
	
	
	public LazyGeneratedCollectionDataSupplier(final Supplier<? extends Iterable<D>> generator, TypeToken<D> type) {
		this.generator = Suppliers.memoize(new Supplier<List<D>>() {

			@Override
			public List<D> get() {
				return Lists.newArrayList(generator.get());
			}
		});

		this.type = type;
	}
	
	
	private List<D> list(){
		return this.generator.get();
	}
	
	
	@Override
	public Iterator<D> iterator() {
		return list().iterator();
	}

	@Override
	public D get(int position) {
		return list().get(position);
	}

	@Override
	public TypeToken<D> getType() {
		return type;
	}

	@Override
	public int getSize() {
		return list().size();
	}

	@Override
	public boolean isEmpty() {
		return list().isEmpty();
	}

	@Override
	public boolean isGenerated() {
		return true;
	}

	@Override
	public boolean isConstant() {
		return true;
	}

}
