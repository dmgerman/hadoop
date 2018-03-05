begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|records
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * Allocation tags under same namespace.  */
end_comment

begin_class
DECL|class|AllocationTags
specifier|public
class|class
name|AllocationTags
block|{
DECL|field|ns
specifier|private
name|AllocationTagNamespace
name|ns
decl_stmt|;
DECL|field|tags
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|tags
decl_stmt|;
DECL|method|AllocationTags (AllocationTagNamespace namespace, Set<String> allocationTags)
specifier|public
name|AllocationTags
parameter_list|(
name|AllocationTagNamespace
name|namespace
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|allocationTags
parameter_list|)
block|{
name|this
operator|.
name|ns
operator|=
name|namespace
expr_stmt|;
name|this
operator|.
name|tags
operator|=
name|allocationTags
expr_stmt|;
block|}
comment|/**    * @return the namespace of these tags.    */
DECL|method|getNamespace ()
specifier|public
name|AllocationTagNamespace
name|getNamespace
parameter_list|()
block|{
return|return
name|this
operator|.
name|ns
return|;
block|}
comment|/**    * @return the allocation tags.    */
DECL|method|getTags ()
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getTags
parameter_list|()
block|{
return|return
name|this
operator|.
name|tags
return|;
block|}
block|}
end_class

end_unit

