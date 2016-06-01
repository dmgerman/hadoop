begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.webapp
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
name|nodemanager
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|io
operator|.
name|OutputStream
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
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Response
operator|.
name|ResponseBuilder
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
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|StreamingOutput
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
name|factories
operator|.
name|RecordFactory
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
name|factory
operator|.
name|providers
operator|.
name|RecordFactoryProvider
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
name|nodemanager
operator|.
name|Context
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
name|nodemanager
operator|.
name|ResourceView
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|Application
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|application
operator|.
name|ApplicationState
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
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
name|nodemanager
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
name|nodemanager
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
name|nodemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ContainerInfo
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
name|nodemanager
operator|.
name|webapp
operator|.
name|dao
operator|.
name|ContainersInfo
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
name|nodemanager
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
name|hadoop
operator|.
name|yarn
operator|.
name|webapp
operator|.
name|WebApp
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
literal|"/ws/v1/node"
argument_list|)
DECL|class|NMWebServices
specifier|public
class|class
name|NMWebServices
block|{
DECL|field|nmContext
specifier|private
name|Context
name|nmContext
decl_stmt|;
DECL|field|rview
specifier|private
name|ResourceView
name|rview
decl_stmt|;
DECL|field|webapp
specifier|private
name|WebApp
name|webapp
decl_stmt|;
DECL|field|recordFactory
specifier|private
specifier|static
name|RecordFactory
name|recordFactory
init|=
name|RecordFactoryProvider
operator|.
name|getRecordFactory
argument_list|(
literal|null
argument_list|)
decl_stmt|;
specifier|private
annotation|@
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
DECL|field|request
name|HttpServletRequest
name|request
decl_stmt|;
specifier|private
annotation|@
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
DECL|field|response
name|HttpServletResponse
name|response
decl_stmt|;
annotation|@
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
DECL|field|uriInfo
name|UriInfo
name|uriInfo
decl_stmt|;
annotation|@
name|Inject
DECL|method|NMWebServices (final Context nm, final ResourceView view, final WebApp webapp)
specifier|public
name|NMWebServices
parameter_list|(
specifier|final
name|Context
name|nm
parameter_list|,
specifier|final
name|ResourceView
name|view
parameter_list|,
specifier|final
name|WebApp
name|webapp
parameter_list|)
block|{
name|this
operator|.
name|nmContext
operator|=
name|nm
expr_stmt|;
name|this
operator|.
name|rview
operator|=
name|view
expr_stmt|;
name|this
operator|.
name|webapp
operator|=
name|webapp
expr_stmt|;
block|}
DECL|method|init ()
specifier|private
name|void
name|init
parameter_list|()
block|{
comment|//clear content type
name|response
operator|.
name|setContentType
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|GET
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|,
name|MediaType
operator|.
name|APPLICATION_XML
block|}
argument_list|)
DECL|method|get ()
specifier|public
name|NodeInfo
name|get
parameter_list|()
block|{
return|return
name|getNodeInfo
argument_list|()
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/info"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|,
name|MediaType
operator|.
name|APPLICATION_XML
block|}
argument_list|)
DECL|method|getNodeInfo ()
specifier|public
name|NodeInfo
name|getNodeInfo
parameter_list|()
block|{
name|init
argument_list|()
expr_stmt|;
return|return
operator|new
name|NodeInfo
argument_list|(
name|this
operator|.
name|nmContext
argument_list|,
name|this
operator|.
name|rview
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/apps"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|,
name|MediaType
operator|.
name|APPLICATION_XML
block|}
argument_list|)
DECL|method|getNodeApps (@ueryParamR) String stateQuery, @QueryParam(R) String userQuery)
specifier|public
name|AppsInfo
name|getNodeApps
parameter_list|(
annotation|@
name|QueryParam
argument_list|(
literal|"state"
argument_list|)
name|String
name|stateQuery
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"user"
argument_list|)
name|String
name|userQuery
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|AppsInfo
name|allApps
init|=
operator|new
name|AppsInfo
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ApplicationId
argument_list|,
name|Application
argument_list|>
name|entry
range|:
name|this
operator|.
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|AppInfo
name|appInfo
init|=
operator|new
name|AppInfo
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|stateQuery
operator|!=
literal|null
operator|&&
operator|!
name|stateQuery
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|ApplicationState
operator|.
name|valueOf
argument_list|(
name|stateQuery
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|appInfo
operator|.
name|getState
argument_list|()
operator|.
name|equalsIgnoreCase
argument_list|(
name|stateQuery
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
if|if
condition|(
name|userQuery
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|userQuery
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|String
name|msg
init|=
literal|"Error: You must specify a non-empty string for the user"
decl_stmt|;
throw|throw
operator|new
name|BadRequestException
argument_list|(
name|msg
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|appInfo
operator|.
name|getUser
argument_list|()
operator|.
name|equals
argument_list|(
name|userQuery
argument_list|)
condition|)
block|{
continue|continue;
block|}
block|}
name|allApps
operator|.
name|add
argument_list|(
name|appInfo
argument_list|)
expr_stmt|;
block|}
return|return
name|allApps
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/apps/{appid}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|,
name|MediaType
operator|.
name|APPLICATION_XML
block|}
argument_list|)
DECL|method|getNodeApp (@athParamR) String appId)
specifier|public
name|AppInfo
name|getNodeApp
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"appid"
argument_list|)
name|String
name|appId
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|ApplicationId
name|id
init|=
name|WebAppUtils
operator|.
name|parseApplicationId
argument_list|(
name|recordFactory
argument_list|,
name|appId
argument_list|)
decl_stmt|;
name|Application
name|app
init|=
name|this
operator|.
name|nmContext
operator|.
name|getApplications
argument_list|()
operator|.
name|get
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|app
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"app with id "
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
argument_list|(
name|app
argument_list|)
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/containers"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|,
name|MediaType
operator|.
name|APPLICATION_XML
block|}
argument_list|)
DECL|method|getNodeContainers (@avax.ws.rs.core.Context HttpServletRequest hsr)
specifier|public
name|ContainersInfo
name|getNodeContainers
parameter_list|(
annotation|@
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
name|HttpServletRequest
name|hsr
parameter_list|)
block|{
name|init
argument_list|()
expr_stmt|;
name|ContainersInfo
name|allContainers
init|=
operator|new
name|ContainersInfo
argument_list|()
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|Container
argument_list|>
name|entry
range|:
name|this
operator|.
name|nmContext
operator|.
name|getContainers
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// just skip it
continue|continue;
block|}
name|ContainerInfo
name|info
init|=
operator|new
name|ContainerInfo
argument_list|(
name|this
operator|.
name|nmContext
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|,
name|uriInfo
operator|.
name|getBaseUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|webapp
operator|.
name|name
argument_list|()
argument_list|,
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
decl_stmt|;
name|allContainers
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|allContainers
return|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/containers/{containerid}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|,
name|MediaType
operator|.
name|APPLICATION_XML
block|}
argument_list|)
DECL|method|getNodeContainer (@avax.ws.rs.core.Context HttpServletRequest hsr, @PathParam(R) String id)
specifier|public
name|ContainerInfo
name|getNodeContainer
parameter_list|(
annotation|@
name|javax
operator|.
name|ws
operator|.
name|rs
operator|.
name|core
operator|.
name|Context
name|HttpServletRequest
name|hsr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"containerid"
argument_list|)
name|String
name|id
parameter_list|)
block|{
name|ContainerId
name|containerId
init|=
literal|null
decl_stmt|;
name|init
argument_list|()
expr_stmt|;
try|try
block|{
name|containerId
operator|=
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BadRequestException
argument_list|(
literal|"invalid container id, "
operator|+
name|id
argument_list|)
throw|;
block|}
name|Container
name|container
init|=
name|nmContext
operator|.
name|getContainers
argument_list|()
operator|.
name|get
argument_list|(
name|containerId
argument_list|)
decl_stmt|;
if|if
condition|(
name|container
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|NotFoundException
argument_list|(
literal|"container with id, "
operator|+
name|id
operator|+
literal|", not found"
argument_list|)
throw|;
block|}
return|return
operator|new
name|ContainerInfo
argument_list|(
name|this
operator|.
name|nmContext
argument_list|,
name|container
argument_list|,
name|uriInfo
operator|.
name|getBaseUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|webapp
operator|.
name|name
argument_list|()
argument_list|,
name|hsr
operator|.
name|getRemoteUser
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Returns the contents of a container's log file in plain text.     *    * Only works for containers that are still in the NodeManager's memory, so    * logs are no longer available after the corresponding application is no    * longer running.    *     * @param containerIdStr    *    The container ID    * @param filename    *    The name of the log file    * @return    *    The contents of the container's log file    */
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
literal|"/containerlogs/{containerid}/{filename}"
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|TEXT_PLAIN
block|}
argument_list|)
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getLogs (@athParamR) String containerIdStr, @PathParam(R) String filename, @QueryParam(R) String download, @QueryParam(R) String size)
specifier|public
name|Response
name|getLogs
parameter_list|(
annotation|@
name|PathParam
argument_list|(
literal|"containerid"
argument_list|)
name|String
name|containerIdStr
parameter_list|,
annotation|@
name|PathParam
argument_list|(
literal|"filename"
argument_list|)
name|String
name|filename
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"download"
argument_list|)
name|String
name|download
parameter_list|,
annotation|@
name|QueryParam
argument_list|(
literal|"size"
argument_list|)
name|String
name|size
parameter_list|)
block|{
name|ContainerId
name|containerId
decl_stmt|;
try|try
block|{
name|containerId
operator|=
name|ConverterUtils
operator|.
name|toContainerId
argument_list|(
name|containerIdStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|ex
parameter_list|)
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
name|File
name|logFile
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logFile
operator|=
name|ContainerLogsUtils
operator|.
name|getContainerLogFile
argument_list|(
name|containerId
argument_list|,
name|filename
argument_list|,
name|request
operator|.
name|getRemoteUser
argument_list|()
argument_list|,
name|nmContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|ex
parameter_list|)
block|{
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|NOT_FOUND
argument_list|)
operator|.
name|entity
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
name|ex
parameter_list|)
block|{
return|return
name|Response
operator|.
name|serverError
argument_list|()
operator|.
name|entity
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
name|boolean
name|downloadFile
init|=
name|parseBooleanParam
argument_list|(
name|download
argument_list|)
decl_stmt|;
specifier|final
name|long
name|bytes
init|=
name|parseLongParam
argument_list|(
name|size
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|FileInputStream
name|fis
init|=
name|ContainerLogsUtils
operator|.
name|openLogFileForRead
argument_list|(
name|containerIdStr
argument_list|,
name|logFile
argument_list|,
name|nmContext
argument_list|)
decl_stmt|;
specifier|final
name|long
name|fileLength
init|=
name|logFile
operator|.
name|length
argument_list|()
decl_stmt|;
name|StreamingOutput
name|stream
init|=
operator|new
name|StreamingOutput
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
throws|,
name|WebApplicationException
block|{
name|int
name|bufferSize
init|=
literal|65536
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
name|bufferSize
index|]
decl_stmt|;
name|long
name|toSkip
init|=
literal|0
decl_stmt|;
name|long
name|totalBytesToRead
init|=
name|fileLength
decl_stmt|;
if|if
condition|(
name|bytes
operator|<
literal|0
condition|)
block|{
name|long
name|absBytes
init|=
name|Math
operator|.
name|abs
argument_list|(
name|bytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|absBytes
operator|<
name|fileLength
condition|)
block|{
name|toSkip
operator|=
name|fileLength
operator|-
name|absBytes
expr_stmt|;
name|totalBytesToRead
operator|=
name|absBytes
expr_stmt|;
block|}
name|long
name|skippedBytes
init|=
name|fis
operator|.
name|skip
argument_list|(
name|toSkip
argument_list|)
decl_stmt|;
if|if
condition|(
name|skippedBytes
operator|!=
name|toSkip
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"The bytes were skipped are different "
operator|+
literal|"from the caller requested"
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|bytes
operator|<
name|fileLength
condition|)
block|{
name|totalBytesToRead
operator|=
name|bytes
expr_stmt|;
block|}
block|}
name|long
name|curRead
init|=
literal|0
decl_stmt|;
name|long
name|pendingRead
init|=
name|totalBytesToRead
operator|-
name|curRead
decl_stmt|;
name|int
name|toRead
init|=
name|pendingRead
operator|>
name|buf
operator|.
name|length
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|pendingRead
decl_stmt|;
name|int
name|len
init|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
decl_stmt|;
while|while
condition|(
name|len
operator|!=
operator|-
literal|1
operator|&&
name|curRead
operator|<
name|totalBytesToRead
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|curRead
operator|+=
name|len
expr_stmt|;
name|pendingRead
operator|=
name|totalBytesToRead
operator|-
name|curRead
expr_stmt|;
name|toRead
operator|=
name|pendingRead
operator|>
name|buf
operator|.
name|length
condition|?
name|buf
operator|.
name|length
else|:
operator|(
name|int
operator|)
name|pendingRead
expr_stmt|;
name|len
operator|=
name|fis
operator|.
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|toRead
argument_list|)
expr_stmt|;
block|}
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|ResponseBuilder
name|resp
init|=
name|Response
operator|.
name|ok
argument_list|(
name|stream
argument_list|)
decl_stmt|;
if|if
condition|(
name|downloadFile
condition|)
block|{
name|resp
operator|.
name|header
argument_list|(
literal|"Content-Type"
argument_list|,
literal|"application/octet-stream"
argument_list|)
expr_stmt|;
block|}
return|return
name|resp
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
return|return
name|Response
operator|.
name|serverError
argument_list|()
operator|.
name|entity
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
DECL|method|parseBooleanParam (String param)
specifier|private
name|boolean
name|parseBooleanParam
parameter_list|(
name|String
name|param
parameter_list|)
block|{
if|if
condition|(
name|param
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
literal|"true"
operator|)
operator|.
name|equalsIgnoreCase
argument_list|(
name|param
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|parseLongParam (String bytes)
specifier|private
name|long
name|parseLongParam
parameter_list|(
name|String
name|bytes
parameter_list|)
block|{
if|if
condition|(
name|bytes
operator|==
literal|null
operator|||
name|bytes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Long
operator|.
name|MAX_VALUE
return|;
block|}
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_class

end_unit

