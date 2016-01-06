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
name|test
operator|.
name|GenericTestUtils
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
operator|.
name|LogCapturer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Level
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
name|Assert
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
name|org
operator|.
name|mockito
operator|.
name|internal
operator|.
name|util
operator|.
name|reflection
operator|.
name|Whitebox
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
name|CountDownLatch
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
name|ExecutorService
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
name|Executors
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
DECL|method|testFsLockFairness ()
specifier|public
name|void
name|testFsLockFairness
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
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"dfs.namenode.fslock.fair"
argument_list|,
literal|true
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
name|assertTrue
argument_list|(
name|fsNamesystem
operator|.
name|getFsLockForTests
argument_list|()
operator|.
name|isFair
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"dfs.namenode.fslock.fair"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|fsNamesystem
operator|=
operator|new
name|FSNamesystem
argument_list|(
name|conf
argument_list|,
name|fsImage
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|fsNamesystem
operator|.
name|getFsLockForTests
argument_list|()
operator|.
name|isFair
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFSNamesystemLockCompatibility ()
specifier|public
name|void
name|testFSNamesystemLockCompatibility
parameter_list|()
block|{
name|FSNamesystemLock
name|rwLock
init|=
operator|new
name|FSNamesystemLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rwLock
operator|.
name|getReadHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rwLock
operator|.
name|getReadHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rwLock
operator|.
name|getReadHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rwLock
operator|.
name|getReadHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rwLock
operator|.
name|getReadHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|rwLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rwLock
operator|.
name|getWriteHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|rwLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rwLock
operator|.
name|getWriteHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|rwLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|rwLock
operator|.
name|getWriteHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|rwLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|rwLock
operator|.
name|getWriteHoldCount
argument_list|()
argument_list|)
expr_stmt|;
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|rwLock
operator|.
name|isWriteLockedByCurrentThread
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rwLock
operator|.
name|getWriteHoldCount
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
DECL|method|testFSLockGetWaiterCount ()
specifier|public
name|void
name|testFSLockGetWaiterCount
parameter_list|()
throws|throws
name|InterruptedException
block|{
specifier|final
name|int
name|threadCount
init|=
literal|3
decl_stmt|;
specifier|final
name|CountDownLatch
name|latch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|threadCount
argument_list|)
decl_stmt|;
specifier|final
name|FSNamesystemLock
name|rwLock
init|=
operator|new
name|FSNamesystemLock
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|rwLock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
name|ExecutorService
name|helper
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|threadCount
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|x
init|=
literal|0
init|;
name|x
operator|<
name|threadCount
condition|;
name|x
operator|++
control|)
block|{
name|helper
operator|.
name|execute
argument_list|(
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|latch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|rwLock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
name|latch
operator|.
name|await
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|// Lets all threads get BLOCKED
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Expected number of blocked thread not found"
argument_list|,
name|threadCount
argument_list|,
name|rwLock
operator|.
name|getQueueLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test when FSNamesystem lock is held for a long time, logger will report it.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|45000
argument_list|)
DECL|method|testFSLockLongHoldingReport ()
specifier|public
name|void
name|testFSLockLongHoldingReport
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
name|LogCapturer
name|logs
init|=
name|LogCapturer
operator|.
name|captureLogs
argument_list|(
name|FSNamesystem
operator|.
name|LOG
argument_list|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|setLogLevel
argument_list|(
name|FSNamesystem
operator|.
name|LOG
argument_list|,
name|Level
operator|.
name|INFO
argument_list|)
expr_stmt|;
comment|// Don't report if the write lock is held for a short time
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|FSNamesystem
operator|.
name|WRITELOCK_REPORTING_THRESHOLD
operator|/
literal|2
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Report if the write lock is held for a long time
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|FSNamesystem
operator|.
name|WRITELOCK_REPORTING_THRESHOLD
operator|+
literal|100
argument_list|)
expr_stmt|;
name|logs
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Report if the write lock is held (interruptibly) for a long time
name|fsn
operator|.
name|writeLockInterruptibly
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|FSNamesystem
operator|.
name|WRITELOCK_REPORTING_THRESHOLD
operator|+
literal|100
argument_list|)
expr_stmt|;
name|logs
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// Report if it's held for a long time when re-entering write lock
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|FSNamesystem
operator|.
name|WRITELOCK_REPORTING_THRESHOLD
operator|/
literal|2
operator|+
literal|1
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|writeLockInterruptibly
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|FSNamesystem
operator|.
name|WRITELOCK_REPORTING_THRESHOLD
operator|/
literal|2
operator|+
literal|1
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|writeLock
argument_list|()
expr_stmt|;
name|Thread
operator|.
name|sleep
argument_list|(
name|FSNamesystem
operator|.
name|WRITELOCK_REPORTING_THRESHOLD
operator|/
literal|2
argument_list|)
expr_stmt|;
name|logs
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logs
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|logs
operator|.
name|clearOutput
argument_list|()
expr_stmt|;
name|fsn
operator|.
name|writeUnlock
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|logs
operator|.
name|getOutput
argument_list|()
operator|.
name|contains
argument_list|(
name|GenericTestUtils
operator|.
name|getMethodName
argument_list|()
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
block|}
end_class

end_unit

