begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.blockmanagement
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
name|blockmanagement
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|fs
operator|.
name|StorageType
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
name|protocol
operator|.
name|ClientProtocol
import|;
end_import

begin_comment
comment|/** Datanode statistics */
end_comment

begin_interface
DECL|interface|DatanodeStatistics
specifier|public
interface|interface
name|DatanodeStatistics
block|{
comment|/** @return the total capacity */
DECL|method|getCapacityTotal ()
specifier|public
name|long
name|getCapacityTotal
parameter_list|()
function_decl|;
comment|/** @return the used capacity */
DECL|method|getCapacityUsed ()
specifier|public
name|long
name|getCapacityUsed
parameter_list|()
function_decl|;
comment|/** @return the percentage of the used capacity over the total capacity. */
DECL|method|getCapacityUsedPercent ()
specifier|public
name|float
name|getCapacityUsedPercent
parameter_list|()
function_decl|;
comment|/** @return the remaining capacity */
DECL|method|getCapacityRemaining ()
specifier|public
name|long
name|getCapacityRemaining
parameter_list|()
function_decl|;
comment|/** @return the percentage of the remaining capacity over the total capacity. */
DECL|method|getCapacityRemainingPercent ()
specifier|public
name|float
name|getCapacityRemainingPercent
parameter_list|()
function_decl|;
comment|/** @return the block pool used. */
DECL|method|getBlockPoolUsed ()
specifier|public
name|long
name|getBlockPoolUsed
parameter_list|()
function_decl|;
comment|/** @return the percentage of the block pool used space over the total capacity. */
DECL|method|getPercentBlockPoolUsed ()
specifier|public
name|float
name|getPercentBlockPoolUsed
parameter_list|()
function_decl|;
comment|/** @return the total cache capacity of all DataNodes */
DECL|method|getCacheCapacity ()
specifier|public
name|long
name|getCacheCapacity
parameter_list|()
function_decl|;
comment|/** @return the total cache used by all DataNodes */
DECL|method|getCacheUsed ()
specifier|public
name|long
name|getCacheUsed
parameter_list|()
function_decl|;
comment|/** @return the xceiver count */
DECL|method|getXceiverCount ()
specifier|public
name|int
name|getXceiverCount
parameter_list|()
function_decl|;
comment|/** @return average xceiver count for non-decommission(ing|ed) nodes */
DECL|method|getInServiceXceiverCount ()
specifier|public
name|int
name|getInServiceXceiverCount
parameter_list|()
function_decl|;
comment|/** @return number of non-decommission(ing|ed) nodes */
DECL|method|getNumDatanodesInService ()
specifier|public
name|int
name|getNumDatanodesInService
parameter_list|()
function_decl|;
comment|/**    * @return the total used space by data nodes for non-DFS purposes    * such as storing temporary files on the local file system    */
DECL|method|getCapacityUsedNonDFS ()
specifier|public
name|long
name|getCapacityUsedNonDFS
parameter_list|()
function_decl|;
comment|/** The same as {@link ClientProtocol#getStats()}.    * The block related entries are set to -1.    */
DECL|method|getStats ()
specifier|public
name|long
index|[]
name|getStats
parameter_list|()
function_decl|;
comment|/** @return the expired heartbeats */
DECL|method|getExpiredHeartbeats ()
specifier|public
name|int
name|getExpiredHeartbeats
parameter_list|()
function_decl|;
comment|/** @return Storage Tier statistics*/
DECL|method|getStorageTypeStats ()
name|Map
argument_list|<
name|StorageType
argument_list|,
name|StorageTypeStats
argument_list|>
name|getStorageTypeStats
parameter_list|()
function_decl|;
comment|/** @return the provided capacity */
DECL|method|getProvidedCapacity ()
specifier|public
name|long
name|getProvidedCapacity
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

