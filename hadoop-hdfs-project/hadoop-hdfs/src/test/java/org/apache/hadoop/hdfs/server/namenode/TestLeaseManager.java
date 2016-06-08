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
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|is
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertNull
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
name|collect
operator|.
name|Lists
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
name|permission
operator|.
name|FsPermission
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
name|permission
operator|.
name|PermissionStatus
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
name|BlockInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|junit
operator|.
name|rules
operator|.
name|Timeout
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|assertThat
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
name|*
import|;
end_import

begin_class
DECL|class|TestLeaseManager
specifier|public
class|class
name|TestLeaseManager
block|{
annotation|@
name|Rule
DECL|field|timeout
specifier|public
name|Timeout
name|timeout
init|=
operator|new
name|Timeout
argument_list|(
literal|300000
argument_list|)
decl_stmt|;
DECL|field|maxLockHoldToReleaseLeaseMs
specifier|public
specifier|static
name|long
name|maxLockHoldToReleaseLeaseMs
init|=
literal|100
decl_stmt|;
annotation|@
name|Test
DECL|method|testRemoveLeases ()
specifier|public
name|void
name|testRemoveLeases
parameter_list|()
throws|throws
name|Exception
block|{
name|FSNamesystem
name|fsn
init|=
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|LeaseManager
name|lm
init|=
operator|new
name|LeaseManager
argument_list|(
name|fsn
argument_list|)
decl_stmt|;
name|ArrayList
argument_list|<
name|Long
argument_list|>
name|ids
init|=
name|Lists
operator|.
name|newArrayList
argument_list|(
name|INodeId
operator|.
name|ROOT_INODE_ID
operator|+
literal|1
argument_list|,
name|INodeId
operator|.
name|ROOT_INODE_ID
operator|+
literal|2
argument_list|,
name|INodeId
operator|.
name|ROOT_INODE_ID
operator|+
literal|3
argument_list|,
name|INodeId
operator|.
name|ROOT_INODE_ID
operator|+
literal|4
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|id
range|:
name|ids
control|)
block|{
name|lm
operator|.
name|addLease
argument_list|(
literal|"foo"
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|lm
operator|.
name|getINodeIdWithLeases
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|id
range|:
name|ids
control|)
block|{
name|lm
operator|.
name|removeLease
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|lm
operator|.
name|getINodeIdWithLeases
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** Check that LeaseManager.checkLease release some leases    */
annotation|@
name|Test
DECL|method|testCheckLease ()
specifier|public
name|void
name|testCheckLease
parameter_list|()
block|{
name|LeaseManager
name|lm
init|=
operator|new
name|LeaseManager
argument_list|(
name|makeMockFsNameSystem
argument_list|()
argument_list|)
decl_stmt|;
name|long
name|numLease
init|=
literal|100
decl_stmt|;
comment|//Make sure the leases we are going to add exceed the hard limit
name|lm
operator|.
name|setLeasePeriod
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<=
name|numLease
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
comment|//Add some leases to the LeaseManager
name|lm
operator|.
name|addLease
argument_list|(
literal|"holder"
operator|+
name|i
argument_list|,
name|INodeId
operator|.
name|ROOT_INODE_ID
operator|+
name|i
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|numLease
argument_list|,
name|lm
operator|.
name|countLease
argument_list|()
argument_list|)
expr_stmt|;
comment|//Initiate a call to checkLease. This should exit within the test timeout
name|lm
operator|.
name|checkLeases
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|lm
operator|.
name|countLease
argument_list|()
operator|<
name|numLease
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCountPath ()
specifier|public
name|void
name|testCountPath
parameter_list|()
block|{
name|LeaseManager
name|lm
init|=
operator|new
name|LeaseManager
argument_list|(
name|makeMockFsNameSystem
argument_list|()
argument_list|)
decl_stmt|;
name|lm
operator|.
name|addLease
argument_list|(
literal|"holder1"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
name|lm
operator|.
name|addLease
argument_list|(
literal|"holder2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|lm
operator|.
name|addLease
argument_list|(
literal|"holder2"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
comment|// Duplicate addition
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove a couple of non-existing leases. countPath should not change.
name|lm
operator|.
name|removeLease
argument_list|(
literal|"holder2"
argument_list|,
name|stubInodeFile
argument_list|(
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|lm
operator|.
name|removeLease
argument_list|(
literal|"InvalidLeaseHolder"
argument_list|,
name|stubInodeFile
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
name|INodeFile
name|file
init|=
name|stubInodeFile
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|lm
operator|.
name|reassignLease
argument_list|(
name|lm
operator|.
name|getLease
argument_list|(
name|file
argument_list|)
argument_list|,
name|file
argument_list|,
literal|"holder2"
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|2L
argument_list|)
argument_list|)
expr_stmt|;
comment|// Count unchanged on reassign
name|lm
operator|.
name|removeLease
argument_list|(
literal|"holder2"
argument_list|,
name|stubInodeFile
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
comment|// Remove existing
name|assertThat
argument_list|(
name|lm
operator|.
name|countPath
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|makeMockFsNameSystem ()
specifier|private
specifier|static
name|FSNamesystem
name|makeMockFsNameSystem
parameter_list|()
block|{
name|FSDirectory
name|dir
init|=
name|mock
argument_list|(
name|FSDirectory
operator|.
name|class
argument_list|)
decl_stmt|;
name|FSNamesystem
name|fsn
init|=
name|mock
argument_list|(
name|FSNamesystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fsn
operator|.
name|isRunning
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fsn
operator|.
name|hasWriteLock
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fsn
operator|.
name|getFSDirectory
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|dir
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fsn
operator|.
name|getMaxLockHoldToReleaseLeaseMs
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|maxLockHoldToReleaseLeaseMs
argument_list|)
expr_stmt|;
return|return
name|fsn
return|;
block|}
DECL|method|stubInodeFile (long inodeId)
specifier|private
specifier|static
name|INodeFile
name|stubInodeFile
parameter_list|(
name|long
name|inodeId
parameter_list|)
block|{
name|PermissionStatus
name|p
init|=
operator|new
name|PermissionStatus
argument_list|(
literal|"dummy"
argument_list|,
literal|"dummy"
argument_list|,
operator|new
name|FsPermission
argument_list|(
operator|(
name|short
operator|)
literal|0777
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|INodeFile
argument_list|(
name|inodeId
argument_list|,
literal|"/foo"
operator|.
name|getBytes
argument_list|()
argument_list|,
name|p
argument_list|,
literal|0L
argument_list|,
literal|0L
argument_list|,
name|BlockInfo
operator|.
name|EMPTY_ARRAY
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
literal|1L
argument_list|)
return|;
block|}
block|}
end_class

end_unit

