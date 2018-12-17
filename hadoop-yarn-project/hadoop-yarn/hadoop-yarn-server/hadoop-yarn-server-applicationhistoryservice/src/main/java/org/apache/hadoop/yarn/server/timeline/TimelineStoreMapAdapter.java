begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timeline
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|timeline
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
comment|/**  * An adapter for map timeline store implementations  * @param<K> the type of the key set  * @param<V> the type of the value set  */
end_comment

begin_interface
DECL|interface|TimelineStoreMapAdapter
interface|interface
name|TimelineStoreMapAdapter
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
block|{
comment|/**    * @param key    * @return map(key)    */
DECL|method|get (K key)
name|V
name|get
parameter_list|(
name|K
name|key
parameter_list|)
function_decl|;
comment|/**    * Add mapping key->value in the map    * @param key    * @param value    */
DECL|method|put (K key, V value)
name|void
name|put
parameter_list|(
name|K
name|key
parameter_list|,
name|V
name|value
parameter_list|)
function_decl|;
comment|/**    * Remove mapping with key keyToRemove    * @param keyToRemove    */
DECL|method|remove (K keyToRemove)
name|void
name|remove
parameter_list|(
name|K
name|keyToRemove
parameter_list|)
function_decl|;
comment|/**    * @return the iterator of the value set of the map    */
DECL|method|valueSetIterator ()
name|CloseableIterator
argument_list|<
name|V
argument_list|>
name|valueSetIterator
parameter_list|()
function_decl|;
comment|/**    * Return the iterator of the value set of the map, starting from minV if type    * V is comparable.    * @param minV    * @return    */
DECL|method|valueSetIterator (V minV)
name|CloseableIterator
argument_list|<
name|V
argument_list|>
name|valueSetIterator
parameter_list|(
name|V
name|minV
parameter_list|)
function_decl|;
DECL|interface|CloseableIterator
interface|interface
name|CloseableIterator
parameter_list|<
name|V
parameter_list|>
extends|extends
name|Iterator
argument_list|<
name|V
argument_list|>
extends|,
name|Closeable
block|{}
block|}
end_interface

end_unit

