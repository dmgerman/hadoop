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
DECL|interface|URL
specifier|public
interface|interface
name|URL
block|{
DECL|method|getScheme ()
specifier|public
specifier|abstract
name|String
name|getScheme
parameter_list|()
function_decl|;
DECL|method|getHost ()
specifier|public
specifier|abstract
name|String
name|getHost
parameter_list|()
function_decl|;
DECL|method|getPort ()
specifier|public
specifier|abstract
name|int
name|getPort
parameter_list|()
function_decl|;
DECL|method|getFile ()
specifier|public
specifier|abstract
name|String
name|getFile
parameter_list|()
function_decl|;
DECL|method|setScheme (String scheme)
specifier|public
specifier|abstract
name|void
name|setScheme
parameter_list|(
name|String
name|scheme
parameter_list|)
function_decl|;
DECL|method|setHost (String host)
specifier|public
specifier|abstract
name|void
name|setHost
parameter_list|(
name|String
name|host
parameter_list|)
function_decl|;
DECL|method|setPort (int port)
specifier|public
specifier|abstract
name|void
name|setPort
parameter_list|(
name|int
name|port
parameter_list|)
function_decl|;
DECL|method|setFile (String file)
specifier|public
specifier|abstract
name|void
name|setFile
parameter_list|(
name|String
name|file
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

