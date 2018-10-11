begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Used to registry custom methods to refresh at runtime.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|RefreshHandler
specifier|public
interface|interface
name|RefreshHandler
block|{
comment|/**    * Implement this method to accept refresh requests from the administrator.    * @param identifier is the identifier you registered earlier    * @param args contains a list of string args from the administrator    * @return a RefreshResponse    */
DECL|method|handleRefresh (String identifier, String[] args)
name|RefreshResponse
name|handleRefresh
parameter_list|(
name|String
name|identifier
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

