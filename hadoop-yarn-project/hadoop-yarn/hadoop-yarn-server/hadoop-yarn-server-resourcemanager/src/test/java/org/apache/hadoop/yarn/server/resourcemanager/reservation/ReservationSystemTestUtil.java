begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*******************************************************************************  *   Licensed to the Apache Software Foundation (ASF) under one  *   or more contributor license agreements.  See the NOTICE file  *   distributed with this work for additional information  *   regarding copyright ownership.  The ASF licenses this file  *   to you under the Apache License, Version 2.0 (the  *   "License"); you may not use this file except in compliance  *   with the License.  You may obtain a copy of the License at  *    *       http://www.apache.org/licenses/LICENSE-2.0  *    *   Unless required by applicable law or agreed to in writing, software  *   distributed under the License is distributed on an "AS IS" BASIS,  *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *   See the License for the specific language governing permissions and  *   limitations under the License.  *******************************************************************************/
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.reservation
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
name|reservation
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Matchers
operator|.
name|anySetOf
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|doReturn
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
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
name|PrintWriter
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
import|import
name|java
operator|.
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|ReservationDefinition
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
name|ReservationId
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
name|ReservationRequest
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
name|ReservationRequestInterpreter
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
name|ReservationRequests
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
name|impl
operator|.
name|pb
operator|.
name|ReservationDefinitionPBImpl
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
name|impl
operator|.
name|pb
operator|.
name|ReservationRequestsPBImpl
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
name|server
operator|.
name|resourcemanager
operator|.
name|MockNodes
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
name|RMContext
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
name|RMContextImpl
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
name|nodelabels
operator|.
name|RMNodeLabelsManager
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
name|reservation
operator|.
name|planning
operator|.
name|AlignedPlannerWithGreedy
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
name|AbstractYarnScheduler
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
name|CapacityScheduler
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
name|CapacitySchedulerConfiguration
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
name|fair
operator|.
name|FairScheduler
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
name|security
operator|.
name|ClientToAMTokenSecretManagerInRM
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
name|security
operator|.
name|NMTokenSecretManagerInRM
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
name|security
operator|.
name|RMContainerTokenSecretManager
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|invocation
operator|.
name|InvocationOnMock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|stubbing
operator|.
name|Answer
import|;
end_import

begin_class
DECL|class|ReservationSystemTestUtil
specifier|public
class|class
name|ReservationSystemTestUtil
block|{
DECL|field|rand
specifier|private
specifier|static
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|field|reservationQ
specifier|public
specifier|final
specifier|static
name|String
name|reservationQ
init|=
literal|"dedicated"
decl_stmt|;
DECL|method|getNewReservationId ()
specifier|public
specifier|static
name|ReservationId
name|getNewReservationId
parameter_list|()
block|{
return|return
name|ReservationId
operator|.
name|newInstance
argument_list|(
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|,
name|rand
operator|.
name|nextLong
argument_list|()
argument_list|)
return|;
block|}
DECL|method|createConf ( String reservationQ, long timeWindow, float instConstraint, float avgConstraint)
specifier|public
specifier|static
name|ReservationSchedulerConfiguration
name|createConf
parameter_list|(
name|String
name|reservationQ
parameter_list|,
name|long
name|timeWindow
parameter_list|,
name|float
name|instConstraint
parameter_list|,
name|float
name|avgConstraint
parameter_list|)
block|{
name|ReservationSchedulerConfiguration
name|conf
init|=
name|mock
argument_list|(
name|ReservationSchedulerConfiguration
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getReservationWindow
argument_list|(
name|reservationQ
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|timeWindow
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getInstantaneousMaxCapacity
argument_list|(
name|reservationQ
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|instConstraint
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getAverageCapacity
argument_list|(
name|reservationQ
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|avgConstraint
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|validateReservationQueue ( AbstractReservationSystem reservationSystem, String planQName)
specifier|public
specifier|static
name|void
name|validateReservationQueue
parameter_list|(
name|AbstractReservationSystem
name|reservationSystem
parameter_list|,
name|String
name|planQName
parameter_list|)
block|{
name|Plan
name|plan
init|=
name|reservationSystem
operator|.
name|getPlan
argument_list|(
name|planQName
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|plan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|instanceof
name|InMemoryPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|planQName
argument_list|,
name|plan
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|8192
argument_list|,
name|plan
operator|.
name|getTotalCapacity
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|.
name|getReservationAgent
argument_list|()
operator|instanceof
name|AlignedPlannerWithGreedy
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|plan
operator|.
name|getSharingPolicy
argument_list|()
operator|instanceof
name|CapacityOverTimePolicy
argument_list|)
expr_stmt|;
block|}
DECL|method|validateNewReservationQueue ( AbstractReservationSystem reservationSystem, String newQ)
specifier|public
specifier|static
name|void
name|validateNewReservationQueue
parameter_list|(
name|AbstractReservationSystem
name|reservationSystem
parameter_list|,
name|String
name|newQ
parameter_list|)
block|{
name|Plan
name|newPlan
init|=
name|reservationSystem
operator|.
name|getPlan
argument_list|(
name|newQ
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|newPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newPlan
operator|instanceof
name|InMemoryPlan
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|newQ
argument_list|,
name|newPlan
operator|.
name|getQueueName
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|1024
argument_list|,
name|newPlan
operator|.
name|getTotalCapacity
argument_list|()
operator|.
name|getMemory
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newPlan
operator|.
name|getReservationAgent
argument_list|()
operator|instanceof
name|AlignedPlannerWithGreedy
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|newPlan
operator|.
name|getSharingPolicy
argument_list|()
operator|instanceof
name|CapacityOverTimePolicy
argument_list|)
expr_stmt|;
block|}
DECL|method|setupFSAllocationFile (String allocationFile)
specifier|public
specifier|static
name|void
name|setupFSAllocationFile
parameter_list|(
name|String
name|allocationFile
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|allocationFile
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"default\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>1</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"a\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>1</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"a1\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>3</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"a2\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>7</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"dedicated\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<reservation></reservation>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>8</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<defaultQueueSchedulingPolicy>drf</defaultQueueSchedulingPolicy>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|updateFSAllocationFile (String allocationFile)
specifier|public
specifier|static
name|void
name|updateFSAllocationFile
parameter_list|(
name|String
name|allocationFile
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintWriter
name|out
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|allocationFile
argument_list|)
argument_list|)
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"default\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>5</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"a\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>5</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"a1\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>3</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"a2\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>7</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"dedicated\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<reservation></reservation>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>80</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<queue name=\"reservation\">"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<reservation></reservation>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<weight>10</weight>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</queue>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<defaultQueueSchedulingPolicy>drf</defaultQueueSchedulingPolicy>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"</allocations>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|setupFairScheduler ( ReservationSystemTestUtil testUtil, RMContext rmContext, Configuration conf, int numContainers)
specifier|public
specifier|static
name|FairScheduler
name|setupFairScheduler
parameter_list|(
name|ReservationSystemTestUtil
name|testUtil
parameter_list|,
name|RMContext
name|rmContext
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|numContainers
parameter_list|)
throws|throws
name|IOException
block|{
name|FairScheduler
name|scheduler
init|=
operator|new
name|FairScheduler
argument_list|()
decl_stmt|;
name|scheduler
operator|.
name|setRMContext
argument_list|(
name|rmContext
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|rmContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|scheduler
operator|.
name|start
argument_list|()
expr_stmt|;
name|scheduler
operator|.
name|reinitialize
argument_list|(
name|conf
argument_list|,
name|rmContext
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|testUtil
operator|.
name|calculateClusterResource
argument_list|(
name|numContainers
argument_list|)
decl_stmt|;
name|RMNode
name|node1
init|=
name|MockNodes
operator|.
name|newNodeInfo
argument_list|(
literal|1
argument_list|,
name|resource
argument_list|,
literal|1
argument_list|,
literal|"127.0.0.1"
argument_list|)
decl_stmt|;
name|NodeAddedSchedulerEvent
name|nodeEvent1
init|=
operator|new
name|NodeAddedSchedulerEvent
argument_list|(
name|node1
argument_list|)
decl_stmt|;
name|scheduler
operator|.
name|handle
argument_list|(
name|nodeEvent1
argument_list|)
expr_stmt|;
return|return
name|scheduler
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|mockCapacityScheduler (int numContainers)
specifier|public
name|CapacityScheduler
name|mockCapacityScheduler
parameter_list|(
name|int
name|numContainers
parameter_list|)
throws|throws
name|IOException
block|{
comment|// stolen from TestCapacityScheduler
name|CapacitySchedulerConfiguration
name|conf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|setupQueueConfiguration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|CapacityScheduler
name|cs
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|CapacityScheduler
argument_list|()
argument_list|)
decl_stmt|;
name|cs
operator|.
name|setConf
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
argument_list|)
expr_stmt|;
name|RMContext
name|mockRmContext
init|=
name|createRMContext
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|cs
operator|.
name|setRMContext
argument_list|(
name|mockRmContext
argument_list|)
expr_stmt|;
try|try
block|{
name|cs
operator|.
name|serviceInit
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
name|Assert
operator|.
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|initializeRMContext
argument_list|(
name|numContainers
argument_list|,
name|cs
argument_list|,
name|mockRmContext
argument_list|)
expr_stmt|;
return|return
name|cs
return|;
block|}
DECL|method|initializeRMContext (int numContainers, AbstractYarnScheduler scheduler, RMContext mockRMContext)
specifier|public
specifier|static
name|void
name|initializeRMContext
parameter_list|(
name|int
name|numContainers
parameter_list|,
name|AbstractYarnScheduler
name|scheduler
parameter_list|,
name|RMContext
name|mockRMContext
parameter_list|)
block|{
name|when
argument_list|(
name|mockRMContext
operator|.
name|getScheduler
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|scheduler
argument_list|)
expr_stmt|;
name|Resource
name|r
init|=
name|calculateClusterResource
argument_list|(
name|numContainers
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|r
argument_list|)
operator|.
name|when
argument_list|(
name|scheduler
argument_list|)
operator|.
name|getClusterResource
argument_list|()
expr_stmt|;
block|}
DECL|method|createRMContext (Configuration conf)
specifier|public
specifier|static
name|RMContext
name|createRMContext
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|RMContext
name|mockRmContext
init|=
name|Mockito
operator|.
name|spy
argument_list|(
operator|new
name|RMContextImpl
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|RMContainerTokenSecretManager
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|NMTokenSecretManagerInRM
argument_list|(
name|conf
argument_list|)
argument_list|,
operator|new
name|ClientToAMTokenSecretManagerInRM
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|RMNodeLabelsManager
name|nlm
init|=
name|mock
argument_list|(
name|RMNodeLabelsManager
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|nlm
operator|.
name|getQueueResource
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|anySetOf
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Resource
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resource
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
return|return
operator|(
name|Resource
operator|)
name|args
index|[
literal|2
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|nlm
operator|.
name|getResourceByLabel
argument_list|(
name|any
argument_list|(
name|String
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|Resource
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
operator|new
name|Answer
argument_list|<
name|Resource
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Resource
name|answer
parameter_list|(
name|InvocationOnMock
name|invocation
parameter_list|)
throws|throws
name|Throwable
block|{
name|Object
index|[]
name|args
init|=
name|invocation
operator|.
name|getArguments
argument_list|()
decl_stmt|;
return|return
operator|(
name|Resource
operator|)
name|args
index|[
literal|1
index|]
return|;
block|}
block|}
argument_list|)
expr_stmt|;
name|mockRmContext
operator|.
name|setNodeLabelManager
argument_list|(
name|nlm
argument_list|)
expr_stmt|;
return|return
name|mockRmContext
return|;
block|}
DECL|method|setupQueueConfiguration (CapacitySchedulerConfiguration conf)
specifier|public
specifier|static
name|void
name|setupQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|)
block|{
comment|// Define default queue
specifier|final
name|String
name|defQ
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".default"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|defQ
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Define top-level queues
name|conf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"default"
block|,
literal|"a"
block|,
name|reservationQ
block|}
argument_list|)
expr_stmt|;
specifier|final
name|String
name|A
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|".a"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
literal|10
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dedicated
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
operator|+
name|reservationQ
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|dedicated
argument_list|,
literal|80
argument_list|)
expr_stmt|;
comment|// Set as reservation queue
name|conf
operator|.
name|setReservable
argument_list|(
name|dedicated
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Define 2nd-level queues
specifier|final
name|String
name|A1
init|=
name|A
operator|+
literal|".a1"
decl_stmt|;
specifier|final
name|String
name|A2
init|=
name|A
operator|+
literal|".a2"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|A
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A1
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A2
argument_list|,
literal|70
argument_list|)
expr_stmt|;
block|}
DECL|method|getFullReservationQueueName ()
specifier|public
name|String
name|getFullReservationQueueName
parameter_list|()
block|{
return|return
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
operator|+
name|reservationQ
return|;
block|}
DECL|method|getreservationQueueName ()
specifier|public
name|String
name|getreservationQueueName
parameter_list|()
block|{
return|return
name|reservationQ
return|;
block|}
DECL|method|updateQueueConfiguration (CapacitySchedulerConfiguration conf, String newQ)
specifier|public
name|void
name|updateQueueConfiguration
parameter_list|(
name|CapacitySchedulerConfiguration
name|conf
parameter_list|,
name|String
name|newQ
parameter_list|)
block|{
comment|// Define default queue
specifier|final
name|String
name|prefix
init|=
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
name|CapacitySchedulerConfiguration
operator|.
name|DOT
decl_stmt|;
specifier|final
name|String
name|defQ
init|=
name|prefix
operator|+
literal|"default"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|defQ
argument_list|,
literal|5
argument_list|)
expr_stmt|;
comment|// Define top-level queues
name|conf
operator|.
name|setQueues
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"default"
block|,
literal|"a"
block|,
name|reservationQ
block|,
name|newQ
block|}
argument_list|)
expr_stmt|;
specifier|final
name|String
name|A
init|=
name|prefix
operator|+
literal|"a"
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A
argument_list|,
literal|5
argument_list|)
expr_stmt|;
specifier|final
name|String
name|dedicated
init|=
name|prefix
operator|+
name|reservationQ
decl_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|dedicated
argument_list|,
literal|80
argument_list|)
expr_stmt|;
comment|// Set as reservation queue
name|conf
operator|.
name|setReservable
argument_list|(
name|dedicated
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|prefix
operator|+
name|newQ
argument_list|,
literal|10
argument_list|)
expr_stmt|;
comment|// Set as reservation queue
name|conf
operator|.
name|setReservable
argument_list|(
name|prefix
operator|+
name|newQ
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// Define 2nd-level queues
specifier|final
name|String
name|A1
init|=
name|A
operator|+
literal|".a1"
decl_stmt|;
specifier|final
name|String
name|A2
init|=
name|A
operator|+
literal|".a2"
decl_stmt|;
name|conf
operator|.
name|setQueues
argument_list|(
name|A
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"a1"
block|,
literal|"a2"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A1
argument_list|,
literal|30
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCapacity
argument_list|(
name|A2
argument_list|,
literal|70
argument_list|)
expr_stmt|;
block|}
DECL|method|generateRandomRR (Random rand, long i)
specifier|public
specifier|static
name|ReservationDefinition
name|generateRandomRR
parameter_list|(
name|Random
name|rand
parameter_list|,
name|long
name|i
parameter_list|)
block|{
name|rand
operator|.
name|setSeed
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// start time at random in the next 12 hours
name|long
name|arrival
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|12
operator|*
literal|3600
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// deadline at random in the next day
name|long
name|deadline
init|=
name|arrival
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|24
operator|*
literal|3600
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// create a request with a single atomic ask
name|ReservationDefinition
name|rr
init|=
operator|new
name|ReservationDefinitionPBImpl
argument_list|()
decl_stmt|;
name|rr
operator|.
name|setArrival
argument_list|(
name|now
operator|+
name|arrival
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setDeadline
argument_list|(
name|now
operator|+
name|deadline
argument_list|)
expr_stmt|;
name|int
name|gang
init|=
literal|1
operator|+
name|rand
operator|.
name|nextInt
argument_list|(
literal|9
argument_list|)
decl_stmt|;
name|int
name|par
init|=
operator|(
name|rand
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|+
literal|1
operator|)
operator|*
name|gang
decl_stmt|;
name|long
name|dur
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|2
operator|*
literal|3600
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// random duration within 2h
name|ReservationRequest
name|r
init|=
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|par
argument_list|,
name|gang
argument_list|,
name|dur
argument_list|)
decl_stmt|;
name|ReservationRequests
name|reqs
init|=
operator|new
name|ReservationRequestsPBImpl
argument_list|()
decl_stmt|;
name|reqs
operator|.
name|setReservationResources
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|rand
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ReservationRequestInterpreter
index|[]
name|type
init|=
name|ReservationRequestInterpreter
operator|.
name|values
argument_list|()
decl_stmt|;
name|reqs
operator|.
name|setInterpreter
argument_list|(
name|type
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|type
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setReservationRequests
argument_list|(
name|reqs
argument_list|)
expr_stmt|;
return|return
name|rr
return|;
block|}
DECL|method|generateBigRR (Random rand, long i)
specifier|public
specifier|static
name|ReservationDefinition
name|generateBigRR
parameter_list|(
name|Random
name|rand
parameter_list|,
name|long
name|i
parameter_list|)
block|{
name|rand
operator|.
name|setSeed
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// start time at random in the next 2 hours
name|long
name|arrival
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|2
operator|*
literal|3600
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// deadline at random in the next day
name|long
name|deadline
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|24
operator|*
literal|3600
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// create a request with a single atomic ask
name|ReservationDefinition
name|rr
init|=
operator|new
name|ReservationDefinitionPBImpl
argument_list|()
decl_stmt|;
name|rr
operator|.
name|setArrival
argument_list|(
name|now
operator|+
name|arrival
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setDeadline
argument_list|(
name|now
operator|+
name|deadline
argument_list|)
expr_stmt|;
name|int
name|gang
init|=
literal|1
decl_stmt|;
name|int
name|par
init|=
literal|100000
decl_stmt|;
comment|// 100k tasks
name|long
name|dur
init|=
name|rand
operator|.
name|nextInt
argument_list|(
literal|60
operator|*
literal|1000
argument_list|)
decl_stmt|;
comment|// 1min tasks
name|ReservationRequest
name|r
init|=
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|par
argument_list|,
name|gang
argument_list|,
name|dur
argument_list|)
decl_stmt|;
name|ReservationRequests
name|reqs
init|=
operator|new
name|ReservationRequestsPBImpl
argument_list|()
decl_stmt|;
name|reqs
operator|.
name|setReservationResources
argument_list|(
name|Collections
operator|.
name|singletonList
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
name|rand
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
name|ReservationRequestInterpreter
index|[]
name|type
init|=
name|ReservationRequestInterpreter
operator|.
name|values
argument_list|()
decl_stmt|;
name|reqs
operator|.
name|setInterpreter
argument_list|(
name|type
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|type
operator|.
name|length
argument_list|)
index|]
argument_list|)
expr_stmt|;
name|rr
operator|.
name|setReservationRequests
argument_list|(
name|reqs
argument_list|)
expr_stmt|;
return|return
name|rr
return|;
block|}
DECL|method|generateAllocation ( long startTime, long step, int[] alloc)
specifier|public
specifier|static
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|generateAllocation
parameter_list|(
name|long
name|startTime
parameter_list|,
name|long
name|step
parameter_list|,
name|int
index|[]
name|alloc
parameter_list|)
block|{
name|Map
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
name|req
init|=
operator|new
name|TreeMap
argument_list|<
name|ReservationInterval
argument_list|,
name|Resource
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|alloc
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|req
operator|.
name|put
argument_list|(
operator|new
name|ReservationInterval
argument_list|(
name|startTime
operator|+
name|i
operator|*
name|step
argument_list|,
name|startTime
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
operator|*
name|step
argument_list|)
argument_list|,
name|ReservationSystemUtil
operator|.
name|toResource
argument_list|(
name|ReservationRequest
operator|.
name|newInstance
argument_list|(
name|Resource
operator|.
name|newInstance
argument_list|(
literal|1024
argument_list|,
literal|1
argument_list|)
argument_list|,
name|alloc
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|req
return|;
block|}
DECL|method|calculateClusterResource (int numContainers)
specifier|public
specifier|static
name|Resource
name|calculateClusterResource
parameter_list|(
name|int
name|numContainers
parameter_list|)
block|{
name|Resource
name|clusterResource
init|=
name|Resource
operator|.
name|newInstance
argument_list|(
name|numContainers
operator|*
literal|1024
argument_list|,
name|numContainers
argument_list|)
decl_stmt|;
return|return
name|clusterResource
return|;
block|}
block|}
end_class

end_unit

