package org.magenta.testing;

import org.assertj.core.api.Assertions;
import org.magenta.Fixture;
import org.magenta.DataSet;
import org.magenta.DataSpecification;

public class MagentaAssertions extends Assertions {

  public static <S extends DataSpecification> DataDomainAssert<S> assertThat(Fixture<S> actual) {
    return new DataDomainAssert<S>(actual);
  }

  public static <D> DataSetAssert<D> assertThat(DataSet<D> actual) {
    return new DataSetAssert<D>(actual);
  }
}
