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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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

begin_comment
comment|/**  * Traversal yielding a hierarchical sequence of paths.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Public
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|TreeWalk
specifier|public
specifier|abstract
class|class
name|TreeWalk
implements|implements
name|Iterable
argument_list|<
name|TreePath
argument_list|>
block|{
comment|/**    * @param path path to the node being explored.    * @param id the id of the node.    * @param iterator the {@link TreeIterator} to use.    * @return paths representing the children of the current node.    */
DECL|method|getChildren ( TreePath path, long id, TreeWalk.TreeIterator iterator)
specifier|protected
specifier|abstract
name|Iterable
argument_list|<
name|TreePath
argument_list|>
name|getChildren
parameter_list|(
name|TreePath
name|path
parameter_list|,
name|long
name|id
parameter_list|,
name|TreeWalk
operator|.
name|TreeIterator
name|iterator
parameter_list|)
function_decl|;
DECL|method|iterator ()
specifier|public
specifier|abstract
name|TreeIterator
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Enumerator class for hierarchies. Implementations SHOULD support a fork()    * operation yielding a subtree of the current cursor.    */
DECL|class|TreeIterator
specifier|public
specifier|abstract
class|class
name|TreeIterator
implements|implements
name|Iterator
argument_list|<
name|TreePath
argument_list|>
block|{
DECL|field|pending
specifier|private
specifier|final
name|Deque
argument_list|<
name|TreePath
argument_list|>
name|pending
decl_stmt|;
DECL|method|TreeIterator ()
name|TreeIterator
parameter_list|()
block|{
name|this
argument_list|(
operator|new
name|ArrayDeque
argument_list|<
name|TreePath
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|TreeIterator (Deque<TreePath> pending)
specifier|protected
name|TreeIterator
parameter_list|(
name|Deque
argument_list|<
name|TreePath
argument_list|>
name|pending
parameter_list|)
block|{
name|this
operator|.
name|pending
operator|=
name|pending
expr_stmt|;
block|}
DECL|method|fork ()
specifier|public
specifier|abstract
name|TreeIterator
name|fork
parameter_list|()
function_decl|;
annotation|@
name|Override
DECL|method|hasNext ()
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|!
name|pending
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|next ()
specifier|public
name|TreePath
name|next
parameter_list|()
block|{
return|return
name|pending
operator|.
name|removeFirst
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|remove ()
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
DECL|method|onAccept (TreePath p, long id)
specifier|protected
name|void
name|onAccept
parameter_list|(
name|TreePath
name|p
parameter_list|,
name|long
name|id
parameter_list|)
block|{
for|for
control|(
name|TreePath
name|k
range|:
name|getChildren
argument_list|(
name|p
argument_list|,
name|id
argument_list|,
name|this
argument_list|)
control|)
block|{
name|pending
operator|.
name|addFirst
argument_list|(
name|k
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @return the Deque containing the pending paths.      */
DECL|method|getPendingQueue ()
specifier|protected
name|Deque
argument_list|<
name|TreePath
argument_list|>
name|getPendingQueue
parameter_list|()
block|{
return|return
name|pending
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
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"{ Treewalk=\""
argument_list|)
operator|.
name|append
argument_list|(
name|TreeWalk
operator|.
name|this
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|", pending=["
argument_list|)
expr_stmt|;
name|Iterator
argument_list|<
name|TreePath
argument_list|>
name|i
init|=
name|pending
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", \""
argument_list|)
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" }"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

