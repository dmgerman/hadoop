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
name|INodeSymlink
import|;
end_import

begin_comment
comment|/** Manage snapshottable directories and their snapshots. */
end_comment

begin_class
DECL|class|SnapshotManager
specifier|public
class|class
name|SnapshotManager
implements|implements
name|SnapshotStats
block|{
DECL|field|namesystem
specifier|private
specifier|final
name|FSNamesystem
name|namesystem
decl_stmt|;
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
DECL|field|snapshotID
specifier|private
name|int
name|snapshotID
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
DECL|method|SnapshotManager (final FSNamesystem namesystem, final FSDirectory fsdir)
specifier|public
name|SnapshotManager
parameter_list|(
specifier|final
name|FSNamesystem
name|namesystem
parameter_list|,
specifier|final
name|FSDirectory
name|fsdir
parameter_list|)
block|{
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
name|this
operator|.
name|fsdir
operator|=
name|fsdir
expr_stmt|;
block|}
comment|/**    * Set the given directory as a snapshottable directory.    * If the path is already a snapshottable directory, update the quota.    */
DECL|method|setSnapshottable (final String path, final int snapshotQuota )
specifier|public
name|void
name|setSnapshottable
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|int
name|snapshotQuota
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodeDirectory
name|d
init|=
name|INodeDirectory
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
name|snapshotQuota
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|INodeDirectorySnapshottable
name|s
init|=
name|INodeDirectorySnapshottable
operator|.
name|newInstance
argument_list|(
name|d
argument_list|,
name|snapshotQuota
argument_list|)
decl_stmt|;
name|fsdir
operator|.
name|replaceINodeDirectory
argument_list|(
name|path
argument_list|,
name|d
argument_list|,
name|s
argument_list|)
expr_stmt|;
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
name|INodeDirectorySnapshottable
name|s
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
specifier|final
name|INodeDirectory
name|d
init|=
operator|new
name|INodeDirectory
argument_list|(
name|s
argument_list|)
decl_stmt|;
name|fsdir
operator|.
name|replaceINodeDirectory
argument_list|(
name|path
argument_list|,
name|s
argument_list|,
name|d
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
comment|/**    * Create a snapshot of the given path.    *     * @param snapshotName The name of the snapshot.    * @param path The directory path where the snapshot will be taken.    */
DECL|method|createSnapshot (final String snapshotName, final String path )
specifier|public
name|void
name|createSnapshot
parameter_list|(
specifier|final
name|String
name|snapshotName
parameter_list|,
specifier|final
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Find the source root directory path where the snapshot is taken.
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
synchronized|synchronized
init|(
name|this
init|)
block|{
specifier|final
name|Snapshot
name|s
init|=
name|srcRoot
operator|.
name|addSnapshot
argument_list|(
name|snapshotID
argument_list|,
name|snapshotName
argument_list|)
decl_stmt|;
operator|new
name|SnapshotCreation
argument_list|()
operator|.
name|processRecursively
argument_list|(
name|srcRoot
argument_list|,
name|s
operator|.
name|getRoot
argument_list|()
argument_list|)
expr_stmt|;
comment|//create success, update id
name|snapshotID
operator|++
expr_stmt|;
block|}
name|numSnapshots
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
block|}
comment|/**    * Create a snapshot of subtrees by recursively coping the directory    * structure from the source directory to the snapshot destination directory.    * This creation algorithm requires O(N) running time and O(N) memory,    * where N = # files + # directories + # symlinks.     */
DECL|class|SnapshotCreation
class|class
name|SnapshotCreation
block|{
comment|/** Process snapshot creation recursively. */
DECL|method|processRecursively (final INodeDirectory srcDir, final INodeDirectory dstDir)
specifier|private
name|void
name|processRecursively
parameter_list|(
specifier|final
name|INodeDirectory
name|srcDir
parameter_list|,
specifier|final
name|INodeDirectory
name|dstDir
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|children
init|=
name|srcDir
operator|.
name|getChildren
argument_list|()
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
specifier|final
name|List
argument_list|<
name|INode
argument_list|>
name|inodes
init|=
operator|new
name|ArrayList
argument_list|<
name|INode
argument_list|>
argument_list|(
name|children
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|INode
name|c
range|:
operator|new
name|ArrayList
argument_list|<
name|INode
argument_list|>
argument_list|(
name|children
argument_list|)
control|)
block|{
specifier|final
name|INode
name|i
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
condition|)
block|{
name|i
operator|=
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|instanceof
name|INodeDirectory
condition|)
block|{
comment|//also handle INodeDirectoryWithQuota
name|i
operator|=
name|processINodeDirectory
argument_list|(
operator|(
name|INodeDirectory
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|instanceof
name|INodeFileUnderConstruction
condition|)
block|{
comment|//TODO: support INodeFileUnderConstruction
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not yet supported."
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|c
operator|instanceof
name|INodeFile
condition|)
block|{
name|i
operator|=
name|processINodeFile
argument_list|(
name|srcDir
argument_list|,
operator|(
name|INodeFile
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|c
operator|instanceof
name|INodeSymlink
condition|)
block|{
name|i
operator|=
operator|new
name|INodeSymlink
argument_list|(
operator|(
name|INodeSymlink
operator|)
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Unknow INode type: "
operator|+
name|c
operator|.
name|getClass
argument_list|()
operator|+
literal|", inode = "
operator|+
name|c
argument_list|)
throw|;
block|}
name|i
operator|.
name|setParent
argument_list|(
name|dstDir
argument_list|)
expr_stmt|;
name|inodes
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
name|dstDir
operator|.
name|setChildren
argument_list|(
name|inodes
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Create destination INodeDirectory and make the recursive call.       * @return destination INodeDirectory.      */
DECL|method|processINodeDirectory (final INodeDirectory srcChild )
specifier|private
name|INodeDirectory
name|processINodeDirectory
parameter_list|(
specifier|final
name|INodeDirectory
name|srcChild
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INodeDirectory
name|dstChild
init|=
operator|new
name|INodeDirectory
argument_list|(
name|srcChild
argument_list|)
decl_stmt|;
name|dstChild
operator|.
name|setChildren
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|processRecursively
argument_list|(
name|srcChild
argument_list|,
name|dstChild
argument_list|)
expr_stmt|;
return|return
name|dstChild
return|;
block|}
comment|/**      * Create destination INodeFileSnapshot and update source INode type.      * @return destination INodeFileSnapshot.      */
DECL|method|processINodeFile (final INodeDirectory parent, final INodeFile file)
specifier|private
name|INodeFileSnapshot
name|processINodeFile
parameter_list|(
specifier|final
name|INodeDirectory
name|parent
parameter_list|,
specifier|final
name|INodeFile
name|file
parameter_list|)
block|{
specifier|final
name|INodeFileSnapshot
name|snapshot
init|=
operator|new
name|INodeFileSnapshot
argument_list|(
name|file
argument_list|,
name|file
operator|.
name|computeFileSize
argument_list|(
literal|true
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|INodeFileWithLink
name|srcWithLink
decl_stmt|;
comment|//check source INode type
if|if
condition|(
name|file
operator|instanceof
name|INodeFileWithLink
condition|)
block|{
name|srcWithLink
operator|=
operator|(
name|INodeFileWithLink
operator|)
name|file
expr_stmt|;
block|}
else|else
block|{
comment|//source is an INodeFile, replace the source.
name|srcWithLink
operator|=
operator|new
name|INodeFileWithLink
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|file
operator|.
name|removeNode
argument_list|()
expr_stmt|;
name|parent
operator|.
name|addChild
argument_list|(
name|srcWithLink
argument_list|,
literal|false
argument_list|)
expr_stmt|;
comment|//update block map
name|namesystem
operator|.
name|getBlockManager
argument_list|()
operator|.
name|addBlockCollection
argument_list|(
name|srcWithLink
argument_list|)
expr_stmt|;
block|}
comment|//insert the snapshot to src's linked list.
name|srcWithLink
operator|.
name|insert
argument_list|(
name|snapshot
argument_list|)
expr_stmt|;
return|return
name|snapshot
return|;
block|}
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
block|}
end_class

end_unit

