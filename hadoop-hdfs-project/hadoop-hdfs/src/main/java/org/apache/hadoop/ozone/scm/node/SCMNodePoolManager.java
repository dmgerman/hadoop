begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.scm.node
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
name|node
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
name|proto
operator|.
name|HdfsProtos
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
name|OzoneConfiguration
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
name|DBIterator
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
name|HashSet
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
name|Set
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
name|ReadWriteLock
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
name|ReentrantReadWriteLock
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
name|FAILED_TO_LOAD_NODEPOOL
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
name|FAILED_TO_FIND_NODE_IN_POOL
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
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
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_MB
import|;
end_import

begin_comment
comment|/**  * SCM node pool manager that manges node pools.  */
end_comment

begin_class
DECL|class|SCMNodePoolManager
specifier|public
specifier|final
class|class
name|SCMNodePoolManager
implements|implements
name|NodePoolManager
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
name|SCMNodePoolManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|EMPTY_NODE_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|EMPTY_NODE_LIST
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|EMPTY_NODEPOOL_LIST
specifier|private
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|EMPTY_NODEPOOL_LIST
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|DEFAULT_NODEPOOL
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_NODEPOOL
init|=
literal|"DefaultNodePool"
decl_stmt|;
comment|// DB that saves the node to node pool mapping.
DECL|field|nodePoolStore
specifier|private
name|LevelDBStore
name|nodePoolStore
decl_stmt|;
comment|// In-memory node pool to nodes mapping
DECL|field|nodePools
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|DatanodeID
argument_list|>
argument_list|>
name|nodePools
decl_stmt|;
comment|// Read-write lock for nodepool operations
DECL|field|lock
specifier|private
name|ReadWriteLock
name|lock
decl_stmt|;
comment|/**    * Construct SCMNodePoolManager class that manages node to node pool mapping.    * @param conf - configuration.    * @throws IOException    */
DECL|method|SCMNodePoolManager (final OzoneConfiguration conf)
specifier|public
name|SCMNodePoolManager
parameter_list|(
specifier|final
name|OzoneConfiguration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|cacheSize
init|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_DB_CACHE_SIZE_MB
argument_list|,
name|OZONE_SCM_DB_CACHE_SIZE_DEFAULT
argument_list|)
decl_stmt|;
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
name|scmMetaDataDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"SCM metadata directory is invalid."
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
name|File
name|nodePoolDBPath
init|=
operator|new
name|File
argument_list|(
name|scmMetaDataDir
argument_list|,
literal|"nodepool.db"
argument_list|)
decl_stmt|;
name|nodePoolStore
operator|=
operator|new
name|LevelDBStore
argument_list|(
name|nodePoolDBPath
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|nodePools
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * Initialize the in-memory store based on persist store from level db.    * No lock is needed as init() is only invoked by constructor.    * @throws SCMException    */
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
throws|throws
name|SCMException
block|{
try|try
init|(
name|DBIterator
name|iter
init|=
name|nodePoolStore
operator|.
name|getIterator
argument_list|()
init|)
block|{
for|for
control|(
name|iter
operator|.
name|seekToFirst
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
name|iter
operator|.
name|next
argument_list|()
control|)
block|{
try|try
block|{
name|byte
index|[]
name|key
init|=
name|iter
operator|.
name|peekNext
argument_list|()
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|DatanodeID
name|nodeId
init|=
name|DatanodeID
operator|.
name|getFromProtoBuf
argument_list|(
name|HdfsProtos
operator|.
name|DatanodeIDProto
operator|.
name|PARSER
operator|.
name|parseFrom
argument_list|(
name|key
argument_list|)
argument_list|)
decl_stmt|;
name|byte
index|[]
name|value
init|=
name|iter
operator|.
name|peekNext
argument_list|()
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
name|poolName
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|DatanodeID
argument_list|>
name|nodePool
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodePools
operator|.
name|containsKey
argument_list|(
name|poolName
argument_list|)
condition|)
block|{
name|nodePool
operator|=
name|nodePools
operator|.
name|get
argument_list|(
name|poolName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodePool
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
name|nodePools
operator|.
name|put
argument_list|(
name|poolName
argument_list|,
name|nodePool
argument_list|)
expr_stmt|;
block|}
name|nodePool
operator|.
name|add
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
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
literal|"Adding node: {} to node pool: {}"
argument_list|,
name|nodeId
argument_list|,
name|poolName
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Can't add a datanode to node pool, continue next..."
argument_list|)
expr_stmt|;
block|}
block|}
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
literal|"Loading node pool error "
operator|+
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|SCMException
argument_list|(
literal|"Failed to load node pool"
argument_list|,
name|FAILED_TO_LOAD_NODEPOOL
argument_list|)
throw|;
block|}
block|}
comment|/**    * Add a datanode to a node pool.    * @param pool - name of the node pool.    * @param node - name of the datanode.    */
annotation|@
name|Override
DECL|method|addNode (final String pool, final DatanodeID node)
specifier|public
name|void
name|addNode
parameter_list|(
specifier|final
name|String
name|pool
parameter_list|,
specifier|final
name|DatanodeID
name|node
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pool
argument_list|,
literal|"pool name is null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|node
argument_list|,
literal|"node is null"
argument_list|)
expr_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// add to the persistent store
name|nodePoolStore
operator|.
name|put
argument_list|(
name|node
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|pool
argument_list|)
argument_list|)
expr_stmt|;
comment|// add to the in-memory store
name|Set
argument_list|<
name|DatanodeID
argument_list|>
name|nodePool
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|nodePools
operator|.
name|containsKey
argument_list|(
name|pool
argument_list|)
condition|)
block|{
name|nodePool
operator|=
name|nodePools
operator|.
name|get
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|nodePool
operator|=
operator|new
name|HashSet
argument_list|<
name|DatanodeID
argument_list|>
argument_list|()
expr_stmt|;
name|nodePools
operator|.
name|put
argument_list|(
name|pool
argument_list|,
name|nodePool
argument_list|)
expr_stmt|;
block|}
name|nodePool
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Remove a datanode from a node pool.    * @param pool - name of the node pool.    * @param node - datanode id.    * @throws SCMException    */
annotation|@
name|Override
DECL|method|removeNode (final String pool, final DatanodeID node)
specifier|public
name|void
name|removeNode
parameter_list|(
specifier|final
name|String
name|pool
parameter_list|,
specifier|final
name|DatanodeID
name|node
parameter_list|)
throws|throws
name|SCMException
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pool
argument_list|,
literal|"pool name is null"
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|node
argument_list|,
literal|"node is null"
argument_list|)
expr_stmt|;
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
comment|// Remove from the persistent store
name|byte
index|[]
name|kName
init|=
name|node
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|byte
index|[]
name|kData
init|=
name|nodePoolStore
operator|.
name|get
argument_list|(
name|kName
argument_list|)
decl_stmt|;
if|if
condition|(
name|kData
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to find node %s from"
operator|+
literal|" pool %s in DB."
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|kName
argument_list|)
argument_list|,
name|pool
argument_list|)
argument_list|,
name|FAILED_TO_FIND_NODE_IN_POOL
argument_list|)
throw|;
block|}
name|nodePoolStore
operator|.
name|delete
argument_list|(
name|kName
argument_list|)
expr_stmt|;
comment|// Remove from the in-memory store
if|if
condition|(
name|nodePools
operator|.
name|containsKey
argument_list|(
name|pool
argument_list|)
condition|)
block|{
name|Set
argument_list|<
name|DatanodeID
argument_list|>
name|nodePool
init|=
name|nodePools
operator|.
name|get
argument_list|(
name|pool
argument_list|)
decl_stmt|;
name|nodePool
operator|.
name|remove
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|SCMException
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Unable to find node %s from"
operator|+
literal|" pool %s in MAP."
argument_list|,
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|kName
argument_list|)
argument_list|,
name|pool
argument_list|)
argument_list|,
name|FAILED_TO_FIND_NODE_IN_POOL
argument_list|)
throw|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get all the node pools.    * @return all the node pools.    */
annotation|@
name|Override
DECL|method|getNodePools ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getNodePools
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|nodePools
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|nodePools
operator|.
name|keySet
argument_list|()
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|EMPTY_NODEPOOL_LIST
return|;
block|}
block|}
finally|finally
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Get all datanodes of a specific node pool.    * @param pool - name of the node pool.    * @return all datanodes of the specified node pool.    */
annotation|@
name|Override
DECL|method|getNodes (final String pool)
specifier|public
name|List
argument_list|<
name|DatanodeID
argument_list|>
name|getNodes
parameter_list|(
specifier|final
name|String
name|pool
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|pool
argument_list|,
literal|"pool name is null"
argument_list|)
expr_stmt|;
if|if
condition|(
name|nodePools
operator|.
name|containsKey
argument_list|(
name|pool
argument_list|)
condition|)
block|{
return|return
name|nodePools
operator|.
name|get
argument_list|(
name|pool
argument_list|)
operator|.
name|stream
argument_list|()
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toList
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|EMPTY_NODE_LIST
return|;
block|}
block|}
comment|/**    * Get the node pool name if the node has been added to a node pool.    * @param datanodeID - datanode ID.    * @return node pool name if it has been assigned.    * null if the node has not been assigned to any node pool yet.    * TODO: Put this in a in-memory map if performance is an issue.    */
annotation|@
name|Override
DECL|method|getNodePool (final DatanodeID datanodeID)
specifier|public
name|String
name|getNodePool
parameter_list|(
specifier|final
name|DatanodeID
name|datanodeID
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkNotNull
argument_list|(
name|datanodeID
argument_list|,
literal|"node is null"
argument_list|)
expr_stmt|;
name|byte
index|[]
name|result
init|=
name|nodePoolStore
operator|.
name|get
argument_list|(
name|datanodeID
operator|.
name|getProtoBufMessage
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|result
argument_list|)
return|;
block|}
comment|/**    * Close node pool level db store.    * @throws IOException    */
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
name|nodePoolStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

