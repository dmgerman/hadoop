begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright (c) 2005, European Commission project OneLab under contract 034819 (http://www.one-lab.org)  * All rights reserved.  * Redistribution and use in source and binary forms, with or   * without modification, are permitted provided that the following   * conditions are met:  *  - Redistributions of source code must retain the above copyright   *    notice, this list of conditions and the following disclaimer.  *  - Redistributions in binary form must reproduce the above copyright   *    notice, this list of conditions and the following disclaimer in   *    the documentation and/or other materials provided with the distribution.  *  - Neither the name of the University Catholique de Louvain - UCL  *    nor the names of its contributors may be used to endorse or   *    promote products derived from this software without specific prior   *    written permission.  *      * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS   * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT   * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS   * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE   * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,   * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,   * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;   * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER   * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT   * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN   * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE   * POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.bloom
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|bloom
package|;
end_package

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
name|IOException
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
name|io
operator|.
name|WritableComparable
import|;
end_import

begin_comment
comment|/**  * The general behavior of a key that must be stored in a filter.  *   * @see Filter The general behavior of a filter  */
end_comment

begin_class
DECL|class|Key
specifier|public
class|class
name|Key
implements|implements
name|WritableComparable
argument_list|<
name|Key
argument_list|>
block|{
comment|/** Byte value of key */
DECL|field|bytes
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/**    * The weight associated to<i>this</i> key.    *<p>    *<b>Invariant</b>: if it is not specified, each instance of     *<code>Key</code> will have a default weight of 1.0    */
DECL|field|weight
name|double
name|weight
decl_stmt|;
comment|/** default constructor - use with readFields */
DECL|method|Key ()
specifier|public
name|Key
parameter_list|()
block|{}
comment|/**    * Constructor.    *<p>    * Builds a key with a default weight.    * @param value The byte value of<i>this</i> key.    */
DECL|method|Key (byte[] value)
specifier|public
name|Key
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
name|this
argument_list|(
name|value
argument_list|,
literal|1.0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor.    *<p>    * Builds a key with a specified weight.    * @param value The value of<i>this</i> key.    * @param weight The weight associated to<i>this</i> key.    */
DECL|method|Key (byte[] value, double weight)
specifier|public
name|Key
parameter_list|(
name|byte
index|[]
name|value
parameter_list|,
name|double
name|weight
parameter_list|)
block|{
name|set
argument_list|(
name|value
argument_list|,
name|weight
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param value    * @param weight    */
DECL|method|set (byte[] value, double weight)
specifier|public
name|void
name|set
parameter_list|(
name|byte
index|[]
name|value
parameter_list|,
name|double
name|weight
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"value can not be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|bytes
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|weight
operator|=
name|weight
expr_stmt|;
block|}
comment|/** @return byte[] The value of<i>this</i> key. */
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|bytes
return|;
block|}
comment|/** @return Returns the weight associated to<i>this</i> key. */
DECL|method|getWeight ()
specifier|public
name|double
name|getWeight
parameter_list|()
block|{
return|return
name|weight
return|;
block|}
comment|/**    * Increments the weight of<i>this</i> key with a specified value.     * @param weight The increment.    */
DECL|method|incrementWeight (double weight)
specifier|public
name|void
name|incrementWeight
parameter_list|(
name|double
name|weight
parameter_list|)
block|{
name|this
operator|.
name|weight
operator|+=
name|weight
expr_stmt|;
block|}
comment|/** Increments the weight of<i>this</i> key by one. */
DECL|method|incrementWeight ()
specifier|public
name|void
name|incrementWeight
parameter_list|()
block|{
name|this
operator|.
name|weight
operator|++
expr_stmt|;
block|}
annotation|@
name|Override
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
name|Key
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|this
operator|.
name|compareTo
argument_list|(
operator|(
name|Key
operator|)
name|o
argument_list|)
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
name|int
name|result
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
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|^=
name|Byte
operator|.
name|valueOf
argument_list|(
name|bytes
index|[
name|i
index|]
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
block|}
name|result
operator|^=
name|Double
operator|.
name|valueOf
argument_list|(
name|weight
argument_list|)
operator|.
name|hashCode
argument_list|()
expr_stmt|;
return|return
name|result
return|;
block|}
comment|// Writable
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
name|bytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|weight
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
name|this
operator|.
name|bytes
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|readInt
argument_list|()
index|]
expr_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|this
operator|.
name|bytes
argument_list|)
expr_stmt|;
name|weight
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
comment|// Comparable
DECL|method|compareTo (Key other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Key
name|other
parameter_list|)
block|{
name|int
name|result
init|=
name|this
operator|.
name|bytes
operator|.
name|length
operator|-
name|other
operator|.
name|getBytes
argument_list|()
operator|.
name|length
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|result
operator|==
literal|0
operator|&&
name|i
operator|<
name|bytes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|=
name|this
operator|.
name|bytes
index|[
name|i
index|]
operator|-
name|other
operator|.
name|bytes
index|[
name|i
index|]
expr_stmt|;
block|}
if|if
condition|(
name|result
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|Double
operator|.
name|valueOf
argument_list|(
name|this
operator|.
name|weight
operator|-
name|other
operator|.
name|weight
argument_list|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

