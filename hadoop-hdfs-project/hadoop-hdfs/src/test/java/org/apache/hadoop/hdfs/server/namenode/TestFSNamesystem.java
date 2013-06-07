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
name|assertTrue
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
literal|"importantFile"
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
argument_list|()
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
name|Whitebox
operator|.
name|setInternalState
argument_list|(
name|fsn
argument_list|,
literal|"haContext"
argument_list|,
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
name|fsn
operator|.
name|isPopulatingReplQueues
argument_list|()
argument_list|)
expr_stmt|;
name|fsn
operator|.
name|leaveSafeMode
argument_list|()
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
name|fsn
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
name|fsn
operator|.
name|isPopulatingReplQueues
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

