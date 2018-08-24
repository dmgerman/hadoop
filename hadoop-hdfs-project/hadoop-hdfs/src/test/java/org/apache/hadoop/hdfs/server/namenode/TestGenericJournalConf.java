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
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
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
name|URI
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
name|server
operator|.
name|common
operator|.
name|Storage
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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|NamespaceInfo
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
DECL|class|TestGenericJournalConf
specifier|public
class|class
name|TestGenericJournalConf
block|{
DECL|field|DUMMY_URI
specifier|private
specifier|static
specifier|final
name|String
name|DUMMY_URI
init|=
literal|"dummy://test"
decl_stmt|;
comment|/**     * Test that an exception is thrown if a journal class doesn't exist    * in the configuration     */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testNotConfigured ()
specifier|public
name|void
name|testNotConfigured
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
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
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"dummy://test"
argument_list|)
expr_stmt|;
try|try
block|{
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
name|numDataNodes
argument_list|(
literal|0
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
block|}
finally|finally
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
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that an exception is thrown if a journal class doesn't    * exist in the classloader.    */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalArgumentException
operator|.
name|class
argument_list|)
DECL|method|testClassDoesntExist ()
specifier|public
name|void
name|testClassDoesntExist
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
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
name|DFS_NAMENODE_EDITS_PLUGIN_PREFIX
operator|+
literal|".dummy"
argument_list|,
literal|"org.apache.hadoop.nonexistent"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"dummy://test"
argument_list|)
expr_stmt|;
try|try
block|{
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
name|numDataNodes
argument_list|(
literal|0
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
block|}
finally|finally
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
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that a implementation of JournalManager without a     * (Configuration,URI) constructor throws an exception    */
annotation|@
name|Test
DECL|method|testBadConstructor ()
specifier|public
name|void
name|testBadConstructor
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
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
name|DFS_NAMENODE_EDITS_PLUGIN_PREFIX
operator|+
literal|".dummy"
argument_list|,
name|BadConstructorJournalManager
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"dummy://test"
argument_list|)
expr_stmt|;
try|try
block|{
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
name|numDataNodes
argument_list|(
literal|0
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
name|fail
argument_list|(
literal|"Should have failed before this point"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
if|if
condition|(
operator|!
name|iae
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Unable to construct journal"
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"Should have failed with unable to construct exception"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
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
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Test that a dummy implementation of JournalManager can    * be initialized on startup    */
annotation|@
name|Test
DECL|method|testDummyJournalManager ()
specifier|public
name|void
name|testDummyJournalManager
parameter_list|()
throws|throws
name|Exception
block|{
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
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
name|DFS_NAMENODE_EDITS_PLUGIN_PREFIX
operator|+
literal|".dummy"
argument_list|,
name|DummyJournalManager
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
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
name|DUMMY_URI
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_CHECKED_VOLUMES_MINIMUM_KEY
argument_list|,
literal|0
argument_list|)
expr_stmt|;
try|try
block|{
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
name|numDataNodes
argument_list|(
literal|0
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
name|assertTrue
argument_list|(
name|DummyJournalManager
operator|.
name|shouldPromptCalled
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|DummyJournalManager
operator|.
name|formatCalled
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DummyJournalManager
operator|.
name|conf
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
name|DUMMY_URI
argument_list|)
argument_list|,
name|DummyJournalManager
operator|.
name|uri
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|DummyJournalManager
operator|.
name|nsInfo
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|DummyJournalManager
operator|.
name|nsInfo
operator|.
name|getClusterID
argument_list|()
argument_list|,
name|cluster
operator|.
name|getNameNode
argument_list|()
operator|.
name|getNamesystem
argument_list|()
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
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
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|class|DummyJournalManager
specifier|public
specifier|static
class|class
name|DummyJournalManager
implements|implements
name|JournalManager
block|{
DECL|field|conf
specifier|static
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
DECL|field|uri
specifier|static
name|URI
name|uri
init|=
literal|null
decl_stmt|;
DECL|field|nsInfo
specifier|static
name|NamespaceInfo
name|nsInfo
init|=
literal|null
decl_stmt|;
DECL|field|formatCalled
specifier|static
name|boolean
name|formatCalled
init|=
literal|false
decl_stmt|;
DECL|field|shouldPromptCalled
specifier|static
name|boolean
name|shouldPromptCalled
init|=
literal|false
decl_stmt|;
DECL|method|DummyJournalManager (Configuration conf, URI u, NamespaceInfo nsInfo)
specifier|public
name|DummyJournalManager
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|URI
name|u
parameter_list|,
name|NamespaceInfo
name|nsInfo
parameter_list|)
block|{
comment|// Set static vars so the test case can verify them.
name|DummyJournalManager
operator|.
name|conf
operator|=
name|conf
expr_stmt|;
name|DummyJournalManager
operator|.
name|uri
operator|=
name|u
expr_stmt|;
name|DummyJournalManager
operator|.
name|nsInfo
operator|=
name|nsInfo
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|format (NamespaceInfo nsInfo, boolean force)
specifier|public
name|void
name|format
parameter_list|(
name|NamespaceInfo
name|nsInfo
parameter_list|,
name|boolean
name|force
parameter_list|)
throws|throws
name|IOException
block|{
name|formatCalled
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|startLogSegment (long txId, int layoutVersion)
specifier|public
name|EditLogOutputStream
name|startLogSegment
parameter_list|(
name|long
name|txId
parameter_list|,
name|int
name|layoutVersion
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|mock
argument_list|(
name|EditLogOutputStream
operator|.
name|class
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|finalizeLogSegment (long firstTxId, long lastTxId)
specifier|public
name|void
name|finalizeLogSegment
parameter_list|(
name|long
name|firstTxId
parameter_list|,
name|long
name|lastTxId
parameter_list|)
throws|throws
name|IOException
block|{
comment|// noop
block|}
annotation|@
name|Override
DECL|method|selectInputStreams (Collection<EditLogInputStream> streams, long fromTxnId, boolean inProgressOk, boolean onlyDurableTxns)
specifier|public
name|void
name|selectInputStreams
parameter_list|(
name|Collection
argument_list|<
name|EditLogInputStream
argument_list|>
name|streams
parameter_list|,
name|long
name|fromTxnId
parameter_list|,
name|boolean
name|inProgressOk
parameter_list|,
name|boolean
name|onlyDurableTxns
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|setOutputBufferCapacity (int size)
specifier|public
name|void
name|setOutputBufferCapacity
parameter_list|(
name|int
name|size
parameter_list|)
block|{}
annotation|@
name|Override
DECL|method|purgeLogsOlderThan (long minTxIdToKeep)
specifier|public
name|void
name|purgeLogsOlderThan
parameter_list|(
name|long
name|minTxIdToKeep
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|recoverUnfinalizedSegments ()
specifier|public
name|void
name|recoverUnfinalizedSegments
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|hasSomeData ()
specifier|public
name|boolean
name|hasSomeData
parameter_list|()
throws|throws
name|IOException
block|{
name|shouldPromptCalled
operator|=
literal|true
expr_stmt|;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|doPreUpgrade ()
specifier|public
name|void
name|doPreUpgrade
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|doUpgrade (Storage storage)
specifier|public
name|void
name|doUpgrade
parameter_list|(
name|Storage
name|storage
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|doFinalize ()
specifier|public
name|void
name|doFinalize
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|canRollBack (StorageInfo storage, StorageInfo prevStorage, int targetLayoutVersion)
specifier|public
name|boolean
name|canRollBack
parameter_list|(
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
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|doRollback ()
specifier|public
name|void
name|doRollback
parameter_list|()
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|discardSegments (long startTxId)
specifier|public
name|void
name|discardSegments
parameter_list|(
name|long
name|startTxId
parameter_list|)
throws|throws
name|IOException
block|{}
annotation|@
name|Override
DECL|method|getJournalCTime ()
specifier|public
name|long
name|getJournalCTime
parameter_list|()
throws|throws
name|IOException
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
DECL|class|BadConstructorJournalManager
specifier|public
specifier|static
class|class
name|BadConstructorJournalManager
extends|extends
name|DummyJournalManager
block|{
DECL|method|BadConstructorJournalManager ()
specifier|public
name|BadConstructorJournalManager
parameter_list|()
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

