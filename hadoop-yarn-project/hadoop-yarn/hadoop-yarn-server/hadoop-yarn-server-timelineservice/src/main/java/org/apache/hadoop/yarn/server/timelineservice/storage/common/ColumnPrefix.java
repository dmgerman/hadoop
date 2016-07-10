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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|NavigableMap
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
comment|/**  * Used to represent a partially qualified column, where the actual column name  * will be composed of a prefix and the remainder of the column qualifier. The  * prefix can be null, in which case the column qualifier will be completely  * determined when the values are stored.  */
end_comment

begin_interface
DECL|interface|ColumnPrefix
specifier|public
interface|interface
name|ColumnPrefix
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**    * Sends a Mutation to the table. The mutations will be buffered and sent over    * the wire as part of a batch.    *    * @param rowKey identifying the row to write. Nothing gets written when null.    * @param tableMutator used to modify the underlying HBase table. Caller is    *          responsible to pass a mutator for the table that actually has this    *          column.    * @param qualifier column qualifier. Nothing gets written when null.    * @param timestamp version timestamp. When null the server timestamp will be    *          used.    * @param attributes attributes for the mutation that are used by the    *          coprocessor to set/read the cell tags.    * @param inputValue the value to write to the rowKey and column qualifier.    *          Nothing gets written when null.    * @throws IOException if there is any exception encountered while doing    *     store operation(sending mutation to the table).    */
DECL|method|store (byte[] rowKey, TypedBufferedMutator<T> tableMutator, byte[] qualifier, Long timestamp, Object inputValue, Attribute... attributes)
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
name|byte
index|[]
name|qualifier
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
comment|/**    * Sends a Mutation to the table. The mutations will be buffered and sent over    * the wire as part of a batch.    *    * @param rowKey identifying the row to write. Nothing gets written when null.    * @param tableMutator used to modify the underlying HBase table. Caller is    *          responsible to pass a mutator for the table that actually has this    *          column.    * @param qualifier column qualifier. Nothing gets written when null.    * @param timestamp version timestamp. When null the server timestamp will be    *          used.    * @param attributes attributes for the mutation that are used by the    *          coprocessor to set/read the cell tags.    * @param inputValue the value to write to the rowKey and column qualifier.    *          Nothing gets written when null.    * @throws IOException if there is any exception encountered while doing    *     store operation(sending mutation to the table).    */
DECL|method|store (byte[] rowKey, TypedBufferedMutator<T> tableMutator, String qualifier, Long timestamp, Object inputValue, Attribute... attributes)
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
name|String
name|qualifier
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
comment|/**    * Get the latest version of this specified column. Note: this call clones the    * value content of the hosting {@link org.apache.hadoop.hbase.Cell Cell}.    *    * @param result Cannot be null    * @param qualifier column qualifier. Nothing gets read when null.    * @return result object (can be cast to whatever object was written to) or    *         null when specified column qualifier for this prefix doesn't exist    *         in the result.    * @throws IOException if there is any exception encountered while reading    *     result.    */
DECL|method|readResult (Result result, String qualifier)
name|Object
name|readResult
parameter_list|(
name|Result
name|result
parameter_list|,
name|String
name|qualifier
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    *    * @param<K> identifies the type of key converter.    * @param result from which to read columns.    * @param keyConverter used to convert column bytes to the appropriate key    *          type    * @return the latest values of columns in the column family with this prefix    *         (or all of them if the prefix value is null).    * @throws IOException if there is any exception encountered while reading    *           results.    */
DECL|method|readResults (Result result, KeyConverter<K> keyConverter)
parameter_list|<
name|K
parameter_list|>
name|Map
argument_list|<
name|K
argument_list|,
name|Object
argument_list|>
name|readResults
parameter_list|(
name|Result
name|result
parameter_list|,
name|KeyConverter
argument_list|<
name|K
argument_list|>
name|keyConverter
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @param result from which to reads data with timestamps.    * @param<K> identifies the type of key converter.    * @param<V> the type of the values. The values will be cast into that type.    * @param keyConverter used to convert column bytes to the appropriate key    *     type.    * @return the cell values at each respective time in for form    *         {@literal {idA={timestamp1->value1}, idA={timestamp2->value2},    *         idB={timestamp3->value3}, idC={timestamp1->value4}}}    * @throws IOException if there is any exception encountered while reading    *     result.    */
DECL|method|readResultsWithTimestamps ( Result result, KeyConverter<K> keyConverter)
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
name|NavigableMap
argument_list|<
name|K
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
argument_list|>
name|readResultsWithTimestamps
parameter_list|(
name|Result
name|result
parameter_list|,
name|KeyConverter
argument_list|<
name|K
argument_list|>
name|keyConverter
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * @param qualifierPrefix Column qualifier or prefix of qualifier.    * @return a byte array encoding column prefix and qualifier/prefix passed.    */
DECL|method|getColumnPrefixBytes (String qualifierPrefix)
name|byte
index|[]
name|getColumnPrefixBytes
parameter_list|(
name|String
name|qualifierPrefix
parameter_list|)
function_decl|;
comment|/**    * @param qualifierPrefix Column qualifier or prefix of qualifier.    * @return a byte array encoding column prefix and qualifier/prefix passed.    */
DECL|method|getColumnPrefixBytes (byte[] qualifierPrefix)
name|byte
index|[]
name|getColumnPrefixBytes
parameter_list|(
name|byte
index|[]
name|qualifierPrefix
parameter_list|)
function_decl|;
comment|/**    * Returns column family name(as bytes) associated with this column prefix.    * @return a byte array encoding column family for this prefix.    */
DECL|method|getColumnFamilyBytes ()
name|byte
index|[]
name|getColumnFamilyBytes
parameter_list|()
function_decl|;
comment|/**    * Returns value converter implementation associated with this column prefix.    * @return a {@link ValueConverter} implementation.    */
DECL|method|getValueConverter ()
name|ValueConverter
name|getValueConverter
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

