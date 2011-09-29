begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|io
operator|.
name|Text
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
name|JobStatus
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
name|TaskAttemptID
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
name|records
operator|.
name|Counters
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
name|JobReport
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
name|JobState
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
name|util
operator|.
name|MRApps
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
name|SecurityInfo
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
name|yarn
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
name|ApplicationReport
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
name|ApplicationState
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
name|exceptions
operator|.
name|YarnRemoteException
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
name|RPCUtil
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
name|SchedulerSecurityInfo
import|;
end_import

begin_class
DECL|class|ClientServiceDelegate
class|class
name|ClientServiceDelegate
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
name|ClientServiceDelegate
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// Caches for per-user NotRunningJobs
DECL|field|notRunningJobs
specifier|private
specifier|static
name|HashMap
argument_list|<
name|JobState
argument_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|NotRunningJob
argument_list|>
argument_list|>
name|notRunningJobs
init|=
operator|new
name|HashMap
argument_list|<
name|JobState
argument_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|NotRunningJob
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|jobId
specifier|private
specifier|final
name|JobID
name|jobId
decl_stmt|;
DECL|field|appId
specifier|private
specifier|final
name|ApplicationId
name|appId
decl_stmt|;
DECL|field|rm
specifier|private
specifier|final
name|ResourceMgrDelegate
name|rm
decl_stmt|;
DECL|field|historyServerProxy
specifier|private
specifier|final
name|MRClientProtocol
name|historyServerProxy
decl_stmt|;
DECL|field|forceRefresh
specifier|private
name|boolean
name|forceRefresh
decl_stmt|;
DECL|field|realProxy
specifier|private
name|MRClientProtocol
name|realProxy
init|=
literal|null
decl_stmt|;
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
DECL|field|UNKNOWN_USER
specifier|private
specifier|static
name|String
name|UNKNOWN_USER
init|=
literal|"Unknown User"
decl_stmt|;
DECL|method|ClientServiceDelegate (Configuration conf, ResourceMgrDelegate rm, JobID jobId, MRClientProtocol historyServerProxy)
name|ClientServiceDelegate
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|ResourceMgrDelegate
name|rm
parameter_list|,
name|JobID
name|jobId
parameter_list|,
name|MRClientProtocol
name|historyServerProxy
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Cloning for modifying.
comment|// For faster redirects from AM to HS.
name|this
operator|.
name|conf
operator|.
name|setInt
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|IPC_CLIENT_CONNECT_MAX_RETRIES_KEY
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|this
operator|.
name|rm
operator|=
name|rm
expr_stmt|;
name|this
operator|.
name|jobId
operator|=
name|jobId
expr_stmt|;
name|this
operator|.
name|historyServerProxy
operator|=
name|historyServerProxy
expr_stmt|;
name|this
operator|.
name|appId
operator|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|jobId
argument_list|)
operator|.
name|getAppId
argument_list|()
expr_stmt|;
block|}
comment|// Get the instance of the NotRunningJob corresponding to the specified
comment|// user and state
DECL|method|getNotRunningJob (String user, JobState state)
specifier|private
name|NotRunningJob
name|getNotRunningJob
parameter_list|(
name|String
name|user
parameter_list|,
name|JobState
name|state
parameter_list|)
block|{
synchronized|synchronized
init|(
name|notRunningJobs
init|)
block|{
name|HashMap
argument_list|<
name|String
argument_list|,
name|NotRunningJob
argument_list|>
name|map
init|=
name|notRunningJobs
operator|.
name|get
argument_list|(
name|state
argument_list|)
decl_stmt|;
if|if
condition|(
name|map
operator|==
literal|null
condition|)
block|{
name|map
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|NotRunningJob
argument_list|>
argument_list|()
expr_stmt|;
name|notRunningJobs
operator|.
name|put
argument_list|(
name|state
argument_list|,
name|map
argument_list|)
expr_stmt|;
block|}
name|NotRunningJob
name|notRunningJob
init|=
name|map
operator|.
name|get
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|notRunningJob
operator|==
literal|null
condition|)
block|{
name|notRunningJob
operator|=
operator|new
name|NotRunningJob
argument_list|(
name|user
argument_list|,
name|state
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|user
argument_list|,
name|notRunningJob
argument_list|)
expr_stmt|;
block|}
return|return
name|notRunningJob
return|;
block|}
block|}
DECL|method|getProxy ()
specifier|private
name|MRClientProtocol
name|getProxy
parameter_list|()
throws|throws
name|YarnRemoteException
block|{
if|if
condition|(
operator|!
name|forceRefresh
operator|&&
name|realProxy
operator|!=
literal|null
condition|)
block|{
return|return
name|realProxy
return|;
block|}
comment|//TODO RM NPEs for unknown jobs. History may still be aware.
comment|// Possibly allow nulls through the PB tunnel, otherwise deal with an exception
comment|// and redirect to the history server.
name|ApplicationReport
name|application
init|=
name|rm
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|String
name|serviceAddr
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|application
operator|==
literal|null
operator|||
name|ApplicationState
operator|.
name|RUNNING
operator|.
name|equals
argument_list|(
name|application
operator|.
name|getState
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not get Job info from RM for job "
operator|+
name|jobId
operator|+
literal|". Redirecting to job history server."
argument_list|)
expr_stmt|;
return|return
name|checkAndGetHSProxy
argument_list|(
name|UNKNOWN_USER
argument_list|,
name|JobState
operator|.
name|NEW
argument_list|)
return|;
block|}
try|try
block|{
if|if
condition|(
name|application
operator|.
name|getHost
argument_list|()
operator|==
literal|null
operator|||
literal|""
operator|.
name|equals
argument_list|(
name|application
operator|.
name|getHost
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"AM not assigned to Job. Waiting to get the AM ..."
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Application state is "
operator|+
name|application
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
name|application
operator|=
name|rm
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|serviceAddr
operator|=
name|application
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|application
operator|.
name|getRpcPort
argument_list|()
expr_stmt|;
if|if
condition|(
name|UserGroupInformation
operator|.
name|isSecurityEnabled
argument_list|()
condition|)
block|{
name|String
name|clientTokenEncoded
init|=
name|application
operator|.
name|getClientToken
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
name|clientToken
init|=
operator|new
name|Token
argument_list|<
name|ApplicationTokenIdentifier
argument_list|>
argument_list|()
decl_stmt|;
name|clientToken
operator|.
name|decodeFromUrlString
argument_list|(
name|clientTokenEncoded
argument_list|)
expr_stmt|;
name|clientToken
operator|.
name|setService
argument_list|(
operator|new
name|Text
argument_list|(
name|application
operator|.
name|getHost
argument_list|()
operator|+
literal|":"
operator|+
name|application
operator|.
name|getRpcPort
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|addToken
argument_list|(
name|clientToken
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Tracking Url of JOB is "
operator|+
name|application
operator|.
name|getTrackingUrl
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Connecting to "
operator|+
name|serviceAddr
argument_list|)
expr_stmt|;
name|instantiateAMProxy
argument_list|(
name|serviceAddr
argument_list|)
expr_stmt|;
return|return
name|realProxy
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//possibly the AM has crashed
comment|//there may be some time before AM is restarted
comment|//keep retrying by getting the address from RM
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not connect to "
operator|+
name|serviceAddr
operator|+
literal|". Waiting for getting the latest AM address..."
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e1
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"getProxy() call interruped"
argument_list|,
name|e1
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e1
argument_list|)
throw|;
block|}
name|application
operator|=
name|rm
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
expr_stmt|;
if|if
condition|(
name|application
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Could not get Job info from RM for job "
operator|+
name|jobId
operator|+
literal|". Redirecting to job history server."
argument_list|)
expr_stmt|;
return|return
name|checkAndGetHSProxy
argument_list|(
name|UNKNOWN_USER
argument_list|,
name|JobState
operator|.
name|RUNNING
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"getProxy() call interruped"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** we just want to return if its allocating, so that we don't      * block on it. This is to be able to return job status       * on an allocating Application.      */
name|String
name|user
init|=
name|application
operator|.
name|getUser
argument_list|()
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
name|RPCUtil
operator|.
name|getRemoteException
argument_list|(
literal|"User is not set in the application report"
argument_list|)
throw|;
block|}
if|if
condition|(
name|application
operator|.
name|getState
argument_list|()
operator|==
name|ApplicationState
operator|.
name|NEW
operator|||
name|application
operator|.
name|getState
argument_list|()
operator|==
name|ApplicationState
operator|.
name|SUBMITTED
condition|)
block|{
name|realProxy
operator|=
literal|null
expr_stmt|;
return|return
name|getNotRunningJob
argument_list|(
name|user
argument_list|,
name|JobState
operator|.
name|NEW
argument_list|)
return|;
block|}
if|if
condition|(
name|application
operator|.
name|getState
argument_list|()
operator|==
name|ApplicationState
operator|.
name|FAILED
condition|)
block|{
name|realProxy
operator|=
literal|null
expr_stmt|;
return|return
name|getNotRunningJob
argument_list|(
name|user
argument_list|,
name|JobState
operator|.
name|FAILED
argument_list|)
return|;
block|}
if|if
condition|(
name|application
operator|.
name|getState
argument_list|()
operator|==
name|ApplicationState
operator|.
name|KILLED
condition|)
block|{
name|realProxy
operator|=
literal|null
expr_stmt|;
return|return
name|getNotRunningJob
argument_list|(
name|user
argument_list|,
name|JobState
operator|.
name|KILLED
argument_list|)
return|;
block|}
comment|//History server can serve a job only if application
comment|//succeeded.
if|if
condition|(
name|application
operator|.
name|getState
argument_list|()
operator|==
name|ApplicationState
operator|.
name|SUCCEEDED
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Application state is completed. "
operator|+
literal|"Redirecting to job history server"
argument_list|)
expr_stmt|;
name|realProxy
operator|=
name|checkAndGetHSProxy
argument_list|(
name|user
argument_list|,
name|JobState
operator|.
name|SUCCEEDED
argument_list|)
expr_stmt|;
block|}
return|return
name|realProxy
return|;
block|}
DECL|method|checkAndGetHSProxy (String user, JobState state)
specifier|private
name|MRClientProtocol
name|checkAndGetHSProxy
parameter_list|(
name|String
name|user
parameter_list|,
name|JobState
name|state
parameter_list|)
block|{
if|if
condition|(
literal|null
operator|==
name|historyServerProxy
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Job History Server is not configured."
argument_list|)
expr_stmt|;
return|return
name|getNotRunningJob
argument_list|(
name|user
argument_list|,
name|state
argument_list|)
return|;
block|}
return|return
name|historyServerProxy
return|;
block|}
DECL|method|instantiateAMProxy (final String serviceAddr)
specifier|private
name|void
name|instantiateAMProxy
parameter_list|(
specifier|final
name|String
name|serviceAddr
parameter_list|)
throws|throws
name|IOException
block|{
name|UserGroupInformation
name|currentUser
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Connecting to ApplicationMaster at: "
operator|+
name|serviceAddr
argument_list|)
expr_stmt|;
name|realProxy
operator|=
name|currentUser
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|MRClientProtocol
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|MRClientProtocol
name|run
parameter_list|()
block|{
name|Configuration
name|myConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|myConf
operator|.
name|setClass
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_SECURITY_INFO
argument_list|,
name|SchedulerSecurityInfo
operator|.
name|class
argument_list|,
name|SecurityInfo
operator|.
name|class
argument_list|)
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|myConf
argument_list|)
decl_stmt|;
return|return
operator|(
name|MRClientProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|MRClientProtocol
operator|.
name|class
argument_list|,
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|serviceAddr
argument_list|)
argument_list|,
name|myConf
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Connected to ApplicationMaster at: "
operator|+
name|serviceAddr
argument_list|)
expr_stmt|;
block|}
DECL|method|invoke (String method, Class argClass, Object args)
specifier|private
specifier|synchronized
name|Object
name|invoke
parameter_list|(
name|String
name|method
parameter_list|,
name|Class
name|argClass
parameter_list|,
name|Object
name|args
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|Method
name|methodOb
init|=
literal|null
decl_stmt|;
try|try
block|{
name|methodOb
operator|=
name|MRClientProtocol
operator|.
name|class
operator|.
name|getMethod
argument_list|(
name|method
argument_list|,
name|argClass
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Method name mismatch"
argument_list|,
name|e
argument_list|)
throw|;
block|}
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
return|return
name|methodOb
operator|.
name|invoke
argument_list|(
name|getProxy
argument_list|()
argument_list|,
name|args
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnRemoteException
name|yre
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception thrown by remote end."
argument_list|,
name|yre
argument_list|)
expr_stmt|;
throw|throw
name|yre
throw|;
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getTargetException
argument_list|()
operator|instanceof
name|YarnRemoteException
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exception thrown by remote end."
argument_list|,
name|e
operator|.
name|getTargetException
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|(
name|YarnRemoteException
operator|)
name|e
operator|.
name|getTargetException
argument_list|()
throw|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Failed to contact AM/History for job "
operator|+
name|jobId
operator|+
literal|"  Will retry.."
argument_list|,
name|e
operator|.
name|getTargetException
argument_list|()
argument_list|)
expr_stmt|;
name|forceRefresh
operator|=
literal|true
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
name|info
argument_list|(
literal|"Failed to contact AM/History for job "
operator|+
name|jobId
operator|+
literal|"  Will retry.."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Failing to contact application master"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|forceRefresh
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
DECL|method|getJobCounters (JobID arg0)
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|Counters
name|getJobCounters
parameter_list|(
name|JobID
name|arg0
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
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
name|jobID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|arg0
argument_list|)
decl_stmt|;
name|GetCountersRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetCountersRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setJobId
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
name|Counters
name|cnt
init|=
operator|(
operator|(
name|GetCountersResponse
operator|)
name|invoke
argument_list|(
literal|"getCounters"
argument_list|,
name|GetCountersRequest
operator|.
name|class
argument_list|,
name|request
argument_list|)
operator|)
operator|.
name|getCounters
argument_list|()
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|cnt
argument_list|)
return|;
block|}
DECL|method|getTaskCompletionEvents (JobID arg0, int arg1, int arg2)
name|TaskCompletionEvent
index|[]
name|getTaskCompletionEvents
parameter_list|(
name|JobID
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
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
name|jobID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|arg0
argument_list|)
decl_stmt|;
name|GetTaskAttemptCompletionEventsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskAttemptCompletionEventsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setJobId
argument_list|(
name|jobID
argument_list|)
expr_stmt|;
name|request
operator|.
name|setFromEventId
argument_list|(
name|arg1
argument_list|)
expr_stmt|;
name|request
operator|.
name|setMaxEvents
argument_list|(
name|arg2
argument_list|)
expr_stmt|;
name|List
argument_list|<
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
name|TaskAttemptCompletionEvent
argument_list|>
name|list
init|=
operator|(
operator|(
name|GetTaskAttemptCompletionEventsResponse
operator|)
name|invoke
argument_list|(
literal|"getTaskAttemptCompletionEvents"
argument_list|,
name|GetTaskAttemptCompletionEventsRequest
operator|.
name|class
argument_list|,
name|request
argument_list|)
operator|)
operator|.
name|getCompletionEventList
argument_list|()
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|list
operator|.
name|toArray
argument_list|(
operator|new
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
name|TaskAttemptCompletionEvent
index|[
literal|0
index|]
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTaskDiagnostics (org.apache.hadoop.mapreduce.TaskAttemptID arg0)
name|String
index|[]
name|getTaskDiagnostics
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskAttemptID
name|arg0
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
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
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|arg0
argument_list|)
decl_stmt|;
name|GetDiagnosticsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetDiagnosticsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setTaskAttemptId
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|(
operator|(
name|GetDiagnosticsResponse
operator|)
name|invoke
argument_list|(
literal|"getDiagnostics"
argument_list|,
name|GetDiagnosticsRequest
operator|.
name|class
argument_list|,
name|request
argument_list|)
operator|)
operator|.
name|getDiagnosticsList
argument_list|()
decl_stmt|;
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
name|String
name|c
range|:
name|list
control|)
block|{
name|result
index|[
name|i
operator|++
index|]
operator|=
name|c
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
DECL|method|getJobStatus (JobID oldJobID)
name|JobStatus
name|getJobStatus
parameter_list|(
name|JobID
name|oldJobID
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
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
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobID
argument_list|)
decl_stmt|;
name|GetJobReportRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetJobReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|JobReport
name|report
init|=
operator|(
operator|(
name|GetJobReportResponse
operator|)
name|invoke
argument_list|(
literal|"getJobReport"
argument_list|,
name|GetJobReportRequest
operator|.
name|class
argument_list|,
name|request
argument_list|)
operator|)
operator|.
name|getJobReport
argument_list|()
decl_stmt|;
name|String
name|jobFile
init|=
name|MRApps
operator|.
name|getJobFile
argument_list|(
name|conf
argument_list|,
name|report
operator|.
name|getUser
argument_list|()
argument_list|,
name|oldJobID
argument_list|)
decl_stmt|;
comment|//TODO: add tracking url in JobReport
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|report
argument_list|,
name|jobFile
argument_list|,
literal|""
argument_list|)
return|;
block|}
DECL|method|getTaskReports (JobID oldJobID, TaskType taskType)
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskReport
index|[]
name|getTaskReports
parameter_list|(
name|JobID
name|oldJobID
parameter_list|,
name|TaskType
name|taskType
parameter_list|)
throws|throws
name|YarnRemoteException
throws|,
name|YarnRemoteException
block|{
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
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobID
argument_list|)
decl_stmt|;
name|GetTaskReportsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetTaskReportsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|request
operator|.
name|setTaskType
argument_list|(
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskType
argument_list|)
argument_list|)
expr_stmt|;
name|List
argument_list|<
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
name|TaskReport
argument_list|>
name|taskReports
init|=
operator|(
operator|(
name|GetTaskReportsResponse
operator|)
name|invoke
argument_list|(
literal|"getTaskReports"
argument_list|,
name|GetTaskReportsRequest
operator|.
name|class
argument_list|,
name|request
argument_list|)
operator|)
operator|.
name|getTaskReportList
argument_list|()
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|taskReports
argument_list|)
operator|.
name|toArray
argument_list|(
operator|new
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|TaskReport
index|[
literal|0
index|]
argument_list|)
return|;
block|}
DECL|method|killTask (TaskAttemptID taskAttemptID, boolean fail)
name|boolean
name|killTask
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|boolean
name|fail
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
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
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|fail
condition|)
block|{
name|FailTaskAttemptRequest
name|failRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|FailTaskAttemptRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|failRequest
operator|.
name|setTaskAttemptId
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|invoke
argument_list|(
literal|"failTaskAttempt"
argument_list|,
name|FailTaskAttemptRequest
operator|.
name|class
argument_list|,
name|failRequest
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|KillTaskAttemptRequest
name|killRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillTaskAttemptRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|killRequest
operator|.
name|setTaskAttemptId
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|invoke
argument_list|(
literal|"killTaskAttempt"
argument_list|,
name|KillTaskAttemptRequest
operator|.
name|class
argument_list|,
name|killRequest
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
DECL|method|killJob (JobID oldJobID)
name|boolean
name|killJob
parameter_list|(
name|JobID
name|oldJobID
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
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
name|jobId
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|oldJobID
argument_list|)
decl_stmt|;
name|KillJobRequest
name|killRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillJobRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|killRequest
operator|.
name|setJobId
argument_list|(
name|jobId
argument_list|)
expr_stmt|;
name|invoke
argument_list|(
literal|"killJob"
argument_list|,
name|KillJobRequest
operator|.
name|class
argument_list|,
name|killRequest
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

