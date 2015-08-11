begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.application
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
name|application
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
name|Column
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
name|TypedBufferedMutator
import|;
end_import

begin_comment
comment|/**  * Identifies fully qualified columns for the {@link ApplicationTable}.  */
end_comment

begin_enum
DECL|enum|ApplicationColumn
specifier|public
enum|enum
name|ApplicationColumn
implements|implements
name|Column
argument_list|<
name|ApplicationTable
argument_list|>
block|{
comment|/**    * App id    */
DECL|enumConstant|ID
name|ID
argument_list|(
name|ApplicationColumnFamily
operator|.
name|INFO
argument_list|,
literal|"id"
argument_list|)
block|,
comment|/**    * When the application was created.    */
DECL|enumConstant|CREATED_TIME
name|CREATED_TIME
argument_list|(
name|ApplicationColumnFamily
operator|.
name|INFO
argument_list|,
literal|"created_time"
argument_list|)
block|,
comment|/**    * When it was modified.    */
DECL|enumConstant|MODIFIED_TIME
name|MODIFIED_TIME
argument_list|(
name|ApplicationColumnFamily
operator|.
name|INFO
argument_list|,
literal|"modified_time"
argument_list|)
block|,
comment|/**    * The version of the flow that this app belongs to.    */
DECL|enumConstant|FLOW_VERSION
name|FLOW_VERSION
argument_list|(
name|ApplicationColumnFamily
operator|.
name|INFO
argument_list|,
literal|"flow_version"
argument_list|)
block|;
DECL|field|column
specifier|private
specifier|final
name|ColumnHelper
argument_list|<
name|ApplicationTable
argument_list|>
name|column
decl_stmt|;
DECL|field|columnFamily
specifier|private
specifier|final
name|ColumnFamily
argument_list|<
name|ApplicationTable
argument_list|>
name|columnFamily
decl_stmt|;
DECL|field|columnQualifier
specifier|private
specifier|final
name|String
name|columnQualifier
decl_stmt|;
DECL|field|columnQualifierBytes
specifier|private
specifier|final
name|byte
index|[]
name|columnQualifierBytes
decl_stmt|;
DECL|method|ApplicationColumn (ColumnFamily<ApplicationTable> columnFamily, String columnQualifier)
specifier|private
name|ApplicationColumn
parameter_list|(
name|ColumnFamily
argument_list|<
name|ApplicationTable
argument_list|>
name|columnFamily
parameter_list|,
name|String
name|columnQualifier
parameter_list|)
block|{
name|this
operator|.
name|columnFamily
operator|=
name|columnFamily
expr_stmt|;
name|this
operator|.
name|columnQualifier
operator|=
name|columnQualifier
expr_stmt|;
comment|// Future-proof by ensuring the right column prefix hygiene.
name|this
operator|.
name|columnQualifierBytes
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
name|columnQualifier
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|column
operator|=
operator|new
name|ColumnHelper
argument_list|<
name|ApplicationTable
argument_list|>
argument_list|(
name|columnFamily
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return the column name value    */
DECL|method|getColumnQualifier ()
specifier|private
name|String
name|getColumnQualifier
parameter_list|()
block|{
return|return
name|columnQualifier
return|;
block|}
DECL|method|store (byte[] rowKey, TypedBufferedMutator<ApplicationTable> tableMutator, Long timestamp, Object inputValue)
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
name|ApplicationTable
argument_list|>
name|tableMutator
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
name|column
operator|.
name|store
argument_list|(
name|rowKey
argument_list|,
name|tableMutator
argument_list|,
name|columnQualifierBytes
argument_list|,
name|timestamp
argument_list|,
name|inputValue
argument_list|)
expr_stmt|;
block|}
DECL|method|readResult (Result result)
specifier|public
name|Object
name|readResult
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
name|readResult
argument_list|(
name|result
argument_list|,
name|columnQualifierBytes
argument_list|)
return|;
block|}
comment|/**    * Retrieve an {@link ApplicationColumn} given a name, or null if there is no    * match. The following holds true: {@code columnFor(x) == columnFor(y)} if    * and only if {@code x.equals(y)} or {@code (x == y == null)}    *    * @param columnQualifier Name of the column to retrieve    * @return the corresponding {@link ApplicationColumn} or null    */
DECL|method|columnFor (String columnQualifier)
specifier|public
specifier|static
specifier|final
name|ApplicationColumn
name|columnFor
parameter_list|(
name|String
name|columnQualifier
parameter_list|)
block|{
comment|// Match column based on value, assume column family matches.
for|for
control|(
name|ApplicationColumn
name|ac
range|:
name|ApplicationColumn
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Find a match based only on name.
if|if
condition|(
name|ac
operator|.
name|getColumnQualifier
argument_list|()
operator|.
name|equals
argument_list|(
name|columnQualifier
argument_list|)
condition|)
block|{
return|return
name|ac
return|;
block|}
block|}
comment|// Default to null
return|return
literal|null
return|;
block|}
comment|/**    * Retrieve an {@link ApplicationColumn} given a name, or null if there is no    * match. The following holds true: {@code columnFor(a,x) == columnFor(b,y)}    * if and only if {@code a.equals(b)& x.equals(y)} or    * {@code (x == y == null)}    *    * @param columnFamily The columnFamily for which to retrieve the column.    * @param name Name of the column to retrieve    * @return the corresponding {@link ApplicationColumn} or null if both    *         arguments don't match.    */
DECL|method|columnFor ( ApplicationColumnFamily columnFamily, String name)
specifier|public
specifier|static
specifier|final
name|ApplicationColumn
name|columnFor
parameter_list|(
name|ApplicationColumnFamily
name|columnFamily
parameter_list|,
name|String
name|name
parameter_list|)
block|{
for|for
control|(
name|ApplicationColumn
name|ac
range|:
name|ApplicationColumn
operator|.
name|values
argument_list|()
control|)
block|{
comment|// Find a match based column family and on name.
if|if
condition|(
name|ac
operator|.
name|columnFamily
operator|.
name|equals
argument_list|(
name|columnFamily
argument_list|)
operator|&&
name|ac
operator|.
name|getColumnQualifier
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|ac
return|;
block|}
block|}
comment|// Default to null
return|return
literal|null
return|;
block|}
block|}
end_enum

end_unit

