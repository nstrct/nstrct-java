package com.nstrct.nstrct;

import org.apache.commons.cli.*;
import org.joou.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CrossPlatformTest {

  public static void main(String args[]) {
    Options options = new Options();
    options.addOption("g", false, "process bytes at STDIN and return result");
    options.addOption("p", false, "generate bytes and write to STDOUT");

    CommandLineParser parser = new BasicParser();
    try {
      CommandLine cmd = parser.parse(options, args);
      if(cmd.hasOption("g")) {
        Instruction i = new Instruction(UShort.MAX_VALUE);
        i.arguments.add(new Argument(Value.Datatype.Boolean, false, new Value(false)));
        i.arguments.add(new Argument(Value.Datatype.Int8, false, new Value(Byte.MIN_VALUE)));
        i.arguments.add(new Argument(Value.Datatype.Int16, false, new Value(Short.MIN_VALUE)));
        i.arguments.add(new Argument(Value.Datatype.Int32, false, new Value(Integer.MIN_VALUE)));
        i.arguments.add(new Argument(Value.Datatype.Int64, false, new Value(Long.MIN_VALUE)));
        i.arguments.add(new Argument(Value.Datatype.uInt8, false, new Value(Unsigned.ubyte(UByte.MAX_VALUE))));
        i.arguments.add(new Argument(Value.Datatype.uInt16, false, new Value(Unsigned.ushort(UShort.MAX_VALUE))));
        i.arguments.add(new Argument(Value.Datatype.uInt32, false, new Value(Unsigned.uint(UInteger.MAX_VALUE))));
        i.arguments.add(new Argument(Value.Datatype.uInt64, false, new Value(Unsigned.ulong(ULong.MAX_VALUE))));
        i.arguments.add(new Argument(Value.Datatype.Float32, false, new Value(Float.MAX_VALUE)));
        i.arguments.add(new Argument(Value.Datatype.Float64, false, new Value(Double.MAX_VALUE)));
        i.arguments.add(new Argument(Value.Datatype.String, false, new Value("hello world")));
        i.arguments.add(new Argument(Value.Datatype.String, false, new Value("")));
        i.arguments.add(new Argument(Value.Datatype.uInt16, true, new Value(Unsigned.ushort(2443)), new Value(Unsigned.ushort(3443))));

        try {
          byte[] data = i.pack();
          ByteBuffer buffer = ByteBuffer.allocate(data.length+4);
          buffer.order(ByteOrder.BIG_ENDIAN);
          buffer.putInt(data.length);
          buffer.put(data);
          System.out.write(buffer.array());
        } catch (IOException e) {
          System.out.println("generating failed");
        }
      } else if (cmd.hasOption("p")) {
        try {
          byte[] lengthData = new byte[4];
          System.in.read(lengthData);
          ByteBuffer lengthBuffer = ByteBuffer.wrap(lengthData);
          int length = lengthBuffer.getInt();
          byte[] data = new byte[length];
          System.in.read(data);
          ByteBuffer buffer = ByteBuffer.wrap(data);
          Instruction i = Instruction.parse(buffer);
          _assert("    boolean value error", i.arguments.get(0).values.get(0).booleanValue.equals(Boolean.FALSE));
          _assert("    int8 value error", i.arguments.get(1).values.get(0).signedNumberValue.equals(Byte.MIN_VALUE));
          _assert("    int16 value error", i.arguments.get(2).values.get(0).signedNumberValue.equals(Short.MIN_VALUE));
          _assert("    int32 value error", i.arguments.get(3).values.get(0).signedNumberValue.equals(Integer.MIN_VALUE));
          _assert("    int64 value error", i.arguments.get(4).values.get(0).signedNumberValue.equals(Long.MIN_VALUE));
          _assert("    uint8 value error", i.arguments.get(5).values.get(0).unsignedNumberValue.equals(Unsigned.ubyte(UByte.MAX_VALUE)));
          _assert("    uint16 value error", i.arguments.get(6).values.get(0).unsignedNumberValue.equals(Unsigned.ushort(UShort.MAX_VALUE)));
          _assert("    uint32 value error", i.arguments.get(7).values.get(0).unsignedNumberValue.equals(Unsigned.uint(UInteger.MAX_VALUE)));
          _assert("    uint64 value error", i.arguments.get(8).values.get(0).unsignedNumberValue.equals(Unsigned.ulong(ULong.MAX_VALUE)));
          _assert("    float32 value error", i.arguments.get(9).values.get(0).signedNumberValue.equals(Float.MAX_VALUE));
          _assert("    loat64 value error", i.arguments.get(10).values.get(0).signedNumberValue.equals(Double.MAX_VALUE));
          _assert("    string value error", i.arguments.get(11).values.get(0).stringValue.equals("hello world"));
          _assert("    string value error", i.arguments.get(12).values.get(0).stringValue.equals(""));
          _assert("    array uint16 value 1 error", i.arguments.get(13).values.get(0).unsignedNumberValue.equals(Unsigned.ushort(2443)));
          _assert("    array uint16 value 2 error", i.arguments.get(13).values.get(1).unsignedNumberValue.equals(Unsigned.ushort(3443)));
          System.out.println("    tests passed");
        } catch (IOException e) {
          System.out.println("process failed");
        }
      }
    } catch (ParseException e) {
      System.out.println("please specify a run mod");
    }
  }

  public static void _assert(String test, boolean exp) {
    if(!exp) {
      System.out.println(test);
      System.exit(1);
    }
  }

}
