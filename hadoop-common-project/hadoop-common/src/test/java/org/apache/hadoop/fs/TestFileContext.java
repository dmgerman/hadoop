begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|junit
operator|.
name|Test
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
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_class
DECL|class|TestFileContext
specifier|public
class|class
name|TestFileContext
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
name|TestFileContext
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Test
DECL|method|testDefaultURIWithoutScheme ()
specifier|public
name|void
name|testDefaultURIWithoutScheme
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FileSystem
operator|.
name|FS_DEFAULT_NAME_KEY
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
try|try
block|{
name|FileContext
operator|.
name|getFileContext
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|UnsupportedFileSystemException
operator|.
name|class
operator|+
literal|" not thrown!"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedFileSystemException
name|ufse
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Expected exception: "
argument_list|,
name|ufse
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

