begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.rpc
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
name|rpc
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
name|conf
operator|.
name|Configuration
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
name|security
operator|.
name|KerberosInfo
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
name|security
operator|.
name|SecurityInfo
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
name|security
operator|.
name|token
operator|.
name|TokenIdentifier
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
name|security
operator|.
name|token
operator|.
name|TokenInfo
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
name|security
operator|.
name|token
operator|.
name|TokenSelector
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
name|security
operator|.
name|client
operator|.
name|ClientToAMTokenSelector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|SliderXmlConfKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|annotation
operator|.
name|Annotation
import|;
end_import

begin_comment
comment|/**  * This is where security information goes.  * It is referred to in the<code>META-INF/services/org.apache.hadoop.security.SecurityInfo</code>  * resource of this JAR, which is used to find the binding info  */
end_comment

begin_class
DECL|class|SliderRPCSecurityInfo
specifier|public
class|class
name|SliderRPCSecurityInfo
extends|extends
name|SecurityInfo
block|{
annotation|@
name|Override
DECL|method|getKerberosInfo (Class<?> protocol, Configuration conf)
specifier|public
name|KerberosInfo
name|getKerberosInfo
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
name|protocol
operator|.
name|equals
argument_list|(
name|SliderClusterProtocolPB
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|KerberosInfo
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|serverPrincipal
parameter_list|()
block|{
return|return
name|SliderXmlConfKeys
operator|.
name|KEY_KERBEROS_PRINCIPAL
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|clientPrincipal
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|getTokenInfo (Class<?> protocol, Configuration conf)
specifier|public
name|TokenInfo
name|getTokenInfo
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
name|protocol
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
if|if
condition|(
operator|!
name|protocol
operator|.
name|equals
argument_list|(
name|SliderClusterProtocolPB
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|TokenInfo
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|Annotation
argument_list|>
name|annotationType
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Class
argument_list|<
name|?
extends|extends
name|TokenSelector
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
argument_list|>
name|value
parameter_list|()
block|{
return|return
name|ClientToAMTokenSelector
operator|.
name|class
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"SliderClusterProtocolPB token info"
return|;
block|}
block|}
return|;
block|}
block|}
end_class

end_unit

