package org.magenta.testing;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.DataSpecification;
import org.magenta.Fixture;
import org.magenta.random.FluentRandom;

public class DataDomainAssert<DATA_SPEC extends DataSpecification> extends AbstractAssert<DataDomainAssert<DATA_SPEC>, Fixture<DATA_SPEC>> {

  protected DataDomainAssert(Fixture<DATA_SPEC> actual) {
    super(actual, DataDomainAssert.class);
  }

  public DataDomainAssert<DATA_SPEC> hasName(String name) {

    Assertions.assertThat(actual.getName()).overridingErrorMessage("Expected data domain name to be <%s> but was <%s>", name, actual.getName())
        .isEqualTo(name);

    return myself;
  }

  public DataDomainAssert<DATA_SPEC> hasSpecification(DATA_SPEC specification) {

    Assertions.assertThat(actual.getSpecification())
        .overridingErrorMessage("Expected data domain specification to be <%s> but was <%s>", specification, actual.getSpecification())
        .isEqualTo(specification);

    return myself;
  }

  public DataDomainAssert<DATA_SPEC> hasRandomizer(FluentRandom randomizer) {
    Assertions.assertThat(actual.getRandomizer())
        .overridingErrorMessage("Expected data domain randomizer to be <%s> but was <%s>", randomizer, actual.getSpecification())
        .isEqualTo(randomizer);

    return myself;
  }

  public DataDomainAssert<DATA_SPEC> containsDatasetFor(Class<?> type) {

    DataKey<?> key = DataKey.makeDefault(type);

    Assertions.assertThat(actual.datasetKeys()).overridingErrorMessage("Expected data domain to have a dataset for <%s> but none was found", type)
        .contains(key);

    return myself;
  }

  public DataDomainAssert<DATA_SPEC> containsDatasetFor(DataKey<?> key) {

    Assertions.assertThat(actual.datasetKeys()).overridingErrorMessage("Expected data domain to have a dataset for <%s> but none was found", key)
        .contains(key);

    return myself;
  }

  public DataDomainAssert<DATA_SPEC> doesNotContainDatasetFor(Class<?> type) {

    DataKey<?> key = DataKey.makeDefault(type);

    Assertions.assertThat(actual.datasetKeys()).overridingErrorMessage("Expected data domain to have a dataset for <%s> but none was found", type)
        .doesNotContain(key);

    return myself;
  }

  public DataDomainAssert<DATA_SPEC> doesNotcontainDatasetFor(DataKey<?> key) {

    Assertions.assertThat(actual.datasetKeys()).overridingErrorMessage("Expected data domain to have a dataset for <%s> but none was found", key)
        .doesNotContain(key);

    return myself;
  }

  public <X> InnerDataSetAssert<X> theDataSet(Class<X> type) {

    DataSet<X> dataset = actual.dataset(type);

    if (dataset != null) {
      // validating coherence
      DataKey<X> key = DataKey.makeDefault(type);
      Assertions.assertThat(actual.datasetKeys()).overridingErrorMessage("The key<%s>  is missing from this data domain dataset keys.", key)
          .contains(key);
      Assertions.assertThat(actual.datasets()).overridingErrorMessage("No dataset found for type <%s> in this domain dataset list", type)
          .contains(dataset);
    }

    return new InnerDataSetAssert<X>(dataset);
  }

  public class InnerDataSetAssert<X> extends AbstractDataSetAssert<InnerDataSetAssert<X>, X> {
    protected InnerDataSetAssert(DataSet<X> actual) {
      super(actual, InnerDataSetAssert.class);
    }

    public DataDomainAssert<DATA_SPEC> andTheDomain() {
      return DataDomainAssert.this.myself;
    }
  }

}
