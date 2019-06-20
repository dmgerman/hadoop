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
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Base class of operations in the store.  * An operation is something which executes against the context to  * perform a single function.  * It is expected to have a limited lifespan.  */
end_comment

begin_class
DECL|class|AbstractStoreOperation
specifier|public
specifier|abstract
class|class
name|AbstractStoreOperation
block|{
DECL|field|storeContext
specifier|private
specifier|final
name|StoreContext
name|storeContext
decl_stmt|;
comment|/**    * constructor.    * @param storeContext store context.    */
DECL|method|AbstractStoreOperation (final StoreContext storeContext)
specifier|protected
name|AbstractStoreOperation
parameter_list|(
specifier|final
name|StoreContext
name|storeContext
parameter_list|)
block|{
name|this
operator|.
name|storeContext
operator|=
name|checkNotNull
argument_list|(
name|storeContext
argument_list|)
expr_stmt|;
block|}
comment|/**    * Get the store context.    * @return the context.    */
DECL|method|getStoreContext ()
specifier|public
specifier|final
name|StoreContext
name|getStoreContext
parameter_list|()
block|{
return|return
name|storeContext
return|;
block|}
block|}
end_class

end_unit

