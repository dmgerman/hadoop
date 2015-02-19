begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline.webapp
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
name|timeline
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
name|util
operator|.
name|ArrayList
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
name|java
operator|.
name|util
operator|.
name|EnumSet
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
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
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|TimelineDomain
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
name|TimelineDomains
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
name|timeline
operator|.
name|TimelineEvents
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
name|timeline
operator|.
name|EntityIdentifier
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
name|timeline
operator|.
name|GenericObjectMapper
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
name|timeline
operator|.
name|NameValuePair
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
name|timeline
operator|.
name|TimelineDataManager
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
name|timeline
operator|.
name|TimelineReader
operator|.
name|Field
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

begin_class
annotation|@
name|Singleton
annotation|@
name|Path
argument_list|(
literal|"/ws/v1/timeline"
argument_list|)
comment|//TODO: support XML serialization/deserialization
DECL|class|TimelineWebServices
specifier|public
class|class
name|TimelineWebServices
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
name|TimelineWebServices
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|timelineDataManager
specifier|private
name|TimelineDataManager
name|timelineDataManager
decl_stmt|;
annotation|@
name|Inject
DECL|method|TimelineWebServices (TimelineDataManager timelineDataManager)
specifier|public
name|TimelineWebServices
parameter_list|(
name|TimelineDataManager
name|timelineDataManager
parameter_list|)
block|{
name|this
operator|.
name|timelineDataManager
operator|=
name|timelineDataManager
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
comment|/**    * Return a list of entities that match the given parameters.    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/{entityType}"
argument_list|)
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
DECL|method|getEntities ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @PathParam(R) String entityType, @QueryParam(R) String primaryFilter, @QueryParam(R) String secondaryFilter, @QueryParam(R) String windowStart, @QueryParam(R) String windowEnd, @QueryParam(R) String fromId, @QueryParam(R) String fromTs, @QueryParam(R) String limit, @QueryParam(R) String fields)
specifier|public
name|TimelineEntities
name|getEntities
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
name|PathParam
argument_list|(
literal|"entityType"
argument_list|)
name|String
name|entityType
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"primaryFilter"
argument_list|)
name|String
name|primaryFilter
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"secondaryFilter"
argument_list|)
name|String
name|secondaryFilter
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"windowStart"
argument_list|)
name|String
name|windowStart
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"windowEnd"
argument_list|)
name|String
name|windowEnd
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"fromId"
argument_list|)
name|String
name|fromId
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"fromTs"
argument_list|)
name|String
name|fromTs
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"limit"
argument_list|)
name|String
name|limit
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"fields"
argument_list|)
name|String
name|fields
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|timelineDataManager
operator|.
name|getEntities
argument_list|(
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|,
name|parsePairStr
argument_list|(
name|primaryFilter
argument_list|,
literal|":"
argument_list|)
argument_list|,
name|parsePairsStr
argument_list|(
name|secondaryFilter
argument_list|,
literal|","
argument_list|,
literal|":"
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|windowStart
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|windowEnd
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|fromId
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|fromTs
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|limit
argument_list|)
argument_list|,
name|parseFieldsStr
argument_list|(
name|fields
argument_list|,
literal|","
argument_list|)
argument_list|,
name|getUser
argument_list|(
name|req
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"windowStart, windowEnd or limit is not a numeric value."
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"requested invalid field."
argument_list|)
throw|;
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
literal|"Error getting entities"
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
comment|/**    * Return a single entity of the given entity type and Id.    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/{entityType}/{entityId}"
argument_list|)
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
DECL|method|getEntity ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @PathParam(R) String entityType, @PathParam(R) String entityId, @QueryParam(R) String fields)
specifier|public
name|TimelineEntity
name|getEntity
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
name|PathParam
argument_list|(
literal|"entityType"
argument_list|)
name|String
name|entityType
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"entityId"
argument_list|)
name|String
name|entityId
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"fields"
argument_list|)
name|String
name|fields
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entity
operator|=
name|timelineDataManager
operator|.
name|getEntity
argument_list|(
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|entityId
argument_list|)
argument_list|,
name|parseFieldsStr
argument_list|(
name|fields
argument_list|,
literal|","
argument_list|)
argument_list|,
name|getUser
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"requested invalid field."
argument_list|)
throw|;
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
literal|"Error getting entity"
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
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Timeline entity "
operator|+
operator|new
name|EntityIdentifier
argument_list|(
name|parseStr
argument_list|(
name|entityId
argument_list|)
argument_list|,
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|)
operator|+
literal|" is not found"
argument_list|)
throw|;
block|}
return|return
name|entity
return|;
block|}
comment|/**    * Return the events that match the given parameters.    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/{entityType}/events"
argument_list|)
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
DECL|method|getEvents ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @PathParam(R) String entityType, @QueryParam(R) String entityId, @QueryParam(R) String eventType, @QueryParam(R) String windowStart, @QueryParam(R) String windowEnd, @QueryParam(R) String limit)
specifier|public
name|TimelineEvents
name|getEvents
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
name|PathParam
argument_list|(
literal|"entityType"
argument_list|)
name|String
name|entityType
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"entityId"
argument_list|)
name|String
name|entityId
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"eventType"
argument_list|)
name|String
name|eventType
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"windowStart"
argument_list|)
name|String
name|windowStart
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"windowEnd"
argument_list|)
name|String
name|windowEnd
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"limit"
argument_list|)
name|String
name|limit
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|timelineDataManager
operator|.
name|getEvents
argument_list|(
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|,
name|parseArrayStr
argument_list|(
name|entityId
argument_list|,
literal|","
argument_list|)
argument_list|,
name|parseArrayStr
argument_list|(
name|eventType
argument_list|,
literal|","
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|windowStart
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|windowEnd
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|limit
argument_list|)
argument_list|,
name|getUser
argument_list|(
name|req
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"windowStart, windowEnd or limit is not a numeric value."
argument_list|)
throw|;
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
literal|"Error getting entity timelines"
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
comment|/**    * Store the given entities into the timeline store, and return the errors    * that happen during storing.    */
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
name|callerUGI
init|=
name|getUser
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|callerUGI
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
try|try
block|{
return|return
name|timelineDataManager
operator|.
name|postEntities
argument_list|(
name|entities
argument_list|,
name|callerUGI
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
comment|/**    * Store the given domain into the timeline store, and return the errors    * that happen during storing.    */
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
literal|"/domain"
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
DECL|method|putDomain ( @ontext HttpServletRequest req, @Context HttpServletResponse res, TimelineDomain domain)
specifier|public
name|TimelinePutResponse
name|putDomain
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
name|TimelineDomain
name|domain
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
name|getUser
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|callerUGI
operator|==
literal|null
condition|)
block|{
name|String
name|msg
init|=
literal|"The owner of the posted timeline domain is not set"
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
name|domain
operator|.
name|setOwner
argument_list|(
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|timelineDataManager
operator|.
name|putDomain
argument_list|(
name|domain
argument_list|,
name|callerUGI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|e
parameter_list|)
block|{
comment|// The user doesn't have the access to override the existing domain.
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ForbiddenException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error putting domain"
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
return|return
operator|new
name|TimelinePutResponse
argument_list|()
return|;
block|}
comment|/**    * Return a single domain of the given domain Id.    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/domain/{domainId}"
argument_list|)
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
DECL|method|getDomain ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @PathParam(R) String domainId)
specifier|public
name|TimelineDomain
name|getDomain
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
name|PathParam
argument_list|(
literal|"domainId"
argument_list|)
name|String
name|domainId
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|domainId
operator|=
name|parseStr
argument_list|(
name|domainId
argument_list|)
expr_stmt|;
if|if
condition|(
name|domainId
operator|==
literal|null
operator|||
name|domainId
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Domain ID is not specified."
argument_list|)
throw|;
block|}
name|TimelineDomain
name|domain
init|=
literal|null
decl_stmt|;
try|try
block|{
name|domain
operator|=
name|timelineDataManager
operator|.
name|getDomain
argument_list|(
name|parseStr
argument_list|(
name|domainId
argument_list|)
argument_list|,
name|getUser
argument_list|(
name|req
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"Error getting domain"
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
if|if
condition|(
name|domain
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"Timeline domain ["
operator|+
name|domainId
operator|+
literal|"] is not found"
argument_list|)
throw|;
block|}
return|return
name|domain
return|;
block|}
comment|/**    * Return a list of domains of the given owner.    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/domain"
argument_list|)
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
DECL|method|getDomains ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @QueryParam(R) String owner)
specifier|public
name|TimelineDomains
name|getDomains
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
literal|"owner"
argument_list|)
name|String
name|owner
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
name|owner
operator|=
name|parseStr
argument_list|(
name|owner
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|callerUGI
init|=
name|getUser
argument_list|(
name|req
argument_list|)
decl_stmt|;
if|if
condition|(
name|owner
operator|==
literal|null
operator|||
name|owner
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|callerUGI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"Domain owner is not specified."
argument_list|)
throw|;
block|}
else|else
block|{
comment|// By default it's going to list the caller's domains
name|owner
operator|=
name|callerUGI
operator|.
name|getShortUserName
argument_list|()
expr_stmt|;
block|}
block|}
try|try
block|{
return|return
name|timelineDataManager
operator|.
name|getDomains
argument_list|(
name|owner
argument_list|,
name|callerUGI
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
literal|"Error getting domains"
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
return|return
name|callerUGI
return|;
block|}
DECL|method|parseArrayStr (String str, String delimiter)
specifier|private
specifier|static
name|SortedSet
argument_list|<
name|String
argument_list|>
name|parseArrayStr
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|delimiter
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|SortedSet
argument_list|<
name|String
argument_list|>
name|strSet
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|String
index|[]
name|strs
init|=
name|str
operator|.
name|split
argument_list|(
name|delimiter
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|aStr
range|:
name|strs
control|)
block|{
name|strSet
operator|.
name|add
argument_list|(
name|aStr
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|strSet
return|;
block|}
DECL|method|parsePairStr (String str, String delimiter)
specifier|private
specifier|static
name|NameValuePair
name|parsePairStr
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|delimiter
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|strs
init|=
name|str
operator|.
name|split
argument_list|(
name|delimiter
argument_list|,
literal|2
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|NameValuePair
argument_list|(
name|strs
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|GenericObjectMapper
operator|.
name|OBJECT_READER
operator|.
name|readValue
argument_list|(
name|strs
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// didn't work as an Object, keep it as a String
return|return
operator|new
name|NameValuePair
argument_list|(
name|strs
index|[
literal|0
index|]
operator|.
name|trim
argument_list|()
argument_list|,
name|strs
index|[
literal|1
index|]
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
block|}
DECL|method|parsePairsStr ( String str, String aDelimiter, String pDelimiter)
specifier|private
specifier|static
name|Collection
argument_list|<
name|NameValuePair
argument_list|>
name|parsePairsStr
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|aDelimiter
parameter_list|,
name|String
name|pDelimiter
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|strs
init|=
name|str
operator|.
name|split
argument_list|(
name|aDelimiter
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|NameValuePair
argument_list|>
name|pairs
init|=
operator|new
name|HashSet
argument_list|<
name|NameValuePair
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|aStr
range|:
name|strs
control|)
block|{
name|pairs
operator|.
name|add
argument_list|(
name|parsePairStr
argument_list|(
name|aStr
argument_list|,
name|pDelimiter
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|pairs
return|;
block|}
DECL|method|parseFieldsStr (String str, String delimiter)
specifier|private
specifier|static
name|EnumSet
argument_list|<
name|Field
argument_list|>
name|parseFieldsStr
parameter_list|(
name|String
name|str
parameter_list|,
name|String
name|delimiter
parameter_list|)
block|{
if|if
condition|(
name|str
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|String
index|[]
name|strs
init|=
name|str
operator|.
name|split
argument_list|(
name|delimiter
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Field
argument_list|>
name|fieldList
init|=
operator|new
name|ArrayList
argument_list|<
name|Field
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|s
range|:
name|strs
control|)
block|{
name|s
operator|=
name|s
operator|.
name|trim
argument_list|()
operator|.
name|toUpperCase
argument_list|(
name|Locale
operator|.
name|ENGLISH
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"EVENTS"
argument_list|)
condition|)
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|EVENTS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"LASTEVENTONLY"
argument_list|)
condition|)
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|LAST_EVENT_ONLY
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"RELATEDENTITIES"
argument_list|)
condition|)
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|RELATED_ENTITIES
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"PRIMARYFILTERS"
argument_list|)
condition|)
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|PRIMARY_FILTERS
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
literal|"OTHERINFO"
argument_list|)
condition|)
block|{
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|OTHER_INFO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Requested nonexistent field "
operator|+
name|s
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|fieldList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Field
name|f1
init|=
name|fieldList
operator|.
name|remove
argument_list|(
name|fieldList
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldList
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|EnumSet
operator|.
name|of
argument_list|(
name|f1
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|EnumSet
operator|.
name|of
argument_list|(
name|f1
argument_list|,
name|fieldList
operator|.
name|toArray
argument_list|(
operator|new
name|Field
index|[
name|fieldList
operator|.
name|size
argument_list|()
index|]
argument_list|)
argument_list|)
return|;
block|}
block|}
DECL|method|parseLongStr (String str)
specifier|private
specifier|static
name|Long
name|parseLongStr
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
operator|==
literal|null
condition|?
literal|null
else|:
name|Long
operator|.
name|parseLong
argument_list|(
name|str
operator|.
name|trim
argument_list|()
argument_list|)
return|;
block|}
DECL|method|parseStr (String str)
specifier|private
specifier|static
name|String
name|parseStr
parameter_list|(
name|String
name|str
parameter_list|)
block|{
return|return
name|str
operator|==
literal|null
condition|?
literal|null
else|:
name|str
operator|.
name|trim
argument_list|()
return|;
block|}
block|}
end_class

end_unit

