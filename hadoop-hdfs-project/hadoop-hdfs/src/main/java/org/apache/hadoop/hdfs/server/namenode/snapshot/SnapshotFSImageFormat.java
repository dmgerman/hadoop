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
name|FSImageFormat
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
name|FSImageFormat
operator|.
name|Loader
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
name|FSImageSerialization
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
name|FileWithSnapshot
operator|.
name|FileDiff
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
name|FileDiffList
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
name|INodeDirectoryWithSnapshot
operator|.
name|DirectoryDiff
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
name|INodeDirectoryWithSnapshot
operator|.
name|DirectoryDiffList
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
name|tools
operator|.
name|snapshot
operator|.
name|SnapshotDiff
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

begin_comment
comment|/**  * A helper class defining static methods for reading/writing snapshot related  * information from/to FSImage.  */
end_comment

begin_class
DECL|class|SnapshotFSImageFormat
specifier|public
class|class
name|SnapshotFSImageFormat
block|{
comment|/**    * Save snapshots and snapshot quota for a snapshottable directory.    * @param current The directory that the snapshots belongs to.    * @param out The {@link DataOutput} to write.    * @throws IOException    */
DECL|method|saveSnapshots (INodeDirectorySnapshottable current, DataOutput out)
specifier|public
specifier|static
name|void
name|saveSnapshots
parameter_list|(
name|INodeDirectorySnapshottable
name|current
parameter_list|,
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// list of snapshots in snapshotsByNames
name|ReadOnlyList
argument_list|<
name|Snapshot
argument_list|>
name|snapshots
init|=
name|current
operator|.
name|getSnapshotsByNames
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|snapshots
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Snapshot
name|s
range|:
name|snapshots
control|)
block|{
comment|// write the snapshot id
name|out
operator|.
name|writeInt
argument_list|(
name|s
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// snapshot quota
name|out
operator|.
name|writeInt
argument_list|(
name|current
operator|.
name|getSnapshotQuota
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Save SnapshotDiff list for an INodeDirectoryWithSnapshot.    * @param sNode The directory that the SnapshotDiff list belongs to.    * @param out The {@link DataOutput} to write.    */
specifier|private
specifier|static
parameter_list|<
name|N
extends|extends
name|INode
parameter_list|,
name|D
extends|extends
name|AbstractINodeDiff
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
parameter_list|>
DECL|method|saveINodeDiffs (final AbstractINodeDiffList<N, D> diffs, final DataOutput out, ReferenceMap referenceMap)
name|void
name|saveINodeDiffs
parameter_list|(
specifier|final
name|AbstractINodeDiffList
argument_list|<
name|N
argument_list|,
name|D
argument_list|>
name|diffs
parameter_list|,
specifier|final
name|DataOutput
name|out
parameter_list|,
name|ReferenceMap
name|referenceMap
parameter_list|)
throws|throws
name|IOException
block|{
comment|// Record the diffs in reversed order, so that we can find the correct
comment|// reference for INodes in the created list when loading the FSImage
if|if
condition|(
name|diffs
operator|==
literal|null
condition|)
block|{
name|out
operator|.
name|writeInt
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
comment|// no diffs
block|}
else|else
block|{
specifier|final
name|List
argument_list|<
name|D
argument_list|>
name|list
init|=
name|diffs
operator|.
name|asList
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|list
operator|.
name|size
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeInt
argument_list|(
name|size
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|size
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|write
argument_list|(
name|out
argument_list|,
name|referenceMap
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|saveDirectoryDiffList (final INodeDirectory dir, final DataOutput out, final ReferenceMap referenceMap )
specifier|public
specifier|static
name|void
name|saveDirectoryDiffList
parameter_list|(
specifier|final
name|INodeDirectory
name|dir
parameter_list|,
specifier|final
name|DataOutput
name|out
parameter_list|,
specifier|final
name|ReferenceMap
name|referenceMap
parameter_list|)
throws|throws
name|IOException
block|{
name|saveINodeDiffs
argument_list|(
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
argument_list|,
name|out
argument_list|,
name|referenceMap
argument_list|)
expr_stmt|;
block|}
DECL|method|saveFileDiffList (final INodeFile file, final DataOutput out)
specifier|public
specifier|static
name|void
name|saveFileDiffList
parameter_list|(
specifier|final
name|INodeFile
name|file
parameter_list|,
specifier|final
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|saveINodeDiffs
argument_list|(
name|file
operator|instanceof
name|FileWithSnapshot
condition|?
operator|(
operator|(
name|FileWithSnapshot
operator|)
name|file
operator|)
operator|.
name|getDiffs
argument_list|()
else|:
literal|null
argument_list|,
name|out
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
DECL|method|loadFileDiffList (DataInput in, FSImageFormat.Loader loader)
specifier|public
specifier|static
name|FileDiffList
name|loadFileDiffList
parameter_list|(
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|==
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
specifier|final
name|FileDiffList
name|diffs
init|=
operator|new
name|FileDiffList
argument_list|()
decl_stmt|;
name|FileDiff
name|posterior
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|FileDiff
name|d
init|=
name|loadFileDiff
argument_list|(
name|posterior
argument_list|,
name|in
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|diffs
operator|.
name|addFirst
argument_list|(
name|d
argument_list|)
expr_stmt|;
name|posterior
operator|=
name|d
expr_stmt|;
block|}
return|return
name|diffs
return|;
block|}
block|}
DECL|method|loadFileDiff (FileDiff posterior, DataInput in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|FileDiff
name|loadFileDiff
parameter_list|(
name|FileDiff
name|posterior
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// 1. Read the full path of the Snapshot root to identify the Snapshot
specifier|final
name|Snapshot
name|snapshot
init|=
name|loader
operator|.
name|getSnapshot
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// 2. Load file size
specifier|final
name|long
name|fileSize
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
comment|// 3. Load snapshotINode
specifier|final
name|INodeFile
name|snapshotINode
init|=
name|in
operator|.
name|readBoolean
argument_list|()
condition|?
name|loader
operator|.
name|loadINodeWithLocalName
argument_list|(
literal|true
argument_list|,
name|in
argument_list|)
operator|.
name|asFile
argument_list|()
else|:
literal|null
decl_stmt|;
return|return
operator|new
name|FileDiff
argument_list|(
name|snapshot
argument_list|,
name|snapshotINode
argument_list|,
name|posterior
argument_list|,
name|fileSize
argument_list|)
return|;
block|}
comment|/**    * Load a node stored in the created list from fsimage.    * @param createdNodeName The name of the created node.    * @param parent The directory that the created list belongs to.    * @return The created node.    */
DECL|method|loadCreated (byte[] createdNodeName, INodeDirectoryWithSnapshot parent)
specifier|private
specifier|static
name|INode
name|loadCreated
parameter_list|(
name|byte
index|[]
name|createdNodeName
parameter_list|,
name|INodeDirectoryWithSnapshot
name|parent
parameter_list|)
throws|throws
name|IOException
block|{
comment|// the INode in the created list should be a reference to another INode
comment|// in posterior SnapshotDiffs or one of the current children
for|for
control|(
name|DirectoryDiff
name|postDiff
range|:
name|parent
operator|.
name|getDiffs
argument_list|()
control|)
block|{
specifier|final
name|INode
name|d
init|=
name|postDiff
operator|.
name|getChildrenDiff
argument_list|()
operator|.
name|search
argument_list|(
name|ListType
operator|.
name|DELETED
argument_list|,
name|createdNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|!=
literal|null
condition|)
block|{
return|return
name|d
return|;
block|}
comment|// else go to the next SnapshotDiff
block|}
comment|// use the current child
name|INode
name|currentChild
init|=
name|parent
operator|.
name|getChild
argument_list|(
name|createdNodeName
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|currentChild
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find an INode associated with the INode "
operator|+
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|createdNodeName
argument_list|)
operator|+
literal|" in created list while loading FSImage."
argument_list|)
throw|;
block|}
return|return
name|currentChild
return|;
block|}
comment|/**    * Load the created list from fsimage.    * @param parent The directory that the created list belongs to.    * @param in The {@link DataInput} to read.    * @return The created list.    */
DECL|method|loadCreatedList (INodeDirectoryWithSnapshot parent, DataInput in)
specifier|private
specifier|static
name|List
argument_list|<
name|INode
argument_list|>
name|loadCreatedList
parameter_list|(
name|INodeDirectoryWithSnapshot
name|parent
parameter_list|,
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read the size of the created list
name|int
name|createdSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|INode
argument_list|>
name|createdList
init|=
operator|new
name|ArrayList
argument_list|<
name|INode
argument_list|>
argument_list|(
name|createdSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|createdSize
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|createdNodeName
init|=
name|FSImageSerialization
operator|.
name|readLocalName
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|INode
name|created
init|=
name|loadCreated
argument_list|(
name|createdNodeName
argument_list|,
name|parent
argument_list|)
decl_stmt|;
name|createdList
operator|.
name|add
argument_list|(
name|created
argument_list|)
expr_stmt|;
block|}
return|return
name|createdList
return|;
block|}
comment|/**    * Load the deleted list from the fsimage.    *     * @param parent The directory that the deleted list belongs to.    * @param createdList The created list associated with the deleted list in     *                    the same Diff.    * @param in The {@link DataInput} to read.    * @param loader The {@link Loader} instance.    * @return The deleted list.    */
DECL|method|loadDeletedList (INodeDirectoryWithSnapshot parent, List<INode> createdList, DataInput in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|List
argument_list|<
name|INode
argument_list|>
name|loadDeletedList
parameter_list|(
name|INodeDirectoryWithSnapshot
name|parent
parameter_list|,
name|List
argument_list|<
name|INode
argument_list|>
name|createdList
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|deletedSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|INode
argument_list|>
name|deletedList
init|=
operator|new
name|ArrayList
argument_list|<
name|INode
argument_list|>
argument_list|(
name|deletedSize
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|deletedSize
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|INode
name|deleted
init|=
name|loader
operator|.
name|loadINodeWithLocalName
argument_list|(
literal|true
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|deletedList
operator|.
name|add
argument_list|(
name|deleted
argument_list|)
expr_stmt|;
comment|// set parent: the parent field of an INode in the deleted list is not
comment|// useful, but set the parent here to be consistent with the original
comment|// fsdir tree.
name|deleted
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
return|return
name|deletedList
return|;
block|}
comment|/**    * Load snapshots and snapshotQuota for a Snapshottable directory.    * @param snapshottableParent The snapshottable directory for loading.    * @param numSnapshots The number of snapshots that the directory has.    * @param in The {@link DataInput} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    */
DECL|method|loadSnapshotList ( INodeDirectorySnapshottable snapshottableParent, int numSnapshots, DataInput in, FSImageFormat.Loader loader)
specifier|public
specifier|static
name|void
name|loadSnapshotList
parameter_list|(
name|INodeDirectorySnapshottable
name|snapshottableParent
parameter_list|,
name|int
name|numSnapshots
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numSnapshots
condition|;
name|i
operator|++
control|)
block|{
comment|// read snapshots
specifier|final
name|Snapshot
name|s
init|=
name|loader
operator|.
name|getSnapshot
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|s
operator|.
name|getRoot
argument_list|()
operator|.
name|setParent
argument_list|(
name|snapshottableParent
argument_list|)
expr_stmt|;
name|snapshottableParent
operator|.
name|addSnapshot
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
name|int
name|snapshotQuota
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|snapshottableParent
operator|.
name|setSnapshotQuota
argument_list|(
name|snapshotQuota
argument_list|)
expr_stmt|;
block|}
comment|/**    * Load the {@link SnapshotDiff} list for the INodeDirectoryWithSnapshot    * directory.    * @param dir The snapshottable directory for loading.    * @param in The {@link DataInput} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    */
DECL|method|loadDirectoryDiffList (INodeDirectory dir, DataInput in, FSImageFormat.Loader loader)
specifier|public
specifier|static
name|void
name|loadDirectoryDiffList
parameter_list|(
name|INodeDirectory
name|dir
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|int
name|size
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|dir
operator|instanceof
name|INodeDirectoryWithSnapshot
condition|)
block|{
name|INodeDirectoryWithSnapshot
name|withSnapshot
init|=
operator|(
name|INodeDirectoryWithSnapshot
operator|)
name|dir
decl_stmt|;
name|DirectoryDiffList
name|diffs
init|=
name|withSnapshot
operator|.
name|getDiffs
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|size
condition|;
name|i
operator|++
control|)
block|{
name|diffs
operator|.
name|addFirst
argument_list|(
name|loadDirectoryDiff
argument_list|(
name|withSnapshot
argument_list|,
name|in
argument_list|,
name|loader
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Load the snapshotINode field of {@link SnapshotDiff}.    * @param snapshot The Snapshot associated with the {@link SnapshotDiff}.    * @param in The {@link DataInput} to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    * @return The snapshotINode.    */
DECL|method|loadSnapshotINodeInDirectoryDiff ( Snapshot snapshot, DataInput in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|INodeDirectory
name|loadSnapshotINodeInDirectoryDiff
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// read the boolean indicating whether snapshotINode == Snapshot.Root
name|boolean
name|useRoot
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
if|if
condition|(
name|useRoot
condition|)
block|{
return|return
name|snapshot
operator|.
name|getRoot
argument_list|()
return|;
block|}
else|else
block|{
comment|// another boolean is used to indicate whether snapshotINode is non-null
return|return
name|in
operator|.
name|readBoolean
argument_list|()
condition|?
name|loader
operator|.
name|loadINodeWithLocalName
argument_list|(
literal|true
argument_list|,
name|in
argument_list|)
operator|.
name|asDirectory
argument_list|()
else|:
literal|null
return|;
block|}
block|}
comment|/**    * Load {@link DirectoryDiff} from fsimage.    * @param parent The directory that the SnapshotDiff belongs to.    * @param in The {@link DataInput} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    * @return A {@link DirectoryDiff}.    */
DECL|method|loadDirectoryDiff ( INodeDirectoryWithSnapshot parent, DataInput in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|DirectoryDiff
name|loadDirectoryDiff
parameter_list|(
name|INodeDirectoryWithSnapshot
name|parent
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
comment|// 1. Read the full path of the Snapshot root to identify the Snapshot
specifier|final
name|Snapshot
name|snapshot
init|=
name|loader
operator|.
name|getSnapshot
argument_list|(
name|in
argument_list|)
decl_stmt|;
comment|// 2. Load DirectoryDiff#childrenSize
name|int
name|childrenSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// 3. Load DirectoryDiff#snapshotINode
name|INodeDirectory
name|snapshotINode
init|=
name|loadSnapshotINodeInDirectoryDiff
argument_list|(
name|snapshot
argument_list|,
name|in
argument_list|,
name|loader
argument_list|)
decl_stmt|;
comment|// 4. Load the created list in SnapshotDiff#Diff
name|List
argument_list|<
name|INode
argument_list|>
name|createdList
init|=
name|loadCreatedList
argument_list|(
name|parent
argument_list|,
name|in
argument_list|)
decl_stmt|;
comment|// 5. Load the deleted list in SnapshotDiff#Diff
name|List
argument_list|<
name|INode
argument_list|>
name|deletedList
init|=
name|loadDeletedList
argument_list|(
name|parent
argument_list|,
name|createdList
argument_list|,
name|in
argument_list|,
name|loader
argument_list|)
decl_stmt|;
comment|// 6. Compose the SnapshotDiff
name|List
argument_list|<
name|DirectoryDiff
argument_list|>
name|diffs
init|=
name|parent
operator|.
name|getDiffs
argument_list|()
operator|.
name|asList
argument_list|()
decl_stmt|;
name|DirectoryDiff
name|sdiff
init|=
operator|new
name|DirectoryDiff
argument_list|(
name|snapshot
argument_list|,
name|snapshotINode
argument_list|,
name|diffs
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|diffs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|childrenSize
argument_list|,
name|createdList
argument_list|,
name|deletedList
argument_list|)
decl_stmt|;
return|return
name|sdiff
return|;
block|}
comment|/** A reference map for fsimage serialization. */
DECL|class|ReferenceMap
specifier|public
specifier|static
class|class
name|ReferenceMap
block|{
comment|/**      * Used to indicate whether the reference node itself has been saved      */
DECL|field|referenceMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|INodeReference
operator|.
name|WithCount
argument_list|>
name|referenceMap
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|INodeReference
operator|.
name|WithCount
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Used to record whether the subtree of the reference node has been saved       */
DECL|field|dirMap
specifier|private
specifier|final
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|dirMap
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|writeINodeReferenceWithCount ( INodeReference.WithCount withCount, DataOutput out, boolean writeUnderConstruction)
specifier|public
name|void
name|writeINodeReferenceWithCount
parameter_list|(
name|INodeReference
operator|.
name|WithCount
name|withCount
parameter_list|,
name|DataOutput
name|out
parameter_list|,
name|boolean
name|writeUnderConstruction
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|INode
name|referred
init|=
name|withCount
operator|.
name|getReferredINode
argument_list|()
decl_stmt|;
specifier|final
name|long
name|id
init|=
name|withCount
operator|.
name|getId
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|firstReferred
init|=
operator|!
name|referenceMap
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeBoolean
argument_list|(
name|firstReferred
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstReferred
condition|)
block|{
name|FSImageSerialization
operator|.
name|saveINode2Image
argument_list|(
name|referred
argument_list|,
name|out
argument_list|,
name|writeUnderConstruction
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|referenceMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|withCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|writeLong
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|toProcessSubtree (long id)
specifier|public
name|boolean
name|toProcessSubtree
parameter_list|(
name|long
name|id
parameter_list|)
block|{
if|if
condition|(
name|dirMap
operator|.
name|containsKey
argument_list|(
name|id
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|dirMap
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|id
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
DECL|method|loadINodeReferenceWithCount ( boolean isSnapshotINode, DataInput in, FSImageFormat.Loader loader )
specifier|public
name|INodeReference
operator|.
name|WithCount
name|loadINodeReferenceWithCount
parameter_list|(
name|boolean
name|isSnapshotINode
parameter_list|,
name|DataInput
name|in
parameter_list|,
name|FSImageFormat
operator|.
name|Loader
name|loader
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|boolean
name|firstReferred
init|=
name|in
operator|.
name|readBoolean
argument_list|()
decl_stmt|;
specifier|final
name|INodeReference
operator|.
name|WithCount
name|withCount
decl_stmt|;
if|if
condition|(
name|firstReferred
condition|)
block|{
specifier|final
name|INode
name|referred
init|=
name|loader
operator|.
name|loadINodeWithLocalName
argument_list|(
name|isSnapshotINode
argument_list|,
name|in
argument_list|)
decl_stmt|;
name|withCount
operator|=
operator|new
name|INodeReference
operator|.
name|WithCount
argument_list|(
literal|null
argument_list|,
name|referred
argument_list|)
expr_stmt|;
name|referenceMap
operator|.
name|put
argument_list|(
name|withCount
operator|.
name|getId
argument_list|()
argument_list|,
name|withCount
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|long
name|id
init|=
name|in
operator|.
name|readLong
argument_list|()
decl_stmt|;
name|withCount
operator|=
name|referenceMap
operator|.
name|get
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|withCount
operator|.
name|incrementReferenceCount
argument_list|()
expr_stmt|;
block|}
return|return
name|withCount
return|;
block|}
block|}
block|}
end_class

end_unit

