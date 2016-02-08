begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.flow
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
name|flow
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
name|timelineservice
operator|.
name|storage
operator|.
name|common
operator|.
name|ColumnFamily
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
name|common
operator|.
name|ColumnHelper
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
name|common
operator|.
name|ColumnPrefix
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
name|common
operator|.
name|Separator
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
name|common
operator|.
name|TimelineStorageUtils
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
name|common
operator|.
name|TypedBufferedMutator
import|;
end_import

begin_comment
comment|/**  * Identifies partially qualified columns for the {@link FlowActivityTable}.  */
end_comment

begin_enum
DECL|enum|FlowActivityColumnPrefix
specifier|public
enum|enum
name|FlowActivityColumnPrefix
implements|implements
name|ColumnPrefix
argument_list|<
name|FlowActivityTable
argument_list|>
block|{
comment|/**    * To store run ids of the flows.    */
DECL|enumConstant|RUN_ID
name|RUN_ID
argument_list|(
name|FlowActivityColumnFamily
operator|.
name|INFO
argument_list|,
literal|"r"
argument_list|,
literal|null
argument_list|)
block|;
DECL|field|column
specifier|private
specifier|final
name|ColumnHelper
argument_list|<
name|FlowActivityTable
argument_list|>
name|column
decl_stmt|;
DECL|field|columnFamily
specifier|private
specifier|final
name|ColumnFamily
argument_list|<
name|FlowActivityTable
argument_list|>
name|columnFamily
decl_stmt|;
comment|/**    * Can be null for those cases where the provided column qualifier is the    * entire column name.    */
DECL|field|columnPrefix
specifier|private
specifier|final
name|String
name|columnPrefix
decl_stmt|;
DECL|field|columnPrefixBytes
specifier|private
specifier|final
name|byte
index|[]
name|columnPrefixBytes
decl_stmt|;
DECL|field|aggOp
specifier|private
specifier|final
name|AggregationOperation
name|aggOp
decl_stmt|;
comment|/**    * Private constructor, meant to be used by the enum definition.    *    * @param columnFamily    *          that this column is stored in.    * @param columnPrefix    *          for this column.    */
DECL|method|FlowActivityColumnPrefix ( ColumnFamily<FlowActivityTable> columnFamily, String columnPrefix, AggregationOperation aggOp)
specifier|private
name|FlowActivityColumnPrefix
parameter_list|(
name|ColumnFamily
argument_list|<
name|FlowActivityTable
argument_list|>
name|columnFamily
parameter_list|,
name|String
name|columnPrefix
parameter_list|,
name|AggregationOperation
name|aggOp
parameter_list|)
block|{
name|column
operator|=
operator|new
name|ColumnHelper
argument_list|<
name|FlowActivityTable
argument_list|>
argument_list|(
name|columnFamily
argument_list|)
expr_stmt|;
name|this
operator|.
name|columnFamily
operator|=
name|columnFamily
expr_stmt|;
name|this
operator|.
name|columnPrefix
operator|=
name|columnPrefix
expr_stmt|;
if|if
condition|(
name|columnPrefix
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|columnPrefixBytes
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
comment|// Future-proof by ensuring the right column prefix hygiene.
name|this
operator|.
name|columnPrefixBytes
operator|=
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
name|columnPrefix
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|aggOp
operator|=
name|aggOp
expr_stmt|;
block|}
comment|/**    * @return the column name value    */
DECL|method|getColumnPrefix ()
specifier|public
name|String
name|getColumnPrefix
parameter_list|()
block|{
return|return
name|columnPrefix
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnPrefixBytes (byte[] qualifierPrefix)
specifier|public
name|byte
index|[]
name|getColumnPrefixBytes
parameter_list|(
name|byte
index|[]
name|qualifierPrefix
parameter_list|)
block|{
return|return
name|ColumnHelper
operator|.
name|getColumnQualifier
argument_list|(
name|this
operator|.
name|columnPrefixBytes
argument_list|,
name|qualifierPrefix
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnPrefixBytes (String qualifierPrefix)
specifier|public
name|byte
index|[]
name|getColumnPrefixBytes
parameter_list|(
name|String
name|qualifierPrefix
parameter_list|)
block|{
return|return
name|ColumnHelper
operator|.
name|getColumnQualifier
argument_list|(
name|this
operator|.
name|columnPrefixBytes
argument_list|,
name|qualifierPrefix
argument_list|)
return|;
block|}
DECL|method|getColumnPrefixBytes ()
specifier|public
name|byte
index|[]
name|getColumnPrefixBytes
parameter_list|()
block|{
return|return
name|columnPrefixBytes
operator|.
name|clone
argument_list|()
return|;
block|}
DECL|method|getAttribute ()
specifier|public
name|AggregationOperation
name|getAttribute
parameter_list|()
block|{
return|return
name|aggOp
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnPrefix    * #store(byte[],    * org.apache.hadoop.yarn.server.timelineservice.storage.common.    * TypedBufferedMutator, byte[], java.lang.Long, java.lang.Object,    * org.apache.hadoop.yarn.server.timelineservice.storage.flow.Attribute[])    */
annotation|@
name|Override
DECL|method|store (byte[] rowKey, TypedBufferedMutator<FlowActivityTable> tableMutator, byte[] qualifier, Long timestamp, Object inputValue, Attribute... attributes)
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
name|FlowActivityTable
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
block|{
comment|// Null check
if|if
condition|(
name|qualifier
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot store column with null qualifier in "
operator|+
name|tableMutator
operator|.
name|getName
argument_list|()
operator|.
name|getNameAsString
argument_list|()
argument_list|)
throw|;
block|}
name|byte
index|[]
name|columnQualifier
init|=
name|getColumnPrefixBytes
argument_list|(
name|qualifier
argument_list|)
decl_stmt|;
name|Attribute
index|[]
name|combinedAttributes
init|=
name|TimelineStorageUtils
operator|.
name|combineAttributes
argument_list|(
name|attributes
argument_list|,
name|this
operator|.
name|aggOp
argument_list|)
decl_stmt|;
name|column
operator|.
name|store
argument_list|(
name|rowKey
argument_list|,
name|tableMutator
argument_list|,
name|columnQualifier
argument_list|,
name|timestamp
argument_list|,
name|inputValue
argument_list|,
name|combinedAttributes
argument_list|)
expr_stmt|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnPrefix    * #readResult(org.apache.hadoop.hbase.client.Result, java.lang.String)    */
DECL|method|readResult (Result result, String qualifier)
specifier|public
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
block|{
name|byte
index|[]
name|columnQualifier
init|=
name|ColumnHelper
operator|.
name|getColumnQualifier
argument_list|(
name|this
operator|.
name|columnPrefixBytes
argument_list|,
name|qualifier
argument_list|)
decl_stmt|;
return|return
name|column
operator|.
name|readResult
argument_list|(
name|result
argument_list|,
name|columnQualifier
argument_list|)
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnPrefix    * #readResults(org.apache.hadoop.hbase.client.Result)    */
DECL|method|readResults (Result result)
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
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|column
operator|.
name|readResults
argument_list|(
name|result
argument_list|,
name|columnPrefixBytes
argument_list|)
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnPrefix    * #readResultsWithTimestamps(org.apache.hadoop.hbase.client.Result)    */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|NavigableMap
argument_list|<
name|String
argument_list|,
name|NavigableMap
argument_list|<
name|Long
argument_list|,
name|T
argument_list|>
argument_list|>
DECL|method|readResultsWithTimestamps (Result result)
name|readResultsWithTimestamps
parameter_list|(
name|Result
name|result
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|column
operator|.
name|readResultsWithTimestamps
argument_list|(
name|result
argument_list|,
name|columnPrefixBytes
argument_list|)
return|;
block|}
comment|/**    * Retrieve an {@link FlowActivityColumnPrefix} given a name, or null if there    * is no match. The following holds true: {@code columnFor(x) == columnFor(y)}    * if and only if {@code x.equals(y)} or {@code (x == y == null)}    *    * @param columnPrefix    *          Name of the column to retrieve    * @return the corresponding {@link FlowActivityColumnPrefix} or null    */
DECL|method|columnFor (String columnPrefix)
specifier|public
specifier|static
specifier|final
name|FlowActivityColumnPrefix
name|columnFor
parameter_list|(
name|String
name|columnPrefix
parameter_list|)
block|{
comment|// Match column based on value, assume column family matches.
for|for
control|(
name|FlowActivityColumnPrefix
name|flowActivityColPrefix
range|:
name|FlowActivityColumnPrefix
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Find a match based only on name.
if|if
condition|(
name|flowActivityColPrefix
operator|.
name|getColumnPrefix
argument_list|()
operator|.
name|equals
argument_list|(
name|columnPrefix
argument_list|)
condition|)
block|{
return|return
name|flowActivityColPrefix
return|;
block|}
block|}
comment|// Default to null
return|return
literal|null
return|;
block|}
comment|/**    * Retrieve an {@link FlowActivityColumnPrefix} given a name, or null if there    * is no match. The following holds true:    * {@code columnFor(a,x) == columnFor(b,y)} if and only if    * {@code (x == y == null)} or {@code a.equals(b)& x.equals(y)}    *    * @param columnFamily    *          The columnFamily for which to retrieve the column.    * @param columnPrefix    *          Name of the column to retrieve    * @return the corresponding {@link FlowActivityColumnPrefix} or null if both    *         arguments don't match.    */
DECL|method|columnFor ( FlowActivityColumnFamily columnFamily, String columnPrefix)
specifier|public
specifier|static
specifier|final
name|FlowActivityColumnPrefix
name|columnFor
parameter_list|(
name|FlowActivityColumnFamily
name|columnFamily
parameter_list|,
name|String
name|columnPrefix
parameter_list|)
block|{
comment|// TODO: needs unit test to confirm and need to update javadoc to explain
comment|// null prefix case.
for|for
control|(
name|FlowActivityColumnPrefix
name|flowActivityColumnPrefix
range|:
name|FlowActivityColumnPrefix
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Find a match based column family and on name.
if|if
condition|(
name|flowActivityColumnPrefix
operator|.
name|columnFamily
operator|.
name|equals
argument_list|(
name|columnFamily
argument_list|)
operator|&&
operator|(
operator|(
operator|(
name|columnPrefix
operator|==
literal|null
operator|)
operator|&&
operator|(
name|flowActivityColumnPrefix
operator|.
name|getColumnPrefix
argument_list|()
operator|==
literal|null
operator|)
operator|)
operator|||
operator|(
name|flowActivityColumnPrefix
operator|.
name|getColumnPrefix
argument_list|()
operator|.
name|equals
argument_list|(
name|columnPrefix
argument_list|)
operator|)
operator|)
condition|)
block|{
return|return
name|flowActivityColumnPrefix
return|;
block|}
block|}
comment|// Default to null
return|return
literal|null
return|;
block|}
comment|/*    * (non-Javadoc)    *    * @see    * org.apache.hadoop.yarn.server.timelineservice.storage.common.ColumnPrefix    * #store(byte[],    * org.apache.hadoop.yarn.server.timelineservice.storage.common.    * TypedBufferedMutator, java.lang.String, java.lang.Long, java.lang.Object,    * org.apache.hadoop.yarn.server.timelineservice.storage.flow.Attribute[])    */
annotation|@
name|Override
DECL|method|store (byte[] rowKey, TypedBufferedMutator<FlowActivityTable> tableMutator, String qualifier, Long timestamp, Object inputValue, Attribute... attributes)
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
name|FlowActivityTable
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
block|{
comment|// Null check
if|if
condition|(
name|qualifier
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Cannot store column with null qualifier in "
operator|+
name|tableMutator
operator|.
name|getName
argument_list|()
operator|.
name|getNameAsString
argument_list|()
argument_list|)
throw|;
block|}
name|byte
index|[]
name|columnQualifier
init|=
name|getColumnPrefixBytes
argument_list|(
name|qualifier
argument_list|)
decl_stmt|;
name|Attribute
index|[]
name|combinedAttributes
init|=
name|TimelineStorageUtils
operator|.
name|combineAttributes
argument_list|(
name|attributes
argument_list|,
name|this
operator|.
name|aggOp
argument_list|)
decl_stmt|;
name|column
operator|.
name|store
argument_list|(
name|rowKey
argument_list|,
name|tableMutator
argument_list|,
name|columnQualifier
argument_list|,
literal|null
argument_list|,
name|inputValue
argument_list|,
name|combinedAttributes
argument_list|)
expr_stmt|;
block|}
block|}
end_enum

end_unit

