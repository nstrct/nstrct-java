# nstrct-java

**a multi-purpose binary protocol for instruction interchange**

Interchange formats like json or xml are great to keep data visible, but due to their parse and pack complexity they aren't used in embedded applications. There are alternatives like msgpack or Google's protocol buffer, which allow a more binary representation of data, but these protcols are still heavy and developers tend to rather implement their own 'simple' binary protocols instead of porting or using the big ones. 

The protcol **nstrct** is designed to be an alternative in those situations where a tiny and versatile protocol is needed. The main transportable unit in nstrct are **instructions** which carry a dynamic amount of data in the form of **arguments**. Each instruction is identified by a code between 0-65535 which can be used by two communicating applications to identify the blueprint of the message. Arguments support all standard types of integers(boolean, int8-64, uint8-64, float32/64), strings, and arrays of integers or strings. There is no support for hashes by design to keep the pack and unpacking functions as small as possible. [Check the main repository for the binary layout of the instructions](http://github.com/nstrct/nstrct).

**Start using nstrct by getting the library for your favorite programming language:**

* [C/C++ (nstrct-c)](http://github.com/nstrct/nstrct-c)
* [Ruby (nstrct-ruby)](http://github.com/nstrct/nstrct-ruby)
* [Obj-C (nstrct-objc)](http://github.com/nstrct/nstrct-objc)
* [Java (nstrct-c)](http://github.com/nstrct/nstrct-java)

_This software has been open sourced by [ElectricFeel Mobility Systems Gmbh](http://electricfeel.com) and is further maintained by [Joël Gähwiler](http://github.com/256dpi)._

## Instruction Composing

```java
// basic example
Instruction instruction = new Instruction(12);
instruction.arguments.add(new Argument(Value.Datatype.uInt8, false, new Value(253)));
Frame frame = new Frame(instruction);
byte[] bytes = frame.pack();

// advaned example
Instruction instruction = new Instruction(233);
instruction.arguments.add(new Argument(Value.Datatype.Int64, false, new Value(87347323)));
instruction.arguments.add(new Argument(Value.Datatype.String, false, new Value("hello there")));
instruction.arguments.add(new Argument(Value.Datatype.Float32, true, new Value(5.4), new Value(5.3), new Value(5.1)));
Frame frame = new Frame(instruction);
byte[] bytes = frame.pack();
```

## Instruction Processing

```java
Instruction instruction = Frame.parse(bytes).instruction;
instruction.code;
instruction.arguments.get(0).datatype;
instruction.arguments.get(0).value.unsignedNumberValue;
```
