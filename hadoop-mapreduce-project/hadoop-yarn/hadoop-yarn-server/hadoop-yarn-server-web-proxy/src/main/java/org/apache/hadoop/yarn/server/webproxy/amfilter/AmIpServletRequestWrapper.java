begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.webproxy.amfilter
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
name|webproxy
operator|.
name|amfilter
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequestWrapper
import|;
end_import

begin_class
DECL|class|AmIpServletRequestWrapper
specifier|public
class|class
name|AmIpServletRequestWrapper
extends|extends
name|HttpServletRequestWrapper
block|{
DECL|field|principal
specifier|private
specifier|final
name|AmIpPrincipal
name|principal
decl_stmt|;
DECL|method|AmIpServletRequestWrapper (HttpServletRequest request, AmIpPrincipal principal)
specifier|public
name|AmIpServletRequestWrapper
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|AmIpPrincipal
name|principal
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|this
operator|.
name|principal
operator|=
name|principal
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getUserPrincipal ()
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
annotation|@
name|Override
DECL|method|getRemoteUser ()
specifier|public
name|String
name|getRemoteUser
parameter_list|()
block|{
return|return
name|principal
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|isUserInRole (String role)
specifier|public
name|boolean
name|isUserInRole
parameter_list|(
name|String
name|role
parameter_list|)
block|{
comment|//No role info so far
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

