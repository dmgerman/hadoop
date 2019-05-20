begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils.db
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|db
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|NotImplementedException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheKey
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
name|utils
operator|.
name|db
operator|.
name|cache
operator|.
name|CacheValue
import|;
end_import

begin_comment
comment|/**  * Interface for key-value store that stores ozone metadata. Ozone metadata is  * stored as key value pairs, both key and value are arbitrary byte arrays. Each  * Table Stores a certain kind of keys and values. This allows a DB to have  * different kind of tables.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|Table
specifier|public
interface|interface
name|Table
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
extends|extends
name|AutoCloseable
block|{
comment|/**    * Puts a key-value pair into the store.    *    * @param key metadata key    * @param value metadata value    */
DECL|method|put (KEY key, VALUE value)
name|void
name|put
parameter_list|(
name|KEY
name|key
parameter_list|,
name|VALUE
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Puts a key-value pair into the store as part of a bath operation.    *    * @param batch the batch operation    * @param key metadata key    * @param value metadata value    */
DECL|method|putWithBatch (BatchOperation batch, KEY key, VALUE value)
name|void
name|putWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
name|KEY
name|key
parameter_list|,
name|VALUE
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return true if the metadata store is empty.    * @throws IOException on Failure    */
DECL|method|isEmpty ()
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the value mapped to the given key in byte array or returns null    * if the key is not found.    *    * @param key metadata key    * @return value in byte array or null if the key is not found.    * @throws IOException on Failure    */
DECL|method|get (KEY key)
name|VALUE
name|get
parameter_list|(
name|KEY
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a key from the metadata store.    *    * @param key metadata key    * @throws IOException on Failure    */
DECL|method|delete (KEY key)
name|void
name|delete
parameter_list|(
name|KEY
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a key from the metadata store as part of a batch operation.    *    * @param batch the batch operation    * @param key metadata key    * @throws IOException on Failure    */
DECL|method|deleteWithBatch (BatchOperation batch, KEY key)
name|void
name|deleteWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
name|KEY
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the iterator for this metadata store.    *    * @return MetaStoreIterator    */
DECL|method|iterator ()
name|TableIterator
argument_list|<
name|KEY
argument_list|,
name|?
extends|extends
name|KeyValue
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
argument_list|>
name|iterator
parameter_list|()
function_decl|;
comment|/**    * Returns the Name of this Table.    * @return - Table Name.    * @throws IOException on failure.    */
DECL|method|getName ()
name|String
name|getName
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Add entry to the table cache.    *    * If the cacheKey already exists, it will override the entry.    * @param cacheKey    * @param cacheValue    */
DECL|method|addCacheEntry (CacheKey<KEY> cacheKey, CacheValue<VALUE> cacheValue)
specifier|default
name|void
name|addCacheEntry
parameter_list|(
name|CacheKey
argument_list|<
name|KEY
argument_list|>
name|cacheKey
parameter_list|,
name|CacheValue
argument_list|<
name|VALUE
argument_list|>
name|cacheValue
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
literal|"addCacheEntry is not implemented"
argument_list|)
throw|;
block|}
comment|/**    * Removes all the entries from the table cache which are having epoch value    * less    * than or equal to specified epoch value.    * @param epoch    */
DECL|method|cleanupCache (long epoch)
specifier|default
name|void
name|cleanupCache
parameter_list|(
name|long
name|epoch
parameter_list|)
block|{
throw|throw
operator|new
name|NotImplementedException
argument_list|(
literal|"cleanupCache is not implemented"
argument_list|)
throw|;
block|}
comment|/**    * Class used to represent the key and value pair of a db entry.    */
DECL|interface|KeyValue
interface|interface
name|KeyValue
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
block|{
DECL|method|getKey ()
name|KEY
name|getKey
parameter_list|()
throws|throws
name|IOException
function_decl|;
DECL|method|getValue ()
name|VALUE
name|getValue
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_interface

end_unit

