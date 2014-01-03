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

begin_comment
comment|/**   * This represents block replicas which are stored in DataNode.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|Replica
specifier|public
interface|interface
name|Replica
block|{
comment|/** Get the block ID  */
DECL|method|getBlockId ()
specifier|public
name|long
name|getBlockId
parameter_list|()
function_decl|;
comment|/** Get the generation stamp */
DECL|method|getGenerationStamp ()
specifier|public
name|long
name|getGenerationStamp
parameter_list|()
function_decl|;
comment|/**    * Get the replica state    * @return the replica state    */
DECL|method|getState ()
specifier|public
name|ReplicaState
name|getState
parameter_list|()
function_decl|;
comment|/**    * Get the number of bytes received    * @return the number of bytes that have been received    */
DECL|method|getNumBytes ()
specifier|public
name|long
name|getNumBytes
parameter_list|()
function_decl|;
comment|/**    * Get the number of bytes that have written to disk    * @return the number of bytes that have written to disk    */
DECL|method|getBytesOnDisk ()
specifier|public
name|long
name|getBytesOnDisk
parameter_list|()
function_decl|;
comment|/**    * Get the number of bytes that are visible to readers    * @return the number of bytes that are visible to readers    */
DECL|method|getVisibleLength ()
specifier|public
name|long
name|getVisibleLength
parameter_list|()
function_decl|;
comment|/**    * Return the storageUuid of the volume that stores this replica.    */
DECL|method|getStorageUuid ()
specifier|public
name|String
name|getStorageUuid
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

