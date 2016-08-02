begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
package|;
end_package

begin_comment
comment|/**  * Paths in the REST App  */
end_comment

begin_class
DECL|class|RestPaths
specifier|public
class|class
name|RestPaths
block|{
DECL|field|WS_CONTEXT
specifier|public
specifier|static
specifier|final
name|String
name|WS_CONTEXT
init|=
literal|"ws"
decl_stmt|;
DECL|field|AGENT_WS_CONTEXT
specifier|public
specifier|static
specifier|final
name|String
name|AGENT_WS_CONTEXT
init|=
literal|"ws"
decl_stmt|;
comment|/**    * Root path for the web services context: {@value}    */
DECL|field|WS_CONTEXT_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|WS_CONTEXT_ROOT
init|=
literal|"/"
operator|+
name|WS_CONTEXT
decl_stmt|;
comment|/**    * agent content root: {@value}    */
DECL|field|WS_AGENT_CONTEXT_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|WS_AGENT_CONTEXT_ROOT
init|=
literal|"/"
operator|+
name|AGENT_WS_CONTEXT
decl_stmt|;
DECL|field|V1_SLIDER
specifier|public
specifier|static
specifier|final
name|String
name|V1_SLIDER
init|=
literal|"/v1/slider"
decl_stmt|;
DECL|field|SLIDER_CONTEXT_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_CONTEXT_ROOT
init|=
name|WS_CONTEXT_ROOT
operator|+
name|V1_SLIDER
decl_stmt|;
DECL|field|RELATIVE_API
specifier|public
specifier|static
specifier|final
name|String
name|RELATIVE_API
init|=
name|WS_CONTEXT
operator|+
name|V1_SLIDER
decl_stmt|;
DECL|field|SLIDER_AGENT_CONTEXT_ROOT
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_AGENT_CONTEXT_ROOT
init|=
name|WS_AGENT_CONTEXT_ROOT
operator|+
name|V1_SLIDER
decl_stmt|;
DECL|field|MANAGEMENT
specifier|public
specifier|static
specifier|final
name|String
name|MANAGEMENT
init|=
literal|"mgmt"
decl_stmt|;
DECL|field|SLIDER_SUBPATH_MANAGEMENT
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_SUBPATH_MANAGEMENT
init|=
literal|"/"
operator|+
name|MANAGEMENT
decl_stmt|;
DECL|field|SLIDER_SUBPATH_AGENTS
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_SUBPATH_AGENTS
init|=
literal|"/agents"
decl_stmt|;
DECL|field|SLIDER_SUBPATH_PUBLISHER
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_SUBPATH_PUBLISHER
init|=
literal|"/publisher"
decl_stmt|;
comment|/**    * management path: {@value}    */
DECL|field|SLIDER_PATH_MANAGEMENT
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_PATH_MANAGEMENT
init|=
name|SLIDER_CONTEXT_ROOT
operator|+
name|SLIDER_SUBPATH_MANAGEMENT
decl_stmt|;
DECL|field|RELATIVE_PATH_MANAGEMENT
specifier|public
specifier|static
specifier|final
name|String
name|RELATIVE_PATH_MANAGEMENT
init|=
name|RELATIVE_API
operator|+
name|SLIDER_SUBPATH_MANAGEMENT
decl_stmt|;
comment|/**    * Agents: {@value}    */
DECL|field|SLIDER_PATH_AGENTS
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_PATH_AGENTS
init|=
name|SLIDER_AGENT_CONTEXT_ROOT
operator|+
name|SLIDER_SUBPATH_AGENTS
decl_stmt|;
comment|/**    * Publisher: {@value}    */
DECL|field|SLIDER_PATH_PUBLISHER
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_PATH_PUBLISHER
init|=
name|SLIDER_CONTEXT_ROOT
operator|+
name|SLIDER_SUBPATH_PUBLISHER
decl_stmt|;
DECL|field|RELATIVE_PATH_PUBLISHER
specifier|public
specifier|static
specifier|final
name|String
name|RELATIVE_PATH_PUBLISHER
init|=
name|RELATIVE_API
operator|+
name|SLIDER_SUBPATH_PUBLISHER
decl_stmt|;
comment|/**    * Registry subpath: {@value}     */
DECL|field|SLIDER_SUBPATH_REGISTRY
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_SUBPATH_REGISTRY
init|=
literal|"/registry"
decl_stmt|;
comment|/**    * Registry: {@value}    */
DECL|field|SLIDER_PATH_REGISTRY
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_PATH_REGISTRY
init|=
name|SLIDER_CONTEXT_ROOT
operator|+
name|SLIDER_SUBPATH_REGISTRY
decl_stmt|;
DECL|field|RELATIVE_PATH_REGISTRY
specifier|public
specifier|static
specifier|final
name|String
name|RELATIVE_PATH_REGISTRY
init|=
name|RELATIVE_API
operator|+
name|SLIDER_SUBPATH_REGISTRY
decl_stmt|;
comment|/**    * The regular expressions used to define valid configuration names/url path    * fragments: {@value}    */
DECL|field|PUBLISHED_CONFIGURATION_REGEXP
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISHED_CONFIGURATION_REGEXP
init|=
literal|"[a-z0-9][a-z0-9_\\+-]*"
decl_stmt|;
DECL|field|PUBLISHED_CONFIGURATION_SET_REGEXP
specifier|public
specifier|static
specifier|final
name|String
name|PUBLISHED_CONFIGURATION_SET_REGEXP
init|=
literal|"[a-z0-9][a-z0-9_.\\+-]*"
decl_stmt|;
DECL|field|SLIDER_CONFIGSET
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_CONFIGSET
init|=
literal|"slider"
decl_stmt|;
DECL|field|SLIDER_EXPORTS
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_EXPORTS
init|=
literal|"exports"
decl_stmt|;
DECL|field|SLIDER_CLASSPATH
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_CLASSPATH
init|=
literal|"classpath"
decl_stmt|;
comment|/**    * Codahale Metrics - base path: {@value}    */
DECL|field|SYSTEM
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM
init|=
literal|"/system"
decl_stmt|;
comment|/**    * Codahale Metrics - health: {@value}    */
DECL|field|SYSTEM_HEALTHCHECK
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM_HEALTHCHECK
init|=
name|SYSTEM
operator|+
literal|"/health"
decl_stmt|;
comment|/**    * Codahale Metrics - metrics: {@value}    */
DECL|field|SYSTEM_METRICS
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM_METRICS
init|=
name|SYSTEM
operator|+
literal|"/metrics"
decl_stmt|;
comment|/**    * Codahale Metrics - metrics as JSON: {@value}    */
DECL|field|SYSTEM_METRICS_JSON
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM_METRICS_JSON
init|=
name|SYSTEM_METRICS
operator|+
literal|"?format=json"
decl_stmt|;
comment|/**    * Codahale Metrics - ping: {@value}    */
DECL|field|SYSTEM_PING
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM_PING
init|=
name|SYSTEM
operator|+
literal|"/ping"
decl_stmt|;
comment|/**    * Codahale Metrics - thread dump: {@value}    */
DECL|field|SYSTEM_THREADS
specifier|public
specifier|static
specifier|final
name|String
name|SYSTEM_THREADS
init|=
name|SYSTEM
operator|+
literal|"/threads"
decl_stmt|;
comment|/**    * application subpath    */
DECL|field|SLIDER_SUBPATH_APPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_SUBPATH_APPLICATION
init|=
literal|"/application"
decl_stmt|;
comment|/**    * management path: {@value}    */
DECL|field|SLIDER_PATH_APPLICATION
specifier|public
specifier|static
specifier|final
name|String
name|SLIDER_PATH_APPLICATION
init|=
name|SLIDER_CONTEXT_ROOT
operator|+
name|SLIDER_SUBPATH_APPLICATION
decl_stmt|;
DECL|field|APPLICATION_WADL
specifier|public
specifier|static
specifier|final
name|String
name|APPLICATION_WADL
init|=
literal|"/application.wadl"
decl_stmt|;
DECL|field|LIVE
specifier|public
specifier|static
specifier|final
name|String
name|LIVE
init|=
literal|"/live"
decl_stmt|;
DECL|field|LIVE_RESOURCES
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_RESOURCES
init|=
literal|"/live/resources"
decl_stmt|;
DECL|field|LIVE_CONTAINERS
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_CONTAINERS
init|=
literal|"/live/containers"
decl_stmt|;
DECL|field|LIVE_COMPONENTS
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_COMPONENTS
init|=
literal|"/live/components"
decl_stmt|;
DECL|field|LIVE_NODES
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_NODES
init|=
literal|"/live/nodes"
decl_stmt|;
DECL|field|LIVE_LIVENESS
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_LIVENESS
init|=
literal|"/live/liveness"
decl_stmt|;
DECL|field|LIVE_STATISTICS
specifier|public
specifier|static
specifier|final
name|String
name|LIVE_STATISTICS
init|=
literal|"/live/statistics"
decl_stmt|;
DECL|field|MODEL
specifier|public
specifier|static
specifier|final
name|String
name|MODEL
init|=
literal|"/model"
decl_stmt|;
DECL|field|MODEL_DESIRED
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_DESIRED
init|=
name|MODEL
operator|+
literal|"/desired"
decl_stmt|;
DECL|field|MODEL_DESIRED_APPCONF
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_DESIRED_APPCONF
init|=
name|MODEL_DESIRED
operator|+
literal|"/appconf"
decl_stmt|;
DECL|field|MODEL_DESIRED_RESOURCES
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_DESIRED_RESOURCES
init|=
name|MODEL_DESIRED
operator|+
literal|"/resources"
decl_stmt|;
DECL|field|MODEL_RESOLVED
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_RESOLVED
init|=
literal|"/model/resolved"
decl_stmt|;
DECL|field|MODEL_RESOLVED_APPCONF
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_RESOLVED_APPCONF
init|=
name|MODEL_RESOLVED
operator|+
literal|"/appconf"
decl_stmt|;
DECL|field|MODEL_RESOLVED_RESOURCES
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_RESOLVED_RESOURCES
init|=
name|MODEL_RESOLVED
operator|+
literal|"/resources"
decl_stmt|;
DECL|field|MODEL_INTERNAL
specifier|public
specifier|static
specifier|final
name|String
name|MODEL_INTERNAL
init|=
literal|"/model/internal"
decl_stmt|;
DECL|field|ACTION
specifier|public
specifier|static
specifier|final
name|String
name|ACTION
init|=
literal|"/action"
decl_stmt|;
DECL|field|ACTION_PING
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_PING
init|=
name|ACTION
operator|+
literal|"/ping"
decl_stmt|;
DECL|field|ACTION_STOP
specifier|public
specifier|static
specifier|final
name|String
name|ACTION_STOP
init|=
name|ACTION
operator|+
literal|"/stop"
decl_stmt|;
comment|/**    * Path to a role    * @param name role name    * @return a path to it    */
DECL|method|pathToRole (String name)
specifier|public
name|String
name|pathToRole
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// ws/v1/slider/application/live/components/$name
return|return
name|SLIDER_PATH_APPLICATION
operator|+
name|LIVE_COMPONENTS
operator|+
literal|"/"
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

