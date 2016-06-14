package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.assertj.core.util.Lists;
import org.junit.Test;
import org.magenta.annotation.InjectDataSet;
import org.magenta.random.FluentRandom;

import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

public class FixtureFactoryInitUseCaseTest {

  @Test
  public void testInit() {

    //setup
    FixtureFactory fixture = Magenta.newFixture();

    int expectedSize = 7;

    fixture.newDataSet(Owner.class).generatedBy(new OwnerGenerator(),5);
    fixture.newDataSet(Pet.class).generatedBy(new PetGenerator(), expectedSize);

    //exercise sut

    List<Owner> owners = fixture.init(Pet.class).dataset(Owner.class).filter(o->!o.getPets().isEmpty()).list();

    //verify outcome
    assertThat(owners).isNotEmpty();
    assertThat(FluentIterable.from(owners).transformAndConcat(o->o.getPets())).hasSize(expectedSize);

  }

  @Test
  public void testInitWithSize() {

    //setup
    FixtureFactory fixture = Magenta.newFixture();
    int expectedSize = 12;

    fixture.newDataSet(Owner.class).generatedBy(new OwnerGenerator(), 5);
    fixture.newDataSet(Pet.class).generatedBy(new PetGenerator());

    //exercise sut

    List<Owner> owners = fixture.init(Pet.class, expectedSize).dataset(Owner.class).filter(o->!o.getPets().isEmpty()).list();

    //verify outcome
    assertThat(owners).isNotEmpty();
    assertThat(FluentIterable.from(owners).transformAndConcat(o->o.getPets())).hasSize(expectedSize);

  }

  @Test
  public void testMultipleInitWithSize() {

    //setup
    FixtureFactory fixture = Magenta.newFixture();
    int expectedSize = 12;

    fixture.newDataSet(Owner.class).generatedBy(new OwnerGenerator());
    fixture.newDataSet(Pet.class).generatedBy(new PetGenerator());

    //exercise sut

    List<Owner> owners = fixture.init(Owner.class,3).init(Pet.class, expectedSize).dataset(Owner.class).filter(o->!o.getPets().isEmpty()).list();

    //verify outcome
    assertThat(owners).isNotEmpty();
    assertThat(FluentIterable.from(owners).transformAndConcat(o->o.getPets())).hasSize(expectedSize);

  }

  public static class Pet{
    private Owner owner;
    private String name;
    public Owner getOwner() {
      return owner;
    }
    public void setOwner(Owner owner) {
      this.owner = owner;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }



  }

  public static class Owner {

    private List<Pet> pets = Lists.newArrayList();
    private String name;

    public List<Pet> getPets() {
      return pets;
    }
    public void setPets(List<Pet> pets) {
      this.pets = pets;
    }
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
  }

  public static class OwnerGenerator implements Supplier<Owner>{

    @Override
    public Owner get() {
      Owner owner = new Owner();
      owner.setName(FluentRandom.strings().charabia(10));

      return owner;
    }

  }

  public static class PetGenerator implements Supplier<Pet>{

    @InjectDataSet
    public DataSet<Owner> owners;

    @Override
    public Pet get() {

      Pet pet = new Pet();
      pet.setName(FluentRandom.strings().charabia(10));

      pet.setOwner(owners.any());

      pet.getOwner().getPets().add(pet);

      return pet;
    }

  }
}
