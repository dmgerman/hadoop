begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.aggregator
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
name|aggregator
package|;
end_package

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
name|timeline
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
name|timeline
operator|.
name|TimelinePutResponse
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
name|Inject
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
DECL|class|PerNodeAggregatorWebService
specifier|public
class|class
name|PerNodeAggregatorWebService
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
name|PerNodeAggregatorWebService
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|serviceManager
specifier|private
specifier|final
name|AppLevelServiceManager
name|serviceManager
decl_stmt|;
annotation|@
name|Inject
DECL|method|PerNodeAggregatorWebService (AppLevelServiceManager serviceManager)
specifier|public
name|PerNodeAggregatorWebService
parameter_list|(
name|AppLevelServiceManager
name|serviceManager
parameter_list|)
block|{
name|this
operator|.
name|serviceManager
operator|=
name|serviceManager
expr_stmt|;
block|}
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
DECL|method|AboutInfo (String about)
specifier|public
name|AboutInfo
parameter_list|(
name|String
name|about
parameter_list|)
block|{
name|this
operator|.
name|about
operator|=
name|about
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
DECL|method|setAbout (String about)
specifier|public
name|void
name|setAbout
parameter_list|(
name|String
name|about
parameter_list|)
block|{
name|this
operator|.
name|about
operator|=
name|about
expr_stmt|;
block|}
block|}
comment|/**    * Return the description of the timeline web services.    */
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
literal|"Timeline API"
argument_list|)
return|;
block|}
comment|/**    * Accepts writes to the aggregator, and returns a response. It simply routes    * the request to the app level aggregator. It expects an application as a    * context.    */
annotation|@
name|POST
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
DECL|method|postEntities ( @ontext HttpServletRequest req, @Context HttpServletResponse res, TimelineEntities entities)
specifier|public
name|TimelinePutResponse
name|postEntities
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
try|try
block|{
name|AppLevelAggregatorService
name|service
init|=
name|getAggregatorService
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Application not found"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|NotFoundException
argument_list|()
throw|;
comment|// different exception?
block|}
return|return
name|service
operator|.
name|postEntities
argument_list|(
name|entities
argument_list|,
name|callerUgi
argument_list|)
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
specifier|private
name|AppLevelAggregatorService
DECL|method|getAggregatorService (HttpServletRequest req)
name|getAggregatorService
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
name|String
name|appIdString
init|=
name|getApplicationId
argument_list|(
name|req
argument_list|)
decl_stmt|;
return|return
name|serviceManager
operator|.
name|getService
argument_list|(
name|appIdString
argument_list|)
return|;
block|}
DECL|method|getApplicationId (HttpServletRequest req)
specifier|private
name|String
name|getApplicationId
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|)
block|{
comment|// TODO the application id from the request
comment|// (most likely from the URI)
return|return
literal|null
return|;
block|}
DECL|method|init (HttpServletResponse response)
specifier|private
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
block|}
end_class

end_unit

