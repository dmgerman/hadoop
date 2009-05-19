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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|*
import|;
end_import

begin_comment
comment|/** A Writable for MD5 hash values.  */
end_comment

begin_class
DECL|class|MD5Hash
specifier|public
class|class
name|MD5Hash
implements|implements
name|WritableComparable
argument_list|<
name|MD5Hash
argument_list|>
block|{
DECL|field|MD5_LEN
specifier|public
specifier|static
specifier|final
name|int
name|MD5_LEN
init|=
literal|16
decl_stmt|;
DECL|field|DIGESTER_FACTORY
specifier|private
specifier|static
name|ThreadLocal
argument_list|<
name|MessageDigest
argument_list|>
name|DIGESTER_FACTORY
init|=
operator|new
name|ThreadLocal
argument_list|<
name|MessageDigest
argument_list|>
argument_list|()
block|{
specifier|protected
name|MessageDigest
name|initialValue
parameter_list|()
block|{
try|try
block|{
return|return
name|MessageDigest
operator|.
name|getInstance
argument_list|(
literal|"MD5"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchAlgorithmException
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
block|}
decl_stmt|;
DECL|field|digest
specifier|private
name|byte
index|[]
name|digest
decl_stmt|;
comment|/** Constructs an MD5Hash. */
DECL|method|MD5Hash ()
specifier|public
name|MD5Hash
parameter_list|()
block|{
name|this
operator|.
name|digest
operator|=
operator|new
name|byte
index|[
name|MD5_LEN
index|]
expr_stmt|;
block|}
comment|/** Constructs an MD5Hash from a hex string. */
DECL|method|MD5Hash (String hex)
specifier|public
name|MD5Hash
parameter_list|(
name|String
name|hex
parameter_list|)
block|{
name|setDigest
argument_list|(
name|hex
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs an MD5Hash with a specified value. */
DECL|method|MD5Hash (byte[] digest)
specifier|public
name|MD5Hash
parameter_list|(
name|byte
index|[]
name|digest
parameter_list|)
block|{
if|if
condition|(
name|digest
operator|.
name|length
operator|!=
name|MD5_LEN
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Wrong length: "
operator|+
name|digest
operator|.
name|length
argument_list|)
throw|;
name|this
operator|.
name|digest
operator|=
name|digest
expr_stmt|;
block|}
comment|// javadoc from Writable
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
name|in
operator|.
name|readFully
argument_list|(
name|digest
argument_list|)
expr_stmt|;
block|}
comment|/** Constructs, reads and returns an instance. */
DECL|method|read (DataInput in)
specifier|public
specifier|static
name|MD5Hash
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|MD5Hash
name|result
init|=
operator|new
name|MD5Hash
argument_list|()
decl_stmt|;
name|result
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// javadoc from Writable
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
name|write
argument_list|(
name|digest
argument_list|)
expr_stmt|;
block|}
comment|/** Copy the contents of another instance into this instance. */
DECL|method|set (MD5Hash that)
specifier|public
name|void
name|set
parameter_list|(
name|MD5Hash
name|that
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|that
operator|.
name|digest
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|digest
argument_list|,
literal|0
argument_list|,
name|MD5_LEN
argument_list|)
expr_stmt|;
block|}
comment|/** Returns the digest bytes. */
DECL|method|getDigest ()
specifier|public
name|byte
index|[]
name|getDigest
parameter_list|()
block|{
return|return
name|digest
return|;
block|}
comment|/** Construct a hash value for a byte array. */
DECL|method|digest (byte[] data)
specifier|public
specifier|static
name|MD5Hash
name|digest
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|digest
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
return|;
block|}
comment|/** Construct a hash value for the content from the InputStream. */
DECL|method|digest (InputStream in)
specifier|public
specifier|static
name|MD5Hash
name|digest
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4
operator|*
literal|1024
index|]
decl_stmt|;
specifier|final
name|MessageDigest
name|digester
init|=
name|DIGESTER_FACTORY
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|n
init|;
operator|(
name|n
operator|=
name|in
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|;
control|)
block|{
name|digester
operator|.
name|update
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|n
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MD5Hash
argument_list|(
name|digester
operator|.
name|digest
argument_list|()
argument_list|)
return|;
block|}
comment|/** Construct a hash value for a byte array. */
DECL|method|digest (byte[] data, int start, int len)
specifier|public
specifier|static
name|MD5Hash
name|digest
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|byte
index|[]
name|digest
decl_stmt|;
name|MessageDigest
name|digester
init|=
name|DIGESTER_FACTORY
operator|.
name|get
argument_list|()
decl_stmt|;
name|digester
operator|.
name|update
argument_list|(
name|data
argument_list|,
name|start
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|digest
operator|=
name|digester
operator|.
name|digest
argument_list|()
expr_stmt|;
return|return
operator|new
name|MD5Hash
argument_list|(
name|digest
argument_list|)
return|;
block|}
comment|/** Construct a hash value for a String. */
DECL|method|digest (String string)
specifier|public
specifier|static
name|MD5Hash
name|digest
parameter_list|(
name|String
name|string
parameter_list|)
block|{
return|return
name|digest
argument_list|(
name|UTF8
operator|.
name|getBytes
argument_list|(
name|string
argument_list|)
argument_list|)
return|;
block|}
comment|/** Construct a hash value for a String. */
DECL|method|digest (UTF8 utf8)
specifier|public
specifier|static
name|MD5Hash
name|digest
parameter_list|(
name|UTF8
name|utf8
parameter_list|)
block|{
return|return
name|digest
argument_list|(
name|utf8
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|0
argument_list|,
name|utf8
operator|.
name|getLength
argument_list|()
argument_list|)
return|;
block|}
comment|/** Construct a half-sized version of this MD5.  Fits in a long **/
DECL|method|halfDigest ()
specifier|public
name|long
name|halfDigest
parameter_list|()
block|{
name|long
name|value
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
literal|8
condition|;
name|i
operator|++
control|)
name|value
operator||=
operator|(
operator|(
name|digest
index|[
name|i
index|]
operator|&
literal|0xffL
operator|)
operator|<<
operator|(
literal|8
operator|*
operator|(
literal|7
operator|-
name|i
operator|)
operator|)
operator|)
expr_stmt|;
return|return
name|value
return|;
block|}
comment|/**    * Return a 32-bit digest of the MD5.    * @return the first 4 bytes of the md5    */
DECL|method|quarterDigest ()
specifier|public
name|int
name|quarterDigest
parameter_list|()
block|{
name|int
name|value
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
literal|4
condition|;
name|i
operator|++
control|)
name|value
operator||=
operator|(
operator|(
name|digest
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
operator|<<
operator|(
literal|8
operator|*
operator|(
literal|3
operator|-
name|i
operator|)
operator|)
operator|)
expr_stmt|;
return|return
name|value
return|;
block|}
comment|/** Returns true iff<code>o</code> is an MD5Hash whose digest contains the    * same values.  */
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
name|MD5Hash
operator|)
condition|)
return|return
literal|false
return|;
name|MD5Hash
name|other
init|=
operator|(
name|MD5Hash
operator|)
name|o
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|this
operator|.
name|digest
argument_list|,
name|other
operator|.
name|digest
argument_list|)
return|;
block|}
comment|/** Returns a hash code value for this object.    * Only uses the first 4 bytes, since md5s are evenly distributed.    */
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|quarterDigest
argument_list|()
return|;
block|}
comment|/** Compares this object with the specified object for order.*/
DECL|method|compareTo (MD5Hash that)
specifier|public
name|int
name|compareTo
parameter_list|(
name|MD5Hash
name|that
parameter_list|)
block|{
return|return
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|this
operator|.
name|digest
argument_list|,
literal|0
argument_list|,
name|MD5_LEN
argument_list|,
name|that
operator|.
name|digest
argument_list|,
literal|0
argument_list|,
name|MD5_LEN
argument_list|)
return|;
block|}
comment|/** A WritableComparator optimized for MD5Hash keys. */
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
name|MD5Hash
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
return|return
name|compareBytes
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|,
name|MD5_LEN
argument_list|,
name|b2
argument_list|,
name|s2
argument_list|,
name|MD5_LEN
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
name|MD5Hash
operator|.
name|class
argument_list|,
operator|new
name|Comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|field|HEX_DIGITS
specifier|private
specifier|static
specifier|final
name|char
index|[]
name|HEX_DIGITS
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'a'
block|,
literal|'b'
block|,
literal|'c'
block|,
literal|'d'
block|,
literal|'e'
block|,
literal|'f'
block|}
decl_stmt|;
comment|/** Returns a string representation of this object. */
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|MD5_LEN
operator|*
literal|2
argument_list|)
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
name|MD5_LEN
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|digest
index|[
name|i
index|]
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
operator|(
name|b
operator|>>
literal|4
operator|)
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|HEX_DIGITS
index|[
name|b
operator|&
literal|0xf
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** Sets the digest value from a hex string. */
DECL|method|setDigest (String hex)
specifier|public
name|void
name|setDigest
parameter_list|(
name|String
name|hex
parameter_list|)
block|{
if|if
condition|(
name|hex
operator|.
name|length
argument_list|()
operator|!=
name|MD5_LEN
operator|*
literal|2
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Wrong length: "
operator|+
name|hex
operator|.
name|length
argument_list|()
argument_list|)
throw|;
name|byte
index|[]
name|digest
init|=
operator|new
name|byte
index|[
name|MD5_LEN
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
name|MD5_LEN
condition|;
name|i
operator|++
control|)
block|{
name|int
name|j
init|=
name|i
operator|<<
literal|1
decl_stmt|;
name|digest
index|[
name|i
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|charToNibble
argument_list|(
name|hex
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
operator|<<
literal|4
operator||
name|charToNibble
argument_list|(
name|hex
operator|.
name|charAt
argument_list|(
name|j
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|digest
operator|=
name|digest
expr_stmt|;
block|}
DECL|method|charToNibble (char c)
specifier|private
specifier|static
specifier|final
name|int
name|charToNibble
parameter_list|(
name|char
name|c
parameter_list|)
block|{
if|if
condition|(
name|c
operator|>=
literal|'0'
operator|&&
name|c
operator|<=
literal|'9'
condition|)
block|{
return|return
name|c
operator|-
literal|'0'
return|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|'a'
operator|&&
name|c
operator|<=
literal|'f'
condition|)
block|{
return|return
literal|0xa
operator|+
operator|(
name|c
operator|-
literal|'a'
operator|)
return|;
block|}
elseif|else
if|if
condition|(
name|c
operator|>=
literal|'A'
operator|&&
name|c
operator|<=
literal|'F'
condition|)
block|{
return|return
literal|0xA
operator|+
operator|(
name|c
operator|-
literal|'A'
operator|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Not a hex character: "
operator|+
name|c
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

