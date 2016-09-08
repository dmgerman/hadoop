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
name|store
operator|.
name|records
operator|.
name|SubClusterPolicyConfiguration
import|;
end_import

begin_comment
comment|/**  * Implementors of this class are able to serializeConf the configuraiton of a  * policy as a {@link SubClusterPolicyConfiguration}. This is used during the  * lifetime of a policy from the admin APIs or policy engine to serializeConf  * the policy into the policy store.  */
end_comment

begin_interface
DECL|interface|FederationPolicyWriter
specifier|public
interface|interface
name|FederationPolicyWriter
block|{
comment|/**    /**    * This method is invoked to derive a {@link SubClusterPolicyConfiguration}.    * This is to be used when writing a policy object in the federation policy    * store.    *    * @return a valid policy configuration representing this object    * parametrization.    *    * @throws FederationPolicyInitializationException if the current state cannot    *                                                 be serialized properly    */
DECL|method|serializeConf ()
name|SubClusterPolicyConfiguration
name|serializeConf
parameter_list|()
throws|throws
name|FederationPolicyInitializationException
function_decl|;
block|}
end_interface

end_unit

