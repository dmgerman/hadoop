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
name|GenericConverter
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
name|HBaseTimelineSchemaUtils
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
name|ValueConverter
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
DECL|field|columnFamily
specifier|private
specifier|final
name|ColumnFamily
argument_list|<
name|FlowActivityTable
argument_list|>
name|columnFamily
decl_stmt|;
DECL|field|valueConverter
specifier|private
specifier|final
name|ValueConverter
name|valueConverter
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
name|this
argument_list|(
name|columnFamily
argument_list|,
name|columnPrefix
argument_list|,
name|aggOp
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|FlowActivityColumnPrefix ( ColumnFamily<FlowActivityTable> columnFamily, String columnPrefix, AggregationOperation aggOp, boolean compoundColQual)
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
parameter_list|,
name|boolean
name|compoundColQual
parameter_list|)
block|{
name|this
operator|.
name|valueConverter
operator|=
name|GenericConverter
operator|.
name|getInstance
argument_list|()
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
annotation|@
name|Override
DECL|method|getColumnFamilyBytes ()
specifier|public
name|byte
index|[]
name|getColumnFamilyBytes
parameter_list|()
block|{
return|return
name|columnFamily
operator|.
name|getBytes
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getColumnPrefixInBytes ()
specifier|public
name|byte
index|[]
name|getColumnPrefixInBytes
parameter_list|()
block|{
return|return
name|columnPrefixBytes
operator|!=
literal|null
condition|?
name|columnPrefixBytes
operator|.
name|clone
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|getValueConverter ()
specifier|public
name|ValueConverter
name|getValueConverter
parameter_list|()
block|{
return|return
name|valueConverter
return|;
block|}
annotation|@
name|Override
DECL|method|getCombinedAttrsWithAggr (Attribute... attributes)
specifier|public
name|Attribute
index|[]
name|getCombinedAttrsWithAggr
parameter_list|(
name|Attribute
modifier|...
name|attributes
parameter_list|)
block|{
return|return
name|HBaseTimelineSchemaUtils
operator|.
name|combineAttributes
argument_list|(
name|attributes
argument_list|,
name|aggOp
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|supplementCellTimeStamp ()
specifier|public
name|boolean
name|supplementCellTimeStamp
parameter_list|()
block|{
return|return
literal|false
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
block|}
end_enum

end_unit

