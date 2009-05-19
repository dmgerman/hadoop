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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_class
DECL|class|RandomDatum
specifier|public
class|class
name|RandomDatum
implements|implements
name|WritableComparable
block|{
DECL|field|length
specifier|private
name|int
name|length
decl_stmt|;
DECL|field|data
specifier|private
name|byte
index|[]
name|data
decl_stmt|;
DECL|method|RandomDatum ()
specifier|public
name|RandomDatum
parameter_list|()
block|{}
DECL|method|RandomDatum (Random random)
specifier|public
name|RandomDatum
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|length
operator|=
literal|10
operator|+
operator|(
name|int
operator|)
name|Math
operator|.
name|pow
argument_list|(
literal|10.0
argument_list|,
name|random
operator|.
name|nextFloat
argument_list|()
operator|*
literal|3.0
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|random
operator|.
name|nextBytes
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
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
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|write
argument_list|(
name|data
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
name|readInt
argument_list|()
expr_stmt|;
if|if
condition|(
name|data
operator|==
literal|null
operator|||
name|length
operator|>
name|data
operator|.
name|length
condition|)
name|data
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
name|data
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|RandomDatum
name|that
init|=
operator|(
name|RandomDatum
operator|)
name|o
decl_stmt|;
return|return
name|WritableComparator
operator|.
name|compareBytes
argument_list|(
name|this
operator|.
name|data
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|length
argument_list|,
name|that
operator|.
name|data
argument_list|,
literal|0
argument_list|,
name|that
operator|.
name|length
argument_list|)
return|;
block|}
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|compareTo
argument_list|(
name|o
argument_list|)
operator|==
literal|0
return|;
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
name|length
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
name|length
condition|;
name|i
operator|++
control|)
block|{
name|int
name|b
init|=
name|data
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
DECL|class|Generator
specifier|public
specifier|static
class|class
name|Generator
block|{
DECL|field|random
name|Random
name|random
decl_stmt|;
DECL|field|key
specifier|private
name|RandomDatum
name|key
decl_stmt|;
DECL|field|value
specifier|private
name|RandomDatum
name|value
decl_stmt|;
DECL|method|Generator ()
specifier|public
name|Generator
parameter_list|()
block|{
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
DECL|method|Generator (int seed)
specifier|public
name|Generator
parameter_list|(
name|int
name|seed
parameter_list|)
block|{
name|random
operator|=
operator|new
name|Random
argument_list|(
name|seed
argument_list|)
expr_stmt|;
block|}
DECL|method|getKey ()
specifier|public
name|RandomDatum
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
DECL|method|getValue ()
specifier|public
name|RandomDatum
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
DECL|method|next ()
specifier|public
name|void
name|next
parameter_list|()
block|{
name|key
operator|=
operator|new
name|RandomDatum
argument_list|(
name|random
argument_list|)
expr_stmt|;
name|value
operator|=
operator|new
name|RandomDatum
argument_list|(
name|random
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** A WritableComparator optimized for RandomDatum. */
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
name|RandomDatum
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
name|readInt
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|)
decl_stmt|;
name|int
name|n2
init|=
name|readInt
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
literal|4
argument_list|,
name|n1
argument_list|,
name|b2
argument_list|,
name|s2
operator|+
literal|4
argument_list|,
name|n2
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

