begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

begin_comment
comment|/**  * OffsetRange is the range of read/write request. A single point (e.g.,[5,5])  * is not a valid range.  */
end_comment

begin_class
DECL|class|OffsetRange
specifier|public
class|class
name|OffsetRange
implements|implements
name|Comparable
argument_list|<
name|OffsetRange
argument_list|>
block|{
DECL|field|min
specifier|private
specifier|final
name|long
name|min
decl_stmt|;
DECL|field|max
specifier|private
specifier|final
name|long
name|max
decl_stmt|;
DECL|method|OffsetRange (long min, long max)
name|OffsetRange
parameter_list|(
name|long
name|min
parameter_list|,
name|long
name|max
parameter_list|)
block|{
if|if
condition|(
operator|(
name|min
operator|>=
name|max
operator|)
operator|||
operator|(
name|min
operator|<
literal|0
operator|)
operator|||
operator|(
name|max
operator|<
literal|0
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Wrong offset range: ("
operator|+
name|min
operator|+
literal|","
operator|+
name|max
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|min
operator|=
name|min
expr_stmt|;
name|this
operator|.
name|max
operator|=
name|max
expr_stmt|;
block|}
DECL|method|getMin ()
name|long
name|getMin
parameter_list|()
block|{
return|return
name|min
return|;
block|}
DECL|method|getMax ()
name|long
name|getMax
parameter_list|()
block|{
return|return
name|max
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
call|(
name|int
call|)
argument_list|(
name|min
operator|^
name|max
argument_list|)
return|;
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
assert|assert
operator|(
name|o
operator|instanceof
name|OffsetRange
operator|)
assert|;
name|OffsetRange
name|range
init|=
operator|(
name|OffsetRange
operator|)
name|o
decl_stmt|;
return|return
operator|(
name|min
operator|==
name|range
operator|.
name|getMin
argument_list|()
operator|)
operator|&&
operator|(
name|max
operator|==
name|range
operator|.
name|getMax
argument_list|()
operator|)
return|;
block|}
DECL|method|compareTo (long left, long right)
specifier|private
specifier|static
name|int
name|compareTo
parameter_list|(
name|long
name|left
parameter_list|,
name|long
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|<
name|right
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|left
operator|>
name|right
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
literal|0
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|compareTo (OffsetRange other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|OffsetRange
name|other
parameter_list|)
block|{
specifier|final
name|int
name|d
init|=
name|compareTo
argument_list|(
name|min
argument_list|,
name|other
operator|.
name|getMin
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|d
operator|!=
literal|0
condition|?
name|d
else|:
name|compareTo
argument_list|(
name|max
argument_list|,
name|other
operator|.
name|getMax
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

