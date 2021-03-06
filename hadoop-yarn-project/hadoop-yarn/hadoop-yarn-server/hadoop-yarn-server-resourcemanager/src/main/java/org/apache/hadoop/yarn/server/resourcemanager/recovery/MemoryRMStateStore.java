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
name|api
operator|.
name|records
operator|.
name|ReservationId
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
name|YarnRuntimeException
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

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|MemoryRMStateStore
specifier|public
class|class
name|MemoryRMStateStore
extends|extends
name|RMStateStore
block|{
DECL|field|state
name|RMState
name|state
init|=
operator|new
name|RMState
argument_list|()
decl_stmt|;
DECL|field|epoch
specifier|private
name|long
name|epoch
init|=
literal|0L
decl_stmt|;
annotation|@
name|VisibleForTesting
DECL|method|getState ()
specifier|public
name|RMState
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
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
block|{   }
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
name|long
name|currentEpoch
init|=
name|epoch
decl_stmt|;
name|epoch
operator|=
name|nextEpoch
argument_list|(
name|epoch
argument_list|)
expr_stmt|;
return|return
name|currentEpoch
return|;
block|}
annotation|@
name|Override
DECL|method|loadState ()
specifier|public
specifier|synchronized
name|RMState
name|loadState
parameter_list|()
throws|throws
name|Exception
block|{
comment|// return a copy of the state to allow for modification of the real state
name|RMState
name|returnState
init|=
operator|new
name|RMState
argument_list|()
decl_stmt|;
name|returnState
operator|.
name|appState
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|appState
argument_list|)
expr_stmt|;
name|returnState
operator|.
name|rmSecretManagerState
operator|.
name|getMasterKeyState
argument_list|()
operator|.
name|addAll
argument_list|(
name|state
operator|.
name|rmSecretManagerState
operator|.
name|getMasterKeyState
argument_list|()
argument_list|)
expr_stmt|;
name|returnState
operator|.
name|rmSecretManagerState
operator|.
name|getTokenState
argument_list|()
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|rmSecretManagerState
operator|.
name|getTokenState
argument_list|()
argument_list|)
expr_stmt|;
name|returnState
operator|.
name|rmSecretManagerState
operator|.
name|dtSequenceNumber
operator|=
name|state
operator|.
name|rmSecretManagerState
operator|.
name|dtSequenceNumber
expr_stmt|;
name|returnState
operator|.
name|amrmTokenSecretManagerState
operator|=
name|state
operator|.
name|amrmTokenSecretManagerState
operator|==
literal|null
condition|?
literal|null
else|:
name|AMRMTokenSecretManagerState
operator|.
name|newInstance
argument_list|(
name|state
operator|.
name|amrmTokenSecretManagerState
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|proxyCAState
operator|.
name|getCaCert
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|caCertData
init|=
name|state
operator|.
name|proxyCAState
operator|.
name|getCaCert
argument_list|()
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
name|returnState
operator|.
name|proxyCAState
operator|.
name|setCaCert
argument_list|(
name|caCertData
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|state
operator|.
name|proxyCAState
operator|.
name|getCaPrivateKey
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|caPrivateKeyData
init|=
name|state
operator|.
name|proxyCAState
operator|.
name|getCaPrivateKey
argument_list|()
operator|.
name|getEncoded
argument_list|()
decl_stmt|;
name|returnState
operator|.
name|proxyCAState
operator|.
name|setCaPrivateKey
argument_list|(
name|caPrivateKeyData
argument_list|)
expr_stmt|;
block|}
return|return
name|returnState
return|;
block|}
annotation|@
name|Override
DECL|method|initInternal (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|initInternal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|epoch
operator|=
name|baseEpoch
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startInternal ()
specifier|protected
specifier|synchronized
name|void
name|startInternal
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|closeInternal ()
specifier|protected
specifier|synchronized
name|void
name|closeInternal
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|Override
DECL|method|storeApplicationStateInternal ( ApplicationId appId, ApplicationStateData appState)
specifier|public
specifier|synchronized
name|void
name|storeApplicationStateInternal
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationStateData
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
name|state
operator|.
name|appState
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|appState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateApplicationStateInternal ( ApplicationId appId, ApplicationStateData appState)
specifier|public
specifier|synchronized
name|void
name|updateApplicationStateInternal
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|ApplicationStateData
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Updating final state "
operator|+
name|appState
operator|.
name|getState
argument_list|()
operator|+
literal|" for app: "
operator|+
name|appId
argument_list|)
expr_stmt|;
if|if
condition|(
name|state
operator|.
name|appState
operator|.
name|get
argument_list|(
name|appId
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|// add the earlier attempts back
name|appState
operator|.
name|attempts
operator|.
name|putAll
argument_list|(
name|state
operator|.
name|appState
operator|.
name|get
argument_list|(
name|appId
argument_list|)
operator|.
name|attempts
argument_list|)
expr_stmt|;
block|}
name|state
operator|.
name|appState
operator|.
name|put
argument_list|(
name|appId
argument_list|,
name|appState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeApplicationAttemptStateInternal ( ApplicationAttemptId appAttemptId, ApplicationAttemptStateData attemptState)
specifier|public
specifier|synchronized
name|void
name|storeApplicationAttemptStateInternal
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|ApplicationAttemptStateData
name|attemptState
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationStateData
name|appState
init|=
name|state
operator|.
name|getApplicationState
argument_list|()
operator|.
name|get
argument_list|(
name|attemptState
operator|.
name|getAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|appState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Application doesn't exist"
argument_list|)
throw|;
block|}
name|appState
operator|.
name|attempts
operator|.
name|put
argument_list|(
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|,
name|attemptState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateApplicationAttemptStateInternal ( ApplicationAttemptId appAttemptId, ApplicationAttemptStateData attemptState)
specifier|public
specifier|synchronized
name|void
name|updateApplicationAttemptStateInternal
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|,
name|ApplicationAttemptStateData
name|attemptState
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationStateData
name|appState
init|=
name|state
operator|.
name|getApplicationState
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|appState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Application doesn't exist"
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Updating final state "
operator|+
name|attemptState
operator|.
name|getState
argument_list|()
operator|+
literal|" for attempt: "
operator|+
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|appState
operator|.
name|attempts
operator|.
name|put
argument_list|(
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|,
name|attemptState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeApplicationAttemptInternal ( ApplicationAttemptId appAttemptId)
specifier|public
specifier|synchronized
name|void
name|removeApplicationAttemptInternal
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationStateData
name|appState
init|=
name|state
operator|.
name|getApplicationState
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|ApplicationAttemptStateData
name|attemptState
init|=
name|appState
operator|.
name|attempts
operator|.
name|remove
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing state for attempt: "
operator|+
name|appAttemptId
argument_list|)
expr_stmt|;
if|if
condition|(
name|attemptState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Application doesn't exist"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|removeApplicationStateInternal ( ApplicationStateData appState)
specifier|public
specifier|synchronized
name|void
name|removeApplicationStateInternal
parameter_list|(
name|ApplicationStateData
name|appState
parameter_list|)
throws|throws
name|Exception
block|{
name|ApplicationId
name|appId
init|=
name|appState
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|ApplicationStateData
name|removed
init|=
name|state
operator|.
name|appState
operator|.
name|remove
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"Removing non-existing application state"
argument_list|)
throw|;
block|}
block|}
DECL|method|storeOrUpdateRMDT (RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate, boolean isUpdate)
specifier|private
name|void
name|storeOrUpdateRMDT
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|,
name|Long
name|renewDate
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|rmDTState
init|=
name|state
operator|.
name|rmSecretManagerState
operator|.
name|getTokenState
argument_list|()
decl_stmt|;
if|if
condition|(
name|rmDTState
operator|.
name|containsKey
argument_list|(
name|rmDTIdentifier
argument_list|)
condition|)
block|{
name|IOException
name|e
init|=
operator|new
name|IOException
argument_list|(
literal|"RMDelegationToken: "
operator|+
name|rmDTIdentifier
operator|+
literal|"is already stored."
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Error storing info for RMDelegationToken: "
operator|+
name|rmDTIdentifier
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|rmDTState
operator|.
name|put
argument_list|(
name|rmDTIdentifier
argument_list|,
name|renewDate
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|isUpdate
condition|)
block|{
name|state
operator|.
name|rmSecretManagerState
operator|.
name|dtSequenceNumber
operator|=
name|rmDTIdentifier
operator|.
name|getSequenceNumber
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Store RMDT with sequence number "
operator|+
name|rmDTIdentifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeRMDelegationTokenState ( RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate)
specifier|public
specifier|synchronized
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
name|storeOrUpdateRMDT
argument_list|(
name|rmDTIdentifier
argument_list|,
name|renewDate
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeRMDelegationTokenState ( RMDelegationTokenIdentifier rmDTIdentifier)
specifier|public
specifier|synchronized
name|void
name|removeRMDelegationTokenState
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|)
throws|throws
name|Exception
block|{
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|rmDTState
init|=
name|state
operator|.
name|rmSecretManagerState
operator|.
name|getTokenState
argument_list|()
decl_stmt|;
name|rmDTState
operator|.
name|remove
argument_list|(
name|rmDTIdentifier
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Remove RMDT with sequence number "
operator|+
name|rmDTIdentifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|updateRMDelegationTokenState ( RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate)
specifier|protected
specifier|synchronized
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
name|removeRMDelegationTokenState
argument_list|(
name|rmDTIdentifier
argument_list|)
expr_stmt|;
name|storeOrUpdateRMDT
argument_list|(
name|rmDTIdentifier
argument_list|,
name|renewDate
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Update RMDT with sequence number "
operator|+
name|rmDTIdentifier
operator|.
name|getSequenceNumber
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeRMDTMasterKeyState (DelegationKey delegationKey)
specifier|public
specifier|synchronized
name|void
name|storeRMDTMasterKeyState
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|rmDTMasterKeyState
init|=
name|state
operator|.
name|rmSecretManagerState
operator|.
name|getMasterKeyState
argument_list|()
decl_stmt|;
if|if
condition|(
name|rmDTMasterKeyState
operator|.
name|contains
argument_list|(
name|delegationKey
argument_list|)
condition|)
block|{
name|IOException
name|e
init|=
operator|new
name|IOException
argument_list|(
literal|"RMDTMasterKey with keyID: "
operator|+
name|delegationKey
operator|.
name|getKeyId
argument_list|()
operator|+
literal|" is already stored"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Error storing info for RMDTMasterKey with keyID: "
operator|+
name|delegationKey
operator|.
name|getKeyId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|state
operator|.
name|getRMDTSecretManagerState
argument_list|()
operator|.
name|getMasterKeyState
argument_list|()
operator|.
name|add
argument_list|(
name|delegationKey
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Store RMDT master key with key id: "
operator|+
name|delegationKey
operator|.
name|getKeyId
argument_list|()
operator|+
literal|". Currently rmDTMasterKeyState size: "
operator|+
name|rmDTMasterKeyState
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeRMDTMasterKeyState (DelegationKey delegationKey)
specifier|public
specifier|synchronized
name|void
name|removeRMDTMasterKeyState
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Remove RMDT master key with key id: "
operator|+
name|delegationKey
operator|.
name|getKeyId
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|rmDTMasterKeyState
init|=
name|state
operator|.
name|rmSecretManagerState
operator|.
name|getMasterKeyState
argument_list|()
decl_stmt|;
name|rmDTMasterKeyState
operator|.
name|remove
argument_list|(
name|delegationKey
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|storeReservationState ( ReservationAllocationStateProto reservationAllocation, String planName, String reservationIdName)
specifier|protected
specifier|synchronized
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing reservationallocation for "
operator|+
name|reservationIdName
operator|+
literal|" "
operator|+
literal|"for plan "
operator|+
name|planName
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ReservationId
argument_list|,
name|ReservationAllocationStateProto
argument_list|>
name|planState
init|=
name|state
operator|.
name|getReservationState
argument_list|()
operator|.
name|get
argument_list|(
name|planName
argument_list|)
decl_stmt|;
if|if
condition|(
name|planState
operator|==
literal|null
condition|)
block|{
name|planState
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
name|state
operator|.
name|getReservationState
argument_list|()
operator|.
name|put
argument_list|(
name|planName
argument_list|,
name|planState
argument_list|)
expr_stmt|;
block|}
name|ReservationId
name|reservationId
init|=
name|ReservationId
operator|.
name|parseReservationId
argument_list|(
name|reservationIdName
argument_list|)
decl_stmt|;
name|planState
operator|.
name|put
argument_list|(
name|reservationId
argument_list|,
name|reservationAllocation
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|removeReservationState ( String planName, String reservationIdName)
specifier|protected
specifier|synchronized
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing reservationallocation "
operator|+
name|reservationIdName
operator|+
literal|" for plan "
operator|+
name|planName
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|ReservationId
argument_list|,
name|ReservationAllocationStateProto
argument_list|>
name|planState
init|=
name|state
operator|.
name|getReservationState
argument_list|()
operator|.
name|get
argument_list|(
name|planName
argument_list|)
decl_stmt|;
if|if
condition|(
name|planState
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
literal|"State for plan "
operator|+
name|planName
operator|+
literal|" does "
operator|+
literal|"not exist"
argument_list|)
throw|;
block|}
name|ReservationId
name|reservationId
init|=
name|ReservationId
operator|.
name|parseReservationId
argument_list|(
name|reservationIdName
argument_list|)
decl_stmt|;
name|planState
operator|.
name|remove
argument_list|(
name|reservationId
argument_list|)
expr_stmt|;
if|if
condition|(
name|planState
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|state
operator|.
name|getReservationState
argument_list|()
operator|.
name|remove
argument_list|(
name|planName
argument_list|)
expr_stmt|;
block|}
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
name|state
operator|.
name|getProxyCAState
argument_list|()
operator|.
name|setCaCert
argument_list|(
name|caCert
argument_list|)
expr_stmt|;
name|state
operator|.
name|getProxyCAState
argument_list|()
operator|.
name|setCaPrivateKey
argument_list|(
name|caPrivateKey
argument_list|)
expr_stmt|;
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
block|{   }
annotation|@
name|Override
DECL|method|getCurrentVersion ()
specifier|protected
name|Version
name|getCurrentVersion
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|storeOrUpdateAMRMTokenSecretManagerState ( AMRMTokenSecretManagerState amrmTokenSecretManagerState, boolean isUpdate)
specifier|public
specifier|synchronized
name|void
name|storeOrUpdateAMRMTokenSecretManagerState
parameter_list|(
name|AMRMTokenSecretManagerState
name|amrmTokenSecretManagerState
parameter_list|,
name|boolean
name|isUpdate
parameter_list|)
block|{
if|if
condition|(
name|amrmTokenSecretManagerState
operator|!=
literal|null
condition|)
block|{
name|state
operator|.
name|amrmTokenSecretManagerState
operator|=
name|AMRMTokenSecretManagerState
operator|.
name|newInstance
argument_list|(
name|amrmTokenSecretManagerState
argument_list|)
expr_stmt|;
block|}
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
block|{   }
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
block|{   }
block|}
end_class

end_unit

