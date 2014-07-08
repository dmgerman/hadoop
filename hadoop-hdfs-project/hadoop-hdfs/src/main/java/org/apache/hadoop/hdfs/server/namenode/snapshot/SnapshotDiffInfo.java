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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|SnapshotDiffReport
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
name|SnapshotDiffReport
operator|.
name|DiffReportEntry
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
name|SnapshotDiffReport
operator|.
name|DiffType
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
name|INode
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
name|INodeDirectory
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
name|INodeReference
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
name|DirectoryWithSnapshotFeature
operator|.
name|ChildrenDiff
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
name|util
operator|.
name|Diff
operator|.
name|ListType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|SignedBytes
import|;
end_import

begin_comment
comment|/**  * A class describing the difference between snapshots of a snapshottable  * directory.  */
end_comment

begin_class
DECL|class|SnapshotDiffInfo
class|class
name|SnapshotDiffInfo
block|{
comment|/** Compare two inodes based on their full names */
DECL|field|INODE_COMPARATOR
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|INode
argument_list|>
name|INODE_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|INode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|INode
name|left
parameter_list|,
name|INode
name|right
parameter_list|)
block|{
if|if
condition|(
name|left
operator|==
literal|null
condition|)
block|{
return|return
name|right
operator|==
literal|null
condition|?
literal|0
else|:
operator|-
literal|1
return|;
block|}
else|else
block|{
if|if
condition|(
name|right
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
name|int
name|cmp
init|=
name|compare
argument_list|(
name|left
operator|.
name|getParent
argument_list|()
argument_list|,
name|right
operator|.
name|getParent
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|cmp
operator|==
literal|0
condition|?
name|SignedBytes
operator|.
name|lexicographicalComparator
argument_list|()
operator|.
name|compare
argument_list|(
name|left
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|,
name|right
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|)
else|:
name|cmp
return|;
block|}
block|}
block|}
block|}
decl_stmt|;
DECL|class|RenameEntry
specifier|static
class|class
name|RenameEntry
block|{
DECL|field|sourcePath
specifier|private
name|byte
index|[]
index|[]
name|sourcePath
decl_stmt|;
DECL|field|targetPath
specifier|private
name|byte
index|[]
index|[]
name|targetPath
decl_stmt|;
DECL|method|setSource (INode source, byte[][] sourceParentPath)
name|void
name|setSource
parameter_list|(
name|INode
name|source
parameter_list|,
name|byte
index|[]
index|[]
name|sourceParentPath
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|sourcePath
operator|==
literal|null
argument_list|)
expr_stmt|;
name|sourcePath
operator|=
operator|new
name|byte
index|[
name|sourceParentPath
operator|.
name|length
operator|+
literal|1
index|]
index|[]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|sourceParentPath
argument_list|,
literal|0
argument_list|,
name|sourcePath
argument_list|,
literal|0
argument_list|,
name|sourceParentPath
operator|.
name|length
argument_list|)
expr_stmt|;
name|sourcePath
index|[
name|sourcePath
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|source
operator|.
name|getLocalNameBytes
argument_list|()
expr_stmt|;
block|}
DECL|method|setTarget (INode target, byte[][] targetParentPath)
name|void
name|setTarget
parameter_list|(
name|INode
name|target
parameter_list|,
name|byte
index|[]
index|[]
name|targetParentPath
parameter_list|)
block|{
name|targetPath
operator|=
operator|new
name|byte
index|[
name|targetParentPath
operator|.
name|length
operator|+
literal|1
index|]
index|[]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|targetParentPath
argument_list|,
literal|0
argument_list|,
name|targetPath
argument_list|,
literal|0
argument_list|,
name|targetParentPath
operator|.
name|length
argument_list|)
expr_stmt|;
name|targetPath
index|[
name|targetPath
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|target
operator|.
name|getLocalNameBytes
argument_list|()
expr_stmt|;
block|}
DECL|method|setTarget (byte[][] targetPath)
name|void
name|setTarget
parameter_list|(
name|byte
index|[]
index|[]
name|targetPath
parameter_list|)
block|{
name|this
operator|.
name|targetPath
operator|=
name|targetPath
expr_stmt|;
block|}
DECL|method|isRename ()
name|boolean
name|isRename
parameter_list|()
block|{
return|return
name|sourcePath
operator|!=
literal|null
operator|&&
name|targetPath
operator|!=
literal|null
return|;
block|}
DECL|method|getSourcePath ()
name|byte
index|[]
index|[]
name|getSourcePath
parameter_list|()
block|{
return|return
name|sourcePath
return|;
block|}
DECL|method|getTargetPath ()
name|byte
index|[]
index|[]
name|getTargetPath
parameter_list|()
block|{
return|return
name|targetPath
return|;
block|}
block|}
comment|/** The root directory of the snapshots */
DECL|field|snapshotRoot
specifier|private
specifier|final
name|INodeDirectory
name|snapshotRoot
decl_stmt|;
comment|/** The starting point of the difference */
DECL|field|from
specifier|private
specifier|final
name|Snapshot
name|from
decl_stmt|;
comment|/** The end point of the difference */
DECL|field|to
specifier|private
specifier|final
name|Snapshot
name|to
decl_stmt|;
comment|/**    * A map recording modified INodeFile and INodeDirectory and their relative    * path corresponding to the snapshot root. Sorted based on their names.    */
DECL|field|diffMap
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|INode
argument_list|,
name|byte
index|[]
index|[]
argument_list|>
name|diffMap
init|=
operator|new
name|TreeMap
argument_list|<
name|INode
argument_list|,
name|byte
index|[]
index|[]
argument_list|>
argument_list|(
name|INODE_COMPARATOR
argument_list|)
decl_stmt|;
comment|/**    * A map capturing the detailed difference about file creation/deletion.    * Each key indicates a directory whose children have been changed between    * the two snapshots, while its associated value is a {@link ChildrenDiff}    * storing the changes (creation/deletion) happened to the children (files).    */
DECL|field|dirDiffMap
specifier|private
specifier|final
name|Map
argument_list|<
name|INodeDirectory
argument_list|,
name|ChildrenDiff
argument_list|>
name|dirDiffMap
init|=
operator|new
name|HashMap
argument_list|<
name|INodeDirectory
argument_list|,
name|ChildrenDiff
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|renameMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|RenameEntry
argument_list|>
name|renameMap
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|RenameEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SnapshotDiffInfo (INodeDirectory snapshotRoot, Snapshot start, Snapshot end)
name|SnapshotDiffInfo
parameter_list|(
name|INodeDirectory
name|snapshotRoot
parameter_list|,
name|Snapshot
name|start
parameter_list|,
name|Snapshot
name|end
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|snapshotRoot
operator|.
name|isSnapshottable
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|snapshotRoot
operator|=
name|snapshotRoot
expr_stmt|;
name|this
operator|.
name|from
operator|=
name|start
expr_stmt|;
name|this
operator|.
name|to
operator|=
name|end
expr_stmt|;
block|}
comment|/** Add a dir-diff pair */
DECL|method|addDirDiff (INodeDirectory dir, byte[][] relativePath, ChildrenDiff diff)
name|void
name|addDirDiff
parameter_list|(
name|INodeDirectory
name|dir
parameter_list|,
name|byte
index|[]
index|[]
name|relativePath
parameter_list|,
name|ChildrenDiff
name|diff
parameter_list|)
block|{
name|dirDiffMap
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|diffMap
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
comment|// detect rename
for|for
control|(
name|INode
name|created
range|:
name|diff
operator|.
name|getList
argument_list|(
name|ListType
operator|.
name|CREATED
argument_list|)
control|)
block|{
if|if
condition|(
name|created
operator|.
name|isReference
argument_list|()
condition|)
block|{
name|RenameEntry
name|entry
init|=
name|getEntry
argument_list|(
name|created
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|.
name|getTargetPath
argument_list|()
operator|==
literal|null
condition|)
block|{
name|entry
operator|.
name|setTarget
argument_list|(
name|created
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|INode
name|deleted
range|:
name|diff
operator|.
name|getList
argument_list|(
name|ListType
operator|.
name|DELETED
argument_list|)
control|)
block|{
if|if
condition|(
name|deleted
operator|instanceof
name|INodeReference
operator|.
name|WithName
condition|)
block|{
name|RenameEntry
name|entry
init|=
name|getEntry
argument_list|(
name|deleted
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|entry
operator|.
name|setSource
argument_list|(
name|deleted
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getFrom ()
name|Snapshot
name|getFrom
parameter_list|()
block|{
return|return
name|from
return|;
block|}
DECL|method|getTo ()
name|Snapshot
name|getTo
parameter_list|()
block|{
return|return
name|to
return|;
block|}
DECL|method|getEntry (long inodeId)
specifier|private
name|RenameEntry
name|getEntry
parameter_list|(
name|long
name|inodeId
parameter_list|)
block|{
name|RenameEntry
name|entry
init|=
name|renameMap
operator|.
name|get
argument_list|(
name|inodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
condition|)
block|{
name|entry
operator|=
operator|new
name|RenameEntry
argument_list|()
expr_stmt|;
name|renameMap
operator|.
name|put
argument_list|(
name|inodeId
argument_list|,
name|entry
argument_list|)
expr_stmt|;
block|}
return|return
name|entry
return|;
block|}
DECL|method|setRenameTarget (long inodeId, byte[][] path)
name|void
name|setRenameTarget
parameter_list|(
name|long
name|inodeId
parameter_list|,
name|byte
index|[]
index|[]
name|path
parameter_list|)
block|{
name|getEntry
argument_list|(
name|inodeId
argument_list|)
operator|.
name|setTarget
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Add a modified file */
DECL|method|addFileDiff (INodeFile file, byte[][] relativePath)
name|void
name|addFileDiff
parameter_list|(
name|INodeFile
name|file
parameter_list|,
name|byte
index|[]
index|[]
name|relativePath
parameter_list|)
block|{
name|diffMap
operator|.
name|put
argument_list|(
name|file
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
block|}
comment|/** @return True if {@link #from} is earlier than {@link #to} */
DECL|method|isFromEarlier ()
name|boolean
name|isFromEarlier
parameter_list|()
block|{
return|return
name|Snapshot
operator|.
name|ID_COMPARATOR
operator|.
name|compare
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
operator|<
literal|0
return|;
block|}
comment|/**    * Generate a {@link SnapshotDiffReport} based on detailed diff information.    * @return A {@link SnapshotDiffReport} describing the difference    */
DECL|method|generateReport ()
specifier|public
name|SnapshotDiffReport
name|generateReport
parameter_list|()
block|{
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|diffReportList
init|=
operator|new
name|ArrayList
argument_list|<
name|DiffReportEntry
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|INode
name|node
range|:
name|diffMap
operator|.
name|keySet
argument_list|()
control|)
block|{
name|diffReportList
operator|.
name|add
argument_list|(
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|MODIFY
argument_list|,
name|diffMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|subList
init|=
name|generateReport
argument_list|(
name|dirDiffMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
argument_list|,
name|diffMap
operator|.
name|get
argument_list|(
name|node
argument_list|)
argument_list|,
name|isFromEarlier
argument_list|()
argument_list|,
name|renameMap
argument_list|)
decl_stmt|;
name|diffReportList
operator|.
name|addAll
argument_list|(
name|subList
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|SnapshotDiffReport
argument_list|(
name|snapshotRoot
operator|.
name|getFullPathName
argument_list|()
argument_list|,
name|Snapshot
operator|.
name|getSnapshotName
argument_list|(
name|from
argument_list|)
argument_list|,
name|Snapshot
operator|.
name|getSnapshotName
argument_list|(
name|to
argument_list|)
argument_list|,
name|diffReportList
argument_list|)
return|;
block|}
comment|/**    * Interpret the ChildrenDiff and generate a list of {@link DiffReportEntry}.    * @param dirDiff The ChildrenDiff.    * @param parentPath The relative path of the parent.    * @param fromEarlier True indicates {@code diff=later-earlier},    *                    False indicates {@code diff=earlier-later}    * @param renameMap A map containing information about rename operations.    * @return A list of {@link DiffReportEntry} as the diff report.    */
DECL|method|generateReport (ChildrenDiff dirDiff, byte[][] parentPath, boolean fromEarlier, Map<Long, RenameEntry> renameMap)
specifier|private
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|generateReport
parameter_list|(
name|ChildrenDiff
name|dirDiff
parameter_list|,
name|byte
index|[]
index|[]
name|parentPath
parameter_list|,
name|boolean
name|fromEarlier
parameter_list|,
name|Map
argument_list|<
name|Long
argument_list|,
name|RenameEntry
argument_list|>
name|renameMap
parameter_list|)
block|{
name|List
argument_list|<
name|DiffReportEntry
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|DiffReportEntry
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|INode
argument_list|>
name|created
init|=
name|dirDiff
operator|.
name|getList
argument_list|(
name|ListType
operator|.
name|CREATED
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|INode
argument_list|>
name|deleted
init|=
name|dirDiff
operator|.
name|getList
argument_list|(
name|ListType
operator|.
name|DELETED
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|fullPath
init|=
operator|new
name|byte
index|[
name|parentPath
operator|.
name|length
operator|+
literal|1
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|parentPath
argument_list|,
literal|0
argument_list|,
name|fullPath
argument_list|,
literal|0
argument_list|,
name|parentPath
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|INode
name|cnode
range|:
name|created
control|)
block|{
name|RenameEntry
name|entry
init|=
name|renameMap
operator|.
name|get
argument_list|(
name|cnode
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|==
literal|null
operator|||
operator|!
name|entry
operator|.
name|isRename
argument_list|()
condition|)
block|{
name|fullPath
index|[
name|fullPath
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|cnode
operator|.
name|getLocalNameBytes
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|DiffReportEntry
argument_list|(
name|fromEarlier
condition|?
name|DiffType
operator|.
name|CREATE
else|:
name|DiffType
operator|.
name|DELETE
argument_list|,
name|fullPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|INode
name|dnode
range|:
name|deleted
control|)
block|{
name|RenameEntry
name|entry
init|=
name|renameMap
operator|.
name|get
argument_list|(
name|dnode
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
operator|&&
name|entry
operator|.
name|isRename
argument_list|()
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
operator|new
name|DiffReportEntry
argument_list|(
name|DiffType
operator|.
name|RENAME
argument_list|,
name|fromEarlier
condition|?
name|entry
operator|.
name|getSourcePath
argument_list|()
else|:
name|entry
operator|.
name|getTargetPath
argument_list|()
argument_list|,
name|fromEarlier
condition|?
name|entry
operator|.
name|getTargetPath
argument_list|()
else|:
name|entry
operator|.
name|getSourcePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fullPath
index|[
name|fullPath
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|dnode
operator|.
name|getLocalNameBytes
argument_list|()
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|DiffReportEntry
argument_list|(
name|fromEarlier
condition|?
name|DiffType
operator|.
name|DELETE
else|:
name|DiffType
operator|.
name|CREATE
argument_list|,
name|fullPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|list
return|;
block|}
block|}
end_class

end_unit

