begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.service.provider
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|service
operator|.
name|provider
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResource
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceType
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|LocalResourceVisibility
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
name|yarn
operator|.
name|api
operator|.
name|records
operator|.
name|URL
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
operator|.
name|ConfigFile
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
name|yarn
operator|.
name|service
operator|.
name|api
operator|.
name|records
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
name|yarn
operator|.
name|service
operator|.
name|containerlaunch
operator|.
name|AbstractLauncher
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
name|yarn
operator|.
name|service
operator|.
name|containerlaunch
operator|.
name|ContainerLaunchService
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
name|yarn
operator|.
name|service
operator|.
name|utils
operator|.
name|SliderFileSystem
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
name|org
operator|.
name|mockito
operator|.
name|Mockito
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|ArgumentMatchers
operator|.
name|any
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_comment
comment|/**  * Test functionality of ProviderUtils.  */
end_comment

begin_class
DECL|class|TestProviderUtils
specifier|public
class|class
name|TestProviderUtils
block|{
annotation|@
name|Test
DECL|method|testStaticFileLocalization ()
specifier|public
name|void
name|testStaticFileLocalization
parameter_list|()
throws|throws
name|IOException
block|{
comment|// A bunch of mocks ...
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
name|compLaunchCtx
init|=
name|mock
argument_list|(
name|ContainerLaunchService
operator|.
name|ComponentLaunchContext
operator|.
name|class
argument_list|)
decl_stmt|;
name|AbstractLauncher
name|launcher
init|=
name|mock
argument_list|(
name|AbstractLauncher
operator|.
name|class
argument_list|)
decl_stmt|;
name|SliderFileSystem
name|sfs
init|=
name|mock
argument_list|(
name|SliderFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|mock
argument_list|(
name|FileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|getFileStatus
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocationOnMock
lambda|->
operator|new
name|FileStatus
argument_list|(
literal|1L
argument_list|,
literal|false
argument_list|,
literal|1
argument_list|,
literal|1L
argument_list|,
literal|1L
argument_list|,
operator|(
name|Path
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|sfs
operator|.
name|getFileSystem
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|fs
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
name|mock
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|ConfigFile
argument_list|>
name|configFileList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|when
argument_list|(
name|conf
operator|.
name|getFiles
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|configFileList
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|compLaunchCtx
operator|.
name|getConfiguration
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|when
argument_list|(
name|sfs
operator|.
name|createAmResource
argument_list|(
name|any
argument_list|(
name|Path
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|LocalResourceType
operator|.
name|class
argument_list|)
argument_list|,
name|any
argument_list|(
name|LocalResourceVisibility
operator|.
name|class
argument_list|)
argument_list|)
argument_list|)
operator|.
name|thenAnswer
argument_list|(
name|invocationOnMock
lambda|->
operator|new
name|LocalResource
argument_list|()
block|{
block|@Override             public URL getResource(
argument_list|)
block|{
return|return
name|URL
operator|.
name|fromPath
argument_list|(
operator|(
operator|(
name|Path
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|0
index|]
operator|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setResource
parameter_list|(
name|URL
name|resource
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|long
name|getSize
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setSize
parameter_list|(
name|long
name|size
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|LocalResourceType
name|getType
parameter_list|()
block|{
return|return
operator|(
name|LocalResourceType
operator|)
name|invocationOnMock
operator|.
name|getArguments
argument_list|()
index|[
literal|1
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setType
parameter_list|(
name|LocalResourceType
name|type
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|LocalResourceVisibility
name|getVisibility
parameter_list|()
block|{
return|return
name|LocalResourceVisibility
operator|.
name|APPLICATION
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setVisibility
parameter_list|(
name|LocalResourceVisibility
name|visibility
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|String
name|getPattern
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{              }
annotation|@
name|Override
specifier|public
name|boolean
name|getShouldBeUploadedToSharedCache
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setShouldBeUploadedToSharedCache
parameter_list|(
name|boolean
name|shouldBeUploadedToSharedCache
parameter_list|)
block|{              }
block|}
block|)
class|;
end_class

begin_comment
comment|// Initialize list of files.
end_comment

begin_comment
comment|//archive
end_comment

begin_expr_stmt
name|configFileList
operator|.
name|add
argument_list|(
operator|new
name|ConfigFile
argument_list|()
operator|.
name|srcFile
argument_list|(
literal|"hdfs://default/sourceFile1"
argument_list|)
operator|.
name|destFile
argument_list|(
literal|"destFile1"
argument_list|)
operator|.
name|type
argument_list|(
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|ARCHIVE
argument_list|)
operator|.
name|visibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|//static file
end_comment

begin_expr_stmt
name|configFileList
operator|.
name|add
argument_list|(
operator|new
name|ConfigFile
argument_list|()
operator|.
name|srcFile
argument_list|(
literal|"hdfs://default/sourceFile2"
argument_list|)
operator|.
name|destFile
argument_list|(
literal|"folder/destFile_2"
argument_list|)
operator|.
name|type
argument_list|(
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|STATIC
argument_list|)
operator|.
name|visibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|//This will be ignored since type is JSON
end_comment

begin_expr_stmt
name|configFileList
operator|.
name|add
argument_list|(
operator|new
name|ConfigFile
argument_list|()
operator|.
name|srcFile
argument_list|(
literal|"hdfs://default/sourceFile3"
argument_list|)
operator|.
name|destFile
argument_list|(
literal|"destFile3"
argument_list|)
operator|.
name|type
argument_list|(
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|JSON
argument_list|)
operator|.
name|visibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_comment
comment|//No destination file specified
end_comment

begin_expr_stmt
name|configFileList
operator|.
name|add
argument_list|(
operator|new
name|ConfigFile
argument_list|()
operator|.
name|srcFile
argument_list|(
literal|"hdfs://default/sourceFile4"
argument_list|)
operator|.
name|type
argument_list|(
name|ConfigFile
operator|.
name|TypeEnum
operator|.
name|STATIC
argument_list|)
operator|.
name|visibility
argument_list|(
name|LocalResourceVisibility
operator|.
name|APPLICATION
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_decl_stmt
name|ProviderService
operator|.
name|ResolvedLaunchParams
name|resolved
init|=
operator|new
name|ProviderService
operator|.
name|ResolvedLaunchParams
argument_list|()
decl_stmt|;
end_decl_stmt

begin_expr_stmt
name|ProviderUtils
operator|.
name|handleStaticFilesForLocalization
argument_list|(
name|launcher
argument_list|,
name|sfs
argument_list|,
name|compLaunchCtx
argument_list|,
name|resolved
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|Mockito
operator|.
name|verify
argument_list|(
name|launcher
argument_list|)
operator|.
name|addLocalResource
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
literal|"destFile1"
argument_list|)
argument_list|,
name|any
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|Mockito
operator|.
name|verify
argument_list|(
name|launcher
argument_list|)
operator|.
name|addLocalResource
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
literal|"destFile_2"
argument_list|)
argument_list|,
name|any
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|Mockito
operator|.
name|verify
argument_list|(
name|launcher
argument_list|)
operator|.
name|addLocalResource
argument_list|(
name|Mockito
operator|.
name|eq
argument_list|(
literal|"sourceFile4"
argument_list|)
argument_list|,
name|any
argument_list|(
name|LocalResource
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|resolved
operator|.
name|getResolvedRsrcPaths
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
end_expr_stmt

begin_expr_stmt
name|Assert
operator|.
name|assertEquals
argument_list|(
name|resolved
operator|.
name|getResolvedRsrcPaths
argument_list|()
operator|.
name|get
argument_list|(
literal|"destFile1"
argument_list|)
argument_list|,
literal|"destFile1"
argument_list|)
expr_stmt|;
end_expr_stmt

begin_function
unit|}    @
name|Test
DECL|method|testReplaceSpacesWithDelimiter ()
specifier|public
name|void
name|testReplaceSpacesWithDelimiter
parameter_list|()
block|{
name|String
name|command
init|=
literal|"ls  -l \" space\""
decl_stmt|;
name|String
name|expected
init|=
literal|"ls,-l, space"
decl_stmt|;
name|String
name|actual
init|=
name|ProviderUtils
operator|.
name|replaceSpacesWithDelimiter
argument_list|(
name|command
argument_list|,
literal|","
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"replaceSpaceWithDelimiter produces unexpected result."
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

