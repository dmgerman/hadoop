begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.applicationhistoryservice.webapp
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
operator|.
name|webapp
package|;
end_package

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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|apptimeline
operator|.
name|ATSEntities
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
name|apptimeline
operator|.
name|ATSEntity
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
name|apptimeline
operator|.
name|ATSEvents
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
name|apptimeline
operator|.
name|ATSPutErrors
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
name|apptimeline
operator|.
name|ApplicationTimelineReader
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
name|server
operator|.
name|applicationhistoryservice
operator|.
name|apptimeline
operator|.
name|ApplicationTimelineStore
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
name|apptimeline
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
name|webapp
operator|.
name|BadRequestException
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
literal|"/ws/v1/apptimeline"
argument_list|)
comment|//TODO: support XML serialization/deserialization
DECL|class|ATSWebServices
specifier|public
class|class
name|ATSWebServices
block|{
DECL|field|store
specifier|private
name|ApplicationTimelineStore
name|store
decl_stmt|;
annotation|@
name|Inject
DECL|method|ATSWebServices (ApplicationTimelineStore store)
specifier|public
name|ATSWebServices
parameter_list|(
name|ApplicationTimelineStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
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
comment|/**    * Return the description of the application timeline web services.    */
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
literal|"Application Timeline API"
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
DECL|method|getEntities ( @ontext HttpServletRequest req, @Context HttpServletResponse res, @PathParam(R) String entityType, @QueryParam(R) String primaryFilter, @QueryParam(R) String secondaryFilter, @QueryParam(R) String windowStart, @QueryParam(R) String windowEnd, @QueryParam(R) String limit, @QueryParam(R) String fields)
specifier|public
name|ATSEntities
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
name|ATSEntities
name|entities
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entities
operator|=
name|store
operator|.
name|getEntities
argument_list|(
name|parseStr
argument_list|(
name|entityType
argument_list|)
argument_list|,
name|parseLongStr
argument_list|(
name|limit
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
name|parseFieldsStr
argument_list|(
name|fields
argument_list|,
literal|","
argument_list|)
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|entities
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ATSEntities
argument_list|()
return|;
block|}
return|return
name|entities
return|;
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
name|ATSEntity
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
name|ATSEntity
name|entity
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entity
operator|=
name|store
operator|.
name|getEntity
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
argument_list|,
name|parseFieldsStr
argument_list|(
name|fields
argument_list|,
literal|","
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
if|if
condition|(
name|entity
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|WebApplicationException
argument_list|(
name|Response
operator|.
name|Status
operator|.
name|NOT_FOUND
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
name|ATSEvents
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
name|ATSEvents
name|events
init|=
literal|null
decl_stmt|;
try|try
block|{
name|events
operator|=
name|store
operator|.
name|getEntityTimelines
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
name|parseLongStr
argument_list|(
name|limit
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
name|parseArrayStr
argument_list|(
name|eventType
argument_list|,
literal|","
argument_list|)
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|events
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ATSEvents
argument_list|()
return|;
block|}
return|return
name|events
return|;
block|}
comment|/**    * Store the given entities into the timeline store, and return the errors    * that happen during storing.    */
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
literal|"/"
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
DECL|method|postEntities ( @ontext HttpServletRequest req, @Context HttpServletResponse res, ATSEntities entities)
specifier|public
name|ATSPutErrors
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
name|ATSEntities
name|entities
parameter_list|)
block|{
name|init
argument_list|(
name|res
argument_list|)
expr_stmt|;
if|if
condition|(
name|entities
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|ATSPutErrors
argument_list|()
return|;
block|}
return|return
name|store
operator|.
name|put
argument_list|(
name|entities
argument_list|)
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
name|fieldList
operator|.
name|add
argument_list|(
name|Field
operator|.
name|valueOf
argument_list|(
name|s
operator|.
name|toUpperCase
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
return|return
literal|null
return|;
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
return|return
name|EnumSet
operator|.
name|of
argument_list|(
name|f1
argument_list|)
return|;
else|else
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

