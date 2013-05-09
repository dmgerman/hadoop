begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.snapshot
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
name|snapshot
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
name|namenode
operator|.
name|INodeFile
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
name|namenode
operator|.
name|INodeFileUnderConstruction
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
name|namenode
operator|.
name|snapshot
operator|.
name|FileWithSnapshot
operator|.
name|FileDiff
import|;
end_import

begin_comment
comment|/**  * A list of FileDiffs for storing snapshot data.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|FileDiffList
specifier|public
class|class
name|FileDiffList
extends|extends
name|AbstractINodeDiffList
argument_list|<
name|INodeFile
argument_list|,
name|FileDiff
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createDiff (Snapshot snapshot, INodeFile file)
name|FileDiff
name|createDiff
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|INodeFile
name|file
parameter_list|)
block|{
return|return
operator|new
name|FileDiff
argument_list|(
name|snapshot
argument_list|,
name|file
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|createSnapshotCopy (INodeFile currentINode)
name|INodeFile
name|createSnapshotCopy
parameter_list|(
name|INodeFile
name|currentINode
parameter_list|)
block|{
if|if
condition|(
name|currentINode
operator|instanceof
name|INodeFileUnderConstructionWithSnapshot
condition|)
block|{
specifier|final
name|INodeFileUnderConstruction
name|uc
init|=
operator|(
name|INodeFileUnderConstruction
operator|)
name|currentINode
decl_stmt|;
specifier|final
name|INodeFileUnderConstruction
name|copy
init|=
operator|new
name|INodeFileUnderConstruction
argument_list|(
name|uc
argument_list|,
name|uc
operator|.
name|getClientName
argument_list|()
argument_list|,
name|uc
operator|.
name|getClientMachine
argument_list|()
argument_list|,
name|uc
operator|.
name|getClientNode
argument_list|()
argument_list|)
decl_stmt|;
name|copy
operator|.
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
else|else
block|{
specifier|final
name|INodeFile
name|copy
init|=
operator|new
name|INodeFile
argument_list|(
name|currentINode
argument_list|)
decl_stmt|;
name|copy
operator|.
name|setBlocks
argument_list|(
literal|null
argument_list|)
expr_stmt|;
return|return
name|copy
return|;
block|}
block|}
block|}
end_class

end_unit

