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
name|List
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
DECL|class|TestApplicationPriorityACLConfiguration
specifier|public
class|class
name|TestApplicationPriorityACLConfiguration
block|{
DECL|field|defaultPriorityQueueA
specifier|private
specifier|final
name|int
name|defaultPriorityQueueA
init|=
literal|3
decl_stmt|;
DECL|field|defaultPriorityQueueB
specifier|private
specifier|final
name|int
name|defaultPriorityQueueB
init|=
operator|-
literal|1
decl_stmt|;
DECL|field|maxPriorityQueueA
specifier|private
specifier|final
name|int
name|maxPriorityQueueA
init|=
literal|5
decl_stmt|;
DECL|field|maxPriorityQueueB
specifier|private
specifier|final
name|int
name|maxPriorityQueueB
init|=
literal|10
decl_stmt|;
DECL|field|clusterMaxPriority
specifier|private
specifier|final
name|int
name|clusterMaxPriority
init|=
literal|10
decl_stmt|;
DECL|field|QUEUE_A_USER
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_A_USER
init|=
literal|"queueA_user"
decl_stmt|;
DECL|field|QUEUE_B_USER
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_B_USER
init|=
literal|"queueB_user"
decl_stmt|;
DECL|field|QUEUE_A_GROUP
specifier|private
specifier|static
specifier|final
name|String
name|QUEUE_A_GROUP
init|=
literal|"queueA_group"
decl_stmt|;
DECL|field|QUEUEA
specifier|private
specifier|static
specifier|final
name|String
name|QUEUEA
init|=
literal|"queueA"
decl_stmt|;
DECL|field|QUEUEB
specifier|private
specifier|static
specifier|final
name|String
name|QUEUEB
init|=
literal|"queueB"
decl_stmt|;
DECL|field|QUEUEC
specifier|private
specifier|static
specifier|final
name|String
name|QUEUEC
init|=
literal|"queueC"
decl_stmt|;
annotation|@
name|Test
DECL|method|testSimpleACLConfiguration ()
specifier|public
name|void
name|testSimpleACLConfiguration
parameter_list|()
throws|throws
name|Exception
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
block|,
name|QUEUEC
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
literal|25f
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
name|QUEUEC
argument_list|,
literal|25f
argument_list|)
expr_stmt|;
comment|// Success case: Configure one user/group level priority acl for queue A.
name|String
index|[]
name|aclsForA
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|aclsForA
index|[
literal|0
index|]
operator|=
name|QUEUE_A_USER
expr_stmt|;
name|aclsForA
index|[
literal|1
index|]
operator|=
name|QUEUE_A_GROUP
expr_stmt|;
name|csConf
operator|.
name|setPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|maxPriorityQueueA
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriorityQueueA
argument_list|)
argument_list|,
name|aclsForA
argument_list|)
expr_stmt|;
comment|// Try to get the ACL configs and make sure there are errors/exceptions
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|pGroupA
init|=
name|csConf
operator|.
name|getPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|clusterMaxPriority
argument_list|)
argument_list|)
decl_stmt|;
comment|// Validate!
name|verifyACLs
argument_list|(
name|pGroupA
argument_list|,
name|QUEUE_A_USER
argument_list|,
name|QUEUE_A_GROUP
argument_list|,
name|maxPriorityQueueA
argument_list|,
name|defaultPriorityQueueA
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testACLConfigurationForInvalidCases ()
specifier|public
name|void
name|testACLConfigurationForInvalidCases
parameter_list|()
throws|throws
name|Exception
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
block|,
name|QUEUEC
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
literal|25f
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
name|QUEUEC
argument_list|,
literal|25f
argument_list|)
expr_stmt|;
comment|// Success case: Configure one user/group level priority acl for queue A.
name|String
index|[]
name|aclsForA
init|=
operator|new
name|String
index|[
literal|2
index|]
decl_stmt|;
name|aclsForA
index|[
literal|0
index|]
operator|=
name|QUEUE_A_USER
expr_stmt|;
name|aclsForA
index|[
literal|1
index|]
operator|=
name|QUEUE_A_GROUP
expr_stmt|;
name|csConf
operator|.
name|setPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|maxPriorityQueueA
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriorityQueueA
argument_list|)
argument_list|,
name|aclsForA
argument_list|)
expr_stmt|;
name|String
index|[]
name|aclsForB
init|=
operator|new
name|String
index|[
literal|1
index|]
decl_stmt|;
name|aclsForB
index|[
literal|0
index|]
operator|=
name|QUEUE_B_USER
expr_stmt|;
name|csConf
operator|.
name|setPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEB
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|maxPriorityQueueB
argument_list|)
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|defaultPriorityQueueB
argument_list|)
argument_list|,
name|aclsForB
argument_list|)
expr_stmt|;
comment|// Try to get the ACL configs and make sure there are errors/exceptions
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|pGroupA
init|=
name|csConf
operator|.
name|getPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEA
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|clusterMaxPriority
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|pGroupB
init|=
name|csConf
operator|.
name|getPriorityAcls
argument_list|(
name|CapacitySchedulerConfiguration
operator|.
name|ROOT
operator|+
literal|"."
operator|+
name|QUEUEB
argument_list|,
name|Priority
operator|.
name|newInstance
argument_list|(
name|clusterMaxPriority
argument_list|)
argument_list|)
decl_stmt|;
comment|// Validate stored ACL values with configured ones.
name|verifyACLs
argument_list|(
name|pGroupA
argument_list|,
name|QUEUE_A_USER
argument_list|,
name|QUEUE_A_GROUP
argument_list|,
name|maxPriorityQueueA
argument_list|,
name|defaultPriorityQueueA
argument_list|)
expr_stmt|;
name|verifyACLs
argument_list|(
name|pGroupB
argument_list|,
name|QUEUE_B_USER
argument_list|,
literal|""
argument_list|,
name|maxPriorityQueueB
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyACLs (List<AppPriorityACLGroup> pGroup, String queueUser, String queueGroup, int maxPriority, int defaultPriority)
specifier|private
name|void
name|verifyACLs
parameter_list|(
name|List
argument_list|<
name|AppPriorityACLGroup
argument_list|>
name|pGroup
parameter_list|,
name|String
name|queueUser
parameter_list|,
name|String
name|queueGroup
parameter_list|,
name|int
name|maxPriority
parameter_list|,
name|int
name|defaultPriority
parameter_list|)
block|{
name|AppPriorityACLGroup
name|group
init|=
name|pGroup
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|String
name|aclString
init|=
name|queueUser
operator|+
literal|" "
operator|+
name|queueGroup
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|aclString
operator|.
name|trim
argument_list|()
argument_list|,
name|group
operator|.
name|getACLList
argument_list|()
operator|.
name|getAclString
argument_list|()
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|maxPriority
argument_list|,
name|group
operator|.
name|getMaxPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|defaultPriority
argument_list|,
name|group
operator|.
name|getDefaultPriority
argument_list|()
operator|.
name|getPriority
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

