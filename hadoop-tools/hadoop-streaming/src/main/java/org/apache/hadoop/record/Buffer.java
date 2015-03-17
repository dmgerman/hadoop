begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.record
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|record
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
comment|/**  * A byte sequence that is used as a Java native type for buffer.  * It is resizable and distinguishes between the count of the sequence and  * the current capacity.  *   * @deprecated Replaced by<a href="http://avro.apache.org/">Avro</a>.  */
end_comment

begin_class
annotation|@
name|Deprecated
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|class|Buffer
specifier|public
class|class
name|Buffer
implements|implements
name|Comparable
implements|,
name|Cloneable
block|{
comment|/** Number of valid bytes in this.bytes. */
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|/** Backing store for Buffer. */
DECL|field|bytes
specifier|private
name|byte
index|[]
name|bytes
init|=
literal|null
decl_stmt|;
comment|/**    * Create a zero-count sequence.    */
DECL|method|Buffer ()
specifier|public
name|Buffer
parameter_list|()
block|{
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
block|}
comment|/**    * Create a Buffer using the byte array as the initial value.    *    * @param bytes This array becomes the backing storage for the object.    */
DECL|method|Buffer (byte[] bytes)
specifier|public
name|Buffer
parameter_list|(
name|byte
index|[]
name|bytes
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
name|count
operator|=
operator|(
name|bytes
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|bytes
operator|.
name|length
expr_stmt|;
block|}
comment|/**    * Create a Buffer using the byte range as the initial value.    *    * @param bytes Copy of this array becomes the backing storage for the object.    * @param offset offset into byte array    * @param length length of data    */
DECL|method|Buffer (byte[] bytes, int offset, int length)
specifier|public
name|Buffer
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|copy
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**    * Use the specified bytes array as underlying sequence.    *    * @param bytes byte sequence    */
DECL|method|set (byte[] bytes)
specifier|public
name|void
name|set
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|count
operator|=
operator|(
name|bytes
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|bytes
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|bytes
operator|=
name|bytes
expr_stmt|;
block|}
comment|/**    * Copy the specified byte array to the Buffer. Replaces the current buffer.    *    * @param bytes byte array to be assigned    * @param offset offset into byte array    * @param length length of data    */
DECL|method|copy (byte[] bytes, int offset, int length)
specifier|public
specifier|final
name|void
name|copy
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|bytes
operator|==
literal|null
operator|||
name|this
operator|.
name|bytes
operator|.
name|length
operator|<
name|length
condition|)
block|{
name|this
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|this
operator|.
name|bytes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|count
operator|=
name|length
expr_stmt|;
block|}
comment|/**    * Get the data from the Buffer.    *     * @return The data is only valid between 0 and getCount() - 1.    */
DECL|method|get ()
specifier|public
name|byte
index|[]
name|get
parameter_list|()
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
condition|)
block|{
name|bytes
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
return|return
name|bytes
return|;
block|}
comment|/**    * Get the current count of the buffer.    */
DECL|method|getCount ()
specifier|public
name|int
name|getCount
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/**    * Get the capacity, which is the maximum count that could handled without    * resizing the backing storage.    *     * @return The number of bytes    */
DECL|method|getCapacity ()
specifier|public
name|int
name|getCapacity
parameter_list|()
block|{
return|return
name|this
operator|.
name|get
argument_list|()
operator|.
name|length
return|;
block|}
comment|/**    * Change the capacity of the backing storage.    * The data is preserved if newCapacity {@literal>=} getCount().    * @param newCapacity The new capacity in bytes.    */
DECL|method|setCapacity (int newCapacity)
specifier|public
name|void
name|setCapacity
parameter_list|(
name|int
name|newCapacity
parameter_list|)
block|{
if|if
condition|(
name|newCapacity
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid capacity argument "
operator|+
name|newCapacity
argument_list|)
throw|;
block|}
if|if
condition|(
name|newCapacity
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|bytes
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|count
operator|=
literal|0
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|newCapacity
operator|!=
name|getCapacity
argument_list|()
condition|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|newCapacity
index|]
decl_stmt|;
if|if
condition|(
name|newCapacity
operator|<
name|count
condition|)
block|{
name|count
operator|=
name|newCapacity
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|!=
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
name|bytes
operator|=
name|data
expr_stmt|;
block|}
block|}
comment|/**    * Reset the buffer to 0 size    */
DECL|method|reset ()
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|setCapacity
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Change the capacity of the backing store to be the same as the current     * count of buffer.    */
DECL|method|truncate ()
specifier|public
name|void
name|truncate
parameter_list|()
block|{
name|setCapacity
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
comment|/**    * Append specified bytes to the buffer.    *    * @param bytes byte array to be appended    * @param offset offset into byte array    * @param length length of data    */
DECL|method|append (byte[] bytes, int offset, int length)
specifier|public
name|void
name|append
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|setCapacity
argument_list|(
name|count
operator|+
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bytes
argument_list|,
name|offset
argument_list|,
name|this
operator|.
name|get
argument_list|()
argument_list|,
name|count
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|count
operator|=
name|count
operator|+
name|length
expr_stmt|;
block|}
comment|/**    * Append specified bytes to the buffer    *    * @param bytes byte array to be appended    */
DECL|method|append (byte[] bytes)
specifier|public
name|void
name|append
parameter_list|(
name|byte
index|[]
name|bytes
parameter_list|)
block|{
name|append
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
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|hash
init|=
literal|1
decl_stmt|;
name|byte
index|[]
name|b
init|=
name|this
operator|.
name|get
argument_list|()
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
name|count
condition|;
name|i
operator|++
control|)
name|hash
operator|=
operator|(
literal|31
operator|*
name|hash
operator|)
operator|+
name|b
index|[
name|i
index|]
expr_stmt|;
return|return
name|hash
return|;
block|}
comment|/**    * Define the sort order of the Buffer.    *     * @param other The other buffer    * @return Positive if this is bigger than other, 0 if they are equal, and    *         negative if this is smaller than other.    */
annotation|@
name|Override
DECL|method|compareTo (Object other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
name|Buffer
name|right
init|=
operator|(
operator|(
name|Buffer
operator|)
name|other
operator|)
decl_stmt|;
name|byte
index|[]
name|lb
init|=
name|this
operator|.
name|get
argument_list|()
decl_stmt|;
name|byte
index|[]
name|rb
init|=
name|right
operator|.
name|get
argument_list|()
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
name|count
operator|&&
name|i
operator|<
name|right
operator|.
name|count
condition|;
name|i
operator|++
control|)
block|{
name|int
name|a
init|=
operator|(
name|lb
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
name|int
name|b
init|=
operator|(
name|rb
index|[
name|i
index|]
operator|&
literal|0xff
operator|)
decl_stmt|;
if|if
condition|(
name|a
operator|!=
name|b
condition|)
block|{
return|return
name|a
operator|-
name|b
return|;
block|}
block|}
return|return
name|count
operator|-
name|right
operator|.
name|count
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|instanceof
name|Buffer
operator|&&
name|this
operator|!=
name|other
condition|)
block|{
return|return
name|compareTo
argument_list|(
name|other
argument_list|)
operator|==
literal|0
return|;
block|}
return|return
operator|(
name|this
operator|==
name|other
operator|)
return|;
block|}
comment|// inheric javadoc
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
literal|2
operator|*
name|count
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
name|count
condition|;
name|idx
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Character
operator|.
name|forDigit
argument_list|(
operator|(
name|bytes
index|[
name|idx
index|]
operator|&
literal|0xF0
operator|)
operator|>>
literal|4
argument_list|,
literal|16
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Character
operator|.
name|forDigit
argument_list|(
name|bytes
index|[
name|idx
index|]
operator|&
literal|0x0F
argument_list|,
literal|16
argument_list|)
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
comment|/**    * Convert the byte buffer to a string an specific character encoding    *    * @param charsetName Valid Java Character Set Name    */
DECL|method|toString (String charsetName)
specifier|public
name|String
name|toString
parameter_list|(
name|String
name|charsetName
parameter_list|)
throws|throws
name|UnsupportedEncodingException
block|{
return|return
operator|new
name|String
argument_list|(
name|this
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|getCount
argument_list|()
argument_list|,
name|charsetName
argument_list|)
return|;
block|}
comment|// inherit javadoc
annotation|@
name|Override
DECL|method|clone ()
specifier|public
name|Object
name|clone
parameter_list|()
throws|throws
name|CloneNotSupportedException
block|{
name|Buffer
name|result
init|=
operator|(
name|Buffer
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|result
operator|.
name|copy
argument_list|(
name|this
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

