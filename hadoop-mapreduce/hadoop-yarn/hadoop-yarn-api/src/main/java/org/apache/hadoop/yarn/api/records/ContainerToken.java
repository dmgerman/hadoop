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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_interface
DECL|interface|ContainerToken
specifier|public
interface|interface
name|ContainerToken
block|{
DECL|method|getIdentifier ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getIdentifier
parameter_list|()
function_decl|;
DECL|method|getPassword ()
specifier|public
specifier|abstract
name|ByteBuffer
name|getPassword
parameter_list|()
function_decl|;
DECL|method|getKind ()
specifier|public
specifier|abstract
name|String
name|getKind
parameter_list|()
function_decl|;
DECL|method|getService ()
specifier|public
specifier|abstract
name|String
name|getService
parameter_list|()
function_decl|;
DECL|method|setIdentifier (ByteBuffer identifier)
specifier|public
specifier|abstract
name|void
name|setIdentifier
parameter_list|(
name|ByteBuffer
name|identifier
parameter_list|)
function_decl|;
DECL|method|setPassword (ByteBuffer password)
specifier|public
specifier|abstract
name|void
name|setPassword
parameter_list|(
name|ByteBuffer
name|password
parameter_list|)
function_decl|;
DECL|method|setKind (String kind)
specifier|public
specifier|abstract
name|void
name|setKind
parameter_list|(
name|String
name|kind
parameter_list|)
function_decl|;
DECL|method|setService (String service)
specifier|public
specifier|abstract
name|void
name|setService
parameter_list|(
name|String
name|service
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

