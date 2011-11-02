begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|InterfaceAudience
operator|.
name|Private
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
name|InterfaceAudience
operator|.
name|Public
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
operator|.
name|Stable
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
operator|.
name|Unstable
import|;
end_import

begin_comment
comment|/**  * Contains various scheduling metrics to be reported by UI and CLI.  */
end_comment

begin_interface
annotation|@
name|Public
annotation|@
name|Stable
DECL|interface|ApplicationResourceUsageReport
specifier|public
interface|interface
name|ApplicationResourceUsageReport
block|{
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumUsedContainers ()
name|int
name|getNumUsedContainers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumUsedContainers (int num_containers)
name|void
name|setNumUsedContainers
parameter_list|(
name|int
name|num_containers
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNumReservedContainers ()
name|int
name|getNumReservedContainers
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNumReservedContainers (int num_reserved_containers)
name|void
name|setNumReservedContainers
parameter_list|(
name|int
name|num_reserved_containers
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getUsedResources ()
name|Resource
name|getUsedResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setUsedResources (Resource resources)
name|void
name|setUsedResources
parameter_list|(
name|Resource
name|resources
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getReservedResources ()
name|Resource
name|getReservedResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setReservedResources (Resource reserved_resources)
name|void
name|setReservedResources
parameter_list|(
name|Resource
name|reserved_resources
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNeededResources ()
name|Resource
name|getNeededResources
parameter_list|()
function_decl|;
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setNeededResources (Resource needed_resources)
name|void
name|setNeededResources
parameter_list|(
name|Resource
name|needed_resources
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

