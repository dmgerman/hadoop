begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
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
name|fs
operator|.
name|permission
operator|.
name|PermissionStatus
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
name|DFSUtil
import|;
end_import

begin_comment
comment|/**  * An {@link INode} representing a symbolic link.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeSymlink
specifier|public
class|class
name|INodeSymlink
extends|extends
name|INode
block|{
DECL|field|symlink
specifier|private
specifier|final
name|byte
index|[]
name|symlink
decl_stmt|;
comment|// The target URI
DECL|method|INodeSymlink (String value, long mtime, long atime, PermissionStatus permissions)
name|INodeSymlink
parameter_list|(
name|String
name|value
parameter_list|,
name|long
name|mtime
parameter_list|,
name|long
name|atime
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|)
block|{
name|super
argument_list|(
name|permissions
argument_list|,
name|mtime
argument_list|,
name|atime
argument_list|)
expr_stmt|;
name|this
operator|.
name|symlink
operator|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
DECL|method|INodeSymlink (INodeSymlink that)
name|INodeSymlink
parameter_list|(
name|INodeSymlink
name|that
parameter_list|)
block|{
name|super
argument_list|(
name|that
argument_list|)
expr_stmt|;
comment|//copy symlink
name|this
operator|.
name|symlink
operator|=
operator|new
name|byte
index|[
name|that
operator|.
name|symlink
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|that
operator|.
name|symlink
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|symlink
argument_list|,
literal|0
argument_list|,
name|that
operator|.
name|symlink
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createSnapshotCopy ()
specifier|public
name|Pair
argument_list|<
name|INodeSymlink
argument_list|,
name|INodeSymlink
argument_list|>
name|createSnapshotCopy
parameter_list|()
block|{
return|return
operator|new
name|Pair
argument_list|<
name|INodeSymlink
argument_list|,
name|INodeSymlink
argument_list|>
argument_list|(
name|this
argument_list|,
operator|new
name|INodeSymlink
argument_list|(
name|this
argument_list|)
argument_list|)
return|;
block|}
comment|/** @return true unconditionally. */
annotation|@
name|Override
DECL|method|isSymlink ()
specifier|public
name|boolean
name|isSymlink
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|getSymlinkString ()
specifier|public
name|String
name|getSymlinkString
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|symlink
argument_list|)
return|;
block|}
DECL|method|getSymlink ()
specifier|public
name|byte
index|[]
name|getSymlink
parameter_list|()
block|{
return|return
name|symlink
return|;
block|}
annotation|@
name|Override
DECL|method|spaceConsumedInTree (DirCounts counts)
name|DirCounts
name|spaceConsumedInTree
parameter_list|(
name|DirCounts
name|counts
parameter_list|)
block|{
name|counts
operator|.
name|nsCount
operator|+=
literal|1
expr_stmt|;
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|collectSubtreeBlocksAndClear (BlocksMapUpdateInfo info)
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|BlocksMapUpdateInfo
name|info
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary (long[] summary)
name|long
index|[]
name|computeContentSummary
parameter_list|(
name|long
index|[]
name|summary
parameter_list|)
block|{
name|summary
index|[
literal|1
index|]
operator|++
expr_stmt|;
comment|// Increment the file count
return|return
name|summary
return|;
block|}
block|}
end_class

end_unit

