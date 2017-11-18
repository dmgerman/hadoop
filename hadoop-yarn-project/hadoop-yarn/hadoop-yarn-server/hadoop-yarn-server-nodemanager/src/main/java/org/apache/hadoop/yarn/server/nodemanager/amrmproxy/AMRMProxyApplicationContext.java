begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.amrmproxy
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
name|amrmproxy
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
name|conf
operator|.
name|Configuration
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
name|registry
operator|.
name|client
operator|.
name|api
operator|.
name|RegistryOperations
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
name|Credentials
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
name|token
operator|.
name|Token
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
name|api
operator|.
name|records
operator|.
name|ApplicationAttemptId
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
name|security
operator|.
name|AMRMTokenIdentifier
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
name|Context
import|;
end_import

begin_comment
comment|/**  * Interface that can be used by the intercepter plugins to get the information  * about one application.  *  */
end_comment

begin_interface
DECL|interface|AMRMProxyApplicationContext
specifier|public
interface|interface
name|AMRMProxyApplicationContext
block|{
comment|/**    * Gets the configuration object instance.    * @return the configuration object.    */
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
function_decl|;
comment|/**    * Gets the application attempt identifier.    * @return the application attempt identifier.    */
DECL|method|getApplicationAttemptId ()
name|ApplicationAttemptId
name|getApplicationAttemptId
parameter_list|()
function_decl|;
comment|/**    * Gets the application submitter.    * @return the application submitter    */
DECL|method|getUser ()
name|String
name|getUser
parameter_list|()
function_decl|;
comment|/**    * Gets the application's AMRMToken that is issued by the RM.    * @return the application's AMRMToken that is issued by the RM.    */
DECL|method|getAMRMToken ()
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|getAMRMToken
parameter_list|()
function_decl|;
comment|/**    * Gets the application's local AMRMToken issued by the proxy service.    * @return the application's local AMRMToken issued by the proxy service.    */
DECL|method|getLocalAMRMToken ()
name|Token
argument_list|<
name|AMRMTokenIdentifier
argument_list|>
name|getLocalAMRMToken
parameter_list|()
function_decl|;
comment|/**    * Gets the NMContext object.    * @return the NMContext.    */
DECL|method|getNMCotext ()
name|Context
name|getNMCotext
parameter_list|()
function_decl|;
comment|/**    * Gets the credentials of this application.    *    * @return the credentials.    */
DECL|method|getCredentials ()
name|Credentials
name|getCredentials
parameter_list|()
function_decl|;
comment|/**    * Gets the registry client.    *    * @return the registry.    */
DECL|method|getRegistryClient ()
name|RegistryOperations
name|getRegistryClient
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

