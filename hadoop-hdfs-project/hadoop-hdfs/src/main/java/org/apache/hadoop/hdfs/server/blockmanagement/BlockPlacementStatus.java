begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|blockmanagement
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

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|BlockPlacementStatus
specifier|public
interface|interface
name|BlockPlacementStatus
block|{
comment|/**    * Boolean value to identify if replicas of this block satisfy requirement of     * placement policy    * @return if replicas satisfy placement policy's requirement     */
DECL|method|isPlacementPolicySatisfied ()
specifier|public
name|boolean
name|isPlacementPolicySatisfied
parameter_list|()
function_decl|;
comment|/**    * Get description info for log or printed in case replicas are failed to meet    * requirement of placement policy    * @return description in case replicas are failed to meet requirement of    * placement policy    */
DECL|method|getErrorDescription ()
specifier|public
name|String
name|getErrorDescription
parameter_list|()
function_decl|;
comment|/**    * Return the number of additional replicas needed to ensure the block    * placement policy is satisfied.    * @return The number of new replicas needed to satisify the placement policy    * or zero if no extra are needed    */
DECL|method|getAdditionalReplicasRequired ()
name|int
name|getAdditionalReplicasRequired
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

