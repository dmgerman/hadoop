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
name|io
operator|.
name|FileNotFoundException
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
name|fs
operator|.
name|permission
operator|.
name|PermissionStatus
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
name|Block
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

begin_comment
comment|/**  * Directory INode class.  */
end_comment

begin_class
DECL|class|INodeDirectory
class|class
name|INodeDirectory
extends|extends
name|INode
block|{
comment|/** Cast INode to INodeDirectory. */
DECL|method|valueOf (INode inode, String path )
specifier|public
specifier|static
name|INodeDirectory
name|valueOf
parameter_list|(
name|INode
name|inode
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Directory does not exist: "
operator|+
name|path
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Path is not a directory: "
operator|+
name|path
argument_list|)
throw|;
block|}
return|return
operator|(
name|INodeDirectory
operator|)
name|inode
return|;
block|}
DECL|field|DEFAULT_FILES_PER_DIRECTORY
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_FILES_PER_DIRECTORY
init|=
literal|5
decl_stmt|;
DECL|field|ROOT_NAME
specifier|final
specifier|static
name|String
name|ROOT_NAME
init|=
literal|""
decl_stmt|;
DECL|field|children
specifier|private
name|List
argument_list|<
name|INode
argument_list|>
name|children
decl_stmt|;
DECL|method|INodeDirectory (String name, PermissionStatus permissions)
name|INodeDirectory
parameter_list|(
name|String
name|name
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|,
name|permissions
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
literal|null
expr_stmt|;
block|}
DECL|method|INodeDirectory (PermissionStatus permissions, long mTime)
specifier|public
name|INodeDirectory
parameter_list|(
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|mTime
parameter_list|)
block|{
name|super
argument_list|(
name|permissions
argument_list|,
name|mTime
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
literal|null
expr_stmt|;
block|}
comment|/** constructor */
DECL|method|INodeDirectory (byte[] localName, PermissionStatus permissions, long mTime)
name|INodeDirectory
parameter_list|(
name|byte
index|[]
name|localName
parameter_list|,
name|PermissionStatus
name|permissions
parameter_list|,
name|long
name|mTime
parameter_list|)
block|{
name|this
argument_list|(
name|permissions
argument_list|,
name|mTime
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|localName
expr_stmt|;
block|}
comment|/** copy constructor    *     * @param other    */
DECL|method|INodeDirectory (INodeDirectory other)
name|INodeDirectory
parameter_list|(
name|INodeDirectory
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|children
operator|=
name|other
operator|.
name|getChildren
argument_list|()
expr_stmt|;
block|}
comment|/** @return true unconditionally. */
annotation|@
name|Override
DECL|method|isDirectory ()
specifier|public
specifier|final
name|boolean
name|isDirectory
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
DECL|method|removeChild (INode node)
name|INode
name|removeChild
parameter_list|(
name|INode
name|node
parameter_list|)
block|{
assert|assert
name|children
operator|!=
literal|null
assert|;
name|int
name|low
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|children
argument_list|,
name|node
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|low
operator|>=
literal|0
condition|)
block|{
return|return
name|children
operator|.
name|remove
argument_list|(
name|low
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/** Replace a child that has the same name as newChild by newChild.    *     * @param newChild Child node to be added    */
DECL|method|replaceChild (INode newChild)
name|void
name|replaceChild
parameter_list|(
name|INode
name|newChild
parameter_list|)
block|{
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"The directory is empty"
argument_list|)
throw|;
block|}
name|int
name|low
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|children
argument_list|,
name|newChild
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|low
operator|>=
literal|0
condition|)
block|{
comment|// an old child exists so replace by the newChild
name|children
operator|.
name|set
argument_list|(
name|low
argument_list|,
name|newChild
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No child exists to be replaced"
argument_list|)
throw|;
block|}
block|}
DECL|method|getChild (String name)
name|INode
name|getChild
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|getChildINode
argument_list|(
name|DFSUtil
operator|.
name|string2Bytes
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getChildINode (byte[] name)
specifier|private
name|INode
name|getChildINode
parameter_list|(
name|byte
index|[]
name|name
parameter_list|)
block|{
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|low
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|children
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|low
operator|>=
literal|0
condition|)
block|{
return|return
name|children
operator|.
name|get
argument_list|(
name|low
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * @return the INode of the last component in components, or null if the last    * component does not exist.    */
DECL|method|getNode (byte[][] components, boolean resolveLink )
specifier|private
name|INode
name|getNode
parameter_list|(
name|byte
index|[]
index|[]
name|components
parameter_list|,
name|boolean
name|resolveLink
parameter_list|)
throws|throws
name|UnresolvedLinkException
block|{
name|INodesInPath
name|inodesInPath
init|=
name|getExistingPathINodes
argument_list|(
name|components
argument_list|,
literal|1
argument_list|,
name|resolveLink
argument_list|)
decl_stmt|;
return|return
name|inodesInPath
operator|.
name|inodes
index|[
literal|0
index|]
return|;
block|}
comment|/**    * This is the external interface    */
DECL|method|getNode (String path, boolean resolveLink)
name|INode
name|getNode
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|resolveLink
parameter_list|)
throws|throws
name|UnresolvedLinkException
block|{
return|return
name|getNode
argument_list|(
name|getPathComponents
argument_list|(
name|path
argument_list|)
argument_list|,
name|resolveLink
argument_list|)
return|;
block|}
comment|/**    * Retrieve existing INodes from a path. If existing is big enough to store    * all path components (existing and non-existing), then existing INodes    * will be stored starting from the root INode into existing[0]; if    * existing is not big enough to store all path components, then only the    * last existing and non existing INodes will be stored so that    * existing[existing.length-1] refers to the INode of the final component.    *     * An UnresolvedPathException is always thrown when an intermediate path     * component refers to a symbolic link. If the final path component refers     * to a symbolic link then an UnresolvedPathException is only thrown if    * resolveLink is true.      *     *<p>    * Example:<br>    * Given the path /c1/c2/c3 where only /c1/c2 exists, resulting in the    * following path components: ["","c1","c2","c3"],    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"], [?])</code> should fill the    * array with [c2]<br>    *<code>getExistingPathINodes(["","c1","c2","c3"], [?])</code> should fill the    * array with [null]    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"], [?,?])</code> should fill the    * array with [c1,c2]<br>    *<code>getExistingPathINodes(["","c1","c2","c3"], [?,?])</code> should fill    * the array with [c2,null]    *     *<p>    *<code>getExistingPathINodes(["","c1","c2"], [?,?,?,?])</code> should fill    * the array with [rootINode,c1,c2,null],<br>    *<code>getExistingPathINodes(["","c1","c2","c3"], [?,?,?,?])</code> should    * fill the array with [rootINode,c1,c2,null]    *     * @param components array of path component name    * @param numOfINodes number of INodes to return    * @param resolveLink indicates whether UnresolvedLinkException should    *        be thrown when the path refers to a symbolic link.    * @return the specified number of existing INodes in the path    */
DECL|method|getExistingPathINodes (byte[][] components, int numOfINodes, boolean resolveLink)
name|INodesInPath
name|getExistingPathINodes
parameter_list|(
name|byte
index|[]
index|[]
name|components
parameter_list|,
name|int
name|numOfINodes
parameter_list|,
name|boolean
name|resolveLink
parameter_list|)
throws|throws
name|UnresolvedLinkException
block|{
assert|assert
name|this
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
operator|:
literal|"Incorrect name "
operator|+
name|getLocalName
argument_list|()
operator|+
literal|" expected "
operator|+
operator|(
name|components
index|[
literal|0
index|]
operator|==
literal|null
condition|?
literal|null
else|:
name|DFSUtil
operator|.
name|bytes2String
argument_list|(
name|components
index|[
literal|0
index|]
argument_list|)
operator|)
assert|;
name|INodesInPath
name|existing
init|=
operator|new
name|INodesInPath
argument_list|(
name|numOfINodes
argument_list|)
decl_stmt|;
name|INode
name|curNode
init|=
name|this
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
name|inodes
index|[
name|index
index|]
operator|=
name|curNode
expr_stmt|;
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
operator|(
name|lastComp
operator|&&
name|resolveLink
operator|)
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
operator|(
operator|(
name|INodeSymlink
operator|)
name|curNode
operator|)
operator|.
name|getLinkValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|NameNode
operator|.
name|stateChangeLog
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NameNode
operator|.
name|stateChangeLog
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
name|curNode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
break|break;
block|}
name|INodeDirectory
name|parentDir
init|=
operator|(
name|INodeDirectory
operator|)
name|curNode
decl_stmt|;
name|curNode
operator|=
name|parentDir
operator|.
name|getChildINode
argument_list|(
name|components
index|[
name|count
operator|+
literal|1
index|]
argument_list|)
expr_stmt|;
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
comment|/**    * Retrieve the existing INodes along the given path. The first INode    * always exist and is this INode.    *     * @param path the path to explore    * @param resolveLink indicates whether UnresolvedLinkException should     *        be thrown when the path refers to a symbolic link.    * @return INodes array containing the existing INodes in the order they    *         appear when following the path from the root INode to the    *         deepest INodes. The array size will be the number of expected    *         components in the path, and non existing components will be    *         filled with null    *             * @see #getExistingPathINodes(byte[][], int, boolean)    */
DECL|method|getExistingPathINodes (String path, boolean resolveLink)
name|INodesInPath
name|getExistingPathINodes
parameter_list|(
name|String
name|path
parameter_list|,
name|boolean
name|resolveLink
parameter_list|)
throws|throws
name|UnresolvedLinkException
block|{
name|byte
index|[]
index|[]
name|components
init|=
name|getPathComponents
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|getExistingPathINodes
argument_list|(
name|components
argument_list|,
name|components
operator|.
name|length
argument_list|,
name|resolveLink
argument_list|)
return|;
block|}
comment|/**    * Given a child's name, return the index of the next child    *    * @param name a child's name    * @return the index of the next child    */
DECL|method|nextChild (byte[] name)
name|int
name|nextChild
parameter_list|(
name|byte
index|[]
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|length
operator|==
literal|0
condition|)
block|{
comment|// empty name
return|return
literal|0
return|;
block|}
name|int
name|nextPos
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|children
argument_list|,
name|name
argument_list|)
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|nextPos
operator|>=
literal|0
condition|)
block|{
return|return
name|nextPos
return|;
block|}
return|return
operator|-
name|nextPos
return|;
block|}
comment|/**    * Add a child inode to the directory.    *     * @param node INode to insert    * @param setModTime set modification time for the parent node    *                   not needed when replaying the addition and     *                   the parent already has the proper mod time    * @return  null if the child with this name already exists;     *          node, otherwise    */
DECL|method|addChild (final T node, boolean setModTime)
parameter_list|<
name|T
extends|extends
name|INode
parameter_list|>
name|T
name|addChild
parameter_list|(
specifier|final
name|T
name|node
parameter_list|,
name|boolean
name|setModTime
parameter_list|)
block|{
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
name|children
operator|=
operator|new
name|ArrayList
argument_list|<
name|INode
argument_list|>
argument_list|(
name|DEFAULT_FILES_PER_DIRECTORY
argument_list|)
expr_stmt|;
block|}
name|int
name|low
init|=
name|Collections
operator|.
name|binarySearch
argument_list|(
name|children
argument_list|,
name|node
operator|.
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|low
operator|>=
literal|0
condition|)
return|return
literal|null
return|;
name|node
operator|.
name|parent
operator|=
name|this
expr_stmt|;
name|children
operator|.
name|add
argument_list|(
operator|-
name|low
operator|-
literal|1
argument_list|,
name|node
argument_list|)
expr_stmt|;
comment|// update modification time of the parent directory
if|if
condition|(
name|setModTime
condition|)
name|setModificationTime
argument_list|(
name|node
operator|.
name|getModificationTime
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getGroupName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|node
operator|.
name|setGroup
argument_list|(
name|getGroupName
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|node
return|;
block|}
comment|/**    * Add new INode to the file tree.    * Find the parent and insert     *     * @param path file path    * @param newNode INode to be added    * @return null if the node already exists; inserted INode, otherwise    * @throws FileNotFoundException if parent does not exist or     * @throws UnresolvedLinkException if any path component is a symbolic link    * is not a directory.    */
DECL|method|addNode (String path, T newNode )
parameter_list|<
name|T
extends|extends
name|INode
parameter_list|>
name|T
name|addNode
parameter_list|(
name|String
name|path
parameter_list|,
name|T
name|newNode
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
block|{
name|byte
index|[]
index|[]
name|pathComponents
init|=
name|getPathComponents
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|addToParent
argument_list|(
name|pathComponents
argument_list|,
name|newNode
argument_list|,
literal|true
argument_list|)
operator|==
literal|null
condition|?
literal|null
else|:
name|newNode
return|;
block|}
comment|/**    * Add new inode to the parent if specified.    * Optimized version of addNode() if parent is not null.    *     * @return  parent INode if new inode is inserted    *          or null if it already exists.    * @throws  FileNotFoundException if parent does not exist or     *          is not a directory.    */
DECL|method|addToParent ( byte[] localname, INode newNode, INodeDirectory parent, boolean propagateModTime )
name|INodeDirectory
name|addToParent
parameter_list|(
name|byte
index|[]
name|localname
parameter_list|,
name|INode
name|newNode
parameter_list|,
name|INodeDirectory
name|parent
parameter_list|,
name|boolean
name|propagateModTime
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
comment|// insert into the parent children list
name|newNode
operator|.
name|name
operator|=
name|localname
expr_stmt|;
if|if
condition|(
name|parent
operator|.
name|addChild
argument_list|(
name|newNode
argument_list|,
name|propagateModTime
argument_list|)
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|parent
return|;
block|}
DECL|method|getParent (byte[][] pathComponents )
name|INodeDirectory
name|getParent
parameter_list|(
name|byte
index|[]
index|[]
name|pathComponents
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
block|{
if|if
condition|(
name|pathComponents
operator|.
name|length
operator|<
literal|2
condition|)
comment|// add root
return|return
literal|null
return|;
comment|// Gets the parent INode
name|INodesInPath
name|inodes
init|=
name|getExistingPathINodes
argument_list|(
name|pathComponents
argument_list|,
literal|2
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|INode
name|inode
init|=
name|inodes
operator|.
name|inodes
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|inode
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Parent path does not exist: "
operator|+
name|DFSUtil
operator|.
name|byteArray2String
argument_list|(
name|pathComponents
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|inode
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"Parent path is not a directory: "
operator|+
name|DFSUtil
operator|.
name|byteArray2String
argument_list|(
name|pathComponents
argument_list|)
argument_list|)
throw|;
block|}
return|return
operator|(
name|INodeDirectory
operator|)
name|inode
return|;
block|}
comment|/**    * Add new inode     * Optimized version of addNode()    *     * @return  parent INode if new inode is inserted    *          or null if it already exists.    * @throws  FileNotFoundException if parent does not exist or     *          is not a directory.    */
DECL|method|addToParent (byte[][] pathComponents, INode newNode, boolean propagateModTime)
name|INodeDirectory
name|addToParent
parameter_list|(
name|byte
index|[]
index|[]
name|pathComponents
parameter_list|,
name|INode
name|newNode
parameter_list|,
name|boolean
name|propagateModTime
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|UnresolvedLinkException
block|{
if|if
condition|(
name|pathComponents
operator|.
name|length
operator|<
literal|2
condition|)
block|{
comment|// add root
return|return
literal|null
return|;
block|}
name|newNode
operator|.
name|name
operator|=
name|pathComponents
index|[
name|pathComponents
operator|.
name|length
operator|-
literal|1
index|]
expr_stmt|;
comment|// insert into the parent children list
name|INodeDirectory
name|parent
init|=
name|getParent
argument_list|(
name|pathComponents
argument_list|)
decl_stmt|;
return|return
name|parent
operator|.
name|addChild
argument_list|(
name|newNode
argument_list|,
name|propagateModTime
argument_list|)
operator|==
literal|null
condition|?
literal|null
else|:
name|parent
return|;
block|}
annotation|@
name|Override
DECL|method|spaceConsumedInTree (DirCounts counts)
name|DirCounts
name|spaceConsumedInTree
parameter_list|(
name|DirCounts
name|counts
parameter_list|)
block|{
name|counts
operator|.
name|nsCount
operator|+=
literal|1
expr_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|INode
name|child
range|:
name|children
control|)
block|{
name|child
operator|.
name|spaceConsumedInTree
argument_list|(
name|counts
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|counts
return|;
block|}
annotation|@
name|Override
DECL|method|computeContentSummary (long[] summary)
name|long
index|[]
name|computeContentSummary
parameter_list|(
name|long
index|[]
name|summary
parameter_list|)
block|{
comment|// Walk through the children of this node, using a new summary array
comment|// for the (sub)tree rooted at this node
assert|assert
literal|4
operator|==
name|summary
operator|.
name|length
assert|;
name|long
index|[]
name|subtreeSummary
init|=
operator|new
name|long
index|[]
block|{
literal|0
block|,
literal|0
block|,
literal|0
block|,
literal|0
block|}
decl_stmt|;
if|if
condition|(
name|children
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|INode
name|child
range|:
name|children
control|)
block|{
name|child
operator|.
name|computeContentSummary
argument_list|(
name|subtreeSummary
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|this
operator|instanceof
name|INodeDirectoryWithQuota
condition|)
block|{
comment|// Warn if the cached and computed diskspace values differ
name|INodeDirectoryWithQuota
name|node
init|=
operator|(
name|INodeDirectoryWithQuota
operator|)
name|this
decl_stmt|;
name|long
name|space
init|=
name|node
operator|.
name|diskspaceConsumed
argument_list|()
decl_stmt|;
assert|assert
operator|-
literal|1
operator|==
name|node
operator|.
name|getDsQuota
argument_list|()
operator|||
name|space
operator|==
name|subtreeSummary
index|[
literal|3
index|]
assert|;
if|if
condition|(
operator|-
literal|1
operator|!=
name|node
operator|.
name|getDsQuota
argument_list|()
operator|&&
name|space
operator|!=
name|subtreeSummary
index|[
literal|3
index|]
condition|)
block|{
name|NameNode
operator|.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Inconsistent diskspace for directory "
operator|+
name|getLocalName
argument_list|()
operator|+
literal|". Cached: "
operator|+
name|space
operator|+
literal|" Computed: "
operator|+
name|subtreeSummary
index|[
literal|3
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// update the passed summary array with the values for this node's subtree
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|summary
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|summary
index|[
name|i
index|]
operator|+=
name|subtreeSummary
index|[
name|i
index|]
expr_stmt|;
block|}
name|summary
index|[
literal|2
index|]
operator|++
expr_stmt|;
return|return
name|summary
return|;
block|}
comment|/**    * @return an empty list if the children list is null;    *         otherwise, return the children list.    *         The returned list should not be modified.    */
DECL|method|getChildrenList ()
specifier|public
name|List
argument_list|<
name|INode
argument_list|>
name|getChildrenList
parameter_list|()
block|{
return|return
name|children
operator|==
literal|null
condition|?
name|EMPTY_LIST
else|:
name|children
return|;
block|}
comment|/** @return the children list which is possibly null. */
DECL|method|getChildren ()
specifier|public
name|List
argument_list|<
name|INode
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|children
return|;
block|}
annotation|@
name|Override
DECL|method|collectSubtreeBlocksAndClear (List<Block> v)
name|int
name|collectSubtreeBlocksAndClear
parameter_list|(
name|List
argument_list|<
name|Block
argument_list|>
name|v
parameter_list|)
block|{
name|int
name|total
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|children
operator|==
literal|null
condition|)
block|{
return|return
name|total
return|;
block|}
for|for
control|(
name|INode
name|child
range|:
name|children
control|)
block|{
name|total
operator|+=
name|child
operator|.
name|collectSubtreeBlocksAndClear
argument_list|(
name|v
argument_list|)
expr_stmt|;
block|}
name|parent
operator|=
literal|null
expr_stmt|;
name|children
operator|=
literal|null
expr_stmt|;
return|return
name|total
return|;
block|}
comment|/**    * Used by    * {@link INodeDirectory#getExistingPathINodes(byte[][], int, boolean)}.    * Containing INodes information resolved from a given path.    */
DECL|class|INodesInPath
specifier|static
class|class
name|INodesInPath
block|{
DECL|field|inodes
specifier|private
name|INode
index|[]
name|inodes
decl_stmt|;
DECL|method|INodesInPath (int number)
specifier|public
name|INodesInPath
parameter_list|(
name|int
name|number
parameter_list|)
block|{
assert|assert
operator|(
name|number
operator|>=
literal|0
operator|)
assert|;
name|this
operator|.
name|inodes
operator|=
operator|new
name|INode
index|[
name|number
index|]
expr_stmt|;
block|}
DECL|method|getINodes ()
name|INode
index|[]
name|getINodes
parameter_list|()
block|{
return|return
name|inodes
return|;
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
index|]
operator|=
name|inode
expr_stmt|;
block|}
block|}
comment|/*    * The following code is to dump the tree recursively for testing.    *     *      \- foo   (INodeDirectory@33dd2717)    *        \- sub1   (INodeDirectory@442172)    *          +- file1   (INodeFile@78392d4)    *          +- file2   (INodeFile@78392d5)    *          +- sub11   (INodeDirectory@8400cff)    *            \- file3   (INodeFile@78392d6)    *          \- z_file4   (INodeFile@45848712)    */
DECL|field|DUMPTREE_EXCEPT_LAST_ITEM
specifier|static
specifier|final
name|String
name|DUMPTREE_EXCEPT_LAST_ITEM
init|=
literal|"+-"
decl_stmt|;
DECL|field|DUMPTREE_LAST_ITEM
specifier|static
specifier|final
name|String
name|DUMPTREE_LAST_ITEM
init|=
literal|"\\-"
decl_stmt|;
annotation|@
name|VisibleForTesting
annotation|@
name|Override
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix)
specifier|public
name|void
name|dumpTreeRecursively
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|)
block|{
name|super
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|prefix
operator|.
name|length
argument_list|()
operator|>=
literal|2
condition|)
block|{
name|prefix
operator|.
name|setLength
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|prefix
operator|.
name|append
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|,
name|children
argument_list|)
expr_stmt|;
block|}
comment|/**    * Dump the given subtrees.    * @param prefix The prefix string that each line should print.    * @param subs The subtrees.    */
annotation|@
name|VisibleForTesting
DECL|method|dumpTreeRecursively (PrintWriter out, StringBuilder prefix, List<? extends INode> subs)
specifier|protected
specifier|static
name|void
name|dumpTreeRecursively
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|StringBuilder
name|prefix
parameter_list|,
name|List
argument_list|<
name|?
extends|extends
name|INode
argument_list|>
name|subs
parameter_list|)
block|{
name|prefix
operator|.
name|append
argument_list|(
name|DUMPTREE_EXCEPT_LAST_ITEM
argument_list|)
expr_stmt|;
if|if
condition|(
name|subs
operator|!=
literal|null
operator|&&
name|subs
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|subs
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
name|prefix
operator|.
name|setLength
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|prefix
operator|.
name|append
argument_list|(
name|DUMPTREE_EXCEPT_LAST_ITEM
argument_list|)
expr_stmt|;
block|}
name|prefix
operator|.
name|setLength
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
name|prefix
operator|.
name|append
argument_list|(
name|DUMPTREE_LAST_ITEM
argument_list|)
expr_stmt|;
name|subs
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|dumpTreeRecursively
argument_list|(
name|out
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
name|prefix
operator|.
name|setLength
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|-
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

