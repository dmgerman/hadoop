begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service
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
package|;
end_package

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
name|service
operator|.
name|AbstractService
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
name|service
operator|.
name|component
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
name|compinstance
operator|.
name|ComponentInstance
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
name|conf
operator|.
name|YarnServiceConf
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
name|InternalKeys
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
name|component
operator|.
name|ComponentEvent
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
name|compinstance
operator|.
name|ComponentInstanceEvent
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
name|component
operator|.
name|ComponentState
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
name|ResourceKeys
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
name|common
operator|.
name|tools
operator|.
name|SliderUtils
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
name|servicemonitor
operator|.
name|ProbeStatus
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
name|Executors
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
name|ScheduledExecutorService
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
name|compinstance
operator|.
name|ComponentInstanceState
operator|.
name|RUNNING_BUT_UNREADY
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
name|component
operator|.
name|ComponentEventType
operator|.
name|FLEX
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
name|compinstance
operator|.
name|ComponentInstanceEventType
operator|.
name|BECOME_NOT_READY
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
name|compinstance
operator|.
name|ComponentInstanceEventType
operator|.
name|BECOME_READY
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
name|compinstance
operator|.
name|ComponentInstanceState
operator|.
name|READY
import|;
end_import

begin_class
DECL|class|ServiceMonitor
specifier|public
class|class
name|ServiceMonitor
extends|extends
name|AbstractService
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
name|ServiceMonitor
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|executorService
specifier|public
name|ScheduledExecutorService
name|executorService
decl_stmt|;
DECL|field|liveInstances
specifier|private
name|Map
argument_list|<
name|ContainerId
argument_list|,
name|ComponentInstance
argument_list|>
name|liveInstances
init|=
literal|null
decl_stmt|;
DECL|field|context
specifier|private
name|ServiceContext
name|context
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|method|ServiceMonitor (String name, ServiceContext context)
specifier|public
name|ServiceMonitor
parameter_list|(
name|String
name|name
parameter_list|,
name|ServiceContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|liveInstances
operator|=
name|context
operator|.
name|scheduler
operator|.
name|getLiveInstances
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|executorService
operator|=
name|Executors
operator|.
name|newScheduledThreadPool
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
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
specifier|public
name|void
name|serviceStart
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|readinessCheckInterval
init|=
name|YarnServiceConf
operator|.
name|getLong
argument_list|(
name|InternalKeys
operator|.
name|MONITOR_INTERVAL
argument_list|,
name|InternalKeys
operator|.
name|DEFAULT_MONITOR_INTERVAL
argument_list|,
name|context
operator|.
name|application
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|ReadinessChecker
argument_list|()
argument_list|,
name|readinessCheckInterval
argument_list|,
name|readinessCheckInterval
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|long
name|failureResetInterval
init|=
name|SliderUtils
operator|.
name|getTimeRange
argument_list|(
name|context
operator|.
name|application
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|ResourceKeys
operator|.
name|CONTAINER_FAILURE_WINDOW
argument_list|,
name|ResourceKeys
operator|.
name|DEFAULT_CONTAINER_FAILURE_WINDOW_DAYS
argument_list|,
name|ResourceKeys
operator|.
name|DEFAULT_CONTAINER_FAILURE_WINDOW_HOURS
argument_list|,
name|ResourceKeys
operator|.
name|DEFAULT_CONTAINER_FAILURE_WINDOW_MINUTES
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|executorService
operator|.
name|scheduleAtFixedRate
argument_list|(
operator|new
name|ContainerFailureReset
argument_list|()
argument_list|,
name|failureResetInterval
argument_list|,
name|failureResetInterval
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|executorService
operator|!=
literal|null
condition|)
block|{
name|executorService
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|ReadinessChecker
specifier|private
class|class
name|ReadinessChecker
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
comment|// check if the comp instance are ready
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|ContainerId
argument_list|,
name|ComponentInstance
argument_list|>
name|entry
range|:
name|liveInstances
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|ComponentInstance
name|instance
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|ProbeStatus
name|status
init|=
name|instance
operator|.
name|ping
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|.
name|isSuccess
argument_list|()
condition|)
block|{
if|if
condition|(
name|instance
operator|.
name|getState
argument_list|()
operator|==
name|RUNNING_BUT_UNREADY
condition|)
block|{
comment|// synchronously update the state.
name|instance
operator|.
name|handle
argument_list|(
operator|new
name|ComponentInstanceEvent
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|BECOME_READY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|instance
operator|.
name|getState
argument_list|()
operator|==
name|READY
condition|)
block|{
name|instance
operator|.
name|handle
argument_list|(
operator|new
name|ComponentInstanceEvent
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|BECOME_NOT_READY
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|Component
name|component
range|:
name|context
operator|.
name|scheduler
operator|.
name|getAllComponents
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
comment|// If comp hasn't started yet and its dependencies are satisfied
if|if
condition|(
name|component
operator|.
name|getState
argument_list|()
operator|==
name|ComponentState
operator|.
name|INIT
operator|&&
name|component
operator|.
name|areDependenciesReady
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"[COMPONENT {}]: Dependencies satisfied, ramping up."
argument_list|,
name|component
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|ComponentEvent
name|event
init|=
operator|new
name|ComponentEvent
argument_list|(
name|component
operator|.
name|getName
argument_list|()
argument_list|,
name|FLEX
argument_list|)
operator|.
name|setDesired
argument_list|(
name|component
operator|.
name|getComponentSpec
argument_list|()
operator|.
name|getNumberOfContainers
argument_list|()
argument_list|)
decl_stmt|;
name|component
operator|.
name|handle
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|class|ContainerFailureReset
specifier|private
class|class
name|ContainerFailureReset
implements|implements
name|Runnable
block|{
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
for|for
control|(
name|Component
name|component
range|:
name|context
operator|.
name|scheduler
operator|.
name|getAllComponents
argument_list|()
operator|.
name|values
argument_list|()
control|)
block|{
name|component
operator|.
name|resetCompFailureCount
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

