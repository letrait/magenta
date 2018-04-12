package org.magenta;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.eventbus.EventBus;

public class Magenta {

  private static Logger log = LoggerFactory.getLogger(Magenta.class);

  private static final Supplier<Dependencies> dependencies = Suppliers.memoize(() -> new Dependencies());

  private static final Supplier<EventBus> eventBus = Suppliers.memoize(() -> new EventBus());

  public static FixtureFactory newFixture() {
    return modules().fixtureFactory();
  }

  public static EventBus eventBus() {
    return eventBus.get();
  }

  public static Dependencies modules() {
    return dependencies.get();
  }

}
