begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.common.tools
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|common
operator|.
name|tools
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_comment
comment|/**  * A duration in milliseconds. This class can be used  * to count time, and to be polled to see if a time limit has  * passed.  */
end_comment

begin_class
DECL|class|Duration
specifier|public
class|class
name|Duration
implements|implements
name|Closeable
block|{
DECL|field|start
DECL|field|finish
specifier|public
name|long
name|start
decl_stmt|,
name|finish
decl_stmt|;
DECL|field|limit
specifier|public
specifier|final
name|long
name|limit
decl_stmt|;
comment|/**    * Create a duration instance with a limit of 0    */
DECL|method|Duration ()
specifier|public
name|Duration
parameter_list|()
block|{
name|this
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a duration with a limit specified in millis    * @param limit duration in milliseconds    */
DECL|method|Duration (long limit)
specifier|public
name|Duration
parameter_list|(
name|long
name|limit
parameter_list|)
block|{
name|this
operator|.
name|limit
operator|=
name|limit
expr_stmt|;
block|}
comment|/**    * Start    * @return self    */
DECL|method|start ()
specifier|public
name|Duration
name|start
parameter_list|()
block|{
name|start
operator|=
name|now
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * The close operation relays to {@link #finish()}.    * Implementing it allows Duration instances to be automatically    * finish()'d in Java7 try blocks for when used in measuring durations.    */
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|final
name|void
name|close
parameter_list|()
block|{
name|finish
argument_list|()
expr_stmt|;
block|}
DECL|method|finish ()
specifier|public
name|void
name|finish
parameter_list|()
block|{
name|finish
operator|=
name|now
argument_list|()
expr_stmt|;
block|}
DECL|method|now ()
specifier|protected
name|long
name|now
parameter_list|()
block|{
return|return
name|System
operator|.
name|nanoTime
argument_list|()
operator|/
literal|1000000
return|;
block|}
DECL|method|getInterval ()
specifier|public
name|long
name|getInterval
parameter_list|()
block|{
return|return
name|finish
operator|-
name|start
return|;
block|}
comment|/**    * return true if the limit has been exceeded    * @return true if a limit was set and the current time    * exceeds it.    */
DECL|method|getLimitExceeded ()
specifier|public
name|boolean
name|getLimitExceeded
parameter_list|()
block|{
return|return
name|limit
operator|>=
literal|0
operator|&&
operator|(
operator|(
name|now
argument_list|()
operator|-
name|start
operator|)
operator|>
name|limit
operator|)
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"Duration"
argument_list|)
expr_stmt|;
if|if
condition|(
name|finish
operator|>=
name|start
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" finished at "
argument_list|)
operator|.
name|append
argument_list|(
name|getInterval
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" millis;"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|start
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" started but not yet finished;"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" unstarted;"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|limit
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" limit: "
argument_list|)
operator|.
name|append
argument_list|(
name|limit
argument_list|)
operator|.
name|append
argument_list|(
literal|" millis"
argument_list|)
expr_stmt|;
if|if
condition|(
name|getLimitExceeded
argument_list|()
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
literal|" -  exceeded"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

