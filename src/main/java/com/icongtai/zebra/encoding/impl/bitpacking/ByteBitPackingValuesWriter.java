/* 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.icongtai.zebra.encoding.impl.bitpacking;


import com.icongtai.zebra.encoding.ValuesWriter;
import com.icongtai.zebra.encoding.bitpacking.ByteBasedBitPackingEncoder;
import com.icongtai.zebra.encoding.bitpacking.Packer;
import com.icongtai.zebra.encoding.bytes.BytesInput;
import com.icongtai.zebra.encoding.bytes.BytesUtils;
import com.icongtai.zebra.encoding.exception.ZebraEncodingException;
import com.icongtai.zebra.encoding.format.EncodeType;

import java.io.IOException;


/**
 * 基于二进制的编码，取一组数中最大的bit宽度。编码压缩率和性能很高.
 * 考虑到大部分数据有重复性，可用{@link com.icongtai.zebra.encoding.impl.rle.RunLengthBitPackingHybridValuesWriter}编码取代
 */
public class ByteBitPackingValuesWriter extends ValuesWriter {

  private final Packer packer;
  private final int bitWidth;
  private ByteBasedBitPackingEncoder encoder;

  public ByteBitPackingValuesWriter(int bound, Packer packer) {
    this.packer = packer;
    this.bitWidth = BytesUtils.getWidthFromMaxInt(bound);
    this.encoder = new ByteBasedBitPackingEncoder(bitWidth, packer);
  }

  @Override
  public void writeInteger(int v) {
    try {
      this.encoder.writeInt(v);
    } catch (IOException e) {
      throw new ZebraEncodingException(e);
    }
  }

  @Override
  public EncodeType getEncoding() {
    return EncodeType.bit_packing;
  }

  @Override
  public BytesInput getBytes() {
    try {
      return encoder.toBytes();
    } catch (IOException e) {
      throw new ZebraEncodingException(e);
    }
  }

  @Override
  public void reset() {
    encoder = new ByteBasedBitPackingEncoder(bitWidth, packer);
  }

  @Override
  public long getBufferedSize() {
    return encoder.getBufferSize();
  }

  @Override
  public long getAllocatedSize() {
    return encoder.getAllocatedSize();
  }

  @Override
  public String memUsageString(String prefix) {
    return encoder.memUsageString(prefix);
  }

}
