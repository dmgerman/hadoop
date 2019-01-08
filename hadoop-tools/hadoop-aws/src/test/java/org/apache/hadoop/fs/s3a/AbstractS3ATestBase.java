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
name|AbstractFSContractTestBase
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
name|contract
operator|.
name|s3a
operator|.
name|S3AContract
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
name|io
operator|.
name|IOUtils
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
name|io
operator|.
name|IOException
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|dataset
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
name|contract
operator|.
name|ContractTestUtils
operator|.
name|writeDataset
import|;
end_import

begin_comment
comment|/**  * An extension of the contract test base set up for S3A tests.  */
end_comment

begin_class
DECL|class|AbstractS3ATestBase
specifier|public
specifier|abstract
class|class
name|AbstractS3ATestBase
extends|extends
name|AbstractFSContractTestBase
implements|implements
name|S3ATestConstants
block|{
DECL|field|LOG
specifier|protected
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractS3ATestBase
operator|.
name|class
argument_list|)
decl_stmt|;
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
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
name|describe
argument_list|(
literal|"closing file system"
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeStream
argument_list|(
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|nameThread ()
specifier|public
name|void
name|nameThread
parameter_list|()
block|{
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|setName
argument_list|(
literal|"JUnit-"
operator|+
name|getMethodName
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|getMethodName ()
specifier|protected
name|String
name|getMethodName
parameter_list|()
block|{
return|return
name|methodName
operator|.
name|getMethodName
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|getTestTimeoutMillis ()
specifier|protected
name|int
name|getTestTimeoutMillis
parameter_list|()
block|{
return|return
name|S3A_TEST_TIMEOUT
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
return|return
name|S3ATestUtils
operator|.
name|prepareTestConfiguration
argument_list|(
name|super
operator|.
name|createConfiguration
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getConfiguration ()
specifier|protected
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|getContract
argument_list|()
operator|.
name|getConf
argument_list|()
return|;
block|}
comment|/**    * Get the filesystem as an S3A filesystem.    * @return the typecast FS    */
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
comment|/**    * Describe a test in the logs.    * @param text text to print    * @param args arguments to format in the printing    */
DECL|method|describe (String text, Object... args)
specifier|protected
name|void
name|describe
parameter_list|(
name|String
name|text
parameter_list|,
name|Object
modifier|...
name|args
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"\n\n{}: {}\n"
argument_list|,
name|getMethodName
argument_list|()
argument_list|,
name|String
operator|.
name|format
argument_list|(
name|text
argument_list|,
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write a file, read it back, validate the dataset. Overwrites the file    * if it is present    * @param name filename (will have the test path prepended to it)    * @param len length of file    * @return the full path to the file    * @throws IOException any IO problem    */
DECL|method|writeThenReadFile (String name, int len)
specifier|protected
name|Path
name|writeThenReadFile
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
name|path
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|writeThenReadFile
argument_list|(
name|path
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
comment|/**    * Write a file, read it back, validate the dataset. Overwrites the file    * if it is present    * @param path path to file    * @param len length of file    * @throws IOException any IO problem    */
DECL|method|writeThenReadFile (Path path, int len)
specifier|protected
name|void
name|writeThenReadFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|byte
index|[]
name|data
init|=
name|dataset
argument_list|(
name|len
argument_list|,
literal|'a'
argument_list|,
literal|'z'
argument_list|)
decl_stmt|;
name|writeDataset
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
name|data
argument_list|,
name|data
operator|.
name|length
argument_list|,
literal|1024
operator|*
literal|1024
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|ContractTestUtils
operator|.
name|verifyFileContents
argument_list|(
name|getFileSystem
argument_list|()
argument_list|,
name|path
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|/**    * Assert that an exception failed with a specific status code.    * @param e exception    * @param code expected status code    * @throws AWSS3IOException rethrown if the status code does not match.    */
DECL|method|assertStatusCode (AWSS3IOException e, int code)
specifier|protected
name|void
name|assertStatusCode
parameter_list|(
name|AWSS3IOException
name|e
parameter_list|,
name|int
name|code
parameter_list|)
throws|throws
name|AWSS3IOException
block|{
if|if
condition|(
name|e
operator|.
name|getStatusCode
argument_list|()
operator|!=
name|code
condition|)
block|{
throw|throw
name|e
throw|;
block|}
block|}
block|}
end_class

end_unit

