begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.nodemanager.containermanager.localizer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|server
operator|.
name|nodemanager
operator|.
name|containermanager
operator|.
name|localizer
package|;
end_package

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
name|conf
operator|.
name|YarnConfiguration
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
name|exceptions
operator|.
name|YarnRuntimeException
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

begin_class
DECL|class|TestLocalCacheDirectoryManager
specifier|public
class|class
name|TestLocalCacheDirectoryManager
block|{
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testHierarchicalSubDirectoryCreation ()
specifier|public
name|void
name|testHierarchicalSubDirectoryCreation
parameter_list|()
block|{
comment|// setting per directory file limit to 1.
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY
argument_list|,
literal|"37"
argument_list|)
expr_stmt|;
name|LocalCacheDirectoryManager
name|hDir
init|=
operator|new
name|LocalCacheDirectoryManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// Test root directory path = ""
name|Assert
operator|.
name|assertTrue
argument_list|(
name|hDir
operator|.
name|getRelativePathForLocalization
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
comment|// Testing path generation from "0" to "0/0/z/z"
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|37
operator|*
literal|36
operator|*
literal|36
condition|;
name|i
operator|++
control|)
block|{
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|num
init|=
name|Integer
operator|.
name|toString
argument_list|(
name|i
operator|-
literal|1
argument_list|,
literal|36
argument_list|)
decl_stmt|;
if|if
condition|(
name|num
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|num
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|num
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
argument_list|,
literal|36
argument_list|)
operator|-
literal|1
argument_list|,
literal|36
argument_list|)
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|j
init|=
literal|1
init|;
name|j
operator|<
name|num
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|Path
operator|.
name|SEPARATOR
argument_list|)
operator|.
name|append
argument_list|(
name|num
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Assert
operator|.
name|assertEquals
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|,
name|hDir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|testPath1
init|=
literal|"4"
decl_stmt|;
name|String
name|testPath2
init|=
literal|"2"
decl_stmt|;
comment|/*      * Making sure directory "4" and "2" becomes non-full so that they are      * reused for future getRelativePathForLocalization() calls in the order      * they are freed.      */
name|hDir
operator|.
name|decrementFileCountForPath
argument_list|(
name|testPath1
argument_list|)
expr_stmt|;
name|hDir
operator|.
name|decrementFileCountForPath
argument_list|(
name|testPath2
argument_list|)
expr_stmt|;
comment|// After below call directory "4" should become full.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|testPath1
argument_list|,
name|hDir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|testPath2
argument_list|,
name|hDir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testMinimumPerDirectoryFileLimit ()
specifier|public
name|void
name|testMinimumPerDirectoryFileLimit
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|Exception
name|e
init|=
literal|null
decl_stmt|;
name|ResourceLocalizationService
name|service
init|=
operator|new
name|ResourceLocalizationService
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
block|{
name|service
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e1
parameter_list|)
block|{
name|e
operator|=
name|e1
expr_stmt|;
block|}
name|Assert
operator|.
name|assertNotNull
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|YarnRuntimeException
operator|.
name|class
argument_list|,
name|e
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|YarnConfiguration
operator|.
name|NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY
operator|+
literal|" parameter is configured with a value less than 37."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|1000
argument_list|)
DECL|method|testDirectoryStateChangeFromFullToNonFull ()
specifier|public
name|void
name|testDirectoryStateChangeFromFullToNonFull
parameter_list|()
block|{
name|YarnConfiguration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|YarnConfiguration
operator|.
name|NM_LOCAL_CACHE_MAX_FILES_PER_DIRECTORY
argument_list|,
literal|"40"
argument_list|)
expr_stmt|;
name|LocalCacheDirectoryManager
name|dir
init|=
operator|new
name|LocalCacheDirectoryManager
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// checking for first four paths
name|String
name|rootPath
init|=
literal|""
decl_stmt|;
name|String
name|firstSubDir
init|=
literal|"0"
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|4
condition|;
name|i
operator|++
control|)
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
name|rootPath
argument_list|,
name|dir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Releasing two files from the root directory.
name|dir
operator|.
name|decrementFileCountForPath
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
name|dir
operator|.
name|decrementFileCountForPath
argument_list|(
name|rootPath
argument_list|)
expr_stmt|;
comment|// Space for two files should be available in root directory.
name|Assert
operator|.
name|assertEquals
argument_list|(
name|rootPath
argument_list|,
name|dir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
name|rootPath
argument_list|,
name|dir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
comment|// As no space is now available in root directory so it should be from
comment|// first sub directory
name|Assert
operator|.
name|assertEquals
argument_list|(
name|firstSubDir
argument_list|,
name|dir
operator|.
name|getRelativePathForLocalization
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

