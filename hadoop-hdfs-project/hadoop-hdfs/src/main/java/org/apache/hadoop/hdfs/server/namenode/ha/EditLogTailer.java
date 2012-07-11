begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode.ha
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
operator|.
name|ha
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
name|security
operator|.
name|PrivilegedAction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|hdfs
operator|.
name|DFSConfigKeys
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
name|hdfs
operator|.
name|HAUtil
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
name|hdfs
operator|.
name|protocol
operator|.
name|HdfsConstants
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
name|hdfs
operator|.
name|protocolPB
operator|.
name|NamenodeProtocolPB
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
name|hdfs
operator|.
name|protocolPB
operator|.
name|NamenodeProtocolTranslatorPB
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|EditLogInputException
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|EditLogInputStream
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSEditLog
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSImage
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSNamesystem
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|NameNode
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|NamenodeProtocol
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
name|security
operator|.
name|SecurityUtil
import|;
end_import

begin_import
import|import static
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
name|common
operator|.
name|Util
operator|.
name|now
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|ExitUtil
operator|.
name|terminate
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

begin_comment
comment|/**  * EditLogTailer represents a thread which periodically reads from edits  * journals and applies the transactions contained within to a given  * FSNamesystem.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Evolving
DECL|class|EditLogTailer
specifier|public
class|class
name|EditLogTailer
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|EditLogTailer
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|tailerThread
specifier|private
specifier|final
name|EditLogTailerThread
name|tailerThread
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|namesystem
specifier|private
specifier|final
name|FSNamesystem
name|namesystem
decl_stmt|;
DECL|field|editLog
specifier|private
name|FSEditLog
name|editLog
decl_stmt|;
DECL|field|activeAddr
specifier|private
name|InetSocketAddress
name|activeAddr
decl_stmt|;
DECL|field|cachedActiveProxy
specifier|private
name|NamenodeProtocol
name|cachedActiveProxy
init|=
literal|null
decl_stmt|;
comment|/**    * The last transaction ID at which an edit log roll was initiated.    */
DECL|field|lastRollTriggerTxId
specifier|private
name|long
name|lastRollTriggerTxId
init|=
name|HdfsConstants
operator|.
name|INVALID_TXID
decl_stmt|;
comment|/**    * The highest transaction ID loaded by the Standby.    */
DECL|field|lastLoadedTxnId
specifier|private
name|long
name|lastLoadedTxnId
init|=
name|HdfsConstants
operator|.
name|INVALID_TXID
decl_stmt|;
comment|/**    * The last time we successfully loaded a non-zero number of edits from the    * shared directory.    */
DECL|field|lastLoadTimestamp
specifier|private
name|long
name|lastLoadTimestamp
decl_stmt|;
comment|/**    * How often the Standby should roll edit logs. Since the Standby only reads    * from finalized log segments, the Standby will only be as up-to-date as how    * often the logs are rolled.    */
DECL|field|logRollPeriodMs
specifier|private
name|long
name|logRollPeriodMs
decl_stmt|;
comment|/**    * How often the Standby should check if there are new finalized segment(s)    * available to be read from.    */
DECL|field|sleepTimeMs
specifier|private
name|long
name|sleepTimeMs
decl_stmt|;
DECL|method|EditLogTailer (FSNamesystem namesystem, Configuration conf)
specifier|public
name|EditLogTailer
parameter_list|(
name|FSNamesystem
name|namesystem
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|tailerThread
operator|=
operator|new
name|EditLogTailerThread
argument_list|()
expr_stmt|;
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|namesystem
operator|=
name|namesystem
expr_stmt|;
name|this
operator|.
name|editLog
operator|=
name|namesystem
operator|.
name|getEditLog
argument_list|()
expr_stmt|;
name|lastLoadTimestamp
operator|=
name|now
argument_list|()
expr_stmt|;
name|logRollPeriodMs
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_LOGROLL_PERIOD_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_HA_LOGROLL_PERIOD_DEFAULT
argument_list|)
operator|*
literal|1000
expr_stmt|;
if|if
condition|(
name|logRollPeriodMs
operator|>=
literal|0
condition|)
block|{
name|this
operator|.
name|activeAddr
operator|=
name|getActiveNodeAddress
argument_list|()
expr_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|activeAddr
operator|.
name|getPort
argument_list|()
operator|>
literal|0
argument_list|,
literal|"Active NameNode must have an IPC port configured. "
operator|+
literal|"Got address '%s'"
argument_list|,
name|activeAddr
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Will roll logs on active node at "
operator|+
name|activeAddr
operator|+
literal|" every "
operator|+
operator|(
name|logRollPeriodMs
operator|/
literal|1000
operator|)
operator|+
literal|" seconds."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Not going to trigger log rolls on active node because "
operator|+
name|DFSConfigKeys
operator|.
name|DFS_HA_LOGROLL_PERIOD_KEY
operator|+
literal|" is negative."
argument_list|)
expr_stmt|;
block|}
name|sleepTimeMs
operator|=
name|conf
operator|.
name|getInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_HA_TAILEDITS_PERIOD_DEFAULT
argument_list|)
operator|*
literal|1000
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"logRollPeriodMs="
operator|+
name|logRollPeriodMs
operator|+
literal|" sleepTime="
operator|+
name|sleepTimeMs
argument_list|)
expr_stmt|;
block|}
DECL|method|getActiveNodeAddress ()
specifier|private
name|InetSocketAddress
name|getActiveNodeAddress
parameter_list|()
block|{
name|Configuration
name|activeConf
init|=
name|HAUtil
operator|.
name|getConfForOtherNode
argument_list|(
name|conf
argument_list|)
decl_stmt|;
return|return
name|NameNode
operator|.
name|getServiceAddress
argument_list|(
name|activeConf
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|getActiveNodeProxy ()
specifier|private
name|NamenodeProtocol
name|getActiveNodeProxy
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cachedActiveProxy
operator|==
literal|null
condition|)
block|{
name|NamenodeProtocolPB
name|proxy
init|=
name|RPC
operator|.
name|waitForProxy
argument_list|(
name|NamenodeProtocolPB
operator|.
name|class
argument_list|,
name|RPC
operator|.
name|getProtocolVersion
argument_list|(
name|NamenodeProtocolPB
operator|.
name|class
argument_list|)
argument_list|,
name|activeAddr
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|cachedActiveProxy
operator|=
operator|new
name|NamenodeProtocolTranslatorPB
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
block|}
assert|assert
name|cachedActiveProxy
operator|!=
literal|null
assert|;
return|return
name|cachedActiveProxy
return|;
block|}
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
block|{
name|tailerThread
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|stop ()
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|tailerThread
operator|.
name|setShouldRun
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|tailerThread
operator|.
name|interrupt
argument_list|()
expr_stmt|;
try|try
block|{
name|tailerThread
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
literal|"Edit log tailer thread exited with an exception"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|VisibleForTesting
DECL|method|getEditLog ()
name|FSEditLog
name|getEditLog
parameter_list|()
block|{
return|return
name|editLog
return|;
block|}
annotation|@
name|VisibleForTesting
DECL|method|setEditLog (FSEditLog editLog)
name|void
name|setEditLog
parameter_list|(
name|FSEditLog
name|editLog
parameter_list|)
block|{
name|this
operator|.
name|editLog
operator|=
name|editLog
expr_stmt|;
block|}
DECL|method|catchupDuringFailover ()
specifier|public
name|void
name|catchupDuringFailover
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
name|tailerThread
operator|==
literal|null
operator|||
operator|!
name|tailerThread
operator|.
name|isAlive
argument_list|()
argument_list|,
literal|"Tailer thread should not be running once failover starts"
argument_list|)
expr_stmt|;
try|try
block|{
name|doTailEdits
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
DECL|method|doTailEdits ()
specifier|private
name|void
name|doTailEdits
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
comment|// Write lock needs to be interruptible here because the
comment|// transitionToActive RPC takes the write lock before calling
comment|// tailer.stop() -- so if we're not interruptible, it will
comment|// deadlock.
name|namesystem
operator|.
name|writeLockInterruptibly
argument_list|()
expr_stmt|;
try|try
block|{
name|FSImage
name|image
init|=
name|namesystem
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|long
name|lastTxnId
init|=
name|image
operator|.
name|getLastAppliedTxId
argument_list|()
decl_stmt|;
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
literal|"lastTxnId: "
operator|+
name|lastTxnId
argument_list|)
expr_stmt|;
block|}
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|streams
decl_stmt|;
try|try
block|{
name|streams
operator|=
name|editLog
operator|.
name|selectInputStreams
argument_list|(
name|lastTxnId
operator|+
literal|1
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// This is acceptable. If we try to tail edits in the middle of an edits
comment|// log roll, i.e. the last one has been finalized but the new inprogress
comment|// edits file hasn't been started yet.
name|LOG
operator|.
name|warn
argument_list|(
literal|"Edits tailer failed to find any streams. Will try again "
operator|+
literal|"later."
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
return|return;
block|}
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
literal|"edit streams to load from: "
operator|+
name|streams
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Once we have streams to load, errors encountered are legitimate cause
comment|// for concern, so we don't catch them here. Simple errors reading from
comment|// disk are ignored.
name|long
name|editsLoaded
init|=
literal|0
decl_stmt|;
try|try
block|{
name|editsLoaded
operator|=
name|image
operator|.
name|loadEdits
argument_list|(
name|streams
argument_list|,
name|namesystem
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EditLogInputException
name|elie
parameter_list|)
block|{
name|editsLoaded
operator|=
name|elie
operator|.
name|getNumEditsLoaded
argument_list|()
expr_stmt|;
throw|throw
name|elie
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|editsLoaded
operator|>
literal|0
operator|||
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"Loaded %d edits starting from txid %d "
argument_list|,
name|editsLoaded
argument_list|,
name|lastTxnId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|editsLoaded
operator|>
literal|0
condition|)
block|{
name|lastLoadTimestamp
operator|=
name|now
argument_list|()
expr_stmt|;
block|}
name|lastLoadedTxnId
operator|=
name|image
operator|.
name|getLastAppliedTxId
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|namesystem
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * @return timestamp (in msec) of when we last loaded a non-zero number of edits.    */
DECL|method|getLastLoadTimestamp ()
specifier|public
name|long
name|getLastLoadTimestamp
parameter_list|()
block|{
return|return
name|lastLoadTimestamp
return|;
block|}
comment|/**    * @return true if the configured log roll period has elapsed.    */
DECL|method|tooLongSinceLastLoad ()
specifier|private
name|boolean
name|tooLongSinceLastLoad
parameter_list|()
block|{
return|return
name|logRollPeriodMs
operator|>=
literal|0
operator|&&
operator|(
name|now
argument_list|()
operator|-
name|lastLoadTimestamp
operator|)
operator|>
name|logRollPeriodMs
return|;
block|}
comment|/**    * Trigger the active node to roll its logs.    */
DECL|method|triggerActiveLogRoll ()
specifier|private
name|void
name|triggerActiveLogRoll
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Triggering log roll on remote NameNode "
operator|+
name|activeAddr
argument_list|)
expr_stmt|;
try|try
block|{
name|getActiveNodeProxy
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|lastRollTriggerTxId
operator|=
name|lastLoadedTxnId
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to trigger a roll of the active NN"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * The thread which does the actual work of tailing edits journals and    * applying the transactions to the FSNS.    */
DECL|class|EditLogTailerThread
specifier|private
class|class
name|EditLogTailerThread
extends|extends
name|Thread
block|{
DECL|field|shouldRun
specifier|private
specifier|volatile
name|boolean
name|shouldRun
init|=
literal|true
decl_stmt|;
DECL|method|EditLogTailerThread ()
specifier|private
name|EditLogTailerThread
parameter_list|()
block|{
name|super
argument_list|(
literal|"Edit log tailer"
argument_list|)
expr_stmt|;
block|}
DECL|method|setShouldRun (boolean shouldRun)
specifier|private
name|void
name|setShouldRun
parameter_list|(
name|boolean
name|shouldRun
parameter_list|)
block|{
name|this
operator|.
name|shouldRun
operator|=
name|shouldRun
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
name|SecurityUtil
operator|.
name|doAsLoginUserOrFatal
argument_list|(
operator|new
name|PrivilegedAction
argument_list|<
name|Object
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Object
name|run
parameter_list|()
block|{
name|doWork
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
DECL|method|doWork ()
specifier|private
name|void
name|doWork
parameter_list|()
block|{
while|while
condition|(
name|shouldRun
condition|)
block|{
try|try
block|{
comment|// There's no point in triggering a log roll if the Standby hasn't
comment|// read any more transactions since the last time a roll was
comment|// triggered.
if|if
condition|(
name|tooLongSinceLastLoad
argument_list|()
operator|&&
name|lastRollTriggerTxId
operator|<
name|lastLoadedTxnId
condition|)
block|{
name|triggerActiveLogRoll
argument_list|()
expr_stmt|;
block|}
comment|/**            * Check again in case someone calls {@link EditLogTailer#stop} while            * we're triggering an edit log roll, since ipc.Client catches and            * ignores {@link InterruptedException} in a few places. This fixes            * the bug described in HDFS-2823.            */
if|if
condition|(
operator|!
name|shouldRun
condition|)
block|{
break|break;
block|}
name|doTailEdits
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EditLogInputException
name|elie
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error while reading edits from disk. Will try again."
argument_list|,
name|elie
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ie
parameter_list|)
block|{
comment|// interrupter should have already set shouldRun to false
continue|continue;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Unknown error encountered while tailing edits. "
operator|+
literal|"Shutting down standby NN."
argument_list|,
name|t
argument_list|)
expr_stmt|;
name|terminate
argument_list|(
literal|1
argument_list|,
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
name|sleepTimeMs
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
literal|"Edit log tailer interrupted"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

