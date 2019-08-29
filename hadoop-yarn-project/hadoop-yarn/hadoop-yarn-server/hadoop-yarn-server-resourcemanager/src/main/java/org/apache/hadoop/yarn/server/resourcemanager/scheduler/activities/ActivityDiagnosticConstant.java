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
comment|/*    * Initial check diagnostics    */
DECL|field|INIT_CHECK_SINGLE_NODE_REMOVED
specifier|public
specifier|final
specifier|static
name|String
name|INIT_CHECK_SINGLE_NODE_REMOVED
init|=
literal|"Initial check: node has been removed from scheduler"
decl_stmt|;
DECL|field|INIT_CHECK_SINGLE_NODE_RESOURCE_INSUFFICIENT
specifier|public
specifier|final
specifier|static
name|String
name|INIT_CHECK_SINGLE_NODE_RESOURCE_INSUFFICIENT
init|=
literal|"Initial check: node resource is insufficient for minimum allocation"
decl_stmt|;
DECL|field|INIT_CHECK_PARTITION_RESOURCE_INSUFFICIENT
specifier|public
specifier|final
specifier|static
name|String
name|INIT_CHECK_PARTITION_RESOURCE_INSUFFICIENT
init|=
literal|"Initial check: insufficient resource in partition"
decl_stmt|;
comment|/*    * Queue level diagnostics    */
DECL|field|QUEUE_NOT_ABLE_TO_ACCESS_PARTITION
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_NOT_ABLE_TO_ACCESS_PARTITION
init|=
literal|"Queue is not able to access partition"
decl_stmt|;
DECL|field|QUEUE_HIT_MAX_CAPACITY_LIMIT
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_HIT_MAX_CAPACITY_LIMIT
init|=
literal|"Queue hits max-capacity limit"
decl_stmt|;
DECL|field|QUEUE_HIT_USER_MAX_CAPACITY_LIMIT
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_HIT_USER_MAX_CAPACITY_LIMIT
init|=
literal|"Queue hits user max-capacity limit"
decl_stmt|;
DECL|field|QUEUE_DO_NOT_HAVE_ENOUGH_HEADROOM
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_DO_NOT_HAVE_ENOUGH_HEADROOM
init|=
literal|"Queue does not have enough headroom for inner highest-priority request"
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
DECL|field|QUEUE_SKIPPED_TO_RESPECT_FIFO
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_SKIPPED_TO_RESPECT_FIFO
init|=
literal|"Queue skipped "
operator|+
literal|"to respect FIFO of applications"
decl_stmt|;
DECL|field|QUEUE_SKIPPED_BECAUSE_SINGLE_NODE_RESERVED
specifier|public
specifier|final
specifier|static
name|String
name|QUEUE_SKIPPED_BECAUSE_SINGLE_NODE_RESERVED
init|=
literal|"Queue skipped because node has been reserved"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
DECL|field|QUEUE_SKIPPED_BECAUSE_SINGLE_NODE_RESOURCE_INSUFFICIENT
name|QUEUE_SKIPPED_BECAUSE_SINGLE_NODE_RESOURCE_INSUFFICIENT
init|=
literal|"Queue skipped because node resource is insufficient"
decl_stmt|;
comment|/*    * Application level diagnostics    */
DECL|field|APPLICATION_FAIL_TO_ALLOCATE
specifier|public
specifier|final
specifier|static
name|String
name|APPLICATION_FAIL_TO_ALLOCATE
init|=
literal|"Application fails to allocate"
decl_stmt|;
DECL|field|APPLICATION_COULD_NOT_GET_CONTAINER
specifier|public
specifier|final
specifier|static
name|String
name|APPLICATION_COULD_NOT_GET_CONTAINER
init|=
literal|"Application couldn't get container for allocation"
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
comment|/*    * Request level diagnostics    */
DECL|field|REQUEST_SKIPPED_BECAUSE_NULL_ANY_REQUEST
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_SKIPPED_BECAUSE_NULL_ANY_REQUEST
init|=
literal|"Request skipped because off-switch request is null"
decl_stmt|;
DECL|field|REQUEST_SKIPPED_IN_IGNORE_EXCLUSIVITY_MODE
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_SKIPPED_IN_IGNORE_EXCLUSIVITY_MODE
init|=
literal|"Request skipped in Ignore Exclusivity mode for AM allocation"
decl_stmt|;
DECL|field|REQUEST_SKIPPED_BECAUSE_OF_RESERVATION
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_SKIPPED_BECAUSE_OF_RESERVATION
init|=
literal|"Request skipped based on reservation algo"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
DECL|field|REQUEST_SKIPPED_BECAUSE_NON_PARTITIONED_PARTITION_FIRST
name|REQUEST_SKIPPED_BECAUSE_NON_PARTITIONED_PARTITION_FIRST
init|=
literal|"Request skipped because non-partitioned resource request should be "
operator|+
literal|"scheduled to non-partitioned partition first"
decl_stmt|;
DECL|field|REQUEST_DO_NOT_NEED_RESOURCE
specifier|public
specifier|final
specifier|static
name|String
name|REQUEST_DO_NOT_NEED_RESOURCE
init|=
literal|"Request does not need more resource"
decl_stmt|;
comment|/*    * Node level diagnostics    */
specifier|public
specifier|final
specifier|static
name|String
DECL|field|NODE_SKIPPED_BECAUSE_OF_NO_OFF_SWITCH_AND_LOCALITY_VIOLATION
name|NODE_SKIPPED_BECAUSE_OF_NO_OFF_SWITCH_AND_LOCALITY_VIOLATION
init|=
literal|"Node skipped because node/rack locality cannot be satisfied"
decl_stmt|;
DECL|field|NODE_SKIPPED_BECAUSE_OF_OFF_SWITCH_DELAY
specifier|public
specifier|final
specifier|static
name|String
name|NODE_SKIPPED_BECAUSE_OF_OFF_SWITCH_DELAY
init|=
literal|"Node skipped because of off-switch delay"
decl_stmt|;
DECL|field|NODE_SKIPPED_BECAUSE_OF_RELAX_LOCALITY
specifier|public
specifier|final
specifier|static
name|String
name|NODE_SKIPPED_BECAUSE_OF_RELAX_LOCALITY
init|=
literal|"Node skipped because relax locality is not allowed"
decl_stmt|;
DECL|field|NODE_TOTAL_RESOURCE_INSUFFICIENT_FOR_REQUEST
specifier|public
specifier|final
specifier|static
name|String
name|NODE_TOTAL_RESOURCE_INSUFFICIENT_FOR_REQUEST
init|=
literal|"Node's total resource is insufficient for request"
decl_stmt|;
DECL|field|NODE_DO_NOT_HAVE_SUFFICIENT_RESOURCE
specifier|public
specifier|final
specifier|static
name|String
name|NODE_DO_NOT_HAVE_SUFFICIENT_RESOURCE
init|=
literal|"Node does not have sufficient resource for request"
decl_stmt|;
DECL|field|NODE_IS_BLACKLISTED
specifier|public
specifier|final
specifier|static
name|String
name|NODE_IS_BLACKLISTED
init|=
literal|"Node is blacklisted"
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

