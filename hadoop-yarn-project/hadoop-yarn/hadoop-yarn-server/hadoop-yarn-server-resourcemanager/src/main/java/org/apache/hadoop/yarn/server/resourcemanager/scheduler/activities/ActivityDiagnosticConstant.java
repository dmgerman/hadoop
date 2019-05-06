begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.activities
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
name|activities
package|;
end_package

begin_comment
comment|/*  * Collection of diagnostics.  */
end_comment

begin_class
DECL|class|ActivityDiagnosticConstant
specifier|public
class|class
name|ActivityDiagnosticConstant
block|{
comment|// EMPTY means it does not have any diagnostic to display.
comment|// In order not to show "diagnostic" line in frontend,
comment|// we set the value to null.
DECL|field|EMPTY
specifier|public
specifier|final
specifier|static
name|String
name|EMPTY
init|=
literal|null
decl_stmt|;
DECL|field|NOT_ABLE_TO_ACCESS_PARTITION
specifier|public
specifier|final
specifier|static
name|String
name|NOT_ABLE_TO_ACCESS_PARTITION
init|=
literal|"Not able to access partition"
decl_stmt|;
DECL|field|QUEUE_DO_NOT_NEED_MORE_RESOURCE
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_DO_NOT_NEED_MORE_RESOURCE
init|=
literal|"Queue does not need more resource"
decl_stmt|;
DECL|field|QUEUE_MAX_CAPACITY_LIMIT
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_MAX_CAPACITY_LIMIT
init|=
literal|"Hit queue max-capacity limit"
decl_stmt|;
DECL|field|USER_CAPACITY_MAXIMUM_LIMIT
specifier|public
specifier|final
specifier|static
name|String
name|USER_CAPACITY_MAXIMUM_LIMIT
init|=
literal|"Hit user capacity maximum limit"
decl_stmt|;
DECL|field|SKIP_BLACK_LISTED_NODE
specifier|public
specifier|final
specifier|static
name|String
name|SKIP_BLACK_LISTED_NODE
init|=
literal|"Skip black listed node"
decl_stmt|;
DECL|field|PRIORITY_SKIPPED
specifier|public
specifier|final
specifier|static
name|String
name|PRIORITY_SKIPPED
init|=
literal|"Priority skipped"
decl_stmt|;
DECL|field|PRIORITY_SKIPPED_BECAUSE_NULL_ANY_REQUEST
specifier|public
specifier|final
specifier|static
name|String
name|PRIORITY_SKIPPED_BECAUSE_NULL_ANY_REQUEST
init|=
literal|"Priority skipped because off-switch request is null"
decl_stmt|;
DECL|field|SKIP_PRIORITY_BECAUSE_OF_RELAX_LOCALITY
specifier|public
specifier|final
specifier|static
name|String
name|SKIP_PRIORITY_BECAUSE_OF_RELAX_LOCALITY
init|=
literal|"Priority skipped because of relax locality is not allowed"
decl_stmt|;
DECL|field|SKIP_IN_IGNORE_EXCLUSIVITY_MODE
specifier|public
specifier|final
specifier|static
name|String
name|SKIP_IN_IGNORE_EXCLUSIVITY_MODE
init|=
literal|"Skipping assigning to Node in Ignore Exclusivity mode"
decl_stmt|;
DECL|field|DO_NOT_NEED_ALLOCATIONATTEMPTINFOS
specifier|public
specifier|final
specifier|static
name|String
name|DO_NOT_NEED_ALLOCATIONATTEMPTINFOS
init|=
literal|"Doesn't need containers based on reservation algo!"
decl_stmt|;
DECL|field|QUEUE_SKIPPED_HEADROOM
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_SKIPPED_HEADROOM
init|=
literal|"Queue skipped because of headroom"
decl_stmt|;
DECL|field|NON_PARTITIONED_PARTITION_FIRST
specifier|public
specifier|final
specifier|static
name|String
name|NON_PARTITIONED_PARTITION_FIRST
init|=
literal|"Non-partitioned resource request should be scheduled to "
operator|+
literal|"non-partitioned partition first"
decl_stmt|;
DECL|field|SKIP_NODE_LOCAL_REQUEST
specifier|public
specifier|final
specifier|static
name|String
name|SKIP_NODE_LOCAL_REQUEST
init|=
literal|"Skip node-local request"
decl_stmt|;
DECL|field|SKIP_RACK_LOCAL_REQUEST
specifier|public
specifier|final
specifier|static
name|String
name|SKIP_RACK_LOCAL_REQUEST
init|=
literal|"Skip rack-local request"
decl_stmt|;
DECL|field|SKIP_OFF_SWITCH_REQUEST
specifier|public
specifier|final
specifier|static
name|String
name|SKIP_OFF_SWITCH_REQUEST
init|=
literal|"Skip offswitch request"
decl_stmt|;
DECL|field|REQUEST_CAN_NOT_ACCESS_NODE_LABEL
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_CAN_NOT_ACCESS_NODE_LABEL
init|=
literal|"Resource request can not access the label"
decl_stmt|;
DECL|field|NOT_SUFFICIENT_RESOURCE
specifier|public
specifier|final
specifier|static
name|String
name|NOT_SUFFICIENT_RESOURCE
init|=
literal|"Node does not have sufficient resource for request"
decl_stmt|;
DECL|field|LOCALITY_SKIPPED
specifier|public
specifier|final
specifier|static
name|String
name|LOCALITY_SKIPPED
init|=
literal|"Locality skipped"
decl_stmt|;
DECL|field|FAIL_TO_ALLOCATE
specifier|public
specifier|final
specifier|static
name|String
name|FAIL_TO_ALLOCATE
init|=
literal|"Fail to allocate"
decl_stmt|;
DECL|field|COULD_NOT_GET_CONTAINER
specifier|public
specifier|final
specifier|static
name|String
name|COULD_NOT_GET_CONTAINER
init|=
literal|"Couldn't get container for allocation"
decl_stmt|;
DECL|field|APPLICATION_DO_NOT_NEED_RESOURCE
specifier|public
specifier|final
specifier|static
name|String
name|APPLICATION_DO_NOT_NEED_RESOURCE
init|=
literal|"Application does not need more resource"
decl_stmt|;
DECL|field|APPLICATION_PRIORITY_DO_NOT_NEED_RESOURCE
specifier|public
specifier|final
specifier|static
name|String
name|APPLICATION_PRIORITY_DO_NOT_NEED_RESOURCE
init|=
literal|"Application priority does not need more resource"
decl_stmt|;
DECL|field|SKIPPED_ALL_PRIORITIES
specifier|public
specifier|final
specifier|static
name|String
name|SKIPPED_ALL_PRIORITIES
init|=
literal|"All priorities are skipped of the app"
decl_stmt|;
DECL|field|RESPECT_FIFO
specifier|public
specifier|final
specifier|static
name|String
name|RESPECT_FIFO
init|=
literal|"To respect FIFO of applications, "
operator|+
literal|"skipped following applications in the queue"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
DECL|field|NODE_DO_NOT_MATCH_PARTITION_OR_PLACEMENT_CONSTRAINTS
name|NODE_DO_NOT_MATCH_PARTITION_OR_PLACEMENT_CONSTRAINTS
init|=
literal|"Node does not match partition or placement constraints"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
DECL|field|NODE_CAN_NOT_FIND_CONTAINER_TO_BE_UNRESERVED_WHEN_NEEDED
name|NODE_CAN_NOT_FIND_CONTAINER_TO_BE_UNRESERVED_WHEN_NEEDED
init|=
literal|"Node can't find a container to be unreserved when needed"
decl_stmt|;
block|}
end_class

end_unit

