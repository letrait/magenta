package org.magenta.core.data.supplier;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.magenta.DataGenerationException;
import org.magenta.DataSupplier;
import org.magenta.Sequence;

import com.google.common.base.Preconditions;
import com.google.common.reflect.TypeToken;

public class LazyGeneratedDataSupplier<D> implements DataSupplier<D> {

  public static final int NO_SPECIFIED_DEFAULT_SIZE = -1;

  private final Sequence<D> generator;

  private final TypeToken<D> type;

  private final int defaultSize;
  private final int maximumAllowedSize;

  private List<Object> generatedData;
  private List<Boolean> generatedFlags;

  public LazyGeneratedDataSupplier(Sequence<D> generator, TypeToken<D> type, int defaultSize, int maximumAllowedSize) {
    super();
    this.generator = checkNotNull(generator);
    this.type = checkNotNull(type);
    Preconditions.checkArgument(defaultSize >= NO_SPECIFIED_DEFAULT_SIZE, "the size must not be negative");
    Preconditions.checkArgument(maximumAllowedSize >= defaultSize, "the maximum allowed size must  greater or equals to the specified default size.");
    this.defaultSize = defaultSize;
    this.maximumAllowedSize = maximumAllowedSize;

    this.generatedData = new ArrayList<Object>();
    this.generatedFlags = new ArrayList<Boolean>();
  }

  @Override
  public Iterator<D> iterator() {
    return new BoundedDataSupplierIterator<D>(this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public D get(int index) {

    Object item = null;

    if (isAlreadyGeneratedAt(index)) {
      item = getGeneratedDataAt(index);
    } else {
      item = generateNewDataAt(index);
    }

    return (D) item;
  }

  private boolean isAlreadyGeneratedAt(int index) {

    return index >= generatedFlags.size() ? false : generatedFlags.get(index);

  }

  private Object getGeneratedDataAt(int index) {
    return generatedData.get(index);
  }

  private Object generateNewDataAt(int index) {

    Object item = doGenerate();

    resizeListIfNeeded(generatedData, index, (Object) null);
    resizeListIfNeeded(generatedFlags, index, Boolean.FALSE);

    generatedData.set(index, item);
    generatedFlags.set(index, Boolean.TRUE);

    return item;
  }

  private D doGenerate() {
    try{
    return generator.get();
    }catch(RuntimeException e){
      throw new DataGenerationException(String.format("Error while generating %s with %s", type, generator),e);
    }
  }

  private <O> void resizeListIfNeeded(List<O> toResize, int index, O filler) {
    if (index >= toResize.size()) {
      int numberOfnullItemsToAdd = index + 1 - toResize.size();
      toResize.addAll(Collections.nCopies(numberOfnullItemsToAdd, filler));
    }
  }

  @Override
  public boolean isGenerated() {
    return true;
  }

  @Override
  public boolean isConstant() {
    return true;
  }

  @Override
  public TypeToken<D> getType() {
    return type;
  }

  @Override
  public boolean isEmpty() {
    return this.defaultSize == NO_SPECIFIED_DEFAULT_SIZE? this.generator.size() == 0 : this.defaultSize == 0;
  }

  @Override
  public int getSize() {
    return this.defaultSize == NO_SPECIFIED_DEFAULT_SIZE? this.generator.size() : this.defaultSize;
  }

  @Override
  public int getMaximumSize() {
    return this.maximumAllowedSize;
  }

}
