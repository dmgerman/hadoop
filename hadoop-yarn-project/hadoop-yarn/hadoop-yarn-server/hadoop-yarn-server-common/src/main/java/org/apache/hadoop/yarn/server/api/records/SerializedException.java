begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.records
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
name|InterfaceStability
operator|.
name|Unstable
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SerializedException
specifier|public
specifier|abstract
class|class
name|SerializedException
block|{
DECL|method|init (String message, Throwable cause)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|String
name|message
parameter_list|,
name|Throwable
name|cause
parameter_list|)
function_decl|;
DECL|method|init (String message)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|String
name|message
parameter_list|)
function_decl|;
DECL|method|init (Throwable cause)
specifier|public
specifier|abstract
name|void
name|init
parameter_list|(
name|Throwable
name|cause
parameter_list|)
function_decl|;
DECL|method|getMessage ()
specifier|public
specifier|abstract
name|String
name|getMessage
parameter_list|()
function_decl|;
DECL|method|getRemoteTrace ()
specifier|public
specifier|abstract
name|String
name|getRemoteTrace
parameter_list|()
function_decl|;
DECL|method|getCause ()
specifier|public
specifier|abstract
name|SerializedException
name|getCause
parameter_list|()
function_decl|;
block|}
end_class

end_unit

