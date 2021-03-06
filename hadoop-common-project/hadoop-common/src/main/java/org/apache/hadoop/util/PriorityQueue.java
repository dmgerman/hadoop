begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
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

begin_comment
comment|/** A PriorityQueue maintains a partial ordering of its elements such that the   least element can always be found in constant time.  Put()'s and pop()'s   require log(size) time. */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|PriorityQueue
specifier|public
specifier|abstract
class|class
name|PriorityQueue
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|heap
specifier|private
name|T
index|[]
name|heap
decl_stmt|;
DECL|field|size
specifier|private
name|int
name|size
decl_stmt|;
DECL|field|maxSize
specifier|private
name|int
name|maxSize
decl_stmt|;
comment|/** Determines the ordering of objects in this priority queue.  Subclasses       must define this one method. */
DECL|method|lessThan (Object a, Object b)
specifier|protected
specifier|abstract
name|boolean
name|lessThan
parameter_list|(
name|Object
name|a
parameter_list|,
name|Object
name|b
parameter_list|)
function_decl|;
comment|/** Subclass constructors must call this. */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|initialize (int maxSize)
specifier|protected
specifier|final
name|void
name|initialize
parameter_list|(
name|int
name|maxSize
parameter_list|)
block|{
name|size
operator|=
literal|0
expr_stmt|;
name|int
name|heapSize
init|=
name|maxSize
operator|+
literal|1
decl_stmt|;
name|heap
operator|=
operator|(
name|T
index|[]
operator|)
operator|new
name|Object
index|[
name|heapSize
index|]
expr_stmt|;
name|this
operator|.
name|maxSize
operator|=
name|maxSize
expr_stmt|;
block|}
comment|/**    * Adds an Object to a PriorityQueue in log(size) time.    * If one tries to add more objects than maxSize from initialize    * a RuntimeException (ArrayIndexOutOfBound) is thrown.    */
DECL|method|put (T element)
specifier|public
specifier|final
name|void
name|put
parameter_list|(
name|T
name|element
parameter_list|)
block|{
name|size
operator|++
expr_stmt|;
name|heap
index|[
name|size
index|]
operator|=
name|element
expr_stmt|;
name|upHeap
argument_list|()
expr_stmt|;
block|}
comment|/**    * Adds element to the PriorityQueue in log(size) time if either    * the PriorityQueue is not full, or not lessThan(element, top()).    * @param element    * @return true if element is added, false otherwise.    */
DECL|method|insert (T element)
specifier|public
name|boolean
name|insert
parameter_list|(
name|T
name|element
parameter_list|)
block|{
if|if
condition|(
name|size
operator|<
name|maxSize
condition|)
block|{
name|put
argument_list|(
name|element
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|size
operator|>
literal|0
operator|&&
operator|!
name|lessThan
argument_list|(
name|element
argument_list|,
name|top
argument_list|()
argument_list|)
condition|)
block|{
name|heap
index|[
literal|1
index|]
operator|=
name|element
expr_stmt|;
name|adjustTop
argument_list|()
expr_stmt|;
return|return
literal|true
return|;
block|}
else|else
return|return
literal|false
return|;
block|}
comment|/** Returns the least element of the PriorityQueue in constant time. */
DECL|method|top ()
specifier|public
specifier|final
name|T
name|top
parameter_list|()
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
return|return
name|heap
index|[
literal|1
index|]
return|;
else|else
return|return
literal|null
return|;
block|}
comment|/** Removes and returns the least element of the PriorityQueue in log(size)       time. */
DECL|method|pop ()
specifier|public
specifier|final
name|T
name|pop
parameter_list|()
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
name|T
name|result
init|=
name|heap
index|[
literal|1
index|]
decl_stmt|;
comment|// save first value
name|heap
index|[
literal|1
index|]
operator|=
name|heap
index|[
name|size
index|]
expr_stmt|;
comment|// move last to first
name|heap
index|[
name|size
index|]
operator|=
literal|null
expr_stmt|;
comment|// permit GC of objects
name|size
operator|--
expr_stmt|;
name|downHeap
argument_list|()
expr_stmt|;
comment|// adjust heap
return|return
name|result
return|;
block|}
else|else
return|return
literal|null
return|;
block|}
comment|/** Should be called when the Object at top changes values.  Still log(n)    * worst case, but it's at least twice as fast to<pre>    *  { pq.top().change(); pq.adjustTop(); }    *</pre> instead of<pre>    *  { o = pq.pop(); o.change(); pq.push(o); }    *</pre>    */
DECL|method|adjustTop ()
specifier|public
specifier|final
name|void
name|adjustTop
parameter_list|()
block|{
name|downHeap
argument_list|()
expr_stmt|;
block|}
comment|/** Returns the number of elements currently stored in the PriorityQueue. */
DECL|method|size ()
specifier|public
specifier|final
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/** Removes all entries from the PriorityQueue. */
DECL|method|clear ()
specifier|public
specifier|final
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|size
condition|;
name|i
operator|++
control|)
name|heap
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
DECL|method|upHeap ()
specifier|private
specifier|final
name|void
name|upHeap
parameter_list|()
block|{
name|int
name|i
init|=
name|size
decl_stmt|;
name|T
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save bottom node
name|int
name|j
init|=
name|i
operator|>>>
literal|1
decl_stmt|;
while|while
condition|(
name|j
operator|>
literal|0
operator|&&
name|lessThan
argument_list|(
name|node
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
comment|// shift parents down
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|j
operator|>>>
literal|1
expr_stmt|;
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
block|}
DECL|method|downHeap ()
specifier|private
specifier|final
name|void
name|downHeap
parameter_list|()
block|{
name|int
name|i
init|=
literal|1
decl_stmt|;
name|T
name|node
init|=
name|heap
index|[
name|i
index|]
decl_stmt|;
comment|// save top node
name|int
name|j
init|=
name|i
operator|<<
literal|1
decl_stmt|;
comment|// find smaller child
name|int
name|k
init|=
name|j
operator|+
literal|1
decl_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|k
index|]
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
while|while
condition|(
name|j
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|j
index|]
argument_list|,
name|node
argument_list|)
condition|)
block|{
name|heap
index|[
name|i
index|]
operator|=
name|heap
index|[
name|j
index|]
expr_stmt|;
comment|// shift up child
name|i
operator|=
name|j
expr_stmt|;
name|j
operator|=
name|i
operator|<<
literal|1
expr_stmt|;
name|k
operator|=
name|j
operator|+
literal|1
expr_stmt|;
if|if
condition|(
name|k
operator|<=
name|size
operator|&&
name|lessThan
argument_list|(
name|heap
index|[
name|k
index|]
argument_list|,
name|heap
index|[
name|j
index|]
argument_list|)
condition|)
block|{
name|j
operator|=
name|k
expr_stmt|;
block|}
block|}
name|heap
index|[
name|i
index|]
operator|=
name|node
expr_stmt|;
comment|// install saved node
block|}
block|}
end_class

end_unit

