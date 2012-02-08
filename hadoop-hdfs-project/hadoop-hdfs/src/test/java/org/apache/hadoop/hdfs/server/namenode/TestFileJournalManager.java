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
name|Collections
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
name|java
operator|.
name|io
operator|.
name|RandomAccessFile
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
name|io
operator|.
name|FilenameFilter
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
name|junit
operator|.
name|Test
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
name|JournalManager
operator|.
name|CorruptionException
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
name|test
operator|.
name|GenericTestUtils
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|TestEditLog
operator|.
name|setupEdits
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|TestEditLog
operator|.
name|AbortSpec
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|TestEditLog
operator|.
name|TXNS_PER_ROLL
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|TestEditLog
operator|.
name|TXNS_PER_FAIL
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableList
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Joiner
import|;
end_import

begin_class
DECL|class|TestFileJournalManager
specifier|public
class|class
name|TestFileJournalManager
block|{
comment|/**     * Test the normal operation of loading transactions from    * file journal manager. 3 edits directories are setup without any    * failures. Test that we read in the expected number of transactions.    */
annotation|@
name|Test
DECL|method|testNormalOperation ()
specifier|public
name|void
name|testNormalOperation
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f1
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/normtest0"
argument_list|)
decl_stmt|;
name|File
name|f2
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/normtest1"
argument_list|)
decl_stmt|;
name|File
name|f3
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/normtest2"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URI
argument_list|>
name|editUris
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|f1
operator|.
name|toURI
argument_list|()
argument_list|,
name|f2
operator|.
name|toURI
argument_list|()
argument_list|,
name|f3
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|editUris
argument_list|,
literal|5
argument_list|)
decl_stmt|;
name|long
name|numJournals
init|=
literal|0
decl_stmt|;
for|for
control|(
name|StorageDirectory
name|sd
range|:
name|storage
operator|.
name|dirIterable
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
control|)
block|{
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
operator|*
name|TXNS_PER_ROLL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|numJournals
operator|++
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|numJournals
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test that inprogress files are handled correct. Set up a single    * edits directory. Fail on after the last roll. Then verify that the     * logs have the expected number of transactions.    */
annotation|@
name|Test
DECL|method|testInprogressRecovery ()
specifier|public
name|void
name|testInprogressRecovery
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest0"
argument_list|)
decl_stmt|;
comment|// abort after the 5th roll
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|5
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|*
name|TXNS_PER_ROLL
operator|+
name|TXNS_PER_FAIL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test a mixture of inprogress files and finalised. Set up 3 edits     * directories and fail the second on the last roll. Verify that reading    * the transactions, reads from the finalised directories.    */
annotation|@
name|Test
DECL|method|testInprogressRecoveryMixed ()
specifier|public
name|void
name|testInprogressRecoveryMixed
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f1
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/mixtest0"
argument_list|)
decl_stmt|;
name|File
name|f2
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/mixtest1"
argument_list|)
decl_stmt|;
name|File
name|f3
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/mixtest2"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URI
argument_list|>
name|editUris
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|f1
operator|.
name|toURI
argument_list|()
argument_list|,
name|f2
operator|.
name|toURI
argument_list|()
argument_list|,
name|f3
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
comment|// abort after the 5th roll
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|editUris
argument_list|,
literal|5
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|5
argument_list|,
literal|1
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|dirs
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|dirs
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
operator|*
name|TXNS_PER_ROLL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sd
operator|=
name|dirs
operator|.
name|next
argument_list|()
expr_stmt|;
name|jm
operator|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|*
name|TXNS_PER_ROLL
operator|+
name|TXNS_PER_FAIL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sd
operator|=
name|dirs
operator|.
name|next
argument_list|()
expr_stmt|;
name|jm
operator|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
operator|*
name|TXNS_PER_ROLL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test that FileJournalManager behaves correctly despite inprogress    * files in all its edit log directories. Set up 3 directories and fail    * all on the last roll. Verify that the correct number of transaction     * are then loaded.    */
annotation|@
name|Test
DECL|method|testInprogressRecoveryAll ()
specifier|public
name|void
name|testInprogressRecoveryAll
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f1
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/failalltest0"
argument_list|)
decl_stmt|;
name|File
name|f2
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/failalltest1"
argument_list|)
decl_stmt|;
name|File
name|f3
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/failalltest2"
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|URI
argument_list|>
name|editUris
init|=
name|ImmutableList
operator|.
name|of
argument_list|(
name|f1
operator|.
name|toURI
argument_list|()
argument_list|,
name|f2
operator|.
name|toURI
argument_list|()
argument_list|,
name|f3
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
comment|// abort after the 5th roll
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|editUris
argument_list|,
literal|5
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|5
argument_list|,
literal|0
argument_list|)
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|5
argument_list|,
literal|1
argument_list|)
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|5
argument_list|,
literal|2
argument_list|)
argument_list|)
decl_stmt|;
name|Iterator
argument_list|<
name|StorageDirectory
argument_list|>
name|dirs
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|dirs
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|*
name|TXNS_PER_ROLL
operator|+
name|TXNS_PER_FAIL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sd
operator|=
name|dirs
operator|.
name|next
argument_list|()
expr_stmt|;
name|jm
operator|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|*
name|TXNS_PER_ROLL
operator|+
name|TXNS_PER_FAIL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|sd
operator|=
name|dirs
operator|.
name|next
argument_list|()
expr_stmt|;
name|jm
operator|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
operator|*
name|TXNS_PER_ROLL
operator|+
name|TXNS_PER_FAIL
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Corrupt an edit log file after the start segment transaction    */
DECL|method|corruptAfterStartSegment (File f)
specifier|private
name|void
name|corruptAfterStartSegment
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
name|RandomAccessFile
name|raf
init|=
operator|new
name|RandomAccessFile
argument_list|(
name|f
argument_list|,
literal|"rw"
argument_list|)
decl_stmt|;
name|raf
operator|.
name|seek
argument_list|(
literal|0x16
argument_list|)
expr_stmt|;
comment|// skip version and first tranaction and a bit of next transaction
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|raf
operator|.
name|writeInt
argument_list|(
literal|0xdeadbeef
argument_list|)
expr_stmt|;
block|}
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**     * Test that we can read from a stream created by FileJournalManager.    * Create a single edits directory, failing it on the final roll.    * Then try loading from the point of the 3rd roll. Verify that we read     * the correct number of transactions from this point.    */
annotation|@
name|Test
DECL|method|testReadFromStream ()
specifier|public
name|void
name|testReadFromStream
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest1"
argument_list|)
decl_stmt|;
comment|// abort after 10th roll
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|10
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|long
name|expectedTotalTxnCount
init|=
name|TXNS_PER_ROLL
operator|*
literal|10
operator|+
name|TXNS_PER_FAIL
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedTotalTxnCount
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|long
name|skippedTxns
init|=
operator|(
literal|3
operator|*
name|TXNS_PER_ROLL
operator|)
decl_stmt|;
comment|// skip first 3 files
name|long
name|startingTxId
init|=
name|skippedTxns
operator|+
literal|1
decl_stmt|;
name|long
name|numTransactionsToLoad
init|=
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
name|startingTxId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|long
name|numLoaded
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|numLoaded
operator|<
name|numTransactionsToLoad
condition|)
block|{
name|EditLogInputStream
name|editIn
init|=
name|jm
operator|.
name|getInputStream
argument_list|(
name|startingTxId
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSEditLogLoader
operator|.
name|EditLogValidation
name|val
init|=
name|FSEditLogLoader
operator|.
name|validateEditLog
argument_list|(
name|editIn
argument_list|)
decl_stmt|;
name|long
name|count
init|=
name|val
operator|.
name|getNumTransactions
argument_list|()
decl_stmt|;
name|editIn
operator|.
name|close
argument_list|()
expr_stmt|;
name|startingTxId
operator|+=
name|count
expr_stmt|;
name|numLoaded
operator|+=
name|count
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|expectedTotalTxnCount
operator|-
name|skippedTxns
argument_list|,
name|numLoaded
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make requests with starting transaction ids which don't match the beginning    * txid of some log segments.    *     * This should succeed.    */
annotation|@
name|Test
DECL|method|testAskForTransactionsMidfile ()
specifier|public
name|void
name|testAskForTransactionsMidfile
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest2"
argument_list|)
decl_stmt|;
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
comment|// 10 rolls, so 11 rolled files, 110 txids total.
specifier|final
name|int
name|TOTAL_TXIDS
init|=
literal|10
operator|*
literal|11
decl_stmt|;
for|for
control|(
name|int
name|txid
init|=
literal|1
init|;
name|txid
operator|<=
name|TOTAL_TXIDS
condition|;
name|txid
operator|++
control|)
block|{
name|assertEquals
argument_list|(
operator|(
name|TOTAL_TXIDS
operator|-
name|txid
operator|)
operator|+
literal|1
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
name|txid
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**     * Test that we receive the correct number of transactions when we count    * the number of transactions around gaps.    * Set up a single edits directory, with no failures. Delete the 4th logfile.    * Test that getNumberOfTransactions returns the correct number of     * transactions before this gap and after this gap. Also verify that if you    * try to count on the gap that an exception is thrown.    */
annotation|@
name|Test
DECL|method|testManyLogsWithGaps ()
specifier|public
name|void
name|testManyLogsWithGaps
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest3"
argument_list|)
decl_stmt|;
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|long
name|startGapTxId
init|=
literal|3
operator|*
name|TXNS_PER_ROLL
operator|+
literal|1
decl_stmt|;
specifier|final
name|long
name|endGapTxId
init|=
literal|4
operator|*
name|TXNS_PER_ROLL
decl_stmt|;
name|File
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|f
argument_list|,
literal|"current"
argument_list|)
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
name|startGapTxId
argument_list|,
name|endGapTxId
argument_list|)
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|files
index|[
literal|0
index|]
operator|.
name|delete
argument_list|()
argument_list|)
expr_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|startGapTxId
operator|-
literal|1
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
name|startGapTxId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Should have thrown an exception by now"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"Gap in transactions, max txnid is 110, 0 txns from 31"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
comment|// rolled 10 times so there should be 11 files.
name|assertEquals
argument_list|(
literal|11
operator|*
name|TXNS_PER_ROLL
operator|-
name|endGapTxId
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
name|endGapTxId
operator|+
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**     * Test that we can load an edits directory with a corrupt inprogress file.    * The corrupt inprogress file should be moved to the side.    */
annotation|@
name|Test
DECL|method|testManyLogsWithCorruptInprogress ()
specifier|public
name|void
name|testManyLogsWithCorruptInprogress
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest5"
argument_list|)
decl_stmt|;
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|,
operator|new
name|AbortSpec
argument_list|(
literal|10
argument_list|,
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|File
index|[]
name|files
init|=
operator|new
name|File
argument_list|(
name|f
argument_list|,
literal|"current"
argument_list|)
operator|.
name|listFiles
argument_list|(
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"edits_inprogress"
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|files
operator|.
name|length
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|corruptAfterStartSegment
argument_list|(
name|files
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|10
operator|*
name|TXNS_PER_ROLL
operator|+
literal|1
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetRemoteEditLog ()
specifier|public
name|void
name|testGetRemoteEditLog
parameter_list|()
throws|throws
name|IOException
block|{
name|StorageDirectory
name|sd
init|=
name|FSImageTestUtil
operator|.
name|mockStorageDirectory
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|,
literal|false
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|1
argument_list|,
literal|100
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|101
argument_list|,
literal|200
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getInProgressEditsFileName
argument_list|(
literal|201
argument_list|)
argument_list|,
name|NNStorage
operator|.
name|getFinalizedEditsFileName
argument_list|(
literal|1001
argument_list|,
literal|1100
argument_list|)
argument_list|)
decl_stmt|;
name|FileJournalManager
name|fjm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[1,100],[101,200],[1001,1100]"
argument_list|,
name|getLogsAsString
argument_list|(
name|fjm
argument_list|,
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[101,200],[1001,1100]"
argument_list|,
name|getLogsAsString
argument_list|(
name|fjm
argument_list|,
literal|101
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"[1001,1100]"
argument_list|,
name|getLogsAsString
argument_list|(
name|fjm
argument_list|,
literal|201
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|getLogsAsString
argument_list|(
name|fjm
argument_list|,
literal|150
argument_list|)
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw when asking for a txn in the middle of a log"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|ioe
parameter_list|)
block|{
name|GenericTestUtils
operator|.
name|assertExceptionContains
argument_list|(
literal|"150 which is in the middle"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|"Asking for a newer log than exists should return empty list"
argument_list|,
literal|""
argument_list|,
name|getLogsAsString
argument_list|(
name|fjm
argument_list|,
literal|9999
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * tests that passing an invalid dir to matchEditLogs throws IOException     */
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
DECL|method|testMatchEditLogInvalidDirThrowsIOException ()
specifier|public
name|void
name|testMatchEditLogInvalidDirThrowsIOException
parameter_list|()
throws|throws
name|IOException
block|{
name|File
name|badDir
init|=
operator|new
name|File
argument_list|(
literal|"does not exist"
argument_list|)
decl_stmt|;
name|FileJournalManager
operator|.
name|matchEditLogs
argument_list|(
name|badDir
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure that we starting reading the correct op when we request a stream    * with a txid in the middle of an edit log file.    */
annotation|@
name|Test
DECL|method|testReadFromMiddleOfEditLog ()
specifier|public
name|void
name|testReadFromMiddleOfEditLog
parameter_list|()
throws|throws
name|CorruptionException
throws|,
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest2"
argument_list|)
decl_stmt|;
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
name|EditLogInputStream
name|elis
init|=
name|jm
operator|.
name|getInputStream
argument_list|(
literal|5
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|FSEditLogOp
name|op
init|=
name|elis
operator|.
name|readOp
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"read unexpected op"
argument_list|,
name|op
operator|.
name|getTransactionId
argument_list|()
argument_list|,
literal|5
argument_list|)
expr_stmt|;
block|}
comment|/**    * Make sure that in-progress streams aren't counted if we don't ask for    * them.    */
annotation|@
name|Test
DECL|method|testExcludeInProgressStreams ()
specifier|public
name|void
name|testExcludeInProgressStreams
parameter_list|()
throws|throws
name|CorruptionException
throws|,
name|IOException
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|TestEditLog
operator|.
name|TEST_DIR
operator|+
literal|"/filejournaltest2"
argument_list|)
decl_stmt|;
comment|// Don't close the edit log once the files have been set up.
name|NNStorage
name|storage
init|=
name|setupEdits
argument_list|(
name|Collections
operator|.
expr|<
name|URI
operator|>
name|singletonList
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|,
literal|10
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|StorageDirectory
name|sd
init|=
name|storage
operator|.
name|dirIterator
argument_list|(
name|NameNodeDirType
operator|.
name|EDITS
argument_list|)
operator|.
name|next
argument_list|()
decl_stmt|;
name|FileJournalManager
name|jm
init|=
operator|new
name|FileJournalManager
argument_list|(
name|sd
argument_list|)
decl_stmt|;
comment|// If we exclude the in-progess stream, we should only have 100 tx.
name|assertEquals
argument_list|(
literal|100
argument_list|,
name|jm
operator|.
name|getNumberOfTransactions
argument_list|(
literal|1
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|EditLogInputStream
name|elis
init|=
name|jm
operator|.
name|getInputStream
argument_list|(
literal|90
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|FSEditLogOp
name|lastReadOp
init|=
literal|null
decl_stmt|;
while|while
condition|(
operator|(
name|lastReadOp
operator|=
name|elis
operator|.
name|readOp
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|assertTrue
argument_list|(
name|lastReadOp
operator|.
name|getTransactionId
argument_list|()
operator|<=
literal|100
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|getLogsAsString ( FileJournalManager fjm, long firstTxId)
specifier|private
specifier|static
name|String
name|getLogsAsString
parameter_list|(
name|FileJournalManager
name|fjm
parameter_list|,
name|long
name|firstTxId
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|Joiner
operator|.
name|on
argument_list|(
literal|","
argument_list|)
operator|.
name|join
argument_list|(
name|fjm
operator|.
name|getRemoteEditLogs
argument_list|(
name|firstTxId
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

