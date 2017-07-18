begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|Collection
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
name|InterfaceAudience
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

begin_comment
comment|/**  * A {@link GSet} is set,  * which supports the {@link #get(Object)} operation.  * The {@link #get(Object)} operation uses a key to lookup an element.  *   * Null element is not supported.  *   * @param<K> The type of the keys.  * @param<E> The type of the elements, which must be a subclass of the keys.  */
end_comment

begin_interface
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|GSet
specifier|public
interface|interface
name|GSet
parameter_list|<
name|K
parameter_list|,
name|E
extends|extends
name|K
parameter_list|>
extends|extends
name|Iterable
argument_list|<
name|E
argument_list|>
block|{
DECL|field|LOG
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|GSet
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**    * @return The size of this set.    */
DECL|method|size ()
name|int
name|size
parameter_list|()
function_decl|;
comment|/**    * Does this set contain an element corresponding to the given key?    * @param key The given key.    * @return true if the given key equals to a stored element.    *         Otherwise, return false.    * @throws NullPointerException if key == null.    */
DECL|method|contains (K key)
name|boolean
name|contains
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**    * Return the stored element which is equal to the given key.    * This operation is similar to {@link java.util.Map#get(Object)}.    * @param key The given key.    * @return The stored element if it exists.    *         Otherwise, return null.    * @throws NullPointerException if key == null.    */
DECL|method|get (K key)
name|E
name|get
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**    * Add/replace an element.    * If the element does not exist, add it to the set.    * Otherwise, replace the existing element.    *    * Note that this operation    * is similar to {@link java.util.Map#put(Object, Object)}    * but is different from {@link java.util.Set#add(Object)}    * which does not replace the existing element if there is any.    *    * @param element The element being put.    * @return the previous stored element if there is any.    *         Otherwise, return null.    * @throws NullPointerException if element == null.    */
DECL|method|put (E element)
name|E
name|put
parameter_list|(
name|E
name|element
parameter_list|)
function_decl|;
comment|/**    * Remove the element corresponding to the given key.     * This operation is similar to {@link java.util.Map#remove(Object)}.    * @param key The key of the element being removed.    * @return If such element exists, return it.    *         Otherwise, return null.      * @throws NullPointerException if key == null.   */
DECL|method|remove (K key)
name|E
name|remove
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**    * Clear the set.    */
DECL|method|clear ()
name|void
name|clear
parameter_list|()
function_decl|;
comment|/**    * Returns a {@link Collection} view of the values contained in this set.    * The collection is backed by the set, so changes to the set are    * reflected in the collection, and vice-versa.    *    * @return the collection of values.    */
DECL|method|values ()
name|Collection
argument_list|<
name|E
argument_list|>
name|values
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

