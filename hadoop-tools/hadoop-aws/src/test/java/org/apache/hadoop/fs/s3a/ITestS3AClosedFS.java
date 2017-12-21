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
name|Path
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
name|test
operator|.
name|LambdaTestUtils
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
name|S3AUtils
operator|.
name|E_FS_CLOSED
import|;
end_import

begin_comment
comment|/**  * Tests of the S3A FileSystem which is closed; just make sure  * that that basic file Ops fail meaningfully.  */
end_comment

begin_class
DECL|class|ITestS3AClosedFS
specifier|public
class|class
name|ITestS3AClosedFS
extends|extends
name|AbstractS3ATestBase
block|{
DECL|field|root
specifier|private
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
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
name|root
operator|=
name|getFileSystem
argument_list|()
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|getFileSystem
argument_list|()
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
comment|// no op, as the FS is closed
block|}
annotation|@
name|Test
DECL|method|testClosedGetFileStatus ()
specifier|public
name|void
name|testClosedGetFileStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|getFileStatus
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosedListStatus ()
specifier|public
name|void
name|testClosedListStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|listStatus
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosedListFile ()
specifier|public
name|void
name|testClosedListFile
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|listFiles
argument_list|(
name|root
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosedListLocatedStatus ()
specifier|public
name|void
name|testClosedListLocatedStatus
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|listLocatedStatus
argument_list|(
name|root
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosedCreate ()
specifier|public
name|void
name|testClosedCreate
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|path
argument_list|(
literal|"to-create"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosedDelete ()
specifier|public
name|void
name|testClosedDelete
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|delete
argument_list|(
name|path
argument_list|(
literal|"to-delete"
argument_list|)
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testClosedOpen ()
specifier|public
name|void
name|testClosedOpen
parameter_list|()
throws|throws
name|Exception
block|{
name|intercept
argument_list|(
name|IOException
operator|.
name|class
argument_list|,
name|E_FS_CLOSED
argument_list|,
parameter_list|()
lambda|->
name|getFileSystem
argument_list|()
operator|.
name|open
argument_list|(
name|path
argument_list|(
literal|"to-open"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

