begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal
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
package|;
end_package

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
name|qjournal
operator|.
name|QJMTestUtil
operator|.
name|FAKE_NSINFO
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
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
name|net
operator|.
name|InetSocketAddress
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
name|net
operator|.
name|URISyntaxException
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
name|TimeoutException
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
name|Supplier
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
name|fs
operator|.
name|FileUtil
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
name|MiniDFSCluster
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
name|qjournal
operator|.
name|server
operator|.
name|JournalNode
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
name|net
operator|.
name|NetUtils
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
name|Joiner
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
name|Lists
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
name|test
operator|.
name|GenericTestUtils
import|;
end_import

begin_class
DECL|class|MiniJournalCluster
specifier|public
class|class
name|MiniJournalCluster
block|{
DECL|field|CLUSTER_WAITACTIVE_URI
specifier|public
specifier|static
specifier|final
name|String
name|CLUSTER_WAITACTIVE_URI
init|=
literal|"waitactive"
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|baseDir
specifier|private
name|String
name|baseDir
decl_stmt|;
DECL|field|numJournalNodes
specifier|private
name|int
name|numJournalNodes
init|=
literal|3
decl_stmt|;
DECL|field|format
specifier|private
name|boolean
name|format
init|=
literal|true
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
static|static
block|{
name|DefaultMetricsSystem
operator|.
name|setMiniClusterMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|Builder (Configuration conf)
specifier|public
name|Builder
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
block|}
DECL|method|baseDir (String d)
specifier|public
name|Builder
name|baseDir
parameter_list|(
name|String
name|d
parameter_list|)
block|{
name|this
operator|.
name|baseDir
operator|=
name|d
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|numJournalNodes (int n)
specifier|public
name|Builder
name|numJournalNodes
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|this
operator|.
name|numJournalNodes
operator|=
name|n
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|format (boolean f)
specifier|public
name|Builder
name|format
parameter_list|(
name|boolean
name|f
parameter_list|)
block|{
name|this
operator|.
name|format
operator|=
name|f
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|build ()
specifier|public
name|MiniJournalCluster
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MiniJournalCluster
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
DECL|class|JNInfo
specifier|private
specifier|static
specifier|final
class|class
name|JNInfo
block|{
DECL|field|node
specifier|private
name|JournalNode
name|node
decl_stmt|;
DECL|field|ipcAddr
specifier|private
specifier|final
name|InetSocketAddress
name|ipcAddr
decl_stmt|;
DECL|field|httpServerURI
specifier|private
specifier|final
name|String
name|httpServerURI
decl_stmt|;
DECL|method|JNInfo (JournalNode node)
specifier|private
name|JNInfo
parameter_list|(
name|JournalNode
name|node
parameter_list|)
block|{
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|ipcAddr
operator|=
name|node
operator|.
name|getBoundIpcAddress
argument_list|()
expr_stmt|;
name|this
operator|.
name|httpServerURI
operator|=
name|node
operator|.
name|getHttpServerURI
argument_list|()
expr_stmt|;
block|}
block|}
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
name|MiniJournalCluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|baseDir
specifier|private
specifier|final
name|File
name|baseDir
decl_stmt|;
DECL|field|nodes
specifier|private
specifier|final
name|JNInfo
index|[]
name|nodes
decl_stmt|;
DECL|method|MiniJournalCluster (Builder b)
specifier|private
name|MiniJournalCluster
parameter_list|(
name|Builder
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting MiniJournalCluster with "
operator|+
name|b
operator|.
name|numJournalNodes
operator|+
literal|" journal nodes"
argument_list|)
expr_stmt|;
if|if
condition|(
name|b
operator|.
name|baseDir
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|b
operator|.
name|baseDir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|baseDir
operator|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|nodes
operator|=
operator|new
name|JNInfo
index|[
name|b
operator|.
name|numJournalNodes
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|b
operator|.
name|numJournalNodes
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|b
operator|.
name|format
condition|)
block|{
name|File
name|dir
init|=
name|getStorageDir
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Fully deleting JN directory "
operator|+
name|dir
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|JournalNode
name|jn
init|=
operator|new
name|JournalNode
argument_list|()
decl_stmt|;
name|jn
operator|.
name|setConf
argument_list|(
name|createConfForNode
argument_list|(
name|b
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|jn
operator|.
name|start
argument_list|()
expr_stmt|;
name|nodes
index|[
name|i
index|]
operator|=
operator|new
name|JNInfo
argument_list|(
name|jn
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Set up the given Configuration object to point to the set of JournalNodes     * in this cluster.    */
DECL|method|getQuorumJournalURI (String jid)
specifier|public
name|URI
name|getQuorumJournalURI
parameter_list|(
name|String
name|jid
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|addrs
init|=
name|Lists
operator|.
name|newArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|JNInfo
name|info
range|:
name|nodes
control|)
block|{
name|addrs
operator|.
name|add
argument_list|(
literal|"127.0.0.1:"
operator|+
name|info
operator|.
name|ipcAddr
operator|.
name|getPort
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|addrsVal
init|=
name|Joiner
operator|.
name|on
argument_list|(
literal|";"
argument_list|)
operator|.
name|join
argument_list|(
name|addrs
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Setting logger addresses to: "
operator|+
name|addrsVal
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|URI
argument_list|(
literal|"qjournal://"
operator|+
name|addrsVal
operator|+
literal|"/"
operator|+
name|jid
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**    * Start the JournalNodes in the cluster.    */
DECL|method|start ()
specifier|public
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|JNInfo
name|info
range|:
name|nodes
control|)
block|{
name|info
operator|.
name|node
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Shutdown all of the JournalNodes in the cluster.    * @throws IOException if one or more nodes failed to stop    */
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|boolean
name|failed
init|=
literal|false
decl_stmt|;
for|for
control|(
name|JNInfo
name|info
range|:
name|nodes
control|)
block|{
try|try
block|{
name|info
operator|.
name|node
operator|.
name|stopAndJoin
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to stop journal node "
operator|+
name|info
operator|.
name|node
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|failed
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unable to shut down. Check log for details"
argument_list|)
throw|;
block|}
block|}
DECL|method|createConfForNode (Builder b, int idx)
specifier|private
name|Configuration
name|createConfForNode
parameter_list|(
name|Builder
name|b
parameter_list|,
name|int
name|idx
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|b
operator|.
name|conf
argument_list|)
decl_stmt|;
name|File
name|logDir
init|=
name|getStorageDir
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_EDITS_DIR_KEY
argument_list|,
name|logDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_RPC_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTP_ADDRESS_KEY
argument_list|,
literal|"localhost:0"
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|getStorageDir (int idx)
specifier|public
name|File
name|getStorageDir
parameter_list|(
name|int
name|idx
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
literal|"journalnode-"
operator|+
name|idx
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
return|;
block|}
DECL|method|getJournalDir (int idx, String jid)
specifier|public
name|File
name|getJournalDir
parameter_list|(
name|int
name|idx
parameter_list|,
name|String
name|jid
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getStorageDir
argument_list|(
name|idx
argument_list|)
argument_list|,
name|jid
argument_list|)
return|;
block|}
DECL|method|getCurrentDir (int idx, String jid)
specifier|public
name|File
name|getCurrentDir
parameter_list|(
name|int
name|idx
parameter_list|,
name|String
name|jid
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getJournalDir
argument_list|(
name|idx
argument_list|,
name|jid
argument_list|)
argument_list|,
literal|"current"
argument_list|)
return|;
block|}
DECL|method|getPreviousDir (int idx, String jid)
specifier|public
name|File
name|getPreviousDir
parameter_list|(
name|int
name|idx
parameter_list|,
name|String
name|jid
parameter_list|)
block|{
return|return
operator|new
name|File
argument_list|(
name|getJournalDir
argument_list|(
name|idx
argument_list|,
name|jid
argument_list|)
argument_list|,
literal|"previous"
argument_list|)
return|;
block|}
DECL|method|getJournalNode (int i)
specifier|public
name|JournalNode
name|getJournalNode
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|nodes
index|[
name|i
index|]
operator|.
name|node
return|;
block|}
DECL|method|getJournalNodeIpcAddress (int i)
specifier|public
name|String
name|getJournalNodeIpcAddress
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|nodes
index|[
name|i
index|]
operator|.
name|ipcAddr
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|restartJournalNode (int i)
specifier|public
name|void
name|restartJournalNode
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
block|{
name|JNInfo
name|info
init|=
name|nodes
index|[
name|i
index|]
decl_stmt|;
name|JournalNode
name|jn
init|=
name|info
operator|.
name|node
decl_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|jn
operator|.
name|getConf
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|jn
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|jn
operator|.
name|stopAndJoin
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_RPC_ADDRESS_KEY
argument_list|,
name|NetUtils
operator|.
name|getHostPortString
argument_list|(
name|info
operator|.
name|ipcAddr
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|uri
init|=
name|info
operator|.
name|httpServerURI
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"http://"
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTP_ADDRESS_KEY
argument_list|,
name|uri
operator|.
name|substring
argument_list|(
operator|(
literal|"http://"
operator|.
name|length
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|info
operator|.
name|httpServerURI
operator|.
name|startsWith
argument_list|(
literal|"https://"
argument_list|)
condition|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_JOURNALNODE_HTTPS_ADDRESS_KEY
argument_list|,
name|uri
operator|.
name|substring
argument_list|(
operator|(
literal|"https://"
operator|.
name|length
argument_list|()
operator|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|JournalNode
name|newJN
init|=
operator|new
name|JournalNode
argument_list|()
decl_stmt|;
name|newJN
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|newJN
operator|.
name|start
argument_list|()
expr_stmt|;
name|info
operator|.
name|node
operator|=
name|newJN
expr_stmt|;
block|}
DECL|method|getQuorumSize ()
specifier|public
name|int
name|getQuorumSize
parameter_list|()
block|{
return|return
name|nodes
operator|.
name|length
operator|/
literal|2
operator|+
literal|1
return|;
block|}
DECL|method|getNumNodes ()
specifier|public
name|int
name|getNumNodes
parameter_list|()
block|{
return|return
name|nodes
operator|.
name|length
return|;
block|}
comment|/**    * Wait until all the journalnodes start.    */
DECL|method|waitActive ()
specifier|public
name|void
name|waitActive
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|index
init|=
name|i
decl_stmt|;
try|try
block|{
name|GenericTestUtils
operator|.
name|waitFor
argument_list|(
operator|new
name|Supplier
argument_list|<
name|Boolean
argument_list|>
argument_list|()
block|{
comment|// wait until all JN's IPC server is running
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
try|try
block|{
name|QuorumJournalManager
name|qjm
init|=
operator|new
name|QuorumJournalManager
argument_list|(
name|nodes
index|[
name|index
index|]
operator|.
name|node
operator|.
name|getConf
argument_list|()
argument_list|,
name|getQuorumJournalURI
argument_list|(
name|CLUSTER_WAITACTIVE_URI
argument_list|)
argument_list|,
name|FAKE_NSINFO
argument_list|)
decl_stmt|;
name|qjm
operator|.
name|hasSomeData
argument_list|()
expr_stmt|;
name|qjm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Exception from IPC call, likely due to server not ready yet.
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|50
argument_list|,
literal|3000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TimeoutException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
literal|"Time out while waiting for journal node "
operator|+
name|index
operator|+
literal|" to start."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|ite
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Thread interrupted when waiting for node start"
argument_list|,
name|ite
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|setNamenodeSharedEditsConf (String jid)
specifier|public
name|void
name|setNamenodeSharedEditsConf
parameter_list|(
name|String
name|jid
parameter_list|)
block|{
name|URI
name|quorumJournalURI
init|=
name|getQuorumJournalURI
argument_list|(
name|jid
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nodes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|nodes
index|[
name|i
index|]
operator|.
name|node
operator|.
name|getConf
argument_list|()
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
name|quorumJournalURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

