begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
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
name|FileSystem
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
name|security
operator|.
name|UserGroupInformation
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
name|nativeio
operator|.
name|NativeIO
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assume
operator|.
name|*
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
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import

begin_class
DECL|class|TestSecureIOUtils
specifier|public
class|class
name|TestSecureIOUtils
block|{
DECL|field|realOwner
DECL|field|realGroup
specifier|private
specifier|static
name|String
name|realOwner
decl_stmt|,
name|realGroup
decl_stmt|;
DECL|field|testFilePath
specifier|private
specifier|static
specifier|final
name|File
name|testFilePath
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|)
argument_list|,
literal|"TestSecureIOContext"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|makeTestFile ()
specifier|public
specifier|static
name|void
name|makeTestFile
parameter_list|()
throws|throws
name|Exception
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|testFilePath
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|"hello"
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|FileSystem
name|rawFS
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|getRaw
argument_list|()
decl_stmt|;
name|FileStatus
name|stat
init|=
name|rawFS
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|testFilePath
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|realOwner
operator|=
name|stat
operator|.
name|getOwner
argument_list|()
expr_stmt|;
name|realGroup
operator|=
name|stat
operator|.
name|getGroup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadUnrestricted ()
specifier|public
name|void
name|testReadUnrestricted
parameter_list|()
throws|throws
name|IOException
block|{
name|SecureIOUtils
operator|.
name|openForRead
argument_list|(
name|testFilePath
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadCorrectlyRestrictedWithSecurity ()
specifier|public
name|void
name|testReadCorrectlyRestrictedWithSecurity
parameter_list|()
throws|throws
name|IOException
block|{
name|SecureIOUtils
operator|.
name|openForRead
argument_list|(
name|testFilePath
argument_list|,
name|realOwner
argument_list|,
name|realGroup
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadIncorrectlyRestrictedWithSecurity ()
specifier|public
name|void
name|testReadIncorrectlyRestrictedWithSecurity
parameter_list|()
throws|throws
name|IOException
block|{
comment|// this will only run if libs are available
name|assumeTrue
argument_list|(
name|NativeIO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Running test with native libs..."
argument_list|)
expr_stmt|;
try|try
block|{
name|SecureIOUtils
operator|.
name|forceSecureOpenForRead
argument_list|(
name|testFilePath
argument_list|,
literal|"invalidUser"
argument_list|,
literal|null
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Didn't throw expection for wrong ownership!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testCreateForWrite ()
specifier|public
name|void
name|testCreateForWrite
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|SecureIOUtils
operator|.
name|createForWrite
argument_list|(
name|testFilePath
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Was able to create file at "
operator|+
name|testFilePath
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SecureIOUtils
operator|.
name|AlreadyExistsException
name|aee
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

