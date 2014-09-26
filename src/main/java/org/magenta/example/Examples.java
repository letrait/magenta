package org.magenta.example;

import static org.fest.assertions.api.Assertions.assertThat;

import java.awt.Color;
import java.util.List;

import org.junit.Test;
import org.magenta.DataDomain;
import org.magenta.DataDomainManager;
import org.magenta.DataKey;
import org.magenta.DataSet;
import org.magenta.Generator;
import org.magenta.SimpleDataSpecification;
import org.magenta.core.EmptyDataSet;
import org.magenta.example.domain.Car;
import org.magenta.example.domain.Owner;
import org.magenta.example.domain.Trip;
import org.magenta.example.generators.CarGenerator;
import org.magenta.example.generators.ColorGenerator;
import org.magenta.example.generators.TripGenerator;
import org.magenta.random.RandomBuilder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

public class Examples {

  @Test
  public void creating_a_new_data_domain(){

    title("creating_a_new_data_domain");
    DataDomainManager<SimpleDataSpecification> primitives = DataDomainManager.newRoot("primitives", SimpleDataSpecification.create(),RandomBuilder.PROVIDER.singleton());

    out("a datadomain that will contain various java primitives (this is certainly not a real-life case) : %s", primitives);

    primitives.newDataSet(Integer.class).composedOf(1,2,3,4,5);
    primitives.newDataSet(String.class).composedOf("cat","dog","mouse");
    primitives.newDataSet(Float.class).composedOf(0.5f,0.75f,1.25f);

    out("Integers : %s", primitives.dataset(Integer.class).list());
    out("Strings : %s", primitives.dataset(String.class).list());
    out("Floats : %s", primitives.dataset(Float.class).list());

    assertThat(primitives.dataset(Integer.class).get()).containsExactly(1,2,3,4,5);
    assertThat(primitives.dataset(String.class).get()).containsExactly("cat","dog","mouse");
    assertThat(primitives.dataset(Float.class).get()).containsExactly(0.5f,0.75f,1.25f);

  }

  @Test
  public void creating_a_new_child_data_domain(){

    title("creating_a_new_child_data_domain");
    DataDomainManager<SimpleDataSpecification> parent = DataDomainManager.newRoot("parent", SimpleDataSpecification.create(), RandomBuilder.PROVIDER.singleton());

    parent.newDataSet(Integer.class).composedOf(1,2,3,4,5);
    parent.newDataSet(String.class).composedOf("cat","dog","mouse");

    DataDomainManager<SimpleDataSpecification> child = parent.newNode("child");
    child.newDataSet(Integer.class).composedOf(6,7,8,9);

    out("parent Integers : %s", parent.dataset(Integer.class).list());
    out("parent Strings : %s", parent.dataset(String.class).list());

    out("child Integers : %s", child.dataset(Integer.class).list());
    out("child Strings (inherited from parent): %s", child.dataset(String.class).list());

  }


  @Test
  public void creating_a_new_data_domain_with_dataset_of_the_same_type(){

    title("creating_a_new_data_domain_with_dataset_of_the_same_type");
    DataDomainManager<SimpleDataSpecification> animals = DataDomainManager.newRoot("primitives", SimpleDataSpecification.create(), RandomBuilder.PROVIDER.singleton());

    out("a datadomain that will contain various dataset composed of string : %s", animals);

    DataKey<String> HOUSE_ANIMALS = DataKey.makeQualified("house animals", String.class);
    DataKey<String> FARM_ANIMALS = DataKey.makeQualified("farm animals", String.class);
    DataKey<String> ZOO_ANIMALS = DataKey.makeQualified("zoo animals", String.class);
    DataKey<String> ANIMALS = DataKey.makeQualified("animals", String.class);


    animals.newDataSet(HOUSE_ANIMALS).composedOf("cat","dog","mouse");
    animals.newDataSet(FARM_ANIMALS).composedOf("cow","pig","horse");
    animals.newDataSet(ZOO_ANIMALS).composedOf("zebra","lion","elephant");
    animals.newDataSet(ANIMALS).materalizedFrom(HOUSE_ANIMALS,FARM_ANIMALS,ZOO_ANIMALS);

    out("house animals : %s", animals.dataset(HOUSE_ANIMALS).list());
    out("farm animals : %s", animals.dataset(FARM_ANIMALS).list());
    out("zoo animals : %s", animals.dataset(ZOO_ANIMALS).list());
    out("all animals : %s", animals.dataset(ANIMALS).list());
  }

  @Test
  public void manipulating_a_dataset() {
    title("useful_method_on_a_dataset");

    DataDomain<SimpleDataSpecification> domain = Fixtures.rgb();

    DataSet<Color> colors = domain.dataset(Color.class);

    Color any = colors.any();
    out("either RED, GREEN or BLUE : %s", any);

    Color anyButBlue = colors.without(Color.BLUE).any();
    out("anything but BLUE : %s",anyButBlue);

    List<Color> listOfColors = colors.list();
    out("List containing exactly RED, GREEN, BLUE : %s",listOfColors);

    List<Color> listOfTwoColors = colors.list(2);
    out("List containing exactly RED, GREENE : %s",listOfTwoColors);

    List<Color> randomListOfColors = colors.randomList();
    out("List containing in any order RED, GREEN, BLUE : %s",randomListOfColors);

    Color[] arrayOfColors = colors.array();

    out("Array of colors containing exactly RED, GREEN, BLUE : %s %s %s",(Object[])arrayOfColors);
  }

  @Test
  public void using_function_on_dataset(){
    DataDomain<ExampleDataSpecification> domain = Fixtures.multicolor();

    DataSet<Color> colors = domain.dataset(Color.class);

    DataSet<Color> reddish = colors.filter(new Predicate<Color>(){

      @Override
      public boolean apply(Color input) {
        return input.getRed()>128;
      }

    });

    out("Colors having a red component greater than 128 : %s",reddish.list());

    DataSet<Integer> redValues = reddish.transform(new Function<Color,Integer>(){

      @Override
      public Integer apply(Color input) {
        return input.getRed();
      }

    }, Integer.class);

    out("Transforming colors to their red compenents : %s",redValues.list());


  }

  @Test
  public void using_simple_generation_strategy(){
    title("using_simple_generation_strategy");

    DataDomainManager<ExampleDataSpecification> domain = DataDomainManager.newRoot("colors", new ExampleDataSpecification(), RandomBuilder.PROVIDER.singleton());
    domain.newDataSet(Color.class).generatedBy(new ColorGenerator());

    DataSet<Color> aDataSetOfColors = domain.dataset(Color.class);

    Color any = aDataSetOfColors.any();
    out("a generated color : %s", any);
  }

  @Test
  public void distinction_between_a_generated_dataset_and_a_generator() {
    title("\ndistinction_between_a_generated_dataset_and_a_generator\n");
    DataDomain<ExampleDataSpecification> multicolors = Fixtures.multicolor();

    List<Color> listOfColors = multicolors.dataset(Color.class).list();
    out("A list of %d colors : %s",multicolors.getSpecification().getDefaultNumberOfItems(),listOfColors);

    List<Color> listOfSameColors = multicolors.dataset(Color.class).list();
    out("A list of the same %d colors (it is a generated dataset, not a generator!) : %s",multicolors.getSpecification().getDefaultNumberOfItems(),listOfSameColors);

    Generator<Color> aGeneratorOfColors = multicolors.generator(Color.class);

    listOfColors = aGeneratorOfColors.list();
    out("A list of %d colors : %s",multicolors.getSpecification().getDefaultNumberOfItems(),listOfColors);

    List<Color> listOfDifferentColors = aGeneratorOfColors.list();
    out("A list of NOT the same %d colors (it is a generator!) : %s",multicolors.getSpecification().getDefaultNumberOfItems(),listOfDifferentColors);

  }

  @Test
  public void configuring_data_generation_from_a_specification(){
    title("configuring_data_generation_from_a_specification");

    DataDomain<ExampleDataSpecification> multicolors = Fixtures.multicolor();

    out("This data domain is using the following spec : %s",multicolors.getSpecification());

    DataSet<Color> aGeneratorOfColors = multicolors.generator(Color.class);

    multicolors.getSpecification().noReds();

    out("A color with no red component : %s", aGeneratorOfColors.any());

    multicolors.getSpecification().noBlues();

    out("A color between black and green: %s", aGeneratorOfColors.any());
  }

  @Test
  public void reusing_existing_datasets_when_generating_data(){
    title("reusing_existing_datasets_when_generating_data");

    DataDomain<ExampleDataSpecification> automotives = Fixtures.automotives();

    out("Here are the list of available colors : %s",automotives.dataset(Color.class).list());
    out("Here are the cars generated with them : %s",automotives.dataset(Car.class).list());
    out("Take a look at the generation strategy : %s", CarGenerator.class.getName());
  }

  @Test
  public void generating_objects_having_bidirectional_associations(){

    title("generating_objects_having_bidirectional_associations");
    DataDomain<ExampleDataSpecification> automotives = Fixtures.automotives();
    out("Here are an owner with its generated cars: %s",automotives.dataset(Owner.class).any());
    out("The car is associated to its owner in the  %s strategy", CarGenerator.class.getName());
    out("The annotation means 'generate also cars when you need to generate owners'");
  }

  @Test
  public void restricting_generated_dataset(){
    title("restricting_generated_dataset");

    DataDomainManager<ExampleDataSpecification> automotives = Fixtures.automotives();

    out("random blue cars : %s",automotives.restrictTo(Color.BLUE).dataset(Car.class).list());
    out("random Ford cars : %s",automotives.restrictTo(Car.Maker.FORD).dataset(Car.class).list());
    out("random green or pink chevrolet cars : %s",automotives.restrictTo(Color.GREEN, Color.PINK, Car.Maker.CHEVROLET).dataset(Car.class).list());

    //using "restrictTo" is a convenient way of replacing generated data by predetermined ones
    //you may also do the following, but it is longer and less intention-revealing:
    DataDomainManager<ExampleDataSpecification> node=automotives.newNode("child");
    node.newDataSet(Color.class).composedOf(Color.GREEN, Color.PINK);
    node.newDataSet(Car.Maker.class).composedOf(Car.Maker.CHEVROLET);

    out("other example of random green or pink chevrolet cars : %s",node.dataset(Car.class).list());
  }

  @Test
  public void restricting_generated_dataset_part2(){
    title("restricting_generated_dataset_part2");

    DataDomainManager<ExampleDataSpecification> automotives = Fixtures.automotives();

    out("random drivers having a random number of blue cars : %s",automotives.restrictTo(Color.BLUE).dataset(Owner.class).any());

  }


  @Test
  public void using_implicit_generation_strategy(){
    title("using_implicit_generation_strategy");

    DataDomainManager<ExampleDataSpecification> automotives = Fixtures.automotives();

    automotives.newDataSet(Trip.class).generatedAsIterableBy(new TripGenerator());
    out("List of cars : %s",automotives.dataset(Car.class).list());

    out("At least one trip was generated for each car : %s", automotives.dataset(Trip.class).list());
    out("Look at the %s generation strategy, look how it returns a collection instead of a single element ",TripGenerator.class);
  }

  @Test
  public void restricting_on_empty_dataset(){
    title("restricting_on_empty_dataset");

    DataDomainManager<ExampleDataSpecification> automotives = Fixtures.automotives();

    automotives.newDataSet(Trip.class).generatedAsIterableBy(new TripGenerator());
    out("No trip were generated : %s",automotives.restrictTo(EmptyDataSet.ofType(Car.class)).dataset(Trip.class).isEmpty());

  }

  private void title(String title, Object... args) {
    System.out.println("====================================================");
    out(title,args);
    System.out.println("====================================================");
  }

  private static void out(String message, Object... args) {
    System.out.println(String.format(message, args));
  }
}
