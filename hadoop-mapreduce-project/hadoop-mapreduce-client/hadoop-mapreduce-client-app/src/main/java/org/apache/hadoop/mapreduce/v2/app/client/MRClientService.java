begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.app.client
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
name|client
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
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|fs
operator|.
name|CommonConfigurationKeysPublic
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
name|ipc
operator|.
name|Server
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
name|MRClientProtocol
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
name|protocolrecords
operator|.
name|CancelDelegationTokenRequest
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
name|protocolrecords
operator|.
name|CancelDelegationTokenResponse
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
name|protocolrecords
operator|.
name|FailTaskAttemptRequest
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
name|protocolrecords
operator|.
name|FailTaskAttemptResponse
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
name|protocolrecords
operator|.
name|GetCountersRequest
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
name|protocolrecords
operator|.
name|GetCountersResponse
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
name|protocolrecords
operator|.
name|GetDelegationTokenRequest
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
name|protocolrecords
operator|.
name|GetDelegationTokenResponse
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
name|protocolrecords
operator|.
name|GetDiagnosticsRequest
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
name|protocolrecords
operator|.
name|GetDiagnosticsResponse
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
name|protocolrecords
operator|.
name|GetJobReportRequest
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
name|protocolrecords
operator|.
name|GetJobReportResponse
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
name|protocolrecords
operator|.
name|GetTaskAttemptCompletionEventsRequest
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
name|protocolrecords
operator|.
name|GetTaskAttemptCompletionEventsResponse
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
name|protocolrecords
operator|.
name|GetTaskAttemptReportRequest
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
name|protocolrecords
operator|.
name|GetTaskAttemptReportResponse
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
name|protocolrecords
operator|.
name|GetTaskReportRequest
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
name|protocolrecords
operator|.
name|GetTaskReportResponse
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
name|protocolrecords
operator|.
name|GetTaskReportsRequest
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
name|protocolrecords
operator|.
name|GetTaskReportsResponse
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
name|protocolrecords
operator|.
name|KillJobRequest
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
name|protocolrecords
operator|.
name|KillJobResponse
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
name|protocolrecords
operator|.
name|KillTaskAttemptRequest
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
name|protocolrecords
operator|.
name|KillTaskAttemptResponse
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
name|protocolrecords
operator|.
name|KillTaskRequest
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
name|protocolrecords
operator|.
name|KillTaskResponse
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
name|protocolrecords
operator|.
name|RenewDelegationTokenRequest
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
name|protocolrecords
operator|.
name|RenewDelegationTokenResponse
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
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
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
name|TaskId
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
name|TaskType
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
name|Task
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
name|TaskAttempt
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
name|event
operator|.
name|JobDiagnosticsUpdateEvent
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
name|event
operator|.
name|JobEvent
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
name|event
operator|.
name|JobEventType
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
name|event
operator|.
name|TaskAttemptDiagnosticsUpdateEvent
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
name|event
operator|.
name|TaskAttemptEvent
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
name|event
operator|.
name|TaskAttemptEventType
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
name|event
operator|.
name|TaskEvent
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
name|event
operator|.
name|TaskEventType
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
name|security
operator|.
name|authorize
operator|.
name|MRAMPolicyProvider
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
name|webapp
operator|.
name|AMWebApp
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
name|net
operator|.
name|NetUtils
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
name|authorize
operator|.
name|PolicyProvider
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
name|webapp
operator|.
name|WebApp
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
name|webapp
operator|.
name|WebApps
import|;
end_import

begin_comment
comment|/**  * This module is responsible for talking to the   * jobclient (user facing).  *  */
end_comment

begin_class
DECL|class|MRClientService
specifier|public
class|class
name|MRClientService
extends|extends
name|AbstractService
implements|implements
name|ClientService
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|MRClientService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|protocolHandler
specifier|private
name|MRClientProtocol
name|protocolHandler
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|webApp
specifier|private
name|WebApp
name|webApp
decl_stmt|;
DECL|field|bindAddress
specifier|private
name|InetSocketAddress
name|bindAddress
decl_stmt|;
DECL|field|appContext
specifier|private
name|AppContext
name|appContext
decl_stmt|;
DECL|method|MRClientService (AppContext appContext)
specifier|public
name|MRClientService
parameter_list|(
name|AppContext
name|appContext
parameter_list|)
block|{
name|super
argument_list|(
literal|"MRClientService"
argument_list|)
expr_stmt|;
name|this
operator|.
name|appContext
operator|=
name|appContext
expr_stmt|;
name|this
operator|.
name|protocolHandler
operator|=
operator|new
name|MRClientProtocolHandler
argument_list|()
expr_stmt|;
block|}
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
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
name|InetSocketAddress
name|address
init|=
operator|new
name|InetSocketAddress
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|server
operator|=
name|rpc
operator|.
name|getServer
argument_list|(
name|MRClientProtocol
operator|.
name|class
argument_list|,
name|protocolHandler
argument_list|,
name|address
argument_list|,
name|conf
argument_list|,
name|appContext
operator|.
name|getClientToAMTokenSecretManager
argument_list|()
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|MRJobConfig
operator|.
name|MR_AM_JOB_CLIENT_THREAD_COUNT
argument_list|,
name|MRJobConfig
operator|.
name|DEFAULT_MR_AM_JOB_CLIENT_THREAD_COUNT
argument_list|)
argument_list|,
name|MRJobConfig
operator|.
name|MR_AM_JOB_CLIENT_PORT_RANGE
argument_list|)
expr_stmt|;
comment|// Enable service authorization?
if|if
condition|(
name|conf
operator|.
name|getBoolean
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SECURITY_AUTHORIZATION
argument_list|,
literal|false
argument_list|)
condition|)
block|{
name|refreshServiceAcls
argument_list|(
name|conf
argument_list|,
operator|new
name|MRAMPolicyProvider
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|this
operator|.
name|bindAddress
operator|=
name|NetUtils
operator|.
name|getConnectAddress
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiated MRClientService at "
operator|+
name|this
operator|.
name|bindAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|webApp
operator|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"mapreduce"
argument_list|,
name|AppContext
operator|.
name|class
argument_list|,
name|appContext
argument_list|,
literal|"ws"
argument_list|)
operator|.
name|with
argument_list|(
name|conf
argument_list|)
operator|.
name|start
argument_list|(
operator|new
name|AMWebApp
argument_list|()
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
literal|"Webapps failed to start. Ignoring for now:"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
block|}
DECL|method|refreshServiceAcls (Configuration configuration, PolicyProvider policyProvider)
name|void
name|refreshServiceAcls
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|PolicyProvider
name|policyProvider
parameter_list|)
block|{
name|this
operator|.
name|server
operator|.
name|refreshServiceAcl
argument_list|(
name|configuration
argument_list|,
name|policyProvider
argument_list|)
expr_stmt|;
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
name|server
operator|!=
literal|null
condition|)
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|webApp
operator|!=
literal|null
condition|)
block|{
name|webApp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getBindAddress ()
specifier|public
name|InetSocketAddress
name|getBindAddress
parameter_list|()
block|{
return|return
name|bindAddress
return|;
block|}
annotation|@
name|Override
DECL|method|getHttpPort ()
specifier|public
name|int
name|getHttpPort
parameter_list|()
block|{
return|return
name|webApp
operator|.
name|port
argument_list|()
return|;
block|}
DECL|class|MRClientProtocolHandler
class|class
name|MRClientProtocolHandler
implements|implements
name|MRClientProtocol
block|{
DECL|field|recordFactory
specifier|private
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
annotation|@
name|Override
DECL|method|getConnectAddress ()
specifier|public
name|InetSocketAddress
name|getConnectAddress
parameter_list|()
block|{
return|return
name|getBindAddress
argument_list|()
return|;
block|}
DECL|method|verifyAndGetJob (JobId jobID, boolean modifyAccess)
specifier|private
name|Job
name|verifyAndGetJob
parameter_list|(
name|JobId
name|jobID
parameter_list|,
name|boolean
name|modifyAccess
parameter_list|)
throws|throws
name|IOException
block|{
name|Job
name|job
init|=
name|appContext
operator|.
name|getJob
argument_list|(
name|jobID
argument_list|)
decl_stmt|;
return|return
name|job
return|;
block|}
DECL|method|verifyAndGetTask (TaskId taskID, boolean modifyAccess)
specifier|private
name|Task
name|verifyAndGetTask
parameter_list|(
name|TaskId
name|taskID
parameter_list|,
name|boolean
name|modifyAccess
parameter_list|)
throws|throws
name|IOException
block|{
name|Task
name|task
init|=
name|verifyAndGetJob
argument_list|(
name|taskID
operator|.
name|getJobId
argument_list|()
argument_list|,
name|modifyAccess
argument_list|)
operator|.
name|getTask
argument_list|(
name|taskID
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown Task "
operator|+
name|taskID
argument_list|)
throw|;
block|}
return|return
name|task
return|;
block|}
DECL|method|verifyAndGetAttempt (TaskAttemptId attemptID, boolean modifyAccess)
specifier|private
name|TaskAttempt
name|verifyAndGetAttempt
parameter_list|(
name|TaskAttemptId
name|attemptID
parameter_list|,
name|boolean
name|modifyAccess
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttempt
name|attempt
init|=
name|verifyAndGetTask
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
argument_list|,
name|modifyAccess
argument_list|)
operator|.
name|getAttempt
argument_list|(
name|attemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|attempt
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unknown TaskAttempt "
operator|+
name|attemptID
argument_list|)
throw|;
block|}
return|return
name|attempt
return|;
block|}
annotation|@
name|Override
DECL|method|getCounters (GetCountersRequest request)
specifier|public
name|GetCountersResponse
name|getCounters
parameter_list|(
name|GetCountersRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|JobId
name|jobId
init|=
name|request
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|verifyAndGetJob
argument_list|(
name|jobId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|GetCountersResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetCountersResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setCounters
argument_list|(
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|job
operator|.
name|getAllCounters
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getJobReport (GetJobReportRequest request)
specifier|public
name|GetJobReportResponse
name|getJobReport
parameter_list|(
name|GetJobReportRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|JobId
name|jobId
init|=
name|request
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|verifyAndGetJob
argument_list|(
name|jobId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|GetJobReportResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetJobReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
if|if
condition|(
name|job
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setJobReport
argument_list|(
name|job
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|response
operator|.
name|setJobReport
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptReport ( GetTaskAttemptReportRequest request)
specifier|public
name|GetTaskAttemptReportResponse
name|getTaskAttemptReport
parameter_list|(
name|GetTaskAttemptReportRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptId
name|taskAttemptId
init|=
name|request
operator|.
name|getTaskAttemptId
argument_list|()
decl_stmt|;
name|GetTaskAttemptReportResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskAttemptReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setTaskAttemptReport
argument_list|(
name|verifyAndGetAttempt
argument_list|(
name|taskAttemptId
argument_list|,
literal|false
argument_list|)
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskReport (GetTaskReportRequest request)
specifier|public
name|GetTaskReportResponse
name|getTaskReport
parameter_list|(
name|GetTaskReportRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskId
name|taskId
init|=
name|request
operator|.
name|getTaskId
argument_list|()
decl_stmt|;
name|GetTaskReportResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|setTaskReport
argument_list|(
name|verifyAndGetTask
argument_list|(
name|taskId
argument_list|,
literal|false
argument_list|)
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getTaskAttemptCompletionEvents ( GetTaskAttemptCompletionEventsRequest request)
specifier|public
name|GetTaskAttemptCompletionEventsResponse
name|getTaskAttemptCompletionEvents
parameter_list|(
name|GetTaskAttemptCompletionEventsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|JobId
name|jobId
init|=
name|request
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|int
name|fromEventId
init|=
name|request
operator|.
name|getFromEventId
argument_list|()
decl_stmt|;
name|int
name|maxEvents
init|=
name|request
operator|.
name|getMaxEvents
argument_list|()
decl_stmt|;
name|Job
name|job
init|=
name|verifyAndGetJob
argument_list|(
name|jobId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|GetTaskAttemptCompletionEventsResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskAttemptCompletionEventsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|addAllCompletionEvents
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|job
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
name|fromEventId
argument_list|,
name|maxEvents
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|killJob (KillJobRequest request)
specifier|public
name|KillJobResponse
name|killJob
parameter_list|(
name|KillJobRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|JobId
name|jobId
init|=
name|request
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"Kill Job received from client "
operator|+
name|jobId
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|verifyAndGetJob
argument_list|(
name|jobId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobDiagnosticsUpdateEvent
argument_list|(
name|jobId
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|JobEvent
argument_list|(
name|jobId
argument_list|,
name|JobEventType
operator|.
name|JOB_KILL
argument_list|)
argument_list|)
expr_stmt|;
name|KillJobResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillJobResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|killTask (KillTaskRequest request)
specifier|public
name|KillTaskResponse
name|killTask
parameter_list|(
name|KillTaskRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskId
name|taskId
init|=
name|request
operator|.
name|getTaskId
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"Kill task received from client "
operator|+
name|taskId
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|verifyAndGetTask
argument_list|(
name|taskId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskEvent
argument_list|(
name|taskId
argument_list|,
name|TaskEventType
operator|.
name|T_KILL
argument_list|)
argument_list|)
expr_stmt|;
name|KillTaskResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillTaskResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|killTaskAttempt ( KillTaskAttemptRequest request)
specifier|public
name|KillTaskAttemptResponse
name|killTaskAttempt
parameter_list|(
name|KillTaskAttemptRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptId
name|taskAttemptId
init|=
name|request
operator|.
name|getTaskAttemptId
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"Kill task attempt received from client "
operator|+
name|taskAttemptId
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|verifyAndGetAttempt
argument_list|(
name|taskAttemptId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_KILL
argument_list|)
argument_list|)
expr_stmt|;
name|KillTaskAttemptResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillTaskAttemptResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getDiagnostics ( GetDiagnosticsRequest request)
specifier|public
name|GetDiagnosticsResponse
name|getDiagnostics
parameter_list|(
name|GetDiagnosticsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptId
name|taskAttemptId
init|=
name|request
operator|.
name|getTaskAttemptId
argument_list|()
decl_stmt|;
name|GetDiagnosticsResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetDiagnosticsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|response
operator|.
name|addAllDiagnostics
argument_list|(
name|verifyAndGetAttempt
argument_list|(
name|taskAttemptId
argument_list|,
literal|false
argument_list|)
operator|.
name|getDiagnostics
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
DECL|method|failTaskAttempt ( FailTaskAttemptRequest request)
specifier|public
name|FailTaskAttemptResponse
name|failTaskAttempt
parameter_list|(
name|FailTaskAttemptRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|TaskAttemptId
name|taskAttemptId
init|=
name|request
operator|.
name|getTaskAttemptId
argument_list|()
decl_stmt|;
name|String
name|message
init|=
literal|"Fail task attempt received from client "
operator|+
name|taskAttemptId
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|verifyAndGetAttempt
argument_list|(
name|taskAttemptId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|message
argument_list|)
argument_list|)
expr_stmt|;
name|appContext
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|taskAttemptId
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
name|FailTaskAttemptResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FailTaskAttemptResponse
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|response
return|;
block|}
DECL|field|getTaskReportsLock
specifier|private
specifier|final
name|Object
name|getTaskReportsLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|getTaskReports ( GetTaskReportsRequest request)
specifier|public
name|GetTaskReportsResponse
name|getTaskReports
parameter_list|(
name|GetTaskReportsRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|JobId
name|jobId
init|=
name|request
operator|.
name|getJobId
argument_list|()
decl_stmt|;
name|TaskType
name|taskType
init|=
name|request
operator|.
name|getTaskType
argument_list|()
decl_stmt|;
name|GetTaskReportsResponse
name|response
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportsResponse
operator|.
name|class
argument_list|)
decl_stmt|;
name|Job
name|job
init|=
name|verifyAndGetJob
argument_list|(
name|jobId
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|Task
argument_list|>
name|tasks
init|=
name|job
operator|.
name|getTasks
argument_list|(
name|taskType
argument_list|)
operator|.
name|values
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Getting task report for "
operator|+
name|taskType
operator|+
literal|"   "
operator|+
name|jobId
operator|+
literal|". Report-size will be "
operator|+
name|tasks
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Take lock to allow only one call, otherwise heap will blow up because
comment|// of counters in the report when there are multiple callers.
synchronized|synchronized
init|(
name|getTaskReportsLock
init|)
block|{
for|for
control|(
name|Task
name|task
range|:
name|tasks
control|)
block|{
name|response
operator|.
name|addTaskReport
argument_list|(
name|task
operator|.
name|getReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|response
return|;
block|}
annotation|@
name|Override
DECL|method|getDelegationToken ( GetDelegationTokenRequest request)
specifier|public
name|GetDelegationTokenResponse
name|getDelegationToken
parameter_list|(
name|GetDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MR AM not authorized to issue delegation"
operator|+
literal|" token"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|renewDelegationToken ( RenewDelegationTokenRequest request)
specifier|public
name|RenewDelegationTokenResponse
name|renewDelegationToken
parameter_list|(
name|RenewDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MR AM not authorized to renew delegation"
operator|+
literal|" token"
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|cancelDelegationToken ( CancelDelegationTokenRequest request)
specifier|public
name|CancelDelegationTokenResponse
name|cancelDelegationToken
parameter_list|(
name|CancelDelegationTokenRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"MR AM not authorized to cancel delegation"
operator|+
literal|" token"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

