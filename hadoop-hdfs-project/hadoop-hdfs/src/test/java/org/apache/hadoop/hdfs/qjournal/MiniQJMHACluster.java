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
name|MiniDFSNNTopology
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
name|namenode
operator|.
name|ha
operator|.
name|HATestUtil
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
name|BindException
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
name|ArrayList
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
name|Random
import|;
end_import

begin_class
DECL|class|MiniQJMHACluster
specifier|public
class|class
name|MiniQJMHACluster
block|{
DECL|field|cluster
specifier|private
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|journalCluster
specifier|private
name|MiniJournalCluster
name|journalCluster
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
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
name|MiniQJMHACluster
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|NAMESERVICE
specifier|public
specifier|static
specifier|final
name|String
name|NAMESERVICE
init|=
literal|"ns1"
decl_stmt|;
DECL|field|RANDOM
specifier|private
specifier|static
specifier|final
name|Random
name|RANDOM
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
DECL|class|Builder
specifier|public
specifier|static
class|class
name|Builder
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
decl_stmt|;
DECL|field|startOpt
specifier|private
name|StartupOption
name|startOpt
init|=
literal|null
decl_stmt|;
DECL|field|numNNs
specifier|private
name|int
name|numNNs
init|=
literal|2
decl_stmt|;
DECL|field|dfsBuilder
specifier|private
specifier|final
name|MiniDFSCluster
operator|.
name|Builder
name|dfsBuilder
decl_stmt|;
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
comment|// most QJMHACluster tests don't need DataNodes, so we'll make
comment|// this the default
name|this
operator|.
name|dfsBuilder
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
DECL|method|getDfsBuilder ()
specifier|public
name|MiniDFSCluster
operator|.
name|Builder
name|getDfsBuilder
parameter_list|()
block|{
return|return
name|dfsBuilder
return|;
block|}
DECL|method|build ()
specifier|public
name|MiniQJMHACluster
name|build
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|new
name|MiniQJMHACluster
argument_list|(
name|this
argument_list|)
return|;
block|}
DECL|method|startupOption (StartupOption startOpt)
specifier|public
name|void
name|startupOption
parameter_list|(
name|StartupOption
name|startOpt
parameter_list|)
block|{
name|this
operator|.
name|startOpt
operator|=
name|startOpt
expr_stmt|;
block|}
DECL|method|setNumNameNodes (int nns)
specifier|public
name|Builder
name|setNumNameNodes
parameter_list|(
name|int
name|nns
parameter_list|)
block|{
name|this
operator|.
name|numNNs
operator|=
name|nns
expr_stmt|;
return|return
name|this
return|;
block|}
block|}
DECL|method|createDefaultTopology (int nns, int startingPort)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|createDefaultTopology
parameter_list|(
name|int
name|nns
parameter_list|,
name|int
name|startingPort
parameter_list|)
block|{
name|MiniDFSNNTopology
operator|.
name|NSConf
name|nameservice
init|=
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
name|NAMESERVICE
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
name|nns
condition|;
name|i
operator|++
control|)
block|{
name|nameservice
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn"
operator|+
name|i
argument_list|)
operator|.
name|setIpcPort
argument_list|(
name|startingPort
operator|++
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|startingPort
operator|++
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
name|nameservice
argument_list|)
return|;
block|}
DECL|method|createDefaultTopology (int basePort)
specifier|public
specifier|static
name|MiniDFSNNTopology
name|createDefaultTopology
parameter_list|(
name|int
name|basePort
parameter_list|)
block|{
return|return
name|createDefaultTopology
argument_list|(
literal|2
argument_list|,
name|basePort
argument_list|)
return|;
block|}
DECL|method|MiniQJMHACluster (Builder builder)
specifier|private
name|MiniQJMHACluster
parameter_list|(
name|Builder
name|builder
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|conf
operator|=
name|builder
operator|.
name|conf
expr_stmt|;
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
name|int
name|basePort
init|=
literal|10000
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|basePort
operator|=
literal|10000
operator|+
name|RANDOM
operator|.
name|nextInt
argument_list|(
literal|1000
argument_list|)
operator|*
literal|4
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Set MiniQJMHACluster basePort to "
operator|+
name|basePort
argument_list|)
expr_stmt|;
comment|// start 3 journal nodes
name|journalCluster
operator|=
operator|new
name|MiniJournalCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|format
argument_list|(
literal|true
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|journalCluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|journalCluster
operator|.
name|setNamenodeSharedEditsConf
argument_list|(
name|NAMESERVICE
argument_list|)
expr_stmt|;
name|URI
name|journalURI
init|=
name|journalCluster
operator|.
name|getQuorumJournalURI
argument_list|(
name|NAMESERVICE
argument_list|)
decl_stmt|;
comment|// start cluster with specified NameNodes
name|MiniDFSNNTopology
name|topology
init|=
name|createDefaultTopology
argument_list|(
name|builder
operator|.
name|numNNs
argument_list|,
name|basePort
argument_list|)
decl_stmt|;
name|initHAConf
argument_list|(
name|journalURI
argument_list|,
name|builder
operator|.
name|conf
argument_list|,
name|builder
operator|.
name|numNNs
argument_list|,
name|basePort
argument_list|)
expr_stmt|;
comment|// First start up the NNs just to format the namespace. The MinIDFSCluster
comment|// has no way to just format the NameNodes without also starting them.
name|cluster
operator|=
name|builder
operator|.
name|dfsBuilder
operator|.
name|nnTopology
argument_list|(
name|topology
argument_list|)
operator|.
name|manageNameDfsSharedDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|shutdownNameNodes
argument_list|()
expr_stmt|;
comment|// initialize the journal nodes
name|Configuration
name|confNN0
init|=
name|cluster
operator|.
name|getConfiguration
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|NameNode
operator|.
name|initializeSharedEdits
argument_list|(
name|confNN0
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|MiniDFSCluster
operator|.
name|NameNodeInfo
name|nn
range|:
name|cluster
operator|.
name|getNameNodeInfos
argument_list|()
control|)
block|{
name|nn
operator|.
name|setStartOpt
argument_list|(
name|builder
operator|.
name|startOpt
argument_list|)
expr_stmt|;
block|}
comment|// restart the cluster
name|cluster
operator|.
name|restartNameNodes
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
operator|++
name|retryCount
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"MiniQJMHACluster port conflicts, retried "
operator|+
name|retryCount
operator|+
literal|" times"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|initHAConf (URI journalURI, Configuration conf, int numNNs, int basePort)
specifier|private
name|Configuration
name|initHAConf
parameter_list|(
name|URI
name|journalURI
parameter_list|,
name|Configuration
name|conf
parameter_list|,
name|int
name|numNNs
parameter_list|,
name|int
name|basePort
parameter_list|)
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
name|journalURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|nns
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|numNNs
argument_list|)
decl_stmt|;
name|int
name|port
init|=
name|basePort
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
name|numNNs
condition|;
name|i
operator|++
control|)
block|{
name|nns
operator|.
name|add
argument_list|(
literal|"127.0.0.1:"
operator|+
name|port
argument_list|)
expr_stmt|;
comment|// increment by 2 each time to account for the http port in the config setting
name|port
operator|+=
literal|2
expr_stmt|;
block|}
comment|// use standard failover configurations
name|HATestUtil
operator|.
name|setFailoverConfigurations
argument_list|(
name|conf
argument_list|,
name|NAMESERVICE
argument_list|,
name|nns
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
DECL|method|getDfsCluster ()
specifier|public
name|MiniDFSCluster
name|getDfsCluster
parameter_list|()
block|{
return|return
name|cluster
return|;
block|}
DECL|method|getJournalCluster ()
specifier|public
name|MiniJournalCluster
name|getJournalCluster
parameter_list|()
block|{
return|return
name|journalCluster
return|;
block|}
DECL|method|shutdown ()
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|journalCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

