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
package com.icongtai.zebra.encoding.impl.deltastrings;


import com.icongtai.zebra.encoding.ValuesWriter;
import com.icongtai.zebra.encoding.bytes.BytesInput;
import com.icongtai.zebra.encoding.bytes.Binary;
import com.icongtai.zebra.encoding.format.EncodeType;
import com.icongtai.zebra.encoding.impl.delta.DeltaIntBitPackingValuesWriter;
import com.icongtai.zebra.encoding.impl.deltalengthbytearray.DeltaLengthByteArrayValuesWriter;

/**
 * Write prefix lengths using delta encoding, followed by suffixes with Delta length byte arrays
 * <pre>
 *   {@code
 *   delta-length-byte-array : prefix-length* suffixes*
 *   } 
 * </pre>
 *
 */
public class DeltaByteArrayWriter extends ValuesWriter {

  private ValuesWriter prefixLengthWriter;
  private ValuesWriter suffixWriter;
  private byte[] previous;

  public DeltaByteArrayWriter(int initialCapacity) {
    this.prefixLengthWriter = new DeltaIntBitPackingValuesWriter(DeltaIntBitPackingValuesWriter.DEFAULT_NUM_BLOCK_VALUES, DeltaIntBitPackingValuesWriter.DEFAULT_NUM_MINIBLOCKS, initialCapacity);
    this.suffixWriter = new DeltaLengthByteArrayValuesWriter(initialCapacity);
    this.previous = new byte[0];
  }

  @Override
  public long getBufferedSize() {
    return prefixLengthWriter.getBufferedSize() + suffixWriter.getBufferedSize();
  }

  @Override
  public BytesInput getBytes() {
    return BytesInput.concat(prefixLengthWriter.getBytes(), suffixWriter.getBytes());
  }

  @Override
  public EncodeType getEncoding() {
    return EncodeType.delta_byte_array;
  }

  @Override
  public void reset() {
    prefixLengthWriter.reset();
    suffixWriter.reset();
  }

  @Override
  public long getAllocatedSize() {
    return prefixLengthWriter.getAllocatedSize() + suffixWriter.getAllocatedSize();
  }

  @Override
  public String memUsageString(String prefix) {
    prefix = prefixLengthWriter.memUsageString(prefix);
    return suffixWriter.memUsageString(prefix + "  DELTA_STRINGS");
  }

  @Override
  public void writeBytes(Binary v) {
    int i = 0;
    byte[] vb = v.getBytes();
    int length = previous.length < vb.length ? previous.length : vb.length;
    for(i = 0; (i < length) && (previous[i] == vb[i]); i++);
    prefixLengthWriter.writeInteger(i);
    suffixWriter.writeBytes(Binary.fromByteArray(vb, i, vb.length - i));
    previous = vb;
  }
}
