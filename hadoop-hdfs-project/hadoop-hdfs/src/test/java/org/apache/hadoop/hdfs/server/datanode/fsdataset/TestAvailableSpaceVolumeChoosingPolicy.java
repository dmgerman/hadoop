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
name|DFS_DATANODE_FSDATASET_VOLUME_CHOOSING_BALANCED_SPACE_THRESHOLD_KEY
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
name|DFS_DATANODE_FSDATASET_VOLUME_CHOOSING_BALANCED_SPACE_PREFERENCE_PERCENT_KEY
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
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|Configurable
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
DECL|class|TestAvailableSpaceVolumeChoosingPolicy
specifier|public
class|class
name|TestAvailableSpaceVolumeChoosingPolicy
block|{
DECL|field|RANDOMIZED_ITERATIONS
specifier|private
specifier|static
specifier|final
name|int
name|RANDOMIZED_ITERATIONS
init|=
literal|10000
decl_stmt|;
DECL|field|RANDOMIZED_ERROR_PERCENT
specifier|private
specifier|static
specifier|final
name|float
name|RANDOMIZED_ERROR_PERCENT
init|=
literal|0.05f
decl_stmt|;
DECL|field|RANDOMIZED_ALLOWED_ERROR
specifier|private
specifier|static
specifier|final
name|long
name|RANDOMIZED_ALLOWED_ERROR
init|=
call|(
name|long
call|)
argument_list|(
name|RANDOMIZED_ERROR_PERCENT
operator|*
name|RANDOMIZED_ITERATIONS
argument_list|)
decl_stmt|;
DECL|method|initPolicy (VolumeChoosingPolicy<FsVolumeSpi> policy, float preferencePercent)
specifier|private
specifier|static
name|void
name|initPolicy
parameter_list|(
name|VolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
parameter_list|,
name|float
name|preferencePercent
parameter_list|)
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// Set the threshold to consider volumes imbalanced to 1MB
name|conf
operator|.
name|setLong
argument_list|(
name|DFS_DATANODE_FSDATASET_VOLUME_CHOOSING_BALANCED_SPACE_THRESHOLD_KEY
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
comment|// 1MB
name|conf
operator|.
name|setFloat
argument_list|(
name|DFS_DATANODE_FSDATASET_VOLUME_CHOOSING_BALANCED_SPACE_PREFERENCE_PERCENT_KEY
argument_list|,
name|preferencePercent
argument_list|)
expr_stmt|;
operator|(
operator|(
name|Configurable
operator|)
name|policy
operator|)
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
comment|// Test the Round-Robin block-volume fallback path when all volumes are within
comment|// the threshold.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
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
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|TestRoundRobinVolumeChoosingPolicy
operator|.
name|testRR
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
comment|// ChooseVolume should throw DiskOutOfSpaceException
comment|// with volume and block sizes in exception message.
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testRRPolicyExceptionMessage ()
specifier|public
name|void
name|testRRPolicyExceptionMessage
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
operator|new
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
argument_list|()
decl_stmt|;
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
name|TestRoundRobinVolumeChoosingPolicy
operator|.
name|testRRPolicyExceptionMessage
argument_list|(
name|policy
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testTwoUnbalancedVolumes ()
specifier|public
name|void
name|testTwoUnbalancedVolumes
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
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
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
comment|// First volume with 1MB free space
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
literal|1024L
operator|*
literal|1024L
argument_list|)
expr_stmt|;
comment|// Second volume with 3MB free space, which is a difference of 2MB, more
comment|// than the threshold of 1MB.
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
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
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
literal|100
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
literal|100
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
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testThreeUnbalancedVolumes ()
specifier|public
name|void
name|testThreeUnbalancedVolumes
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
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
comment|// First volume with 1MB free space
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
literal|1024L
operator|*
literal|1024L
argument_list|)
expr_stmt|;
comment|// Second volume with 3MB free space, which is a difference of 2MB, more
comment|// than the threshold of 1MB.
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
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
expr_stmt|;
comment|// Third volume, again with 3MB free space.
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
literal|2
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
expr_stmt|;
comment|// We should alternate assigning between the two volumes with a lot of free
comment|// space.
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|1.0f
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
literal|100
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
literal|2
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
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
literal|100
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
literal|2
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// All writes should be assigned to the volume with the least free space.
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|0.0f
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
literal|100
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
literal|100
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
literal|100
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
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testFourUnbalancedVolumes ()
specifier|public
name|void
name|testFourUnbalancedVolumes
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
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
comment|// First volume with 1MB free space
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
literal|1024L
operator|*
literal|1024L
argument_list|)
expr_stmt|;
comment|// Second volume with 1MB + 1 byte free space
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
literal|1024L
operator|*
literal|1024L
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|// Third volume with 3MB free space, which is a difference of 2MB, more
comment|// than the threshold of 1MB.
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
literal|2
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
expr_stmt|;
comment|// Fourth volume, again with 3MB free space.
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
literal|3
argument_list|)
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
expr_stmt|;
comment|// We should alternate assigning between the two volumes with a lot of free
comment|// space.
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|1.0f
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
literal|2
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
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
literal|3
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
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
literal|2
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
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
literal|3
argument_list|)
argument_list|,
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
argument_list|)
argument_list|)
expr_stmt|;
comment|// We should alternate assigning between the two volumes with less free
comment|// space.
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|0.0f
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
literal|100
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
literal|100
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
literal|100
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
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testNotEnoughSpaceOnSelectedVolume ()
specifier|public
name|void
name|testNotEnoughSpaceOnSelectedVolume
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
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
comment|// First volume with 1MB free space
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
literal|1024L
operator|*
literal|1024L
argument_list|)
expr_stmt|;
comment|// Second volume with 3MB free space, which is a difference of 2MB, more
comment|// than the threshold of 1MB.
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
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
expr_stmt|;
comment|// All writes should be assigned to the volume with the least free space.
comment|// However, if the volume with the least free space doesn't have enough
comment|// space to accept the replica size, and another volume does have enough
comment|// free space, that should be chosen instead.
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|0.0f
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
literal|1024L
operator|*
literal|1024L
operator|*
literal|2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|testAvailableSpaceChanges ()
specifier|public
name|void
name|testAvailableSpaceChanges
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
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|initPolicy
argument_list|(
name|policy
argument_list|,
literal|1.0f
argument_list|)
expr_stmt|;
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
comment|// First volume with 1MB free space
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
literal|1024L
operator|*
literal|1024L
argument_list|)
expr_stmt|;
comment|// Second volume with 3MB free space, which is a difference of 2MB, more
comment|// than the threshold of 1MB.
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
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|1
argument_list|)
expr_stmt|;
comment|// After the third check, return 1MB.
comment|// Should still be able to get a volume for the replica even though the
comment|// available space on the second volume changed.
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
literal|100
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|randomizedTest1 ()
specifier|public
name|void
name|randomizedTest1
parameter_list|()
throws|throws
name|Exception
block|{
name|doRandomizedTest
argument_list|(
literal|0.75f
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|randomizedTest2 ()
specifier|public
name|void
name|randomizedTest2
parameter_list|()
throws|throws
name|Exception
block|{
name|doRandomizedTest
argument_list|(
literal|0.75f
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|randomizedTest3 ()
specifier|public
name|void
name|randomizedTest3
parameter_list|()
throws|throws
name|Exception
block|{
name|doRandomizedTest
argument_list|(
literal|0.75f
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|60000
argument_list|)
DECL|method|randomizedTest4 ()
specifier|public
name|void
name|randomizedTest4
parameter_list|()
throws|throws
name|Exception
block|{
name|doRandomizedTest
argument_list|(
literal|0.90f
argument_list|,
literal|5
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/*    * Ensure that we randomly select the lesser-used volumes with appropriate    * frequency.    */
DECL|method|doRandomizedTest (float preferencePercent, int lowSpaceVolumes, int highSpaceVolumes)
specifier|public
name|void
name|doRandomizedTest
parameter_list|(
name|float
name|preferencePercent
parameter_list|,
name|int
name|lowSpaceVolumes
parameter_list|,
name|int
name|highSpaceVolumes
parameter_list|)
throws|throws
name|Exception
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|final
name|AvailableSpaceVolumeChoosingPolicy
argument_list|<
name|FsVolumeSpi
argument_list|>
name|policy
init|=
name|ReflectionUtils
operator|.
name|newInstance
argument_list|(
name|AvailableSpaceVolumeChoosingPolicy
operator|.
name|class
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
comment|// Volumes with 1MB free space
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|lowSpaceVolumes
condition|;
name|i
operator|++
control|)
block|{
name|FsVolumeSpi
name|volume
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volume
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
argument_list|)
expr_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
comment|// Volumes with 3MB free space
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|highSpaceVolumes
condition|;
name|i
operator|++
control|)
block|{
name|FsVolumeSpi
name|volume
init|=
name|Mockito
operator|.
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
decl_stmt|;
name|Mockito
operator|.
name|when
argument_list|(
name|volume
operator|.
name|getAvailable
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|1024L
operator|*
literal|1024L
operator|*
literal|3
argument_list|)
expr_stmt|;
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
name|initPolicy
argument_list|(
name|policy
argument_list|,
name|preferencePercent
argument_list|)
expr_stmt|;
name|long
name|lowAvailableSpaceVolumeSelected
init|=
literal|0
decl_stmt|;
name|long
name|highAvailableSpaceVolumeSelected
init|=
literal|0
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
name|RANDOMIZED_ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|FsVolumeSpi
name|volume
init|=
name|policy
operator|.
name|chooseVolume
argument_list|(
name|volumes
argument_list|,
literal|100
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|volumes
operator|.
name|size
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
comment|// Note how many times the first low available volume was selected
if|if
condition|(
name|volume
operator|==
name|volumes
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|&&
name|j
operator|==
literal|0
condition|)
block|{
name|lowAvailableSpaceVolumeSelected
operator|++
expr_stmt|;
block|}
comment|// Note how many times the first high available volume was selected
if|if
condition|(
name|volume
operator|==
name|volumes
operator|.
name|get
argument_list|(
name|j
argument_list|)
operator|&&
name|j
operator|==
name|lowSpaceVolumes
condition|)
block|{
name|highAvailableSpaceVolumeSelected
operator|++
expr_stmt|;
break|break;
block|}
block|}
block|}
comment|// Calculate the expected ratio of how often low available space volumes
comment|// were selected vs. high available space volumes.
name|float
name|expectedSelectionRatio
init|=
name|preferencePercent
operator|/
operator|(
literal|1
operator|-
name|preferencePercent
operator|)
decl_stmt|;
name|GenericTestUtils
operator|.
name|assertValueNear
argument_list|(
call|(
name|long
call|)
argument_list|(
name|lowAvailableSpaceVolumeSelected
operator|*
name|expectedSelectionRatio
argument_list|)
argument_list|,
name|highAvailableSpaceVolumeSelected
argument_list|,
name|RANDOMIZED_ALLOWED_ERROR
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

