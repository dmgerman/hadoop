begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.amrmproxy
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
name|nodemanager
operator|.
name|amrmproxy
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
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
name|AllocateRequest
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
name|RegisterApplicationMasterRequest
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
name|exceptions
operator|.
name|YarnException
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|DistSchedAllocateResponse
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
name|server
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|DistSchedRegisterResponse
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Implements the RequestInterceptor interface and provides common functionality  * which can can be used and/or extended by other concrete intercepter classes.  *  */
end_comment

begin_class
DECL|class|AbstractRequestInterceptor
specifier|public
specifier|abstract
class|class
name|AbstractRequestInterceptor
implements|implements
name|RequestInterceptor
block|{
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|appContext
specifier|private
name|AMRMProxyApplicationContext
name|appContext
decl_stmt|;
DECL|field|nextInterceptor
specifier|private
name|RequestInterceptor
name|nextInterceptor
decl_stmt|;
comment|/**    * Sets the {@link RequestInterceptor} in the chain.    */
annotation|@
name|Override
DECL|method|setNextInterceptor (RequestInterceptor nextInterceptor)
specifier|public
name|void
name|setNextInterceptor
parameter_list|(
name|RequestInterceptor
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
comment|/**    * Initializes the {@link RequestInterceptor}.    */
annotation|@
name|Override
DECL|method|init (AMRMProxyApplicationContext appContext)
specifier|public
name|void
name|init
parameter_list|(
name|AMRMProxyApplicationContext
name|appContext
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|appContext
operator|==
literal|null
argument_list|,
literal|"init is called multiple times on this interceptor: "
operator|+
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|appContext
operator|=
name|appContext
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
name|init
argument_list|(
name|appContext
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Disposes the {@link RequestInterceptor}.    */
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
comment|/**    * Gets the next {@link RequestInterceptor} in the chain.    */
annotation|@
name|Override
DECL|method|getNextInterceptor ()
specifier|public
name|RequestInterceptor
name|getNextInterceptor
parameter_list|()
block|{
return|return
name|this
operator|.
name|nextInterceptor
return|;
block|}
comment|/**    * Gets the {@link AMRMProxyApplicationContext}.    */
DECL|method|getApplicationContext ()
specifier|public
name|AMRMProxyApplicationContext
name|getApplicationContext
parameter_list|()
block|{
return|return
name|this
operator|.
name|appContext
return|;
block|}
comment|/**    * Default implementation that invokes the distributed scheduling version    * of the register method.    *    * @param request ApplicationMaster allocate request    * @return Distribtued Scheduler Allocate Response    * @throws YarnException    * @throws IOException    */
annotation|@
name|Override
DECL|method|allocateForDistributedScheduling (AllocateRequest request)
specifier|public
name|DistSchedAllocateResponse
name|allocateForDistributedScheduling
parameter_list|(
name|AllocateRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
operator|(
name|this
operator|.
name|nextInterceptor
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|nextInterceptor
operator|.
name|allocateForDistributedScheduling
argument_list|(
name|request
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**    * Default implementation that invokes the distributed scheduling version    * of the allocate method.    *    * @param request ApplicationMaster registration request    * @return Distributed Scheduler Register Response    * @throws YarnException    * @throws IOException    */
annotation|@
name|Override
specifier|public
name|DistSchedRegisterResponse
DECL|method|registerApplicationMasterForDistributedScheduling (RegisterApplicationMasterRequest request)
name|registerApplicationMasterForDistributedScheduling
parameter_list|(
name|RegisterApplicationMasterRequest
name|request
parameter_list|)
throws|throws
name|YarnException
throws|,
name|IOException
block|{
return|return
operator|(
name|this
operator|.
name|nextInterceptor
operator|!=
literal|null
operator|)
condition|?
name|this
operator|.
name|nextInterceptor
operator|.
name|registerApplicationMasterForDistributedScheduling
argument_list|(
name|request
argument_list|)
else|:
literal|null
return|;
block|}
block|}
end_class

end_unit

