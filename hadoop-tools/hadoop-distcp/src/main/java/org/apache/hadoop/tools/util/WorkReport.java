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
comment|/**  *  WorkReport<T> is a simple container for items of class T and its  *  corresponding retry counter that indicates how many times this item  *  was previously attempted to be processed.  */
end_comment

begin_class
DECL|class|WorkReport
specifier|public
class|class
name|WorkReport
parameter_list|<
name|T
parameter_list|>
block|{
DECL|field|item
specifier|private
name|T
name|item
decl_stmt|;
DECL|field|success
specifier|private
specifier|final
name|boolean
name|success
decl_stmt|;
DECL|field|retry
specifier|private
specifier|final
name|int
name|retry
decl_stmt|;
DECL|field|exception
specifier|private
specifier|final
name|Exception
name|exception
decl_stmt|;
comment|/**    *  @param  item       Object representing work report.    *  @param  retry      Number of unsuccessful attempts to process work.    *  @param  success    Indicates whether work was successfully completed.    */
DECL|method|WorkReport (T item, int retry, boolean success)
specifier|public
name|WorkReport
parameter_list|(
name|T
name|item
parameter_list|,
name|int
name|retry
parameter_list|,
name|boolean
name|success
parameter_list|)
block|{
name|this
argument_list|(
name|item
argument_list|,
name|retry
argument_list|,
name|success
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**    *  @param  item       Object representing work report.    *  @param  retry      Number of unsuccessful attempts to process work.    *  @param  success    Indicates whether work was successfully completed.    *  @param  exception  Exception thrown while processing work.    */
DECL|method|WorkReport (T item, int retry, boolean success, Exception exception)
specifier|public
name|WorkReport
parameter_list|(
name|T
name|item
parameter_list|,
name|int
name|retry
parameter_list|,
name|boolean
name|success
parameter_list|,
name|Exception
name|exception
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
name|this
operator|.
name|success
operator|=
name|success
expr_stmt|;
name|this
operator|.
name|exception
operator|=
name|exception
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
comment|/**    *  @return True if the work was processed successfully.    */
DECL|method|getSuccess ()
specifier|public
name|boolean
name|getSuccess
parameter_list|()
block|{
return|return
name|success
return|;
block|}
comment|/**    *  @return  Number of unsuccessful attempts to process work.    */
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
comment|/**    *  @return  Exception thrown while processing work.    */
DECL|method|getException ()
specifier|public
name|Exception
name|getException
parameter_list|()
block|{
return|return
name|exception
return|;
block|}
block|}
end_class

end_unit

