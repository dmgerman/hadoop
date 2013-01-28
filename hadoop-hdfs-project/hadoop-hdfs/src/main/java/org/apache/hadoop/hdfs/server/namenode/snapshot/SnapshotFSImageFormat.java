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
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
name|Collections
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
name|snapshot
operator|.
name|INodeDirectoryWithSnapshot
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
name|server
operator|.
name|namenode
operator|.
name|snapshot
operator|.
name|INodeDirectoryWithSnapshot
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
name|server
operator|.
name|namenode
operator|.
name|snapshot
operator|.
name|Snapshot
operator|.
name|Root
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
comment|/**    * Save snapshots and snapshot quota for a snapshottable directory.    * @param current The directory that the snapshots belongs to.    * @param out The {@link DataOutputStream} to write.    * @throws IOException    */
DECL|method|saveSnapshots (INodeDirectorySnapshottable current, DataOutputStream out)
specifier|public
specifier|static
name|void
name|saveSnapshots
parameter_list|(
name|INodeDirectorySnapshottable
name|current
parameter_list|,
name|DataOutputStream
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
name|ss
range|:
name|snapshots
control|)
block|{
comment|// write the snapshot
name|ss
operator|.
name|write
argument_list|(
name|out
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
comment|/**    * Save SnapshotDiff list for an INodeDirectoryWithSnapshot.    * @param sNode The directory that the SnapshotDiff list belongs to.    * @param out The {@link DataOutputStream} to write.    */
DECL|method|saveSnapshotDiffs (INodeDirectoryWithSnapshot sNode, DataOutputStream out)
specifier|public
specifier|static
name|void
name|saveSnapshotDiffs
parameter_list|(
name|INodeDirectoryWithSnapshot
name|sNode
parameter_list|,
name|DataOutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
comment|// # of SnapshotDiff
name|List
argument_list|<
name|SnapshotDiff
argument_list|>
name|diffs
init|=
name|sNode
operator|.
name|getSnapshotDiffs
argument_list|()
decl_stmt|;
comment|// Record the SnapshotDiff in reversed order, so that we can find the
comment|// correct reference for INodes in the created list when loading the
comment|// FSImage
name|out
operator|.
name|writeInt
argument_list|(
name|diffs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|diffs
operator|.
name|size
argument_list|()
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
name|SnapshotDiff
name|sdiff
init|=
name|diffs
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|sdiff
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
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
name|SnapshotDiff
name|postDiff
range|:
name|parent
operator|.
name|getSnapshotDiffs
argument_list|()
control|)
block|{
name|INode
name|created
init|=
name|findCreated
argument_list|(
name|createdNodeName
argument_list|,
name|postDiff
operator|.
name|getDiff
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
block|{
return|return
name|created
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
comment|/**    * Search the given {@link ChildrenDiff} to find an inode matching the specific name.    * @param createdNodeName The name of the node for searching.    * @param diff The given {@link ChildrenDiff} where to search the node.    * @return The matched inode. Return null if no matched inode can be found.    */
DECL|method|findCreated (byte[] createdNodeName, ChildrenDiff diff)
specifier|private
specifier|static
name|INode
name|findCreated
parameter_list|(
name|byte
index|[]
name|createdNodeName
parameter_list|,
name|ChildrenDiff
name|diff
parameter_list|)
block|{
name|INode
name|c
init|=
name|diff
operator|.
name|searchCreated
argument_list|(
name|createdNodeName
argument_list|)
decl_stmt|;
name|INode
name|d
init|=
name|diff
operator|.
name|searchDeleted
argument_list|(
name|createdNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|null
operator|&&
name|d
operator|!=
literal|null
condition|)
block|{
comment|// if an INode with the same name is only contained in the deleted
comment|// list, then the node should be the snapshot copy of a deleted
comment|// node, and the node in the created list should be its reference
return|return
name|d
return|;
block|}
elseif|else
if|if
condition|(
name|c
operator|!=
literal|null
operator|&&
name|d
operator|!=
literal|null
condition|)
block|{
comment|// in a posterior SnapshotDiff, if the created/deleted lists both
comment|// contains nodes with the same name (c& d), there are two
comment|// possibilities:
comment|//
comment|// 1) c and d are used to represent a modification, and
comment|// 2) d indicates the deletion of the node, while c was originally
comment|// contained in the created list of a later snapshot, but c was
comment|// moved here because of the snapshot deletion.
comment|//
comment|// For case 1), c and d should be both INodeFile and should share
comment|// the same blockInfo list.
if|if
condition|(
name|c
operator|.
name|isFile
argument_list|()
operator|&&
name|INodeFile
operator|.
name|isOfSameFile
argument_list|(
operator|(
name|INodeFile
operator|)
name|c
argument_list|,
operator|(
name|INodeFile
operator|)
name|d
argument_list|)
condition|)
block|{
return|return
name|c
return|;
block|}
else|else
block|{
return|return
name|d
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Load the created list from fsimage.    * @param parent The directory that the created list belongs to.    * @param in The {@link DataInputStream} to read.    * @return The created list.    */
DECL|method|loadCreatedList (INodeDirectoryWithSnapshot parent, DataInputStream in)
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
name|DataInputStream
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
operator|new
name|byte
index|[
name|in
operator|.
name|readShort
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|createdNodeName
argument_list|)
expr_stmt|;
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
comment|/**    * Load the deleted list from the fsimage.    *     * @param parent The directory that the deleted list belongs to.    * @param createdList The created list associated with the deleted list in     *                    the same Diff.    * @param in The {@link DataInputStream} to read.    * @param loader The {@link Loader} instance. Used to call the    *               {@link Loader#loadINode(DataInputStream)} method.    * @return The deleted list.    */
DECL|method|loadDeletedList (INodeDirectoryWithSnapshot parent, List<INode> createdList, DataInputStream in, FSImageFormat.Loader loader)
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
name|DataInputStream
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
name|byte
index|[]
name|deletedNodeName
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readShort
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|deletedNodeName
argument_list|)
expr_stmt|;
name|INode
name|deleted
init|=
name|loader
operator|.
name|loadINode
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|deleted
operator|.
name|setLocalName
argument_list|(
name|deletedNodeName
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|deleted
operator|instanceof
name|INodeFile
operator|&&
operator|(
operator|(
name|INodeFile
operator|)
name|deleted
operator|)
operator|.
name|getBlocks
argument_list|()
operator|==
literal|null
condition|)
block|{
comment|// if deleted is an INodeFile, and its blocks is null, then deleted
comment|// must be an INodeFileWithLink, and we need to rebuild its next link
name|int
name|c
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|createdList
argument_list|,
name|deletedNodeName
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot find the INode linked with the INode "
operator|+
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|deletedNodeName
argument_list|)
operator|+
literal|" in deleted list while loading FSImage."
argument_list|)
throw|;
block|}
comment|// deleted must be an FileWithSnapshot (INodeFileSnapshot or
comment|// INodeFileUnderConstructionSnapshot)
name|FileWithSnapshot
name|deletedWithLink
init|=
operator|(
name|FileWithSnapshot
operator|)
name|deleted
decl_stmt|;
name|INodeFile
name|cNode
init|=
operator|(
name|INodeFile
operator|)
name|createdList
operator|.
name|get
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|INodeFileWithSnapshot
name|cNodeWithLink
init|=
operator|(
name|INodeFileWithSnapshot
operator|)
name|cNode
decl_stmt|;
operator|(
operator|(
name|INodeFile
operator|)
name|deleted
operator|)
operator|.
name|setBlocks
argument_list|(
name|cNode
operator|.
name|getBlocks
argument_list|()
argument_list|)
expr_stmt|;
comment|// insert deleted into the circular list
name|cNodeWithLink
operator|.
name|insertBefore
argument_list|(
name|deletedWithLink
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|deletedList
return|;
block|}
comment|/**    * Load snapshots and snapshotQuota for a Snapshottable directory.    * @param snapshottableParent The snapshottable directory for loading.    * @param numSnapshots The number of snapshots that the directory has.    * @param in The {@link DataInputStream} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    */
DECL|method|loadSnapshotList ( INodeDirectorySnapshottable snapshottableParent, int numSnapshots, DataInputStream in, FSImageFormat.Loader loader)
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
name|DataInputStream
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
name|Snapshot
name|ss
init|=
name|loadSnapshot
argument_list|(
name|snapshottableParent
argument_list|,
name|in
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|snapshottableParent
operator|.
name|addSnapshot
argument_list|(
name|ss
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
comment|/**    * Load a {@link Snapshot} from fsimage.    * @param parent The directory that the snapshot belongs to.    * @param in The {@link DataInputStream} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    * @return The snapshot.    */
DECL|method|loadSnapshot (INodeDirectorySnapshottable parent, DataInputStream in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|Snapshot
name|loadSnapshot
parameter_list|(
name|INodeDirectorySnapshottable
name|parent
parameter_list|,
name|DataInputStream
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
name|snapshotId
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|snapshotName
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readShort
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|snapshotName
argument_list|)
expr_stmt|;
name|INode
name|rootNode
init|=
name|loader
operator|.
name|loadINode
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|rootNode
operator|.
name|setLocalName
argument_list|(
name|snapshotName
argument_list|)
expr_stmt|;
name|rootNode
operator|.
name|setParent
argument_list|(
name|parent
argument_list|)
expr_stmt|;
return|return
operator|new
name|Snapshot
argument_list|(
name|snapshotId
argument_list|,
operator|(
name|INodeDirectory
operator|)
name|rootNode
argument_list|)
return|;
block|}
comment|/**    * Load the {@link SnapshotDiff} list for the INodeDirectoryWithSnapshot    * directory.    * @param snapshottableParent The snapshottable directory for loading.    * @param numSnapshotDiffs The number of {@link SnapshotDiff} that the     *                         directory has.    * @param in The {@link DataInputStream} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    */
DECL|method|loadSnapshotDiffList ( INodeDirectoryWithSnapshot parentWithSnapshot, int numSnapshotDiffs, DataInputStream in, FSImageFormat.Loader loader)
specifier|public
specifier|static
name|void
name|loadSnapshotDiffList
parameter_list|(
name|INodeDirectoryWithSnapshot
name|parentWithSnapshot
parameter_list|,
name|int
name|numSnapshotDiffs
parameter_list|,
name|DataInputStream
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
name|numSnapshotDiffs
condition|;
name|i
operator|++
control|)
block|{
name|SnapshotDiff
name|diff
init|=
name|loadSnapshotDiff
argument_list|(
name|parentWithSnapshot
argument_list|,
name|in
argument_list|,
name|loader
argument_list|)
decl_stmt|;
name|parentWithSnapshot
operator|.
name|insertDiff
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Use the given full path to a {@link Root} directory to find the    * associated snapshot.    */
DECL|method|findSnapshot (String sRootFullPath, FSDirectory fsdir)
specifier|private
specifier|static
name|Snapshot
name|findSnapshot
parameter_list|(
name|String
name|sRootFullPath
parameter_list|,
name|FSDirectory
name|fsdir
parameter_list|)
throws|throws
name|IOException
block|{
comment|// find the root
name|INode
name|root
init|=
name|fsdir
operator|.
name|getINode
argument_list|(
name|sRootFullPath
argument_list|)
decl_stmt|;
name|INodeDirectorySnapshottable
name|snapshotRoot
init|=
name|INodeDirectorySnapshottable
operator|.
name|valueOf
argument_list|(
name|root
operator|.
name|getParent
argument_list|()
argument_list|,
name|root
operator|.
name|getParent
argument_list|()
operator|.
name|getFullPathName
argument_list|()
argument_list|)
decl_stmt|;
comment|// find the snapshot
return|return
name|snapshotRoot
operator|.
name|getSnapshot
argument_list|(
name|root
operator|.
name|getLocalNameBytes
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Load the snapshotINode field of {@link SnapshotDiff}.    * @param snapshot The Snapshot associated with the {@link SnapshotDiff}.    * @param in The {@link DataInputStream} to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    * @return The snapshotINode.    */
DECL|method|loadSnapshotINodeInSnapshotDiff ( Snapshot snapshot, DataInputStream in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|INodeDirectory
name|loadSnapshotINodeInSnapshotDiff
parameter_list|(
name|Snapshot
name|snapshot
parameter_list|,
name|DataInputStream
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
if|if
condition|(
name|in
operator|.
name|readBoolean
argument_list|()
condition|)
block|{
name|byte
index|[]
name|localName
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|readShort
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|localName
argument_list|)
expr_stmt|;
name|INodeDirectory
name|snapshotINode
init|=
operator|(
name|INodeDirectory
operator|)
name|loader
operator|.
name|loadINode
argument_list|(
name|in
argument_list|)
decl_stmt|;
name|snapshotINode
operator|.
name|setLocalName
argument_list|(
name|localName
argument_list|)
expr_stmt|;
return|return
name|snapshotINode
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * Load {@link SnapshotDiff} from fsimage.    * @param parent The directory that the SnapshotDiff belongs to.    * @param in The {@link DataInputStream} instance to read.    * @param loader The {@link Loader} instance that this loading procedure is     *               using.    * @return A {@link SnapshotDiff}.    */
DECL|method|loadSnapshotDiff ( INodeDirectoryWithSnapshot parent, DataInputStream in, FSImageFormat.Loader loader)
specifier|private
specifier|static
name|SnapshotDiff
name|loadSnapshotDiff
parameter_list|(
name|INodeDirectoryWithSnapshot
name|parent
parameter_list|,
name|DataInputStream
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
comment|// 1. Load SnapshotDiff#childrenSize
name|int
name|childrenSize
init|=
name|in
operator|.
name|readInt
argument_list|()
decl_stmt|;
comment|// 2. Read the full path of the Snapshot's Root, identify
comment|//    SnapshotDiff#Snapshot
name|Snapshot
name|snapshot
init|=
name|findSnapshot
argument_list|(
name|FSImageSerialization
operator|.
name|readString
argument_list|(
name|in
argument_list|)
argument_list|,
name|loader
operator|.
name|getFSDirectoryInLoading
argument_list|()
argument_list|)
decl_stmt|;
comment|// 3. Load SnapshotDiff#snapshotINode
name|INodeDirectory
name|snapshotINode
init|=
name|loadSnapshotINodeInSnapshotDiff
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
name|SnapshotDiff
name|sdiff
init|=
name|parent
operator|.
expr|new
name|SnapshotDiff
argument_list|(
name|snapshot
argument_list|,
name|childrenSize
argument_list|,
name|snapshotINode
argument_list|,
name|parent
operator|.
name|getSnapshotDiffs
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|?
literal|null
else|:
name|parent
operator|.
name|getSnapshotDiffs
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
block|}
end_class

end_unit

