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
name|hdfs
operator|.
name|server
operator|.
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
import|;
end_import

begin_comment
comment|/**************************************************  * BlockVolumeChoosingPolicy allows a DataNode to  * specify what policy is to be used while choosing  * a volume for a block request.  *  * Note: This is an evolving i/f and is only for  * advanced use.  *  ***************************************************/
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|BlockVolumeChoosingPolicy
specifier|public
interface|interface
name|BlockVolumeChoosingPolicy
parameter_list|<
name|V
extends|extends
name|FsVolumeSpi
parameter_list|>
block|{
comment|/**    * Returns a specific FSVolume after applying a suitable choice algorithm    * to place a given block, given a list of FSVolumes and the block    * size sought for storage.    *     * (Policies that maintain state must be thread-safe.)    *     * @param volumes - the array of FSVolumes that are available.    * @param blockSize - the size of the block for which a volume is sought.    * @return the chosen volume to store the block.    * @throws IOException when disks are unavailable or are full.    */
DECL|method|chooseVolume (List<V> volumes, long blockSize)
specifier|public
name|V
name|chooseVolume
parameter_list|(
name|List
argument_list|<
name|V
argument_list|>
name|volumes
parameter_list|,
name|long
name|blockSize
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

