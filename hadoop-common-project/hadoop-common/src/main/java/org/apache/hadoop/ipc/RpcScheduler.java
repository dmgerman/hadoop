begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
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

begin_comment
comment|/**  * Implement this interface to be used for RPC scheduling and backoff.  *  */
end_comment

begin_interface
DECL|interface|RpcScheduler
specifier|public
interface|interface
name|RpcScheduler
block|{
comment|/**    * Returns priority level greater than zero as a hint for scheduling.    */
DECL|method|getPriorityLevel (Schedulable obj)
name|int
name|getPriorityLevel
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
function_decl|;
DECL|method|shouldBackOff (Schedulable obj)
name|boolean
name|shouldBackOff
parameter_list|(
name|Schedulable
name|obj
parameter_list|)
function_decl|;
comment|/**    * This method only exists to maintain backwards compatibility with old    * implementations. It will not be called by any Hadoop code, and should not    * be implemented by new implementations.    *    * @deprecated Use    * {@link #addResponseTime(String, Schedulable, ProcessingDetails)} instead.    */
annotation|@
name|Deprecated
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
DECL|method|addResponseTime (String name, int priorityLevel, int queueTime, int processingTime)
specifier|default
name|void
name|addResponseTime
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|priorityLevel
parameter_list|,
name|int
name|queueTime
parameter_list|,
name|int
name|processingTime
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This method is deprecated: use the other addResponseTime"
argument_list|)
throw|;
block|}
comment|/**    * Store a processing time value for an RPC call into this scheduler.    *    * @param callName The name of the call.    * @param schedulable The schedulable representing the incoming call.    * @param details The details of processing time.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|addResponseTime (String callName, Schedulable schedulable, ProcessingDetails details)
specifier|default
name|void
name|addResponseTime
parameter_list|(
name|String
name|callName
parameter_list|,
name|Schedulable
name|schedulable
parameter_list|,
name|ProcessingDetails
name|details
parameter_list|)
block|{
comment|// For the sake of backwards compatibility with old implementations of
comment|// this interface, a default implementation is supplied which uses the old
comment|// method. All new implementations MUST override this interface and should
comment|// NOT use the other addResponseTime method.
name|int
name|queueTimeMs
init|=
operator|(
name|int
operator|)
name|details
operator|.
name|get
argument_list|(
name|ProcessingDetails
operator|.
name|Timing
operator|.
name|QUEUE
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|int
name|processingTimeMs
init|=
operator|(
name|int
operator|)
name|details
operator|.
name|get
argument_list|(
name|ProcessingDetails
operator|.
name|Timing
operator|.
name|PROCESSING
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
decl_stmt|;
name|addResponseTime
argument_list|(
name|callName
argument_list|,
name|schedulable
operator|.
name|getPriorityLevel
argument_list|()
argument_list|,
name|queueTimeMs
argument_list|,
name|processingTimeMs
argument_list|)
expr_stmt|;
block|}
DECL|method|stop ()
name|void
name|stop
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

