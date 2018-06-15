begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.azurebfs.services
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|azurebfs
operator|.
name|services
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
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
name|Ignore
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
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|FSDataInputStream
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
name|FSDataOutputStream
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
name|FileStatus
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
name|azurebfs
operator|.
name|AzureBlobFileSystem
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
name|azurebfs
operator|.
name|DependencyInjectedTest
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
name|azurebfs
operator|.
name|contracts
operator|.
name|services
operator|.
name|AbfsHttpService
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
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Test AbfsHttpServiceImpl.  */
end_comment

begin_class
DECL|class|ITestAbfsHttpServiceImpl
specifier|public
class|class
name|ITestAbfsHttpServiceImpl
extends|extends
name|DependencyInjectedTest
block|{
DECL|field|TEST_DATA
specifier|private
specifier|static
specifier|final
name|int
name|TEST_DATA
init|=
literal|100
decl_stmt|;
DECL|field|TEST_PATH
specifier|private
specifier|static
specifier|final
name|Path
name|TEST_PATH
init|=
operator|new
name|Path
argument_list|(
literal|"/testfile"
argument_list|)
decl_stmt|;
DECL|method|ITestAbfsHttpServiceImpl ()
specifier|public
name|ITestAbfsHttpServiceImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWriteBytesToFileAndEnsureThreadPoolCleanup ()
specifier|public
name|void
name|testReadWriteBytesToFileAndEnsureThreadPoolCleanup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|testWriteOneByteToFileAndEnsureThreadPoolCleanup
argument_list|()
expr_stmt|;
name|FSDataInputStream
name|inputStream
init|=
name|fs
operator|.
name|open
argument_list|(
name|TEST_PATH
argument_list|,
literal|4
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
decl_stmt|;
name|int
name|i
init|=
name|inputStream
operator|.
name|read
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_DATA
argument_list|,
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWriteOneByteToFileAndEnsureThreadPoolCleanup ()
specifier|public
name|void
name|testWriteOneByteToFileAndEnsureThreadPoolCleanup
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|FSDataOutputStream
name|stream
init|=
name|fs
operator|.
name|create
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|TEST_DATA
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|TEST_PATH
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|fileStatus
operator|.
name|getLen
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"JDK7 doesn't support PATCH, so PUT is used. Fix is applied in latest test tenant"
argument_list|)
DECL|method|testBase64FileSystemProperties ()
specifier|public
name|void
name|testBase64FileSystemProperties
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"{ value: value }"
argument_list|)
expr_stmt|;
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|setFilesystemProperties
argument_list|(
name|fs
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fetchedProperties
init|=
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|getFilesystemProperties
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|properties
argument_list|,
name|fetchedProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testBase64PathProperties ()
specifier|public
name|void
name|testBase64PathProperties
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"{ value: valueTest }"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|setPathProperties
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fetchedProperties
init|=
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|getPathProperties
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|properties
argument_list|,
name|fetchedProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|Exception
operator|.
name|class
argument_list|)
DECL|method|testBase64InvalidFileSystemProperties ()
specifier|public
name|void
name|testBase64InvalidFileSystemProperties
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"{ value: valueæ­² }"
argument_list|)
expr_stmt|;
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|setFilesystemProperties
argument_list|(
name|fs
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fetchedProperties
init|=
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|getFilesystemProperties
argument_list|(
name|fs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|properties
argument_list|,
name|fetchedProperties
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|Exception
operator|.
name|class
argument_list|)
DECL|method|testBase64InvalidPathProperties ()
specifier|public
name|void
name|testBase64InvalidPathProperties
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|AzureBlobFileSystem
name|fs
init|=
name|this
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
specifier|final
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|properties
init|=
operator|new
name|Hashtable
argument_list|<>
argument_list|()
decl_stmt|;
name|properties
operator|.
name|put
argument_list|(
literal|"key"
argument_list|,
literal|"{ value: valueTestå© }"
argument_list|)
expr_stmt|;
name|fs
operator|.
name|create
argument_list|(
name|TEST_PATH
argument_list|)
expr_stmt|;
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|setPathProperties
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|,
name|properties
argument_list|)
expr_stmt|;
name|Hashtable
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|fetchedProperties
init|=
name|AbfsServiceProviderImpl
operator|.
name|instance
argument_list|()
operator|.
name|get
argument_list|(
name|AbfsHttpService
operator|.
name|class
argument_list|)
operator|.
name|getPathProperties
argument_list|(
name|fs
argument_list|,
name|TEST_PATH
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|properties
argument_list|,
name|fetchedProperties
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

