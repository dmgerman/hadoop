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
comment|/**  * Identifies the attributes to be set for puts into the {@link FlowRunTable}.  * The numbers used for tagType are prime numbers.  */
end_comment

begin_enum
DECL|enum|AggregationOperation
specifier|public
enum|enum
name|AggregationOperation
block|{
comment|/**    * When the flow was started.    */
DECL|enumConstant|GLOBAL_MIN
name|GLOBAL_MIN
argument_list|(
operator|(
name|byte
operator|)
literal|71
argument_list|)
block|,
comment|/**    * When it ended.    */
DECL|enumConstant|GLOBAL_MAX
name|GLOBAL_MAX
argument_list|(
operator|(
name|byte
operator|)
literal|73
argument_list|)
block|,
comment|/**    * The metrics of the flow.    */
DECL|enumConstant|SUM
name|SUM
argument_list|(
operator|(
name|byte
operator|)
literal|79
argument_list|)
block|,
comment|/**    * application running.    */
DECL|enumConstant|SUM_FINAL
name|SUM_FINAL
argument_list|(
operator|(
name|byte
operator|)
literal|83
argument_list|)
block|,
comment|/**    * Min value as per the latest timestamp    * seen for a given app.    */
DECL|enumConstant|LATEST_MIN
name|LATEST_MIN
argument_list|(
operator|(
name|byte
operator|)
literal|89
argument_list|)
block|,
comment|/**    * Max value as per the latest timestamp    * seen for a given app.    */
DECL|enumConstant|LATEST_MAX
name|LATEST_MAX
argument_list|(
operator|(
name|byte
operator|)
literal|97
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
DECL|method|AggregationOperation (byte tagType)
specifier|private
name|AggregationOperation
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
DECL|method|getAttribute ()
specifier|public
name|Attribute
name|getAttribute
parameter_list|()
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
name|this
operator|.
name|inBytes
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
comment|/**    * returns the AggregationOperation enum that represents that string.    * @param aggOpStr Aggregation operation.    * @return the AggregationOperation enum that represents that string    */
DECL|method|getAggregationOperation (String aggOpStr)
specifier|public
specifier|static
name|AggregationOperation
name|getAggregationOperation
parameter_list|(
name|String
name|aggOpStr
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
if|if
condition|(
name|aggOp
operator|.
name|name
argument_list|()
operator|.
name|equals
argument_list|(
name|aggOpStr
argument_list|)
condition|)
block|{
return|return
name|aggOp
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

