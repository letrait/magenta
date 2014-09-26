package org.magenta.core.injection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;

import org.junit.Test;

public class FieldInjectorUtilsTest {

  public Object publicField;
  protected Object protectedField;
  Object packageField;
  private Object privateField;

  private final Object finalField = null;

  @Test
  public void shouldBeAbleToInjectAValueToAPublicField() throws NoSuchFieldException, SecurityException {

    //setup fixtures
    Object expected = new Object();

    Field f = FieldInjectorUtilsTest.class.getDeclaredField("publicField");

    //exercise SUT
    FieldInjectorUtils.injectInto(this, f, expected);

    //verify outcome
    assertThat(publicField).isEqualTo(expected);
  }

  @Test
  public void shouldBeAbleToInjectAValueToAProtectedField() throws NoSuchFieldException, SecurityException {

    //setup fixtures

    Object expected = new Object();

    Field f = FieldInjectorUtilsTest.class.getDeclaredField("protectedField");

    //exercise SUT
    FieldInjectorUtils.injectInto(this, f, expected);

    //verify outcome
    assertThat(protectedField).isEqualTo(expected);
  }

  @Test
  public void shouldBeAbleToInjectAValueToAPackageField() throws NoSuchFieldException, SecurityException {

    //setup fixtures
    Object expected = new Object();

    Field f = FieldInjectorUtilsTest.class.getDeclaredField("packageField");

    //exercise SUT
    FieldInjectorUtils.injectInto(this, f, expected);

    //verify outcome
    assertThat(packageField).isEqualTo(expected);
  }

  @Test
  public void shouldBeAbleToInjectAValueToAPrivateField() throws NoSuchFieldException, SecurityException {

    //setup fixtures
    Object expected = new Object();

    Field f = FieldInjectorUtilsTest.class.getDeclaredField("privateField");

    //exercise SUT
    FieldInjectorUtils.injectInto(this, f, expected);

    //verify outcome
    assertThat(privateField).isEqualTo(expected);
  }


  @Test
  public void shouldBeAbleToSetNull() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    Field f = FieldInjectorUtilsTest.class.getDeclaredField("privateField");

    //exercise SUT
    FieldInjectorUtils.injectInto(this, f, null);

    //verify outcome
    assertThat(privateField).isNull();
  }

  @Test
  public void shouldFailGracefullyWhenAttemptingToInjectIntoAFinalField() throws NoSuchFieldException, SecurityException{
    //setup fixtures
    Field f = FieldInjectorUtilsTest.class.getDeclaredField("finalField");

    //exercise SUT
    try{
      FieldInjectorUtils.injectInto(this, f, new Object());
    }catch(RuntimeException re){
      fail("expecting a  "+RuntimeException.class.getName());
    }
  }

}
