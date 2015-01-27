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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
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
name|ExitUtil
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
name|ShutdownHookManager
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
name|StringUtils
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
name|YarnUncaughtExceptionHandler
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
name|server
operator|.
name|api
operator|.
name|ApplicationInitializationContext
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
name|api
operator|.
name|ApplicationTerminationContext
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
name|api
operator|.
name|AuxiliaryService
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
name|api
operator|.
name|ContainerContext
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
name|api
operator|.
name|ContainerInitializationContext
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
name|api
operator|.
name|ContainerTerminationContext
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
name|WebApps
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
name|YarnWebParams
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
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_comment
comment|/**  * The top-level server for the per-node timeline aggregator service. Currently  * it is defined as an auxiliary service to accommodate running within another  * daemon (e.g. node manager).  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|PerNodeAggregatorServer
specifier|public
class|class
name|PerNodeAggregatorServer
extends|extends
name|AuxiliaryService
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
name|PerNodeAggregatorServer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|SHUTDOWN_HOOK_PRIORITY
specifier|private
specifier|static
specifier|final
name|int
name|SHUTDOWN_HOOK_PRIORITY
init|=
literal|30
decl_stmt|;
DECL|field|serviceManager
specifier|private
specifier|final
name|AppLevelServiceManager
name|serviceManager
decl_stmt|;
DECL|field|webApp
specifier|private
name|WebApp
name|webApp
decl_stmt|;
DECL|method|PerNodeAggregatorServer ()
specifier|public
name|PerNodeAggregatorServer
parameter_list|()
block|{
comment|// use the same singleton
name|this
argument_list|(
name|AppLevelServiceManager
operator|.
name|getInstance
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|PerNodeAggregatorServer (AppLevelServiceManager serviceManager)
name|PerNodeAggregatorServer
parameter_list|(
name|AppLevelServiceManager
name|serviceManager
parameter_list|)
block|{
name|super
argument_list|(
literal|"timeline_aggregator"
argument_list|)
expr_stmt|;
name|this
operator|.
name|serviceManager
operator|=
name|serviceManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|protected
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|serviceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStart ()
specifier|protected
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStart
argument_list|()
expr_stmt|;
name|serviceManager
operator|.
name|start
argument_list|()
expr_stmt|;
name|startWebApp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|webApp
operator|!=
literal|null
condition|)
block|{
name|webApp
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// stop the service manager
name|serviceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
DECL|method|startWebApp ()
specifier|private
name|void
name|startWebApp
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
comment|// use the same ports as the old ATS for now; we could create new properties
comment|// for the new timeline service if needed
name|String
name|bindAddress
init|=
name|WebAppUtils
operator|.
name|getWebAppBindURL
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|TIMELINE_SERVICE_BIND_HOST
argument_list|,
name|WebAppUtils
operator|.
name|getAHSWebAppURLWithoutScheme
argument_list|(
name|conf
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Instantiating the per-node aggregator webapp at "
operator|+
name|bindAddress
argument_list|)
expr_stmt|;
try|try
block|{
name|webApp
operator|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"timeline"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|"ws"
argument_list|)
operator|.
name|with
argument_list|(
name|conf
argument_list|)
operator|.
name|at
argument_list|(
name|bindAddress
argument_list|)
operator|.
name|start
argument_list|(
operator|new
name|TimelineServiceWebApp
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|msg
init|=
literal|"The per-node aggregator webapp failed to start."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|YarnRuntimeException
argument_list|(
name|msg
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
DECL|class|TimelineServiceWebApp
specifier|private
specifier|static
class|class
name|TimelineServiceWebApp
extends|extends
name|WebApp
implements|implements
name|YarnWebParams
block|{
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|bind
argument_list|(
name|PerNodeAggregatorWebService
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// bind to the global singleton
name|bind
argument_list|(
name|AppLevelServiceManager
operator|.
name|class
argument_list|)
operator|.
name|toProvider
argument_list|(
name|AppLevelServiceManagerProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
comment|// these methods can be used as the basis for future service methods if the
comment|// per-node aggregator runs separate from the node manager
comment|/**    * Creates and adds an app level aggregator service for the specified    * application id. The service is also initialized and started. If the service    * already exists, no new service is created.    *    * @return whether it was added successfully    */
DECL|method|addApplication (ApplicationId appId)
specifier|public
name|boolean
name|addApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|String
name|appIdString
init|=
name|appId
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|serviceManager
operator|.
name|addService
argument_list|(
name|appIdString
argument_list|)
return|;
block|}
comment|/**    * Removes the app level aggregator service for the specified application id.    * The service is also stopped as a result. If the service does not exist, no    * change is made.    *    * @return whether it was removed successfully    */
DECL|method|removeApplication (ApplicationId appId)
specifier|public
name|boolean
name|removeApplication
parameter_list|(
name|ApplicationId
name|appId
parameter_list|)
block|{
name|String
name|appIdString
init|=
name|appId
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|serviceManager
operator|.
name|removeService
argument_list|(
name|appIdString
argument_list|)
return|;
block|}
comment|/**    * Creates and adds an app level aggregator service for the specified    * application id. The service is also initialized and started. If the service    * already exists, no new service is created.    */
annotation|@
name|Override
DECL|method|initializeContainer (ContainerInitializationContext context)
specifier|public
name|void
name|initializeContainer
parameter_list|(
name|ContainerInitializationContext
name|context
parameter_list|)
block|{
comment|// intercept the event of the AM container being created and initialize the
comment|// app level aggregator service
if|if
condition|(
name|isApplicationMaster
argument_list|(
name|context
argument_list|)
condition|)
block|{
name|ApplicationId
name|appId
init|=
name|context
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|addApplication
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Removes the app level aggregator service for the specified application id.    * The service is also stopped as a result. If the service does not exist, no    * change is made.    */
annotation|@
name|Override
DECL|method|stopContainer (ContainerTerminationContext context)
specifier|public
name|void
name|stopContainer
parameter_list|(
name|ContainerTerminationContext
name|context
parameter_list|)
block|{
comment|// intercept the event of the AM container being stopped and remove the app
comment|// level aggregator service
if|if
condition|(
name|isApplicationMaster
argument_list|(
name|context
argument_list|)
condition|)
block|{
name|ApplicationId
name|appId
init|=
name|context
operator|.
name|getContainerId
argument_list|()
operator|.
name|getApplicationAttemptId
argument_list|()
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|removeApplication
argument_list|(
name|appId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|isApplicationMaster (ContainerContext context)
specifier|private
name|boolean
name|isApplicationMaster
parameter_list|(
name|ContainerContext
name|context
parameter_list|)
block|{
comment|// TODO this is based on a (shaky) assumption that the container id (the
comment|// last field of the full container id) for an AM is always 1
comment|// we want to make this much more reliable
name|ContainerId
name|containerId
init|=
name|context
operator|.
name|getContainerId
argument_list|()
decl_stmt|;
return|return
name|containerId
operator|.
name|getContainerId
argument_list|()
operator|==
literal|1L
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|hasApplication (String appId)
name|boolean
name|hasApplication
parameter_list|(
name|String
name|appId
parameter_list|)
block|{
return|return
name|serviceManager
operator|.
name|hasService
argument_list|(
name|appId
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|initializeApplication (ApplicationInitializationContext context)
specifier|public
name|void
name|initializeApplication
parameter_list|(
name|ApplicationInitializationContext
name|context
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|stopApplication (ApplicationTerminationContext context)
specifier|public
name|void
name|stopApplication
parameter_list|(
name|ApplicationTerminationContext
name|context
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|getMetaData ()
specifier|public
name|ByteBuffer
name|getMetaData
parameter_list|()
block|{
comment|// TODO currently it is not used; we can return a more meaningful data when
comment|// we connect it with an AM
return|return
name|ByteBuffer
operator|.
name|allocate
argument_list|(
literal|0
argument_list|)
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|launchServer (String[] args)
specifier|static
name|PerNodeAggregatorServer
name|launchServer
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Thread
operator|.
name|setDefaultUncaughtExceptionHandler
argument_list|(
operator|new
name|YarnUncaughtExceptionHandler
argument_list|()
argument_list|)
expr_stmt|;
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|PerNodeAggregatorServer
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|PerNodeAggregatorServer
name|server
init|=
literal|null
decl_stmt|;
try|try
block|{
name|server
operator|=
operator|new
name|PerNodeAggregatorServer
argument_list|()
expr_stmt|;
name|ShutdownHookManager
operator|.
name|get
argument_list|()
operator|.
name|addShutdownHook
argument_list|(
operator|new
name|ShutdownHook
argument_list|(
name|server
argument_list|)
argument_list|,
name|SHUTDOWN_HOOK_PRIORITY
argument_list|)
expr_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|server
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error starting PerNodeAggregatorServer"
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
operator|-
literal|1
argument_list|,
literal|"Error starting PerNodeAggregatorServer"
argument_list|)
expr_stmt|;
block|}
return|return
name|server
return|;
block|}
DECL|class|ShutdownHook
specifier|private
specifier|static
class|class
name|ShutdownHook
implements|implements
name|Runnable
block|{
DECL|field|server
specifier|private
specifier|final
name|PerNodeAggregatorServer
name|server
decl_stmt|;
DECL|method|ShutdownHook (PerNodeAggregatorServer server)
specifier|public
name|ShutdownHook
parameter_list|(
name|PerNodeAggregatorServer
name|server
parameter_list|)
block|{
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|launchServer
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

