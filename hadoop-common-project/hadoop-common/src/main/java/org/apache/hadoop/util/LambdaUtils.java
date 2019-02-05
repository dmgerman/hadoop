begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CompletableFuture
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
comment|/**  * Lambda-expression utilities be they generic or specific to  * Hadoop datatypes.  */
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
DECL|class|LambdaUtils
specifier|public
specifier|final
class|class
name|LambdaUtils
block|{
DECL|method|LambdaUtils ()
specifier|private
name|LambdaUtils
parameter_list|()
block|{   }
comment|/**    * Utility method to evaluate a callable and fill in the future    * with the result or the exception raised.    * Once this method returns, the future will have been evaluated to    * either a return value or an exception.    * @param<T> type of future    * @param result future for the result.    * @param call callable to invoke.    * @return the future passed in    */
DECL|method|eval ( final CompletableFuture<T> result, final Callable<T> call)
specifier|public
specifier|static
parameter_list|<
name|T
parameter_list|>
name|CompletableFuture
argument_list|<
name|T
argument_list|>
name|eval
parameter_list|(
specifier|final
name|CompletableFuture
argument_list|<
name|T
argument_list|>
name|result
parameter_list|,
specifier|final
name|Callable
argument_list|<
name|T
argument_list|>
name|call
parameter_list|)
block|{
try|try
block|{
name|result
operator|.
name|complete
argument_list|(
name|call
operator|.
name|call
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|tx
parameter_list|)
block|{
name|result
operator|.
name|completeExceptionally
argument_list|(
name|tx
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

