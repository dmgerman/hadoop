begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|ozone
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
name|ozone
operator|.
name|OzoneConsts
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
name|Test
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

begin_comment
comment|/**  * Ozone file system tests that are not covered by contract tests.  */
end_comment

begin_class
DECL|class|TestOzoneFileSystem
specifier|public
class|class
name|TestOzoneFileSystem
block|{
annotation|@
name|Test
DECL|method|testOzoneFsServiceLoader ()
specifier|public
name|void
name|testOzoneFsServiceLoader
parameter_list|()
throws|throws
name|IOException
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|FileSystem
operator|.
name|getFileSystemClass
argument_list|(
name|OzoneConsts
operator|.
name|OZONE_URI_SCHEME
argument_list|,
literal|null
argument_list|)
argument_list|,
name|OzoneFileSystem
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

