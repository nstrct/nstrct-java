package com.nstrct.nstrct;

import org.joou.Unsigned;
import org.junit.Test;
import junit.framework.Assert;

public class TestInstruction {

  public static void dumpBytes(byte[] bytes) {
    System.out.print(':');
    for (int i = 0; i < bytes.length; i++ ) {
      System.out.print(bytes[i]);
      System.out.print(':');
    }
    System.out.println();
  }

  public void proofInstruction(Instruction instruction1) {
    Instruction instruction2 = Instruction.parse(instruction1.pack());
    dumpBytes(instruction1.pack());
    Assert.assertEquals(instruction2.toString(), instruction1.toString());
  }

  @Test
  public void testBasicInstruction() {
    Instruction instruction = new Instruction(11);
    this.proofInstruction(instruction);
  }

  @Test
  public void testInstruction() {
    Instruction instruction = new Instruction(12);
    instruction.arguments.add(new Argument(Value.Datatype.uInt8, false, new Value(Unsigned.ubyte(253))));
    this.proofInstruction(instruction);
  }

  @Test
  public void testBigInstruction() {
    Instruction instruction = new Instruction(233);
    instruction.arguments.add(new Argument(Value.Datatype.Int64, false, new Value(87347323)));
    instruction.arguments.add(new Argument(Value.Datatype.String, false, new Value("hello there")));
    instruction.arguments.add(new Argument(Value.Datatype.Float32, true, new Value(5.4), new Value(5.3), new Value(5.1)));
    this.proofInstruction(instruction);
  }

}
