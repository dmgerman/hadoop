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
name|Stable
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
name|api
operator|.
name|protocolrecords
operator|.
name|AllocateResponse
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
comment|/**  *<p>The NMToken is used for authenticating communication with  *<code>NodeManager</code></p>  *<p>It is issued by<code>ResourceMananger</code> when<code>ApplicationMaster</code>  * negotiates resource with<code>ResourceManager</code> and  * validated on<code>NodeManager</code> side.</p>  * @see  AllocateResponse#getNMTokens()  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Stable
DECL|class|NMToken
specifier|public
specifier|abstract
class|class
name|NMToken
block|{
annotation|@
name|Private
annotation|@
name|Unstable
DECL|method|newInstance (NodeId nodeId, Token token)
specifier|public
specifier|static
name|NMToken
name|newInstance
parameter_list|(
name|NodeId
name|nodeId
parameter_list|,
name|Token
name|token
parameter_list|)
block|{
name|NMToken
name|nmToken
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NMToken
operator|.
name|class
argument_list|)
decl_stmt|;
name|nmToken
operator|.
name|setNodeId
argument_list|(
name|nodeId
argument_list|)
expr_stmt|;
name|nmToken
operator|.
name|setToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
return|return
name|nmToken
return|;
block|}
comment|/**    * Get the {@link NodeId} of the<code>NodeManager</code> for which the NMToken    * is used to authenticate.    * @return the {@link NodeId} of the<code>NodeManager</code> for which the    * NMToken is used to authenticate.    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getNodeId ()
specifier|public
specifier|abstract
name|NodeId
name|getNodeId
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setNodeId (NodeId nodeId)
specifier|public
specifier|abstract
name|void
name|setNodeId
parameter_list|(
name|NodeId
name|nodeId
parameter_list|)
function_decl|;
comment|/**    * Get the {@link Token} used for authenticating with<code>NodeManager</code>    * @return the {@link Token} used for authenticating with<code>NodeManager</code>    */
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|getToken ()
specifier|public
specifier|abstract
name|Token
name|getToken
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Stable
DECL|method|setToken (Token token)
specifier|public
specifier|abstract
name|void
name|setToken
parameter_list|(
name|Token
name|token
parameter_list|)
function_decl|;
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
specifier|final
name|int
name|prime
init|=
literal|31
decl_stmt|;
name|int
name|result
init|=
literal|1
decl_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|getNodeId
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|getNodeId
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
name|result
operator|=
name|prime
operator|*
name|result
operator|+
operator|(
operator|(
name|getToken
argument_list|()
operator|==
literal|null
operator|)
condition|?
literal|0
else|:
name|getToken
argument_list|()
operator|.
name|hashCode
argument_list|()
operator|)
expr_stmt|;
return|return
name|result
return|;
block|}
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
return|return
literal|true
return|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|false
return|;
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
return|return
literal|false
return|;
name|NMToken
name|other
init|=
operator|(
name|NMToken
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|getNodeId
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getNodeId
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|getNodeId
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getNodeId
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|getToken
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getToken
argument_list|()
operator|!=
literal|null
condition|)
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
operator|!
name|getToken
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|getToken
argument_list|()
argument_list|)
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

