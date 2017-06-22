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
name|java
operator|.
name|util
operator|.
name|Map
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
name|conf
operator|.
name|Configurable
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
name|DistributedSchedulingAMProtocol
import|;
end_import

begin_comment
comment|/**  * Defines the contract to be implemented by the request intercepter classes,  * that can be used to intercept and inspect messages sent from the application  * master to the resource manager.  */
end_comment

begin_interface
DECL|interface|RequestInterceptor
specifier|public
interface|interface
name|RequestInterceptor
extends|extends
name|DistributedSchedulingAMProtocol
extends|,
name|Configurable
block|{
comment|/**    * This method is called for initializing the intercepter. This is guaranteed    * to be called only once in the lifetime of this instance.    *    * @param ctx AMRMProxy application context    */
DECL|method|init (AMRMProxyApplicationContext ctx)
name|void
name|init
parameter_list|(
name|AMRMProxyApplicationContext
name|ctx
parameter_list|)
function_decl|;
comment|/**    * Recover intercepter state when NM recovery is enabled. AMRMProxy will    * recover the data map into    * AMRMProxyApplicationContext.getRecoveredDataMap(). All intercepters should    * recover state from it.    *    * For example, registerRequest has to be saved by the last intercepter (i.e.    * the one that actually connects to RM), in order to re-register when RM    * fails over.    *    * @param recoveredDataMap states for all intercepters recovered from NMSS    */
DECL|method|recover (Map<String, byte[]> recoveredDataMap)
name|void
name|recover
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|recoveredDataMap
parameter_list|)
function_decl|;
comment|/**    * This method is called to release the resources held by the intercepter.    * This will be called when the application pipeline is being destroyed. The    * concrete implementations should dispose the resources and forward the    * request to the next intercepter, if any.    */
DECL|method|shutdown ()
name|void
name|shutdown
parameter_list|()
function_decl|;
comment|/**    * Sets the next intercepter in the pipeline. The concrete implementation of    * this interface should always pass the request to the nextInterceptor after    * inspecting the message. The last intercepter in the chain is responsible to    * send the messages to the resource manager service and so the last    * intercepter will not receive this method call.    *    * @param nextInterceptor the next intercepter to set    */
DECL|method|setNextInterceptor (RequestInterceptor nextInterceptor)
name|void
name|setNextInterceptor
parameter_list|(
name|RequestInterceptor
name|nextInterceptor
parameter_list|)
function_decl|;
comment|/**    * Returns the next intercepter in the chain.    *     * @return the next intercepter in the chain    */
DECL|method|getNextInterceptor ()
name|RequestInterceptor
name|getNextInterceptor
parameter_list|()
function_decl|;
comment|/**    * Returns the context.    *     * @return the context    */
DECL|method|getApplicationContext ()
name|AMRMProxyApplicationContext
name|getApplicationContext
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

