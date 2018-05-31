begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone.container.common.impl
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
operator|.
name|container
operator|.
name|common
operator|.
name|impl
package|;
end_package

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
name|mockito
operator|.
name|Mockito
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

begin_comment
comment|/**  * Tests {@link RoundRobinVolumeChoosingPolicy}.  */
end_comment

begin_class
DECL|class|TestRoundRobinVolumeChoosingPolicy
specifier|public
class|class
name|TestRoundRobinVolumeChoosingPolicy
block|{
DECL|field|policy
specifier|private
name|RoundRobinVolumeChoosingPolicy
name|policy
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
block|{
name|policy
operator|=
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
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRRVolumeChoosingPolicy ()
specifier|public
name|void
name|testRRVolumeChoosingPolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|VolumeInfo
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
name|VolumeInfo
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// The first volume has only 100L space, so the policy should
comment|// choose the second one in case we ask for more.
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
argument_list|)
argument_list|)
expr_stmt|;
comment|// Fail if no volume has enough space available
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
name|List
argument_list|<
name|VolumeInfo
argument_list|>
name|volumes
init|=
operator|new
name|ArrayList
argument_list|<>
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
name|VolumeInfo
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
name|VolumeInfo
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
name|int
name|blockSize
init|=
literal|300
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
literal|200
operator|+
literal|" B) is less than the container size (="
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
block|}
end_class

end_unit

