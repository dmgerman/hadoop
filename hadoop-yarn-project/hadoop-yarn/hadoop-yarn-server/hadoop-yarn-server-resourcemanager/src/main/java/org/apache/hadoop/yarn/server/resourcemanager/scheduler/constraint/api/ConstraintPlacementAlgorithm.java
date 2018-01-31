begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.constraint.api
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
name|constraint
operator|.
name|api
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
name|server
operator|.
name|resourcemanager
operator|.
name|RMContext
import|;
end_import

begin_comment
comment|/**  * Marker interface for a Constraint Placement. The only contract is that it  * should be initialized with the RMContext.  */
end_comment

begin_interface
DECL|interface|ConstraintPlacementAlgorithm
specifier|public
interface|interface
name|ConstraintPlacementAlgorithm
block|{
comment|/**    * Initialize the Algorithm.    * @param rmContext RMContext.    */
DECL|method|init (RMContext rmContext)
name|void
name|init
parameter_list|(
name|RMContext
name|rmContext
parameter_list|)
function_decl|;
comment|/**    * The Algorithm is expected to compute the placement of the provided    * ConstraintPlacementAlgorithmInput and use the collector to aggregate    * any output.    * @param algorithmInput Input to the Algorithm.    * @param collector Collector for output of algorithm.    */
DECL|method|place (ConstraintPlacementAlgorithmInput algorithmInput, ConstraintPlacementAlgorithmOutputCollector collector)
name|void
name|place
parameter_list|(
name|ConstraintPlacementAlgorithmInput
name|algorithmInput
parameter_list|,
name|ConstraintPlacementAlgorithmOutputCollector
name|collector
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

