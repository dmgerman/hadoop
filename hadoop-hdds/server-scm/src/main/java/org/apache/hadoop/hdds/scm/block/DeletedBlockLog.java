begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.block
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdds
operator|.
name|scm
operator|.
name|block
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
name|hdds
operator|.
name|protocol
operator|.
name|proto
operator|.
name|StorageContainerDatanodeProtocolProtos
operator|.
name|DeletedBlocksTransaction
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * The DeletedBlockLog is a persisted log in SCM to keep tracking  * container blocks which are under deletion. It maintains info  * about under-deletion container blocks that notified by OM,  * and the state how it is processed.  */
end_comment

begin_interface
DECL|interface|DeletedBlockLog
specifier|public
interface|interface
name|DeletedBlockLog
extends|extends
name|Closeable
block|{
comment|/**    *  A limit size list of transactions. Note count is the max number    *  of TXs to return, we might not be able to always return this    *  number. and the processCount of those transactions    *  should be [0, MAX_RETRY).    *    * @param count - number of transactions.    * @return a list of BlockDeletionTransaction.    */
DECL|method|getTransactions (int count)
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|getTransactions
parameter_list|(
name|int
name|count
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Scan entire log once and returns TXs to DatanodeDeletedBlockTransactions.    * Once DatanodeDeletedBlockTransactions is full, the scan behavior will    * stop.    * @param transactions a list of TXs will be set into.    * @throws IOException    */
DECL|method|getTransactions (DatanodeDeletedBlockTransactions transactions)
name|void
name|getTransactions
parameter_list|(
name|DatanodeDeletedBlockTransactions
name|transactions
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Return all failed transactions in the log. A transaction is considered    * to be failed if it has been sent more than MAX_RETRY limit and its    * count is reset to -1.    *    * @return a list of failed deleted block transactions.    * @throws IOException    */
DECL|method|getFailedTransactions ()
name|List
argument_list|<
name|DeletedBlocksTransaction
argument_list|>
name|getFailedTransactions
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**    * Increments count for given list of transactions by 1.    * The log maintains a valid range of counts for each transaction    * [0, MAX_RETRY]. If exceed this range, resets it to -1 to indicate    * the transaction is no longer valid.    *    * @param txIDs - transaction ID.    */
DECL|method|incrementCount (List<Long> txIDs)
name|void
name|incrementCount
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Commits a transaction means to delete all footprints of a transaction    * from the log. This method doesn't guarantee all transactions can be    * successfully deleted, it tolerate failures and tries best efforts to.    *    * @param txIDs - transaction IDs.    */
DECL|method|commitTransactions (List<Long> txIDs)
name|void
name|commitTransactions
parameter_list|(
name|List
argument_list|<
name|Long
argument_list|>
name|txIDs
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates a block deletion transaction and adds that into the log.    *    * @param containerID - container ID.    * @param blocks - blocks that belong to the same container.    *    * @throws IOException    */
DECL|method|addTransaction (long containerID, List<Long> blocks)
name|void
name|addTransaction
parameter_list|(
name|long
name|containerID
parameter_list|,
name|List
argument_list|<
name|Long
argument_list|>
name|blocks
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Creates block deletion transactions for a set of containers,    * add into the log and persist them atomically. An object key    * might be stored in multiple containers and multiple blocks,    * this API ensures that these updates are done in atomic manner    * so if any of them fails, the entire operation fails without    * any updates to the log. Note, this doesn't mean to create only    * one transaction, it creates multiple transactions (depends on the    * number of containers) together (on success) or non (on failure).    *    * @param containerBlocksMap a map of containerBlocks.    * @return Mapping from containerId to latest transactionId for the container.    * @throws IOException    */
DECL|method|addTransactions (Map<Long, List<Long>> containerBlocksMap)
name|Map
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|addTransactions
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|List
argument_list|<
name|Long
argument_list|>
argument_list|>
name|containerBlocksMap
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Returns the total number of valid transactions. A transaction is    * considered to be valid as long as its count is in range [0, MAX_RETRY].    *    * @return number of a valid transactions.    * @throws IOException    */
DECL|method|getNumOfValidTransactions ()
name|int
name|getNumOfValidTransactions
parameter_list|()
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

