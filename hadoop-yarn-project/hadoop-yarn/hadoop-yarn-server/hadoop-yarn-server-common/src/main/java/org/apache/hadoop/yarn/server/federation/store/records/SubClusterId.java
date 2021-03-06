begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records
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
name|store
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
name|InterfaceAudience
operator|.
name|Public
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  * SubClusterId represents the<em>globally unique</em> identifier for a  * subcluster that is participating in federation.  *  *<p>  * The globally unique nature of the identifier is obtained from the  *<code>FederationMembershipStateStore</code> on initialization.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SubClusterId
specifier|public
specifier|abstract
class|class
name|SubClusterId
implements|implements
name|Comparable
argument_list|<
name|SubClusterId
argument_list|>
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (String subClusterId)
specifier|public
specifier|static
name|SubClusterId
name|newInstance
parameter_list|(
name|String
name|subClusterId
parameter_list|)
block|{
name|SubClusterId
name|id
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|SubClusterId
operator|.
name|class
argument_list|)
decl_stmt|;
name|id
operator|.
name|setId
argument_list|(
name|subClusterId
argument_list|)
expr_stmt|;
return|return
name|id
return|;
block|}
comment|/**    * Get the string identifier of the<em>subcluster</em> which is unique across    * the federated cluster. The identifier is static, i.e. preserved across    * restarts and failover.    *    * @return unique identifier of the subcluster    */
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getId ()
specifier|public
specifier|abstract
name|String
name|getId
parameter_list|()
function_decl|;
comment|/**    * Set the string identifier of the<em>subcluster</em> which is unique across    * the federated cluster. The identifier is static, i.e. preserved across    * restarts and failover.    *    * @param subClusterId unique identifier of the subcluster    */
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|setId (String subClusterId)
specifier|protected
specifier|abstract
name|void
name|setId
parameter_list|(
name|String
name|subClusterId
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|equals (Object obj)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|obj
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|getClass
argument_list|()
operator|!=
name|obj
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SubClusterId
name|other
init|=
operator|(
name|SubClusterId
operator|)
name|obj
decl_stmt|;
return|return
name|this
operator|.
name|getId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getId
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|compareTo (SubClusterId other)
specifier|public
name|int
name|compareTo
parameter_list|(
name|SubClusterId
name|other
parameter_list|)
block|{
return|return
name|getId
argument_list|()
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

