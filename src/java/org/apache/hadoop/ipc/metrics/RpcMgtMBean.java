begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ipc.metrics
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ipc
operator|.
name|metrics
package|;
end_package

begin_comment
comment|/**  *   * This is the JMX management interface for the RPC layer.  * Many of the statistics are sampled and averaged on an interval   * which can be specified in the metrics config file.  *<p>  * For the statistics that are sampled and averaged, one must specify   * a metrics context that does periodic update calls. Most do.  * The default Null metrics context however does NOT. So if you aren't  * using any other metrics context then you can turn on the viewing and averaging  * of sampled metrics by  specifying the following two lines  *  in the hadoop-meterics.properties file:  *<pre>  *        rpc.class=org.apache.hadoop.metrics.spi.NullContextWithUpdateThread  *        rpc.period=10  *</pre>  *<p>  * Note that the metrics are collected regardless of the context used.  * The context with the update thread is used to average the data periodically  *  */
end_comment

begin_interface
DECL|interface|RpcMgtMBean
specifier|public
interface|interface
name|RpcMgtMBean
block|{
comment|/**    * Number of RPC Operations in the last interval    * @return number of operations    */
DECL|method|getRpcOpsNumber ()
name|int
name|getRpcOpsNumber
parameter_list|()
function_decl|;
comment|/**    * Average time for RPC Operations in last interval    * @return time in msec    */
DECL|method|getRpcOpsAvgProcessingTime ()
name|long
name|getRpcOpsAvgProcessingTime
parameter_list|()
function_decl|;
comment|/**    * The Minimum RPC Operation Processing Time since reset was called    * @return time in msec    */
DECL|method|getRpcOpsAvgProcessingTimeMin ()
name|long
name|getRpcOpsAvgProcessingTimeMin
parameter_list|()
function_decl|;
comment|/**    * The Maximum RPC Operation Processing Time since reset was called    * @return time in msec    */
DECL|method|getRpcOpsAvgProcessingTimeMax ()
name|long
name|getRpcOpsAvgProcessingTimeMax
parameter_list|()
function_decl|;
comment|/**    * The Average RPC Operation Queued Time in the last interval    * @return time in msec    */
DECL|method|getRpcOpsAvgQueueTime ()
name|long
name|getRpcOpsAvgQueueTime
parameter_list|()
function_decl|;
comment|/**    * The Minimum RPC Operation Queued Time since reset was called    * @return time in msec    */
DECL|method|getRpcOpsAvgQueueTimeMin ()
name|long
name|getRpcOpsAvgQueueTimeMin
parameter_list|()
function_decl|;
comment|/**    * The Maximum RPC Operation Queued Time since reset was called    * @return time in msec    */
DECL|method|getRpcOpsAvgQueueTimeMax ()
name|long
name|getRpcOpsAvgQueueTimeMax
parameter_list|()
function_decl|;
comment|/**    * Reset all min max times    */
DECL|method|resetAllMinMax ()
name|void
name|resetAllMinMax
parameter_list|()
function_decl|;
comment|/**    * The number of open RPC conections    * @return the number of open rpc connections    */
DECL|method|getNumOpenConnections ()
specifier|public
name|int
name|getNumOpenConnections
parameter_list|()
function_decl|;
comment|/**    * The number of rpc calls in the queue.    * @return The number of rpc calls in the queue.    */
DECL|method|getCallQueueLen ()
specifier|public
name|int
name|getCallQueueLen
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

