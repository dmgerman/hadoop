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
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|ReplicaOutputStreams
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
name|util
operator|.
name|DataChecksum
import|;
end_import

begin_comment
comment|/**   * This defines the interface of a replica in Pipeline that's being written to  */
end_comment

begin_interface
DECL|interface|ReplicaInPipelineInterface
specifier|public
interface|interface
name|ReplicaInPipelineInterface
extends|extends
name|Replica
block|{
comment|/**    * Set the number of bytes received    * @param bytesReceived number of bytes received    */
DECL|method|setNumBytes (long bytesReceived)
name|void
name|setNumBytes
parameter_list|(
name|long
name|bytesReceived
parameter_list|)
function_decl|;
comment|/**    * Get the number of bytes acked    * @return the number of bytes acked    */
DECL|method|getBytesAcked ()
name|long
name|getBytesAcked
parameter_list|()
function_decl|;
comment|/**    * Set the number bytes that have acked    * @param bytesAcked number bytes acked    */
DECL|method|setBytesAcked (long bytesAcked)
name|void
name|setBytesAcked
parameter_list|(
name|long
name|bytesAcked
parameter_list|)
function_decl|;
comment|/**    * Release any disk space reserved for this replica.    */
DECL|method|releaseAllBytesReserved ()
specifier|public
name|void
name|releaseAllBytesReserved
parameter_list|()
function_decl|;
comment|/**    * store the checksum for the last chunk along with the data length    * @param dataLength number of bytes on disk    * @param lastChecksum - checksum bytes for the last chunk    */
DECL|method|setLastChecksumAndDataLen (long dataLength, byte[] lastChecksum)
specifier|public
name|void
name|setLastChecksumAndDataLen
parameter_list|(
name|long
name|dataLength
parameter_list|,
name|byte
index|[]
name|lastChecksum
parameter_list|)
function_decl|;
comment|/**    * gets the last chunk checksum and the length of the block corresponding    * to that checksum    */
DECL|method|getLastChecksumAndDataLen ()
specifier|public
name|ChunkChecksum
name|getLastChecksumAndDataLen
parameter_list|()
function_decl|;
comment|/**    * Create output streams for writing to this replica,     * one for block file and one for CRC file    *     * @param isCreate if it is for creation    * @param requestedChecksum the checksum the writer would prefer to use    * @return output streams for writing    * @throws IOException if any error occurs    */
DECL|method|createStreams (boolean isCreate, DataChecksum requestedChecksum)
specifier|public
name|ReplicaOutputStreams
name|createStreams
parameter_list|(
name|boolean
name|isCreate
parameter_list|,
name|DataChecksum
name|requestedChecksum
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

