begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|block
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
name|hdfs
operator|.
name|DFSUtil
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
name|ozone
operator|.
name|OzoneConsts
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
name|ozone
operator|.
name|scm
operator|.
name|container
operator|.
name|Mapping
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
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
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
name|ozone
operator|.
name|scm
operator|.
name|node
operator|.
name|NodeManager
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
name|ozone
operator|.
name|web
operator|.
name|utils
operator|.
name|OzoneUtils
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
name|scm
operator|.
name|ScmConfigKeys
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|Pipeline
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
name|StringUtils
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
name|utils
operator|.
name|BatchOperation
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
name|utils
operator|.
name|MetadataStore
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
name|utils
operator|.
name|MetadataStoreBuilder
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
name|concurrent
operator|.
name|locks
operator|.
name|Lock
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
name|locks
operator|.
name|ReentrantLock
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|BLOCK_DB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|OzoneConsts
operator|.
name|OPEN_CONTAINERS_DB
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|CHILL_MODE_EXCEPTION
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_ALLOCATE_CONTAINER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER_WITH_SAPCE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_BLOCK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_LOAD_OPEN_CONTAINER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|scm
operator|.
name|exceptions
operator|.
name|SCMException
operator|.
name|ResultCodes
operator|.
name|INVALID_BLOCK_SIZE
import|;
end_import

begin_comment
comment|/**  * Block Manager manages the block access for SCM.  */
end_comment

begin_class
DECL|class|BlockManagerImpl
specifier|public
class|class
name|BlockManagerImpl
implements|implements
name|BlockManager
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
name|BlockManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|nodeManager
specifier|private
specifier|final
name|NodeManager
name|nodeManager
decl_stmt|;
DECL|field|containerManager
specifier|private
specifier|final
name|Mapping
name|containerManager
decl_stmt|;
DECL|field|blockStore
specifier|private
specifier|final
name|MetadataStore
name|blockStore
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|field|containerSize
specifier|private
specifier|final
name|long
name|containerSize
decl_stmt|;
DECL|field|cacheSize
specifier|private
specifier|final
name|long
name|cacheSize
decl_stmt|;
DECL|field|openContainerStore
specifier|private
specifier|final
name|MetadataStore
name|openContainerStore
decl_stmt|;
DECL|field|openContainers
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|openContainers
decl_stmt|;
DECL|field|containerProvisionBatchSize
specifier|private
specifier|final
name|int
name|containerProvisionBatchSize
decl_stmt|;
DECL|field|rand
specifier|private
specifier|final
name|Random
name|rand
decl_stmt|;
comment|/**    * Constructor.    * @param conf - configuration.    * @param nodeManager - node manager.    * @param containerManager - container manager.    * @param cacheSizeMB - cache size for level db store.    * @throws IOException    */
DECL|method|BlockManagerImpl (final Configuration conf, final NodeManager nodeManager, final Mapping containerManager, final int cacheSizeMB)
specifier|public
name|BlockManagerImpl
parameter_list|(
specifier|final
name|Configuration
name|conf
parameter_list|,
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|Mapping
name|containerManager
parameter_list|,
specifier|final
name|int
name|cacheSizeMB
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|nodeManager
operator|=
name|nodeManager
expr_stmt|;
name|this
operator|.
name|containerManager
operator|=
name|containerManager
expr_stmt|;
name|this
operator|.
name|cacheSize
operator|=
name|cacheSizeMB
expr_stmt|;
name|File
name|metaDir
init|=
name|OzoneUtils
operator|.
name|getScmMetadirPath
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
name|scmMetaDataDir
init|=
name|metaDir
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|// Write the block key to container name mapping.
name|File
name|blockContainerDbPath
init|=
operator|new
name|File
argument_list|(
name|scmMetaDataDir
argument_list|,
name|BLOCK_DB
argument_list|)
decl_stmt|;
name|blockStore
operator|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|blockContainerDbPath
argument_list|)
operator|.
name|setCacheSize
argument_list|(
name|this
operator|.
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerSize
operator|=
name|OzoneConsts
operator|.
name|GB
operator|*
name|conf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_GB
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_SIZE_DEFAULT
argument_list|)
expr_stmt|;
comment|// Load store of all open contains for block allocation
name|File
name|openContainsDbPath
init|=
operator|new
name|File
argument_list|(
name|scmMetaDataDir
argument_list|,
name|OPEN_CONTAINERS_DB
argument_list|)
decl_stmt|;
name|openContainerStore
operator|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|openContainsDbPath
argument_list|)
operator|.
name|setCacheSize
argument_list|(
name|this
operator|.
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|openContainers
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|loadOpenContainers
argument_list|()
expr_stmt|;
name|this
operator|.
name|containerProvisionBatchSize
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE
argument_list|,
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PROVISION_BATCH_SIZE_DEFAULT
argument_list|)
expr_stmt|;
name|rand
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
name|this
operator|.
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
block|}
comment|// TODO: close full (or almost full) containers with a separate thread.
comment|/**    * Load open containers from persistent store.    * @throws IOException    */
DECL|method|loadOpenContainers ()
specifier|private
name|void
name|loadOpenContainers
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|openContainerStore
operator|.
name|iterate
argument_list|(
literal|null
argument_list|,
parameter_list|(
name|key
parameter_list|,
name|value
parameter_list|)
lambda|->
block|{
try|try
block|{
name|String
name|containerName
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|Long
name|containerUsed
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|value
argument_list|)
argument_list|)
decl_stmt|;
name|openContainers
operator|.
name|put
argument_list|(
name|containerName
argument_list|,
name|containerUsed
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Loading open container: {} used : {}"
argument_list|,
name|containerName
argument_list|,
name|containerUsed
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed loading open container, continue next..."
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Loading open container store failed."
operator|+
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Failed to load open container store"
argument_list|,
name|FAILED_TO_LOAD_OPEN_CONTAINER
argument_list|)
throw|;
block|}
block|}
comment|/**    * Pre-provision specified count of containers for block creation.    * @param count - number of containers to create.    * @return list of container names created.    * @throws IOException    */
DECL|method|provisionContainers (int count)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|provisionContainers
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|String
argument_list|>
name|results
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|String
name|containerName
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
try|try
block|{
name|Pipeline
name|pipeline
init|=
name|containerManager
operator|.
name|allocateContainer
argument_list|(
name|containerName
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipeline
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to allocate container."
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to allocate container: "
operator|+
name|ex
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|openContainers
operator|.
name|put
argument_list|(
name|containerName
argument_list|,
literal|0L
argument_list|)
expr_stmt|;
name|openContainerStore
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|containerName
argument_list|)
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
literal|0L
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|results
operator|.
name|add
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
name|results
return|;
block|}
comment|/**    * Allocates a new block for a given size.    *    * SCM choose one of the open containers and returns that as the location for    * the new block. An open container is a container that is actively written to    * via replicated log.    * @param size - size of the block to be allocated    * @return - the allocated pipeline and key for the block    * @throws IOException    */
annotation|@
name|Override
DECL|method|allocateBlock (final long size)
specifier|public
name|AllocatedBlock
name|allocateBlock
parameter_list|(
specifier|final
name|long
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|boolean
name|createContainer
decl_stmt|;
name|Pipeline
name|pipeline
decl_stmt|;
if|if
condition|(
name|size
argument_list|<
literal|0
operator|||
name|size
argument_list|>
name|containerSize
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unsupported block size"
argument_list|,
name|INVALID_BLOCK_SIZE
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|nodeManager
operator|.
name|isOutOfNodeChillMode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unable to create block while in chill mode"
argument_list|,
name|CHILL_MODE_EXCEPTION
argument_list|)
throw|;
block|}
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|List
argument_list|<
name|String
argument_list|>
name|candidates
decl_stmt|;
if|if
condition|(
name|openContainers
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|candidates
operator|=
name|provisionContainers
argument_list|(
name|containerProvisionBatchSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unable to allocate container for the block"
argument_list|,
name|FAILED_TO_ALLOCATE_CONTAINER
argument_list|)
throw|;
block|}
name|createContainer
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|candidates
operator|=
name|openContainers
operator|.
name|entrySet
argument_list|()
operator|.
name|parallelStream
argument_list|()
operator|.
name|filter
argument_list|(
name|e
lambda|->
operator|(
name|e
operator|.
name|getValue
argument_list|()
operator|+
name|size
operator|<
name|containerSize
operator|)
argument_list|)
operator|.
name|map
argument_list|(
name|e
lambda|->
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
expr_stmt|;
name|createContainer
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
name|candidates
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
try|try
block|{
name|candidates
operator|=
name|provisionContainers
argument_list|(
name|containerProvisionBatchSize
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unable to allocate container for the block"
argument_list|,
name|FAILED_TO_ALLOCATE_CONTAINER
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|candidates
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Fail to find any container to allocate block "
operator|+
literal|"of size "
operator|+
name|size
operator|+
literal|"."
argument_list|,
name|FAILED_TO_FIND_CONTAINER_WITH_SAPCE
argument_list|)
throw|;
block|}
name|int
name|randomIdx
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|candidates
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|containerName
init|=
name|candidates
operator|.
name|get
argument_list|(
name|randomIdx
argument_list|)
decl_stmt|;
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
literal|"Find {} candidates: {}, picking: {}"
argument_list|,
name|candidates
operator|.
name|size
argument_list|()
argument_list|,
name|candidates
operator|.
name|toString
argument_list|()
argument_list|,
name|containerName
argument_list|)
expr_stmt|;
block|}
name|pipeline
operator|=
name|containerManager
operator|.
name|getContainer
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
if|if
condition|(
name|pipeline
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to find container for the block"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unable to find container to allocate block"
argument_list|,
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
comment|// TODO: make block key easier to debug (e.g., seq no)
comment|// Allocate key for the block
name|String
name|blockKey
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|AllocatedBlock
operator|.
name|Builder
name|abb
init|=
operator|new
name|AllocatedBlock
operator|.
name|Builder
argument_list|()
operator|.
name|setKey
argument_list|(
name|blockKey
argument_list|)
operator|.
name|setPipeline
argument_list|(
name|pipeline
argument_list|)
operator|.
name|setShouldCreateContainer
argument_list|(
name|createContainer
argument_list|)
decl_stmt|;
if|if
condition|(
name|pipeline
operator|.
name|getMachines
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|blockStore
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|blockKey
argument_list|)
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|containerName
argument_list|)
argument_list|)
expr_stmt|;
comment|// update the container usage information
name|Long
name|newUsed
init|=
name|openContainers
operator|.
name|get
argument_list|(
name|containerName
argument_list|)
operator|+
name|size
decl_stmt|;
name|openContainers
operator|.
name|put
argument_list|(
name|containerName
argument_list|,
name|newUsed
argument_list|)
expr_stmt|;
name|openContainerStore
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|containerName
argument_list|)
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|newUsed
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|abb
operator|.
name|build
argument_list|()
return|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    *    * Given a block key, return the Pipeline information.    * @param key - block key assigned by SCM.    * @return Pipeline (list of DNs and leader) to access the block.    * @throws IOException    */
annotation|@
name|Override
DECL|method|getBlock (final String key)
specifier|public
name|Pipeline
name|getBlock
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|containerBytes
init|=
name|blockStore
operator|.
name|get
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerBytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Specified block key does not exist. key : "
operator|+
name|key
argument_list|,
name|FAILED_TO_FIND_BLOCK
argument_list|)
throw|;
block|}
return|return
name|containerManager
operator|.
name|getContainer
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|containerBytes
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Given a block key, delete a block.    * @param key - block key assigned by SCM.    * @throws IOException    */
annotation|@
name|Override
DECL|method|deleteBlock (final String key)
specifier|public
name|void
name|deleteBlock
parameter_list|(
specifier|final
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|nodeManager
operator|.
name|isOutOfNodeChillMode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Unable to delete block while in chill mode"
argument_list|,
name|CHILL_MODE_EXCEPTION
argument_list|)
throw|;
block|}
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|containerBytes
init|=
name|blockStore
operator|.
name|get
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerBytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Specified block key does not exist. key : "
operator|+
name|key
argument_list|,
name|FAILED_TO_FIND_BLOCK
argument_list|)
throw|;
block|}
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|containerManager
operator|.
name|getContainer
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|containerBytes
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|deletedKeyName
init|=
name|getDeletedKeyName
argument_list|(
name|key
argument_list|)
decl_stmt|;
comment|// Add a tombstone for the deleted key
name|batch
operator|.
name|put
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|deletedKeyName
argument_list|)
argument_list|,
name|containerBytes
argument_list|)
expr_stmt|;
comment|// Delete the block key
name|batch
operator|.
name|delete
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
name|blockStore
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
comment|// TODO: Add async tombstone clean thread to send delete command to
comment|// datanodes in the pipeline to clean up the blocks from containers.
comment|// TODO: Container report handling of the deleted blocks:
comment|// Remove tombstone and update open container usage.
comment|// We will revisit this when the closed container replication is done.
block|}
finally|finally
block|{
name|lock
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDeletedKeyName (String key)
specifier|public
name|String
name|getDeletedKeyName
parameter_list|(
name|String
name|key
parameter_list|)
block|{
return|return
name|StringUtils
operator|.
name|format
argument_list|(
literal|".Deleted/%s"
argument_list|,
name|key
argument_list|)
return|;
block|}
comment|/**    * Close the resources for BlockManager.    * @throws IOException    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|blockStore
operator|!=
literal|null
condition|)
block|{
name|blockStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|openContainerStore
operator|!=
literal|null
condition|)
block|{
name|openContainerStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

