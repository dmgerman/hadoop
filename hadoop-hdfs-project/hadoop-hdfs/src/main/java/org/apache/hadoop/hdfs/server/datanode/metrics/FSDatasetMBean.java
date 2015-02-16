begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.metrics
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
name|metrics
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_comment
comment|/**  *   * This Interface defines the methods to get the status of a the FSDataset of  * a data node.  * It is also used for publishing via JMX (hence we follow the JMX naming  * convention.)   *  * Note we have not used the MetricsDynamicMBeanBase to implement this  * because the interface for the FSDatasetMBean is stable and should  * be published as an interface.  *   *<p>  * Data Node runtime statistic  info is report in another MBean  * @see org.apache.hadoop.hdfs.server.datanode.metrics.DataNodeMetrics  *  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|FSDatasetMBean
specifier|public
interface|interface
name|FSDatasetMBean
block|{
comment|/**    * Returns the total space (in bytes) used by a block pool    * @return  the total space used by a block pool    * @throws IOException    */
DECL|method|getBlockPoolUsed (String bpid)
specifier|public
name|long
name|getBlockPoolUsed
parameter_list|(
name|String
name|bpid
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the total space (in bytes) used by dfs datanode    * @return  the total space used by dfs datanode    * @throws IOException    */
DECL|method|getDfsUsed ()
specifier|public
name|long
name|getDfsUsed
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns total capacity (in bytes) of storage (used and unused)    * @return  total capacity of storage (used and unused)    * @throws IOException    */
DECL|method|getCapacity ()
specifier|public
name|long
name|getCapacity
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the amount of free storage space (in bytes)    * @return The amount of free storage space    * @throws IOException    */
DECL|method|getRemaining ()
specifier|public
name|long
name|getRemaining
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the storage id of the underlying storage    */
DECL|method|getStorageInfo ()
specifier|public
name|String
name|getStorageInfo
parameter_list|()
function_decl|;
comment|/**    * Returns the number of failed volumes in the datanode.    * @return The number of failed volumes in the datanode.    */
DECL|method|getNumFailedVolumes ()
specifier|public
name|int
name|getNumFailedVolumes
parameter_list|()
function_decl|;
comment|/**    * Returns each storage location that has failed, sorted.    * @return each storage location that has failed, sorted    */
DECL|method|getFailedStorageLocations ()
name|String
index|[]
name|getFailedStorageLocations
parameter_list|()
function_decl|;
comment|/**    * Returns the date/time of the last volume failure in milliseconds since    * epoch.    * @return date/time of last volume failure in milliseconds since epoch    */
DECL|method|getLastVolumeFailureDate ()
name|long
name|getLastVolumeFailureDate
parameter_list|()
function_decl|;
comment|/**    * Returns an estimate of total capacity lost due to volume failures in bytes.    * @return estimate of total capacity lost in bytes    */
DECL|method|getEstimatedCapacityLostTotal ()
name|long
name|getEstimatedCapacityLostTotal
parameter_list|()
function_decl|;
comment|/**    * Returns the amount of cache used by the datanode (in bytes).    */
DECL|method|getCacheUsed ()
specifier|public
name|long
name|getCacheUsed
parameter_list|()
function_decl|;
comment|/**    * Returns the total cache capacity of the datanode (in bytes).    */
DECL|method|getCacheCapacity ()
specifier|public
name|long
name|getCacheCapacity
parameter_list|()
function_decl|;
comment|/**    * Returns the number of blocks cached.    */
DECL|method|getNumBlocksCached ()
specifier|public
name|long
name|getNumBlocksCached
parameter_list|()
function_decl|;
comment|/**    * Returns the number of blocks that the datanode was unable to cache    */
DECL|method|getNumBlocksFailedToCache ()
specifier|public
name|long
name|getNumBlocksFailedToCache
parameter_list|()
function_decl|;
comment|/**    * Returns the number of blocks that the datanode was unable to uncache    */
DECL|method|getNumBlocksFailedToUncache ()
specifier|public
name|long
name|getNumBlocksFailedToUncache
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

