begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.datanode.checker
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
name|checker
package|;
end_package

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
name|Optional
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
name|util
operator|.
name|concurrent
operator|.
name|Futures
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
name|util
operator|.
name|concurrent
operator|.
name|ListenableFuture
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
name|server
operator|.
name|datanode
operator|.
name|StorageLocation
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
name|datanode
operator|.
name|fsdataset
operator|.
name|*
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
name|datanode
operator|.
name|fsdataset
operator|.
name|FsVolumeSpi
operator|.
name|VolumeCheckContext
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
name|DiskErrorException
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
name|FakeTimer
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
name|TestName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|Collection
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|TimeUnit
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
name|atomic
operator|.
name|AtomicLong
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
name|server
operator|.
name|datanode
operator|.
name|checker
operator|.
name|VolumeCheckResult
operator|.
name|*
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
name|*
import|;
end_import

begin_comment
comment|/**  * Tests for {@link DatasetVolumeChecker} when the {@link FsVolumeSpi#check}  * method returns different values of {@link VolumeCheckResult}.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
DECL|class|TestDatasetVolumeChecker
specifier|public
class|class
name|TestDatasetVolumeChecker
block|{
DECL|field|LOG
specifier|public
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|TestDatasetVolumeChecker
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|testName
specifier|public
name|TestName
name|testName
init|=
operator|new
name|TestName
argument_list|()
decl_stmt|;
comment|/**    * Run each test case for each possible value of {@link VolumeCheckResult}.    * Including "null" for 'throw exception'.    * @return    */
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}"
argument_list|)
DECL|method|data ()
specifier|public
specifier|static
name|Collection
argument_list|<
name|Object
index|[]
argument_list|>
name|data
parameter_list|()
block|{
name|List
argument_list|<
name|Object
index|[]
argument_list|>
name|values
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|VolumeCheckResult
name|result
range|:
name|VolumeCheckResult
operator|.
name|values
argument_list|()
control|)
block|{
name|values
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
name|result
block|}
argument_list|)
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
operator|new
name|Object
index|[]
block|{
literal|null
block|}
argument_list|)
expr_stmt|;
return|return
name|values
return|;
block|}
comment|/**    * When null, the check call should throw an exception.    */
DECL|field|expectedVolumeHealth
specifier|private
specifier|final
name|VolumeCheckResult
name|expectedVolumeHealth
decl_stmt|;
DECL|field|NUM_VOLUMES
specifier|private
specifier|static
specifier|final
name|int
name|NUM_VOLUMES
init|=
literal|2
decl_stmt|;
DECL|method|TestDatasetVolumeChecker (VolumeCheckResult expectedVolumeHealth)
specifier|public
name|TestDatasetVolumeChecker
parameter_list|(
name|VolumeCheckResult
name|expectedVolumeHealth
parameter_list|)
block|{
name|this
operator|.
name|expectedVolumeHealth
operator|=
name|expectedVolumeHealth
expr_stmt|;
block|}
comment|/**    * Test {@link DatasetVolumeChecker#checkVolume} propagates the    * check to the delegate checker.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCheckOneVolume ()
specifier|public
name|void
name|testCheckOneVolume
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing {}"
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|FsVolumeSpi
name|volume
init|=
name|makeVolumes
argument_list|(
literal|1
argument_list|,
name|expectedVolumeHealth
argument_list|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|DatasetVolumeChecker
name|checker
init|=
operator|new
name|DatasetVolumeChecker
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|,
operator|new
name|FakeTimer
argument_list|()
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setDelegateChecker
argument_list|(
operator|new
name|DummyChecker
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AtomicLong
name|numCallbackInvocations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|/**      * Request a check and ensure it triggered {@link FsVolumeSpi#check}.      */
name|boolean
name|result
init|=
name|checker
operator|.
name|checkVolume
argument_list|(
name|volume
argument_list|,
operator|new
name|DatasetVolumeChecker
operator|.
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|Set
argument_list|<
name|FsVolumeSpi
argument_list|>
name|healthyVolumes
parameter_list|,
name|Set
argument_list|<
name|FsVolumeSpi
argument_list|>
name|failedVolumes
parameter_list|)
block|{
name|numCallbackInvocations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
if|if
condition|(
name|expectedVolumeHealth
operator|!=
literal|null
operator|&&
name|expectedVolumeHealth
operator|!=
name|FAILED
condition|)
block|{
name|assertThat
argument_list|(
name|healthyVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|healthyVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
decl_stmt|;
comment|// Ensure that the check was invoked at least once.
name|verify
argument_list|(
name|volume
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|check
argument_list|(
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
condition|)
block|{
name|assertThat
argument_list|(
name|numCallbackInvocations
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test {@link DatasetVolumeChecker#checkAllVolumes} propagates    * checks for all volumes to the delegate checker.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCheckAllVolumes ()
specifier|public
name|void
name|testCheckAllVolumes
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing {}"
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|volumes
init|=
name|makeVolumes
argument_list|(
name|NUM_VOLUMES
argument_list|,
name|expectedVolumeHealth
argument_list|)
decl_stmt|;
specifier|final
name|FsDatasetSpi
argument_list|<
name|FsVolumeSpi
argument_list|>
name|dataset
init|=
name|makeDataset
argument_list|(
name|volumes
argument_list|)
decl_stmt|;
specifier|final
name|DatasetVolumeChecker
name|checker
init|=
operator|new
name|DatasetVolumeChecker
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|,
operator|new
name|FakeTimer
argument_list|()
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setDelegateChecker
argument_list|(
operator|new
name|DummyChecker
argument_list|()
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|FsVolumeSpi
argument_list|>
name|failedVolumes
init|=
name|checker
operator|.
name|checkAllVolumes
argument_list|(
name|dataset
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Got back {} failed volumes"
argument_list|,
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedVolumeHealth
operator|==
literal|null
operator|||
name|expectedVolumeHealth
operator|==
name|FAILED
condition|)
block|{
name|assertThat
argument_list|(
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|NUM_VOLUMES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertTrue
argument_list|(
name|failedVolumes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Ensure each volume's check() method was called exactly once.
for|for
control|(
name|FsVolumeSpi
name|volume
range|:
name|volumes
control|)
block|{
name|verify
argument_list|(
name|volume
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|check
argument_list|(
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Unit test for {@link DatasetVolumeChecker#checkAllVolumesAsync}.    *    * @throws Exception    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testCheckAllVolumesAsync ()
specifier|public
name|void
name|testCheckAllVolumesAsync
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Executing {}"
argument_list|,
name|testName
operator|.
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|volumes
init|=
name|makeVolumes
argument_list|(
name|NUM_VOLUMES
argument_list|,
name|expectedVolumeHealth
argument_list|)
decl_stmt|;
specifier|final
name|FsDatasetSpi
argument_list|<
name|FsVolumeSpi
argument_list|>
name|dataset
init|=
name|makeDataset
argument_list|(
name|volumes
argument_list|)
decl_stmt|;
specifier|final
name|DatasetVolumeChecker
name|checker
init|=
operator|new
name|DatasetVolumeChecker
argument_list|(
operator|new
name|HdfsConfiguration
argument_list|()
argument_list|,
operator|new
name|FakeTimer
argument_list|()
argument_list|)
decl_stmt|;
name|checker
operator|.
name|setDelegateChecker
argument_list|(
operator|new
name|DummyChecker
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|AtomicLong
name|numCallbackInvocations
init|=
operator|new
name|AtomicLong
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|boolean
name|result
init|=
name|checker
operator|.
name|checkAllVolumesAsync
argument_list|(
name|dataset
argument_list|,
operator|new
name|DatasetVolumeChecker
operator|.
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|call
parameter_list|(
name|Set
argument_list|<
name|FsVolumeSpi
argument_list|>
name|healthyVolumes
parameter_list|,
name|Set
argument_list|<
name|FsVolumeSpi
argument_list|>
name|failedVolumes
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got back {} failed volumes"
argument_list|,
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expectedVolumeHealth
operator|==
literal|null
operator|||
name|expectedVolumeHealth
operator|==
name|FAILED
condition|)
block|{
name|assertThat
argument_list|(
name|healthyVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|NUM_VOLUMES
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertThat
argument_list|(
name|healthyVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
name|NUM_VOLUMES
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|failedVolumes
operator|.
name|size
argument_list|()
argument_list|,
name|is
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|numCallbackInvocations
operator|.
name|incrementAndGet
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
decl_stmt|;
comment|// The callback should be invoked exactly once.
if|if
condition|(
name|result
condition|)
block|{
name|assertThat
argument_list|(
name|numCallbackInvocations
operator|.
name|get
argument_list|()
argument_list|,
name|is
argument_list|(
literal|1L
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Ensure each volume's check() method was called exactly once.
for|for
control|(
name|FsVolumeSpi
name|volume
range|:
name|volumes
control|)
block|{
name|verify
argument_list|(
name|volume
argument_list|,
name|times
argument_list|(
literal|1
argument_list|)
argument_list|)
operator|.
name|check
argument_list|(
name|anyObject
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * A checker to wraps the result of {@link FsVolumeSpi#check} in    * an ImmediateFuture.    */
DECL|class|DummyChecker
specifier|static
class|class
name|DummyChecker
implements|implements
name|AsyncChecker
argument_list|<
name|VolumeCheckContext
argument_list|,
name|VolumeCheckResult
argument_list|>
block|{
annotation|@
name|Override
DECL|method|schedule ( Checkable<VolumeCheckContext, VolumeCheckResult> target, VolumeCheckContext context)
specifier|public
name|Optional
argument_list|<
name|ListenableFuture
argument_list|<
name|VolumeCheckResult
argument_list|>
argument_list|>
name|schedule
parameter_list|(
name|Checkable
argument_list|<
name|VolumeCheckContext
argument_list|,
name|VolumeCheckResult
argument_list|>
name|target
parameter_list|,
name|VolumeCheckContext
name|context
parameter_list|)
block|{
try|try
block|{
return|return
name|Optional
operator|.
name|of
argument_list|(
name|Futures
operator|.
name|immediateFuture
argument_list|(
name|target
operator|.
name|check
argument_list|(
name|context
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"check routine threw exception "
operator|+
name|e
argument_list|)
expr_stmt|;
return|return
name|Optional
operator|.
name|of
argument_list|(
name|Futures
operator|.
name|immediateFailedFuture
argument_list|(
name|e
argument_list|)
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|shutdownAndWait (long timeout, TimeUnit timeUnit)
specifier|public
name|void
name|shutdownAndWait
parameter_list|(
name|long
name|timeout
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
throws|throws
name|InterruptedException
block|{
comment|// Nothing to cancel.
block|}
block|}
comment|/**    * Create a dataset with the given volumes.    */
DECL|method|makeDataset (List<FsVolumeSpi> volumes)
specifier|static
name|FsDatasetSpi
argument_list|<
name|FsVolumeSpi
argument_list|>
name|makeDataset
parameter_list|(
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|volumes
parameter_list|)
throws|throws
name|Exception
block|{
comment|// Create dataset and init volume health.
specifier|final
name|FsDatasetSpi
argument_list|<
name|FsVolumeSpi
argument_list|>
name|dataset
init|=
name|mock
argument_list|(
name|FsDatasetSpi
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
name|references
init|=
operator|new
name|FsDatasetSpi
operator|.
name|FsVolumeReferences
argument_list|(
name|volumes
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|dataset
operator|.
name|getFsVolumeReferences
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|references
argument_list|)
expr_stmt|;
return|return
name|dataset
return|;
block|}
DECL|method|makeVolumes ( int numVolumes, VolumeCheckResult health)
specifier|static
name|List
argument_list|<
name|FsVolumeSpi
argument_list|>
name|makeVolumes
parameter_list|(
name|int
name|numVolumes
parameter_list|,
name|VolumeCheckResult
name|health
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
argument_list|<>
argument_list|(
name|numVolumes
argument_list|)
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
name|numVolumes
condition|;
operator|++
name|i
control|)
block|{
specifier|final
name|FsVolumeSpi
name|volume
init|=
name|mock
argument_list|(
name|FsVolumeSpi
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|FsVolumeReference
name|reference
init|=
name|mock
argument_list|(
name|FsVolumeReference
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|StorageLocation
name|location
init|=
name|mock
argument_list|(
name|StorageLocation
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|reference
operator|.
name|getVolume
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|volume
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|volume
operator|.
name|obtainReference
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|reference
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|volume
operator|.
name|getStorageLocation
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|location
argument_list|)
expr_stmt|;
if|if
condition|(
name|health
operator|!=
literal|null
condition|)
block|{
name|when
argument_list|(
name|volume
operator|.
name|check
argument_list|(
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|health
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|DiskErrorException
name|de
init|=
operator|new
name|DiskErrorException
argument_list|(
literal|"Fake Exception"
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|volume
operator|.
name|check
argument_list|(
name|anyObject
argument_list|()
argument_list|)
argument_list|)
operator|.
name|thenThrow
argument_list|(
name|de
argument_list|)
expr_stmt|;
block|}
name|volumes
operator|.
name|add
argument_list|(
name|volume
argument_list|)
expr_stmt|;
block|}
return|return
name|volumes
return|;
block|}
block|}
end_class

end_unit

