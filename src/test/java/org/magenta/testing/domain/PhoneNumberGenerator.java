package org.magenta.testing.domain;

import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;

public class PhoneNumberGenerator implements Supplier<PhoneNumber>{

  @Override
  public PhoneNumber get() {
    PhoneNumber phoneNumber = new PhoneNumber();
    phoneNumber.setPhoneNumber(FluentRandom.singleton().strings().generateFromExample("555-528-7878"));

    return phoneNumber;
  }

}
