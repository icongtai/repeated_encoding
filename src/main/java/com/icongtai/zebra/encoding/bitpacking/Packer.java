package com.icongtai.zebra.encoding.bitpacking;/*


/**
 *
 */
public enum Packer {

  /**
   * packers who fill the Least Significant Bit First
   * int and byte packer have the same result on Big Endian architectures
   */
  BIG_ENDIAN {
    @Override
    public IntPacker newIntPacker(int width) {
      return beIntPackerFactory.newIntPacker(width);
    }
    @Override
    public BytePacker newBytePacker(int width) {
      return beBytePackerFactory.newBytePacker(width);
    }
    @Override
    public BytePackerForLong newBytePackerForLong(int width) {
      return beBytePackerForLongFactory.newBytePackerForLong(width);
    }
  },

  /**
   * packers who fill the Most Significant Bit first
   * int and byte packer have the same result on Little Endian architectures
   */
  LITTLE_ENDIAN {
    @Override
    public IntPacker newIntPacker(int width) {
      return leIntPackerFactory.newIntPacker(width);
    }
    @Override
    public BytePacker newBytePacker(int width) {
      return leBytePackerFactory.newBytePacker(width);
    }
    @Override
    public BytePackerForLong newBytePackerForLong(int width) {
      return leBytePackerForLongFactory.newBytePackerForLong(width);
    }
  };

  private static IntPackerFactory getIntPackerFactory(String name) {
    return (IntPackerFactory)getStaticField("com.icongtai.zebra.encoding.bitpacking." + name, "factory");
  }

  private static BytePackerFactory getBytePackerFactory(String name) {
    return (BytePackerFactory)getStaticField("com.icongtai.zebra.encoding.bitpacking." + name, "factory");
  }

  private static BytePackerForLongFactory getBytePackerForLongFactory(String name) {
    return (BytePackerForLongFactory)getStaticField("com.icongtai.zebra.encoding.bitpacking." + name, "factory");
  }

  private static Object getStaticField(String className, String fieldName) {
    try {
      return Class.forName(className).getField(fieldName).get(null);
    } catch (IllegalArgumentException e) {
      throw new RuntimeException(e);
    } catch (IllegalAccessException e) {
      throw new RuntimeException(e);
    } catch (NoSuchFieldException e) {
      throw new RuntimeException(e);
    } catch (SecurityException e) {
      throw new RuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  static IntPackerFactory beIntPackerFactory = getIntPackerFactory("LemireBitPackingBE");
  static IntPackerFactory leIntPackerFactory = getIntPackerFactory("LemireBitPackingLE");
  static BytePackerFactory beBytePackerFactory = getBytePackerFactory("ByteBitPackingBE");
  static BytePackerFactory leBytePackerFactory = getBytePackerFactory("ByteBitPackingLE");
  static BytePackerForLongFactory beBytePackerForLongFactory = getBytePackerForLongFactory("ByteBitPackingForLongBE");
  static BytePackerForLongFactory leBytePackerForLongFactory = getBytePackerForLongFactory("ByteBitPackingForLongLE");

  /**
   * @param width the width in bits of the packed values
   * @return an int based packer
   */
  public abstract IntPacker newIntPacker(int width);

  /**
   * @param width the width in bits of the packed values
   * @return a byte based packer
   */
  public abstract BytePacker newBytePacker(int width);

  /**
   * @param width the width in bits of the packed values
   * @return a byte based packer for INT64
   */
  public abstract BytePackerForLong newBytePackerForLong(int width);
}
