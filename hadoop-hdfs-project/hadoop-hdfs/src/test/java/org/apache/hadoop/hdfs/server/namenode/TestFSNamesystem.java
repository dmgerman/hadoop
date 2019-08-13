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
name|fs
operator|.
name|CommonConfigurationKeysPublic
operator|.
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
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
name|DFS_NAMENODE_NAME_DIR_KEY
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|either
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|instanceOf
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
name|*
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
name|InetAddress
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
name|fs
operator|.
name|FileStatus
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
name|ha
operator|.
name|HAServiceProtocol
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
name|DFSTestUtil
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
name|HdfsConfiguration
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
name|blockmanagement
operator|.
name|BlockManager
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
name|NamenodeRole
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
name|HAContext
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
name|HAState
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
name|snapshot
operator|.
name|Snapshot
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
name|top
operator|.
name|TopAuditLogger
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
name|apache
operator|.
name|hadoop
operator|.
name|test
operator|.
name|Whitebox
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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

begin_class
DECL|class|TestFSNamesystem
specifier|public
class|class
name|TestFSNamesystem
block|{
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDeleteContents
argument_list|(
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that the namenode edits dirs are gotten with duplicates removed    */
annotation|@
name|Test
DECL|method|testUniqueEditDirs ()
specifier|public
name|void
name|testUniqueEditDirs
parameter_list|()
throws|throws
name|IOException
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_EDITS_DIR_KEY
argument_list|,
literal|"file://edits/dir, "
operator|+
literal|"file://edits/dir1,file://edits/dir1"
argument_list|)
expr_stmt|;
comment|// overlapping internally
comment|// getNamespaceEditsDirs removes duplicates
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
name|config
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
comment|/**    * Test that FSNamesystem#clear clears all leases.    */
annotation|@
name|Test
DECL|method|testFSNamespaceClearLeases ()
specifier|public
name|void
name|testFSNamespaceClearLeases
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|File
name|nameDir
init|=
operator|new
name|File
argument_list|(
name|MiniDFSCluster
operator|.
name|getBaseDirectory
argument_list|()
argument_list|,
literal|"name"
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|DFS_NAMENODE_NAME_DIR_KEY
argument_list|,
name|nameDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|NameNode
operator|.
name|initMetrics
argument_list|(
name|conf
argument_list|,
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|)
expr_stmt|;
name|DFSTestUtil
operator|.
name|formatNameNode
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsn
init|=
name|FSNamesystem
operator|.
name|loadFromDisk
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LeaseManager
name|leaseMan
init|=
name|fsn
operator|.
name|getLeaseManager
argument_list|()
decl_stmt|;
name|leaseMan
operator|.
name|addLease
argument_list|(
literal|"client1"
argument_list|,
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|allocateNewInodeId
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|leaseMan
operator|.
name|countLease
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|clear
argument_list|()
expr_stmt|;
name|leaseMan
operator|=
name|fsn
operator|.
name|getLeaseManager
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|leaseMan
operator|.
name|countLease
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
comment|/**    * Test that isInStartupSafemode returns true only during startup safemode    * and not also during low-resource safemode    */
DECL|method|testStartupSafemode ()
specifier|public
name|void
name|testStartupSafemode
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
name|FSImage
name|fsImage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSEditLog
name|fsEditLog
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsEditLog
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsn
init|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
decl_stmt|;
name|fsn
operator|.
name|leaveSafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After leaving safemode FSNamesystem.isInStartupSafeMode still "
operator|+
literal|"returned true"
argument_list|,
operator|!
name|fsn
operator|.
name|isInStartupSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After leaving safemode FSNamesystem.isInSafeMode still returned"
operator|+
literal|" true"
argument_list|,
operator|!
name|fsn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|enterSafeMode
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After entering safemode due to low resources FSNamesystem."
operator|+
literal|"isInStartupSafeMode still returned true"
argument_list|,
operator|!
name|fsn
operator|.
name|isInStartupSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"After entering safemode due to low resources FSNamesystem."
operator|+
literal|"isInSafeMode still returned false"
argument_list|,
name|fsn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReplQueuesActiveAfterStartupSafemode ()
specifier|public
name|void
name|testReplQueuesActiveAfterStartupSafemode
parameter_list|()
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FSEditLog
name|fsEditLog
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSImage
name|fsImage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsEditLog
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsNamesystem
init|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fsNamesystem
argument_list|)
decl_stmt|;
name|BlockManager
name|bm
init|=
name|fsn
operator|.
name|getBlockManager
argument_list|()
decl_stmt|;
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|bm
argument_list|,
literal|"namesystem"
argument_list|,
name|fsn
argument_list|)
expr_stmt|;
comment|//Make shouldPopulaeReplQueues return true
name|HAContext
name|haContext
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HAContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|HAState
name|haState
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|HAState
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|haContext
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|haState
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|haState
operator|.
name|shouldPopulateReplQueues
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsn
operator|.
name|getHAContext
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|haContext
argument_list|)
expr_stmt|;
comment|//Make NameNode.getNameNodeMetrics() not return null
name|NameNode
operator|.
name|initMetrics
argument_list|(
name|conf
argument_list|,
name|NamenodeRole
operator|.
name|NAMENODE
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|enterSafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FSNamesystem didn't enter safemode"
argument_list|,
name|fsn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Replication queues were being populated during very first "
operator|+
literal|"safemode"
argument_list|,
operator|!
name|bm
operator|.
name|isPopulatingReplQueues
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|leaveSafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FSNamesystem didn't leave safemode"
argument_list|,
operator|!
name|fsn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Replication queues weren't being populated even after leaving "
operator|+
literal|"safemode"
argument_list|,
name|bm
operator|.
name|isPopulatingReplQueues
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|enterSafeMode
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"FSNamesystem didn't enter safemode"
argument_list|,
name|fsn
operator|.
name|isInSafeMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Replication queues weren't being populated after entering "
operator|+
literal|"safemode 2nd time"
argument_list|,
name|bm
operator|.
name|isPopulatingReplQueues
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testHAStateInNamespaceInfo ()
specifier|public
name|void
name|testHAStateInNamespaceInfo
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
name|FSEditLog
name|fsEditLog
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSImage
name|fsImage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsEditLog
argument_list|)
expr_stmt|;
name|NNStorage
name|nnStorage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|NNStorage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getStorage
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|nnStorage
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsNamesystem
init|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|fsNamesystem
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsn
operator|.
name|getState
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|HAServiceProtocol
operator|.
name|HAServiceState
operator|.
name|ACTIVE
argument_list|)
expr_stmt|;
name|NamespaceInfo
name|nsInfo
init|=
name|fsn
operator|.
name|unprotectedGetNamespaceInfo
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|nsInfo
operator|.
name|getState
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReset ()
specifier|public
name|void
name|testReset
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FSEditLog
name|fsEditLog
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSImage
name|fsImage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsEditLog
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsn
init|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
decl_stmt|;
name|fsn
operator|.
name|imageLoadComplete
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fsn
operator|.
name|isImageLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|clear
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|fsn
operator|.
name|isImageLoaded
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|INodeDirectory
name|root
init|=
operator|(
name|INodeDirectory
operator|)
name|fsn
operator|.
name|getFSDirectory
argument_list|()
operator|.
name|getINode
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|root
operator|.
name|getChildrenList
argument_list|(
name|Snapshot
operator|.
name|CURRENT_STATE_ID
argument_list|)
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|imageLoadComplete
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|fsn
operator|.
name|isImageLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetEffectiveLayoutVersion ()
specifier|public
name|void
name|testGetEffectiveLayoutVersion
parameter_list|()
block|{
name|assertEquals
argument_list|(
operator|-
literal|63
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|true
argument_list|,
operator|-
literal|60
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|61
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|true
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|62
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|true
argument_list|,
operator|-
literal|62
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|63
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|true
argument_list|,
operator|-
literal|63
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|63
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|false
argument_list|,
operator|-
literal|60
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|63
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|false
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|63
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|false
argument_list|,
operator|-
literal|62
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|63
argument_list|,
name|FSNamesystem
operator|.
name|getEffectiveLayoutVersion
argument_list|(
literal|false
argument_list|,
operator|-
literal|63
argument_list|,
operator|-
literal|61
argument_list|,
operator|-
literal|63
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSafemodeReplicationConf ()
specifier|public
name|void
name|testSafemodeReplicationConf
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
name|FSImage
name|fsImage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSEditLog
name|fsEditLog
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsEditLog
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_REPLICATION_MIN_KEY
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsn
init|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
decl_stmt|;
name|Object
name|bmSafeMode
init|=
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|fsn
operator|.
name|getBlockManager
argument_list|()
argument_list|,
literal|"bmSafeMode"
argument_list|)
decl_stmt|;
name|int
name|safeReplication
init|=
operator|(
name|int
operator|)
name|Whitebox
operator|.
name|getInternalState
argument_list|(
name|bmSafeMode
argument_list|,
literal|"safeReplication"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|safeReplication
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
DECL|method|testInitAuditLoggers ()
specifier|public
name|void
name|testInitAuditLoggers
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
name|FSImage
name|fsImage
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSImage
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSEditLog
name|fsEditLog
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FSEditLog
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|fsImage
operator|.
name|getEditLog
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fsEditLog
argument_list|)
expr_stmt|;
name|FSNamesystem
name|fsn
decl_stmt|;
name|List
argument_list|<
name|AuditLogger
argument_list|>
name|auditLoggers
decl_stmt|;
comment|// Not to specify any audit loggers in config
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Disable top logger
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|NNTOP_ENABLED_KEY
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
name|HADOOP_CALLER_CONTEXT_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsn
operator|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
expr_stmt|;
name|auditLoggers
operator|=
name|fsn
operator|.
name|getAuditLoggers
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|auditLoggers
operator|.
name|size
argument_list|()
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|auditLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|FSNamesystem
operator|.
name|FSNamesystemAuditLogger
argument_list|)
expr_stmt|;
name|FSNamesystem
operator|.
name|FSNamesystemAuditLogger
name|defaultAuditLogger
init|=
operator|(
name|FSNamesystem
operator|.
name|FSNamesystemAuditLogger
operator|)
name|auditLoggers
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|defaultAuditLogger
operator|.
name|getCallerContextEnabled
argument_list|()
argument_list|)
expr_stmt|;
comment|// Not to specify any audit loggers in config
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
literal|""
argument_list|)
expr_stmt|;
comment|// Enable top logger
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|NNTOP_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsn
operator|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
expr_stmt|;
name|auditLoggers
operator|=
name|fsn
operator|.
name|getAuditLoggers
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|auditLoggers
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
comment|// the audit loggers order is not defined
for|for
control|(
name|AuditLogger
name|auditLogger
range|:
name|auditLoggers
control|)
block|{
name|assertThat
argument_list|(
name|auditLogger
argument_list|,
name|either
argument_list|(
name|instanceOf
argument_list|(
name|FSNamesystem
operator|.
name|FSNamesystemAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|TopAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Configure default audit loggers in config
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
literal|"default"
argument_list|)
expr_stmt|;
comment|// Enable top logger
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|NNTOP_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsn
operator|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
expr_stmt|;
name|auditLoggers
operator|=
name|fsn
operator|.
name|getAuditLoggers
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|auditLoggers
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
for|for
control|(
name|AuditLogger
name|auditLogger
range|:
name|auditLoggers
control|)
block|{
name|assertThat
argument_list|(
name|auditLogger
argument_list|,
name|either
argument_list|(
name|instanceOf
argument_list|(
name|FSNamesystem
operator|.
name|FSNamesystemAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|TopAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Configure default and customized audit loggers in config with whitespaces
name|conf
operator|.
name|set
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_AUDIT_LOGGERS_KEY
argument_list|,
literal|" default, org.apache.hadoop.hdfs.server.namenode.TestFSNamesystem$DummyAuditLogger  "
argument_list|)
expr_stmt|;
comment|// Enable top logger
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|NNTOP_ENABLED_KEY
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fsn
operator|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
expr_stmt|;
name|auditLoggers
operator|=
name|fsn
operator|.
name|getAuditLoggers
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|auditLoggers
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
for|for
control|(
name|AuditLogger
name|auditLogger
range|:
name|auditLoggers
control|)
block|{
name|assertThat
argument_list|(
name|auditLogger
argument_list|,
name|either
argument_list|(
name|instanceOf
argument_list|(
name|FSNamesystem
operator|.
name|FSNamesystemAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|TopAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
operator|.
name|or
argument_list|(
name|instanceOf
argument_list|(
name|DummyAuditLogger
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|DummyAuditLogger
specifier|static
class|class
name|DummyAuditLogger
implements|implements
name|AuditLogger
block|{
annotation|@
name|Override
DECL|method|initialize (Configuration conf)
specifier|public
name|void
name|initialize
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{     }
annotation|@
name|Override
DECL|method|logAuditEvent (boolean succeeded, String userName, InetAddress addr, String cmd, String src, String dst, FileStatus stat)
specifier|public
name|void
name|logAuditEvent
parameter_list|(
name|boolean
name|succeeded
parameter_list|,
name|String
name|userName
parameter_list|,
name|InetAddress
name|addr
parameter_list|,
name|String
name|cmd
parameter_list|,
name|String
name|src
parameter_list|,
name|String
name|dst
parameter_list|,
name|FileStatus
name|stat
parameter_list|)
block|{     }
block|}
block|}
end_class

end_unit

