begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.providers.agent
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|providers
operator|.
name|agent
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
name|slider
operator|.
name|providers
operator|.
name|AbstractClientProvider
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
name|providers
operator|.
name|ProviderService
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
name|providers
operator|.
name|SliderProviderFactory
import|;
end_import

begin_class
DECL|class|AgentProviderFactory
specifier|public
class|class
name|AgentProviderFactory
extends|extends
name|SliderProviderFactory
block|{
DECL|field|CLASSNAME
specifier|public
specifier|static
specifier|final
name|String
name|CLASSNAME
init|=
literal|"org.apache.slider.providers.agent.AgentProviderFactory"
decl_stmt|;
DECL|method|AgentProviderFactory ()
specifier|public
name|AgentProviderFactory
parameter_list|()
block|{   }
DECL|method|AgentProviderFactory (Configuration conf)
specifier|public
name|AgentProviderFactory
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createClientProvider ()
specifier|public
name|AbstractClientProvider
name|createClientProvider
parameter_list|()
block|{
return|return
operator|new
name|AgentClientProvider
argument_list|(
name|getConf
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createServerProvider ()
specifier|public
name|ProviderService
name|createServerProvider
parameter_list|()
block|{
return|return
operator|new
name|AgentProviderService
argument_list|()
return|;
block|}
block|}
end_class

end_unit

