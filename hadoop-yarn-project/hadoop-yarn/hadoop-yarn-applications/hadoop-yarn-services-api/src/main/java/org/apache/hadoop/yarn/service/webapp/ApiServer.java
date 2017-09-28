begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.webapp
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|webapp
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
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
name|util
operator|.
name|VersionInfo
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
name|conf
operator|.
name|YarnConfiguration
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Component
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|Service
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ServiceState
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
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ServiceStatus
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
name|service
operator|.
name|client
operator|.
name|ServiceClient
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
name|service
operator|.
name|utils
operator|.
name|ServiceApiUtil
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
name|Status
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
name|Collections
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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ServiceState
operator|.
name|ACCEPTED
import|;
end_import

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
name|service
operator|.
name|conf
operator|.
name|RestApiConstants
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * The rest API endpoints for users to manage services on YARN.  */
end_comment

begin_class
annotation|@
name|Singleton
annotation|@
name|Path
argument_list|(
name|CONTEXT_ROOT
argument_list|)
DECL|class|ApiServer
specifier|public
class|class
name|ApiServer
block|{
annotation|@
name|Inject
DECL|method|ApiServer (Configuration conf)
specifier|public
name|ApiServer
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
block|}
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
name|ApiServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|YARN_CONFIG
specifier|private
specifier|static
name|Configuration
name|YARN_CONFIG
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|SERVICE_CLIENT
specifier|private
specifier|static
name|ServiceClient
name|SERVICE_CLIENT
decl_stmt|;
static|static
block|{
name|init
argument_list|()
expr_stmt|;
block|}
comment|// initialize all the common resources - order is important
DECL|method|init ()
specifier|private
specifier|static
name|void
name|init
parameter_list|()
block|{
name|SERVICE_CLIENT
operator|=
operator|new
name|ServiceClient
argument_list|()
expr_stmt|;
name|SERVICE_CLIENT
operator|.
name|init
argument_list|(
name|YARN_CONFIG
argument_list|)
expr_stmt|;
name|SERVICE_CLIENT
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|VERSION
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getVersion ()
specifier|public
name|Response
name|getVersion
parameter_list|()
block|{
name|String
name|version
init|=
name|VersionInfo
operator|.
name|getBuildVersion
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|version
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
literal|"{ \"hadoop_version\": \""
operator|+
name|version
operator|+
literal|"\"}"
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|POST
annotation|@
name|Path
argument_list|(
name|SERVICE_ROOT_PATH
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|createService (Service service)
specifier|public
name|Response
name|createService
parameter_list|(
name|Service
name|service
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"POST: createService = {}"
argument_list|,
name|service
argument_list|)
expr_stmt|;
name|ServiceStatus
name|serviceStatus
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
try|try
block|{
name|ApplicationId
name|applicationId
init|=
name|SERVICE_CLIENT
operator|.
name|actionCreate
argument_list|(
name|service
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully created service "
operator|+
name|service
operator|.
name|getName
argument_list|()
operator|+
literal|" applicationId = "
operator|+
name|applicationId
argument_list|)
expr_stmt|;
name|serviceStatus
operator|.
name|setState
argument_list|(
name|ACCEPTED
argument_list|)
expr_stmt|;
name|serviceStatus
operator|.
name|setUri
argument_list|(
name|CONTEXT_ROOT
operator|+
name|SERVICE_ROOT_PATH
operator|+
literal|"/"
operator|+
name|service
operator|.
name|getName
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
name|ACCEPTED
argument_list|)
operator|.
name|entity
argument_list|(
name|serviceStatus
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|serviceStatus
operator|.
name|setDiagnostics
argument_list|(
name|e
operator|.
name|getMessage
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
name|BAD_REQUEST
argument_list|)
operator|.
name|entity
argument_list|(
name|serviceStatus
argument_list|)
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
name|String
name|message
init|=
literal|"Failed to create service "
operator|+
name|service
operator|.
name|getName
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|serviceStatus
operator|.
name|setDiagnostics
argument_list|(
name|message
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
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
name|INTERNAL_SERVER_ERROR
argument_list|)
operator|.
name|entity
argument_list|(
name|serviceStatus
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|GET
annotation|@
name|Path
argument_list|(
name|SERVICE_PATH
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|getService (@athParamSERVICE_NAME) String appName)
specifier|public
name|Response
name|getService
parameter_list|(
annotation|@
name|PathParam
argument_list|(
name|SERVICE_NAME
argument_list|)
name|String
name|appName
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"GET: getService for appName = {}"
argument_list|,
name|appName
argument_list|)
expr_stmt|;
name|ServiceStatus
name|serviceStatus
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
try|try
block|{
name|Service
name|app
init|=
name|SERVICE_CLIENT
operator|.
name|getStatus
argument_list|(
name|appName
argument_list|)
decl_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|app
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|serviceStatus
operator|.
name|setDiagnostics
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|serviceStatus
operator|.
name|setCode
argument_list|(
name|ERROR_CODE_APP_NAME_INVALID
argument_list|)
expr_stmt|;
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
name|serviceStatus
argument_list|)
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
literal|"Get service failed"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|serviceStatus
operator|.
name|setDiagnostics
argument_list|(
literal|"Failed to retrieve service: "
operator|+
name|e
operator|.
name|getMessage
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
name|INTERNAL_SERVER_ERROR
argument_list|)
operator|.
name|entity
argument_list|(
name|serviceStatus
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|DELETE
annotation|@
name|Path
argument_list|(
name|SERVICE_PATH
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|deleteService (@athParamSERVICE_NAME) String appName)
specifier|public
name|Response
name|deleteService
parameter_list|(
annotation|@
name|PathParam
argument_list|(
name|SERVICE_NAME
argument_list|)
name|String
name|appName
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"DELETE: deleteService for appName = {}"
argument_list|,
name|appName
argument_list|)
expr_stmt|;
return|return
name|stopService
argument_list|(
name|appName
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|stopService (String appName, boolean destroy)
specifier|private
name|Response
name|stopService
parameter_list|(
name|String
name|appName
parameter_list|,
name|boolean
name|destroy
parameter_list|)
block|{
try|try
block|{
name|SERVICE_CLIENT
operator|.
name|actionStop
argument_list|(
name|appName
argument_list|,
name|destroy
argument_list|)
expr_stmt|;
if|if
condition|(
name|destroy
condition|)
block|{
name|SERVICE_CLIENT
operator|.
name|actionDestroy
argument_list|(
name|appName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully deleted service {}"
argument_list|,
name|appName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully stopped service {}"
argument_list|,
name|appName
argument_list|)
expr_stmt|;
block|}
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|NO_CONTENT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ApplicationNotFoundException
name|e
parameter_list|)
block|{
name|ServiceStatus
name|serviceStatus
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
name|serviceStatus
operator|.
name|setDiagnostics
argument_list|(
literal|"Service "
operator|+
name|appName
operator|+
literal|" not found "
operator|+
name|e
operator|.
name|getMessage
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
name|NOT_FOUND
argument_list|)
operator|.
name|entity
argument_list|(
name|serviceStatus
argument_list|)
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
name|ServiceStatus
name|serviceStatus
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
name|serviceStatus
operator|.
name|setDiagnostics
argument_list|(
name|e
operator|.
name|getMessage
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
name|INTERNAL_SERVER_ERROR
argument_list|)
operator|.
name|entity
argument_list|(
name|serviceStatus
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
name|COMPONENT_PATH
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
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
name|TEXT_PLAIN
block|}
argument_list|)
DECL|method|updateComponent (@athParamSERVICE_NAME) String appName, @PathParam(COMPONENT_NAME) String componentName, Component component)
specifier|public
name|Response
name|updateComponent
parameter_list|(
annotation|@
name|PathParam
argument_list|(
name|SERVICE_NAME
argument_list|)
name|String
name|appName
parameter_list|,
annotation|@
name|PathParam
argument_list|(
name|COMPONENT_NAME
argument_list|)
name|String
name|componentName
parameter_list|,
name|Component
name|component
parameter_list|)
block|{
if|if
condition|(
name|component
operator|.
name|getNumberOfContainers
argument_list|()
operator|<
literal|0
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
name|entity
argument_list|(
literal|"Service = "
operator|+
name|appName
operator|+
literal|", Component = "
operator|+
name|component
operator|.
name|getName
argument_list|()
operator|+
literal|": Invalid number of containers specified "
operator|+
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
name|ServiceStatus
name|status
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
try|try
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
name|original
init|=
name|SERVICE_CLIENT
operator|.
name|flexByRestService
argument_list|(
name|appName
argument_list|,
name|Collections
operator|.
name|singletonMap
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
literal|"Updating component ("
operator|+
name|componentName
operator|+
literal|") size from "
operator|+
name|original
operator|.
name|get
argument_list|(
name|componentName
argument_list|)
operator|+
literal|" to "
operator|+
name|component
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|()
operator|.
name|entity
argument_list|(
name|status
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|YarnException
decl||
name|IOException
name|e
parameter_list|)
block|{
name|status
operator|.
name|setDiagnostics
argument_list|(
name|e
operator|.
name|getMessage
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
name|INTERNAL_SERVER_ERROR
argument_list|)
operator|.
name|entity
argument_list|(
name|status
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
annotation|@
name|PUT
annotation|@
name|Path
argument_list|(
name|SERVICE_PATH
argument_list|)
annotation|@
name|Consumes
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
annotation|@
name|Produces
argument_list|(
block|{
name|MediaType
operator|.
name|APPLICATION_JSON
block|}
argument_list|)
DECL|method|updateService (@athParamSERVICE_NAME) String appName, Service updateServiceData)
specifier|public
name|Response
name|updateService
parameter_list|(
annotation|@
name|PathParam
argument_list|(
name|SERVICE_NAME
argument_list|)
name|String
name|appName
parameter_list|,
name|Service
name|updateServiceData
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"PUT: updateService for app = {} with data = {}"
argument_list|,
name|appName
argument_list|,
name|updateServiceData
argument_list|)
expr_stmt|;
comment|// Ignore the app name provided in updateServiceData and always use appName
comment|// path param
name|updateServiceData
operator|.
name|setName
argument_list|(
name|appName
argument_list|)
expr_stmt|;
comment|// For STOP the app should be running. If already stopped then this
comment|// operation will be a no-op. For START it should be in stopped state.
comment|// If already running then this operation will be a no-op.
if|if
condition|(
name|updateServiceData
operator|.
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
name|updateServiceData
operator|.
name|getState
argument_list|()
operator|==
name|ServiceState
operator|.
name|STOPPED
condition|)
block|{
return|return
name|stopService
argument_list|(
name|appName
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|// If a START is requested
if|if
condition|(
name|updateServiceData
operator|.
name|getState
argument_list|()
operator|!=
literal|null
operator|&&
name|updateServiceData
operator|.
name|getState
argument_list|()
operator|==
name|ServiceState
operator|.
name|STARTED
condition|)
block|{
return|return
name|startService
argument_list|(
name|appName
argument_list|)
return|;
block|}
comment|// If new lifetime value specified then update it
if|if
condition|(
name|updateServiceData
operator|.
name|getLifetime
argument_list|()
operator|!=
literal|null
operator|&&
name|updateServiceData
operator|.
name|getLifetime
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|updateLifetime
argument_list|(
name|appName
argument_list|,
name|updateServiceData
argument_list|)
return|;
block|}
comment|// flex a single component app
if|if
condition|(
name|updateServiceData
operator|.
name|getNumberOfContainers
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|ServiceApiUtil
operator|.
name|hasComponent
argument_list|(
name|updateServiceData
argument_list|)
condition|)
block|{
name|Component
name|defaultComp
init|=
name|ServiceApiUtil
operator|.
name|createDefaultComponent
argument_list|(
name|updateServiceData
argument_list|)
decl_stmt|;
return|return
name|updateComponent
argument_list|(
name|updateServiceData
operator|.
name|getName
argument_list|()
argument_list|,
name|defaultComp
operator|.
name|getName
argument_list|()
argument_list|,
name|defaultComp
argument_list|)
return|;
block|}
comment|// If nothing happens consider it a no-op
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|NO_CONTENT
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
DECL|method|updateLifetime (String appName, Service updateAppData)
specifier|private
name|Response
name|updateLifetime
parameter_list|(
name|String
name|appName
parameter_list|,
name|Service
name|updateAppData
parameter_list|)
block|{
name|ServiceStatus
name|status
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|newLifeTime
init|=
name|SERVICE_CLIENT
operator|.
name|updateLifetime
argument_list|(
name|appName
argument_list|,
name|updateAppData
operator|.
name|getLifetime
argument_list|()
argument_list|)
decl_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
literal|"Service ("
operator|+
name|appName
operator|+
literal|")'s lifeTime is updated to "
operator|+
name|newLifeTime
operator|+
literal|", "
operator|+
name|updateAppData
operator|.
name|getLifetime
argument_list|()
operator|+
literal|" seconds remaining"
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|status
argument_list|)
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
name|String
name|message
init|=
literal|"Failed to update service ("
operator|+
name|appName
operator|+
literal|")'s lifetime to "
operator|+
name|updateAppData
operator|.
name|getLifetime
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
name|message
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
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
name|INTERNAL_SERVER_ERROR
argument_list|)
operator|.
name|entity
argument_list|(
name|status
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
DECL|method|startService (String appName)
specifier|private
name|Response
name|startService
parameter_list|(
name|String
name|appName
parameter_list|)
block|{
name|ServiceStatus
name|status
init|=
operator|new
name|ServiceStatus
argument_list|()
decl_stmt|;
try|try
block|{
name|SERVICE_CLIENT
operator|.
name|actionStart
argument_list|(
name|appName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Successfully started service "
operator|+
name|appName
argument_list|)
expr_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
literal|"Service "
operator|+
name|appName
operator|+
literal|" is successfully started."
argument_list|)
expr_stmt|;
name|status
operator|.
name|setState
argument_list|(
name|ServiceState
operator|.
name|ACCEPTED
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|ok
argument_list|(
name|status
argument_list|)
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
name|String
name|message
init|=
literal|"Failed to start service "
operator|+
name|appName
decl_stmt|;
name|status
operator|.
name|setDiagnostics
argument_list|(
name|message
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
name|Response
operator|.
name|status
argument_list|(
name|Status
operator|.
name|INTERNAL_SERVER_ERROR
argument_list|)
operator|.
name|entity
argument_list|(
name|status
argument_list|)
operator|.
name|build
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

