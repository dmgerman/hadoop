begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.entity
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
name|entity
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
name|Separator
import|;
end_import

begin_comment
comment|/**  * Represents the entity table column families.  */
end_comment

begin_enum
DECL|enum|EntityColumnFamily
specifier|public
enum|enum
name|EntityColumnFamily
implements|implements
name|ColumnFamily
argument_list|<
name|EntityTable
argument_list|>
block|{
comment|/**    * Info column family houses known columns, specifically ones included in    * columnfamily filters.    */
DECL|enumConstant|INFO
name|INFO
argument_list|(
literal|"i"
argument_list|)
block|,
comment|/**    * Configurations are in a separate column family for two reasons: a) the size    * of the config values can be very large and b) we expect that config values    * are often separately accessed from other metrics and info columns.    */
DECL|enumConstant|CONFIGS
name|CONFIGS
argument_list|(
literal|"c"
argument_list|)
block|,
comment|/**    * Metrics have a separate column family, because they have a separate TTL.    */
DECL|enumConstant|METRICS
name|METRICS
argument_list|(
literal|"m"
argument_list|)
block|;
comment|/**    * Byte representation of this column family.    */
DECL|field|bytes
specifier|private
specifier|final
name|byte
index|[]
name|bytes
decl_stmt|;
comment|/**    * @param value create a column family with this name. Must be lower case and    *          without spaces.    */
DECL|method|EntityColumnFamily (String value)
name|EntityColumnFamily
parameter_list|(
name|String
name|value
parameter_list|)
block|{
comment|// column families should be lower case and not contain any spaces.
name|this
operator|.
name|bytes
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
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|getBytes ()
specifier|public
name|byte
index|[]
name|getBytes
parameter_list|()
block|{
return|return
name|Bytes
operator|.
name|copy
argument_list|(
name|bytes
argument_list|)
return|;
block|}
block|}
end_enum

end_unit

