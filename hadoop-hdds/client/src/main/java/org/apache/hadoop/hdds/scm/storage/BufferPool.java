begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.storage
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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

begin_comment
comment|/**  * This class creates and manages pool of n buffers.  */
end_comment

begin_class
DECL|class|BufferPool
specifier|public
class|class
name|BufferPool
block|{
DECL|field|bufferList
specifier|private
name|List
argument_list|<
name|ByteBuffer
argument_list|>
name|bufferList
decl_stmt|;
DECL|field|currentBufferIndex
specifier|private
name|int
name|currentBufferIndex
decl_stmt|;
DECL|field|bufferSize
specifier|private
specifier|final
name|int
name|bufferSize
decl_stmt|;
DECL|field|capacity
specifier|private
specifier|final
name|int
name|capacity
decl_stmt|;
DECL|method|BufferPool (int bufferSize, int capacity)
specifier|public
name|BufferPool
parameter_list|(
name|int
name|bufferSize
parameter_list|,
name|int
name|capacity
parameter_list|)
block|{
name|this
operator|.
name|capacity
operator|=
name|capacity
expr_stmt|;
name|this
operator|.
name|bufferSize
operator|=
name|bufferSize
expr_stmt|;
name|bufferList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|currentBufferIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|getBuffer ()
specifier|public
name|ByteBuffer
name|getBuffer
parameter_list|()
block|{
return|return
name|currentBufferIndex
operator|==
operator|-
literal|1
condition|?
literal|null
else|:
name|bufferList
operator|.
name|get
argument_list|(
name|currentBufferIndex
argument_list|)
return|;
block|}
comment|/**    * If the currentBufferIndex is less than the buffer size - 1,    * it means, the next buffer in the list has been freed up for    * rewriting. Reuse the next available buffer in such cases.    *    * In case, the currentBufferIndex == buffer.size and buffer size is still    * less than the capacity to be allocated, just allocate a buffer of size    * chunk size.    *    */
DECL|method|allocateBufferIfNeeded ()
specifier|public
name|ByteBuffer
name|allocateBufferIfNeeded
parameter_list|()
block|{
name|ByteBuffer
name|buffer
init|=
name|getBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|buffer
operator|!=
literal|null
operator|&&
name|buffer
operator|.
name|hasRemaining
argument_list|()
condition|)
block|{
return|return
name|buffer
return|;
block|}
if|if
condition|(
name|currentBufferIndex
operator|<
name|bufferList
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|buffer
operator|=
name|getBuffer
argument_list|(
name|currentBufferIndex
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buffer
operator|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|bufferSize
argument_list|)
expr_stmt|;
name|bufferList
operator|.
name|add
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|bufferList
operator|.
name|size
argument_list|()
operator|<=
name|capacity
argument_list|)
expr_stmt|;
name|currentBufferIndex
operator|++
expr_stmt|;
comment|// TODO: Turn the below precondition check on when Standalone pipeline
comment|// is removed in the write path in tests
comment|// Preconditions.checkArgument(buffer.position() == 0);
return|return
name|buffer
return|;
block|}
DECL|method|releaseBuffer ()
specifier|public
name|void
name|releaseBuffer
parameter_list|()
block|{
comment|// always remove from head of the list and append at last
name|ByteBuffer
name|buffer
init|=
name|bufferList
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|buffer
operator|.
name|clear
argument_list|()
expr_stmt|;
name|bufferList
operator|.
name|add
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|currentBufferIndex
operator|--
expr_stmt|;
block|}
DECL|method|clearBufferPool ()
specifier|public
name|void
name|clearBufferPool
parameter_list|()
block|{
name|bufferList
operator|.
name|clear
argument_list|()
expr_stmt|;
name|currentBufferIndex
operator|=
operator|-
literal|1
expr_stmt|;
block|}
DECL|method|checkBufferPoolEmpty ()
specifier|public
name|void
name|checkBufferPoolEmpty
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|computeBufferData
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|computeBufferData ()
specifier|public
name|long
name|computeBufferData
parameter_list|()
block|{
return|return
name|bufferList
operator|.
name|stream
argument_list|()
operator|.
name|mapToInt
argument_list|(
name|value
lambda|->
name|value
operator|.
name|position
argument_list|()
argument_list|)
operator|.
name|sum
argument_list|()
return|;
block|}
DECL|method|getSize ()
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|bufferList
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getBuffer (int index)
name|ByteBuffer
name|getBuffer
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|bufferList
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
block|}
end_class

end_unit

