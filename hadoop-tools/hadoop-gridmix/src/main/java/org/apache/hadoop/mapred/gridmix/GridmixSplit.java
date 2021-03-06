begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|WritableUtils
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
operator|.
name|CombineFileSplit
import|;
end_import

begin_class
DECL|class|GridmixSplit
class|class
name|GridmixSplit
extends|extends
name|CombineFileSplit
block|{
DECL|field|id
specifier|private
name|int
name|id
decl_stmt|;
DECL|field|nSpec
specifier|private
name|int
name|nSpec
decl_stmt|;
DECL|field|maps
specifier|private
name|int
name|maps
decl_stmt|;
DECL|field|reduces
specifier|private
name|int
name|reduces
decl_stmt|;
DECL|field|inputRecords
specifier|private
name|long
name|inputRecords
decl_stmt|;
DECL|field|outputBytes
specifier|private
name|long
name|outputBytes
decl_stmt|;
DECL|field|outputRecords
specifier|private
name|long
name|outputRecords
decl_stmt|;
DECL|field|maxMemory
specifier|private
name|long
name|maxMemory
decl_stmt|;
DECL|field|reduceBytes
specifier|private
name|double
index|[]
name|reduceBytes
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
DECL|field|reduceRecords
specifier|private
name|double
index|[]
name|reduceRecords
init|=
operator|new
name|double
index|[
literal|0
index|]
decl_stmt|;
comment|// Spec for reduces id mod this
DECL|field|reduceOutputBytes
specifier|private
name|long
index|[]
name|reduceOutputBytes
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
DECL|field|reduceOutputRecords
specifier|private
name|long
index|[]
name|reduceOutputRecords
init|=
operator|new
name|long
index|[
literal|0
index|]
decl_stmt|;
DECL|method|GridmixSplit ()
name|GridmixSplit
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
DECL|method|GridmixSplit (CombineFileSplit cfsplit, int maps, int id, long inputBytes, long inputRecords, long outputBytes, long outputRecords, double[] reduceBytes, double[] reduceRecords, long[] reduceOutputBytes, long[] reduceOutputRecords)
specifier|public
name|GridmixSplit
parameter_list|(
name|CombineFileSplit
name|cfsplit
parameter_list|,
name|int
name|maps
parameter_list|,
name|int
name|id
parameter_list|,
name|long
name|inputBytes
parameter_list|,
name|long
name|inputRecords
parameter_list|,
name|long
name|outputBytes
parameter_list|,
name|long
name|outputRecords
parameter_list|,
name|double
index|[]
name|reduceBytes
parameter_list|,
name|double
index|[]
name|reduceRecords
parameter_list|,
name|long
index|[]
name|reduceOutputBytes
parameter_list|,
name|long
index|[]
name|reduceOutputRecords
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|cfsplit
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|maps
operator|=
name|maps
expr_stmt|;
name|reduces
operator|=
name|reduceBytes
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|inputRecords
operator|=
name|inputRecords
expr_stmt|;
name|this
operator|.
name|outputBytes
operator|=
name|outputBytes
expr_stmt|;
name|this
operator|.
name|outputRecords
operator|=
name|outputRecords
expr_stmt|;
name|this
operator|.
name|reduceBytes
operator|=
name|reduceBytes
expr_stmt|;
name|this
operator|.
name|reduceRecords
operator|=
name|reduceRecords
expr_stmt|;
name|nSpec
operator|=
name|reduceOutputBytes
operator|.
name|length
expr_stmt|;
name|this
operator|.
name|reduceOutputBytes
operator|=
name|reduceOutputBytes
expr_stmt|;
name|this
operator|.
name|reduceOutputRecords
operator|=
name|reduceOutputRecords
expr_stmt|;
block|}
DECL|method|getId ()
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
DECL|method|getMapCount ()
specifier|public
name|int
name|getMapCount
parameter_list|()
block|{
return|return
name|maps
return|;
block|}
DECL|method|getInputRecords ()
specifier|public
name|long
name|getInputRecords
parameter_list|()
block|{
return|return
name|inputRecords
return|;
block|}
DECL|method|getOutputBytes ()
specifier|public
name|long
index|[]
name|getOutputBytes
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|==
name|reduces
condition|)
block|{
return|return
operator|new
name|long
index|[]
block|{
name|outputBytes
block|}
return|;
block|}
specifier|final
name|long
index|[]
name|ret
init|=
operator|new
name|long
index|[
name|reduces
index|]
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
name|reduces
condition|;
operator|++
name|i
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|round
argument_list|(
name|outputBytes
operator|*
name|reduceBytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|getOutputRecords ()
specifier|public
name|long
index|[]
name|getOutputRecords
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|==
name|reduces
condition|)
block|{
return|return
operator|new
name|long
index|[]
block|{
name|outputRecords
block|}
return|;
block|}
specifier|final
name|long
index|[]
name|ret
init|=
operator|new
name|long
index|[
name|reduces
index|]
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
name|reduces
condition|;
operator|++
name|i
control|)
block|{
name|ret
index|[
name|i
index|]
operator|=
name|Math
operator|.
name|round
argument_list|(
name|outputRecords
operator|*
name|reduceRecords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
DECL|method|getReduceBytes (int i)
specifier|public
name|long
name|getReduceBytes
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|reduceOutputBytes
index|[
name|i
index|]
return|;
block|}
DECL|method|getReduceRecords (int i)
specifier|public
name|long
name|getReduceRecords
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|reduceOutputRecords
index|[
name|i
index|]
return|;
block|}
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
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|maps
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|inputRecords
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|outputBytes
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|outputRecords
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|maxMemory
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|reduces
argument_list|)
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
name|reduces
condition|;
operator|++
name|i
control|)
block|{
name|out
operator|.
name|writeDouble
argument_list|(
name|reduceBytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeDouble
argument_list|(
name|reduceRecords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|WritableUtils
operator|.
name|writeVInt
argument_list|(
name|out
argument_list|,
name|nSpec
argument_list|)
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
name|nSpec
condition|;
operator|++
name|i
control|)
block|{
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|reduceOutputBytes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|reduceOutputRecords
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
name|id
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|maps
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|inputRecords
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|outputBytes
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|outputRecords
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|maxMemory
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|reduces
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|reduceBytes
operator|.
name|length
operator|<
name|reduces
condition|)
block|{
name|reduceBytes
operator|=
operator|new
name|double
index|[
name|reduces
index|]
expr_stmt|;
name|reduceRecords
operator|=
operator|new
name|double
index|[
name|reduces
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|reduces
condition|;
operator|++
name|i
control|)
block|{
name|reduceBytes
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
name|reduceRecords
index|[
name|i
index|]
operator|=
name|in
operator|.
name|readDouble
argument_list|()
expr_stmt|;
block|}
name|nSpec
operator|=
name|WritableUtils
operator|.
name|readVInt
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|reduceOutputBytes
operator|.
name|length
operator|<
name|nSpec
condition|)
block|{
name|reduceOutputBytes
operator|=
operator|new
name|long
index|[
name|nSpec
index|]
expr_stmt|;
name|reduceOutputRecords
operator|=
operator|new
name|long
index|[
name|nSpec
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nSpec
condition|;
operator|++
name|i
control|)
block|{
name|reduceOutputBytes
index|[
name|i
index|]
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|reduceOutputRecords
index|[
name|i
index|]
operator|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

