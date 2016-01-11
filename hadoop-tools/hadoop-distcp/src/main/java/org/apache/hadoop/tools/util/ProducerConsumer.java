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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
name|ExecutorService
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
name|Executors
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
name|LinkedBlockingQueue
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

begin_comment
comment|/**  * ProducerConsumer class encapsulates input and output queues and a  * thread-pool of Workers that loop on WorkRequest{@literal<T>} inputQueue  * and for each consumed WorkRequest Workers invoke  * WorkRequestProcessor.processItem() and output resulting  * WorkReport{@literal<R>} to the outputQueue.  */
end_comment

begin_class
DECL|class|ProducerConsumer
specifier|public
class|class
name|ProducerConsumer
parameter_list|<
name|T
parameter_list|,
name|R
parameter_list|>
block|{
DECL|field|LOG
specifier|private
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|ProducerConsumer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|inputQueue
specifier|private
name|LinkedBlockingQueue
argument_list|<
name|WorkRequest
argument_list|<
name|T
argument_list|>
argument_list|>
name|inputQueue
decl_stmt|;
DECL|field|outputQueue
specifier|private
name|LinkedBlockingQueue
argument_list|<
name|WorkReport
argument_list|<
name|R
argument_list|>
argument_list|>
name|outputQueue
decl_stmt|;
DECL|field|executor
specifier|private
name|ExecutorService
name|executor
decl_stmt|;
DECL|field|workCnt
specifier|private
name|AtomicInteger
name|workCnt
decl_stmt|;
comment|/**    *  ProducerConsumer maintains input and output queues and a thread-pool of    *  workers.    *    *  @param numThreads   Size of thread-pool to execute Workers.    */
DECL|method|ProducerConsumer (int numThreads)
specifier|public
name|ProducerConsumer
parameter_list|(
name|int
name|numThreads
parameter_list|)
block|{
name|this
operator|.
name|inputQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|this
operator|.
name|outputQueue
operator|=
operator|new
name|LinkedBlockingQueue
argument_list|<>
argument_list|()
expr_stmt|;
name|executor
operator|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numThreads
argument_list|)
expr_stmt|;
name|workCnt
operator|=
operator|new
name|AtomicInteger
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Add another worker that will consume WorkRequest{@literal<T>} items    *  from input queue, process each item using supplied processor, and for    *  every processed item output WorkReport{@literal<R>} to output queue.    *    *  @param processor  Processor implementing WorkRequestProcessor interface.    *    */
DECL|method|addWorker (WorkRequestProcessor<T, R> processor)
specifier|public
name|void
name|addWorker
parameter_list|(
name|WorkRequestProcessor
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
name|processor
parameter_list|)
block|{
name|executor
operator|.
name|execute
argument_list|(
operator|new
name|Worker
argument_list|(
name|processor
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Shutdown ProducerConsumer worker thread-pool without waiting for    *  completion of any pending work.    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
block|{
name|executor
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|/**    *  Returns number of pending ProducerConsumer items (submitted to input    *  queue for processing via put() method but not yet consumed by take()    *  or blockingTake().    *    *  @return  Number of items in ProducerConsumer (either pending for    *           processing or waiting to be consumed).    */
DECL|method|getWorkCnt ()
specifier|public
name|int
name|getWorkCnt
parameter_list|()
block|{
return|return
name|workCnt
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**    *  Returns true if there are items in ProducerConsumer that are either    *  pending for processing or waiting to be consumed.    *    *  @return  True if there were more items put() to ProducerConsumer than    *           consumed by take() or blockingTake().    */
DECL|method|hasWork ()
specifier|public
name|boolean
name|hasWork
parameter_list|()
block|{
return|return
name|workCnt
operator|.
name|get
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/**    *  Blocking put workRequest to ProducerConsumer input queue.    *    *  @param  workRequest item to be processed.    */
DECL|method|put (WorkRequest<T> workRequest)
specifier|public
name|void
name|put
parameter_list|(
name|WorkRequest
argument_list|<
name|T
argument_list|>
name|workRequest
parameter_list|)
block|{
name|boolean
name|isDone
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|isDone
condition|)
block|{
try|try
block|{
name|inputQueue
operator|.
name|put
argument_list|(
name|workRequest
argument_list|)
expr_stmt|;
name|workCnt
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
name|isDone
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Could not put workRequest into inputQueue. Retrying..."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    *  Blocking take from ProducerConsumer output queue that can be interrupted.    *    *  @return  item returned by processor's processItem().    */
DECL|method|take ()
specifier|public
name|WorkReport
argument_list|<
name|R
argument_list|>
name|take
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|WorkReport
argument_list|<
name|R
argument_list|>
name|report
init|=
name|outputQueue
operator|.
name|take
argument_list|()
decl_stmt|;
name|workCnt
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
return|return
name|report
return|;
block|}
comment|/**    *  Blocking take from ProducerConsumer output queue (catches exceptions and    *  retries forever).    *    *  @return  item returned by processor's processItem().    */
DECL|method|blockingTake ()
specifier|public
name|WorkReport
argument_list|<
name|R
argument_list|>
name|blockingTake
parameter_list|()
block|{
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|WorkReport
argument_list|<
name|R
argument_list|>
name|report
init|=
name|outputQueue
operator|.
name|take
argument_list|()
decl_stmt|;
name|workCnt
operator|.
name|decrementAndGet
argument_list|()
expr_stmt|;
return|return
name|report
return|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retrying in blockingTake..."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|Worker
specifier|private
class|class
name|Worker
implements|implements
name|Runnable
block|{
DECL|field|processor
specifier|private
name|WorkRequestProcessor
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
name|processor
decl_stmt|;
DECL|method|Worker (WorkRequestProcessor<T, R> processor)
specifier|public
name|Worker
parameter_list|(
name|WorkRequestProcessor
argument_list|<
name|T
argument_list|,
name|R
argument_list|>
name|processor
parameter_list|)
block|{
name|this
operator|.
name|processor
operator|=
name|processor
expr_stmt|;
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
try|try
block|{
name|WorkRequest
argument_list|<
name|T
argument_list|>
name|work
init|=
name|inputQueue
operator|.
name|take
argument_list|()
decl_stmt|;
name|WorkReport
argument_list|<
name|R
argument_list|>
name|result
init|=
name|processor
operator|.
name|processItem
argument_list|(
name|work
argument_list|)
decl_stmt|;
name|boolean
name|isDone
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|isDone
condition|)
block|{
try|try
block|{
name|outputQueue
operator|.
name|put
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|isDone
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not put report into outputQueue. Retrying..."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Interrupted while waiting for request from inputQueue."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

