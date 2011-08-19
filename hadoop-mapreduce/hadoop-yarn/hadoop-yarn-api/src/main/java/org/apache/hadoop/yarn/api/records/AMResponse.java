begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
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
name|util
operator|.
name|List
import|;
end_import

begin_interface
DECL|interface|AMResponse
specifier|public
interface|interface
name|AMResponse
block|{
DECL|method|getReboot ()
specifier|public
name|boolean
name|getReboot
parameter_list|()
function_decl|;
DECL|method|getResponseId ()
specifier|public
name|int
name|getResponseId
parameter_list|()
function_decl|;
DECL|method|getNewContainerList ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getNewContainerList
parameter_list|()
function_decl|;
DECL|method|getNewContainer (int index)
specifier|public
name|Container
name|getNewContainer
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getNewContainerCount ()
specifier|public
name|int
name|getNewContainerCount
parameter_list|()
function_decl|;
DECL|method|setReboot (boolean reboot)
specifier|public
name|void
name|setReboot
parameter_list|(
name|boolean
name|reboot
parameter_list|)
function_decl|;
DECL|method|setResponseId (int responseId)
specifier|public
name|void
name|setResponseId
parameter_list|(
name|int
name|responseId
parameter_list|)
function_decl|;
DECL|method|addAllNewContainers (List<Container> containers)
specifier|public
name|void
name|addAllNewContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
DECL|method|addNewContainer (Container container)
specifier|public
name|void
name|addNewContainer
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
DECL|method|removeNewContainer (int index)
specifier|public
name|void
name|removeNewContainer
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearNewContainers ()
specifier|public
name|void
name|clearNewContainers
parameter_list|()
function_decl|;
DECL|method|setAvailableResources (Resource limit)
specifier|public
name|void
name|setAvailableResources
parameter_list|(
name|Resource
name|limit
parameter_list|)
function_decl|;
DECL|method|getAvailableResources ()
specifier|public
name|Resource
name|getAvailableResources
parameter_list|()
function_decl|;
DECL|method|getFinishedContainerList ()
specifier|public
name|List
argument_list|<
name|Container
argument_list|>
name|getFinishedContainerList
parameter_list|()
function_decl|;
DECL|method|getFinishedContainer (int index)
specifier|public
name|Container
name|getFinishedContainer
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|getFinishedContainerCount ()
specifier|public
name|int
name|getFinishedContainerCount
parameter_list|()
function_decl|;
DECL|method|addAllFinishedContainers (List<Container> containers)
specifier|public
name|void
name|addAllFinishedContainers
parameter_list|(
name|List
argument_list|<
name|Container
argument_list|>
name|containers
parameter_list|)
function_decl|;
DECL|method|addFinishedContainer (Container container)
specifier|public
name|void
name|addFinishedContainer
parameter_list|(
name|Container
name|container
parameter_list|)
function_decl|;
DECL|method|removeFinishedContainer (int index)
specifier|public
name|void
name|removeFinishedContainer
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
DECL|method|clearFinishedContainers ()
specifier|public
name|void
name|clearFinishedContainers
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

