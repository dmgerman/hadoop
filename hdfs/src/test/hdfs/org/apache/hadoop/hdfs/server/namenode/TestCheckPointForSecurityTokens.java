begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.namenode
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|server
operator|.
name|namenode
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
name|*
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
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
name|hdfs
operator|.
name|DistributedFileSystem
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
name|HdfsConfiguration
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
name|MiniDFSCluster
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
name|protocol
operator|.
name|FSConstants
operator|.
name|SafeModeAction
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
name|security
operator|.
name|token
operator|.
name|delegation
operator|.
name|DelegationTokenIdentifier
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
name|server
operator|.
name|common
operator|.
name|Storage
operator|.
name|StorageDirectory
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
name|server
operator|.
name|namenode
operator|.
name|FileJournalManager
operator|.
name|EditLogFile
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
name|tools
operator|.
name|DFSAdmin
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
name|io
operator|.
name|Text
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests the creation and validation of a checkpoint.  */
end_comment

begin_class
DECL|class|TestCheckPointForSecurityTokens
specifier|public
class|class
name|TestCheckPointForSecurityTokens
block|{
DECL|field|seed
specifier|static
specifier|final
name|long
name|seed
init|=
literal|0xDEADBEEFL
decl_stmt|;
DECL|field|blockSize
specifier|static
specifier|final
name|int
name|blockSize
init|=
literal|4096
decl_stmt|;
DECL|field|fileSize
specifier|static
specifier|final
name|int
name|fileSize
init|=
literal|8192
decl_stmt|;
DECL|field|numDatanodes
specifier|static
specifier|final
name|int
name|numDatanodes
init|=
literal|3
decl_stmt|;
DECL|field|replication
name|short
name|replication
init|=
literal|3
decl_stmt|;
DECL|field|cluster
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
DECL|method|cancelToken (Token<DelegationTokenIdentifier> token)
specifier|private
name|void
name|cancelToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
DECL|method|renewToken (Token<DelegationTokenIdentifier> token)
specifier|private
name|void
name|renewToken
parameter_list|(
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token
parameter_list|)
throws|throws
name|IOException
block|{
name|cluster
operator|.
name|getNamesystem
argument_list|()
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests save namepsace.    */
annotation|@
name|Test
DECL|method|testSaveNamespace ()
specifier|public
name|void
name|testSaveNamespace
parameter_list|()
throws|throws
name|IOException
block|{
name|DistributedFileSystem
name|fs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
call|(
name|DistributedFileSystem
call|)
argument_list|(
name|cluster
operator|.
name|getFileSystem
argument_list|()
argument_list|)
expr_stmt|;
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
name|namesystem
operator|.
name|getDelegationTokenSecretManager
argument_list|()
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|String
name|renewer
init|=
name|UserGroupInformation
operator|.
name|getLoginUser
argument_list|()
operator|.
name|getUserName
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token1
init|=
name|namesystem
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token2
init|=
name|namesystem
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
comment|// Saving image without safe mode should fail
name|DFSAdmin
name|admin
init|=
operator|new
name|DFSAdmin
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|String
index|[]
name|args
init|=
operator|new
name|String
index|[]
block|{
literal|"-saveNamespace"
block|}
decl_stmt|;
comment|// verify that the edits file is NOT empty
name|NameNode
name|nn
init|=
name|cluster
operator|.
name|getNameNode
argument_list|()
decl_stmt|;
for|for
control|(
name|StorageDirectory
name|sd
range|:
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|dirIterable
argument_list|(
literal|null
argument_list|)
control|)
block|{
name|EditLogFile
name|log
init|=
name|FSImageTestUtil
operator|.
name|findLatestEditsLog
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|log
operator|.
name|isInProgress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"In-progress log "
operator|+
name|log
operator|+
literal|" should have 5 transactions"
argument_list|,
literal|5
argument_list|,
name|log
operator|.
name|validateLog
argument_list|()
operator|.
name|numTransactions
argument_list|)
expr_stmt|;
block|}
comment|// Saving image in safe mode should succeed
name|fs
operator|.
name|setSafeMode
argument_list|(
name|SafeModeAction
operator|.
name|SAFEMODE_ENTER
argument_list|)
expr_stmt|;
try|try
block|{
name|admin
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|// verify that the edits file is empty except for the START txn
for|for
control|(
name|StorageDirectory
name|sd
range|:
name|nn
operator|.
name|getFSImage
argument_list|()
operator|.
name|getStorage
argument_list|()
operator|.
name|dirIterable
argument_list|(
literal|null
argument_list|)
control|)
block|{
name|EditLogFile
name|log
init|=
name|FSImageTestUtil
operator|.
name|findLatestEditsLog
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|log
operator|.
name|isInProgress
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"In-progress log "
operator|+
name|log
operator|+
literal|" should only have START txn"
argument_list|,
literal|1
argument_list|,
name|log
operator|.
name|validateLog
argument_list|()
operator|.
name|numTransactions
argument_list|)
expr_stmt|;
block|}
comment|// restart cluster
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
comment|//Should be able to renew& cancel the delegation token after cluster restart
try|try
block|{
name|renewToken
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token2
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Could not renew or cancel the token"
argument_list|)
expr_stmt|;
block|}
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|namesystem
operator|.
name|getDelegationTokenSecretManager
argument_list|()
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token3
init|=
name|namesystem
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token4
init|=
name|namesystem
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
comment|// restart cluster again
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|namesystem
operator|.
name|getDelegationTokenSecretManager
argument_list|()
operator|.
name|startThreads
argument_list|()
expr_stmt|;
name|Token
argument_list|<
name|DelegationTokenIdentifier
argument_list|>
name|token5
init|=
name|namesystem
operator|.
name|getDelegationToken
argument_list|(
operator|new
name|Text
argument_list|(
name|renewer
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|renewToken
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token3
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token4
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Could not renew or cancel the token"
argument_list|)
expr_stmt|;
block|}
comment|// restart cluster again
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|format
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|namesystem
operator|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
expr_stmt|;
name|namesystem
operator|.
name|getDelegationTokenSecretManager
argument_list|()
operator|.
name|startThreads
argument_list|()
expr_stmt|;
try|try
block|{
name|renewToken
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|cancelToken
argument_list|(
name|token1
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|cancelToken
argument_list|(
name|token2
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token3
argument_list|)
expr_stmt|;
name|cancelToken
argument_list|(
name|token3
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token4
argument_list|)
expr_stmt|;
name|cancelToken
argument_list|(
name|token4
argument_list|)
expr_stmt|;
name|renewToken
argument_list|(
name|token5
argument_list|)
expr_stmt|;
name|cancelToken
argument_list|(
name|token5
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|Assert
operator|.
name|fail
argument_list|(
literal|"Could not renew or cancel the token"
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

