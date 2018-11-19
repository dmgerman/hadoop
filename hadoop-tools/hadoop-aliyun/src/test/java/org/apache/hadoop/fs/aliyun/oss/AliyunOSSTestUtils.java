begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.aliyun.oss
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|aliyun
operator|.
name|oss
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
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
name|FileContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|internal
operator|.
name|AssumptionViolatedException
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
name|net
operator|.
name|URI
import|;
end_import

begin_comment
comment|/**  * Utility class for Aliyun OSS Tests.  */
end_comment

begin_class
DECL|class|AliyunOSSTestUtils
specifier|public
specifier|final
class|class
name|AliyunOSSTestUtils
block|{
DECL|method|AliyunOSSTestUtils ()
specifier|private
name|AliyunOSSTestUtils
parameter_list|()
block|{   }
comment|/**    * Create the test filesystem.    *    * If the test.fs.oss.name property is not set,    * tests will fail.    *    * @param conf configuration    * @return the FS    * @throws IOException    */
DECL|method|createTestFileSystem (Configuration conf)
specifier|public
specifier|static
name|AliyunOSSFileSystem
name|createTestFileSystem
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
name|AliyunOSSFileSystem
name|ossfs
init|=
operator|new
name|AliyunOSSFileSystem
argument_list|()
decl_stmt|;
name|ossfs
operator|.
name|initialize
argument_list|(
name|getURI
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
return|return
name|ossfs
return|;
block|}
DECL|method|createTestFileContext (Configuration conf)
specifier|public
specifier|static
name|FileContext
name|createTestFileContext
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|getURI
argument_list|(
name|conf
argument_list|)
argument_list|,
name|conf
argument_list|)
return|;
block|}
DECL|method|getURI (Configuration conf)
specifier|private
specifier|static
name|URI
name|getURI
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|String
name|fsname
init|=
name|conf
operator|.
name|getTrimmed
argument_list|(
name|TestAliyunOSSFileSystemContract
operator|.
name|TEST_FS_OSS_NAME
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|boolean
name|liveTest
init|=
operator|!
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|fsname
argument_list|)
decl_stmt|;
name|URI
name|testURI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|liveTest
condition|)
block|{
name|testURI
operator|=
name|URI
operator|.
name|create
argument_list|(
name|fsname
argument_list|)
expr_stmt|;
name|liveTest
operator|=
name|testURI
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
name|Constants
operator|.
name|FS_OSS
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|liveTest
condition|)
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"No test filesystem in "
operator|+
name|TestAliyunOSSFileSystemContract
operator|.
name|TEST_FS_OSS_NAME
argument_list|)
throw|;
block|}
return|return
name|testURI
return|;
block|}
comment|/**    * Generate unique test path for multiple user tests.    *    * @return root test path    */
DECL|method|generateUniqueTestPath ()
specifier|public
specifier|static
name|String
name|generateUniqueTestPath
parameter_list|()
block|{
name|String
name|testUniqueForkId
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.unique.fork.id"
argument_list|)
decl_stmt|;
return|return
name|testUniqueForkId
operator|==
literal|null
condition|?
literal|"/test"
else|:
literal|"/"
operator|+
name|testUniqueForkId
operator|+
literal|"/test"
return|;
block|}
block|}
end_class

end_unit

