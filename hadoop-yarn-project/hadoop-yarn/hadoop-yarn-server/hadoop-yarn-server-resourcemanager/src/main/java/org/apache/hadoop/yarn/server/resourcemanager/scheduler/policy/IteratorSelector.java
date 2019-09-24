begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.scheduler.policy
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
name|resourcemanager
operator|.
name|scheduler
operator|.
name|policy
package|;
end_package

begin_comment
comment|/**  * IteratorSelector contains information needed to tell an  * {@link OrderingPolicy} what to return in an iterator.  */
end_comment

begin_class
DECL|class|IteratorSelector
specifier|public
class|class
name|IteratorSelector
block|{
DECL|field|EMPTY_ITERATOR_SELECTOR
specifier|public
specifier|static
specifier|final
name|IteratorSelector
name|EMPTY_ITERATOR_SELECTOR
init|=
operator|new
name|IteratorSelector
argument_list|()
decl_stmt|;
DECL|field|partition
specifier|private
name|String
name|partition
decl_stmt|;
comment|/**    * The partition for this iterator selector.    * @return partition    */
DECL|method|getPartition ()
specifier|public
name|String
name|getPartition
parameter_list|()
block|{
return|return
name|this
operator|.
name|partition
return|;
block|}
comment|/**    * Set partition for this iterator selector.    * @param p partition    */
DECL|method|setPartition (String p)
specifier|public
name|void
name|setPartition
parameter_list|(
name|String
name|p
parameter_list|)
block|{
name|this
operator|.
name|partition
operator|=
name|p
expr_stmt|;
block|}
block|}
end_class

end_unit

