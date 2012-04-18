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
name|ClientRMProtocol
import|;
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
name|GetAllApplicationsRequest
import|;
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
name|GetAllApplicationsResponse
import|;
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
name|GetApplicationReportRequest
import|;
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
name|GetApplicationReportResponse
import|;
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
name|GetClusterMetricsRequest
import|;
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
name|GetClusterMetricsResponse
import|;
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
name|GetClusterNodesRequest
import|;
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
name|GetClusterNodesResponse
import|;
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
name|GetNewApplicationRequest
import|;
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
name|GetQueueInfoRequest
import|;
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
name|GetQueueUserAclsInfoRequest
import|;
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
name|KillApplicationRequest
import|;
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
name|SubmitApplicationRequest
import|;
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
name|DelegationToken
import|;
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
name|client
operator|.
name|RMDelegationTokenIdentifier
import|;
end_import

begin_comment
comment|// TODO: This should be part of something like yarn-client.
end_comment

begin_class
DECL|class|ResourceMgrDelegate
specifier|public
class|class
name|ResourceMgrDelegate
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
DECL|field|rmAddress
specifier|private
specifier|final
name|String
name|rmAddress
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|applicationsManager
name|ClientRMProtocol
name|applicationsManager
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
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
comment|/**    * Delegate responsible for communicating with the Resource Manager's {@link ClientRMProtocol}.    * @param conf the configuration object.    */
DECL|method|ResourceMgrDelegate (YarnConfiguration conf)
specifier|public
name|ResourceMgrDelegate
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|YarnRPC
name|rpc
init|=
name|YarnRPC
operator|.
name|create
argument_list|(
name|this
operator|.
name|conf
argument_list|)
decl_stmt|;
name|InetSocketAddress
name|rmAddress
init|=
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
decl_stmt|;
name|this
operator|.
name|rmAddress
operator|=
name|rmAddress
operator|.
name|toString
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connecting to ResourceManager at "
operator|+
name|rmAddress
argument_list|)
expr_stmt|;
name|applicationsManager
operator|=
operator|(
name|ClientRMProtocol
operator|)
name|rpc
operator|.
name|getProxy
argument_list|(
name|ClientRMProtocol
operator|.
name|class
argument_list|,
name|rmAddress
argument_list|,
name|this
operator|.
name|conf
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Connected to ResourceManager at "
operator|+
name|rmAddress
argument_list|)
expr_stmt|;
block|}
comment|/**    * Used for injecting applicationsManager, mostly for testing.    * @param conf the configuration object    * @param applicationsManager the handle to talk the resource managers     *                            {@link ClientRMProtocol}.    */
DECL|method|ResourceMgrDelegate (YarnConfiguration conf, ClientRMProtocol applicationsManager)
specifier|public
name|ResourceMgrDelegate
parameter_list|(
name|YarnConfiguration
name|conf
parameter_list|,
name|ClientRMProtocol
name|applicationsManager
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|applicationsManager
operator|=
name|applicationsManager
expr_stmt|;
name|this
operator|.
name|rmAddress
operator|=
name|applicationsManager
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
DECL|method|cancelDelegationToken (Token<DelegationTokenIdentifier> arg0)
specifier|public
name|void
name|cancelDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|arg0
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
return|return;
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
name|GetClusterNodesRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetClusterNodesRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetClusterNodesResponse
name|response
init|=
name|applicationsManager
operator|.
name|getClusterNodes
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnNodes
argument_list|(
name|response
operator|.
name|getNodeReports
argument_list|()
argument_list|)
return|;
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
name|GetAllApplicationsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetAllApplicationsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetAllApplicationsResponse
name|response
init|=
name|applicationsManager
operator|.
name|getAllApplications
argument_list|(
name|request
argument_list|)
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnApps
argument_list|(
name|response
operator|.
name|getApplicationList
argument_list|()
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
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
name|GetClusterMetricsRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetClusterMetricsRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|GetClusterMetricsResponse
name|response
init|=
name|applicationsManager
operator|.
name|getClusterMetrics
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|YarnClusterMetrics
name|metrics
init|=
name|response
operator|.
name|getClusterMetrics
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
comment|/* get the token from RM */
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
name|GetDelegationTokenRequest
name|rmDTRequest
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
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
name|GetDelegationTokenRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|rmDTRequest
operator|.
name|setRenewer
argument_list|(
name|renewer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
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
name|GetDelegationTokenResponse
name|response
init|=
name|applicationsManager
operator|.
name|getDelegationToken
argument_list|(
name|rmDTRequest
argument_list|)
decl_stmt|;
name|DelegationToken
name|yarnToken
init|=
name|response
operator|.
name|getRMDelegationToken
argument_list|()
decl_stmt|;
return|return
operator|new
name|Token
argument_list|<
name|RMDelegationTokenIdentifier
argument_list|>
argument_list|(
name|yarnToken
operator|.
name|getIdentifier
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
name|yarnToken
operator|.
name|getPassword
argument_list|()
operator|.
name|array
argument_list|()
argument_list|,
operator|new
name|Text
argument_list|(
name|yarnToken
operator|.
name|getKind
argument_list|()
argument_list|)
argument_list|,
operator|new
name|Text
argument_list|(
name|yarnToken
operator|.
name|getService
argument_list|()
argument_list|)
argument_list|)
return|;
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
name|GetNewApplicationRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetNewApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|applicationId
operator|=
name|applicationsManager
operator|.
name|getNewApplication
argument_list|(
name|request
argument_list|)
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
DECL|field|ROOT
specifier|private
specifier|static
specifier|final
name|String
name|ROOT
init|=
literal|"root"
decl_stmt|;
DECL|method|getQueueInfoRequest (String queueName, boolean includeApplications, boolean includeChildQueues, boolean recursive)
specifier|private
name|GetQueueInfoRequest
name|getQueueInfoRequest
parameter_list|(
name|String
name|queueName
parameter_list|,
name|boolean
name|includeApplications
parameter_list|,
name|boolean
name|includeChildQueues
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{
name|GetQueueInfoRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetQueueInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setQueueName
argument_list|(
name|queueName
argument_list|)
expr_stmt|;
name|request
operator|.
name|setIncludeApplications
argument_list|(
name|includeApplications
argument_list|)
expr_stmt|;
name|request
operator|.
name|setIncludeChildQueues
argument_list|(
name|includeChildQueues
argument_list|)
expr_stmt|;
name|request
operator|.
name|setRecursive
argument_list|(
name|recursive
argument_list|)
expr_stmt|;
return|return
name|request
return|;
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
name|GetQueueInfoRequest
name|request
init|=
name|getQueueInfoRequest
argument_list|(
name|queueName
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetQueueInfoRequest
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|applicationsManager
operator|.
name|getQueueInfo
argument_list|(
name|request
argument_list|)
operator|.
name|getQueueInfo
argument_list|()
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
DECL|method|getChildQueues (org.apache.hadoop.yarn.api.records.QueueInfo parent, List<org.apache.hadoop.yarn.api.records.QueueInfo> queues, boolean recursive)
specifier|private
name|void
name|getChildQueues
parameter_list|(
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
name|parent
parameter_list|,
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
name|queues
parameter_list|,
name|boolean
name|recursive
parameter_list|)
block|{
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
name|childQueues
init|=
name|parent
operator|.
name|getChildQueues
argument_list|()
decl_stmt|;
for|for
control|(
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
name|child
range|:
name|childQueues
control|)
block|{
name|queues
operator|.
name|add
argument_list|(
name|child
argument_list|)
expr_stmt|;
if|if
condition|(
name|recursive
condition|)
block|{
name|getChildQueues
argument_list|(
name|child
argument_list|,
name|queues
argument_list|,
name|recursive
argument_list|)
expr_stmt|;
block|}
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
name|GetQueueUserAclsInfoRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetQueueUserAclsInfoRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|QueueUserACLInfo
argument_list|>
name|userAcls
init|=
name|applicationsManager
operator|.
name|getQueueUserAcls
argument_list|(
name|request
argument_list|)
operator|.
name|getUserAclsInfoList
argument_list|()
decl_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnQueueUserAclsInfo
argument_list|(
name|userAcls
argument_list|)
return|;
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
name|queues
init|=
operator|new
name|ArrayList
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
argument_list|()
decl_stmt|;
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
name|rootQueue
init|=
name|applicationsManager
operator|.
name|getQueueInfo
argument_list|(
name|getQueueInfoRequest
argument_list|(
name|ROOT
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|getQueueInfo
argument_list|()
decl_stmt|;
name|getChildQueues
argument_list|(
name|rootQueue
argument_list|,
name|queues
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|queues
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
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
name|queues
init|=
operator|new
name|ArrayList
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
argument_list|()
decl_stmt|;
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
name|rootQueue
init|=
name|applicationsManager
operator|.
name|getQueueInfo
argument_list|(
name|getQueueInfoRequest
argument_list|(
name|ROOT
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
argument_list|)
operator|.
name|getQueueInfo
argument_list|()
decl_stmt|;
name|getChildQueues
argument_list|(
name|rootQueue
argument_list|,
name|queues
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|queues
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
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
name|queues
init|=
operator|new
name|ArrayList
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
argument_list|()
decl_stmt|;
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
name|parentQueue
init|=
name|applicationsManager
operator|.
name|getQueueInfo
argument_list|(
name|getQueueInfoRequest
argument_list|(
name|parent
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
operator|.
name|getQueueInfo
argument_list|()
decl_stmt|;
name|getChildQueues
argument_list|(
name|parentQueue
argument_list|,
name|queues
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|queues
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
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
DECL|method|renewDelegationToken (Token<DelegationTokenIdentifier> arg0)
specifier|public
name|long
name|renewDelegationToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|arg0
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// TODO: Implement renewDelegationToken
name|LOG
operator|.
name|warn
argument_list|(
literal|"renewDelegationToken - Not implemented"
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
DECL|method|submitApplication ( ApplicationSubmissionContext appContext)
specifier|public
name|ApplicationId
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContext
name|appContext
parameter_list|)
throws|throws
name|IOException
block|{
name|appContext
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|SubmitApplicationRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|SubmitApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationSubmissionContext
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
name|applicationsManager
operator|.
name|submitApplication
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Submitted application "
operator|+
name|applicationId
operator|+
literal|" to ResourceManager"
operator|+
literal|" at "
operator|+
name|rmAddress
argument_list|)
expr_stmt|;
return|return
name|applicationId
return|;
block|}
DECL|method|killApplication (ApplicationId applicationId)
specifier|public
name|void
name|killApplication
parameter_list|(
name|ApplicationId
name|applicationId
parameter_list|)
throws|throws
name|IOException
block|{
name|KillApplicationRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|KillApplicationRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationId
argument_list|(
name|applicationId
argument_list|)
expr_stmt|;
name|applicationsManager
operator|.
name|forceKillApplication
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Killing application "
operator|+
name|applicationId
argument_list|)
expr_stmt|;
block|}
DECL|method|getApplicationReport (ApplicationId appId)
specifier|public
name|ApplicationReport
name|getApplicationReport
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|YarnRemoteException
block|{
name|GetApplicationReportRequest
name|request
init|=
name|recordFactory
operator|.
name|newRecordInstance
argument_list|(
name|GetApplicationReportRequest
operator|.
name|class
argument_list|)
decl_stmt|;
name|request
operator|.
name|setApplicationId
argument_list|(
name|appId
argument_list|)
expr_stmt|;
name|GetApplicationReportResponse
name|response
init|=
name|applicationsManager
operator|.
name|getApplicationReport
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|ApplicationReport
name|applicationReport
init|=
name|response
operator|.
name|getApplicationReport
argument_list|()
decl_stmt|;
return|return
name|applicationReport
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
block|}
end_class

end_unit

