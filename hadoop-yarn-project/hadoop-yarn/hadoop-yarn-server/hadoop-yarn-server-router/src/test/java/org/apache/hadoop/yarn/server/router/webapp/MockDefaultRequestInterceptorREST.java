begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
operator|.
name|webapp
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
name|ConnectException
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|HttpHeaders
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|Status
import|;
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
name|AuthorizationException
import|;
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
name|exceptions
operator|.
name|ApplicationNotFoundException
import|;
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
import|;
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|AppState
import|;
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
name|ApplicationSubmissionContextInfo
import|;
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
name|AppsInfo
import|;
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
name|ClusterMetricsInfo
import|;
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
name|NewApplication
import|;
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
name|NodeInfo
import|;
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
name|NodesInfo
import|;
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
name|ResourceInfo
import|;
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
name|ResourceOptionInfo
import|;
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
name|NotFoundException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This class mocks the RESTRequestInterceptor.  */
end_comment

begin_class
DECL|class|MockDefaultRequestInterceptorREST
specifier|public
class|class
name|MockDefaultRequestInterceptorREST
extends|extends
name|DefaultRequestInterceptorREST
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|MockDefaultRequestInterceptorREST
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|applicationCounter
specifier|final
specifier|private
name|AtomicInteger
name|applicationCounter
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// True if the Mock RM is running, false otherwise.
comment|// This property allows us to write tests for specific scenario as YARN RM
comment|// down e.g. network issue, failover.
DECL|field|isRunning
specifier|private
name|boolean
name|isRunning
init|=
literal|true
decl_stmt|;
DECL|field|applicationMap
specifier|private
name|HashSet
argument_list|<
name|ApplicationId
argument_list|>
name|applicationMap
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|APP_STATE_RUNNING
specifier|public
specifier|static
specifier|final
name|String
name|APP_STATE_RUNNING
init|=
literal|"RUNNING"
decl_stmt|;
DECL|method|validateRunning ()
specifier|private
name|void
name|validateRunning
parameter_list|()
throws|throws
name|ConnectException
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|ConnectException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|createNewApplication (HttpServletRequest hsr)
specifier|public
name|Response
name|createNewApplication
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|validateRunning
argument_list|()
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|applicationCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
decl_stmt|;
name|NewApplication
name|appId
init|=
operator|new
name|NewApplication
argument_list|(
name|applicationId
operator|.
name|toString
argument_list|()
argument_list|,
operator|new
name|ResourceInfo
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|entity
argument_list|(
name|appId
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|submitApplication (ApplicationSubmissionContextInfo newApp, HttpServletRequest hsr)
specifier|public
name|Response
name|submitApplication
parameter_list|(
name|ApplicationSubmissionContextInfo
name|newApp
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|IOException
throws|,
name|InterruptedException
block|{
name|validateRunning
argument_list|()
expr_stmt|;
name|ApplicationId
name|appId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|newApp
operator|.
name|getApplicationId
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Application submitted: "
operator|+
name|appId
argument_list|)
expr_stmt|;
name|applicationMap
operator|.
name|add
argument_list|(
name|appId
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|ACCEPTED
argument_list|)
operator|.
name|header
argument_list|(
name|HttpHeaders
operator|.
name|LOCATION
argument_list|,
literal|""
argument_list|)
operator|.
name|entity
argument_list|(
name|getSubClusterId
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getApp (HttpServletRequest hsr, String appId, Set<String> unselectedFields)
specifier|public
name|AppInfo
name|getApp
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|unselectedFields
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|applicationMap
operator|.
name|contains
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"app with id: "
operator|+
name|appId
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
return|return
operator|new
name|AppInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getApps (HttpServletRequest hsr, String stateQuery, Set<String> statesQuery, String finalStatusQuery, String userQuery, String queueQuery, String count, String startedBegin, String startedEnd, String finishBegin, String finishEnd, Set<String> applicationTypes, Set<String> applicationTags, Set<String> unselectedFields)
specifier|public
name|AppsInfo
name|getApps
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|stateQuery
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|statesQuery
parameter_list|,
name|String
name|finalStatusQuery
parameter_list|,
name|String
name|userQuery
parameter_list|,
name|String
name|queueQuery
parameter_list|,
name|String
name|count
parameter_list|,
name|String
name|startedBegin
parameter_list|,
name|String
name|startedEnd
parameter_list|,
name|String
name|finishBegin
parameter_list|,
name|String
name|finishEnd
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|applicationTags
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|unselectedFields
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|AppsInfo
name|appsInfo
init|=
operator|new
name|AppsInfo
argument_list|()
decl_stmt|;
name|AppInfo
name|appInfo
init|=
operator|new
name|AppInfo
argument_list|()
decl_stmt|;
name|appInfo
operator|.
name|setAppId
argument_list|(
name|ApplicationId
operator|.
name|newInstance
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|,
name|applicationCounter
operator|.
name|incrementAndGet
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|appInfo
operator|.
name|setAMHostHttpAddress
argument_list|(
literal|"http://i_am_the_AM:1234"
argument_list|)
expr_stmt|;
name|appsInfo
operator|.
name|add
argument_list|(
name|appInfo
argument_list|)
expr_stmt|;
return|return
name|appsInfo
return|;
block|}
annotation|@
name|Override
DECL|method|updateAppState (AppState targetState, HttpServletRequest hsr, String appId)
specifier|public
name|Response
name|updateAppState
parameter_list|(
name|AppState
name|targetState
parameter_list|,
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
throws|,
name|YarnException
throws|,
name|InterruptedException
throws|,
name|IOException
block|{
name|validateRunning
argument_list|()
expr_stmt|;
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|applicationMap
operator|.
name|remove
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|ApplicationNotFoundException
argument_list|(
literal|"Trying to kill an absent application: "
operator|+
name|appId
argument_list|)
throw|;
block|}
if|if
condition|(
name|targetState
operator|==
literal|null
condition|)
block|{
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|BAD_REQUEST
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Force killing application: "
operator|+
name|appId
argument_list|)
expr_stmt|;
name|AppState
name|ret
init|=
operator|new
name|AppState
argument_list|()
decl_stmt|;
name|ret
operator|.
name|setState
argument_list|(
name|targetState
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|OK
argument_list|)
operator|.
name|entity
argument_list|(
name|ret
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNode (String nodeId)
specifier|public
name|NodeInfo
name|getNode
parameter_list|(
name|String
name|nodeId
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|NodeInfo
name|node
init|=
operator|new
name|NodeInfo
argument_list|()
decl_stmt|;
name|node
operator|.
name|setId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|node
operator|.
name|setLastHealthUpdate
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|node
return|;
block|}
annotation|@
name|Override
DECL|method|getNodes (String states)
specifier|public
name|NodesInfo
name|getNodes
parameter_list|(
name|String
name|states
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|NodeInfo
name|node
init|=
operator|new
name|NodeInfo
argument_list|()
decl_stmt|;
name|node
operator|.
name|setId
argument_list|(
literal|"Node "
operator|+
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|node
operator|.
name|setLastHealthUpdate
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|NodesInfo
name|nodes
init|=
operator|new
name|NodesInfo
argument_list|()
decl_stmt|;
name|nodes
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
return|return
name|nodes
return|;
block|}
annotation|@
name|Override
DECL|method|updateNodeResource (HttpServletRequest hsr, String nodeId, ResourceOptionInfo resourceOption)
specifier|public
name|ResourceInfo
name|updateNodeResource
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|nodeId
parameter_list|,
name|ResourceOptionInfo
name|resourceOption
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|Resource
name|resource
init|=
name|resourceOption
operator|.
name|getResourceOption
argument_list|()
operator|.
name|getResource
argument_list|()
decl_stmt|;
return|return
operator|new
name|ResourceInfo
argument_list|(
name|resource
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getClusterMetricsInfo ()
specifier|public
name|ClusterMetricsInfo
name|getClusterMetricsInfo
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|ClusterMetricsInfo
name|metrics
init|=
operator|new
name|ClusterMetricsInfo
argument_list|()
decl_stmt|;
name|metrics
operator|.
name|setAppsSubmitted
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAppsCompleted
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAppsPending
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAppsRunning
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAppsFailed
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|metrics
operator|.
name|setAppsKilled
argument_list|(
name|Integer
operator|.
name|valueOf
argument_list|(
name|getSubClusterId
argument_list|()
operator|.
name|getId
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|metrics
return|;
block|}
annotation|@
name|Override
DECL|method|getAppState (HttpServletRequest hsr, String appId)
specifier|public
name|AppState
name|getAppState
parameter_list|(
name|HttpServletRequest
name|hsr
parameter_list|,
name|String
name|appId
parameter_list|)
throws|throws
name|AuthorizationException
block|{
if|if
condition|(
operator|!
name|isRunning
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"RM is stopped"
argument_list|)
throw|;
block|}
name|ApplicationId
name|applicationId
init|=
name|ApplicationId
operator|.
name|fromString
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|applicationMap
operator|.
name|contains
argument_list|(
name|applicationId
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"app with id: "
operator|+
name|appId
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
return|return
operator|new
name|AppState
argument_list|(
name|APP_STATE_RUNNING
argument_list|)
return|;
block|}
DECL|method|setSubClusterId (int subClusterId)
specifier|public
name|void
name|setSubClusterId
parameter_list|(
name|int
name|subClusterId
parameter_list|)
block|{
name|setSubClusterId
argument_list|(
name|SubClusterId
operator|.
name|newInstance
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|subClusterId
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|isRunning
return|;
block|}
DECL|method|setRunning (boolean runningMode)
specifier|public
name|void
name|setRunning
parameter_list|(
name|boolean
name|runningMode
parameter_list|)
block|{
name|this
operator|.
name|isRunning
operator|=
name|runningMode
expr_stmt|;
block|}
block|}
end_class

end_unit

