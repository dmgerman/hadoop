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

begin_comment
comment|/**  * Identifies the compaction dimensions for the data in the {@link FlowRunTable}  * .  */
end_comment

begin_enum
DECL|enum|AggregationCompactionDimension
specifier|public
enum|enum
name|AggregationCompactionDimension
block|{
comment|/**    * the application id.    */
DECL|enumConstant|APPLICATION_ID
name|APPLICATION_ID
argument_list|(
operator|(
name|byte
operator|)
literal|101
argument_list|)
block|;
DECL|field|tagType
specifier|private
name|byte
name|tagType
decl_stmt|;
DECL|field|inBytes
specifier|private
name|byte
index|[]
name|inBytes
decl_stmt|;
DECL|method|AggregationCompactionDimension (byte tagType)
specifier|private
name|AggregationCompactionDimension
parameter_list|(
name|byte
name|tagType
parameter_list|)
block|{
name|this
operator|.
name|tagType
operator|=
name|tagType
expr_stmt|;
name|this
operator|.
name|inBytes
operator|=
name|Bytes
operator|.
name|toBytes
argument_list|(
name|this
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getAttribute (String attributeValue)
specifier|public
name|Attribute
name|getAttribute
parameter_list|(
name|String
name|attributeValue
parameter_list|)
block|{
return|return
operator|new
name|Attribute
argument_list|(
name|this
operator|.
name|name
argument_list|()
argument_list|,
name|Bytes
operator|.
name|toBytes
argument_list|(
name|attributeValue
argument_list|)
argument_list|)
return|;
block|}
DECL|method|getTagType ()
specifier|public
name|byte
name|getTagType
parameter_list|()
block|{
return|return
name|tagType
return|;
block|}
DECL|method|getInBytes ()
specifier|public
name|byte
index|[]
name|getInBytes
parameter_list|()
block|{
return|return
name|this
operator|.
name|inBytes
operator|.
name|clone
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|AggregationCompactionDimension
DECL|method|getAggregationCompactionDimension (String aggCompactDimStr)
name|getAggregationCompactionDimension
parameter_list|(
name|String
name|aggCompactDimStr
parameter_list|)
block|{
for|for
control|(
name|AggregationCompactionDimension
name|aggDim
range|:
name|AggregationCompactionDimension
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|aggDim
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|aggCompactDimStr
argument_list|)
condition|)
block|{
return|return
name|aggDim
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_enum

end_unit

