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
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceStability
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
extends|extends
name|AutoCloseable
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
comment|/**    * Puts a key-value pair into the store as part of a bath operation.    *    * @param batch the batch operation    * @param key metadata key    * @param value metadata value    */
DECL|method|putWithBatch (BatchOperation batch, byte[] key, byte[] value)
name|void
name|putWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
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
comment|/**    * @return true if the metadata store is empty.    * @throws IOException on Failure    */
DECL|method|isEmpty ()
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the value mapped to the given key in byte array or returns null    * if the key is not found.    *    * @param key metadata key    * @return value in byte array or null if the key is not found.    * @throws IOException on Failure    */
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
comment|/**    * Deletes a key from the metadata store.    *    * @param key metadata key    * @throws IOException on Failure    */
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
comment|/**    * Deletes a key from the metadata store as part of a batch operation.    *    * @param batch the batch operation    * @param key metadata key    * @throws IOException on Failure    */
DECL|method|deleteWithBatch (BatchOperation batch, byte[] key)
name|void
name|deleteWithBatch
parameter_list|(
name|BatchOperation
name|batch
parameter_list|,
name|byte
index|[]
name|key
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the iterator for this metadata store.    *    * @return MetaStoreIterator    */
DECL|method|iterator ()
name|TableIterator
argument_list|<
name|KeyValue
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
comment|/**    * Class used to represent the key and value pair of a db entry.    */
DECL|class|KeyValue
class|class
name|KeyValue
block|{
DECL|field|key
specifier|private
specifier|final
name|byte
index|[]
name|key
decl_stmt|;
DECL|field|value
specifier|private
specifier|final
name|byte
index|[]
name|value
decl_stmt|;
comment|/**      * KeyValue Constructor, used to represent a key and value of a db entry.      *      * @param key - Key Bytes      * @param value - Value bytes      */
DECL|method|KeyValue (byte[] key, byte[] value)
specifier|private
name|KeyValue
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Create a KeyValue pair.      *      * @param key - Key Bytes      * @param value - Value bytes      * @return KeyValue object.      */
DECL|method|create (byte[] key, byte[] value)
specifier|public
specifier|static
name|KeyValue
name|create
parameter_list|(
name|byte
index|[]
name|key
parameter_list|,
name|byte
index|[]
name|value
parameter_list|)
block|{
return|return
operator|new
name|KeyValue
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
return|;
block|}
comment|/**      * Return key.      *      * @return byte[]      */
DECL|method|getKey ()
specifier|public
name|byte
index|[]
name|getKey
parameter_list|()
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|key
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|key
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|key
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * Return value.      *      * @return byte[]      */
DECL|method|getValue ()
specifier|public
name|byte
index|[]
name|getValue
parameter_list|()
block|{
name|byte
index|[]
name|result
init|=
operator|new
name|byte
index|[
name|value
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|value
argument_list|,
literal|0
argument_list|,
name|result
argument_list|,
literal|0
argument_list|,
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
block|}
block|}
end_interface

end_unit

