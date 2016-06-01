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
comment|/**    * The number of operations to perform: {@value}.    */
DECL|field|KEY_OPERATION_COUNT
name|String
name|KEY_OPERATION_COUNT
init|=
name|SCALE_TEST
operator|+
literal|"operation.count"
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
comment|/**    * The default number of operations to perform: {@value}.    */
DECL|field|DEFAULT_OPERATION_COUNT
name|long
name|DEFAULT_OPERATION_COUNT
init|=
literal|2005
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
block|}
end_interface

end_unit

