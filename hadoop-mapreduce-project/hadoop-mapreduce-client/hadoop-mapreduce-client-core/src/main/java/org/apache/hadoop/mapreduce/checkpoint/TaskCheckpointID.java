begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.checkpoint
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|checkpoint
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
name|fs
operator|.
name|Path
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
name|Text
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
name|mapred
operator|.
name|Counters
import|;
end_import

begin_comment
comment|/**  * Implementation of CheckpointID used in MR. It contains a reference to an  * underlying FileSsytem based checkpoint, and various metadata about the  * cost of checkpoints and other counters. This is sent by the task to the AM  * to be stored and provided to the next execution of the same task.  */
end_comment

begin_class
DECL|class|TaskCheckpointID
specifier|public
class|class
name|TaskCheckpointID
implements|implements
name|CheckpointID
block|{
DECL|field|rawId
name|FSCheckpointID
name|rawId
decl_stmt|;
DECL|field|partialOutput
specifier|private
name|List
argument_list|<
name|Path
argument_list|>
name|partialOutput
decl_stmt|;
DECL|field|counters
specifier|private
name|Counters
name|counters
decl_stmt|;
DECL|method|TaskCheckpointID ()
specifier|public
name|TaskCheckpointID
parameter_list|()
block|{
name|this
operator|.
name|rawId
operator|=
operator|new
name|FSCheckpointID
argument_list|()
expr_stmt|;
name|this
operator|.
name|partialOutput
operator|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|TaskCheckpointID (FSCheckpointID rawId, List<Path> partialOutput, Counters counters)
specifier|public
name|TaskCheckpointID
parameter_list|(
name|FSCheckpointID
name|rawId
parameter_list|,
name|List
argument_list|<
name|Path
argument_list|>
name|partialOutput
parameter_list|,
name|Counters
name|counters
parameter_list|)
block|{
name|this
operator|.
name|rawId
operator|=
name|rawId
expr_stmt|;
name|this
operator|.
name|counters
operator|=
name|counters
expr_stmt|;
if|if
condition|(
name|partialOutput
operator|==
literal|null
condition|)
name|this
operator|.
name|partialOutput
operator|=
operator|new
name|ArrayList
argument_list|<
name|Path
argument_list|>
argument_list|()
expr_stmt|;
else|else
name|this
operator|.
name|partialOutput
operator|=
name|partialOutput
expr_stmt|;
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
name|counters
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
if|if
condition|(
name|partialOutput
operator|==
literal|null
condition|)
block|{
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|WritableUtils
operator|.
name|writeVLong
argument_list|(
name|out
argument_list|,
name|partialOutput
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Path
name|p
range|:
name|partialOutput
control|)
block|{
name|Text
operator|.
name|writeString
argument_list|(
name|out
argument_list|,
name|p
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|rawId
operator|.
name|write
argument_list|(
name|out
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
name|partialOutput
operator|.
name|clear
argument_list|()
expr_stmt|;
name|counters
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|long
name|numPout
init|=
name|WritableUtils
operator|.
name|readVLong
argument_list|(
name|in
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
name|numPout
condition|;
name|i
operator|++
control|)
name|partialOutput
operator|.
name|add
argument_list|(
operator|new
name|Path
argument_list|(
name|Text
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|rawId
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
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
name|TaskCheckpointID
condition|)
block|{
return|return
name|this
operator|.
name|rawId
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|TaskCheckpointID
operator|)
name|other
operator|)
operator|.
name|rawId
argument_list|)
operator|&&
name|this
operator|.
name|counters
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|TaskCheckpointID
operator|)
name|other
operator|)
operator|.
name|counters
argument_list|)
operator|&&
name|this
operator|.
name|partialOutput
operator|.
name|containsAll
argument_list|(
operator|(
operator|(
name|TaskCheckpointID
operator|)
name|other
operator|)
operator|.
name|partialOutput
argument_list|)
operator|&&
operator|(
operator|(
name|TaskCheckpointID
operator|)
name|other
operator|)
operator|.
name|partialOutput
operator|.
name|containsAll
argument_list|(
name|this
operator|.
name|partialOutput
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
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
name|rawId
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**    * @return the size of the checkpoint in bytes    */
DECL|method|getCheckpointBytes ()
specifier|public
name|long
name|getCheckpointBytes
parameter_list|()
block|{
return|return
name|counters
operator|.
name|findCounter
argument_list|(
name|EnumCounter
operator|.
name|CHECKPOINT_BYTES
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
comment|/**    * @return how long it took to take this checkpoint    */
DECL|method|getCheckpointTime ()
specifier|public
name|long
name|getCheckpointTime
parameter_list|()
block|{
return|return
name|counters
operator|.
name|findCounter
argument_list|(
name|EnumCounter
operator|.
name|CHECKPOINT_MS
argument_list|)
operator|.
name|getValue
argument_list|()
return|;
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|rawId
operator|.
name|toString
argument_list|()
operator|+
literal|" counters:"
operator|+
name|counters
return|;
block|}
DECL|method|getPartialCommittedOutput ()
specifier|public
name|List
argument_list|<
name|Path
argument_list|>
name|getPartialCommittedOutput
parameter_list|()
block|{
return|return
name|partialOutput
return|;
block|}
DECL|method|getCounters ()
specifier|public
name|Counters
name|getCounters
parameter_list|()
block|{
return|return
name|counters
return|;
block|}
block|}
end_class

end_unit

