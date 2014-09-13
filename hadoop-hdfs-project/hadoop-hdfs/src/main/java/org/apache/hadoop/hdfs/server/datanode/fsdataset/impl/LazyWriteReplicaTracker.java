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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
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

begin_class
DECL|class|LazyWriteReplicaTracker
class|class
name|LazyWriteReplicaTracker
block|{
DECL|enum|State
enum|enum
name|State
block|{
DECL|enumConstant|IN_MEMORY
name|IN_MEMORY
block|,
DECL|enumConstant|LAZY_PERSIST_IN_PROGRESS
name|LAZY_PERSIST_IN_PROGRESS
block|,
DECL|enumConstant|LAZY_PERSIST_COMPLETE
name|LAZY_PERSIST_COMPLETE
block|,   }
DECL|class|ReplicaState
specifier|static
class|class
name|ReplicaState
implements|implements
name|Comparable
argument_list|<
name|ReplicaState
argument_list|>
block|{
DECL|field|bpid
specifier|final
name|String
name|bpid
decl_stmt|;
DECL|field|blockId
specifier|final
name|long
name|blockId
decl_stmt|;
DECL|field|state
name|State
name|state
decl_stmt|;
comment|/**      * transient storage volume that holds the original replica.      */
DECL|field|transientVolume
specifier|final
name|FsVolumeSpi
name|transientVolume
decl_stmt|;
comment|/**      * Persistent volume that holds or will hold the saved replica.      */
DECL|field|lazyPersistVolume
name|FsVolumeImpl
name|lazyPersistVolume
decl_stmt|;
DECL|field|savedMetaFile
name|File
name|savedMetaFile
decl_stmt|;
DECL|field|savedBlockFile
name|File
name|savedBlockFile
decl_stmt|;
DECL|method|ReplicaState (final String bpid, final long blockId, FsVolumeSpi transientVolume)
name|ReplicaState
parameter_list|(
specifier|final
name|String
name|bpid
parameter_list|,
specifier|final
name|long
name|blockId
parameter_list|,
name|FsVolumeSpi
name|transientVolume
parameter_list|)
block|{
name|this
operator|.
name|bpid
operator|=
name|bpid
expr_stmt|;
name|this
operator|.
name|blockId
operator|=
name|blockId
expr_stmt|;
name|this
operator|.
name|transientVolume
operator|=
name|transientVolume
expr_stmt|;
name|state
operator|=
name|State
operator|.
name|IN_MEMORY
expr_stmt|;
name|lazyPersistVolume
operator|=
literal|null
expr_stmt|;
name|savedMetaFile
operator|=
literal|null
expr_stmt|;
name|savedBlockFile
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|deleteSavedFiles ()
name|void
name|deleteSavedFiles
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|savedBlockFile
operator|!=
literal|null
condition|)
block|{
name|savedBlockFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|savedBlockFile
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|savedMetaFile
operator|!=
literal|null
condition|)
block|{
name|savedMetaFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|savedMetaFile
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// Ignore any exceptions.
block|}
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
literal|"[Bpid="
operator|+
name|bpid
operator|+
literal|";blockId="
operator|+
name|blockId
operator|+
literal|"]"
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
name|bpid
operator|.
name|hashCode
argument_list|()
operator|^
operator|(
name|int
operator|)
name|blockId
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
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|other
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|other
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|ReplicaState
name|otherState
init|=
operator|(
name|ReplicaState
operator|)
name|other
decl_stmt|;
return|return
operator|(
name|otherState
operator|.
name|bpid
operator|.
name|equals
argument_list|(
name|bpid
argument_list|)
operator|&&
name|otherState
operator|.
name|blockId
operator|==
name|blockId
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (ReplicaState other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|ReplicaState
name|other
parameter_list|)
block|{
if|if
condition|(
name|blockId
operator|==
name|other
operator|.
name|blockId
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|blockId
operator|<
name|other
operator|.
name|blockId
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
DECL|field|fsDataset
specifier|final
name|FsDatasetImpl
name|fsDataset
decl_stmt|;
comment|/**    * Map of blockpool ID to map of blockID to ReplicaInfo.    */
DECL|field|replicaMaps
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ReplicaState
argument_list|>
argument_list|>
name|replicaMaps
decl_stmt|;
comment|/**    * Queue of replicas that need to be written to disk.    * Stale entries are GC'd by dequeueNextReplicaToPersist.    */
DECL|field|replicasNotPersisted
specifier|final
name|Queue
argument_list|<
name|ReplicaState
argument_list|>
name|replicasNotPersisted
decl_stmt|;
comment|/**    * Queue of replicas in the order in which they were persisted.    * We'll dequeue them in the same order.    * We can improve the eviction scheme later.    * Stale entries are GC'd by getNextCandidateForEviction.    */
DECL|field|replicasPersisted
specifier|final
name|Queue
argument_list|<
name|ReplicaState
argument_list|>
name|replicasPersisted
decl_stmt|;
DECL|method|LazyWriteReplicaTracker (final FsDatasetImpl fsDataset)
name|LazyWriteReplicaTracker
parameter_list|(
specifier|final
name|FsDatasetImpl
name|fsDataset
parameter_list|)
block|{
name|this
operator|.
name|fsDataset
operator|=
name|fsDataset
expr_stmt|;
name|replicaMaps
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|ReplicaState
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|replicasNotPersisted
operator|=
operator|new
name|LinkedList
argument_list|<
name|ReplicaState
argument_list|>
argument_list|()
expr_stmt|;
name|replicasPersisted
operator|=
operator|new
name|LinkedList
argument_list|<
name|ReplicaState
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|addReplica (String bpid, long blockId, final FsVolumeSpi transientVolume)
specifier|synchronized
name|void
name|addReplica
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|,
specifier|final
name|FsVolumeSpi
name|transientVolume
parameter_list|)
block|{
name|Map
argument_list|<
name|Long
argument_list|,
name|ReplicaState
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
argument_list|<
name|Long
argument_list|,
name|ReplicaState
argument_list|>
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
name|ReplicaState
name|replicaState
init|=
operator|new
name|ReplicaState
argument_list|(
name|bpid
argument_list|,
name|blockId
argument_list|,
name|transientVolume
argument_list|)
decl_stmt|;
name|map
operator|.
name|put
argument_list|(
name|blockId
argument_list|,
name|replicaState
argument_list|)
expr_stmt|;
name|replicasNotPersisted
operator|.
name|add
argument_list|(
name|replicaState
argument_list|)
expr_stmt|;
block|}
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
name|ReplicaState
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
name|ReplicaState
name|replicaState
init|=
name|map
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
name|replicaState
operator|.
name|state
operator|=
name|State
operator|.
name|LAZY_PERSIST_IN_PROGRESS
expr_stmt|;
name|replicaState
operator|.
name|lazyPersistVolume
operator|=
name|checkpointVolume
expr_stmt|;
block|}
comment|/**    * @param bpid    * @param blockId    * @param savedFiles The saved meta and block files, in that order.    */
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
name|ReplicaState
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
name|ReplicaState
name|replicaState
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
name|replicaState
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
name|replicaState
operator|.
name|state
operator|=
name|State
operator|.
name|LAZY_PERSIST_COMPLETE
expr_stmt|;
name|replicaState
operator|.
name|savedMetaFile
operator|=
name|savedFiles
index|[
literal|0
index|]
expr_stmt|;
name|replicaState
operator|.
name|savedBlockFile
operator|=
name|savedFiles
index|[
literal|1
index|]
expr_stmt|;
if|if
condition|(
name|replicasNotPersisted
operator|.
name|peek
argument_list|()
operator|==
name|replicaState
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
comment|// Should never occur in practice as lazy writer always persists
comment|// the replica at the head of the queue before moving to the next
comment|// one.
name|replicasNotPersisted
operator|.
name|remove
argument_list|(
name|replicaState
argument_list|)
expr_stmt|;
block|}
name|replicasPersisted
operator|.
name|add
argument_list|(
name|replicaState
argument_list|)
expr_stmt|;
block|}
DECL|method|dequeueNextReplicaToPersist ()
specifier|synchronized
name|ReplicaState
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
name|ReplicaState
name|replicaState
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
name|ReplicaState
argument_list|>
name|replicaMap
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|replicaState
operator|.
name|bpid
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
name|replicaState
operator|.
name|blockId
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|replicaState
return|;
block|}
comment|// The replica no longer exists, look for the next one.
block|}
return|return
literal|null
return|;
block|}
DECL|method|reenqueueReplicaNotPersisted (final ReplicaState replicaState)
specifier|synchronized
name|void
name|reenqueueReplicaNotPersisted
parameter_list|(
specifier|final
name|ReplicaState
name|replicaState
parameter_list|)
block|{
name|replicasNotPersisted
operator|.
name|add
argument_list|(
name|replicaState
argument_list|)
expr_stmt|;
block|}
DECL|method|reenqueueReplicaPersisted (final ReplicaState replicaState)
specifier|synchronized
name|void
name|reenqueueReplicaPersisted
parameter_list|(
specifier|final
name|ReplicaState
name|replicaState
parameter_list|)
block|{
name|replicasPersisted
operator|.
name|add
argument_list|(
name|replicaState
argument_list|)
expr_stmt|;
block|}
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
DECL|method|getNextCandidateForEviction ()
specifier|synchronized
name|ReplicaState
name|getNextCandidateForEviction
parameter_list|()
block|{
while|while
condition|(
name|replicasPersisted
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|ReplicaState
name|replicaState
init|=
name|replicasPersisted
operator|.
name|remove
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Long
argument_list|,
name|ReplicaState
argument_list|>
name|replicaMap
init|=
name|replicaMaps
operator|.
name|get
argument_list|(
name|replicaState
operator|.
name|bpid
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
name|replicaState
operator|.
name|blockId
argument_list|)
operator|!=
literal|null
condition|)
block|{
return|return
name|replicaState
return|;
block|}
comment|// The replica no longer exists, look for the next one.
block|}
return|return
literal|null
return|;
block|}
DECL|method|discardReplica (ReplicaState replicaState, boolean deleteSavedCopies)
name|void
name|discardReplica
parameter_list|(
name|ReplicaState
name|replicaState
parameter_list|,
name|boolean
name|deleteSavedCopies
parameter_list|)
block|{
name|discardReplica
argument_list|(
name|replicaState
operator|.
name|bpid
argument_list|,
name|replicaState
operator|.
name|blockId
argument_list|,
name|deleteSavedCopies
argument_list|)
expr_stmt|;
block|}
comment|/**    * Discard any state we are tracking for the given replica. This could mean    * the block is either deleted from the block space or the replica is no longer    * on transient storage.    *    * @param deleteSavedCopies true if we should delete the saved copies on    *                          persistent storage. This should be set by the    *                          caller when the block is no longer needed.    */
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
name|ReplicaState
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
name|ReplicaState
name|replicaState
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
name|replicaState
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
name|replicaState
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
block|}
block|}
end_class

end_unit

