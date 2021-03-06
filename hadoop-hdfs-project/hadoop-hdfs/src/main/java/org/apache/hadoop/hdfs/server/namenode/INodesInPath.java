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
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|HdfsConstants
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
name|common
operator|.
name|HdfsServerConstants
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
import|import static
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
name|CURRENT_STATE_ID
import|;
end_import

begin_import
import|import static
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
name|ID_INTEGER_COMPARATOR
import|;
end_import

begin_comment
comment|/**  * Contains INodes information resolved from a given path.  */
end_comment

begin_class
DECL|class|INodesInPath
specifier|public
class|class
name|INodesInPath
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|INodesInPath
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * @return true if path component is {@link HdfsConstants#DOT_SNAPSHOT_DIR}    */
DECL|method|isDotSnapshotDir (byte[] pathComponent)
specifier|private
specifier|static
name|boolean
name|isDotSnapshotDir
parameter_list|(
name|byte
index|[]
name|pathComponent
parameter_list|)
block|{
return|return
name|pathComponent
operator|!=
literal|null
operator|&&
name|Arrays
operator|.
name|equals
argument_list|(
name|HdfsServerConstants
operator|.
name|DOT_SNAPSHOT_DIR_BYTES
argument_list|,
name|pathComponent
argument_list|)
return|;
block|}
DECL|method|getINodes (final INode inode)
specifier|private
specifier|static
name|INode
index|[]
name|getINodes
parameter_list|(
specifier|final
name|INode
name|inode
parameter_list|)
block|{
name|int
name|depth
init|=
literal|0
decl_stmt|,
name|index
decl_stmt|;
name|INode
name|tmp
init|=
name|inode
decl_stmt|;
while|while
condition|(
name|tmp
operator|!=
literal|null
condition|)
block|{
name|depth
operator|++
expr_stmt|;
name|tmp
operator|=
name|tmp
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
name|INode
index|[]
name|inodes
init|=
operator|new
name|INode
index|[
name|depth
index|]
decl_stmt|;
name|tmp
operator|=
name|inode
expr_stmt|;
name|index
operator|=
name|depth
expr_stmt|;
while|while
condition|(
name|tmp
operator|!=
literal|null
condition|)
block|{
name|index
operator|--
expr_stmt|;
name|inodes
index|[
name|index
index|]
operator|=
name|tmp
expr_stmt|;
name|tmp
operator|=
name|tmp
operator|.
name|getParent
argument_list|()
expr_stmt|;
block|}
return|return
name|inodes
return|;
block|}
DECL|method|getPaths (final INode[] inodes)
specifier|private
specifier|static
name|byte
index|[]
index|[]
name|getPaths
parameter_list|(
specifier|final
name|INode
index|[]
name|inodes
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|paths
init|=
operator|new
name|byte
index|[
name|inodes
operator|.
name|length
index|]
index|[]
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
name|inodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|paths
index|[
name|i
index|]
operator|=
name|inodes
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
expr_stmt|;
block|}
return|return
name|paths
return|;
block|}
comment|/**    * Construct {@link INodesInPath} from {@link INode}.    *    * @param inode to construct from    * @return INodesInPath    */
DECL|method|fromINode (INode inode)
specifier|static
name|INodesInPath
name|fromINode
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
name|INode
index|[]
name|inodes
init|=
name|getINodes
argument_list|(
name|inode
argument_list|)
decl_stmt|;
name|byte
index|[]
index|[]
name|paths
init|=
name|getPaths
argument_list|(
name|inodes
argument_list|)
decl_stmt|;
return|return
operator|new
name|INodesInPath
argument_list|(
name|inodes
argument_list|,
name|paths
argument_list|)
return|;
block|}
comment|/**    * Construct {@link INodesInPath} from {@link INode} and its root    * {@link INodeDirectory}. INodesInPath constructed this way will    * each have its snapshot and latest snapshot id filled in.    *    * This routine is specifically for    * {@link LeaseManager#getINodeWithLeases(INodeDirectory)} to get    * open files along with their snapshot details which is used during    * new snapshot creation to capture their meta data.    *    * @param rootDir the root {@link INodeDirectory} under which inode    *                needs to be resolved    * @param inode the {@link INode} to be resolved    * @return INodesInPath    */
DECL|method|fromINode (final INodeDirectory rootDir, INode inode)
specifier|static
name|INodesInPath
name|fromINode
parameter_list|(
specifier|final
name|INodeDirectory
name|rootDir
parameter_list|,
name|INode
name|inode
parameter_list|)
block|{
name|byte
index|[]
index|[]
name|paths
init|=
name|getPaths
argument_list|(
name|getINodes
argument_list|(
name|inode
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|resolve
argument_list|(
name|rootDir
argument_list|,
name|paths
argument_list|)
return|;
block|}
DECL|method|fromComponents (byte[][] components)
specifier|static
name|INodesInPath
name|fromComponents
parameter_list|(
name|byte
index|[]
index|[]
name|components
parameter_list|)
block|{
return|return
operator|new
name|INodesInPath
argument_list|(
operator|new
name|INode
index|[
name|components
operator|.
name|length
index|]
argument_list|,
name|components
argument_list|)
return|;
block|}
comment|/**    * Retrieve existing INodes from a path.  The number of INodes is equal    * to the number of path components.  For a snapshot path    * (e.g. /foo/.snapshot/s1/bar), the ".snapshot/s1" will be represented in    * one path component corresponding to its Snapshot.Root inode.  This 1-1    * mapping ensures the path can always be properly reconstructed.    *    *<p>    * Example:<br>    * Given the path /c1/c2/c3 where only /c1/c2 exists, resulting in the    * following path components: ["","c1","c2","c3"]    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"])</code> should fill    * the array with [rootINode,c1,c2],<br>    *<code>getExistingPathINodes(["","c1","c2","c3"])</code> should    * fill the array with [rootINode,c1,c2,null]    *     * @param startingDir the starting directory    * @param components array of path component name    * @return the specified number of existing INodes in the path    */
DECL|method|resolve (final INodeDirectory startingDir, final byte[][] components)
specifier|static
name|INodesInPath
name|resolve
parameter_list|(
specifier|final
name|INodeDirectory
name|startingDir
parameter_list|,
specifier|final
name|byte
index|[]
index|[]
name|components
parameter_list|)
block|{
return|return
name|resolve
argument_list|(
name|startingDir
argument_list|,
name|components
argument_list|,
literal|false
argument_list|)
return|;
block|}
DECL|method|resolve (final INodeDirectory startingDir, byte[][] components, final boolean isRaw)
specifier|static
name|INodesInPath
name|resolve
parameter_list|(
specifier|final
name|INodeDirectory
name|startingDir
parameter_list|,
name|byte
index|[]
index|[]
name|components
parameter_list|,
specifier|final
name|boolean
name|isRaw
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|startingDir
operator|.
name|compareTo
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|)
operator|==
literal|0
argument_list|)
expr_stmt|;
name|INode
name|curNode
init|=
name|startingDir
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|inodeNum
init|=
literal|0
decl_stmt|;
name|INode
index|[]
name|inodes
init|=
operator|new
name|INode
index|[
name|components
operator|.
name|length
index|]
decl_stmt|;
name|boolean
name|isSnapshot
init|=
literal|false
decl_stmt|;
name|int
name|snapshotId
init|=
name|CURRENT_STATE_ID
decl_stmt|;
while|while
condition|(
name|count
operator|<
name|components
operator|.
name|length
operator|&&
name|curNode
operator|!=
literal|null
condition|)
block|{
specifier|final
name|boolean
name|lastComp
init|=
operator|(
name|count
operator|==
name|components
operator|.
name|length
operator|-
literal|1
operator|)
decl_stmt|;
name|inodes
index|[
name|inodeNum
operator|++
index|]
operator|=
name|curNode
expr_stmt|;
specifier|final
name|boolean
name|isRef
init|=
name|curNode
operator|.
name|isReference
argument_list|()
decl_stmt|;
specifier|final
name|boolean
name|isDir
init|=
name|curNode
operator|.
name|isDirectory
argument_list|()
decl_stmt|;
specifier|final
name|INodeDirectory
name|dir
init|=
name|isDir
condition|?
name|curNode
operator|.
name|asDirectory
argument_list|()
else|:
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|isRef
operator|&&
name|isDir
operator|&&
name|dir
operator|.
name|isWithSnapshot
argument_list|()
condition|)
block|{
comment|//if the path is a non-snapshot path, update the latest snapshot.
if|if
condition|(
operator|!
name|isSnapshot
operator|&&
name|shouldUpdateLatestId
argument_list|(
name|dir
operator|.
name|getDirectoryWithSnapshotFeature
argument_list|()
operator|.
name|getLastSnapshotId
argument_list|()
argument_list|,
name|snapshotId
argument_list|)
condition|)
block|{
name|snapshotId
operator|=
name|dir
operator|.
name|getDirectoryWithSnapshotFeature
argument_list|()
operator|.
name|getLastSnapshotId
argument_list|()
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
name|isRef
operator|&&
name|isDir
operator|&&
operator|!
name|lastComp
condition|)
block|{
comment|// If the curNode is a reference node, need to check its dstSnapshot:
comment|// 1. if the existing snapshot is no later than the dstSnapshot (which
comment|// is the latest snapshot in dst before the rename), the changes
comment|// should be recorded in previous snapshots (belonging to src).
comment|// 2. however, if the ref node is already the last component, we still
comment|// need to know the latest snapshot among the ref node's ancestors,
comment|// in case of processing a deletion operation. Thus we do not overwrite
comment|// the latest snapshot if lastComp is true. In case of the operation is
comment|// a modification operation, we do a similar check in corresponding
comment|// recordModification method.
if|if
condition|(
operator|!
name|isSnapshot
condition|)
block|{
name|int
name|dstSnapshotId
init|=
name|curNode
operator|.
name|asReference
argument_list|()
operator|.
name|getDstSnapshotId
argument_list|()
decl_stmt|;
if|if
condition|(
name|snapshotId
operator|==
name|CURRENT_STATE_ID
operator|||
comment|// no snapshot in dst tree of rename
operator|(
name|dstSnapshotId
operator|!=
name|CURRENT_STATE_ID
operator|&&
name|dstSnapshotId
operator|>=
name|snapshotId
operator|)
condition|)
block|{
comment|// the above scenario
name|int
name|lastSnapshot
init|=
name|CURRENT_STATE_ID
decl_stmt|;
name|DirectoryWithSnapshotFeature
name|sf
decl_stmt|;
if|if
condition|(
name|curNode
operator|.
name|isDirectory
argument_list|()
operator|&&
operator|(
name|sf
operator|=
name|curNode
operator|.
name|asDirectory
argument_list|()
operator|.
name|getDirectoryWithSnapshotFeature
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|lastSnapshot
operator|=
name|sf
operator|.
name|getLastSnapshotId
argument_list|()
expr_stmt|;
block|}
name|snapshotId
operator|=
name|lastSnapshot
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|lastComp
operator|||
operator|!
name|isDir
condition|)
block|{
break|break;
block|}
specifier|final
name|byte
index|[]
name|childName
init|=
name|components
index|[
operator|++
name|count
index|]
decl_stmt|;
comment|// check if the next byte[] in components is for ".snapshot"
if|if
condition|(
name|isDotSnapshotDir
argument_list|(
name|childName
argument_list|)
operator|&&
name|dir
operator|.
name|isSnapshottable
argument_list|()
condition|)
block|{
name|isSnapshot
operator|=
literal|true
expr_stmt|;
comment|// check if ".snapshot" is the last element of components
if|if
condition|(
name|count
operator|==
name|components
operator|.
name|length
operator|-
literal|1
condition|)
block|{
break|break;
block|}
comment|// Resolve snapshot root
specifier|final
name|Snapshot
name|s
init|=
name|dir
operator|.
name|getSnapshot
argument_list|(
name|components
index|[
name|count
operator|+
literal|1
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|s
operator|==
literal|null
condition|)
block|{
name|curNode
operator|=
literal|null
expr_stmt|;
comment|// snapshot not found
block|}
else|else
block|{
name|curNode
operator|=
name|s
operator|.
name|getRoot
argument_list|()
expr_stmt|;
name|snapshotId
operator|=
name|s
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
comment|// combine .snapshot& name into 1 component element to ensure
comment|// 1-to-1 correspondence between components and inodes arrays is
comment|// preserved so a path can be reconstructed.
name|byte
index|[]
index|[]
name|componentsCopy
init|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|components
argument_list|,
name|components
operator|.
name|length
operator|-
literal|1
argument_list|)
decl_stmt|;
name|componentsCopy
index|[
name|count
index|]
operator|=
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|components
argument_list|,
name|count
argument_list|,
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// shift the remaining components after snapshot name
name|int
name|start
init|=
name|count
operator|+
literal|2
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|components
argument_list|,
name|start
argument_list|,
name|componentsCopy
argument_list|,
name|count
operator|+
literal|1
argument_list|,
name|components
operator|.
name|length
operator|-
name|start
argument_list|)
expr_stmt|;
name|components
operator|=
name|componentsCopy
expr_stmt|;
comment|// reduce the inodes array to compensate for reduction in components
name|inodes
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|inodes
argument_list|,
name|components
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// normal case, and also for resolving file/dir under snapshot root
name|curNode
operator|=
name|dir
operator|.
name|getChild
argument_list|(
name|childName
argument_list|,
name|isSnapshot
condition|?
name|snapshotId
else|:
name|CURRENT_STATE_ID
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|INodesInPath
argument_list|(
name|inodes
argument_list|,
name|components
argument_list|,
name|isRaw
argument_list|,
name|isSnapshot
argument_list|,
name|snapshotId
argument_list|)
return|;
block|}
DECL|method|shouldUpdateLatestId (int sid, int snapshotId)
specifier|private
specifier|static
name|boolean
name|shouldUpdateLatestId
parameter_list|(
name|int
name|sid
parameter_list|,
name|int
name|snapshotId
parameter_list|)
block|{
return|return
name|snapshotId
operator|==
name|CURRENT_STATE_ID
operator|||
operator|(
name|sid
operator|!=
name|CURRENT_STATE_ID
operator|&&
name|ID_INTEGER_COMPARATOR
operator|.
name|compare
argument_list|(
name|snapshotId
argument_list|,
name|sid
argument_list|)
operator|<
literal|0
operator|)
return|;
block|}
comment|/**    * Replace an inode of the given INodesInPath in the given position. We do a    * deep copy of the INode array.    * @param pos the position of the replacement    * @param inode the new inode    * @return a new INodesInPath instance    */
DECL|method|replace (INodesInPath iip, int pos, INode inode)
specifier|public
specifier|static
name|INodesInPath
name|replace
parameter_list|(
name|INodesInPath
name|iip
parameter_list|,
name|int
name|pos
parameter_list|,
name|INode
name|inode
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|iip
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|pos
operator|>
literal|0
comment|// no for root
operator|&&
name|pos
operator|<
name|iip
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|iip
operator|.
name|getINode
argument_list|(
name|pos
argument_list|)
operator|==
literal|null
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|iip
operator|.
name|getINode
argument_list|(
name|pos
operator|-
literal|1
argument_list|)
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
name|INode
index|[]
name|inodes
init|=
operator|new
name|INode
index|[
name|iip
operator|.
name|inodes
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|iip
operator|.
name|inodes
argument_list|,
literal|0
argument_list|,
name|inodes
argument_list|,
literal|0
argument_list|,
name|inodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|inodes
index|[
name|pos
index|]
operator|=
name|inode
expr_stmt|;
return|return
operator|new
name|INodesInPath
argument_list|(
name|inodes
argument_list|,
name|iip
operator|.
name|path
argument_list|,
name|iip
operator|.
name|isRaw
argument_list|,
name|iip
operator|.
name|isSnapshot
argument_list|,
name|iip
operator|.
name|snapshotId
argument_list|)
return|;
block|}
comment|/**    * Extend a given INodesInPath with a child INode. The child INode will be    * appended to the end of the new INodesInPath.    */
DECL|method|append (INodesInPath iip, INode child, byte[] childName)
specifier|public
specifier|static
name|INodesInPath
name|append
parameter_list|(
name|INodesInPath
name|iip
parameter_list|,
name|INode
name|child
parameter_list|,
name|byte
index|[]
name|childName
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|iip
operator|.
name|length
argument_list|()
operator|>
literal|0
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|iip
operator|.
name|getLastINode
argument_list|()
operator|!=
literal|null
operator|&&
name|iip
operator|.
name|getLastINode
argument_list|()
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|INode
index|[]
name|inodes
init|=
operator|new
name|INode
index|[
name|iip
operator|.
name|length
argument_list|()
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|iip
operator|.
name|inodes
argument_list|,
literal|0
argument_list|,
name|inodes
argument_list|,
literal|0
argument_list|,
name|inodes
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|inodes
index|[
name|inodes
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|child
expr_stmt|;
name|byte
index|[]
index|[]
name|path
init|=
operator|new
name|byte
index|[
name|iip
operator|.
name|path
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
name|iip
operator|.
name|path
argument_list|,
literal|0
argument_list|,
name|path
argument_list|,
literal|0
argument_list|,
name|path
operator|.
name|length
operator|-
literal|1
argument_list|)
expr_stmt|;
name|path
index|[
name|path
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|childName
expr_stmt|;
return|return
operator|new
name|INodesInPath
argument_list|(
name|inodes
argument_list|,
name|path
argument_list|,
name|iip
operator|.
name|isRaw
argument_list|,
name|iip
operator|.
name|isSnapshot
argument_list|,
name|iip
operator|.
name|snapshotId
argument_list|)
return|;
block|}
DECL|field|path
specifier|private
specifier|final
name|byte
index|[]
index|[]
name|path
decl_stmt|;
DECL|field|pathname
specifier|private
specifier|volatile
name|String
name|pathname
decl_stmt|;
comment|/**    * Array with the specified number of INodes resolved for a given path.    */
DECL|field|inodes
specifier|private
specifier|final
name|INode
index|[]
name|inodes
decl_stmt|;
comment|/**    * true if this path corresponds to a snapshot    */
DECL|field|isSnapshot
specifier|private
specifier|final
name|boolean
name|isSnapshot
decl_stmt|;
comment|/**    * true if this is a /.reserved/raw path.  path component resolution strips    * it from the path so need to track it separately.    */
DECL|field|isRaw
specifier|private
specifier|final
name|boolean
name|isRaw
decl_stmt|;
comment|/**    * For snapshot paths, it is the id of the snapshot; or     * {@link Snapshot#CURRENT_STATE_ID} if the snapshot does not exist. For     * non-snapshot paths, it is the id of the latest snapshot found in the path;    * or {@link Snapshot#CURRENT_STATE_ID} if no snapshot is found.    */
DECL|field|snapshotId
specifier|private
specifier|final
name|int
name|snapshotId
decl_stmt|;
DECL|method|INodesInPath (INode[] inodes, byte[][] path, boolean isRaw, boolean isSnapshot,int snapshotId)
specifier|private
name|INodesInPath
parameter_list|(
name|INode
index|[]
name|inodes
parameter_list|,
name|byte
index|[]
index|[]
name|path
parameter_list|,
name|boolean
name|isRaw
parameter_list|,
name|boolean
name|isSnapshot
parameter_list|,
name|int
name|snapshotId
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|inodes
operator|!=
literal|null
operator|&&
name|path
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|inodes
operator|=
name|inodes
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|isRaw
operator|=
name|isRaw
expr_stmt|;
name|this
operator|.
name|isSnapshot
operator|=
name|isSnapshot
expr_stmt|;
name|this
operator|.
name|snapshotId
operator|=
name|snapshotId
expr_stmt|;
block|}
DECL|method|INodesInPath (INode[] inodes, byte[][] path)
specifier|private
name|INodesInPath
parameter_list|(
name|INode
index|[]
name|inodes
parameter_list|,
name|byte
index|[]
index|[]
name|path
parameter_list|)
block|{
name|this
argument_list|(
name|inodes
argument_list|,
name|path
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|CURRENT_STATE_ID
argument_list|)
expr_stmt|;
block|}
comment|/**    * For non-snapshot paths, return the latest snapshot id found in the path.    */
DECL|method|getLatestSnapshotId ()
specifier|public
name|int
name|getLatestSnapshotId
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isSnapshot
argument_list|)
expr_stmt|;
return|return
name|snapshotId
return|;
block|}
comment|/**    * For snapshot paths, return the id of the snapshot specified in the path.    * For non-snapshot paths, return {@link Snapshot#CURRENT_STATE_ID}.    */
DECL|method|getPathSnapshotId ()
specifier|public
name|int
name|getPathSnapshotId
parameter_list|()
block|{
return|return
name|isSnapshot
condition|?
name|snapshotId
else|:
name|CURRENT_STATE_ID
return|;
block|}
comment|/**    * @return the i-th inode if i {@literal>=} 0;    *         otherwise, i {@literal<} 0, return the (length + i)-th inode.    */
DECL|method|getINode (int i)
specifier|public
name|INode
name|getINode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|inodes
index|[
operator|(
name|i
operator|<
literal|0
operator|)
condition|?
name|inodes
operator|.
name|length
operator|+
name|i
else|:
name|i
index|]
return|;
block|}
comment|/** @return the last inode. */
DECL|method|getLastINode ()
specifier|public
name|INode
name|getLastINode
parameter_list|()
block|{
return|return
name|getINode
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
DECL|method|getLastLocalName ()
name|byte
index|[]
name|getLastLocalName
parameter_list|()
block|{
return|return
name|path
index|[
name|path
operator|.
name|length
operator|-
literal|1
index|]
return|;
block|}
DECL|method|getPathComponents ()
specifier|public
name|byte
index|[]
index|[]
name|getPathComponents
parameter_list|()
block|{
return|return
name|path
return|;
block|}
DECL|method|getPathComponent (int i)
specifier|public
name|byte
index|[]
name|getPathComponent
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|path
index|[
name|i
index|]
return|;
block|}
comment|/** @return the full path in string form */
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|pathname
operator|==
literal|null
condition|)
block|{
name|pathname
operator|=
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|pathname
return|;
block|}
DECL|method|getParentPath ()
specifier|public
name|String
name|getParentPath
parameter_list|()
block|{
return|return
name|getPath
argument_list|(
name|path
operator|.
name|length
operator|-
literal|2
argument_list|)
return|;
block|}
DECL|method|getPath (int pos)
specifier|public
name|String
name|getPath
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|path
argument_list|,
literal|0
argument_list|,
name|pos
operator|+
literal|1
argument_list|)
return|;
comment|// it's a length...
block|}
DECL|method|length ()
specifier|public
name|int
name|length
parameter_list|()
block|{
return|return
name|inodes
operator|.
name|length
return|;
block|}
DECL|method|getINodesArray ()
specifier|public
name|INode
index|[]
name|getINodesArray
parameter_list|()
block|{
name|INode
index|[]
name|retArr
init|=
operator|new
name|INode
index|[
name|inodes
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|inodes
argument_list|,
literal|0
argument_list|,
name|retArr
argument_list|,
literal|0
argument_list|,
name|inodes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|retArr
return|;
block|}
comment|/**    * @param length number of ancestral INodes in the returned INodesInPath    *               instance    * @return the INodesInPath instance containing ancestral INodes. Note that    * this method only handles non-snapshot paths.    */
DECL|method|getAncestorINodesInPath (int length)
specifier|private
name|INodesInPath
name|getAncestorINodesInPath
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|length
operator|>=
literal|0
operator|&&
name|length
operator|<
name|inodes
operator|.
name|length
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isDotSnapshotDir
argument_list|()
operator|||
operator|!
name|isSnapshot
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|INode
index|[]
name|anodes
init|=
operator|new
name|INode
index|[
name|length
index|]
decl_stmt|;
specifier|final
name|byte
index|[]
index|[]
name|apath
init|=
operator|new
name|byte
index|[
name|length
index|]
index|[]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|inodes
argument_list|,
literal|0
argument_list|,
name|anodes
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|this
operator|.
name|path
argument_list|,
literal|0
argument_list|,
name|apath
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
return|return
operator|new
name|INodesInPath
argument_list|(
name|anodes
argument_list|,
name|apath
argument_list|,
name|isRaw
argument_list|,
literal|false
argument_list|,
name|snapshotId
argument_list|)
return|;
block|}
comment|/**    * @return an INodesInPath instance containing all the INodes in the parent    *         path. We do a deep copy here.    */
DECL|method|getParentINodesInPath ()
specifier|public
name|INodesInPath
name|getParentINodesInPath
parameter_list|()
block|{
return|return
name|inodes
operator|.
name|length
operator|>
literal|1
condition|?
name|getAncestorINodesInPath
argument_list|(
name|inodes
operator|.
name|length
operator|-
literal|1
argument_list|)
else|:
literal|null
return|;
block|}
comment|/**    * Verify if this {@link INodesInPath} is a descendant of the    * requested {@link INodeDirectory}.    *    * @param inodeDirectory the ancestor directory    * @return true if this INodesInPath is a descendant of inodeDirectory    */
DECL|method|isDescendant (final INodeDirectory inodeDirectory)
specifier|public
name|boolean
name|isDescendant
parameter_list|(
specifier|final
name|INodeDirectory
name|inodeDirectory
parameter_list|)
block|{
specifier|final
name|INodesInPath
name|dirIIP
init|=
name|fromINode
argument_list|(
name|inodeDirectory
argument_list|)
decl_stmt|;
return|return
name|isDescendant
argument_list|(
name|dirIIP
argument_list|)
return|;
block|}
DECL|method|isDescendant (final INodesInPath ancestorDirIIP)
specifier|private
name|boolean
name|isDescendant
parameter_list|(
specifier|final
name|INodesInPath
name|ancestorDirIIP
parameter_list|)
block|{
name|int
name|ancestorDirINodesLength
init|=
name|ancestorDirIIP
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|myParentINodesLength
init|=
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|myParentINodesLength
operator|<
name|ancestorDirINodesLength
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|index
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|index
operator|<
name|ancestorDirINodesLength
condition|)
block|{
if|if
condition|(
name|inodes
index|[
name|index
index|]
operator|!=
name|ancestorDirIIP
operator|.
name|getINode
argument_list|(
name|index
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|index
operator|++
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
comment|/**    * @return a new INodesInPath instance that only contains existing INodes.    * Note that this method only handles non-snapshot paths.    */
DECL|method|getExistingINodes ()
specifier|public
name|INodesInPath
name|getExistingINodes
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isSnapshot
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|inodes
operator|.
name|length
init|;
name|i
operator|>
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
name|inodes
index|[
name|i
operator|-
literal|1
index|]
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|i
operator|==
name|inodes
operator|.
name|length
operator|)
condition|?
name|this
else|:
name|getAncestorINodesInPath
argument_list|(
name|i
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**    * @return isSnapshot true for a snapshot path    */
DECL|method|isSnapshot ()
name|boolean
name|isSnapshot
parameter_list|()
block|{
return|return
name|this
operator|.
name|isSnapshot
return|;
block|}
comment|/**    * @return if .snapshot is the last path component.    */
DECL|method|isDotSnapshotDir ()
name|boolean
name|isDotSnapshotDir
parameter_list|()
block|{
return|return
name|isDotSnapshotDir
argument_list|(
name|getLastLocalName
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * @return if this is a /.reserved/raw path.    */
DECL|method|isRaw ()
specifier|public
name|boolean
name|isRaw
parameter_list|()
block|{
return|return
name|isRaw
return|;
block|}
DECL|method|toString (INode inode)
specifier|private
specifier|static
name|String
name|toString
parameter_list|(
name|INode
name|inode
parameter_list|)
block|{
return|return
name|inode
operator|==
literal|null
condition|?
literal|null
else|:
name|inode
operator|.
name|getLocalName
argument_list|()
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
name|toString
argument_list|(
literal|true
argument_list|)
return|;
block|}
DECL|method|toString (boolean vaildateObject)
specifier|private
name|String
name|toString
parameter_list|(
name|boolean
name|vaildateObject
parameter_list|)
block|{
if|if
condition|(
name|vaildateObject
condition|)
block|{
name|validate
argument_list|()
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|b
init|=
operator|new
name|StringBuilder
argument_list|(
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|": path = "
argument_list|)
operator|.
name|append
argument_list|(
name|getPath
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  inodes = "
argument_list|)
decl_stmt|;
if|if
condition|(
name|inodes
operator|==
literal|null
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"null"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|inodes
operator|.
name|length
operator|==
literal|0
condition|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"[]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|b
operator|.
name|append
argument_list|(
literal|"["
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|inodes
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|inodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|(
name|inodes
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"], length="
argument_list|)
operator|.
name|append
argument_list|(
name|inodes
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|b
operator|.
name|append
argument_list|(
literal|"\n  isSnapshot        = "
argument_list|)
operator|.
name|append
argument_list|(
name|isSnapshot
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  snapshotId        = "
argument_list|)
operator|.
name|append
argument_list|(
name|snapshotId
argument_list|)
expr_stmt|;
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|validate ()
name|void
name|validate
parameter_list|()
block|{
comment|// check parent up to snapshotRootIndex if this is a snapshot path
name|int
name|i
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|inodes
index|[
name|i
index|]
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|i
operator|++
init|;
name|i
operator|<
name|inodes
operator|.
name|length
operator|&&
name|inodes
index|[
name|i
index|]
operator|!=
literal|null
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|INodeDirectory
name|parent_i
init|=
name|inodes
index|[
name|i
index|]
operator|.
name|getParent
argument_list|()
decl_stmt|;
specifier|final
name|INodeDirectory
name|parent_i_1
init|=
name|inodes
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|getParent
argument_list|()
decl_stmt|;
if|if
condition|(
name|parent_i
operator|!=
name|inodes
index|[
name|i
operator|-
literal|1
index|]
operator|&&
operator|(
name|parent_i_1
operator|==
literal|null
operator|||
operator|!
name|parent_i_1
operator|.
name|isSnapshottable
argument_list|()
operator|||
name|parent_i
operator|!=
name|parent_i_1
operator|)
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"inodes["
operator|+
name|i
operator|+
literal|"].getParent() != inodes["
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
literal|"]\n  inodes["
operator|+
name|i
operator|+
literal|"]="
operator|+
name|inodes
index|[
name|i
index|]
operator|.
name|toDetailString
argument_list|()
operator|+
literal|"\n  inodes["
operator|+
operator|(
name|i
operator|-
literal|1
operator|)
operator|+
literal|"]="
operator|+
name|inodes
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|toDetailString
argument_list|()
operator|+
literal|"\n this="
operator|+
name|toString
argument_list|(
literal|false
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
if|if
condition|(
name|i
operator|!=
name|inodes
operator|.
name|length
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"i = "
operator|+
name|i
operator|+
literal|" != "
operator|+
name|inodes
operator|.
name|length
operator|+
literal|", this="
operator|+
name|toString
argument_list|(
literal|false
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

