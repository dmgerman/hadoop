begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
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
name|primitives
operator|.
name|Longs
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|DeletedBlocksTransaction
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
name|Arrays
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
name|atomic
operator|.
name|AtomicInteger
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY_DEFAULT
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
name|hdds
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
name|hdds
operator|.
name|scm
operator|.
name|ScmConfigKeys
operator|.
name|OZONE_SCM_DB_CACHE_SIZE_MB
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
name|hdds
operator|.
name|server
operator|.
name|ServerUtils
operator|.
name|getOzoneMetaDirPath
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
name|DELETED_BLOCK_DB
import|;
end_import

begin_comment
comment|/**  * A implement class of {@link DeletedBlockLog}, and it uses  * K/V db to maintain block deletion transactions between scm and datanode.  * This is a very basic implementation, it simply scans the log and  * memorize the position that scanned by last time, and uses this to  * determine where the next scan starts. It has no notion about weight  * of each transaction so as long as transaction is still valid, they get  * equally same chance to be retrieved which only depends on the nature  * order of the transaction ID.  */
end_comment

begin_class
DECL|class|DeletedBlockLogImpl
specifier|public
class|class
name|DeletedBlockLogImpl
implements|implements
name|DeletedBlockLog
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
name|DeletedBlockLogImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LATEST_TXID
specifier|private
specifier|static
specifier|final
name|byte
index|[]
name|LATEST_TXID
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
literal|"#LATEST_TXID#"
argument_list|)
decl_stmt|;
DECL|field|maxRetry
specifier|private
specifier|final
name|int
name|maxRetry
decl_stmt|;
DECL|field|deletedStore
specifier|private
specifier|final
name|MetadataStore
name|deletedStore
decl_stmt|;
DECL|field|lock
specifier|private
specifier|final
name|Lock
name|lock
decl_stmt|;
comment|// The latest id of deleted blocks in the db.
DECL|field|lastTxID
specifier|private
name|long
name|lastTxID
decl_stmt|;
DECL|field|lastReadTxID
specifier|private
name|long
name|lastReadTxID
decl_stmt|;
DECL|method|DeletedBlockLogImpl (Configuration conf)
specifier|public
name|DeletedBlockLogImpl
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|maxRetry
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY
argument_list|,
name|OZONE_SCM_BLOCK_DELETION_MAX_RETRY_DEFAULT
argument_list|)
expr_stmt|;
name|File
name|metaDir
init|=
name|getOzoneMetaDirPath
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
name|File
name|deletedLogDbPath
init|=
operator|new
name|File
argument_list|(
name|scmMetaDataDir
argument_list|,
name|DELETED_BLOCK_DB
argument_list|)
decl_stmt|;
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
comment|// Load store of all transactions.
name|deletedStore
operator|=
name|MetadataStoreBuilder
operator|.
name|newBuilder
argument_list|()
operator|.
name|setCreateIfMissing
argument_list|(
literal|true
argument_list|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
operator|.
name|setDbFile
argument_list|(
name|deletedLogDbPath
argument_list|)
operator|.
name|setCacheSize
argument_list|(
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
comment|// start from the head of deleted store.
name|lastReadTxID
operator|=
literal|0
expr_stmt|;
name|lastTxID
operator|=
name|findLatestTxIDInStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getDeletedStore ()
name|MetadataStore
name|getDeletedStore
parameter_list|()
block|{
return|return
name|deletedStore
return|;
block|}
comment|/**    * There is no need to lock before reading because    * it's only used in construct method.    *    * @return latest txid.    * @throws IOException    */
DECL|method|findLatestTxIDInStore ()
specifier|private
name|long
name|findLatestTxIDInStore
parameter_list|()
throws|throws
name|IOException
block|{
name|long
name|txid
init|=
literal|0
decl_stmt|;
name|byte
index|[]
name|value
init|=
name|deletedStore
operator|.
name|get
argument_list|(
name|LATEST_TXID
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|txid
operator|=
name|Longs
operator|.
name|fromByteArray
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
return|return
name|txid
return|;
block|}
annotation|@
name|Override
DECL|method|getTransactions ( int count)
specifier|public
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|getTransactions
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|result
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|MetadataKeyFilter
name|getNextTxID
init|=
parameter_list|(
name|preKey
parameter_list|,
name|currentKey
parameter_list|,
name|nextKey
parameter_list|)
lambda|->
name|Longs
operator|.
name|fromByteArray
argument_list|(
name|currentKey
argument_list|)
operator|>
name|lastReadTxID
decl_stmt|;
name|MetadataKeyFilter
name|avoidInvalidTxid
init|=
parameter_list|(
name|preKey
parameter_list|,
name|currentKey
parameter_list|,
name|nextKey
parameter_list|)
lambda|->
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|LATEST_TXID
argument_list|,
name|currentKey
argument_list|)
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|deletedStore
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
if|if
condition|(
name|getNextTxID
operator|.
name|filterKey
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
literal|null
argument_list|)
operator|&&
name|avoidInvalidTxid
operator|.
name|filterKey
argument_list|(
literal|null
argument_list|,
name|key
argument_list|,
literal|null
argument_list|)
condition|)
block|{
name|DeletedBlocksTransaction
name|block
init|=
name|DeletedBlocksTransaction
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|block
operator|.
name|getCount
argument_list|()
operator|>
operator|-
literal|1
operator|&&
name|block
operator|.
name|getCount
argument_list|()
operator|<=
name|maxRetry
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
operator|.
name|size
argument_list|()
operator|<
name|count
return|;
block|}
argument_list|)
expr_stmt|;
comment|// Scan the metadata from the beginning.
if|if
condition|(
name|result
operator|.
name|size
argument_list|()
operator|<
name|count
operator|||
name|result
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
block|{
name|lastReadTxID
operator|=
literal|0
expr_stmt|;
block|}
else|else
block|{
name|lastReadTxID
operator|=
name|result
operator|.
name|get
argument_list|(
name|result
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|.
name|getTxID
argument_list|()
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
name|result
return|;
block|}
annotation|@
name|Override
DECL|method|getFailedTransactions ()
specifier|public
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|getFailedTransactions
parameter_list|()
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
specifier|final
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|failedTXs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
name|deletedStore
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
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|LATEST_TXID
argument_list|,
name|key
argument_list|)
condition|)
block|{
name|DeletedBlocksTransaction
name|delTX
init|=
name|DeletedBlocksTransaction
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|delTX
operator|.
name|getCount
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
name|failedTXs
operator|.
name|add
argument_list|(
name|delTX
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
argument_list|)
expr_stmt|;
return|return
name|failedTXs
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
comment|/**    * {@inheritDoc}    *    * @param txIDs - transaction ID.    * @throws IOException    */
annotation|@
name|Override
DECL|method|incrementCount (List<Long> txIDs)
specifier|public
name|void
name|incrementCount
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
parameter_list|)
throws|throws
name|IOException
block|{
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
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
name|Long
name|txID
range|:
name|txIDs
control|)
block|{
try|try
block|{
name|byte
index|[]
name|deleteBlockBytes
init|=
name|deletedStore
operator|.
name|get
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|txID
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|deleteBlockBytes
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Delete txID {} not found"
argument_list|,
name|txID
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|DeletedBlocksTransaction
name|block
init|=
name|DeletedBlocksTransaction
operator|.
name|parseFrom
argument_list|(
name|deleteBlockBytes
argument_list|)
decl_stmt|;
name|DeletedBlocksTransaction
operator|.
name|Builder
name|builder
init|=
name|block
operator|.
name|toBuilder
argument_list|()
decl_stmt|;
name|int
name|currentCount
init|=
name|block
operator|.
name|getCount
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentCount
operator|>
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|setCount
argument_list|(
operator|++
name|currentCount
argument_list|)
expr_stmt|;
block|}
comment|// if the retry time exceeds the maxRetry value
comment|// then set the retry value to -1, stop retrying, admins can
comment|// analyze those blocks and purge them manually by SCMCli.
if|if
condition|(
name|currentCount
operator|>
name|maxRetry
condition|)
block|{
name|builder
operator|.
name|setCount
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|deletedStore
operator|.
name|put
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|txID
argument_list|)
argument_list|,
name|builder
operator|.
name|build
argument_list|()
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"Cannot increase count for txID "
operator|+
name|txID
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
name|deletedStore
operator|.
name|writeBatch
argument_list|(
name|batch
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
DECL|method|constructNewTransaction (long txID, long containerID, List<Long> blocks)
specifier|private
name|DeletedBlocksTransaction
name|constructNewTransaction
parameter_list|(
name|long
name|txID
parameter_list|,
name|long
name|containerID
parameter_list|,
name|List
argument_list|<
name|Long
argument_list|>
name|blocks
parameter_list|)
block|{
return|return
name|DeletedBlocksTransaction
operator|.
name|newBuilder
argument_list|()
operator|.
name|setTxID
argument_list|(
name|txID
argument_list|)
operator|.
name|setContainerID
argument_list|(
name|containerID
argument_list|)
operator|.
name|addAllLocalID
argument_list|(
name|blocks
argument_list|)
operator|.
name|setCount
argument_list|(
literal|0
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
comment|/**    * {@inheritDoc}    *    * @param txIDs - transaction IDs.    * @throws IOException    */
annotation|@
name|Override
DECL|method|commitTransactions (List<Long> txIDs)
specifier|public
name|void
name|commitTransactions
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
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
for|for
control|(
name|Long
name|txID
range|:
name|txIDs
control|)
block|{
try|try
block|{
name|deletedStore
operator|.
name|delete
argument_list|(
name|Longs
operator|.
name|toByteArray
argument_list|(
name|txID
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Cannot commit txID "
operator|+
name|txID
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
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
comment|/**    * {@inheritDoc}    *    * @param containerID - container ID.    * @param blocks - blocks that belong to the same container.    * @throws IOException    */
annotation|@
name|Override
DECL|method|addTransaction (long containerID, List<Long> blocks)
specifier|public
name|void
name|addTransaction
parameter_list|(
name|long
name|containerID
parameter_list|,
name|List
argument_list|<
name|Long
argument_list|>
name|blocks
parameter_list|)
throws|throws
name|IOException
block|{
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|DeletedBlocksTransaction
name|tx
init|=
name|constructNewTransaction
argument_list|(
name|lastTxID
operator|+
literal|1
argument_list|,
name|containerID
argument_list|,
name|blocks
argument_list|)
decl_stmt|;
name|byte
index|[]
name|key
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|lastTxID
operator|+
literal|1
argument_list|)
decl_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|tx
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|LATEST_TXID
argument_list|,
name|Longs
operator|.
name|toByteArray
argument_list|(
name|lastTxID
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|deletedStore
operator|.
name|writeBatch
argument_list|(
name|batch
argument_list|)
expr_stmt|;
name|lastTxID
operator|+=
literal|1
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
DECL|method|getNumOfValidTransactions ()
specifier|public
name|int
name|getNumOfValidTransactions
parameter_list|()
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
specifier|final
name|AtomicInteger
name|num
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|deletedStore
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
comment|// Exclude latest txid record
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|LATEST_TXID
argument_list|,
name|key
argument_list|)
condition|)
block|{
name|DeletedBlocksTransaction
name|delTX
init|=
name|DeletedBlocksTransaction
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|delTX
operator|.
name|getCount
argument_list|()
operator|>
operator|-
literal|1
condition|)
block|{
name|num
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
return|return
literal|true
return|;
block|}
argument_list|)
expr_stmt|;
return|return
name|num
operator|.
name|get
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
comment|/**    * {@inheritDoc}    *    * @param containerBlocksMap a map of containerBlocks.    * @throws IOException    */
annotation|@
name|Override
DECL|method|addTransactions (Map<Long, List<Long>> containerBlocksMap)
specifier|public
name|void
name|addTransactions
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|containerBlocksMap
parameter_list|)
throws|throws
name|IOException
block|{
name|BatchOperation
name|batch
init|=
operator|new
name|BatchOperation
argument_list|()
decl_stmt|;
name|lock
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|long
name|currentLatestID
init|=
name|lastTxID
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|entry
range|:
name|containerBlocksMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|currentLatestID
operator|+=
literal|1
expr_stmt|;
name|byte
index|[]
name|key
init|=
name|Longs
operator|.
name|toByteArray
argument_list|(
name|currentLatestID
argument_list|)
decl_stmt|;
name|DeletedBlocksTransaction
name|tx
init|=
name|constructNewTransaction
argument_list|(
name|currentLatestID
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|tx
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|lastTxID
operator|=
name|currentLatestID
expr_stmt|;
name|batch
operator|.
name|put
argument_list|(
name|LATEST_TXID
argument_list|,
name|Longs
operator|.
name|toByteArray
argument_list|(
name|lastTxID
argument_list|)
argument_list|)
expr_stmt|;
name|deletedStore
operator|.
name|writeBatch
argument_list|(
name|batch
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
name|deletedStore
operator|!=
literal|null
condition|)
block|{
name|deletedStore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getTransactions (DatanodeDeletedBlockTransactions transactions)
specifier|public
name|void
name|getTransactions
parameter_list|(
name|DatanodeDeletedBlockTransactions
name|transactions
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
name|deletedStore
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
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|LATEST_TXID
argument_list|,
name|key
argument_list|)
condition|)
block|{
name|DeletedBlocksTransaction
name|block
init|=
name|DeletedBlocksTransaction
operator|.
name|parseFrom
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|block
operator|.
name|getCount
argument_list|()
operator|>
operator|-
literal|1
operator|&&
name|block
operator|.
name|getCount
argument_list|()
operator|<=
name|maxRetry
condition|)
block|{
name|transactions
operator|.
name|addTransaction
argument_list|(
name|block
argument_list|)
expr_stmt|;
block|}
return|return
operator|!
name|transactions
operator|.
name|isFull
argument_list|()
return|;
block|}
return|return
literal|true
return|;
block|}
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
block|}
end_class

end_unit

