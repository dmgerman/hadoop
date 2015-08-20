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
name|DFS_NAMENODE_EDITS_DIR_KEY
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
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
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
name|assertEquals
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|URL
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
name|namenode
operator|.
name|SecondaryNameNode
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
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_comment
comment|/**  * Test cases that the HA configuration is reasonably validated and  * interpreted in various places. These should be proper unit tests  * which don't start daemons.  */
end_comment

begin_class
DECL|class|TestHAConfiguration
specifier|public
class|class
name|TestHAConfiguration
block|{
DECL|field|fsn
specifier|private
specifier|final
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testCheckpointerValidityChecks ()
specifier|public
name|void
name|testCheckpointerValidityChecks
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
operator|new
name|StandbyCheckpointer
argument_list|(
name|conf
argument_list|,
name|fsn
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Bad config did not throw an error"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Invalid URI for NameNode address"
argument_list|,
name|iae
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getHAConf (String nsId, String ... hosts)
specifier|private
name|Configuration
name|getHAConf
parameter_list|(
name|String
name|nsId
parameter_list|,
name|String
modifier|...
name|hosts
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICES
argument_list|,
name|nsId
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODE_ID_KEY
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|String
index|[]
name|nnids
init|=
operator|new
name|String
index|[
name|hosts
operator|.
name|length
index|]
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
name|hosts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|nnid
init|=
literal|"nn"
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
decl_stmt|;
name|nnids
index|[
name|i
index|]
operator|=
name|nnid
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_RPC_ADDRESS_KEY
argument_list|,
name|nsId
argument_list|,
name|nnid
argument_list|)
argument_list|,
name|hosts
index|[
name|i
index|]
operator|+
literal|":12345"
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|DFSUtil
operator|.
name|addKeySuffixes
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_HA_NAMENODES_KEY_PREFIX
argument_list|,
name|nsId
argument_list|)
argument_list|,
name|Joiner
operator|.
name|on
argument_list|(
literal|','
argument_list|)
operator|.
name|join
argument_list|(
name|nnids
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Test
DECL|method|testGetOtherNNHttpAddress ()
specifier|public
name|void
name|testGetOtherNNHttpAddress
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Use non-local addresses to avoid host address matching
name|Configuration
name|conf
init|=
name|getHAConf
argument_list|(
literal|"ns1"
argument_list|,
literal|"1.2.3.1"
argument_list|,
literal|"1.2.3.2"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMESERVICE_ID
argument_list|,
literal|"ns1"
argument_list|)
expr_stmt|;
comment|// This is done by the NN before the StandbyCheckpointer is created
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|conf
argument_list|,
literal|"ns1"
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
comment|// Since we didn't configure the HTTP address, and the default is
comment|// 0.0.0.0, it should substitute the address from the RPC configuration
comment|// above.
name|StandbyCheckpointer
name|checkpointer
init|=
operator|new
name|StandbyCheckpointer
argument_list|(
name|conf
argument_list|,
name|fsn
argument_list|)
decl_stmt|;
name|assertAddressMatches
argument_list|(
literal|"1.2.3.2"
argument_list|,
name|checkpointer
operator|.
name|getActiveNNAddresses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
comment|//test when there are three NNs
comment|// Use non-local addresses to avoid host address matching
name|conf
operator|=
name|getHAConf
argument_list|(
literal|"ns1"
argument_list|,
literal|"1.2.3.1"
argument_list|,
literal|"1.2.3.2"
argument_list|,
literal|"1.2.3.3"
argument_list|)
expr_stmt|;
comment|// This is done by the NN before the StandbyCheckpointer is created
name|NameNode
operator|.
name|initializeGenericKeys
argument_list|(
name|conf
argument_list|,
literal|"ns1"
argument_list|,
literal|"nn1"
argument_list|)
expr_stmt|;
name|checkpointer
operator|=
operator|new
name|StandbyCheckpointer
argument_list|(
name|conf
argument_list|,
name|fsn
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Got an unexpected number of possible active NNs"
argument_list|,
literal|2
argument_list|,
name|checkpointer
operator|.
name|getActiveNNAddresses
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http"
argument_list|,
literal|"1.2.3.2"
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_PORT_DEFAULT
argument_list|,
literal|""
argument_list|)
argument_list|,
name|checkpointer
operator|.
name|getActiveNNAddresses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertAddressMatches
argument_list|(
literal|"1.2.3.2"
argument_list|,
name|checkpointer
operator|.
name|getActiveNNAddresses
argument_list|()
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertAddressMatches
argument_list|(
literal|"1.2.3.3"
argument_list|,
name|checkpointer
operator|.
name|getActiveNNAddresses
argument_list|()
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|assertAddressMatches (String address, URL url)
specifier|private
name|void
name|assertAddressMatches
parameter_list|(
name|String
name|address
parameter_list|,
name|URL
name|url
parameter_list|)
throws|throws
name|MalformedURLException
block|{
name|assertEquals
argument_list|(
operator|new
name|URL
argument_list|(
literal|"http"
argument_list|,
name|address
argument_list|,
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_HTTP_PORT_DEFAULT
argument_list|,
literal|""
argument_list|)
argument_list|,
name|url
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that the namenode edits dirs and shared edits dirs are gotten with    * duplicates removed    */
annotation|@
name|Test
DECL|method|testHAUniqueEditDirs ()
specifier|public
name|void
name|testHAUniqueEditDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"file://edits/dir, "
operator|+
literal|"file://edits/shared/dir"
argument_list|)
expr_stmt|;
comment|// overlapping
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_SHARED_EDITS_DIR_KEY
argument_list|,
literal|"file://edits/shared/dir"
argument_list|)
expr_stmt|;
comment|// getNamespaceEditsDirs removes duplicates across edits and shared.edits
name|Collection
argument_list|<
name|URI
argument_list|>
name|editsDirs
init|=
name|FSNamesystem
operator|.
name|getNamespaceEditsDirs
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|editsDirs
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that the 2NN does not start if given a config with HA NNs.    */
annotation|@
name|Test
DECL|method|testSecondaryNameNodeDoesNotStart ()
specifier|public
name|void
name|testSecondaryNameNodeDoesNotStart
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Note we're not explicitly setting the nameservice Id in the
comment|// config as it is not required to be set and we want to test
comment|// that we can determine if HA is enabled when the nameservice Id
comment|// is not explicitly defined.
name|Configuration
name|conf
init|=
name|getHAConf
argument_list|(
literal|"ns1"
argument_list|,
literal|"1.2.3.1"
argument_list|,
literal|"1.2.3.2"
argument_list|)
decl_stmt|;
try|try
block|{
operator|new
name|SecondaryNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Created a 2NN with an HA config"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Cannot use SecondaryNameNode in an HA cluster"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

