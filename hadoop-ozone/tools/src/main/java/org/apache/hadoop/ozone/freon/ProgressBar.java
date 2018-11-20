begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.freon
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|freon
package|;
end_package

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * Creates and runs a ProgressBar in new Thread which gets printed on  * the provided PrintStream.  */
end_comment

begin_class
DECL|class|ProgressBar
specifier|public
class|class
name|ProgressBar
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ProgressBar
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|REFRESH_INTERVAL
specifier|private
specifier|static
specifier|final
name|long
name|REFRESH_INTERVAL
init|=
literal|1000L
decl_stmt|;
DECL|field|maxValue
specifier|private
specifier|final
name|long
name|maxValue
decl_stmt|;
DECL|field|currentValue
specifier|private
specifier|final
name|Supplier
argument_list|<
name|Long
argument_list|>
name|currentValue
decl_stmt|;
DECL|field|progressBar
specifier|private
specifier|final
name|Thread
name|progressBar
decl_stmt|;
DECL|field|running
specifier|private
specifier|volatile
name|boolean
name|running
decl_stmt|;
DECL|field|startTime
specifier|private
specifier|volatile
name|long
name|startTime
decl_stmt|;
comment|/**    * Creates a new ProgressBar instance which prints the progress on the given    * PrintStream when started.    *    * @param stream to display the progress    * @param maxValue Maximum value of the progress    * @param currentValue Supplier that provides the current value    */
DECL|method|ProgressBar (final PrintStream stream, final Long maxValue, final Supplier<Long> currentValue)
specifier|public
name|ProgressBar
parameter_list|(
specifier|final
name|PrintStream
name|stream
parameter_list|,
specifier|final
name|Long
name|maxValue
parameter_list|,
specifier|final
name|Supplier
argument_list|<
name|Long
argument_list|>
name|currentValue
parameter_list|)
block|{
name|this
operator|.
name|maxValue
operator|=
name|maxValue
expr_stmt|;
name|this
operator|.
name|currentValue
operator|=
name|currentValue
expr_stmt|;
name|this
operator|.
name|progressBar
operator|=
operator|new
name|Thread
argument_list|(
name|getProgressBar
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|running
operator|=
literal|false
expr_stmt|;
block|}
comment|/**    * Starts the ProgressBar in a new Thread.    * This is a non blocking call.    */
DECL|method|start ()
specifier|public
specifier|synchronized
name|void
name|start
parameter_list|()
block|{
if|if
condition|(
operator|!
name|running
condition|)
block|{
name|running
operator|=
literal|true
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|nanoTime
argument_list|()
expr_stmt|;
name|progressBar
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Graceful shutdown, waits for the progress bar to complete.    * This is a blocking call.    */
DECL|method|shutdown ()
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|running
condition|)
block|{
try|try
block|{
name|progressBar
operator|.
name|join
argument_list|()
expr_stmt|;
name|running
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got interrupted while waiting for the progress bar to "
operator|+
literal|"complete."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Terminates the progress bar. This doesn't wait for the progress bar    * to complete.    */
DECL|method|terminate ()
specifier|public
specifier|synchronized
name|void
name|terminate
parameter_list|()
block|{
if|if
condition|(
name|running
condition|)
block|{
try|try
block|{
name|running
operator|=
literal|false
expr_stmt|;
name|progressBar
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Got interrupted while waiting for the progress bar to "
operator|+
literal|"complete."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getProgressBar (final PrintStream stream)
specifier|private
name|Runnable
name|getProgressBar
parameter_list|(
specifier|final
name|PrintStream
name|stream
parameter_list|)
block|{
return|return
parameter_list|()
lambda|->
block|{
name|stream
operator|.
name|println
argument_list|()
expr_stmt|;
while|while
condition|(
name|running
operator|&&
name|currentValue
operator|.
name|get
argument_list|()
operator|<
name|maxValue
condition|)
block|{
name|print
argument_list|(
name|stream
argument_list|,
name|currentValue
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|REFRESH_INTERVAL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"ProgressBar was interrupted."
argument_list|)
expr_stmt|;
block|}
block|}
name|print
argument_list|(
name|stream
argument_list|,
name|maxValue
argument_list|)
expr_stmt|;
name|stream
operator|.
name|println
argument_list|()
expr_stmt|;
name|running
operator|=
literal|false
expr_stmt|;
block|}
return|;
block|}
comment|/**    * Given current value prints the progress bar.    *    * @param value current progress position    */
DECL|method|print (final PrintStream stream, final long value)
specifier|private
name|void
name|print
parameter_list|(
specifier|final
name|PrintStream
name|stream
parameter_list|,
specifier|final
name|long
name|value
parameter_list|)
block|{
name|stream
operator|.
name|print
argument_list|(
literal|'\r'
argument_list|)
expr_stmt|;
name|double
name|percent
init|=
literal|100.0
operator|*
name|value
operator|/
name|maxValue
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|String
operator|.
name|format
argument_list|(
literal|"%.2f"
argument_list|,
name|percent
argument_list|)
operator|+
literal|"% |"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|percent
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'â'
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
literal|100
operator|-
name|percent
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"|  "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|value
operator|+
literal|"/"
operator|+
name|maxValue
argument_list|)
expr_stmt|;
name|long
name|timeInSec
init|=
name|TimeUnit
operator|.
name|SECONDS
operator|.
name|convert
argument_list|(
name|System
operator|.
name|nanoTime
argument_list|()
operator|-
name|startTime
argument_list|,
name|TimeUnit
operator|.
name|NANOSECONDS
argument_list|)
decl_stmt|;
name|String
name|timeToPrint
init|=
name|String
operator|.
name|format
argument_list|(
literal|"%d:%02d:%02d"
argument_list|,
name|timeInSec
operator|/
literal|3600
argument_list|,
operator|(
name|timeInSec
operator|%
literal|3600
operator|)
operator|/
literal|60
argument_list|,
name|timeInSec
operator|%
literal|60
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" Time: "
operator|+
name|timeToPrint
argument_list|)
expr_stmt|;
name|stream
operator|.
name|print
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

