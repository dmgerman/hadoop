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
name|File
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
name|ArrayList
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
comment|/**  * The DBStore interface provides the ability to create Tables, which store  * a specific type of Key-Value pair. Some DB interfaces like LevelDB will not  * be able to do this. In those case a Table creation will map to a default  * store.  *  */
end_comment

begin_interface
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|interface|DBStore
specifier|public
interface|interface
name|DBStore
extends|extends
name|AutoCloseable
block|{
comment|/**    * Gets an existing TableStore.    *    * @param name - Name of the TableStore to get    * @return - TableStore.    * @throws IOException on Failure    */
DECL|method|getTable (String name)
name|Table
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Gets an existing TableStore with implicit key/value conversion.    *    * @param name - Name of the TableStore to get    * @param keyType    * @param valueType    * @return - TableStore.    * @throws IOException on Failure    */
DECL|method|getTable (String name, Class<KEY> keyType, Class<VALUE> valueType)
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|getTable
parameter_list|(
name|String
name|name
parameter_list|,
name|Class
argument_list|<
name|KEY
argument_list|>
name|keyType
parameter_list|,
name|Class
argument_list|<
name|VALUE
argument_list|>
name|valueType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Lists the Known list of Tables in a DB.    *    * @return List of Tables, in case of Rocks DB and LevelDB we will return at    * least one entry called DEFAULT.    * @throws IOException on Failure    */
DECL|method|listTables ()
name|ArrayList
argument_list|<
name|Table
argument_list|>
name|listTables
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Flush the DB buffer onto persistent storage.    * @throws IOException    */
DECL|method|flush ()
name|void
name|flush
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Compact the entire database.    *    * @throws IOException on Failure    */
DECL|method|compactDB ()
name|void
name|compactDB
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Moves a key from the Source Table to the destination Table.    *    * @param key - Key to move.    * @param source - Source Table.    * @param dest - Destination Table.    * @throws IOException on Failure    */
DECL|method|move (KEY key, Table<KEY, VALUE> source, Table<KEY, VALUE> dest)
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|void
name|move
parameter_list|(
name|KEY
name|key
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|source
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|dest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Moves a key from the Source Table to the destination Table and updates the    * destination to the new value.    *    * @param key - Key to move.    * @param value - new value to write to the destination table.    * @param source - Source Table.    * @param dest - Destination Table.    * @throws IOException on Failure    */
DECL|method|move (KEY key, VALUE value, Table<KEY, VALUE> source, Table<KEY, VALUE> dest)
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|void
name|move
parameter_list|(
name|KEY
name|key
parameter_list|,
name|VALUE
name|value
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|source
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|dest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Moves a key from the Source Table to the destination Table and updates the    * destination with the new key name and value.    * This is similar to deleting an entry in one table and adding an entry in    * another table, here it is done atomically.    *    * @param sourceKey - Key to move.    * @param destKey - Destination key name.    * @param value - new value to write to the destination table.    * @param source - Source Table.    * @param dest - Destination Table.    * @throws IOException on Failure    */
DECL|method|move (KEY sourceKey, KEY destKey, VALUE value, Table<KEY, VALUE> source, Table<KEY, VALUE> dest)
parameter_list|<
name|KEY
parameter_list|,
name|VALUE
parameter_list|>
name|void
name|move
parameter_list|(
name|KEY
name|sourceKey
parameter_list|,
name|KEY
name|destKey
parameter_list|,
name|VALUE
name|value
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|source
parameter_list|,
name|Table
argument_list|<
name|KEY
argument_list|,
name|VALUE
argument_list|>
name|dest
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns an estimated count of keys in this DB.    *    * @return long, estimate of keys in the DB.    */
DECL|method|getEstimatedKeyCount ()
name|long
name|getEstimatedKeyCount
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Initialize an atomic batch operation which can hold multiple PUT/DELETE    * operations and committed later in one step.    *    * @return BatchOperation holder which can be used to add or commit batch    * operations.    */
DECL|method|initBatchOperation ()
name|BatchOperation
name|initBatchOperation
parameter_list|()
function_decl|;
comment|/**    * Commit the batch operations.    *    * @param operation which contains all the required batch operation.    * @throws IOException on Failure.    */
DECL|method|commitBatchOperation (BatchOperation operation)
name|void
name|commitBatchOperation
parameter_list|(
name|BatchOperation
name|operation
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get current snapshot of OM DB store as an artifact stored on    * the local filesystem.    * @return An object that encapsulates the checkpoint information along with    * location.    */
DECL|method|getCheckpoint (boolean flush)
name|DBCheckpoint
name|getCheckpoint
parameter_list|(
name|boolean
name|flush
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get DB Store location.    * @return DB file location.    */
DECL|method|getDbLocation ()
name|File
name|getDbLocation
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

