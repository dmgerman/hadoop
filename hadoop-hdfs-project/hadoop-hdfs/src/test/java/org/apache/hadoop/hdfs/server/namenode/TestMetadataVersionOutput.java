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
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|GenericTestUtils
operator|.
name|assertExceptionContains
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
name|server
operator|.
name|common
operator|.
name|HdfsServerConstants
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
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
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
name|io
operator|.
name|PrintStream
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
name|DFS_NAMENODE_NAME_DIR_KEY
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
name|DFS_NAMESERVICE_ID
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
name|DFS_HA_NAMENODE_ID_KEY
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

begin_class
DECL|class|TestMetadataVersionOutput
specifier|public
class|class
name|TestMetadataVersionOutput
block|{
DECL|field|dfsCluster
specifier|private
name|MiniDFSCluster
name|dfsCluster
init|=
literal|null
decl_stmt|;
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
annotation|@
name|After
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
block|}
DECL|method|initConfig ()
specifier|private
name|void
name|initConfig
parameter_list|()
block|{
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMESERVICE_ID
argument_list|,
literal|"ns1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_HA_NAMENODES_KEY_PREFIX
operator|+
literal|".ns1"
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_HA_NAMENODE_ID_KEY
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_NAME_DIR_KEY
operator|+
literal|".ns1.nn1"
argument_list|,
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
operator|+
literal|"1"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|unset
argument_list|(
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testMetadataVersionOutput ()
specifier|public
name|void
name|testMetadataVersionOutput
parameter_list|()
throws|throws
name|IOException
block|{
name|initConfig
argument_list|()
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
name|manageNameDfsDirs
argument_list|(
literal|false
argument_list|)
operator|.
name|numDataNodes
argument_list|(
literal|1
argument_list|)
operator|.
name|checkExitOnShutdown
argument_list|(
literal|false
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
name|dfsCluster
operator|.
name|shutdown
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|initConfig
argument_list|()
expr_stmt|;
specifier|final
name|PrintStream
name|origOut
init|=
name|System
operator|.
name|out
decl_stmt|;
specifier|final
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|final
name|PrintStream
name|stdOut
init|=
operator|new
name|PrintStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|stdOut
argument_list|)
expr_stmt|;
try|try
block|{
name|NameNode
operator|.
name|createNameNode
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-metadataVersion"
block|}
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertExceptionContains
argument_list|(
literal|"ExitException"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
comment|/* Check if meta data version is printed correctly. */
specifier|final
name|String
name|verNumStr
init|=
name|HdfsServerConstants
operator|.
name|NAMENODE_LAYOUT_VERSION
operator|+
literal|""
decl_stmt|;
name|assertTrue
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"HDFS Image Version: "
operator|+
name|verNumStr
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|baos
operator|.
name|toString
argument_list|(
literal|"UTF-8"
argument_list|)
operator|.
name|contains
argument_list|(
literal|"Software format version: "
operator|+
name|verNumStr
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
name|origOut
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

