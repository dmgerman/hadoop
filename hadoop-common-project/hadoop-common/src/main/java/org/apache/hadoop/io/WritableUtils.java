begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|InterfaceStability
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
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPOutputStream
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|WritableUtils
specifier|public
specifier|final
class|class
name|WritableUtils
block|{
DECL|method|readCompressedByteArray (DataInput in)
specifier|public
specifier|static
name|byte
index|[]
name|readCompressedByteArray
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
return|return
literal|null
return|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// could/should use readFully(buffer,0,length)?
name|GZIPInputStream
name|gzi
init|=
operator|new
name|GZIPInputStream
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|buffer
operator|.
name|length
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|outbuf
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|int
name|len
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|gzi
operator|.
name|read
argument_list|(
name|outbuf
argument_list|,
literal|0
argument_list|,
name|outbuf
operator|.
name|length
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|outbuf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|decompressed
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|bos
operator|.
name|close
argument_list|()
expr_stmt|;
name|gzi
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|decompressed
return|;
block|}
DECL|method|skipCompressedByteArray (DataInput in)
specifier|public
specifier|static
name|void
name|skipCompressedByteArray
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|!=
operator|-
literal|1
condition|)
block|{
name|skipFully
argument_list|(
name|in
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|writeCompressedByteArray (DataOutput out, byte[] bytes)
specifier|public
specifier|static
name|int
name|writeCompressedByteArray
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|byte
index|[]
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|bytes
operator|!=
literal|null
condition|)
block|{
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|GZIPOutputStream
name|gzout
init|=
operator|new
name|GZIPOutputStream
argument_list|(
name|bos
argument_list|)
decl_stmt|;
try|try
block|{
name|gzout
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|gzout
operator|.
name|close
argument_list|()
expr_stmt|;
name|gzout
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|gzout
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|buffer
init|=
name|bos
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|buffer
operator|.
name|length
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
comment|/* debug only! Once we have confidence, can lose this. */
return|return
operator|(
operator|(
name|bytes
operator|.
name|length
operator|!=
literal|0
operator|)
condition|?
operator|(
literal|100
operator|*
name|buffer
operator|.
name|length
operator|)
operator|/
name|bytes
operator|.
name|length
else|:
literal|0
operator|)
return|;
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
block|}
comment|/* Ugly utility, maybe someone else can do this better  */
DECL|method|readCompressedString (DataInput in)
specifier|public
specifier|static
name|String
name|readCompressedString
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|bytes
init|=
name|readCompressedByteArray
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|new
name|String
argument_list|(
name|bytes
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
DECL|method|writeCompressedString (DataOutput out, String s)
specifier|public
specifier|static
name|int
name|writeCompressedString
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|writeCompressedByteArray
argument_list|(
name|out
argument_list|,
operator|(
name|s
operator|!=
literal|null
operator|)
condition|?
name|s
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
else|:
literal|null
argument_list|)
return|;
block|}
comment|/*    *    * Write a String as a Network Int n, followed by n Bytes    * Alternative to 16 bit read/writeUTF.    * Encoding standard is... ?    *     */
DECL|method|writeString (DataOutput out, String s)
specifier|public
specifier|static
name|void
name|writeString
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|s
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|buffer
init|=
name|s
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|buffer
operator|.
name|length
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Read a String as a Network Int n, followed by n Bytes    * Alternative to 16 bit read/writeUTF.    * Encoding standard is... ?    *    */
DECL|method|readString (DataInput in)
specifier|public
specifier|static
name|String
name|readString
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|length
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|length
operator|==
operator|-
literal|1
condition|)
return|return
literal|null
return|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
comment|// could/should use readFully(buffer,0,length)?
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
comment|/*    * Write a String array as a Nework Int N, followed by Int N Byte Array Strings.    * Could be generalised using introspection.    *    */
DECL|method|writeStringArray (DataOutput out, String[] s)
specifier|public
specifier|static
name|void
name|writeStringArray
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
index|[]
name|s
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|s
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeString
argument_list|(
name|out
argument_list|,
name|s
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Write a String array as a Nework Int N, followed by Int N Byte Array of    * compressed Strings. Handles also null arrays and null values.    * Could be generalised using introspection.    *    */
DECL|method|writeCompressedStringArray (DataOutput out, String[] s)
specifier|public
specifier|static
name|void
name|writeCompressedStringArray
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
index|[]
name|s
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
return|return;
block|}
name|out
operator|.
name|writeInt
argument_list|(
name|s
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|s
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|writeCompressedString
argument_list|(
name|out
argument_list|,
name|s
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/*    * Write a String array as a Nework Int N, followed by Int N Byte Array Strings.    * Could be generalised using introspection. Actually this bit couldn't...    *    */
DECL|method|readStringArray (DataInput in)
specifier|public
specifier|static
name|String
index|[]
name|readStringArray
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|len
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|s
index|[
name|i
index|]
operator|=
name|readString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/*    * Write a String array as a Nework Int N, followed by Int N Byte Array Strings.    * Could be generalised using introspection. Handles null arrays and null values.    *    */
DECL|method|readCompressedStringArray (DataInput in)
specifier|public
specifier|static
name|String
index|[]
name|readCompressedStringArray
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|len
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
return|return
literal|null
return|;
name|String
index|[]
name|s
init|=
operator|new
name|String
index|[
name|len
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|s
index|[
name|i
index|]
operator|=
name|readCompressedString
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/*    *    * Test Utility Method Display Byte Array.     *    */
DECL|method|displayByteArray (byte[] record)
specifier|public
specifier|static
name|void
name|displayByteArray
parameter_list|(
name|byte
index|[]
name|record
parameter_list|)
block|{
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|record
operator|.
name|length
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|%
literal|16
operator|==
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|record
index|[
name|i
index|]
operator|>>
literal|4
operator|&
literal|0x0F
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|record
index|[
name|i
index|]
operator|&
literal|0x0F
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|","
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|record
index|[
name|i
index|]
operator|>>
literal|4
operator|&
literal|0x0F
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|record
index|[
name|i
index|]
operator|&
literal|0x0F
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/**    * Make a copy of a writable object using serialization to a buffer.    * @param orig The object to copy    * @return The copied object    */
DECL|method|clone (T orig, Configuration conf)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Writable
parameter_list|>
name|T
name|clone
parameter_list|(
name|T
name|orig
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
try|try
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|// Unchecked cast from Class to Class<T>
name|T
name|newInst
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
operator|(
name|Class
argument_list|<
name|T
argument_list|>
operator|)
name|orig
operator|.
name|getClass
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|ReflectionUtils
operator|.
name|copy
argument_list|(
name|conf
argument_list|,
name|orig
argument_list|,
name|newInst
argument_list|)
expr_stmt|;
return|return
name|newInst
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Error writing/reading clone buffer"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Make a copy of the writable object using serialization to a buffer    * @param dst the object to copy from    * @param src the object to copy into, which is destroyed    * @throws IOException    * @deprecated use ReflectionUtils.cloneInto instead.    */
annotation|@
name|Deprecated
DECL|method|cloneInto (Writable dst, Writable src)
specifier|public
specifier|static
name|void
name|cloneInto
parameter_list|(
name|Writable
name|dst
parameter_list|,
name|Writable
name|src
parameter_list|)
throws|throws
name|IOException
block|{
name|ReflectionUtils
operator|.
name|cloneWritableInto
argument_list|(
name|dst
argument_list|,
name|src
argument_list|)
expr_stmt|;
block|}
comment|/**    * Serializes an integer to a binary stream with zero-compressed encoding.    * For -120<= i<= 127, only one byte is used with the actual value.    * For other values of i, the first byte value indicates whether the    * integer is positive or negative, and the number of bytes that follow.    * If the first byte value v is between -121 and -124, the following integer    * is positive, with number of bytes that follow are -(v+120).    * If the first byte value v is between -125 and -128, the following integer    * is negative, with number of bytes that follow are -(v+124). Bytes are    * stored in the high-non-zero-byte-first order.    *    * @param stream Binary output stream    * @param i Integer to be serialized    * @throws java.io.IOException     */
DECL|method|writeVInt (DataOutput stream, int i)
specifier|public
specifier|static
name|void
name|writeVInt
parameter_list|(
name|DataOutput
name|stream
parameter_list|,
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|writeVLong
argument_list|(
name|stream
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
comment|/**    * Serializes a long to a binary stream with zero-compressed encoding.    * For -112<= i<= 127, only one byte is used with the actual value.    * For other values of i, the first byte value indicates whether the    * long is positive or negative, and the number of bytes that follow.    * If the first byte value v is between -113 and -120, the following long    * is positive, with number of bytes that follow are -(v+112).    * If the first byte value v is between -121 and -128, the following long    * is negative, with number of bytes that follow are -(v+120). Bytes are    * stored in the high-non-zero-byte-first order.    *     * @param stream Binary output stream    * @param i Long to be serialized    * @throws java.io.IOException     */
DECL|method|writeVLong (DataOutput stream, long i)
specifier|public
specifier|static
name|void
name|writeVLong
parameter_list|(
name|DataOutput
name|stream
parameter_list|,
name|long
name|i
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|i
operator|>=
operator|-
literal|112
operator|&&
name|i
operator|<=
literal|127
condition|)
block|{
name|stream
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|len
init|=
operator|-
literal|112
decl_stmt|;
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|i
operator|^=
operator|-
literal|1L
expr_stmt|;
comment|// take one's complement'
name|len
operator|=
operator|-
literal|120
expr_stmt|;
block|}
name|long
name|tmp
init|=
name|i
decl_stmt|;
while|while
condition|(
name|tmp
operator|!=
literal|0
condition|)
block|{
name|tmp
operator|=
name|tmp
operator|>>
literal|8
expr_stmt|;
name|len
operator|--
expr_stmt|;
block|}
name|stream
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|len
argument_list|)
expr_stmt|;
name|len
operator|=
operator|(
name|len
operator|<
operator|-
literal|120
operator|)
condition|?
operator|-
operator|(
name|len
operator|+
literal|120
operator|)
else|:
operator|-
operator|(
name|len
operator|+
literal|112
operator|)
expr_stmt|;
for|for
control|(
name|int
name|idx
init|=
name|len
init|;
name|idx
operator|!=
literal|0
condition|;
name|idx
operator|--
control|)
block|{
name|int
name|shiftbits
init|=
operator|(
name|idx
operator|-
literal|1
operator|)
operator|*
literal|8
decl_stmt|;
name|long
name|mask
init|=
literal|0xFFL
operator|<<
name|shiftbits
decl_stmt|;
name|stream
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
operator|(
name|i
operator|&
name|mask
operator|)
operator|>>
name|shiftbits
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Reads a zero-compressed encoded long from input stream and returns it.    * @param stream Binary input stream    * @throws java.io.IOException     * @return deserialized long from stream.    */
DECL|method|readVLong (DataInput stream)
specifier|public
specifier|static
name|long
name|readVLong
parameter_list|(
name|DataInput
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
name|firstByte
init|=
name|stream
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|decodeVIntSize
argument_list|(
name|firstByte
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|1
condition|)
block|{
return|return
name|firstByte
return|;
block|}
name|long
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|len
operator|-
literal|1
condition|;
name|idx
operator|++
control|)
block|{
name|byte
name|b
init|=
name|stream
operator|.
name|readByte
argument_list|()
decl_stmt|;
name|i
operator|=
name|i
operator|<<
literal|8
expr_stmt|;
name|i
operator|=
name|i
operator||
operator|(
name|b
operator|&
literal|0xFF
operator|)
expr_stmt|;
block|}
return|return
operator|(
name|isNegativeVInt
argument_list|(
name|firstByte
argument_list|)
condition|?
operator|(
name|i
operator|^
operator|-
literal|1L
operator|)
else|:
name|i
operator|)
return|;
block|}
comment|/**    * Reads a zero-compressed encoded integer from input stream and returns it.    * @param stream Binary input stream    * @throws java.io.IOException     * @return deserialized integer from stream.    */
DECL|method|readVInt (DataInput stream)
specifier|public
specifier|static
name|int
name|readVInt
parameter_list|(
name|DataInput
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|n
init|=
name|readVLong
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|n
operator|>
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|||
operator|(
name|n
operator|<
name|Integer
operator|.
name|MIN_VALUE
operator|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"value too long to fit in integer"
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|n
return|;
block|}
comment|/**    * Reads an integer from the input stream and returns it.    *    * This function validates that the integer is between [lower, upper],    * inclusive.    *    * @param stream Binary input stream    * @throws java.io.IOException    * @return deserialized integer from stream    */
DECL|method|readVIntInRange (DataInput stream, int lower, int upper)
specifier|public
specifier|static
name|int
name|readVIntInRange
parameter_list|(
name|DataInput
name|stream
parameter_list|,
name|int
name|lower
parameter_list|,
name|int
name|upper
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|n
init|=
name|readVLong
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|n
operator|<
name|lower
condition|)
block|{
if|if
condition|(
name|lower
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"expected non-negative integer, got "
operator|+
name|n
argument_list|)
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"expected integer greater than or equal to "
operator|+
name|lower
operator|+
literal|", got "
operator|+
name|n
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|n
operator|>
name|upper
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"expected integer less or equal to "
operator|+
name|upper
operator|+
literal|", got "
operator|+
name|n
argument_list|)
throw|;
block|}
return|return
operator|(
name|int
operator|)
name|n
return|;
block|}
comment|/**    * Given the first byte of a vint/vlong, determine the sign    * @param value the first byte    * @return is the value negative    */
DECL|method|isNegativeVInt (byte value)
specifier|public
specifier|static
name|boolean
name|isNegativeVInt
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
return|return
name|value
operator|<
operator|-
literal|120
operator|||
operator|(
name|value
operator|>=
operator|-
literal|112
operator|&&
name|value
operator|<
literal|0
operator|)
return|;
block|}
comment|/**    * Parse the first byte of a vint/vlong to determine the number of bytes    * @param value the first byte of the vint/vlong    * @return the total number of bytes (1 to 9)    */
DECL|method|decodeVIntSize (byte value)
specifier|public
specifier|static
name|int
name|decodeVIntSize
parameter_list|(
name|byte
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|>=
operator|-
literal|112
condition|)
block|{
return|return
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|value
operator|<
operator|-
literal|120
condition|)
block|{
return|return
operator|-
literal|119
operator|-
name|value
return|;
block|}
return|return
operator|-
literal|111
operator|-
name|value
return|;
block|}
comment|/**    * Get the encoded length if an integer is stored in a variable-length format    * @return the encoded length     */
DECL|method|getVIntSize (long i)
specifier|public
specifier|static
name|int
name|getVIntSize
parameter_list|(
name|long
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|>=
operator|-
literal|112
operator|&&
name|i
operator|<=
literal|127
condition|)
block|{
return|return
literal|1
return|;
block|}
if|if
condition|(
name|i
operator|<
literal|0
condition|)
block|{
name|i
operator|^=
operator|-
literal|1L
expr_stmt|;
comment|// take one's complement'
block|}
comment|// find the number of bytes with non-leading zeros
name|int
name|dataBits
init|=
name|Long
operator|.
name|SIZE
operator|-
name|Long
operator|.
name|numberOfLeadingZeros
argument_list|(
name|i
argument_list|)
decl_stmt|;
comment|// find the number of data bytes + length byte
return|return
operator|(
name|dataBits
operator|+
literal|7
operator|)
operator|/
literal|8
operator|+
literal|1
return|;
block|}
comment|/**    * Read an Enum value from DataInput, Enums are read and written     * using String values.     * @param<T> Enum type    * @param in DataInput to read from     * @param enumType Class type of Enum    * @return Enum represented by String read from DataInput    * @throws IOException    */
DECL|method|readEnum (DataInput in, Class<T> enumType)
specifier|public
specifier|static
parameter_list|<
name|T
extends|extends
name|Enum
argument_list|<
name|T
argument_list|>
parameter_list|>
name|T
name|readEnum
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|Class
argument_list|<
name|T
argument_list|>
name|enumType
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|T
operator|.
name|valueOf
argument_list|(
name|enumType
argument_list|,
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
return|;
block|}
comment|/**    * writes String value of enum to DataOutput.     * @param out Dataoutput stream    * @param enumVal enum value    * @throws IOException    */
DECL|method|writeEnum (DataOutput out, Enum<?> enumVal)
specifier|public
specifier|static
name|void
name|writeEnum
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|Enum
argument_list|<
name|?
argument_list|>
name|enumVal
parameter_list|)
throws|throws
name|IOException
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|enumVal
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Skip<i>len</i> number of bytes in input stream<i>in</i>    * @param in input stream    * @param len number of bytes to skip    * @throws IOException when skipped less number of bytes    */
DECL|method|skipFully (DataInput in, int len)
specifier|public
specifier|static
name|void
name|skipFully
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|total
init|=
literal|0
decl_stmt|;
name|int
name|cur
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|total
operator|<
name|len
operator|)
operator|&&
operator|(
operator|(
name|cur
operator|=
name|in
operator|.
name|skipBytes
argument_list|(
name|len
operator|-
name|total
argument_list|)
operator|)
operator|>
literal|0
operator|)
condition|)
block|{
name|total
operator|+=
name|cur
expr_stmt|;
block|}
if|if
condition|(
name|total
operator|<
name|len
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not able to skip "
operator|+
name|len
operator|+
literal|" bytes, possibly "
operator|+
literal|"due to end of input."
argument_list|)
throw|;
block|}
block|}
comment|/** Convert writables to a byte array */
DECL|method|toByteArray (Writable... writables)
specifier|public
specifier|static
name|byte
index|[]
name|toByteArray
parameter_list|(
name|Writable
modifier|...
name|writables
parameter_list|)
block|{
specifier|final
name|DataOutputBuffer
name|out
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Writable
name|w
range|:
name|writables
control|)
block|{
name|w
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Fail to convert writables to a byte array"
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|out
operator|.
name|getData
argument_list|()
return|;
block|}
comment|/**    * Read a string, but check it for sanity. The format consists of a vint    * followed by the given number of bytes.    * @param in the stream to read from    * @param maxLength the largest acceptable length of the encoded string    * @return the bytes as a string    * @throws IOException if reading from the DataInput fails    * @throws IllegalArgumentException if the encoded byte size for string               is negative or larger than maxSize. Only the vint is read.    */
DECL|method|readStringSafely (DataInput in, int maxLength )
specifier|public
specifier|static
name|String
name|readStringSafely
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|int
name|maxLength
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
block|{
name|int
name|length
init|=
name|readVInt
argument_list|(
name|in
argument_list|)
decl_stmt|;
if|if
condition|(
name|length
argument_list|<
literal|0
operator|||
name|length
argument_list|>
name|maxLength
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Encoded byte size for String was "
operator|+
name|length
operator|+
literal|", which is outside of 0.."
operator|+
name|maxLength
operator|+
literal|" range."
argument_list|)
throw|;
block|}
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|length
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
name|Text
operator|.
name|decode
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

