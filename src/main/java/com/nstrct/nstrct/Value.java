package com.nstrct.nstrct;

import org.joou.UNumber;
import org.joou.Unsigned;

import java.nio.ByteBuffer;
import java.util.HashMap;

public class Value {

  public static enum Datatype {
    Unset(-1), Boolean(1),
    Int8(10), Int16(11), Int32(12), Int64(13),
    uInt8(14), uInt16(15), uInt32(16), uInt64(17),
    Float32(21), Float64(22),
    String(31), Array(32);

    private final int id;
    Datatype(int id) { this.id = id; }
    public int getValue() { return id; }
  }

  private static final HashMap<Integer, Datatype> intToDatatypeMap = new HashMap<Integer, Datatype>();
  static {
    for (Datatype type : Datatype.values()) {
      intToDatatypeMap.put(type.getValue(), type);
    }
  }

  public static Datatype datatypeFromInt(int i) {
    Datatype type = intToDatatypeMap.get(Integer.valueOf(i));
    if (type == null)
      return Datatype.Unset;
    return type;
  }

  public static final byte TRUE = 1;
  public static final byte FALSE = 0;

  public static Value parse(Datatype datatype, ByteBuffer buffer) {
    switch(datatype) {
      case Unset: throw new RuntimeException("unset datatype not supported");
      case Boolean: return new Value(buffer.get() == TRUE);
      case Int8: return new Value(buffer.get());
      case Int16: return new Value(buffer.getShort());
      case Int32: return new Value(buffer.getInt());
      case Int64: return new Value(buffer.getLong());
      case uInt8: return new Value(Unsigned.ubyte(buffer.get()));
      case uInt16: return new Value(Unsigned.ushort(buffer.getShort()));
      case uInt32: return new Value(Unsigned.uint(buffer.getInt()));
      case uInt64: return new Value(Unsigned.ulong(buffer.getLong()));
      case Float32: return new Value(buffer.getFloat());
      case Float64: return new Value(buffer.getDouble());
      case String: {
        int stringLength = buffer.get();
        byte[] stringContent = new byte[stringLength];
        buffer.get(stringContent, buffer.arrayOffset(), stringLength);
        return new Value(new String(stringContent));
      }
      case Array: {
        throw new RuntimeException("cannot parse datatype array as value");
      }
    }
    return null;
  }

  public Boolean booleanValue;
  public Number signedNumberValue;
  public UNumber unsignedNumberValue;
  public String stringValue;

  public Value(Boolean value) {
    this.booleanValue = value;
  }

  public Value(Number value) {
    this.signedNumberValue = value;
  }

  public Value(UNumber value) {
    this.unsignedNumberValue = value;
  }

  public Value(String value) {
    this.stringValue = value;
  }

  public int length(Datatype datatype) {
    switch(datatype) {
      case Unset: throw new RuntimeException("unset datatype not supported");
      case Boolean: return 1;
      case Int8: return 1;
      case Int16: return 2;
      case Int32: return 4;
      case Int64: return 8;
      case uInt8: return 1;
      case uInt16: return 2;
      case uInt32: return 4;
      case uInt64: return 8;
      case Float32: return 4;
      case Float64: return 8;
      case String: return 1 + this.stringValue.length();
      case Array: throw new RuntimeException("array datatype not supported");
    }
    return 0;
  }

  public void pack(Datatype datatype, ByteBuffer buffer) {
    switch(datatype) {
      case Unset: throw new RuntimeException("unset datatype not supported");
      case Boolean: buffer.put(this.booleanValue ? TRUE : FALSE); break;
      case Int8: buffer.put(this.signedNumberValue.byteValue()); break;
      case Int16: buffer.putShort(this.signedNumberValue.shortValue()); break;
      case Int32: buffer.putInt(this.signedNumberValue.intValue()); break;
      case Int64: buffer.putLong(this.signedNumberValue.longValue()); break;
      case uInt8: buffer.put(this.unsignedNumberValue.byteValue()); break;
      case uInt16: buffer.putShort(this.unsignedNumberValue.shortValue()); break;
      case uInt32: buffer.putInt(this.unsignedNumberValue.intValue()); break;
      case uInt64: buffer.putLong(this.unsignedNumberValue.longValue()); break;
      case Float32: buffer.putFloat(this.signedNumberValue.floatValue()); break;
      case Float64: buffer.putDouble(this.signedNumberValue.doubleValue()); break;
      case String: {
        buffer.put((byte)this.stringValue.length());
        buffer.put(this.stringValue.getBytes());
        break;
      }
      case Array: {
        throw new RuntimeException("cannot pack datatype array as value");
      }
    }
  }

  @Override
  public String toString() {
    return "<#Value booleanValue="+this.booleanValue+" signedNumberValue="+this.signedNumberValue+" unsignedNumberValue="+this.unsignedNumberValue+" stringValue="+this.stringValue+">";
  }
}
