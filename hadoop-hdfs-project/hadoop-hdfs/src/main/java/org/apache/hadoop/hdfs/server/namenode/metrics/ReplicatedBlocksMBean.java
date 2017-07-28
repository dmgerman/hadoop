begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.metrics
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
name|metrics
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

begin_comment
comment|/**  * This interface defines the methods to get status pertaining to blocks of type  * {@link org.apache.hadoop.hdfs.protocol.BlockType#CONTIGUOUS} in FSNamesystem  * of a NameNode. It is also used for publishing via JMX.  *<p>  * Aggregated status of all blocks is reported in  * @see FSNamesystemMBean  * Name Node runtime activity statistic info is reported in  * @see org.apache.hadoop.hdfs.server.namenode.metrics.NameNodeMetrics  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|ReplicatedBlocksMBean
specifier|public
interface|interface
name|ReplicatedBlocksMBean
block|{
comment|/**    * Return low redundancy blocks count.    */
DECL|method|getLowRedundancyReplicatedBlocks ()
name|long
name|getLowRedundancyReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Return corrupt blocks count.    */
DECL|method|getCorruptReplicatedBlocks ()
name|long
name|getCorruptReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Return missing blocks count.    */
DECL|method|getMissingReplicatedBlocks ()
name|long
name|getMissingReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Return count of missing blocks with replication factor one.    */
DECL|method|getMissingReplicationOneBlocks ()
name|long
name|getMissingReplicationOneBlocks
parameter_list|()
function_decl|;
comment|/**    * Return total bytes of future blocks.    */
DECL|method|getBytesInFutureReplicatedBlocks ()
name|long
name|getBytesInFutureReplicatedBlocks
parameter_list|()
function_decl|;
comment|/**    * Return count of blocks that are pending deletion.    */
DECL|method|getPendingDeletionReplicatedBlocks ()
name|long
name|getPendingDeletionReplicatedBlocks
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

