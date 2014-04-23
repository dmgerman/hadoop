begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
name|Callable
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
name|CancellationException
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
name|ExecutionException
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
name|ExecutorService
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
name|Future
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
name|ScheduledThreadPoolExecutor
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
name|TimeUnit
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|conf
operator|.
name|Configuration
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
name|BlockLocation
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
name|BlockStorageLocation
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
name|HdfsVolumeId
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
name|VolumeId
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
name|ClientDatanodeProtocol
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
name|DatanodeInfo
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
name|HdfsBlocksMetadata
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
name|LocatedBlock
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|BlockTokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|block
operator|.
name|InvalidBlockTokenException
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
name|ipc
operator|.
name|RPC
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|collect
operator|.
name|Lists
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
name|collect
operator|.
name|Maps
import|;
end_import

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|BlockStorageLocationUtil
class|class
name|BlockStorageLocationUtil
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|BlockStorageLocationUtil
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * Create a list of {@link VolumeBlockLocationCallable} corresponding to a set    * of datanodes and blocks. The blocks must all correspond to the same    * block pool.    *     * @param datanodeBlocks    *          Map of datanodes to block replicas at each datanode    * @return callables Used to query each datanode for location information on    *         the block replicas at the datanode    */
DECL|method|createVolumeBlockLocationCallables ( Configuration conf, Map<DatanodeInfo, List<LocatedBlock>> datanodeBlocks, int timeout, boolean connectToDnViaHostname)
specifier|private
specifier|static
name|List
argument_list|<
name|VolumeBlockLocationCallable
argument_list|>
name|createVolumeBlockLocationCallables
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|List
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|datanodeBlocks
parameter_list|,
name|int
name|timeout
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|)
block|{
if|if
condition|(
name|datanodeBlocks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|()
return|;
block|}
comment|// Construct the callables, one per datanode
name|List
argument_list|<
name|VolumeBlockLocationCallable
argument_list|>
name|callables
init|=
operator|new
name|ArrayList
argument_list|<
name|VolumeBlockLocationCallable
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DatanodeInfo
argument_list|,
name|List
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|entry
range|:
name|datanodeBlocks
operator|.
name|entrySet
argument_list|()
control|)
block|{
comment|// Construct RPC parameters
name|DatanodeInfo
name|datanode
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|locatedBlocks
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|locatedBlocks
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
continue|continue;
block|}
comment|// Ensure that the blocks all are from the same block pool.
name|String
name|poolId
init|=
name|locatedBlocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|lb
range|:
name|locatedBlocks
control|)
block|{
if|if
condition|(
operator|!
name|poolId
operator|.
name|equals
argument_list|(
name|lb
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"All blocks to be queried must be in the same block pool: "
operator|+
name|locatedBlocks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getBlock
argument_list|()
operator|+
literal|" and "
operator|+
name|lb
operator|+
literal|" are from different pools."
argument_list|)
throw|;
block|}
block|}
name|long
index|[]
name|blockIds
init|=
operator|new
name|long
index|[
name|locatedBlocks
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
name|List
argument_list|<
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|>
name|dnTokens
init|=
operator|new
name|ArrayList
argument_list|<
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|>
argument_list|(
name|locatedBlocks
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|b
range|:
name|locatedBlocks
control|)
block|{
name|blockIds
index|[
name|i
operator|++
index|]
operator|=
name|b
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
expr_stmt|;
name|dnTokens
operator|.
name|add
argument_list|(
name|b
operator|.
name|getBlockToken
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|VolumeBlockLocationCallable
name|callable
init|=
operator|new
name|VolumeBlockLocationCallable
argument_list|(
name|conf
argument_list|,
name|datanode
argument_list|,
name|poolId
argument_list|,
name|blockIds
argument_list|,
name|dnTokens
argument_list|,
name|timeout
argument_list|,
name|connectToDnViaHostname
argument_list|)
decl_stmt|;
name|callables
operator|.
name|add
argument_list|(
name|callable
argument_list|)
expr_stmt|;
block|}
return|return
name|callables
return|;
block|}
comment|/**    * Queries datanodes for the blocks specified in<code>datanodeBlocks</code>,    * making one RPC to each datanode. These RPCs are made in parallel using a    * threadpool.    *     * @param datanodeBlocks    *          Map of datanodes to the blocks present on the DN    * @return metadatas Map of datanodes to block metadata of the DN    * @throws InvalidBlockTokenException    *           if client does not have read access on a requested block    */
DECL|method|queryDatanodesForHdfsBlocksMetadata ( Configuration conf, Map<DatanodeInfo, List<LocatedBlock>> datanodeBlocks, int poolsize, int timeoutMs, boolean connectToDnViaHostname)
specifier|static
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|HdfsBlocksMetadata
argument_list|>
name|queryDatanodesForHdfsBlocksMetadata
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|List
argument_list|<
name|LocatedBlock
argument_list|>
argument_list|>
name|datanodeBlocks
parameter_list|,
name|int
name|poolsize
parameter_list|,
name|int
name|timeoutMs
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|)
throws|throws
name|InvalidBlockTokenException
block|{
name|List
argument_list|<
name|VolumeBlockLocationCallable
argument_list|>
name|callables
init|=
name|createVolumeBlockLocationCallables
argument_list|(
name|conf
argument_list|,
name|datanodeBlocks
argument_list|,
name|timeoutMs
argument_list|,
name|connectToDnViaHostname
argument_list|)
decl_stmt|;
comment|// Use a thread pool to execute the Callables in parallel
name|List
argument_list|<
name|Future
argument_list|<
name|HdfsBlocksMetadata
argument_list|>
argument_list|>
name|futures
init|=
operator|new
name|ArrayList
argument_list|<
name|Future
argument_list|<
name|HdfsBlocksMetadata
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|ExecutorService
name|executor
init|=
operator|new
name|ScheduledThreadPoolExecutor
argument_list|(
name|poolsize
argument_list|)
decl_stmt|;
try|try
block|{
name|futures
operator|=
name|executor
operator|.
name|invokeAll
argument_list|(
name|callables
argument_list|,
name|timeoutMs
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Swallow the exception here, because we can return partial results
block|}
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|HdfsBlocksMetadata
argument_list|>
name|metadatas
init|=
name|Maps
operator|.
name|newHashMapWithExpectedSize
argument_list|(
name|datanodeBlocks
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
comment|// Fill in metadatas with results from DN RPCs, where possible
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|futures
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|VolumeBlockLocationCallable
name|callable
init|=
name|callables
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|DatanodeInfo
name|datanode
init|=
name|callable
operator|.
name|getDatanodeInfo
argument_list|()
decl_stmt|;
name|Future
argument_list|<
name|HdfsBlocksMetadata
argument_list|>
name|future
init|=
name|futures
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
try|try
block|{
name|HdfsBlocksMetadata
name|metadata
init|=
name|future
operator|.
name|get
argument_list|()
decl_stmt|;
name|metadatas
operator|.
name|put
argument_list|(
name|callable
operator|.
name|getDatanodeInfo
argument_list|()
argument_list|,
name|metadata
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|CancellationException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Cancelled while waiting for datanode "
operator|+
name|datanode
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
operator|+
literal|": "
operator|+
name|e
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExecutionException
name|e
parameter_list|)
block|{
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|InvalidBlockTokenException
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Invalid access token when trying to retrieve "
operator|+
literal|"information from datanode "
operator|+
name|datanode
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|InvalidBlockTokenException
operator|)
name|t
throw|;
block|}
elseif|else
if|if
condition|(
name|t
operator|instanceof
name|UnsupportedOperationException
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Datanode "
operator|+
name|datanode
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
operator|+
literal|" does not support"
operator|+
literal|" required #getHdfsBlocksMetadata() API"
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|UnsupportedOperationException
operator|)
name|t
throw|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to query block locations on datanode "
operator|+
name|datanode
operator|.
name|getIpcAddr
argument_list|(
literal|false
argument_list|)
operator|+
literal|": "
operator|+
name|t
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not fetch information from datanode"
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|// Shouldn't happen, because invokeAll waits for all Futures to be ready
name|LOG
operator|.
name|info
argument_list|(
literal|"Interrupted while fetching HdfsBlocksMetadata"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|metadatas
return|;
block|}
comment|/**    * Group the per-replica {@link VolumeId} info returned from    * {@link DFSClient#queryDatanodesForHdfsBlocksMetadata(Map)} to be    * associated    * with the corresponding {@link LocatedBlock}.    *     * @param blocks    *          Original LocatedBlock array    * @param metadatas    *          VolumeId information for the replicas on each datanode    * @return blockVolumeIds per-replica VolumeId information associated with the    *         parent LocatedBlock    */
DECL|method|associateVolumeIdsWithBlocks ( List<LocatedBlock> blocks, Map<DatanodeInfo, HdfsBlocksMetadata> metadatas)
specifier|static
name|Map
argument_list|<
name|LocatedBlock
argument_list|,
name|List
argument_list|<
name|VolumeId
argument_list|>
argument_list|>
name|associateVolumeIdsWithBlocks
parameter_list|(
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
parameter_list|,
name|Map
argument_list|<
name|DatanodeInfo
argument_list|,
name|HdfsBlocksMetadata
argument_list|>
name|metadatas
parameter_list|)
block|{
comment|// Initialize mapping of ExtendedBlock to LocatedBlock.
comment|// Used to associate results from DN RPCs to the parent LocatedBlock
name|Map
argument_list|<
name|Long
argument_list|,
name|LocatedBlock
argument_list|>
name|blockIdToLocBlock
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|LocatedBlock
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|b
range|:
name|blocks
control|)
block|{
name|blockIdToLocBlock
operator|.
name|put
argument_list|(
name|b
operator|.
name|getBlock
argument_list|()
operator|.
name|getBlockId
argument_list|()
argument_list|,
name|b
argument_list|)
expr_stmt|;
block|}
comment|// Initialize the mapping of blocks -> list of VolumeIds, one per replica
comment|// This is filled out with real values from the DN RPCs
name|Map
argument_list|<
name|LocatedBlock
argument_list|,
name|List
argument_list|<
name|VolumeId
argument_list|>
argument_list|>
name|blockVolumeIds
init|=
operator|new
name|HashMap
argument_list|<
name|LocatedBlock
argument_list|,
name|List
argument_list|<
name|VolumeId
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|LocatedBlock
name|b
range|:
name|blocks
control|)
block|{
name|ArrayList
argument_list|<
name|VolumeId
argument_list|>
name|l
init|=
operator|new
name|ArrayList
argument_list|<
name|VolumeId
argument_list|>
argument_list|(
name|b
operator|.
name|getLocations
argument_list|()
operator|.
name|length
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
name|b
operator|.
name|getLocations
argument_list|()
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|l
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|blockVolumeIds
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
comment|// Iterate through the list of metadatas (one per datanode).
comment|// For each metadata, if it's valid, insert its volume location information
comment|// into the Map returned to the caller
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|DatanodeInfo
argument_list|,
name|HdfsBlocksMetadata
argument_list|>
name|entry
range|:
name|metadatas
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|DatanodeInfo
name|datanode
init|=
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|HdfsBlocksMetadata
name|metadata
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Check if metadata is valid
if|if
condition|(
name|metadata
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|long
index|[]
name|metaBlockIds
init|=
name|metadata
operator|.
name|getBlockIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|metaVolumeIds
init|=
name|metadata
operator|.
name|getVolumeIds
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Integer
argument_list|>
name|metaVolumeIndexes
init|=
name|metadata
operator|.
name|getVolumeIndexes
argument_list|()
decl_stmt|;
comment|// Add VolumeId for each replica in the HdfsBlocksMetadata
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|metaBlockIds
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|int
name|volumeIndex
init|=
name|metaVolumeIndexes
operator|.
name|get
argument_list|(
name|j
argument_list|)
decl_stmt|;
name|long
name|blockId
init|=
name|metaBlockIds
index|[
name|j
index|]
decl_stmt|;
comment|// Skip if block wasn't found, or not a valid index into metaVolumeIds
comment|// Also skip if the DN responded with a block we didn't ask for
if|if
condition|(
name|volumeIndex
operator|==
name|Integer
operator|.
name|MAX_VALUE
operator|||
name|volumeIndex
operator|>=
name|metaVolumeIds
operator|.
name|size
argument_list|()
operator|||
operator|!
name|blockIdToLocBlock
operator|.
name|containsKey
argument_list|(
name|blockId
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No data for block "
operator|+
name|blockId
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
comment|// Get the VolumeId by indexing into the list of VolumeIds
comment|// provided by the datanode
name|byte
index|[]
name|volumeId
init|=
name|metaVolumeIds
operator|.
name|get
argument_list|(
name|volumeIndex
argument_list|)
decl_stmt|;
name|HdfsVolumeId
name|id
init|=
operator|new
name|HdfsVolumeId
argument_list|(
name|volumeId
argument_list|)
decl_stmt|;
comment|// Find out which index we are in the LocatedBlock's replicas
name|LocatedBlock
name|locBlock
init|=
name|blockIdToLocBlock
operator|.
name|get
argument_list|(
name|blockId
argument_list|)
decl_stmt|;
name|DatanodeInfo
index|[]
name|dnInfos
init|=
name|locBlock
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|int
name|index
init|=
operator|-
literal|1
decl_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|dnInfos
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|dnInfos
index|[
name|k
index|]
operator|.
name|equals
argument_list|(
name|datanode
argument_list|)
condition|)
block|{
name|index
operator|=
name|k
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Datanode responded with a block volume id we did"
operator|+
literal|" not request, omitting."
argument_list|)
expr_stmt|;
block|}
continue|continue;
block|}
comment|// Place VolumeId at the same index as the DN's index in the list of
comment|// replicas
name|List
argument_list|<
name|VolumeId
argument_list|>
name|volumeIds
init|=
name|blockVolumeIds
operator|.
name|get
argument_list|(
name|locBlock
argument_list|)
decl_stmt|;
name|volumeIds
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|blockVolumeIds
return|;
block|}
comment|/**    * Helper method to combine a list of {@link LocatedBlock} with associated    * {@link VolumeId} information to form a list of {@link BlockStorageLocation}    * .    */
DECL|method|convertToVolumeBlockLocations ( List<LocatedBlock> blocks, Map<LocatedBlock, List<VolumeId>> blockVolumeIds)
specifier|static
name|BlockStorageLocation
index|[]
name|convertToVolumeBlockLocations
parameter_list|(
name|List
argument_list|<
name|LocatedBlock
argument_list|>
name|blocks
parameter_list|,
name|Map
argument_list|<
name|LocatedBlock
argument_list|,
name|List
argument_list|<
name|VolumeId
argument_list|>
argument_list|>
name|blockVolumeIds
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Construct the final return value of VolumeBlockLocation[]
name|BlockLocation
index|[]
name|locations
init|=
name|DFSUtil
operator|.
name|locatedBlocks2Locations
argument_list|(
name|blocks
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|BlockStorageLocation
argument_list|>
name|volumeBlockLocs
init|=
operator|new
name|ArrayList
argument_list|<
name|BlockStorageLocation
argument_list|>
argument_list|(
name|locations
operator|.
name|length
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
name|locations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LocatedBlock
name|locBlock
init|=
name|blocks
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|VolumeId
argument_list|>
name|volumeIds
init|=
name|blockVolumeIds
operator|.
name|get
argument_list|(
name|locBlock
argument_list|)
decl_stmt|;
name|BlockStorageLocation
name|bsLoc
init|=
operator|new
name|BlockStorageLocation
argument_list|(
name|locations
index|[
name|i
index|]
argument_list|,
name|volumeIds
operator|.
name|toArray
argument_list|(
operator|new
name|VolumeId
index|[
literal|0
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|volumeBlockLocs
operator|.
name|add
argument_list|(
name|bsLoc
argument_list|)
expr_stmt|;
block|}
return|return
name|volumeBlockLocs
operator|.
name|toArray
argument_list|(
operator|new
name|BlockStorageLocation
index|[]
block|{}
argument_list|)
return|;
block|}
comment|/**    * Callable that sets up an RPC proxy to a datanode and queries it for    * volume location information for a list of ExtendedBlocks.     */
DECL|class|VolumeBlockLocationCallable
specifier|private
specifier|static
class|class
name|VolumeBlockLocationCallable
implements|implements
name|Callable
argument_list|<
name|HdfsBlocksMetadata
argument_list|>
block|{
DECL|field|configuration
specifier|private
specifier|final
name|Configuration
name|configuration
decl_stmt|;
DECL|field|timeout
specifier|private
specifier|final
name|int
name|timeout
decl_stmt|;
DECL|field|datanode
specifier|private
specifier|final
name|DatanodeInfo
name|datanode
decl_stmt|;
DECL|field|poolId
specifier|private
specifier|final
name|String
name|poolId
decl_stmt|;
DECL|field|blockIds
specifier|private
specifier|final
name|long
index|[]
name|blockIds
decl_stmt|;
DECL|field|dnTokens
specifier|private
specifier|final
name|List
argument_list|<
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|>
name|dnTokens
decl_stmt|;
DECL|field|connectToDnViaHostname
specifier|private
specifier|final
name|boolean
name|connectToDnViaHostname
decl_stmt|;
DECL|method|VolumeBlockLocationCallable (Configuration configuration, DatanodeInfo datanode, String poolId, long []blockIds, List<Token<BlockTokenIdentifier>> dnTokens, int timeout, boolean connectToDnViaHostname)
name|VolumeBlockLocationCallable
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|DatanodeInfo
name|datanode
parameter_list|,
name|String
name|poolId
parameter_list|,
name|long
index|[]
name|blockIds
parameter_list|,
name|List
argument_list|<
name|Token
argument_list|<
name|BlockTokenIdentifier
argument_list|>
argument_list|>
name|dnTokens
parameter_list|,
name|int
name|timeout
parameter_list|,
name|boolean
name|connectToDnViaHostname
parameter_list|)
block|{
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
name|this
operator|.
name|datanode
operator|=
name|datanode
expr_stmt|;
name|this
operator|.
name|poolId
operator|=
name|poolId
expr_stmt|;
name|this
operator|.
name|blockIds
operator|=
name|blockIds
expr_stmt|;
name|this
operator|.
name|dnTokens
operator|=
name|dnTokens
expr_stmt|;
name|this
operator|.
name|connectToDnViaHostname
operator|=
name|connectToDnViaHostname
expr_stmt|;
block|}
DECL|method|getDatanodeInfo ()
specifier|public
name|DatanodeInfo
name|getDatanodeInfo
parameter_list|()
block|{
return|return
name|datanode
return|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|HdfsBlocksMetadata
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|HdfsBlocksMetadata
name|metadata
init|=
literal|null
decl_stmt|;
comment|// Create the RPC proxy and make the RPC
name|ClientDatanodeProtocol
name|cdp
init|=
literal|null
decl_stmt|;
try|try
block|{
name|cdp
operator|=
name|DFSUtil
operator|.
name|createClientDatanodeProtocolProxy
argument_list|(
name|datanode
argument_list|,
name|configuration
argument_list|,
name|timeout
argument_list|,
name|connectToDnViaHostname
argument_list|)
expr_stmt|;
name|metadata
operator|=
name|cdp
operator|.
name|getHdfsBlocksMetadata
argument_list|(
name|poolId
argument_list|,
name|blockIds
argument_list|,
name|dnTokens
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Bubble this up to the caller, handle with the Future
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|cdp
operator|!=
literal|null
condition|)
block|{
name|RPC
operator|.
name|stopProxy
argument_list|(
name|cdp
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|metadata
return|;
block|}
block|}
block|}
end_class

end_unit

