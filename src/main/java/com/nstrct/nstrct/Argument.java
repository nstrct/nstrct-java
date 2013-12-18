package com.nstrct.nstrct;

import java.nio.ByteBuffer;
import java.util.ArrayList;

public class Argument {

  public static Argument parse(ByteBuffer buffer) {
    Argument argument = new Argument();
    argument.datatype = Value.datatypeFromInt((int)buffer.get());

    if(argument.datatype == Value.Datatype.Array) {
      argument.elementDatatype = Value.datatypeFromInt((int)buffer.get());
      byte numElements = buffer.get();

      for(int i=0; i<numElements; i++) {
        argument.values.add(Value.parse(argument.elementDatatype, buffer));
      }
    } else {
      argument.values.add(Value.parse(argument.datatype, buffer));
    }

    return argument;
  }

  public Value.Datatype datatype;
  public Value.Datatype elementDatatype;
  public ArrayList<Value> values;

  public Argument(Value.Datatype datatype, boolean asArrray, Value ... values) {
    this.values = new ArrayList<Value>();
    if(asArrray) {
      this.datatype = Value.Datatype.Array;
      this.elementDatatype = datatype;
      for(int i=0; i<values.length; i++) {
        this.values.add(values[i]);
      }
    } else {
      this.datatype = datatype;
      this.elementDatatype = Value.Datatype.Unset;
      this.values.add(values[0]);
    }
  }

  public Argument() {
    this.datatype = Value.Datatype.Unset;
    this.elementDatatype = Value.Datatype.Unset;
    this.values = new ArrayList<Value>();
  }

  public boolean isArray() {
    return this.datatype == Value.Datatype.Array;
  }

  public int length() {
    int counter = 1;
    if(this.isArray()) {
      counter += 2;
      for(Value value : this.values) {
        counter += value.length(this.elementDatatype);
      }
    } else {
      counter += this.values.get(0).length(this.datatype);
    }
    return counter;
  }

  public void pack(ByteBuffer buffer) {
    buffer.put((byte)this.datatype.getValue());
    if (this.isArray()) {
      buffer.put((byte)this.elementDatatype.getValue());
      buffer.put((byte)this.values.size());
      for(Value value : this.values) {
        value.pack(this.elementDatatype, buffer);
      }
    } else {
      this.values.get(0).pack(this.datatype, buffer);
    }
  }

  @Override
  public String toString() {
    return "<#Argument datatype="+this.datatype.toString()+" elementDatatype="+this.elementDatatype.toString()+" values="+values.toString()+">";
  }
}
