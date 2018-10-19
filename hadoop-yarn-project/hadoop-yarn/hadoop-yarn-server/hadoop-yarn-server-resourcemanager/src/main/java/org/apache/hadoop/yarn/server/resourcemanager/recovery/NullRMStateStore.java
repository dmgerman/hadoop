begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.recovery
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
name|resourcemanager
operator|.
name|recovery
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
name|token
operator|.
name|delegation
operator|.
name|DelegationKey
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
name|proto
operator|.
name|YarnProtos
operator|.
name|ReservationAllocationStateProto
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
name|client
operator|.
name|RMDelegationTokenIdentifier
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
name|records
operator|.
name|Version
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|AMRMTokenSecretManagerState
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|ApplicationAttemptStateData
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|records
operator|.
name|ApplicationStateData
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivateKey
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
import|;
end_import

begin_class
annotation|@
name|Unstable
DECL|class|NullRMStateStore
specifier|public
class|class
name|NullRMStateStore
extends|extends
name|RMStateStore
block|{
annotation|@
name|Override
DECL|method|initInternal (Configuration conf)
specifier|protected
name|void
name|initInternal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|startInternal ()
specifier|protected
name|void
name|startInternal
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|closeInternal ()
specifier|protected
name|void
name|closeInternal
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|getAndIncrementEpoch ()
specifier|public
specifier|synchronized
name|long
name|getAndIncrementEpoch
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|0L
return|;
block|}
annotation|@
name|Override
DECL|method|loadState ()
specifier|public
name|RMState
name|loadState
parameter_list|()
throws|throws
name|Exception
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Cannot load state from null store"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|storeApplicationStateInternal (ApplicationId appId, ApplicationStateData appStateData)
specifier|protected
name|void
name|storeApplicationStateInternal
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationStateData
name|appStateData
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeApplicationAttemptStateInternal (ApplicationAttemptId attemptId, ApplicationAttemptStateData attemptStateData)
specifier|protected
name|void
name|storeApplicationAttemptStateInternal
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|ApplicationAttemptStateData
name|attemptStateData
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeApplicationStateInternal (ApplicationStateData appState)
specifier|protected
name|void
name|removeApplicationStateInternal
parameter_list|(
name|ApplicationStateData
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeRMDelegationTokenState ( RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate)
specifier|public
name|void
name|storeRMDelegationTokenState
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|,
name|Long
name|renewDate
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeRMDelegationTokenState (RMDelegationTokenIdentifier rmDTIdentifier)
specifier|public
name|void
name|removeRMDelegationTokenState
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|updateRMDelegationTokenState ( RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate)
specifier|protected
name|void
name|updateRMDelegationTokenState
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|,
name|Long
name|renewDate
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeRMDTMasterKeyState (DelegationKey delegationKey)
specifier|public
name|void
name|storeRMDTMasterKeyState
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeReservationState ( ReservationAllocationStateProto reservationAllocation, String planName, String reservationIdName)
specifier|protected
name|void
name|storeReservationState
parameter_list|(
name|ReservationAllocationStateProto
name|reservationAllocation
parameter_list|,
name|String
name|planName
parameter_list|,
name|String
name|reservationIdName
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeReservationState (String planName, String reservationIdName)
specifier|protected
name|void
name|removeReservationState
parameter_list|(
name|String
name|planName
parameter_list|,
name|String
name|reservationIdName
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeRMDTMasterKeyState (DelegationKey delegationKey)
specifier|public
name|void
name|removeRMDTMasterKeyState
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|updateApplicationStateInternal (ApplicationId appId, ApplicationStateData appStateData)
specifier|protected
name|void
name|updateApplicationStateInternal
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationStateData
name|appStateData
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|updateApplicationAttemptStateInternal (ApplicationAttemptId attemptId, ApplicationAttemptStateData attemptStateData)
specifier|protected
name|void
name|updateApplicationAttemptStateInternal
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|ApplicationAttemptStateData
name|attemptStateData
parameter_list|)
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|removeApplicationAttemptInternal ( ApplicationAttemptId attemptId)
specifier|public
specifier|synchronized
name|void
name|removeApplicationAttemptInternal
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|checkVersion ()
specifier|public
name|void
name|checkVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|loadVersion ()
specifier|protected
name|Version
name|loadVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|storeVersion ()
specifier|protected
name|void
name|storeVersion
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|getCurrentVersion ()
specifier|protected
name|Version
name|getCurrentVersion
parameter_list|()
block|{
comment|// Do nothing
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|storeOrUpdateAMRMTokenSecretManagerState ( AMRMTokenSecretManagerState state, boolean isUpdate)
specifier|public
name|void
name|storeOrUpdateAMRMTokenSecretManagerState
parameter_list|(
name|AMRMTokenSecretManagerState
name|state
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
block|{
comment|//DO Nothing
block|}
annotation|@
name|Override
DECL|method|deleteStore ()
specifier|public
name|void
name|deleteStore
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|removeApplication (ApplicationId removeAppId)
specifier|public
name|void
name|removeApplication
parameter_list|(
name|ApplicationId
name|removeAppId
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
annotation|@
name|Override
DECL|method|storeProxyCACertState ( X509Certificate caCert, PrivateKey caPrivateKey)
specifier|protected
name|void
name|storeProxyCACertState
parameter_list|(
name|X509Certificate
name|caCert
parameter_list|,
name|PrivateKey
name|caPrivateKey
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Do nothing
block|}
block|}
end_class

end_unit

