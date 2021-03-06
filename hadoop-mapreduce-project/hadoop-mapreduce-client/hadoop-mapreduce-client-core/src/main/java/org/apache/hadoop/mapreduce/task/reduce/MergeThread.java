begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.task.reduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|task
operator|.
name|reduce
package|;
end_package

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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|AtomicInteger
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

begin_class
DECL|class|MergeThread
specifier|abstract
class|class
name|MergeThread
parameter_list|<
name|T
parameter_list|,
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|Thread
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
name|MergeThread
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|numPending
specifier|private
name|AtomicInteger
name|numPending
init|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
decl_stmt|;
DECL|field|pendingToBeMerged
specifier|private
name|LinkedList
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
name|pendingToBeMerged
decl_stmt|;
DECL|field|manager
specifier|protected
specifier|final
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|manager
decl_stmt|;
DECL|field|reporter
specifier|private
specifier|final
name|ExceptionReporter
name|reporter
decl_stmt|;
DECL|field|closed
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
DECL|field|mergeFactor
specifier|private
specifier|final
name|int
name|mergeFactor
decl_stmt|;
DECL|method|MergeThread (MergeManagerImpl<K,V> manager, int mergeFactor, ExceptionReporter reporter)
specifier|public
name|MergeThread
parameter_list|(
name|MergeManagerImpl
argument_list|<
name|K
argument_list|,
name|V
argument_list|>
name|manager
parameter_list|,
name|int
name|mergeFactor
parameter_list|,
name|ExceptionReporter
name|reporter
parameter_list|)
block|{
name|this
operator|.
name|pendingToBeMerged
operator|=
operator|new
name|LinkedList
argument_list|<
name|List
argument_list|<
name|T
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|manager
operator|=
name|manager
expr_stmt|;
name|this
operator|.
name|mergeFactor
operator|=
name|mergeFactor
expr_stmt|;
name|this
operator|.
name|reporter
operator|=
name|reporter
expr_stmt|;
block|}
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|closed
operator|=
literal|true
expr_stmt|;
name|waitForMerge
argument_list|()
expr_stmt|;
name|interrupt
argument_list|()
expr_stmt|;
block|}
DECL|method|startMerge (Set<T> inputs)
specifier|public
name|void
name|startMerge
parameter_list|(
name|Set
argument_list|<
name|T
argument_list|>
name|inputs
parameter_list|)
block|{
if|if
condition|(
operator|!
name|closed
condition|)
block|{
name|numPending
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|List
argument_list|<
name|T
argument_list|>
name|toMergeInputs
init|=
operator|new
name|ArrayList
argument_list|<
name|T
argument_list|>
argument_list|()
decl_stmt|;
name|Iterator
argument_list|<
name|T
argument_list|>
name|iter
init|=
name|inputs
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|ctr
init|=
literal|0
init|;
name|iter
operator|.
name|hasNext
argument_list|()
operator|&&
name|ctr
operator|<
name|mergeFactor
condition|;
operator|++
name|ctr
control|)
block|{
name|toMergeInputs
operator|.
name|add
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|iter
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|getName
argument_list|()
operator|+
literal|": Starting merge with "
operator|+
name|toMergeInputs
operator|.
name|size
argument_list|()
operator|+
literal|" segments, while ignoring "
operator|+
name|inputs
operator|.
name|size
argument_list|()
operator|+
literal|" segments"
argument_list|)
expr_stmt|;
synchronized|synchronized
init|(
name|pendingToBeMerged
init|)
block|{
name|pendingToBeMerged
operator|.
name|addLast
argument_list|(
name|toMergeInputs
argument_list|)
expr_stmt|;
name|pendingToBeMerged
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|waitForMerge ()
specifier|public
specifier|synchronized
name|void
name|waitForMerge
parameter_list|()
throws|throws
name|InterruptedException
block|{
while|while
condition|(
name|numPending
operator|.
name|get
argument_list|()
operator|>
literal|0
condition|)
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|List
argument_list|<
name|T
argument_list|>
name|inputs
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// Wait for notification to start the merge...
synchronized|synchronized
init|(
name|pendingToBeMerged
init|)
block|{
while|while
condition|(
name|pendingToBeMerged
operator|.
name|size
argument_list|()
operator|<=
literal|0
condition|)
block|{
name|pendingToBeMerged
operator|.
name|wait
argument_list|()
expr_stmt|;
block|}
comment|// Pickup the inputs to merge.
name|inputs
operator|=
name|pendingToBeMerged
operator|.
name|removeFirst
argument_list|()
expr_stmt|;
block|}
comment|// Merge
name|merge
argument_list|(
name|inputs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|numPending
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|numPending
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|reporter
operator|.
name|reportException
argument_list|(
name|t
argument_list|)
expr_stmt|;
return|return;
block|}
finally|finally
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
name|numPending
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
DECL|method|merge (List<T> inputs)
specifier|public
specifier|abstract
name|void
name|merge
parameter_list|(
name|List
argument_list|<
name|T
argument_list|>
name|inputs
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit

