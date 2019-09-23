begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.function
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|function
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
import|;
end_import

begin_comment
comment|/**  * Functional interface like java.util.function.Function but with  * checked exception.  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
DECL|interface|FunctionWithServiceException
specifier|public
interface|interface
name|FunctionWithServiceException
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
block|{
comment|/**    * Applies this function to the given argument.    *    * @param t the function argument    * @return the function result    */
DECL|method|apply (T t)
name|R
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
throws|throws
name|ServiceException
function_decl|;
block|}
end_interface

end_unit

