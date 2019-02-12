begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.router
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
name|router
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
operator|.
name|FederationRPCMetrics
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
name|hdfs
operator|.
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|StateStoreService
import|;
end_import

begin_comment
comment|/**  * Metrics and monitoring interface for the router RPC server. Allows pluggable  * diagnostics and monitoring services to be attached.  */
end_comment

begin_interface
DECL|interface|RouterRpcMonitor
specifier|public
interface|interface
name|RouterRpcMonitor
block|{
comment|/**    * Initialize the monitor.    * @param conf Configuration for the monitor.    * @param server RPC server.    * @param store State Store.    */
DECL|method|init ( Configuration conf, RouterRpcServer server, StateStoreService store)
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|RouterRpcServer
name|server
parameter_list|,
name|StateStoreService
name|store
parameter_list|)
function_decl|;
comment|/**    * Get Router RPC metrics info.    * @return The instance of FederationRPCMetrics.    */
DECL|method|getRPCMetrics ()
name|FederationRPCMetrics
name|getRPCMetrics
parameter_list|()
function_decl|;
comment|/**    * Close the monitor.    */
DECL|method|close ()
name|void
name|close
parameter_list|()
function_decl|;
comment|/**    * Start processing an operation on the Router.    */
DECL|method|startOp ()
name|void
name|startOp
parameter_list|()
function_decl|;
comment|/**    * Start proxying an operation to the Namenode.    * @return Id of the thread doing the proxying.    */
DECL|method|proxyOp ()
name|long
name|proxyOp
parameter_list|()
function_decl|;
comment|/**    * Mark a proxy operation as completed.    * @param success If the operation was successful.    */
DECL|method|proxyOpComplete (boolean success)
name|void
name|proxyOpComplete
parameter_list|(
name|boolean
name|success
parameter_list|)
function_decl|;
comment|/**    * Failed to proxy an operation to a Namenode because it was in standby.    */
DECL|method|proxyOpFailureStandby ()
name|void
name|proxyOpFailureStandby
parameter_list|()
function_decl|;
comment|/**    * Failed to proxy an operation to a Namenode because of an unexpected    * exception.    */
DECL|method|proxyOpFailureCommunicate ()
name|void
name|proxyOpFailureCommunicate
parameter_list|()
function_decl|;
comment|/**    * Failed to proxy an operation to a Namenode because the client was    * overloaded.    */
DECL|method|proxyOpFailureClientOverloaded ()
name|void
name|proxyOpFailureClientOverloaded
parameter_list|()
function_decl|;
comment|/**    * Failed to proxy an operation because it is not implemented.    */
DECL|method|proxyOpNotImplemented ()
name|void
name|proxyOpNotImplemented
parameter_list|()
function_decl|;
comment|/**    * Retry to proxy an operation to a Namenode because of an unexpected    * exception.    */
DECL|method|proxyOpRetries ()
name|void
name|proxyOpRetries
parameter_list|()
function_decl|;
comment|/**    * Failed to proxy an operation because of no namenodes available.    */
DECL|method|proxyOpNoNamenodes ()
name|void
name|proxyOpNoNamenodes
parameter_list|()
function_decl|;
comment|/**    * If the Router cannot contact the State Store in an operation.    */
DECL|method|routerFailureStateStore ()
name|void
name|routerFailureStateStore
parameter_list|()
function_decl|;
comment|/**    * If the Router is in safe mode.    */
DECL|method|routerFailureSafemode ()
name|void
name|routerFailureSafemode
parameter_list|()
function_decl|;
comment|/**    * If a path is locked.    */
DECL|method|routerFailureLocked ()
name|void
name|routerFailureLocked
parameter_list|()
function_decl|;
comment|/**    * If a path is in a read only mount point.    */
DECL|method|routerFailureReadOnly ()
name|void
name|routerFailureReadOnly
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

