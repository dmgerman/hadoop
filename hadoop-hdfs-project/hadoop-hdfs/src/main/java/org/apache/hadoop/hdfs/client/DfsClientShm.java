begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.client
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|client
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|Iterator
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
name|hdfs
operator|.
name|ShortCircuitShm
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
name|hdfs
operator|.
name|client
operator|.
name|DfsClientShmManager
operator|.
name|EndpointShmManager
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
name|hdfs
operator|.
name|net
operator|.
name|DomainPeer
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
name|net
operator|.
name|unix
operator|.
name|DomainSocket
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
name|net
operator|.
name|unix
operator|.
name|DomainSocketWatcher
import|;
end_import

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

begin_comment
comment|/**  * DfsClientShm is a subclass of ShortCircuitShm which is used by the  * DfsClient.  * When the UNIX domain socket associated with this shared memory segment  * closes unexpectedly, we mark the slots inside this segment as stale.  * ShortCircuitReplica objects that contain stale slots are themselves stale,  * and will not be used to service new reads or mmap operations.  * However, in-progress read or mmap operations will continue to proceed.  * Once the last slot is deallocated, the segment can be safely munmapped.  */
end_comment

begin_class
DECL|class|DfsClientShm
specifier|public
class|class
name|DfsClientShm
extends|extends
name|ShortCircuitShm
implements|implements
name|DomainSocketWatcher
operator|.
name|Handler
block|{
comment|/**    * The EndpointShmManager associated with this shared memory segment.    */
DECL|field|manager
specifier|private
specifier|final
name|EndpointShmManager
name|manager
decl_stmt|;
comment|/**    * The UNIX domain socket associated with this DfsClientShm.    * We rely on the DomainSocketWatcher to close the socket associated with    * this DomainPeer when necessary.    */
DECL|field|peer
specifier|private
specifier|final
name|DomainPeer
name|peer
decl_stmt|;
comment|/**    * True if this shared memory segment has lost its connection to the    * DataNode.    *    * {@link DfsClientShm#handle} sets this to true.    */
DECL|field|stale
specifier|private
name|boolean
name|stale
init|=
literal|false
decl_stmt|;
DECL|method|DfsClientShm (ShmId shmId, FileInputStream stream, EndpointShmManager manager, DomainPeer peer)
name|DfsClientShm
parameter_list|(
name|ShmId
name|shmId
parameter_list|,
name|FileInputStream
name|stream
parameter_list|,
name|EndpointShmManager
name|manager
parameter_list|,
name|DomainPeer
name|peer
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|shmId
argument_list|,
name|stream
argument_list|)
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|peer
operator|=
name|peer
expr_stmt|;
block|}
DECL|method|getEndpointShmManager ()
specifier|public
name|EndpointShmManager
name|getEndpointShmManager
parameter_list|()
block|{
return|return
name|manager
return|;
block|}
DECL|method|getPeer ()
specifier|public
name|DomainPeer
name|getPeer
parameter_list|()
block|{
return|return
name|peer
return|;
block|}
comment|/**    * Determine if the shared memory segment is stale.    *    * This must be called with the DfsClientShmManager lock held.    *    * @return   True if the shared memory segment is stale.    */
DECL|method|isStale ()
specifier|public
specifier|synchronized
name|boolean
name|isStale
parameter_list|()
block|{
return|return
name|stale
return|;
block|}
comment|/**    * Handle the closure of the UNIX domain socket associated with this shared    * memory segment by marking this segment as stale.    *    * If there are no slots associated with this shared memory segment, it will    * be freed immediately in this function.    */
annotation|@
name|Override
DECL|method|handle (DomainSocket sock)
specifier|public
name|boolean
name|handle
parameter_list|(
name|DomainSocket
name|sock
parameter_list|)
block|{
name|manager
operator|.
name|unregisterShm
argument_list|(
name|getShmId
argument_list|()
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|stale
argument_list|)
expr_stmt|;
name|stale
operator|=
literal|true
expr_stmt|;
name|boolean
name|hadSlots
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Slot
argument_list|>
name|iter
init|=
name|slotIterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Slot
name|slot
init|=
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|slot
operator|.
name|makeInvalid
argument_list|()
expr_stmt|;
name|hadSlots
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|hadSlots
condition|)
block|{
name|free
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

