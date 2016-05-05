begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity
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
operator|.
name|monitor
operator|.
name|capacity
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
name|server
operator|.
name|resourcemanager
operator|.
name|scheduler
operator|.
name|SchedulerNode
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
name|capacity
operator|.
name|LeafQueue
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
name|common
operator|.
name|fica
operator|.
name|FiCaSchedulerApp
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
name|Test
import|;
end_import

begin_class
DECL|class|TestProportionalCapacityPreemptionPolicyMockFramework
specifier|public
class|class
name|TestProportionalCapacityPreemptionPolicyMockFramework
extends|extends
name|ProportionalCapacityPreemptionPolicyMockFramework
block|{
annotation|@
name|Test
DECL|method|testBuilder ()
specifier|public
name|void
name|testBuilder
parameter_list|()
throws|throws
name|Exception
block|{
comment|/**      * Test of test, make sure we build expected mock schedulable objects      */
name|String
name|labelsConfig
init|=
literal|"=200,true;"
operator|+
comment|// default partition
literal|"red=100,false;"
operator|+
comment|// partition=red
literal|"blue=200,true"
decl_stmt|;
comment|// partition=blue
name|String
name|nodesConfig
init|=
literal|"n1=red;"
operator|+
comment|// n1 has partition=red
literal|"n2=blue;"
operator|+
comment|// n2 has partition=blue
literal|"n3="
decl_stmt|;
comment|// n3 doesn't have partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[200 200 100 100],red=[100 100 100 100],blue=[200 200 200 200]);"
operator|+
comment|//root
literal|"-a(=[100 200 100 100],red=[0 0 0 0],blue=[200 200 200 200]);"
operator|+
comment|// a
literal|"--a1(=[50 100 50 100],red=[0 0 0 0],blue=[100 200 200 0]);"
operator|+
comment|// a1
literal|"--a2(=[50 200 50 0],red=[0 0 0 0],blue=[100 200 0 200]);"
operator|+
comment|// a2
literal|"-b(=[100 200 0 0],red=[100 100 100 100],blue=[0 0 0 0])"
decl_stmt|;
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
comment|// app1 in a1, , 50 in n2 (reserved), 50 in n2 (allocated)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,1,n3,red,50,false);"
operator|+
comment|// 50 * default in n3
literal|"a1\t"
comment|// app2 in a1
operator|+
literal|"(2,1,n2,,50,true)(2,1,n2,,50,false)"
comment|// 50 * ignore-exclusivity (reserved),
comment|// 50 * ignore-exclusivity (allocated)
operator|+
literal|"(2,1,n2,blue,50,true)(2,1,n2,blue,50,true);"
operator|+
comment|// 50 in n2 (reserved),
comment|// 50 in n2 (allocated)
literal|"a2\t"
comment|// app3 in a2
operator|+
literal|"(1,1,n3,red,50,false);"
operator|+
comment|// 50 * default in n3
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n1,red,100,false);"
decl_stmt|;
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
comment|// Check queues:
comment|// root
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|// a
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0.5f
argument_list|,
literal|1f
argument_list|,
literal|0.5f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|// a1
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0.25f
argument_list|,
literal|0.5f
argument_list|,
literal|0.25f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|0.5f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// a2
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0.25f
argument_list|,
literal|1f
argument_list|,
literal|0.25f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|0.5f
argument_list|,
literal|1f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|200
argument_list|)
expr_stmt|;
comment|// b1
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0.5f
argument_list|,
literal|1f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|,
literal|1f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|checkAbsCapacities
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|,
literal|0f
argument_list|)
expr_stmt|;
name|checkPendingResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"blue"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|// Check ignored partitioned containers in queue
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
operator|(
operator|(
name|LeafQueue
operator|)
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
operator|)
operator|.
name|getIgnoreExclusivityRMContainers
argument_list|()
operator|.
name|get
argument_list|(
literal|"blue"
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check applications
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
operator|(
operator|(
name|LeafQueue
operator|)
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
operator|)
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|LeafQueue
operator|)
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a2"
argument_list|)
operator|)
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
operator|(
name|LeafQueue
operator|)
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
operator|)
operator|.
name|getApplications
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check #containers
name|FiCaSchedulerApp
name|app1
init|=
name|getApp
argument_list|(
literal|"a1"
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|FiCaSchedulerApp
name|app2
init|=
name|getApp
argument_list|(
literal|"a1"
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|FiCaSchedulerApp
name|app3
init|=
name|getApp
argument_list|(
literal|"a2"
argument_list|,
literal|3
argument_list|)
decl_stmt|;
name|FiCaSchedulerApp
name|app4
init|=
name|getApp
argument_list|(
literal|"b"
argument_list|,
literal|4
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|app1
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkContainerNodesInApp
argument_list|(
name|app1
argument_list|,
literal|50
argument_list|,
literal|"n3"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|app2
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|150
argument_list|,
name|app2
operator|.
name|getReservedContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkContainerNodesInApp
argument_list|(
name|app2
argument_list|,
literal|200
argument_list|,
literal|"n2"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|app3
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkContainerNodesInApp
argument_list|(
name|app3
argument_list|,
literal|50
argument_list|,
literal|"n3"
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|app4
operator|.
name|getLiveContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|checkContainerNodesInApp
argument_list|(
name|app4
argument_list|,
literal|100
argument_list|,
literal|"n1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuilderWithReservedResource ()
specifier|public
name|void
name|testBuilderWithReservedResource
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|labelsConfig
init|=
literal|"=200,true;"
operator|+
comment|// default partition
literal|"red=100,false;"
operator|+
comment|// partition=red
literal|"blue=200,true"
decl_stmt|;
comment|// partition=blue
name|String
name|nodesConfig
init|=
literal|"n1=red;"
operator|+
comment|// n1 has partition=red
literal|"n2=blue;"
operator|+
comment|// n2 has partition=blue
literal|"n3="
decl_stmt|;
comment|// n3 doesn't have partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[200 200 100 100 100],red=[100 100 100 100 90],blue=[200 200 200 200 80]);"
operator|+
comment|//root
literal|"-a(=[100 200 100 100 50],red=[0 0 0 0 40],blue=[200 200 200 200 30]);"
operator|+
comment|// a
literal|"--a1(=[50 100 50 100 40],red=[0 0 0 0 20],blue=[100 200 200 0]);"
operator|+
comment|// a1
literal|"--a2(=[50 200 50 0 10],red=[0 0 0 0 20],blue=[100 200 0 200]);"
operator|+
comment|// a2
literal|"-b(=[100 200 0 0],red=[100 100 100 100],blue=[0 0 0 0])"
decl_stmt|;
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
comment|// app1 in a1, , 50 in n2 (reserved), 50 in n2 (allocated)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,1,n3,red,50,false);"
operator|+
comment|// 50 * default in n3
literal|"a1\t"
comment|// app2 in a1
operator|+
literal|"(2,1,n2,,50,true)(2,1,n2,,50,false)"
comment|// 50 * ignore-exclusivity (reserved),
comment|// 50 * ignore-exclusivity (allocated)
operator|+
literal|"(2,1,n2,blue,50,true)(2,1,n2,blue,50,true);"
operator|+
comment|// 50 in n2 (reserved),
comment|// 50 in n2 (allocated)
literal|"a2\t"
comment|// app3 in a2
operator|+
literal|"(1,1,n3,red,50,false);"
operator|+
comment|// 50 * default in n3
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n1,red,100,false);"
decl_stmt|;
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
comment|// Check queues:
comment|// root
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"root"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|90
argument_list|)
expr_stmt|;
comment|// a
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|50
argument_list|)
expr_stmt|;
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|40
argument_list|)
expr_stmt|;
comment|// a1
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|40
argument_list|)
expr_stmt|;
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"a1"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|20
argument_list|)
expr_stmt|;
comment|// b
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|""
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|checkReservedResource
argument_list|(
name|cs
operator|.
name|getQueue
argument_list|(
literal|"b"
argument_list|)
argument_list|,
literal|"red"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBuilderWithSpecifiedNodeResources ()
specifier|public
name|void
name|testBuilderWithSpecifiedNodeResources
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|labelsConfig
init|=
literal|"=200,true;"
operator|+
comment|// default partition
literal|"red=100,false;"
operator|+
comment|// partition=red
literal|"blue=200,true"
decl_stmt|;
comment|// partition=blue
name|String
name|nodesConfig
init|=
literal|"n1=red res=100;"
operator|+
comment|// n1 has partition=red
literal|"n2=blue;"
operator|+
comment|// n2 has partition=blue
literal|"n3= res=30"
decl_stmt|;
comment|// n3 doesn't have partition
name|String
name|queuesConfig
init|=
comment|// guaranteed,max,used,pending
literal|"root(=[200 200 100 100 100],red=[100 100 100 100 90],blue=[200 200 200 200 80]);"
operator|+
comment|//root
literal|"-a(=[100 200 100 100 50],red=[0 0 0 0 40],blue=[200 200 200 200 30]);"
operator|+
comment|// a
literal|"--a1(=[50 100 50 100 40],red=[0 0 0 0 20],blue=[100 200 200 0]);"
operator|+
comment|// a1
literal|"--a2(=[50 200 50 0 10],red=[0 0 0 0 20],blue=[100 200 0 200]);"
operator|+
comment|// a2
literal|"-b(=[100 200 0 0],red=[100 100 100 100],blue=[0 0 0 0])"
decl_stmt|;
name|String
name|appsConfig
init|=
comment|//queueName\t(priority,resource,host,expression,#repeat,reserved)
comment|// app1 in a1, , 50 in n2 (reserved), 50 in n2 (allocated)
literal|"a1\t"
comment|// app1 in a1
operator|+
literal|"(1,1,n3,red,50,false);"
operator|+
comment|// 50 * default in n3
literal|"a1\t"
comment|// app2 in a1
operator|+
literal|"(2,1,n2,,50,true)(2,1,n2,,50,false)"
comment|// 50 * ignore-exclusivity (reserved),
comment|// 50 * ignore-exclusivity (allocated)
operator|+
literal|"(2,1,n2,blue,50,true)(2,1,n2,blue,50,true);"
operator|+
comment|// 50 in n2 (reserved),
comment|// 50 in n2 (allocated)
literal|"a2\t"
comment|// app3 in a2
operator|+
literal|"(1,1,n3,red,50,false);"
operator|+
comment|// 50 * default in n3
literal|"b\t"
comment|// app4 in b
operator|+
literal|"(1,1,n1,red,100,false);"
decl_stmt|;
name|buildEnv
argument_list|(
name|labelsConfig
argument_list|,
name|nodesConfig
argument_list|,
name|queuesConfig
argument_list|,
name|appsConfig
argument_list|)
expr_stmt|;
comment|// Check host resources
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|this
operator|.
name|cs
operator|.
name|getAllNodes
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|SchedulerNode
name|node1
init|=
name|cs
operator|.
name|getSchedulerNode
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n1"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|node1
operator|.
name|getTotalResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|node1
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|node1
operator|.
name|getReservedContainer
argument_list|()
argument_list|)
expr_stmt|;
name|SchedulerNode
name|node2
init|=
name|cs
operator|.
name|getSchedulerNode
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n2"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|node2
operator|.
name|getTotalResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|50
argument_list|,
name|node2
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|node2
operator|.
name|getReservedContainer
argument_list|()
argument_list|)
expr_stmt|;
name|SchedulerNode
name|node3
init|=
name|cs
operator|.
name|getSchedulerNode
argument_list|(
name|NodeId
operator|.
name|newInstance
argument_list|(
literal|"n3"
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|30
argument_list|,
name|node3
operator|.
name|getTotalResource
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|node3
operator|.
name|getCopiedListOfRunningContainers
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertNull
argument_list|(
name|node3
operator|.
name|getReservedContainer
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

