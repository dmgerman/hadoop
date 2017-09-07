begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Serializable
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
name|api
operator|.
name|records
operator|.
name|Resource
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
name|proto
operator|.
name|YarnServerNodemanagerRecoveryProtos
operator|.
name|LogDeleterProto
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

begin_comment
comment|// The state store to use when state isn't being stored
end_comment

begin_class
DECL|class|NMNullStateStoreService
specifier|public
class|class
name|NMNullStateStoreService
extends|extends
name|NMStateStoreService
block|{
DECL|method|NMNullStateStoreService ()
specifier|public
name|NMNullStateStoreService
parameter_list|()
block|{
name|super
argument_list|(
name|NMNullStateStoreService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|canRecover ()
specifier|public
name|boolean
name|canRecover
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|loadApplicationsState ()
specifier|public
name|RecoveredApplicationsState
name|loadApplicationsState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeApplication (ApplicationId appId, ContainerManagerApplicationProto p)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|removeApplication (ApplicationId appId)
specifier|public
name|void
name|removeApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|loadContainersState ()
specifier|public
name|List
argument_list|<
name|RecoveredContainerState
argument_list|>
name|loadContainersState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeContainer (ContainerId containerId, int version, long startTime, StartContainerRequest startRequest)
specifier|public
name|void
name|storeContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|version
parameter_list|,
name|long
name|startTime
parameter_list|,
name|StartContainerRequest
name|startRequest
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerQueued (ContainerId containerId)
specifier|public
name|void
name|storeContainerQueued
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerDiagnostics (ContainerId containerId, StringBuilder diagnostics)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|storeContainerLaunched (ContainerId containerId)
specifier|public
name|void
name|storeContainerLaunched
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerResourceChanged (ContainerId containerId, int version, Resource capability)
specifier|public
name|void
name|storeContainerResourceChanged
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|version
parameter_list|,
name|Resource
name|capability
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerKilled (ContainerId containerId)
specifier|public
name|void
name|storeContainerKilled
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerCompleted (ContainerId containerId, int exitCode)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|storeContainerRemainingRetryAttempts (ContainerId containerId, int remainingRetryAttempts)
specifier|public
name|void
name|storeContainerRemainingRetryAttempts
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|int
name|remainingRetryAttempts
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerWorkDir (ContainerId containerId, String workDir)
specifier|public
name|void
name|storeContainerWorkDir
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|workDir
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerLogDir (ContainerId containerId, String logDir)
specifier|public
name|void
name|storeContainerLogDir
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|logDir
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|removeContainer (ContainerId containerId)
specifier|public
name|void
name|removeContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|loadLocalizationState ()
specifier|public
name|RecoveredLocalizationState
name|loadLocalizationState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|startResourceLocalization (String user, ApplicationId appId, LocalResourceProto proto, Path localPath)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|finishResourceLocalization (String user, ApplicationId appId, LocalizedResourceProto proto)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|removeLocalizedResource (String user, ApplicationId appId, Path localPath)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|loadDeletionServiceState ()
specifier|public
name|RecoveredDeletionServiceState
name|loadDeletionServiceState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeDeletionTask (int taskId, DeletionServiceDeleteTaskProto taskProto)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|removeDeletionTask (int taskId)
specifier|public
name|void
name|removeDeletionTask
parameter_list|(
name|int
name|taskId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|loadNMTokensState ()
specifier|public
name|RecoveredNMTokensState
name|loadNMTokensState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeNMTokenCurrentMasterKey (MasterKey key)
specifier|public
name|void
name|storeNMTokenCurrentMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeNMTokenPreviousMasterKey (MasterKey key)
specifier|public
name|void
name|storeNMTokenPreviousMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeNMTokenApplicationMasterKey (ApplicationAttemptId attempt, MasterKey key)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|removeNMTokenApplicationMasterKey (ApplicationAttemptId attempt)
specifier|public
name|void
name|removeNMTokenApplicationMasterKey
parameter_list|(
name|ApplicationAttemptId
name|attempt
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|loadContainerTokensState ()
specifier|public
name|RecoveredContainerTokensState
name|loadContainerTokensState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeContainerTokenCurrentMasterKey (MasterKey key)
specifier|public
name|void
name|storeContainerTokenCurrentMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerTokenPreviousMasterKey (MasterKey key)
specifier|public
name|void
name|storeContainerTokenPreviousMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeContainerToken (ContainerId containerId, Long expirationTime)
specifier|public
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
block|{   }
annotation|@
name|Override
DECL|method|removeContainerToken (ContainerId containerId)
specifier|public
name|void
name|removeContainerToken
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|loadLogDeleterState ()
specifier|public
name|RecoveredLogDeleterState
name|loadLogDeleterState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeLogDeleter (ApplicationId appId, LogDeleterProto proto)
specifier|public
name|void
name|storeLogDeleter
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|LogDeleterProto
name|proto
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|removeLogDeleter (ApplicationId appId)
specifier|public
name|void
name|removeLogDeleter
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|loadAMRMProxyState ()
specifier|public
name|RecoveredAMRMProxyState
name|loadAMRMProxyState
parameter_list|()
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Recovery not supported by this state store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeAMRMProxyCurrentMasterKey (MasterKey key)
specifier|public
name|void
name|storeAMRMProxyCurrentMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeAMRMProxyNextMasterKey (MasterKey key)
specifier|public
name|void
name|storeAMRMProxyNextMasterKey
parameter_list|(
name|MasterKey
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeAMRMProxyAppContextEntry (ApplicationAttemptId attempt, String key, byte[] data)
specifier|public
name|void
name|storeAMRMProxyAppContextEntry
parameter_list|(
name|ApplicationAttemptId
name|attempt
parameter_list|,
name|String
name|key
parameter_list|,
name|byte
index|[]
name|data
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|removeAMRMProxyAppContextEntry (ApplicationAttemptId attempt, String key)
specifier|public
name|void
name|removeAMRMProxyAppContextEntry
parameter_list|(
name|ApplicationAttemptId
name|attempt
parameter_list|,
name|String
name|key
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|removeAMRMProxyAppContext (ApplicationAttemptId attempt)
specifier|public
name|void
name|removeAMRMProxyAppContext
parameter_list|(
name|ApplicationAttemptId
name|attempt
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|storeAssignedResources (ContainerId containerId, String resourceType, List<Serializable> assignedResources)
specifier|public
name|void
name|storeAssignedResources
parameter_list|(
name|ContainerId
name|containerId
parameter_list|,
name|String
name|resourceType
parameter_list|,
name|List
argument_list|<
name|Serializable
argument_list|>
name|assignedResources
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|initStorage (Configuration conf)
specifier|protected
name|void
name|initStorage
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|startStorage ()
specifier|protected
name|void
name|startStorage
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Override
DECL|method|closeStorage ()
specifier|protected
name|void
name|closeStorage
parameter_list|()
throws|throws
name|IOException
block|{   }
block|}
end_class

end_unit

