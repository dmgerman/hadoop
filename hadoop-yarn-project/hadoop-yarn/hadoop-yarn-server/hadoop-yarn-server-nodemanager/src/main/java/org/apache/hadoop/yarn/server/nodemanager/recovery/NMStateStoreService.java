begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.recovery
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
name|nodemanager
operator|.
name|recovery
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
name|Private
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
name|fs
operator|.
name|Path
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
name|protocolrecords
operator|.
name|StartContainerRequest
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
name|ApplicationAttemptId
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
name|ContainerExitStatus
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
name|ContainerId
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
name|proto
operator|.
name|YarnProtos
operator|.
name|LocalResourceProto
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|ContainerManagerApplicationProto
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|DeletionServiceDeleteTaskProto
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|LocalizedResourceProto
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
name|api
operator|.
name|records
operator|.
name|MasterKey
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|NMStateStoreService
specifier|public
specifier|abstract
class|class
name|NMStateStoreService
extends|extends
name|AbstractService
block|{
DECL|method|NMStateStoreService (String name)
specifier|public
name|NMStateStoreService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
DECL|class|RecoveredApplicationsState
specifier|public
specifier|static
class|class
name|RecoveredApplicationsState
block|{
DECL|field|applications
name|List
argument_list|<
name|ContainerManagerApplicationProto
argument_list|>
name|applications
decl_stmt|;
DECL|field|finishedApplications
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|finishedApplications
decl_stmt|;
DECL|method|getApplications ()
specifier|public
name|List
argument_list|<
name|ContainerManagerApplicationProto
argument_list|>
name|getApplications
parameter_list|()
block|{
return|return
name|applications
return|;
block|}
DECL|method|getFinishedApplications ()
specifier|public
name|List
argument_list|<
name|ApplicationId
argument_list|>
name|getFinishedApplications
parameter_list|()
block|{
return|return
name|finishedApplications
return|;
block|}
block|}
DECL|enum|RecoveredContainerStatus
specifier|public
enum|enum
name|RecoveredContainerStatus
block|{
DECL|enumConstant|REQUESTED
name|REQUESTED
block|,
DECL|enumConstant|LAUNCHED
name|LAUNCHED
block|,
DECL|enumConstant|COMPLETED
name|COMPLETED
block|}
DECL|class|RecoveredContainerState
specifier|public
specifier|static
class|class
name|RecoveredContainerState
block|{
DECL|field|status
name|RecoveredContainerStatus
name|status
decl_stmt|;
DECL|field|exitCode
name|int
name|exitCode
init|=
name|ContainerExitStatus
operator|.
name|INVALID
decl_stmt|;
DECL|field|killed
name|boolean
name|killed
init|=
literal|false
decl_stmt|;
DECL|field|diagnostics
name|String
name|diagnostics
init|=
literal|""
decl_stmt|;
DECL|field|startRequest
name|StartContainerRequest
name|startRequest
decl_stmt|;
DECL|method|getStatus ()
specifier|public
name|RecoveredContainerStatus
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
DECL|method|getExitCode ()
specifier|public
name|int
name|getExitCode
parameter_list|()
block|{
return|return
name|exitCode
return|;
block|}
DECL|method|getKilled ()
specifier|public
name|boolean
name|getKilled
parameter_list|()
block|{
return|return
name|killed
return|;
block|}
DECL|method|getDiagnostics ()
specifier|public
name|String
name|getDiagnostics
parameter_list|()
block|{
return|return
name|diagnostics
return|;
block|}
DECL|method|getStartRequest ()
specifier|public
name|StartContainerRequest
name|getStartRequest
parameter_list|()
block|{
return|return
name|startRequest
return|;
block|}
block|}
DECL|class|LocalResourceTrackerState
specifier|public
specifier|static
class|class
name|LocalResourceTrackerState
block|{
DECL|field|localizedResources
name|List
argument_list|<
name|LocalizedResourceProto
argument_list|>
name|localizedResources
init|=
operator|new
name|ArrayList
argument_list|<
name|LocalizedResourceProto
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|inProgressResources
name|Map
argument_list|<
name|LocalResourceProto
argument_list|,
name|Path
argument_list|>
name|inProgressResources
init|=
operator|new
name|HashMap
argument_list|<
name|LocalResourceProto
argument_list|,
name|Path
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getLocalizedResources ()
specifier|public
name|List
argument_list|<
name|LocalizedResourceProto
argument_list|>
name|getLocalizedResources
parameter_list|()
block|{
return|return
name|localizedResources
return|;
block|}
DECL|method|getInProgressResources ()
specifier|public
name|Map
argument_list|<
name|LocalResourceProto
argument_list|,
name|Path
argument_list|>
name|getInProgressResources
parameter_list|()
block|{
return|return
name|inProgressResources
return|;
block|}
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|localizedResources
operator|.
name|isEmpty
argument_list|()
operator|&&
name|inProgressResources
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
DECL|class|RecoveredUserResources
specifier|public
specifier|static
class|class
name|RecoveredUserResources
block|{
DECL|field|privateTrackerState
name|LocalResourceTrackerState
name|privateTrackerState
init|=
operator|new
name|LocalResourceTrackerState
argument_list|()
decl_stmt|;
DECL|field|appTrackerStates
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|LocalResourceTrackerState
argument_list|>
name|appTrackerStates
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|LocalResourceTrackerState
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getPrivateTrackerState ()
specifier|public
name|LocalResourceTrackerState
name|getPrivateTrackerState
parameter_list|()
block|{
return|return
name|privateTrackerState
return|;
block|}
specifier|public
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|LocalResourceTrackerState
argument_list|>
DECL|method|getAppTrackerStates ()
name|getAppTrackerStates
parameter_list|()
block|{
return|return
name|appTrackerStates
return|;
block|}
block|}
DECL|class|RecoveredLocalizationState
specifier|public
specifier|static
class|class
name|RecoveredLocalizationState
block|{
DECL|field|publicTrackerState
name|LocalResourceTrackerState
name|publicTrackerState
init|=
operator|new
name|LocalResourceTrackerState
argument_list|()
decl_stmt|;
DECL|field|userResources
name|Map
argument_list|<
name|String
argument_list|,
name|RecoveredUserResources
argument_list|>
name|userResources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|RecoveredUserResources
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|getPublicTrackerState ()
specifier|public
name|LocalResourceTrackerState
name|getPublicTrackerState
parameter_list|()
block|{
return|return
name|publicTrackerState
return|;
block|}
DECL|method|getUserResources ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|RecoveredUserResources
argument_list|>
name|getUserResources
parameter_list|()
block|{
return|return
name|userResources
return|;
block|}
block|}
DECL|class|RecoveredDeletionServiceState
specifier|public
specifier|static
class|class
name|RecoveredDeletionServiceState
block|{
DECL|field|tasks
name|List
argument_list|<
name|DeletionServiceDeleteTaskProto
argument_list|>
name|tasks
decl_stmt|;
DECL|method|getTasks ()
specifier|public
name|List
argument_list|<
name|DeletionServiceDeleteTaskProto
argument_list|>
name|getTasks
parameter_list|()
block|{
return|return
name|tasks
return|;
block|}
block|}
DECL|class|RecoveredNMTokensState
specifier|public
specifier|static
class|class
name|RecoveredNMTokensState
block|{
DECL|field|currentMasterKey
name|MasterKey
name|currentMasterKey
decl_stmt|;
DECL|field|previousMasterKey
name|MasterKey
name|previousMasterKey
decl_stmt|;
DECL|field|applicationMasterKeys
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|MasterKey
argument_list|>
name|applicationMasterKeys
decl_stmt|;
DECL|method|getCurrentMasterKey ()
specifier|public
name|MasterKey
name|getCurrentMasterKey
parameter_list|()
block|{
return|return
name|currentMasterKey
return|;
block|}
DECL|method|getPreviousMasterKey ()
specifier|public
name|MasterKey
name|getPreviousMasterKey
parameter_list|()
block|{
return|return
name|previousMasterKey
return|;
block|}
DECL|method|getApplicationMasterKeys ()
specifier|public
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|MasterKey
argument_list|>
name|getApplicationMasterKeys
parameter_list|()
block|{
return|return
name|applicationMasterKeys
return|;
block|}
block|}
DECL|class|RecoveredContainerTokensState
specifier|public
specifier|static
class|class
name|RecoveredContainerTokensState
block|{
DECL|field|currentMasterKey
name|MasterKey
name|currentMasterKey
decl_stmt|;
DECL|field|previousMasterKey
name|MasterKey
name|previousMasterKey
decl_stmt|;
DECL|field|activeTokens
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Long
argument_list|>
name|activeTokens
decl_stmt|;
DECL|method|getCurrentMasterKey ()
specifier|public
name|MasterKey
name|getCurrentMasterKey
parameter_list|()
block|{
return|return
name|currentMasterKey
return|;
block|}
DECL|method|getPreviousMasterKey ()
specifier|public
name|MasterKey
name|getPreviousMasterKey
parameter_list|()
block|{
return|return
name|previousMasterKey
return|;
block|}
DECL|method|getActiveTokens ()
specifier|public
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|Long
argument_list|>
name|getActiveTokens
parameter_list|()
block|{
return|return
name|activeTokens
return|;
block|}
block|}
comment|/** Initialize the state storage */
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|initStorage
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/** Start the state storage for use */
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|IOException
block|{
name|startStorage
argument_list|()
expr_stmt|;
block|}
comment|/** Shutdown the state storage. */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|IOException
block|{
name|closeStorage
argument_list|()
expr_stmt|;
block|}
DECL|method|canRecover ()
specifier|public
name|boolean
name|canRecover
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Load the state of applications    * @return recovered state for applications    * @throws IOException    */
DECL|method|loadApplicationsState ()
specifier|public
specifier|abstract
name|RecoveredApplicationsState
name|loadApplicationsState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the start of an application    * @param appId the application ID    * @param p state to store for the application    * @throws IOException    */
DECL|method|storeApplication (ApplicationId appId, ContainerManagerApplicationProto p)
specifier|public
specifier|abstract
name|void
name|storeApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|ContainerManagerApplicationProto
name|p
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record that an application has finished    * @param appId the application ID    * @throws IOException    */
DECL|method|storeFinishedApplication (ApplicationId appId)
specifier|public
specifier|abstract
name|void
name|storeFinishedApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove records corresponding to an application    * @param appId the application ID    * @throws IOException    */
DECL|method|removeApplication (ApplicationId appId)
specifier|public
specifier|abstract
name|void
name|removeApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the state of containers    * @return recovered state for containers    * @throws IOException    */
DECL|method|loadContainersState ()
specifier|public
specifier|abstract
name|List
argument_list|<
name|RecoveredContainerState
argument_list|>
name|loadContainersState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Record a container start request    * @param containerId the container ID    * @param startRequest the container start request    * @throws IOException    */
DECL|method|storeContainer (ContainerId containerId, StartContainerRequest startRequest)
specifier|public
specifier|abstract
name|void
name|storeContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|StartContainerRequest
name|startRequest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record that a container has been launched    * @param containerId the container ID    * @throws IOException    */
DECL|method|storeContainerLaunched (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|storeContainerLaunched
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record that a container has completed    * @param containerId the container ID    * @param exitCode the exit code from the container    * @throws IOException    */
DECL|method|storeContainerCompleted (ContainerId containerId, int exitCode)
specifier|public
specifier|abstract
name|void
name|storeContainerCompleted
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|exitCode
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record a request to kill a container    * @param containerId the container ID    * @throws IOException    */
DECL|method|storeContainerKilled (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|storeContainerKilled
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record diagnostics for a container    * @param containerId the container ID    * @param diagnostics the container diagnostics    * @throws IOException    */
DECL|method|storeContainerDiagnostics (ContainerId containerId, StringBuilder diagnostics)
specifier|public
specifier|abstract
name|void
name|storeContainerDiagnostics
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|StringBuilder
name|diagnostics
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove records corresponding to a container    * @param containerId the container ID    * @throws IOException    */
DECL|method|removeContainer (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|removeContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the state of localized resources    * @return recovered localized resource state    * @throws IOException    */
DECL|method|loadLocalizationState ()
specifier|public
specifier|abstract
name|RecoveredLocalizationState
name|loadLocalizationState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the start of localization for a resource    * @param user the username or null if the resource is public    * @param appId the application ID if the resource is app-specific or null    * @param proto the resource request    * @param localPath local filesystem path where the resource will be stored    * @throws IOException    */
DECL|method|startResourceLocalization (String user, ApplicationId appId, LocalResourceProto proto, Path localPath)
specifier|public
specifier|abstract
name|void
name|startResourceLocalization
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|LocalResourceProto
name|proto
parameter_list|,
name|Path
name|localPath
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the completion of a resource localization    * @param user the username or null if the resource is public    * @param appId the application ID if the resource is app-specific or null    * @param proto the serialized localized resource    * @throws IOException    */
DECL|method|finishResourceLocalization (String user, ApplicationId appId, LocalizedResourceProto proto)
specifier|public
specifier|abstract
name|void
name|finishResourceLocalization
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|LocalizedResourceProto
name|proto
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove records related to a resource localization    * @param user the username or null if the resource is public    * @param appId the application ID if the resource is app-specific or null    * @param localPath local filesystem path where the resource will be stored    * @throws IOException    */
DECL|method|removeLocalizedResource (String user, ApplicationId appId, Path localPath)
specifier|public
specifier|abstract
name|void
name|removeLocalizedResource
parameter_list|(
name|String
name|user
parameter_list|,
name|ApplicationId
name|appId
parameter_list|,
name|Path
name|localPath
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the state of the deletion service    * @return recovered deletion service state    * @throws IOException    */
DECL|method|loadDeletionServiceState ()
specifier|public
specifier|abstract
name|RecoveredDeletionServiceState
name|loadDeletionServiceState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Record a deletion task    * @param taskId the deletion task ID    * @param taskProto the deletion task protobuf    * @throws IOException    */
DECL|method|storeDeletionTask (int taskId, DeletionServiceDeleteTaskProto taskProto)
specifier|public
specifier|abstract
name|void
name|storeDeletionTask
parameter_list|(
name|int
name|taskId
parameter_list|,
name|DeletionServiceDeleteTaskProto
name|taskProto
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove records corresponding to a deletion task    * @param taskId the deletion task ID    * @throws IOException    */
DECL|method|removeDeletionTask (int taskId)
specifier|public
specifier|abstract
name|void
name|removeDeletionTask
parameter_list|(
name|int
name|taskId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the state of NM tokens    * @return recovered state of NM tokens    * @throws IOException    */
DECL|method|loadNMTokensState ()
specifier|public
specifier|abstract
name|RecoveredNMTokensState
name|loadNMTokensState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the current NM token master key    * @param key the master key    * @throws IOException    */
DECL|method|storeNMTokenCurrentMasterKey (MasterKey key)
specifier|public
specifier|abstract
name|void
name|storeNMTokenCurrentMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the previous NM token master key    * @param key the previous master key    * @throws IOException    */
DECL|method|storeNMTokenPreviousMasterKey (MasterKey key)
specifier|public
specifier|abstract
name|void
name|storeNMTokenPreviousMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record a master key corresponding to an application    * @param attempt the application attempt ID    * @param key the master key    * @throws IOException    */
DECL|method|storeNMTokenApplicationMasterKey ( ApplicationAttemptId attempt, MasterKey key)
specifier|public
specifier|abstract
name|void
name|storeNMTokenApplicationMasterKey
parameter_list|(
name|ApplicationAttemptId
name|attempt
parameter_list|,
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove a master key corresponding to an application    * @param attempt the application attempt ID    * @throws IOException    */
DECL|method|removeNMTokenApplicationMasterKey ( ApplicationAttemptId attempt)
specifier|public
specifier|abstract
name|void
name|removeNMTokenApplicationMasterKey
parameter_list|(
name|ApplicationAttemptId
name|attempt
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Load the state of container tokens    * @return recovered state of container tokens    * @throws IOException    */
DECL|method|loadContainerTokensState ()
specifier|public
specifier|abstract
name|RecoveredContainerTokensState
name|loadContainerTokensState
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the current container token master key    * @param key the master key    * @throws IOException    */
DECL|method|storeContainerTokenCurrentMasterKey (MasterKey key)
specifier|public
specifier|abstract
name|void
name|storeContainerTokenCurrentMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the previous container token master key    * @param key the previous master key    * @throws IOException    */
DECL|method|storeContainerTokenPreviousMasterKey (MasterKey key)
specifier|public
specifier|abstract
name|void
name|storeContainerTokenPreviousMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Record the expiration time for a container token    * @param containerId the container ID    * @param expirationTime the container token expiration time    * @throws IOException    */
DECL|method|storeContainerToken (ContainerId containerId, Long expirationTime)
specifier|public
specifier|abstract
name|void
name|storeContainerToken
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|Long
name|expirationTime
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Remove records for a container token    * @param containerId the container ID    * @throws IOException    */
DECL|method|removeContainerToken (ContainerId containerId)
specifier|public
specifier|abstract
name|void
name|removeContainerToken
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|initStorage (Configuration conf)
specifier|protected
specifier|abstract
name|void
name|initStorage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
function_decl|;
DECL|method|startStorage ()
specifier|protected
specifier|abstract
name|void
name|startStorage
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|closeStorage ()
specifier|protected
specifier|abstract
name|void
name|closeStorage
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

