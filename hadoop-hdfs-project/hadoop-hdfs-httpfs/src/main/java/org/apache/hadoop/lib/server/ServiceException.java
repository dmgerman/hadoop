begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.lib.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|lib
operator|.
name|server
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
name|lib
operator|.
name|lang
operator|.
name|XException
import|;
end_import

begin_comment
comment|/**  * Exception thrown by {@link Service} implementations.  */
end_comment

begin_class
DECL|class|ServiceException
specifier|public
class|class
name|ServiceException
extends|extends
name|ServerException
block|{
comment|/**    * Creates an service exception using the specified error code.    * The exception message is resolved using the error code template    * and the passed parameters.    *    * @param error error code for the XException.    * @param params parameters to use when creating the error message    * with the error code template.    */
DECL|method|ServiceException (XException.ERROR error, Object... params)
specifier|public
name|ServiceException
parameter_list|(
name|XException
operator|.
name|ERROR
name|error
parameter_list|,
name|Object
modifier|...
name|params
parameter_list|)
block|{
name|super
argument_list|(
name|error
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

