begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
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
name|yarn
operator|.
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  * NodesToAttributesMappingResponse holds response object for attribute  * mapping.  */
end_comment

begin_class
DECL|class|NodesToAttributesMappingResponse
specifier|public
class|class
name|NodesToAttributesMappingResponse
block|{
DECL|method|newInstance ()
specifier|public
specifier|static
name|NodesToAttributesMappingResponse
name|newInstance
parameter_list|()
block|{
return|return
name|Records
operator|.
name|newRecord
argument_list|(
name|NodesToAttributesMappingResponse
operator|.
name|class
argument_list|)
return|;
block|}
block|}
end_class

end_unit

