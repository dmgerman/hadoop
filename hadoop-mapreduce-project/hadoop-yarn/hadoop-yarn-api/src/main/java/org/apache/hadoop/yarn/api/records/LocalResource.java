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

begin_interface
DECL|interface|LocalResource
specifier|public
interface|interface
name|LocalResource
block|{
DECL|method|getResource ()
specifier|public
specifier|abstract
name|URL
name|getResource
parameter_list|()
function_decl|;
DECL|method|getSize ()
specifier|public
specifier|abstract
name|long
name|getSize
parameter_list|()
function_decl|;
DECL|method|getTimestamp ()
specifier|public
specifier|abstract
name|long
name|getTimestamp
parameter_list|()
function_decl|;
DECL|method|getType ()
specifier|public
specifier|abstract
name|LocalResourceType
name|getType
parameter_list|()
function_decl|;
DECL|method|getVisibility ()
specifier|public
specifier|abstract
name|LocalResourceVisibility
name|getVisibility
parameter_list|()
function_decl|;
DECL|method|setResource (URL resource)
specifier|public
specifier|abstract
name|void
name|setResource
parameter_list|(
name|URL
name|resource
parameter_list|)
function_decl|;
DECL|method|setSize (long size)
specifier|public
specifier|abstract
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
DECL|method|setTimestamp (long timestamp)
specifier|public
specifier|abstract
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
function_decl|;
DECL|method|setType (LocalResourceType type)
specifier|public
specifier|abstract
name|void
name|setType
parameter_list|(
name|LocalResourceType
name|type
parameter_list|)
function_decl|;
DECL|method|setVisibility (LocalResourceVisibility visibility)
specifier|public
specifier|abstract
name|void
name|setVisibility
parameter_list|(
name|LocalResourceVisibility
name|visibility
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

