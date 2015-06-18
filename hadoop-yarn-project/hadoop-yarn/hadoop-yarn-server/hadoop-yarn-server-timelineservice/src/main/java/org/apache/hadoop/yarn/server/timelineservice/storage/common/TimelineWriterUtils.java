begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|common
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
comment|/**  * bunch of utility functions used across TimelineWriter classes  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TimelineWriterUtils
specifier|public
class|class
name|TimelineWriterUtils
block|{
comment|/** empty bytes */
DECL|field|EMPTY_BYTES
specifier|public
specifier|static
specifier|final
name|byte
index|[]
name|EMPTY_BYTES
init|=
operator|new
name|byte
index|[
literal|0
index|]
decl_stmt|;
comment|/**    * Splits the source array into multiple array segments using the given    * separator, up to a maximum of count items. This will naturally produce    * copied byte arrays for each of the split segments. To identify the split    * ranges without the array copies, see    * {@link TimelineWriterUtils#splitRanges(byte[], byte[])}.    *    * @param source    * @param separator    * @return byte[] array after splitting the source    */
DECL|method|split (byte[] source, byte[] separator)
specifier|public
specifier|static
name|byte
index|[]
index|[]
name|split
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|byte
index|[]
name|separator
parameter_list|)
block|{
return|return
name|split
argument_list|(
name|source
argument_list|,
name|separator
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Splits the source array into multiple array segments using the given    * separator, up to a maximum of count items. This will naturally produce    * copied byte arrays for each of the split segments. To identify the split    * ranges without the array copies, see    * {@link TimelineWriterUtils#splitRanges(byte[], byte[])}.    *    * @param source    * @param separator    * @param limit a negative value indicates no limit on number of segments.    * @return byte[][] after splitting the input source    */
DECL|method|split (byte[] source, byte[] separator, int limit)
specifier|public
specifier|static
name|byte
index|[]
index|[]
name|split
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|byte
index|[]
name|separator
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|List
argument_list|<
name|Range
argument_list|>
name|segments
init|=
name|splitRanges
argument_list|(
name|source
argument_list|,
name|separator
argument_list|,
name|limit
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|splits
init|=
operator|new
name|byte
index|[
name|segments
operator|.
name|size
argument_list|()
index|]
index|[]
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
name|segments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Range
name|r
init|=
name|segments
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|byte
index|[]
name|tmp
init|=
operator|new
name|byte
index|[
name|r
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
if|if
condition|(
name|tmp
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|source
argument_list|,
name|r
operator|.
name|start
argument_list|()
argument_list|,
name|tmp
argument_list|,
literal|0
argument_list|,
name|r
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|splits
index|[
name|i
index|]
operator|=
name|tmp
expr_stmt|;
block|}
return|return
name|splits
return|;
block|}
comment|/**    * Returns a list of ranges identifying [start, end) -- closed, open --    * positions within the source byte array that would be split using the    * separator byte array.    */
DECL|method|splitRanges (byte[] source, byte[] separator)
specifier|public
specifier|static
name|List
argument_list|<
name|Range
argument_list|>
name|splitRanges
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|byte
index|[]
name|separator
parameter_list|)
block|{
return|return
name|splitRanges
argument_list|(
name|source
argument_list|,
name|separator
argument_list|,
operator|-
literal|1
argument_list|)
return|;
block|}
comment|/**    * Returns a list of ranges identifying [start, end) -- closed, open --    * positions within the source byte array that would be split using the    * separator byte array.    *    * @param source the source data    * @param separator the separator pattern to look for    * @param limit the maximum number of splits to identify in the source    */
DECL|method|splitRanges (byte[] source, byte[] separator, int limit)
specifier|public
specifier|static
name|List
argument_list|<
name|Range
argument_list|>
name|splitRanges
parameter_list|(
name|byte
index|[]
name|source
parameter_list|,
name|byte
index|[]
name|separator
parameter_list|,
name|int
name|limit
parameter_list|)
block|{
name|List
argument_list|<
name|Range
argument_list|>
name|segments
init|=
operator|new
name|ArrayList
argument_list|<
name|Range
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|source
operator|==
literal|null
operator|)
operator|||
operator|(
name|separator
operator|==
literal|null
operator|)
condition|)
block|{
return|return
name|segments
return|;
block|}
name|int
name|start
init|=
literal|0
decl_stmt|;
name|itersource
label|:
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|source
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|separator
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
if|if
condition|(
name|source
index|[
name|i
operator|+
name|j
index|]
operator|!=
name|separator
index|[
name|j
index|]
condition|)
block|{
continue|continue
name|itersource
continue|;
block|}
block|}
comment|// all separator elements matched
if|if
condition|(
name|limit
operator|>
literal|0
operator|&&
name|segments
operator|.
name|size
argument_list|()
operator|>=
operator|(
name|limit
operator|-
literal|1
operator|)
condition|)
block|{
comment|// everything else goes in one final segment
break|break;
block|}
name|segments
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|start
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|start
operator|=
name|i
operator|+
name|separator
operator|.
name|length
expr_stmt|;
comment|// i will be incremented again in outer for loop
name|i
operator|+=
name|separator
operator|.
name|length
operator|-
literal|1
expr_stmt|;
block|}
comment|// add in remaining to a final range
if|if
condition|(
name|start
operator|<=
name|source
operator|.
name|length
condition|)
block|{
name|segments
operator|.
name|add
argument_list|(
operator|new
name|Range
argument_list|(
name|start
argument_list|,
name|source
operator|.
name|length
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|segments
return|;
block|}
block|}
end_class

end_unit

