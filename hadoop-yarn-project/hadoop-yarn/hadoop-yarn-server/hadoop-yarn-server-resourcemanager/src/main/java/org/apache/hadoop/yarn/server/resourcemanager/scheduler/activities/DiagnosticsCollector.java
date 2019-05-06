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
name|resource
operator|.
name|PlacementConstraint
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
name|util
operator|.
name|resource
operator|.
name|ResourceCalculator
import|;
end_import

begin_comment
comment|/**  * Generic interface that can be used for collecting diagnostics.  */
end_comment

begin_interface
DECL|interface|DiagnosticsCollector
specifier|public
interface|interface
name|DiagnosticsCollector
block|{
DECL|method|collect (String diagnostics, String details)
name|void
name|collect
parameter_list|(
name|String
name|diagnostics
parameter_list|,
name|String
name|details
parameter_list|)
function_decl|;
DECL|method|getDiagnostics ()
name|String
name|getDiagnostics
parameter_list|()
function_decl|;
DECL|method|getDetails ()
name|String
name|getDetails
parameter_list|()
function_decl|;
DECL|method|collectResourceDiagnostics (ResourceCalculator rc, Resource required, Resource available)
name|void
name|collectResourceDiagnostics
parameter_list|(
name|ResourceCalculator
name|rc
parameter_list|,
name|Resource
name|required
parameter_list|,
name|Resource
name|available
parameter_list|)
function_decl|;
DECL|method|collectPlacementConstraintDiagnostics (PlacementConstraint pc, PlacementConstraint.TargetExpression.TargetType targetType)
name|void
name|collectPlacementConstraintDiagnostics
parameter_list|(
name|PlacementConstraint
name|pc
parameter_list|,
name|PlacementConstraint
operator|.
name|TargetExpression
operator|.
name|TargetType
name|targetType
parameter_list|)
function_decl|;
DECL|method|collectPartitionDiagnostics ( String requiredPartition, String nodePartition)
name|void
name|collectPartitionDiagnostics
parameter_list|(
name|String
name|requiredPartition
parameter_list|,
name|String
name|nodePartition
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

