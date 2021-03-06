begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.authorize
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|authorize
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
name|conf
operator|.
name|Configurable
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
name|security
operator|.
name|UserGroupInformation
import|;
end_import

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Unstable
annotation|@
name|InterfaceAudience
operator|.
name|Public
DECL|interface|ImpersonationProvider
specifier|public
interface|interface
name|ImpersonationProvider
extends|extends
name|Configurable
block|{
comment|/**    * Specifies the configuration prefix for the proxy user properties and    * initializes the provider.    *    * @param configurationPrefix the configuration prefix for the proxy user    * properties    */
DECL|method|init (String configurationPrefix)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|configurationPrefix
parameter_list|)
function_decl|;
comment|/**    * Authorize the superuser which is doing doAs    *     * @param user ugi of the effective or proxy user which contains a real user    * @param remoteAddress the ip address of client    * @throws AuthorizationException    */
DECL|method|authorize (UserGroupInformation user, String remoteAddress)
specifier|public
name|void
name|authorize
parameter_list|(
name|UserGroupInformation
name|user
parameter_list|,
name|String
name|remoteAddress
parameter_list|)
throws|throws
name|AuthorizationException
function_decl|;
block|}
end_interface

end_unit

