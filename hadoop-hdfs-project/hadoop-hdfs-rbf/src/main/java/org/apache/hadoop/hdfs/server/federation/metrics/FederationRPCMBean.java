begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.metrics
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
name|federation
operator|.
name|metrics
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

begin_comment
comment|/**  * JMX interface for the RPC server.  * TODO use the default RPC MBean.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|FederationRPCMBean
specifier|public
interface|interface
name|FederationRPCMBean
block|{
DECL|method|getProxyOps ()
name|long
name|getProxyOps
parameter_list|()
function_decl|;
DECL|method|getProxyAvg ()
name|double
name|getProxyAvg
parameter_list|()
function_decl|;
DECL|method|getProcessingOps ()
name|long
name|getProcessingOps
parameter_list|()
function_decl|;
DECL|method|getProcessingAvg ()
name|double
name|getProcessingAvg
parameter_list|()
function_decl|;
DECL|method|getProxyOpFailureCommunicate ()
name|long
name|getProxyOpFailureCommunicate
parameter_list|()
function_decl|;
DECL|method|getProxyOpFailureStandby ()
name|long
name|getProxyOpFailureStandby
parameter_list|()
function_decl|;
DECL|method|getProxyOpFailureClientOverloaded ()
name|long
name|getProxyOpFailureClientOverloaded
parameter_list|()
function_decl|;
DECL|method|getProxyOpNotImplemented ()
name|long
name|getProxyOpNotImplemented
parameter_list|()
function_decl|;
DECL|method|getProxyOpRetries ()
name|long
name|getProxyOpRetries
parameter_list|()
function_decl|;
DECL|method|getProxyOpNoNamenodes ()
name|long
name|getProxyOpNoNamenodes
parameter_list|()
function_decl|;
DECL|method|getRouterFailureStateStoreOps ()
name|long
name|getRouterFailureStateStoreOps
parameter_list|()
function_decl|;
DECL|method|getRouterFailureReadOnlyOps ()
name|long
name|getRouterFailureReadOnlyOps
parameter_list|()
function_decl|;
DECL|method|getRouterFailureLockedOps ()
name|long
name|getRouterFailureLockedOps
parameter_list|()
function_decl|;
DECL|method|getRouterFailureSafemodeOps ()
name|long
name|getRouterFailureSafemodeOps
parameter_list|()
function_decl|;
DECL|method|getRpcServerCallQueue ()
name|int
name|getRpcServerCallQueue
parameter_list|()
function_decl|;
comment|/**    * Get the number of RPC connections between the clients and the Router.    * @return Number of RPC connections between the clients and the Router.    */
DECL|method|getRpcServerNumOpenConnections ()
name|int
name|getRpcServerNumOpenConnections
parameter_list|()
function_decl|;
comment|/**    * Get the number of RPC connections between the Router and the NNs.    * @return Number of RPC connections between the Router and the NNs.    */
DECL|method|getRpcClientNumConnections ()
name|int
name|getRpcClientNumConnections
parameter_list|()
function_decl|;
comment|/**    * Get the number of active RPC connections between the Router and the NNs.    * @return Number of active RPC connections between the Router and the NNs.    */
DECL|method|getRpcClientNumActiveConnections ()
name|int
name|getRpcClientNumActiveConnections
parameter_list|()
function_decl|;
comment|/**    * Get the number of RPC connections to be created.    * @return Number of RPC connections to be created.    */
DECL|method|getRpcClientNumCreatingConnections ()
name|int
name|getRpcClientNumCreatingConnections
parameter_list|()
function_decl|;
comment|/**    * Get the number of connection pools between the Router and a NNs.    * @return Number of connection pools between the Router and a NNs.    */
DECL|method|getRpcClientNumConnectionPools ()
name|int
name|getRpcClientNumConnectionPools
parameter_list|()
function_decl|;
comment|/**    * JSON representation of the RPC connections from the Router to the NNs.    * @return JSON string representation.    */
DECL|method|getRpcClientConnections ()
name|String
name|getRpcClientConnections
parameter_list|()
function_decl|;
comment|/**    * Get the JSON representation of the async caller thread pool.    * @return JSON string representation of the async caller thread pool.    */
DECL|method|getAsyncCallerPool ()
name|String
name|getAsyncCallerPool
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

