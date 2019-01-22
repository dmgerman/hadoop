begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.checker
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|checker
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
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

begin_comment
comment|/**  * A class that can be used to schedule an asynchronous check on a given  * {@link Checkable}. If the check is successfully scheduled then a  * {@link ListenableFuture} is returned.  *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|interface|AsyncChecker
specifier|public
interface|interface
name|AsyncChecker
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**    * Schedule an asynchronous check for the given object.    *    * @param target object to be checked.    *    * @param context the interpretation of the context depends on the    *                target.    *    * @return returns a {@link Optional of ListenableFuture} that can be used to    *         retrieve the result of the asynchronous check.    */
DECL|method|schedule (Checkable<K, V> target, K context)
name|Optional
argument_list|<
name|ListenableFuture
argument_list|<
name|V
argument_list|>
argument_list|>
name|schedule
parameter_list|(
name|Checkable
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|target
parameter_list|,
name|K
name|context
parameter_list|)
function_decl|;
comment|/**    * Cancel all executing checks and wait for them to complete.    * First attempts a graceful cancellation, then cancels forcefully.    * Waits for the supplied timeout after both attempts.    *    * See {@link ExecutorService#awaitTermination} for a description of    * the parameters.    *    * @throws InterruptedException    */
DECL|method|shutdownAndWait (long timeout, TimeUnit timeUnit)
name|void
name|shutdownAndWait
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|InterruptedException
function_decl|;
block|}
end_interface

end_unit

