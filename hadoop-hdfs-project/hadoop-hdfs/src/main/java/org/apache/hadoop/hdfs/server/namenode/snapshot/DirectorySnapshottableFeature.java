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
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|Set
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
name|protocol
operator|.
name|QuotaExceededException
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
name|SnapshotException
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
name|blockmanagement
operator|.
name|BlockStoragePolicySuite
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
name|Content
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
name|ContentCounts
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
name|INodeDirectory
operator|.
name|SnapshotAndINode
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
name|INodeReference
operator|.
name|WithCount
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
operator|.
name|WithName
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
name|LeaseManager
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
name|annotations
operator|.
name|VisibleForTesting
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
name|collect
operator|.
name|Lists
import|;
end_import

begin_comment
comment|/**  * A directory with this feature is a snapshottable directory, where snapshots  * can be taken. This feature extends {@link DirectoryWithSnapshotFeature}, and  * maintains extra information about all the snapshots taken on this directory.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|DirectorySnapshottableFeature
specifier|public
class|class
name|DirectorySnapshottableFeature
extends|extends
name|DirectoryWithSnapshotFeature
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
comment|/**    * Snapshots of this directory in ascending order of snapshot names.    * Note that snapshots in ascending order of snapshot id are stored in    * {@link DirectoryWithSnapshotFeature}.diffs (a private field).    */
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
comment|/** Number of snapshots allowed. */
DECL|field|snapshotQuota
specifier|private
name|int
name|snapshotQuota
init|=
name|SNAPSHOT_LIMIT
decl_stmt|;
DECL|method|DirectorySnapshottableFeature (DirectoryWithSnapshotFeature feature)
specifier|public
name|DirectorySnapshottableFeature
parameter_list|(
name|DirectoryWithSnapshotFeature
name|feature
parameter_list|)
block|{
name|super
argument_list|(
name|feature
operator|==
literal|null
condition|?
literal|null
else|:
name|feature
operator|.
name|getDiffs
argument_list|()
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
DECL|method|getSnapshotById (int sid)
specifier|public
name|Snapshot
name|getSnapshotById
parameter_list|(
name|int
name|sid
parameter_list|)
block|{
for|for
control|(
name|Snapshot
name|s
range|:
name|snapshotsByNames
control|)
block|{
if|if
condition|(
name|s
operator|.
name|getId
argument_list|()
operator|==
name|sid
condition|)
block|{
return|return
name|s
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/** @return {@link #snapshotsByNames} as a {@link ReadOnlyList} */
DECL|method|getSnapshotList ()
specifier|public
name|ReadOnlyList
argument_list|<
name|Snapshot
argument_list|>
name|getSnapshotList
parameter_list|()
block|{
return|return
name|ReadOnlyList
operator|.
name|Util
operator|.
name|asReadOnlyList
argument_list|(
name|snapshotsByNames
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
specifier|final
name|byte
index|[]
name|newNameBytes
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|newName
argument_list|)
decl_stmt|;
name|int
name|indexOfNew
init|=
name|searchSnapshot
argument_list|(
name|newNameBytes
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexOfNew
operator|>=
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
name|newNameBytes
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
comment|/**    * Simply add a snapshot into the {@link #snapshotsByNames}. Used when loading    * fsimage.    */
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
DECL|method|addSnapshot (INodeDirectory snapshotRoot, int id, String name, final LeaseManager leaseManager, final boolean captureOpenFiles)
specifier|public
name|Snapshot
name|addSnapshot
parameter_list|(
name|INodeDirectory
name|snapshotRoot
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|,
specifier|final
name|LeaseManager
name|leaseManager
parameter_list|,
specifier|final
name|boolean
name|captureOpenFiles
parameter_list|)
throws|throws
name|SnapshotException
throws|,
name|QuotaExceededException
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
name|snapshotRoot
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
name|Snapshot
operator|.
name|getSnapshotName
argument_list|(
name|s
argument_list|)
operator|+
literal|"\"."
argument_list|)
throw|;
block|}
specifier|final
name|DirectoryDiff
name|d
init|=
name|getDiffs
argument_list|()
operator|.
name|addDiff
argument_list|(
name|id
argument_list|,
name|snapshotRoot
argument_list|)
decl_stmt|;
name|d
operator|.
name|setSnapshotRoot
argument_list|(
name|s
operator|.
name|getRoot
argument_list|()
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
comment|// set modification time
specifier|final
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|snapshotRoot
operator|.
name|updateModificationTime
argument_list|(
name|now
argument_list|,
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
expr_stmt|;
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|setModificationTime
argument_list|(
name|now
argument_list|,
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
expr_stmt|;
if|if
condition|(
name|captureOpenFiles
condition|)
block|{
name|Set
argument_list|<
name|INodesInPath
argument_list|>
name|openFilesIIP
init|=
name|leaseManager
operator|.
name|getINodeWithLeases
argument_list|(
name|snapshotRoot
argument_list|)
decl_stmt|;
for|for
control|(
name|INodesInPath
name|openFileIIP
range|:
name|openFilesIIP
control|)
block|{
name|INodeFile
name|openFile
init|=
name|openFileIIP
operator|.
name|getLastINode
argument_list|()
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|openFile
operator|.
name|recordModification
argument_list|(
name|openFileIIP
operator|.
name|getLatestSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|s
return|;
block|}
comment|/**    * Remove the snapshot with the given name from {@link #snapshotsByNames},    * and delete all the corresponding DirectoryDiff.    *    * @param reclaimContext records blocks and inodes that need to be reclaimed    * @param snapshotRoot The directory where we take snapshots    * @param snapshotName The name of the snapshot to be removed    * @return The removed snapshot. Null if no snapshot with the given name    *         exists.    */
DECL|method|removeSnapshot ( INode.ReclaimContext reclaimContext, INodeDirectory snapshotRoot, String snapshotName)
specifier|public
name|Snapshot
name|removeSnapshot
parameter_list|(
name|INode
operator|.
name|ReclaimContext
name|reclaimContext
parameter_list|,
name|INodeDirectory
name|snapshotRoot
parameter_list|,
name|String
name|snapshotName
parameter_list|)
throws|throws
name|SnapshotException
block|{
specifier|final
name|int
name|i
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
name|i
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
name|snapshotRoot
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
specifier|final
name|Snapshot
name|snapshot
init|=
name|snapshotsByNames
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|int
name|prior
init|=
name|Snapshot
operator|.
name|findLatestSnapshot
argument_list|(
name|snapshotRoot
argument_list|,
name|snapshot
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|snapshotRoot
operator|.
name|cleanSubtree
argument_list|(
name|reclaimContext
argument_list|,
name|snapshot
operator|.
name|getId
argument_list|()
argument_list|,
name|prior
argument_list|)
expr_stmt|;
comment|// remove from snapshotsByNames after successfully cleaning the subtree
name|snapshotsByNames
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
name|snapshot
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|computeContentSummary4Snapshot (final BlockStoragePolicySuite bsps, final ContentCounts counts)
specifier|public
name|void
name|computeContentSummary4Snapshot
parameter_list|(
specifier|final
name|BlockStoragePolicySuite
name|bsps
parameter_list|,
specifier|final
name|ContentCounts
name|counts
parameter_list|)
block|{
name|counts
operator|.
name|addContent
argument_list|(
name|Content
operator|.
name|SNAPSHOT
argument_list|,
name|snapshotsByNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|counts
operator|.
name|addContent
argument_list|(
name|Content
operator|.
name|SNAPSHOTTABLE_DIRECTORY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|super
operator|.
name|computeContentSummary4Snapshot
argument_list|(
name|bsps
argument_list|,
name|counts
argument_list|)
expr_stmt|;
block|}
comment|/**    * Compute the difference between two snapshots (or a snapshot and the current    * directory) of the directory.    *    * @param from The name of the start point of the comparison. Null indicating    *          the current tree.    * @param to The name of the end point. Null indicating the current tree.    * @return The difference between the start/end points.    * @throws SnapshotException If there is no snapshot matching the starting    *           point, or if endSnapshotName is not null but cannot be identified    *           as a previous snapshot.    */
DECL|method|computeDiff (final INodeDirectory snapshotRoot, final String from, final String to)
name|SnapshotDiffInfo
name|computeDiff
parameter_list|(
specifier|final
name|INodeDirectory
name|snapshotRoot
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
name|SnapshotException
block|{
name|Snapshot
name|fromSnapshot
init|=
name|getSnapshotByName
argument_list|(
name|snapshotRoot
argument_list|,
name|from
argument_list|)
decl_stmt|;
name|Snapshot
name|toSnapshot
init|=
name|getSnapshotByName
argument_list|(
name|snapshotRoot
argument_list|,
name|to
argument_list|)
decl_stmt|;
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
name|SnapshotDiffInfo
name|diffs
init|=
operator|new
name|SnapshotDiffInfo
argument_list|(
name|snapshotRoot
argument_list|,
name|fromSnapshot
argument_list|,
name|toSnapshot
argument_list|)
decl_stmt|;
name|computeDiffRecursively
argument_list|(
name|snapshotRoot
argument_list|,
name|snapshotRoot
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|byte
index|[]
argument_list|>
argument_list|()
argument_list|,
name|diffs
argument_list|)
expr_stmt|;
return|return
name|diffs
return|;
block|}
comment|/**    * Find the snapshot matching the given name.    *    * @param snapshotRoot The directory where snapshots were taken.    * @param snapshotName The name of the snapshot.    * @return The corresponding snapshot. Null if snapshotName is null or empty.    * @throws SnapshotException If snapshotName is not null or empty, but there    *           is no snapshot matching the name.    */
DECL|method|getSnapshotByName (INodeDirectory snapshotRoot, String snapshotName)
specifier|private
name|Snapshot
name|getSnapshotByName
parameter_list|(
name|INodeDirectory
name|snapshotRoot
parameter_list|,
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
name|snapshotRoot
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
comment|/**    * Recursively compute the difference between snapshots under a given    * directory/file.    * @param snapshotRoot The directory where snapshots were taken.    * @param node The directory/file under which the diff is computed.    * @param parentPath Relative path (corresponding to the snapshot root) of    *                   the node's parent.    * @param diffReport data structure used to store the diff.    */
DECL|method|computeDiffRecursively (final INodeDirectory snapshotRoot, INode node, List<byte[]> parentPath, SnapshotDiffInfo diffReport)
specifier|private
name|void
name|computeDiffRecursively
parameter_list|(
specifier|final
name|INodeDirectory
name|snapshotRoot
parameter_list|,
name|INode
name|node
parameter_list|,
name|List
argument_list|<
name|byte
index|[]
argument_list|>
name|parentPath
parameter_list|,
name|SnapshotDiffInfo
name|diffReport
parameter_list|)
block|{
specifier|final
name|Snapshot
name|earlierSnapshot
init|=
name|diffReport
operator|.
name|isFromEarlier
argument_list|()
condition|?
name|diffReport
operator|.
name|getFrom
argument_list|()
else|:
name|diffReport
operator|.
name|getTo
argument_list|()
decl_stmt|;
specifier|final
name|Snapshot
name|laterSnapshot
init|=
name|diffReport
operator|.
name|isFromEarlier
argument_list|()
condition|?
name|diffReport
operator|.
name|getTo
argument_list|()
else|:
name|diffReport
operator|.
name|getFrom
argument_list|()
decl_stmt|;
name|byte
index|[]
index|[]
name|relativePath
init|=
name|parentPath
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|parentPath
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
specifier|final
name|ChildrenDiff
name|diff
init|=
operator|new
name|ChildrenDiff
argument_list|()
decl_stmt|;
name|INodeDirectory
name|dir
init|=
name|node
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
name|DirectoryWithSnapshotFeature
name|sf
init|=
name|dir
operator|.
name|getDirectoryWithSnapshotFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
name|boolean
name|change
init|=
name|sf
operator|.
name|computeDiffBetweenSnapshots
argument_list|(
name|earlierSnapshot
argument_list|,
name|laterSnapshot
argument_list|,
name|diff
argument_list|,
name|dir
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
condition|)
block|{
name|diffReport
operator|.
name|addDirDiff
argument_list|(
name|dir
argument_list|,
name|relativePath
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
name|earlierSnapshot
operator|.
name|getId
argument_list|()
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
specifier|final
name|byte
index|[]
name|name
init|=
name|child
operator|.
name|getLocalNameBytes
argument_list|()
decl_stmt|;
name|boolean
name|toProcess
init|=
name|diff
operator|.
name|searchIndex
argument_list|(
name|ListType
operator|.
name|DELETED
argument_list|,
name|name
argument_list|)
operator|<
literal|0
decl_stmt|;
if|if
condition|(
operator|!
name|toProcess
operator|&&
name|child
operator|instanceof
name|INodeReference
operator|.
name|WithName
condition|)
block|{
name|byte
index|[]
index|[]
name|renameTargetPath
init|=
name|findRenameTargetPath
argument_list|(
name|snapshotRoot
argument_list|,
operator|(
name|WithName
operator|)
name|child
argument_list|,
name|laterSnapshot
operator|==
literal|null
condition|?
name|Snapshot
operator|.
name|CURRENT_STATE_ID
else|:
name|laterSnapshot
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|renameTargetPath
operator|!=
literal|null
condition|)
block|{
name|toProcess
operator|=
literal|true
expr_stmt|;
name|diffReport
operator|.
name|setRenameTarget
argument_list|(
name|child
operator|.
name|getId
argument_list|()
argument_list|,
name|renameTargetPath
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|toProcess
condition|)
block|{
name|parentPath
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|computeDiffRecursively
argument_list|(
name|snapshotRoot
argument_list|,
name|child
argument_list|,
name|parentPath
argument_list|,
name|diffReport
argument_list|)
expr_stmt|;
name|parentPath
operator|.
name|remove
argument_list|(
name|parentPath
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
elseif|else
if|if
condition|(
name|node
operator|.
name|isFile
argument_list|()
operator|&&
name|node
operator|.
name|asFile
argument_list|()
operator|.
name|isWithSnapshot
argument_list|()
condition|)
block|{
name|INodeFile
name|file
init|=
name|node
operator|.
name|asFile
argument_list|()
decl_stmt|;
name|boolean
name|change
init|=
name|file
operator|.
name|getFileWithSnapshotFeature
argument_list|()
operator|.
name|changedBetweenSnapshots
argument_list|(
name|file
argument_list|,
name|earlierSnapshot
argument_list|,
name|laterSnapshot
argument_list|)
decl_stmt|;
if|if
condition|(
name|change
condition|)
block|{
name|diffReport
operator|.
name|addFileDiff
argument_list|(
name|file
argument_list|,
name|relativePath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * We just found a deleted WithName node as the source of a rename operation.    * However, we should include it in our snapshot diff report as rename only    * if the rename target is also under the same snapshottable directory.    */
DECL|method|findRenameTargetPath (final INodeDirectory snapshotRoot, INodeReference.WithName wn, final int snapshotId)
specifier|private
name|byte
index|[]
index|[]
name|findRenameTargetPath
parameter_list|(
specifier|final
name|INodeDirectory
name|snapshotRoot
parameter_list|,
name|INodeReference
operator|.
name|WithName
name|wn
parameter_list|,
specifier|final
name|int
name|snapshotId
parameter_list|)
block|{
name|INode
name|inode
init|=
name|wn
operator|.
name|getReferredINode
argument_list|()
decl_stmt|;
specifier|final
name|LinkedList
argument_list|<
name|byte
index|[]
argument_list|>
name|ancestors
init|=
name|Lists
operator|.
name|newLinkedList
argument_list|()
decl_stmt|;
while|while
condition|(
name|inode
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|inode
operator|==
name|snapshotRoot
condition|)
block|{
return|return
name|ancestors
operator|.
name|toArray
argument_list|(
operator|new
name|byte
index|[
name|ancestors
operator|.
name|size
argument_list|()
index|]
index|[]
argument_list|)
return|;
block|}
if|if
condition|(
name|inode
operator|instanceof
name|INodeReference
operator|.
name|WithCount
condition|)
block|{
name|inode
operator|=
operator|(
operator|(
name|WithCount
operator|)
name|inode
operator|)
operator|.
name|getParentRef
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|INode
name|parent
init|=
name|inode
operator|.
name|getParentReference
argument_list|()
operator|!=
literal|null
condition|?
name|inode
operator|.
name|getParentReference
argument_list|()
else|:
name|inode
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent
operator|!=
literal|null
operator|&&
name|parent
operator|instanceof
name|INodeDirectory
condition|)
block|{
name|int
name|sid
init|=
name|parent
operator|.
name|asDirectory
argument_list|()
operator|.
name|searchChild
argument_list|(
name|inode
argument_list|)
decl_stmt|;
if|if
condition|(
name|sid
operator|<
name|snapshotId
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
if|if
condition|(
operator|!
operator|(
name|parent
operator|instanceof
name|WithCount
operator|)
condition|)
block|{
name|ancestors
operator|.
name|addFirst
argument_list|(
name|inode
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|inode
operator|=
name|parent
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"snapshotsByNames="
operator|+
name|snapshotsByNames
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|dumpTreeRecursively (INodeDirectory snapshotRoot, PrintWriter out, StringBuilder prefix, int snapshot)
specifier|public
name|void
name|dumpTreeRecursively
parameter_list|(
name|INodeDirectory
name|snapshotRoot
parameter_list|,
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|,
name|int
name|snapshot
parameter_list|)
block|{
if|if
condition|(
name|snapshot
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
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
name|out
operator|.
name|print
argument_list|(
literal|"Snapshot of "
argument_list|)
expr_stmt|;
specifier|final
name|String
name|name
init|=
name|snapshotRoot
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|out
operator|.
name|print
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
name|out
operator|.
name|print
argument_list|(
literal|": quota="
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|getSnapshotQuota
argument_list|()
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
name|Preconditions
operator|.
name|checkState
argument_list|(
name|n
operator|==
name|snapshotsByNames
operator|.
name|size
argument_list|()
argument_list|,
literal|"#n="
operator|+
name|n
operator|+
literal|", snapshotsByNames.size()="
operator|+
name|snapshotsByNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|", #snapshot="
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|n
argument_list|)
expr_stmt|;
name|INodeDirectory
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|,
operator|new
name|Iterable
argument_list|<
name|SnapshotAndINode
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|SnapshotAndINode
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|SnapshotAndINode
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
name|SnapshotAndINode
name|next
parameter_list|()
block|{
specifier|final
name|SnapshotAndINode
name|pair
init|=
operator|new
name|SnapshotAndINode
argument_list|(
name|next
operator|.
name|getSnapshotId
argument_list|()
argument_list|,
name|getSnapshotById
argument_list|(
name|next
operator|.
name|getSnapshotId
argument_list|()
argument_list|)
operator|.
name|getRoot
argument_list|()
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
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

