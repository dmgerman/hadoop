begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|block
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|AllocatedBlock
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HddsProtos
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
name|hdds
operator|.
name|client
operator|.
name|BlockID
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
name|hdds
operator|.
name|scm
operator|.
name|container
operator|.
name|common
operator|.
name|helpers
operator|.
name|ExcludeList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  *  *  Block APIs.  *  Container is transparent to these APIs.  */
end_comment

begin_interface
DECL|interface|BlockManager
specifier|public
interface|interface
name|BlockManager
extends|extends
name|Closeable
block|{
comment|/**    * Allocates a new block for a given size.    * @param size - Block Size    * @param type Replication Type    * @param factor - Replication Factor    * @param excludeList List of datanodes/containers to exclude during block    *                    allocation.    * @return AllocatedBlock    * @throws IOException    */
DECL|method|allocateBlock (long size, HddsProtos.ReplicationType type, HddsProtos.ReplicationFactor factor, String owner, ExcludeList excludeList)
name|AllocatedBlock
name|allocateBlock
parameter_list|(
name|long
name|size
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationType
name|type
parameter_list|,
name|HddsProtos
operator|.
name|ReplicationFactor
name|factor
parameter_list|,
name|String
name|owner
parameter_list|,
name|ExcludeList
name|excludeList
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a list of blocks in an atomic operation. Internally, SCM    * writes these blocks into a {@link DeletedBlockLog} and deletes them    * from SCM DB. If this is successful, given blocks are entering pending    * deletion state and becomes invisible from SCM namespace.    *    * @param blockIDs block IDs. This is often the list of blocks of    *                 a particular object key.    * @throws IOException if exception happens, non of the blocks is deleted.    */
DECL|method|deleteBlocks (List<BlockID> blockIDs)
name|void
name|deleteBlocks
parameter_list|(
name|List
argument_list|<
name|BlockID
argument_list|>
name|blockIDs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the block deletion transaction log maintained by SCM.    */
DECL|method|getDeletedBlockLog ()
name|DeletedBlockLog
name|getDeletedBlockLog
parameter_list|()
function_decl|;
comment|/**    * Start block manager background services.    * @throws IOException    */
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Shutdown block manager background services.    * @throws IOException    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * @return the block deleting service executed in SCM.    */
DECL|method|getSCMBlockDeletingService ()
name|SCMBlockDeletingService
name|getSCMBlockDeletingService
parameter_list|()
function_decl|;
comment|/**    * Set SafeMode status.    *    * @param safeModeStatus    */
DECL|method|setSafeModeStatus (boolean safeModeStatus)
name|void
name|setSafeModeStatus
parameter_list|(
name|boolean
name|safeModeStatus
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

