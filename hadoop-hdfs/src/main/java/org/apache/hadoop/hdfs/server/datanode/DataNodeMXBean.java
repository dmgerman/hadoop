begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode
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
comment|/**  *   * This is the JMX management interface for data node information  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Stable
DECL|interface|DataNodeMXBean
specifier|public
interface|interface
name|DataNodeMXBean
block|{
comment|/**    * Gets the version of Hadoop.    *     * @return the version of Hadoop    */
DECL|method|getVersion ()
specifier|public
name|String
name|getVersion
parameter_list|()
function_decl|;
comment|/**    * Gets the rpc port.    *     * @return the rpc port    */
DECL|method|getRpcPort ()
specifier|public
name|String
name|getRpcPort
parameter_list|()
function_decl|;
comment|/**    * Gets the http port.    *     * @return the http port    */
DECL|method|getHttpPort ()
specifier|public
name|String
name|getHttpPort
parameter_list|()
function_decl|;
comment|/**    * Gets the namenode IP addresses    *     * @return the namenode IP addresses that the datanode is talking to    */
DECL|method|getNamenodeAddresses ()
specifier|public
name|String
name|getNamenodeAddresses
parameter_list|()
function_decl|;
comment|/**    * Gets the information of each volume on the Datanode. Please    * see the implementation for the format of returned information.    *     * @return the volume info    */
DECL|method|getVolumeInfo ()
specifier|public
name|String
name|getVolumeInfo
parameter_list|()
function_decl|;
comment|/**    * Gets the cluster id.    *     * @return the cluster id    */
DECL|method|getClusterId ()
specifier|public
name|String
name|getClusterId
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

