begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage.apptoflow
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
name|apptoflow
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
name|Separator
import|;
end_import

begin_comment
comment|/**  * Represents a rowkey for the app_flow table.  */
end_comment

begin_class
DECL|class|AppToFlowRowKey
specifier|public
class|class
name|AppToFlowRowKey
block|{
comment|/**    * Constructs a row key prefix for the app_flow table as follows:    * {@code clusterId!AppId}    *    * @param clusterId    * @param appId    * @return byte array with the row key    */
DECL|method|getRowKey (String clusterId, String appId)
specifier|public
specifier|static
name|byte
index|[]
name|getRowKey
parameter_list|(
name|String
name|clusterId
parameter_list|,
name|String
name|appId
parameter_list|)
block|{
return|return
name|Bytes
operator|.
name|toBytes
argument_list|(
name|Separator
operator|.
name|QUALIFIERS
operator|.
name|joinEncoded
argument_list|(
name|clusterId
argument_list|,
name|appId
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

