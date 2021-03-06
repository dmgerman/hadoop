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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|HadoopIllegalArgumentException
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
name|protocol
operator|.
name|Block
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
name|ReplicaInfo
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
name|util
operator|.
name|FoldedTreeSet
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
name|AutoCloseableLock
import|;
end_import

begin_comment
comment|/**  * Maintains the replica map.   */
end_comment

begin_class
DECL|class|ReplicaMap
class|class
name|ReplicaMap
block|{
comment|// Lock object to synchronize this instance.
DECL|field|lock
specifier|private
specifier|final
name|AutoCloseableLock
name|lock
decl_stmt|;
comment|// Map of block pool Id to a set of ReplicaInfo.
DECL|field|map
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// Special comparator used to compare Long to Block ID in the TreeSet.
DECL|field|LONG_AND_BLOCK_COMPARATOR
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Object
argument_list|>
name|LONG_AND_BLOCK_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Object
name|o1
parameter_list|,
name|Object
name|o2
parameter_list|)
block|{
name|long
name|lookup
init|=
operator|(
name|long
operator|)
name|o1
decl_stmt|;
name|long
name|stored
init|=
operator|(
operator|(
name|Block
operator|)
name|o2
operator|)
operator|.
name|getBlockId
argument_list|()
decl_stmt|;
return|return
name|lookup
operator|>
name|stored
condition|?
literal|1
else|:
name|lookup
operator|<
name|stored
condition|?
operator|-
literal|1
else|:
literal|0
return|;
block|}
block|}
decl_stmt|;
DECL|method|ReplicaMap (AutoCloseableLock lock)
name|ReplicaMap
parameter_list|(
name|AutoCloseableLock
name|lock
parameter_list|)
block|{
if|if
condition|(
name|lock
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Lock to synchronize on cannot be null"
argument_list|)
throw|;
block|}
name|this
operator|.
name|lock
operator|=
name|lock
expr_stmt|;
block|}
DECL|method|getBlockPoolList ()
name|String
index|[]
name|getBlockPoolList
parameter_list|()
block|{
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|map
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
block|}
DECL|method|checkBlockPool (String bpid)
specifier|private
name|void
name|checkBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
if|if
condition|(
name|bpid
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Block Pool Id is null"
argument_list|)
throw|;
block|}
block|}
DECL|method|checkBlock (Block b)
specifier|private
name|void
name|checkBlock
parameter_list|(
name|Block
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Block is null"
argument_list|)
throw|;
block|}
block|}
comment|/**    * Get the meta information of the replica that matches both block id     * and generation stamp    * @param bpid block pool id    * @param block block with its id as the key    * @return the replica's meta information    * @throws IllegalArgumentException if the input block or block pool is null    */
DECL|method|get (String bpid, Block block)
name|ReplicaInfo
name|get
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|block
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|checkBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
name|ReplicaInfo
name|replicaInfo
init|=
name|get
argument_list|(
name|bpid
argument_list|,
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicaInfo
operator|!=
literal|null
operator|&&
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|==
name|replicaInfo
operator|.
name|getGenerationStamp
argument_list|()
condition|)
block|{
return|return
name|replicaInfo
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get the meta information of the replica that matches the block id    * @param bpid block pool id    * @param blockId a block's id    * @return the replica's meta information    */
DECL|method|get (String bpid, long blockId)
name|ReplicaInfo
name|get
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|set
operator|.
name|get
argument_list|(
name|blockId
argument_list|,
name|LONG_AND_BLOCK_COMPARATOR
argument_list|)
return|;
block|}
block|}
comment|/**    * Add a replica's meta information into the map     *     * @param bpid block pool id    * @param replicaInfo a replica's meta information    * @return previous meta information of the replica    * @throws IllegalArgumentException if the input parameter is null    */
DECL|method|add (String bpid, ReplicaInfo replicaInfo)
name|ReplicaInfo
name|add
parameter_list|(
name|String
name|bpid
parameter_list|,
name|ReplicaInfo
name|replicaInfo
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|checkBlock
argument_list|(
name|replicaInfo
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// Add an entry for block pool if it does not exist already
name|set
operator|=
operator|new
name|FoldedTreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
return|return
name|set
operator|.
name|addOrReplace
argument_list|(
name|replicaInfo
argument_list|)
return|;
block|}
block|}
comment|/**    * Add a replica's meta information into the map, if already exist    * return the old replicaInfo.    */
DECL|method|addAndGet (String bpid, ReplicaInfo replicaInfo)
name|ReplicaInfo
name|addAndGet
parameter_list|(
name|String
name|bpid
parameter_list|,
name|ReplicaInfo
name|replicaInfo
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|checkBlock
argument_list|(
name|replicaInfo
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// Add an entry for block pool if it does not exist already
name|set
operator|=
operator|new
name|FoldedTreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
name|ReplicaInfo
name|oldReplicaInfo
init|=
name|set
operator|.
name|get
argument_list|(
name|replicaInfo
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|LONG_AND_BLOCK_COMPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldReplicaInfo
operator|!=
literal|null
condition|)
block|{
return|return
name|oldReplicaInfo
return|;
block|}
else|else
block|{
name|set
operator|.
name|addOrReplace
argument_list|(
name|replicaInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|replicaInfo
return|;
block|}
block|}
comment|/**    * Add all entries from the given replica map into the local replica map.    */
DECL|method|addAll (ReplicaMap other)
name|void
name|addAll
parameter_list|(
name|ReplicaMap
name|other
parameter_list|)
block|{
name|map
operator|.
name|putAll
argument_list|(
name|other
operator|.
name|map
argument_list|)
expr_stmt|;
block|}
comment|/**    * Merge all entries from the given replica map into the local replica map.    */
DECL|method|mergeAll (ReplicaMap other)
name|void
name|mergeAll
parameter_list|(
name|ReplicaMap
name|other
parameter_list|)
block|{
name|other
operator|.
name|map
operator|.
name|forEach
argument_list|(
parameter_list|(
name|bp
parameter_list|,
name|replicaInfos
parameter_list|)
lambda|->
block|{
name|replicaInfos
operator|.
name|forEach
argument_list|(
name|replicaInfo
lambda|->
name|add
argument_list|(
name|bp
argument_list|,
name|replicaInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
block|}
comment|/**    * Remove the replica's meta information from the map that matches    * the input block's id and generation stamp    * @param bpid block pool id    * @param block block with its id as the key    * @return the removed replica's meta information    * @throws IllegalArgumentException if the input block is null    */
DECL|method|remove (String bpid, Block block)
name|ReplicaInfo
name|remove
parameter_list|(
name|String
name|bpid
parameter_list|,
name|Block
name|block
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
name|checkBlock
argument_list|(
name|block
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
name|ReplicaInfo
name|replicaInfo
init|=
name|set
operator|.
name|get
argument_list|(
name|block
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|LONG_AND_BLOCK_COMPARATOR
argument_list|)
decl_stmt|;
if|if
condition|(
name|replicaInfo
operator|!=
literal|null
operator|&&
name|block
operator|.
name|getGenerationStamp
argument_list|()
operator|==
name|replicaInfo
operator|.
name|getGenerationStamp
argument_list|()
condition|)
block|{
return|return
name|set
operator|.
name|removeAndGet
argument_list|(
name|replicaInfo
argument_list|)
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Remove the replica's meta information from the map if present    * @param bpid block pool id    * @param blockId block id of the replica to be removed    * @return the removed replica's meta information    */
DECL|method|remove (String bpid, long blockId)
name|ReplicaInfo
name|remove
parameter_list|(
name|String
name|bpid
parameter_list|,
name|long
name|blockId
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|!=
literal|null
condition|)
block|{
return|return
name|set
operator|.
name|removeAndGet
argument_list|(
name|blockId
argument_list|,
name|LONG_AND_BLOCK_COMPARATOR
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get the size of the map for given block pool    * @param bpid block pool id    * @return the number of replicas in the map    */
DECL|method|size (String bpid)
name|int
name|size
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
return|return
name|set
operator|!=
literal|null
condition|?
name|set
operator|.
name|size
argument_list|()
else|:
literal|0
return|;
block|}
block|}
comment|/**    * Get a collection of the replicas for given block pool    * This method is<b>not synchronized</b>. It needs to be synchronized    * externally using the lock, both for getting the replicas    * values from the map and iterating over it. Mutex can be accessed using    * {@link #getLock()} method.    *     * @param bpid block pool id    * @return a collection of the replicas belonging to the block pool    */
DECL|method|replicas (String bpid)
name|Collection
argument_list|<
name|ReplicaInfo
argument_list|>
name|replicas
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
return|;
block|}
DECL|method|initBlockPool (String bpid)
name|void
name|initBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|FoldedTreeSet
argument_list|<
name|ReplicaInfo
argument_list|>
name|set
init|=
name|map
operator|.
name|get
argument_list|(
name|bpid
argument_list|)
decl_stmt|;
if|if
condition|(
name|set
operator|==
literal|null
condition|)
block|{
comment|// Add an entry for block pool if it does not exist already
name|set
operator|=
operator|new
name|FoldedTreeSet
argument_list|<>
argument_list|()
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|bpid
argument_list|,
name|set
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|cleanUpBlockPool (String bpid)
name|void
name|cleanUpBlockPool
parameter_list|(
name|String
name|bpid
parameter_list|)
block|{
name|checkBlockPool
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
try|try
init|(
name|AutoCloseableLock
name|l
init|=
name|lock
operator|.
name|acquire
argument_list|()
init|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|bpid
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the lock object used for synchronizing ReplicasMap    * @return lock object    */
DECL|method|getLock ()
name|AutoCloseableLock
name|getLock
parameter_list|()
block|{
return|return
name|lock
return|;
block|}
block|}
end_class

end_unit

