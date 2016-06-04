begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util.concurrent
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|concurrent
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
comment|/**  * This interface defines an asynchronous {@link #get(long, TimeUnit)} method.  *  * When the return value is still being computed, invoking  * {@link #get(long, TimeUnit)} will result in a {@link TimeoutException}.  * The method should be invoked again and again  * until the underlying computation is completed.  *  * @param<R> The type of the return value.  * @param<E> The exception type that the underlying implementation may throw.  */
end_comment

begin_interface
DECL|interface|AsyncGet
specifier|public
interface|interface
name|AsyncGet
parameter_list|<
name|R
parameter_list|,
name|E
extends|extends
name|Throwable
parameter_list|>
block|{
comment|/**    * Get the result.    *    * @param timeout The maximum time period to wait.    *                When timeout == 0, it does not wait at all.    *                When timeout< 0, it waits indefinitely.    * @param unit The unit of the timeout value    * @return the result, which is possibly null.    * @throws E an exception thrown by the underlying implementation.    * @throws TimeoutException if it cannot return after the given time period.    * @throws InterruptedException if the thread is interrupted.    */
DECL|method|get (long timeout, TimeUnit unit)
name|R
name|get
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
throws|throws
name|E
throws|,
name|TimeoutException
throws|,
name|InterruptedException
function_decl|;
comment|/** Utility */
DECL|class|Util
class|class
name|Util
block|{
comment|/**      * @return {@link Object#wait(long)} timeout converted      *         from {@link #get(long, TimeUnit)} timeout.      */
DECL|method|asyncGetTimeout2WaitTimeout (long timeout, TimeUnit unit)
specifier|public
specifier|static
name|long
name|asyncGetTimeout2WaitTimeout
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|unit
parameter_list|)
block|{
return|return
name|timeout
operator|<
literal|0
condition|?
literal|0
else|:
name|timeout
operator|==
literal|0
condition|?
literal|1
else|:
name|unit
operator|.
name|toMillis
argument_list|(
name|timeout
argument_list|)
return|;
block|}
block|}
block|}
end_interface

end_unit

