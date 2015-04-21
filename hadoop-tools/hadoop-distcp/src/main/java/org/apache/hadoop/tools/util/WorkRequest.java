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
comment|/**  *  WorkRequest<T> is a simple container for items of class T and its  *  corresponding retry counter that indicates how many times this item  *  was previously attempted to be processed.  */
end_comment

begin_class
DECL|class|WorkRequest
specifier|public
class|class
name|WorkRequest
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|retry
specifier|private
name|int
name|retry
decl_stmt|;
DECL|field|item
specifier|private
name|T
name|item
decl_stmt|;
DECL|method|WorkRequest (T item)
specifier|public
name|WorkRequest
parameter_list|(
name|T
name|item
parameter_list|)
block|{
name|this
argument_list|(
name|item
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    *  @param  item   Object representing WorkRequest input data.    *  @param  retry  Number of previous attempts to process this work request.    */
DECL|method|WorkRequest (T item, int retry)
specifier|public
name|WorkRequest
parameter_list|(
name|T
name|item
parameter_list|,
name|int
name|retry
parameter_list|)
block|{
name|this
operator|.
name|item
operator|=
name|item
expr_stmt|;
name|this
operator|.
name|retry
operator|=
name|retry
expr_stmt|;
block|}
DECL|method|getItem ()
specifier|public
name|T
name|getItem
parameter_list|()
block|{
return|return
name|item
return|;
block|}
comment|/**    *  @return  Number of previous attempts to process this work request.    */
DECL|method|getRetry ()
specifier|public
name|int
name|getRetry
parameter_list|()
block|{
return|return
name|retry
return|;
block|}
block|}
end_class

end_unit

