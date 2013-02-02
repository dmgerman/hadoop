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
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|ArrayList
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
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
name|protocol
operator|.
name|SnapshottableDirectoryStatus
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
name|FSDirectory
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
name|FSNamesystem
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
name|INode
operator|.
name|BlocksMapUpdateInfo
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
name|INodeDirectory
operator|.
name|INodesInPath
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
name|INodeDirectorySnapshottable
operator|.
name|SnapshotDiffInfo
import|;
end_import

begin_comment
comment|/**  * Manage snapshottable directories and their snapshots.  *   * This class includes operations that create, access, modify snapshots and/or  * snapshot-related data. In general, the locking structure of snapshot  * operations is:<br>  *   * 1. Lock the {@link FSNamesystem} lock in {@link FSNamesystem} before calling  * into {@link SnapshotManager} methods.<br>  * 2. Lock the {@link FSDirectory} lock for the {@link SnapshotManager} methods  * if necessary.  */
end_comment

begin_class
DECL|class|SnapshotManager
specifier|public
class|class
name|SnapshotManager
implements|implements
name|SnapshotStats
block|{
DECL|field|fsdir
specifier|private
specifier|final
name|FSDirectory
name|fsdir
decl_stmt|;
DECL|field|numSnapshottableDirs
specifier|private
specifier|final
name|AtomicInteger
name|numSnapshottableDirs
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|numSnapshots
specifier|private
specifier|final
name|AtomicInteger
name|numSnapshots
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
DECL|field|snapshotCounter
specifier|private
name|int
name|snapshotCounter
init|=
literal|0
decl_stmt|;
comment|/** All snapshottable directories in the namesystem. */
DECL|field|snapshottables
specifier|private
specifier|final
name|List
argument_list|<
name|INodeDirectorySnapshottable
argument_list|>
name|snapshottables
init|=
operator|new
name|ArrayList
argument_list|<
name|INodeDirectorySnapshottable
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|SnapshotManager (final FSDirectory fsdir)
specifier|public
name|SnapshotManager
parameter_list|(
specifier|final
name|FSDirectory
name|fsdir
parameter_list|)
block|{
name|this
operator|.
name|fsdir
operator|=
name|fsdir
expr_stmt|;
block|}
comment|/**    * Set the given directory as a snapshottable directory.    * If the path is already a snapshottable directory, update the quota.    */
DECL|method|setSnapshottable (final String path)
specifier|public
name|void
name|setSnapshottable
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodesInPath
name|iip
init|=
name|fsdir
operator|.
name|getLastINodeInPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|INodeDirectory
name|d
init|=
name|INodeDirectory
operator|.
name|valueOf
argument_list|(
name|iip
operator|.
name|getINode
argument_list|(
literal|0
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|.
name|isSnapshottable
argument_list|()
condition|)
block|{
comment|//The directory is already a snapshottable directory.
operator|(
operator|(
name|INodeDirectorySnapshottable
operator|)
name|d
operator|)
operator|.
name|setSnapshotQuota
argument_list|(
name|INodeDirectorySnapshottable
operator|.
name|SNAPSHOT_LIMIT
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|INodeDirectorySnapshottable
name|s
init|=
name|d
operator|.
name|replaceSelf4INodeDirectorySnapshottable
argument_list|(
name|iip
operator|.
name|getLatestSnapshot
argument_list|()
argument_list|)
decl_stmt|;
name|snapshottables
operator|.
name|add
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|numSnapshottableDirs
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
comment|/**    * Set the given snapshottable directory to non-snapshottable.    *     * @throws SnapshotException if there are snapshots in the directory.    */
DECL|method|resetSnapshottable (final String path )
specifier|public
name|void
name|resetSnapshottable
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodesInPath
name|iip
init|=
name|fsdir
operator|.
name|getLastINodeInPath
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|INodeDirectorySnapshottable
name|s
init|=
name|INodeDirectorySnapshottable
operator|.
name|valueOf
argument_list|(
name|iip
operator|.
name|getINode
argument_list|(
literal|0
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|getNumSnapshots
argument_list|()
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"The directory "
operator|+
name|path
operator|+
literal|" has snapshot(s). "
operator|+
literal|"Please redo the operation after removing all the snapshots."
argument_list|)
throw|;
block|}
name|s
operator|.
name|replaceSelf
argument_list|(
name|iip
operator|.
name|getLatestSnapshot
argument_list|()
argument_list|)
expr_stmt|;
name|snapshottables
operator|.
name|remove
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|numSnapshottableDirs
operator|.
name|getAndDecrement
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a snapshot of the given path.    * @param path    *          The directory path where the snapshot will be taken.    * @param snapshotName    *          The name of the snapshot.    * @throws IOException    *           Throw IOException when 1) the given path does not lead to an    *           existing snapshottable directory, and/or 2) there exists a    *           snapshot with the given name for the directory, and/or 3)    *           snapshot number exceeds quota    */
DECL|method|createSnapshot (final String path, final String snapshotName )
specifier|public
name|void
name|createSnapshot
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|snapshotName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Find the source root directory path where the snapshot is taken.
specifier|final
name|INodesInPath
name|i
init|=
name|fsdir
operator|.
name|getINodesInPath4Write
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|INodeDirectorySnapshottable
name|srcRoot
init|=
name|INodeDirectorySnapshottable
operator|.
name|valueOf
argument_list|(
name|i
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|srcRoot
operator|.
name|addSnapshot
argument_list|(
name|snapshotCounter
argument_list|,
name|snapshotName
argument_list|)
expr_stmt|;
comment|//create success, update id
name|snapshotCounter
operator|++
expr_stmt|;
name|numSnapshots
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
comment|/**    * Delete a snapshot for a snapshottable directory    * @param path Path to the directory where the snapshot was taken    * @param snapshotName Name of the snapshot to be deleted    * @param collectedBlocks Used to collect information to update blocksMap     * @throws IOException    */
DECL|method|deleteSnapshot (final String path, final String snapshotName, BlocksMapUpdateInfo collectedBlocks)
specifier|public
name|void
name|deleteSnapshot
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|snapshotName
parameter_list|,
name|BlocksMapUpdateInfo
name|collectedBlocks
parameter_list|)
throws|throws
name|IOException
block|{
comment|// parse the path, and check if the path is a snapshot path
name|INodesInPath
name|inodesInPath
init|=
name|fsdir
operator|.
name|getINodesInPath4Write
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
comment|// transfer the inode for path to an INodeDirectorySnapshottable.
comment|// the INodeDirectorySnapshottable#valueOf method will throw Exception
comment|// if the path is not for a snapshottable directory
name|INodeDirectorySnapshottable
name|dir
init|=
name|INodeDirectorySnapshottable
operator|.
name|valueOf
argument_list|(
name|inodesInPath
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|dir
operator|.
name|removeSnapshot
argument_list|(
name|snapshotName
argument_list|,
name|collectedBlocks
argument_list|)
expr_stmt|;
name|numSnapshots
operator|.
name|getAndDecrement
argument_list|()
expr_stmt|;
block|}
comment|/**    * Rename the given snapshot    * @param path    *          The directory path where the snapshot was taken    * @param oldSnapshotName    *          Old name of the snapshot    * @param newSnapshotName    *          New name of the snapshot    * @throws IOException    *           Throw IOException when 1) the given path does not lead to an    *           existing snapshottable directory, and/or 2) the snapshot with the    *           old name does not exist for the directory, and/or 3) there exists    *           a snapshot with the new name for the directory    */
DECL|method|renameSnapshot (final String path, final String oldSnapshotName, final String newSnapshotName)
specifier|public
name|void
name|renameSnapshot
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|oldSnapshotName
parameter_list|,
specifier|final
name|String
name|newSnapshotName
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Find the source root directory path where the snapshot was taken.
comment|// All the check for path has been included in the valueOf method.
specifier|final
name|INodeDirectorySnapshottable
name|srcRoot
init|=
name|INodeDirectorySnapshottable
operator|.
name|valueOf
argument_list|(
name|fsdir
operator|.
name|getINode
argument_list|(
name|path
argument_list|)
argument_list|,
name|path
argument_list|)
decl_stmt|;
comment|// Note that renameSnapshot and createSnapshot are synchronized externally
comment|// through FSNamesystem's write lock
name|srcRoot
operator|.
name|renameSnapshot
argument_list|(
name|path
argument_list|,
name|oldSnapshotName
argument_list|,
name|newSnapshotName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getNumSnapshottableDirs ()
specifier|public
name|long
name|getNumSnapshottableDirs
parameter_list|()
block|{
return|return
name|numSnapshottableDirs
operator|.
name|get
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getNumSnapshots ()
specifier|public
name|long
name|getNumSnapshots
parameter_list|()
block|{
return|return
name|numSnapshots
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * Write {@link #snapshotCounter}, {@link #numSnapshots}, and    * {@link #numSnapshottableDirs} to the DataOutput.    */
DECL|method|write (DataOutput out)
specifier|public
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|out
operator|.
name|writeInt
argument_list|(
name|snapshotCounter
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numSnapshots
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|numSnapshottableDirs
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Read values of {@link #snapshotCounter}, {@link #numSnapshots}, and    * {@link #numSnapshottableDirs} from the DataInput    */
DECL|method|read (DataInput in)
specifier|public
name|void
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|snapshotCounter
operator|=
name|in
operator|.
name|readInt
argument_list|()
expr_stmt|;
name|numSnapshots
operator|.
name|set
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
name|numSnapshottableDirs
operator|.
name|set
argument_list|(
name|in
operator|.
name|readInt
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * List all the snapshottable directories that are owned by the current user.    * @param userName Current user name.    * @return Snapshottable directories that are owned by the current user,    *         represented as an array of {@link SnapshottableDirectoryStatus}. If    *         {@code userName} is null, return all the snapshottable dirs.    */
DECL|method|getSnapshottableDirListing ( String userName)
specifier|public
name|SnapshottableDirectoryStatus
index|[]
name|getSnapshottableDirListing
parameter_list|(
name|String
name|userName
parameter_list|)
block|{
if|if
condition|(
name|snapshottables
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|SnapshottableDirectoryStatus
argument_list|>
name|statusList
init|=
operator|new
name|ArrayList
argument_list|<
name|SnapshottableDirectoryStatus
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|INodeDirectorySnapshottable
name|dir
range|:
name|snapshottables
control|)
block|{
if|if
condition|(
name|userName
operator|==
literal|null
operator|||
name|userName
operator|.
name|equals
argument_list|(
name|dir
operator|.
name|getUserName
argument_list|()
argument_list|)
condition|)
block|{
name|SnapshottableDirectoryStatus
name|status
init|=
operator|new
name|SnapshottableDirectoryStatus
argument_list|(
name|dir
operator|.
name|getModificationTime
argument_list|()
argument_list|,
name|dir
operator|.
name|getAccessTime
argument_list|()
argument_list|,
name|dir
operator|.
name|getFsPermission
argument_list|()
argument_list|,
name|dir
operator|.
name|getUserName
argument_list|()
argument_list|,
name|dir
operator|.
name|getGroupName
argument_list|()
argument_list|,
name|dir
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|,
name|dir
operator|.
name|getNumSnapshots
argument_list|()
argument_list|,
name|dir
operator|.
name|getSnapshotQuota
argument_list|()
argument_list|,
name|dir
operator|.
name|getParent
argument_list|()
operator|==
literal|null
condition|?
name|INode
operator|.
name|EMPTY_BYTES
else|:
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|dir
operator|.
name|getParent
argument_list|()
operator|.
name|getFullPathName
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|statusList
operator|.
name|add
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|statusList
operator|.
name|toArray
argument_list|(
operator|new
name|SnapshottableDirectoryStatus
index|[
name|statusList
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|/**    * Remove snapshottable directories from {@link #snapshottables}    * @param toRemoveList A list of INodeDirectorySnapshottable to be removed    */
DECL|method|removeSnapshottableDirs ( List<INodeDirectorySnapshottable> toRemoveList)
specifier|public
name|void
name|removeSnapshottableDirs
parameter_list|(
name|List
argument_list|<
name|INodeDirectorySnapshottable
argument_list|>
name|toRemoveList
parameter_list|)
block|{
if|if
condition|(
name|toRemoveList
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|snapshottables
operator|.
name|removeAll
argument_list|(
name|toRemoveList
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Compute the difference between two snapshots of a directory, or between a    * snapshot of the directory and its current tree.    */
DECL|method|diff (final String path, final String from, final String to)
specifier|public
name|SnapshotDiffInfo
name|diff
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|String
name|from
parameter_list|,
specifier|final
name|String
name|to
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|from
operator|==
literal|null
operator|||
name|from
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|to
operator|==
literal|null
operator|||
name|to
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
comment|// both fromSnapshot and toSnapshot indicate the current tree
return|return
literal|null
return|;
block|}
comment|// if the start point is equal to the end point, return null
if|if
condition|(
name|from
operator|.
name|equals
argument_list|(
name|to
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Find the source root directory path where the snapshots were taken.
comment|// All the check for path has been included in the valueOf method.
name|INodesInPath
name|inodesInPath
init|=
name|fsdir
operator|.
name|getINodesInPath4Write
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|INodeDirectorySnapshottable
name|snapshotRoot
init|=
name|INodeDirectorySnapshottable
operator|.
name|valueOf
argument_list|(
name|inodesInPath
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
return|return
name|snapshotRoot
operator|.
name|computeDiff
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
return|;
block|}
block|}
end_class

end_unit

