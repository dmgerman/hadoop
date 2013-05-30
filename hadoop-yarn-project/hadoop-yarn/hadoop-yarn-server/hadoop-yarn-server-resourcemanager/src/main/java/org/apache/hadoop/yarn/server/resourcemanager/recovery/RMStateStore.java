begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|nio
operator|.
name|ByteBuffer
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|io
operator|.
name|DataOutputBuffer
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
name|Credentials
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
name|api
operator|.
name|records
operator|.
name|Container
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationSubmissionContextPBImpl
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|EventHandler
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
name|ApplicationTokenIdentifier
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
name|ClientTokenIdentifier
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationAttemptStateDataPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ApplicationStateDataPBImpl
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|rmapp
operator|.
name|RMAppStoredEvent
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
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|rmapp
operator|.
name|attempt
operator|.
name|event
operator|.
name|RMAppAttemptStoredEvent
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
comment|/**  * Base class to implement storage of ResourceManager state.  * Takes care of asynchronous notifications and interfacing with YARN objects.  * Real store implementations need to derive from it and implement blocking  * store and load methods to actually store and load the state.  */
DECL|class|RMStateStore
specifier|public
specifier|abstract
class|class
name|RMStateStore
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RMStateStore
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * State of an application attempt    */
DECL|class|ApplicationAttemptState
specifier|public
specifier|static
class|class
name|ApplicationAttemptState
block|{
DECL|field|attemptId
specifier|final
name|ApplicationAttemptId
name|attemptId
decl_stmt|;
DECL|field|masterContainer
specifier|final
name|Container
name|masterContainer
decl_stmt|;
DECL|field|appAttemptTokens
specifier|final
name|Credentials
name|appAttemptTokens
decl_stmt|;
DECL|method|ApplicationAttemptState (ApplicationAttemptId attemptId, Container masterContainer, Credentials appAttemptTokens)
specifier|public
name|ApplicationAttemptState
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|Container
name|masterContainer
parameter_list|,
name|Credentials
name|appAttemptTokens
parameter_list|)
block|{
name|this
operator|.
name|attemptId
operator|=
name|attemptId
expr_stmt|;
name|this
operator|.
name|masterContainer
operator|=
name|masterContainer
expr_stmt|;
name|this
operator|.
name|appAttemptTokens
operator|=
name|appAttemptTokens
expr_stmt|;
block|}
DECL|method|getMasterContainer ()
specifier|public
name|Container
name|getMasterContainer
parameter_list|()
block|{
return|return
name|masterContainer
return|;
block|}
DECL|method|getAttemptId ()
specifier|public
name|ApplicationAttemptId
name|getAttemptId
parameter_list|()
block|{
return|return
name|attemptId
return|;
block|}
DECL|method|getAppAttemptTokens ()
specifier|public
name|Credentials
name|getAppAttemptTokens
parameter_list|()
block|{
return|return
name|appAttemptTokens
return|;
block|}
block|}
comment|/**    * State of an application application    */
DECL|class|ApplicationState
specifier|public
specifier|static
class|class
name|ApplicationState
block|{
DECL|field|context
specifier|final
name|ApplicationSubmissionContext
name|context
decl_stmt|;
DECL|field|submitTime
specifier|final
name|long
name|submitTime
decl_stmt|;
DECL|field|user
specifier|final
name|String
name|user
decl_stmt|;
DECL|field|attempts
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptState
argument_list|>
name|attempts
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptState
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|ApplicationState (long submitTime, ApplicationSubmissionContext context, String user)
name|ApplicationState
parameter_list|(
name|long
name|submitTime
parameter_list|,
name|ApplicationSubmissionContext
name|context
parameter_list|,
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|submitTime
operator|=
name|submitTime
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
DECL|method|getAppId ()
specifier|public
name|ApplicationId
name|getAppId
parameter_list|()
block|{
return|return
name|context
operator|.
name|getApplicationId
argument_list|()
return|;
block|}
DECL|method|getSubmitTime ()
specifier|public
name|long
name|getSubmitTime
parameter_list|()
block|{
return|return
name|submitTime
return|;
block|}
DECL|method|getAttemptCount ()
specifier|public
name|int
name|getAttemptCount
parameter_list|()
block|{
return|return
name|attempts
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|getApplicationSubmissionContext ()
specifier|public
name|ApplicationSubmissionContext
name|getApplicationSubmissionContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|getAttempt (ApplicationAttemptId attemptId)
specifier|public
name|ApplicationAttemptState
name|getAttempt
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|)
block|{
return|return
name|attempts
operator|.
name|get
argument_list|(
name|attemptId
argument_list|)
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|user
return|;
block|}
block|}
DECL|class|RMDTSecretManagerState
specifier|public
specifier|static
class|class
name|RMDTSecretManagerState
block|{
comment|// DTIdentifier -> renewDate
DECL|field|delegationTokenState
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|delegationTokenState
init|=
operator|new
name|HashMap
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|masterKeyState
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|masterKeyState
init|=
operator|new
name|HashSet
argument_list|<
name|DelegationKey
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|dtSequenceNumber
name|int
name|dtSequenceNumber
init|=
literal|0
decl_stmt|;
DECL|method|getTokenState ()
specifier|public
name|Map
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|,
name|Long
argument_list|>
name|getTokenState
parameter_list|()
block|{
return|return
name|delegationTokenState
return|;
block|}
DECL|method|getMasterKeyState ()
specifier|public
name|Set
argument_list|<
name|DelegationKey
argument_list|>
name|getMasterKeyState
parameter_list|()
block|{
return|return
name|masterKeyState
return|;
block|}
DECL|method|getDTSequenceNumber ()
specifier|public
name|int
name|getDTSequenceNumber
parameter_list|()
block|{
return|return
name|dtSequenceNumber
return|;
block|}
block|}
comment|/**    * State of the ResourceManager    */
DECL|class|RMState
specifier|public
specifier|static
class|class
name|RMState
block|{
DECL|field|appState
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationState
argument_list|>
name|appState
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationState
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|rmSecretManagerState
name|RMDTSecretManagerState
name|rmSecretManagerState
init|=
operator|new
name|RMDTSecretManagerState
argument_list|()
decl_stmt|;
DECL|method|getApplicationState ()
specifier|public
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationState
argument_list|>
name|getApplicationState
parameter_list|()
block|{
return|return
name|appState
return|;
block|}
DECL|method|getRMDTSecretManagerState ()
specifier|public
name|RMDTSecretManagerState
name|getRMDTSecretManagerState
parameter_list|()
block|{
return|return
name|rmSecretManagerState
return|;
block|}
block|}
DECL|field|rmDispatcher
specifier|private
name|Dispatcher
name|rmDispatcher
decl_stmt|;
comment|/**    * Dispatcher used to send state operation completion events to     * ResourceManager services    */
DECL|method|setDispatcher (Dispatcher dispatcher)
specifier|public
name|void
name|setDispatcher
parameter_list|(
name|Dispatcher
name|dispatcher
parameter_list|)
block|{
name|this
operator|.
name|rmDispatcher
operator|=
name|dispatcher
expr_stmt|;
block|}
DECL|field|dispatcher
name|AsyncDispatcher
name|dispatcher
decl_stmt|;
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
comment|// create async handler
name|dispatcher
operator|=
operator|new
name|AsyncDispatcher
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|RMStateStoreEventType
operator|.
name|class
argument_list|,
operator|new
name|ForwardingEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|initInternal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|/**    * Derived classes initialize themselves using this method.    * The base class is initialized and the event dispatcher is ready to use at    * this point    */
DECL|method|initInternal (Configuration conf)
specifier|protected
specifier|abstract
name|void
name|initInternal
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|Exception
block|{
name|closeInternal
argument_list|()
expr_stmt|;
name|dispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Derived classes close themselves using this method.    * The base class will be closed and the event dispatcher will be shutdown     * after this    */
DECL|method|closeInternal ()
specifier|protected
specifier|abstract
name|void
name|closeInternal
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Blocking API    * The derived class must recover state from the store and return a new     * RMState object populated with that state    * This must not be called on the dispatcher thread    */
DECL|method|loadState ()
specifier|public
specifier|abstract
name|RMState
name|loadState
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Non-Blocking API    * ResourceManager services use this to store the application's state    * This does not block the dispatcher threads    * RMAppStoredEvent will be sent on completion to notify the RMApp    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|storeApplication (RMApp app)
specifier|public
specifier|synchronized
name|void
name|storeApplication
parameter_list|(
name|RMApp
name|app
parameter_list|)
block|{
name|ApplicationSubmissionContext
name|context
init|=
name|app
operator|.
name|getApplicationSubmissionContext
argument_list|()
decl_stmt|;
assert|assert
name|context
operator|instanceof
name|ApplicationSubmissionContextPBImpl
assert|;
name|ApplicationState
name|appState
init|=
operator|new
name|ApplicationState
argument_list|(
name|app
operator|.
name|getSubmitTime
argument_list|()
argument_list|,
name|context
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMStateStoreAppEvent
argument_list|(
name|appState
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to store the state of an     * application.    */
DECL|method|storeApplicationState (String appId, ApplicationStateDataPBImpl appStateData)
specifier|protected
specifier|abstract
name|void
name|storeApplicationState
parameter_list|(
name|String
name|appId
parameter_list|,
name|ApplicationStateDataPBImpl
name|appStateData
parameter_list|)
throws|throws
name|Exception
function_decl|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|/**    * Non-blocking API    * ResourceManager services call this to store state on an application attempt    * This does not block the dispatcher threads    * RMAppAttemptStoredEvent will be sent on completion to notify the RMAppAttempt    */
DECL|method|storeApplicationAttempt (RMAppAttempt appAttempt)
specifier|public
specifier|synchronized
name|void
name|storeApplicationAttempt
parameter_list|(
name|RMAppAttempt
name|appAttempt
parameter_list|)
block|{
name|Credentials
name|credentials
init|=
name|getTokensFromAppAttempt
argument_list|(
name|appAttempt
argument_list|)
decl_stmt|;
name|ApplicationAttemptState
name|attemptState
init|=
operator|new
name|ApplicationAttemptState
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getMasterContainer
argument_list|()
argument_list|,
name|credentials
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMStateStoreAppAttemptEvent
argument_list|(
name|attemptState
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to store the state of an     * application attempt    */
DECL|method|storeApplicationAttemptState (String attemptId, ApplicationAttemptStateDataPBImpl attemptStateData)
specifier|protected
specifier|abstract
name|void
name|storeApplicationAttemptState
parameter_list|(
name|String
name|attemptId
parameter_list|,
name|ApplicationAttemptStateDataPBImpl
name|attemptStateData
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * RMDTSecretManager call this to store the state of a delegation token    * and sequence number    */
DECL|method|storeRMDelegationTokenAndSequenceNumber ( RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate, int latestSequenceNumber)
specifier|public
specifier|synchronized
name|void
name|storeRMDelegationTokenAndSequenceNumber
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|,
name|Long
name|renewDate
parameter_list|,
name|int
name|latestSequenceNumber
parameter_list|)
throws|throws
name|Exception
block|{
name|storeRMDelegationTokenAndSequenceNumberState
argument_list|(
name|rmDTIdentifier
argument_list|,
name|renewDate
argument_list|,
name|latestSequenceNumber
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to store the state of    * RMDelegationToken and sequence number    */
DECL|method|storeRMDelegationTokenAndSequenceNumberState ( RMDelegationTokenIdentifier rmDTIdentifier, Long renewDate, int latestSequenceNumber)
specifier|protected
specifier|abstract
name|void
name|storeRMDelegationTokenAndSequenceNumberState
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|,
name|Long
name|renewDate
parameter_list|,
name|int
name|latestSequenceNumber
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * RMDTSecretManager call this to remove the state of a delegation token    */
DECL|method|removeRMDelegationToken ( RMDelegationTokenIdentifier rmDTIdentifier, int sequenceNumber)
specifier|public
specifier|synchronized
name|void
name|removeRMDelegationToken
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|,
name|int
name|sequenceNumber
parameter_list|)
throws|throws
name|Exception
block|{
name|removeRMDelegationTokenState
argument_list|(
name|rmDTIdentifier
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to remove the state of RMDelegationToken    */
DECL|method|removeRMDelegationTokenState ( RMDelegationTokenIdentifier rmDTIdentifier)
specifier|protected
specifier|abstract
name|void
name|removeRMDelegationTokenState
parameter_list|(
name|RMDelegationTokenIdentifier
name|rmDTIdentifier
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * RMDTSecretManager call this to store the state of a master key    */
DECL|method|storeRMDTMasterKey (DelegationKey delegationKey)
specifier|public
specifier|synchronized
name|void
name|storeRMDTMasterKey
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
block|{
name|storeRMDTMasterKeyState
argument_list|(
name|delegationKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to store the state of    * DelegationToken Master Key    */
DECL|method|storeRMDTMasterKeyState (DelegationKey delegationKey)
specifier|protected
specifier|abstract
name|void
name|storeRMDTMasterKeyState
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * RMDTSecretManager call this to remove the state of a master key    */
DECL|method|removeRMDTMasterKey (DelegationKey delegationKey)
specifier|public
specifier|synchronized
name|void
name|removeRMDTMasterKey
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
block|{
name|removeRMDTMasterKeyState
argument_list|(
name|delegationKey
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to remove the state of    * DelegationToken Master Key    */
DECL|method|removeRMDTMasterKeyState (DelegationKey delegationKey)
specifier|protected
specifier|abstract
name|void
name|removeRMDTMasterKeyState
parameter_list|(
name|DelegationKey
name|delegationKey
parameter_list|)
throws|throws
name|Exception
function_decl|;
comment|/**    * Non-blocking API    * ResourceManager services call this to remove an application from the state    * store    * This does not block the dispatcher threads    * There is no notification of completion for this operation.    */
DECL|method|removeApplication (RMApp app)
specifier|public
specifier|synchronized
name|void
name|removeApplication
parameter_list|(
name|RMApp
name|app
parameter_list|)
block|{
name|ApplicationState
name|appState
init|=
operator|new
name|ApplicationState
argument_list|(
name|app
operator|.
name|getSubmitTime
argument_list|()
argument_list|,
name|app
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|RMAppAttempt
name|appAttempt
range|:
name|app
operator|.
name|getAppAttempts
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|Credentials
name|credentials
init|=
name|getTokensFromAppAttempt
argument_list|(
name|appAttempt
argument_list|)
decl_stmt|;
name|ApplicationAttemptState
name|attemptState
init|=
operator|new
name|ApplicationAttemptState
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getMasterContainer
argument_list|()
argument_list|,
name|credentials
argument_list|)
decl_stmt|;
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
name|removeApplication
argument_list|(
name|appState
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|/**    * Non-Blocking API    */
DECL|method|removeApplication (ApplicationState appState)
specifier|public
specifier|synchronized
name|void
name|removeApplication
parameter_list|(
name|ApplicationState
name|appState
parameter_list|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMStateStoreRemoveAppEvent
argument_list|(
name|appState
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Blocking API    * Derived classes must implement this method to remove the state of an     * application and its attempts    */
DECL|method|removeApplicationState (ApplicationState appState)
specifier|protected
specifier|abstract
name|void
name|removeApplicationState
parameter_list|(
name|ApplicationState
name|appState
parameter_list|)
throws|throws
name|Exception
function_decl|;
DECL|method|getTokensFromAppAttempt (RMAppAttempt appAttempt)
specifier|private
name|Credentials
name|getTokensFromAppAttempt
parameter_list|(
name|RMAppAttempt
name|appAttempt
parameter_list|)
block|{
name|Credentials
name|credentials
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
name|appToken
init|=
name|appAttempt
operator|.
name|getApplicationToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|appToken
operator|!=
literal|null
condition|)
block|{
name|credentials
operator|.
name|addToken
argument_list|(
name|appToken
operator|.
name|getService
argument_list|()
argument_list|,
name|appToken
argument_list|)
expr_stmt|;
block|}
name|Token
argument_list|<
name|ClientTokenIdentifier
argument_list|>
name|clientToken
init|=
name|appAttempt
operator|.
name|getClientToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|clientToken
operator|!=
literal|null
condition|)
block|{
name|credentials
operator|.
name|addToken
argument_list|(
name|clientToken
operator|.
name|getService
argument_list|()
argument_list|,
name|clientToken
argument_list|)
expr_stmt|;
block|}
return|return
name|credentials
return|;
block|}
comment|// Dispatcher related code
DECL|method|handleStoreEvent (RMStateStoreEvent event)
specifier|private
specifier|synchronized
name|void
name|handleStoreEvent
parameter_list|(
name|RMStateStoreEvent
name|event
parameter_list|)
block|{
switch|switch
condition|(
name|event
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|STORE_APP
case|:
block|{
name|ApplicationState
name|apptState
init|=
operator|(
operator|(
name|RMStateStoreAppEvent
operator|)
name|event
operator|)
operator|.
name|getAppState
argument_list|()
decl_stmt|;
name|Exception
name|storedException
init|=
literal|null
decl_stmt|;
name|ApplicationStateDataPBImpl
name|appStateData
init|=
operator|new
name|ApplicationStateDataPBImpl
argument_list|()
decl_stmt|;
name|appStateData
operator|.
name|setSubmitTime
argument_list|(
name|apptState
operator|.
name|getSubmitTime
argument_list|()
argument_list|)
expr_stmt|;
name|appStateData
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|apptState
operator|.
name|getApplicationSubmissionContext
argument_list|()
argument_list|)
expr_stmt|;
name|appStateData
operator|.
name|setUser
argument_list|(
name|apptState
operator|.
name|getUser
argument_list|()
argument_list|)
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|apptState
operator|.
name|getApplicationSubmissionContext
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing info for app: "
operator|+
name|appId
argument_list|)
expr_stmt|;
try|try
block|{
name|storeApplicationState
argument_list|(
name|appId
operator|.
name|toString
argument_list|()
argument_list|,
name|appStateData
argument_list|)
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
literal|"Error storing app: "
operator|+
name|appId
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|storedException
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|notifyDoneStoringApplication
argument_list|(
name|appId
argument_list|,
name|storedException
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|STORE_APP_ATTEMPT
case|:
block|{
name|ApplicationAttemptState
name|attemptState
init|=
operator|(
operator|(
name|RMStateStoreAppAttemptEvent
operator|)
name|event
operator|)
operator|.
name|getAppAttemptState
argument_list|()
decl_stmt|;
name|Exception
name|storedException
init|=
literal|null
decl_stmt|;
name|Credentials
name|credentials
init|=
name|attemptState
operator|.
name|getAppAttemptTokens
argument_list|()
decl_stmt|;
name|ByteBuffer
name|appAttemptTokens
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|credentials
operator|!=
literal|null
condition|)
block|{
name|DataOutputBuffer
name|dob
init|=
operator|new
name|DataOutputBuffer
argument_list|()
decl_stmt|;
name|credentials
operator|.
name|writeTokenStorageToStream
argument_list|(
name|dob
argument_list|)
expr_stmt|;
name|appAttemptTokens
operator|=
name|ByteBuffer
operator|.
name|wrap
argument_list|(
name|dob
operator|.
name|getData
argument_list|()
argument_list|,
literal|0
argument_list|,
name|dob
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|ApplicationAttemptStateDataPBImpl
name|attemptStateData
init|=
operator|(
name|ApplicationAttemptStateDataPBImpl
operator|)
name|ApplicationAttemptStateDataPBImpl
operator|.
name|newApplicationAttemptStateData
argument_list|(
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|,
name|attemptState
operator|.
name|getMasterContainer
argument_list|()
argument_list|,
name|appAttemptTokens
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing info for attempt: "
operator|+
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|)
expr_stmt|;
name|storeApplicationAttemptState
argument_list|(
name|attemptState
operator|.
name|getAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|attemptStateData
argument_list|)
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
literal|"Error storing appAttempt: "
operator|+
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|storedException
operator|=
name|e
expr_stmt|;
block|}
finally|finally
block|{
name|notifyDoneStoringApplicationAttempt
argument_list|(
name|attemptState
operator|.
name|getAttemptId
argument_list|()
argument_list|,
name|storedException
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
case|case
name|REMOVE_APP
case|:
block|{
name|ApplicationState
name|appState
init|=
operator|(
operator|(
name|RMStateStoreRemoveAppEvent
operator|)
name|event
operator|)
operator|.
name|getAppState
argument_list|()
decl_stmt|;
name|ApplicationId
name|appId
init|=
name|appState
operator|.
name|getAppId
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Removing info for app: "
operator|+
name|appId
argument_list|)
expr_stmt|;
try|try
block|{
name|removeApplicationState
argument_list|(
name|appState
argument_list|)
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
literal|"Error removing app: "
operator|+
name|appId
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown RMStateStoreEvent type: "
operator|+
name|event
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|/**    * In (@link handleStoreEvent}, this method is called to notify the    * application about operation completion    * @param appId id of the application that has been saved    * @param storedException the exception that is thrown when storing the    * application    */
DECL|method|notifyDoneStoringApplication (ApplicationId appId, Exception storedException)
specifier|private
name|void
name|notifyDoneStoringApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|,
name|Exception
name|storedException
parameter_list|)
block|{
name|rmDispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppStoredEvent
argument_list|(
name|appId
argument_list|,
name|storedException
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
comment|/**    * In (@link handleStoreEvent}, this method is called to notify the    * application attempt about operation completion    * @param appAttempt attempt that has been saved    */
DECL|method|notifyDoneStoringApplicationAttempt (ApplicationAttemptId attemptId, Exception storedException)
specifier|private
name|void
name|notifyDoneStoringApplicationAttempt
parameter_list|(
name|ApplicationAttemptId
name|attemptId
parameter_list|,
name|Exception
name|storedException
parameter_list|)
block|{
name|rmDispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|RMAppAttemptStoredEvent
argument_list|(
name|attemptId
argument_list|,
name|storedException
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * EventHandler implementation which forward events to the FSRMStateStore    * This hides the EventHandle methods of the store from its public interface     */
DECL|class|ForwardingEventHandler
specifier|private
specifier|final
class|class
name|ForwardingEventHandler
implements|implements
name|EventHandler
argument_list|<
name|RMStateStoreEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (RMStateStoreEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMStateStoreEvent
name|event
parameter_list|)
block|{
name|handleStoreEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

