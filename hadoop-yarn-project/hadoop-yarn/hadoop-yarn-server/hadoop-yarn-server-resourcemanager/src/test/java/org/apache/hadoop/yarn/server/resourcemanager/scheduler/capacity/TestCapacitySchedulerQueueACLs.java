begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity
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
name|scheduler
operator|.
name|capacity
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|security
operator|.
name|authorize
operator|.
name|AccessControlList
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
name|QueueACL
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
name|QueueACLsTestBase
import|;
end_import

begin_class
DECL|class|TestCapacitySchedulerQueueACLs
specifier|public
class|class
name|TestCapacitySchedulerQueueACLs
extends|extends
name|QueueACLsTestBase
block|{
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|CapacitySchedulerConfiguration
name|csConf
init|=
operator|new
name|CapacitySchedulerConfiguration
argument_list|()
decl_stmt|;
name|csConf
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
name|QUEUEA
block|,
name|QUEUEB
block|}
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
literal|50f
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setCapacity
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEB
argument_list|,
literal|50f
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|aclsOnQueueA
init|=
operator|new
name|HashMap
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
name|AccessControlList
name|submitACLonQueueA
init|=
operator|new
name|AccessControlList
argument_list|(
name|QUEUE_A_USER
argument_list|)
decl_stmt|;
name|submitACLonQueueA
operator|.
name|addUser
argument_list|(
name|COMMON_USER
argument_list|)
expr_stmt|;
name|AccessControlList
name|adminACLonQueueA
init|=
operator|new
name|AccessControlList
argument_list|(
name|QUEUE_A_ADMIN
argument_list|)
decl_stmt|;
name|aclsOnQueueA
operator|.
name|put
argument_list|(
name|QueueACL
operator|.
name|SUBMIT_APPLICATIONS
argument_list|,
name|submitACLonQueueA
argument_list|)
expr_stmt|;
name|aclsOnQueueA
operator|.
name|put
argument_list|(
name|QueueACL
operator|.
name|ADMINISTER_QUEUE
argument_list|,
name|adminACLonQueueA
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
name|aclsOnQueueA
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|aclsOnQueueB
init|=
operator|new
name|HashMap
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
name|AccessControlList
name|submitACLonQueueB
init|=
operator|new
name|AccessControlList
argument_list|(
name|QUEUE_B_USER
argument_list|)
decl_stmt|;
name|submitACLonQueueB
operator|.
name|addUser
argument_list|(
name|COMMON_USER
argument_list|)
expr_stmt|;
name|AccessControlList
name|adminACLonQueueB
init|=
operator|new
name|AccessControlList
argument_list|(
name|QUEUE_B_ADMIN
argument_list|)
decl_stmt|;
name|aclsOnQueueB
operator|.
name|put
argument_list|(
name|QueueACL
operator|.
name|SUBMIT_APPLICATIONS
argument_list|,
name|submitACLonQueueB
argument_list|)
expr_stmt|;
name|aclsOnQueueB
operator|.
name|put
argument_list|(
name|QueueACL
operator|.
name|ADMINISTER_QUEUE
argument_list|,
name|adminACLonQueueB
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEB
argument_list|,
name|aclsOnQueueB
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
name|aclsOnRootQueue
init|=
operator|new
name|HashMap
argument_list|<
name|QueueACL
argument_list|,
name|AccessControlList
argument_list|>
argument_list|()
decl_stmt|;
name|AccessControlList
name|submitACLonRoot
init|=
operator|new
name|AccessControlList
argument_list|(
literal|""
argument_list|)
decl_stmt|;
name|AccessControlList
name|adminACLonRoot
init|=
operator|new
name|AccessControlList
argument_list|(
name|ROOT_ADMIN
argument_list|)
decl_stmt|;
name|aclsOnRootQueue
operator|.
name|put
argument_list|(
name|QueueACL
operator|.
name|SUBMIT_APPLICATIONS
argument_list|,
name|submitACLonRoot
argument_list|)
expr_stmt|;
name|aclsOnRootQueue
operator|.
name|put
argument_list|(
name|QueueACL
operator|.
name|ADMINISTER_QUEUE
argument_list|,
name|adminACLonRoot
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
argument_list|,
name|aclsOnRootQueue
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|setBoolean
argument_list|(
name|YarnConfiguration
operator|.
name|YARN_ACL_ENABLE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|csConf
operator|.
name|set
argument_list|(
literal|"yarn.resourcemanager.scheduler.class"
argument_list|,
name|CapacityScheduler
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|csConf
return|;
block|}
block|}
end_class

end_unit

