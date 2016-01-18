begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|mockito
operator|.
name|Mockito
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
name|spy
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
name|times
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
name|verify
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
name|server
operator|.
name|resourcemanager
operator|.
name|ResourceManager
operator|.
name|SchedulerEventDispatcher
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
name|RMContainer
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
name|event
operator|.
name|ContainerPreemptEvent
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
DECL|class|TestRMDispatcher
specifier|public
class|class
name|TestRMDispatcher
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testSchedulerEventDispatcherForPreemptionEvents ()
specifier|public
name|void
name|testSchedulerEventDispatcherForPreemptionEvents
parameter_list|()
block|{
name|AsyncDispatcher
name|rmDispatcher
init|=
operator|new
name|AsyncDispatcher
argument_list|()
decl_stmt|;
name|CapacityScheduler
name|sched
init|=
name|spy
argument_list|(
operator|new
name|CapacityScheduler
argument_list|()
argument_list|)
decl_stmt|;
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|SchedulerEventDispatcher
name|schedulerDispatcher
init|=
operator|new
name|SchedulerEventDispatcher
argument_list|(
name|sched
argument_list|)
decl_stmt|;
name|rmDispatcher
operator|.
name|register
argument_list|(
name|SchedulerEventType
operator|.
name|class
argument_list|,
name|schedulerDispatcher
argument_list|)
expr_stmt|;
name|rmDispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rmDispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
name|schedulerDispatcher
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|schedulerDispatcher
operator|.
name|start
argument_list|()
expr_stmt|;
try|try
block|{
name|ApplicationAttemptId
name|appAttemptId
init|=
name|mock
argument_list|(
name|ApplicationAttemptId
operator|.
name|class
argument_list|)
decl_stmt|;
name|RMContainer
name|container
init|=
name|mock
argument_list|(
name|RMContainer
operator|.
name|class
argument_list|)
decl_stmt|;
name|ContainerPreemptEvent
name|event1
init|=
operator|new
name|ContainerPreemptEvent
argument_list|(
name|appAttemptId
argument_list|,
name|container
argument_list|,
name|SchedulerEventType
operator|.
name|KILL_RESERVED_CONTAINER
argument_list|)
decl_stmt|;
name|rmDispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event1
argument_list|)
expr_stmt|;
name|ContainerPreemptEvent
name|event2
init|=
operator|new
name|ContainerPreemptEvent
argument_list|(
name|appAttemptId
argument_list|,
name|container
argument_list|,
name|SchedulerEventType
operator|.
name|KILL_PREEMPTED_CONTAINER
argument_list|)
decl_stmt|;
name|rmDispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event2
argument_list|)
expr_stmt|;
name|ContainerPreemptEvent
name|event3
init|=
operator|new
name|ContainerPreemptEvent
argument_list|(
name|appAttemptId
argument_list|,
name|container
argument_list|,
name|SchedulerEventType
operator|.
name|PREEMPT_CONTAINER
argument_list|)
decl_stmt|;
name|rmDispatcher
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
name|event3
argument_list|)
expr_stmt|;
comment|// Wait for events to be processed by scheduler dispatcher.
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sched
argument_list|,
name|times
argument_list|(
literal|3
argument_list|)
argument_list|)
operator|.
name|handle
argument_list|(
name|any
argument_list|(
name|SchedulerEvent
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sched
argument_list|)
operator|.
name|killReservedContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sched
argument_list|)
operator|.
name|preemptContainer
argument_list|(
name|appAttemptId
argument_list|,
name|container
argument_list|)
expr_stmt|;
name|verify
argument_list|(
name|sched
argument_list|)
operator|.
name|killPreemptedContainer
argument_list|(
name|container
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|schedulerDispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
name|rmDispatcher
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

