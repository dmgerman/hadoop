begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.uam
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|uam
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
name|HashSet
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
name|ConcurrentHashMap
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
name|ExecutorCompletionService
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
name|Executors
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Unstable
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
name|security
operator|.
name|UserGroupInformation
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|service
operator|.
name|AbstractService
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
name|yarn
operator|.
name|api
operator|.
name|ApplicationClientProtocol
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|FinishApplicationMasterRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|FinishApplicationMasterResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|GetNewApplicationResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|KillApplicationResponse
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterApplicationMasterRequest
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|RegisterApplicationMasterResponse
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationId
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ApplicationSubmissionContext
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
name|yarn
operator|.
name|client
operator|.
name|AMRMClientUtils
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
name|yarn
operator|.
name|exceptions
operator|.
name|YarnException
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
name|yarn
operator|.
name|security
operator|.
name|AMRMTokenIdentifier
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
name|yarn
operator|.
name|server
operator|.
name|AMRMClientRelayer
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
name|yarn
operator|.
name|util
operator|.
name|AsyncCallback
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
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * A service that manages a pool of UAM managers in  * {@link UnmanagedApplicationManager}.  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|UnmanagedAMPoolManager
specifier|public
class|class
name|UnmanagedAMPoolManager
extends|extends
name|AbstractService
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|UnmanagedAMPoolManager
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Map from uamId to UAM instances
DECL|field|unmanagedAppMasterMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|UnmanagedApplicationManager
argument_list|>
name|unmanagedAppMasterMap
decl_stmt|;
DECL|field|appIdMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ApplicationId
argument_list|>
name|appIdMap
decl_stmt|;
DECL|field|threadpool
specifier|private
name|ExecutorService
name|threadpool
decl_stmt|;
DECL|method|UnmanagedAMPoolManager (ExecutorService threadpool)
specifier|public
name|UnmanagedAMPoolManager
parameter_list|(
name|ExecutorService
name|threadpool
parameter_list|)
block|{
name|super
argument_list|(
name|UnmanagedAMPoolManager
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|threadpool
operator|=
name|threadpool
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|this
operator|.
name|threadpool
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|threadpool
operator|=
name|Executors
operator|.
name|newCachedThreadPool
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|unmanagedAppMasterMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|appIdMap
operator|=
operator|new
name|ConcurrentHashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
comment|/**    * Normally we should finish all applications before stop. If there are still    * UAMs running, force kill all of them. Do parallel kill because of    * performance reasons.    *    * TODO: move waiting for the kill to finish into a separate thread, without    * blocking the serviceStop.    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|ExecutorCompletionService
argument_list|<
name|KillApplicationResponse
argument_list|>
name|completionService
init|=
operator|new
name|ExecutorCompletionService
argument_list|<>
argument_list|(
name|this
operator|.
name|threadpool
argument_list|)
decl_stmt|;
if|if
condition|(
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
comment|// Save a local copy of the key set so that it won't change with the map
name|Set
argument_list|<
name|String
argument_list|>
name|addressList
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|keySet
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Abnormal shutdown of UAMPoolManager, still {} UAMs in map"
argument_list|,
name|addressList
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|uamId
range|:
name|addressList
control|)
block|{
name|completionService
operator|.
name|submit
argument_list|(
operator|new
name|Callable
argument_list|<
name|KillApplicationResponse
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|KillApplicationResponse
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Force-killing UAM id "
operator|+
name|uamId
operator|+
literal|" for application "
operator|+
name|appIdMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|unmanagedAppMasterMap
operator|.
name|remove
argument_list|(
name|uamId
argument_list|)
operator|.
name|forceKillApplication
argument_list|()
return|;
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
literal|"Failed to kill unmanaged application master"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|addressList
operator|.
name|size
argument_list|()
condition|;
operator|++
name|i
control|)
block|{
try|try
block|{
name|Future
argument_list|<
name|KillApplicationResponse
argument_list|>
name|future
init|=
name|completionService
operator|.
name|take
argument_list|()
decl_stmt|;
name|future
operator|.
name|get
argument_list|()
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
name|error
argument_list|(
literal|"Failed to kill unmanaged application master"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|this
operator|.
name|appIdMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a new UAM and register the application, without specifying uamId and    * appId. We will ask for an appId from RM and use it as the uamId.    *    * @param registerRequest RegisterApplicationMasterRequest    * @param conf configuration for this UAM    * @param queueName queue of the application    * @param submitter submitter name of the UAM    * @param appNameSuffix application name suffix for the UAM    * @param keepContainersAcrossApplicationAttempts keep container flag for UAM    *          recovery.    * @param rmName name of the YarnRM    * @see ApplicationSubmissionContext    *          #setKeepContainersAcrossApplicationAttempts(boolean)    * @return uamId for the UAM    * @throws YarnException if registerApplicationMaster fails    * @throws IOException if registerApplicationMaster fails    */
DECL|method|createAndRegisterNewUAM ( RegisterApplicationMasterRequest registerRequest, Configuration conf, String queueName, String submitter, String appNameSuffix, boolean keepContainersAcrossApplicationAttempts, String rmName)
specifier|public
name|String
name|createAndRegisterNewUAM
parameter_list|(
name|RegisterApplicationMasterRequest
name|registerRequest
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|queueName
parameter_list|,
name|String
name|submitter
parameter_list|,
name|String
name|appNameSuffix
parameter_list|,
name|boolean
name|keepContainersAcrossApplicationAttempts
parameter_list|,
name|String
name|rmName
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|ApplicationId
name|appId
init|=
literal|null
decl_stmt|;
name|ApplicationClientProtocol
name|rmClient
decl_stmt|;
try|try
block|{
name|UserGroupInformation
name|appSubmitter
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|submitter
argument_list|)
decl_stmt|;
name|rmClient
operator|=
name|AMRMClientUtils
operator|.
name|createRMProxy
argument_list|(
name|conf
argument_list|,
name|ApplicationClientProtocol
operator|.
name|class
argument_list|,
name|appSubmitter
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|// Get a new appId from RM
name|GetNewApplicationResponse
name|response
init|=
name|rmClient
operator|.
name|getNewApplication
argument_list|(
name|GetNewApplicationRequest
operator|.
name|newInstance
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"getNewApplication got null response"
argument_list|)
throw|;
block|}
name|appId
operator|=
name|response
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Received new application ID {} from RM"
argument_list|,
name|appId
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|rmClient
operator|=
literal|null
expr_stmt|;
block|}
comment|// Launch the UAM in RM
name|launchUAM
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|conf
argument_list|,
name|appId
argument_list|,
name|queueName
argument_list|,
name|submitter
argument_list|,
name|appNameSuffix
argument_list|,
name|keepContainersAcrossApplicationAttempts
argument_list|,
name|rmName
argument_list|)
expr_stmt|;
comment|// Register the UAM application
name|registerApplicationMaster
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|registerRequest
argument_list|)
expr_stmt|;
comment|// Returns the appId as uamId
return|return
name|appId
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Launch a new UAM, using the provided uamId and appId.    *    * @param uamId uam Id    * @param conf configuration for this UAM    * @param appId application id for the UAM    * @param queueName queue of the application    * @param submitter submitter name of the UAM    * @param appNameSuffix application name suffix for the UAM    * @param keepContainersAcrossApplicationAttempts keep container flag for UAM    *          recovery.    * @param rmName name of the YarnRM    * @see ApplicationSubmissionContext    *          #setKeepContainersAcrossApplicationAttempts(boolean)    * @return UAM token    * @throws YarnException if fails    * @throws IOException if fails    */
DECL|method|launchUAM (String uamId, Configuration conf, ApplicationId appId, String queueName, String submitter, String appNameSuffix, boolean keepContainersAcrossApplicationAttempts, String rmName)
specifier|public
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|launchUAM
parameter_list|(
name|String
name|uamId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|queueName
parameter_list|,
name|String
name|submitter
parameter_list|,
name|String
name|appNameSuffix
parameter_list|,
name|boolean
name|keepContainersAcrossApplicationAttempts
parameter_list|,
name|String
name|rmName
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
name|UnmanagedApplicationManager
name|uam
init|=
name|createUAM
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|queueName
argument_list|,
name|submitter
argument_list|,
name|appNameSuffix
argument_list|,
name|keepContainersAcrossApplicationAttempts
argument_list|,
name|rmName
argument_list|)
decl_stmt|;
comment|// Put the UAM into map first before initializing it to avoid additional UAM
comment|// for the same uamId being created concurrently
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|put
argument_list|(
name|uamId
argument_list|,
name|uam
argument_list|)
expr_stmt|;
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|amrmToken
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Launching UAM id {} for application {}"
argument_list|,
name|uamId
argument_list|,
name|appId
argument_list|)
expr_stmt|;
name|amrmToken
operator|=
name|uam
operator|.
name|launchUAM
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Add the map earlier and remove here if register failed because we want
comment|// to make sure there is only one uam instance per uamId at any given time
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|remove
argument_list|(
name|uamId
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|this
operator|.
name|appIdMap
operator|.
name|put
argument_list|(
name|uamId
argument_list|,
name|uam
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|amrmToken
return|;
block|}
comment|/**    * Re-attach to an existing UAM, using the provided uamIdentifier.    *    * @param uamId uam Id    * @param conf configuration for this UAM    * @param appId application id for the UAM    * @param queueName queue of the application    * @param submitter submitter name of the UAM    * @param appNameSuffix application name suffix for the UAM    * @param uamToken UAM token    * @param rmName name of the YarnRM    * @throws YarnException if fails    * @throws IOException if fails    */
DECL|method|reAttachUAM (String uamId, Configuration conf, ApplicationId appId, String queueName, String submitter, String appNameSuffix, Token<AMRMTokenIdentifier> uamToken, String rmName)
specifier|public
name|void
name|reAttachUAM
parameter_list|(
name|String
name|uamId
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|queueName
parameter_list|,
name|String
name|submitter
parameter_list|,
name|String
name|appNameSuffix
parameter_list|,
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|uamToken
parameter_list|,
name|String
name|rmName
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" already exists"
argument_list|)
throw|;
block|}
name|UnmanagedApplicationManager
name|uam
init|=
name|createUAM
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|queueName
argument_list|,
name|submitter
argument_list|,
name|appNameSuffix
argument_list|,
literal|true
argument_list|,
name|rmName
argument_list|)
decl_stmt|;
comment|// Put the UAM into map first before initializing it to avoid additional UAM
comment|// for the same uamId being created concurrently
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|put
argument_list|(
name|uamId
argument_list|,
name|uam
argument_list|)
expr_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Reattaching UAM id {} for application {}"
argument_list|,
name|uamId
argument_list|,
name|appId
argument_list|)
expr_stmt|;
name|uam
operator|.
name|reAttachUAM
argument_list|(
name|uamToken
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// Add the map earlier and remove here if register failed because we want
comment|// to make sure there is only one uam instance per uamId at any given time
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|remove
argument_list|(
name|uamId
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|this
operator|.
name|appIdMap
operator|.
name|put
argument_list|(
name|uamId
argument_list|,
name|uam
operator|.
name|getAppId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates the UAM instance. Pull out to make unit test easy.    *    * @param conf Configuration    * @param appId application id    * @param queueName queue of the application    * @param submitter submitter name of the application    * @param appNameSuffix application name suffix    * @param keepContainersAcrossApplicationAttempts keep container flag for UAM    * @param rmName name of the YarnRM    * @return the UAM instance    */
annotation|@
name|VisibleForTesting
DECL|method|createUAM (Configuration conf, ApplicationId appId, String queueName, String submitter, String appNameSuffix, boolean keepContainersAcrossApplicationAttempts, String rmName)
specifier|protected
name|UnmanagedApplicationManager
name|createUAM
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|String
name|queueName
parameter_list|,
name|String
name|submitter
parameter_list|,
name|String
name|appNameSuffix
parameter_list|,
name|boolean
name|keepContainersAcrossApplicationAttempts
parameter_list|,
name|String
name|rmName
parameter_list|)
block|{
return|return
operator|new
name|UnmanagedApplicationManager
argument_list|(
name|conf
argument_list|,
name|appId
argument_list|,
name|queueName
argument_list|,
name|submitter
argument_list|,
name|appNameSuffix
argument_list|,
name|keepContainersAcrossApplicationAttempts
argument_list|,
name|rmName
argument_list|)
return|;
block|}
comment|/**    * Register application master for the UAM.    *    * @param uamId uam Id    * @param registerRequest RegisterApplicationMasterRequest    * @return register response    * @throws YarnException if register fails    * @throws IOException if register fails    */
DECL|method|registerApplicationMaster ( String uamId, RegisterApplicationMasterRequest registerRequest)
specifier|public
name|RegisterApplicationMasterResponse
name|registerApplicationMaster
parameter_list|(
name|String
name|uamId
parameter_list|,
name|RegisterApplicationMasterRequest
name|registerRequest
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Registering UAM id {} for application {}"
argument_list|,
name|uamId
argument_list|,
name|this
operator|.
name|appIdMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
operator|.
name|registerApplicationMaster
argument_list|(
name|registerRequest
argument_list|)
return|;
block|}
comment|/**    * AllocateAsync to an UAM.    *    * @param uamId uam Id    * @param request AllocateRequest    * @param callback callback for response    * @throws YarnException if allocate fails    * @throws IOException if allocate fails    */
DECL|method|allocateAsync (String uamId, AllocateRequest request, AsyncCallback<AllocateResponse> callback)
specifier|public
name|void
name|allocateAsync
parameter_list|(
name|String
name|uamId
parameter_list|,
name|AllocateRequest
name|request
parameter_list|,
name|AsyncCallback
argument_list|<
name|AllocateResponse
argument_list|>
name|callback
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
operator|.
name|allocateAsync
argument_list|(
name|request
argument_list|,
name|callback
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish an UAM/application.    *    * @param uamId uam Id    * @param request FinishApplicationMasterRequest    * @return FinishApplicationMasterResponse    * @throws YarnException if finishApplicationMaster call fails    * @throws IOException if finishApplicationMaster call fails    */
DECL|method|finishApplicationMaster (String uamId, FinishApplicationMasterRequest request)
specifier|public
name|FinishApplicationMasterResponse
name|finishApplicationMaster
parameter_list|(
name|String
name|uamId
parameter_list|,
name|FinishApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing UAM id {} for application {}"
argument_list|,
name|uamId
argument_list|,
name|this
operator|.
name|appIdMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
argument_list|)
expr_stmt|;
name|FinishApplicationMasterResponse
name|response
init|=
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|response
operator|.
name|getIsUnregistered
argument_list|()
condition|)
block|{
comment|// Only remove the UAM when the unregister finished
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|remove
argument_list|(
name|uamId
argument_list|)
expr_stmt|;
name|this
operator|.
name|appIdMap
operator|.
name|remove
argument_list|(
name|uamId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"UAM id {} is unregistered"
argument_list|,
name|uamId
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
comment|/**    * Shutdown an UAM client without killing it in YarnRM.    *    * @param uamId uam Id    * @throws YarnException if fails    */
DECL|method|shutDownConnections (String uamId)
specifier|public
name|void
name|shutDownConnections
parameter_list|(
name|String
name|uamId
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Shutting down UAM id {} for application {} without killing the UAM"
argument_list|,
name|uamId
argument_list|,
name|this
operator|.
name|appIdMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|remove
argument_list|(
name|uamId
argument_list|)
operator|.
name|shutDownConnections
argument_list|()
expr_stmt|;
block|}
comment|/**    * Shutdown all UAM clients without killing them in YarnRM.    *    * @throws YarnException if fails    */
DECL|method|shutDownConnections ()
specifier|public
name|void
name|shutDownConnections
parameter_list|()
throws|throws
name|YarnException
block|{
for|for
control|(
name|String
name|uamId
range|:
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|shutDownConnections
argument_list|(
name|uamId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Get the id of all running UAMs.    *    * @return uamId set    */
DECL|method|getAllUAMIds ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAllUAMIds
parameter_list|()
block|{
comment|// Return a clone of the current id set for concurrency reasons, so that the
comment|// returned map won't change with the actual map
return|return
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|keySet
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Return whether an UAM exists.    *    * @param uamId uam Id    * @return UAM exists or not    */
DECL|method|hasUAMId (String uamId)
specifier|public
name|boolean
name|hasUAMId
parameter_list|(
name|String
name|uamId
parameter_list|)
block|{
return|return
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
return|;
block|}
comment|/**    * Return the rmProxy relayer of an UAM.    *    * @param uamId uam Id    * @return the rmProxy relayer    * @throws YarnException if fails    */
DECL|method|getAMRMClientRelayer (String uamId)
specifier|public
name|AMRMClientRelayer
name|getAMRMClientRelayer
parameter_list|(
name|String
name|uamId
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
operator|.
name|getAMRMClientRelayer
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|getRequestQueueSize (String uamId)
specifier|public
name|int
name|getRequestQueueSize
parameter_list|(
name|String
name|uamId
parameter_list|)
throws|throws
name|YarnException
block|{
if|if
condition|(
operator|!
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|containsKey
argument_list|(
name|uamId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"UAM "
operator|+
name|uamId
operator|+
literal|" does not exist"
argument_list|)
throw|;
block|}
return|return
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|get
argument_list|(
name|uamId
argument_list|)
operator|.
name|getRequestQueueSize
argument_list|()
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|drainUAMHeartbeats ()
specifier|public
name|void
name|drainUAMHeartbeats
parameter_list|()
block|{
for|for
control|(
name|UnmanagedApplicationManager
name|uam
range|:
name|this
operator|.
name|unmanagedAppMasterMap
operator|.
name|values
argument_list|()
control|)
block|{
name|uam
operator|.
name|drainHeartbeatThread
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

