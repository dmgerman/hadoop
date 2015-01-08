begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|io
operator|.
name|Closeable
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
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * A simplified StopWatch implementation which can measure times in nanoseconds.  */
end_comment

begin_class
DECL|class|StopWatch
specifier|public
class|class
name|StopWatch
implements|implements
name|Closeable
block|{
DECL|field|isStarted
specifier|private
name|boolean
name|isStarted
decl_stmt|;
DECL|field|startNanos
specifier|private
name|long
name|startNanos
decl_stmt|;
DECL|field|currentElapsedNanos
specifier|private
name|long
name|currentElapsedNanos
decl_stmt|;
DECL|method|StopWatch ()
specifier|public
name|StopWatch
parameter_list|()
block|{   }
comment|/**    * The method is used to find out if the StopWatch is started.    * @return boolean If the StopWatch is started.    */
DECL|method|isRunning ()
specifier|public
name|boolean
name|isRunning
parameter_list|()
block|{
return|return
name|isStarted
return|;
block|}
comment|/**    * Start to measure times and make the state of stopwatch running.    * @return this instance of StopWatch.    */
DECL|method|start ()
specifier|public
name|StopWatch
name|start
parameter_list|()
block|{
if|if
condition|(
name|isStarted
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"StopWatch is already running"
argument_list|)
throw|;
block|}
name|isStarted
operator|=
literal|true
expr_stmt|;
name|startNanos
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Stop elapsed time and make the state of stopwatch stop.    * @return this instance of StopWatch.    */
DECL|method|stop ()
specifier|public
name|StopWatch
name|stop
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isStarted
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"StopWatch is already stopped"
argument_list|)
throw|;
block|}
name|long
name|now
init|=
name|System
operator|.
name|nanoTime
argument_list|()
decl_stmt|;
name|isStarted
operator|=
literal|false
expr_stmt|;
name|currentElapsedNanos
operator|+=
name|now
operator|-
name|startNanos
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * Reset elapsed time to zero and make the state of stopwatch stop.    * @return this instance of StopWatch.    */
DECL|method|reset ()
specifier|public
name|StopWatch
name|reset
parameter_list|()
block|{
name|currentElapsedNanos
operator|=
literal|0
expr_stmt|;
name|isStarted
operator|=
literal|false
expr_stmt|;
return|return
name|this
return|;
block|}
comment|/**    * @return current elapsed time in specified timeunit.    */
DECL|method|now (TimeUnit timeUnit)
specifier|public
name|long
name|now
parameter_list|(
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
return|return
name|timeUnit
operator|.
name|convert
argument_list|(
name|now
argument_list|()
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
return|;
block|}
comment|/**    * @return current elapsed time in nanosecond.    */
DECL|method|now ()
specifier|public
name|long
name|now
parameter_list|()
block|{
return|return
name|isStarted
condition|?
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startNanos
operator|+
name|currentElapsedNanos
else|:
name|currentElapsedNanos
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
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|now
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|isStarted
condition|)
block|{
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

