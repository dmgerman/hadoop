begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|KMSClientProvider
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
name|crypto
operator|.
name|key
operator|.
name|kms
operator|.
name|server
operator|.
name|MiniKMS
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
name|Credentials
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
name|security
operator|.
name|token
operator|.
name|Token
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
name|permission
operator|.
name|FsPermission
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_class
DECL|class|TestEncryptionZonesWithKMS
specifier|public
class|class
name|TestEncryptionZonesWithKMS
extends|extends
name|TestEncryptionZones
block|{
DECL|field|miniKMS
specifier|private
name|MiniKMS
name|miniKMS
decl_stmt|;
annotation|@
name|Override
DECL|method|getKeyProviderURI ()
specifier|protected
name|String
name|getKeyProviderURI
parameter_list|()
block|{
return|return
name|KMSClientProvider
operator|.
name|SCHEME_NAME
operator|+
literal|"://"
operator|+
name|miniKMS
operator|.
name|getKMSUrl
argument_list|()
operator|.
name|toExternalForm
argument_list|()
operator|.
name|replace
argument_list|(
literal|"://"
argument_list|,
literal|"@"
argument_list|)
return|;
block|}
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|kmsDir
init|=
operator|new
name|File
argument_list|(
literal|"target/test-classes/"
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
name|kmsDir
operator|.
name|mkdirs
argument_list|()
argument_list|)
expr_stmt|;
name|MiniKMS
operator|.
name|Builder
name|miniKMSBuilder
init|=
operator|new
name|MiniKMS
operator|.
name|Builder
argument_list|()
decl_stmt|;
name|miniKMS
operator|=
name|miniKMSBuilder
operator|.
name|setKmsConfDir
argument_list|(
name|kmsDir
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|miniKMS
operator|.
name|start
argument_list|()
expr_stmt|;
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
block|{
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
name|miniKMS
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|setProvider ()
specifier|protected
name|void
name|setProvider
parameter_list|()
block|{   }
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testCreateEZPopulatesEDEKCache ()
specifier|public
name|void
name|testCreateEZPopulatesEDEKCache
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|zonePath
init|=
operator|new
name|Path
argument_list|(
literal|"/TestEncryptionZone"
argument_list|)
decl_stmt|;
name|fsWrapper
operator|.
name|mkdir
argument_list|(
name|zonePath
argument_list|,
name|FsPermission
operator|.
name|getDirDefault
argument_list|()
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|dfsAdmin
operator|.
name|createEncryptionZone
argument_list|(
name|zonePath
argument_list|,
name|TEST_KEY
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|(
operator|(
name|KMSClientProvider
operator|)
name|fs
operator|.
name|getClient
argument_list|()
operator|.
name|provider
operator|)
operator|.
name|getEncKeyQueueSize
argument_list|(
name|TEST_KEY
argument_list|)
operator|>
literal|0
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|120000
argument_list|)
DECL|method|testDelegationToken ()
specifier|public
name|void
name|testDelegationToken
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|renewer
init|=
literal|"JobTracker"
decl_stmt|;
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
name|renewer
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|?
argument_list|>
name|tokens
index|[]
init|=
name|fs
operator|.
name|addDelegationTokens
argument_list|(
name|renewer
argument_list|,
name|creds
argument_list|)
decl_stmt|;
name|DistributedFileSystem
operator|.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Delegation tokens: "
operator|+
name|Arrays
operator|.
name|asList
argument_list|(
name|tokens
argument_list|)
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|creds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
comment|// If the dt exists, will not get again
name|tokens
operator|=
name|fs
operator|.
name|addDelegationTokens
argument_list|(
name|renewer
argument_list|,
name|creds
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|tokens
operator|.
name|length
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|creds
operator|.
name|numberOfTokens
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

