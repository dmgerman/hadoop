begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
package|;
end_package

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
name|tuple
operator|.
name|ImmutablePair
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
name|MetadataKeyFilters
operator|.
name|MetadataKeyFilter
import|;
end_import

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
name|io
operator|.
name|IOException
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
name|Map
import|;
end_import

begin_comment
comment|/**  * Interface for key-value store that stores ozone metadata.  * Ozone metadata is stored as key value pairs, both key and value  * are arbitrary byte arrays.  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|MetadataStore
specifier|public
interface|interface
name|MetadataStore
extends|extends
name|Closeable
block|{
comment|/**    * Puts a key-value pair into the store.    *    * @param key metadata key    * @param value metadata value    */
DECL|method|put (byte[] key, byte[] value)
name|void
name|put
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @return true if the metadata store is empty.    *    * @throws IOException    */
DECL|method|isEmpty ()
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the value mapped to the given key in byte array.    *    * @param key metadata key    * @return value in byte array    * @throws IOException    */
DECL|method|get (byte[] key)
name|byte
index|[]
name|get
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Deletes a key from the metadata store.    *    * @param key metadata key    * @throws IOException    */
DECL|method|delete (byte[] key)
name|void
name|delete
parameter_list|(
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns a certain range of key value pairs as a list based on a    * startKey or count. Further a {@link MetadataKeyFilter} can be added to    * filter keys if necessary. To prevent race conditions while listing    * entries, this implementation takes a snapshot and lists the entries from    * the snapshot. This may, on the other hand, cause the range result slight    * different with actual data if data is updating concurrently.    *<p>    * If the startKey is specified and found in levelDB, this key and the keys    * after this key will be included in the result. If the startKey is null    * all entries will be included as long as other conditions are satisfied.    * If the given startKey doesn't exist, an IOException will be thrown.    *<p>    * The count argument is to limit number of total entries to return,    * the value for count must be an integer greater than 0.    *<p>    * This method allows to specify one or more {@link MetadataKeyFilter}    * to filter keys by certain condition. Once given, only the entries    * whose key passes all the filters will be included in the result.    *    * @param startKey a start key.    * @param count max number of entries to return.    * @param filters customized one or more {@link MetadataKeyFilter}.    * @return a list of entries found in the database.    * @throws IOException if an invalid startKey is given or other I/O errors.    * @throws IllegalArgumentException if count is less than 0.    */
DECL|method|getRangeKVs (byte[] startKey, int count, MetadataKeyFilter... filters)
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|getRangeKVs
parameter_list|(
name|byte
index|[]
name|startKey
parameter_list|,
name|int
name|count
parameter_list|,
name|MetadataKeyFilter
modifier|...
name|filters
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
function_decl|;
comment|/**    * This method is very similar with    * {@link #getRangeKVs(byte[], int, MetadataKeyFilter...)}, the only    * different is this method is supposed to return a sequential range    * of elements based on the filters. While iterating the elements,    * if it met any entry that cannot pass the filter, the iterator will stop    * from this point without looking for next match. If no filter is given,    * this method behaves just like    * {@link #getRangeKVs(byte[], int, MetadataKeyFilter...)}.    *    * @param startKey a start key.    * @param count max number of entries to return.    * @param filters customized one or more {@link MetadataKeyFilter}.    * @return a list of entries found in the database.    * @throws IOException    * @throws IllegalArgumentException    */
DECL|method|getSequentialRangeKVs (byte[] startKey, int count, MetadataKeyFilter... filters)
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|getSequentialRangeKVs
parameter_list|(
name|byte
index|[]
name|startKey
parameter_list|,
name|int
name|count
parameter_list|,
name|MetadataKeyFilter
modifier|...
name|filters
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
function_decl|;
comment|/**    * A batch of PUT, DELETE operations handled as a single atomic write.    *    * @throws IOException write fails    */
DECL|method|writeBatch (BatchOperation operation)
name|void
name|writeBatch
parameter_list|(
name|BatchOperation
name|operation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Compact the entire database.    * @throws IOException    */
DECL|method|compactDB ()
name|void
name|compactDB
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Destroy the content of the specified database,    * a destroyed database will not be able to load again.    * Be very careful with this method.    *    * @throws IOException if I/O error happens    */
DECL|method|destroy ()
name|void
name|destroy
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Seek the database to a certain key, returns the key-value    * pairs around this key based on the given offset. Note, this method    * can only support offset -1 (left), 0 (current) and 1 (right),    * any other offset given will cause a {@link IllegalArgumentException}.    *    * @param offset offset to the key    * @param from from which key    * @return a key-value pair    * @throws IOException    */
DECL|method|peekAround (int offset, byte[] from)
name|ImmutablePair
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|peekAround
parameter_list|(
name|int
name|offset
parameter_list|,
name|byte
index|[]
name|from
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalArgumentException
function_decl|;
comment|/**    * Iterates entries in the database from a certain key.    * Applies the given {@link EntryConsumer} to the key and value of    * each entry, the function produces a boolean result which is used    * as the criteria to exit from iteration.    *    * @param from the start key    * @param consumer    *   a {@link EntryConsumer} applied to each key and value. If the consumer    *   returns true, continues the iteration to next entry; otherwise exits    *   the iteration.    * @throws IOException    */
DECL|method|iterate (byte[] from, EntryConsumer consumer)
name|void
name|iterate
parameter_list|(
name|byte
index|[]
name|from
parameter_list|,
name|EntryConsumer
name|consumer
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

