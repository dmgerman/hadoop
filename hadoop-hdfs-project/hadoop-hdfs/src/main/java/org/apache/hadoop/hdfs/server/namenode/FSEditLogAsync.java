begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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
name|concurrent
operator|.
name|ArrayBlockingQueue
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
name|BlockingQueue
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
name|Semaphore
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
name|ipc
operator|.
name|Server
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
name|ExitUtil
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_class
DECL|class|FSEditLogAsync
class|class
name|FSEditLogAsync
extends|extends
name|FSEditLog
implements|implements
name|Runnable
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
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
comment|// use separate mutex to avoid possible deadlock when stopping the thread.
DECL|field|syncThreadLock
specifier|private
specifier|final
name|Object
name|syncThreadLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
DECL|field|syncThread
specifier|private
name|Thread
name|syncThread
decl_stmt|;
DECL|field|THREAD_EDIT
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|Edit
argument_list|>
name|THREAD_EDIT
init|=
operator|new
name|ThreadLocal
argument_list|<
name|Edit
argument_list|>
argument_list|()
decl_stmt|;
comment|// requires concurrent access from caller threads and syncing thread.
DECL|field|editPendingQ
specifier|private
specifier|final
name|BlockingQueue
argument_list|<
name|Edit
argument_list|>
name|editPendingQ
init|=
operator|new
name|ArrayBlockingQueue
argument_list|<
name|Edit
argument_list|>
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
comment|// only accessed by syncing thread so no synchronization required.
comment|// queue is unbounded because it's effectively limited by the size
comment|// of the edit log buffer - ie. a sync will eventually be forced.
DECL|field|syncWaitQ
specifier|private
specifier|final
name|Deque
argument_list|<
name|Edit
argument_list|>
name|syncWaitQ
init|=
operator|new
name|ArrayDeque
argument_list|<
name|Edit
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|FSEditLogAsync (Configuration conf, NNStorage storage, List<URI> editsDirs)
name|FSEditLogAsync
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|NNStorage
name|storage
parameter_list|,
name|List
argument_list|<
name|URI
argument_list|>
name|editsDirs
parameter_list|)
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|storage
argument_list|,
name|editsDirs
argument_list|)
expr_stmt|;
comment|// op instances cannot be shared due to queuing for background thread.
name|cache
operator|.
name|disableCache
argument_list|()
expr_stmt|;
block|}
DECL|method|isSyncThreadAlive ()
specifier|private
name|boolean
name|isSyncThreadAlive
parameter_list|()
block|{
synchronized|synchronized
init|(
name|syncThreadLock
init|)
block|{
return|return
name|syncThread
operator|!=
literal|null
operator|&&
name|syncThread
operator|.
name|isAlive
argument_list|()
return|;
block|}
block|}
DECL|method|startSyncThread ()
specifier|private
name|void
name|startSyncThread
parameter_list|()
block|{
synchronized|synchronized
init|(
name|syncThreadLock
init|)
block|{
if|if
condition|(
operator|!
name|isSyncThreadAlive
argument_list|()
condition|)
block|{
name|syncThread
operator|=
operator|new
name|Thread
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|syncThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|stopSyncThread ()
specifier|private
name|void
name|stopSyncThread
parameter_list|()
block|{
synchronized|synchronized
init|(
name|syncThreadLock
init|)
block|{
if|if
condition|(
name|syncThread
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|syncThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|syncThread
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
comment|// we're quitting anyway.
block|}
finally|finally
block|{
name|syncThread
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|VisibleForTesting
annotation|@
name|Override
DECL|method|restart ()
specifier|public
name|void
name|restart
parameter_list|()
block|{
name|stopSyncThread
argument_list|()
expr_stmt|;
name|startSyncThread
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|openForWrite (int layoutVersion)
name|void
name|openForWrite
parameter_list|(
name|int
name|layoutVersion
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|startSyncThread
argument_list|()
expr_stmt|;
name|super
operator|.
name|openForWrite
argument_list|(
name|layoutVersion
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|stopSyncThread
argument_list|()
expr_stmt|;
throw|throw
name|ioe
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|stopSyncThread
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|logEdit (final FSEditLogOp op)
name|void
name|logEdit
parameter_list|(
specifier|final
name|FSEditLogOp
name|op
parameter_list|)
block|{
name|Edit
name|edit
init|=
name|getEditInstance
argument_list|(
name|op
argument_list|)
decl_stmt|;
name|THREAD_EDIT
operator|.
name|set
argument_list|(
name|edit
argument_list|)
expr_stmt|;
name|enqueueEdit
argument_list|(
name|edit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|logSync ()
specifier|public
name|void
name|logSync
parameter_list|()
block|{
name|Edit
name|edit
init|=
name|THREAD_EDIT
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|edit
operator|!=
literal|null
condition|)
block|{
comment|// do NOT remove to avoid expunge& rehash penalties.
name|THREAD_EDIT
operator|.
name|set
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"logSync "
operator|+
name|edit
argument_list|)
expr_stmt|;
block|}
name|edit
operator|.
name|logSyncWait
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|logSyncAll ()
specifier|public
name|void
name|logSyncAll
parameter_list|()
block|{
comment|// doesn't actually log anything, just ensures that the queues are
comment|// drained when it returns.
name|Edit
name|edit
init|=
operator|new
name|SyncEdit
argument_list|(
name|this
argument_list|,
literal|null
argument_list|)
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|logEdit
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
decl_stmt|;
name|enqueueEdit
argument_list|(
name|edit
argument_list|)
expr_stmt|;
name|edit
operator|.
name|logSyncWait
argument_list|()
expr_stmt|;
block|}
comment|// draining permits is intended to provide a high priority reservation.
comment|// however, release of outstanding permits must be postponed until
comment|// drained permits are restored to avoid starvation.  logic has some races
comment|// but is good enough to serve its purpose.
DECL|field|overflowMutex
specifier|private
name|Semaphore
name|overflowMutex
init|=
operator|new
name|Semaphore
argument_list|(
literal|8
argument_list|)
block|{
specifier|private
name|AtomicBoolean
name|draining
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
specifier|private
name|AtomicInteger
name|pendingReleases
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|drainPermits
parameter_list|()
block|{
name|draining
operator|.
name|set
argument_list|(
literal|true
argument_list|)
expr_stmt|;
return|return
name|super
operator|.
name|drainPermits
argument_list|()
return|;
block|}
comment|// while draining, count the releases until release(int)
specifier|private
name|void
name|tryRelease
parameter_list|(
name|int
name|permits
parameter_list|)
block|{
name|pendingReleases
operator|.
name|getAndAdd
argument_list|(
name|permits
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|draining
operator|.
name|get
argument_list|()
condition|)
block|{
name|super
operator|.
name|release
argument_list|(
name|pendingReleases
operator|.
name|getAndSet
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|()
block|{
name|tryRelease
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|release
parameter_list|(
name|int
name|permits
parameter_list|)
block|{
name|draining
operator|.
name|set
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|tryRelease
argument_list|(
name|permits
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
DECL|method|enqueueEdit (Edit edit)
specifier|private
name|void
name|enqueueEdit
parameter_list|(
name|Edit
name|edit
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"logEdit "
operator|+
name|edit
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// not checking for overflow yet to avoid penalizing performance of
comment|// the common case.  if there is persistent overflow, a mutex will be
comment|// use to throttle contention on the queue.
if|if
condition|(
operator|!
name|editPendingQ
operator|.
name|offer
argument_list|(
name|edit
argument_list|)
condition|)
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|isSyncThreadAlive
argument_list|()
argument_list|,
literal|"sync thread is not alive"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
condition|)
block|{
comment|// if queue is full, synchronized caller must immediately relinquish
comment|// the monitor before re-offering to avoid deadlock with sync thread
comment|// which needs the monitor to write transactions.
name|int
name|permits
init|=
name|overflowMutex
operator|.
name|drainPermits
argument_list|()
decl_stmt|;
try|try
block|{
do|do
block|{
name|this
operator|.
name|wait
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// will be notified by next logSync.
block|}
do|while
condition|(
operator|!
name|editPendingQ
operator|.
name|offer
argument_list|(
name|edit
argument_list|)
condition|)
do|;
block|}
finally|finally
block|{
name|overflowMutex
operator|.
name|release
argument_list|(
name|permits
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// mutex will throttle contention during persistent overflow.
name|overflowMutex
operator|.
name|acquire
argument_list|()
expr_stmt|;
try|try
block|{
name|editPendingQ
operator|.
name|put
argument_list|(
name|edit
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|overflowMutex
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
comment|// should never happen!  failure to enqueue an edit is fatal
name|terminate
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|dequeueEdit ()
specifier|private
name|Edit
name|dequeueEdit
parameter_list|()
throws|throws
name|InterruptedException
block|{
comment|// only block for next edit if no pending syncs.
return|return
name|syncWaitQ
operator|.
name|isEmpty
argument_list|()
condition|?
name|editPendingQ
operator|.
name|take
argument_list|()
else|:
name|editPendingQ
operator|.
name|poll
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|boolean
name|doSync
decl_stmt|;
name|Edit
name|edit
init|=
name|dequeueEdit
argument_list|()
decl_stmt|;
if|if
condition|(
name|edit
operator|!=
literal|null
condition|)
block|{
comment|// sync if requested by edit log.
name|doSync
operator|=
name|edit
operator|.
name|logEdit
argument_list|()
expr_stmt|;
name|syncWaitQ
operator|.
name|add
argument_list|(
name|edit
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// sync when editq runs dry, but have edits pending a sync.
name|doSync
operator|=
operator|!
name|syncWaitQ
operator|.
name|isEmpty
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|doSync
condition|)
block|{
comment|// normally edit log exceptions cause the NN to terminate, but tests
comment|// relying on ExitUtil.terminate need to see the exception.
name|RuntimeException
name|syncEx
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logSync
argument_list|(
name|getLastWrittenTxId
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
name|syncEx
operator|=
name|ex
expr_stmt|;
block|}
while|while
condition|(
operator|(
name|edit
operator|=
name|syncWaitQ
operator|.
name|poll
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|edit
operator|.
name|logSyncNotify
argument_list|(
name|syncEx
argument_list|)
expr_stmt|;
block|}
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
name|info
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" was interrupted, exiting"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|terminate
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|terminate (Throwable t)
specifier|private
name|void
name|terminate
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|String
name|message
init|=
literal|"Exception while edit logging: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|message
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|ExitUtil
operator|.
name|terminate
argument_list|(
literal|1
argument_list|,
name|message
argument_list|)
expr_stmt|;
block|}
DECL|method|getEditInstance (FSEditLogOp op)
specifier|private
name|Edit
name|getEditInstance
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
block|{
specifier|final
name|Edit
name|edit
decl_stmt|;
specifier|final
name|Server
operator|.
name|Call
name|rpcCall
init|=
name|Server
operator|.
name|getCurCall
argument_list|()
operator|.
name|get
argument_list|()
decl_stmt|;
comment|// only rpc calls not explicitly sync'ed on the log will be async.
if|if
condition|(
name|rpcCall
operator|!=
literal|null
operator|&&
operator|!
name|Thread
operator|.
name|holdsLock
argument_list|(
name|this
argument_list|)
condition|)
block|{
name|edit
operator|=
operator|new
name|RpcEdit
argument_list|(
name|this
argument_list|,
name|op
argument_list|,
name|rpcCall
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|edit
operator|=
operator|new
name|SyncEdit
argument_list|(
name|this
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
return|return
name|edit
return|;
block|}
DECL|class|Edit
specifier|private
specifier|abstract
specifier|static
class|class
name|Edit
block|{
DECL|field|log
specifier|final
name|FSEditLog
name|log
decl_stmt|;
DECL|field|op
specifier|final
name|FSEditLogOp
name|op
decl_stmt|;
DECL|method|Edit (FSEditLog log, FSEditLogOp op)
name|Edit
parameter_list|(
name|FSEditLog
name|log
parameter_list|,
name|FSEditLogOp
name|op
parameter_list|)
block|{
name|this
operator|.
name|log
operator|=
name|log
expr_stmt|;
name|this
operator|.
name|op
operator|=
name|op
expr_stmt|;
block|}
comment|// return whether edit log wants to sync.
DECL|method|logEdit ()
name|boolean
name|logEdit
parameter_list|()
block|{
return|return
name|log
operator|.
name|doEditTransaction
argument_list|(
name|op
argument_list|)
return|;
block|}
comment|// wait for background thread to finish syncing.
DECL|method|logSyncWait ()
specifier|abstract
name|void
name|logSyncWait
parameter_list|()
function_decl|;
comment|// wake up the thread in logSyncWait.
DECL|method|logSyncNotify (RuntimeException ex)
specifier|abstract
name|void
name|logSyncNotify
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
function_decl|;
block|}
comment|// the calling thread is synchronously waiting for the edit to complete.
DECL|class|SyncEdit
specifier|private
specifier|static
class|class
name|SyncEdit
extends|extends
name|Edit
block|{
DECL|field|lock
specifier|private
specifier|final
name|Object
name|lock
decl_stmt|;
DECL|field|done
specifier|private
name|boolean
name|done
init|=
literal|false
decl_stmt|;
DECL|field|syncEx
specifier|private
name|RuntimeException
name|syncEx
decl_stmt|;
DECL|method|SyncEdit (FSEditLog log, FSEditLogOp op)
name|SyncEdit
parameter_list|(
name|FSEditLog
name|log
parameter_list|,
name|FSEditLogOp
name|op
parameter_list|)
block|{
name|super
argument_list|(
name|log
argument_list|,
name|op
argument_list|)
expr_stmt|;
comment|// if the log is already sync'ed (ex. log rolling), must wait on it to
comment|// avoid deadlock with sync thread.  the fsn lock protects against
comment|// logging during a roll.  else lock on this object to avoid sync
comment|// contention on edit log.
name|lock
operator|=
name|Thread
operator|.
name|holdsLock
argument_list|(
name|log
argument_list|)
condition|?
name|log
else|:
name|this
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|logSyncWait ()
specifier|public
name|void
name|logSyncWait
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
while|while
condition|(
operator|!
name|done
condition|)
block|{
try|try
block|{
name|lock
operator|.
name|wait
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{}
block|}
comment|// only needed by tests that rely on ExitUtil.terminate() since
comment|// normally exceptions terminate the NN.
if|if
condition|(
name|syncEx
operator|!=
literal|null
condition|)
block|{
name|syncEx
operator|.
name|fillInStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|syncEx
throw|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|logSyncNotify (RuntimeException ex)
specifier|public
name|void
name|logSyncNotify
parameter_list|(
name|RuntimeException
name|ex
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lock
init|)
block|{
name|done
operator|=
literal|true
expr_stmt|;
name|syncEx
operator|=
name|ex
expr_stmt|;
name|lock
operator|.
name|notifyAll
argument_list|()
expr_stmt|;
block|}
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
literal|"["
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" op:"
operator|+
name|op
operator|+
literal|"]"
return|;
block|}
block|}
comment|// the calling rpc thread will return immediately from logSync but the
comment|// rpc response will not be sent until the edit is durable.
DECL|class|RpcEdit
specifier|private
specifier|static
class|class
name|RpcEdit
extends|extends
name|Edit
block|{
DECL|field|call
specifier|private
specifier|final
name|Server
operator|.
name|Call
name|call
decl_stmt|;
DECL|method|RpcEdit (FSEditLog log, FSEditLogOp op, Server.Call call)
name|RpcEdit
parameter_list|(
name|FSEditLog
name|log
parameter_list|,
name|FSEditLogOp
name|op
parameter_list|,
name|Server
operator|.
name|Call
name|call
parameter_list|)
block|{
name|super
argument_list|(
name|log
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
name|call
operator|.
name|postponeResponse
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|logSyncWait ()
specifier|public
name|void
name|logSyncWait
parameter_list|()
block|{
comment|// logSync is a no-op to immediately free up rpc handlers.  the
comment|// response is sent when the sync thread calls syncNotify.
block|}
annotation|@
name|Override
DECL|method|logSyncNotify (RuntimeException syncEx)
specifier|public
name|void
name|logSyncNotify
parameter_list|(
name|RuntimeException
name|syncEx
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|syncEx
operator|==
literal|null
condition|)
block|{
name|call
operator|.
name|sendResponse
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|call
operator|.
name|abortResponse
argument_list|(
name|syncEx
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{}
comment|// don't care if not sent.
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
literal|"["
operator|+
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|" op:"
operator|+
name|op
operator|+
literal|" call:"
operator|+
name|call
operator|+
literal|"]"
return|;
block|}
block|}
block|}
end_class

end_unit

