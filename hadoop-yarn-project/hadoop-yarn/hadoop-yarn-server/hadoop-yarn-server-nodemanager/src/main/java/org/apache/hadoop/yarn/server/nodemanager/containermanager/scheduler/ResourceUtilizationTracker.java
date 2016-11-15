begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.scheduler
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|scheduler
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
name|ResourceUtilization
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
name|nodemanager
operator|.
name|containermanager
operator|.
name|container
operator|.
name|Container
import|;
end_import

begin_comment
comment|/**  * This interface abstracts out how a container contributes to  * Resource Utilization of the node.  * It is used by the {@link ContainerScheduler} to determine which  * OPPORTUNISTIC containers to be killed to make room for a GUARANTEED  * container.  */
end_comment

begin_interface
DECL|interface|ResourceUtilizationTracker
specifier|public
interface|interface
name|ResourceUtilizationTracker
block|{
comment|/**    * Get the current total utilization of all the Containers running on    * the node.    * @return ResourceUtilization Resource Utilization.    */
DECL|method|getCurrentUtilization ()
name|ResourceUtilization
name|getCurrentUtilization
parameter_list|()
function_decl|;
comment|/**    * Add Container's resources to Node Utilization.    * @param container Container.    */
DECL|method|addContainerResources (Container container)
name|void
name|addContainerResources
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
comment|/**    * Subtract Container's resources to Node Utilization.    * @param container Container.    */
DECL|method|subtractContainerResource (Container container)
name|void
name|subtractContainerResource
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
comment|/**    * Check if NM has resources available currently to run the container.    * @param container Container.    * @return True, if NM has resources available currently to run the container.    */
DECL|method|hasResourcesAvailable (Container container)
name|boolean
name|hasResourcesAvailable
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

