begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|cli
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
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
name|cli
operator|.
name|util
operator|.
name|CLICommand
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
name|cli
operator|.
name|util
operator|.
name|CommandExecutor
operator|.
name|Result
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
name|DistributedFileSystem
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
name|HDFSPolicyProvider
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
name|security
operator|.
name|authorize
operator|.
name|PolicyProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Test
import|;
end_import

begin_class
DECL|class|TestHDFSCLI
specifier|public
class|class
name|TestHDFSCLI
extends|extends
name|CLITestHelperDFS
block|{
DECL|field|dfsCluster
specifier|protected
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|fs
specifier|protected
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|field|namenode
specifier|protected
name|String
name|namenode
init|=
literal|null
decl_stmt|;
annotation|@
name|Before
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setClass
argument_list|(
name|PolicyProvider
operator|.
name|POLICY_PROVIDER_CONFIG
argument_list|,
name|HDFSPolicyProvider
operator|.
name|class
argument_list|,
name|PolicyProvider
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// Many of the tests expect a replication value of 1 in the output
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
comment|// Build racks and hosts configuration to test dfsAdmin -printTopology
name|String
index|[]
name|racks
init|=
block|{
literal|"/rack1"
block|,
literal|"/rack1"
block|,
literal|"/rack2"
block|,
literal|"/rack2"
block|,
literal|"/rack2"
block|,
literal|"/rack3"
block|,
literal|"/rack4"
block|,
literal|"/rack4"
block|}
decl_stmt|;
name|String
index|[]
name|hosts
init|=
block|{
literal|"host1"
block|,
literal|"host2"
block|,
literal|"host3"
block|,
literal|"host4"
block|,
literal|"host5"
block|,
literal|"host6"
block|,
literal|"host7"
block|,
literal|"host8"
block|}
decl_stmt|;
name|dfsCluster
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
literal|8
argument_list|)
operator|.
name|racks
argument_list|(
name|racks
argument_list|)
operator|.
name|hosts
argument_list|(
name|hosts
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|waitClusterUp
argument_list|()
expr_stmt|;
name|namenode
operator|=
name|conf
operator|.
name|get
argument_list|(
name|DFSConfigKeys
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|username
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
expr_stmt|;
name|fs
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not a HDFS: "
operator|+
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|instanceof
name|DistributedFileSystem
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getTestFile ()
specifier|protected
name|String
name|getTestFile
parameter_list|()
block|{
return|return
literal|"testHDFSConf.xml"
return|;
block|}
annotation|@
name|After
annotation|@
name|Override
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
literal|null
operator|!=
name|fs
condition|)
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|2000
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|expandCommand (final String cmd)
specifier|protected
name|String
name|expandCommand
parameter_list|(
specifier|final
name|String
name|cmd
parameter_list|)
block|{
name|String
name|expCmd
init|=
name|cmd
decl_stmt|;
name|expCmd
operator|=
name|expCmd
operator|.
name|replaceAll
argument_list|(
literal|"NAMENODE"
argument_list|,
name|namenode
argument_list|)
expr_stmt|;
name|expCmd
operator|=
name|super
operator|.
name|expandCommand
argument_list|(
name|expCmd
argument_list|)
expr_stmt|;
return|return
name|expCmd
return|;
block|}
annotation|@
name|Override
DECL|method|execute (CLICommand cmd)
specifier|protected
name|Result
name|execute
parameter_list|(
name|CLICommand
name|cmd
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|cmd
operator|.
name|getExecutor
argument_list|(
name|namenode
argument_list|)
operator|.
name|executeCommand
argument_list|(
name|cmd
operator|.
name|getCmd
argument_list|()
argument_list|)
return|;
block|}
comment|//TODO: The test is failing due to the change in HADOOP-7360.
comment|//      HDFS-2038 is going to fix it.  Disable the test for the moment.
annotation|@
name|Test
annotation|@
name|Override
DECL|method|testAll ()
specifier|public
name|void
name|testAll
parameter_list|()
block|{
name|super
operator|.
name|testAll
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

