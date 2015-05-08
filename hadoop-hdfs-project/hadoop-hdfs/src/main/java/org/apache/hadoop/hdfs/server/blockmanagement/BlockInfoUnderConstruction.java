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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
operator|.
name|BlockUCState
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|ReplicaState
import|;
end_import

begin_interface
DECL|interface|BlockInfoUnderConstruction
specifier|public
interface|interface
name|BlockInfoUnderConstruction
block|{
comment|/**    * Create array of expected replica locations    * (as has been assigned by chooseTargets()).    */
DECL|method|getExpectedStorageLocations ()
specifier|public
name|DatanodeStorageInfo
index|[]
name|getExpectedStorageLocations
parameter_list|()
function_decl|;
comment|/** Get recover block */
DECL|method|getTruncateBlock ()
specifier|public
name|Block
name|getTruncateBlock
parameter_list|()
function_decl|;
comment|/** Convert to a Block object */
DECL|method|toBlock ()
specifier|public
name|Block
name|toBlock
parameter_list|()
function_decl|;
comment|/** Get block recovery ID */
DECL|method|getBlockRecoveryId ()
specifier|public
name|long
name|getBlockRecoveryId
parameter_list|()
function_decl|;
comment|/** Get the number of expected locations */
DECL|method|getNumExpectedLocations ()
specifier|public
name|int
name|getNumExpectedLocations
parameter_list|()
function_decl|;
comment|/** Set expected locations */
DECL|method|setExpectedLocations (DatanodeStorageInfo[] targets)
specifier|public
name|void
name|setExpectedLocations
parameter_list|(
name|DatanodeStorageInfo
index|[]
name|targets
parameter_list|)
function_decl|;
comment|/**    * Process the recorded replicas. When about to commit or finish the    * pipeline recovery sort out bad replicas.    * @param genStamp  The final generation stamp for the block.    */
DECL|method|setGenerationStampAndVerifyReplicas (long genStamp)
specifier|public
name|void
name|setGenerationStampAndVerifyReplicas
parameter_list|(
name|long
name|genStamp
parameter_list|)
function_decl|;
comment|/**    * Initialize lease recovery for this block.    * Find the first alive data-node starting from the previous primary and    * make it primary.    */
DECL|method|initializeBlockRecovery (long recoveryId)
specifier|public
name|void
name|initializeBlockRecovery
parameter_list|(
name|long
name|recoveryId
parameter_list|)
function_decl|;
comment|/** Add the reported replica if it is not already in the replica list. */
DECL|method|addReplicaIfNotPresent (DatanodeStorageInfo storage, Block reportedBlock, ReplicaState rState)
specifier|public
name|void
name|addReplicaIfNotPresent
parameter_list|(
name|DatanodeStorageInfo
name|storage
parameter_list|,
name|Block
name|reportedBlock
parameter_list|,
name|ReplicaState
name|rState
parameter_list|)
function_decl|;
comment|/**    * Commit block's length and generation stamp as reported by the client.    * Set block state to {@link BlockUCState#COMMITTED}.    * @param block - contains client reported block length and generation     * @throws IOException if block ids are inconsistent.    */
DECL|method|commitBlock (Block block)
specifier|public
name|void
name|commitBlock
parameter_list|(
name|Block
name|block
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Convert an under construction block to a complete block.    *     * @return a complete block.    * @throws IOException    *           if the state of the block (the generation stamp and the length)    *           has not been committed by the client or it does not have at least    *           a minimal number of replicas reported from data-nodes.    */
DECL|method|convertToCompleteBlock ()
specifier|public
name|BlockInfo
name|convertToCompleteBlock
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

