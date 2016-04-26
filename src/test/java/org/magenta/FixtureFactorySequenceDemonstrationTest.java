package org.magenta;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Test;
import org.magenta.annotation.InjectSequence;
import org.magenta.testing.domain.playing.cards.Card;
import org.magenta.testing.domain.playing.cards.Kind;

import com.google.common.base.Supplier;

public class FixtureFactorySequenceDemonstrationTest {

  @Test
  public void testGenerateFullCardDeck(){
    FixtureFactory fixtures = createCardFixtures();

    //test
    List<Card> cards= fixtures.dataset(Card.class).list();

    //exercise
    assertThat(cards).extracting("kind",Kind.class).containsOnly(Kind.values());
    assertThat(cards).extracting("value",Integer.class).containsOnly(1,2,3,4,5,6,7,8,9,10,11,12,13);
    assertThat(cards).hasSize(52);
  }

  @Test
  public void testGenerateAllFaces(){
    FixtureFactory fixtures = createCardFixtures();

    //test
    List<Card> cards= fixtures.restrictTo(11,12,13).dataset(Card.class).list();

    //exercise
    cards.forEach(c->System.out.println(c));

    assertThat(cards).extracting("kind",Kind.class).containsOnly(Kind.values());
    assertThat(cards).extracting("value",Integer.class).containsExactly(11,12,13,11,12,13,11,12,13,11,12,13);
    assertThat(cards).hasSize(12);

  }

  @Test
  public void testGenerateAllReds(){
    //setup
    FixtureFactory fixtures = createCardFixtures();

    //test
    List<Card> cards= fixtures.restrictTo(Kind.HEART,Kind.DIAMOND).dataset(Card.class).list();

    //exercise
    cards.forEach(c->System.out.println(c));

    assertThat(cards).extracting("kind",Kind.class).containsOnly(Kind.HEART,Kind.DIAMOND);
    assertThat(cards).extracting("value",Integer.class).containsOnly(1,2,3,4,5,6,7,8,9,10,11,12,13);
    assertThat(cards).hasSize(26);

  }

  private FixtureFactory createCardFixtures() {
    FixtureFactory fixtures = Magenta.newFixture();
    fixtures.newDataSetOf(Kind.values());
    fixtures.newDataSetOf(1,2,3,4,5,6,7,8,9,10,11,12,13);
    fixtures.newDataSet(Card.class).autoMagicallyGenerated();
    return fixtures;
  }

  public static class CardGenerator implements Supplier<Card>{

    @InjectSequence
    Sequence<Kind> kinds;

    @InjectSequence
    Sequence<Integer> values;

    @Override
    public Card get() {
      Card c = new Card();
      c.setKind(kinds.next());
      c.setValue(values.next());
      return c;
    }
  }
}
