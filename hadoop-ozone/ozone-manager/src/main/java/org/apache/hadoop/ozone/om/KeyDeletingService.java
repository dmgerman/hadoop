begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.om
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|om
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
name|hdds
operator|.
name|scm
operator|.
name|protocol
operator|.
name|ScmBlockLocationProtocol
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
name|common
operator|.
name|BlockGroup
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
name|common
operator|.
name|DeleteBlockGroupResult
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|BackgroundService
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
name|BackgroundTask
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
name|BackgroundTaskQueue
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
name|BackgroundTaskResult
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
name|BackgroundTaskResult
operator|.
name|EmptyTaskResult
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
name|db
operator|.
name|Table
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|RocksDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|rocksdb
operator|.
name|WriteBatch
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
name|IOException
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
name|TimeUnit
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
name|AtomicLong
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
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_KEY_DELETING_LIMIT_PER_TASK
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
name|om
operator|.
name|OMConfigKeys
operator|.
name|OZONE_KEY_DELETING_LIMIT_PER_TASK_DEFAULT
import|;
end_import

begin_comment
comment|/**  * This is the background service to delete keys. Scan the metadata of om  * periodically to get the keys from DeletedTable and ask scm to delete  * metadata accordingly, if scm returns success for keys, then clean up those  * keys.  */
end_comment

begin_class
DECL|class|KeyDeletingService
specifier|public
class|class
name|KeyDeletingService
extends|extends
name|BackgroundService
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
name|KeyDeletingService
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// The thread pool size for key deleting service.
DECL|field|KEY_DELETING_CORE_POOL_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|KEY_DELETING_CORE_POOL_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|scmClient
specifier|private
specifier|final
name|ScmBlockLocationProtocol
name|scmClient
decl_stmt|;
DECL|field|manager
specifier|private
specifier|final
name|KeyManager
name|manager
decl_stmt|;
DECL|field|keyLimitPerTask
specifier|private
specifier|final
name|int
name|keyLimitPerTask
decl_stmt|;
DECL|field|deletedKeyCount
specifier|private
specifier|final
name|AtomicLong
name|deletedKeyCount
decl_stmt|;
DECL|field|runCount
specifier|private
specifier|final
name|AtomicLong
name|runCount
decl_stmt|;
DECL|method|KeyDeletingService (ScmBlockLocationProtocol scmClient, KeyManager manager, long serviceInterval, long serviceTimeout, Configuration conf)
specifier|public
name|KeyDeletingService
parameter_list|(
name|ScmBlockLocationProtocol
name|scmClient
parameter_list|,
name|KeyManager
name|manager
parameter_list|,
name|long
name|serviceInterval
parameter_list|,
name|long
name|serviceTimeout
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
literal|"KeyDeletingService"
argument_list|,
name|serviceInterval
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|KEY_DELETING_CORE_POOL_SIZE
argument_list|,
name|serviceTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|scmClient
operator|=
name|scmClient
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|keyLimitPerTask
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|OZONE_KEY_DELETING_LIMIT_PER_TASK
argument_list|,
name|OZONE_KEY_DELETING_LIMIT_PER_TASK_DEFAULT
argument_list|)
expr_stmt|;
name|this
operator|.
name|deletedKeyCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|runCount
operator|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Returns the number of times this Background service has run.    *    * @return Long, run count.    */
annotation|@
name|VisibleForTesting
DECL|method|getRunCount ()
specifier|public
name|AtomicLong
name|getRunCount
parameter_list|()
block|{
return|return
name|runCount
return|;
block|}
comment|/**    * Returns the number of keys deleted by the background service.    *    * @return Long count.    */
annotation|@
name|VisibleForTesting
DECL|method|getDeletedKeyCount ()
specifier|public
name|AtomicLong
name|getDeletedKeyCount
parameter_list|()
block|{
return|return
name|deletedKeyCount
return|;
block|}
annotation|@
name|Override
DECL|method|getTasks ()
specifier|public
name|BackgroundTaskQueue
name|getTasks
parameter_list|()
block|{
name|BackgroundTaskQueue
name|queue
init|=
operator|new
name|BackgroundTaskQueue
argument_list|()
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|KeyDeletingTask
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
block|}
comment|/**    * A key deleting task scans OM DB and looking for a certain number of    * pending-deletion keys, sends these keys along with their associated blocks    * to SCM for deletion. Once SCM confirms keys are deleted (once SCM persisted    * the blocks info in its deletedBlockLog), it removes these keys from the    * DB.    */
DECL|class|KeyDeletingTask
specifier|private
class|class
name|KeyDeletingTask
implements|implements
name|BackgroundTask
argument_list|<
name|BackgroundTaskResult
argument_list|>
block|{
annotation|@
name|Override
DECL|method|getPriority ()
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|BackgroundTaskResult
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|runCount
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
try|try
block|{
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|BlockGroup
argument_list|>
name|keyBlocksList
init|=
name|manager
operator|.
name|getPendingDeletionKeys
argument_list|(
name|keyLimitPerTask
argument_list|)
decl_stmt|;
if|if
condition|(
name|keyBlocksList
operator|!=
literal|null
operator|&&
name|keyBlocksList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|results
init|=
name|scmClient
operator|.
name|deleteKeyBlocks
argument_list|(
name|keyBlocksList
argument_list|)
decl_stmt|;
if|if
condition|(
name|results
operator|!=
literal|null
condition|)
block|{
name|int
name|delCount
init|=
name|deleteAllKeys
argument_list|(
name|results
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Number of keys deleted: {}, elapsed time: {}ms"
argument_list|,
name|delCount
argument_list|,
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|deletedKeyCount
operator|.
name|addAndGet
argument_list|(
name|delCount
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
literal|"Error while running delete keys background task. Will "
operator|+
literal|"retry at next run."
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|// By desing, no one cares about the results of this call back.
return|return
name|EmptyTaskResult
operator|.
name|newResult
argument_list|()
return|;
block|}
comment|/**      * Deletes all the keys that SCM has acknowledged and queued for delete.      *      * @param results DeleteBlockGroups returned by SCM.      * @throws RocksDBException on Error.      * @throws IOException      on Error      */
DECL|method|deleteAllKeys (List<DeleteBlockGroupResult> results)
specifier|private
name|int
name|deleteAllKeys
parameter_list|(
name|List
argument_list|<
name|DeleteBlockGroupResult
argument_list|>
name|results
parameter_list|)
throws|throws
name|RocksDBException
throws|,
name|IOException
block|{
name|Table
name|deletedTable
init|=
name|manager
operator|.
name|getMetadataManager
argument_list|()
operator|.
name|getDeletedTable
argument_list|()
decl_stmt|;
comment|// Put all keys to delete in a single transaction and call for delete.
name|int
name|deletedCount
init|=
literal|0
decl_stmt|;
try|try
init|(
name|WriteBatch
name|writeBatch
init|=
operator|new
name|WriteBatch
argument_list|()
init|)
block|{
for|for
control|(
name|DeleteBlockGroupResult
name|result
range|:
name|results
control|)
block|{
if|if
condition|(
name|result
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
comment|// Purge key from OM DB.
name|writeBatch
operator|.
name|delete
argument_list|(
name|deletedTable
operator|.
name|getHandle
argument_list|()
argument_list|,
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Key {} deleted from OM DB"
argument_list|,
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
expr_stmt|;
name|deletedCount
operator|++
expr_stmt|;
block|}
block|}
comment|// Write a single transaction for delete.
name|manager
operator|.
name|getMetadataManager
argument_list|()
operator|.
name|getStore
argument_list|()
operator|.
name|write
argument_list|(
name|writeBatch
argument_list|)
expr_stmt|;
block|}
return|return
name|deletedCount
return|;
block|}
block|}
block|}
end_class

end_unit

