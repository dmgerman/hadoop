begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
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
name|HashSet
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
name|NavigableMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|ConcurrentSkipListMap
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
name|annotations
operator|.
name|VisibleForTesting
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
name|StorageType
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
name|DFSConfigKeys
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
name|protocol
operator|.
name|BlockListAsLongs
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
name|DatanodeID
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
name|DatanodeInfoWithStorage
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
name|ExtendedBlock
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
name|protocol
operator|.
name|LocatedBlocks
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
name|protocol
operator|.
name|BlockReportContext
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
name|protocol
operator|.
name|DatanodeStorage
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
name|protocol
operator|.
name|DatanodeStorage
operator|.
name|State
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
name|RwLock
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
name|ReflectionUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ByteString
import|;
end_import

begin_comment
comment|/**  * This class allows us to manage and multiplex between storages local to  * datanodes, and provided storage.  */
end_comment

begin_class
DECL|class|ProvidedStorageMap
specifier|public
class|class
name|ProvidedStorageMap
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ProvidedStorageMap
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// limit to a single provider for now
DECL|field|blockProvider
specifier|private
specifier|final
name|BlockProvider
name|blockProvider
decl_stmt|;
DECL|field|storageId
specifier|private
specifier|final
name|String
name|storageId
decl_stmt|;
DECL|field|providedDescriptor
specifier|private
specifier|final
name|ProvidedDescriptor
name|providedDescriptor
decl_stmt|;
DECL|field|providedStorageInfo
specifier|private
specifier|final
name|DatanodeStorageInfo
name|providedStorageInfo
decl_stmt|;
DECL|field|providedEnabled
specifier|private
name|boolean
name|providedEnabled
decl_stmt|;
DECL|method|ProvidedStorageMap (RwLock lock, BlockManager bm, Configuration conf)
name|ProvidedStorageMap
parameter_list|(
name|RwLock
name|lock
parameter_list|,
name|BlockManager
name|bm
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|storageId
operator|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_PROVIDER_STORAGEUUID
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_PROVIDER_STORAGEUUID_DEFAULT
argument_list|)
expr_stmt|;
name|providedEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_PROVIDED_ENABLED
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_PROVIDED_ENABLED_DEFAULT
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|providedEnabled
condition|)
block|{
comment|// disable mapping
name|blockProvider
operator|=
literal|null
expr_stmt|;
name|providedDescriptor
operator|=
literal|null
expr_stmt|;
name|providedStorageInfo
operator|=
literal|null
expr_stmt|;
return|return;
block|}
name|DatanodeStorage
name|ds
init|=
operator|new
name|DatanodeStorage
argument_list|(
name|storageId
argument_list|,
name|State
operator|.
name|NORMAL
argument_list|,
name|StorageType
operator|.
name|PROVIDED
argument_list|)
decl_stmt|;
name|providedDescriptor
operator|=
operator|new
name|ProvidedDescriptor
argument_list|()
expr_stmt|;
name|providedStorageInfo
operator|=
name|providedDescriptor
operator|.
name|createProvidedStorage
argument_list|(
name|ds
argument_list|)
expr_stmt|;
comment|// load block reader into storage
name|Class
argument_list|<
name|?
extends|extends
name|BlockProvider
argument_list|>
name|fmt
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_BLOCK_PROVIDER_CLASS
argument_list|,
name|BlockFormatProvider
operator|.
name|class
argument_list|,
name|BlockProvider
operator|.
name|class
argument_list|)
decl_stmt|;
name|blockProvider
operator|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|fmt
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|blockProvider
operator|.
name|init
argument_list|(
name|lock
argument_list|,
name|bm
argument_list|,
name|providedStorageInfo
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Loaded block provider class: "
operator|+
name|blockProvider
operator|.
name|getClass
argument_list|()
operator|+
literal|" storage: "
operator|+
name|providedStorageInfo
argument_list|)
expr_stmt|;
block|}
comment|/**    * @param dn datanode descriptor    * @param s data node storage    * @param context the block report context    * @return the {@link DatanodeStorageInfo} for the specified datanode.    * If {@code s} corresponds to a provided storage, the storage info    * representing provided storage is returned.    * @throws IOException    */
DECL|method|getStorage (DatanodeDescriptor dn, DatanodeStorage s, BlockReportContext context)
name|DatanodeStorageInfo
name|getStorage
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|,
name|DatanodeStorage
name|s
parameter_list|,
name|BlockReportContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|providedEnabled
operator|&&
name|storageId
operator|.
name|equals
argument_list|(
name|s
operator|.
name|getStorageID
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|StorageType
operator|.
name|PROVIDED
operator|.
name|equals
argument_list|(
name|s
operator|.
name|getStorageType
argument_list|()
argument_list|)
condition|)
block|{
comment|// poll service, initiate
name|blockProvider
operator|.
name|start
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|dn
operator|.
name|injectStorage
argument_list|(
name|providedStorageInfo
argument_list|)
expr_stmt|;
return|return
name|providedDescriptor
operator|.
name|getProvidedStorage
argument_list|(
name|dn
argument_list|,
name|s
argument_list|)
return|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Reserved storage {} reported as non-provided from {}"
argument_list|,
name|s
argument_list|,
name|dn
argument_list|)
expr_stmt|;
block|}
return|return
name|dn
operator|.
name|getStorageInfo
argument_list|(
name|s
operator|.
name|getStorageID
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getProvidedStorageInfo ()
specifier|public
name|DatanodeStorageInfo
name|getProvidedStorageInfo
parameter_list|()
block|{
return|return
name|providedStorageInfo
return|;
block|}
DECL|method|newLocatedBlocks (int maxValue)
specifier|public
name|LocatedBlockBuilder
name|newLocatedBlocks
parameter_list|(
name|int
name|maxValue
parameter_list|)
block|{
if|if
condition|(
operator|!
name|providedEnabled
condition|)
block|{
return|return
operator|new
name|LocatedBlockBuilder
argument_list|(
name|maxValue
argument_list|)
return|;
block|}
return|return
operator|new
name|ProvidedBlocksBuilder
argument_list|(
name|maxValue
argument_list|)
return|;
block|}
DECL|method|removeDatanode (DatanodeDescriptor dnToRemove)
specifier|public
name|void
name|removeDatanode
parameter_list|(
name|DatanodeDescriptor
name|dnToRemove
parameter_list|)
block|{
if|if
condition|(
name|providedDescriptor
operator|!=
literal|null
condition|)
block|{
name|int
name|remainingDatanodes
init|=
name|providedDescriptor
operator|.
name|remove
argument_list|(
name|dnToRemove
argument_list|)
decl_stmt|;
if|if
condition|(
name|remainingDatanodes
operator|==
literal|0
condition|)
block|{
name|blockProvider
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Builder used for creating {@link LocatedBlocks} when a block is provided.    */
DECL|class|ProvidedBlocksBuilder
class|class
name|ProvidedBlocksBuilder
extends|extends
name|LocatedBlockBuilder
block|{
DECL|field|pending
specifier|private
name|ShadowDatanodeInfoWithStorage
name|pending
decl_stmt|;
DECL|field|hasProvidedLocations
specifier|private
name|boolean
name|hasProvidedLocations
decl_stmt|;
DECL|method|ProvidedBlocksBuilder (int maxBlocks)
name|ProvidedBlocksBuilder
parameter_list|(
name|int
name|maxBlocks
parameter_list|)
block|{
name|super
argument_list|(
name|maxBlocks
argument_list|)
expr_stmt|;
name|pending
operator|=
operator|new
name|ShadowDatanodeInfoWithStorage
argument_list|(
name|providedDescriptor
argument_list|,
name|storageId
argument_list|)
expr_stmt|;
name|hasProvidedLocations
operator|=
literal|false
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|newLocatedBlock (ExtendedBlock eb, DatanodeStorageInfo[] storages, long pos, boolean isCorrupt)
name|LocatedBlock
name|newLocatedBlock
parameter_list|(
name|ExtendedBlock
name|eb
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|storages
parameter_list|,
name|long
name|pos
parameter_list|,
name|boolean
name|isCorrupt
parameter_list|)
block|{
name|DatanodeInfoWithStorage
index|[]
name|locs
init|=
operator|new
name|DatanodeInfoWithStorage
index|[
name|storages
operator|.
name|length
index|]
decl_stmt|;
name|String
index|[]
name|sids
init|=
operator|new
name|String
index|[
name|storages
operator|.
name|length
index|]
decl_stmt|;
name|StorageType
index|[]
name|types
init|=
operator|new
name|StorageType
index|[
name|storages
operator|.
name|length
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
name|storages
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|sids
index|[
name|i
index|]
operator|=
name|storages
index|[
name|i
index|]
operator|.
name|getStorageID
argument_list|()
expr_stmt|;
name|types
index|[
name|i
index|]
operator|=
name|storages
index|[
name|i
index|]
operator|.
name|getStorageType
argument_list|()
expr_stmt|;
if|if
condition|(
name|StorageType
operator|.
name|PROVIDED
operator|.
name|equals
argument_list|(
name|storages
index|[
name|i
index|]
operator|.
name|getStorageType
argument_list|()
argument_list|)
condition|)
block|{
name|locs
index|[
name|i
index|]
operator|=
name|pending
expr_stmt|;
name|hasProvidedLocations
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|locs
index|[
name|i
index|]
operator|=
operator|new
name|DatanodeInfoWithStorage
argument_list|(
name|storages
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|,
name|sids
index|[
name|i
index|]
argument_list|,
name|types
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|LocatedBlock
argument_list|(
name|eb
argument_list|,
name|locs
argument_list|,
name|sids
argument_list|,
name|types
argument_list|,
name|pos
argument_list|,
name|isCorrupt
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build (DatanodeDescriptor client)
name|LocatedBlocks
name|build
parameter_list|(
name|DatanodeDescriptor
name|client
parameter_list|)
block|{
comment|// TODO: to support multiple provided storages, need to pass/maintain map
if|if
condition|(
name|hasProvidedLocations
condition|)
block|{
comment|// set all fields of pending DatanodeInfo
name|List
argument_list|<
name|String
argument_list|>
name|excludedUUids
init|=
operator|new
name|ArrayList
argument_list|<
name|String
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
name|DatanodeInfo
index|[]
name|infos
init|=
name|b
operator|.
name|getLocations
argument_list|()
decl_stmt|;
name|StorageType
index|[]
name|types
init|=
name|b
operator|.
name|getStorageTypes
argument_list|()
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
name|types
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|StorageType
operator|.
name|PROVIDED
operator|.
name|equals
argument_list|(
name|types
index|[
name|i
index|]
argument_list|)
condition|)
block|{
name|excludedUUids
operator|.
name|add
argument_list|(
name|infos
index|[
name|i
index|]
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|DatanodeDescriptor
name|dn
init|=
name|providedDescriptor
operator|.
name|choose
argument_list|(
name|client
argument_list|,
name|excludedUUids
argument_list|)
decl_stmt|;
if|if
condition|(
name|dn
operator|==
literal|null
condition|)
block|{
name|dn
operator|=
name|providedDescriptor
operator|.
name|choose
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
name|pending
operator|.
name|replaceInternal
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|LocatedBlocks
argument_list|(
name|flen
argument_list|,
name|isUC
argument_list|,
name|blocks
argument_list|,
name|last
argument_list|,
name|lastComplete
argument_list|,
name|feInfo
argument_list|,
name|ecPolicy
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|build ()
name|LocatedBlocks
name|build
parameter_list|()
block|{
return|return
name|build
argument_list|(
name|providedDescriptor
operator|.
name|chooseRandom
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|/**    * An abstract {@link DatanodeInfoWithStorage} to represent provided storage.    */
DECL|class|ShadowDatanodeInfoWithStorage
specifier|static
class|class
name|ShadowDatanodeInfoWithStorage
extends|extends
name|DatanodeInfoWithStorage
block|{
DECL|field|shadowUuid
specifier|private
name|String
name|shadowUuid
decl_stmt|;
DECL|method|ShadowDatanodeInfoWithStorage (DatanodeDescriptor d, String storageId)
name|ShadowDatanodeInfoWithStorage
parameter_list|(
name|DatanodeDescriptor
name|d
parameter_list|,
name|String
name|storageId
parameter_list|)
block|{
name|super
argument_list|(
name|d
argument_list|,
name|storageId
argument_list|,
name|StorageType
operator|.
name|PROVIDED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getDatanodeUuid ()
specifier|public
name|String
name|getDatanodeUuid
parameter_list|()
block|{
return|return
name|shadowUuid
return|;
block|}
DECL|method|setDatanodeUuid (String uuid)
specifier|public
name|void
name|setDatanodeUuid
parameter_list|(
name|String
name|uuid
parameter_list|)
block|{
name|shadowUuid
operator|=
name|uuid
expr_stmt|;
block|}
DECL|method|replaceInternal (DatanodeDescriptor dn)
name|void
name|replaceInternal
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|)
block|{
name|updateRegInfo
argument_list|(
name|dn
argument_list|)
expr_stmt|;
comment|// overwrite DatanodeID (except UUID)
name|setDatanodeUuid
argument_list|(
name|dn
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
name|setCapacity
argument_list|(
name|dn
operator|.
name|getCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|setDfsUsed
argument_list|(
name|dn
operator|.
name|getDfsUsed
argument_list|()
argument_list|)
expr_stmt|;
name|setRemaining
argument_list|(
name|dn
operator|.
name|getRemaining
argument_list|()
argument_list|)
expr_stmt|;
name|setBlockPoolUsed
argument_list|(
name|dn
operator|.
name|getBlockPoolUsed
argument_list|()
argument_list|)
expr_stmt|;
name|setCacheCapacity
argument_list|(
name|dn
operator|.
name|getCacheCapacity
argument_list|()
argument_list|)
expr_stmt|;
name|setCacheUsed
argument_list|(
name|dn
operator|.
name|getCacheUsed
argument_list|()
argument_list|)
expr_stmt|;
name|setLastUpdate
argument_list|(
name|dn
operator|.
name|getLastUpdate
argument_list|()
argument_list|)
expr_stmt|;
name|setLastUpdateMonotonic
argument_list|(
name|dn
operator|.
name|getLastUpdateMonotonic
argument_list|()
argument_list|)
expr_stmt|;
name|setXceiverCount
argument_list|(
name|dn
operator|.
name|getXceiverCount
argument_list|()
argument_list|)
expr_stmt|;
name|setNetworkLocation
argument_list|(
name|dn
operator|.
name|getNetworkLocation
argument_list|()
argument_list|)
expr_stmt|;
name|adminState
operator|=
name|dn
operator|.
name|getAdminState
argument_list|()
expr_stmt|;
name|setUpgradeDomain
argument_list|(
name|dn
operator|.
name|getUpgradeDomain
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/**    * An abstract DatanodeDescriptor to track datanodes with provided storages.    * NOTE: never resolved through registerDatanode, so not in the topology.    */
DECL|class|ProvidedDescriptor
specifier|static
class|class
name|ProvidedDescriptor
extends|extends
name|DatanodeDescriptor
block|{
DECL|field|dns
specifier|private
specifier|final
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
argument_list|>
name|dns
init|=
operator|new
name|ConcurrentSkipListMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|method|ProvidedDescriptor ()
name|ProvidedDescriptor
parameter_list|()
block|{
name|super
argument_list|(
operator|new
name|DatanodeID
argument_list|(
literal|null
argument_list|,
comment|// String ipAddr,
literal|null
argument_list|,
comment|// String hostName,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
comment|// String datanodeUuid,
literal|0
argument_list|,
comment|// int xferPort,
literal|0
argument_list|,
comment|// int infoPort,
literal|0
argument_list|,
comment|// int infoSecurePort,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|// int ipcPort
block|}
DECL|method|getProvidedStorage ( DatanodeDescriptor dn, DatanodeStorage s)
name|DatanodeStorageInfo
name|getProvidedStorage
parameter_list|(
name|DatanodeDescriptor
name|dn
parameter_list|,
name|DatanodeStorage
name|s
parameter_list|)
block|{
name|dns
operator|.
name|put
argument_list|(
name|dn
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|,
name|dn
argument_list|)
expr_stmt|;
comment|// TODO: maintain separate RPC ident per dn
return|return
name|storageMap
operator|.
name|get
argument_list|(
name|s
operator|.
name|getStorageID
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createProvidedStorage (DatanodeStorage ds)
name|DatanodeStorageInfo
name|createProvidedStorage
parameter_list|(
name|DatanodeStorage
name|ds
parameter_list|)
block|{
assert|assert
literal|null
operator|==
name|storageMap
operator|.
name|get
argument_list|(
name|ds
operator|.
name|getStorageID
argument_list|()
argument_list|)
assert|;
name|DatanodeStorageInfo
name|storage
init|=
operator|new
name|ProvidedDatanodeStorageInfo
argument_list|(
name|this
argument_list|,
name|ds
argument_list|)
decl_stmt|;
name|storage
operator|.
name|setHeartbeatedSinceFailover
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|storageMap
operator|.
name|put
argument_list|(
name|storage
operator|.
name|getStorageID
argument_list|()
argument_list|,
name|storage
argument_list|)
expr_stmt|;
return|return
name|storage
return|;
block|}
DECL|method|choose (DatanodeDescriptor client)
name|DatanodeDescriptor
name|choose
parameter_list|(
name|DatanodeDescriptor
name|client
parameter_list|)
block|{
comment|// exact match for now
name|DatanodeDescriptor
name|dn
init|=
name|client
operator|!=
literal|null
condition|?
name|dns
operator|.
name|get
argument_list|(
name|client
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|dn
condition|)
block|{
name|dn
operator|=
name|chooseRandom
argument_list|()
expr_stmt|;
block|}
return|return
name|dn
return|;
block|}
DECL|method|choose (DatanodeDescriptor client, List<String> excludedUUids)
name|DatanodeDescriptor
name|choose
parameter_list|(
name|DatanodeDescriptor
name|client
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|excludedUUids
parameter_list|)
block|{
comment|// exact match for now
name|DatanodeDescriptor
name|dn
init|=
name|client
operator|!=
literal|null
condition|?
name|dns
operator|.
name|get
argument_list|(
name|client
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
else|:
literal|null
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|dn
operator|||
name|excludedUUids
operator|.
name|contains
argument_list|(
name|client
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
condition|)
block|{
name|dn
operator|=
literal|null
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|exploredUUids
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|exploredUUids
operator|.
name|size
argument_list|()
operator|<
name|dns
operator|.
name|size
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
argument_list|>
name|d
init|=
name|dns
operator|.
name|ceilingEntry
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|d
condition|)
block|{
name|d
operator|=
name|dns
operator|.
name|firstEntry
argument_list|()
expr_stmt|;
block|}
name|String
name|uuid
init|=
name|d
operator|.
name|getValue
argument_list|()
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
comment|//this node has already been explored, and was not selected earlier
if|if
condition|(
name|exploredUUids
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|exploredUUids
operator|.
name|add
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
comment|//this node has been excluded
if|if
condition|(
name|excludedUUids
operator|.
name|contains
argument_list|(
name|uuid
argument_list|)
condition|)
block|{
continue|continue;
block|}
return|return
name|dns
operator|.
name|get
argument_list|(
name|uuid
argument_list|)
return|;
block|}
block|}
return|return
name|dn
return|;
block|}
DECL|method|chooseRandom (DatanodeStorageInfo[] excludedStorages)
name|DatanodeDescriptor
name|chooseRandom
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|excludedStorages
parameter_list|)
block|{
comment|// TODO: Currently this is not uniformly random;
comment|// skewed toward sparse sections of the ids
name|Set
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|excludedNodes
init|=
operator|new
name|HashSet
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|excludedStorages
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|excludedStorages
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Excluded: "
operator|+
name|excludedStorages
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
expr_stmt|;
name|excludedNodes
operator|.
name|add
argument_list|(
name|excludedStorages
index|[
name|i
index|]
operator|.
name|getDatanodeDescriptor
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|Set
argument_list|<
name|DatanodeDescriptor
argument_list|>
name|exploredNodes
init|=
operator|new
name|HashSet
argument_list|<
name|DatanodeDescriptor
argument_list|>
argument_list|()
decl_stmt|;
while|while
condition|(
name|exploredNodes
operator|.
name|size
argument_list|()
operator|<
name|dns
operator|.
name|size
argument_list|()
condition|)
block|{
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|DatanodeDescriptor
argument_list|>
name|d
init|=
name|dns
operator|.
name|ceilingEntry
argument_list|(
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|d
condition|)
block|{
name|d
operator|=
name|dns
operator|.
name|firstEntry
argument_list|()
expr_stmt|;
block|}
name|DatanodeDescriptor
name|node
init|=
name|d
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|//this node has already been explored, and was not selected earlier
if|if
condition|(
name|exploredNodes
operator|.
name|contains
argument_list|(
name|node
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|exploredNodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
comment|//this node has been excluded
if|if
condition|(
name|excludedNodes
operator|.
name|contains
argument_list|(
name|node
argument_list|)
condition|)
block|{
continue|continue;
block|}
return|return
name|node
return|;
block|}
return|return
literal|null
return|;
block|}
DECL|method|chooseRandom ()
name|DatanodeDescriptor
name|chooseRandom
parameter_list|()
block|{
return|return
name|chooseRandom
argument_list|(
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|addBlockToBeReplicated (Block block, DatanodeStorageInfo[] targets)
name|void
name|addBlockToBeReplicated
parameter_list|(
name|Block
name|block
parameter_list|,
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
block|{
comment|// pick a random datanode, delegate to it
name|DatanodeDescriptor
name|node
init|=
name|chooseRandom
argument_list|(
name|targets
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|addBlockToBeReplicated
argument_list|(
name|block
argument_list|,
name|targets
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Cannot find a source node to replicate block: "
operator|+
name|block
operator|+
literal|" from"
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|remove (DatanodeDescriptor dnToRemove)
name|int
name|remove
parameter_list|(
name|DatanodeDescriptor
name|dnToRemove
parameter_list|)
block|{
comment|// this operation happens under the FSNamesystem lock;
comment|// no additional synchronization required.
if|if
condition|(
name|dnToRemove
operator|!=
literal|null
condition|)
block|{
name|DatanodeDescriptor
name|storedDN
init|=
name|dns
operator|.
name|get
argument_list|(
name|dnToRemove
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedDN
operator|!=
literal|null
condition|)
block|{
name|dns
operator|.
name|remove
argument_list|(
name|dnToRemove
operator|.
name|getDatanodeUuid
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|dns
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|activeProvidedDatanodes ()
name|int
name|activeProvidedDatanodes
parameter_list|()
block|{
return|return
name|dns
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
return|return
operator|(
name|this
operator|==
name|obj
operator|)
operator|||
name|super
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
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
name|super
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
comment|/**    * The DatanodeStorageInfo used for the provided storage.    */
DECL|class|ProvidedDatanodeStorageInfo
specifier|static
class|class
name|ProvidedDatanodeStorageInfo
extends|extends
name|DatanodeStorageInfo
block|{
DECL|method|ProvidedDatanodeStorageInfo (ProvidedDescriptor dn, DatanodeStorage ds)
name|ProvidedDatanodeStorageInfo
parameter_list|(
name|ProvidedDescriptor
name|dn
parameter_list|,
name|DatanodeStorage
name|ds
parameter_list|)
block|{
name|super
argument_list|(
name|dn
argument_list|,
name|ds
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeBlock (BlockInfo b)
name|boolean
name|removeBlock
parameter_list|(
name|BlockInfo
name|b
parameter_list|)
block|{
name|ProvidedDescriptor
name|dn
init|=
operator|(
name|ProvidedDescriptor
operator|)
name|getDatanodeDescriptor
argument_list|()
decl_stmt|;
if|if
condition|(
name|dn
operator|.
name|activeProvidedDatanodes
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|super
operator|.
name|removeBlock
argument_list|(
name|b
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
block|}
comment|/**    * Used to emulate block reports for provided blocks.    */
DECL|class|ProvidedBlockList
specifier|static
class|class
name|ProvidedBlockList
extends|extends
name|BlockListAsLongs
block|{
DECL|field|inner
specifier|private
specifier|final
name|Iterator
argument_list|<
name|Block
argument_list|>
name|inner
decl_stmt|;
DECL|method|ProvidedBlockList (Iterator<Block> inner)
name|ProvidedBlockList
parameter_list|(
name|Iterator
argument_list|<
name|Block
argument_list|>
name|inner
parameter_list|)
block|{
name|this
operator|.
name|inner
operator|=
name|inner
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|BlockReportReplica
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|BlockReportReplica
name|next
parameter_list|()
block|{
return|return
operator|new
name|BlockReportReplica
argument_list|(
name|inner
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|inner
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getNumberOfBlocks ()
specifier|public
name|int
name|getNumberOfBlocks
parameter_list|()
block|{
comment|// VERIFY: only printed for debugging
return|return
operator|-
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|getBlocksBuffer ()
specifier|public
name|ByteString
name|getBlocksBuffer
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getBlockListAsLongs ()
specifier|public
name|long
index|[]
name|getBlockListAsLongs
parameter_list|()
block|{
comment|// should only be used for backwards compat, DN.ver> NN.ver
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
block|}
end_class

end_unit

