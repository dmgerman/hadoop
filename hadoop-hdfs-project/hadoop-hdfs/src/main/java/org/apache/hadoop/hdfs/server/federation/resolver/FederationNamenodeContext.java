begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.resolver
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
name|resolver
package|;
end_package

begin_comment
comment|/**  * Interface for a discovered NN and its current server endpoints.  */
end_comment

begin_interface
DECL|interface|FederationNamenodeContext
specifier|public
interface|interface
name|FederationNamenodeContext
block|{
comment|/**    * Get the RPC server address of the namenode.    *    * @return RPC server address in the form of host:port.    */
DECL|method|getRpcAddress ()
name|String
name|getRpcAddress
parameter_list|()
function_decl|;
comment|/**    * Get the Service RPC server address of the namenode.    *    * @return Service RPC server address in the form of host:port.    */
DECL|method|getServiceAddress ()
name|String
name|getServiceAddress
parameter_list|()
function_decl|;
comment|/**    * Get the Lifeline RPC server address of the namenode.    *    * @return Lifeline RPC server address in the form of host:port.    */
DECL|method|getLifelineAddress ()
name|String
name|getLifelineAddress
parameter_list|()
function_decl|;
comment|/**    * Get the HTTP server address of the namenode.    *    * @return HTTP address in the form of host:port.    */
DECL|method|getWebAddress ()
name|String
name|getWebAddress
parameter_list|()
function_decl|;
comment|/**    * Get the unique key representing the namenode.    *    * @return Combination of the nameservice and the namenode IDs.    */
DECL|method|getNamenodeKey ()
name|String
name|getNamenodeKey
parameter_list|()
function_decl|;
comment|/**    * Identifier for the nameservice/namespace.    *    * @return Namenode nameservice identifier.    */
DECL|method|getNameserviceId ()
name|String
name|getNameserviceId
parameter_list|()
function_decl|;
comment|/**    * Identifier for the namenode.    *    * @return String    */
DECL|method|getNamenodeId ()
name|String
name|getNamenodeId
parameter_list|()
function_decl|;
comment|/**    * The current state of the namenode (active, standby, etc).    *    * @return FederationNamenodeServiceState State of the namenode.    */
DECL|method|getState ()
name|FederationNamenodeServiceState
name|getState
parameter_list|()
function_decl|;
comment|/**    * The update date.    *    * @return Long with the update date.    */
DECL|method|getDateModified ()
name|long
name|getDateModified
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

