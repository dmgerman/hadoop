begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|gson
operator|.
name|Gson
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
name|lang3
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
name|Container
import|;
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
name|scheduler
operator|.
name|AbstractYarnScheduler
import|;
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
name|scheduler
operator|.
name|SchedulerApplicationAttempt
import|;
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

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"appAttempt"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|AppAttemptInfo
specifier|public
class|class
name|AppAttemptInfo
block|{
DECL|field|id
specifier|protected
name|int
name|id
decl_stmt|;
DECL|field|startTime
specifier|protected
name|long
name|startTime
decl_stmt|;
DECL|field|finishedTime
specifier|protected
name|long
name|finishedTime
decl_stmt|;
DECL|field|containerId
specifier|protected
name|String
name|containerId
decl_stmt|;
DECL|field|nodeHttpAddress
specifier|protected
name|String
name|nodeHttpAddress
decl_stmt|;
DECL|field|nodeId
specifier|protected
name|String
name|nodeId
decl_stmt|;
DECL|field|logsLink
specifier|protected
name|String
name|logsLink
decl_stmt|;
DECL|field|blacklistedNodes
specifier|protected
name|String
name|blacklistedNodes
decl_stmt|;
DECL|field|nodesBlacklistedBySystem
specifier|private
name|String
name|nodesBlacklistedBySystem
decl_stmt|;
DECL|field|appAttemptId
specifier|protected
name|String
name|appAttemptId
decl_stmt|;
DECL|field|exportPorts
specifier|private
name|String
name|exportPorts
decl_stmt|;
DECL|method|AppAttemptInfo ()
specifier|public
name|AppAttemptInfo
parameter_list|()
block|{   }
DECL|method|AppAttemptInfo (ResourceManager rm, RMAppAttempt attempt, String user, String schemePrefix)
specifier|public
name|AppAttemptInfo
parameter_list|(
name|ResourceManager
name|rm
parameter_list|,
name|RMAppAttempt
name|attempt
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|schemePrefix
parameter_list|)
block|{
name|this
operator|.
name|startTime
operator|=
literal|0
expr_stmt|;
name|this
operator|.
name|containerId
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|nodeHttpAddress
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|logsLink
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|blacklistedNodes
operator|=
literal|""
expr_stmt|;
name|this
operator|.
name|exportPorts
operator|=
literal|""
expr_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|id
operator|=
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|getAttemptId
argument_list|()
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|attempt
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishedTime
operator|=
name|attempt
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|Container
name|masterContainer
init|=
name|attempt
operator|.
name|getMasterContainer
argument_list|()
decl_stmt|;
if|if
condition|(
name|masterContainer
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|containerId
operator|=
name|masterContainer
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeHttpAddress
operator|=
name|masterContainer
operator|.
name|getNodeHttpAddress
argument_list|()
expr_stmt|;
name|this
operator|.
name|nodeId
operator|=
name|masterContainer
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|logsLink
operator|=
name|WebAppUtils
operator|.
name|getRunningLogURL
argument_list|(
name|schemePrefix
operator|+
name|masterContainer
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|,
name|masterContainer
operator|.
name|getId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|user
argument_list|)
expr_stmt|;
name|Gson
name|gson
init|=
operator|new
name|Gson
argument_list|()
decl_stmt|;
name|this
operator|.
name|exportPorts
operator|=
name|gson
operator|.
name|toJson
argument_list|(
name|masterContainer
operator|.
name|getExposedPorts
argument_list|()
argument_list|)
expr_stmt|;
name|nodesBlacklistedBySystem
operator|=
name|StringUtils
operator|.
name|join
argument_list|(
name|attempt
operator|.
name|getAMBlacklistManager
argument_list|()
operator|.
name|getBlacklistUpdates
argument_list|()
operator|.
name|getBlacklistAdditions
argument_list|()
argument_list|,
literal|", "
argument_list|)
expr_stmt|;
if|if
condition|(
name|rm
operator|.
name|getResourceScheduler
argument_list|()
operator|instanceof
name|AbstractYarnScheduler
condition|)
block|{
name|AbstractYarnScheduler
name|ayScheduler
init|=
operator|(
name|AbstractYarnScheduler
operator|)
name|rm
operator|.
name|getResourceScheduler
argument_list|()
decl_stmt|;
name|SchedulerApplicationAttempt
name|sattempt
init|=
name|ayScheduler
operator|.
name|getApplicationAttempt
argument_list|(
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|sattempt
operator|!=
literal|null
condition|)
block|{
name|blacklistedNodes
operator|=
name|StringUtils
operator|.
name|join
argument_list|(
name|sattempt
operator|.
name|getBlacklistedNodes
argument_list|()
argument_list|,
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|this
operator|.
name|appAttemptId
operator|=
name|attempt
operator|.
name|getAppAttemptId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getAttemptId ()
specifier|public
name|int
name|getAttemptId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|getStartTime ()
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|startTime
return|;
block|}
DECL|method|getFinishedTime ()
specifier|public
name|long
name|getFinishedTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|finishedTime
return|;
block|}
DECL|method|getNodeHttpAddress ()
specifier|public
name|String
name|getNodeHttpAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|nodeHttpAddress
return|;
block|}
DECL|method|getLogsLink ()
specifier|public
name|String
name|getLogsLink
parameter_list|()
block|{
return|return
name|this
operator|.
name|logsLink
return|;
block|}
DECL|method|getAppAttemptId ()
specifier|public
name|String
name|getAppAttemptId
parameter_list|()
block|{
return|return
name|this
operator|.
name|appAttemptId
return|;
block|}
block|}
end_class

end_unit

