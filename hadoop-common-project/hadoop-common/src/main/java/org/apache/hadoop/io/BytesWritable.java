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
comment|/**   * A byte sequence that is usable as a key or value.  * It is resizable and distinguishes between the size of the seqeunce and  * the current capacity. The hash function is the front of the md5 of the   * buffer. The sort order is the same as memcmp.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|BytesWritable
specifier|public
class|class
name|BytesWritable
extends|extends
name|BinaryComparable
implements|implements
name|WritableComparable
argument_list|<
name|BinaryComparable
argument_list|>
block|{
DECL|field|LENGTH_BYTES
specifier|private
specifier|static
specifier|final
name|int
name|LENGTH_BYTES
init|=
literal|4
decl_stmt|;
DECL|field|EMPTY_BYTES
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
block|{}
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/**    * Create a zero-size sequence.    */
DECL|method|BytesWritable ()
specifier|public
name|BytesWritable
parameter_list|()
block|{
name|this
argument_list|(
name|EMPTY_BYTES
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a BytesWritable using the byte array as the initial value.    * @param bytes This array becomes the backing storage for the object.    */
DECL|method|BytesWritable (byte[] bytes)
specifier|public
name|BytesWritable
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
argument_list|(
name|bytes
argument_list|,
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a BytesWritable using the byte array as the initial value    * and length as the length. Use this constructor if the array is larger    * than the value it represents.    * @param bytes This array becomes the backing storage for the object.    * @param length The number of bytes to use from array.    */
DECL|method|BytesWritable (byte[] bytes, int length)
specifier|public
name|BytesWritable
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
name|this
operator|.
name|size
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Get a copy of the bytes that is exactly the length of the data.    * See {@link #getBytes()} for faster access to the underlying array.    */
DECL|method|copyBytes ()
specifier|public
name|byte
index|[]
name|copyBytes
parameter_list|()
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**    * Get the data backing the BytesWritable. Please use {@link #copyBytes()}    * if you need the returned array to be precisely the length of the data.    * @return The data is only valid between 0 and getLength() - 1.    */
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
comment|/**    * Get the data from the BytesWritable.    * @deprecated Use {@link #getBytes()} instead.    */
annotation|@
name|Deprecated
DECL|method|get ()
specifier|public
name|byte
index|[]
name|get
parameter_list|()
block|{
return|return
name|getBytes
argument_list|()
return|;
block|}
comment|/**    * Get the current size of the buffer.    */
DECL|method|getLength ()
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/**    * Get the current size of the buffer.    * @deprecated Use {@link #getLength()} instead.    */
annotation|@
name|Deprecated
DECL|method|getSize ()
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|getLength
argument_list|()
return|;
block|}
comment|/**    * Change the size of the buffer. The values in the old range are preserved    * and any new values are undefined. The capacity is changed if it is     * necessary.    * @param size The new number of bytes    */
DECL|method|setSize (int size)
specifier|public
name|void
name|setSize
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>
name|getCapacity
argument_list|()
condition|)
block|{
name|setCapacity
argument_list|(
name|size
operator|*
literal|3
operator|/
literal|2
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
block|}
comment|/**    * Get the capacity, which is the maximum size that could handled without    * resizing the backing storage.    * @return The number of bytes    */
DECL|method|getCapacity ()
specifier|public
name|int
name|getCapacity
parameter_list|()
block|{
return|return
name|bytes
operator|.
name|length
return|;
block|}
comment|/**    * Change the capacity of the backing storage.    * The data is preserved.    * @param new_cap The new capacity in bytes.    */
DECL|method|setCapacity (int new_cap)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|int
name|new_cap
parameter_list|)
block|{
if|if
condition|(
name|new_cap
operator|!=
name|getCapacity
argument_list|()
condition|)
block|{
name|byte
index|[]
name|new_data
init|=
operator|new
name|byte
index|[
name|new_cap
index|]
decl_stmt|;
if|if
condition|(
name|new_cap
operator|<
name|size
condition|)
block|{
name|size
operator|=
name|new_cap
expr_stmt|;
block|}
if|if
condition|(
name|size
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|new_data
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|=
name|new_data
expr_stmt|;
block|}
block|}
comment|/**    * Set the BytesWritable to the contents of the given newData.    * @param newData the value to set this BytesWritable to.    */
DECL|method|set (BytesWritable newData)
specifier|public
name|void
name|set
parameter_list|(
name|BytesWritable
name|newData
parameter_list|)
block|{
name|set
argument_list|(
name|newData
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|newData
operator|.
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Set the value to a copy of the given byte range    * @param newData the new values to copy in    * @param offset the offset in newData to start at    * @param length the number of bytes to copy    */
DECL|method|set (byte[] newData, int offset, int length)
specifier|public
name|void
name|set
parameter_list|(
name|byte
index|[]
name|newData
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|setSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|setSize
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|newData
argument_list|,
name|offset
argument_list|,
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|// inherit javadoc
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
name|setSize
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// clear the old data
name|setSize
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|,
literal|0
argument_list|,
name|size
argument_list|)
expr_stmt|;
block|}
comment|// inherit javadoc
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
name|writeInt
argument_list|(
name|size
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
name|size
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * Are the two byte sequences equal?    */
annotation|@
name|Override
DECL|method|equals (Object right_obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|right_obj
parameter_list|)
block|{
if|if
condition|(
name|right_obj
operator|instanceof
name|BytesWritable
condition|)
return|return
name|super
operator|.
name|equals
argument_list|(
name|right_obj
argument_list|)
return|;
return|return
literal|false
return|;
block|}
comment|/**    * Generate the stream of bytes as hex pairs separated by ' '.    */
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|3
operator|*
name|size
argument_list|)
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
name|size
condition|;
name|idx
operator|++
control|)
block|{
comment|// if not the first, put a blank separator in
if|if
condition|(
name|idx
operator|!=
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|String
name|num
init|=
name|Integer
operator|.
name|toHexString
argument_list|(
literal|0xff
operator|&
name|bytes
index|[
name|idx
index|]
argument_list|)
decl_stmt|;
comment|// if it is only one digit, add a leading 0.
if|if
condition|(
name|num
operator|.
name|length
argument_list|()
operator|<
literal|2
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|num
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/** A Comparator optimized for BytesWritable. */
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
name|BytesWritable
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/**      * Compare the buffers in serialized form.      */
annotation|@
name|Override
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
operator|+
name|LENGTH_BYTES
argument_list|,
name|l1
operator|-
name|LENGTH_BYTES
argument_list|,
name|b2
argument_list|,
name|s2
operator|+
name|LENGTH_BYTES
argument_list|,
name|l2
operator|-
name|LENGTH_BYTES
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
name|BytesWritable
operator|.
name|class
argument_list|,
operator|new
name|Comparator
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

