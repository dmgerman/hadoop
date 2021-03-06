begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.utils
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|utils
package|;
end_package

begin_import
import|import
name|com
operator|.
name|fasterxml
operator|.
name|jackson
operator|.
name|core
operator|.
name|JsonProcessingException
import|;
end_import

begin_comment
comment|/**  * Persistence of {@link SerializedApplicationReport}  *   */
end_comment

begin_class
DECL|class|ApplicationReportSerDeser
specifier|public
class|class
name|ApplicationReportSerDeser
extends|extends
name|JsonSerDeser
argument_list|<
name|SerializedApplicationReport
argument_list|>
block|{
DECL|method|ApplicationReportSerDeser ()
specifier|public
name|ApplicationReportSerDeser
parameter_list|()
block|{
name|super
argument_list|(
name|SerializedApplicationReport
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
name|ApplicationReportSerDeser
DECL|field|staticinstance
name|staticinstance
init|=
operator|new
name|ApplicationReportSerDeser
argument_list|()
decl_stmt|;
comment|/**    * Convert an instance to a JSON string -sync access to a shared ser/deser    * object instance    * @param instance object to convert    * @return a JSON string description    * @throws JsonProcessingException parse problems    */
DECL|method|toString (SerializedApplicationReport instance)
specifier|public
specifier|static
name|String
name|toString
parameter_list|(
name|SerializedApplicationReport
name|instance
parameter_list|)
throws|throws
name|JsonProcessingException
block|{
synchronized|synchronized
init|(
name|staticinstance
init|)
block|{
return|return
name|staticinstance
operator|.
name|toJson
argument_list|(
name|instance
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

