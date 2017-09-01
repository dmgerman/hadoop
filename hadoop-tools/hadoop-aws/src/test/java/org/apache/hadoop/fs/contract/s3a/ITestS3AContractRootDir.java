begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  *  or more contributor license agreements.  See the NOTICE file  *  distributed with this work for additional information  *  regarding copyright ownership.  The ASF licenses this file  *  to you under the Apache License, Version 2.0 (the  *  "License"); you may not use this file except in compliance  *  with the License.  You may obtain a copy of the License at  *  *       http://www.apache.org/licenses/LICENSE-2.0  *  *  Unless required by applicable law or agreed to in writing, software  *  distributed under the License is distributed on an "AS IS" BASIS,  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *  See the License for the specific language governing permissions and  *  limitations under the License.  */
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
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|contract
operator|.
name|AbstractContractRootDirectoryTest
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
name|maybeEnableS3Guard
import|;
end_import

begin_comment
comment|/**  * root dir operations against an S3 bucket.  */
end_comment

begin_class
DECL|class|ITestS3AContractRootDir
specifier|public
class|class
name|ITestS3AContractRootDir
extends|extends
name|AbstractContractRootDirectoryTest
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
name|ITestS3AContractRootDir
operator|.
name|class
argument_list|)
decl_stmt|;
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
comment|// patch in S3Guard options
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
annotation|@
name|Override
DECL|method|testListEmptyRootDirectory ()
specifier|public
name|void
name|testListEmptyRootDirectory
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|attempt
init|=
literal|1
init|,
name|maxAttempts
init|=
literal|10
init|;
name|attempt
operator|<=
name|maxAttempts
condition|;
operator|++
name|attempt
control|)
block|{
try|try
block|{
name|super
operator|.
name|testListEmptyRootDirectory
argument_list|()
expr_stmt|;
break|break;
block|}
catch|catch
parameter_list|(
name|AssertionError
decl||
name|FileNotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|attempt
operator|<
name|maxAttempts
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempt {} of {} for empty root directory test failed.  "
operator|+
literal|"This is likely caused by eventual consistency of S3 "
operator|+
literal|"listings.  Attempting retry."
argument_list|,
name|attempt
argument_list|,
name|maxAttempts
argument_list|)
expr_stmt|;
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e2
parameter_list|)
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Test interrupted."
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Empty root directory test failed {} attempts.  Failing test."
argument_list|,
name|maxAttempts
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

