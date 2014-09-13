begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice
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
name|applicationhistoryservice
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
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|util
operator|.
name|ReflectionUtils
import|;
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
name|ApplicationAttemptReport
import|;
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
name|ContainerId
import|;
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
name|ContainerReport
import|;
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|records
operator|.
name|ApplicationAttemptHistoryData
import|;
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
name|ApplicationHistoryData
import|;
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
name|ContainerHistoryData
import|;
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
DECL|class|ApplicationHistoryManagerImpl
specifier|public
class|class
name|ApplicationHistoryManagerImpl
extends|extends
name|AbstractService
implements|implements
name|ApplicationHistoryManager
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
name|ApplicationHistoryManagerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|UNAVAILABLE
specifier|private
specifier|static
specifier|final
name|String
name|UNAVAILABLE
init|=
literal|"N/A"
decl_stmt|;
DECL|field|historyStore
specifier|private
name|ApplicationHistoryStore
name|historyStore
decl_stmt|;
DECL|field|serverHttpAddress
specifier|private
name|String
name|serverHttpAddress
decl_stmt|;
DECL|method|ApplicationHistoryManagerImpl ()
specifier|public
name|ApplicationHistoryManagerImpl
parameter_list|()
block|{
name|super
argument_list|(
name|ApplicationHistoryManagerImpl
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
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"ApplicationHistory Init"
argument_list|)
expr_stmt|;
name|historyStore
operator|=
name|createApplicationHistoryStore
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|historyStore
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|serverHttpAddress
operator|=
name|WebAppUtils
operator|.
name|getHttpSchemePrefix
argument_list|(
name|conf
argument_list|)
operator|+
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting ApplicationHistory"
argument_list|)
expr_stmt|;
name|historyStore
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping ApplicationHistory"
argument_list|)
expr_stmt|;
name|historyStore
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
DECL|method|createApplicationHistoryStore ( Configuration conf)
specifier|protected
name|ApplicationHistoryStore
name|createApplicationHistoryStore
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
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
argument_list|,
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAMContainer (ApplicationAttemptId appAttemptId)
specifier|public
name|ContainerReport
name|getAMContainer
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
block|{
name|ApplicationReport
name|app
init|=
name|getApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|convertToContainerReport
argument_list|(
name|historyStore
operator|.
name|getAMContainer
argument_list|(
name|appAttemptId
argument_list|)
argument_list|,
name|app
operator|==
literal|null
condition|?
literal|null
else|:
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getAllApplications ()
specifier|public
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationReport
argument_list|>
name|getAllApplications
parameter_list|()
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationHistoryData
argument_list|>
name|histData
init|=
name|historyStore
operator|.
name|getAllApplications
argument_list|()
decl_stmt|;
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationReport
argument_list|>
name|applicationsReport
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|ApplicationHistoryData
argument_list|>
name|entry
range|:
name|histData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|applicationsReport
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|convertToApplicationReport
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|applicationsReport
return|;
block|}
annotation|@
name|Override
DECL|method|getApplication (ApplicationId appId)
specifier|public
name|ApplicationReport
name|getApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|convertToApplicationReport
argument_list|(
name|historyStore
operator|.
name|getApplication
argument_list|(
name|appId
argument_list|)
argument_list|)
return|;
block|}
DECL|method|convertToApplicationReport ( ApplicationHistoryData appHistory)
specifier|private
name|ApplicationReport
name|convertToApplicationReport
parameter_list|(
name|ApplicationHistoryData
name|appHistory
parameter_list|)
throws|throws
name|IOException
block|{
name|ApplicationAttemptId
name|currentApplicationAttemptId
init|=
literal|null
decl_stmt|;
name|String
name|trackingUrl
init|=
name|UNAVAILABLE
decl_stmt|;
name|String
name|host
init|=
name|UNAVAILABLE
decl_stmt|;
name|int
name|rpcPort
init|=
operator|-
literal|1
decl_stmt|;
name|ApplicationAttemptHistoryData
name|lastAttempt
init|=
name|getLastAttempt
argument_list|(
name|appHistory
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastAttempt
operator|!=
literal|null
condition|)
block|{
name|currentApplicationAttemptId
operator|=
name|lastAttempt
operator|.
name|getApplicationAttemptId
argument_list|()
expr_stmt|;
name|trackingUrl
operator|=
name|lastAttempt
operator|.
name|getTrackingURL
argument_list|()
expr_stmt|;
name|host
operator|=
name|lastAttempt
operator|.
name|getHost
argument_list|()
expr_stmt|;
name|rpcPort
operator|=
name|lastAttempt
operator|.
name|getRPCPort
argument_list|()
expr_stmt|;
block|}
return|return
name|ApplicationReport
operator|.
name|newInstance
argument_list|(
name|appHistory
operator|.
name|getApplicationId
argument_list|()
argument_list|,
name|currentApplicationAttemptId
argument_list|,
name|appHistory
operator|.
name|getUser
argument_list|()
argument_list|,
name|appHistory
operator|.
name|getQueue
argument_list|()
argument_list|,
name|appHistory
operator|.
name|getApplicationName
argument_list|()
argument_list|,
name|host
argument_list|,
name|rpcPort
argument_list|,
literal|null
argument_list|,
name|appHistory
operator|.
name|getYarnApplicationState
argument_list|()
argument_list|,
name|appHistory
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|,
name|trackingUrl
argument_list|,
name|appHistory
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|appHistory
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|appHistory
operator|.
name|getFinalApplicationStatus
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|""
argument_list|,
literal|100
argument_list|,
name|appHistory
operator|.
name|getApplicationType
argument_list|()
argument_list|,
literal|null
argument_list|)
return|;
block|}
DECL|method|getLastAttempt (ApplicationId appId)
specifier|private
name|ApplicationAttemptHistoryData
name|getLastAttempt
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptHistoryData
argument_list|>
name|attempts
init|=
name|historyStore
operator|.
name|getApplicationAttempts
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|ApplicationAttemptId
name|prevMaxAttemptId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|ApplicationAttemptId
name|attemptId
range|:
name|attempts
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
name|prevMaxAttemptId
operator|==
literal|null
condition|)
block|{
name|prevMaxAttemptId
operator|=
name|attemptId
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|prevMaxAttemptId
operator|.
name|getAttemptId
argument_list|()
operator|<
name|attemptId
operator|.
name|getAttemptId
argument_list|()
condition|)
block|{
name|prevMaxAttemptId
operator|=
name|attemptId
expr_stmt|;
block|}
block|}
block|}
return|return
name|attempts
operator|.
name|get
argument_list|(
name|prevMaxAttemptId
argument_list|)
return|;
block|}
DECL|method|convertToApplicationAttemptReport ( ApplicationAttemptHistoryData appAttemptHistory)
specifier|private
name|ApplicationAttemptReport
name|convertToApplicationAttemptReport
parameter_list|(
name|ApplicationAttemptHistoryData
name|appAttemptHistory
parameter_list|)
block|{
return|return
name|ApplicationAttemptReport
operator|.
name|newInstance
argument_list|(
name|appAttemptHistory
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|appAttemptHistory
operator|.
name|getHost
argument_list|()
argument_list|,
name|appAttemptHistory
operator|.
name|getRPCPort
argument_list|()
argument_list|,
name|appAttemptHistory
operator|.
name|getTrackingURL
argument_list|()
argument_list|,
literal|null
argument_list|,
name|appAttemptHistory
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|,
name|appAttemptHistory
operator|.
name|getYarnApplicationAttemptState
argument_list|()
argument_list|,
name|appAttemptHistory
operator|.
name|getMasterContainerId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getApplicationAttempt ( ApplicationAttemptId appAttemptId)
specifier|public
name|ApplicationAttemptReport
name|getApplicationAttempt
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|convertToApplicationAttemptReport
argument_list|(
name|historyStore
operator|.
name|getApplicationAttempt
argument_list|(
name|appAttemptId
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptReport
argument_list|>
DECL|method|getApplicationAttempts (ApplicationId appId)
name|getApplicationAttempts
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptHistoryData
argument_list|>
name|histData
init|=
name|historyStore
operator|.
name|getApplicationAttempts
argument_list|(
name|appId
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptReport
argument_list|>
name|applicationAttemptsReport
init|=
operator|new
name|HashMap
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationAttemptId
argument_list|,
name|ApplicationAttemptHistoryData
argument_list|>
name|entry
range|:
name|histData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|applicationAttemptsReport
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|convertToApplicationAttemptReport
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|applicationAttemptsReport
return|;
block|}
annotation|@
name|Override
DECL|method|getContainer (ContainerId containerId)
specifier|public
name|ContainerReport
name|getContainer
parameter_list|(
name|ContainerId
name|containerId
parameter_list|)
throws|throws
name|IOException
block|{
name|ApplicationReport
name|app
init|=
name|getApplication
argument_list|(
name|containerId
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|convertToContainerReport
argument_list|(
name|historyStore
operator|.
name|getContainer
argument_list|(
name|containerId
argument_list|)
argument_list|,
name|app
operator|==
literal|null
condition|?
literal|null
else|:
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
return|;
block|}
DECL|method|convertToContainerReport ( ContainerHistoryData containerHistory, String user)
specifier|private
name|ContainerReport
name|convertToContainerReport
parameter_list|(
name|ContainerHistoryData
name|containerHistory
parameter_list|,
name|String
name|user
parameter_list|)
block|{
comment|// If the container has the aggregated log, add the server root url
name|String
name|logUrl
init|=
name|WebAppUtils
operator|.
name|getAggregatedLogURL
argument_list|(
name|serverHttpAddress
argument_list|,
name|containerHistory
operator|.
name|getAssignedNode
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getContainerId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|)
decl_stmt|;
return|return
name|ContainerReport
operator|.
name|newInstance
argument_list|(
name|containerHistory
operator|.
name|getContainerId
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getAllocatedResource
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getAssignedNode
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getPriority
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getStartTime
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getFinishTime
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getDiagnosticsInfo
argument_list|()
argument_list|,
name|logUrl
argument_list|,
name|containerHistory
operator|.
name|getContainerExitStatus
argument_list|()
argument_list|,
name|containerHistory
operator|.
name|getContainerState
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getContainers ( ApplicationAttemptId appAttemptId)
specifier|public
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerReport
argument_list|>
name|getContainers
parameter_list|(
name|ApplicationAttemptId
name|appAttemptId
parameter_list|)
throws|throws
name|IOException
block|{
name|ApplicationReport
name|app
init|=
name|getApplication
argument_list|(
name|appAttemptId
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ContainerHistoryData
argument_list|>
name|histData
init|=
name|historyStore
operator|.
name|getContainers
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerReport
argument_list|>
name|containersReport
init|=
operator|new
name|HashMap
argument_list|<
name|ContainerId
argument_list|,
name|ContainerReport
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|ContainerHistoryData
argument_list|>
name|entry
range|:
name|histData
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|containersReport
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|convertToContainerReport
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|app
operator|==
literal|null
condition|?
literal|null
else|:
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|containersReport
return|;
block|}
annotation|@
name|Private
annotation|@
name|VisibleForTesting
DECL|method|getHistoryStore ()
specifier|public
name|ApplicationHistoryStore
name|getHistoryStore
parameter_list|()
block|{
return|return
name|this
operator|.
name|historyStore
return|;
block|}
block|}
end_class

end_unit

