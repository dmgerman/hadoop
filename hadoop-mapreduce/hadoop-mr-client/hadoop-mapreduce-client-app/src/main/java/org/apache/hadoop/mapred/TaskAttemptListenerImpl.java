begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/** * Licensed to the Apache Software Foundation (ASF) under one * or more contributor license agreements.  See the NOTICE file * distributed with this work for additional information * regarding copyright ownership.  The ASF licenses this file * to you under the Apache License, Version 2.0 (the * "License"); you may not use this file except in compliance * with the License.  You may obtain a copy of the License at * *     http://www.apache.org/licenses/LICENSE-2.0 * * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS, * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. * See the License for the specific language governing permissions and * limitations under the License. */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
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
name|InetSocketAddress
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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|Map
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
name|ProtocolSignature
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
name|RPC
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
name|RPC
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
name|ipc
operator|.
name|VersionedProtocol
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
name|mapred
operator|.
name|SortedRanges
operator|.
name|Range
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
name|mapreduce
operator|.
name|TypeConverter
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
name|mapreduce
operator|.
name|security
operator|.
name|token
operator|.
name|JobTokenSecretManager
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
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskType
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AMConstants
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|AppContext
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|TaskAttemptListener
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|TaskHeartbeatHandler
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Job
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|Task
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptDiagnosticsUpdateEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptEventType
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
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
name|mapreduce
operator|.
name|v2
operator|.
name|app
operator|.
name|job
operator|.
name|event
operator|.
name|TaskAttemptStatusUpdateEvent
operator|.
name|TaskAttemptStatus
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
name|net
operator|.
name|NetUtils
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
name|yarn
operator|.
name|YarnException
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
name|yarn
operator|.
name|service
operator|.
name|CompositeService
import|;
end_import

begin_comment
comment|/**  * This class is responsible for talking to the task umblical.  * It also converts all the old data structures  * to yarn data structures.  *   * This class HAS to be in this package to access package private   * methods/classes.  */
end_comment

begin_class
DECL|class|TaskAttemptListenerImpl
specifier|public
class|class
name|TaskAttemptListenerImpl
extends|extends
name|CompositeService
implements|implements
name|TaskUmbilicalProtocol
implements|,
name|TaskAttemptListener
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TaskAttemptListenerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|context
specifier|private
name|AppContext
name|context
decl_stmt|;
DECL|field|server
specifier|private
name|Server
name|server
decl_stmt|;
DECL|field|taskHeartbeatHandler
specifier|private
name|TaskHeartbeatHandler
name|taskHeartbeatHandler
decl_stmt|;
DECL|field|address
specifier|private
name|InetSocketAddress
name|address
decl_stmt|;
DECL|field|jvmIDToAttemptMap
specifier|private
name|Map
argument_list|<
name|WrappedJvmID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
argument_list|>
name|jvmIDToAttemptMap
init|=
name|Collections
operator|.
name|synchronizedMap
argument_list|(
operator|new
name|HashMap
argument_list|<
name|WrappedJvmID
argument_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
argument_list|>
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|jobTokenSecretManager
specifier|private
name|JobTokenSecretManager
name|jobTokenSecretManager
init|=
literal|null
decl_stmt|;
DECL|method|TaskAttemptListenerImpl (AppContext context, JobTokenSecretManager jobTokenSecretManager)
specifier|public
name|TaskAttemptListenerImpl
parameter_list|(
name|AppContext
name|context
parameter_list|,
name|JobTokenSecretManager
name|jobTokenSecretManager
parameter_list|)
block|{
name|super
argument_list|(
name|TaskAttemptListenerImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|jobTokenSecretManager
operator|=
name|jobTokenSecretManager
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|init (Configuration conf)
specifier|public
name|void
name|init
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|registerHeartbeatHandler
argument_list|()
expr_stmt|;
name|super
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|startRpcServer
argument_list|()
expr_stmt|;
name|super
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|registerHeartbeatHandler ()
specifier|protected
name|void
name|registerHeartbeatHandler
parameter_list|()
block|{
name|taskHeartbeatHandler
operator|=
operator|new
name|TaskHeartbeatHandler
argument_list|(
name|context
operator|.
name|getEventHandler
argument_list|()
argument_list|,
name|context
operator|.
name|getClock
argument_list|()
argument_list|)
expr_stmt|;
name|addService
argument_list|(
name|taskHeartbeatHandler
argument_list|)
expr_stmt|;
block|}
DECL|method|startRpcServer ()
specifier|protected
name|void
name|startRpcServer
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|getConfig
argument_list|()
decl_stmt|;
try|try
block|{
name|server
operator|=
name|RPC
operator|.
name|getServer
argument_list|(
name|TaskUmbilicalProtocol
operator|.
name|class
argument_list|,
name|this
argument_list|,
literal|"0.0.0.0"
argument_list|,
literal|0
argument_list|,
name|conf
operator|.
name|getInt
argument_list|(
name|AMConstants
operator|.
name|AM_TASK_LISTENER_THREADS
argument_list|,
name|AMConstants
operator|.
name|DEFAULT_AM_TASK_LISTENER_THREADS
argument_list|)
argument_list|,
literal|false
argument_list|,
name|conf
argument_list|,
name|jobTokenSecretManager
argument_list|)
expr_stmt|;
name|server
operator|.
name|start
argument_list|()
expr_stmt|;
name|InetSocketAddress
name|listenerAddress
init|=
name|server
operator|.
name|getListenerAddress
argument_list|()
decl_stmt|;
name|this
operator|.
name|address
operator|=
name|NetUtils
operator|.
name|createSocketAddr
argument_list|(
name|listenerAddress
operator|.
name|getAddress
argument_list|()
operator|.
name|getLocalHost
argument_list|()
operator|.
name|getCanonicalHostName
argument_list|()
operator|+
literal|":"
operator|+
name|listenerAddress
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|YarnException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
block|{
name|stopRpcServer
argument_list|()
expr_stmt|;
name|super
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
DECL|method|stopRpcServer ()
specifier|protected
name|void
name|stopRpcServer
parameter_list|()
block|{
name|server
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getAddress ()
specifier|public
name|InetSocketAddress
name|getAddress
parameter_list|()
block|{
return|return
name|address
return|;
block|}
comment|/**    * Child checking whether it can commit.    *     *<br/>    * Commit is a two-phased protocol. First the attempt informs the    * ApplicationMaster that it is    * {@link #commitPending(TaskAttemptID, TaskStatus)}. Then it repeatedly polls    * the ApplicationMaster whether it {@link #canCommit(TaskAttemptID)} This is    * a legacy from the centralized commit protocol handling by the JobTracker.    */
annotation|@
name|Override
DECL|method|canCommit (TaskAttemptID taskAttemptID)
specifier|public
name|boolean
name|canCommit
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Commit go/no-go request from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// An attempt is asking if it can commit its output. This can be decided
comment|// only by the task which is managing the multiple attempts. So redirect the
comment|// request there.
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|context
operator|.
name|getJob
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
decl_stmt|;
name|Task
name|task
init|=
name|job
operator|.
name|getTask
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|task
operator|.
name|canCommit
argument_list|(
name|attemptID
argument_list|)
return|;
block|}
comment|/**    * TaskAttempt is reporting that it is in commit_pending and it is waiting for    * the commit Response    *     *<br/>    * Commit it a two-phased protocol. First the attempt informs the    * ApplicationMaster that it is    * {@link #commitPending(TaskAttemptID, TaskStatus)}. Then it repeatedly polls    * the ApplicationMaster whether it {@link #canCommit(TaskAttemptID)} This is    * a legacy from the centralized commit protocol handling by the JobTracker.    */
annotation|@
name|Override
DECL|method|commitPending (TaskAttemptID taskAttemptID, TaskStatus taskStatsu)
specifier|public
name|void
name|commitPending
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|TaskStatus
name|taskStatsu
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Commit-pending state update from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// An attempt is asking if it can commit its output. This can be decided
comment|// only by the task which is managing the multiple attempts. So redirect the
comment|// request there.
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
comment|//Ignorable TaskStatus? - since a task will send a LastStatusUpdate
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_COMMIT_PENDING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|done (TaskAttemptID taskAttemptID)
specifier|public
name|void
name|done
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Done acknowledgement from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_DONE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fatalError (TaskAttemptID taskAttemptID, String msg)
specifier|public
name|void
name|fatalError
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|String
name|msg
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This happens only in Child and in the Task.
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Task: "
operator|+
name|taskAttemptID
operator|+
literal|" - exited : "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|reportDiagnosticInfo
argument_list|(
name|taskAttemptID
argument_list|,
literal|"Error: "
operator|+
name|msg
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|fsError (TaskAttemptID taskAttemptID, String message)
specifier|public
name|void
name|fsError
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This happens only in Child.
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Task: "
operator|+
name|taskAttemptID
operator|+
literal|" - failed due to FSError: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|reportDiagnosticInfo
argument_list|(
name|taskAttemptID
argument_list|,
literal|"FSError: "
operator|+
name|message
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptEvent
argument_list|(
name|attemptID
argument_list|,
name|TaskAttemptEventType
operator|.
name|TA_FAILMSG
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|shuffleError (TaskAttemptID taskAttemptID, String message)
specifier|public
name|void
name|shuffleError
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: This isn't really used in any MR code. Ask for removal.
block|}
annotation|@
name|Override
DECL|method|getMapCompletionEvents ( JobID jobIdentifier, int fromEventId, int maxEvents, TaskAttemptID taskAttemptID)
specifier|public
name|MapTaskCompletionEventsUpdate
name|getMapCompletionEvents
parameter_list|(
name|JobID
name|jobIdentifier
parameter_list|,
name|int
name|fromEventId
parameter_list|,
name|int
name|maxEvents
parameter_list|,
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MapCompletionEvents request from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
operator|+
literal|". fromEventID "
operator|+
name|fromEventId
operator|+
literal|" maxEvents "
operator|+
name|maxEvents
argument_list|)
expr_stmt|;
comment|// TODO: shouldReset is never used. See TT. Ask for Removal.
name|boolean
name|shouldReset
init|=
literal|false
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptCompletionEvent
index|[]
name|events
init|=
name|context
operator|.
name|getJob
argument_list|(
name|attemptID
operator|.
name|getTaskId
argument_list|()
operator|.
name|getJobId
argument_list|()
argument_list|)
operator|.
name|getTaskAttemptCompletionEvents
argument_list|(
name|fromEventId
argument_list|,
name|maxEvents
argument_list|)
decl_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
comment|// filter the events to return only map completion events in old format
name|List
argument_list|<
name|TaskCompletionEvent
argument_list|>
name|mapEvents
init|=
operator|new
name|ArrayList
argument_list|<
name|TaskCompletionEvent
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptCompletionEvent
name|event
range|:
name|events
control|)
block|{
if|if
condition|(
name|TaskType
operator|.
name|MAP
operator|.
name|equals
argument_list|(
name|event
operator|.
name|getAttemptId
argument_list|()
operator|.
name|getTaskId
argument_list|()
operator|.
name|getTaskType
argument_list|()
argument_list|)
condition|)
block|{
name|mapEvents
operator|.
name|add
argument_list|(
name|TypeConverter
operator|.
name|fromYarn
argument_list|(
name|event
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|MapTaskCompletionEventsUpdate
argument_list|(
name|mapEvents
operator|.
name|toArray
argument_list|(
operator|new
name|TaskCompletionEvent
index|[
literal|0
index|]
argument_list|)
argument_list|,
name|shouldReset
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|ping (TaskAttemptID taskAttemptID)
specifier|public
name|boolean
name|ping
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Ping from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|reportDiagnosticInfo (TaskAttemptID taskAttemptID, String diagnosticInfo)
specifier|public
name|void
name|reportDiagnosticInfo
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|String
name|diagnosticInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Diagnostics report from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
operator|+
literal|": "
operator|+
name|diagnosticInfo
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
comment|// This is mainly used for cases where we want to propagate exception traces
comment|// of tasks that fail.
comment|// This call exists as a hadoop mapreduce legacy wherein all changes in
comment|// counters/progress/phase/output-size are reported through statusUpdate()
comment|// call but not diagnosticInformation.
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptDiagnosticsUpdateEvent
argument_list|(
name|attemptID
argument_list|,
name|diagnosticInfo
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|statusUpdate (TaskAttemptID taskAttemptID, TaskStatus taskStatus)
specifier|public
name|boolean
name|statusUpdate
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|TaskStatus
name|taskStatus
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Status update from "
operator|+
name|taskAttemptID
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|yarnAttemptID
init|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskAttemptID
argument_list|)
decl_stmt|;
name|taskHeartbeatHandler
operator|.
name|receivedPing
argument_list|(
name|yarnAttemptID
argument_list|)
expr_stmt|;
name|TaskAttemptStatus
name|taskAttemptStatus
init|=
operator|new
name|TaskAttemptStatus
argument_list|()
decl_stmt|;
name|taskAttemptStatus
operator|.
name|id
operator|=
name|yarnAttemptID
expr_stmt|;
comment|// Task sends the updated progress to the TT.
name|taskAttemptStatus
operator|.
name|progress
operator|=
name|taskStatus
operator|.
name|getProgress
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Progress of TaskAttempt "
operator|+
name|taskAttemptID
operator|+
literal|" is : "
operator|+
name|taskStatus
operator|.
name|getProgress
argument_list|()
argument_list|)
expr_stmt|;
comment|// Task sends the diagnostic information to the TT
name|taskAttemptStatus
operator|.
name|diagnosticInfo
operator|=
name|taskStatus
operator|.
name|getDiagnosticInfo
argument_list|()
expr_stmt|;
comment|// Task sends the updated state-string to the TT.
name|taskAttemptStatus
operator|.
name|stateString
operator|=
name|taskStatus
operator|.
name|getStateString
argument_list|()
expr_stmt|;
comment|// Set the output-size when map-task finishes. Set by the task itself.
name|taskAttemptStatus
operator|.
name|outputSize
operator|=
name|taskStatus
operator|.
name|getOutputSize
argument_list|()
expr_stmt|;
comment|// Task sends the updated phase to the TT.
name|taskAttemptStatus
operator|.
name|phase
operator|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskStatus
operator|.
name|getPhase
argument_list|()
argument_list|)
expr_stmt|;
comment|// Counters are updated by the task.
name|taskAttemptStatus
operator|.
name|counters
operator|=
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|taskStatus
operator|.
name|getCounters
argument_list|()
argument_list|)
expr_stmt|;
comment|// Map Finish time set by the task (map only)
if|if
condition|(
name|taskStatus
operator|.
name|getIsMap
argument_list|()
operator|&&
name|taskStatus
operator|.
name|getMapFinishTime
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|taskAttemptStatus
operator|.
name|mapFinishTime
operator|=
name|taskStatus
operator|.
name|getMapFinishTime
argument_list|()
expr_stmt|;
block|}
comment|// Shuffle Finish time set by the task (reduce only).
if|if
condition|(
operator|!
name|taskStatus
operator|.
name|getIsMap
argument_list|()
operator|&&
name|taskStatus
operator|.
name|getShuffleFinishTime
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|taskAttemptStatus
operator|.
name|shuffleFinishTime
operator|=
name|taskStatus
operator|.
name|getShuffleFinishTime
argument_list|()
expr_stmt|;
block|}
comment|// Sort finish time set by the task (reduce only).
if|if
condition|(
operator|!
name|taskStatus
operator|.
name|getIsMap
argument_list|()
operator|&&
name|taskStatus
operator|.
name|getSortFinishTime
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|taskAttemptStatus
operator|.
name|sortFinishTime
operator|=
name|taskStatus
operator|.
name|getSortFinishTime
argument_list|()
expr_stmt|;
block|}
comment|// Not Setting the task state. Used by speculation - will be set in TaskAttemptImpl
comment|//taskAttemptStatus.taskState =  TypeConverter.toYarn(taskStatus.getRunState());
comment|//set the fetch failures
if|if
condition|(
name|taskStatus
operator|.
name|getFetchFailedMaps
argument_list|()
operator|!=
literal|null
operator|&&
name|taskStatus
operator|.
name|getFetchFailedMaps
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|taskAttemptStatus
operator|.
name|fetchFailedMaps
operator|=
operator|new
name|ArrayList
argument_list|<
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|TaskAttemptID
name|failedMapId
range|:
name|taskStatus
operator|.
name|getFetchFailedMaps
argument_list|()
control|)
block|{
name|taskAttemptStatus
operator|.
name|fetchFailedMaps
operator|.
name|add
argument_list|(
name|TypeConverter
operator|.
name|toYarn
argument_list|(
name|failedMapId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Task sends the information about the nextRecordRange to the TT
comment|//    TODO: The following are not needed here, but needed to be set somewhere inside AppMaster.
comment|//    taskStatus.getRunState(); // Set by the TT/JT. Transform into a state TODO
comment|//    taskStatus.getStartTime(); // Used to be set by the TaskTracker. This should be set by getTask().
comment|//    taskStatus.getFinishTime(); // Used to be set by TT/JT. Should be set when task finishes
comment|//    // This was used by TT to do counter updates only once every minute. So this
comment|//    // isn't ever changed by the Task itself.
comment|//    taskStatus.getIncludeCounters();
name|context
operator|.
name|getEventHandler
argument_list|()
operator|.
name|handle
argument_list|(
operator|new
name|TaskAttemptStatusUpdateEvent
argument_list|(
name|taskAttemptStatus
operator|.
name|id
argument_list|,
name|taskAttemptStatus
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
DECL|method|getProtocolVersion (String arg0, long arg1)
specifier|public
name|long
name|getProtocolVersion
parameter_list|(
name|String
name|arg0
parameter_list|,
name|long
name|arg1
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|TaskUmbilicalProtocol
operator|.
name|versionID
return|;
block|}
annotation|@
name|Override
DECL|method|reportNextRecordRange (TaskAttemptID taskAttemptID, Range range)
specifier|public
name|void
name|reportNextRecordRange
parameter_list|(
name|TaskAttemptID
name|taskAttemptID
parameter_list|,
name|Range
name|range
parameter_list|)
throws|throws
name|IOException
block|{
comment|// This is used when the feature of skipping records is enabled.
comment|// This call exists as a hadoop mapreduce legacy wherein all changes in
comment|// counters/progress/phase/output-size are reported through statusUpdate()
comment|// call but not the next record range information.
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Not yet implemented."
argument_list|)
throw|;
block|}
annotation|@
name|Override
DECL|method|getTask (JvmContext context)
specifier|public
name|JvmTask
name|getTask
parameter_list|(
name|JvmContext
name|context
parameter_list|)
throws|throws
name|IOException
block|{
comment|// A rough imitation of code from TaskTracker.
name|JVMId
name|jvmId
init|=
name|context
operator|.
name|jvmId
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"JVM with ID : "
operator|+
name|jvmId
operator|+
literal|" asked for a task"
argument_list|)
expr_stmt|;
comment|// TODO: Is it an authorised container to get a task? Otherwise return null.
comment|// TODO: Is the request for task-launch still valid?
comment|// TODO: Child.java's firstTaskID isn't really firstTaskID. Ask for update
comment|// to jobId and task-type.
name|WrappedJvmID
name|wJvmID
init|=
operator|new
name|WrappedJvmID
argument_list|(
name|jvmId
operator|.
name|getJobId
argument_list|()
argument_list|,
name|jvmId
operator|.
name|isMap
argument_list|,
name|jvmId
operator|.
name|getId
argument_list|()
argument_list|)
decl_stmt|;
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
name|task
init|=
name|jvmIDToAttemptMap
operator|.
name|get
argument_list|(
name|wJvmID
argument_list|)
decl_stmt|;
if|if
condition|(
name|task
operator|!=
literal|null
condition|)
block|{
comment|//there may be lag in the attempt getting added here
name|LOG
operator|.
name|info
argument_list|(
literal|"JVM with ID: "
operator|+
name|jvmId
operator|+
literal|" given task: "
operator|+
name|task
operator|.
name|getTaskID
argument_list|()
argument_list|)
expr_stmt|;
name|JvmTask
name|jvmTask
init|=
operator|new
name|JvmTask
argument_list|(
name|task
argument_list|,
literal|false
argument_list|)
decl_stmt|;
comment|//remove the task as it is no more needed and free up the memory
name|jvmIDToAttemptMap
operator|.
name|remove
argument_list|(
name|wJvmID
argument_list|)
expr_stmt|;
return|return
name|jvmTask
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|register (org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId attemptID, org.apache.hadoop.mapred.Task task, WrappedJvmID jvmID)
specifier|public
name|void
name|register
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
parameter_list|,
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|Task
name|task
parameter_list|,
name|WrappedJvmID
name|jvmID
parameter_list|)
block|{
comment|//create the mapping so that it is easy to look up
comment|//when it comes back to ask for Task.
name|jvmIDToAttemptMap
operator|.
name|put
argument_list|(
name|jvmID
argument_list|,
name|task
argument_list|)
expr_stmt|;
comment|//register this attempt
name|taskHeartbeatHandler
operator|.
name|register
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|unregister (org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId attemptID, WrappedJvmID jvmID)
specifier|public
name|void
name|unregister
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|api
operator|.
name|records
operator|.
name|TaskAttemptId
name|attemptID
parameter_list|,
name|WrappedJvmID
name|jvmID
parameter_list|)
block|{
comment|//remove the mapping if not already removed
name|jvmIDToAttemptMap
operator|.
name|remove
argument_list|(
name|jvmID
argument_list|)
expr_stmt|;
comment|//unregister this attempt
name|taskHeartbeatHandler
operator|.
name|unregister
argument_list|(
name|attemptID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProtocolSignature (String protocol, long clientVersion, int clientMethodsHash)
specifier|public
name|ProtocolSignature
name|getProtocolSignature
parameter_list|(
name|String
name|protocol
parameter_list|,
name|long
name|clientVersion
parameter_list|,
name|int
name|clientMethodsHash
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ProtocolSignature
operator|.
name|getProtocolSignature
argument_list|(
name|this
argument_list|,
name|protocol
argument_list|,
name|clientVersion
argument_list|,
name|clientMethodsHash
argument_list|)
return|;
block|}
block|}
end_class

end_unit

