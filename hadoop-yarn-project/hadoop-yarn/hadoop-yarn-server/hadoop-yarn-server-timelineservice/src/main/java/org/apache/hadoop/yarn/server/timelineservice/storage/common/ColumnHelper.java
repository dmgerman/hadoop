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
name|HashMap
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
name|Map
operator|.
name|Entry
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
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|Cell
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
name|Put
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
name|hbase
operator|.
name|util
operator|.
name|Bytes
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
name|timeline
operator|.
name|GenericObjectMapper
import|;
end_import

begin_comment
comment|/**  * This class is meant to be used only by explicit Columns, and not directly to  * write by clients.  *  * @param<T> refers to the table.  */
end_comment

begin_class
DECL|class|ColumnHelper
specifier|public
class|class
name|ColumnHelper
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|columnFamily
specifier|private
specifier|final
name|ColumnFamily
argument_list|<
name|T
argument_list|>
name|columnFamily
decl_stmt|;
comment|/**    * Local copy of bytes representation of columnFamily so that we can avoid    * cloning a new copy over and over.    */
DECL|field|columnFamilyBytes
specifier|private
specifier|final
name|byte
index|[]
name|columnFamilyBytes
decl_stmt|;
DECL|method|ColumnHelper (ColumnFamily<T> columnFamily)
specifier|public
name|ColumnHelper
parameter_list|(
name|ColumnFamily
argument_list|<
name|T
argument_list|>
name|columnFamily
parameter_list|)
block|{
name|this
operator|.
name|columnFamily
operator|=
name|columnFamily
expr_stmt|;
name|columnFamilyBytes
operator|=
name|columnFamily
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
comment|/**    * Sends a Mutation to the table. The mutations will be buffered and sent over    * the wire as part of a batch.    *    * @param rowKey identifying the row to write. Nothing gets written when null.    * @param tableMutator used to modify the underlying HBase table    * @param columnQualifier column qualifier. Nothing gets written when null.    * @param timestamp version timestamp. When null the server timestamp will be    *          used.    * @param inputValue the value to write to the rowKey and column qualifier.    *          Nothing gets written when null.    * @throws IOException    */
DECL|method|store (byte[] rowKey, TypedBufferedMutator<?> tableMutator, byte[] columnQualifier, Long timestamp, Object inputValue)
specifier|public
name|void
name|store
parameter_list|(
name|byte
index|[]
name|rowKey
parameter_list|,
name|TypedBufferedMutator
argument_list|<
name|?
argument_list|>
name|tableMutator
parameter_list|,
name|byte
index|[]
name|columnQualifier
parameter_list|,
name|Long
name|timestamp
parameter_list|,
name|Object
name|inputValue
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|(
name|rowKey
operator|==
literal|null
operator|)
operator|||
operator|(
name|columnQualifier
operator|==
literal|null
operator|)
operator|||
operator|(
name|inputValue
operator|==
literal|null
operator|)
condition|)
block|{
return|return;
block|}
name|Put
name|p
init|=
operator|new
name|Put
argument_list|(
name|rowKey
argument_list|)
decl_stmt|;
if|if
condition|(
name|timestamp
operator|==
literal|null
condition|)
block|{
name|p
operator|.
name|addColumn
argument_list|(
name|columnFamilyBytes
argument_list|,
name|columnQualifier
argument_list|,
name|GenericObjectMapper
operator|.
name|write
argument_list|(
name|inputValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|p
operator|.
name|addColumn
argument_list|(
name|columnFamilyBytes
argument_list|,
name|columnQualifier
argument_list|,
name|timestamp
argument_list|,
name|GenericObjectMapper
operator|.
name|write
argument_list|(
name|inputValue
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|tableMutator
operator|.
name|mutate
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the column family for this column implementation.    */
DECL|method|getColumnFamily ()
specifier|public
name|ColumnFamily
argument_list|<
name|T
argument_list|>
name|getColumnFamily
parameter_list|()
block|{
return|return
name|columnFamily
return|;
block|}
comment|/**    * Get the latest version of this specified column. Note: this call clones the    * value content of the hosting {@link Cell}.    *    * @param result from which to read the value. Cannot be null    * @param columnQualifierBytes referring to the column to be read.    * @return latest version of the specified column of whichever object was    *         written.    * @throws IOException    */
DECL|method|readResult (Result result, byte[] columnQualifierBytes)
specifier|public
name|Object
name|readResult
parameter_list|(
name|Result
name|result
parameter_list|,
name|byte
index|[]
name|columnQualifierBytes
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|result
operator|==
literal|null
operator|||
name|columnQualifierBytes
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Would have preferred to be able to use getValueAsByteBuffer and get a
comment|// ByteBuffer to avoid copy, but GenericObjectMapper doesn't seem to like
comment|// that.
name|byte
index|[]
name|value
init|=
name|result
operator|.
name|getValue
argument_list|(
name|columnFamilyBytes
argument_list|,
name|columnQualifierBytes
argument_list|)
decl_stmt|;
return|return
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/**    * @param result from which to reads data with timestamps    * @param columnPrefixBytes optional prefix to limit columns. If null all    *          columns are returned.    * @param<V> the type of the values. The values will be cast into that type.    * @return the cell values at each respective time in for form    *         {idA={timestamp1->value1}, idA={timestamp2->value2},    *         idB={timestamp3->value3}, idC={timestamp1->value4}}    * @throws IOException    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
parameter_list|<
name|V
parameter_list|>
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
argument_list|>
DECL|method|readResultsWithTimestamps (Result result, byte[] columnPrefixBytes)
name|readResultsWithTimestamps
parameter_list|(
name|Result
name|result
parameter_list|,
name|byte
index|[]
name|columnPrefixBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
argument_list|>
name|results
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|NavigableMap
argument_list|<
name|byte
index|[]
argument_list|,
name|NavigableMap
argument_list|<
name|byte
index|[]
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
argument_list|>
name|resultMap
init|=
name|result
operator|.
name|getMap
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|byte
index|[]
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|columnCellMap
init|=
name|resultMap
operator|.
name|get
argument_list|(
name|columnFamilyBytes
argument_list|)
decl_stmt|;
comment|// could be that there is no such column family.
if|if
condition|(
name|columnCellMap
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
argument_list|>
name|entry
range|:
name|columnCellMap
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|String
name|columnName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|columnPrefixBytes
operator|==
literal|null
condition|)
block|{
comment|// Decode the spaces we encoded in the column name.
name|columnName
operator|=
name|Separator
operator|.
name|decode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// A non-null prefix means columns are actually of the form
comment|// prefix!columnNameRemainder
name|byte
index|[]
index|[]
name|columnNameParts
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|byte
index|[]
name|actualColumnPrefixBytes
init|=
name|columnNameParts
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|Bytes
operator|.
name|equals
argument_list|(
name|columnPrefixBytes
argument_list|,
name|actualColumnPrefixBytes
argument_list|)
operator|&&
name|columnNameParts
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// This is the prefix that we want
name|columnName
operator|=
name|Separator
operator|.
name|decode
argument_list|(
name|columnNameParts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If this column has the prefix we want
if|if
condition|(
name|columnName
operator|!=
literal|null
condition|)
block|{
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
name|cellResults
init|=
operator|new
name|TreeMap
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
argument_list|()
decl_stmt|;
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
name|cells
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|cells
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|byte
index|[]
argument_list|>
name|cell
range|:
name|cells
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|V
name|value
init|=
operator|(
name|V
operator|)
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|cell
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|cellResults
operator|.
name|put
argument_list|(
name|cell
operator|.
name|getKey
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|results
operator|.
name|put
argument_list|(
name|columnName
argument_list|,
name|cellResults
argument_list|)
expr_stmt|;
block|}
block|}
comment|// for entry : columnCellMap
block|}
comment|// if columnCellMap != null
block|}
comment|// if result != null
return|return
name|results
return|;
block|}
comment|/**    * @param result from which to read columns    * @param columnPrefixBytes optional prefix to limit columns. If null all    *          columns are returned.    * @return the latest values of columns in the column family.    * @throws IOException    */
DECL|method|readResults (Result result, byte[] columnPrefixBytes)
specifier|public
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|readResults
parameter_list|(
name|Result
name|result
parameter_list|,
name|byte
index|[]
name|columnPrefixBytes
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|results
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
name|Map
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|columns
init|=
name|result
operator|.
name|getFamilyMap
argument_list|(
name|columnFamilyBytes
argument_list|)
decl_stmt|;
for|for
control|(
name|Entry
argument_list|<
name|byte
index|[]
argument_list|,
name|byte
index|[]
argument_list|>
name|entry
range|:
name|columns
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|!=
literal|null
operator|&&
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|String
name|columnName
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|columnPrefixBytes
operator|==
literal|null
condition|)
block|{
comment|// Decode the spaces we encoded in the column name.
name|columnName
operator|=
name|Separator
operator|.
name|decode
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Separator
operator|.
name|SPACE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// A non-null prefix means columns are actually of the form
comment|// prefix!columnNameRemainder
name|byte
index|[]
index|[]
name|columnNameParts
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|split
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
literal|2
argument_list|)
decl_stmt|;
name|byte
index|[]
name|actualColumnPrefixBytes
init|=
name|columnNameParts
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|Bytes
operator|.
name|equals
argument_list|(
name|columnPrefixBytes
argument_list|,
name|actualColumnPrefixBytes
argument_list|)
operator|&&
name|columnNameParts
operator|.
name|length
operator|==
literal|2
condition|)
block|{
comment|// This is the prefix that we want
name|columnName
operator|=
name|Separator
operator|.
name|decode
argument_list|(
name|columnNameParts
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// If this column has the prefix we want
if|if
condition|(
name|columnName
operator|!=
literal|null
condition|)
block|{
name|Object
name|value
init|=
name|GenericObjectMapper
operator|.
name|read
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
name|results
operator|.
name|put
argument_list|(
name|columnName
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// for entry
block|}
return|return
name|results
return|;
block|}
comment|/**    * @param columnPrefixBytes The byte representation for the column prefix.    *          Should not contain {@link Separator#QUALIFIERS}.    * @param qualifier for the remainder of the column. Any    *          {@link Separator#QUALIFIERS} will be encoded in the qualifier.    * @return fully sanitized column qualifier that is a combination of prefix    *         and qualifier. If prefix is null, the result is simply the encoded    *         qualifier without any separator.    */
DECL|method|getColumnQualifier (byte[] columnPrefixBytes, String qualifier)
specifier|public
specifier|static
name|byte
index|[]
name|getColumnQualifier
parameter_list|(
name|byte
index|[]
name|columnPrefixBytes
parameter_list|,
name|String
name|qualifier
parameter_list|)
block|{
comment|// We don't want column names to have spaces
name|byte
index|[]
name|encodedQualifier
init|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|SPACE
operator|.
name|encode
argument_list|(
name|qualifier
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|columnPrefixBytes
operator|==
literal|null
condition|)
block|{
return|return
name|encodedQualifier
return|;
block|}
comment|// Convert qualifier to lower case, strip of separators and tag on column
comment|// prefix.
name|byte
index|[]
name|columnQualifier
init|=
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|join
argument_list|(
name|columnPrefixBytes
argument_list|,
name|encodedQualifier
argument_list|)
decl_stmt|;
return|return
name|columnQualifier
return|;
block|}
block|}
end_class

end_unit

