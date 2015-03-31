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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|Collection
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
name|http
operator|.
name|lib
operator|.
name|StaticUserWebFilter
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
name|net
operator|.
name|NetworkTopology
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
name|AuthenticationFilterInitializer
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
name|Priority
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
name|Resource
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
name|ResourceRequest
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
name|resourcemanager
operator|.
name|rmapp
operator|.
name|attempt
operator|.
name|RMAppAttemptState
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
name|scheduler
operator|.
name|event
operator|.
name|AppAttemptRemovedSchedulerEvent
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
name|NodeAddedSchedulerEvent
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
name|NodeUpdateSchedulerEvent
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
name|http
operator|.
name|RMAuthenticationFilterInitializer
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
name|resource
operator|.
name|Resources
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestResourceManager
specifier|public
class|class
name|TestResourceManager
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
name|TestResourceManager
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|resourceManager
specifier|private
name|ResourceManager
name|resourceManager
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|()
expr_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getContainerTokenSecretManager
argument_list|()
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getNMTokenSecretManager
argument_list|()
operator|.
name|rollMasterKey
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{   }
specifier|private
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
name|NodeManager
DECL|method|registerNode (String hostName, int containerManagerPort, int httpPort, String rackName, Resource capability)
name|registerNode
parameter_list|(
name|String
name|hostName
parameter_list|,
name|int
name|containerManagerPort
parameter_list|,
name|int
name|httpPort
parameter_list|,
name|String
name|rackName
parameter_list|,
name|Resource
name|capability
parameter_list|)
throws|throws
name|IOException
throws|,
name|YarnException
block|{
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
name|NodeManager
name|nm
init|=
operator|new
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
name|NodeManager
argument_list|(
name|hostName
argument_list|,
name|containerManagerPort
argument_list|,
name|httpPort
argument_list|,
name|rackName
argument_list|,
name|capability
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|NodeAddedSchedulerEvent
name|nodeAddEvent1
init|=
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm
operator|.
name|getNodeId
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|nodeAddEvent1
argument_list|)
expr_stmt|;
return|return
name|nm
return|;
block|}
annotation|@
name|Test
DECL|method|testResourceAllocation ()
specifier|public
name|void
name|testResourceAllocation
parameter_list|()
throws|throws
name|IOException
throws|,
name|YarnException
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"--- START: testResourceAllocation ---"
argument_list|)
expr_stmt|;
specifier|final
name|int
name|memory
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
specifier|final
name|int
name|vcores
init|=
literal|4
decl_stmt|;
comment|// Register node1
name|String
name|host1
init|=
literal|"host1"
decl_stmt|;
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
name|NodeManager
name|nm1
init|=
name|registerNode
argument_list|(
name|host1
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
argument_list|,
name|vcores
argument_list|)
argument_list|)
decl_stmt|;
comment|// Register node2
name|String
name|host2
init|=
literal|"host2"
decl_stmt|;
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
name|NodeManager
name|nm2
init|=
name|registerNode
argument_list|(
name|host2
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
operator|/
literal|2
argument_list|,
name|vcores
operator|/
literal|2
argument_list|)
argument_list|)
decl_stmt|;
comment|// Submit an application
name|Application
name|application
init|=
operator|new
name|Application
argument_list|(
literal|"user1"
argument_list|,
name|resourceManager
argument_list|)
decl_stmt|;
name|application
operator|.
name|submit
argument_list|()
expr_stmt|;
name|application
operator|.
name|addNodeManager
argument_list|(
name|host1
argument_list|,
literal|1234
argument_list|,
name|nm1
argument_list|)
expr_stmt|;
name|application
operator|.
name|addNodeManager
argument_list|(
name|host2
argument_list|,
literal|1234
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
comment|// Application resource requirements
specifier|final
name|int
name|memory1
init|=
literal|1024
decl_stmt|;
name|Resource
name|capability1
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory1
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Priority
name|priority1
init|=
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
name|resource
operator|.
name|Priority
operator|.
name|create
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|application
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority1
argument_list|,
name|capability1
argument_list|)
expr_stmt|;
name|Task
name|t1
init|=
operator|new
name|Task
argument_list|(
name|application
argument_list|,
name|priority1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host1
block|,
name|host2
block|}
argument_list|)
decl_stmt|;
name|application
operator|.
name|addTask
argument_list|(
name|t1
argument_list|)
expr_stmt|;
specifier|final
name|int
name|memory2
init|=
literal|2048
decl_stmt|;
name|Resource
name|capability2
init|=
name|Resources
operator|.
name|createResource
argument_list|(
name|memory2
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|Priority
name|priority0
init|=
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
name|resource
operator|.
name|Priority
operator|.
name|create
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// higher
name|application
operator|.
name|addResourceRequestSpec
argument_list|(
name|priority0
argument_list|,
name|capability2
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|nodeUpdate
argument_list|(
name|nm1
argument_list|)
expr_stmt|;
comment|// Get allocations from the scheduler
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Adding new tasks..."
argument_list|)
expr_stmt|;
name|Task
name|t2
init|=
operator|new
name|Task
argument_list|(
name|application
argument_list|,
name|priority1
argument_list|,
operator|new
name|String
index|[]
block|{
name|host1
block|,
name|host2
block|}
argument_list|)
decl_stmt|;
name|application
operator|.
name|addTask
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|Task
name|t3
init|=
operator|new
name|Task
argument_list|(
name|application
argument_list|,
name|priority0
argument_list|,
operator|new
name|String
index|[]
block|{
name|ResourceRequest
operator|.
name|ANY
block|}
argument_list|)
decl_stmt|;
name|application
operator|.
name|addTask
argument_list|(
name|t3
argument_list|)
expr_stmt|;
comment|// Send resource requests to the scheduler
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
comment|// Send heartbeats to kick the tires on the Scheduler
name|nodeUpdate
argument_list|(
name|nm2
argument_list|)
expr_stmt|;
name|nodeUpdate
argument_list|(
name|nm2
argument_list|)
expr_stmt|;
name|nodeUpdate
argument_list|(
name|nm1
argument_list|)
expr_stmt|;
name|nodeUpdate
argument_list|(
name|nm1
argument_list|)
expr_stmt|;
comment|// Get allocations from the scheduler
name|LOG
operator|.
name|info
argument_list|(
literal|"Trying to allocate..."
argument_list|)
expr_stmt|;
name|application
operator|.
name|schedule
argument_list|()
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
comment|// Complete tasks
name|LOG
operator|.
name|info
argument_list|(
literal|"Finishing up tasks..."
argument_list|)
expr_stmt|;
name|application
operator|.
name|finishTask
argument_list|(
name|t1
argument_list|)
expr_stmt|;
name|application
operator|.
name|finishTask
argument_list|(
name|t2
argument_list|)
expr_stmt|;
name|application
operator|.
name|finishTask
argument_list|(
name|t3
argument_list|)
expr_stmt|;
comment|// Notify scheduler application is finished.
name|AppAttemptRemovedSchedulerEvent
name|appRemovedEvent1
init|=
operator|new
name|AppAttemptRemovedSchedulerEvent
argument_list|(
name|application
operator|.
name|getApplicationAttemptId
argument_list|()
argument_list|,
name|RMAppAttemptState
operator|.
name|FINISHED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|appRemovedEvent1
argument_list|)
expr_stmt|;
name|checkResourceUsage
argument_list|(
name|nm1
argument_list|,
name|nm2
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"--- END: testResourceAllocation ---"
argument_list|)
expr_stmt|;
block|}
DECL|method|nodeUpdate ( org.apache.hadoop.yarn.server.resourcemanager.NodeManager nm1)
specifier|private
name|void
name|nodeUpdate
parameter_list|(
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
name|NodeManager
name|nm1
parameter_list|)
block|{
name|RMNode
name|node
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|get
argument_list|(
name|nm1
operator|.
name|getNodeId
argument_list|()
argument_list|)
decl_stmt|;
comment|// Send a heartbeat to kick the tires on the Scheduler
name|NodeUpdateSchedulerEvent
name|nodeUpdate
init|=
operator|new
name|NodeUpdateSchedulerEvent
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|resourceManager
operator|.
name|getResourceScheduler
argument_list|()
operator|.
name|handle
argument_list|(
name|nodeUpdate
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNodeHealthReportIsNotNull ()
specifier|public
name|void
name|testNodeHealthReportIsNotNull
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|host1
init|=
literal|"host1"
decl_stmt|;
specifier|final
name|int
name|memory
init|=
literal|4
operator|*
literal|1024
decl_stmt|;
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
name|NodeManager
name|nm1
init|=
name|registerNode
argument_list|(
name|host1
argument_list|,
literal|1234
argument_list|,
literal|2345
argument_list|,
name|NetworkTopology
operator|.
name|DEFAULT_RACK
argument_list|,
name|Resources
operator|.
name|createResource
argument_list|(
name|memory
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|nm1
operator|.
name|heartbeat
argument_list|()
expr_stmt|;
name|Collection
argument_list|<
name|RMNode
argument_list|>
name|values
init|=
name|resourceManager
operator|.
name|getRMContext
argument_list|()
operator|.
name|getRMNodes
argument_list|()
operator|.
name|values
argument_list|()
decl_stmt|;
for|for
control|(
name|RMNode
name|ni
range|:
name|values
control|)
block|{
name|assertNotNull
argument_list|(
name|ni
operator|.
name|getHealthReport
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|checkResourceUsage ( org.apache.hadoop.yarn.server.resourcemanager.NodeManager... nodes )
specifier|private
name|void
name|checkResourceUsage
parameter_list|(
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
name|NodeManager
modifier|...
name|nodes
parameter_list|)
block|{
for|for
control|(
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
name|NodeManager
name|nodeManager
range|:
name|nodes
control|)
block|{
name|nodeManager
operator|.
name|checkResourceUsage
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testResourceManagerInitConfigValidation ()
specifier|public
name|void
name|testResourceManagerInitConfigValidation
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|YarnConfiguration
operator|.
name|RM_AM_MAX_ATTEMPTS
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Exception is expected because the global max attempts"
operator|+
literal|" is negative."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
comment|// Exception is expected.
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Invalid global max attempts configuration"
argument_list|)
condition|)
throw|throw
name|e
throw|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNMExpiryAndHeartbeatIntervalsValidation ()
specifier|public
name|void
name|testNMExpiryAndHeartbeatIntervalsValidation
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NM_EXPIRY_INTERVAL_MS
argument_list|,
literal|1000
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|YarnConfiguration
operator|.
name|RM_NM_HEARTBEAT_INTERVAL_MS
argument_list|,
literal|1001
argument_list|)
expr_stmt|;
try|try
block|{
name|resourceManager
operator|=
operator|new
name|MockRM
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|YarnRuntimeException
name|e
parameter_list|)
block|{
comment|// Exception is expected.
if|if
condition|(
operator|!
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"Nodemanager expiry interval should be no"
operator|+
literal|" less than heartbeat interval"
argument_list|)
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|50000
argument_list|)
DECL|method|testFilterOverrides ()
specifier|public
name|void
name|testFilterOverrides
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|filterInitializerConfKey
init|=
literal|"hadoop.http.filter.initializers"
decl_stmt|;
name|String
index|[]
name|filterInitializers
init|=
block|{
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|", "
operator|+
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
name|AuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|", "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|String
name|filterInitializer
range|:
name|filterInitializers
control|)
block|{
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|()
block|{
annotation|@
name|Override
specifier|protected
name|void
name|doSecureLogin
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Skip the login.
block|}
block|}
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|filterInitializerConfKey
argument_list|,
name|filterInitializer
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.security.authentication"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"hadoop.http.authentication.type"
argument_list|,
literal|"kerberos"
argument_list|)
expr_stmt|;
try|try
block|{
try|try
block|{
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// ignore we just care about getting true for
comment|// isSecurityEnabled()
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|)
expr_stmt|;
block|}
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|startWepApp
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Exceptions are expected because we didn't setup everything
comment|// just want to test filter settings
name|String
name|tmp
init|=
name|resourceManager
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|filterInitializerConfKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterInitializer
operator|.
name|contains
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
name|resourceManager
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|// simple mode overrides
name|String
index|[]
name|simpleFilterInitializers
init|=
block|{
literal|""
block|,
name|StaticUserWebFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
block|}
decl_stmt|;
for|for
control|(
name|String
name|filterInitializer
range|:
name|simpleFilterInitializers
control|)
block|{
name|resourceManager
operator|=
operator|new
name|ResourceManager
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|filterInitializerConfKey
argument_list|,
name|filterInitializer
argument_list|)
expr_stmt|;
try|try
block|{
name|UserGroupInformation
operator|.
name|setConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|resourceManager
operator|.
name|startWepApp
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
comment|// Exceptions are expected because we didn't setup everything
comment|// just want to test filter settings
name|String
name|tmp
init|=
name|resourceManager
operator|.
name|getConfig
argument_list|()
operator|.
name|get
argument_list|(
name|filterInitializerConfKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|filterInitializer
operator|.
name|equals
argument_list|(
name|StaticUserWebFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|","
operator|+
name|StaticUserWebFilter
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|RMAuthenticationFilterInitializer
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|tmp
argument_list|)
expr_stmt|;
block|}
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

