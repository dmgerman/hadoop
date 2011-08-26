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
comment|/** A WritableComparable for longs in a variable-length format. Such values take  *  between one and five bytes.  Smaller values take fewer bytes.  *    *  @see org.apache.hadoop.io.WritableUtils#readVLong(DataInput)  */
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
DECL|class|VLongWritable
specifier|public
class|class
name|VLongWritable
implements|implements
name|WritableComparable
argument_list|<
name|VLongWritable
argument_list|>
block|{
DECL|field|value
specifier|private
name|long
name|value
decl_stmt|;
DECL|method|VLongWritable ()
specifier|public
name|VLongWritable
parameter_list|()
block|{}
DECL|method|VLongWritable (long value)
specifier|public
name|VLongWritable
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|set
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Set the value of this LongWritable. */
DECL|method|set (long value)
specifier|public
name|void
name|set
parameter_list|(
name|long
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
comment|/** Return the value of this LongWritable. */
DECL|method|get ()
specifier|public
name|long
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
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
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
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/** Returns true iff<code>o</code> is a VLongWritable with the same value. */
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
name|VLongWritable
operator|)
condition|)
return|return
literal|false
return|;
name|VLongWritable
name|other
init|=
operator|(
name|VLongWritable
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
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
comment|/** Compares two VLongWritables. */
annotation|@
name|Override
DECL|method|compareTo (VLongWritable o)
specifier|public
name|int
name|compareTo
parameter_list|(
name|VLongWritable
name|o
parameter_list|)
block|{
name|long
name|thisValue
init|=
name|this
operator|.
name|value
decl_stmt|;
name|long
name|thatValue
init|=
name|o
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
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

