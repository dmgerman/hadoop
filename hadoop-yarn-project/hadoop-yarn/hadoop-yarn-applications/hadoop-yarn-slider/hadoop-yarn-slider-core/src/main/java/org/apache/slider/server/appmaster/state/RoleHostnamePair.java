begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.state
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|state
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
import|;
end_import

begin_class
DECL|class|RoleHostnamePair
specifier|public
class|class
name|RoleHostnamePair
block|{
comment|/**    * requested role    */
DECL|field|roleId
specifier|public
specifier|final
name|int
name|roleId
decl_stmt|;
comment|/**    * hostname -will be null if node==null    */
DECL|field|hostname
specifier|public
specifier|final
name|String
name|hostname
decl_stmt|;
DECL|method|RoleHostnamePair (int roleId, String hostname)
specifier|public
name|RoleHostnamePair
parameter_list|(
name|int
name|roleId
parameter_list|,
name|String
name|hostname
parameter_list|)
block|{
name|this
operator|.
name|roleId
operator|=
name|roleId
expr_stmt|;
name|this
operator|.
name|hostname
operator|=
name|hostname
expr_stmt|;
block|}
DECL|method|getRoleId ()
specifier|public
name|int
name|getRoleId
parameter_list|()
block|{
return|return
name|roleId
return|;
block|}
DECL|method|getHostname ()
specifier|public
name|String
name|getHostname
parameter_list|()
block|{
return|return
name|hostname
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
operator|!
operator|(
name|o
operator|instanceof
name|RoleHostnamePair
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RoleHostnamePair
name|that
init|=
operator|(
name|RoleHostnamePair
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|roleId
argument_list|,
name|that
operator|.
name|roleId
argument_list|)
operator|&&
name|Objects
operator|.
name|equals
argument_list|(
name|hostname
argument_list|,
name|that
operator|.
name|hostname
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
name|Objects
operator|.
name|hash
argument_list|(
name|roleId
argument_list|,
name|hostname
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
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"RoleHostnamePair{"
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"roleId="
argument_list|)
operator|.
name|append
argument_list|(
name|roleId
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", hostname='"
argument_list|)
operator|.
name|append
argument_list|(
name|hostname
argument_list|)
operator|.
name|append
argument_list|(
literal|'\''
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|'}'
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

