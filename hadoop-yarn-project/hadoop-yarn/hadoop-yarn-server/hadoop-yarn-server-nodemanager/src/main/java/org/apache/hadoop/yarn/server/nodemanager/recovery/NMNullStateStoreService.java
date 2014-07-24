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

