begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|ozone
operator|.
name|protocol
operator|.
name|proto
operator|.
name|ContainerProtos
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
name|ozone
operator|.
name|OzoneConfigKeys
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
name|placement
operator|.
name|algorithms
operator|.
name|ContainerPlacementPolicy
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
name|placement
operator|.
name|algorithms
operator|.
name|SCMContainerPlacementRandom
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
name|client
operator|.
name|ScmClient
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
name|LevelDBStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|iq80
operator|.
name|leveldb
operator|.
name|Options
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
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
name|List
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
comment|/**  * Mapping class contains the mapping from a name to a pipeline mapping. This is  * used by SCM when allocating new locations and when looking up a key.  */
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
name|LevelDBStore
name|containerStore
decl_stmt|;
DECL|field|placementPolicy
specifier|private
specifier|final
name|ContainerPlacementPolicy
name|placementPolicy
decl_stmt|;
DECL|field|containerSize
specifier|private
specifier|final
name|long
name|containerSize
decl_stmt|;
comment|/**    * Constructs a mapping class that creates mapping between container names and    * pipelines.    *    * @param nodeManager - NodeManager so that we can get the nodes that are    * healthy to place new containers.    * @param cacheSizeMB - Amount of memory reserved for the LSM tree to cache    * its nodes. This is passed to LevelDB and this memory is allocated in Native    * code space. CacheSize is specified in MB.    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|ContainerMapping (final Configuration conf, final NodeManager nodeManager, final int cacheSizeMB)
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
comment|// TODO: Fix this checking.
name|String
name|scmMetaDataDir
init|=
name|conf
operator|.
name|get
argument_list|(
name|OzoneConfigKeys
operator|.
name|OZONE_CONTAINER_METADATA_DIRS
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|scmMetaDataDir
operator|==
literal|null
operator|)
operator|||
operator|(
name|scmMetaDataDir
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SCM metadata directory is not valid."
argument_list|)
throw|;
block|}
name|Options
name|options
init|=
operator|new
name|Options
argument_list|()
decl_stmt|;
name|options
operator|.
name|cacheSize
argument_list|(
name|this
operator|.
name|cacheSize
operator|*
name|OzoneConsts
operator|.
name|MB
argument_list|)
expr_stmt|;
name|options
operator|.
name|createIfMissing
argument_list|()
expr_stmt|;
comment|// Write the container name to pipeline mapping.
name|File
name|containerDBPath
init|=
operator|new
name|File
argument_list|(
name|scmMetaDataDir
argument_list|,
name|CONTAINER_DB
argument_list|)
decl_stmt|;
name|containerStore
operator|=
operator|new
name|LevelDBStore
argument_list|(
name|containerDBPath
argument_list|,
name|options
argument_list|)
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
name|this
operator|.
name|placementPolicy
operator|=
name|createContainerPlacementPolicy
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create pluggable container placement policy implementation instance.    *    * @param nodeManager - SCM node manager.    * @param conf - configuration.    * @return SCM container placement policy implementation instance.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|createContainerPlacementPolicy ( final NodeManager nodeManager, final Configuration conf)
specifier|private
specifier|static
name|ContainerPlacementPolicy
name|createContainerPlacementPolicy
parameter_list|(
specifier|final
name|NodeManager
name|nodeManager
parameter_list|,
specifier|final
name|Configuration
name|conf
parameter_list|)
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
name|implClass
init|=
operator|(
name|Class
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
operator|)
name|conf
operator|.
name|getClass
argument_list|(
name|ScmConfigKeys
operator|.
name|OZONE_SCM_CONTAINER_PLACEMENT_IMPL_KEY
argument_list|,
name|SCMContainerPlacementRandom
operator|.
name|class
argument_list|)
decl_stmt|;
try|try
block|{
name|Constructor
argument_list|<
name|?
extends|extends
name|ContainerPlacementPolicy
argument_list|>
name|ctor
init|=
name|implClass
operator|.
name|getDeclaredConstructor
argument_list|(
name|NodeManager
operator|.
name|class
argument_list|,
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|ctor
operator|.
name|newInstance
argument_list|(
name|nodeManager
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|implClass
operator|.
name|getName
argument_list|()
operator|+
literal|" could not be constructed."
argument_list|,
name|e
operator|.
name|getCause
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unhandled exception occured, Placement policy will not be "
operator|+
literal|"functional."
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unable to load "
operator|+
literal|"ContainerPlacementPolicy"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Translates a list of nodes, ordered such that the first is the leader, into    * a corresponding {@link Pipeline} object.    * @param nodes - list of datanodes on which we will allocate the container.    * The first of the list will be the leader node.    * @param containerName container name    * @return pipeline corresponding to nodes    */
DECL|method|newPipelineFromNodes (final List<DatanodeID> nodes, final String containerName)
specifier|private
specifier|static
name|Pipeline
name|newPipelineFromNodes
parameter_list|(
specifier|final
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|nodes
parameter_list|,
specifier|final
name|String
name|containerName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|nodes
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|nodes
operator|.
name|size
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|String
name|leaderId
init|=
name|nodes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getDatanodeUuid
argument_list|()
decl_stmt|;
name|Pipeline
name|pipeline
init|=
operator|new
name|Pipeline
argument_list|(
name|leaderId
argument_list|)
decl_stmt|;
for|for
control|(
name|DatanodeID
name|node
range|:
name|nodes
control|)
block|{
name|pipeline
operator|.
name|addMember
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
name|pipeline
operator|.
name|setContainerName
argument_list|(
name|containerName
argument_list|)
expr_stmt|;
return|return
name|pipeline
return|;
block|}
comment|/**    * Returns the Pipeline from the container name.    *    * @param containerName - Name    * @return - Pipeline that makes up this container.    */
annotation|@
name|Override
DECL|method|getContainer (final String containerName)
specifier|public
name|Pipeline
name|getContainer
parameter_list|(
specifier|final
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
name|Pipeline
name|pipeline
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
name|pipelineBytes
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
name|pipelineBytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Specified key does not exist. key : "
operator|+
name|containerName
argument_list|)
throw|;
block|}
name|pipeline
operator|=
name|Pipeline
operator|.
name|getFromProtoBuf
argument_list|(
name|ContainerProtos
operator|.
name|Pipeline
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|pipelineBytes
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|pipeline
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
comment|/**    * Allocates a new container.    *    * @param containerName - Name of the container.    * @return - Pipeline that makes up this container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|allocateContainer (final String containerName)
specifier|public
name|Pipeline
name|allocateContainer
parameter_list|(
specifier|final
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|allocateContainer
argument_list|(
name|containerName
argument_list|,
name|ScmClient
operator|.
name|ReplicationFactor
operator|.
name|ONE
argument_list|)
return|;
block|}
comment|/**    * Allocates a new container.    *    * @param containerName - Name of the container.    * @param replicationFactor - replication factor of the container.    * @return - Pipeline that makes up this container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|allocateContainer (final String containerName, final ScmClient.ReplicationFactor replicationFactor)
specifier|public
name|Pipeline
name|allocateContainer
parameter_list|(
specifier|final
name|String
name|containerName
parameter_list|,
specifier|final
name|ScmClient
operator|.
name|ReplicationFactor
name|replicationFactor
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
name|Pipeline
name|pipeline
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
name|IOException
argument_list|(
literal|"Unable to create container while in chill mode"
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
name|pipelineBytes
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
name|pipelineBytes
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Specified container already exists. key : "
operator|+
name|containerName
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|datanodes
init|=
name|placementPolicy
operator|.
name|chooseDatanodes
argument_list|(
name|replicationFactor
operator|.
name|getValue
argument_list|()
argument_list|,
name|containerSize
argument_list|)
decl_stmt|;
comment|// TODO: handle under replicated container
if|if
condition|(
name|datanodes
operator|!=
literal|null
operator|&&
name|datanodes
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|pipeline
operator|=
name|newPipelineFromNodes
argument_list|(
name|datanodes
argument_list|,
name|containerName
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
name|pipeline
operator|.
name|getProtobufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Unable to find enough datanodes for new container. "
operator|+
literal|"Required {} found {}"
argument_list|,
name|replicationFactor
argument_list|,
name|datanodes
operator|!=
literal|null
condition|?
name|datanodes
operator|.
name|size
argument_list|()
else|:
literal|0
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
name|pipeline
return|;
block|}
comment|/**    * Deletes a container from SCM.    *    * @param containerName - Container name    * @throws IOException    *   if container doesn't exist    *   or container store failed to delete the specified key.    */
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
name|pipelineBytes
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
name|pipelineBytes
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Failed to delete container "
operator|+
name|containerName
operator|+
literal|", reason : container doesn't exist."
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
comment|/**    * Closes this stream and releases any system resources associated with it. If    * the stream is already closed then invoking this method has no effect.    *<p>    *<p> As noted in {@link AutoCloseable#close()}, cases where the close may    * fail require careful attention. It is strongly advised to relinquish the    * underlying resources and to internally<em>mark</em> the {@code Closeable}    * as closed, prior to throwing the {@code IOException}.    *    * @throws IOException if an I/O error occurs    */
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

