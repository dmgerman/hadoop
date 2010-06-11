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

begin_comment
comment|/** A WritableComparable for ints. */
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
DECL|class|IntWritable
specifier|public
class|class
name|IntWritable
implements|implements
name|WritableComparable
block|{
DECL|field|value
specifier|private
name|int
name|value
decl_stmt|;
DECL|method|IntWritable ()
specifier|public
name|IntWritable
parameter_list|()
block|{}
DECL|method|IntWritable (int value)
specifier|public
name|IntWritable
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Set the value of this IntWritable. */
DECL|method|set (int value)
specifier|public
name|void
name|set
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/** Return the value of this IntWritable. */
DECL|method|get ()
specifier|public
name|int
name|get
parameter_list|()
block|{
return|return
name|value
return|;
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
name|value
operator|=
name|in
operator|.
name|readInt
argument_list|()
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
name|writeInt
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Returns true iff<code>o</code> is a IntWritable with the same value. */
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
name|IntWritable
operator|)
condition|)
return|return
literal|false
return|;
name|IntWritable
name|other
init|=
operator|(
name|IntWritable
operator|)
name|o
decl_stmt|;
return|return
name|this
operator|.
name|value
operator|==
name|other
operator|.
name|value
return|;
block|}
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/** Compares two IntWritables. */
DECL|method|compareTo (Object o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
name|int
name|thisValue
init|=
name|this
operator|.
name|value
decl_stmt|;
name|int
name|thatValue
init|=
operator|(
operator|(
name|IntWritable
operator|)
name|o
operator|)
operator|.
name|value
decl_stmt|;
return|return
operator|(
name|thisValue
operator|<
name|thatValue
condition|?
operator|-
literal|1
else|:
operator|(
name|thisValue
operator|==
name|thatValue
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Integer
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/** A Comparator optimized for IntWritable. */
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
name|IntWritable
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
name|thisValue
init|=
name|readInt
argument_list|(
name|b1
argument_list|,
name|s1
argument_list|)
decl_stmt|;
name|int
name|thatValue
init|=
name|readInt
argument_list|(
name|b2
argument_list|,
name|s2
argument_list|)
decl_stmt|;
return|return
operator|(
name|thisValue
operator|<
name|thatValue
condition|?
operator|-
literal|1
else|:
operator|(
name|thisValue
operator|==
name|thatValue
condition|?
literal|0
else|:
literal|1
operator|)
operator|)
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
name|IntWritable
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

