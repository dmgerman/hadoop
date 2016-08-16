begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.publisher
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
name|web
operator|.
name|rest
operator|.
name|publisher
package|;
end_package

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
name|agent
operator|.
name|AgentProviderFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  *  */
end_comment

begin_class
DECL|class|TestSliderProviderFactory
specifier|public
class|class
name|TestSliderProviderFactory
extends|extends
name|AgentProviderFactory
block|{
DECL|field|log
specifier|protected
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestSliderProviderFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|TestSliderProviderFactory ()
specifier|public
name|TestSliderProviderFactory
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Created TestSliderProviderFactory"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createServerProvider ()
specifier|public
name|ProviderService
name|createServerProvider
parameter_list|()
block|{
name|log
operator|.
name|info
argument_list|(
literal|"Creating TestAgentProviderService"
argument_list|)
expr_stmt|;
return|return
operator|new
name|TestAgentProviderService
argument_list|()
return|;
block|}
block|}
end_class

end_unit

