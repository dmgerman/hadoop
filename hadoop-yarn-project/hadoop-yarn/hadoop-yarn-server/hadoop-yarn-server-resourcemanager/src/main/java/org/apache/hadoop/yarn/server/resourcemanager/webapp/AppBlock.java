begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp
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
name|webapp
package|;
end_package

begin_import
import|import static
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
name|StringHelper
operator|.
name|join
import|;
end_import

begin_import
import|import static
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
name|YarnWebParams
operator|.
name|APPLICATION_ID
import|;
end_import

begin_import
import|import static
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
name|view
operator|.
name|JQueryUI
operator|.
name|_EVEN
import|;
end_import

begin_import
import|import static
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
name|view
operator|.
name|JQueryUI
operator|.
name|_INFO_WRAP
import|;
end_import

begin_import
import|import static
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
name|view
operator|.
name|JQueryUI
operator|.
name|_ODD
import|;
end_import

begin_import
import|import static
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
name|view
operator|.
name|JQueryUI
operator|.
name|_TH
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
name|util
operator|.
name|StringUtils
import|;
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
name|QueueACL
import|;
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
import|;
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
name|RMAppMetrics
import|;
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
name|RMAppAttemptMetrics
import|;
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
name|security
operator|.
name|QueueACLsManager
import|;
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
name|webapp
operator|.
name|dao
operator|.
name|AppAttemptInfo
import|;
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
name|webapp
operator|.
name|dao
operator|.
name|AppInfo
import|;
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
name|security
operator|.
name|ApplicationACLsManager
import|;
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
name|Apps
import|;
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
name|Times
import|;
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
name|resource
operator|.
name|Resources
import|;
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
name|hamlet
operator|.
name|Hamlet
import|;
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|DIV
import|;
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
name|hamlet
operator|.
name|Hamlet
operator|.
name|TABLE
import|;
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
name|util
operator|.
name|WebAppUtils
import|;
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
name|view
operator|.
name|HtmlBlock
import|;
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
name|view
operator|.
name|InfoBlock
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Inject
import|;
end_import

begin_class
DECL|class|AppBlock
specifier|public
class|class
name|AppBlock
extends|extends
name|HtmlBlock
block|{
DECL|field|aclsManager
specifier|private
name|ApplicationACLsManager
name|aclsManager
decl_stmt|;
DECL|field|queueACLsManager
specifier|private
name|QueueACLsManager
name|queueACLsManager
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
annotation|@
name|Inject
DECL|method|AppBlock (ResourceManager rm, ViewContext ctx, ApplicationACLsManager aclsManager, QueueACLsManager queueACLsManager, Configuration conf)
name|AppBlock
parameter_list|(
name|ResourceManager
name|rm
parameter_list|,
name|ViewContext
name|ctx
parameter_list|,
name|ApplicationACLsManager
name|aclsManager
parameter_list|,
name|QueueACLsManager
name|queueACLsManager
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|ctx
argument_list|)
expr_stmt|;
name|this
operator|.
name|aclsManager
operator|=
name|aclsManager
expr_stmt|;
name|this
operator|.
name|queueACLsManager
operator|=
name|queueACLsManager
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|render (Block html)
specifier|protected
name|void
name|render
parameter_list|(
name|Block
name|html
parameter_list|)
block|{
name|String
name|aid
init|=
name|$
argument_list|(
name|APPLICATION_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|aid
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|puts
argument_list|(
literal|"Bad request: requires application ID"
argument_list|)
expr_stmt|;
return|return;
block|}
name|ApplicationId
name|appID
init|=
literal|null
decl_stmt|;
try|try
block|{
name|appID
operator|=
name|Apps
operator|.
name|toAppID
argument_list|(
name|aid
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|puts
argument_list|(
literal|"Invalid Application ID: "
operator|+
name|aid
argument_list|)
expr_stmt|;
return|return;
block|}
name|RMContext
name|context
init|=
name|getInstance
argument_list|(
name|RMContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|context
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|==
literal|null
condition|)
block|{
name|puts
argument_list|(
literal|"Application not found: "
operator|+
name|aid
argument_list|)
expr_stmt|;
return|return;
block|}
name|AppInfo
name|app
init|=
operator|new
name|AppInfo
argument_list|(
name|rmApp
argument_list|,
literal|true
argument_list|,
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
comment|// Check for the authorization.
name|String
name|remoteUser
init|=
name|request
argument_list|()
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|remoteUser
operator|!=
literal|null
condition|)
block|{
name|callerUGI
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|callerUGI
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|this
operator|.
name|aclsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|ApplicationAccessType
operator|.
name|VIEW_APP
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|,
name|appID
argument_list|)
operator|||
name|this
operator|.
name|queueACLsManager
operator|.
name|checkAccess
argument_list|(
name|callerUGI
argument_list|,
name|QueueACL
operator|.
name|ADMINISTER_QUEUE
argument_list|,
name|app
operator|.
name|getQueue
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|puts
argument_list|(
literal|"You (User "
operator|+
name|remoteUser
operator|+
literal|") are not authorized to view application "
operator|+
name|appID
argument_list|)
expr_stmt|;
return|return;
block|}
name|setTitle
argument_list|(
name|join
argument_list|(
literal|"Application "
argument_list|,
name|aid
argument_list|)
argument_list|)
expr_stmt|;
name|RMAppMetrics
name|appMerics
init|=
name|rmApp
operator|.
name|getRMAppMetrics
argument_list|()
decl_stmt|;
comment|// Get attempt metrics and fields, it is possible currentAttempt of RMApp is
comment|// null. In that case, we will assume resource preempted and number of Non
comment|// AM container preempted on that attempt is 0
name|RMAppAttemptMetrics
name|attemptMetrics
decl_stmt|;
if|if
condition|(
literal|null
operator|==
name|rmApp
operator|.
name|getCurrentAppAttempt
argument_list|()
condition|)
block|{
name|attemptMetrics
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|attemptMetrics
operator|=
name|rmApp
operator|.
name|getCurrentAppAttempt
argument_list|()
operator|.
name|getRMAppAttemptMetrics
argument_list|()
expr_stmt|;
block|}
name|Resource
name|attemptResourcePreempted
init|=
name|attemptMetrics
operator|==
literal|null
condition|?
name|Resources
operator|.
name|none
argument_list|()
else|:
name|attemptMetrics
operator|.
name|getResourcePreempted
argument_list|()
decl_stmt|;
name|int
name|attemptNumNonAMContainerPreempted
init|=
name|attemptMetrics
operator|==
literal|null
condition|?
literal|0
else|:
name|attemptMetrics
operator|.
name|getNumNonAMContainersPreempted
argument_list|()
decl_stmt|;
name|info
argument_list|(
literal|"Application Overview"
argument_list|)
operator|.
name|_
argument_list|(
literal|"User:"
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Name:"
argument_list|,
name|app
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Application Type:"
argument_list|,
name|app
operator|.
name|getApplicationType
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Application Tags:"
argument_list|,
name|app
operator|.
name|getApplicationTags
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"State:"
argument_list|,
name|app
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"FinalStatus:"
argument_list|,
name|app
operator|.
name|getFinalStatus
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Started:"
argument_list|,
name|Times
operator|.
name|format
argument_list|(
name|app
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Elapsed:"
argument_list|,
name|StringUtils
operator|.
name|formatTime
argument_list|(
name|Times
operator|.
name|elapsed
argument_list|(
name|app
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|app
operator|.
name|getFinishTime
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Tracking URL:"
argument_list|,
operator|!
name|app
operator|.
name|isTrackingUrlReady
argument_list|()
condition|?
literal|"#"
else|:
name|app
operator|.
name|getTrackingUrlPretty
argument_list|()
argument_list|,
name|app
operator|.
name|getTrackingUI
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Diagnostics:"
argument_list|,
name|app
operator|.
name|getNote
argument_list|()
argument_list|)
expr_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|pdiv
init|=
name|html
operator|.
name|_
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
name|info
argument_list|(
literal|"Application Overview"
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
name|info
argument_list|(
literal|"Application Metrics"
argument_list|)
operator|.
name|_
argument_list|(
literal|"Total Resource Preempted:"
argument_list|,
name|appMerics
operator|.
name|getResourcePreempted
argument_list|()
argument_list|)
operator|.
name|_
argument_list|(
literal|"Total Number of Non-AM Containers Preempted:"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|appMerics
operator|.
name|getNumNonAMContainersPreempted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Total Number of AM Containers Preempted:"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|appMerics
operator|.
name|getNumAMContainersPreempted
argument_list|()
argument_list|)
argument_list|)
operator|.
name|_
argument_list|(
literal|"Resource Preempted from Current Attempt:"
argument_list|,
name|attemptResourcePreempted
argument_list|)
operator|.
name|_
argument_list|(
literal|"Number of Non-AM Containers Preempted from Current Attempt:"
argument_list|,
name|attemptNumNonAMContainerPreempted
argument_list|)
operator|.
name|_
argument_list|(
literal|"Aggregate Resource Allocation:"
argument_list|,
name|String
operator|.
name|format
argument_list|(
literal|"%d MB-seconds, %d vcore-seconds"
argument_list|,
name|appMerics
operator|.
name|getMemorySeconds
argument_list|()
argument_list|,
name|appMerics
operator|.
name|getVcoreSeconds
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|pdiv
operator|.
name|_
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|RMAppAttempt
argument_list|>
name|attempts
init|=
name|rmApp
operator|.
name|getAppAttempts
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
name|String
name|amString
init|=
name|attempts
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|?
literal|"ApplicationMaster"
else|:
literal|"ApplicationMasters"
decl_stmt|;
name|DIV
argument_list|<
name|Hamlet
argument_list|>
name|div
init|=
name|html
operator|.
name|_
argument_list|(
name|InfoBlock
operator|.
name|class
argument_list|)
operator|.
name|div
argument_list|(
name|_INFO_WRAP
argument_list|)
decl_stmt|;
comment|// MRAppMasters Table
name|TABLE
argument_list|<
name|DIV
argument_list|<
name|Hamlet
argument_list|>
argument_list|>
name|table
init|=
name|div
operator|.
name|table
argument_list|(
literal|"#app"
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|amString
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|tr
argument_list|()
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Attempt Number"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Start Time"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Node"
argument_list|)
operator|.
name|th
argument_list|(
name|_TH
argument_list|,
literal|"Logs"
argument_list|)
operator|.
name|_
argument_list|()
expr_stmt|;
name|boolean
name|odd
init|=
literal|false
decl_stmt|;
for|for
control|(
name|RMAppAttempt
name|attempt
range|:
name|attempts
control|)
block|{
name|AppAttemptInfo
name|attemptInfo
init|=
operator|new
name|AppAttemptInfo
argument_list|(
name|attempt
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|table
operator|.
name|tr
argument_list|(
operator|(
name|odd
operator|=
operator|!
name|odd
operator|)
condition|?
name|_ODD
else|:
name|_EVEN
argument_list|)
operator|.
name|td
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|attemptInfo
operator|.
name|getAttemptId
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|(
name|Times
operator|.
name|format
argument_list|(
name|attemptInfo
operator|.
name|getStartTime
argument_list|()
argument_list|)
argument_list|)
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|".nodelink"
argument_list|,
name|url
argument_list|(
literal|"//"
argument_list|,
name|attemptInfo
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
argument_list|,
name|attemptInfo
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|td
argument_list|()
operator|.
name|a
argument_list|(
literal|".logslink"
argument_list|,
name|url
argument_list|(
name|attemptInfo
operator|.
name|getLogsLink
argument_list|()
argument_list|)
argument_list|,
literal|"logs"
argument_list|)
operator|.
name|_
argument_list|()
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
name|table
operator|.
name|_
argument_list|()
expr_stmt|;
name|div
operator|.
name|_
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

