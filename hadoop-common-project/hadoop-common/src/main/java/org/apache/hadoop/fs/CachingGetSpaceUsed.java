begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

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
name|Closeable
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
name|ThreadLocalRandom
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
name|AtomicBoolean
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
comment|/**  * Interface for class that can tell estimate much space  * is used in a directory.  *<p>  * The implementor is fee to cache space used. As such there  * are methods to update the cached value with any known changes.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|LimitedPrivate
argument_list|(
block|{
literal|"HDFS"
block|,
literal|"MapReduce"
block|}
argument_list|)
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|CachingGetSpaceUsed
specifier|public
specifier|abstract
class|class
name|CachingGetSpaceUsed
implements|implements
name|Closeable
implements|,
name|GetSpaceUsed
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CachingGetSpaceUsed
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|used
specifier|protected
specifier|final
name|AtomicLong
name|used
init|=
operator|new
name|AtomicLong
argument_list|()
decl_stmt|;
DECL|field|running
specifier|private
specifier|final
name|AtomicBoolean
name|running
init|=
operator|new
name|AtomicBoolean
argument_list|(
literal|true
argument_list|)
decl_stmt|;
DECL|field|refreshInterval
specifier|private
specifier|final
name|long
name|refreshInterval
decl_stmt|;
DECL|field|jitter
specifier|private
specifier|final
name|long
name|jitter
decl_stmt|;
DECL|field|dirPath
specifier|private
specifier|final
name|String
name|dirPath
decl_stmt|;
DECL|field|refreshUsed
specifier|private
name|Thread
name|refreshUsed
decl_stmt|;
comment|/**    * This is the constructor used by the builder.    * All overriding classes should implement this.    */
DECL|method|CachingGetSpaceUsed (CachingGetSpaceUsed.Builder builder)
specifier|public
name|CachingGetSpaceUsed
parameter_list|(
name|CachingGetSpaceUsed
operator|.
name|Builder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|builder
operator|.
name|getPath
argument_list|()
argument_list|,
name|builder
operator|.
name|getInterval
argument_list|()
argument_list|,
name|builder
operator|.
name|getJitter
argument_list|()
argument_list|,
name|builder
operator|.
name|getInitialUsed
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Keeps track of disk usage.    *    * @param path        the path to check disk usage in    * @param interval    refresh the disk usage at this interval    * @param initialUsed use this value until next refresh    * @throws IOException if we fail to refresh the disk usage    */
DECL|method|CachingGetSpaceUsed (File path, long interval, long jitter, long initialUsed)
name|CachingGetSpaceUsed
parameter_list|(
name|File
name|path
parameter_list|,
name|long
name|interval
parameter_list|,
name|long
name|jitter
parameter_list|,
name|long
name|initialUsed
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|dirPath
operator|=
name|path
operator|.
name|getCanonicalPath
argument_list|()
expr_stmt|;
name|this
operator|.
name|refreshInterval
operator|=
name|interval
expr_stmt|;
name|this
operator|.
name|jitter
operator|=
name|jitter
expr_stmt|;
name|this
operator|.
name|used
operator|.
name|set
argument_list|(
name|initialUsed
argument_list|)
expr_stmt|;
block|}
DECL|method|init ()
name|void
name|init
parameter_list|()
block|{
if|if
condition|(
name|used
operator|.
name|get
argument_list|()
operator|<
literal|0
condition|)
block|{
name|used
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|refresh
argument_list|()
expr_stmt|;
block|}
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
name|RefreshThread
argument_list|(
name|this
argument_list|)
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
else|else
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|refreshUsed
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|refresh ()
specifier|protected
specifier|abstract
name|void
name|refresh
parameter_list|()
function_decl|;
comment|/**    * @return an estimate of space used in the directory path.    */
DECL|method|getUsed ()
annotation|@
name|Override
specifier|public
name|long
name|getUsed
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|Math
operator|.
name|max
argument_list|(
name|used
operator|.
name|get
argument_list|()
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**    * @return The directory path being monitored.    */
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
comment|/**    * Increment the cached value of used space.    */
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
comment|/**    * Is the background thread running.    */
DECL|method|running ()
name|boolean
name|running
parameter_list|()
block|{
return|return
name|running
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    * How long in between runs of the background refresh.    */
DECL|method|getRefreshInterval ()
name|long
name|getRefreshInterval
parameter_list|()
block|{
return|return
name|refreshInterval
return|;
block|}
comment|/**    * Reset the current used data amount. This should be called    * when the cached value is re-computed.    *    * @param usedValue new value that should be the disk usage.    */
DECL|method|setUsed (long usedValue)
specifier|protected
name|void
name|setUsed
parameter_list|(
name|long
name|usedValue
parameter_list|)
block|{
name|this
operator|.
name|used
operator|.
name|set
argument_list|(
name|usedValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|running
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
if|if
condition|(
name|refreshUsed
operator|!=
literal|null
condition|)
block|{
name|refreshUsed
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|RefreshThread
specifier|private
specifier|static
specifier|final
class|class
name|RefreshThread
implements|implements
name|Runnable
block|{
DECL|field|spaceUsed
specifier|final
name|CachingGetSpaceUsed
name|spaceUsed
decl_stmt|;
DECL|method|RefreshThread (CachingGetSpaceUsed spaceUsed)
name|RefreshThread
parameter_list|(
name|CachingGetSpaceUsed
name|spaceUsed
parameter_list|)
block|{
name|this
operator|.
name|spaceUsed
operator|=
name|spaceUsed
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
name|spaceUsed
operator|.
name|running
argument_list|()
condition|)
block|{
try|try
block|{
name|long
name|refreshInterval
init|=
name|spaceUsed
operator|.
name|refreshInterval
decl_stmt|;
if|if
condition|(
name|spaceUsed
operator|.
name|jitter
operator|>
literal|0
condition|)
block|{
name|long
name|jitter
init|=
name|spaceUsed
operator|.
name|jitter
decl_stmt|;
comment|// add/subtract the jitter.
name|refreshInterval
operator|+=
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextLong
argument_list|(
operator|-
name|jitter
argument_list|,
name|jitter
argument_list|)
expr_stmt|;
block|}
comment|// Make sure that after the jitter we didn't end up at 0.
name|refreshInterval
operator|=
name|Math
operator|.
name|max
argument_list|(
name|refreshInterval
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|refreshInterval
argument_list|)
expr_stmt|;
comment|// update the used variable
name|spaceUsed
operator|.
name|refresh
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
literal|"Thread Interrupted waiting to refresh disk information"
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

