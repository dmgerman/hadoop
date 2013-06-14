begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.rm
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|rm
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|PrivilegedAction
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
name|concurrent
operator|.
name|ConcurrentLinkedQueue
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
name|atomic
operator|.
name|AtomicBoolean
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
name|mapreduce
operator|.
name|JobID
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|TypeConverter
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|JobId
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|client
operator|.
name|ClientService
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|JobStateInternal
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|impl
operator|.
name|JobImpl
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
name|mapreduce
operator|.
name|v2
operator|.
name|jobhistory
operator|.
name|JobHistoryUtils
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
name|yarn
operator|.
name|api
operator|.
name|AMRMProtocol
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
name|ApplicationAccessType
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
name|FinalApplicationStatus
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|ipc
operator|.
name|YarnRPC
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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_comment
comment|/**  * Registers/unregisters to RM and sends heartbeats to RM.  */
end_comment

begin_class
DECL|class|RMCommunicator
specifier|public
specifier|abstract
class|class
name|RMCommunicator
extends|extends
name|AbstractService
implements|implements
name|RMHeartbeatHandler
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|RMContainerAllocator
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|rmPollInterval
specifier|private
name|int
name|rmPollInterval
decl_stmt|;
comment|//millis
DECL|field|applicationId
specifier|protected
name|ApplicationId
name|applicationId
decl_stmt|;
DECL|field|applicationAttemptId
specifier|protected
name|ApplicationAttemptId
name|applicationAttemptId
decl_stmt|;
DECL|field|stopped
specifier|private
specifier|final
name|AtomicBoolean
name|stopped
decl_stmt|;
DECL|field|allocatorThread
specifier|protected
name|Thread
name|allocatorThread
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|field|eventHandler
specifier|protected
name|EventHandler
name|eventHandler
decl_stmt|;
DECL|field|scheduler
specifier|protected
name|AMRMProtocol
name|scheduler
decl_stmt|;
DECL|field|clientService
specifier|private
specifier|final
name|ClientService
name|clientService
decl_stmt|;
DECL|field|lastResponseID
specifier|protected
name|int
name|lastResponseID
decl_stmt|;
DECL|field|maxContainerCapability
specifier|private
name|Resource
name|maxContainerCapability
decl_stmt|;
DECL|field|applicationACLs
specifier|protected
name|Map
argument_list|<
name|ApplicationAccessType
argument_list|,
name|String
argument_list|>
name|applicationACLs
decl_stmt|;
DECL|field|lastHeartbeatTime
specifier|private
specifier|volatile
name|long
name|lastHeartbeatTime
decl_stmt|;
DECL|field|heartbeatCallbacks
specifier|private
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
name|heartbeatCallbacks
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|final
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
specifier|final
name|AppContext
name|context
decl_stmt|;
DECL|field|job
specifier|private
name|Job
name|job
decl_stmt|;
comment|// Has a signal (SIGTERM etc) been issued?
DECL|field|isSignalled
specifier|protected
specifier|volatile
name|boolean
name|isSignalled
init|=
literal|false
decl_stmt|;
DECL|field|shouldUnregister
specifier|private
specifier|volatile
name|boolean
name|shouldUnregister
init|=
literal|true
decl_stmt|;
DECL|method|RMCommunicator (ClientService clientService, AppContext context)
specifier|public
name|RMCommunicator
parameter_list|(
name|ClientService
name|clientService
parameter_list|,
name|AppContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
literal|"RMCommunicator"
argument_list|)
expr_stmt|;
name|this
operator|.
name|clientService
operator|=
name|clientService
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|eventHandler
operator|=
name|context
operator|.
name|getEventHandler
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|context
operator|.
name|getApplicationID
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationAttemptId
operator|=
name|context
operator|.
name|getApplicationAttemptId
argument_list|()
expr_stmt|;
name|this
operator|.
name|stopped
operator|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|this
operator|.
name|heartbeatCallbacks
operator|=
operator|new
name|ConcurrentLinkedQueue
argument_list|<
name|Runnable
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rmPollInterval
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_TO_RM_HEARTBEAT_INTERVAL_MS
argument_list|)
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
name|scheduler
operator|=
name|createSchedulerProxy
argument_list|()
expr_stmt|;
name|register
argument_list|()
expr_stmt|;
name|startAllocatorThread
argument_list|()
expr_stmt|;
name|JobID
name|id
init|=
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|this
operator|.
name|applicationId
argument_list|)
decl_stmt|;
name|JobId
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|job
operator|=
name|context
operator|.
name|getJob
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|getContext ()
specifier|protected
name|AppContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
DECL|method|getJob ()
specifier|protected
name|Job
name|getJob
parameter_list|()
block|{
return|return
name|job
return|;
block|}
comment|/**    * Get the appProgress. Can be used only after this component is started.    * @return the appProgress.    */
DECL|method|getApplicationProgress ()
specifier|protected
name|float
name|getApplicationProgress
parameter_list|()
block|{
comment|// For now just a single job. In future when we have a DAG, we need an
comment|// aggregate progress.
return|return
name|this
operator|.
name|job
operator|.
name|getProgress
argument_list|()
return|;
block|}
DECL|method|register ()
specifier|protected
name|void
name|register
parameter_list|()
block|{
comment|//Register
name|InetSocketAddress
name|serviceAddr
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|clientService
operator|!=
literal|null
condition|)
block|{
name|serviceAddr
operator|=
name|clientService
operator|.
name|getBindAddress
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|RegisterApplicationMasterRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|RegisterApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationAttemptId
argument_list|(
name|applicationAttemptId
argument_list|)
expr_stmt|;
if|if
condition|(
name|serviceAddr
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|setHost
argument_list|(
name|serviceAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRpcPort
argument_list|(
name|serviceAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTrackingUrl
argument_list|(
name|serviceAddr
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|clientService
operator|.
name|getHttpPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|RegisterApplicationMasterResponse
name|response
init|=
name|scheduler
operator|.
name|registerApplicationMaster
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|maxContainerCapability
operator|=
name|response
operator|.
name|getMaximumResourceCapability
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|.
name|getClusterInfo
argument_list|()
operator|.
name|setMaxContainerCapability
argument_list|(
name|maxContainerCapability
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationACLs
operator|=
name|response
operator|.
name|getApplicationACLs
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"maxContainerCapability: "
operator|+
name|maxContainerCapability
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|are
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while registering"
argument_list|,
name|are
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|are
argument_list|)
throw|;
block|}
block|}
DECL|method|unregister ()
specifier|protected
name|void
name|unregister
parameter_list|()
block|{
try|try
block|{
name|FinalApplicationStatus
name|finishState
init|=
name|FinalApplicationStatus
operator|.
name|UNDEFINED
decl_stmt|;
name|JobImpl
name|jobImpl
init|=
operator|(
name|JobImpl
operator|)
name|job
decl_stmt|;
if|if
condition|(
name|jobImpl
operator|.
name|getInternalState
argument_list|()
operator|==
name|JobStateInternal
operator|.
name|SUCCEEDED
condition|)
block|{
name|finishState
operator|=
name|FinalApplicationStatus
operator|.
name|SUCCEEDED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jobImpl
operator|.
name|getInternalState
argument_list|()
operator|==
name|JobStateInternal
operator|.
name|KILLED
operator|||
operator|(
name|jobImpl
operator|.
name|getInternalState
argument_list|()
operator|==
name|JobStateInternal
operator|.
name|RUNNING
operator|&&
name|isSignalled
operator|)
condition|)
block|{
name|finishState
operator|=
name|FinalApplicationStatus
operator|.
name|KILLED
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|jobImpl
operator|.
name|getInternalState
argument_list|()
operator|==
name|JobStateInternal
operator|.
name|FAILED
operator|||
name|jobImpl
operator|.
name|getInternalState
argument_list|()
operator|==
name|JobStateInternal
operator|.
name|ERROR
condition|)
block|{
name|finishState
operator|=
name|FinalApplicationStatus
operator|.
name|FAILED
expr_stmt|;
block|}
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|job
operator|.
name|getDiagnostics
argument_list|()
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Setting job diagnostics to "
operator|+
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|historyUrl
init|=
name|JobHistoryUtils
operator|.
name|getHistoryUrl
argument_list|(
name|getConfig
argument_list|()
argument_list|,
name|context
operator|.
name|getApplicationID
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"History url is "
operator|+
name|historyUrl
argument_list|)
expr_stmt|;
name|FinishApplicationMasterRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FinishApplicationMasterRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setAppAttemptId
argument_list|(
name|this
operator|.
name|applicationAttemptId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setFinishApplicationStatus
argument_list|(
name|finishState
argument_list|)
expr_stmt|;
name|request
operator|.
name|setDiagnostics
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTrackingUrl
argument_list|(
name|historyUrl
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|finishApplicationMaster
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|are
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Exception while unregistering "
argument_list|,
name|are
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getMaxContainerCapability ()
specifier|protected
name|Resource
name|getMaxContainerCapability
parameter_list|()
block|{
return|return
name|maxContainerCapability
return|;
block|}
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
if|if
condition|(
name|stopped
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
condition|)
block|{
comment|// return if already stopped
return|return;
block|}
if|if
condition|(
name|allocatorThread
operator|!=
literal|null
condition|)
block|{
name|allocatorThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|allocatorThread
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"InterruptedException while stopping"
argument_list|,
name|ie
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|shouldUnregister
condition|)
block|{
name|unregister
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|startAllocatorThread ()
specifier|protected
name|void
name|startAllocatorThread
parameter_list|()
block|{
name|allocatorThread
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
operator|&&
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|rmPollInterval
argument_list|)
expr_stmt|;
try|try
block|{
name|heartbeat
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error communicating with RM: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return;
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
literal|"ERROR IN CONTACTING RM. "
argument_list|,
name|e
argument_list|)
expr_stmt|;
continue|continue;
comment|// TODO: for other exceptions
block|}
name|lastHeartbeatTime
operator|=
name|context
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|executeHeartbeatCallbacks
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
if|if
condition|(
operator|!
name|stopped
operator|.
name|get
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Allocated thread interrupted. Returning."
argument_list|)
expr_stmt|;
block|}
return|return;
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|allocatorThread
operator|.
name|setName
argument_list|(
literal|"RMCommunicator Allocator"
argument_list|)
expr_stmt|;
name|allocatorThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|createSchedulerProxy ()
specifier|protected
name|AMRMProtocol
name|createSchedulerProxy
parameter_list|()
block|{
specifier|final
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
specifier|final
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|InetSocketAddress
name|serviceAddr
init|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_SCHEDULER_PORT
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|currentUser
decl_stmt|;
try|try
block|{
name|currentUser
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
comment|// CurrentUser should already have AMToken loaded.
return|return
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|AMRMProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|AMRMProtocol
name|run
parameter_list|()
block|{
return|return
operator|(
name|AMRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|AMRMProtocol
operator|.
name|class
argument_list|,
name|serviceAddr
argument_list|,
name|conf
argument_list|)
return|;
block|}
block|}
argument_list|)
return|;
block|}
DECL|method|heartbeat ()
specifier|protected
specifier|abstract
name|void
name|heartbeat
parameter_list|()
throws|throws
name|Exception
function_decl|;
DECL|method|executeHeartbeatCallbacks ()
specifier|private
name|void
name|executeHeartbeatCallbacks
parameter_list|()
block|{
name|Runnable
name|callback
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|callback
operator|=
name|heartbeatCallbacks
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|callback
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|getLastHeartbeatTime ()
specifier|public
name|long
name|getLastHeartbeatTime
parameter_list|()
block|{
return|return
name|lastHeartbeatTime
return|;
block|}
annotation|@
name|Override
DECL|method|runOnNextHeartbeat (Runnable callback)
specifier|public
name|void
name|runOnNextHeartbeat
parameter_list|(
name|Runnable
name|callback
parameter_list|)
block|{
name|heartbeatCallbacks
operator|.
name|add
argument_list|(
name|callback
argument_list|)
expr_stmt|;
block|}
DECL|method|setShouldUnregister (boolean shouldUnregister)
specifier|public
name|void
name|setShouldUnregister
parameter_list|(
name|boolean
name|shouldUnregister
parameter_list|)
block|{
name|this
operator|.
name|shouldUnregister
operator|=
name|shouldUnregister
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RMCommunicator notified that shouldUnregistered is: "
operator|+
name|shouldUnregister
argument_list|)
expr_stmt|;
block|}
DECL|method|setSignalled (boolean isSignalled)
specifier|public
name|void
name|setSignalled
parameter_list|(
name|boolean
name|isSignalled
parameter_list|)
block|{
name|this
operator|.
name|isSignalled
operator|=
name|isSignalled
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"RMCommunicator notified that iSignalled is: "
operator|+
name|isSignalled
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

