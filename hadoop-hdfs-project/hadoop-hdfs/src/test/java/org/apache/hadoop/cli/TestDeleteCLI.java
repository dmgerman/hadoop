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
name|CommonConfigurationKeysPublic
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
name|MiniDFSCluster
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
DECL|class|TestDeleteCLI
specifier|public
class|class
name|TestDeleteCLI
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
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_REPLICATION_KEY
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setLong
argument_list|(
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_SHELL_SAFELY_DELETE_LIMIT_NUM_FILES
argument_list|,
literal|5
argument_list|)
expr_stmt|;
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
literal|1
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
name|fs
operator|=
name|dfsCluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Not an HDFS: "
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
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dfsCluster
operator|!=
literal|null
condition|)
block|{
name|dfsCluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
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
DECL|method|getTestFile ()
specifier|protected
name|String
name|getTestFile
parameter_list|()
block|{
return|return
literal|"testDeleteConf.xml"
return|;
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
argument_list|,
name|conf
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

