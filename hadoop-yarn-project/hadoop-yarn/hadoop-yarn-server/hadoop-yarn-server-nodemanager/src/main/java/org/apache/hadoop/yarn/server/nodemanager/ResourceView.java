begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager
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
package|;
end_package

begin_interface
DECL|interface|ResourceView
specifier|public
interface|interface
name|ResourceView
block|{
comment|/**    * Get virtual memory allocated to the containers.    * @return Virtual memory in bytes.    */
DECL|method|getVmemAllocatedForContainers ()
name|long
name|getVmemAllocatedForContainers
parameter_list|()
function_decl|;
DECL|method|isVmemCheckEnabled ()
name|boolean
name|isVmemCheckEnabled
parameter_list|()
function_decl|;
comment|/**    * Get physical memory allocated to the containers.    * @return Physical memory in bytes.    */
DECL|method|getPmemAllocatedForContainers ()
name|long
name|getPmemAllocatedForContainers
parameter_list|()
function_decl|;
DECL|method|isPmemCheckEnabled ()
name|boolean
name|isPmemCheckEnabled
parameter_list|()
function_decl|;
DECL|method|getVCoresAllocatedForContainers ()
name|long
name|getVCoresAllocatedForContainers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

