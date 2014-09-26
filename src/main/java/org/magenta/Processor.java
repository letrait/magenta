package org.magenta;

import java.util.List;

/**
 * Implementation of this interface process data from a given domain when one of
 * its affected data set is being loaded.
 *
 * @author normand
 *
 *         L'implementation devrait vérifier si le processing a déjà été fait
 *         pour ce Fixture
 * @param <S>
 */
public interface Processor<S extends DataSpecification> {

  /**
   * Process any data from any dataset in the specified <code>dataDomain</code>.
   *
   * @param dataDomain
   *          the data domain
   */
  public void process(Fixture<? super S> dataDomain);

  /**
   * @return the affected data set keys
   */
  public List<DataKey<?>> getAffectedDataSetKeys();

}
