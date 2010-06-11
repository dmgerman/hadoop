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
name|java
operator|.
name|util
operator|.
name|BitSet
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
comment|/**  * Implements a<i>Bloom filter</i>, as defined by Bloom in 1970.  *<p>  * The Bloom filter is a data structure that was introduced in 1970 and that has been adopted by   * the networking research community in the past decade thanks to the bandwidth efficiencies that it  * offers for the transmission of set membership information between networked hosts.  A sender encodes   * the information into a bit vector, the Bloom filter, that is more compact than a conventional   * representation. Computation and space costs for construction are linear in the number of elements.    * The receiver uses the filter to test whether various elements are members of the set. Though the   * filter will occasionally return a false positive, it will never return a false negative. When creating   * the filter, the sender can choose its desired point in a trade-off between the false positive rate and the size.   *   *<p>  * Originally created by  *<a href="http://www.one-lab.org">European Commission One-Lab Project 034819</a>.  *   * @see Filter The general behavior of a filter  *   * @see<a href="http://portal.acm.org/citation.cfm?id=362692&dl=ACM&coll=portal">Space/Time Trade-Offs in Hash Coding with Allowable Errors</a>  */
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
DECL|class|BloomFilter
specifier|public
class|class
name|BloomFilter
extends|extends
name|Filter
block|{
DECL|field|bitvalues
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|bitvalues
init|=
operator|new
name|byte
index|[]
block|{
operator|(
name|byte
operator|)
literal|0x01
block|,
operator|(
name|byte
operator|)
literal|0x02
block|,
operator|(
name|byte
operator|)
literal|0x04
block|,
operator|(
name|byte
operator|)
literal|0x08
block|,
operator|(
name|byte
operator|)
literal|0x10
block|,
operator|(
name|byte
operator|)
literal|0x20
block|,
operator|(
name|byte
operator|)
literal|0x40
block|,
operator|(
name|byte
operator|)
literal|0x80
block|}
decl_stmt|;
comment|/** The bit vector. */
DECL|field|bits
name|BitSet
name|bits
decl_stmt|;
comment|/** Default constructor - use with readFields */
DECL|method|BloomFilter ()
specifier|public
name|BloomFilter
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/**    * Constructor    * @param vectorSize The vector size of<i>this</i> filter.    * @param nbHash The number of hash function to consider.    * @param hashType type of the hashing function (see    * {@link org.apache.hadoop.util.hash.Hash}).    */
DECL|method|BloomFilter (int vectorSize, int nbHash, int hashType)
specifier|public
name|BloomFilter
parameter_list|(
name|int
name|vectorSize
parameter_list|,
name|int
name|nbHash
parameter_list|,
name|int
name|hashType
parameter_list|)
block|{
name|super
argument_list|(
name|vectorSize
argument_list|,
name|nbHash
argument_list|,
name|hashType
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|new
name|BitSet
argument_list|(
name|this
operator|.
name|vectorSize
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|add (Key key)
specifier|public
name|void
name|add
parameter_list|(
name|Key
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"key cannot be null"
argument_list|)
throw|;
block|}
name|int
index|[]
name|h
init|=
name|hash
operator|.
name|hash
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|hash
operator|.
name|clear
argument_list|()
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
name|nbHash
condition|;
name|i
operator|++
control|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|h
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|and (Filter filter)
specifier|public
name|void
name|and
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
operator|!
operator|(
name|filter
operator|instanceof
name|BloomFilter
operator|)
operator|||
name|filter
operator|.
name|vectorSize
operator|!=
name|this
operator|.
name|vectorSize
operator|||
name|filter
operator|.
name|nbHash
operator|!=
name|this
operator|.
name|nbHash
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"filters cannot be and-ed"
argument_list|)
throw|;
block|}
name|this
operator|.
name|bits
operator|.
name|and
argument_list|(
operator|(
operator|(
name|BloomFilter
operator|)
name|filter
operator|)
operator|.
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|membershipTest (Key key)
specifier|public
name|boolean
name|membershipTest
parameter_list|(
name|Key
name|key
parameter_list|)
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NullPointerException
argument_list|(
literal|"key cannot be null"
argument_list|)
throw|;
block|}
name|int
index|[]
name|h
init|=
name|hash
operator|.
name|hash
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|hash
operator|.
name|clear
argument_list|()
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
name|nbHash
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|bits
operator|.
name|get
argument_list|(
name|h
index|[
name|i
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|not ()
specifier|public
name|void
name|not
parameter_list|()
block|{
name|bits
operator|.
name|flip
argument_list|(
literal|0
argument_list|,
name|vectorSize
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|or (Filter filter)
specifier|public
name|void
name|or
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
operator|!
operator|(
name|filter
operator|instanceof
name|BloomFilter
operator|)
operator|||
name|filter
operator|.
name|vectorSize
operator|!=
name|this
operator|.
name|vectorSize
operator|||
name|filter
operator|.
name|nbHash
operator|!=
name|this
operator|.
name|nbHash
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"filters cannot be or-ed"
argument_list|)
throw|;
block|}
name|bits
operator|.
name|or
argument_list|(
operator|(
operator|(
name|BloomFilter
operator|)
name|filter
operator|)
operator|.
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|xor (Filter filter)
specifier|public
name|void
name|xor
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|==
literal|null
operator|||
operator|!
operator|(
name|filter
operator|instanceof
name|BloomFilter
operator|)
operator|||
name|filter
operator|.
name|vectorSize
operator|!=
name|this
operator|.
name|vectorSize
operator|||
name|filter
operator|.
name|nbHash
operator|!=
name|this
operator|.
name|nbHash
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"filters cannot be xor-ed"
argument_list|)
throw|;
block|}
name|bits
operator|.
name|xor
argument_list|(
operator|(
operator|(
name|BloomFilter
operator|)
name|filter
operator|)
operator|.
name|bits
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|bits
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * @return size of the the bloomfilter    */
DECL|method|getVectorSize ()
specifier|public
name|int
name|getVectorSize
parameter_list|()
block|{
return|return
name|this
operator|.
name|vectorSize
return|;
block|}
comment|// Writable
annotation|@
name|Override
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
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|getNBytes
argument_list|()
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|byteIndex
init|=
literal|0
init|,
name|bitIndex
init|=
literal|0
init|;
name|i
operator|<
name|vectorSize
condition|;
name|i
operator|++
operator|,
name|bitIndex
operator|++
control|)
block|{
if|if
condition|(
name|bitIndex
operator|==
literal|8
condition|)
block|{
name|bitIndex
operator|=
literal|0
expr_stmt|;
name|byteIndex
operator|++
expr_stmt|;
block|}
if|if
condition|(
name|bitIndex
operator|==
literal|0
condition|)
block|{
name|bytes
index|[
name|byteIndex
index|]
operator|=
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|bits
operator|.
name|get
argument_list|(
name|i
argument_list|)
condition|)
block|{
name|bytes
index|[
name|byteIndex
index|]
operator||=
name|bitvalues
index|[
name|bitIndex
index|]
expr_stmt|;
block|}
block|}
name|out
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|super
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|bits
operator|=
operator|new
name|BitSet
argument_list|(
name|this
operator|.
name|vectorSize
argument_list|)
expr_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
name|getNBytes
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|,
name|byteIndex
init|=
literal|0
init|,
name|bitIndex
init|=
literal|0
init|;
name|i
operator|<
name|vectorSize
condition|;
name|i
operator|++
operator|,
name|bitIndex
operator|++
control|)
block|{
if|if
condition|(
name|bitIndex
operator|==
literal|8
condition|)
block|{
name|bitIndex
operator|=
literal|0
expr_stmt|;
name|byteIndex
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|bytes
index|[
name|byteIndex
index|]
operator|&
name|bitvalues
index|[
name|bitIndex
index|]
operator|)
operator|!=
literal|0
condition|)
block|{
name|bits
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/* @return number of bytes needed to hold bit vector */
DECL|method|getNBytes ()
specifier|private
name|int
name|getNBytes
parameter_list|()
block|{
return|return
operator|(
name|vectorSize
operator|+
literal|7
operator|)
operator|/
literal|8
return|;
block|}
block|}
end_class

begin_comment
comment|//end class
end_comment

end_unit

