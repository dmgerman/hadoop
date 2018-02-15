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

begin_comment
comment|/**  * This interface defines the methods used to store and manage InodeDiffs.  * @param<T> Type of the object in this list.  */
end_comment

begin_interface
DECL|interface|DiffList
specifier|public
interface|interface
name|DiffList
parameter_list|<
name|T
extends|extends
name|Comparable
parameter_list|<
name|Integer
parameter_list|>
parameter_list|>
extends|extends
name|Iterable
argument_list|<
name|T
argument_list|>
block|{
DECL|field|EMPTY_LIST
name|DiffList
name|EMPTY_LIST
init|=
operator|new
name|DiffListByArrayList
argument_list|(
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
decl_stmt|;
comment|/**    * Returns an empty DiffList.    */
DECL|method|emptyList ()
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|Integer
argument_list|>
parameter_list|>
name|DiffList
argument_list|<
name|T
argument_list|>
name|emptyList
parameter_list|()
block|{
return|return
name|EMPTY_LIST
return|;
block|}
comment|/**    * Returns an unmodifiable diffList.    * @param diffs DiffList    * @param<T> Type of the object in the the diffList    * @return Unmodifiable diffList    */
DECL|method|unmodifiableList ( DiffList<T> diffs)
specifier|static
parameter_list|<
name|T
extends|extends
name|Comparable
argument_list|<
name|Integer
argument_list|>
parameter_list|>
name|DiffList
argument_list|<
name|T
argument_list|>
name|unmodifiableList
parameter_list|(
name|DiffList
argument_list|<
name|T
argument_list|>
name|diffs
parameter_list|)
block|{
return|return
operator|new
name|DiffList
argument_list|<
name|T
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|T
name|get
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|diffs
operator|.
name|get
argument_list|(
name|i
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|diffs
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|diffs
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|remove
parameter_list|(
name|int
name|i
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This list is unmodifiable."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|addLast
parameter_list|(
name|T
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This list is unmodifiable."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addFirst
parameter_list|(
name|T
name|t
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"This list is unmodifiable."
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|binarySearch
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|diffs
operator|.
name|binarySearch
argument_list|(
name|i
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|diffs
operator|.
name|iterator
argument_list|()
return|;
block|}
block|}
return|;
block|}
comment|/**    * Returns the element at the specified position in this list.    *    * @param index index of the element to return    * @return the element at the specified position in this list    * @throws IndexOutOfBoundsException if the index is out of range    *         (<tt>index&lt; 0 || index&gt;= size()</tt>)    */
DECL|method|get (int index)
name|T
name|get
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**    * Returns true if this list contains no elements.    *    * @return true if this list contains no elements    */
DECL|method|isEmpty ()
name|boolean
name|isEmpty
parameter_list|()
function_decl|;
comment|/**    * Returns the number of elements in this list.    * @return the number of elements in this list.    */
DECL|method|size ()
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Removes the element at the specified position in this list.    * @param index the index of the element to be removed    * @return the element previously at the specified position    */
DECL|method|remove (int index)
name|T
name|remove
parameter_list|(
name|int
name|index
parameter_list|)
function_decl|;
comment|/**    * Adds an element at the end of the list.    * @param t element to be appended to this list    * @return true, if insertion is successful    */
DECL|method|addLast (T t)
name|boolean
name|addLast
parameter_list|(
name|T
name|t
parameter_list|)
function_decl|;
comment|/**    * Adds an element at the beginning of the list.    * @param t element to be added to this list    */
DECL|method|addFirst (T t)
name|void
name|addFirst
parameter_list|(
name|T
name|t
parameter_list|)
function_decl|;
comment|/**    * Searches the list for the specified object using the binary    * search algorithm.    * @param key key to be searched for    * @return the index of the search key, if it is contained in the list    *         otherwise, (-insertion point - 1).    */
DECL|method|binarySearch (int key)
name|int
name|binarySearch
parameter_list|(
name|int
name|key
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

