begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ContainerData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicLong
import|;
end_import

begin_comment
comment|/**  * This class represents the state of a container. if the  * container reading encountered an error when we boot up we will post that  * info to a recovery queue and keep the info in the containerMap.  *<p/>  * if and when the issue is fixed, the expectation is that this entry will be  * deleted by the recovery thread from the containerMap and will insert entry  * instead of modifying this class.  */
end_comment

begin_class
DECL|class|ContainerStatus
specifier|public
class|class
name|ContainerStatus
block|{
DECL|field|containerData
specifier|private
specifier|final
name|ContainerData
name|containerData
decl_stmt|;
comment|/**    * Number of pending deletion blocks in container.    */
DECL|field|numPendingDeletionBlocks
specifier|private
name|int
name|numPendingDeletionBlocks
decl_stmt|;
DECL|field|readBytes
specifier|private
name|AtomicLong
name|readBytes
decl_stmt|;
DECL|field|writeBytes
specifier|private
name|AtomicLong
name|writeBytes
decl_stmt|;
DECL|field|readCount
specifier|private
name|AtomicLong
name|readCount
decl_stmt|;
DECL|field|writeCount
specifier|private
name|AtomicLong
name|writeCount
decl_stmt|;
comment|/**    * Creates a Container Status class.    *    * @param containerData - ContainerData.    */
DECL|method|ContainerStatus (ContainerData containerData)
name|ContainerStatus
parameter_list|(
name|ContainerData
name|containerData
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|containerData
operator|=
name|containerData
expr_stmt|;
name|this
operator|.
name|readCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|readBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
name|this
operator|.
name|writeBytes
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0L
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns container if it is active. It is not active if we have had an    * error and we are waiting for the background threads to fix the issue.    *    * @return ContainerData.    */
DECL|method|getContainer ()
specifier|public
name|ContainerData
name|getContainer
parameter_list|()
block|{
return|return
name|containerData
return|;
block|}
comment|/**    * Increase the count of pending deletion blocks.    *    * @param numBlocks increment number    */
DECL|method|incrPendingDeletionBlocks (int numBlocks)
specifier|public
name|void
name|incrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|+=
name|numBlocks
expr_stmt|;
block|}
comment|/**    * Decrease the count of pending deletion blocks.    *    * @param numBlocks decrement number    */
DECL|method|decrPendingDeletionBlocks (int numBlocks)
specifier|public
name|void
name|decrPendingDeletionBlocks
parameter_list|(
name|int
name|numBlocks
parameter_list|)
block|{
name|this
operator|.
name|numPendingDeletionBlocks
operator|-=
name|numBlocks
expr_stmt|;
block|}
comment|/**    * Get the number of pending deletion blocks.    */
DECL|method|getNumPendingDeletionBlocks ()
specifier|public
name|int
name|getNumPendingDeletionBlocks
parameter_list|()
block|{
return|return
name|this
operator|.
name|numPendingDeletionBlocks
return|;
block|}
comment|/**    * Get the number of bytes read from the container.    * @return the number of bytes read from the container.    */
DECL|method|getReadBytes ()
specifier|public
name|long
name|getReadBytes
parameter_list|()
block|{
return|return
name|readBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of bytes read from the container.    * @param bytes number of bytes read.    */
DECL|method|incrReadBytes (long bytes)
specifier|public
name|void
name|incrReadBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|readBytes
operator|.
name|addAndGet
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of times the container is read.    * @return the number of times the container is read.    */
DECL|method|getReadCount ()
specifier|public
name|long
name|getReadCount
parameter_list|()
block|{
return|return
name|readCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of container read count by 1.    */
DECL|method|incrReadCount ()
specifier|public
name|void
name|incrReadCount
parameter_list|()
block|{
name|this
operator|.
name|readCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the number of bytes write into the container.    * @return the number of bytes write into the container.    */
DECL|method|getWriteBytes ()
specifier|public
name|long
name|getWriteBytes
parameter_list|()
block|{
return|return
name|writeBytes
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of bytes write into the container.    * @param bytes the number of bytes write into the container.    */
DECL|method|incrWriteBytes (long bytes)
specifier|public
name|void
name|incrWriteBytes
parameter_list|(
name|long
name|bytes
parameter_list|)
block|{
name|this
operator|.
name|writeBytes
operator|.
name|addAndGet
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of writes into the container.    * @return the number of writes into the container.    */
DECL|method|getWriteCount ()
specifier|public
name|long
name|getWriteCount
parameter_list|()
block|{
return|return
name|writeCount
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Increase the number of writes into the container by 1.    */
DECL|method|incrWriteCount ()
specifier|public
name|void
name|incrWriteCount
parameter_list|()
block|{
name|this
operator|.
name|writeCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
comment|/**    * Get the number of bytes used by the container.    * @return the number of bytes used by the container.    */
DECL|method|getBytesUsed ()
specifier|public
name|long
name|getBytesUsed
parameter_list|()
block|{
return|return
name|containerData
operator|.
name|getBytesUsed
argument_list|()
return|;
block|}
comment|/**    * Increase the number of bytes used by the container.    * @param used number of bytes used by the container.    * @return the current number of bytes used by the container afert increase.    */
DECL|method|incrBytesUsed (long used)
specifier|public
name|long
name|incrBytesUsed
parameter_list|(
name|long
name|used
parameter_list|)
block|{
return|return
name|containerData
operator|.
name|addBytesUsed
argument_list|(
name|used
argument_list|)
return|;
block|}
comment|/**    * Set the number of bytes used by the container.    * @param used the number of bytes used by the container.    */
DECL|method|setBytesUsed (long used)
specifier|public
name|void
name|setBytesUsed
parameter_list|(
name|long
name|used
parameter_list|)
block|{
name|containerData
operator|.
name|setBytesUsed
argument_list|(
name|used
argument_list|)
expr_stmt|;
block|}
comment|/**    * Decrease the number of bytes used by the container.    * @param reclaimed the number of bytes reclaimed from the container.    * @return the current number of bytes used by the container after decrease.    */
DECL|method|decrBytesUsed (long reclaimed)
specifier|public
name|long
name|decrBytesUsed
parameter_list|(
name|long
name|reclaimed
parameter_list|)
block|{
return|return
name|this
operator|.
name|containerData
operator|.
name|addBytesUsed
argument_list|(
operator|-
literal|1L
operator|*
name|reclaimed
argument_list|)
return|;
block|}
comment|/**    * Get the maximum container size.    * @return the maximum container size.    */
DECL|method|getMaxSize ()
specifier|public
name|long
name|getMaxSize
parameter_list|()
block|{
return|return
name|containerData
operator|.
name|getMaxSize
argument_list|()
return|;
block|}
comment|/**    * Set the maximum container size.    * @param size the maximum container size.    */
DECL|method|setMaxSize (long size)
specifier|public
name|void
name|setMaxSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{
name|this
operator|.
name|containerData
operator|.
name|setMaxSize
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the number of keys in the container.    * @return the number of keys in the container.    */
DECL|method|getNumKeys ()
specifier|public
name|long
name|getNumKeys
parameter_list|()
block|{
return|return
name|containerData
operator|.
name|getKeyCount
argument_list|()
return|;
block|}
block|}
end_class

end_unit

