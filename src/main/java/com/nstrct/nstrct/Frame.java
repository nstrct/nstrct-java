package com.nstrct.nstrct;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;
import org.joou.Unsigned;

public class Frame {

  public static final short FRAME_START = 0x55;
  public static final short FRAME_END = 0xAA;

  public static long checksum(byte[] buffer) {
    CRC32 crc = new CRC32();
    crc.update(buffer);
    return crc.getValue();
  }

  public static boolean available(ByteBuffer buffer) {
    if(buffer.capacity() >= 1) {
      if(Unsigned.ubyte(buffer.get()).shortValue() != FRAME_START) {
        throw new RuntimeException("invalid frame start");
      }

      if(buffer.capacity() >= 3) {
        int payload_length = buffer.getShort();

        if(buffer.capacity() >= payload_length+8) {
          if(Unsigned.ubyte(buffer.get(buffer.capacity()-1)).shortValue() != FRAME_END) {
            throw new RuntimeException("invalid frame end");
          }

          return true;
        }
      }
    }

    return false;
  }

  public static Frame parse(ByteBuffer buffer) {
    buffer.order(ByteOrder.BIG_ENDIAN);

    if(!Frame.available(buffer)) {
      throw new RuntimeException("no frame available");
    }

    buffer.position(0);

    buffer.get(); // skip frame start
    int payload_length = Unsigned.ushort(buffer.getShort()).intValue();

    byte payload[] = new byte[payload_length];
    buffer.get(payload, 0, payload_length);

    long checksum = Unsigned.uint(buffer.getInt()).longValue();
    buffer.get(); // skip frame end

    if(checksum != Frame.checksum(payload)) {
      throw new RuntimeException("checksum invalid");
    }

    return new Frame(Instruction.parse(ByteBuffer.wrap(payload)));
  }

  public Instruction instruction;

  public Frame(Instruction instruction) {
    this.instruction = instruction;
  }

  public int length() {
    return this.instruction.length()+8;
  }

  public byte[] pack() {
    ByteBuffer buffer = ByteBuffer.allocate(this.length());
    buffer.order(ByteOrder.BIG_ENDIAN);

    byte payload[] = this.instruction.pack();

    buffer.put((byte)FRAME_START);
    buffer.putShort((short)this.instruction.length());
    buffer.put(payload);
    buffer.putInt((int) Frame.checksum(payload));
    buffer.put((byte)FRAME_END);

    return buffer.array();
  }

  @Override
  public String toString() {
    return "<#Frame instruction="+this.instruction.toString()+">";
  }
}
