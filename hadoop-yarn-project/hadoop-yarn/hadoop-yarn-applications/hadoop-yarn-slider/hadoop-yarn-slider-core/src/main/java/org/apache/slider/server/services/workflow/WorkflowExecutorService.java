begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.services.workflow
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|services
operator|.
name|workflow
package|;
end_package

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
name|service
operator|.
name|AbstractService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_comment
comment|/**  * A service that hosts an executor -when the service is stopped,  * {@link ExecutorService#shutdownNow()} is invoked.  */
end_comment

begin_class
DECL|class|WorkflowExecutorService
specifier|public
class|class
name|WorkflowExecutorService
parameter_list|<
name|E
extends|extends
name|ExecutorService
parameter_list|>
extends|extends
name|AbstractService
block|{
DECL|field|executor
specifier|private
name|E
name|executor
decl_stmt|;
comment|/**    * Construct an instance with the given name -but    * no executor    * @param name service name    */
DECL|method|WorkflowExecutorService (String name)
specifier|public
name|WorkflowExecutorService
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    * Construct an instance with the given name and executor    * @param name service name    * @param executor exectuor    */
DECL|method|WorkflowExecutorService (String name, E executor)
specifier|public
name|WorkflowExecutorService
parameter_list|(
name|String
name|name
parameter_list|,
name|E
name|executor
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
comment|/**    * Get the executor    * @return the executor    */
DECL|method|getExecutor ()
specifier|public
specifier|synchronized
name|E
name|getExecutor
parameter_list|()
block|{
return|return
name|executor
return|;
block|}
comment|/**    * Set the executor. Only valid if the current one is null    * @param executor executor    */
DECL|method|setExecutor (E executor)
specifier|public
specifier|synchronized
name|void
name|setExecutor
parameter_list|(
name|E
name|executor
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|this
operator|.
name|executor
operator|==
literal|null
argument_list|,
literal|"Executor already set"
argument_list|)
expr_stmt|;
name|this
operator|.
name|executor
operator|=
name|executor
expr_stmt|;
block|}
comment|/**    * Execute the runnable with the executor (which     * must have been created already)    * @param runnable runnable to execute    */
DECL|method|execute (Runnable runnable)
specifier|public
name|void
name|execute
parameter_list|(
name|Runnable
name|runnable
parameter_list|)
block|{
name|getExecutor
argument_list|()
operator|.
name|execute
argument_list|(
name|runnable
argument_list|)
expr_stmt|;
block|}
comment|/**    * Submit a callable    * @param callable callable    * @param<V> type of the final get    * @return a future to wait on    */
DECL|method|submit (Callable<V> callable)
specifier|public
parameter_list|<
name|V
parameter_list|>
name|Future
argument_list|<
name|V
argument_list|>
name|submit
parameter_list|(
name|Callable
argument_list|<
name|V
argument_list|>
name|callable
parameter_list|)
block|{
return|return
name|getExecutor
argument_list|()
operator|.
name|submit
argument_list|(
name|callable
argument_list|)
return|;
block|}
comment|/**    * Stop the service: halt the executor.     * @throws Exception exception.    */
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|stopExecutor
argument_list|()
expr_stmt|;
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
comment|/**    * Stop the executor if it is not null.    * This uses {@link ExecutorService#shutdownNow()}    * and so does not block until they have completed.    */
DECL|method|stopExecutor ()
specifier|protected
specifier|synchronized
name|void
name|stopExecutor
parameter_list|()
block|{
if|if
condition|(
name|executor
operator|!=
literal|null
condition|)
block|{
name|executor
operator|.
name|shutdownNow
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

