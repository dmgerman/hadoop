begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|HashSet
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
name|mapreduce
operator|.
name|ClusterMetrics
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
name|QueueAclsInfo
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
name|QueueInfo
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
name|TaskTrackerInfo
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
name|api
operator|.
name|ApplicationClientProtocol
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
name|NodeReport
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
name|QueueUserACLInfo
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
name|NodeState
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
name|YarnClusterMetrics
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
name|client
operator|.
name|api
operator|.
name|YarnClient
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
name|client
operator|.
name|api
operator|.
name|YarnClientApplication
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|util
operator|.
name|ConverterUtils
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
DECL|class|ResourceMgrDelegate
specifier|public
class|class
name|ResourceMgrDelegate
extends|extends
name|YarnClient
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
name|ResourceMgrDelegate
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|application
specifier|private
name|ApplicationSubmissionContext
name|application
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
decl_stmt|;
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|field|client
specifier|protected
name|YarnClient
name|client
decl_stmt|;
DECL|field|rmAddress
specifier|private
name|InetSocketAddress
name|rmAddress
decl_stmt|;
comment|/**    * Delegate responsible for communicating with the Resource Manager's    * {@link ApplicationClientProtocol}.    * @param conf the configuration object.    */
DECL|method|ResourceMgrDelegate (YarnConfiguration conf)
specifier|public
name|ResourceMgrDelegate
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ResourceMgrDelegate
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|client
operator|=
name|YarnClient
operator|.
name|createYarnClient
argument_list|()
expr_stmt|;
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|start
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
name|this
operator|.
name|rmAddress
operator|=
name|conf
operator|.
name|getSocketAddr
argument_list|(
name|YarnConfiguration
operator|.
name|RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_PORT
argument_list|)
expr_stmt|;
name|client
operator|.
name|init
argument_list|(
name|conf
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
name|client
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStart
argument_list|()
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
name|client
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|getActiveTrackers ()
specifier|public
name|TaskTrackerInfo
index|[]
name|getActiveTrackers
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
return|return
name|TypeConverter
operator|.
name|fromYarnNodes
argument_list|(
name|client
operator|.
name|getNodeReports
argument_list|(
name|NodeState
operator|.
name|RUNNING
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getAllJobs ()
specifier|public
name|JobStatus
index|[]
name|getAllJobs
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|appTypes
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|appTypes
operator|.
name|add
argument_list|(
name|MRJobConfig
operator|.
name|MR_APPLICATION_TYPE
argument_list|)
expr_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnApps
argument_list|(
name|client
operator|.
name|getApplications
argument_list|(
name|appTypes
argument_list|)
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getBlacklistedTrackers ()
specifier|public
name|TaskTrackerInfo
index|[]
name|getBlacklistedTrackers
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// TODO: Implement getBlacklistedTrackers
name|LOG
operator|.
name|warn
argument_list|(
literal|"getBlacklistedTrackers - Not implemented yet"
argument_list|)
expr_stmt|;
return|return
operator|new
name|TaskTrackerInfo
index|[
literal|0
index|]
return|;
block|}
DECL|method|getClusterMetrics ()
specifier|public
name|ClusterMetrics
name|getClusterMetrics
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
name|YarnClusterMetrics
name|metrics
init|=
name|client
operator|.
name|getYarnClusterMetrics
argument_list|()
decl_stmt|;
name|ClusterMetrics
name|oldMetrics
init|=
operator|new
name|ClusterMetrics
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
name|metrics
operator|.
name|getNumNodeManagers
argument_list|()
operator|*
literal|10
argument_list|,
name|metrics
operator|.
name|getNumNodeManagers
argument_list|()
operator|*
literal|2
argument_list|,
literal|1
argument_list|,
name|metrics
operator|.
name|getNumNodeManagers
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
return|return
name|oldMetrics
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getConnectAddress ()
name|InetSocketAddress
name|getConnectAddress
parameter_list|()
block|{
return|return
name|rmAddress
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
DECL|method|getDelegationToken (Text renewer)
specifier|public
name|Token
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
try|try
block|{
return|return
name|ConverterUtils
operator|.
name|convertFromYarn
argument_list|(
name|client
operator|.
name|getRMDelegationToken
argument_list|(
name|renewer
argument_list|)
argument_list|,
name|rmAddress
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getFilesystemName ()
specifier|public
name|String
name|getFilesystemName
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getNewJobID ()
specifier|public
name|JobID
name|getNewJobID
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
name|this
operator|.
name|application
operator|=
name|client
operator|.
name|createApplication
argument_list|()
operator|.
name|getApplicationSubmissionContext
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationId
operator|=
name|this
operator|.
name|application
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|applicationId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getQueue (String queueName)
specifier|public
name|QueueInfo
name|getQueue
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
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
name|QueueInfo
name|queueInfo
init|=
name|client
operator|.
name|getQueueInfo
argument_list|(
name|queueName
argument_list|)
decl_stmt|;
return|return
operator|(
name|queueInfo
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|queueInfo
argument_list|,
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
try|try
block|{
return|return
name|TypeConverter
operator|.
name|fromYarnQueueUserAclsInfo
argument_list|(
name|client
operator|.
name|getQueueAclsInfo
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
try|try
block|{
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|client
operator|.
name|getAllQueues
argument_list|()
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
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
try|try
block|{
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|client
operator|.
name|getRootQueueInfos
argument_list|()
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getChildQueues (String parent)
specifier|public
name|QueueInfo
index|[]
name|getChildQueues
parameter_list|(
name|String
name|parent
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
try|try
block|{
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|client
operator|.
name|getChildQueueInfos
argument_list|(
name|parent
argument_list|)
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|getStagingAreaDir ()
specifier|public
name|String
name|getStagingAreaDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|//    Path path = new Path(MRJobConstants.JOB_SUBMIT_DIR);
name|String
name|user
init|=
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
operator|.
name|getShortUserName
argument_list|()
decl_stmt|;
name|Path
name|path
init|=
name|MRApps
operator|.
name|getStagingAreaDir
argument_list|(
name|conf
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"getStagingAreaDir: dir="
operator|+
name|path
argument_list|)
expr_stmt|;
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getSystemDir ()
specifier|public
name|String
name|getSystemDir
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Path
name|sysDir
init|=
operator|new
name|Path
argument_list|(
name|MRJobConfig
operator|.
name|JOB_SUBMIT_DIR
argument_list|)
decl_stmt|;
comment|//FileContext.getFileContext(conf).delete(sysDir, true);
return|return
name|sysDir
operator|.
name|toString
argument_list|()
return|;
block|}
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
literal|0
return|;
block|}
DECL|method|setJobPriority (JobID arg0, String arg1)
specifier|public
name|void
name|setJobPriority
parameter_list|(
name|JobID
name|arg0
parameter_list|,
name|String
name|arg1
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return;
block|}
DECL|method|getProtocolVersion (String arg0, long arg1)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|arg0
parameter_list|,
name|long
name|arg1
parameter_list|)
throws|throws
name|IOException
block|{
return|return
literal|0
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|applicationId
return|;
block|}
annotation|@
name|Override
DECL|method|createApplication ()
specifier|public
name|YarnClientApplication
name|createApplication
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|createApplication
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ApplicationId
DECL|method|submitApplication (ApplicationSubmissionContext appContext)
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContext
name|appContext
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|submitApplication
argument_list|(
name|appContext
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|killApplication (ApplicationId applicationId)
specifier|public
name|void
name|killApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
name|client
operator|.
name|killApplication
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getApplicationReport (ApplicationId appId)
specifier|public
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getApplicationReport
argument_list|(
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAMRMToken (ApplicationId appId)
specifier|public
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|getAMRMToken
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
DECL|method|getApplications ()
specifier|public
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getApplications
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getApplications ( Set<String> applicationTypes)
specifier|public
name|List
argument_list|<
name|ApplicationReport
argument_list|>
name|getApplications
parameter_list|(
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getApplications
argument_list|(
name|applicationTypes
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getYarnClusterMetrics ()
specifier|public
name|YarnClusterMetrics
name|getYarnClusterMetrics
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getYarnClusterMetrics
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNodeReports (NodeState... states)
specifier|public
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|(
name|NodeState
modifier|...
name|states
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getNodeReports
argument_list|(
name|states
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getRMDelegationToken ( Text renewer)
specifier|public
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
name|Token
name|getRMDelegationToken
parameter_list|(
name|Text
name|renewer
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getRMDelegationToken
argument_list|(
name|renewer
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueInfo ( String queueName)
specifier|public
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
name|QueueInfo
name|getQueueInfo
parameter_list|(
name|String
name|queueName
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getQueueInfo
argument_list|(
name|queueName
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAllQueues ()
specifier|public
name|List
argument_list|<
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
name|QueueInfo
argument_list|>
name|getAllQueues
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getAllQueues
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getRootQueueInfos ()
specifier|public
name|List
argument_list|<
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
name|QueueInfo
argument_list|>
name|getRootQueueInfos
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getRootQueueInfos
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getChildQueueInfos ( String parent)
specifier|public
name|List
argument_list|<
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
name|QueueInfo
argument_list|>
name|getChildQueueInfos
parameter_list|(
name|String
name|parent
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getChildQueueInfos
argument_list|(
name|parent
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getQueueAclsInfo ()
specifier|public
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|getQueueAclsInfo
parameter_list|()
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
name|client
operator|.
name|getQueueAclsInfo
argument_list|()
return|;
block|}
block|}
end_class

end_unit

