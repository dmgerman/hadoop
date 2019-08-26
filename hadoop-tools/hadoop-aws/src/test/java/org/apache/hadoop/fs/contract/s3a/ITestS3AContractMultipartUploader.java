begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.contract.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|contract
operator|.
name|s3a
package|;
end_package

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
name|Path
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
name|contract
operator|.
name|AbstractFSContract
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
name|contract
operator|.
name|AbstractContractMultipartUploaderTest
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
name|contract
operator|.
name|ContractTestUtils
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
name|s3a
operator|.
name|S3AFileSystem
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
name|s3a
operator|.
name|WriteOperationHelper
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
name|fs
operator|.
name|s3a
operator|.
name|S3ATestConstants
operator|.
name|*
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
name|fs
operator|.
name|s3a
operator|.
name|S3ATestUtils
operator|.
name|*
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
name|fs
operator|.
name|s3a
operator|.
name|scale
operator|.
name|AbstractSTestS3AHugeFiles
operator|.
name|DEFAULT_HUGE_PARTITION_SIZE
import|;
end_import

begin_comment
comment|/**  * Test MultipartUploader with S3A.  * Although not an S3A Scale test subclass, it uses the -Dscale option  * to enable it, and partition size option to control the size of  * parts uploaded.  */
end_comment

begin_class
DECL|class|ITestS3AContractMultipartUploader
specifier|public
class|class
name|ITestS3AContractMultipartUploader
extends|extends
name|AbstractContractMultipartUploaderTest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|ITestS3AContractMultipartUploader
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|partitionSize
specifier|private
name|int
name|partitionSize
decl_stmt|;
comment|/**    * S3 requires a minimum part size of 5MB (except the last part).    * @return 5MB    */
annotation|@
name|Override
DECL|method|partSizeInBytes ()
specifier|protected
name|int
name|partSizeInBytes
parameter_list|()
block|{
return|return
name|partitionSize
return|;
block|}
annotation|@
name|Override
DECL|method|getTestPayloadCount ()
specifier|protected
name|int
name|getTestPayloadCount
parameter_list|()
block|{
return|return
literal|3
return|;
block|}
annotation|@
name|Override
DECL|method|getFileSystem ()
specifier|public
name|S3AFileSystem
name|getFileSystem
parameter_list|()
block|{
return|return
operator|(
name|S3AFileSystem
operator|)
name|super
operator|.
name|getFileSystem
argument_list|()
return|;
block|}
comment|/**    * Create a configuration, possibly patching in S3Guard options.    * @return a configuration    */
annotation|@
name|Override
DECL|method|createConfiguration ()
specifier|protected
name|Configuration
name|createConfiguration
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|super
operator|.
name|createConfiguration
argument_list|()
decl_stmt|;
name|maybeEnableS3Guard
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
name|conf
return|;
block|}
annotation|@
name|Override
DECL|method|createContract (Configuration conf)
specifier|protected
name|AbstractFSContract
name|createContract
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
return|return
operator|new
name|S3AContract
argument_list|(
name|conf
argument_list|)
return|;
block|}
comment|/**    * Bigger test: use the scale timeout.    * @return the timeout for scale tests.    */
annotation|@
name|Override
DECL|method|getTestTimeoutMillis ()
specifier|protected
name|int
name|getTestTimeoutMillis
parameter_list|()
block|{
return|return
name|SCALE_TEST_TIMEOUT_MILLIS
return|;
block|}
annotation|@
name|Override
DECL|method|supportsConcurrentUploadsToSamePath ()
specifier|protected
name|boolean
name|supportsConcurrentUploadsToSamePath
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
comment|/**    * Provide a pessimistic time to become consistent.    * @return a time in milliseconds    */
annotation|@
name|Override
DECL|method|timeToBecomeConsistentMillis ()
specifier|protected
name|int
name|timeToBecomeConsistentMillis
parameter_list|()
block|{
return|return
literal|30
operator|*
literal|1000
return|;
block|}
annotation|@
name|Override
DECL|method|finalizeConsumesUploadIdImmediately ()
specifier|protected
name|boolean
name|finalizeConsumesUploadIdImmediately
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
decl_stmt|;
name|boolean
name|enabled
init|=
name|getTestPropertyBool
argument_list|(
name|conf
argument_list|,
name|KEY_SCALE_TESTS_ENABLED
argument_list|,
name|DEFAULT_SCALE_TESTS_ENABLED
argument_list|)
decl_stmt|;
name|assume
argument_list|(
literal|"Scale test disabled: to enable set property "
operator|+
name|KEY_SCALE_TESTS_ENABLED
argument_list|,
name|enabled
argument_list|)
expr_stmt|;
name|partitionSize
operator|=
operator|(
name|int
operator|)
name|getTestPropertyBytes
argument_list|(
name|conf
argument_list|,
name|KEY_HUGE_PARTITION_SIZE
argument_list|,
name|DEFAULT_HUGE_PARTITION_SIZE
argument_list|)
expr_stmt|;
block|}
comment|/**    * Extend superclass teardown with actions to help clean up the S3 store,    * including aborting uploads under the test path.    */
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|teardown
init|=
name|path
argument_list|(
literal|"teardown"
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|WriteOperationHelper
name|helper
init|=
name|fs
operator|.
name|getWriteOperationHelper
argument_list|()
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Teardown: aborting outstanding uploads under {}"
argument_list|,
name|teardown
argument_list|)
expr_stmt|;
name|int
name|count
init|=
name|helper
operator|.
name|abortMultipartUploadsUnderPath
argument_list|(
name|fs
operator|.
name|pathToKey
argument_list|(
name|teardown
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found {} incomplete uploads"
argument_list|,
name|count
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Exeception in teardown"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
comment|/**    * S3 has no concept of directories, so this test does not apply.    */
DECL|method|testDirectoryInTheWay ()
specifier|public
name|void
name|testDirectoryInTheWay
parameter_list|()
throws|throws
name|Exception
block|{
comment|// no-op
block|}
annotation|@
name|Override
DECL|method|testMultipartUploadReverseOrder ()
specifier|public
name|void
name|testMultipartUploadReverseOrder
parameter_list|()
throws|throws
name|Exception
block|{
name|ContractTestUtils
operator|.
name|skip
argument_list|(
literal|"skipped for speed"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

