begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
package|;
end_package

begin_comment
comment|/**  * Constants for S3A Testing.  */
end_comment

begin_interface
DECL|interface|S3ATestConstants
specifier|public
interface|interface
name|S3ATestConstants
block|{
comment|/**    * Prefix for any cross-filesystem scale test options.    */
DECL|field|SCALE_TEST
name|String
name|SCALE_TEST
init|=
literal|"scale.test."
decl_stmt|;
comment|/**    * Prefix for s3a-specific scale tests.    */
DECL|field|S3A_SCALE_TEST
name|String
name|S3A_SCALE_TEST
init|=
literal|"fs.s3a.scale.test."
decl_stmt|;
comment|/**    * Prefix for FS s3a tests.    */
DECL|field|TEST_FS_S3A
name|String
name|TEST_FS_S3A
init|=
literal|"test.fs.s3a."
decl_stmt|;
comment|/**    * Name of the test filesystem.    */
DECL|field|TEST_FS_S3A_NAME
name|String
name|TEST_FS_S3A_NAME
init|=
name|TEST_FS_S3A
operator|+
literal|"name"
decl_stmt|;
comment|/**    * Run the encryption tests?    */
DECL|field|KEY_ENCRYPTION_TESTS
name|String
name|KEY_ENCRYPTION_TESTS
init|=
name|TEST_FS_S3A
operator|+
literal|"encryption.enabled"
decl_stmt|;
comment|/**    * Tell tests that they are being executed in parallel: {@value}.    */
DECL|field|KEY_PARALLEL_TEST_EXECUTION
name|String
name|KEY_PARALLEL_TEST_EXECUTION
init|=
literal|"test.parallel.execution"
decl_stmt|;
comment|/**    * A property set to true in maven if scale tests are enabled: {@value}.    */
DECL|field|KEY_SCALE_TESTS_ENABLED
name|String
name|KEY_SCALE_TESTS_ENABLED
init|=
name|S3A_SCALE_TEST
operator|+
literal|"enabled"
decl_stmt|;
comment|/**    * The number of operations to perform: {@value}.    */
DECL|field|KEY_OPERATION_COUNT
name|String
name|KEY_OPERATION_COUNT
init|=
name|SCALE_TEST
operator|+
literal|"operation.count"
decl_stmt|;
comment|/**    * The number of directory operations to perform: {@value}.    */
DECL|field|KEY_DIRECTORY_COUNT
name|String
name|KEY_DIRECTORY_COUNT
init|=
name|SCALE_TEST
operator|+
literal|"directory.count"
decl_stmt|;
comment|/**    * The readahead buffer: {@value}.    */
DECL|field|KEY_READ_BUFFER_SIZE
name|String
name|KEY_READ_BUFFER_SIZE
init|=
name|S3A_SCALE_TEST
operator|+
literal|"read.buffer.size"
decl_stmt|;
DECL|field|DEFAULT_READ_BUFFER_SIZE
name|int
name|DEFAULT_READ_BUFFER_SIZE
init|=
literal|16384
decl_stmt|;
comment|/**    * Key for a multi MB test file: {@value}.    */
DECL|field|KEY_CSVTEST_FILE
name|String
name|KEY_CSVTEST_FILE
init|=
name|S3A_SCALE_TEST
operator|+
literal|"csvfile"
decl_stmt|;
comment|/**    * Default path for the multi MB test file: {@value}.    */
DECL|field|DEFAULT_CSVTEST_FILE
name|String
name|DEFAULT_CSVTEST_FILE
init|=
literal|"s3a://landsat-pds/scene_list.gz"
decl_stmt|;
comment|/**    * Name of the property to define the timeout for scale tests: {@value}.    * Measured in seconds.    */
DECL|field|KEY_TEST_TIMEOUT
name|String
name|KEY_TEST_TIMEOUT
init|=
name|S3A_SCALE_TEST
operator|+
literal|"timeout"
decl_stmt|;
comment|/**    * Name of the property to define the file size for the huge file    * tests: {@value}.    * Measured in KB; a suffix like "M", or "G" will change the unit.    */
DECL|field|KEY_HUGE_FILESIZE
name|String
name|KEY_HUGE_FILESIZE
init|=
name|S3A_SCALE_TEST
operator|+
literal|"huge.filesize"
decl_stmt|;
comment|/**    * Name of the property to define the partition size for the huge file    * tests: {@value}.    * Measured in KB; a suffix like "M", or "G" will change the unit.    */
DECL|field|KEY_HUGE_PARTITION_SIZE
name|String
name|KEY_HUGE_PARTITION_SIZE
init|=
name|S3A_SCALE_TEST
operator|+
literal|"huge.partitionsize"
decl_stmt|;
comment|/**    * The default huge size is small âfull 5GB+ scale tests are something    * to run in long test runs on EC2 VMs. {@value}.    */
DECL|field|DEFAULT_HUGE_FILESIZE
name|String
name|DEFAULT_HUGE_FILESIZE
init|=
literal|"10M"
decl_stmt|;
comment|/**    * The default number of operations to perform: {@value}.    */
DECL|field|DEFAULT_OPERATION_COUNT
name|long
name|DEFAULT_OPERATION_COUNT
init|=
literal|2005
decl_stmt|;
comment|/**    * Default number of directories to create when performing    * directory performance/scale tests.    */
DECL|field|DEFAULT_DIRECTORY_COUNT
name|int
name|DEFAULT_DIRECTORY_COUNT
init|=
literal|2
decl_stmt|;
comment|/**    * Default policy on scale tests: {@value}.    */
DECL|field|DEFAULT_SCALE_TESTS_ENABLED
name|boolean
name|DEFAULT_SCALE_TESTS_ENABLED
init|=
literal|false
decl_stmt|;
comment|/**    * Fork ID passed down from maven if the test is running in parallel.    */
DECL|field|TEST_UNIQUE_FORK_ID
name|String
name|TEST_UNIQUE_FORK_ID
init|=
literal|"test.unique.fork.id"
decl_stmt|;
DECL|field|TEST_STS_ENABLED
name|String
name|TEST_STS_ENABLED
init|=
literal|"test.fs.s3a.sts.enabled"
decl_stmt|;
DECL|field|TEST_STS_ENDPOINT
name|String
name|TEST_STS_ENDPOINT
init|=
literal|"test.fs.s3a.sts.endpoint"
decl_stmt|;
comment|/**    * Various S3Guard tests.    */
DECL|field|TEST_S3GUARD_PREFIX
name|String
name|TEST_S3GUARD_PREFIX
init|=
literal|"fs.s3a.s3guard.test"
decl_stmt|;
DECL|field|TEST_S3GUARD_ENABLED
name|String
name|TEST_S3GUARD_ENABLED
init|=
name|TEST_S3GUARD_PREFIX
operator|+
literal|".enabled"
decl_stmt|;
DECL|field|TEST_S3GUARD_AUTHORITATIVE
name|String
name|TEST_S3GUARD_AUTHORITATIVE
init|=
name|TEST_S3GUARD_PREFIX
operator|+
literal|".authoritative"
decl_stmt|;
DECL|field|TEST_S3GUARD_IMPLEMENTATION
name|String
name|TEST_S3GUARD_IMPLEMENTATION
init|=
name|TEST_S3GUARD_PREFIX
operator|+
literal|".implementation"
decl_stmt|;
DECL|field|TEST_S3GUARD_IMPLEMENTATION_LOCAL
name|String
name|TEST_S3GUARD_IMPLEMENTATION_LOCAL
init|=
literal|"local"
decl_stmt|;
DECL|field|TEST_S3GUARD_IMPLEMENTATION_DYNAMO
name|String
name|TEST_S3GUARD_IMPLEMENTATION_DYNAMO
init|=
literal|"dynamo"
decl_stmt|;
DECL|field|TEST_S3GUARD_IMPLEMENTATION_NONE
name|String
name|TEST_S3GUARD_IMPLEMENTATION_NONE
init|=
literal|"none"
decl_stmt|;
comment|/**    * Timeout in Milliseconds for standard tests: {@value}.    */
DECL|field|S3A_TEST_TIMEOUT
name|int
name|S3A_TEST_TIMEOUT
init|=
literal|10
operator|*
literal|60
operator|*
literal|1000
decl_stmt|;
comment|/**    * Timeout in Seconds for Scale Tests: {@value}.    */
DECL|field|SCALE_TEST_TIMEOUT_SECONDS
name|int
name|SCALE_TEST_TIMEOUT_SECONDS
init|=
literal|30
operator|*
literal|60
decl_stmt|;
DECL|field|SCALE_TEST_TIMEOUT_MILLIS
name|int
name|SCALE_TEST_TIMEOUT_MILLIS
init|=
name|SCALE_TEST_TIMEOUT_SECONDS
operator|*
literal|1000
decl_stmt|;
comment|/**    * Optional custom endpoint for S3A configuration tests.    * This does<i>not</i> set the endpoint for s3 access elsewhere.    */
DECL|field|CONFIGURATION_TEST_ENDPOINT
name|String
name|CONFIGURATION_TEST_ENDPOINT
init|=
literal|"test.fs.s3a.endpoint"
decl_stmt|;
comment|/**    * Property to set to disable caching.    */
DECL|field|FS_S3A_IMPL_DISABLE_CACHE
name|String
name|FS_S3A_IMPL_DISABLE_CACHE
init|=
literal|"fs.s3a.impl.disable.cache"
decl_stmt|;
block|}
end_interface

end_unit

