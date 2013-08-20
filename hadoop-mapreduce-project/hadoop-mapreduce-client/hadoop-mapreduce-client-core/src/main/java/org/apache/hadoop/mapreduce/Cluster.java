begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
import|;
end_import

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
name|PrivilegedExceptionAction
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
name|java
operator|.
name|util
operator|.
name|ServiceLoader
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
name|FileSystem
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
name|mapred
operator|.
name|JobConf
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
name|protocol
operator|.
name|ClientProtocol
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
name|protocol
operator|.
name|ClientProtocolProvider
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|util
operator|.
name|ConfigUtil
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
name|LogParams
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
name|SecretManager
operator|.
name|InvalidToken
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

begin_comment
comment|/**  * Provides a way to access information about the map/reduce cluster.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|Cluster
specifier|public
class|class
name|Cluster
block|{
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|enum|JobTrackerStatus
DECL|enumConstant|INITIALIZING
DECL|enumConstant|RUNNING
specifier|public
specifier|static
enum|enum
name|JobTrackerStatus
block|{
name|INITIALIZING
block|,
name|RUNNING
block|}
empty_stmt|;
DECL|field|clientProtocolProvider
specifier|private
name|ClientProtocolProvider
name|clientProtocolProvider
decl_stmt|;
DECL|field|client
specifier|private
name|ClientProtocol
name|client
decl_stmt|;
DECL|field|ugi
specifier|private
name|UserGroupInformation
name|ugi
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|fs
specifier|private
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|sysDir
specifier|private
name|Path
name|sysDir
init|=
literal|null
decl_stmt|;
DECL|field|stagingAreaDir
specifier|private
name|Path
name|stagingAreaDir
init|=
literal|null
decl_stmt|;
DECL|field|jobHistoryDir
specifier|private
name|Path
name|jobHistoryDir
init|=
literal|null
decl_stmt|;
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
name|Cluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|frameworkLoader
specifier|private
specifier|static
name|ServiceLoader
argument_list|<
name|ClientProtocolProvider
argument_list|>
name|frameworkLoader
init|=
name|ServiceLoader
operator|.
name|load
argument_list|(
name|ClientProtocolProvider
operator|.
name|class
argument_list|)
decl_stmt|;
static|static
block|{
name|ConfigUtil
operator|.
name|loadResources
argument_list|()
expr_stmt|;
block|}
DECL|method|Cluster (Configuration conf)
specifier|public
name|Cluster
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|Cluster (InetSocketAddress jobTrackAddr, Configuration conf)
specifier|public
name|Cluster
parameter_list|(
name|InetSocketAddress
name|jobTrackAddr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|ugi
operator|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
expr_stmt|;
name|initialize
argument_list|(
name|jobTrackAddr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|initialize (InetSocketAddress jobTrackAddr, Configuration conf)
specifier|private
name|void
name|initialize
parameter_list|(
name|InetSocketAddress
name|jobTrackAddr
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
synchronized|synchronized
init|(
name|frameworkLoader
init|)
block|{
for|for
control|(
name|ClientProtocolProvider
name|provider
range|:
name|frameworkLoader
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Trying ClientProtocolProvider : "
operator|+
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ClientProtocol
name|clientProtocol
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|jobTrackAddr
operator|==
literal|null
condition|)
block|{
name|clientProtocol
operator|=
name|provider
operator|.
name|create
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|clientProtocol
operator|=
name|provider
operator|.
name|create
argument_list|(
name|jobTrackAddr
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|clientProtocol
operator|!=
literal|null
condition|)
block|{
name|clientProtocolProvider
operator|=
name|provider
expr_stmt|;
name|client
operator|=
name|clientProtocol
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Picked "
operator|+
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" as the ClientProtocolProvider"
argument_list|)
expr_stmt|;
break|break;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Cannot pick "
operator|+
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" as the ClientProtocolProvider - returned null protocol"
argument_list|)
expr_stmt|;
block|}
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
literal|"Failed to use "
operator|+
name|provider
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" due to error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
literal|null
operator|==
name|clientProtocolProvider
operator|||
literal|null
operator|==
name|client
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot initialize Cluster. Please check your configuration for "
operator|+
name|MRConfig
operator|.
name|FRAMEWORK_NAME
operator|+
literal|" and the correspond server addresses."
argument_list|)
throw|;
block|}
block|}
DECL|method|getClient ()
name|ClientProtocol
name|getClient
parameter_list|()
block|{
return|return
name|client
return|;
block|}
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
comment|/**    * Close the<code>Cluster</code>.    */
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|clientProtocolProvider
operator|.
name|close
argument_list|(
name|client
argument_list|)
expr_stmt|;
block|}
DECL|method|getJobs (JobStatus[] stats)
specifier|private
name|Job
index|[]
name|getJobs
parameter_list|(
name|JobStatus
index|[]
name|stats
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|Job
argument_list|>
name|jobs
init|=
operator|new
name|ArrayList
argument_list|<
name|Job
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|JobStatus
name|stat
range|:
name|stats
control|)
block|{
name|jobs
operator|.
name|add
argument_list|(
name|Job
operator|.
name|getInstance
argument_list|(
name|this
argument_list|,
name|stat
argument_list|,
operator|new
name|JobConf
argument_list|(
name|stat
operator|.
name|getJobFile
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|jobs
operator|.
name|toArray
argument_list|(
operator|new
name|Job
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**    * Get the file system where job-specific files are stored    *     * @return object of FileSystem    * @throws IOException    * @throws InterruptedException    */
DECL|method|getFileSystem ()
specifier|public
specifier|synchronized
name|FileSystem
name|getFileSystem
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|this
operator|.
name|fs
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|this
operator|.
name|fs
operator|=
name|ugi
operator|.
name|doAs
argument_list|(
operator|new
name|PrivilegedExceptionAction
argument_list|<
name|FileSystem
argument_list|>
argument_list|()
block|{
specifier|public
name|FileSystem
name|run
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
specifier|final
name|Path
name|sysDir
init|=
operator|new
name|Path
argument_list|(
name|client
operator|.
name|getSystemDir
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|sysDir
operator|.
name|getFileSystem
argument_list|(
name|getConf
argument_list|()
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|fs
return|;
block|}
comment|/**    * Get job corresponding to jobid.    *     * @param jobId    * @return object of {@link Job}    * @throws IOException    * @throws InterruptedException    */
DECL|method|getJob (JobID jobId)
specifier|public
name|Job
name|getJob
parameter_list|(
name|JobID
name|jobId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|JobStatus
name|status
init|=
name|client
operator|.
name|getJobStatus
argument_list|(
name|jobId
argument_list|)
decl_stmt|;
if|if
condition|(
name|status
operator|!=
literal|null
condition|)
block|{
name|JobConf
name|conf
decl_stmt|;
try|try
block|{
name|conf
operator|=
operator|new
name|JobConf
argument_list|(
name|status
operator|.
name|getJobFile
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
comment|// If job file doesn't exist it means we can't find the job
if|if
condition|(
name|ex
operator|.
name|getCause
argument_list|()
operator|instanceof
name|FileNotFoundException
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
return|return
name|Job
operator|.
name|getInstance
argument_list|(
name|this
argument_list|,
name|status
argument_list|,
name|conf
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Get all the queues in cluster.    *     * @return array of {@link QueueInfo}    * @throws IOException    * @throws InterruptedException    */
DECL|method|getQueues ()
specifier|public
name|QueueInfo
index|[]
name|getQueues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getQueues
argument_list|()
return|;
block|}
comment|/**    * Get queue information for the specified name.    *     * @param name queuename    * @return object of {@link QueueInfo}    * @throws IOException    * @throws InterruptedException    */
DECL|method|getQueue (String name)
specifier|public
name|QueueInfo
name|getQueue
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getQueue
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**    * Get log parameters for the specified jobID or taskAttemptID    * @param jobID the job id.    * @param taskAttemptID the task attempt id. Optional.    * @return the LogParams    * @throws IOException    * @throws InterruptedException    */
DECL|method|getLogParams (JobID jobID, TaskAttemptID taskAttemptID)
specifier|public
name|LogParams
name|getLogParams
parameter_list|(
name|JobID
name|jobID
parameter_list|,
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getLogFileParams
argument_list|(
name|jobID
argument_list|,
name|taskAttemptID
argument_list|)
return|;
block|}
comment|/**    * Get current cluster status.    *     * @return object of {@link ClusterMetrics}    * @throws IOException    * @throws InterruptedException    */
DECL|method|getClusterStatus ()
specifier|public
name|ClusterMetrics
name|getClusterStatus
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getClusterMetrics
argument_list|()
return|;
block|}
comment|/**    * Get all active trackers in the cluster.    *     * @return array of {@link TaskTrackerInfo}    * @throws IOException    * @throws InterruptedException    */
DECL|method|getActiveTaskTrackers ()
specifier|public
name|TaskTrackerInfo
index|[]
name|getActiveTaskTrackers
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getActiveTrackers
argument_list|()
return|;
block|}
comment|/**    * Get blacklisted trackers.    *     * @return array of {@link TaskTrackerInfo}    * @throws IOException    * @throws InterruptedException    */
DECL|method|getBlackListedTaskTrackers ()
specifier|public
name|TaskTrackerInfo
index|[]
name|getBlackListedTaskTrackers
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getBlacklistedTrackers
argument_list|()
return|;
block|}
comment|/**    * Get all the jobs in cluster.    *     * @return array of {@link Job}    * @throws IOException    * @throws InterruptedException    * @deprecated Use {@link #getAllJobStatuses()} instead.    */
annotation|@
name|Deprecated
DECL|method|getAllJobs ()
specifier|public
name|Job
index|[]
name|getAllJobs
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|getJobs
argument_list|(
name|client
operator|.
name|getAllJobs
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Get job status for all jobs in the cluster.    * @return job status for all jobs in cluster    * @throws IOException    * @throws InterruptedException    */
DECL|method|getAllJobStatuses ()
specifier|public
name|JobStatus
index|[]
name|getAllJobStatuses
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getAllJobs
argument_list|()
return|;
block|}
comment|/**    * Grab the jobtracker system directory path where     * job-specific files will  be placed.    *     * @return the system directory where job-specific files are to be placed.    */
DECL|method|getSystemDir ()
specifier|public
name|Path
name|getSystemDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|sysDir
operator|==
literal|null
condition|)
block|{
name|sysDir
operator|=
operator|new
name|Path
argument_list|(
name|client
operator|.
name|getSystemDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sysDir
return|;
block|}
comment|/**    * Grab the jobtracker's view of the staging directory path where     * job-specific files will  be placed.    *     * @return the staging directory where job-specific files are to be placed.    */
DECL|method|getStagingAreaDir ()
specifier|public
name|Path
name|getStagingAreaDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|stagingAreaDir
operator|==
literal|null
condition|)
block|{
name|stagingAreaDir
operator|=
operator|new
name|Path
argument_list|(
name|client
operator|.
name|getStagingAreaDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|stagingAreaDir
return|;
block|}
comment|/**    * Get the job history file path for a given job id. The job history file at     * this path may or may not be existing depending on the job completion state.    * The file is present only for the completed jobs.    * @param jobId the JobID of the job submitted by the current user.    * @return the file path of the job history file    * @throws IOException    * @throws InterruptedException    */
DECL|method|getJobHistoryUrl (JobID jobId)
specifier|public
name|String
name|getJobHistoryUrl
parameter_list|(
name|JobID
name|jobId
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
if|if
condition|(
name|jobHistoryDir
operator|==
literal|null
condition|)
block|{
name|jobHistoryDir
operator|=
operator|new
name|Path
argument_list|(
name|client
operator|.
name|getJobHistoryDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Path
argument_list|(
name|jobHistoryDir
argument_list|,
name|jobId
operator|.
name|toString
argument_list|()
operator|+
literal|"_"
operator|+
name|ugi
operator|.
name|getShortUserName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**    * Gets the Queue ACLs for current user    * @return array of QueueAclsInfo object for current user.    * @throws IOException    */
DECL|method|getQueueAclsForCurrentUser ()
specifier|public
name|QueueAclsInfo
index|[]
name|getQueueAclsForCurrentUser
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getQueueAclsForCurrentUser
argument_list|()
return|;
block|}
comment|/**    * Gets the root level queues.    * @return array of JobQueueInfo object.    * @throws IOException    */
DECL|method|getRootQueues ()
specifier|public
name|QueueInfo
index|[]
name|getRootQueues
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getRootQueues
argument_list|()
return|;
block|}
comment|/**    * Returns immediate children of queueName.    * @param queueName    * @return array of JobQueueInfo which are children of queueName    * @throws IOException    */
DECL|method|getChildQueues (String queueName)
specifier|public
name|QueueInfo
index|[]
name|getChildQueues
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getChildQueues
argument_list|(
name|queueName
argument_list|)
return|;
block|}
comment|/**    * Get the JobTracker's status.    *     * @return {@link JobTrackerStatus} of the JobTracker    * @throws IOException    * @throws InterruptedException    */
DECL|method|getJobTrackerStatus ()
specifier|public
name|JobTrackerStatus
name|getJobTrackerStatus
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getJobTrackerStatus
argument_list|()
return|;
block|}
comment|/**    * Get the tasktracker expiry interval for the cluster    * @return the expiry interval in msec    */
DECL|method|getTaskTrackerExpiryInterval ()
specifier|public
name|long
name|getTaskTrackerExpiryInterval
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|client
operator|.
name|getTaskTrackerExpiryInterval
argument_list|()
return|;
block|}
comment|/**    * Get a delegation token for the user from the JobTracker.    * @param renewer the user who can renew the token    * @return the new token    * @throws IOException    */
specifier|public
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
DECL|method|getDelegationToken (Text renewer)
name|getDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// client has already set the service
return|return
name|client
operator|.
name|getDelegationToken
argument_list|(
name|renewer
argument_list|)
return|;
block|}
comment|/**    * Renew a delegation token    * @param token the token to renew    * @return the new expiration time    * @throws InvalidToken    * @throws IOException    * @deprecated Use {@link Token#renew} instead    */
DECL|method|renewDelegationToken (Token<DelegationTokenIdentifier> token )
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|InvalidToken
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|token
operator|.
name|renew
argument_list|(
name|getConf
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Cancel a delegation token from the JobTracker    * @param token the token to cancel    * @throws IOException    * @deprecated Use {@link Token#cancel} instead    */
DECL|method|cancelDelegationToken (Token<DelegationTokenIdentifier> token )
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|token
operator|.
name|cancel
argument_list|(
name|getConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

