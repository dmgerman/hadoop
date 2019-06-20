begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|impl
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Evolving support for functional programming/lambda-expressions.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|FunctionsRaisingIOE
specifier|public
specifier|final
class|class
name|FunctionsRaisingIOE
block|{
DECL|method|FunctionsRaisingIOE ()
specifier|private
name|FunctionsRaisingIOE
parameter_list|()
block|{   }
comment|/**    * Function of arity 1 which may raise an IOException.    * @param<T> type of arg1    * @param<R> type of return value.    */
annotation|@
name|FunctionalInterface
DECL|interface|FunctionRaisingIOE
specifier|public
interface|interface
name|FunctionRaisingIOE
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
block|{
DECL|method|apply (T t)
name|R
name|apply
parameter_list|(
name|T
name|t
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * Function of arity 2 which may raise an IOException.    * @param<T> type of arg1    * @param<U> type of arg2    * @param<R> type of return value.    */
annotation|@
name|FunctionalInterface
DECL|interface|BiFunctionRaisingIOE
specifier|public
interface|interface
name|BiFunctionRaisingIOE
parameter_list|<
name|T
parameter_list|,
name|U
parameter_list|,
name|R
parameter_list|>
block|{
DECL|method|apply (T t, U u)
name|R
name|apply
parameter_list|(
name|T
name|t
parameter_list|,
name|U
name|u
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
comment|/**    * This is a callable which only raises an IOException.    * @param<R> return type    */
annotation|@
name|FunctionalInterface
DECL|interface|CallableRaisingIOE
specifier|public
interface|interface
name|CallableRaisingIOE
parameter_list|<
name|R
parameter_list|>
block|{
DECL|method|apply ()
name|R
name|apply
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
block|}
end_class

end_unit

