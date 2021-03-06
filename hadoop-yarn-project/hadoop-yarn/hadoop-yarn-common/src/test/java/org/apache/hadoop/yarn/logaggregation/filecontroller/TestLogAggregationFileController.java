begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.logaggregation.filecontroller
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|logaggregation
operator|.
name|filecontroller
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
name|FileNotFoundException
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
name|ArgumentMatchers
operator|.
name|eq
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
name|doReturn
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
name|doThrow
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
name|verify
import|;
end_import

begin_comment
comment|/**  * Test for the abstract {@link LogAggregationFileController} class,  * checking its core functionality.  */
end_comment

begin_class
DECL|class|TestLogAggregationFileController
specifier|public
class|class
name|TestLogAggregationFileController
block|{
annotation|@
name|Test
DECL|method|testRemoteDirCreationDefault ()
specifier|public
name|void
name|testRemoteDirCreationDefault
parameter_list|()
throws|throws
name|Exception
block|{
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
name|doReturn
argument_list|(
operator|new
name|URI
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getUri
argument_list|()
expr_stmt|;
name|doThrow
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
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
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|LogAggregationFileController
name|controller
init|=
name|mock
argument_list|(
name|LogAggregationFileController
operator|.
name|class
argument_list|,
name|Mockito
operator|.
name|CALLS_REAL_METHODS
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|fs
argument_list|)
operator|.
name|when
argument_list|(
name|controller
argument_list|)
operator|.
name|getFileSystem
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"yarn_user"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"yarn_group"
block|,
literal|"other_group"
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|controller
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|"TFile"
argument_list|)
expr_stmt|;
name|controller
operator|.
name|verifyAndCreateRemoteLogDir
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setOwner
argument_list|(
name|any
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|"yarn_user"
argument_list|)
argument_list|,
name|eq
argument_list|(
literal|"yarn_group"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemoteDirCreationWithCustomGroup ()
specifier|public
name|void
name|testRemoteDirCreationWithCustomGroup
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|testGroupName
init|=
literal|"testGroup"
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
name|doReturn
argument_list|(
operator|new
name|URI
argument_list|(
literal|""
argument_list|)
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
operator|.
name|getUri
argument_list|()
expr_stmt|;
name|doThrow
argument_list|(
name|FileNotFoundException
operator|.
name|class
argument_list|)
operator|.
name|when
argument_list|(
name|fs
argument_list|)
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
expr_stmt|;
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
name|YarnConfiguration
operator|.
name|NM_REMOTE_APP_LOG_DIR_GROUPNAME
argument_list|,
name|testGroupName
argument_list|)
expr_stmt|;
name|LogAggregationFileController
name|controller
init|=
name|mock
argument_list|(
name|LogAggregationFileController
operator|.
name|class
argument_list|,
name|Mockito
operator|.
name|CALLS_REAL_METHODS
argument_list|)
decl_stmt|;
name|doReturn
argument_list|(
name|fs
argument_list|)
operator|.
name|when
argument_list|(
name|controller
argument_list|)
operator|.
name|getFileSystem
argument_list|(
name|any
argument_list|(
name|Configuration
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
name|UserGroupInformation
name|ugi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
literal|"yarn_user"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"yarn_group"
block|,
literal|"other_group"
block|}
argument_list|)
decl_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|ugi
argument_list|)
expr_stmt|;
name|controller
operator|.
name|initialize
argument_list|(
name|conf
argument_list|,
literal|"TFile"
argument_list|)
expr_stmt|;
name|controller
operator|.
name|verifyAndCreateRemoteLogDir
argument_list|()
expr_stmt|;
name|verify
argument_list|(
name|fs
argument_list|)
operator|.
name|setOwner
argument_list|(
name|any
argument_list|()
argument_list|,
name|eq
argument_list|(
literal|"yarn_user"
argument_list|)
argument_list|,
name|eq
argument_list|(
name|testGroupName
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

