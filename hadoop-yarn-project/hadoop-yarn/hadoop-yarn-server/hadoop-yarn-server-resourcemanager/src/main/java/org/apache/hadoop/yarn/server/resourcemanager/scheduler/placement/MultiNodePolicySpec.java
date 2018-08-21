begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.placement
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|placement
package|;
end_package

begin_comment
comment|/**  * MultiNodePolicySpec contains policyName and timeout.  */
end_comment

begin_class
DECL|class|MultiNodePolicySpec
specifier|public
class|class
name|MultiNodePolicySpec
block|{
DECL|field|policyName
specifier|private
name|String
name|policyName
decl_stmt|;
DECL|field|sortingInterval
specifier|private
name|long
name|sortingInterval
decl_stmt|;
DECL|method|MultiNodePolicySpec (String policyName, long timeout)
specifier|public
name|MultiNodePolicySpec
parameter_list|(
name|String
name|policyName
parameter_list|,
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|setSortingInterval
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|this
operator|.
name|setPolicyName
argument_list|(
name|policyName
argument_list|)
expr_stmt|;
block|}
DECL|method|getSortingInterval ()
specifier|public
name|long
name|getSortingInterval
parameter_list|()
block|{
return|return
name|sortingInterval
return|;
block|}
DECL|method|setSortingInterval (long timeout)
specifier|public
name|void
name|setSortingInterval
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|sortingInterval
operator|=
name|timeout
expr_stmt|;
block|}
DECL|method|getPolicyName ()
specifier|public
name|String
name|getPolicyName
parameter_list|()
block|{
return|return
name|policyName
return|;
block|}
DECL|method|setPolicyName (String policyName)
specifier|public
name|void
name|setPolicyName
parameter_list|(
name|String
name|policyName
parameter_list|)
block|{
name|this
operator|.
name|policyName
operator|=
name|policyName
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"MultiNodePolicySpec {"
operator|+
literal|"policyName='"
operator|+
name|policyName
operator|+
literal|'\''
operator|+
literal|", sortingInterval="
operator|+
name|sortingInterval
operator|+
literal|'}'
return|;
block|}
block|}
end_class

end_unit

