begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|Shell
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

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
name|AtomicLong
import|;
end_import

begin_comment
comment|/** Filesystem disk space usage statistics.  Uses the unix 'du' program*/
end_comment

begin_class
DECL|class|DU
specifier|public
class|class
name|DU
extends|extends
name|Shell
block|{
DECL|field|dirPath
specifier|private
name|String
name|dirPath
decl_stmt|;
DECL|field|used
specifier|private
name|AtomicLong
name|used
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|shouldRun
specifier|private
specifier|volatile
name|boolean
name|shouldRun
init|=
literal|true
decl_stmt|;
DECL|field|refreshUsed
specifier|private
name|Thread
name|refreshUsed
decl_stmt|;
DECL|field|duException
specifier|private
name|IOException
name|duException
init|=
literal|null
decl_stmt|;
DECL|field|refreshInterval
specifier|private
name|long
name|refreshInterval
decl_stmt|;
comment|/**    * Keeps track of disk usage.    * @param path the path to check disk usage in    * @param interval refresh the disk usage at this interval    * @throws IOException if we fail to refresh the disk usage    */
DECL|method|DU (File path, long interval)
specifier|public
name|DU
parameter_list|(
name|File
name|path
parameter_list|,
name|long
name|interval
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|//we set the Shell interval to 0 so it will always run our command
comment|//and use this one to set the thread sleep interval
name|this
operator|.
name|refreshInterval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|dirPath
operator|=
name|path
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
comment|//populate the used variable
name|run
argument_list|()
expr_stmt|;
block|}
comment|/**    * Keeps track of disk usage.    * @param path the path to check disk usage in    * @param conf configuration object    * @throws IOException if we fail to refresh the disk usage    */
DECL|method|DU (File path, Configuration conf)
specifier|public
name|DU
parameter_list|(
name|File
name|path
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|path
argument_list|,
literal|600000L
argument_list|)
expr_stmt|;
comment|//10 minutes default refresh interval
block|}
comment|/**    * This thread refreshes the "used" variable.    *     * Future improvements could be to not permanently    * run this thread, instead run when getUsed is called.    **/
DECL|class|DURefreshThread
class|class
name|DURefreshThread
implements|implements
name|Runnable
block|{
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|refreshInterval
argument_list|)
expr_stmt|;
try|try
block|{
comment|//update the used variable
name|DU
operator|.
name|this
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
synchronized|synchronized
init|(
name|DU
operator|.
name|this
init|)
block|{
comment|//save the latest exception so we can return it in getUsed()
name|duException
operator|=
name|e
expr_stmt|;
block|}
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not get disk usage information"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{         }
block|}
block|}
block|}
comment|/**    * Decrease how much disk space we use.    * @param value decrease by this value    */
DECL|method|decDfsUsed (long value)
specifier|public
name|void
name|decDfsUsed
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|used
operator|.
name|addAndGet
argument_list|(
operator|-
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * Increase how much disk space we use.    * @param value increase by this value    */
DECL|method|incDfsUsed (long value)
specifier|public
name|void
name|incDfsUsed
parameter_list|(
name|long
name|value
parameter_list|)
block|{
name|used
operator|.
name|addAndGet
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**    * @return disk space used     * @throws IOException if the shell command fails    */
DECL|method|getUsed ()
specifier|public
name|long
name|getUsed
parameter_list|()
throws|throws
name|IOException
block|{
comment|//if the updating thread isn't started, update on demand
if|if
condition|(
name|refreshUsed
operator|==
literal|null
condition|)
block|{
name|run
argument_list|()
expr_stmt|;
block|}
else|else
block|{
synchronized|synchronized
init|(
name|DU
operator|.
name|this
init|)
block|{
comment|//if an exception was thrown in the last run, rethrow
if|if
condition|(
name|duException
operator|!=
literal|null
condition|)
block|{
name|IOException
name|tmp
init|=
name|duException
decl_stmt|;
name|duException
operator|=
literal|null
expr_stmt|;
throw|throw
name|tmp
throw|;
block|}
block|}
block|}
return|return
name|used
operator|.
name|longValue
argument_list|()
return|;
block|}
comment|/**    * @return the path of which we're keeping track of disk usage    */
DECL|method|getDirPath ()
specifier|public
name|String
name|getDirPath
parameter_list|()
block|{
return|return
name|dirPath
return|;
block|}
comment|/**    * Start the disk usage checking thread.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
comment|//only start the thread if the interval is sane
if|if
condition|(
name|refreshInterval
operator|>
literal|0
condition|)
block|{
name|refreshUsed
operator|=
operator|new
name|Thread
argument_list|(
operator|new
name|DURefreshThread
argument_list|()
argument_list|,
literal|"refreshUsed-"
operator|+
name|dirPath
argument_list|)
expr_stmt|;
name|refreshUsed
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|refreshUsed
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Shut down the refreshing thread.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|this
operator|.
name|shouldRun
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|refreshUsed
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|refreshUsed
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"du -sk "
operator|+
name|dirPath
operator|+
literal|"\n"
operator|+
name|used
operator|+
literal|"\t"
operator|+
name|dirPath
return|;
block|}
DECL|method|getExecString ()
specifier|protected
name|String
index|[]
name|getExecString
parameter_list|()
block|{
return|return
operator|new
name|String
index|[]
block|{
literal|"du"
block|,
literal|"-sk"
block|,
name|dirPath
block|}
return|;
block|}
DECL|method|parseExecResult (BufferedReader lines)
specifier|protected
name|void
name|parseExecResult
parameter_list|(
name|BufferedReader
name|lines
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|line
init|=
name|lines
operator|.
name|readLine
argument_list|()
decl_stmt|;
if|if
condition|(
name|line
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Expecting a line not the end of stream"
argument_list|)
throw|;
block|}
name|String
index|[]
name|tokens
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
if|if
condition|(
name|tokens
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Illegal du output"
argument_list|)
throw|;
block|}
name|this
operator|.
name|used
operator|.
name|set
argument_list|(
name|Long
operator|.
name|parseLong
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|)
operator|*
literal|1024
argument_list|)
expr_stmt|;
block|}
DECL|method|main (String[] args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|path
init|=
literal|"."
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|path
operator|=
name|args
index|[
literal|0
index|]
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|DU
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
argument_list|,
operator|new
name|Configuration
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

