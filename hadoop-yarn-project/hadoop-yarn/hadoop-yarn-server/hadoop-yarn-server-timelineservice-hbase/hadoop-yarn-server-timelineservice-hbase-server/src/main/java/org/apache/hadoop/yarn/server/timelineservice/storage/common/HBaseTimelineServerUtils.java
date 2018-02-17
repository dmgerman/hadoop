begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
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
name|CellUtil
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
name|KeyValue
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
name|Tag
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
name|flow
operator|.
name|AggregationCompactionDimension
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
name|AggregationOperation
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
comment|/**  * A utility class used by hbase-server module.  */
end_comment

begin_class
DECL|class|HBaseTimelineServerUtils
specifier|public
specifier|final
class|class
name|HBaseTimelineServerUtils
block|{
DECL|method|HBaseTimelineServerUtils ()
specifier|private
name|HBaseTimelineServerUtils
parameter_list|()
block|{   }
comment|/**    * Creates a {@link Tag} from the input attribute.    *    * @param attribute Attribute from which tag has to be fetched.    * @return a HBase Tag.    */
DECL|method|getTagFromAttribute (Map.Entry<String, byte[]> attribute)
specifier|public
specifier|static
name|Tag
name|getTagFromAttribute
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|byte
index|[]
argument_list|>
name|attribute
parameter_list|)
block|{
comment|// attribute could be either an Aggregation Operation or
comment|// an Aggregation Dimension
comment|// Get the Tag type from either
name|AggregationOperation
name|aggOp
init|=
name|AggregationOperation
operator|.
name|getAggregationOperation
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggOp
operator|!=
literal|null
condition|)
block|{
name|Tag
name|t
init|=
operator|new
name|Tag
argument_list|(
name|aggOp
operator|.
name|getTagType
argument_list|()
argument_list|,
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|t
return|;
block|}
name|AggregationCompactionDimension
name|aggCompactDim
init|=
name|AggregationCompactionDimension
operator|.
name|getAggregationCompactionDimension
argument_list|(
name|attribute
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|aggCompactDim
operator|!=
literal|null
condition|)
block|{
name|Tag
name|t
init|=
operator|new
name|Tag
argument_list|(
name|aggCompactDim
operator|.
name|getTagType
argument_list|()
argument_list|,
name|attribute
operator|.
name|getValue
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|t
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**    * creates a new cell based on the input cell but with the new value.    *    * @param origCell Original cell    * @param newValue new cell value    * @return cell    * @throws IOException while creating new cell.    */
DECL|method|createNewCell (Cell origCell, byte[] newValue)
specifier|public
specifier|static
name|Cell
name|createNewCell
parameter_list|(
name|Cell
name|origCell
parameter_list|,
name|byte
index|[]
name|newValue
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CellUtil
operator|.
name|createCell
argument_list|(
name|CellUtil
operator|.
name|cloneRow
argument_list|(
name|origCell
argument_list|)
argument_list|,
name|CellUtil
operator|.
name|cloneFamily
argument_list|(
name|origCell
argument_list|)
argument_list|,
name|CellUtil
operator|.
name|cloneQualifier
argument_list|(
name|origCell
argument_list|)
argument_list|,
name|origCell
operator|.
name|getTimestamp
argument_list|()
argument_list|,
name|KeyValue
operator|.
name|Type
operator|.
name|Put
operator|.
name|getCode
argument_list|()
argument_list|,
name|newValue
argument_list|)
return|;
block|}
comment|/**    * creates a cell with the given inputs.    *    * @param row row of the cell to be created    * @param family column family name of the new cell    * @param qualifier qualifier for the new cell    * @param ts timestamp of the new cell    * @param newValue value of the new cell    * @param tags tags in the new cell    * @return cell    * @throws IOException while creating the cell.    */
DECL|method|createNewCell (byte[] row, byte[] family, byte[] qualifier, long ts, byte[] newValue, byte[] tags)
specifier|public
specifier|static
name|Cell
name|createNewCell
parameter_list|(
name|byte
index|[]
name|row
parameter_list|,
name|byte
index|[]
name|family
parameter_list|,
name|byte
index|[]
name|qualifier
parameter_list|,
name|long
name|ts
parameter_list|,
name|byte
index|[]
name|newValue
parameter_list|,
name|byte
index|[]
name|tags
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|CellUtil
operator|.
name|createCell
argument_list|(
name|row
argument_list|,
name|family
argument_list|,
name|qualifier
argument_list|,
name|ts
argument_list|,
name|KeyValue
operator|.
name|Type
operator|.
name|Put
argument_list|,
name|newValue
argument_list|,
name|tags
argument_list|)
return|;
block|}
comment|/**    * returns app id from the list of tags.    *    * @param tags cell tags to be looked into    * @return App Id as the AggregationCompactionDimension    */
DECL|method|getAggregationCompactionDimension (List<Tag> tags)
specifier|public
specifier|static
name|String
name|getAggregationCompactionDimension
parameter_list|(
name|List
argument_list|<
name|Tag
argument_list|>
name|tags
parameter_list|)
block|{
name|String
name|appId
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Tag
name|t
range|:
name|tags
control|)
block|{
if|if
condition|(
name|AggregationCompactionDimension
operator|.
name|APPLICATION_ID
operator|.
name|getTagType
argument_list|()
operator|==
name|t
operator|.
name|getType
argument_list|()
condition|)
block|{
name|appId
operator|=
name|Bytes
operator|.
name|toString
argument_list|(
name|t
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|appId
return|;
block|}
block|}
return|return
name|appId
return|;
block|}
comment|/**    * Returns the first seen aggregation operation as seen in the list of input    * tags or null otherwise.    *    * @param tags list of HBase tags.    * @return AggregationOperation    */
DECL|method|getAggregationOperationFromTagsList ( List<Tag> tags)
specifier|public
specifier|static
name|AggregationOperation
name|getAggregationOperationFromTagsList
parameter_list|(
name|List
argument_list|<
name|Tag
argument_list|>
name|tags
parameter_list|)
block|{
for|for
control|(
name|AggregationOperation
name|aggOp
range|:
name|AggregationOperation
operator|.
name|values
argument_list|()
control|)
block|{
for|for
control|(
name|Tag
name|tag
range|:
name|tags
control|)
block|{
if|if
condition|(
name|tag
operator|.
name|getType
argument_list|()
operator|==
name|aggOp
operator|.
name|getTagType
argument_list|()
condition|)
block|{
return|return
name|aggOp
return|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

