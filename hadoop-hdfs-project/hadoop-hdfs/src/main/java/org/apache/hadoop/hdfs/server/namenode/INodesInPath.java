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
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|UnresolvedLinkException
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
name|protocol
operator|.
name|UnresolvedPathException
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
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
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
name|HdfsConstants
operator|.
name|DOT_SNAPSHOT_DIR_BYTES
argument_list|,
name|pathComponent
argument_list|)
return|;
block|}
DECL|method|fromINode (INode inode)
specifier|static
name|INodesInPath
name|fromINode
parameter_list|(
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
specifier|final
name|byte
index|[]
index|[]
name|path
init|=
operator|new
name|byte
index|[
name|depth
index|]
index|[]
decl_stmt|;
specifier|final
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
specifier|final
name|INodesInPath
name|iip
init|=
operator|new
name|INodesInPath
argument_list|(
name|path
argument_list|,
name|depth
argument_list|)
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
name|path
index|[
name|index
index|]
operator|=
name|tmp
operator|.
name|getKey
argument_list|()
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
name|iip
operator|.
name|setINodes
argument_list|(
name|inodes
argument_list|)
expr_stmt|;
return|return
name|iip
return|;
block|}
comment|/**    * Given some components, create a path name.    * @param components The path components    * @param start index    * @param end index    * @return concatenated path    */
DECL|method|constructPath (byte[][] components, int start, int end)
specifier|private
specifier|static
name|String
name|constructPath
parameter_list|(
name|byte
index|[]
index|[]
name|components
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
block|{
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|end
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|<
name|end
operator|-
literal|1
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
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
throws|throws
name|UnresolvedLinkException
block|{
return|return
name|resolve
argument_list|(
name|startingDir
argument_list|,
name|components
argument_list|,
name|components
operator|.
name|length
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**    * Retrieve existing INodes from a path. If existing is big enough to store    * all path components (existing and non-existing), then existing INodes    * will be stored starting from the root INode into existing[0]; if    * existing is not big enough to store all path components, then only the    * last existing and non existing INodes will be stored so that    * existing[existing.length-1] refers to the INode of the final component.    *     * An UnresolvedPathException is always thrown when an intermediate path     * component refers to a symbolic link. If the final path component refers     * to a symbolic link then an UnresolvedPathException is only thrown if    * resolveLink is true.      *     *<p>    * Example:<br>    * Given the path /c1/c2/c3 where only /c1/c2 exists, resulting in the    * following path components: ["","c1","c2","c3"],    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"], [?])</code> should fill the    * array with [c2]<br>    *<code>getExistingPathINodes(["","c1","c2","c3"], [?])</code> should fill the    * array with [null]    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"], [?,?])</code> should fill the    * array with [c1,c2]<br>    *<code>getExistingPathINodes(["","c1","c2","c3"], [?,?])</code> should fill    * the array with [c2,null]    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"], [?,?,?,?])</code> should fill    * the array with [rootINode,c1,c2,null],<br>    *<code>getExistingPathINodes(["","c1","c2","c3"], [?,?,?,?])</code> should    * fill the array with [rootINode,c1,c2,null]    *     * @param startingDir the starting directory    * @param components array of path component name    * @param numOfINodes number of INodes to return    * @param resolveLink indicates whether UnresolvedLinkException should    *        be thrown when the path refers to a symbolic link.    * @return the specified number of existing INodes in the path    */
DECL|method|resolve (final INodeDirectory startingDir, final byte[][] components, final int numOfINodes, final boolean resolveLink)
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
parameter_list|,
specifier|final
name|int
name|numOfINodes
parameter_list|,
specifier|final
name|boolean
name|resolveLink
parameter_list|)
throws|throws
name|UnresolvedLinkException
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
specifier|final
name|INodesInPath
name|existing
init|=
operator|new
name|INodesInPath
argument_list|(
name|components
argument_list|,
name|numOfINodes
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|int
name|index
init|=
name|numOfINodes
operator|-
name|components
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|index
operator|>
literal|0
condition|)
block|{
name|index
operator|=
literal|0
expr_stmt|;
block|}
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
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
name|existing
operator|.
name|addNode
argument_list|(
name|curNode
argument_list|)
expr_stmt|;
block|}
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
name|existing
operator|.
name|isSnapshot
argument_list|()
condition|)
block|{
name|existing
operator|.
name|updateLatestSnapshotId
argument_list|(
name|dir
operator|.
name|getDirectoryWithSnapshotFeature
argument_list|()
operator|.
name|getLastSnapshotId
argument_list|()
argument_list|)
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
name|existing
operator|.
name|isSnapshot
argument_list|()
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
name|int
name|latest
init|=
name|existing
operator|.
name|getLatestSnapshotId
argument_list|()
decl_stmt|;
if|if
condition|(
name|latest
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|||
comment|// no snapshot in dst tree of rename
operator|(
name|dstSnapshotId
operator|!=
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|&&
name|dstSnapshotId
operator|>=
name|latest
operator|)
condition|)
block|{
comment|// the above scenario
name|int
name|lastSnapshot
init|=
name|Snapshot
operator|.
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
name|existing
operator|.
name|setSnapshotId
argument_list|(
name|lastSnapshot
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|curNode
operator|.
name|isSymlink
argument_list|()
operator|&&
operator|(
operator|!
name|lastComp
operator|||
name|resolveLink
operator|)
condition|)
block|{
specifier|final
name|String
name|path
init|=
name|constructPath
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
name|components
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|String
name|preceding
init|=
name|constructPath
argument_list|(
name|components
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
decl_stmt|;
specifier|final
name|String
name|remainder
init|=
name|constructPath
argument_list|(
name|components
argument_list|,
name|count
operator|+
literal|1
argument_list|,
name|components
operator|.
name|length
argument_list|)
decl_stmt|;
specifier|final
name|String
name|link
init|=
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|components
index|[
name|count
index|]
argument_list|)
decl_stmt|;
specifier|final
name|String
name|target
init|=
name|curNode
operator|.
name|asSymlink
argument_list|()
operator|.
name|getSymlinkString
argument_list|()
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"UnresolvedPathException "
operator|+
literal|" path: "
operator|+
name|path
operator|+
literal|" preceding: "
operator|+
name|preceding
operator|+
literal|" count: "
operator|+
name|count
operator|+
literal|" link: "
operator|+
name|link
operator|+
literal|" target: "
operator|+
name|target
operator|+
literal|" remainder: "
operator|+
name|remainder
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|UnresolvedPathException
argument_list|(
name|path
argument_list|,
name|preceding
argument_list|,
name|remainder
argument_list|,
name|target
argument_list|)
throw|;
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
name|count
operator|+
literal|1
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
comment|// skip the ".snapshot" in components
name|count
operator|++
expr_stmt|;
name|index
operator|++
expr_stmt|;
name|existing
operator|.
name|isSnapshot
operator|=
literal|true
expr_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
condition|)
block|{
comment|// decrease the capacity by 1 to account for .snapshot
name|existing
operator|.
name|capacity
operator|--
expr_stmt|;
block|}
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
comment|//snapshot not found
name|curNode
operator|=
literal|null
expr_stmt|;
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
name|existing
operator|.
name|setSnapshotId
argument_list|(
name|s
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|index
operator|>=
operator|-
literal|1
condition|)
block|{
name|existing
operator|.
name|snapshotRootIndex
operator|=
name|existing
operator|.
name|numNonNull
expr_stmt|;
block|}
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
name|existing
operator|.
name|getPathSnapshotId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
return|return
name|existing
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
comment|/**    * Array with the specified number of INodes resolved for a given path.    */
DECL|field|inodes
specifier|private
name|INode
index|[]
name|inodes
decl_stmt|;
comment|/**    * Indicate the number of non-null elements in {@link #inodes}    */
DECL|field|numNonNull
specifier|private
name|int
name|numNonNull
decl_stmt|;
comment|/**    * The path for a snapshot file/dir contains the .snapshot thus makes the    * length of the path components larger the number of inodes. We use    * the capacity to control this special case.    */
DECL|field|capacity
specifier|private
name|int
name|capacity
decl_stmt|;
comment|/**    * true if this path corresponds to a snapshot    */
DECL|field|isSnapshot
specifier|private
name|boolean
name|isSnapshot
decl_stmt|;
comment|/**    * index of the {@link Snapshot.Root} node in the inodes array,    * -1 for non-snapshot paths.    */
DECL|field|snapshotRootIndex
specifier|private
name|int
name|snapshotRootIndex
decl_stmt|;
comment|/**    * For snapshot paths, it is the id of the snapshot; or     * {@link Snapshot#CURRENT_STATE_ID} if the snapshot does not exist. For     * non-snapshot paths, it is the id of the latest snapshot found in the path;    * or {@link Snapshot#CURRENT_STATE_ID} if no snapshot is found.    */
DECL|field|snapshotId
specifier|private
name|int
name|snapshotId
init|=
name|Snapshot
operator|.
name|CURRENT_STATE_ID
decl_stmt|;
DECL|method|INodesInPath (byte[][] path, int number)
specifier|private
name|INodesInPath
parameter_list|(
name|byte
index|[]
index|[]
name|path
parameter_list|,
name|int
name|number
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
assert|assert
operator|(
name|number
operator|>=
literal|0
operator|)
assert|;
name|inodes
operator|=
operator|new
name|INode
index|[
name|number
index|]
expr_stmt|;
name|capacity
operator|=
name|number
expr_stmt|;
name|numNonNull
operator|=
literal|0
expr_stmt|;
name|isSnapshot
operator|=
literal|false
expr_stmt|;
name|snapshotRootIndex
operator|=
operator|-
literal|1
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
name|Snapshot
operator|.
name|CURRENT_STATE_ID
return|;
block|}
DECL|method|setSnapshotId (int sid)
specifier|private
name|void
name|setSnapshotId
parameter_list|(
name|int
name|sid
parameter_list|)
block|{
name|snapshotId
operator|=
name|sid
expr_stmt|;
block|}
DECL|method|updateLatestSnapshotId (int sid)
specifier|private
name|void
name|updateLatestSnapshotId
parameter_list|(
name|int
name|sid
parameter_list|)
block|{
if|if
condition|(
name|snapshotId
operator|==
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|||
operator|(
name|sid
operator|!=
name|Snapshot
operator|.
name|CURRENT_STATE_ID
operator|&&
name|Snapshot
operator|.
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
condition|)
block|{
name|snapshotId
operator|=
name|sid
expr_stmt|;
block|}
block|}
comment|/**    * @return a new array of inodes excluding the null elements introduced by    * snapshot path elements. E.g., after resolving path "/dir/.snapshot",    * {@link #inodes} is {/, dir, null}, while the returned array only contains    * inodes of "/" and "dir". Note the length of the returned array is always    * equal to {@link #capacity}.    */
DECL|method|getINodes ()
name|INode
index|[]
name|getINodes
parameter_list|()
block|{
if|if
condition|(
name|capacity
operator|==
name|inodes
operator|.
name|length
condition|)
block|{
return|return
name|inodes
return|;
block|}
name|INode
index|[]
name|newNodes
init|=
operator|new
name|INode
index|[
name|capacity
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
name|newNodes
argument_list|,
literal|0
argument_list|,
name|capacity
argument_list|)
expr_stmt|;
return|return
name|newNodes
return|;
block|}
comment|/**    * @return the i-th inode if i>= 0;    *         otherwise, i< 0, return the (length + i)-th inode.    */
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
name|i
operator|>=
literal|0
condition|?
name|i
else|:
name|inodes
operator|.
name|length
operator|+
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
name|inodes
index|[
name|inodes
operator|.
name|length
operator|-
literal|1
index|]
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
comment|/** @return the full path in string form */
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|path
argument_list|)
return|;
block|}
comment|/**    * @return index of the {@link Snapshot.Root} node in the inodes array,    * -1 for non-snapshot paths.    */
DECL|method|getSnapshotRootIndex ()
name|int
name|getSnapshotRootIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|snapshotRootIndex
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
comment|/**    * Add an INode at the end of the array    */
DECL|method|addNode (INode node)
specifier|private
name|void
name|addNode
parameter_list|(
name|INode
name|node
parameter_list|)
block|{
name|inodes
index|[
name|numNonNull
operator|++
index|]
operator|=
name|node
expr_stmt|;
block|}
DECL|method|setINodes (INode inodes[])
specifier|private
name|void
name|setINodes
parameter_list|(
name|INode
name|inodes
index|[]
parameter_list|)
block|{
name|this
operator|.
name|inodes
operator|=
name|inodes
expr_stmt|;
name|this
operator|.
name|numNonNull
operator|=
name|this
operator|.
name|inodes
operator|.
name|length
expr_stmt|;
block|}
DECL|method|setINode (int i, INode inode)
name|void
name|setINode
parameter_list|(
name|int
name|i
parameter_list|,
name|INode
name|inode
parameter_list|)
block|{
name|inodes
index|[
name|i
operator|>=
literal|0
condition|?
name|i
else|:
name|inodes
operator|.
name|length
operator|+
name|i
index|]
operator|=
name|inode
expr_stmt|;
block|}
DECL|method|setLastINode (INode last)
name|void
name|setLastINode
parameter_list|(
name|INode
name|last
parameter_list|)
block|{
name|inodes
index|[
name|inodes
operator|.
name|length
operator|-
literal|1
index|]
operator|=
name|last
expr_stmt|;
block|}
comment|/**    * @return The number of non-null elements    */
DECL|method|getNumNonNull ()
name|int
name|getNumNonNull
parameter_list|()
block|{
return|return
name|numNonNull
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
name|DFSUtil
operator|.
name|byteArray2PathString
argument_list|(
name|path
argument_list|)
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
literal|"\n  numNonNull = "
argument_list|)
operator|.
name|append
argument_list|(
name|numNonNull
argument_list|)
operator|.
name|append
argument_list|(
literal|"\n  capacity   = "
argument_list|)
operator|.
name|append
argument_list|(
name|capacity
argument_list|)
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
literal|"\n  snapshotRootIndex = "
argument_list|)
operator|.
name|append
argument_list|(
name|snapshotRootIndex
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
comment|// check parent up to snapshotRootIndex or numNonNull
specifier|final
name|int
name|n
init|=
name|snapshotRootIndex
operator|>=
literal|0
condition|?
name|snapshotRootIndex
operator|+
literal|1
else|:
name|numNonNull
decl_stmt|;
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
name|n
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
name|n
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
name|n
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

