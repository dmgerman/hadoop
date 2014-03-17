begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.ahs
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
name|ahs
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
name|List
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
name|service
operator|.
name|CompositeService
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
name|conf
operator|.
name|YarnConfiguration
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
name|Event
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|ApplicationHistoryStore
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
name|applicationhistoryservice
operator|.
name|ApplicationHistoryWriter
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
name|applicationhistoryservice
operator|.
name|FileSystemApplicationHistoryStore
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
name|applicationhistoryservice
operator|.
name|NullApplicationHistoryStore
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptFinishData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptStartData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationFinishData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationStartData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerFinishData
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
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ContainerStartData
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
name|RMServerUtils
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
name|ResourceManager
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
name|RMAppState
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
name|RMAppAttemptState
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
name|rmcontainer
operator|.
name|RMContainer
import|;
end_import

begin_comment
comment|/**  *<p>  * {@link ResourceManager} uses this class to write the information of  * {@link RMApp}, {@link RMAppAttempt} and {@link RMContainer}. These APIs are  * non-blocking, and just schedule a writing history event. An self-contained  * dispatcher vector will handle the event in separate threads, and extract the  * required fields that are going to be persisted. Then, the extracted  * information will be persisted via the implementation of  * {@link ApplicationHistoryStore}.  *</p>  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|RMApplicationHistoryWriter
specifier|public
class|class
name|RMApplicationHistoryWriter
extends|extends
name|CompositeService
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
name|RMApplicationHistoryWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|dispatcher
specifier|private
name|Dispatcher
name|dispatcher
decl_stmt|;
DECL|field|writer
specifier|private
name|ApplicationHistoryWriter
name|writer
decl_stmt|;
DECL|field|historyServiceEnabled
specifier|private
name|boolean
name|historyServiceEnabled
decl_stmt|;
DECL|method|RMApplicationHistoryWriter ()
specifier|public
name|RMApplicationHistoryWriter
parameter_list|()
block|{
name|super
argument_list|(
name|RMApplicationHistoryWriter
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
DECL|method|serviceInit (Configuration conf)
specifier|protected
specifier|synchronized
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|historyServiceEnabled
operator|=
name|conf
operator|.
name|getBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_ENABLED
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_APPLICATION_HISTORY_ENABLED
argument_list|)
expr_stmt|;
name|writer
operator|=
name|createApplicationHistoryStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|addIfService
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|dispatcher
operator|=
name|createDispatcher
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|dispatcher
operator|.
name|register
argument_list|(
name|WritingHistoryEventType
operator|.
name|class
argument_list|,
operator|new
name|ForwardingEventHandler
argument_list|()
argument_list|)
expr_stmt|;
name|addIfService
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|createDispatcher (Configuration conf)
specifier|protected
name|Dispatcher
name|createDispatcher
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|MultiThreadedDispatcher
name|dispatcher
init|=
operator|new
name|MultiThreadedDispatcher
argument_list|(
name|conf
operator|.
name|getInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_HISTORY_WRITER_MULTI_THREADED_DISPATCHER_POOL_SIZE
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_HISTORY_WRITER_MULTI_THREADED_DISPATCHER_POOL_SIZE
argument_list|)
argument_list|)
decl_stmt|;
name|dispatcher
operator|.
name|setDrainEventsOnStop
argument_list|()
expr_stmt|;
return|return
name|dispatcher
return|;
block|}
DECL|method|createApplicationHistoryStore ( Configuration conf)
specifier|protected
name|ApplicationHistoryStore
name|createApplicationHistoryStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
comment|// If the history writer is not enabled, a dummy store will be used to
comment|// write nothing
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
extends|extends
name|ApplicationHistoryStore
argument_list|>
name|storeClass
init|=
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_STORE
argument_list|,
name|FileSystemApplicationHistoryStore
operator|.
name|class
argument_list|,
name|ApplicationHistoryStore
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|storeClass
operator|.
name|newInstance
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"Could not instantiate ApplicationHistoryWriter: "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|APPLICATION_HISTORY_STORE
argument_list|,
name|FileSystemApplicationHistoryStore
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
operator|new
name|NullApplicationHistoryStore
argument_list|()
return|;
block|}
block|}
DECL|method|handleWritingApplicationHistoryEvent ( WritingApplicationHistoryEvent event)
specifier|protected
name|void
name|handleWritingApplicationHistoryEvent
parameter_list|(
name|WritingApplicationHistoryEvent
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
name|APP_START
case|:
name|WritingApplicationStartEvent
name|wasEvent
init|=
operator|(
name|WritingApplicationStartEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|applicationStarted
argument_list|(
name|wasEvent
operator|.
name|getApplicationStartData
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored the start data of application "
operator|+
name|wasEvent
operator|.
name|getApplicationId
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when storing the start data of application "
operator|+
name|wasEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|APP_FINISH
case|:
name|WritingApplicationFinishEvent
name|wafEvent
init|=
operator|(
name|WritingApplicationFinishEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|applicationFinished
argument_list|(
name|wafEvent
operator|.
name|getApplicationFinishData
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored the finish data of application "
operator|+
name|wafEvent
operator|.
name|getApplicationId
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when storing the finish data of application "
operator|+
name|wafEvent
operator|.
name|getApplicationId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|APP_ATTEMPT_START
case|:
name|WritingApplicationAttemptStartEvent
name|waasEvent
init|=
operator|(
name|WritingApplicationAttemptStartEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|applicationAttemptStarted
argument_list|(
name|waasEvent
operator|.
name|getApplicationAttemptStartData
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored the start data of application attempt "
operator|+
name|waasEvent
operator|.
name|getApplicationAttemptId
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when storing the start data of application attempt "
operator|+
name|waasEvent
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|APP_ATTEMPT_FINISH
case|:
name|WritingApplicationAttemptFinishEvent
name|waafEvent
init|=
operator|(
name|WritingApplicationAttemptFinishEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|applicationAttemptFinished
argument_list|(
name|waafEvent
operator|.
name|getApplicationAttemptFinishData
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored the finish data of application attempt "
operator|+
name|waafEvent
operator|.
name|getApplicationAttemptId
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when storing the finish data of application attempt "
operator|+
name|waafEvent
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|CONTAINER_START
case|:
name|WritingContainerStartEvent
name|wcsEvent
init|=
operator|(
name|WritingContainerStartEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|containerStarted
argument_list|(
name|wcsEvent
operator|.
name|getContainerStartData
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored the start data of container "
operator|+
name|wcsEvent
operator|.
name|getContainerId
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when storing the start data of container "
operator|+
name|wcsEvent
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|CONTAINER_FINISH
case|:
name|WritingContainerFinishEvent
name|wcfEvent
init|=
operator|(
name|WritingContainerFinishEvent
operator|)
name|event
decl_stmt|;
try|try
block|{
name|writer
operator|.
name|containerFinished
argument_list|(
name|wcfEvent
operator|.
name|getContainerFinishData
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stored the finish data of container "
operator|+
name|wcfEvent
operator|.
name|getContainerId
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error when storing the finish data of container "
operator|+
name|wcfEvent
operator|.
name|getContainerId
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown WritingApplicationHistoryEvent type: "
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
DECL|method|applicationStarted (RMApp app)
specifier|public
name|void
name|applicationStarted
parameter_list|(
name|RMApp
name|app
parameter_list|)
block|{
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|WritingApplicationStartEvent
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|ApplicationStartData
operator|.
name|newInstance
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app
operator|.
name|getName
argument_list|()
argument_list|,
name|app
operator|.
name|getApplicationType
argument_list|()
argument_list|,
name|app
operator|.
name|getQueue
argument_list|()
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|app
operator|.
name|getSubmitTime
argument_list|()
argument_list|,
name|app
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applicationFinished (RMApp app, RMAppState finalState)
specifier|public
name|void
name|applicationFinished
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|RMAppState
name|finalState
parameter_list|)
block|{
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|WritingApplicationFinishEvent
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|ApplicationFinishData
operator|.
name|newInstance
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|app
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|app
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|app
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|,
name|RMServerUtils
operator|.
name|createApplicationState
argument_list|(
name|finalState
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applicationAttemptStarted (RMAppAttempt appAttempt)
specifier|public
name|void
name|applicationAttemptStarted
parameter_list|(
name|RMAppAttempt
name|appAttempt
parameter_list|)
block|{
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|WritingApplicationAttemptStartEvent
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|ApplicationAttemptStartData
operator|.
name|newInstance
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getHost
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getRpcPort
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getMasterContainer
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applicationAttemptFinished (RMAppAttempt appAttempt, RMAppAttemptState finalState)
specifier|public
name|void
name|applicationAttemptFinished
parameter_list|(
name|RMAppAttempt
name|appAttempt
parameter_list|,
name|RMAppAttemptState
name|finalState
parameter_list|)
block|{
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|WritingApplicationAttemptFinishEvent
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|ApplicationAttemptFinishData
operator|.
name|newInstance
argument_list|(
name|appAttempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getTrackingUrl
argument_list|()
argument_list|,
name|appAttempt
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|,
name|RMServerUtils
operator|.
name|createApplicationAttemptState
argument_list|(
name|finalState
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|containerStarted (RMContainer container)
specifier|public
name|void
name|containerStarted
parameter_list|(
name|RMContainer
name|container
parameter_list|)
block|{
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|WritingContainerStartEvent
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|ContainerStartData
operator|.
name|newInstance
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|container
operator|.
name|getAllocatedResource
argument_list|()
argument_list|,
name|container
operator|.
name|getAllocatedNode
argument_list|()
argument_list|,
name|container
operator|.
name|getAllocatedPriority
argument_list|()
argument_list|,
name|container
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|containerFinished (RMContainer container)
specifier|public
name|void
name|containerFinished
parameter_list|(
name|RMContainer
name|container
parameter_list|)
block|{
if|if
condition|(
name|historyServiceEnabled
condition|)
block|{
name|dispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|WritingContainerFinishEvent
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|ContainerFinishData
operator|.
name|newInstance
argument_list|(
name|container
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|container
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|container
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerExitStatus
argument_list|()
argument_list|,
name|container
operator|.
name|getContainerState
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * EventHandler implementation which forward events to HistoryWriter Making    * use of it, HistoryWriter can avoid to have a public handle method    */
DECL|class|ForwardingEventHandler
specifier|private
specifier|final
class|class
name|ForwardingEventHandler
implements|implements
name|EventHandler
argument_list|<
name|WritingApplicationHistoryEvent
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (WritingApplicationHistoryEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|WritingApplicationHistoryEvent
name|event
parameter_list|)
block|{
name|handleWritingApplicationHistoryEvent
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
block|{
literal|"rawtypes"
block|,
literal|"unchecked"
block|}
argument_list|)
DECL|class|MultiThreadedDispatcher
specifier|protected
specifier|static
class|class
name|MultiThreadedDispatcher
extends|extends
name|CompositeService
implements|implements
name|Dispatcher
block|{
DECL|field|dispatchers
specifier|private
name|List
argument_list|<
name|AsyncDispatcher
argument_list|>
name|dispatchers
init|=
operator|new
name|ArrayList
argument_list|<
name|AsyncDispatcher
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|MultiThreadedDispatcher (int num)
specifier|public
name|MultiThreadedDispatcher
parameter_list|(
name|int
name|num
parameter_list|)
block|{
name|super
argument_list|(
name|MultiThreadedDispatcher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|num
condition|;
operator|++
name|i
control|)
block|{
name|AsyncDispatcher
name|dispatcher
init|=
name|createDispatcher
argument_list|()
decl_stmt|;
name|dispatchers
operator|.
name|add
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
name|addIfService
argument_list|(
name|dispatcher
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getEventHandler ()
specifier|public
name|EventHandler
name|getEventHandler
parameter_list|()
block|{
return|return
operator|new
name|CompositEventHandler
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|register (Class<? extends Enum> eventType, EventHandler handler)
specifier|public
name|void
name|register
parameter_list|(
name|Class
argument_list|<
name|?
extends|extends
name|Enum
argument_list|>
name|eventType
parameter_list|,
name|EventHandler
name|handler
parameter_list|)
block|{
for|for
control|(
name|AsyncDispatcher
name|dispatcher
range|:
name|dispatchers
control|)
block|{
name|dispatcher
operator|.
name|register
argument_list|(
name|eventType
argument_list|,
name|handler
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setDrainEventsOnStop ()
specifier|public
name|void
name|setDrainEventsOnStop
parameter_list|()
block|{
for|for
control|(
name|AsyncDispatcher
name|dispatcher
range|:
name|dispatchers
control|)
block|{
name|dispatcher
operator|.
name|setDrainEventsOnStop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|CompositEventHandler
specifier|private
class|class
name|CompositEventHandler
implements|implements
name|EventHandler
argument_list|<
name|Event
argument_list|>
block|{
annotation|@
name|Override
DECL|method|handle (Event event)
specifier|public
name|void
name|handle
parameter_list|(
name|Event
name|event
parameter_list|)
block|{
comment|// Use hashCode (of ApplicationId) to dispatch the event to the child
comment|// dispatcher, such that all the writing events of one application will
comment|// be handled by one thread, the scheduled order of the these events
comment|// will be preserved
name|int
name|index
init|=
operator|(
name|event
operator|.
name|hashCode
argument_list|()
operator|&
name|Integer
operator|.
name|MAX_VALUE
operator|)
operator|%
name|dispatchers
operator|.
name|size
argument_list|()
decl_stmt|;
name|dispatchers
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createDispatcher ()
specifier|protected
name|AsyncDispatcher
name|createDispatcher
parameter_list|()
block|{
return|return
operator|new
name|AsyncDispatcher
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

