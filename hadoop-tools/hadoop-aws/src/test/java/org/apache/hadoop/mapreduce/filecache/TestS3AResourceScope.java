begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.filecache
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|filecache
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
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|s3a
operator|.
name|S3AFileStatus
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
name|test
operator|.
name|HadoopTestBase
import|;
end_import

begin_comment
comment|/**  * Test how S3A resources are scoped in YARN caching.  * In this package to make use of package-private methods of  * {@link ClientDistributedCacheManager}.  */
end_comment

begin_class
DECL|class|TestS3AResourceScope
specifier|public
class|class
name|TestS3AResourceScope
extends|extends
name|HadoopTestBase
block|{
DECL|field|PATH
specifier|private
specifier|static
specifier|final
name|Path
name|PATH
init|=
operator|new
name|Path
argument_list|(
literal|"s3a://example/path"
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testS3AFilesArePrivate ()
specifier|public
name|void
name|testS3AFilesArePrivate
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AFileStatus
name|status
init|=
operator|new
name|S3AFileStatus
argument_list|(
literal|false
argument_list|,
name|PATH
argument_list|,
literal|"self"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not encrypted: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotExecutable
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testS3AFilesArePrivateOtherContstructor ()
specifier|public
name|void
name|testS3AFilesArePrivateOtherContstructor
parameter_list|()
throws|throws
name|Throwable
block|{
name|S3AFileStatus
name|status
init|=
operator|new
name|S3AFileStatus
argument_list|(
literal|0
argument_list|,
literal|0
argument_list|,
name|PATH
argument_list|,
literal|1
argument_list|,
literal|"self"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Not encrypted: "
operator|+
name|status
argument_list|,
name|status
operator|.
name|isEncrypted
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotExecutable
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
DECL|method|assertNotExecutable (final S3AFileStatus status)
specifier|private
name|void
name|assertNotExecutable
parameter_list|(
specifier|final
name|S3AFileStatus
name|status
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|URI
argument_list|,
name|FileStatus
argument_list|>
name|cache
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|cache
operator|.
name|put
argument_list|(
name|PATH
operator|.
name|toUri
argument_list|()
argument_list|,
name|status
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Should not have been executable "
operator|+
name|status
argument_list|,
name|ClientDistributedCacheManager
operator|.
name|ancestorsHaveExecutePermissions
argument_list|(
literal|null
argument_list|,
name|PATH
argument_list|,
name|cache
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

