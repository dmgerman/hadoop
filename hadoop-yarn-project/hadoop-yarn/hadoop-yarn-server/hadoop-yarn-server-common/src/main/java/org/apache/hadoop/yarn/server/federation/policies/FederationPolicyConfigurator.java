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
name|amrmproxy
operator|.
name|FederationAMRMProxyPolicy
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
name|federation
operator|.
name|policies
operator|.
name|exceptions
operator|.
name|FederationPolicyInitializationException
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
name|federation
operator|.
name|policies
operator|.
name|router
operator|.
name|FederationRouterPolicy
import|;
end_import

begin_comment
comment|/**  * Implementors of this interface are capable to instantiate and (re)initalize  * {@link FederationAMRMProxyPolicy} and {@link FederationRouterPolicy} based on  * a {@link FederationPolicyInitializationContext}. The reason to bind these two  * policies together is to make sure we remain consistent across the router and  * amrmproxy policy decisions.  */
end_comment

begin_interface
DECL|interface|FederationPolicyConfigurator
specifier|public
interface|interface
name|FederationPolicyConfigurator
block|{
comment|/**    * If the current instance is compatible, this method returns the same    * instance of {@link FederationAMRMProxyPolicy} reinitialized with the    * current context, otherwise a new instance initialized with the current    * context is provided. If the instance is compatible with the current class    * the implementors should attempt to reinitalize (retaining state). To affect    * a complete policy reset oldInstance should be null.    *    * @param federationPolicyInitializationContext the current context    * @param oldInstance                           the existing (possibly null)    *                                              instance.    *    * @return an updated {@link FederationAMRMProxyPolicy   }.    *    * @throws FederationPolicyInitializationException if the initialization    *                                                 cannot be completed    *                                                 properly. The oldInstance    *                                                 should be still valid in    *                                                 case of failed    *                                                 initialization.    */
DECL|method|getAMRMPolicy ( FederationPolicyInitializationContext federationPolicyInitializationContext, FederationAMRMProxyPolicy oldInstance)
name|FederationAMRMProxyPolicy
name|getAMRMPolicy
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyInitializationContext
parameter_list|,
name|FederationAMRMProxyPolicy
name|oldInstance
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
function_decl|;
comment|/**    * If the current instance is compatible, this method returns the same    * instance of {@link FederationRouterPolicy} reinitialized with the current    * context, otherwise a new instance initialized with the current context is    * provided. If the instance is compatible with the current class the    * implementors should attempt to reinitalize (retaining state). To affect a    * complete policy reset oldInstance shoulb be set to null.    *    * @param federationPolicyInitializationContext the current context    * @param oldInstance                           the existing (possibly null)    *                                              instance.    *    * @return an updated {@link FederationRouterPolicy}.    *    * @throws FederationPolicyInitializationException if the initalization cannot    *                                                 be completed properly. The    *                                                 oldInstance should be still    *                                                 valid in case of failed    *                                                 initialization.    */
DECL|method|getRouterPolicy ( FederationPolicyInitializationContext federationPolicyInitializationContext, FederationRouterPolicy oldInstance)
name|FederationRouterPolicy
name|getRouterPolicy
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyInitializationContext
parameter_list|,
name|FederationRouterPolicy
name|oldInstance
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
function_decl|;
block|}
end_interface

end_unit

