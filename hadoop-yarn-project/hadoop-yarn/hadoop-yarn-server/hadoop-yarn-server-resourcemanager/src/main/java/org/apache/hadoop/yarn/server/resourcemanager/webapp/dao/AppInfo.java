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
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlTransient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|http
operator|.
name|HttpConfig
import|;
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
name|RMAppState
import|;
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
name|util
operator|.
name|ConverterUtils
import|;
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
literal|"app"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|AppInfo
specifier|public
class|class
name|AppInfo
block|{
annotation|@
name|XmlTransient
DECL|field|appIdNum
specifier|protected
name|String
name|appIdNum
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|trackingUrlIsNotReady
specifier|protected
name|boolean
name|trackingUrlIsNotReady
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|trackingUrlPretty
specifier|protected
name|String
name|trackingUrlPretty
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|amContainerLogsExist
specifier|protected
name|boolean
name|amContainerLogsExist
init|=
literal|false
decl_stmt|;
annotation|@
name|XmlTransient
DECL|field|applicationId
specifier|protected
name|ApplicationId
name|applicationId
decl_stmt|;
comment|// these are ok for any user to see
DECL|field|id
specifier|protected
name|String
name|id
decl_stmt|;
DECL|field|user
specifier|protected
name|String
name|user
decl_stmt|;
DECL|field|name
specifier|protected
name|String
name|name
decl_stmt|;
DECL|field|queue
specifier|protected
name|String
name|queue
decl_stmt|;
DECL|field|state
specifier|protected
name|RMAppState
name|state
decl_stmt|;
DECL|field|finalStatus
specifier|protected
name|FinalApplicationStatus
name|finalStatus
decl_stmt|;
DECL|field|progress
specifier|protected
name|float
name|progress
decl_stmt|;
DECL|field|trackingUI
specifier|protected
name|String
name|trackingUI
decl_stmt|;
DECL|field|trackingUrl
specifier|protected
name|String
name|trackingUrl
decl_stmt|;
DECL|field|diagnostics
specifier|protected
name|String
name|diagnostics
decl_stmt|;
DECL|field|clusterId
specifier|protected
name|long
name|clusterId
decl_stmt|;
DECL|field|applicationType
specifier|protected
name|String
name|applicationType
decl_stmt|;
comment|// these are only allowed if acls allow
DECL|field|startedTime
specifier|protected
name|long
name|startedTime
decl_stmt|;
DECL|field|finishedTime
specifier|protected
name|long
name|finishedTime
decl_stmt|;
DECL|field|elapsedTime
specifier|protected
name|long
name|elapsedTime
decl_stmt|;
DECL|field|amContainerLogs
specifier|protected
name|String
name|amContainerLogs
decl_stmt|;
DECL|field|amHostHttpAddress
specifier|protected
name|String
name|amHostHttpAddress
decl_stmt|;
DECL|method|AppInfo ()
specifier|public
name|AppInfo
parameter_list|()
block|{   }
comment|// JAXB needs this
DECL|method|AppInfo (RMApp app, Boolean hasAccess, String host)
specifier|public
name|AppInfo
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|Boolean
name|hasAccess
parameter_list|,
name|String
name|host
parameter_list|)
block|{
name|this
argument_list|(
name|app
argument_list|,
name|hasAccess
argument_list|)
expr_stmt|;
block|}
DECL|method|AppInfo (RMApp app, Boolean hasAccess)
specifier|public
name|AppInfo
parameter_list|(
name|RMApp
name|app
parameter_list|,
name|Boolean
name|hasAccess
parameter_list|)
block|{
if|if
condition|(
name|app
operator|!=
literal|null
condition|)
block|{
name|String
name|trackingUrl
init|=
name|app
operator|.
name|getTrackingUrl
argument_list|()
decl_stmt|;
name|this
operator|.
name|state
operator|=
name|app
operator|.
name|getState
argument_list|()
expr_stmt|;
name|this
operator|.
name|trackingUrlIsNotReady
operator|=
name|trackingUrl
operator|==
literal|null
operator|||
name|trackingUrl
operator|.
name|isEmpty
argument_list|()
operator|||
name|RMAppState
operator|.
name|NEW
operator|==
name|this
operator|.
name|state
operator|||
name|RMAppState
operator|.
name|NEW_SAVING
operator|==
name|this
operator|.
name|state
operator|||
name|RMAppState
operator|.
name|SUBMITTED
operator|==
name|this
operator|.
name|state
operator|||
name|RMAppState
operator|.
name|ACCEPTED
operator|==
name|this
operator|.
name|state
expr_stmt|;
name|this
operator|.
name|trackingUI
operator|=
name|this
operator|.
name|trackingUrlIsNotReady
condition|?
literal|"UNASSIGNED"
else|:
operator|(
name|app
operator|.
name|getFinishTime
argument_list|()
operator|==
literal|0
condition|?
literal|"ApplicationMaster"
else|:
literal|"History"
operator|)
expr_stmt|;
if|if
condition|(
operator|!
name|trackingUrlIsNotReady
condition|)
block|{
name|this
operator|.
name|trackingUrl
operator|=
name|WebAppUtils
operator|.
name|getURLWithScheme
argument_list|(
name|HttpConfig
operator|.
name|getSchemePrefix
argument_list|()
argument_list|,
name|trackingUrl
argument_list|)
expr_stmt|;
name|this
operator|.
name|trackingUrlPretty
operator|=
name|this
operator|.
name|trackingUrl
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|trackingUrlPretty
operator|=
literal|"UNASSIGNED"
expr_stmt|;
block|}
name|this
operator|.
name|applicationId
operator|=
name|app
operator|.
name|getApplicationId
argument_list|()
expr_stmt|;
name|this
operator|.
name|applicationType
operator|=
name|app
operator|.
name|getApplicationType
argument_list|()
expr_stmt|;
name|this
operator|.
name|appIdNum
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|app
operator|.
name|getApplicationId
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|app
operator|.
name|getUser
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|app
operator|.
name|getName
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|queue
operator|=
name|app
operator|.
name|getQueue
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|progress
operator|=
name|app
operator|.
name|getProgress
argument_list|()
operator|*
literal|100
expr_stmt|;
name|this
operator|.
name|diagnostics
operator|=
name|app
operator|.
name|getDiagnostics
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|diagnostics
operator|==
literal|null
operator|||
name|diagnostics
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|this
operator|.
name|diagnostics
operator|=
literal|""
expr_stmt|;
block|}
name|this
operator|.
name|finalStatus
operator|=
name|app
operator|.
name|getFinalApplicationStatus
argument_list|()
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|ResourceManager
operator|.
name|getClusterTimeStamp
argument_list|()
expr_stmt|;
if|if
condition|(
name|hasAccess
condition|)
block|{
name|this
operator|.
name|startedTime
operator|=
name|app
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|finishedTime
operator|=
name|app
operator|.
name|getFinishTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|elapsedTime
operator|=
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
expr_stmt|;
name|RMAppAttempt
name|attempt
init|=
name|app
operator|.
name|getCurrentAppAttempt
argument_list|()
decl_stmt|;
if|if
condition|(
name|attempt
operator|!=
literal|null
condition|)
block|{
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
name|amContainerLogsExist
operator|=
literal|true
expr_stmt|;
name|String
name|url
init|=
name|join
argument_list|(
name|HttpConfig
operator|.
name|getSchemePrefix
argument_list|()
argument_list|,
name|masterContainer
operator|.
name|getNodeHttpAddress
argument_list|()
argument_list|,
literal|"/node"
argument_list|,
literal|"/containerlogs/"
argument_list|,
name|ConverterUtils
operator|.
name|toString
argument_list|(
name|masterContainer
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
literal|"/"
argument_list|,
name|app
operator|.
name|getUser
argument_list|()
argument_list|)
decl_stmt|;
name|this
operator|.
name|amContainerLogs
operator|=
name|url
expr_stmt|;
name|this
operator|.
name|amHostHttpAddress
operator|=
name|masterContainer
operator|.
name|getNodeHttpAddress
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
DECL|method|isTrackingUrlReady ()
specifier|public
name|boolean
name|isTrackingUrlReady
parameter_list|()
block|{
return|return
operator|!
name|this
operator|.
name|trackingUrlIsNotReady
return|;
block|}
DECL|method|getApplicationId ()
specifier|public
name|ApplicationId
name|getApplicationId
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationId
return|;
block|}
DECL|method|getAppId ()
specifier|public
name|String
name|getAppId
parameter_list|()
block|{
return|return
name|this
operator|.
name|id
return|;
block|}
DECL|method|getAppIdNum ()
specifier|public
name|String
name|getAppIdNum
parameter_list|()
block|{
return|return
name|this
operator|.
name|appIdNum
return|;
block|}
DECL|method|getUser ()
specifier|public
name|String
name|getUser
parameter_list|()
block|{
return|return
name|this
operator|.
name|user
return|;
block|}
DECL|method|getQueue ()
specifier|public
name|String
name|getQueue
parameter_list|()
block|{
return|return
name|this
operator|.
name|queue
return|;
block|}
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
DECL|method|getState ()
specifier|public
name|String
name|getState
parameter_list|()
block|{
return|return
name|this
operator|.
name|state
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getProgress ()
specifier|public
name|float
name|getProgress
parameter_list|()
block|{
return|return
name|this
operator|.
name|progress
return|;
block|}
DECL|method|getTrackingUI ()
specifier|public
name|String
name|getTrackingUI
parameter_list|()
block|{
return|return
name|this
operator|.
name|trackingUI
return|;
block|}
DECL|method|getNote ()
specifier|public
name|String
name|getNote
parameter_list|()
block|{
return|return
name|this
operator|.
name|diagnostics
return|;
block|}
DECL|method|getFinalStatus ()
specifier|public
name|String
name|getFinalStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|finalStatus
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getTrackingUrl ()
specifier|public
name|String
name|getTrackingUrl
parameter_list|()
block|{
return|return
name|this
operator|.
name|trackingUrl
return|;
block|}
DECL|method|getTrackingUrlPretty ()
specifier|public
name|String
name|getTrackingUrlPretty
parameter_list|()
block|{
return|return
name|this
operator|.
name|trackingUrlPretty
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
name|startedTime
return|;
block|}
DECL|method|getFinishTime ()
specifier|public
name|long
name|getFinishTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|finishedTime
return|;
block|}
DECL|method|getElapsedTime ()
specifier|public
name|long
name|getElapsedTime
parameter_list|()
block|{
return|return
name|this
operator|.
name|elapsedTime
return|;
block|}
DECL|method|getAMContainerLogs ()
specifier|public
name|String
name|getAMContainerLogs
parameter_list|()
block|{
return|return
name|this
operator|.
name|amContainerLogs
return|;
block|}
DECL|method|getAMHostHttpAddress ()
specifier|public
name|String
name|getAMHostHttpAddress
parameter_list|()
block|{
return|return
name|this
operator|.
name|amHostHttpAddress
return|;
block|}
DECL|method|amContainerLogsExist ()
specifier|public
name|boolean
name|amContainerLogsExist
parameter_list|()
block|{
return|return
name|this
operator|.
name|amContainerLogsExist
return|;
block|}
DECL|method|getClusterId ()
specifier|public
name|long
name|getClusterId
parameter_list|()
block|{
return|return
name|this
operator|.
name|clusterId
return|;
block|}
DECL|method|getApplicationType ()
specifier|public
name|String
name|getApplicationType
parameter_list|()
block|{
return|return
name|this
operator|.
name|applicationType
return|;
block|}
block|}
end_class

end_unit

