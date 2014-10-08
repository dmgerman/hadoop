begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azure
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azure
package|;
end_package

begin_comment
comment|/**  * A simple generic stack implementation using linked lists. The stack  * implementation has five main operations:  *<ul>  *<li>push -- adds an element to the top of the stack</li>  *<li>pop -- removes an element from the top of the stack and returns a  * reference to it</li>  *<li>peek -- peek returns an element from the top of the stack without  * removing it</li>  *<li>isEmpty -- tests whether the stack is empty</li>  *<li>size -- returns the size of the stack</li>  *<li>toString -- returns a string representation of the stack.</li>  *</ul>  */
end_comment

begin_class
DECL|class|AzureLinkedStack
specifier|public
class|class
name|AzureLinkedStack
parameter_list|<
name|E
parameter_list|>
block|{
comment|/*    * Linked node for Azure stack collection.    */
DECL|class|AzureLinkedNode
specifier|private
specifier|static
class|class
name|AzureLinkedNode
parameter_list|<
name|E
parameter_list|>
block|{
DECL|field|element
specifier|private
name|E
name|element
decl_stmt|;
comment|// Linked element on the list.
DECL|field|next
specifier|private
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
name|next
decl_stmt|;
comment|// Reference to the next linked element on
comment|// list.
comment|/*      * The constructor builds the linked node with no successor      *      * @param element : The value of the element to be stored with this node.      */
DECL|method|AzureLinkedNode (E anElement)
specifier|private
name|AzureLinkedNode
parameter_list|(
name|E
name|anElement
parameter_list|)
block|{
name|element
operator|=
name|anElement
expr_stmt|;
name|next
operator|=
literal|null
expr_stmt|;
block|}
comment|/*      * Constructor builds a linked node with a specified successor. The      * successor may be null.      *      * @param anElement : new element to be created.      *      * @param nextElement: successor to the new element.      */
DECL|method|AzureLinkedNode (E anElement, AzureLinkedNode<E> nextElement)
specifier|private
name|AzureLinkedNode
parameter_list|(
name|E
name|anElement
parameter_list|,
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
name|nextElement
parameter_list|)
block|{
name|element
operator|=
name|anElement
expr_stmt|;
name|next
operator|=
name|nextElement
expr_stmt|;
block|}
comment|/*      * Get the element stored in the linked node.      *      * @return E : element stored in linked node.      */
DECL|method|getElement ()
specifier|private
name|E
name|getElement
parameter_list|()
block|{
return|return
name|element
return|;
block|}
comment|/*      * Get the successor node to the element.      *      * @return E : reference to the succeeding node on the list.      */
DECL|method|getNext ()
specifier|private
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
block|}
DECL|field|count
specifier|private
name|int
name|count
decl_stmt|;
comment|// The number of elements stored on the stack.
DECL|field|top
specifier|private
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
name|top
decl_stmt|;
comment|// Top of the stack.
comment|/*    * Constructor creating an empty stack.    */
DECL|method|AzureLinkedStack ()
specifier|public
name|AzureLinkedStack
parameter_list|()
block|{
comment|// Simply initialize the member variables.
comment|//
name|count
operator|=
literal|0
expr_stmt|;
name|top
operator|=
literal|null
expr_stmt|;
block|}
comment|/*    * Adds an element to the top of the stack.    *    * @param element : element pushed to the top of the stack.    */
DECL|method|push (E element)
specifier|public
name|void
name|push
parameter_list|(
name|E
name|element
parameter_list|)
block|{
comment|// Create a new node containing a reference to be placed on the stack.
comment|// Set the next reference to the new node to point to the current top
comment|// of the stack. Set the top reference to point to the new node. Finally
comment|// increment the count of nodes on the stack.
comment|//
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
name|newNode
init|=
operator|new
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
argument_list|(
name|element
argument_list|,
name|top
argument_list|)
decl_stmt|;
name|top
operator|=
name|newNode
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
comment|/*    * Removes the element at the top of the stack and returns a reference to it.    *    * @return E : element popped from the top of the stack.    *    * @throws Exception on pop from an empty stack.    */
DECL|method|pop ()
specifier|public
name|E
name|pop
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure the stack is not empty. If it is empty, throw a StackEmpty
comment|// exception.
comment|//
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"AzureStackEmpty"
argument_list|)
throw|;
block|}
comment|// Set a temporary reference equal to the element at the top of the stack,
comment|// decrement the count of elements and return reference to the temporary.
comment|//
name|E
name|element
init|=
name|top
operator|.
name|getElement
argument_list|()
decl_stmt|;
name|top
operator|=
name|top
operator|.
name|getNext
argument_list|()
expr_stmt|;
name|count
operator|--
expr_stmt|;
comment|// Return the reference to the element that was at the top of the stack.
comment|//
return|return
name|element
return|;
block|}
comment|/*    * Return the top element of the stack without removing it.    *    * @return E    *    * @throws Exception on peek into an empty stack.    */
DECL|method|peek ()
specifier|public
name|E
name|peek
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Make sure the stack is not empty. If it is empty, throw a StackEmpty
comment|// exception.
comment|//
if|if
condition|(
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"AzureStackEmpty"
argument_list|)
throw|;
block|}
comment|// Set a temporary reference equal to the element at the top of the stack
comment|// and return the temporary.
comment|//
name|E
name|element
init|=
name|top
operator|.
name|getElement
argument_list|()
decl_stmt|;
return|return
name|element
return|;
block|}
comment|/*    * Determines whether the stack is empty    *    * @return boolean true if the stack is empty and false otherwise.    */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
if|if
condition|(
literal|0
operator|==
name|size
argument_list|()
condition|)
block|{
comment|// Zero-sized stack so the stack is empty.
comment|//
return|return
literal|true
return|;
block|}
comment|// The stack is not empty.
comment|//
return|return
literal|false
return|;
block|}
comment|/*    * Determines the size of the stack    *    * @return int: Count of the number of elements in the stack.    */
DECL|method|size ()
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/*    * Returns a string representation of the stack.    *    * @return String String representation of all elements in the stack.    */
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
name|AzureLinkedNode
argument_list|<
name|E
argument_list|>
name|current
init|=
name|top
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
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|E
name|element
init|=
name|current
operator|.
name|getElement
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|element
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|current
operator|=
name|current
operator|.
name|getNext
argument_list|()
expr_stmt|;
comment|// Insert commas between strings except after the last string.
comment|//
if|if
condition|(
name|size
argument_list|()
operator|-
literal|1
operator|>
name|i
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Return the string.
comment|//
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

