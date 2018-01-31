begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
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
name|classification
operator|.
name|InterfaceAudience
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Reason for rejecting a Scheduling Request.  */
end_comment

begin_enum
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|enum|RejectionReason
specifier|public
enum|enum
name|RejectionReason
block|{
comment|/**    * This is used to indicate a possible constraint violation. For eg. If the    * App requested anti-affinity across 5 container requests, but only 4 nodes    * exist. Another eg. could be if tag A has affinity with tag B and tag B has    * affinity with tag C, but tag A has anti-affinity with tag C, all at a rack    * scope - and only 1 rack exists. Essentially all situations where the    * Algorithm cannot assign a Node to SchedulingRequest.    */
DECL|enumConstant|COULD_NOT_PLACE_ON_NODE
name|COULD_NOT_PLACE_ON_NODE
block|,
comment|/**    * This is used to indicate when after the Algorithm has placed a Scheduling    * Request at a node, but the commit failed because the Queue has no    * capacity etc. This can be a transient situation.    */
DECL|enumConstant|COULD_NOT_SCHEDULE_ON_NODE
name|COULD_NOT_SCHEDULE_ON_NODE
block|}
end_enum

end_unit

