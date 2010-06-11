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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
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

begin_comment
comment|/** A WritableComparable for strings that uses the UTF8 encoding.  *   *<p>Also includes utilities for efficiently reading and writing UTF-8.  *  * @deprecated replaced by Text  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|UTF8
specifier|public
class|class
name|UTF8
implements|implements
name|WritableComparable
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|UTF8
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|IBUF
specifier|private
specifier|static
specifier|final
name|DataInputBuffer
name|IBUF
init|=
operator|new
name|DataInputBuffer
argument_list|()
decl_stmt|;
DECL|field|OBUF_FACTORY
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|DataOutputBuffer
argument_list|>
name|OBUF_FACTORY
init|=
operator|new
name|ThreadLocal
argument_list|<
name|DataOutputBuffer
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|DataOutputBuffer
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|DataOutputBuffer
argument_list|()
return|;
block|}
block|}
decl_stmt|;
DECL|field|EMPTY_BYTES
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
init|=
name|EMPTY_BYTES
decl_stmt|;
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|method|UTF8 ()
specifier|public
name|UTF8
parameter_list|()
block|{
comment|//set("");
block|}
comment|/** Construct from a given string. */
DECL|method|UTF8 (String string)
specifier|public
name|UTF8
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|set
argument_list|(
name|string
argument_list|)
expr_stmt|;
block|}
comment|/** Construct from a given string. */
DECL|method|UTF8 (UTF8 utf8)
specifier|public
name|UTF8
parameter_list|(
name|UTF8
name|utf8
parameter_list|)
block|{
name|set
argument_list|(
name|utf8
argument_list|)
expr_stmt|;
block|}
comment|/** The raw bytes. */
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|bytes
return|;
block|}
comment|/** The number of bytes in the encoded string. */
DECL|method|getLength ()
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|length
return|;
block|}
comment|/** Set to contain the contents of a string. */
DECL|method|set (String string)
specifier|public
name|void
name|set
parameter_list|(
name|String
name|string
parameter_list|)
block|{
if|if
condition|(
name|string
operator|.
name|length
argument_list|()
operator|>
literal|0xffff
operator|/
literal|3
condition|)
block|{
comment|// maybe too long
name|LOG
operator|.
name|warn
argument_list|(
literal|"truncating long string: "
operator|+
name|string
operator|.
name|length
argument_list|()
operator|+
literal|" chars, starting with "
operator|+
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|string
operator|=
name|string
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|0xffff
operator|/
literal|3
argument_list|)
expr_stmt|;
block|}
name|length
operator|=
name|utf8Length
argument_list|(
name|string
argument_list|)
expr_stmt|;
comment|// compute length
if|if
condition|(
name|length
operator|>
literal|0xffff
condition|)
comment|// double-check length
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"string too long!"
argument_list|)
throw|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|length
operator|>
name|bytes
operator|.
name|length
condition|)
comment|// grow buffer
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
try|try
block|{
comment|// avoid sync'd allocations
name|DataOutputBuffer
name|obuf
init|=
name|OBUF_FACTORY
operator|.
name|get
argument_list|()
decl_stmt|;
name|obuf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|writeChars
argument_list|(
name|obuf
argument_list|,
name|string
argument_list|,
literal|0
argument_list|,
name|string
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|obuf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
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
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** Set to contain the contents of a string. */
DECL|method|set (UTF8 other)
specifier|public
name|void
name|set
parameter_list|(
name|UTF8
name|other
parameter_list|)
block|{
name|length
operator|=
name|other
operator|.
name|length
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|length
operator|>
name|bytes
operator|.
name|length
condition|)
comment|// grow buffer
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|other
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|readFields (DataInput in)
specifier|public
name|void
name|readFields
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|length
operator|=
name|in
operator|.
name|readUnsignedShort
argument_list|()
expr_stmt|;
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|bytes
operator|.
name|length
operator|<
name|length
condition|)
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
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
block|}
comment|/** Skips over one UTF8 in the input. */
DECL|method|skip (DataInput in)
specifier|public
specifier|static
name|void
name|skip
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
name|readUnsignedShort
argument_list|()
decl_stmt|;
name|WritableUtils
operator|.
name|skipFully
argument_list|(
name|in
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeShort
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/** Compare two UTF8s. */
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|UTF8
name|that
init|=
operator|(
name|UTF8
operator|)
name|o
decl_stmt|;
return|return
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|that
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|that
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Convert to a String. */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
name|length
argument_list|)
decl_stmt|;
try|try
block|{
synchronized|synchronized
init|(
name|IBUF
init|)
block|{
name|IBUF
operator|.
name|reset
argument_list|(
name|bytes
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|readChars
argument_list|(
name|IBUF
argument_list|,
name|buffer
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
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
name|e
argument_list|)
throw|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Returns true iff<code>o</code> is a UTF8 with the same contents.  */
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|UTF8
operator|)
condition|)
return|return
literal|false
return|;
name|UTF8
name|that
init|=
operator|(
name|UTF8
operator|)
name|o
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|length
operator|!=
name|that
operator|.
name|length
condition|)
return|return
literal|false
return|;
else|else
return|return
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|,
name|that
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|that
operator|.
name|length
argument_list|)
operator|==
literal|0
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|WritableComparator
operator|.
name|hashBytes
argument_list|(
name|bytes
argument_list|,
name|length
argument_list|)
return|;
block|}
comment|/** A WritableComparator optimized for UTF8 keys. */
DECL|class|Comparator
specifier|public
specifier|static
class|class
name|Comparator
extends|extends
name|WritableComparator
block|{
DECL|method|Comparator ()
specifier|public
name|Comparator
parameter_list|()
block|{
name|super
argument_list|(
name|UTF8
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
DECL|method|compare (byte[] b1, int s1, int l1, byte[] b2, int s2, int l2)
specifier|public
name|int
name|compare
parameter_list|(
name|byte
index|[]
name|b1
parameter_list|,
name|int
name|s1
parameter_list|,
name|int
name|l1
parameter_list|,
name|byte
index|[]
name|b2
parameter_list|,
name|int
name|s2
parameter_list|,
name|int
name|l2
parameter_list|)
block|{
name|int
name|n1
init|=
name|readUnsignedShort
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|)
decl_stmt|;
name|int
name|n2
init|=
name|readUnsignedShort
argument_list|(
name|b2
argument_list|,
name|s2
argument_list|)
decl_stmt|;
return|return
name|compareBytes
argument_list|(
name|b1
argument_list|,
name|s1
operator|+
literal|2
argument_list|,
name|n1
argument_list|,
name|b2
argument_list|,
name|s2
operator|+
literal|2
argument_list|,
name|n2
argument_list|)
return|;
block|}
block|}
static|static
block|{
comment|// register this comparator
name|WritableComparator
operator|.
name|define
argument_list|(
name|UTF8
operator|.
name|class
argument_list|,
operator|new
name|Comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/// STATIC UTILITIES FROM HERE DOWN
comment|/// These are probably not used much anymore, and might be removed...
comment|/** Convert a string to a UTF-8 encoded byte array.    * @see String#getBytes(String)    */
DECL|method|getBytes (String string)
specifier|public
specifier|static
name|byte
index|[]
name|getBytes
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|utf8Length
argument_list|(
name|string
argument_list|)
index|]
decl_stmt|;
try|try
block|{
comment|// avoid sync'd allocations
name|DataOutputBuffer
name|obuf
init|=
name|OBUF_FACTORY
operator|.
name|get
argument_list|()
decl_stmt|;
name|obuf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|writeChars
argument_list|(
name|obuf
argument_list|,
name|string
argument_list|,
literal|0
argument_list|,
name|string
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|obuf
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|obuf
operator|.
name|getLength
argument_list|()
argument_list|)
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
name|e
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/** Read a UTF-8 encoded string.    *    * @see DataInput#readUTF()    */
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
name|bytes
init|=
name|in
operator|.
name|readUnsignedShort
argument_list|()
decl_stmt|;
name|StringBuilder
name|buffer
init|=
operator|new
name|StringBuilder
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
name|readChars
argument_list|(
name|in
argument_list|,
name|buffer
argument_list|,
name|bytes
argument_list|)
expr_stmt|;
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|readChars (DataInput in, StringBuilder buffer, int nBytes)
specifier|private
specifier|static
name|void
name|readChars
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|StringBuilder
name|buffer
parameter_list|,
name|int
name|nBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|DataOutputBuffer
name|obuf
init|=
name|OBUF_FACTORY
operator|.
name|get
argument_list|()
decl_stmt|;
name|obuf
operator|.
name|reset
argument_list|()
expr_stmt|;
name|obuf
operator|.
name|write
argument_list|(
name|in
argument_list|,
name|nBytes
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
name|obuf
operator|.
name|getData
argument_list|()
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|i
operator|<
name|nBytes
condition|)
block|{
name|byte
name|b
init|=
name|bytes
index|[
name|i
operator|++
index|]
decl_stmt|;
if|if
condition|(
operator|(
name|b
operator|&
literal|0x80
operator|)
operator|==
literal|0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
name|b
operator|&
literal|0x7F
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|(
name|b
operator|&
literal|0xE0
operator|)
operator|!=
literal|0xE0
condition|)
block|{
name|buffer
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x1F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|.
name|append
argument_list|(
call|(
name|char
call|)
argument_list|(
operator|(
operator|(
name|b
operator|&
literal|0x0F
operator|)
operator|<<
literal|12
operator|)
operator||
operator|(
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0x3F
operator|)
operator|<<
literal|6
operator|)
operator||
operator|(
name|bytes
index|[
name|i
operator|++
index|]
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** Write a UTF-8 encoded string.    *    * @see DataOutput#writeUTF(String)    */
DECL|method|writeString (DataOutput out, String s)
specifier|public
specifier|static
name|int
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
operator|.
name|length
argument_list|()
operator|>
literal|0xffff
operator|/
literal|3
condition|)
block|{
comment|// maybe too long
name|LOG
operator|.
name|warn
argument_list|(
literal|"truncating long string: "
operator|+
name|s
operator|.
name|length
argument_list|()
operator|+
literal|" chars, starting with "
operator|+
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|)
expr_stmt|;
name|s
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|0xffff
operator|/
literal|3
argument_list|)
expr_stmt|;
block|}
name|int
name|len
init|=
name|utf8Length
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|0xffff
condition|)
comment|// double-check length
throw|throw
operator|new
name|IOException
argument_list|(
literal|"string too long!"
argument_list|)
throw|;
name|out
operator|.
name|writeShort
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|writeChars
argument_list|(
name|out
argument_list|,
name|s
argument_list|,
literal|0
argument_list|,
name|s
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|len
return|;
block|}
comment|/** Returns the number of bytes required to write this. */
DECL|method|utf8Length (String string)
specifier|private
specifier|static
name|int
name|utf8Length
parameter_list|(
name|String
name|string
parameter_list|)
block|{
name|int
name|stringLength
init|=
name|string
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|utf8Length
init|=
literal|0
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
name|stringLength
condition|;
name|i
operator|++
control|)
block|{
name|int
name|c
init|=
name|string
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<=
literal|0x007F
condition|)
block|{
name|utf8Length
operator|++
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>
literal|0x07FF
condition|)
block|{
name|utf8Length
operator|+=
literal|3
expr_stmt|;
block|}
else|else
block|{
name|utf8Length
operator|+=
literal|2
expr_stmt|;
block|}
block|}
return|return
name|utf8Length
return|;
block|}
DECL|method|writeChars (DataOutput out, String s, int start, int length)
specifier|private
specifier|static
name|void
name|writeChars
parameter_list|(
name|DataOutput
name|out
parameter_list|,
name|String
name|s
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|end
init|=
name|start
operator|+
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|int
name|code
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|<=
literal|0x7F
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
operator|(
name|byte
operator|)
name|code
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|code
operator|<=
literal|0x07FF
condition|)
block|{
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xC0
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x1F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
name|code
operator|&
literal|0x3F
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0xE0
operator||
operator|(
operator|(
name|code
operator|>>
literal|12
operator|)
operator|&
literal|0X0F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeByte
argument_list|(
call|(
name|byte
call|)
argument_list|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

