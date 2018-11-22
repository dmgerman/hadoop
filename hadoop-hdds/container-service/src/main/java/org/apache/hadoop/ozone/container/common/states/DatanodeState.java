begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.states
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|states
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
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
name|TimeUnit
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
name|TimeoutException
import|;
end_import

begin_comment
comment|/**  * State Interface that allows tasks to maintain states.  */
end_comment

begin_interface
DECL|interface|DatanodeState
specifier|public
interface|interface
name|DatanodeState
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Called before entering this state.    */
DECL|method|onEnter ()
name|void
name|onEnter
parameter_list|()
function_decl|;
comment|/**    * Called After exiting this state.    */
DECL|method|onExit ()
name|void
name|onExit
parameter_list|()
function_decl|;
comment|/**    * Executes one or more tasks that is needed by this state.    *    * @param executor -  ExecutorService    */
DECL|method|execute (ExecutorService executor)
name|void
name|execute
parameter_list|(
name|ExecutorService
name|executor
parameter_list|)
function_decl|;
comment|/**    * Wait for execute to finish.    *    * @param time - Time    * @param timeUnit - Unit of time.    * @throws InterruptedException    * @throws ExecutionException    * @throws TimeoutException    */
DECL|method|await (long time, TimeUnit timeUnit)
name|T
name|await
parameter_list|(
name|long
name|time
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|ExecutionException
throws|,
name|TimeoutException
function_decl|;
block|}
end_interface

end_unit

