begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.router.webapp
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
name|router
operator|.
name|webapp
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

begin_comment
comment|/**  * Extends the RequestInterceptor class and provides common functionality which  * can be used and/or extended by other concrete intercepter classes.  */
end_comment

begin_class
DECL|class|AbstractRESTRequestInterceptor
specifier|public
specifier|abstract
class|class
name|AbstractRESTRequestInterceptor
implements|implements
name|RESTRequestInterceptor
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|nextInterceptor
specifier|private
name|RESTRequestInterceptor
name|nextInterceptor
decl_stmt|;
comment|/**    * Sets the {@link RESTRequestInterceptor} in the chain.    */
annotation|@
name|Override
DECL|method|setNextInterceptor (RESTRequestInterceptor nextInterceptor)
specifier|public
name|void
name|setNextInterceptor
parameter_list|(
name|RESTRequestInterceptor
name|nextInterceptor
parameter_list|)
block|{
name|this
operator|.
name|nextInterceptor
operator|=
name|nextInterceptor
expr_stmt|;
block|}
comment|/**    * Sets the {@link Configuration}.    */
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|nextInterceptor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nextInterceptor
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Gets the {@link Configuration}.    */
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|this
operator|.
name|conf
return|;
block|}
comment|/**    * Initializes the {@link RESTRequestInterceptor}.    */
annotation|@
name|Override
DECL|method|init (String user)
specifier|public
name|void
name|init
parameter_list|(
name|String
name|user
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|nextInterceptor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nextInterceptor
operator|.
name|init
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Disposes the {@link RESTRequestInterceptor}.    */
annotation|@
name|Override
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|nextInterceptor
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|nextInterceptor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Gets the next {@link RESTRequestInterceptor} in the chain.    */
annotation|@
name|Override
DECL|method|getNextInterceptor ()
specifier|public
name|RESTRequestInterceptor
name|getNextInterceptor
parameter_list|()
block|{
return|return
name|this
operator|.
name|nextInterceptor
return|;
block|}
block|}
end_class

end_unit

