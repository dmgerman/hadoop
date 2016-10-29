begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_comment
comment|/**  * diagnostic messages for AMcontainer launching  */
end_comment

begin_interface
DECL|interface|CSAMContainerLaunchDiagnosticsConstants
specifier|public
interface|interface
name|CSAMContainerLaunchDiagnosticsConstants
block|{
DECL|field|SKIP_AM_ALLOCATION_IN_IGNORE_EXCLUSIVE_MODE
name|String
name|SKIP_AM_ALLOCATION_IN_IGNORE_EXCLUSIVE_MODE
init|=
literal|"Skipping assigning to Node in Ignore Exclusivity mode. "
decl_stmt|;
DECL|field|SKIP_AM_ALLOCATION_IN_BLACK_LISTED_NODE
name|String
name|SKIP_AM_ALLOCATION_IN_BLACK_LISTED_NODE
init|=
literal|"Skipped scheduling for this Node as its black listed. "
decl_stmt|;
DECL|field|SKIP_AM_ALLOCATION_DUE_TO_LOCALITY
name|String
name|SKIP_AM_ALLOCATION_DUE_TO_LOCALITY
init|=
literal|"Skipping assigning to Node as request locality is not matching. "
decl_stmt|;
DECL|field|QUEUE_AM_RESOURCE_LIMIT_EXCEED
name|String
name|QUEUE_AM_RESOURCE_LIMIT_EXCEED
init|=
literal|"Queue's AM resource limit exceeded. "
decl_stmt|;
DECL|field|USER_AM_RESOURCE_LIMIT_EXCEED
name|String
name|USER_AM_RESOURCE_LIMIT_EXCEED
init|=
literal|"User's AM resource limit exceeded. "
decl_stmt|;
DECL|field|LAST_NODE_PROCESSED_MSG
name|String
name|LAST_NODE_PROCESSED_MSG
init|=
literal|" Last Node which was processed for the application : "
decl_stmt|;
DECL|field|CLUSTER_RESOURCE_EMPTY
name|String
name|CLUSTER_RESOURCE_EMPTY
init|=
literal|"Skipping AM assignment as cluster resource is empty. "
decl_stmt|;
block|}
end_interface

end_unit

