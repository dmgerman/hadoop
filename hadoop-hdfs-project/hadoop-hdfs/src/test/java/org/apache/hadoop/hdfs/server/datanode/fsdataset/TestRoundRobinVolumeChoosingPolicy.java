begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.fsdataset
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
name|datanode
operator|.
name|fsdataset
package|;
end_package

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
name|util
operator|.
name|ArrayList
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
name|StorageType
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
name|util
operator|.
name|DiskChecker
operator|.
name|DiskOutOfSpaceException
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
name|util
operator|.
name|ReflectionUtils
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

begin_class
DECL|class|TestRoundRobinVolumeChoosingPolicy
specifier|public
class|class
name|TestRoundRobinVolumeChoosingPolicy
block|{
comment|// Test the Round-Robin block-volume choosing algorithm.
annotation|@
name|Test
DECL|method|testRR ()
specifier|public
name|void
name|testRR
parameter_list|()
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|RoundRobinVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|testRR
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
DECL|method|testRR (VolumeChoosingPolicy<FsVolumeSpi> policy)
specifier|public
specifier|static
name|void
name|testRR
parameter_list|(
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
comment|// First volume, with 100 bytes of space.
name|volumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
comment|// Second volume, with 200 bytes of space.
name|volumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|200L
argument_list|)
expr_stmt|;
comment|// Test two rounds of round-robin choosing
name|Assert
operator|.
name|assertEquals
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// The first volume has only 100L space, so the policy should
comment|// wisely choose the second one in case we ask for more.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|150
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fail if no volume can be chosen?
try|try
block|{
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Passed.
block|}
block|}
comment|// ChooseVolume should throw DiskOutOfSpaceException
comment|// with volume and block sizes in exception message.
annotation|@
name|Test
DECL|method|testRRPolicyExceptionMessage ()
specifier|public
name|void
name|testRRPolicyExceptionMessage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
name|testRRPolicyExceptionMessage
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
DECL|method|testRRPolicyExceptionMessage ( VolumeChoosingPolicy<FsVolumeSpi> policy)
specifier|public
specifier|static
name|void
name|testRRPolicyExceptionMessage
parameter_list|(
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
comment|// First volume, with 500 bytes of space.
name|volumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|500L
argument_list|)
expr_stmt|;
comment|// Second volume, with 600 bytes of space.
name|volumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|600L
argument_list|)
expr_stmt|;
name|int
name|blockSize
init|=
literal|700
decl_stmt|;
try|try
block|{
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
name|blockSize
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"expected to throw DiskOutOfSpaceException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskOutOfSpaceException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Not returnig the expected message"
argument_list|,
literal|"Out of space: The volume with the most available space (="
operator|+
literal|600
operator|+
literal|" B) is less than the block size (="
operator|+
name|blockSize
operator|+
literal|" B)."
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Test Round-Robin choosing algorithm with heterogeneous storage.
annotation|@
name|Test
DECL|method|testRRPolicyWithStorageTypes ()
specifier|public
name|void
name|testRRPolicyWithStorageTypes
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
operator|new
name|RoundRobinVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
name|testRRPolicyWithStorageTypes
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
DECL|method|testRRPolicyWithStorageTypes ( VolumeChoosingPolicy<FsVolumeSpi> policy)
specifier|public
specifier|static
name|void
name|testRRPolicyWithStorageTypes
parameter_list|(
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|diskVolumes
init|=
operator|new
name|ArrayList
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|ssdVolumes
init|=
operator|new
name|ArrayList
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
comment|// Add two DISK volumes to diskVolumes
name|diskVolumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|diskVolumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|StorageType
operator|.
name|DISK
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
comment|// Add two SSD volumes to ssdVolumes
name|ssdVolumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|ssdVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|ssdVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|200L
argument_list|)
expr_stmt|;
name|ssdVolumes
operator|.
name|add
argument_list|(
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|ssdVolumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getStorageType
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|StorageType
operator|.
name|SSD
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|ssdVolumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|100L
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|diskVolumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Independent Round-Robin for different storage type
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ssdVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|ssdVolumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// Take block size into consideration
name|Assert
operator|.
name|assertEquals
argument_list|(
name|ssdVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|ssdVolumes
argument_list|,
literal|150L
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|1
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|diskVolumes
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|diskVolumes
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|diskVolumes
argument_list|,
literal|50L
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|policy
operator|.
name|chooseVolume
argument_list|(
name|diskVolumes
argument_list|,
literal|200L
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|fail
argument_list|(
literal|"Should throw an DiskOutOfSpaceException before this!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DiskOutOfSpaceException
name|e
parameter_list|)
block|{
comment|// Pass.
block|}
block|}
block|}
end_class

end_unit

