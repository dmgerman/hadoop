begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.tools.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|tools
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  *  Interface for ProducerConsumer worker loop.  *  */
end_comment

begin_interface
DECL|interface|WorkRequestProcessor
specifier|public
interface|interface
name|WorkRequestProcessor
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
block|{
comment|/**    * Work processor.    * The processor should be stateless: that is, it can be repeated after    * being interrupted.    *    * @param   workRequest  Input work item.    * @return  Outputs WorkReport after processing workRequest item.    *    */
DECL|method|processItem (WorkRequest<T> workRequest)
specifier|public
name|WorkReport
argument_list|<
name|R
argument_list|>
name|processItem
parameter_list|(
name|WorkRequest
argument_list|<
name|T
argument_list|>
name|workRequest
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

