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
name|junit
operator|.
name|Ignore
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
name|FSMainOperationsBaseTest
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
name|createTestPath
import|;
end_import

begin_comment
comment|/**  * S3A Test suite for the FSMainOperationsBaseTest tests.  */
end_comment

begin_class
DECL|class|ITestS3AFSMainOperations
specifier|public
class|class
name|ITestS3AFSMainOperations
extends|extends
name|FSMainOperationsBaseTest
block|{
DECL|method|ITestS3AFSMainOperations ()
specifier|public
name|ITestS3AFSMainOperations
parameter_list|()
block|{
name|super
argument_list|(
name|createTestPath
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/ITestS3AFSMainOperations"
argument_list|)
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createFileSystem ()
specifier|protected
name|FileSystem
name|createFileSystem
parameter_list|()
throws|throws
name|Exception
block|{
name|S3AContract
name|contract
init|=
operator|new
name|S3AContract
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|contract
operator|.
name|init
argument_list|()
expr_stmt|;
return|return
name|contract
operator|.
name|getTestFileSystem
argument_list|()
return|;
block|}
annotation|@
name|Override
annotation|@
name|Ignore
argument_list|(
literal|"Permissions not supported"
argument_list|)
DECL|method|testListStatusThrowsExceptionForUnreadableDir ()
specifier|public
name|void
name|testListStatusThrowsExceptionForUnreadableDir
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
argument_list|(
literal|"Permissions not supported"
argument_list|)
DECL|method|testGlobStatusThrowsExceptionForUnreadableDir ()
specifier|public
name|void
name|testGlobStatusThrowsExceptionForUnreadableDir
parameter_list|()
block|{   }
annotation|@
name|Override
annotation|@
name|Ignore
argument_list|(
literal|"local FS path setup broken"
argument_list|)
DECL|method|testCopyToLocalWithUseRawLocalFileSystemOption ()
specifier|public
name|void
name|testCopyToLocalWithUseRawLocalFileSystemOption
parameter_list|()
throws|throws
name|Exception
block|{   }
block|}
end_class

end_unit

