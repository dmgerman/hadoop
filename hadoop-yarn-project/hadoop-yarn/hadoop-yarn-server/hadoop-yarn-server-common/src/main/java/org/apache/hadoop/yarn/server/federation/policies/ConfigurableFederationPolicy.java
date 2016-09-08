begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.policies
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
name|federation
operator|.
name|policies
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
name|yarn
operator|.
name|server
operator|.
name|federation
operator|.
name|policies
operator|.
name|exceptions
operator|.
name|FederationPolicyInitializationException
import|;
end_import

begin_comment
comment|/**  * This interface provides a general method to reinitialize a policy. The  * semantics are try-n-swap, so in case of an exception is thrown the  * implmentation must ensure the previous state and configuration is preserved.  */
end_comment

begin_interface
DECL|interface|ConfigurableFederationPolicy
specifier|public
interface|interface
name|ConfigurableFederationPolicy
block|{
comment|/**    * This method is invoked to initialize of update the configuration of    * policies. The implementor should provide try-n-swap semantics, and retain    * state if possible.    *    * @param federationPolicyInitializationContext the new context to provide to    *                                              implementor.    *    * @throws FederationPolicyInitializationException in case the initialization    *                                                 fails.    */
DECL|method|reinitialize ( FederationPolicyInitializationContext federationPolicyInitializationContext)
name|void
name|reinitialize
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyInitializationContext
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
function_decl|;
block|}
end_interface

end_unit

