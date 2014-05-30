begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.nfs.nfs3
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|nfs
operator|.
name|nfs3
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|KERBEROS
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|core
operator|.
name|Is
operator|.
name|is
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
name|assertEquals
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
name|assertFalse
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
name|assertThat
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
name|assertTrue
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
name|hdfs
operator|.
name|DFSClient
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
name|hdfs
operator|.
name|nfs
operator|.
name|conf
operator|.
name|NfsConfiguration
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
name|junit
operator|.
name|Test
import|;
end_import

begin_class
DECL|class|TestDFSClientCache
specifier|public
class|class
name|TestDFSClientCache
block|{
annotation|@
name|Test
DECL|method|testEviction ()
specifier|public
name|void
name|testEviction
parameter_list|()
throws|throws
name|IOException
block|{
name|NfsConfiguration
name|conf
init|=
operator|new
name|NfsConfiguration
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
literal|"hdfs://localhost"
argument_list|)
expr_stmt|;
comment|// Only one entry will be in the cache
specifier|final
name|int
name|MAX_CACHE_SIZE
init|=
literal|2
decl_stmt|;
name|DFSClientCache
name|cache
init|=
operator|new
name|DFSClientCache
argument_list|(
name|conf
argument_list|,
name|MAX_CACHE_SIZE
argument_list|)
decl_stmt|;
name|DFSClient
name|c1
init|=
name|cache
operator|.
name|getDfsClient
argument_list|(
literal|"test1"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|cache
operator|.
name|getDfsClient
argument_list|(
literal|"test1"
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"ugi=test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|c1
argument_list|,
name|cache
operator|.
name|getDfsClient
argument_list|(
literal|"test1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|isDfsClientClose
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|cache
operator|.
name|getDfsClient
argument_list|(
literal|"test2"
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|isDfsClientClose
argument_list|(
name|c1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|MAX_CACHE_SIZE
operator|-
literal|1
argument_list|,
name|cache
operator|.
name|clientCache
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetUserGroupInformationSecure ()
specifier|public
name|void
name|testGetUserGroupInformationSecure
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|userName
init|=
literal|"user1"
decl_stmt|;
name|String
name|currentUser
init|=
literal|"test-user"
decl_stmt|;
name|NfsConfiguration
name|conf
init|=
operator|new
name|NfsConfiguration
argument_list|()
decl_stmt|;
name|UserGroupInformation
name|currentUserUgi
init|=
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|currentUser
argument_list|)
decl_stmt|;
name|currentUserUgi
operator|.
name|setAuthenticationMethod
argument_list|(
name|KERBEROS
argument_list|)
expr_stmt|;
name|UserGroupInformation
operator|.
name|setLoginUser
argument_list|(
name|currentUserUgi
argument_list|)
expr_stmt|;
name|DFSClientCache
name|cache
init|=
operator|new
name|DFSClientCache
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugiResult
init|=
name|cache
operator|.
name|getUserGroupInformation
argument_list|(
name|userName
argument_list|,
name|currentUserUgi
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ugiResult
operator|.
name|getUserName
argument_list|()
argument_list|,
name|is
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ugiResult
operator|.
name|getRealUser
argument_list|()
argument_list|,
name|is
argument_list|(
name|currentUserUgi
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ugiResult
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|,
name|is
argument_list|(
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|PROXY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetUserGroupInformation ()
specifier|public
name|void
name|testGetUserGroupInformation
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|userName
init|=
literal|"user1"
decl_stmt|;
name|String
name|currentUser
init|=
literal|"currentUser"
decl_stmt|;
name|UserGroupInformation
name|currentUserUgi
init|=
name|UserGroupInformation
operator|.
name|createUserForTesting
argument_list|(
name|currentUser
argument_list|,
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|NfsConfiguration
name|conf
init|=
operator|new
name|NfsConfiguration
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
literal|"hdfs://localhost"
argument_list|)
expr_stmt|;
name|DFSClientCache
name|cache
init|=
operator|new
name|DFSClientCache
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|UserGroupInformation
name|ugiResult
init|=
name|cache
operator|.
name|getUserGroupInformation
argument_list|(
name|userName
argument_list|,
name|currentUserUgi
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|ugiResult
operator|.
name|getUserName
argument_list|()
argument_list|,
name|is
argument_list|(
name|userName
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ugiResult
operator|.
name|getRealUser
argument_list|()
argument_list|,
name|is
argument_list|(
name|currentUserUgi
argument_list|)
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
name|ugiResult
operator|.
name|getAuthenticationMethod
argument_list|()
argument_list|,
name|is
argument_list|(
name|UserGroupInformation
operator|.
name|AuthenticationMethod
operator|.
name|PROXY
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|isDfsClientClose (DFSClient c)
specifier|private
specifier|static
name|boolean
name|isDfsClientClose
parameter_list|(
name|DFSClient
name|c
parameter_list|)
block|{
try|try
block|{
name|c
operator|.
name|exists
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
return|return
name|e
operator|.
name|getMessage
argument_list|()
operator|.
name|equals
argument_list|(
literal|"Filesystem closed"
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

