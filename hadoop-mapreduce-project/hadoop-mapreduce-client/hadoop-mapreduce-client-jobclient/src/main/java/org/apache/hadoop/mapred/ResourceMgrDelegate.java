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
name|GetNewApplicationResponse
import|;
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
name|util
operator|.
name|ProtoUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|client
operator|.
name|YarnClientImpl
import|;
end_import

begin_class
DECL|class|ResourceMgrDelegate
specifier|public
class|class
name|ResourceMgrDelegate
extends|extends
name|YarnClientImpl
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
name|GetNewApplicationResponse
name|application
decl_stmt|;
DECL|field|applicationId
specifier|private
name|ApplicationId
name|applicationId
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
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
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
return|return
name|TypeConverter
operator|.
name|fromYarnNodes
argument_list|(
name|super
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
return|return
name|TypeConverter
operator|.
name|fromYarnApps
argument_list|(
name|super
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
name|YarnClusterMetrics
name|metrics
init|=
name|super
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
return|return
name|ProtoUtils
operator|.
name|convertFromProtoFormat
argument_list|(
name|super
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
name|this
operator|.
name|application
operator|=
name|super
operator|.
name|getNewApplication
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
return|return
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|super
operator|.
name|getQueueInfo
argument_list|(
name|queueName
argument_list|)
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
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
return|return
name|TypeConverter
operator|.
name|fromYarnQueueUserAclsInfo
argument_list|(
name|super
operator|.
name|getQueueAclsInfo
argument_list|()
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
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|super
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
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|super
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
return|return
name|TypeConverter
operator|.
name|fromYarnQueueInfo
argument_list|(
name|super
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

