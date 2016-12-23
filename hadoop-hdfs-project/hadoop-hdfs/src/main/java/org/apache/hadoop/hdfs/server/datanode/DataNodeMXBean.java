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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  *   * This is the JMX management interface for data node information.  * End users shouldn't be implementing these interfaces, and instead  * access this information through the JMX APIs.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
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
comment|/**    * Get the version of software running on the DataNode    *    * @return a string representing the version    */
DECL|method|getSoftwareVersion ()
specifier|public
name|String
name|getSoftwareVersion
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
comment|/**    * Gets the data port.    *    * @return the data port    */
DECL|method|getDataPort ()
name|String
name|getDataPort
parameter_list|()
function_decl|;
comment|/**    * Gets the namenode IP addresses.    *     * @return the namenode IP addresses that the datanode is talking to    */
DECL|method|getNamenodeAddresses ()
specifier|public
name|String
name|getNamenodeAddresses
parameter_list|()
function_decl|;
comment|/**    * Gets information of the block pool service actors.    *    * @return block pool service actors info    */
DECL|method|getBPServiceActorInfo ()
name|String
name|getBPServiceActorInfo
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
comment|/**    * Returns an estimate of the number of Datanode threads    * actively transferring blocks.    */
DECL|method|getXceiverCount ()
specifier|public
name|int
name|getXceiverCount
parameter_list|()
function_decl|;
comment|/**    * Returns an estimate of the number of data replication/reconstruction tasks    * running currently.    */
DECL|method|getXmitsInProgress ()
specifier|public
name|int
name|getXmitsInProgress
parameter_list|()
function_decl|;
comment|/**    * Gets the network error counts on a per-Datanode basis.    */
DECL|method|getDatanodeNetworkCounts ()
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Long
argument_list|>
argument_list|>
name|getDatanodeNetworkCounts
parameter_list|()
function_decl|;
comment|/**    * Gets the diskBalancer Status.    * Please see implementation for the format of the returned information.    *    * @return  DiskBalancer Status    */
DECL|method|getDiskBalancerStatus ()
name|String
name|getDiskBalancerStatus
parameter_list|()
function_decl|;
comment|/**    * Gets the {@link FileIoProvider} statistics.    */
DECL|method|getFileIoProviderStatistics ()
name|String
name|getFileIoProviderStatistics
parameter_list|()
function_decl|;
comment|/**    * Gets the average info (e.g. time) of SendPacketDownstream when the DataNode    * acts as the penultimate (2nd to the last) node in pipeline.    *<p>    * Example Json:    * {"[185.164.159.81:9801]RollingAvgTime":504.867,    *  "[49.236.149.246:9801]RollingAvgTime":504.463,    *  "[84.125.113.65:9801]RollingAvgTime":497.954}    *</p>    */
DECL|method|getSendPacketDownstreamAvgInfo ()
name|String
name|getSendPacketDownstreamAvgInfo
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

