begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.utils.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|utils
operator|.
name|db
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_comment
comment|/**  * Generic DB Checkpoint interface.  */
end_comment

begin_interface
DECL|interface|DBCheckpoint
specifier|public
interface|interface
name|DBCheckpoint
block|{
comment|/**    * Get Snapshot location.    */
DECL|method|getCheckpointLocation ()
name|Path
name|getCheckpointLocation
parameter_list|()
function_decl|;
comment|/**    * Get Snapshot creation timestamp.    */
DECL|method|getCheckpointTimestamp ()
name|long
name|getCheckpointTimestamp
parameter_list|()
function_decl|;
comment|/**    * Get last sequence number of Snapshot.    */
DECL|method|getLatestSequenceNumber ()
name|long
name|getLatestSequenceNumber
parameter_list|()
function_decl|;
comment|/**    * Time taken in milliseconds for the checkpoint to be created.    */
DECL|method|checkpointCreationTimeTaken ()
name|long
name|checkpointCreationTimeTaken
parameter_list|()
function_decl|;
comment|/**    * Destroy the contents of the specified checkpoint to ensure    * proper cleanup of the footprint on disk.    *    * @throws IOException if I/O error happens    */
DECL|method|cleanupCheckpoint ()
name|void
name|cleanupCheckpoint
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Set the OM Ratis snapshot index corresponding to the OM DB checkpoint.    * The snapshot index is the latest snapshot index saved by ratis    * snapshots. It is not guaranteed to be the last ratis index applied to    * the OM DB state.    * @param omRatisSnapshotIndex the saved ratis snapshot index    */
DECL|method|setRatisSnapshotIndex (long omRatisSnapshotIndex)
name|void
name|setRatisSnapshotIndex
parameter_list|(
name|long
name|omRatisSnapshotIndex
parameter_list|)
function_decl|;
comment|/**    * Get the OM Ratis snapshot index corresponding to the OM DB checkpoint.    * The ratis snapshot index indicates upto which index is definitely    * included in the DB checkpoint. It is not guaranteed to be the last ratis    * log index applied to the DB checkpoint.    */
DECL|method|getRatisSnapshotIndex ()
name|long
name|getRatisSnapshotIndex
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

