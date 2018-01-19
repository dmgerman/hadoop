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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|BlockStorageMovementCommand
operator|.
name|BlockMovingInfo
import|;
end_import

begin_comment
comment|/**  * Interface for implementing different ways of block moving approaches. One can  * connect directly to DN and request block move, and other can talk NN to  * schedule via heart-beats.  */
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
DECL|interface|BlockMoveTaskHandler
specifier|public
interface|interface
name|BlockMoveTaskHandler
block|{
comment|/**    * This is an interface method to handle the move tasks. BlockMovingInfo must    * contain the required info to move the block, that source location,    * destination location and storage types.    */
DECL|method|submitMoveTask (BlockMovingInfo blkMovingInfo, BlockMovementListener blockMoveCompletionListener)
name|void
name|submitMoveTask
parameter_list|(
name|BlockMovingInfo
name|blkMovingInfo
parameter_list|,
name|BlockMovementListener
name|blockMoveCompletionListener
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

