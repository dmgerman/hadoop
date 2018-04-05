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

begin_comment
comment|/**  * This is the background service to delete hanging open keys.  * Scan the metadata of ksm periodically to get  * the keys with prefix "#open#" and ask scm to  * delete metadata accordingly, if scm returns  * success for keys, then clean up those keys.  */
end_comment

begin_class
DECL|class|OpenKeyCleanupService
specifier|public
class|class
name|OpenKeyCleanupService
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
name|OpenKeyCleanupService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|OPEN_KEY_DELETING_CORE_POOL_SIZE
specifier|private
specifier|final
specifier|static
name|int
name|OPEN_KEY_DELETING_CORE_POOL_SIZE
init|=
literal|2
decl_stmt|;
DECL|field|keyManager
specifier|private
specifier|final
name|KeyManager
name|keyManager
decl_stmt|;
DECL|field|scmClient
specifier|private
specifier|final
name|ScmBlockLocationProtocol
name|scmClient
decl_stmt|;
DECL|method|OpenKeyCleanupService (ScmBlockLocationProtocol scmClient, KeyManager keyManager, int serviceInterval, long serviceTimeout)
specifier|public
name|OpenKeyCleanupService
parameter_list|(
name|ScmBlockLocationProtocol
name|scmClient
parameter_list|,
name|KeyManager
name|keyManager
parameter_list|,
name|int
name|serviceInterval
parameter_list|,
name|long
name|serviceTimeout
parameter_list|)
block|{
name|super
argument_list|(
literal|"OpenKeyCleanupService"
argument_list|,
name|serviceInterval
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|,
name|OPEN_KEY_DELETING_CORE_POOL_SIZE
argument_list|,
name|serviceTimeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyManager
operator|=
name|keyManager
expr_stmt|;
name|this
operator|.
name|scmClient
operator|=
name|scmClient
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
name|OpenKeyDeletingTask
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|queue
return|;
block|}
DECL|class|OpenKeyDeletingTask
specifier|private
class|class
name|OpenKeyDeletingTask
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
name|keyManager
operator|.
name|getExpiredOpenKeys
argument_list|()
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
name|int
name|toDeleteSize
init|=
name|keyBlocksList
operator|.
name|size
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found {} to-delete open keys in KSM"
argument_list|,
name|toDeleteSize
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
name|int
name|deletedSize
init|=
literal|0
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
name|keyManager
operator|.
name|deleteExpiredOpenKey
argument_list|(
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Key {} deleted from KSM DB"
argument_list|,
name|result
operator|.
name|getObjectKey
argument_list|()
argument_list|)
expr_stmt|;
name|deletedSize
operator|+=
literal|1
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
name|warn
argument_list|(
literal|"Failed to delete hanging-open key {}"
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
name|LOG
operator|.
name|warn
argument_list|(
literal|"Deleting open Key {} failed because some of the blocks"
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Found {} expired open key entries, successfully "
operator|+
literal|"cleaned up {} entries"
argument_list|,
name|toDeleteSize
argument_list|,
name|deletedSize
argument_list|)
expr_stmt|;
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
literal|"No hanging open key fond in KSM"
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
literal|"Unable to get hanging open keys, retry in"
operator|+
literal|" next interval"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|BackgroundTaskResult
operator|.
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

