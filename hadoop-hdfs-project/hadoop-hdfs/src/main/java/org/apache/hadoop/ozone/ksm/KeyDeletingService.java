begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.ksm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
package|;
end_package

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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|ksm
operator|.
name|KSMConfigKeys
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
name|ksm
operator|.
name|KSMConfigKeys
operator|.
name|OZONE_KEY_DELETING_LIMIT_PER_TASK_DEFAULT
import|;
end_import

begin_comment
comment|/**  * This is the background service to delete keys.  * Scan the metadata of ksm periodically to get  * the keys with prefix "#deleting" and ask scm to  * delete metadata accordingly, if scm returns  * success for keys, then clean up those keys.  */
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
DECL|method|KeyDeletingService (ScmBlockLocationProtocol scmClient, KeyManager manager, int serviceInterval, long serviceTimeout, Configuration conf)
specifier|public
name|KeyDeletingService
parameter_list|(
name|ScmBlockLocationProtocol
name|scmClient
parameter_list|,
name|KeyManager
name|manager
parameter_list|,
name|int
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
comment|/**    * A key deleting task scans KSM DB and looking for a certain number    * of pending-deletion keys, sends these keys along with their associated    * blocks to SCM for deletion. Once SCM confirms keys are deleted (once    * SCM persisted the blocks info in its deletedBlockLog), it removes    * these keys from the DB.    */
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
try|try
block|{
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
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Found {} to-delete keys in KSM"
argument_list|,
name|keyBlocksList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
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
try|try
block|{
comment|// Purge key from KSM DB.
name|manager
operator|.
name|deletePendingDeletionKey
argument_list|(
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Key {} deleted from KSM DB"
argument_list|,
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// if a pending deletion key is failed to delete,
comment|// print a warning here and retain it in this state,
comment|// so that it can be attempt to delete next time.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to delete pending-deletion key {}"
argument_list|,
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// Key deletion failed, retry in next interval.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Key {} deletion failed because some of the blocks"
operator|+
literal|" were failed to delete, failed blocks: {}"
argument_list|,
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|,
name|String
operator|.
name|join
argument_list|(
literal|","
argument_list|,
name|result
operator|.
name|getFailedBlocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|results
operator|::
name|size
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No pending deletion key found in KSM"
argument_list|)
expr_stmt|;
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
literal|"Unable to get pending deletion keys, retry in"
operator|+
literal|" next interval"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|EmptyTaskResult
operator|.
name|newResult
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

