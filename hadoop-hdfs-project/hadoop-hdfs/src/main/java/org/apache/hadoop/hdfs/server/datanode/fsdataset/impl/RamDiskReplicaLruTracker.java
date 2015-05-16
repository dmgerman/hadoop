begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|impl
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
name|collect
operator|.
name|TreeMultimap
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Time
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * An implementation of RamDiskReplicaTracker that uses an LRU  * eviction scheme.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|RamDiskReplicaLruTracker
specifier|public
class|class
name|RamDiskReplicaLruTracker
extends|extends
name|RamDiskReplicaTracker
block|{
DECL|class|RamDiskReplicaLru
specifier|private
class|class
name|RamDiskReplicaLru
extends|extends
name|RamDiskReplica
block|{
DECL|field|lastUsedTime
name|long
name|lastUsedTime
decl_stmt|;
DECL|method|RamDiskReplicaLru (String bpid, long blockId, FsVolumeImpl ramDiskVolume, long lockedBytesReserved)
specifier|private
name|RamDiskReplicaLru
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|,
name|FsVolumeImpl
name|ramDiskVolume
parameter_list|,
name|long
name|lockedBytesReserved
parameter_list|)
block|{
name|super
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|,
name|ramDiskVolume
argument_list|,
name|lockedBytesReserved
argument_list|)
expr_stmt|;
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
name|super
operator|.
name|hashCode
argument_list|()
return|;
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
return|return
name|super
operator|.
name|equals
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
comment|/**    * Map of blockpool ID to<map of blockID to ReplicaInfo>.    */
DECL|field|replicaMaps
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
argument_list|>
name|replicaMaps
decl_stmt|;
comment|/**    * Queue of replicas that need to be written to disk.    * Stale entries are GC'd by dequeueNextReplicaToPersist.    */
DECL|field|replicasNotPersisted
name|Queue
argument_list|<
name|RamDiskReplicaLru
argument_list|>
name|replicasNotPersisted
decl_stmt|;
comment|/**    * Map of persisted replicas ordered by their last use times.    */
DECL|field|replicasPersisted
name|TreeMultimap
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|replicasPersisted
decl_stmt|;
DECL|method|RamDiskReplicaLruTracker ()
name|RamDiskReplicaLruTracker
parameter_list|()
block|{
name|replicaMaps
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|replicasNotPersisted
operator|=
operator|new
name|LinkedList
argument_list|<>
argument_list|()
expr_stmt|;
name|replicasPersisted
operator|=
name|TreeMultimap
operator|.
name|create
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|addReplica (final String bpid, final long blockId, final FsVolumeImpl transientVolume, long lockedBytesReserved)
specifier|synchronized
name|void
name|addReplica
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|,
specifier|final
name|FsVolumeImpl
name|transientVolume
parameter_list|,
name|long
name|lockedBytesReserved
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|map
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|replicaMaps
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
operator|new
name|RamDiskReplicaLru
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|,
name|transientVolume
argument_list|,
name|lockedBytesReserved
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|blockId
argument_list|,
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
name|replicasNotPersisted
operator|.
name|add
argument_list|(
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|touch (final String bpid, final long blockId)
specifier|synchronized
name|void
name|touch
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|map
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ramDiskReplicaLru
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|ramDiskReplicaLru
operator|.
name|numReads
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
comment|// Reinsert the replica with its new timestamp.
if|if
condition|(
name|replicasPersisted
operator|.
name|remove
argument_list|(
name|ramDiskReplicaLru
operator|.
name|lastUsedTime
argument_list|,
name|ramDiskReplicaLru
argument_list|)
condition|)
block|{
name|ramDiskReplicaLru
operator|.
name|lastUsedTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|replicasPersisted
operator|.
name|put
argument_list|(
name|ramDiskReplicaLru
operator|.
name|lastUsedTime
argument_list|,
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|recordStartLazyPersist ( final String bpid, final long blockId, FsVolumeImpl checkpointVolume)
specifier|synchronized
name|void
name|recordStartLazyPersist
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|,
name|FsVolumeImpl
name|checkpointVolume
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|map
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
name|ramDiskReplicaLru
operator|.
name|setLazyPersistVolume
argument_list|(
name|checkpointVolume
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recordEndLazyPersist ( final String bpid, final long blockId, final File[] savedFiles)
specifier|synchronized
name|void
name|recordEndLazyPersist
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|,
specifier|final
name|File
index|[]
name|savedFiles
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|map
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ramDiskReplicaLru
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown replica bpid="
operator|+
name|bpid
operator|+
literal|"; blockId="
operator|+
name|blockId
argument_list|)
throw|;
block|}
name|ramDiskReplicaLru
operator|.
name|recordSavedBlockFiles
argument_list|(
name|savedFiles
argument_list|)
expr_stmt|;
if|if
condition|(
name|replicasNotPersisted
operator|.
name|peek
argument_list|()
operator|==
name|ramDiskReplicaLru
condition|)
block|{
comment|// Common case.
name|replicasNotPersisted
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// Caller error? Fallback to O(n) removal.
name|replicasNotPersisted
operator|.
name|remove
argument_list|(
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
block|}
name|ramDiskReplicaLru
operator|.
name|lastUsedTime
operator|=
name|Time
operator|.
name|monotonicNow
argument_list|()
expr_stmt|;
name|replicasPersisted
operator|.
name|put
argument_list|(
name|ramDiskReplicaLru
operator|.
name|lastUsedTime
argument_list|,
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
name|ramDiskReplicaLru
operator|.
name|isPersisted
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|dequeueNextReplicaToPersist ()
specifier|synchronized
name|RamDiskReplicaLru
name|dequeueNextReplicaToPersist
parameter_list|()
block|{
while|while
condition|(
name|replicasNotPersisted
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
name|replicasNotPersisted
operator|.
name|remove
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|replicaMap
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|ramDiskReplicaLru
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicaMap
operator|!=
literal|null
operator|&&
name|replicaMap
operator|.
name|get
argument_list|(
name|ramDiskReplicaLru
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|ramDiskReplicaLru
return|;
block|}
comment|// The replica no longer exists, look for the next one.
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|reenqueueReplicaNotPersisted (final RamDiskReplica ramDiskReplicaLru)
specifier|synchronized
name|void
name|reenqueueReplicaNotPersisted
parameter_list|(
specifier|final
name|RamDiskReplica
name|ramDiskReplicaLru
parameter_list|)
block|{
name|replicasNotPersisted
operator|.
name|add
argument_list|(
operator|(
name|RamDiskReplicaLru
operator|)
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|numReplicasNotPersisted ()
specifier|synchronized
name|int
name|numReplicasNotPersisted
parameter_list|()
block|{
return|return
name|replicasNotPersisted
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNextCandidateForEviction ()
specifier|synchronized
name|RamDiskReplicaLru
name|getNextCandidateForEviction
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|RamDiskReplicaLru
argument_list|>
name|it
init|=
name|replicasPersisted
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|replicaMap
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|ramDiskReplicaLru
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicaMap
operator|!=
literal|null
operator|&&
name|replicaMap
operator|.
name|get
argument_list|(
name|ramDiskReplicaLru
operator|.
name|getBlockId
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|ramDiskReplicaLru
return|;
block|}
comment|// The replica no longer exists, look for the next one.
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Discard any state we are tracking for the given replica. This could mean    * the block is either deleted from the block space or the replica is no longer    * on transient storage.    *    * @param deleteSavedCopies true if we should delete the saved copies on    *                          persistent storage. This should be set by the    *                          caller when the block is no longer needed.    */
annotation|@
name|Override
DECL|method|discardReplica ( final String bpid, final long blockId, boolean deleteSavedCopies)
specifier|synchronized
name|void
name|discardReplica
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|,
name|boolean
name|deleteSavedCopies
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|map
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|RamDiskReplicaLru
name|ramDiskReplicaLru
init|=
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
if|if
condition|(
name|ramDiskReplicaLru
operator|==
literal|null
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|deleteSavedCopies
condition|)
block|{
name|ramDiskReplicaLru
operator|.
name|deleteSavedFiles
argument_list|()
expr_stmt|;
block|}
name|map
operator|.
name|remove
argument_list|(
name|blockId
argument_list|)
expr_stmt|;
name|replicasPersisted
operator|.
name|remove
argument_list|(
name|ramDiskReplicaLru
operator|.
name|lastUsedTime
argument_list|,
name|ramDiskReplicaLru
argument_list|)
expr_stmt|;
comment|// replicasNotPersisted will be lazily GC'ed.
block|}
annotation|@
name|Override
DECL|method|getReplica ( final String bpid, final long blockId)
specifier|synchronized
name|RamDiskReplica
name|getReplica
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|RamDiskReplicaLru
argument_list|>
name|map
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
return|;
block|}
block|}
end_class

end_unit

