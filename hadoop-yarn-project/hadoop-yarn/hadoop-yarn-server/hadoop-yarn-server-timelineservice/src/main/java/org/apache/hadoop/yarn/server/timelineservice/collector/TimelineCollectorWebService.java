begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.collector
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
name|timelineservice
operator|.
name|collector
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletContext
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
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
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
name|QueryParam
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
import|import
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|MediaType
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
name|XmlElement
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Public
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
name|InterfaceStability
operator|.
name|Unstable
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
name|timelineservice
operator|.
name|ApplicationAttemptEntity
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
name|timelineservice
operator|.
name|ApplicationEntity
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
name|timelineservice
operator|.
name|ClusterEntity
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
name|timelineservice
operator|.
name|ContainerEntity
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
name|timelineservice
operator|.
name|FlowRunEntity
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
name|timelineservice
operator|.
name|QueueEntity
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
name|timelineservice
operator|.
name|TimelineEntities
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
name|timelineservice
operator|.
name|TimelineEntity
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
name|timelineservice
operator|.
name|TimelineEntityType
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
name|timelineservice
operator|.
name|UserEntity
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
name|webapp
operator|.
name|ForbiddenException
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
name|com
operator|.
name|google
operator|.
name|inject
operator|.
name|Singleton
import|;
end_import

begin_comment
comment|/**  * The main per-node REST end point for timeline service writes. It is  * essentially a container service that routes requests to the appropriate  * per-app services.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
annotation|@
name|Singleton
annotation|@
name|Path
argument_list|(
literal|"/ws/v2/timeline"
argument_list|)
DECL|class|TimelineCollectorWebService
specifier|public
class|class
name|TimelineCollectorWebService
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
name|TimelineCollectorWebService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
annotation|@
name|Context
name|ServletContext
name|context
decl_stmt|;
comment|/**    * Gives information about timeline collector.    */
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"about"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|NONE
argument_list|)
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|AboutInfo
specifier|public
specifier|static
class|class
name|AboutInfo
block|{
DECL|field|about
specifier|private
name|String
name|about
decl_stmt|;
DECL|method|AboutInfo ()
specifier|public
name|AboutInfo
parameter_list|()
block|{      }
DECL|method|AboutInfo (String abt)
specifier|public
name|AboutInfo
parameter_list|(
name|String
name|abt
parameter_list|)
block|{
name|this
operator|.
name|about
operator|=
name|abt
expr_stmt|;
block|}
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"About"
argument_list|)
DECL|method|getAbout ()
specifier|public
name|String
name|getAbout
parameter_list|()
block|{
return|return
name|about
return|;
block|}
DECL|method|setAbout (String abt)
specifier|public
name|void
name|setAbout
parameter_list|(
name|String
name|abt
parameter_list|)
block|{
name|this
operator|.
name|about
operator|=
name|abt
expr_stmt|;
block|}
block|}
comment|/**    * Return the description of the timeline web services.    *    * @param req Servlet request.    * @param res Servlet response.    * @return description of timeline web service.    */
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
comment|/* , MediaType.APPLICATION_XML */
block|}
argument_list|)
DECL|method|about ( @ontext HttpServletRequest req, @Context HttpServletResponse res)
specifier|public
name|AboutInfo
name|about
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|req
parameter_list|,
annotation|@
name|Context
name|HttpServletResponse
name|res
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
return|return
operator|new
name|AboutInfo
argument_list|(
literal|"Timeline Collector API"
argument_list|)
return|;
block|}
comment|/**    * Accepts writes to the collector, and returns a response. It simply routes    * the request to the app level collector. It expects an application as a    * context.    *    * @param req Servlet request.    * @param res Servlet response.    * @param async flag indicating whether its an async put or not. "true"    *     indicates, its an async call. If null, its considered false.    * @param appId Application Id to which the entities to be put belong to. If    *     appId is not there or it cannot be parsed, HTTP 400 will be sent back.    * @param entities timeline entities to be put.    * @return a Response with appropriate HTTP status.    */
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
literal|"/entities"
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
comment|/* , MediaType.APPLICATION_XML */
block|}
argument_list|)
DECL|method|putEntities ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @QueryParam(R) String async, @QueryParam(R) String appId, TimelineEntities entities)
specifier|public
name|Response
name|putEntities
parameter_list|(
annotation|@
name|Context
name|HttpServletRequest
name|req
parameter_list|,
annotation|@
name|Context
name|HttpServletResponse
name|res
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"async"
argument_list|)
name|String
name|async
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"appid"
argument_list|)
name|String
name|appId
parameter_list|,
name|TimelineEntities
name|entities
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|callerUgi
init|=
name|getUser
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|callerUgi
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"The owner of the posted timeline entities is not set"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ForbiddenException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
comment|// TODO how to express async posts and handle them
name|boolean
name|isAsync
init|=
name|async
operator|!=
literal|null
operator|&&
name|async
operator|.
name|trim
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
decl_stmt|;
try|try
block|{
name|ApplicationId
name|appID
init|=
name|parseApplicationId
argument_list|(
name|appId
argument_list|)
decl_stmt|;
if|if
condition|(
name|appID
operator|==
literal|null
condition|)
block|{
return|return
name|Response
operator|.
name|status
argument_list|(
name|Response
operator|.
name|Status
operator|.
name|BAD_REQUEST
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
name|NodeTimelineCollectorManager
name|collectorManager
init|=
operator|(
name|NodeTimelineCollectorManager
operator|)
name|context
operator|.
name|getAttribute
argument_list|(
name|NodeTimelineCollectorManager
operator|.
name|COLLECTOR_MANAGER_ATTR_KEY
argument_list|)
decl_stmt|;
name|TimelineCollector
name|collector
init|=
name|collectorManager
operator|.
name|get
argument_list|(
name|appID
argument_list|)
decl_stmt|;
if|if
condition|(
name|collector
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Application: "
operator|+
name|appId
operator|+
literal|" is not found"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotFoundException
argument_list|()
throw|;
comment|// different exception?
block|}
name|collector
operator|.
name|putEntities
argument_list|(
name|processTimelineEntities
argument_list|(
name|entities
argument_list|)
argument_list|,
name|callerUgi
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error putting entities"
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|WebApplicationException
argument_list|(
name|e
argument_list|,
name|Response
operator|.
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|)
throw|;
block|}
block|}
DECL|method|parseApplicationId (String appId)
specifier|private
specifier|static
name|ApplicationId
name|parseApplicationId
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|appId
operator|!=
literal|null
condition|)
block|{
return|return
name|ConverterUtils
operator|.
name|toApplicationId
argument_list|(
name|appId
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Invalid application ID: "
operator|+
name|appId
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
DECL|method|init (HttpServletResponse response)
specifier|private
specifier|static
name|void
name|init
parameter_list|(
name|HttpServletResponse
name|response
parameter_list|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|getUser (HttpServletRequest req)
specifier|private
specifier|static
name|UserGroupInformation
name|getUser
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|remoteUser
init|=
name|req
operator|.
name|getRemoteUser
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|callerUgi
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
name|callerUgi
operator|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|remoteUser
argument_list|)
expr_stmt|;
block|}
return|return
name|callerUgi
return|;
block|}
comment|// The process may not be necessary according to the way we write the backend,
comment|// but let's keep it for now in case we need to use sub-classes APIs in the
comment|// future (e.g., aggregation).
DECL|method|processTimelineEntities ( TimelineEntities entities)
specifier|private
specifier|static
name|TimelineEntities
name|processTimelineEntities
parameter_list|(
name|TimelineEntities
name|entities
parameter_list|)
block|{
name|TimelineEntities
name|entitiesToReturn
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineEntity
name|entity
range|:
name|entities
operator|.
name|getEntities
argument_list|()
control|)
block|{
name|TimelineEntityType
name|type
init|=
literal|null
decl_stmt|;
try|try
block|{
name|type
operator|=
name|TimelineEntityType
operator|.
name|valueOf
argument_list|(
name|entity
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|type
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|type
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|YARN_CLUSTER
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|ClusterEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|YARN_FLOW_RUN
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|FlowRunEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|YARN_APPLICATION
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|ApplicationEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|YARN_APPLICATION_ATTEMPT
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|ApplicationAttemptEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|YARN_CONTAINER
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|ContainerEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|YARN_QUEUE
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|QueueEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|YARN_USER
case|:
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
operator|new
name|UserEntity
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
break|break;
default|default:
break|break;
block|}
block|}
else|else
block|{
name|entitiesToReturn
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|entitiesToReturn
return|;
block|}
block|}
end_class

end_unit

