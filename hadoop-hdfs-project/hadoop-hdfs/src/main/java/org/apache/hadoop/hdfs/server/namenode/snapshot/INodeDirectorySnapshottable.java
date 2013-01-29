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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

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
name|Collections
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
name|Iterator
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
name|HadoopIllegalArgumentException
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
name|DFSUtil
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
name|snapshot
operator|.
name|diff
operator|.
name|Diff
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
name|ReadOnlyList
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
name|Time
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

begin_comment
comment|/**  * Directories where taking snapshots is allowed.  *   * Like other {@link INode} subclasses, this class is synchronized externally  * by the namesystem and FSDirectory locks.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|INodeDirectorySnapshottable
specifier|public
class|class
name|INodeDirectorySnapshottable
extends|extends
name|INodeDirectoryWithSnapshot
block|{
comment|/** Limit the number of snapshot per snapshottable directory. */
DECL|field|SNAPSHOT_LIMIT
specifier|static
specifier|final
name|int
name|SNAPSHOT_LIMIT
init|=
literal|1
operator|<<
literal|16
decl_stmt|;
comment|/** Cast INode to INodeDirectorySnapshottable. */
DECL|method|valueOf ( INode inode, String src)
specifier|static
specifier|public
name|INodeDirectorySnapshottable
name|valueOf
parameter_list|(
name|INode
name|inode
parameter_list|,
name|String
name|src
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodeDirectory
name|dir
init|=
name|INodeDirectory
operator|.
name|valueOf
argument_list|(
name|inode
argument_list|,
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isSnapshottable
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Directory is not a snapshottable directory: "
operator|+
name|src
argument_list|)
throw|;
block|}
return|return
operator|(
name|INodeDirectorySnapshottable
operator|)
name|dir
return|;
block|}
comment|/**    * A class describing the difference between snapshots of a snapshottable    * directory.    */
DECL|class|SnapshotDiffReport
specifier|public
specifier|static
class|class
name|SnapshotDiffReport
block|{
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
return|return
name|right
operator|==
literal|null
condition|?
literal|1
else|:
name|left
operator|.
name|compareTo
argument_list|(
name|right
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
decl_stmt|;
comment|/** The root directory of the snapshots */
DECL|field|snapshotRoot
specifier|private
specifier|final
name|INodeDirectorySnapshottable
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
comment|/**      * A map capturing the detailed difference. Each key indicates a directory      * whose metadata or children have been changed between the two snapshots,      * while its associated value is a {@link Diff} storing the changes happened      * to the children (files).      */
DECL|field|diffMap
specifier|private
specifier|final
name|SortedMap
argument_list|<
name|INodeDirectoryWithSnapshot
argument_list|,
name|ChildrenDiff
argument_list|>
name|diffMap
decl_stmt|;
DECL|method|SnapshotDiffReport (INodeDirectorySnapshottable snapshotRoot, Snapshot start, Snapshot end)
specifier|public
name|SnapshotDiffReport
parameter_list|(
name|INodeDirectorySnapshottable
name|snapshotRoot
parameter_list|,
name|Snapshot
name|start
parameter_list|,
name|Snapshot
name|end
parameter_list|)
block|{
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
name|this
operator|.
name|diffMap
operator|=
operator|new
name|TreeMap
argument_list|<
name|INodeDirectoryWithSnapshot
argument_list|,
name|ChildrenDiff
argument_list|>
argument_list|(
name|INODE_COMPARATOR
argument_list|)
expr_stmt|;
block|}
comment|/** Add a dir-diff pair into {@link #diffMap} */
DECL|method|addDiff (INodeDirectoryWithSnapshot dir, ChildrenDiff diff)
specifier|public
name|void
name|addDiff
parameter_list|(
name|INodeDirectoryWithSnapshot
name|dir
parameter_list|,
name|ChildrenDiff
name|diff
parameter_list|)
block|{
name|diffMap
operator|.
name|put
argument_list|(
name|dir
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
comment|/**      * dump the diff      */
DECL|method|dump ()
specifier|public
name|String
name|dump
parameter_list|()
block|{
name|StringBuilder
name|strBuffer
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|String
name|fromStr
init|=
name|from
operator|==
literal|null
condition|?
literal|"current directory"
else|:
literal|"snapshot "
operator|+
name|from
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|String
name|toStr
init|=
name|to
operator|==
literal|null
condition|?
literal|"current directory"
else|:
literal|"snapshot "
operator|+
name|to
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|strBuffer
operator|.
name|append
argument_list|(
literal|"Diffence between snapshot "
operator|+
name|fromStr
operator|+
literal|" and "
operator|+
name|toStr
operator|+
literal|" under directory "
operator|+
name|snapshotRoot
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|":\n"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|diffMap
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|INodeDirectoryWithSnapshot
argument_list|,
name|ChildrenDiff
argument_list|>
name|entry
range|:
name|diffMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|strBuffer
operator|.
name|append
argument_list|(
literal|"M\t"
operator|+
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|printDiff
argument_list|(
name|strBuffer
argument_list|,
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|from
operator|==
literal|null
operator|||
operator|(
name|to
operator|!=
literal|null
operator|&&
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
operator|>
literal|0
operator|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|strBuffer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**    * Snapshots of this directory in ascending order of snapshot names.    * Note that snapshots in ascending order of snapshot id are stored in    * {@link INodeDirectoryWithSnapshot}.diffs (a private field).    */
DECL|field|snapshotsByNames
specifier|private
specifier|final
name|List
argument_list|<
name|Snapshot
argument_list|>
name|snapshotsByNames
init|=
operator|new
name|ArrayList
argument_list|<
name|Snapshot
argument_list|>
argument_list|()
decl_stmt|;
comment|/**    * @return {@link #snapshotsByNames}    */
DECL|method|getSnapshotsByNames ()
name|ReadOnlyList
argument_list|<
name|Snapshot
argument_list|>
name|getSnapshotsByNames
parameter_list|()
block|{
return|return
name|ReadOnlyList
operator|.
name|Util
operator|.
name|asReadOnlyList
argument_list|(
name|this
operator|.
name|snapshotsByNames
argument_list|)
return|;
block|}
comment|/** Number of snapshots allowed. */
DECL|field|snapshotQuota
specifier|private
name|int
name|snapshotQuota
init|=
name|SNAPSHOT_LIMIT
decl_stmt|;
DECL|method|INodeDirectorySnapshottable (INodeDirectory dir)
specifier|public
name|INodeDirectorySnapshottable
parameter_list|(
name|INodeDirectory
name|dir
parameter_list|)
block|{
name|super
argument_list|(
name|dir
argument_list|,
literal|true
argument_list|,
name|dir
operator|instanceof
name|INodeDirectoryWithSnapshot
condition|?
operator|(
operator|(
name|INodeDirectoryWithSnapshot
operator|)
name|dir
operator|)
operator|.
name|getDiffs
argument_list|()
else|:
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/** @return the number of existing snapshots. */
DECL|method|getNumSnapshots ()
specifier|public
name|int
name|getNumSnapshots
parameter_list|()
block|{
return|return
name|snapshotsByNames
operator|.
name|size
argument_list|()
return|;
block|}
DECL|method|searchSnapshot (byte[] snapshotName)
specifier|private
name|int
name|searchSnapshot
parameter_list|(
name|byte
index|[]
name|snapshotName
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|binarySearch
argument_list|(
name|snapshotsByNames
argument_list|,
name|snapshotName
argument_list|)
return|;
block|}
comment|/** @return the snapshot with the given name. */
DECL|method|getSnapshot (byte[] snapshotName)
specifier|public
name|Snapshot
name|getSnapshot
parameter_list|(
name|byte
index|[]
name|snapshotName
parameter_list|)
block|{
specifier|final
name|int
name|i
init|=
name|searchSnapshot
argument_list|(
name|snapshotName
argument_list|)
decl_stmt|;
return|return
name|i
operator|<
literal|0
condition|?
literal|null
else|:
name|snapshotsByNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
comment|/**    * Rename a snapshot    * @param path    *          The directory path where the snapshot was taken. Used for    *          generating exception message.    * @param oldName    *          Old name of the snapshot    * @param newName    *          New name the snapshot will be renamed to    * @throws SnapshotException    *           Throw SnapshotException when either the snapshot with the old    *           name does not exist or a snapshot with the new name already    *           exists    */
DECL|method|renameSnapshot (String path, String oldName, String newName)
specifier|public
name|void
name|renameSnapshot
parameter_list|(
name|String
name|path
parameter_list|,
name|String
name|oldName
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|SnapshotException
block|{
if|if
condition|(
name|newName
operator|.
name|equals
argument_list|(
name|oldName
argument_list|)
condition|)
block|{
return|return;
block|}
specifier|final
name|int
name|indexOfOld
init|=
name|searchSnapshot
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|oldName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfOld
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"The snapshot "
operator|+
name|oldName
operator|+
literal|" does not exist for directory "
operator|+
name|path
argument_list|)
throw|;
block|}
else|else
block|{
name|int
name|indexOfNew
init|=
name|searchSnapshot
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|newName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfNew
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"The snapshot "
operator|+
name|newName
operator|+
literal|" already exists for directory "
operator|+
name|path
argument_list|)
throw|;
block|}
comment|// remove the one with old name from snapshotsByNames
name|Snapshot
name|snapshot
init|=
name|snapshotsByNames
operator|.
name|remove
argument_list|(
name|indexOfOld
argument_list|)
decl_stmt|;
specifier|final
name|INodeDirectory
name|ssRoot
init|=
name|snapshot
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|ssRoot
operator|.
name|setLocalName
argument_list|(
name|newName
argument_list|)
expr_stmt|;
name|indexOfNew
operator|=
operator|-
name|indexOfNew
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|indexOfNew
operator|<=
name|indexOfOld
condition|)
block|{
name|snapshotsByNames
operator|.
name|add
argument_list|(
name|indexOfNew
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// indexOfNew> indexOfOld
name|snapshotsByNames
operator|.
name|add
argument_list|(
name|indexOfNew
operator|-
literal|1
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getSnapshotQuota ()
specifier|public
name|int
name|getSnapshotQuota
parameter_list|()
block|{
return|return
name|snapshotQuota
return|;
block|}
DECL|method|setSnapshotQuota (int snapshotQuota)
specifier|public
name|void
name|setSnapshotQuota
parameter_list|(
name|int
name|snapshotQuota
parameter_list|)
block|{
if|if
condition|(
name|snapshotQuota
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Cannot set snapshot quota to "
operator|+
name|snapshotQuota
operator|+
literal|"< 0"
argument_list|)
throw|;
block|}
name|this
operator|.
name|snapshotQuota
operator|=
name|snapshotQuota
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|isSnapshottable ()
specifier|public
name|boolean
name|isSnapshottable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Simply add a snapshot into the {@link #snapshotsByNames}. Used by FSImage    * loading.    */
DECL|method|addSnapshot (Snapshot snapshot)
name|void
name|addSnapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|this
operator|.
name|snapshotsByNames
operator|.
name|add
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
block|}
comment|/** Add a snapshot. */
DECL|method|addSnapshot (int id, String name)
name|Snapshot
name|addSnapshot
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|SnapshotException
block|{
comment|//check snapshot quota
specifier|final
name|int
name|n
init|=
name|getNumSnapshots
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|+
literal|1
operator|>
name|snapshotQuota
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Failed to add snapshot: there are already "
operator|+
name|n
operator|+
literal|" snapshot(s) and the snapshot quota is "
operator|+
name|snapshotQuota
argument_list|)
throw|;
block|}
specifier|final
name|Snapshot
name|s
init|=
operator|new
name|Snapshot
argument_list|(
name|id
argument_list|,
name|name
argument_list|,
name|this
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|nameBytes
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|getLocalNameBytes
argument_list|()
decl_stmt|;
specifier|final
name|int
name|i
init|=
name|searchSnapshot
argument_list|(
name|nameBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Failed to add snapshot: there is already a "
operator|+
literal|"snapshot with the same name \""
operator|+
name|name
operator|+
literal|"\"."
argument_list|)
throw|;
block|}
name|getDiffs
argument_list|()
operator|.
name|addSnapshotDiff
argument_list|(
name|s
argument_list|,
name|this
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|snapshotsByNames
operator|.
name|add
argument_list|(
operator|-
name|i
operator|-
literal|1
argument_list|,
name|s
argument_list|)
expr_stmt|;
comment|//set modification time
specifier|final
name|long
name|timestamp
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|updateModificationTime
argument_list|(
name|timestamp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|updateModificationTime
argument_list|(
name|timestamp
argument_list|,
literal|null
argument_list|)
expr_stmt|;
return|return
name|s
return|;
block|}
comment|/**    * Remove the snapshot with the given name from {@link #snapshotsByNames},    * and delete all the corresponding DirectoryDiff.    *     * @param snapshotName The name of the snapshot to be removed    * @param collectedBlocks Used to collect information to update blocksMap    * @return The removed snapshot. Null if no snapshot with the given name     *         exists.    */
DECL|method|removeSnapshot (String snapshotName, BlocksMapUpdateInfo collectedBlocks)
name|Snapshot
name|removeSnapshot
parameter_list|(
name|String
name|snapshotName
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
throws|throws
name|SnapshotException
block|{
specifier|final
name|int
name|indexOfOld
init|=
name|searchSnapshot
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|snapshotName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfOld
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Cannot delete snapshot "
operator|+
name|snapshotName
operator|+
literal|" from path "
operator|+
name|this
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|": the snapshot does not exist."
argument_list|)
throw|;
block|}
else|else
block|{
name|Snapshot
name|snapshot
init|=
name|snapshotsByNames
operator|.
name|remove
argument_list|(
name|indexOfOld
argument_list|)
decl_stmt|;
name|deleteDiffsForSnapshot
argument_list|(
name|snapshot
argument_list|,
name|this
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
return|return
name|snapshot
return|;
block|}
block|}
comment|/**    * Recursively delete DirectoryDiff associated with the given snapshot under a    * directory    */
DECL|method|deleteDiffsForSnapshot (Snapshot snapshot, INodeDirectory dir, BlocksMapUpdateInfo collectedBlocks)
specifier|private
name|void
name|deleteDiffsForSnapshot
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|INodeDirectory
name|dir
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
block|{
if|if
condition|(
name|dir
operator|instanceof
name|INodeDirectoryWithSnapshot
condition|)
block|{
name|INodeDirectoryWithSnapshot
name|sdir
init|=
operator|(
name|INodeDirectoryWithSnapshot
operator|)
name|dir
decl_stmt|;
name|sdir
operator|.
name|getDiffs
argument_list|()
operator|.
name|deleteSnapshotDiff
argument_list|(
name|snapshot
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
block|}
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|children
init|=
name|dir
operator|.
name|getChildrenList
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|INode
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|INodeDirectory
condition|)
block|{
name|deleteDiffsForSnapshot
argument_list|(
name|snapshot
argument_list|,
operator|(
name|INodeDirectory
operator|)
name|child
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Compute the difference between two snapshots (or a snapshot and the current    * directory) of the directory.    *     * @param from The name of the start point of the comparison. Null indicating    *          the current tree.    * @param to The name of the end point. Null indicating the current tree.    * @return The difference between the start/end points.    * @throws SnapshotException If there is no snapshot matching the starting    *           point, or if endSnapshotName is not null but cannot be identified    *           as a previous snapshot.    */
DECL|method|computeDiff (final String from, final String to)
name|SnapshotDiffReport
name|computeDiff
parameter_list|(
specifier|final
name|String
name|from
parameter_list|,
specifier|final
name|String
name|to
parameter_list|)
throws|throws
name|SnapshotException
block|{
name|Snapshot
name|fromSnapshot
init|=
name|getSnapshotByName
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|Snapshot
name|toSnapshot
init|=
name|getSnapshotByName
argument_list|(
name|to
argument_list|)
decl_stmt|;
name|SnapshotDiffReport
name|diffs
init|=
operator|new
name|SnapshotDiffReport
argument_list|(
name|this
argument_list|,
name|fromSnapshot
argument_list|,
name|toSnapshot
argument_list|)
decl_stmt|;
name|computeDiffInDir
argument_list|(
name|this
argument_list|,
name|diffs
argument_list|)
expr_stmt|;
return|return
name|diffs
return|;
block|}
comment|/**    * Find the snapshot matching the given name.    *     * @param snapshotName The name of the snapshot.    * @return The corresponding snapshot. Null if snapshotName is null or empty.    * @throws SnapshotException If snapshotName is not null or empty, but there    *           is no snapshot matching the name.    */
DECL|method|getSnapshotByName (String snapshotName)
specifier|private
name|Snapshot
name|getSnapshotByName
parameter_list|(
name|String
name|snapshotName
parameter_list|)
throws|throws
name|SnapshotException
block|{
name|Snapshot
name|s
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|snapshotName
operator|!=
literal|null
operator|&&
operator|!
name|snapshotName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|int
name|index
init|=
name|searchSnapshot
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|snapshotName
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"Cannot find the snapshot of directory "
operator|+
name|this
operator|.
name|getFullPathName
argument_list|()
operator|+
literal|" with name "
operator|+
name|snapshotName
argument_list|)
throw|;
block|}
name|s
operator|=
name|snapshotsByNames
operator|.
name|get
argument_list|(
name|index
argument_list|)
expr_stmt|;
block|}
return|return
name|s
return|;
block|}
comment|/**    * Recursively compute the difference between snapshots under a given    * directory.    * @param dir The directory under which the diff is computed.    * @param diffReport data structure used to store the diff.    */
DECL|method|computeDiffInDir (INodeDirectory dir, SnapshotDiffReport diffReport)
specifier|private
name|void
name|computeDiffInDir
parameter_list|(
name|INodeDirectory
name|dir
parameter_list|,
name|SnapshotDiffReport
name|diffReport
parameter_list|)
block|{
name|ChildrenDiff
name|diff
init|=
operator|new
name|ChildrenDiff
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|INodeDirectoryWithSnapshot
condition|)
block|{
name|boolean
name|change
init|=
operator|(
operator|(
name|INodeDirectoryWithSnapshot
operator|)
name|dir
operator|)
operator|.
name|computeDiffBetweenSnapshots
argument_list|(
name|diffReport
operator|.
name|from
argument_list|,
name|diffReport
operator|.
name|to
argument_list|,
name|diff
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
condition|)
block|{
name|diffReport
operator|.
name|addDiff
argument_list|(
operator|(
name|INodeDirectoryWithSnapshot
operator|)
name|dir
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
name|ReadOnlyList
argument_list|<
name|INode
argument_list|>
name|children
init|=
name|dir
operator|.
name|getChildrenList
argument_list|(
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
name|INode
name|child
range|:
name|children
control|)
block|{
if|if
condition|(
name|child
operator|instanceof
name|INodeDirectory
operator|&&
name|diff
operator|.
name|searchCreated
argument_list|(
name|child
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// Compute diff recursively for children that are directories. We do not
comment|// need to compute diff for those contained in the created list since
comment|// directory contained in the created list must be new created.
name|computeDiffInDir
argument_list|(
operator|(
name|INodeDirectory
operator|)
name|child
argument_list|,
name|diffReport
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Replace itself with {@link INodeDirectoryWithSnapshot} or    * {@link INodeDirectory} depending on the latest snapshot.    */
DECL|method|replaceSelf (final Snapshot latest)
name|void
name|replaceSelf
parameter_list|(
specifier|final
name|Snapshot
name|latest
parameter_list|)
block|{
if|if
condition|(
name|latest
operator|==
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|getLastSnapshot
argument_list|()
operator|==
literal|null
argument_list|,
literal|"latest == null but getLastSnapshot() != null, this=%s"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|replaceSelf4INodeDirectory
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|replaceSelf4INodeDirectoryWithSnapshot
argument_list|(
name|latest
argument_list|)
operator|.
name|recordModification
argument_list|(
name|latest
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix, Snapshot snapshot)
specifier|public
name|void
name|dumpTreeRecursively
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|,
name|Snapshot
name|snapshot
parameter_list|)
block|{
name|super
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|,
name|snapshot
argument_list|)
expr_stmt|;
if|if
condition|(
name|snapshot
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
name|int
name|n
init|=
literal|0
decl_stmt|;
for|for
control|(
name|DirectoryDiff
name|diff
range|:
name|getDiffs
argument_list|()
control|)
block|{
if|if
condition|(
name|diff
operator|.
name|isSnapshotRoot
argument_list|()
condition|)
block|{
name|n
operator|++
expr_stmt|;
block|}
block|}
name|out
operator|.
name|print
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|n
operator|<=
literal|1
condition|?
literal|" snapshot of "
else|:
literal|" snapshots of "
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|getLocalName
argument_list|()
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|name
operator|.
name|isEmpty
argument_list|()
condition|?
literal|"/"
else|:
name|name
argument_list|)
expr_stmt|;
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|,
operator|new
name|Iterable
argument_list|<
name|Pair
argument_list|<
name|?
extends|extends
name|INode
argument_list|,
name|Snapshot
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|?
extends|extends
name|INode
argument_list|,
name|Snapshot
argument_list|>
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|Pair
argument_list|<
name|?
extends|extends
name|INode
argument_list|,
name|Snapshot
argument_list|>
argument_list|>
argument_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|DirectoryDiff
argument_list|>
name|i
init|=
name|getDiffs
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|private
name|DirectoryDiff
name|next
init|=
name|findNext
argument_list|()
decl_stmt|;
specifier|private
name|DirectoryDiff
name|findNext
parameter_list|()
block|{
for|for
control|(
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|DirectoryDiff
name|diff
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|diff
operator|.
name|isSnapshotRoot
argument_list|()
condition|)
block|{
return|return
name|diff
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|next
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Pair
argument_list|<
name|INodeDirectory
argument_list|,
name|Snapshot
argument_list|>
name|next
parameter_list|()
block|{
specifier|final
name|Snapshot
name|s
init|=
name|next
operator|.
name|snapshot
decl_stmt|;
specifier|final
name|Pair
argument_list|<
name|INodeDirectory
argument_list|,
name|Snapshot
argument_list|>
name|pair
init|=
operator|new
name|Pair
argument_list|<
name|INodeDirectory
argument_list|,
name|Snapshot
argument_list|>
argument_list|(
name|s
operator|.
name|getRoot
argument_list|()
argument_list|,
name|s
argument_list|)
decl_stmt|;
name|next
operator|=
name|findNext
argument_list|()
expr_stmt|;
return|return
name|pair
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
empty_stmt|;
block|}
block|}
block|)
empty_stmt|;
block|}
block|}
end_class

unit|}
end_unit

