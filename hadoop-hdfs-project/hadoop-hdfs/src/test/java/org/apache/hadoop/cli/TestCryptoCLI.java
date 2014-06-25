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
name|security
operator|.
name|NoSuchAlgorithmException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|CLICommandCryptoAdmin
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
name|CLICommandTypes
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
name|CLITestCmd
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
name|CryptoAdminCmdExecutor
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
name|crypto
operator|.
name|key
operator|.
name|JavaKeyStoreProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProvider
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
name|crypto
operator|.
name|key
operator|.
name|KeyProviderFactory
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
name|hdfs
operator|.
name|tools
operator|.
name|CryptoAdmin
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_class
DECL|class|TestCryptoCLI
specifier|public
class|class
name|TestCryptoCLI
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
DECL|field|tmpDir
specifier|private
specifier|static
name|File
name|tmpDir
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
name|tmpDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target"
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
name|JavaKeyStoreProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://file"
operator|+
name|tmpDir
operator|+
literal|"/test.jks"
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
name|createAKey
argument_list|(
literal|"mykey"
argument_list|,
name|conf
argument_list|)
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
comment|/* Helper function to create a key in the Key Provider. */
DECL|method|createAKey (String keyId, Configuration conf)
specifier|private
name|void
name|createAKey
parameter_list|(
name|String
name|keyId
parameter_list|,
name|Configuration
name|conf
parameter_list|)
throws|throws
name|NoSuchAlgorithmException
throws|,
name|IOException
block|{
specifier|final
name|KeyProvider
name|provider
init|=
name|dfsCluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getProvider
argument_list|()
decl_stmt|;
specifier|final
name|KeyProvider
operator|.
name|Options
name|options
init|=
name|KeyProvider
operator|.
name|options
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|provider
operator|.
name|createKey
argument_list|(
name|keyId
argument_list|,
name|options
argument_list|)
expr_stmt|;
name|provider
operator|.
name|flush
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
literal|"testCryptoConf.xml"
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
name|expCmd
operator|.
name|replaceAll
argument_list|(
literal|"#LF#"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
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
DECL|method|getConfigParser ()
specifier|protected
name|TestConfigFileParser
name|getConfigParser
parameter_list|()
block|{
return|return
operator|new
name|TestConfigFileParserCryptoAdmin
argument_list|()
return|;
block|}
DECL|class|TestConfigFileParserCryptoAdmin
specifier|private
class|class
name|TestConfigFileParserCryptoAdmin
extends|extends
name|CLITestHelper
operator|.
name|TestConfigFileParser
block|{
annotation|@
name|Override
DECL|method|endElement (String uri, String localName, String qName)
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|localName
parameter_list|,
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|qName
operator|.
name|equals
argument_list|(
literal|"crypto-admin-command"
argument_list|)
condition|)
block|{
if|if
condition|(
name|testCommands
operator|!=
literal|null
condition|)
block|{
name|testCommands
operator|.
name|add
argument_list|(
operator|new
name|CLITestCmdCryptoAdmin
argument_list|(
name|charString
argument_list|,
operator|new
name|CLICommandCryptoAdmin
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|cleanupCommands
operator|!=
literal|null
condition|)
block|{
name|cleanupCommands
operator|.
name|add
argument_list|(
operator|new
name|CLITestCmdCryptoAdmin
argument_list|(
name|charString
argument_list|,
operator|new
name|CLICommandCryptoAdmin
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|class|CLITestCmdCryptoAdmin
specifier|private
class|class
name|CLITestCmdCryptoAdmin
extends|extends
name|CLITestCmd
block|{
DECL|method|CLITestCmdCryptoAdmin (String str, CLICommandTypes type)
specifier|public
name|CLITestCmdCryptoAdmin
parameter_list|(
name|String
name|str
parameter_list|,
name|CLICommandTypes
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|str
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getExecutor (String tag)
specifier|public
name|CommandExecutor
name|getExecutor
parameter_list|(
name|String
name|tag
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|getType
argument_list|()
operator|instanceof
name|CLICommandCryptoAdmin
condition|)
block|{
return|return
operator|new
name|CryptoAdminCmdExecutor
argument_list|(
name|tag
argument_list|,
operator|new
name|CryptoAdmin
argument_list|(
name|conf
argument_list|)
argument_list|)
return|;
block|}
return|return
name|super
operator|.
name|getExecutor
argument_list|(
name|tag
argument_list|)
return|;
block|}
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

