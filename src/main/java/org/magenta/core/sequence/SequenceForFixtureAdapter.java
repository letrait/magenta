package org.magenta.core.sequence;

import org.magenta.Fixture;
import org.magenta.Sequence;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

public class SequenceForFixtureAdapter<D> implements Sequence<D> {

  private final Function<Fixture,Sequence<D>> sequenceProvider;
  private final Supplier<? extends Fixture> fixtureSupplier;

  private SequenceForFixtureAdapter(Supplier<? extends Fixture> fixtureSupplier, Function<Fixture, Sequence<D>> sequenceProvider) {
    super();
    this.fixtureSupplier = fixtureSupplier;

    //TODO : caching the sequence may be faster
    this.sequenceProvider = sequenceProvider;

  }

  public static <D> SequenceForFixtureAdapter<D> from(Supplier<? extends Fixture> fixtureSupplier, Function<Fixture, Sequence<D>> sequenceProvider){
    return new SequenceForFixtureAdapter<D>(fixtureSupplier, sequenceProvider);
  }

  @Override
  public D get() {

    Fixture f = fixtureSupplier.get();

    return sequenceProvider.apply(f).get();
  }

  @Override
  public int size() {
    Fixture f = fixtureSupplier.get();

    return sequenceProvider.apply(f).size();
  }

}
