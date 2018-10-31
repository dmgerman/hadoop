begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
package|;
end_package

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
name|Iterator
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
comment|/**  * The difference between the current state and a previous state of a list.  *   * Given a previous state of a set and a sequence of create, delete and modify  * operations such that the current state of the set can be obtained by applying  * the operations on the previous state, the following algorithm construct the  * difference between the current state and the previous state of the set.  *   *<pre>  * Two lists are maintained in the algorithm:  * - c-list for newly created elements  * - d-list for the deleted elements  *  * Denote the state of an element by the following  *   (0, 0): neither in c-list nor d-list  *   (c, 0): in c-list but not in d-list  *   (0, d): in d-list but not in c-list  *   (c, d): in both c-list and d-list  *  * For each case below, ( , ) at the end shows the result state of the element.  *  * Case 1. Suppose the element i is NOT in the previous state.           (0, 0)  *   1.1. create i in current: add it to c-list                          (c, 0)  *   1.1.1. create i in current and then create: impossible  *   1.1.2. create i in current and then delete: remove it from c-list   (0, 0)  *   1.1.3. create i in current and then modify: replace it in c-list    (c', 0)  *  *   1.2. delete i from current: impossible  *  *   1.3. modify i in current: impossible  *  * Case 2. Suppose the element i is ALREADY in the previous state.       (0, 0)  *   2.1. create i in current: impossible  *  *   2.2. delete i from current: add it to d-list                        (0, d)  *   2.2.1. delete i from current and then create: add it to c-list      (c, d)  *   2.2.2. delete i from current and then delete: impossible  *   2.2.2. delete i from current and then modify: impossible  *  *   2.3. modify i in current: put it in both c-list and d-list          (c, d)  *   2.3.1. modify i in current and then create: impossible  *   2.3.2. modify i in current and then delete: remove it from c-list   (0, d)  *   2.3.3. modify i in current and then modify: replace it in c-list    (c', d)  *</pre>  *  * @param<K> The key type.  * @param<E> The element type, which must implement {@link Element} interface.  */
end_comment

begin_class
DECL|class|Diff
specifier|public
class|class
name|Diff
parameter_list|<
name|K
parameter_list|,
name|E
extends|extends
name|Diff
operator|.
name|Element
parameter_list|<
name|K
parameter_list|>
parameter_list|>
block|{
comment|/** An interface for the elements in a {@link Diff}. */
DECL|interface|Element
specifier|public
specifier|static
interface|interface
name|Element
parameter_list|<
name|K
parameter_list|>
extends|extends
name|Comparable
argument_list|<
name|K
argument_list|>
block|{
comment|/** @return the key of this object. */
DECL|method|getKey ()
specifier|public
name|K
name|getKey
parameter_list|()
function_decl|;
block|}
comment|/** An interface for passing a method in order to process elements. */
DECL|interface|Processor
specifier|public
specifier|static
interface|interface
name|Processor
parameter_list|<
name|E
parameter_list|>
block|{
comment|/** Process the given element. */
DECL|method|process (E element)
specifier|public
name|void
name|process
parameter_list|(
name|E
name|element
parameter_list|)
function_decl|;
block|}
comment|/** Containing exactly one element. */
DECL|class|Container
specifier|public
specifier|static
class|class
name|Container
parameter_list|<
name|E
parameter_list|>
block|{
DECL|field|element
specifier|private
specifier|final
name|E
name|element
decl_stmt|;
DECL|method|Container (E element)
specifier|private
name|Container
parameter_list|(
name|E
name|element
parameter_list|)
block|{
name|this
operator|.
name|element
operator|=
name|element
expr_stmt|;
block|}
comment|/** @return the element. */
DECL|method|getElement ()
specifier|public
name|E
name|getElement
parameter_list|()
block|{
return|return
name|element
return|;
block|}
block|}
comment|/**     * Undo information for some operations such as delete(E)    * and {@link Diff#modify(Element, Element)}.    */
DECL|class|UndoInfo
specifier|public
specifier|static
class|class
name|UndoInfo
parameter_list|<
name|E
parameter_list|>
block|{
DECL|field|createdInsertionPoint
specifier|private
specifier|final
name|int
name|createdInsertionPoint
decl_stmt|;
DECL|field|trashed
specifier|private
specifier|final
name|E
name|trashed
decl_stmt|;
DECL|field|deletedInsertionPoint
specifier|private
specifier|final
name|Integer
name|deletedInsertionPoint
decl_stmt|;
DECL|method|UndoInfo (final int createdInsertionPoint, final E trashed, final Integer deletedInsertionPoint)
specifier|private
name|UndoInfo
parameter_list|(
specifier|final
name|int
name|createdInsertionPoint
parameter_list|,
specifier|final
name|E
name|trashed
parameter_list|,
specifier|final
name|Integer
name|deletedInsertionPoint
parameter_list|)
block|{
name|this
operator|.
name|createdInsertionPoint
operator|=
name|createdInsertionPoint
expr_stmt|;
name|this
operator|.
name|trashed
operator|=
name|trashed
expr_stmt|;
name|this
operator|.
name|deletedInsertionPoint
operator|=
name|deletedInsertionPoint
expr_stmt|;
block|}
DECL|method|getTrashedElement ()
specifier|public
name|E
name|getTrashedElement
parameter_list|()
block|{
return|return
name|trashed
return|;
block|}
block|}
DECL|field|DEFAULT_ARRAY_INITIAL_CAPACITY
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_ARRAY_INITIAL_CAPACITY
init|=
literal|4
decl_stmt|;
comment|/**    * Search the element from the list.    * @return -1 if the list is null; otherwise, return the insertion point    *    defined in {@link Collections#binarySearch(List, Object)}.    *    Note that, when the list is null, -1 is the correct insertion point.    */
DECL|method|search ( final List<E> elements, final K name)
specifier|protected
specifier|static
parameter_list|<
name|K
parameter_list|,
name|E
extends|extends
name|Comparable
argument_list|<
name|K
argument_list|>
parameter_list|>
name|int
name|search
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|elements
parameter_list|,
specifier|final
name|K
name|name
parameter_list|)
block|{
return|return
name|elements
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|Collections
operator|.
name|binarySearch
argument_list|(
name|elements
argument_list|,
name|name
argument_list|)
return|;
block|}
DECL|method|remove (final List<E> elements, final int i, final E expected)
specifier|private
specifier|static
parameter_list|<
name|E
parameter_list|>
name|void
name|remove
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|elements
parameter_list|,
specifier|final
name|int
name|i
parameter_list|,
specifier|final
name|E
name|expected
parameter_list|)
block|{
specifier|final
name|E
name|removed
init|=
name|elements
operator|.
name|remove
argument_list|(
operator|-
name|i
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkState
argument_list|(
name|removed
operator|==
name|expected
argument_list|,
literal|"removed != expected=%s, removed=%s."
argument_list|,
name|expected
argument_list|,
name|removed
argument_list|)
expr_stmt|;
block|}
comment|/** c-list: element(s) created in current. */
DECL|field|created
specifier|private
name|List
argument_list|<
name|E
argument_list|>
name|created
decl_stmt|;
comment|/** d-list: element(s) deleted from current. */
DECL|field|deleted
specifier|private
name|List
argument_list|<
name|E
argument_list|>
name|deleted
decl_stmt|;
DECL|method|Diff ()
specifier|protected
name|Diff
parameter_list|()
block|{}
DECL|method|Diff (final List<E> created, final List<E> deleted)
specifier|protected
name|Diff
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|created
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|deleted
parameter_list|)
block|{
name|this
operator|.
name|created
operator|=
name|created
expr_stmt|;
name|this
operator|.
name|deleted
operator|=
name|deleted
expr_stmt|;
block|}
DECL|method|getCreatedUnmodifiable ()
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|getCreatedUnmodifiable
parameter_list|()
block|{
return|return
name|created
operator|!=
literal|null
condition|?
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|created
argument_list|)
else|:
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|setCreated (int index, E element)
specifier|public
name|E
name|setCreated
parameter_list|(
name|int
name|index
parameter_list|,
name|E
name|element
parameter_list|)
block|{
specifier|final
name|E
name|old
init|=
name|created
operator|.
name|set
argument_list|(
name|index
argument_list|,
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|.
name|compareTo
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Element mismatched: element="
operator|+
name|element
operator|+
literal|" but old="
operator|+
name|old
argument_list|)
throw|;
block|}
return|return
name|old
return|;
block|}
DECL|method|clearCreated ()
specifier|public
name|void
name|clearCreated
parameter_list|()
block|{
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
block|{
name|created
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|getDeletedUnmodifiable ()
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|getDeletedUnmodifiable
parameter_list|()
block|{
return|return
name|deleted
operator|!=
literal|null
condition|?
name|Collections
operator|.
name|unmodifiableList
argument_list|(
name|deleted
argument_list|)
else|:
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
DECL|method|containsDeleted (final K key)
specifier|public
name|boolean
name|containsDeleted
parameter_list|(
specifier|final
name|K
name|key
parameter_list|)
block|{
if|if
condition|(
name|deleted
operator|!=
literal|null
condition|)
block|{
return|return
name|search
argument_list|(
name|deleted
argument_list|,
name|key
argument_list|)
operator|>=
literal|0
return|;
block|}
return|return
literal|false
return|;
block|}
DECL|method|containsDeleted (final E element)
specifier|public
name|boolean
name|containsDeleted
parameter_list|(
specifier|final
name|E
name|element
parameter_list|)
block|{
return|return
name|getDeleted
argument_list|(
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
operator|==
name|element
return|;
block|}
comment|/**    * @return null if the element is not found;    *         otherwise, return the element in the deleted list.    */
DECL|method|getDeleted (final K key)
specifier|public
name|E
name|getDeleted
parameter_list|(
specifier|final
name|K
name|key
parameter_list|)
block|{
if|if
condition|(
name|deleted
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|c
init|=
name|search
argument_list|(
name|deleted
argument_list|,
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|0
condition|)
block|{
return|return
name|deleted
operator|.
name|get
argument_list|(
name|c
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
DECL|method|removeDeleted (final E element)
specifier|public
name|boolean
name|removeDeleted
parameter_list|(
specifier|final
name|E
name|element
parameter_list|)
block|{
if|if
condition|(
name|deleted
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|i
init|=
name|search
argument_list|(
name|deleted
argument_list|,
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>=
literal|0
operator|&&
name|deleted
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|==
name|element
condition|)
block|{
name|deleted
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
DECL|method|clearDeleted ()
specifier|public
name|void
name|clearDeleted
parameter_list|()
block|{
if|if
condition|(
name|deleted
operator|!=
literal|null
condition|)
block|{
name|deleted
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** @return true if no changes contained in the diff */
DECL|method|isEmpty ()
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
operator|(
name|created
operator|==
literal|null
operator|||
name|created
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
operator|(
name|deleted
operator|==
literal|null
operator|||
name|deleted
operator|.
name|isEmpty
argument_list|()
operator|)
return|;
block|}
comment|/**    * Add the given element to the created list,    * provided the element does not exist, i.e. i< 0.    *    * @param i the insertion point defined    *          in {@link Collections#binarySearch(List, Object)}    * @throws AssertionError if i>= 0.    */
DECL|method|addCreated (final E element, final int i)
specifier|private
name|void
name|addCreated
parameter_list|(
specifier|final
name|E
name|element
parameter_list|,
specifier|final
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Element already exists: element="
operator|+
name|element
operator|+
literal|", created="
operator|+
name|created
argument_list|)
throw|;
block|}
if|if
condition|(
name|created
operator|==
literal|null
condition|)
block|{
name|created
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|DEFAULT_ARRAY_INITIAL_CAPACITY
argument_list|)
expr_stmt|;
block|}
name|created
operator|.
name|add
argument_list|(
operator|-
name|i
operator|-
literal|1
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
comment|/** Similar to {@link #addCreated(Element, int)} but for the deleted list. */
DECL|method|addDeleted (final E element, final int i)
specifier|private
name|void
name|addDeleted
parameter_list|(
specifier|final
name|E
name|element
parameter_list|,
specifier|final
name|int
name|i
parameter_list|)
block|{
if|if
condition|(
name|i
operator|>=
literal|0
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Element already exists: element="
operator|+
name|element
operator|+
literal|", deleted="
operator|+
name|deleted
argument_list|)
throw|;
block|}
if|if
condition|(
name|deleted
operator|==
literal|null
condition|)
block|{
name|deleted
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|DEFAULT_ARRAY_INITIAL_CAPACITY
argument_list|)
expr_stmt|;
block|}
name|deleted
operator|.
name|add
argument_list|(
operator|-
name|i
operator|-
literal|1
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create an element in current state.    * @return the c-list insertion point for undo.    */
DECL|method|create (final E element)
specifier|public
name|int
name|create
parameter_list|(
specifier|final
name|E
name|element
parameter_list|)
block|{
specifier|final
name|int
name|c
init|=
name|search
argument_list|(
name|created
argument_list|,
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|addCreated
argument_list|(
name|element
argument_list|,
name|c
argument_list|)
expr_stmt|;
return|return
name|c
return|;
block|}
comment|/**    * Undo the previous create(E) operation. Note that the behavior is    * undefined if the previous operation is not create(E).    */
DECL|method|undoCreate (final E element, final int insertionPoint)
specifier|public
name|void
name|undoCreate
parameter_list|(
specifier|final
name|E
name|element
parameter_list|,
specifier|final
name|int
name|insertionPoint
parameter_list|)
block|{
name|remove
argument_list|(
name|created
argument_list|,
name|insertionPoint
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
comment|/**    * Delete an element from current state.    * @return the undo information.    */
DECL|method|delete (final E element)
specifier|public
name|UndoInfo
argument_list|<
name|E
argument_list|>
name|delete
parameter_list|(
specifier|final
name|E
name|element
parameter_list|)
block|{
specifier|final
name|int
name|c
init|=
name|search
argument_list|(
name|created
argument_list|,
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|E
name|previous
init|=
literal|null
decl_stmt|;
name|Integer
name|d
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|0
condition|)
block|{
comment|// remove a newly created element
name|previous
operator|=
name|created
operator|.
name|remove
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// not in c-list, it must be in previous
name|d
operator|=
name|search
argument_list|(
name|deleted
argument_list|,
name|element
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|addDeleted
argument_list|(
name|element
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|UndoInfo
argument_list|<
name|E
argument_list|>
argument_list|(
name|c
argument_list|,
name|previous
argument_list|,
name|d
argument_list|)
return|;
block|}
comment|/**    * Undo the previous delete(E) operation. Note that the behavior is    * undefined if the previous operation is not delete(E).    */
DECL|method|undoDelete (final E element, final UndoInfo<E> undoInfo)
specifier|public
name|void
name|undoDelete
parameter_list|(
specifier|final
name|E
name|element
parameter_list|,
specifier|final
name|UndoInfo
argument_list|<
name|E
argument_list|>
name|undoInfo
parameter_list|)
block|{
specifier|final
name|int
name|c
init|=
name|undoInfo
operator|.
name|createdInsertionPoint
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|0
condition|)
block|{
name|created
operator|.
name|add
argument_list|(
name|c
argument_list|,
name|undoInfo
operator|.
name|trashed
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|remove
argument_list|(
name|deleted
argument_list|,
name|undoInfo
operator|.
name|deletedInsertionPoint
argument_list|,
name|element
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Modify an element in current state.    * @return the undo information.    */
DECL|method|modify (final E oldElement, final E newElement)
specifier|public
name|UndoInfo
argument_list|<
name|E
argument_list|>
name|modify
parameter_list|(
specifier|final
name|E
name|oldElement
parameter_list|,
specifier|final
name|E
name|newElement
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|oldElement
operator|!=
name|newElement
argument_list|,
literal|"They are the same object: oldElement == newElement = %s"
argument_list|,
name|newElement
argument_list|)
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|oldElement
operator|.
name|compareTo
argument_list|(
name|newElement
operator|.
name|getKey
argument_list|()
argument_list|)
operator|==
literal|0
argument_list|,
literal|"The names do not match: oldElement=%s, newElement=%s"
argument_list|,
name|oldElement
argument_list|,
name|newElement
argument_list|)
expr_stmt|;
specifier|final
name|int
name|c
init|=
name|search
argument_list|(
name|created
argument_list|,
name|newElement
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
name|E
name|previous
init|=
literal|null
decl_stmt|;
name|Integer
name|d
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|0
condition|)
block|{
comment|// Case 1.1.3 and 2.3.3: element is already in c-list,
name|previous
operator|=
name|created
operator|.
name|set
argument_list|(
name|c
argument_list|,
name|newElement
argument_list|)
expr_stmt|;
comment|// For previous != oldElement, set it to oldElement
name|previous
operator|=
name|oldElement
expr_stmt|;
block|}
else|else
block|{
name|d
operator|=
name|search
argument_list|(
name|deleted
argument_list|,
name|oldElement
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
comment|// Case 2.3: neither in c-list nor d-list
name|addCreated
argument_list|(
name|newElement
argument_list|,
name|c
argument_list|)
expr_stmt|;
name|addDeleted
argument_list|(
name|oldElement
argument_list|,
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|UndoInfo
argument_list|<
name|E
argument_list|>
argument_list|(
name|c
argument_list|,
name|previous
argument_list|,
name|d
argument_list|)
return|;
block|}
comment|/**    * Undo the previous modify(E, E) operation. Note that the behavior    * is undefined if the previous operation is not modify(E, E).    */
DECL|method|undoModify (final E oldElement, final E newElement, final UndoInfo<E> undoInfo)
specifier|public
name|void
name|undoModify
parameter_list|(
specifier|final
name|E
name|oldElement
parameter_list|,
specifier|final
name|E
name|newElement
parameter_list|,
specifier|final
name|UndoInfo
argument_list|<
name|E
argument_list|>
name|undoInfo
parameter_list|)
block|{
specifier|final
name|int
name|c
init|=
name|undoInfo
operator|.
name|createdInsertionPoint
decl_stmt|;
if|if
condition|(
name|c
operator|>=
literal|0
condition|)
block|{
name|created
operator|.
name|set
argument_list|(
name|c
argument_list|,
name|undoInfo
operator|.
name|trashed
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|int
name|d
init|=
name|undoInfo
operator|.
name|deletedInsertionPoint
decl_stmt|;
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
name|remove
argument_list|(
name|created
argument_list|,
name|c
argument_list|,
name|newElement
argument_list|)
expr_stmt|;
name|remove
argument_list|(
name|deleted
argument_list|,
name|d
argument_list|,
name|oldElement
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Find an element in the previous state.    *     * @return null if the element cannot be determined in the previous state    *         since no change is recorded and it should be determined in the    *         current state; otherwise, return a {@link Container} containing the    *         element in the previous state. Note that the element can possibly    *         be null which means that the element is not found in the previous    *         state.    */
DECL|method|accessPrevious (final K name)
specifier|public
name|Container
argument_list|<
name|E
argument_list|>
name|accessPrevious
parameter_list|(
specifier|final
name|K
name|name
parameter_list|)
block|{
return|return
name|accessPrevious
argument_list|(
name|name
argument_list|,
name|created
argument_list|,
name|deleted
argument_list|)
return|;
block|}
DECL|method|accessPrevious ( final K name, final List<E> clist, final List<E> dlist)
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|E
extends|extends
name|Diff
operator|.
name|Element
argument_list|<
name|K
argument_list|>
parameter_list|>
name|Container
argument_list|<
name|E
argument_list|>
name|accessPrevious
parameter_list|(
specifier|final
name|K
name|name
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|clist
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|dlist
parameter_list|)
block|{
specifier|final
name|int
name|d
init|=
name|search
argument_list|(
name|dlist
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|>=
literal|0
condition|)
block|{
comment|// the element was in previous and was once deleted in current.
return|return
operator|new
name|Container
argument_list|<
name|E
argument_list|>
argument_list|(
name|dlist
operator|.
name|get
argument_list|(
name|d
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
specifier|final
name|int
name|c
init|=
name|search
argument_list|(
name|clist
argument_list|,
name|name
argument_list|)
decl_stmt|;
comment|// When c>= 0, the element in current is a newly created element.
return|return
name|c
operator|<
literal|0
condition|?
literal|null
else|:
operator|new
name|Container
argument_list|<
name|E
argument_list|>
argument_list|(
literal|null
argument_list|)
return|;
block|}
block|}
comment|/**    * Find an element in the current state.    *     * @return null if the element cannot be determined in the current state since    *         no change is recorded and it should be determined in the previous    *         state; otherwise, return a {@link Container} containing the element in    *         the current state. Note that the element can possibly be null which    *         means that the element is not found in the current state.    */
DECL|method|accessCurrent (K name)
specifier|public
name|Container
argument_list|<
name|E
argument_list|>
name|accessCurrent
parameter_list|(
name|K
name|name
parameter_list|)
block|{
return|return
name|accessPrevious
argument_list|(
name|name
argument_list|,
name|deleted
argument_list|,
name|created
argument_list|)
return|;
block|}
comment|/**    * Apply this diff to previous state in order to obtain current state.    * @return the current state of the list.    */
DECL|method|apply2Previous (final List<E> previous)
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|apply2Previous
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|previous
parameter_list|)
block|{
return|return
name|apply2Previous
argument_list|(
name|previous
argument_list|,
name|getCreatedUnmodifiable
argument_list|()
argument_list|,
name|getDeletedUnmodifiable
argument_list|()
argument_list|)
return|;
block|}
DECL|method|apply2Previous ( final List<E> previous, final List<E> clist, final List<E> dlist)
specifier|private
specifier|static
parameter_list|<
name|K
parameter_list|,
name|E
extends|extends
name|Diff
operator|.
name|Element
argument_list|<
name|K
argument_list|>
parameter_list|>
name|List
argument_list|<
name|E
argument_list|>
name|apply2Previous
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|previous
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|clist
parameter_list|,
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|dlist
parameter_list|)
block|{
comment|// Assumptions:
comment|// (A1) All lists are sorted.
comment|// (A2) All elements in dlist must be in previous.
comment|// (A3) All elements in clist must be not in tmp = previous - dlist.
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|tmp
init|=
operator|new
name|ArrayList
argument_list|<
name|E
argument_list|>
argument_list|(
name|previous
operator|.
name|size
argument_list|()
operator|-
name|dlist
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
block|{
comment|// tmp = previous - dlist
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|i
init|=
name|previous
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|E
name|deleted
range|:
name|dlist
control|)
block|{
name|E
name|e
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//since dlist is non-empty, e must exist by (A2).
name|int
name|cmp
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
operator|(
name|cmp
operator|=
name|e
operator|.
name|compareTo
argument_list|(
name|deleted
operator|.
name|getKey
argument_list|()
argument_list|)
operator|)
operator|<
literal|0
condition|;
name|e
operator|=
name|i
operator|.
name|next
argument_list|()
control|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
name|Preconditions
operator|.
name|checkState
argument_list|(
name|cmp
operator|==
literal|0
argument_list|)
expr_stmt|;
comment|// check (A2)
block|}
for|for
control|(
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|current
init|=
operator|new
name|ArrayList
argument_list|<
name|E
argument_list|>
argument_list|(
name|tmp
operator|.
name|size
argument_list|()
operator|+
name|clist
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
block|{
comment|// current = tmp + clist
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|tmpIterator
init|=
name|tmp
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|cIterator
init|=
name|clist
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|E
name|t
init|=
name|tmpIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|tmpIterator
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|E
name|c
init|=
name|cIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|cIterator
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
for|for
control|(
init|;
name|t
operator|!=
literal|null
operator|||
name|c
operator|!=
literal|null
condition|;
control|)
block|{
specifier|final
name|int
name|cmp
init|=
name|c
operator|==
literal|null
condition|?
literal|1
else|:
name|t
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|c
operator|.
name|compareTo
argument_list|(
name|t
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
name|current
operator|.
name|add
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|cIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|cIterator
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
name|current
operator|.
name|add
argument_list|(
name|t
argument_list|)
expr_stmt|;
name|t
operator|=
name|tmpIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|tmpIterator
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"Violated assumption (A3)."
argument_list|)
throw|;
block|}
block|}
block|}
return|return
name|current
return|;
block|}
comment|/**    * Apply the reverse of this diff to current state in order    * to obtain the previous state.    * @return the previous state of the list.    */
DECL|method|apply2Current (final List<E> current)
specifier|public
name|List
argument_list|<
name|E
argument_list|>
name|apply2Current
parameter_list|(
specifier|final
name|List
argument_list|<
name|E
argument_list|>
name|current
parameter_list|)
block|{
return|return
name|apply2Previous
argument_list|(
name|current
argument_list|,
name|getDeletedUnmodifiable
argument_list|()
argument_list|,
name|getCreatedUnmodifiable
argument_list|()
argument_list|)
return|;
block|}
comment|/**    * Combine this diff with a posterior diff.  We have the following cases:    *     *<pre>    * 1. For (c, 0) in the posterior diff, check the element in this diff:    * 1.1 (c', 0)  in this diff: impossible    * 1.2 (0, d')  in this diff: put in c-list --&gt; (c, d')    * 1.3 (c', d') in this diff: impossible    * 1.4 (0, 0)   in this diff: put in c-list --&gt; (c, 0)    * This is the same logic as create(E).    *     * 2. For (0, d) in the posterior diff,    * 2.1 (c', 0)  in this diff: remove from c-list --&gt; (0, 0)    * 2.2 (0, d')  in this diff: impossible    * 2.3 (c', d') in this diff: remove from c-list --&gt; (0, d')    * 2.4 (0, 0)   in this diff: put in d-list --&gt; (0, d)    * This is the same logic as delete(E).    *     * 3. For (c, d) in the posterior diff,    * 3.1 (c', 0)  in this diff: replace the element in c-list --&gt; (c, 0)    * 3.2 (0, d')  in this diff: impossible    * 3.3 (c', d') in this diff: replace the element in c-list --&gt; (c, d')    * 3.4 (0, 0)   in this diff: put in c-list and d-list --&gt; (c, d)    * This is the same logic as modify(E, E).    *</pre>    *     * @param posterior The posterior diff to combine with.    * @param deletedProcesser    *     process the deleted/overwritten elements in case 2.1, 2.3, 3.1 and 3.3.    */
DECL|method|combinePosterior (final Diff<K, E> posterior, final Processor<E> deletedProcesser)
specifier|public
name|void
name|combinePosterior
parameter_list|(
specifier|final
name|Diff
argument_list|<
name|K
argument_list|,
name|E
argument_list|>
name|posterior
parameter_list|,
specifier|final
name|Processor
argument_list|<
name|E
argument_list|>
name|deletedProcesser
parameter_list|)
block|{
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|createdIterator
init|=
name|posterior
operator|.
name|getCreatedUnmodifiable
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
specifier|final
name|Iterator
argument_list|<
name|E
argument_list|>
name|deletedIterator
init|=
name|posterior
operator|.
name|getDeletedUnmodifiable
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
name|E
name|c
init|=
name|createdIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|createdIterator
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
name|E
name|d
init|=
name|deletedIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|deletedIterator
operator|.
name|next
argument_list|()
else|:
literal|null
decl_stmt|;
for|for
control|(
init|;
name|c
operator|!=
literal|null
operator|||
name|d
operator|!=
literal|null
condition|;
control|)
block|{
specifier|final
name|int
name|cmp
init|=
name|c
operator|==
literal|null
condition|?
literal|1
else|:
name|d
operator|==
literal|null
condition|?
operator|-
literal|1
else|:
name|c
operator|.
name|compareTo
argument_list|(
name|d
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|<
literal|0
condition|)
block|{
comment|// case 1: only in c-list
name|create
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|c
operator|=
name|createdIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|createdIterator
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
comment|// case 2: only in d-list
specifier|final
name|UndoInfo
argument_list|<
name|E
argument_list|>
name|ui
init|=
name|delete
argument_list|(
name|d
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletedProcesser
operator|!=
literal|null
condition|)
block|{
name|deletedProcesser
operator|.
name|process
argument_list|(
name|ui
operator|.
name|trashed
argument_list|)
expr_stmt|;
block|}
name|d
operator|=
name|deletedIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|deletedIterator
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// case 3: in both c-list and d-list
specifier|final
name|UndoInfo
argument_list|<
name|E
argument_list|>
name|ui
init|=
name|modify
argument_list|(
name|d
argument_list|,
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|deletedProcesser
operator|!=
literal|null
condition|)
block|{
name|deletedProcesser
operator|.
name|process
argument_list|(
name|ui
operator|.
name|trashed
argument_list|)
expr_stmt|;
block|}
name|c
operator|=
name|createdIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|createdIterator
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
name|d
operator|=
name|deletedIterator
operator|.
name|hasNext
argument_list|()
condition|?
name|deletedIterator
operator|.
name|next
argument_list|()
else|:
literal|null
expr_stmt|;
block|}
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
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"{created="
operator|+
name|getCreatedUnmodifiable
argument_list|()
operator|+
literal|", deleted="
operator|+
name|getDeletedUnmodifiable
argument_list|()
operator|+
literal|"}"
return|;
block|}
block|}
end_class

end_unit

