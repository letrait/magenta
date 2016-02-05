package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;

import com.google.common.reflect.TypeToken;

public class DataKeyTest {

  @Test
  public void testOfAClass(){

    //setup fixture

    DataKey<String> sut = DataKey.of(String.class);

    //exercise sut and verify outcome
    assertThat(sut.getQualifier()).isEqualTo(DataKey.DEFAULT);
    assertThat(sut.getType()).isEqualTo(TypeToken.of(String.class));
    assertThat(sut.toString()).contains(String.class.getName());


  }

  @Test
  public void testOfAClassAndAQualifier(){

    //setup fixture

    DataKey<String> sut = DataKey.of("qualifier", String.class);

    //exercise sut and verify outcome
    assertThat(sut.getQualifier()).isEqualTo("qualifier");
    assertThat(sut.getType()).isEqualTo(TypeToken.of(String.class));
    assertThat(sut.toString()).contains(String.class.getName());

  }

  @Test
  public void testOfATypeToken(){

    //setup fixture

    DataKey<List<String>> sut = DataKey.of(new TypeToken<List<String>>(){});

    //exercise sut and verify outcome
    assertThat(sut.getQualifier()).isEqualTo(DataKey.DEFAULT);
    assertThat(sut.getType()).isEqualTo(new TypeToken<List<String>>(){});
    assertThat(sut.toString()).contains("List","String");

  }
}
