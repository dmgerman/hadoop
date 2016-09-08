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
comment|/**  * Helper class used to factor out common validation steps for policies.  */
end_comment

begin_class
DECL|class|FederationPolicyInitializationContextValidator
specifier|public
specifier|final
class|class
name|FederationPolicyInitializationContextValidator
block|{
DECL|method|FederationPolicyInitializationContextValidator ()
specifier|private
name|FederationPolicyInitializationContextValidator
parameter_list|()
block|{
comment|//disable constructor per checkstyle
block|}
DECL|method|validate ( FederationPolicyInitializationContext federationPolicyInitializationContext, String myType)
specifier|public
specifier|static
name|void
name|validate
parameter_list|(
name|FederationPolicyInitializationContext
name|federationPolicyInitializationContext
parameter_list|,
name|String
name|myType
parameter_list|)
throws|throws
name|FederationPolicyInitializationException
block|{
if|if
condition|(
name|myType
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyInitializationException
argument_list|(
literal|"The myType parameter"
operator|+
literal|" should not be null."
argument_list|)
throw|;
block|}
if|if
condition|(
name|federationPolicyInitializationContext
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyInitializationException
argument_list|(
literal|"The FederationPolicyInitializationContext provided is null. Cannot"
operator|+
literal|" reinitalize "
operator|+
literal|"successfully."
argument_list|)
throw|;
block|}
if|if
condition|(
name|federationPolicyInitializationContext
operator|.
name|getFederationStateStoreFacade
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyInitializationException
argument_list|(
literal|"The FederationStateStoreFacade provided is null. Cannot"
operator|+
literal|" reinitalize successfully."
argument_list|)
throw|;
block|}
if|if
condition|(
name|federationPolicyInitializationContext
operator|.
name|getFederationSubclusterResolver
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyInitializationException
argument_list|(
literal|"The FederationStateStoreFacase provided is null. Cannot"
operator|+
literal|" reinitalize successfully."
argument_list|)
throw|;
block|}
if|if
condition|(
name|federationPolicyInitializationContext
operator|.
name|getSubClusterPolicyConfiguration
argument_list|()
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyInitializationException
argument_list|(
literal|"The FederationSubclusterResolver provided is null. Cannot "
operator|+
literal|"reinitalize successfully."
argument_list|)
throw|;
block|}
name|String
name|intendedType
init|=
name|federationPolicyInitializationContext
operator|.
name|getSubClusterPolicyConfiguration
argument_list|()
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|myType
operator|.
name|equals
argument_list|(
name|intendedType
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|FederationPolicyInitializationException
argument_list|(
literal|"The FederationPolicyConfiguration carries a type ("
operator|+
name|intendedType
operator|+
literal|") different then mine ("
operator|+
name|myType
operator|+
literal|"). Cannot reinitialize successfully."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

