begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.common
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
name|timelineservice
operator|.
name|storage
operator|.
name|common
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
name|hbase
operator|.
name|client
operator|.
name|Result
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
name|yarn
operator|.
name|server
operator|.
name|timelineservice
operator|.
name|storage
operator|.
name|flow
operator|.
name|Attribute
import|;
end_import

begin_comment
comment|/**  * A Column represents the way to store a fully qualified column in a specific  * table.  */
end_comment

begin_interface
DECL|interface|Column
specifier|public
interface|interface
name|Column
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Sends a Mutation to the table. The mutations will be buffered and sent over    * the wire as part of a batch.    *    * @param rowKey identifying the row to write. Nothing gets written when null.    * @param tableMutator used to modify the underlying HBase table. Caller is    *          responsible to pass a mutator for the table that actually has this    *          column.    * @param timestamp version timestamp. When null the server timestamp will be    *          used.    * @param attributes Map of attributes for this mutation. used in the    *     coprocessor to set/read the cell tags. Can be null.    * @param inputValue the value to write to the rowKey and column qualifier.    *          Nothing gets written when null.    * @throws IOException if there is any exception encountered during store.    */
DECL|method|store (byte[] rowKey, TypedBufferedMutator<T> tableMutator, Long timestamp, Object inputValue, Attribute... attributes)
name|void
name|store
parameter_list|(
name|byte
index|[]
name|rowKey
parameter_list|,
name|TypedBufferedMutator
argument_list|<
name|T
argument_list|>
name|tableMutator
parameter_list|,
name|Long
name|timestamp
parameter_list|,
name|Object
name|inputValue
parameter_list|,
name|Attribute
modifier|...
name|attributes
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Get the latest version of this specified column. Note: this call clones the    * value content of the hosting {@link org.apache.hadoop.hbase.Cell Cell}.    *    * @param result Cannot be null    * @return result object (can be cast to whatever object was written to), or    *         null when result doesn't contain this column.    * @throws IOException if there is any exception encountered while reading    *     result.    */
DECL|method|readResult (Result result)
name|Object
name|readResult
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns column family name(as bytes) associated with this column.    * @return a byte array encoding column family for this column qualifier.    */
DECL|method|getColumnFamilyBytes ()
name|byte
index|[]
name|getColumnFamilyBytes
parameter_list|()
function_decl|;
comment|/**    * Get byte representation for this column qualifier.    * @return a byte array representing column qualifier.    */
DECL|method|getColumnQualifierBytes ()
name|byte
index|[]
name|getColumnQualifierBytes
parameter_list|()
function_decl|;
comment|/**    * Returns value converter implementation associated with this column.    * @return a {@link ValueConverter} implementation.    */
DECL|method|getValueConverter ()
name|ValueConverter
name|getValueConverter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

