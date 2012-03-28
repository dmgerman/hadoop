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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|DFSConfigKeys
operator|.
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
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
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
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
name|fs
operator|.
name|FileSystem
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
name|DFSUtil
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
name|server
operator|.
name|datanode
operator|.
name|DataNode
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
name|datanode
operator|.
name|DataNodeTestUtils
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
name|FSImageTestUtil
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
name|test
operator|.
name|GenericTestUtils
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

begin_comment
comment|/**  * Static utility functions useful for testing HA.  */
end_comment

begin_class
DECL|class|HATestUtil
specifier|public
specifier|abstract
class|class
name|HATestUtil
block|{
DECL|field|LOG
specifier|private
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|HATestUtil
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|LOGICAL_HOSTNAME
specifier|private
specifier|static
specifier|final
name|String
name|LOGICAL_HOSTNAME
init|=
literal|"ha-nn-uri-%d"
decl_stmt|;
comment|/**    * Trigger an edits log roll on the active and then wait for the standby to    * catch up to all the edits done by the active. This method will check    * repeatedly for up to NN_LAG_TIMEOUT milliseconds, and then fail throwing    * {@link CouldNotCatchUpException}    *     * @param active active NN    * @param standby standby NN which should catch up to active    * @throws IOException if an error occurs rolling the edit log    * @throws CouldNotCatchUpException if the standby doesn't catch up to the    *         active in NN_LAG_TIMEOUT milliseconds    */
DECL|method|waitForStandbyToCatchUp (NameNode active, NameNode standby)
specifier|static
name|void
name|waitForStandbyToCatchUp
parameter_list|(
name|NameNode
name|active
parameter_list|,
name|NameNode
name|standby
parameter_list|)
throws|throws
name|InterruptedException
throws|,
name|IOException
throws|,
name|CouldNotCatchUpException
block|{
name|long
name|activeTxId
init|=
name|active
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getEditLog
argument_list|()
operator|.
name|getLastWrittenTxId
argument_list|()
decl_stmt|;
name|active
operator|.
name|getRpcServer
argument_list|()
operator|.
name|rollEditLog
argument_list|()
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|<
name|TestEditLogTailer
operator|.
name|NN_LAG_TIMEOUT
condition|)
block|{
name|long
name|nn2HighestTxId
init|=
name|standby
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getLastAppliedTxId
argument_list|()
decl_stmt|;
if|if
condition|(
name|nn2HighestTxId
operator|>=
name|activeTxId
condition|)
block|{
return|return;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
name|TestEditLogTailer
operator|.
name|SLEEP_TIME
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|CouldNotCatchUpException
argument_list|(
literal|"Standby did not catch up to txid "
operator|+
name|activeTxId
operator|+
literal|" (currently at "
operator|+
name|standby
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getFSImage
argument_list|()
operator|.
name|getLastAppliedTxId
argument_list|()
operator|+
literal|")"
argument_list|)
throw|;
block|}
comment|/**    * Wait for the datanodes in the cluster to process any block    * deletions that have already been asynchronously queued.    */
DECL|method|waitForDNDeletions (final MiniDFSCluster cluster)
specifier|static
name|void
name|waitForDNDeletions
parameter_list|(
specifier|final
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|TimeoutException
throws|,
name|InterruptedException
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
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
for|for
control|(
name|DataNode
name|dn
range|:
name|cluster
operator|.
name|getDataNodes
argument_list|()
control|)
block|{
if|if
condition|(
name|DataNodeTestUtils
operator|.
name|getPendingAsyncDeletions
argument_list|(
name|dn
argument_list|)
operator|>
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|,
literal|1000
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
comment|/**    * Wait for the NameNode to issue any deletions that are already    * pending (i.e. for the pendingDeletionBlocksCount to go to 0)    */
DECL|method|waitForNNToIssueDeletions (final NameNode nn)
specifier|static
name|void
name|waitForNNToIssueDeletions
parameter_list|(
specifier|final
name|NameNode
name|nn
parameter_list|)
throws|throws
name|Exception
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
annotation|@
name|Override
specifier|public
name|Boolean
name|get
parameter_list|()
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Waiting for NN to issue block deletions to DNs"
argument_list|)
expr_stmt|;
return|return
name|nn
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getBlockManager
argument_list|()
operator|.
name|getPendingDeletionBlocksCount
argument_list|()
operator|==
literal|0
return|;
block|}
block|}
argument_list|,
literal|250
argument_list|,
literal|10000
argument_list|)
expr_stmt|;
block|}
DECL|class|CouldNotCatchUpException
specifier|public
specifier|static
class|class
name|CouldNotCatchUpException
extends|extends
name|IOException
block|{
DECL|field|serialVersionUID
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
decl_stmt|;
DECL|method|CouldNotCatchUpException (String message)
specifier|public
name|CouldNotCatchUpException
parameter_list|(
name|String
name|message
parameter_list|)
block|{
name|super
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** Gets the filesystem instance by setting the failover configurations */
DECL|method|configureFailoverFs (MiniDFSCluster cluster, Configuration conf)
specifier|public
specifier|static
name|FileSystem
name|configureFailoverFs
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
return|return
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|,
literal|0
argument_list|)
return|;
block|}
comment|/**     * Gets the filesystem instance by setting the failover configurations    * @param cluster the single process DFS cluster    * @param conf cluster configuration    * @param nsIndex namespace index starting with zero    * @throws IOException if an error occurs rolling the edit log    */
DECL|method|configureFailoverFs (MiniDFSCluster cluster, Configuration conf, int nsIndex)
specifier|public
specifier|static
name|FileSystem
name|configureFailoverFs
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|nsIndex
parameter_list|)
throws|throws
name|IOException
throws|,
name|URISyntaxException
block|{
name|conf
operator|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|String
name|logicalName
init|=
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
decl_stmt|;
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|,
name|logicalName
argument_list|,
name|nsIndex
argument_list|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|URI
argument_list|(
literal|"hdfs://"
operator|+
name|logicalName
argument_list|)
argument_list|,
name|conf
argument_list|)
decl_stmt|;
return|return
name|fs
return|;
block|}
DECL|method|setFailoverConfigurations (MiniDFSCluster cluster, Configuration conf)
specifier|public
specifier|static
name|void
name|setFailoverConfigurations
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|)
block|{
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|,
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the required configurations for performing failover of default namespace. */
DECL|method|setFailoverConfigurations (MiniDFSCluster cluster, Configuration conf, String logicalName)
specifier|public
specifier|static
name|void
name|setFailoverConfigurations
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|logicalName
parameter_list|)
block|{
name|setFailoverConfigurations
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|,
name|logicalName
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/** Sets the required configurations for performing failover.  */
DECL|method|setFailoverConfigurations (MiniDFSCluster cluster, Configuration conf, String logicalName, int nsIndex)
specifier|public
specifier|static
name|void
name|setFailoverConfigurations
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|String
name|logicalName
parameter_list|,
name|int
name|nsIndex
parameter_list|)
block|{
name|InetSocketAddress
name|nnAddr1
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|2
operator|*
name|nsIndex
argument_list|)
operator|.
name|getNameNodeAddress
argument_list|()
decl_stmt|;
name|InetSocketAddress
name|nnAddr2
init|=
name|cluster
operator|.
name|getNameNode
argument_list|(
literal|2
operator|*
name|nsIndex
operator|+
literal|1
argument_list|)
operator|.
name|getNameNodeAddress
argument_list|()
decl_stmt|;
name|String
name|nameNodeId1
init|=
literal|"nn1"
decl_stmt|;
name|String
name|nameNodeId2
init|=
literal|"nn2"
decl_stmt|;
name|String
name|address1
init|=
literal|"hdfs://"
operator|+
name|nnAddr1
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|nnAddr1
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|String
name|address2
init|=
literal|"hdfs://"
operator|+
name|nnAddr2
operator|.
name|getHostName
argument_list|()
operator|+
literal|":"
operator|+
name|nnAddr2
operator|.
name|getPort
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|logicalName
argument_list|,
name|nameNodeId1
argument_list|)
argument_list|,
name|address1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|logicalName
argument_list|,
name|nameNodeId2
argument_list|)
argument_list|,
name|address2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_FEDERATION_NAMESERVICES
argument_list|,
name|logicalName
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFS_HA_NAMENODES_KEY_PREFIX
argument_list|,
name|logicalName
argument_list|)
argument_list|,
name|nameNodeId1
operator|+
literal|","
operator|+
name|nameNodeId2
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_CLIENT_FAILOVER_PROXY_PROVIDER_KEY_PREFIX
operator|+
literal|"."
operator|+
name|logicalName
argument_list|,
name|ConfiguredFailoverProxyProvider
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"hdfs://"
operator|+
name|logicalName
argument_list|)
expr_stmt|;
block|}
DECL|method|getLogicalHostname (MiniDFSCluster cluster)
specifier|public
specifier|static
name|String
name|getLogicalHostname
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
block|{
return|return
name|String
operator|.
name|format
argument_list|(
name|LOGICAL_HOSTNAME
argument_list|,
name|cluster
operator|.
name|getInstanceId
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getLogicalUri (MiniDFSCluster cluster)
specifier|public
specifier|static
name|URI
name|getLogicalUri
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|)
throws|throws
name|URISyntaxException
block|{
return|return
operator|new
name|URI
argument_list|(
name|HdfsConstants
operator|.
name|HDFS_URI_SCHEME
operator|+
literal|"://"
operator|+
name|getLogicalHostname
argument_list|(
name|cluster
argument_list|)
argument_list|)
return|;
block|}
DECL|method|waitForCheckpoint (MiniDFSCluster cluster, int nnIdx, List<Integer> txids)
specifier|public
specifier|static
name|void
name|waitForCheckpoint
parameter_list|(
name|MiniDFSCluster
name|cluster
parameter_list|,
name|int
name|nnIdx
parameter_list|,
name|List
argument_list|<
name|Integer
argument_list|>
name|txids
parameter_list|)
throws|throws
name|InterruptedException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|FSImageTestUtil
operator|.
name|assertNNHasCheckpoints
argument_list|(
name|cluster
argument_list|,
name|nnIdx
argument_list|,
name|txids
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|AssertionError
name|err
parameter_list|)
block|{
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|>
literal|10000
condition|)
block|{
throw|throw
name|err
throw|;
block|}
else|else
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|300
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

