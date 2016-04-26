package org.magenta.core.data.supplier;

import org.magenta.Fixture;
import org.magenta.Sequence;
import org.magenta.core.FixtureContext;

/**
 * TODO a better name for this class...
 * @author ngagnon
 *
 * @param <D>
 */
@Deprecated
public class SequenceWithFixtureContextManagementDecorator<D> implements Sequence<D> {

  private final Sequence<D> delegate;
  private final Fixture fixture;
  private final FixtureContext context;

  public SequenceWithFixtureContextManagementDecorator(Sequence<D> delegate, Fixture fixture, FixtureContext context){
    this.delegate = delegate;
    this.fixture = fixture;
    this.context = context;
  }

  @Override
  public D next() {
    boolean managed = false;
    try {
      if(!context.isSet()){
        managed = true;
        context.set(fixture);
      }
      return delegate.next();
    } finally{
      if(managed){
        context.clear();
      }
    }
  }

  @Override
  public int size() {
    boolean managed = false;
    try {
      if(!context.isSet()){
        managed = true;
        context.set(fixture);
      }
      return delegate.size();
    } finally{
      if(managed){
        context.clear();
      }
    }
  }



  @Override
  public String toString(){
    return "FixtureContextManagement decorator of [" + delegate+"]";
  }





}
