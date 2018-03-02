begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|DirectoryWithSnapshotFeature
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
name|DirectoryWithSnapshotFeature
operator|.
name|ChildrenDiff
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
name|ArrayList
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
name|Arrays
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
name|Random
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Objects
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
name|ThreadLocalRandom
import|;
end_import

begin_comment
comment|/**  * SkipList is an implementation of a data structure for storing a sorted list  * of Directory Diff elements, using a hierarchy of linked lists that connect  * increasingly sparse subsequences(defined by skip interval here) of the diffs.  * The elements contained in the tree must be mutually comparable.  *<p>  * Consider  a case where we have 10 snapshots for a directory starting from s0  * to s9 each associated with certain change records in terms of inodes deleted  * and created after a particular snapshot and before the next snapshot. The  * sequence will look like this:  *<p>  * s0->s1->s2->s3->s4->s5->s6->s7->s8->s9.  *<p>  * Assuming a skip interval of 3, which means a new diff will be added at a  * level higher than the current level after we have  ore than 3 snapshots.  * Next level promotion happens after 9 snapshots and so on.  *<p>  * level 2:   s08------------------------------->s9  * level 1:   S02------->s35-------->s68-------->s9  * level 0:  s0->s1->s2->s3->s4->s5->s6->s7->s8->s9  *<p>  * s02 will be created by combining diffs for s0, s1, s2 once s3 gets created.  * Similarly, s08 will be created by combining s02, s35 and s68 once s9 gets  * created.So, for constructing the children list fot s0, we have  to combine  * s08, s9 and reverse apply to the live fs.  *<p>  * Similarly, for constructing the children list for s2, s2, s35, s68 and s9  * need to get combined(or added) and reverse applied to current fs.  *<p>  * This approach will improve the snapshot deletion and snapshot diff  * calculation.  *<p>  * Once a snapshot gets deleted, the list needs to be balanced.  */
end_comment

begin_class
DECL|class|DirectoryDiffList
specifier|public
class|class
name|DirectoryDiffList
implements|implements
name|DiffList
argument_list|<
name|DirectoryDiff
argument_list|>
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
name|DirectoryDiffList
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|class|SkipDiff
specifier|private
specifier|static
class|class
name|SkipDiff
block|{
comment|/**      * The references to the subsequent nodes.      */
DECL|field|skipTo
specifier|private
name|SkipListNode
name|skipTo
decl_stmt|;
comment|/**      * combined diff over a skip Interval.      */
DECL|field|diff
specifier|private
name|ChildrenDiff
name|diff
decl_stmt|;
DECL|method|SkipDiff (ChildrenDiff diff)
name|SkipDiff
parameter_list|(
name|ChildrenDiff
name|diff
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
block|}
DECL|method|getDiff ()
specifier|public
name|ChildrenDiff
name|getDiff
parameter_list|()
block|{
return|return
name|diff
return|;
block|}
DECL|method|getSkipTo ()
specifier|public
name|SkipListNode
name|getSkipTo
parameter_list|()
block|{
return|return
name|skipTo
return|;
block|}
DECL|method|setSkipTo (SkipListNode node)
specifier|public
name|void
name|setSkipTo
parameter_list|(
name|SkipListNode
name|node
parameter_list|)
block|{
name|skipTo
operator|=
name|node
expr_stmt|;
block|}
DECL|method|setDiff (ChildrenDiff diff)
specifier|public
name|void
name|setDiff
parameter_list|(
name|ChildrenDiff
name|diff
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
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
literal|"->"
operator|+
name|skipTo
operator|+
operator|(
name|diff
operator|==
literal|null
condition|?
literal|" (diff==null)"
else|:
literal|""
operator|)
return|;
block|}
block|}
comment|/**    * SkipListNode is an implementation of a DirectoryDiff List node,    * which stores a Directory Diff and references to subsequent nodes.    */
DECL|class|SkipListNode
specifier|private
specifier|final
specifier|static
class|class
name|SkipListNode
implements|implements
name|Comparable
argument_list|<
name|Integer
argument_list|>
block|{
comment|/**      * The data element stored in this node.      */
DECL|field|diff
specifier|private
name|DirectoryDiff
name|diff
decl_stmt|;
comment|/**      * List containing combined children diffs over a skip interval.      */
DECL|field|skipDiffList
specifier|private
name|List
argument_list|<
name|SkipDiff
argument_list|>
name|skipDiffList
decl_stmt|;
comment|/**      * Constructs a new instance of SkipListNode with the specified data element      * and level.      *      * @param diff The element to be stored in the node.      */
DECL|method|SkipListNode (DirectoryDiff diff, int level)
name|SkipListNode
parameter_list|(
name|DirectoryDiff
name|diff
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|diff
expr_stmt|;
name|skipDiffList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|level
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the level of this SkipListNode.      */
DECL|method|level ()
specifier|public
name|int
name|level
parameter_list|()
block|{
return|return
name|skipDiffList
operator|.
name|size
argument_list|()
operator|-
literal|1
return|;
block|}
DECL|method|trim ()
name|void
name|trim
parameter_list|()
block|{
for|for
control|(
name|int
name|level
init|=
name|level
argument_list|()
init|;
name|level
operator|>
literal|0
operator|&&
name|getSkipNode
argument_list|(
name|level
argument_list|)
operator|==
literal|null
condition|;
name|level
operator|--
control|)
block|{
name|skipDiffList
operator|.
name|remove
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getDiff ()
specifier|public
name|DirectoryDiff
name|getDiff
parameter_list|()
block|{
return|return
name|diff
return|;
block|}
comment|/**      * Compare diffs with snapshot ID.      */
annotation|@
name|Override
DECL|method|compareTo (Integer that)
specifier|public
name|int
name|compareTo
parameter_list|(
name|Integer
name|that
parameter_list|)
block|{
return|return
name|diff
operator|.
name|compareTo
argument_list|(
name|that
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object o)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SkipListNode
name|that
init|=
operator|(
name|SkipListNode
operator|)
name|o
decl_stmt|;
return|return
name|Objects
operator|.
name|equals
argument_list|(
name|diff
argument_list|,
name|that
operator|.
name|diff
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hash
argument_list|(
name|diff
argument_list|)
return|;
block|}
DECL|method|setSkipDiff (ChildrenDiff cDiff, int level)
specifier|public
name|void
name|setSkipDiff
parameter_list|(
name|ChildrenDiff
name|cDiff
parameter_list|,
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|level
operator|<
name|skipDiffList
operator|.
name|size
argument_list|()
condition|)
block|{
name|skipDiffList
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|setDiff
argument_list|(
name|cDiff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|skipDiffList
operator|.
name|add
argument_list|(
operator|new
name|SkipDiff
argument_list|(
name|cDiff
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|setSkipTo (SkipListNode node, int level)
specifier|public
name|void
name|setSkipTo
parameter_list|(
name|SkipListNode
name|node
parameter_list|,
name|int
name|level
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
name|skipDiffList
operator|.
name|size
argument_list|()
init|;
name|i
operator|<=
name|level
condition|;
name|i
operator|++
control|)
block|{
name|skipDiffList
operator|.
name|add
argument_list|(
operator|new
name|SkipDiff
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|skipDiffList
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|setSkipTo
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
DECL|method|getChildrenDiff (int level)
specifier|public
name|ChildrenDiff
name|getChildrenDiff
parameter_list|(
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|level
operator|==
literal|0
condition|)
block|{
return|return
name|diff
operator|.
name|getChildrenDiff
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|skipDiffList
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|getDiff
argument_list|()
return|;
block|}
block|}
DECL|method|getSkipNode (int level)
name|SkipListNode
name|getSkipNode
parameter_list|(
name|int
name|level
parameter_list|)
block|{
if|if
condition|(
name|level
operator|>=
name|skipDiffList
operator|.
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
else|else
block|{
return|return
name|skipDiffList
operator|.
name|get
argument_list|(
name|level
argument_list|)
operator|.
name|getSkipTo
argument_list|()
return|;
block|}
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
name|diff
operator|!=
literal|null
condition|?
literal|""
operator|+
name|diff
operator|.
name|getSnapshotId
argument_list|()
else|:
literal|"?"
return|;
block|}
block|}
comment|/**    * The reference to the first node of the list.    * The list will grow linearly once a new Directory diff gets added.    * All the list inteface defined methods provide a linear view of the list.    */
DECL|field|skipNodeList
specifier|private
name|List
argument_list|<
name|SkipListNode
argument_list|>
name|skipNodeList
decl_stmt|;
comment|/**    * The max no of skipLevels.    */
DECL|field|maxSkipLevels
specifier|private
specifier|final
name|int
name|maxSkipLevels
decl_stmt|;
comment|/**    * The no of diffs after which the level promotion happens.    */
DECL|field|skipInterval
specifier|private
specifier|final
name|int
name|skipInterval
decl_stmt|;
comment|/**    * The head node to the list.    */
DECL|field|head
specifier|private
name|SkipListNode
name|head
decl_stmt|;
comment|/**    * Constructs a new, empty instance of SkipList.    */
DECL|method|DirectoryDiffList (int capacity, int interval, int skipLevel)
specifier|public
name|DirectoryDiffList
parameter_list|(
name|int
name|capacity
parameter_list|,
name|int
name|interval
parameter_list|,
name|int
name|skipLevel
parameter_list|)
block|{
name|skipNodeList
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|capacity
argument_list|)
expr_stmt|;
name|head
operator|=
operator|new
name|SkipListNode
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|this
operator|.
name|maxSkipLevels
operator|=
name|skipLevel
expr_stmt|;
name|this
operator|.
name|skipInterval
operator|=
name|interval
expr_stmt|;
block|}
comment|/**    * Adds the specified data element to the beginning of the SkipList,    * if the element is not already present.    * @param diff the element to be inserted    */
annotation|@
name|Override
DECL|method|addFirst (DirectoryDiff diff)
specifier|public
name|void
name|addFirst
parameter_list|(
name|DirectoryDiff
name|diff
parameter_list|)
block|{
specifier|final
name|int
name|nodeLevel
init|=
name|randomLevel
argument_list|(
name|skipInterval
argument_list|,
name|maxSkipLevels
argument_list|)
decl_stmt|;
specifier|final
name|SkipListNode
index|[]
name|nodePath
init|=
operator|new
name|SkipListNode
index|[
name|nodeLevel
operator|+
literal|1
index|]
decl_stmt|;
name|Arrays
operator|.
name|fill
argument_list|(
name|nodePath
argument_list|,
name|head
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|level
init|=
name|head
operator|.
name|level
argument_list|()
operator|+
literal|1
init|;
name|level
operator|<=
name|nodeLevel
condition|;
name|level
operator|++
control|)
block|{
name|head
operator|.
name|skipDiffList
operator|.
name|add
argument_list|(
operator|new
name|SkipDiff
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|SkipListNode
name|newNode
init|=
operator|new
name|SkipListNode
argument_list|(
name|diff
argument_list|,
name|nodeLevel
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
literal|0
init|;
name|level
operator|<=
name|nodeLevel
condition|;
name|level
operator|++
control|)
block|{
if|if
condition|(
name|level
operator|>
literal|0
condition|)
block|{
comment|// Case : S0 is added at the beginning and it has 3 levels
comment|//  suppose the list is like:
comment|//  level 1: head ------------------->s5------------->NULL
comment|//  level 0:head->    s1->s2->s3->s4->s5->s6->s7->s8->s9
comment|//  in this case:
comment|//  level 2: head -> s0 -------------------------------->NULL
comment|//  level 1: head -> s0'---------------->s5------------->NULL
comment|//  level 0:head->   s0->s1->s2->s3->s4->s5->s6->s7->s8->s9
comment|//  At level 1, we need to combine s0, s1, s2, s3, s4 and s5 and store
comment|//  as s0'. At level 2, s0 of next is pointing to null;
comment|//  Note: in this case, the diff of element being added is included
comment|//  while combining the diffs.
specifier|final
name|SkipListNode
name|nextNode
init|=
name|head
operator|.
name|getSkipNode
argument_list|(
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|nextNode
operator|!=
literal|null
condition|)
block|{
name|ChildrenDiff
name|combined
init|=
name|combineDiff
argument_list|(
name|newNode
argument_list|,
name|nextNode
argument_list|,
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|combined
operator|!=
literal|null
condition|)
block|{
name|newNode
operator|.
name|setSkipDiff
argument_list|(
name|combined
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|//insert to the linked list
name|newNode
operator|.
name|setSkipTo
argument_list|(
name|nodePath
index|[
name|level
index|]
operator|.
name|getSkipNode
argument_list|(
name|level
argument_list|)
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|nodePath
index|[
name|level
index|]
operator|.
name|setSkipTo
argument_list|(
name|newNode
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
name|skipNodeList
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|newNode
argument_list|)
expr_stmt|;
block|}
DECL|method|findPreviousNodes (SkipListNode node, int nodeLevel)
specifier|private
name|SkipListNode
index|[]
name|findPreviousNodes
parameter_list|(
name|SkipListNode
name|node
parameter_list|,
name|int
name|nodeLevel
parameter_list|)
block|{
specifier|final
name|SkipListNode
index|[]
name|nodePath
init|=
operator|new
name|SkipListNode
index|[
name|nodeLevel
operator|+
literal|1
index|]
decl_stmt|;
name|SkipListNode
name|cur
init|=
name|head
decl_stmt|;
specifier|final
name|int
name|headLevel
init|=
name|head
operator|.
name|level
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
name|headLevel
operator|<
name|nodeLevel
condition|?
name|headLevel
else|:
name|nodeLevel
init|;
name|level
operator|>=
literal|0
condition|;
name|level
operator|--
control|)
block|{
while|while
condition|(
name|cur
operator|.
name|getSkipNode
argument_list|(
name|level
argument_list|)
operator|!=
name|node
condition|)
block|{
name|cur
operator|=
name|cur
operator|.
name|getSkipNode
argument_list|(
name|level
argument_list|)
expr_stmt|;
block|}
name|nodePath
index|[
name|level
index|]
operator|=
name|cur
expr_stmt|;
block|}
for|for
control|(
name|int
name|level
init|=
name|headLevel
operator|+
literal|1
init|;
name|level
operator|<=
name|nodeLevel
condition|;
name|level
operator|++
control|)
block|{
name|nodePath
index|[
name|level
index|]
operator|=
name|head
expr_stmt|;
block|}
return|return
name|nodePath
return|;
block|}
comment|/**    * Adds the specified data element to the end of the SkipList,    * if the element is not already present.    * @param diff the element to be inserted    */
annotation|@
name|Override
DECL|method|addLast (DirectoryDiff diff)
specifier|public
name|boolean
name|addLast
parameter_list|(
name|DirectoryDiff
name|diff
parameter_list|)
block|{
specifier|final
name|int
name|nodeLevel
init|=
name|randomLevel
argument_list|(
name|skipInterval
argument_list|,
name|maxSkipLevels
argument_list|)
decl_stmt|;
specifier|final
name|int
name|headLevel
init|=
name|head
operator|.
name|level
argument_list|()
decl_stmt|;
specifier|final
name|SkipListNode
index|[]
name|nodePath
init|=
name|findPreviousNodes
argument_list|(
literal|null
argument_list|,
name|nodeLevel
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
name|headLevel
operator|+
literal|1
init|;
name|level
operator|<=
name|nodeLevel
condition|;
name|level
operator|++
control|)
block|{
name|head
operator|.
name|skipDiffList
operator|.
name|add
argument_list|(
operator|new
name|SkipDiff
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|nodePath
index|[
name|level
index|]
operator|=
name|head
expr_stmt|;
block|}
specifier|final
name|SkipListNode
name|current
init|=
operator|new
name|SkipListNode
argument_list|(
name|diff
argument_list|,
name|nodeLevel
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
literal|0
init|;
name|level
operator|<=
name|nodeLevel
condition|;
name|level
operator|++
control|)
block|{
if|if
condition|(
name|level
operator|>
literal|0
operator|&&
name|nodePath
index|[
name|level
index|]
operator|!=
name|head
condition|)
block|{
comment|//  suppose the list is like:
comment|//  level 2: head ->  s1----------------------------->NULL
comment|//  level 1: head ->  s1---->s3'------>s5------------->NULL
comment|//  level 0:head->    s1->s2->s3->s4->s5->s6->s7->s8->s9
comment|// case : s10 is added at the end the let the level for this node = 4
comment|//  in this case,
comment|//  level 2: head ->  s1''------------------------------------>s10
comment|//  level 1: head ->  s1'---->s3'------>s5'-------------------->s10
comment|//  level 0:head->    s1->s2->s3->s4->s5->s6->s7->s8->s9---->s10
comment|//  At level 1, we combine s5, s6, s7, s8, s9 and store as s5'
comment|//  At level 2, we combine s1', s3', s5' and form s1'' and store at s1.
comment|// Note : the last element(elemnt being added) diff is not added while
comment|// combining the diffs.
name|ChildrenDiff
name|combined
init|=
name|combineDiff
argument_list|(
name|nodePath
index|[
name|level
index|]
argument_list|,
name|current
argument_list|,
name|level
argument_list|)
decl_stmt|;
if|if
condition|(
name|combined
operator|!=
literal|null
condition|)
block|{
name|nodePath
index|[
name|level
index|]
operator|.
name|setSkipDiff
argument_list|(
name|combined
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
block|}
name|nodePath
index|[
name|level
index|]
operator|.
name|setSkipTo
argument_list|(
name|current
argument_list|,
name|level
argument_list|)
expr_stmt|;
name|current
operator|.
name|setSkipTo
argument_list|(
literal|null
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
return|return
name|skipNodeList
operator|.
name|add
argument_list|(
name|current
argument_list|)
return|;
block|}
DECL|method|combineDiff (SkipListNode from, SkipListNode to, int level)
specifier|private
specifier|static
name|ChildrenDiff
name|combineDiff
parameter_list|(
name|SkipListNode
name|from
parameter_list|,
name|SkipListNode
name|to
parameter_list|,
name|int
name|level
parameter_list|)
block|{
name|ChildrenDiff
name|combined
init|=
literal|null
decl_stmt|;
name|SkipListNode
name|cur
init|=
name|from
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|level
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
while|while
condition|(
name|cur
operator|!=
name|to
condition|)
block|{
specifier|final
name|SkipListNode
name|next
init|=
name|cur
operator|.
name|getSkipNode
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|combined
operator|==
literal|null
condition|)
block|{
name|combined
operator|=
operator|new
name|ChildrenDiff
argument_list|()
expr_stmt|;
block|}
name|combined
operator|.
name|combinePosterior
argument_list|(
name|cur
operator|.
name|getChildrenDiff
argument_list|(
name|i
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|cur
operator|=
name|next
expr_stmt|;
block|}
block|}
return|return
name|combined
return|;
block|}
comment|/**    * Returns the data element at the specified index in this SkipList.    *    * @param index The index of the element to be returned.    * @return The element at the specified index in this SkipList.    */
annotation|@
name|Override
DECL|method|get (int index)
specifier|public
name|DirectoryDiff
name|get
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|skipNodeList
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getDiff
argument_list|()
return|;
block|}
comment|/**    * Removes the element at the specified position in this list.    *    * @param index the index of the element to be removed    * @return the removed DirectoryDiff    */
annotation|@
name|Override
DECL|method|remove (int index)
specifier|public
name|DirectoryDiff
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|SkipListNode
name|node
init|=
name|getNode
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|int
name|headLevel
init|=
name|head
operator|.
name|level
argument_list|()
decl_stmt|;
name|int
name|nodeLevel
init|=
name|node
operator|.
name|level
argument_list|()
decl_stmt|;
specifier|final
name|SkipListNode
index|[]
name|nodePath
init|=
name|findPreviousNodes
argument_list|(
name|node
argument_list|,
name|nodeLevel
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
literal|0
init|;
name|level
operator|<=
name|nodeLevel
condition|;
name|level
operator|++
control|)
block|{
if|if
condition|(
name|nodePath
index|[
name|level
index|]
operator|!=
name|head
operator|&&
name|level
operator|>
literal|0
condition|)
block|{
comment|// if the last snapshot is deleted, for all the skip level nodes
comment|// pointing to the last one, the combined children diff at each level
comment|//> 0 should be made null and skip pointers will be updated to null.
comment|// if the snapshot being deleted is not the last one, we have to merge
comment|// the diff of deleted node at each level to the previous skip level
comment|// node at that level and the skip pointers will be updated to point to
comment|// the skip nodes of the deleted node.
if|if
condition|(
name|index
operator|==
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
name|nodePath
index|[
name|level
index|]
operator|.
name|setSkipDiff
argument_list|(
literal|null
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|/* Ideally at level 0, the deleted diff will be combined with            * the previous diff , and deleted inodes will be cleaned up            * by passing a deleted processor here while combining the diffs.            * Level 0 merge with previous diff will be handled inside the            * {@link AbstractINodeDiffList#deleteSnapshotDiff} function.            */
if|if
condition|(
name|node
operator|.
name|getChildrenDiff
argument_list|(
name|level
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|nodePath
index|[
name|level
index|]
operator|.
name|getChildrenDiff
argument_list|(
name|level
argument_list|)
operator|.
name|combinePosterior
argument_list|(
name|node
operator|.
name|getChildrenDiff
argument_list|(
name|level
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|nodePath
index|[
name|level
index|]
operator|.
name|setSkipTo
argument_list|(
name|node
operator|.
name|getSkipNode
argument_list|(
name|level
argument_list|)
argument_list|,
name|level
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|nodeLevel
operator|==
name|headLevel
condition|)
block|{
name|head
operator|.
name|trim
argument_list|()
expr_stmt|;
block|}
return|return
name|skipNodeList
operator|.
name|remove
argument_list|(
name|index
argument_list|)
operator|.
name|getDiff
argument_list|()
return|;
block|}
comment|/**    * Returns true if this SkipList contains no data elements. In other words,    * returns true if the size of this SkipList is zero.    *    * @return True if this SkipList contains no elements.    */
annotation|@
name|Override
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|skipNodeList
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|/**    * Returns the number of data elements in this SkipList.    *    * @return The number of elements in this SkipList.    */
annotation|@
name|Override
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|skipNodeList
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**    * Iterator is an iterator over the SkipList. This should    * always provide a linear view of the list.    */
annotation|@
name|Override
DECL|method|iterator ()
specifier|public
name|Iterator
argument_list|<
name|DirectoryDiff
argument_list|>
name|iterator
parameter_list|()
block|{
specifier|final
name|Iterator
argument_list|<
name|SkipListNode
argument_list|>
name|i
init|=
name|skipNodeList
operator|.
name|iterator
argument_list|()
decl_stmt|;
return|return
operator|new
name|Iterator
argument_list|<
name|DirectoryDiff
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|i
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DirectoryDiff
name|next
parameter_list|()
block|{
return|return
name|i
operator|.
name|next
argument_list|()
operator|.
name|getDiff
argument_list|()
return|;
block|}
block|}
return|;
block|}
annotation|@
name|Override
DECL|method|binarySearch (int key)
specifier|public
name|int
name|binarySearch
parameter_list|(
name|int
name|key
parameter_list|)
block|{
return|return
name|Collections
operator|.
name|binarySearch
argument_list|(
name|skipNodeList
argument_list|,
name|key
argument_list|)
return|;
block|}
DECL|method|getNode (int index)
specifier|private
name|SkipListNode
name|getNode
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|skipNodeList
operator|.
name|get
argument_list|(
name|index
argument_list|)
return|;
block|}
comment|/**    * Returns the level of the skipList node.    *    * @param skipInterval The max interval after which the next level promotion    *                     should happen.    * @param maxLevel     Maximum no of skip levels    * @return A value in the range 0 to maxLevel-1.    */
DECL|method|randomLevel (int skipInterval, int maxLevel)
specifier|static
name|int
name|randomLevel
parameter_list|(
name|int
name|skipInterval
parameter_list|,
name|int
name|maxLevel
parameter_list|)
block|{
specifier|final
name|Random
name|r
init|=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
literal|0
init|;
name|level
operator|<
name|maxLevel
condition|;
name|level
operator|++
control|)
block|{
comment|// skip to the next level with probability 1/skipInterval
if|if
condition|(
name|r
operator|.
name|nextInt
argument_list|(
name|skipInterval
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
name|level
return|;
block|}
block|}
return|return
name|maxLevel
return|;
block|}
comment|/**    * This function returns the minimal set of diffs required to combine in    * order to generate all the changes occurred between fromIndex and    * toIndex.    *    * @param fromIndex index from where the summation has to start(inclusive)    * @param toIndex   index till where the summation has to end(exclusive)    * @return list of Directory Diff    */
annotation|@
name|Override
DECL|method|getMinListForRange (int fromIndex, int toIndex, INodeDirectory dir)
specifier|public
name|List
argument_list|<
name|DirectoryDiff
argument_list|>
name|getMinListForRange
parameter_list|(
name|int
name|fromIndex
parameter_list|,
name|int
name|toIndex
parameter_list|,
name|INodeDirectory
name|dir
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|DirectoryDiff
argument_list|>
name|subList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|int
name|toSnapshotId
init|=
name|get
argument_list|(
name|toIndex
operator|-
literal|1
argument_list|)
operator|.
name|getSnapshotId
argument_list|()
decl_stmt|;
for|for
control|(
name|SkipListNode
name|current
init|=
name|getNode
argument_list|(
name|fromIndex
argument_list|)
init|;
name|current
operator|!=
literal|null
condition|;
control|)
block|{
name|SkipListNode
name|next
init|=
literal|null
decl_stmt|;
name|ChildrenDiff
name|childrenDiff
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|level
init|=
name|current
operator|.
name|level
argument_list|()
init|;
name|level
operator|>=
literal|0
condition|;
name|level
operator|--
control|)
block|{
name|next
operator|=
name|current
operator|.
name|getSkipNode
argument_list|(
name|level
argument_list|)
expr_stmt|;
if|if
condition|(
name|next
operator|!=
literal|null
operator|&&
name|next
operator|.
name|getDiff
argument_list|()
operator|.
name|compareTo
argument_list|(
name|toSnapshotId
argument_list|)
operator|<=
literal|0
condition|)
block|{
name|childrenDiff
operator|=
name|current
operator|.
name|getChildrenDiff
argument_list|(
name|level
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
specifier|final
name|DirectoryDiff
name|curDiff
init|=
name|current
operator|.
name|getDiff
argument_list|()
decl_stmt|;
name|subList
operator|.
name|add
argument_list|(
name|childrenDiff
operator|==
literal|null
condition|?
name|curDiff
else|:
operator|new
name|DirectoryDiff
argument_list|(
name|curDiff
operator|.
name|getSnapshotId
argument_list|()
argument_list|,
name|dir
argument_list|,
name|childrenDiff
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
operator|.
name|getDiff
argument_list|()
operator|.
name|compareTo
argument_list|(
name|toSnapshotId
argument_list|)
operator|==
literal|0
condition|)
block|{
break|break;
block|}
name|current
operator|=
name|next
expr_stmt|;
block|}
return|return
name|subList
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
decl_stmt|;
name|b
operator|.
name|append
argument_list|(
literal|" head: "
argument_list|)
operator|.
name|append
argument_list|(
name|head
argument_list|)
operator|.
name|append
argument_list|(
name|head
operator|.
name|skipDiffList
argument_list|)
expr_stmt|;
for|for
control|(
name|SkipListNode
name|n
range|:
name|skipNodeList
control|)
block|{
name|b
operator|.
name|append
argument_list|(
literal|"\n  "
argument_list|)
operator|.
name|append
argument_list|(
name|n
argument_list|)
operator|.
name|append
argument_list|(
name|n
operator|.
name|skipDiffList
argument_list|)
expr_stmt|;
block|}
return|return
name|b
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

