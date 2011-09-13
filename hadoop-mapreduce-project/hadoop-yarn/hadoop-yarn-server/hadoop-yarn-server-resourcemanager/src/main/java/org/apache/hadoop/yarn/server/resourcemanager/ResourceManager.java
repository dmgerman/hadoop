begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager
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
name|resourcemanager
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
name|concurrent
operator|.
name|BlockingQueue
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
name|LinkedBlockingQueue
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
name|atomic
operator|.
name|AtomicBoolean
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|SecurityUtil
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
name|ReflectionUtils
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|NodeId
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
name|event
operator|.
name|AsyncDispatcher
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
name|event
operator|.
name|Dispatcher
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
name|event
operator|.
name|EventHandler
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
name|security
operator|.
name|ApplicationTokenSecretManager
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
name|security
operator|.
name|client
operator|.
name|ClientToAMSecretManager
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
name|resourcemanager
operator|.
name|amlauncher
operator|.
name|ApplicationMasterLauncher
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|Recoverable
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|Store
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|Store
operator|.
name|RMState
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
name|resourcemanager
operator|.
name|recovery
operator|.
name|StoreFactory
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMApp
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppEvent
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|RMAppEventType
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|AMLivelinessMonitor
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttempt
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptEvent
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptEventType
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
name|resourcemanager
operator|.
name|rmcontainer
operator|.
name|ContainerAllocationExpirer
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNode
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNodeEvent
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
name|resourcemanager
operator|.
name|rmnode
operator|.
name|RMNodeEventType
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|ResourceScheduler
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|event
operator|.
name|SchedulerEvent
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|event
operator|.
name|SchedulerEventType
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|fifo
operator|.
name|FifoScheduler
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|RMWebApp
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
name|security
operator|.
name|ContainerTokenSecretManager
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
name|service
operator|.
name|CompositeService
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

begin_comment
comment|/**  * The ResourceManager is the main class that is a set of components.  *  */
end_comment

begin_class
DECL|class|ResourceManager
specifier|public
class|class
name|ResourceManager
extends|extends
name|CompositeService
implements|implements
name|Recoverable
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
name|ResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|clusterTimeStamp
specifier|public
specifier|static
specifier|final
name|long
name|clusterTimeStamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
DECL|field|conf
specifier|private
name|YarnConfiguration
name|conf
decl_stmt|;
DECL|field|clientToAMSecretManager
specifier|protected
name|ClientToAMSecretManager
name|clientToAMSecretManager
init|=
operator|new
name|ClientToAMSecretManager
argument_list|()
decl_stmt|;
DECL|field|containerTokenSecretManager
specifier|protected
name|ContainerTokenSecretManager
name|containerTokenSecretManager
init|=
operator|new
name|ContainerTokenSecretManager
argument_list|()
decl_stmt|;
DECL|field|appTokenSecretManager
specifier|protected
name|ApplicationTokenSecretManager
name|appTokenSecretManager
init|=
operator|new
name|ApplicationTokenSecretManager
argument_list|()
decl_stmt|;
DECL|field|rmDispatcher
specifier|private
name|Dispatcher
name|rmDispatcher
decl_stmt|;
DECL|field|scheduler
specifier|protected
name|ResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|clientRM
specifier|private
name|ClientRMService
name|clientRM
decl_stmt|;
DECL|field|masterService
specifier|protected
name|ApplicationMasterService
name|masterService
decl_stmt|;
DECL|field|applicationMasterLauncher
specifier|private
name|ApplicationMasterLauncher
name|applicationMasterLauncher
decl_stmt|;
DECL|field|adminService
specifier|private
name|AdminService
name|adminService
decl_stmt|;
DECL|field|containerAllocationExpirer
specifier|private
name|ContainerAllocationExpirer
name|containerAllocationExpirer
decl_stmt|;
DECL|field|nmLivelinessMonitor
specifier|protected
name|NMLivelinessMonitor
name|nmLivelinessMonitor
decl_stmt|;
DECL|field|nodesListManager
specifier|protected
name|NodesListManager
name|nodesListManager
decl_stmt|;
DECL|field|schedulerDispatcher
specifier|private
name|SchedulerEventDispatcher
name|schedulerDispatcher
decl_stmt|;
DECL|field|rmAppManager
specifier|protected
name|RMAppManager
name|rmAppManager
decl_stmt|;
DECL|field|shutdown
specifier|private
specifier|final
name|AtomicBoolean
name|shutdown
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
DECL|field|webApp
specifier|private
name|WebApp
name|webApp
decl_stmt|;
DECL|field|rmContext
specifier|private
name|RMContext
name|rmContext
decl_stmt|;
DECL|field|store
specifier|private
specifier|final
name|Store
name|store
decl_stmt|;
DECL|field|resourceTracker
specifier|protected
name|ResourceTrackerService
name|resourceTracker
decl_stmt|;
DECL|method|ResourceManager (Store store)
specifier|public
name|ResourceManager
parameter_list|(
name|Store
name|store
parameter_list|)
block|{
name|super
argument_list|(
literal|"ResourceManager"
argument_list|)
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|nodesListManager
operator|=
operator|new
name|NodesListManager
argument_list|()
expr_stmt|;
block|}
DECL|method|getRMContext ()
specifier|public
name|RMContext
name|getRMContext
parameter_list|()
block|{
return|return
name|this
operator|.
name|rmContext
return|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
specifier|synchronized
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|rmDispatcher
operator|=
operator|new
name|AsyncDispatcher
argument_list|()
expr_stmt|;
name|addIfService
argument_list|(
name|this
operator|.
name|rmDispatcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|containerAllocationExpirer
operator|=
operator|new
name|ContainerAllocationExpirer
argument_list|(
name|this
operator|.
name|rmDispatcher
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|this
operator|.
name|containerAllocationExpirer
argument_list|)
expr_stmt|;
name|AMLivelinessMonitor
name|amLivelinessMonitor
init|=
name|createAMLivelinessMonitor
argument_list|()
decl_stmt|;
name|addService
argument_list|(
name|amLivelinessMonitor
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmContext
operator|=
operator|new
name|RMContextImpl
argument_list|(
name|this
operator|.
name|store
argument_list|,
name|this
operator|.
name|rmDispatcher
argument_list|,
name|this
operator|.
name|containerAllocationExpirer
argument_list|,
name|amLivelinessMonitor
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|nodesListManager
argument_list|)
expr_stmt|;
comment|// Initialize the config
name|this
operator|.
name|conf
operator|=
operator|new
name|YarnConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// Initialize the scheduler
name|this
operator|.
name|scheduler
operator|=
name|createScheduler
argument_list|()
expr_stmt|;
name|this
operator|.
name|schedulerDispatcher
operator|=
operator|new
name|SchedulerEventDispatcher
argument_list|(
name|this
operator|.
name|scheduler
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|this
operator|.
name|schedulerDispatcher
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmDispatcher
operator|.
name|register
argument_list|(
name|SchedulerEventType
operator|.
name|class
argument_list|,
name|this
operator|.
name|schedulerDispatcher
argument_list|)
expr_stmt|;
comment|// Register event handler for RmAppEvents
name|this
operator|.
name|rmDispatcher
operator|.
name|register
argument_list|(
name|RMAppEventType
operator|.
name|class
argument_list|,
operator|new
name|ApplicationEventDispatcher
argument_list|(
name|this
operator|.
name|rmContext
argument_list|)
argument_list|)
expr_stmt|;
comment|// Register event handler for RmAppAttemptEvents
name|this
operator|.
name|rmDispatcher
operator|.
name|register
argument_list|(
name|RMAppAttemptEventType
operator|.
name|class
argument_list|,
operator|new
name|ApplicationAttemptEventDispatcher
argument_list|(
name|this
operator|.
name|rmContext
argument_list|)
argument_list|)
expr_stmt|;
comment|// Register event handler for RmNodes
name|this
operator|.
name|rmDispatcher
operator|.
name|register
argument_list|(
name|RMNodeEventType
operator|.
name|class
argument_list|,
operator|new
name|NodeEventDispatcher
argument_list|(
name|this
operator|.
name|rmContext
argument_list|)
argument_list|)
expr_stmt|;
comment|//TODO change this to be random
name|this
operator|.
name|appTokenSecretManager
operator|.
name|setMasterKey
argument_list|(
name|ApplicationTokenSecretManager
operator|.
name|createSecretKey
argument_list|(
literal|"Dummy"
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|nmLivelinessMonitor
operator|=
name|createNMLivelinessMonitor
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|this
operator|.
name|nmLivelinessMonitor
argument_list|)
expr_stmt|;
name|this
operator|.
name|resourceTracker
operator|=
name|createResourceTrackerService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|resourceTracker
argument_list|)
expr_stmt|;
try|try
block|{
name|this
operator|.
name|scheduler
operator|.
name|reinitialize
argument_list|(
name|this
operator|.
name|conf
argument_list|,
name|this
operator|.
name|containerTokenSecretManager
argument_list|,
name|this
operator|.
name|rmContext
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Failed to initialize scheduler"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|masterService
operator|=
name|createApplicationMasterService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|masterService
argument_list|)
expr_stmt|;
name|this
operator|.
name|rmAppManager
operator|=
name|createRMAppManager
argument_list|()
expr_stmt|;
comment|// Register event handler for RMAppManagerEvents
name|this
operator|.
name|rmDispatcher
operator|.
name|register
argument_list|(
name|RMAppManagerEventType
operator|.
name|class
argument_list|,
name|this
operator|.
name|rmAppManager
argument_list|)
expr_stmt|;
name|clientRM
operator|=
name|createClientRMService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|clientRM
argument_list|)
expr_stmt|;
name|adminService
operator|=
name|createAdminService
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|adminService
argument_list|)
expr_stmt|;
name|this
operator|.
name|applicationMasterLauncher
operator|=
name|createAMLauncher
argument_list|()
expr_stmt|;
name|addService
argument_list|(
name|applicationMasterLauncher
argument_list|)
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
DECL|method|addIfService (Object object)
specifier|protected
name|void
name|addIfService
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|object
operator|instanceof
name|Service
condition|)
block|{
name|addService
argument_list|(
operator|(
name|Service
operator|)
name|object
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|createScheduler ()
specifier|protected
name|ResourceScheduler
name|createScheduler
parameter_list|()
block|{
return|return
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|conf
operator|.
name|getClass
argument_list|(
name|YarnConfiguration
operator|.
name|RM_SCHEDULER
argument_list|,
name|FifoScheduler
operator|.
name|class
argument_list|,
name|ResourceScheduler
operator|.
name|class
argument_list|)
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
DECL|method|createAMLauncher ()
specifier|protected
name|ApplicationMasterLauncher
name|createAMLauncher
parameter_list|()
block|{
return|return
operator|new
name|ApplicationMasterLauncher
argument_list|(
name|this
operator|.
name|appTokenSecretManager
argument_list|,
name|this
operator|.
name|clientToAMSecretManager
argument_list|,
name|this
operator|.
name|rmContext
argument_list|)
return|;
block|}
DECL|method|createNMLivelinessMonitor ()
specifier|private
name|NMLivelinessMonitor
name|createNMLivelinessMonitor
parameter_list|()
block|{
return|return
operator|new
name|NMLivelinessMonitor
argument_list|(
name|this
operator|.
name|rmContext
operator|.
name|getDispatcher
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createAMLivelinessMonitor ()
specifier|protected
name|AMLivelinessMonitor
name|createAMLivelinessMonitor
parameter_list|()
block|{
return|return
operator|new
name|AMLivelinessMonitor
argument_list|(
name|this
operator|.
name|rmDispatcher
argument_list|)
return|;
block|}
DECL|method|createRMAppManager ()
specifier|protected
name|RMAppManager
name|createRMAppManager
parameter_list|()
block|{
return|return
operator|new
name|RMAppManager
argument_list|(
name|this
operator|.
name|rmContext
argument_list|,
name|this
operator|.
name|clientToAMSecretManager
argument_list|,
name|this
operator|.
name|scheduler
argument_list|,
name|this
operator|.
name|masterService
argument_list|,
name|this
operator|.
name|conf
argument_list|)
return|;
block|}
annotation|@
name|Private
DECL|class|SchedulerEventDispatcher
specifier|public
specifier|static
specifier|final
class|class
name|SchedulerEventDispatcher
extends|extends
name|AbstractService
implements|implements
name|EventHandler
argument_list|<
name|SchedulerEvent
argument_list|>
block|{
DECL|field|scheduler
specifier|private
specifier|final
name|ResourceScheduler
name|scheduler
decl_stmt|;
DECL|field|eventQueue
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|SchedulerEvent
argument_list|>
name|eventQueue
init|=
operator|new
name|LinkedBlockingQueue
argument_list|<
name|SchedulerEvent
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|eventProcessor
specifier|private
specifier|final
name|Thread
name|eventProcessor
decl_stmt|;
DECL|method|SchedulerEventDispatcher (ResourceScheduler scheduler)
specifier|public
name|SchedulerEventDispatcher
parameter_list|(
name|ResourceScheduler
name|scheduler
parameter_list|)
block|{
name|super
argument_list|(
name|SchedulerEventDispatcher
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|scheduler
operator|=
name|scheduler
expr_stmt|;
name|this
operator|.
name|eventProcessor
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|EventProcessor
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
name|this
operator|.
name|eventProcessor
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|class|EventProcessor
specifier|private
specifier|final
class|class
name|EventProcessor
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
name|SchedulerEvent
name|event
decl_stmt|;
while|while
condition|(
operator|!
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|isInterrupted
argument_list|()
condition|)
block|{
try|try
block|{
name|event
operator|=
name|eventQueue
operator|.
name|take
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Returning, interrupted : "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return;
comment|// TODO: Kill RM.
block|}
try|try
block|{
name|scheduler
operator|.
name|handle
argument_list|(
name|event
argument_list|)
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
name|error
argument_list|(
literal|"Error in handling event type "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" to the scheduler"
argument_list|,
name|t
argument_list|)
expr_stmt|;
return|return;
comment|// TODO: Kill RM.
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
specifier|synchronized
name|void
name|stop
parameter_list|()
block|{
name|this
operator|.
name|eventProcessor
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|eventProcessor
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (SchedulerEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|SchedulerEvent
name|event
parameter_list|)
block|{
try|try
block|{
name|int
name|qSize
init|=
name|eventQueue
operator|.
name|size
argument_list|()
decl_stmt|;
if|if
condition|(
name|qSize
operator|!=
literal|0
operator|&&
name|qSize
operator|%
literal|1000
operator|==
literal|0
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Size of scheduler event-queue is "
operator|+
name|qSize
argument_list|)
expr_stmt|;
block|}
name|int
name|remCapacity
init|=
name|eventQueue
operator|.
name|remainingCapacity
argument_list|()
decl_stmt|;
if|if
condition|(
name|remCapacity
operator|<
literal|1000
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Very low remaining capacity on scheduler event queue: "
operator|+
name|remCapacity
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|eventQueue
operator|.
name|put
argument_list|(
name|event
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Private
DECL|class|ApplicationEventDispatcher
specifier|public
specifier|static
specifier|final
class|class
name|ApplicationEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|RMAppEvent
argument_list|>
block|{
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|ApplicationEventDispatcher (RMContext rmContext)
specifier|public
name|ApplicationEventDispatcher
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (RMAppEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMAppEvent
name|event
parameter_list|)
block|{
name|ApplicationId
name|appID
init|=
name|event
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appID
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|rmApp
operator|.
name|handle
argument_list|(
name|event
argument_list|)
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
name|error
argument_list|(
literal|"Error in handling event type "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" for application "
operator|+
name|appID
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Private
DECL|class|ApplicationAttemptEventDispatcher
specifier|public
specifier|static
specifier|final
class|class
name|ApplicationAttemptEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|RMAppAttemptEvent
argument_list|>
block|{
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|ApplicationAttemptEventDispatcher (RMContext rmContext)
specifier|public
name|ApplicationAttemptEventDispatcher
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (RMAppAttemptEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMAppAttemptEvent
name|event
parameter_list|)
block|{
name|ApplicationAttemptId
name|appAttemptID
init|=
name|event
operator|.
name|getApplicationAttemptId
argument_list|()
decl_stmt|;
name|ApplicationId
name|appAttemptId
init|=
name|appAttemptID
operator|.
name|getApplicationId
argument_list|()
decl_stmt|;
name|RMApp
name|rmApp
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMApps
argument_list|()
operator|.
name|get
argument_list|(
name|appAttemptId
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmApp
operator|!=
literal|null
condition|)
block|{
name|RMAppAttempt
name|rmAppAttempt
init|=
name|rmApp
operator|.
name|getRMAppAttempt
argument_list|(
name|appAttemptID
argument_list|)
decl_stmt|;
if|if
condition|(
name|rmAppAttempt
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|rmAppAttempt
operator|.
name|handle
argument_list|(
name|event
argument_list|)
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
name|error
argument_list|(
literal|"Error in handling event type "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" for applicationAttempt "
operator|+
name|appAttemptId
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
annotation|@
name|Private
DECL|class|NodeEventDispatcher
specifier|public
specifier|static
specifier|final
class|class
name|NodeEventDispatcher
implements|implements
name|EventHandler
argument_list|<
name|RMNodeEvent
argument_list|>
block|{
DECL|field|rmContext
specifier|private
specifier|final
name|RMContext
name|rmContext
decl_stmt|;
DECL|method|NodeEventDispatcher (RMContext rmContext)
specifier|public
name|NodeEventDispatcher
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
block|{
name|this
operator|.
name|rmContext
operator|=
name|rmContext
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|handle (RMNodeEvent event)
specifier|public
name|void
name|handle
parameter_list|(
name|RMNodeEvent
name|event
parameter_list|)
block|{
name|NodeId
name|nodeId
init|=
name|event
operator|.
name|getNodeId
argument_list|()
decl_stmt|;
name|RMNode
name|node
init|=
name|this
operator|.
name|rmContext
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
try|try
block|{
operator|(
operator|(
name|EventHandler
argument_list|<
name|RMNodeEvent
argument_list|>
operator|)
name|node
operator|)
operator|.
name|handle
argument_list|(
name|event
argument_list|)
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
name|error
argument_list|(
literal|"Error in handling event type "
operator|+
name|event
operator|.
name|getType
argument_list|()
operator|+
literal|" for node "
operator|+
name|nodeId
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|startWepApp ()
specifier|protected
name|void
name|startWepApp
parameter_list|()
block|{
name|webApp
operator|=
name|WebApps
operator|.
name|$for
argument_list|(
literal|"yarn"
argument_list|,
name|masterService
argument_list|)
operator|.
name|at
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|YarnConfiguration
operator|.
name|RM_WEBAPP_ADDRESS
argument_list|,
name|YarnConfiguration
operator|.
name|DEFAULT_RM_WEBAPP_ADDRESS
argument_list|)
argument_list|)
operator|.
name|start
argument_list|(
operator|new
name|RMWebApp
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
try|try
block|{
name|doSecureLogin
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
literal|"Failed to login"
argument_list|,
name|ie
argument_list|)
throw|;
block|}
name|startWepApp
argument_list|()
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"ResourceManager"
argument_list|)
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
comment|/*synchronized(shutdown) {       try {         while(!shutdown.get()) {           shutdown.wait();         }       } catch(InterruptedException ie) {         LOG.info("Interrupted while waiting", ie);       }     }*/
block|}
DECL|method|doSecureLogin ()
specifier|protected
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|YarnConfiguration
operator|.
name|RM_KEYTAB
argument_list|,
name|YarnConfiguration
operator|.
name|RM_PRINCIPAL
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
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
comment|/*synchronized(shutdown) {       shutdown.set(true);       shutdown.notifyAll();     }*/
name|DefaultMetricsSystem
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|createResourceTrackerService ()
specifier|protected
name|ResourceTrackerService
name|createResourceTrackerService
parameter_list|()
block|{
return|return
operator|new
name|ResourceTrackerService
argument_list|(
name|this
operator|.
name|rmContext
argument_list|,
name|this
operator|.
name|nodesListManager
argument_list|,
name|this
operator|.
name|nmLivelinessMonitor
argument_list|,
name|this
operator|.
name|containerTokenSecretManager
argument_list|)
return|;
block|}
DECL|method|createClientRMService ()
specifier|protected
name|ClientRMService
name|createClientRMService
parameter_list|()
block|{
return|return
operator|new
name|ClientRMService
argument_list|(
name|this
operator|.
name|rmContext
argument_list|,
name|scheduler
argument_list|,
name|this
operator|.
name|rmAppManager
argument_list|)
return|;
block|}
DECL|method|createApplicationMasterService ()
specifier|protected
name|ApplicationMasterService
name|createApplicationMasterService
parameter_list|()
block|{
return|return
operator|new
name|ApplicationMasterService
argument_list|(
name|this
operator|.
name|rmContext
argument_list|,
name|this
operator|.
name|appTokenSecretManager
argument_list|,
name|scheduler
argument_list|)
return|;
block|}
DECL|method|createAdminService ()
specifier|protected
name|AdminService
name|createAdminService
parameter_list|()
block|{
return|return
operator|new
name|AdminService
argument_list|(
name|conf
argument_list|,
name|scheduler
argument_list|,
name|rmContext
argument_list|,
name|this
operator|.
name|nodesListManager
argument_list|)
return|;
block|}
annotation|@
name|Private
DECL|method|getClientRMService ()
specifier|public
name|ClientRMService
name|getClientRMService
parameter_list|()
block|{
return|return
name|this
operator|.
name|clientRM
return|;
block|}
comment|/**    * return the scheduler.    * @return the scheduler for the Resource Manager.    */
annotation|@
name|Private
DECL|method|getResourceScheduler ()
specifier|public
name|ResourceScheduler
name|getResourceScheduler
parameter_list|()
block|{
return|return
name|this
operator|.
name|scheduler
return|;
block|}
comment|/**    * return the resource tracking component.    * @return the resource tracking component.    */
annotation|@
name|Private
DECL|method|getResourceTrackerService ()
specifier|public
name|ResourceTrackerService
name|getResourceTrackerService
parameter_list|()
block|{
return|return
name|this
operator|.
name|resourceTracker
return|;
block|}
annotation|@
name|Private
DECL|method|getApplicationMasterService ()
specifier|public
name|ApplicationMasterService
name|getApplicationMasterService
parameter_list|()
block|{
return|return
name|this
operator|.
name|masterService
return|;
block|}
annotation|@
name|Override
DECL|method|recover (RMState state)
specifier|public
name|void
name|recover
parameter_list|(
name|RMState
name|state
parameter_list|)
throws|throws
name|Exception
block|{
name|resourceTracker
operator|.
name|recover
argument_list|(
name|state
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|recover
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String argv[])
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|argv
index|[]
parameter_list|)
block|{
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|ResourceManager
operator|.
name|class
argument_list|,
name|argv
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|ResourceManager
name|resourceManager
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|Store
name|store
init|=
name|StoreFactory
operator|.
name|getStore
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|(
name|store
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|//resourceManager.recover(store.restore());
comment|//store.doneWithRecovery();
name|resourceManager
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error starting RM"
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|resourceManager
operator|!=
literal|null
condition|)
block|{
name|resourceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

