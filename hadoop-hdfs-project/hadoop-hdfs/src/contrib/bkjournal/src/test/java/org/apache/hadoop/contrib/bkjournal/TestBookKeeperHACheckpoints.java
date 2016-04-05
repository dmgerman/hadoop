begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.contrib.bkjournal
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|contrib
operator|.
name|bkjournal
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
name|namenode
operator|.
name|ha
operator|.
name|HATestUtil
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
name|TestStandbyCheckpoints
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
name|util
operator|.
name|Random
import|;
end_import

begin_comment
comment|/**  * Runs the same tests as TestStandbyCheckpoints, but  * using a bookkeeper journal manager as the shared directory  */
end_comment

begin_class
DECL|class|TestBookKeeperHACheckpoints
specifier|public
class|class
name|TestBookKeeperHACheckpoints
extends|extends
name|TestStandbyCheckpoints
block|{
comment|//overwrite the nn count
static|static
block|{
name|TestStandbyCheckpoints
operator|.
name|NUM_NNS
operator|=
literal|2
expr_stmt|;
block|}
DECL|field|bkutil
specifier|private
specifier|static
name|BKJMUtil
name|bkutil
init|=
literal|null
decl_stmt|;
DECL|field|numBookies
specifier|static
name|int
name|numBookies
init|=
literal|3
decl_stmt|;
DECL|field|journalCount
specifier|static
name|int
name|journalCount
init|=
literal|0
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
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
name|TestStandbyCheckpoints
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"rawtypes"
argument_list|)
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setupCluster ()
specifier|public
name|void
name|setupCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
name|setupCommonConfig
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
name|BKJMUtil
operator|.
name|createJournalURI
argument_list|(
literal|"/checkpointing"
operator|+
name|journalCount
operator|++
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BKJMUtil
operator|.
name|addJournalManagerDefinition
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|int
name|retryCount
init|=
literal|0
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|int
name|basePort
init|=
literal|10060
operator|+
name|random
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|*
literal|2
decl_stmt|;
name|MiniDFSNNTopology
name|topology
init|=
operator|new
name|MiniDFSNNTopology
argument_list|()
operator|.
name|addNameservice
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NSConf
argument_list|(
literal|"ns1"
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn1"
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|basePort
argument_list|)
argument_list|)
operator|.
name|addNN
argument_list|(
operator|new
name|MiniDFSNNTopology
operator|.
name|NNConf
argument_list|(
literal|"nn2"
argument_list|)
operator|.
name|setHttpPort
argument_list|(
name|basePort
operator|+
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|nnTopology
argument_list|(
name|topology
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
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
name|setNNs
argument_list|()
expr_stmt|;
name|fs
operator|=
name|HATestUtil
operator|.
name|configureFailoverFs
argument_list|(
name|cluster
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|transitionToActive
argument_list|(
literal|0
argument_list|)
expr_stmt|;
operator|++
name|retryCount
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|BindException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Set up MiniDFSCluster failed due to port conflicts, retry "
operator|+
name|retryCount
operator|+
literal|" times"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|BeforeClass
DECL|method|startBK ()
specifier|public
specifier|static
name|void
name|startBK
parameter_list|()
throws|throws
name|Exception
block|{
name|journalCount
operator|=
literal|0
expr_stmt|;
name|bkutil
operator|=
operator|new
name|BKJMUtil
argument_list|(
name|numBookies
argument_list|)
expr_stmt|;
name|bkutil
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|shutdownBK ()
specifier|public
specifier|static
name|void
name|shutdownBK
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|bkutil
operator|!=
literal|null
condition|)
block|{
name|bkutil
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|testCheckpointCancellation ()
specifier|public
name|void
name|testCheckpointCancellation
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Overriden as the implementation in the superclass assumes that writes
comment|// are to a file. This should be fixed at some point
block|}
block|}
end_class

end_unit

