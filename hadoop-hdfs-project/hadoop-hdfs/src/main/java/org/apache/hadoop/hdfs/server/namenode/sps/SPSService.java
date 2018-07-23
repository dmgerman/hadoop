begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.sps
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
name|namenode
operator|.
name|sps
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|Block
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
name|DatanodeInfo
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
name|HdfsConstants
operator|.
name|StoragePolicySatisfierMode
import|;
end_import

begin_comment
comment|/**  * An interface for SPSService, which exposes life cycle and processing APIs.  */
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
DECL|interface|SPSService
specifier|public
interface|interface
name|SPSService
block|{
comment|/**    * Initializes the helper services.    *    * @param ctxt    *          - context is an helper service to provide communication channel    *          between NN and SPS    */
DECL|method|init (Context ctxt)
name|void
name|init
parameter_list|(
name|Context
name|ctxt
parameter_list|)
function_decl|;
comment|/**    * Starts the SPS service. Make sure to initialize the helper services before    * invoking this method.    *    * @param reconfigStart    *          - to indicate whether the SPS startup requested from    *          reconfiguration service    * @param spsMode sps service mode    */
DECL|method|start (boolean reconfigStart, StoragePolicySatisfierMode spsMode)
name|void
name|start
parameter_list|(
name|boolean
name|reconfigStart
parameter_list|,
name|StoragePolicySatisfierMode
name|spsMode
parameter_list|)
function_decl|;
comment|/**    * Stops the SPS service gracefully. Timed wait to stop storage policy    * satisfier daemon threads.    */
DECL|method|stopGracefully ()
name|void
name|stopGracefully
parameter_list|()
function_decl|;
comment|/**    * Stops the SPS service.    *    * @param forceStop    *          true represents to clear all the sps path's hint, false otherwise.    */
DECL|method|stop (boolean forceStop)
name|void
name|stop
parameter_list|(
name|boolean
name|forceStop
parameter_list|)
function_decl|;
comment|/**    * Check whether StoragePolicySatisfier is running.    *    * @return true if running    */
DECL|method|isRunning ()
name|boolean
name|isRunning
parameter_list|()
function_decl|;
comment|/**    * Adds the Item information(file etc) to processing queue.    *    * @param itemInfo    *          file info object for which need to satisfy the policy    */
DECL|method|addFileToProcess (ItemInfo itemInfo, boolean scanCompleted)
name|void
name|addFileToProcess
parameter_list|(
name|ItemInfo
name|itemInfo
parameter_list|,
name|boolean
name|scanCompleted
parameter_list|)
function_decl|;
comment|/**    * Adds all the Item information(file etc) to processing queue.    *    * @param startPathId    *          - directoryId/fileId, on which SPS was called.    * @param itemInfoList    *          - list of item infos    * @param scanCompleted    *          - whether the scanning of directory fully done with itemInfoList    */
DECL|method|addAllFilesToProcess (long startPathId, List<ItemInfo> itemInfoList, boolean scanCompleted)
name|void
name|addAllFilesToProcess
parameter_list|(
name|long
name|startPathId
parameter_list|,
name|List
argument_list|<
name|ItemInfo
argument_list|>
name|itemInfoList
parameter_list|,
name|boolean
name|scanCompleted
parameter_list|)
function_decl|;
comment|/**    * @return current processing queue size.    */
DECL|method|processingQueueSize ()
name|int
name|processingQueueSize
parameter_list|()
function_decl|;
comment|/**    * @return the configuration.    */
DECL|method|getConf ()
name|Configuration
name|getConf
parameter_list|()
function_decl|;
comment|/**    * Marks the scanning of directory if finished.    *    * @param spsPath    *          - satisfier path id    */
DECL|method|markScanCompletedForPath (long spsPath)
name|void
name|markScanCompletedForPath
parameter_list|(
name|long
name|spsPath
parameter_list|)
function_decl|;
comment|/**    * Given node is reporting that it received a certain movement attempt    * finished block.    *    * @param dnInfo    *          - reported datanode    * @param storageType    *          - storage type    * @param block    *          - block that is attempted to move    */
DECL|method|notifyStorageMovementAttemptFinishedBlk (DatanodeInfo dnInfo, StorageType storageType, Block block)
name|void
name|notifyStorageMovementAttemptFinishedBlk
parameter_list|(
name|DatanodeInfo
name|dnInfo
parameter_list|,
name|StorageType
name|storageType
parameter_list|,
name|Block
name|block
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

