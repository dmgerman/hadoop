begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  *<p>http://www.apache.org/licenses/LICENSE-2.0  *<p>  *<p>Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.container
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
name|container
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|Owner
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationFactor
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
name|protocol
operator|.
name|proto
operator|.
name|OzoneProtos
operator|.
name|ReplicationType
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
name|scm
operator|.
name|pipelines
operator|.
name|PipelineSelector
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
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|BlockContainerInfo
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
name|ContainerInfo
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
name|utils
operator|.
name|MetadataKeyFilters
operator|.
name|KeyPrefixFilter
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
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
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
name|nio
operator|.
name|charset
operator|.
name|Charset
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
name|CONTAINER_DB
import|;
end_import

begin_comment
comment|/**  * Mapping class contains the mapping from a name to a pipeline mapping. This  * is used by SCM when  * allocating new locations and when looking up a key.  */
end_comment

begin_class
DECL|class|ContainerMapping
specifier|public
class|class
name|ContainerMapping
implements|implements
name|Mapping
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
name|ContainerMapping
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
DECL|field|cacheSize
specifier|private
specifier|final
name|long
name|cacheSize
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
DECL|field|encoding
specifier|private
specifier|final
name|Charset
name|encoding
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"UTF-8"
argument_list|)
decl_stmt|;
DECL|field|containerStore
specifier|private
specifier|final
name|MetadataStore
name|containerStore
decl_stmt|;
DECL|field|pipelineSelector
specifier|private
specifier|final
name|PipelineSelector
name|pipelineSelector
decl_stmt|;
DECL|field|containerStateManager
specifier|private
specifier|final
name|ContainerStateManager
name|containerStateManager
decl_stmt|;
comment|/**    * Constructs a mapping class that creates mapping between container names    * and pipelines.    *    * @param nodeManager - NodeManager so that we can get the nodes that are    * healthy to place new    *     containers.    * @param cacheSizeMB - Amount of memory reserved for the LSM tree to cache    * its nodes. This is    *     passed to LevelDB and this memory is allocated in Native code space.    *     CacheSize is specified    *     in MB.    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ContainerMapping ( final Configuration conf, final NodeManager nodeManager, final int cacheSizeMB)
specifier|public
name|ContainerMapping
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
comment|// Write the container name to pipeline mapping.
name|File
name|containerDBPath
init|=
operator|new
name|File
argument_list|(
name|metaDir
argument_list|,
name|CONTAINER_DB
argument_list|)
decl_stmt|;
name|containerStore
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
name|containerDBPath
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
name|lock
operator|=
operator|new
name|ReentrantLock
argument_list|()
expr_stmt|;
name|this
operator|.
name|pipelineSelector
operator|=
operator|new
name|PipelineSelector
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerStateManager
operator|=
operator|new
name|ContainerStateManager
argument_list|(
name|conf
argument_list|,
operator|+
name|this
operator|.
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Container State Manager created."
argument_list|)
expr_stmt|;
block|}
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|getContainer (final String containerName)
specifier|public
name|ContainerInfo
name|getContainer
parameter_list|(
specifier|final
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerInfo
name|containerInfo
decl_stmt|;
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
name|containerStore
operator|.
name|get
argument_list|(
name|containerName
operator|.
name|getBytes
argument_list|(
name|encoding
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
literal|"Specified key does not exist. key : "
operator|+
name|containerName
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
name|containerInfo
operator|=
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|OzoneProtos
operator|.
name|SCMContainerInfo
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|containerBytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|containerInfo
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
comment|/** {@inheritDoc} */
annotation|@
name|Override
DECL|method|listContainer (String startName, String prefixName, int count)
specifier|public
name|List
argument_list|<
name|Pipeline
argument_list|>
name|listContainer
parameter_list|(
name|String
name|startName
parameter_list|,
name|String
name|prefixName
parameter_list|,
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Pipeline
argument_list|>
name|pipelineList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
name|containerStore
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"No container exists in current db"
argument_list|)
throw|;
block|}
name|MetadataKeyFilter
name|prefixFilter
init|=
operator|new
name|KeyPrefixFilter
argument_list|(
name|prefixName
argument_list|)
decl_stmt|;
name|byte
index|[]
name|startKey
init|=
name|startName
operator|==
literal|null
condition|?
literal|null
else|:
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|startName
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|range
init|=
name|containerStore
operator|.
name|getRangeKVs
argument_list|(
name|startKey
argument_list|,
name|count
argument_list|,
name|prefixFilter
argument_list|)
decl_stmt|;
comment|// Transform the values into the pipelines.
comment|// TODO: return list of ContainerInfo instead of pipelines.
comment|// TODO: filter by container state
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|range
control|)
block|{
name|ContainerInfo
name|containerInfo
init|=
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|OzoneProtos
operator|.
name|SCMContainerInfo
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
name|pipelineList
operator|.
name|add
argument_list|(
name|containerInfo
operator|.
name|getPipeline
argument_list|()
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
name|pipelineList
return|;
block|}
comment|/**    * Allocates a new container.    *    * @param replicationFactor - replication factor of the container.    * @param containerName - Name of the container.    * @param owner    * @return - Pipeline that makes up this container.    * @throws IOException - Exception    */
annotation|@
name|Override
DECL|method|allocateContainer ( ReplicationType type, ReplicationFactor replicationFactor, final String containerName, Owner owner)
specifier|public
name|ContainerInfo
name|allocateContainer
parameter_list|(
name|ReplicationType
name|type
parameter_list|,
name|ReplicationFactor
name|replicationFactor
parameter_list|,
specifier|final
name|String
name|containerName
parameter_list|,
name|Owner
name|owner
parameter_list|)
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|containerName
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|ContainerInfo
name|containerInfo
init|=
literal|null
decl_stmt|;
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
literal|"Unable to create container while in chill mode"
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
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
name|containerStore
operator|.
name|get
argument_list|(
name|containerName
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|containerBytes
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Specified container already exists. key : "
operator|+
name|containerName
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|CONTAINER_EXISTS
argument_list|)
throw|;
block|}
name|containerInfo
operator|=
name|containerStateManager
operator|.
name|allocateContainer
argument_list|(
name|pipelineSelector
argument_list|,
name|type
argument_list|,
name|replicationFactor
argument_list|,
name|containerName
argument_list|,
name|owner
argument_list|)
expr_stmt|;
name|containerStore
operator|.
name|put
argument_list|(
name|containerName
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
argument_list|,
name|containerInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
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
name|containerInfo
return|;
block|}
comment|/**    * Deletes a container from SCM.    *    * @param containerName - Container name    * @throws IOException if container doesn't exist or container store failed    * to delete the    *     specified key.    */
annotation|@
name|Override
DECL|method|deleteContainer (String containerName)
specifier|public
name|void
name|deleteContainer
parameter_list|(
name|String
name|containerName
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
name|dbKey
init|=
name|containerName
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
name|byte
index|[]
name|containerBytes
init|=
name|containerStore
operator|.
name|get
argument_list|(
name|dbKey
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
literal|"Failed to delete container "
operator|+
name|containerName
operator|+
literal|", reason : "
operator|+
literal|"container doesn't exist."
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
name|containerStore
operator|.
name|delete
argument_list|(
name|dbKey
argument_list|)
expr_stmt|;
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
name|Override
DECL|method|closeContainer (String containerName)
specifier|public
name|void
name|closeContainer
parameter_list|(
name|String
name|containerName
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
name|OzoneProtos
operator|.
name|LifeCycleState
name|newState
init|=
name|updateContainerState
argument_list|(
name|containerName
argument_list|,
name|OzoneProtos
operator|.
name|LifeCycleEvent
operator|.
name|CLOSE
argument_list|)
decl_stmt|;
if|if
condition|(
name|newState
operator|!=
name|OzoneProtos
operator|.
name|LifeCycleState
operator|.
name|CLOSED
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Failed to close container "
operator|+
name|containerName
operator|+
literal|", reason : container in state "
operator|+
name|newState
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|UNEXPECTED_CONTAINER_STATE
argument_list|)
throw|;
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
block|}
comment|/** {@inheritDoc} Used by client to update container state on SCM. */
annotation|@
name|Override
DECL|method|updateContainerState ( String containerName, OzoneProtos.LifeCycleEvent event)
specifier|public
name|OzoneProtos
operator|.
name|LifeCycleState
name|updateContainerState
parameter_list|(
name|String
name|containerName
parameter_list|,
name|OzoneProtos
operator|.
name|LifeCycleEvent
name|event
parameter_list|)
throws|throws
name|IOException
block|{
name|ContainerInfo
name|containerInfo
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|byte
index|[]
name|dbKey
init|=
name|containerName
operator|.
name|getBytes
argument_list|(
name|encoding
argument_list|)
decl_stmt|;
name|byte
index|[]
name|containerBytes
init|=
name|containerStore
operator|.
name|get
argument_list|(
name|dbKey
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
literal|"Failed to update container state"
operator|+
name|containerName
operator|+
literal|", reason : container doesn't exist."
argument_list|,
name|SCMException
operator|.
name|ResultCodes
operator|.
name|FAILED_TO_FIND_CONTAINER
argument_list|)
throw|;
block|}
name|containerInfo
operator|=
name|ContainerInfo
operator|.
name|fromProtobuf
argument_list|(
name|OzoneProtos
operator|.
name|SCMContainerInfo
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|containerBytes
argument_list|)
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|containerInfo
argument_list|)
expr_stmt|;
comment|// TODO: Actual used will be updated via Container Reports later.
name|containerInfo
operator|.
name|setState
argument_list|(
name|containerStateManager
operator|.
name|updateContainerState
argument_list|(
operator|new
name|BlockContainerInfo
argument_list|(
name|containerInfo
argument_list|,
literal|0
argument_list|)
argument_list|,
name|event
argument_list|)
argument_list|)
expr_stmt|;
name|containerStore
operator|.
name|put
argument_list|(
name|dbKey
argument_list|,
name|containerInfo
operator|.
name|getProtobuf
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|containerInfo
operator|.
name|getState
argument_list|()
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
comment|/** + * Returns the container State Manager. + * + * @return    * ContainerStateManager + */
annotation|@
name|Override
DECL|method|getStateManager ()
specifier|public
name|ContainerStateManager
name|getStateManager
parameter_list|()
block|{
return|return
name|containerStateManager
return|;
block|}
comment|/**    * Closes this stream and releases any system resources associated with it.    * If the stream is    * already closed then invoking this method has no effect.    *    *<p>    *    *<p>As noted in {@link AutoCloseable#close()}, cases where the close may    * fail require careful    * attention. It is strongly advised to relinquish the underlying resources    * and to internally    *<em>mark</em> the {@code Closeable} as closed, prior to throwing the    * {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
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
name|containerStore
operator|!=
literal|null
condition|)
block|{
name|containerStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

