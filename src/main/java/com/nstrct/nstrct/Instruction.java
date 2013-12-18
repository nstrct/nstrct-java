package com.nstrct.nstrct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Instruction {

  public static Instruction parse(byte[] bytes) {
    return Instruction.parse(ByteBuffer.wrap(bytes));
  }

  public static Instruction parse(ByteBuffer buffer) {
    buffer.order(ByteOrder.BIG_ENDIAN);
    Instruction instruction = new Instruction(buffer.getShort());
    int numArguments = buffer.get();
    buffer.getShort(); // take out unneeded total number of array elements

    for(int i=0; i<numArguments; i++) {
      instruction.arguments.add(Argument.parse(buffer));
    }

    return instruction;
  }

  public int code;
  public ArrayList<Argument> arguments;

  public Instruction(int code) {
    this.code = code;
    this.arguments = new ArrayList<Argument>();
  }

  public short countTotalArrayElements() {
    short counter = 0;
    for(Argument argument : this.arguments) {
      if(argument.datatype == Value.Datatype.Array) {
        counter += argument.values.size();
      }
    }
    return counter;
  }

  public int length() {
    int counter = 5;
    for(Argument argument: this.arguments) {
      counter += argument.length();
    }
    return counter;
  }

  public byte[] pack() {
    ByteBuffer buffer = ByteBuffer.allocate(this.length());
    buffer.order(ByteOrder.BIG_ENDIAN);
    buffer.putShort((short)this.code);
    buffer.put((byte)this.arguments.size());
    buffer.putShort(this.countTotalArrayElements());

    for(Argument argument : this.arguments) {
      argument.pack(buffer);
    }

    return buffer.array();
  }

  @Override
  public String toString() {
    return "<#Instruction code="+this.code+" args="+this.arguments.toString()+">";
  }
}
