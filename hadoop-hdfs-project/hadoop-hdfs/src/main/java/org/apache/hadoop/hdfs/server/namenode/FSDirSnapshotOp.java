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
name|fs
operator|.
name|InvalidPathException
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
name|Path
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
name|FsAction
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
name|FSLimitException
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
name|HdfsFileStatus
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
name|SnapshotDiffReportListing
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
operator|.
name|DirOp
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
name|DirectorySnapshottableFeature
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
name|Snapshot
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
name|SnapshotManager
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
name|ChunkedArrayList
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
name|Collection
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

begin_class
DECL|class|FSDirSnapshotOp
class|class
name|FSDirSnapshotOp
block|{
comment|/** Verify if the snapshot name is legal. */
DECL|method|verifySnapshotName (FSDirectory fsd, String snapshotName, String path)
specifier|static
name|void
name|verifySnapshotName
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|String
name|snapshotName
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|FSLimitException
operator|.
name|PathComponentTooLongException
block|{
if|if
condition|(
name|snapshotName
operator|.
name|contains
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|HadoopIllegalArgumentException
argument_list|(
literal|"Snapshot name cannot contain \""
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"\""
argument_list|)
throw|;
block|}
specifier|final
name|byte
index|[]
name|bytes
init|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|snapshotName
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|verifyINodeName
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|verifyMaxComponentLength
argument_list|(
name|bytes
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
comment|/** Allow snapshot on a directory. */
DECL|method|allowSnapshot (FSDirectory fsd, SnapshotManager snapshotManager, String path)
specifier|static
name|void
name|allowSnapshot
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|snapshotManager
operator|.
name|setSnapshottable
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logAllowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|disallowSnapshot ( FSDirectory fsd, SnapshotManager snapshotManager, String path)
specifier|static
name|void
name|disallowSnapshot
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|snapshotManager
operator|.
name|resetSnapshottable
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logDisallowSnapshot
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a snapshot    * @param fsd FS directory    * @param pc FS permission checker    * @param snapshotRoot The directory path where the snapshot is taken    * @param snapshotName The name of the snapshot    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding.    */
DECL|method|createSnapshot ( FSDirectory fsd, FSPermissionChecker pc, SnapshotManager snapshotManager, String snapshotRoot, String snapshotName, boolean logRetryCache)
specifier|static
name|String
name|createSnapshot
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|snapshotRoot
parameter_list|,
name|String
name|snapshotName
parameter_list|,
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|snapshotRoot
argument_list|,
name|DirOp
operator|.
name|WRITE
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|snapshotName
operator|==
literal|null
operator|||
name|snapshotName
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|snapshotName
operator|=
name|Snapshot
operator|.
name|generateDefaultSnapshotName
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|DFSUtil
operator|.
name|isValidNameForComponent
argument_list|(
name|snapshotName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidPathException
argument_list|(
literal|"Invalid snapshot name: "
operator|+
name|snapshotName
argument_list|)
throw|;
block|}
name|String
name|snapshotPath
decl_stmt|;
name|verifySnapshotName
argument_list|(
name|fsd
argument_list|,
name|snapshotName
argument_list|,
name|snapshotRoot
argument_list|)
expr_stmt|;
comment|// time of snapshot creation
specifier|final
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|snapshotPath
operator|=
name|snapshotManager
operator|.
name|createSnapshot
argument_list|(
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getLeaseManager
argument_list|()
argument_list|,
name|iip
argument_list|,
name|snapshotRoot
argument_list|,
name|snapshotName
argument_list|,
name|now
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logCreateSnapshot
argument_list|(
name|snapshotRoot
argument_list|,
name|snapshotName
argument_list|,
name|logRetryCache
argument_list|,
name|now
argument_list|)
expr_stmt|;
return|return
name|snapshotPath
return|;
block|}
DECL|method|renameSnapshot (FSDirectory fsd, FSPermissionChecker pc, SnapshotManager snapshotManager, String path, String snapshotOldName, String snapshotNewName, boolean logRetryCache)
specifier|static
name|void
name|renameSnapshot
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|snapshotOldName
parameter_list|,
name|String
name|snapshotNewName
parameter_list|,
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|path
argument_list|,
name|DirOp
operator|.
name|WRITE
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
name|verifySnapshotName
argument_list|(
name|fsd
argument_list|,
name|snapshotNewName
argument_list|,
name|path
argument_list|)
expr_stmt|;
comment|// time of snapshot modification
specifier|final
name|long
name|now
init|=
name|Time
operator|.
name|now
argument_list|()
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|snapshotManager
operator|.
name|renameSnapshot
argument_list|(
name|iip
argument_list|,
name|path
argument_list|,
name|snapshotOldName
argument_list|,
name|snapshotNewName
argument_list|,
name|now
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logRenameSnapshot
argument_list|(
name|path
argument_list|,
name|snapshotOldName
argument_list|,
name|snapshotNewName
argument_list|,
name|logRetryCache
argument_list|,
name|now
argument_list|)
expr_stmt|;
block|}
DECL|method|getSnapshottableDirListing ( FSDirectory fsd, FSPermissionChecker pc, SnapshotManager snapshotManager)
specifier|static
name|SnapshottableDirectoryStatus
index|[]
name|getSnapshottableDirListing
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|)
throws|throws
name|IOException
block|{
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|user
init|=
name|pc
operator|.
name|isSuperUser
argument_list|()
condition|?
literal|null
else|:
name|pc
operator|.
name|getUser
argument_list|()
decl_stmt|;
return|return
name|snapshotManager
operator|.
name|getSnapshottableDirListing
argument_list|(
name|user
argument_list|)
return|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getSnapshotDiffReport (FSDirectory fsd, FSPermissionChecker pc, SnapshotManager snapshotManager, String path, String fromSnapshot, String toSnapshot)
specifier|static
name|SnapshotDiffReport
name|getSnapshotDiffReport
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|fromSnapshot
parameter_list|,
name|String
name|toSnapshot
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotDiffReport
name|diffs
decl_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|path
argument_list|,
name|DirOp
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|checkSubtreeReadPermission
argument_list|(
name|fsd
argument_list|,
name|pc
argument_list|,
name|path
argument_list|,
name|fromSnapshot
argument_list|)
expr_stmt|;
name|checkSubtreeReadPermission
argument_list|(
name|fsd
argument_list|,
name|pc
argument_list|,
name|path
argument_list|,
name|toSnapshot
argument_list|)
expr_stmt|;
block|}
name|diffs
operator|=
name|snapshotManager
operator|.
name|diff
argument_list|(
name|iip
argument_list|,
name|path
argument_list|,
name|fromSnapshot
argument_list|,
name|toSnapshot
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|diffs
return|;
block|}
DECL|method|getSnapshotDiffReportListing (FSDirectory fsd, FSPermissionChecker pc, SnapshotManager snapshotManager, String path, String fromSnapshot, String toSnapshot, byte[] startPath, int index, int snapshotDiffReportLimit)
specifier|static
name|SnapshotDiffReportListing
name|getSnapshotDiffReportListing
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|path
parameter_list|,
name|String
name|fromSnapshot
parameter_list|,
name|String
name|toSnapshot
parameter_list|,
name|byte
index|[]
name|startPath
parameter_list|,
name|int
name|index
parameter_list|,
name|int
name|snapshotDiffReportLimit
parameter_list|)
throws|throws
name|IOException
block|{
name|SnapshotDiffReportListing
name|diffs
decl_stmt|;
name|fsd
operator|.
name|readLock
argument_list|()
expr_stmt|;
try|try
block|{
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|path
argument_list|,
name|DirOp
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|checkSubtreeReadPermission
argument_list|(
name|fsd
argument_list|,
name|pc
argument_list|,
name|path
argument_list|,
name|fromSnapshot
argument_list|)
expr_stmt|;
name|checkSubtreeReadPermission
argument_list|(
name|fsd
argument_list|,
name|pc
argument_list|,
name|path
argument_list|,
name|toSnapshot
argument_list|)
expr_stmt|;
block|}
name|diffs
operator|=
name|snapshotManager
operator|.
name|diff
argument_list|(
name|iip
argument_list|,
name|path
argument_list|,
name|fromSnapshot
argument_list|,
name|toSnapshot
argument_list|,
name|startPath
argument_list|,
name|index
argument_list|,
name|snapshotDiffReportLimit
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|readUnlock
argument_list|()
expr_stmt|;
block|}
return|return
name|diffs
return|;
block|}
comment|/** Get a collection of full snapshot paths given file and snapshot dir.    * @param lsf a list of snapshottable features    * @param file full path of the file    * @return collection of full paths of snapshot of the file    */
DECL|method|getSnapshotFiles (FSDirectory fsd, List<DirectorySnapshottableFeature> lsf, String file)
specifier|static
name|Collection
argument_list|<
name|String
argument_list|>
name|getSnapshotFiles
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|List
argument_list|<
name|DirectorySnapshottableFeature
argument_list|>
name|lsf
parameter_list|,
name|String
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|snaps
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|DirectorySnapshottableFeature
name|sf
range|:
name|lsf
control|)
block|{
comment|// for each snapshottable dir e.g. /dir1, /dir2
specifier|final
name|ReadOnlyList
argument_list|<
name|Snapshot
argument_list|>
name|lsnap
init|=
name|sf
operator|.
name|getSnapshotList
argument_list|()
decl_stmt|;
for|for
control|(
name|Snapshot
name|s
range|:
name|lsnap
control|)
block|{
comment|// for each snapshot name under snapshottable dir
comment|// e.g. /dir1/.snapshot/s1, /dir1/.snapshot/s2
specifier|final
name|String
name|dirName
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|getRootFullPathName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|startsWith
argument_list|(
name|dirName
argument_list|)
condition|)
block|{
comment|// file not in current snapshot root dir, no need to check other snaps
break|break;
block|}
name|String
name|snapname
init|=
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|getFullPathName
argument_list|()
decl_stmt|;
if|if
condition|(
name|dirName
operator|.
name|equals
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
condition|)
block|{
comment|// handle rootDir
name|snapname
operator|+=
name|Path
operator|.
name|SEPARATOR
expr_stmt|;
block|}
name|snapname
operator|+=
name|file
operator|.
name|substring
argument_list|(
name|file
operator|.
name|indexOf
argument_list|(
name|dirName
argument_list|)
operator|+
name|dirName
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
name|HdfsFileStatus
name|stat
init|=
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getFileInfo
argument_list|(
name|snapname
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|stat
operator|!=
literal|null
condition|)
block|{
name|snaps
operator|.
name|add
argument_list|(
name|snapname
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|snaps
return|;
block|}
comment|/**    * Delete a snapshot of a snapshottable directory    * @param fsd The FS directory    * @param pc The permission checker    * @param snapshotManager The snapshot manager    * @param snapshotRoot The snapshottable directory    * @param snapshotName The name of the to-be-deleted snapshot    * @param logRetryCache whether to record RPC ids in editlog for retry cache    *                      rebuilding.    * @throws IOException    */
DECL|method|deleteSnapshot ( FSDirectory fsd, FSPermissionChecker pc, SnapshotManager snapshotManager, String snapshotRoot, String snapshotName, boolean logRetryCache)
specifier|static
name|INode
operator|.
name|BlocksMapUpdateInfo
name|deleteSnapshot
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|FSPermissionChecker
name|pc
parameter_list|,
name|SnapshotManager
name|snapshotManager
parameter_list|,
name|String
name|snapshotRoot
parameter_list|,
name|String
name|snapshotName
parameter_list|,
name|boolean
name|logRetryCache
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|snapshotRoot
argument_list|,
name|DirOp
operator|.
name|WRITE
argument_list|)
decl_stmt|;
if|if
condition|(
name|fsd
operator|.
name|isPermissionEnabled
argument_list|()
condition|)
block|{
name|fsd
operator|.
name|checkOwner
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|)
expr_stmt|;
block|}
name|INode
operator|.
name|BlocksMapUpdateInfo
name|collectedBlocks
init|=
operator|new
name|INode
operator|.
name|BlocksMapUpdateInfo
argument_list|()
decl_stmt|;
name|ChunkedArrayList
argument_list|<
name|INode
argument_list|>
name|removedINodes
init|=
operator|new
name|ChunkedArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|INode
operator|.
name|ReclaimContext
name|context
init|=
operator|new
name|INode
operator|.
name|ReclaimContext
argument_list|(
name|fsd
operator|.
name|getBlockStoragePolicySuite
argument_list|()
argument_list|,
name|collectedBlocks
argument_list|,
name|removedINodes
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|writeLock
argument_list|()
expr_stmt|;
try|try
block|{
name|snapshotManager
operator|.
name|deleteSnapshot
argument_list|(
name|iip
argument_list|,
name|snapshotName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|updateCount
argument_list|(
name|iip
argument_list|,
name|context
operator|.
name|quotaDelta
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|removeFromInodeMap
argument_list|(
name|removedINodes
argument_list|)
expr_stmt|;
name|fsd
operator|.
name|updateReplicationFactor
argument_list|(
name|context
operator|.
name|collectedBlocks
argument_list|()
operator|.
name|toUpdateReplicationInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsd
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
name|removedINodes
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fsd
operator|.
name|getEditLog
argument_list|()
operator|.
name|logDeleteSnapshot
argument_list|(
name|snapshotRoot
argument_list|,
name|snapshotName
argument_list|,
name|logRetryCache
argument_list|)
expr_stmt|;
return|return
name|collectedBlocks
return|;
block|}
DECL|method|checkSubtreeReadPermission ( FSDirectory fsd, final FSPermissionChecker pc, String snapshottablePath, String snapshot)
specifier|private
specifier|static
name|void
name|checkSubtreeReadPermission
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
specifier|final
name|FSPermissionChecker
name|pc
parameter_list|,
name|String
name|snapshottablePath
parameter_list|,
name|String
name|snapshot
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|String
name|fromPath
init|=
name|snapshot
operator|==
literal|null
condition|?
name|snapshottablePath
else|:
name|Snapshot
operator|.
name|getSnapshotPath
argument_list|(
name|snapshottablePath
argument_list|,
name|snapshot
argument_list|)
decl_stmt|;
name|INodesInPath
name|iip
init|=
name|fsd
operator|.
name|resolvePath
argument_list|(
name|pc
argument_list|,
name|fromPath
argument_list|,
name|DirOp
operator|.
name|READ
argument_list|)
decl_stmt|;
name|fsd
operator|.
name|checkPermission
argument_list|(
name|pc
argument_list|,
name|iip
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|,
name|FsAction
operator|.
name|READ
argument_list|)
expr_stmt|;
block|}
comment|/**    * Check if the given INode (or one of its descendants) is snapshottable and    * already has snapshots.    *    * @param target The given INode    * @param snapshottableDirs The list of directories that are snapshottable    *                          but do not have snapshots yet    */
DECL|method|checkSnapshot ( INode target, List<INodeDirectory> snapshottableDirs)
specifier|private
specifier|static
name|void
name|checkSnapshot
parameter_list|(
name|INode
name|target
parameter_list|,
name|List
argument_list|<
name|INodeDirectory
argument_list|>
name|snapshottableDirs
parameter_list|)
throws|throws
name|SnapshotException
block|{
if|if
condition|(
name|target
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|INodeDirectory
name|targetDir
init|=
name|target
operator|.
name|asDirectory
argument_list|()
decl_stmt|;
name|DirectorySnapshottableFeature
name|sf
init|=
name|targetDir
operator|.
name|getDirectorySnapshottableFeature
argument_list|()
decl_stmt|;
if|if
condition|(
name|sf
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sf
operator|.
name|getNumSnapshots
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|fullPath
init|=
name|targetDir
operator|.
name|getFullPathName
argument_list|()
decl_stmt|;
throw|throw
operator|new
name|SnapshotException
argument_list|(
literal|"The directory "
operator|+
name|fullPath
operator|+
literal|" cannot be deleted since "
operator|+
name|fullPath
operator|+
literal|" is snapshottable and already has snapshots"
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|snapshottableDirs
operator|!=
literal|null
condition|)
block|{
name|snapshottableDirs
operator|.
name|add
argument_list|(
name|targetDir
argument_list|)
expr_stmt|;
block|}
block|}
block|}
for|for
control|(
name|INode
name|child
range|:
name|targetDir
operator|.
name|getChildrenList
argument_list|(
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
control|)
block|{
name|checkSnapshot
argument_list|(
name|child
argument_list|,
name|snapshottableDirs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Check if the given path (or one of its descendants) is snapshottable and    * already has snapshots.    *    * @param fsd the FSDirectory    * @param iip inodes of the path    * @param snapshottableDirs The list of directories that are snapshottable    *                          but do not have snapshots yet    */
DECL|method|checkSnapshot (FSDirectory fsd, INodesInPath iip, List<INodeDirectory> snapshottableDirs)
specifier|static
name|void
name|checkSnapshot
parameter_list|(
name|FSDirectory
name|fsd
parameter_list|,
name|INodesInPath
name|iip
parameter_list|,
name|List
argument_list|<
name|INodeDirectory
argument_list|>
name|snapshottableDirs
parameter_list|)
throws|throws
name|SnapshotException
block|{
comment|// avoid the performance penalty of recursing the tree if snapshots
comment|// are not in use
name|SnapshotManager
name|sm
init|=
name|fsd
operator|.
name|getFSNamesystem
argument_list|()
operator|.
name|getSnapshotManager
argument_list|()
decl_stmt|;
if|if
condition|(
name|sm
operator|.
name|getNumSnapshottableDirs
argument_list|()
operator|>
literal|0
condition|)
block|{
name|checkSnapshot
argument_list|(
name|iip
operator|.
name|getLastINode
argument_list|()
argument_list|,
name|snapshottableDirs
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

