package org.magenta.core;

import java.util.Arrays;
import java.util.Collection;

import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Fixture;
import org.magenta.FixtureFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

/**
 * Helper class for restriction applications. By "restrictions", we mean
 * replacing an existing dataset by a fixed one having only one or two elements.
 * The purpose of doing this is to restrain the range of a certain type of data
 * so every other dataset depending on it for their generation are forced to use
 * a small sample. Let say we have a dataset of "City" containing dozens of
 * cities. This dataset is used by a generation strategy to generate "Monument".
 * Each "Monument" generated within one of the available cities. If we want to
 * generate "Monument" for the city of Paris only, then we need to "restrict"
 * the dataset of Cities to only one element being "Paris".
 *
 * Here is a sample of code:
 *
 * <pre>
 * FixtureFactory tourismDomain = TourismDomain.createDomain();
 * City paris = CityBuilder.build(&quot;Paris&quot;); // custom logic to
 * // create/build/generate a city
 * Monument monument = tourismDomain.restrictTo(paris)
 *     .dataset(Monument.class)
 *     .any(); // existing
 * // city
 * // dataset
 * // replaced
 * // by
 * // a
 * // unique
 * // element
 * // dataset
 *
 * Assert.assertEquals(paris, monument.getCity());
 *
 * </pre>
 *
 * @author ngagnon
 *
 */
public class RestrictionHelper {

  private static final Object EMPTY = new Object();

  /**
   * Fixes corresponding dataset with the given <code>objects</code> into the
   * <code>domain</code>.
   *
   * @param domain
   *          the domain
   * @param objects
   *          an array of object
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public static void applyRestrictions(FixtureFactory domain, Object first, Object... rest) {
    Multimap<DataKey<?>, Object> multimap = LinkedHashMultimap.create();

    normalize(domain, Lists.asList(first, rest), multimap);

    for (DataKey key : multimap.keySet()) {
      Collection<Object> objs = multimap.get(key);
      if (objs.size() == 1 && objs.iterator()
          .next() == EMPTY) {
        domain.newDataSet(key)
            .composedOf();
      } else {
        domain.newDataSet(key)
            .composedOf(multimap.get(key));
      }

    }
  }

  @VisibleForTesting
  static void normalize(Fixture domain, Iterable<?> objects, Multimap<DataKey<?>, Object> multimap) {
    for (Object o : objects) {
      /*if (o instanceof QualifiedDataSet) {
        QualifiedDataSet<?> qDataSetItem = (QualifiedDataSet<?>) o;
        if (!qDataSetItem.isEmpty()) {
          multimap.putAll(qDataSetItem.getKey(), qDataSetItem.get());
        } else {

          if (multimap.get(qDataSetItem.getKey())
              .isEmpty()) {
            multimap.put(qDataSetItem.getKey(), EMPTY);
          }
          ;

        }

      } else */if (o instanceof DataSet) {
        DataSet<?> dataSetItem = (DataSet<?>) o;
        DataKey<?> key = DataKey.of(dataSetItem.getType());
        if (!dataSetItem.isEmpty()) {
          multimap.putAll(key, dataSetItem);
        } else {
          if (!multimap.containsKey(key)) {
            multimap.put(key, EMPTY);
          }

        }
      } else if (o instanceof Iterable) {
        Iterable<?> iterableItem = (Iterable<?>) o;
        normalize(domain, iterableItem, multimap);
      } else if (o.getClass()
          .isArray()) {
        Object[] arrayItem = (Object[]) o;
        normalize(domain, Arrays.asList(arrayItem), multimap);
      } else {
        DataKey<?> key = findKeyForClass(domain, o.getClass());
        if (key == null) {
          throw new IllegalArgumentException("Cannot restrict with " + o.getClass() + ", this dataset does not exist in this DataSetManager.");
        }
        multimap.put(key, o);
      }
    }
  }

  @VisibleForTesting
  static DataKey<?> findKeyForClass(Fixture fixture, Class<? extends Object> clazz) {
    DataKey<?> key = DataKey.of(clazz);
    if (fixture.keys()
        .contains(key)) {
      return key;
    }

    Class<?>[] interfaces = clazz.getInterfaces();
    for (Class<?> i : interfaces) {
      key = findKeyForClass(fixture, i);
      if (key != null) {
        return key;
      }
    }

    Class<?> parent = clazz.getSuperclass();
    if (parent != null) {
      return findKeyForClass(fixture, parent);
    }

    return null;

  }
}
