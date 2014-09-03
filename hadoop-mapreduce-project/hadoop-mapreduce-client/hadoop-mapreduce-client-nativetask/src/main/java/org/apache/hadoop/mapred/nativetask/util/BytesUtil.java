begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Longs
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|BytesUtil
specifier|public
class|class
name|BytesUtil
block|{
DECL|field|HEX_CHARS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEX_CHARS
init|=
literal|"0123456789abcdef"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
comment|/**    * Converts a big-endian byte array to a long value.    *    * @param bytes array of bytes    * @param offset offset into array    */
DECL|method|toLong (byte[] bytes, int offset)
specifier|public
specifier|static
name|long
name|toLong
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|Longs
operator|.
name|fromBytes
argument_list|(
name|bytes
index|[
name|offset
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|4
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|5
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|6
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|7
index|]
argument_list|)
return|;
block|}
comment|/**    * Convert a big-endian integer from a byte array to a primitive value.    * @param bytes the array to parse from    * @param offset the offset in the array    */
DECL|method|toInt (byte[] bytes, int offset)
specifier|public
specifier|static
name|int
name|toInt
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|Ints
operator|.
name|fromBytes
argument_list|(
name|bytes
index|[
name|offset
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|1
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|2
index|]
argument_list|,
name|bytes
index|[
name|offset
operator|+
literal|3
index|]
argument_list|)
return|;
block|}
comment|/**    * Presumes float encoded as IEEE 754 floating-point "single format"    * @param bytes byte array    * @return Float made from passed byte array.    */
DECL|method|toFloat (byte [] bytes)
specifier|public
specifier|static
name|float
name|toFloat
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
name|toFloat
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * Presumes float encoded as IEEE 754 floating-point "single format"    * @param bytes array to convert    * @param offset offset into array    * @return Float made from passed byte array.    */
DECL|method|toFloat (byte [] bytes, int offset)
specifier|public
specifier|static
name|float
name|toFloat
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|)
block|{
return|return
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|toInt
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * @param bytes byte array    * @return Return double made from passed bytes.    */
DECL|method|toDouble (final byte [] bytes)
specifier|public
specifier|static
name|double
name|toDouble
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|)
block|{
return|return
name|toDouble
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * @param bytes byte array    * @param offset offset where double is    * @return Return double made from passed bytes.    */
DECL|method|toDouble (final byte [] bytes, final int offset)
specifier|public
specifier|static
name|double
name|toDouble
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|toLong
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Write a printable representation of a byte array.    *    * @param b byte array    * @return string    * @see #toStringBinary(byte[], int, int)    */
DECL|method|toStringBinary (final byte [] b)
specifier|public
specifier|static
name|String
name|toStringBinary
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
return|return
literal|"null"
return|;
return|return
name|toStringBinary
argument_list|(
name|b
argument_list|,
literal|0
argument_list|,
name|b
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**    * Write a printable representation of a byte array. Non-printable    * characters are hex escaped in the format \\x%02X, eg:    * \x00 \x05 etc    *    * @param b array to write out    * @param off offset to start at    * @param len length to write    * @return string output    */
DECL|method|toStringBinary (final byte [] b, int off, int len)
specifier|public
specifier|static
name|String
name|toStringBinary
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Just in case we are passed a 'len' that is> buffer length...
if|if
condition|(
name|off
operator|>=
name|b
operator|.
name|length
condition|)
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
if|if
condition|(
name|off
operator|+
name|len
operator|>
name|b
operator|.
name|length
condition|)
name|len
operator|=
name|b
operator|.
name|length
operator|-
name|off
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|off
init|;
name|i
operator|<
name|off
operator|+
name|len
condition|;
operator|++
name|i
control|)
block|{
name|int
name|ch
init|=
name|b
index|[
name|i
index|]
operator|&
literal|0xFF
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|>=
literal|'0'
operator|&&
name|ch
operator|<=
literal|'9'
operator|)
operator|||
operator|(
name|ch
operator|>=
literal|'A'
operator|&&
name|ch
operator|<=
literal|'Z'
operator|)
operator|||
operator|(
name|ch
operator|>=
literal|'a'
operator|&&
name|ch
operator|<=
literal|'z'
operator|)
operator|||
literal|" `~!@#$%^&*()-_=+[]{}|;:'\",.<>/?"
operator|.
name|indexOf
argument_list|(
name|ch
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
operator|(
name|char
operator|)
name|ch
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|"\\x"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|HEX_CHARS
index|[
operator|(
name|ch
operator|>>
literal|4
operator|)
operator|&
literal|0x0F
index|]
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|HEX_CHARS
index|[
name|ch
operator|&
literal|0x0F
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Convert a boolean to a byte array. True becomes -1    * and false becomes 0.    *    * @param b value    * @return<code>b</code> encoded in a byte array.    */
DECL|method|toBytes (final boolean b)
specifier|public
specifier|static
name|byte
index|[]
name|toBytes
parameter_list|(
specifier|final
name|boolean
name|b
parameter_list|)
block|{
return|return
operator|new
name|byte
index|[]
block|{
name|b
condition|?
operator|(
name|byte
operator|)
operator|-
literal|1
else|:
operator|(
name|byte
operator|)
literal|0
block|}
return|;
block|}
comment|/**    * @param f float value    * @return the float represented as byte []    */
DECL|method|toBytes (final float f)
specifier|public
specifier|static
name|byte
index|[]
name|toBytes
parameter_list|(
specifier|final
name|float
name|f
parameter_list|)
block|{
comment|// Encode it as int
return|return
name|Ints
operator|.
name|toByteArray
argument_list|(
name|Float
operator|.
name|floatToRawIntBits
argument_list|(
name|f
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * Serialize a double as the IEEE 754 double format output. The resultant    * array will be 8 bytes long.    *    * @param d value    * @return the double represented as byte []    */
DECL|method|toBytes (final double d)
specifier|public
specifier|static
name|byte
index|[]
name|toBytes
parameter_list|(
specifier|final
name|double
name|d
parameter_list|)
block|{
comment|// Encode it as a long
return|return
name|Longs
operator|.
name|toByteArray
argument_list|(
name|Double
operator|.
name|doubleToRawLongBits
argument_list|(
name|d
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

