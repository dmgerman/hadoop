begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  *  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.adl.live
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|adl
operator|.
name|live
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
name|adl
operator|.
name|AdlFileSystem
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
name|Assume
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

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|adl
operator|.
name|AdlConfKeys
operator|.
name|ADL_HTTP_TIMEOUT
import|;
end_import

begin_comment
comment|/**  * Tests interactions with SDK and ensures configuration is having the desired  * effect.  */
end_comment

begin_class
DECL|class|TestAdlSdkConfiguration
specifier|public
class|class
name|TestAdlSdkConfiguration
block|{
annotation|@
name|Test
DECL|method|testDefaultTimeout ()
specifier|public
name|void
name|testDefaultTimeout
parameter_list|()
throws|throws
name|IOException
block|{
name|AdlFileSystem
name|fs
init|=
literal|null
decl_stmt|;
name|Configuration
name|conf
init|=
literal|null
decl_stmt|;
name|int
name|effectiveTimeout
decl_stmt|;
name|conf
operator|=
name|AdlStorageConfiguration
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ADL_HTTP_TIMEOUT
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
call|(
name|AdlFileSystem
call|)
argument_list|(
name|AdlStorageConfiguration
operator|.
name|createStorageConnector
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can not initialize ADL FileSystem. "
operator|+
literal|"Please check test.fs.adl.name property."
argument_list|,
name|e
argument_list|)
throw|;
block|}
comment|// Skip this test if we can't get a real FS
name|Assume
operator|.
name|assumeNotNull
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|effectiveTimeout
operator|=
name|fs
operator|.
name|getAdlClient
argument_list|()
operator|.
name|getDefaultTimeout
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
literal|"A negative timeout is not supposed to take effect"
argument_list|,
name|effectiveTimeout
operator|<
literal|0
argument_list|)
expr_stmt|;
name|conf
operator|=
name|AdlStorageConfiguration
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
name|conf
operator|.
name|setInt
argument_list|(
name|ADL_HTTP_TIMEOUT
argument_list|,
literal|17
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|=
call|(
name|AdlFileSystem
call|)
argument_list|(
name|AdlStorageConfiguration
operator|.
name|createStorageConnector
argument_list|(
name|conf
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Can not initialize ADL FileSystem. "
operator|+
literal|"Please check test.fs.adl.name property."
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|effectiveTimeout
operator|=
name|fs
operator|.
name|getAdlClient
argument_list|()
operator|.
name|getDefaultTimeout
argument_list|()
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Timeout is getting set"
argument_list|,
name|effectiveTimeout
argument_list|,
literal|17
argument_list|)
expr_stmt|;
comment|// The default value may vary by SDK, so that value is not tested here.
block|}
block|}
end_class

end_unit

