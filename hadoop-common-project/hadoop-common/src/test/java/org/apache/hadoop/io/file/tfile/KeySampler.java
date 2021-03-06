begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *   * http://www.apache.org/licenses/LICENSE-2.0  *   * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
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
name|util
operator|.
name|Random
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
name|BytesWritable
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
name|file
operator|.
name|tfile
operator|.
name|RandomDistribution
operator|.
name|DiscreteRNG
import|;
end_import

begin_class
DECL|class|KeySampler
class|class
name|KeySampler
block|{
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|min
DECL|field|max
name|int
name|min
decl_stmt|,
name|max
decl_stmt|;
DECL|field|keyLenRNG
name|DiscreteRNG
name|keyLenRNG
decl_stmt|;
DECL|field|MIN_KEY_LEN
specifier|private
specifier|static
specifier|final
name|int
name|MIN_KEY_LEN
init|=
literal|4
decl_stmt|;
DECL|method|KeySampler (Random random, RawComparable first, RawComparable last, DiscreteRNG keyLenRNG)
specifier|public
name|KeySampler
parameter_list|(
name|Random
name|random
parameter_list|,
name|RawComparable
name|first
parameter_list|,
name|RawComparable
name|last
parameter_list|,
name|DiscreteRNG
name|keyLenRNG
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
name|min
operator|=
name|keyPrefixToInt
argument_list|(
name|first
argument_list|)
expr_stmt|;
name|max
operator|=
name|keyPrefixToInt
argument_list|(
name|last
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyLenRNG
operator|=
name|keyLenRNG
expr_stmt|;
block|}
DECL|method|keyPrefixToInt (RawComparable key)
specifier|private
name|int
name|keyPrefixToInt
parameter_list|(
name|RawComparable
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|b
init|=
name|key
operator|.
name|buffer
argument_list|()
decl_stmt|;
name|int
name|o
init|=
name|key
operator|.
name|offset
argument_list|()
decl_stmt|;
return|return
operator|(
name|b
index|[
name|o
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator||
operator|(
name|b
index|[
name|o
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator||
operator|(
name|b
index|[
name|o
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator||
operator|(
name|b
index|[
name|o
operator|+
literal|3
index|]
operator|&
literal|0xff
operator|)
return|;
block|}
DECL|method|next (BytesWritable key)
specifier|public
name|void
name|next
parameter_list|(
name|BytesWritable
name|key
parameter_list|)
block|{
name|key
operator|.
name|setSize
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|MIN_KEY_LEN
argument_list|,
name|keyLenRNG
operator|.
name|nextInt
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|key
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|int
name|n
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|max
operator|-
name|min
argument_list|)
operator|+
name|min
decl_stmt|;
name|byte
index|[]
name|b
init|=
name|key
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|b
index|[
literal|0
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|n
operator|>>
literal|24
argument_list|)
expr_stmt|;
name|b
index|[
literal|1
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|n
operator|>>
literal|16
argument_list|)
expr_stmt|;
name|b
index|[
literal|2
index|]
operator|=
call|(
name|byte
call|)
argument_list|(
name|n
operator|>>
literal|8
argument_list|)
expr_stmt|;
name|b
index|[
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
name|n
expr_stmt|;
block|}
block|}
end_class

end_unit

