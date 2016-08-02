begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.application
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
operator|.
name|application
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
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
name|YarnRuntimeException
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
name|BadRequestException
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
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|ApplicationLivenessInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|ComponentInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|ContainerInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|NodeInformation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|NodeInformationList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|AggregateConf
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|conf
operator|.
name|ConfTree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|exceptions
operator|.
name|NoSuchNodeException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|core
operator|.
name|persist
operator|.
name|ConfTreeSerDeser
import|;
end_import

begin_import
import|import
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
name|actions
operator|.
name|ActionFlexCluster
import|;
end_import

begin_import
import|import
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
name|actions
operator|.
name|AsyncAction
import|;
end_import

begin_import
import|import
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
name|actions
operator|.
name|QueueAccess
import|;
end_import

begin_import
import|import
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
name|state
operator|.
name|RoleInstance
import|;
end_import

begin_import
import|import
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
name|state
operator|.
name|StateAccessForProviders
import|;
end_import

begin_import
import|import
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
name|WebAppApi
import|;
end_import

begin_import
import|import
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
operator|.
name|AbstractSliderResource
import|;
end_import

begin_import
import|import static
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
operator|.
name|RestPaths
operator|.
name|*
import|;
end_import

begin_import
import|import
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
operator|.
name|application
operator|.
name|actions
operator|.
name|RestActionStop
import|;
end_import

begin_import
import|import
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
operator|.
name|application
operator|.
name|actions
operator|.
name|StopResponse
import|;
end_import

begin_import
import|import
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
operator|.
name|application
operator|.
name|resources
operator|.
name|ContentCache
import|;
end_import

begin_import
import|import
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
operator|.
name|application
operator|.
name|actions
operator|.
name|RestActionPing
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|api
operator|.
name|types
operator|.
name|PingInformation
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

begin_import
import|import
name|javax
operator|.
name|inject
operator|.
name|Singleton
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
name|Consumes
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
name|DELETE
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
name|GET
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
name|HEAD
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
name|POST
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
name|PUT
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
name|Path
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
name|PathParam
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
name|Produces
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
name|WebApplicationException
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
name|Context
import|;
end_import

begin_import
import|import static
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
operator|.
name|*
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
name|UriInfo
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
name|Map
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
name|TimeUnit
import|;
end_import

begin_class
annotation|@
name|Singleton
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|class|ApplicationResource
specifier|public
class|class
name|ApplicationResource
extends|extends
name|AbstractSliderResource
block|{
DECL|field|log
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ApplicationResource
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LIVE_ENTRIES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|LIVE_ENTRIES
init|=
name|toJsonList
argument_list|(
literal|"resources"
argument_list|,
literal|"containers"
argument_list|,
literal|"components"
argument_list|,
literal|"nodes"
argument_list|,
literal|"statistics"
argument_list|,
literal|"internal"
argument_list|)
decl_stmt|;
DECL|field|ROOT_ENTRIES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|ROOT_ENTRIES
init|=
name|toJsonList
argument_list|(
literal|"model"
argument_list|,
literal|"live"
argument_list|,
literal|"actions"
argument_list|)
decl_stmt|;
DECL|field|MODEL_ENTRIES
specifier|public
specifier|static
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|MODEL_ENTRIES
init|=
name|toJsonList
argument_list|(
literal|"desired"
argument_list|,
literal|"resolved"
argument_list|)
decl_stmt|;
comment|/**    * This is the cache of all content ... each entry is    * designed to be self-refreshing on get operations,     * so is never very out of date, yet many GETs don't    * overload the rest of the system.    */
DECL|field|cache
specifier|private
specifier|final
name|ContentCache
name|cache
decl_stmt|;
DECL|field|state
specifier|private
specifier|final
name|StateAccessForProviders
name|state
decl_stmt|;
DECL|field|actionQueues
specifier|private
specifier|final
name|QueueAccess
name|actionQueues
decl_stmt|;
DECL|method|ApplicationResource (WebAppApi slider)
specifier|public
name|ApplicationResource
parameter_list|(
name|WebAppApi
name|slider
parameter_list|)
block|{
name|super
argument_list|(
name|slider
argument_list|)
expr_stmt|;
name|state
operator|=
name|slider
operator|.
name|getAppState
argument_list|()
expr_stmt|;
name|cache
operator|=
name|slider
operator|.
name|getContentCache
argument_list|()
expr_stmt|;
name|actionQueues
operator|=
name|slider
operator|.
name|getQueues
argument_list|()
expr_stmt|;
block|}
comment|/**    * Build a new JSON-marshallable list of string elements    * @param elements elements    * @return something that can be returned    */
DECL|method|toJsonList (String... elements)
specifier|private
specifier|static
name|List
argument_list|<
name|String
argument_list|>
name|toJsonList
parameter_list|(
name|String
modifier|...
name|elements
parameter_list|)
block|{
return|return
name|Lists
operator|.
name|newArrayList
argument_list|(
name|elements
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getRoot ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getRoot
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|)
expr_stmt|;
return|return
name|ROOT_ENTRIES
return|;
block|}
comment|/**    * Enum model values: desired and resolved    * @return the desired and resolved model    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModel ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getModel
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL
argument_list|)
expr_stmt|;
return|return
name|MODEL_ENTRIES
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL_DESIRED
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModelDesired ()
specifier|public
name|AggregateConf
name|getModelDesired
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_DESIRED
argument_list|)
expr_stmt|;
return|return
name|lookupAggregateConf
argument_list|(
name|MODEL_DESIRED
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL_DESIRED_APPCONF
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModelDesiredAppconf ()
specifier|public
name|ConfTree
name|getModelDesiredAppconf
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_DESIRED_APPCONF
argument_list|)
expr_stmt|;
return|return
name|lookupConfTree
argument_list|(
name|MODEL_DESIRED_APPCONF
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL_DESIRED_RESOURCES
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModelDesiredResources ()
specifier|public
name|ConfTree
name|getModelDesiredResources
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_DESIRED_RESOURCES
argument_list|)
expr_stmt|;
return|return
name|lookupConfTree
argument_list|(
name|MODEL_DESIRED_RESOURCES
argument_list|)
return|;
block|}
comment|/*   @PUT   @Path(MODEL_DESIRED_RESOURCES) //  @Consumes({APPLICATION_JSON, TEXT_PLAIN})   @Consumes({TEXT_PLAIN})   @Produces({APPLICATION_JSON}) */
DECL|method|setModelDesiredResources ( String json)
specifier|public
name|ConfTree
name|setModelDesiredResources
parameter_list|(
name|String
name|json
parameter_list|)
block|{
name|markPut
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_DESIRED_RESOURCES
argument_list|)
expr_stmt|;
name|int
name|size
init|=
name|json
operator|!=
literal|null
condition|?
name|json
operator|.
name|length
argument_list|()
else|:
literal|0
decl_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"PUT {} {} bytes:\n{}"
argument_list|,
name|MODEL_DESIRED_RESOURCES
argument_list|,
name|size
argument_list|,
name|json
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"No JSON in PUT request; rejecting"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"No JSON in PUT"
argument_list|)
throw|;
block|}
try|try
block|{
name|ConfTreeSerDeser
name|serDeser
init|=
operator|new
name|ConfTreeSerDeser
argument_list|()
decl_stmt|;
name|ConfTree
name|updated
init|=
name|serDeser
operator|.
name|fromJson
argument_list|(
name|json
argument_list|)
decl_stmt|;
name|queue
argument_list|(
operator|new
name|ActionFlexCluster
argument_list|(
literal|"flex"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|updated
argument_list|)
argument_list|)
expr_stmt|;
comment|// return the updated value, even though it potentially hasn't yet
comment|// been executed
return|return
name|updated
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
literal|"PUT to "
operator|+
name|MODEL_DESIRED_RESOURCES
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
name|MODEL_DESIRED_RESOURCES
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|setModelDesiredResources ( ConfTree updated)
specifier|public
name|ConfTree
name|setModelDesiredResources
parameter_list|(
name|ConfTree
name|updated
parameter_list|)
block|{
try|try
block|{
name|queue
argument_list|(
operator|new
name|ActionFlexCluster
argument_list|(
literal|"flex"
argument_list|,
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|,
name|updated
argument_list|)
argument_list|)
expr_stmt|;
comment|// return the updated value, even though it potentially hasn't yet
comment|// been executed
return|return
name|updated
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
literal|"PUT to "
operator|+
name|MODEL_DESIRED_RESOURCES
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL_RESOLVED
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModelResolved ()
specifier|public
name|AggregateConf
name|getModelResolved
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_RESOLVED
argument_list|)
expr_stmt|;
return|return
name|lookupAggregateConf
argument_list|(
name|MODEL_RESOLVED
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL_RESOLVED_APPCONF
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModelResolvedAppconf ()
specifier|public
name|ConfTree
name|getModelResolvedAppconf
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_RESOLVED_APPCONF
argument_list|)
expr_stmt|;
return|return
name|lookupConfTree
argument_list|(
name|MODEL_RESOLVED_APPCONF
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|MODEL_RESOLVED_RESOURCES
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getModelResolvedResources ()
specifier|public
name|ConfTree
name|getModelResolvedResources
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|MODEL_RESOLVED_RESOURCES
argument_list|)
expr_stmt|;
return|return
name|lookupConfTree
argument_list|(
name|MODEL_RESOLVED_RESOURCES
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLive ()
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|getLive
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE
argument_list|)
expr_stmt|;
return|return
name|LIVE_ENTRIES
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_RESOURCES
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveResources ()
specifier|public
name|ConfTree
name|getLiveResources
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_RESOURCES
argument_list|)
expr_stmt|;
return|return
name|lookupConfTree
argument_list|(
name|LIVE_RESOURCES
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_CONTAINERS
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveContainers ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ContainerInformation
argument_list|>
name|getLiveContainers
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_CONTAINERS
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|ContainerInformation
argument_list|>
operator|)
name|cache
operator|.
name|lookup
argument_list|(
name|LIVE_CONTAINERS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_CONTAINERS
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_CONTAINERS
operator|+
literal|"/{containerId}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveContainer ( @athParamR) String containerId)
specifier|public
name|ContainerInformation
name|getLiveContainer
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"containerId"
argument_list|)
name|String
name|containerId
parameter_list|)
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_CONTAINERS
argument_list|)
expr_stmt|;
try|try
block|{
name|RoleInstance
name|id
init|=
name|state
operator|.
name|getLiveInstanceByContainerID
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
return|return
name|id
operator|.
name|serialize
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|NoSuchNodeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Unknown container: "
operator|+
name|containerId
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_CONTAINERS
operator|+
literal|"/"
operator|+
name|containerId
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_COMPONENTS
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveComponents ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
name|getLiveComponents
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_COMPONENTS
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|ComponentInformation
argument_list|>
operator|)
name|cache
operator|.
name|lookup
argument_list|(
name|LIVE_COMPONENTS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_COMPONENTS
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_COMPONENTS
operator|+
literal|"/{component}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveComponent ( @athParamR) String component)
specifier|public
name|ComponentInformation
name|getLiveComponent
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"component"
argument_list|)
name|String
name|component
parameter_list|)
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_COMPONENTS
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|state
operator|.
name|getComponentInformation
argument_list|(
name|component
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Unknown component: "
operator|+
name|component
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_CONTAINERS
operator|+
literal|"/"
operator|+
name|component
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Liveness information for the application as a whole    * @return snapshot of liveness    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_LIVENESS
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLivenessInformation ()
specifier|public
name|ApplicationLivenessInformation
name|getLivenessInformation
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_LIVENESS
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|state
operator|.
name|getApplicationLivenessInformation
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_CONTAINERS
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* TODO: decide what structure to return here, then implement    @GET   @Path(LIVE_LIVENESS + "/{component}")   @Produces({APPLICATION_JSON})   public ApplicationLivenessInformation getLivenessForComponent(       @PathParam("component") String component) {     markGet(SLIDER_SUBPATH_APPLICATION, LIVE_COMPONENTS);     try {       RoleStatus roleStatus = state.lookupRoleStatus(component);       ApplicationLivenessInformation info = new ApplicationLivenessInformation();       info.requested = roleStatus.getRequested();       info.allRequestsSatisfied = info.requested == 0;       return info;     } catch (YarnRuntimeException e) {       throw new NotFoundException("Unknown component: " + component);     } catch (Exception e) {       throw buildException(LIVE_LIVENESS + "/" + component, e);     }   } */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_NODES
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveNodes ()
specifier|public
name|NodeInformationList
name|getLiveNodes
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_COMPONENTS
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|NodeInformationList
operator|)
name|cache
operator|.
name|lookup
argument_list|(
name|LIVE_NODES
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_COMPONENTS
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_NODES
operator|+
literal|"/{hostname}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveNode (@athParamR) String hostname)
specifier|public
name|NodeInformation
name|getLiveNode
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"hostname"
argument_list|)
name|String
name|hostname
parameter_list|)
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_COMPONENTS
argument_list|)
expr_stmt|;
try|try
block|{
name|NodeInformation
name|ni
init|=
name|state
operator|.
name|getNodeInformation
argument_list|(
name|hostname
argument_list|)
decl_stmt|;
if|if
condition|(
name|ni
operator|!=
literal|null
condition|)
block|{
return|return
name|ni
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Unknown node: "
operator|+
name|hostname
argument_list|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_COMPONENTS
operator|+
literal|"/"
operator|+
name|hostname
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Statistics of the application    * @return snapshot statistics    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|LIVE_STATISTICS
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getLiveStatistics ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getLiveStatistics
parameter_list|()
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|LIVE_LIVENESS
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
operator|)
name|cache
operator|.
name|lookup
argument_list|(
name|LIVE_STATISTICS
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|LIVE_STATISTICS
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper method; look up an aggregate configuration in the cache from    * a key, or raise an exception    * @param key key to resolve    * @return the configuration    * @throws WebApplicationException on a failure    */
DECL|method|lookupAggregateConf (String key)
specifier|protected
name|AggregateConf
name|lookupAggregateConf
parameter_list|(
name|String
name|key
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|AggregateConf
operator|)
name|cache
operator|.
name|lookup
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Helper method; look up an conf tree in the cache from    * a key, or raise an exception    * @param key key to resolve    * @return the configuration    * @throws WebApplicationException on a failure    */
DECL|method|lookupConfTree (String key)
specifier|protected
name|ConfTree
name|lookupConfTree
parameter_list|(
name|String
name|key
parameter_list|)
block|{
try|try
block|{
return|return
operator|(
name|ConfTree
operator|)
name|cache
operator|.
name|lookup
argument_list|(
name|key
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|buildException
argument_list|(
name|key
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/* ************************************************************************      ACTION PING      **************************************************************************/
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|ACTION_PING
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|actionPingGet (@ontext HttpServletRequest request, @Context UriInfo uriInfo)
specifier|public
name|PingInformation
name|actionPingGet
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|uriInfo
parameter_list|)
block|{
name|markGet
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|ACTION_PING
argument_list|)
expr_stmt|;
return|return
operator|new
name|RestActionPing
argument_list|()
operator|.
name|ping
argument_list|(
name|request
argument_list|,
name|uriInfo
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
name|ACTION_PING
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|actionPingPost (@ontext HttpServletRequest request, @Context UriInfo uriInfo, String body)
specifier|public
name|PingInformation
name|actionPingPost
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|uriInfo
parameter_list|,
name|String
name|body
parameter_list|)
block|{
name|markPost
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|ACTION_PING
argument_list|)
expr_stmt|;
return|return
operator|new
name|RestActionPing
argument_list|()
operator|.
name|ping
argument_list|(
name|request
argument_list|,
name|uriInfo
argument_list|,
name|body
argument_list|)
return|;
block|}
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
name|ACTION_PING
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|TEXT_PLAIN
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|actionPingPut (@ontext HttpServletRequest request, @Context UriInfo uriInfo, String body)
specifier|public
name|PingInformation
name|actionPingPut
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|uriInfo
parameter_list|,
name|String
name|body
parameter_list|)
block|{
name|markPut
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|ACTION_PING
argument_list|)
expr_stmt|;
return|return
operator|new
name|RestActionPing
argument_list|()
operator|.
name|ping
argument_list|(
name|request
argument_list|,
name|uriInfo
argument_list|,
name|body
argument_list|)
return|;
block|}
annotation|@
name|DELETE
annotation|@
name|Path
argument_list|(
name|ACTION_PING
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|actionPingDelete (@ontext HttpServletRequest request, @Context UriInfo uriInfo)
specifier|public
name|PingInformation
name|actionPingDelete
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|uriInfo
parameter_list|)
block|{
name|markDelete
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|ACTION_PING
argument_list|)
expr_stmt|;
return|return
operator|new
name|RestActionPing
argument_list|()
operator|.
name|ping
argument_list|(
name|request
argument_list|,
name|uriInfo
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|HEAD
annotation|@
name|Path
argument_list|(
name|ACTION_PING
argument_list|)
DECL|method|actionPingHead (@ontext HttpServletRequest request, @Context UriInfo uriInfo)
specifier|public
name|Object
name|actionPingHead
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|uriInfo
parameter_list|)
block|{
name|mark
argument_list|(
literal|"HEAD"
argument_list|,
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|ACTION_PING
argument_list|)
expr_stmt|;
return|return
operator|new
name|RestActionPing
argument_list|()
operator|.
name|ping
argument_list|(
name|request
argument_list|,
name|uriInfo
argument_list|,
literal|""
argument_list|)
return|;
block|}
comment|/* ************************************************************************      ACTION STOP      **************************************************************************/
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
name|ACTION_STOP
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|actionStop (@ontext HttpServletRequest request, @Context UriInfo uriInfo, String body)
specifier|public
name|StopResponse
name|actionStop
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|request
parameter_list|,
annotation|@
name|Context
name|UriInfo
name|uriInfo
parameter_list|,
name|String
name|body
parameter_list|)
block|{
name|markPost
argument_list|(
name|SLIDER_SUBPATH_APPLICATION
argument_list|,
name|ACTION_STOP
argument_list|)
expr_stmt|;
return|return
operator|new
name|RestActionStop
argument_list|(
name|slider
argument_list|)
operator|.
name|stop
argument_list|(
name|request
argument_list|,
name|uriInfo
argument_list|,
name|body
argument_list|)
return|;
block|}
comment|/**    * Schedule an action    * @param action for delayed execution    */
DECL|method|schedule (AsyncAction action)
specifier|public
name|void
name|schedule
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
block|{
name|actionQueues
operator|.
name|schedule
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
comment|/**    * Put an action on the immediate queue -to be executed when the queue    * reaches it.    * @param action action to queue    */
DECL|method|queue (AsyncAction action)
specifier|public
name|void
name|queue
parameter_list|(
name|AsyncAction
name|action
parameter_list|)
block|{
name|actionQueues
operator|.
name|put
argument_list|(
name|action
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

