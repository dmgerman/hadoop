begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.server
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|server
package|;
end_package

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
name|FileFilter
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
name|HashMap
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
name|javax
operator|.
name|management
operator|.
name|ObjectName
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
name|conf
operator|.
name|Configurable
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
name|HdfsConfiguration
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
name|qjournal
operator|.
name|client
operator|.
name|QuorumJournalManager
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
name|common
operator|.
name|HdfsServerConstants
operator|.
name|StartupOption
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
name|common
operator|.
name|StorageErrorReporter
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
name|common
operator|.
name|StorageInfo
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
name|io
operator|.
name|IOUtils
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
name|metrics2
operator|.
name|lib
operator|.
name|DefaultMetricsSystem
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
name|metrics2
operator|.
name|source
operator|.
name|JvmMetrics
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
name|metrics2
operator|.
name|util
operator|.
name|MBeans
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
import|import
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|DiskChecker
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
name|StringUtils
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
name|Tool
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
name|ToolRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|ajax
operator|.
name|JSON
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

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Maps
import|;
end_import

begin_comment
comment|/**  * The JournalNode is a daemon which allows namenodes using  * the QuorumJournalManager to log and retrieve edits stored  * remotely. It is a thin wrapper around a local edit log  * directory with the addition of facilities to participate  * in the quorum protocol.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|JournalNode
specifier|public
class|class
name|JournalNode
implements|implements
name|Tool
implements|,
name|Configurable
implements|,
name|JournalNodeMXBean
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
name|JournalNode
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
name|Configuration
name|conf
decl_stmt|;
DECL|field|rpcServer
specifier|private
name|JournalNodeRpcServer
name|rpcServer
decl_stmt|;
DECL|field|httpServer
specifier|private
name|JournalNodeHttpServer
name|httpServer
decl_stmt|;
DECL|field|journalsById
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Journal
argument_list|>
name|journalsById
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
DECL|field|journalNodeInfoBeanName
specifier|private
name|ObjectName
name|journalNodeInfoBeanName
decl_stmt|;
DECL|field|httpServerURI
specifier|private
name|String
name|httpServerURI
decl_stmt|;
DECL|field|localDir
specifier|private
name|File
name|localDir
decl_stmt|;
static|static
block|{
name|HdfsConfiguration
operator|.
name|init
argument_list|()
expr_stmt|;
block|}
comment|/**    * When stopped, the daemon will exit with this code.     */
DECL|field|resultCode
specifier|private
name|int
name|resultCode
init|=
literal|0
decl_stmt|;
DECL|method|getOrCreateJournal (String jid, StartupOption startOpt)
specifier|synchronized
name|Journal
name|getOrCreateJournal
parameter_list|(
name|String
name|jid
parameter_list|,
name|StartupOption
name|startOpt
parameter_list|)
throws|throws
name|IOException
block|{
name|QuorumJournalManager
operator|.
name|checkJournalId
argument_list|(
name|jid
argument_list|)
expr_stmt|;
name|Journal
name|journal
init|=
name|journalsById
operator|.
name|get
argument_list|(
name|jid
argument_list|)
decl_stmt|;
if|if
condition|(
name|journal
operator|==
literal|null
condition|)
block|{
name|File
name|logDir
init|=
name|getLogDir
argument_list|(
name|jid
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing journal in directory "
operator|+
name|logDir
argument_list|)
expr_stmt|;
name|journal
operator|=
operator|new
name|Journal
argument_list|(
name|conf
argument_list|,
name|logDir
argument_list|,
name|jid
argument_list|,
name|startOpt
argument_list|,
operator|new
name|ErrorReporter
argument_list|()
argument_list|)
expr_stmt|;
name|journalsById
operator|.
name|put
argument_list|(
name|jid
argument_list|,
name|journal
argument_list|)
expr_stmt|;
block|}
return|return
name|journal
return|;
block|}
DECL|method|getOrCreateJournal (String jid)
name|Journal
name|getOrCreateJournal
parameter_list|(
name|String
name|jid
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getOrCreateJournal
argument_list|(
name|jid
argument_list|,
name|StartupOption
operator|.
name|REGULAR
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|setConf (Configuration conf)
specifier|public
name|void
name|setConf
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|this
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|this
operator|.
name|localDir
operator|=
operator|new
name|File
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_EDITS_DIR_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_EDITS_DIR_DEFAULT
argument_list|)
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|validateAndCreateJournalDir (File dir)
specifier|private
specifier|static
name|void
name|validateAndCreateJournalDir
parameter_list|(
name|File
name|dir
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|dir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Journal dir '"
operator|+
name|dir
operator|+
literal|"' should be an absolute path"
argument_list|)
throw|;
block|}
name|DiskChecker
operator|.
name|checkDir
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getConf ()
specifier|public
name|Configuration
name|getConf
parameter_list|()
block|{
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|run (String[] args)
specifier|public
name|int
name|run
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|start
argument_list|()
expr_stmt|;
return|return
name|join
argument_list|()
return|;
block|}
comment|/**    * Start listening for edits via RPC.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|Preconditions
operator|.
name|checkState
argument_list|(
operator|!
name|isStarted
argument_list|()
argument_list|,
literal|"JN already running"
argument_list|)
expr_stmt|;
name|validateAndCreateJournalDir
argument_list|(
name|localDir
argument_list|)
expr_stmt|;
name|DefaultMetricsSystem
operator|.
name|initialize
argument_list|(
literal|"JournalNode"
argument_list|)
expr_stmt|;
name|JvmMetrics
operator|.
name|create
argument_list|(
literal|"JournalNode"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_METRICS_SESSION_ID_KEY
argument_list|)
argument_list|,
name|DefaultMetricsSystem
operator|.
name|instance
argument_list|()
argument_list|)
expr_stmt|;
name|InetSocketAddress
name|socAddr
init|=
name|JournalNodeRpcServer
operator|.
name|getAddress
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|SecurityUtil
operator|.
name|login
argument_list|(
name|conf
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_KEYTAB_FILE_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_KERBEROS_PRINCIPAL_KEY
argument_list|,
name|socAddr
operator|.
name|getHostName
argument_list|()
argument_list|)
expr_stmt|;
name|registerJNMXBean
argument_list|()
expr_stmt|;
name|httpServer
operator|=
operator|new
name|JournalNodeHttpServer
argument_list|(
name|conf
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|httpServer
operator|.
name|start
argument_list|()
expr_stmt|;
name|httpServerURI
operator|=
name|httpServer
operator|.
name|getServerURI
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
name|rpcServer
operator|=
operator|new
name|JournalNodeRpcServer
argument_list|(
name|conf
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|rpcServer
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
DECL|method|isStarted ()
specifier|public
name|boolean
name|isStarted
parameter_list|()
block|{
return|return
name|rpcServer
operator|!=
literal|null
return|;
block|}
comment|/**    * @return the address the IPC server is bound to    */
DECL|method|getBoundIpcAddress ()
specifier|public
name|InetSocketAddress
name|getBoundIpcAddress
parameter_list|()
block|{
return|return
name|rpcServer
operator|.
name|getAddress
argument_list|()
return|;
block|}
annotation|@
name|Deprecated
DECL|method|getBoundHttpAddress ()
specifier|public
name|InetSocketAddress
name|getBoundHttpAddress
parameter_list|()
block|{
return|return
name|httpServer
operator|.
name|getAddress
argument_list|()
return|;
block|}
DECL|method|getHttpServerURI ()
specifier|public
name|String
name|getHttpServerURI
parameter_list|()
block|{
return|return
name|httpServerURI
return|;
block|}
comment|/**    * Stop the daemon with the given status code    * @param rc the status code with which to exit (non-zero    * should indicate an error)    */
DECL|method|stop (int rc)
specifier|public
name|void
name|stop
parameter_list|(
name|int
name|rc
parameter_list|)
block|{
name|this
operator|.
name|resultCode
operator|=
name|rc
expr_stmt|;
if|if
condition|(
name|rpcServer
operator|!=
literal|null
condition|)
block|{
name|rpcServer
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|httpServer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|httpServer
operator|.
name|stop
argument_list|()
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
literal|"Unable to stop HTTP server for "
operator|+
name|this
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
name|Journal
name|j
range|:
name|journalsById
operator|.
name|values
argument_list|()
control|)
block|{
name|IOUtils
operator|.
name|cleanup
argument_list|(
name|LOG
argument_list|,
name|j
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|journalNodeInfoBeanName
operator|!=
literal|null
condition|)
block|{
name|MBeans
operator|.
name|unregister
argument_list|(
name|journalNodeInfoBeanName
argument_list|)
expr_stmt|;
name|journalNodeInfoBeanName
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**    * Wait for the daemon to exit.    * @return the result code (non-zero if error)    */
DECL|method|join ()
name|int
name|join
parameter_list|()
throws|throws
name|InterruptedException
block|{
if|if
condition|(
name|rpcServer
operator|!=
literal|null
condition|)
block|{
name|rpcServer
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
return|return
name|resultCode
return|;
block|}
DECL|method|stopAndJoin (int rc)
specifier|public
name|void
name|stopAndJoin
parameter_list|(
name|int
name|rc
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|stop
argument_list|(
name|rc
argument_list|)
expr_stmt|;
name|join
argument_list|()
expr_stmt|;
block|}
comment|/**    * Return the directory inside our configured storage    * dir which corresponds to a given journal.     * @param jid the journal identifier    * @return the file, which may or may not exist yet    */
DECL|method|getLogDir (String jid)
specifier|private
name|File
name|getLogDir
parameter_list|(
name|String
name|jid
parameter_list|)
block|{
name|String
name|dir
init|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_EDITS_DIR_KEY
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_EDITS_DIR_DEFAULT
argument_list|)
decl_stmt|;
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|jid
operator|!=
literal|null
operator|&&
operator|!
name|jid
operator|.
name|isEmpty
argument_list|()
argument_list|,
literal|"bad journal identifier: %s"
argument_list|,
name|jid
argument_list|)
expr_stmt|;
return|return
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|dir
argument_list|)
argument_list|,
name|jid
argument_list|)
return|;
block|}
annotation|@
name|Override
comment|// JournalNodeMXBean
DECL|method|getJournalsStatus ()
specifier|public
name|String
name|getJournalsStatus
parameter_list|()
block|{
comment|// jid:{Formatted:True/False}
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|status
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Journal
argument_list|>
name|entry
range|:
name|journalsById
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|jMap
operator|.
name|put
argument_list|(
literal|"Formatted"
argument_list|,
name|Boolean
operator|.
name|toString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|isFormatted
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|status
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|jMap
argument_list|)
expr_stmt|;
block|}
block|}
comment|// It is possible that some journals have been formatted before, while the
comment|// corresponding journals are not in journalsById yet (because of restarting
comment|// JN, e.g.). For simplicity, let's just assume a journal is formatted if
comment|// there is a directory for it. We can also call analyzeStorage method for
comment|// these directories if necessary.
comment|// Also note that we do not need to check localDir here since
comment|// validateAndCreateJournalDir has been called before we register the
comment|// MXBean.
name|File
index|[]
name|journalDirs
init|=
name|localDir
operator|.
name|listFiles
argument_list|(
operator|new
name|FileFilter
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|file
parameter_list|)
block|{
return|return
name|file
operator|.
name|isDirectory
argument_list|()
return|;
block|}
block|}
argument_list|)
decl_stmt|;
for|for
control|(
name|File
name|journalDir
range|:
name|journalDirs
control|)
block|{
name|String
name|jid
init|=
name|journalDir
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|status
operator|.
name|containsKey
argument_list|(
name|jid
argument_list|)
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|jMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|jMap
operator|.
name|put
argument_list|(
literal|"Formatted"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|status
operator|.
name|put
argument_list|(
name|jid
argument_list|,
name|jMap
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|JSON
operator|.
name|toString
argument_list|(
name|status
argument_list|)
return|;
block|}
comment|/**    * Register JournalNodeMXBean    */
DECL|method|registerJNMXBean ()
specifier|private
name|void
name|registerJNMXBean
parameter_list|()
block|{
name|journalNodeInfoBeanName
operator|=
name|MBeans
operator|.
name|register
argument_list|(
literal|"JournalNode"
argument_list|,
literal|"JournalNodeInfo"
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
DECL|class|ErrorReporter
specifier|private
class|class
name|ErrorReporter
implements|implements
name|StorageErrorReporter
block|{
annotation|@
name|Override
DECL|method|reportErrorOnFile (File f)
specifier|public
name|void
name|reportErrorOnFile
parameter_list|(
name|File
name|f
parameter_list|)
block|{
name|LOG
operator|.
name|fatal
argument_list|(
literal|"Error reported on file "
operator|+
name|f
operator|+
literal|"... exiting"
argument_list|,
operator|new
name|Exception
argument_list|()
argument_list|)
expr_stmt|;
name|stop
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
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
name|StringUtils
operator|.
name|startupShutdownMessage
argument_list|(
name|JournalNode
operator|.
name|class
argument_list|,
name|args
argument_list|,
name|LOG
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
name|ToolRunner
operator|.
name|run
argument_list|(
operator|new
name|JournalNode
argument_list|()
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|doPreUpgrade (String journalId)
specifier|public
name|void
name|doPreUpgrade
parameter_list|(
name|String
name|journalId
parameter_list|)
throws|throws
name|IOException
block|{
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|doPreUpgrade
argument_list|()
expr_stmt|;
block|}
DECL|method|doUpgrade (String journalId, StorageInfo sInfo)
specifier|public
name|void
name|doUpgrade
parameter_list|(
name|String
name|journalId
parameter_list|,
name|StorageInfo
name|sInfo
parameter_list|)
throws|throws
name|IOException
block|{
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|doUpgrade
argument_list|(
name|sInfo
argument_list|)
expr_stmt|;
block|}
DECL|method|doFinalize (String journalId)
specifier|public
name|void
name|doFinalize
parameter_list|(
name|String
name|journalId
parameter_list|)
throws|throws
name|IOException
block|{
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|doFinalize
argument_list|()
expr_stmt|;
block|}
DECL|method|canRollBack (String journalId, StorageInfo storage, StorageInfo prevStorage, int targetLayoutVersion)
specifier|public
name|Boolean
name|canRollBack
parameter_list|(
name|String
name|journalId
parameter_list|,
name|StorageInfo
name|storage
parameter_list|,
name|StorageInfo
name|prevStorage
parameter_list|,
name|int
name|targetLayoutVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|,
name|StartupOption
operator|.
name|ROLLBACK
argument_list|)
operator|.
name|canRollBack
argument_list|(
name|storage
argument_list|,
name|prevStorage
argument_list|,
name|targetLayoutVersion
argument_list|)
return|;
block|}
DECL|method|doRollback (String journalId)
specifier|public
name|void
name|doRollback
parameter_list|(
name|String
name|journalId
parameter_list|)
throws|throws
name|IOException
block|{
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|,
name|StartupOption
operator|.
name|ROLLBACK
argument_list|)
operator|.
name|doRollback
argument_list|()
expr_stmt|;
block|}
DECL|method|discardSegments (String journalId, long startTxId)
specifier|public
name|void
name|discardSegments
parameter_list|(
name|String
name|journalId
parameter_list|,
name|long
name|startTxId
parameter_list|)
throws|throws
name|IOException
block|{
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|discardSegments
argument_list|(
name|startTxId
argument_list|)
expr_stmt|;
block|}
DECL|method|getJournalCTime (String journalId)
specifier|public
name|Long
name|getJournalCTime
parameter_list|(
name|String
name|journalId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|getOrCreateJournal
argument_list|(
name|journalId
argument_list|)
operator|.
name|getJournalCTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit

