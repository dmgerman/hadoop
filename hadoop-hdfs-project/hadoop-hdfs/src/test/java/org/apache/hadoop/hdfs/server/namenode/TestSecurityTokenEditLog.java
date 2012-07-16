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
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|Iterator
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
name|DFSConfigKeys
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
name|namenode
operator|.
name|EditLogFileInputStream
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
name|NNStorage
operator|.
name|NameNodeDirType
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

begin_comment
comment|/**  * This class tests the creation and validation of a checkpoint.  */
end_comment

begin_class
DECL|class|TestSecurityTokenEditLog
specifier|public
class|class
name|TestSecurityTokenEditLog
extends|extends
name|TestCase
block|{
DECL|field|NUM_DATA_NODES
specifier|static
specifier|final
name|int
name|NUM_DATA_NODES
init|=
literal|1
decl_stmt|;
comment|// This test creates NUM_THREADS threads and each thread does
comment|// 2 * NUM_TRANSACTIONS Transactions concurrently.
DECL|field|NUM_TRANSACTIONS
specifier|static
specifier|final
name|int
name|NUM_TRANSACTIONS
init|=
literal|100
decl_stmt|;
DECL|field|NUM_THREADS
specifier|static
specifier|final
name|int
name|NUM_THREADS
init|=
literal|100
decl_stmt|;
DECL|field|opsPerTrans
specifier|static
specifier|final
name|int
name|opsPerTrans
init|=
literal|3
decl_stmt|;
comment|//
comment|// an object that does a bunch of transactions
comment|//
DECL|class|Transactions
specifier|static
class|class
name|Transactions
implements|implements
name|Runnable
block|{
DECL|field|namesystem
name|FSNamesystem
name|namesystem
decl_stmt|;
DECL|field|numTransactions
name|int
name|numTransactions
decl_stmt|;
DECL|field|replication
name|short
name|replication
init|=
literal|3
decl_stmt|;
DECL|field|blockSize
name|long
name|blockSize
init|=
literal|64
decl_stmt|;
DECL|method|Transactions (FSNamesystem ns, int num)
name|Transactions
parameter_list|(
name|FSNamesystem
name|ns
parameter_list|,
name|int
name|num
parameter_list|)
block|{
name|namesystem
operator|=
name|ns
expr_stmt|;
name|numTransactions
operator|=
name|num
expr_stmt|;
block|}
comment|// add a bunch of transactions.
annotation|@
name|Override
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
name|FSEditLog
name|editLog
init|=
name|namesystem
operator|.
name|getEditLog
argument_list|()
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
name|numTransactions
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
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
name|token
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
name|namesystem
operator|.
name|renewDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|namesystem
operator|.
name|cancelDelegationToken
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|editLog
operator|.
name|logSync
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction "
operator|+
name|i
operator|+
literal|" encountered exception "
operator|+
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|/**    * Tests transaction logging in dfs.    */
DECL|method|testEditLog ()
specifier|public
name|void
name|testEditLog
parameter_list|()
throws|throws
name|IOException
block|{
comment|// start a cluster
name|Configuration
name|conf
init|=
operator|new
name|HdfsConfiguration
argument_list|()
decl_stmt|;
name|MiniDFSCluster
name|cluster
init|=
literal|null
decl_stmt|;
name|FileSystem
name|fileSys
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|setBoolean
argument_list|(
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_DELEGATION_TOKEN_ALWAYS_USE_KEY
argument_list|,
literal|true
argument_list|)
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
name|NUM_DATA_NODES
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
name|fileSys
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
specifier|final
name|FSNamesystem
name|namesystem
init|=
name|cluster
operator|.
name|getNamesystem
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|URI
argument_list|>
name|it
init|=
name|cluster
operator|.
name|getNameDirs
argument_list|(
literal|0
argument_list|)
operator|.
name|iterator
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|it
operator|.
name|next
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
name|FSImage
name|fsimage
init|=
name|namesystem
operator|.
name|getFSImage
argument_list|()
decl_stmt|;
name|FSEditLog
name|editLog
init|=
name|fsimage
operator|.
name|getEditLog
argument_list|()
decl_stmt|;
comment|// set small size of flush buffer
name|editLog
operator|.
name|setOutputBufferCapacity
argument_list|(
literal|2048
argument_list|)
expr_stmt|;
comment|// Create threads and make them run transactions concurrently.
name|Thread
name|threadId
index|[]
init|=
operator|new
name|Thread
index|[
name|NUM_THREADS
index|]
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
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
name|Transactions
name|trans
init|=
operator|new
name|Transactions
argument_list|(
name|namesystem
argument_list|,
name|NUM_TRANSACTIONS
argument_list|)
decl_stmt|;
name|threadId
index|[
name|i
index|]
operator|=
operator|new
name|Thread
argument_list|(
name|trans
argument_list|,
literal|"TransactionThread-"
operator|+
name|i
argument_list|)
expr_stmt|;
name|threadId
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// wait for all transactions to get over
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|NUM_THREADS
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|threadId
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
name|i
operator|--
expr_stmt|;
comment|// retry
block|}
block|}
name|editLog
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// Verify that we can read in all the transactions that we have written.
comment|// If there were any corruptions, it is likely that the reading in
comment|// of these transactions will throw an exception.
comment|//
name|namesystem
operator|.
name|getDelegationTokenSecretManager
argument_list|()
operator|.
name|stopThreads
argument_list|()
expr_stmt|;
name|int
name|numKeys
init|=
name|namesystem
operator|.
name|getDelegationTokenSecretManager
argument_list|()
operator|.
name|getNumberOfKeys
argument_list|()
decl_stmt|;
name|int
name|expectedTransactions
init|=
name|NUM_THREADS
operator|*
name|opsPerTrans
operator|*
name|NUM_TRANSACTIONS
operator|+
name|numKeys
operator|+
literal|2
decl_stmt|;
comment|// + 2 for BEGIN and END txns
for|for
control|(
name|StorageDirectory
name|sd
range|:
name|fsimage
operator|.
name|getStorage
argument_list|()
operator|.
name|dirIterable
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
control|)
block|{
name|File
name|editFile
init|=
name|NNStorage
operator|.
name|getFinalizedEditsFile
argument_list|(
name|sd
argument_list|,
literal|1
argument_list|,
literal|1
operator|+
name|expectedTransactions
operator|-
literal|1
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Verifying file: "
operator|+
name|editFile
argument_list|)
expr_stmt|;
name|FSEditLogLoader
name|loader
init|=
operator|new
name|FSEditLogLoader
argument_list|(
name|namesystem
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|long
name|numEdits
init|=
name|loader
operator|.
name|loadFSEdits
argument_list|(
operator|new
name|EditLogFileInputStream
argument_list|(
name|editFile
argument_list|)
argument_list|,
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Verification for "
operator|+
name|editFile
argument_list|,
name|expectedTransactions
argument_list|,
name|numEdits
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
name|fileSys
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

