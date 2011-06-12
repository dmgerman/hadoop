begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_comment
comment|/**  * Class to hold queue share info to be  * communicated between scheduler and   * queue share manager  */
end_comment

begin_class
DECL|class|QueueAllocation
specifier|public
class|class
name|QueueAllocation
block|{
DECL|field|name
specifier|private
name|String
name|name
decl_stmt|;
DECL|field|share
specifier|private
name|float
name|share
decl_stmt|;
comment|/**    * @param name queue name    * @param share queue share of total capacity (0..1)    */
DECL|method|QueueAllocation (String name, float share)
specifier|public
name|QueueAllocation
parameter_list|(
name|String
name|name
parameter_list|,
name|float
name|share
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|share
operator|=
name|share
expr_stmt|;
block|}
comment|/**    * Gets queue share    * @return queue share of total capacity (0..1)    */
DECL|method|getShare ()
specifier|public
name|float
name|getShare
parameter_list|()
block|{
return|return
name|this
operator|.
name|share
return|;
block|}
comment|/**    * Gets queue name    * @return queue name    */
DECL|method|getName ()
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|this
operator|.
name|name
return|;
block|}
block|}
end_class

end_unit

