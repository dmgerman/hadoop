begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicBoolean
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * A subclass of {@link AbstractStoreOperation} which  * provides a method {@link #execute()} that may be invoked  * exactly once.  * @param<T> return type of executed operation.  */
end_comment

begin_class
DECL|class|ExecutingStoreOperation
specifier|public
specifier|abstract
class|class
name|ExecutingStoreOperation
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractStoreOperation
block|{
comment|/**    * Used to stop any re-entrancy of the rename.    * This is an execute-once operation.    */
DECL|field|executed
specifier|private
specifier|final
name|AtomicBoolean
name|executed
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|/**    * constructor.    * @param storeContext store context.    */
DECL|method|ExecutingStoreOperation (final StoreContext storeContext)
specifier|protected
name|ExecutingStoreOperation
parameter_list|(
specifier|final
name|StoreContext
name|storeContext
parameter_list|)
block|{
name|super
argument_list|(
name|storeContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * Execute the operation.    * Subclasses MUST call {@link #executeOnlyOnce()} so as to force    * the (atomic) re-entrancy check.    * @return the result.    * @throws IOException IO problem    */
DECL|method|execute ()
specifier|public
specifier|abstract
name|T
name|execute
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Check that the operation has not been invoked twice.    * This is an atomic check.    * @throws IllegalStateException on a second invocation.    */
DECL|method|executeOnlyOnce ()
specifier|protected
name|void
name|executeOnlyOnce
parameter_list|()
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|executed
operator|.
name|getAndSet
argument_list|(
literal|true
argument_list|)
argument_list|,
literal|"Operation attempted twice"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

