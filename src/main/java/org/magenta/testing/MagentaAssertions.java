package org.magenta.testing;

import org.fest.assertions.api.Assertions;
import org.magenta.DataDomain;
import org.magenta.DataSet;
import org.magenta.DataSpecification;

public class MagentaAssertions extends Assertions {

  public static <S extends DataSpecification> DataDomainAssert<S> assertThat(DataDomain<S> actual) {
    return new DataDomainAssert<S>(actual);
  }

  public static <D> DataSetAssert<D> assertThat(DataSet<D> actual) {
    return new DataSetAssert<D>(actual);
  }
}
