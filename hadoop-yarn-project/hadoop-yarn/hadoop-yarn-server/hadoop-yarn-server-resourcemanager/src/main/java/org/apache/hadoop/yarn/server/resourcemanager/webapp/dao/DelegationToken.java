begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlElement
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"delegation-token"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|DelegationToken
specifier|public
class|class
name|DelegationToken
block|{
DECL|field|token
name|String
name|token
decl_stmt|;
DECL|field|renewer
name|String
name|renewer
decl_stmt|;
DECL|field|owner
name|String
name|owner
decl_stmt|;
DECL|field|kind
name|String
name|kind
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"expiration-time"
argument_list|)
DECL|field|nextExpirationTime
name|Long
name|nextExpirationTime
decl_stmt|;
annotation|@
name|XmlElement
argument_list|(
name|name
operator|=
literal|"max-validity"
argument_list|)
DECL|field|maxValidity
name|Long
name|maxValidity
decl_stmt|;
DECL|method|DelegationToken ()
specifier|public
name|DelegationToken
parameter_list|()
block|{   }
DECL|method|DelegationToken (String token, String renewer, String owner, String kind, Long nextExpirationTime, Long maxValidity)
specifier|public
name|DelegationToken
parameter_list|(
name|String
name|token
parameter_list|,
name|String
name|renewer
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|kind
parameter_list|,
name|Long
name|nextExpirationTime
parameter_list|,
name|Long
name|maxValidity
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
name|this
operator|.
name|renewer
operator|=
name|renewer
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
name|this
operator|.
name|nextExpirationTime
operator|=
name|nextExpirationTime
expr_stmt|;
name|this
operator|.
name|maxValidity
operator|=
name|maxValidity
expr_stmt|;
block|}
DECL|method|getToken ()
specifier|public
name|String
name|getToken
parameter_list|()
block|{
return|return
name|token
return|;
block|}
DECL|method|getRenewer ()
specifier|public
name|String
name|getRenewer
parameter_list|()
block|{
return|return
name|renewer
return|;
block|}
DECL|method|getNextExpirationTime ()
specifier|public
name|Long
name|getNextExpirationTime
parameter_list|()
block|{
return|return
name|nextExpirationTime
return|;
block|}
DECL|method|setToken (String token)
specifier|public
name|void
name|setToken
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
block|}
DECL|method|setRenewer (String renewer)
specifier|public
name|void
name|setRenewer
parameter_list|(
name|String
name|renewer
parameter_list|)
block|{
name|this
operator|.
name|renewer
operator|=
name|renewer
expr_stmt|;
block|}
DECL|method|setNextExpirationTime (long nextExpirationTime)
specifier|public
name|void
name|setNextExpirationTime
parameter_list|(
name|long
name|nextExpirationTime
parameter_list|)
block|{
name|this
operator|.
name|nextExpirationTime
operator|=
name|Long
operator|.
name|valueOf
argument_list|(
name|nextExpirationTime
argument_list|)
expr_stmt|;
block|}
DECL|method|getOwner ()
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
DECL|method|getKind ()
specifier|public
name|String
name|getKind
parameter_list|()
block|{
return|return
name|kind
return|;
block|}
DECL|method|getMaxValidity ()
specifier|public
name|Long
name|getMaxValidity
parameter_list|()
block|{
return|return
name|maxValidity
return|;
block|}
DECL|method|setOwner (String owner)
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
DECL|method|setKind (String kind)
specifier|public
name|void
name|setKind
parameter_list|(
name|String
name|kind
parameter_list|)
block|{
name|this
operator|.
name|kind
operator|=
name|kind
expr_stmt|;
block|}
DECL|method|setMaxValidity (Long maxValidity)
specifier|public
name|void
name|setMaxValidity
parameter_list|(
name|Long
name|maxValidity
parameter_list|)
block|{
name|this
operator|.
name|maxValidity
operator|=
name|maxValidity
expr_stmt|;
block|}
block|}
end_class

end_unit

