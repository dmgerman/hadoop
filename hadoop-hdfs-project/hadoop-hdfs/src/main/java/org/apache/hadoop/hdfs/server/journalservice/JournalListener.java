begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.journalservice
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
name|journalservice
package|;
end_package

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
name|hdfs
operator|.
name|server
operator|.
name|protocol
operator|.
name|JournalProtocol
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
name|protocol
operator|.
name|NamespaceInfo
import|;
end_import

begin_comment
comment|/**  * JournalListener is a callback interface to handle journal records  * received from the namenode.  */
end_comment

begin_interface
DECL|interface|JournalListener
specifier|public
interface|interface
name|JournalListener
block|{
comment|/**    * Check the namespace information returned by a namenode    * @param service service that is making the callback    * @param info returned namespace information from the namenode    *     * The application using {@link JournalService} can stop the service if    * {@code info} validation fails.    */
DECL|method|verifyVersion (JournalService service, NamespaceInfo info)
specifier|public
name|void
name|verifyVersion
parameter_list|(
name|JournalService
name|service
parameter_list|,
name|NamespaceInfo
name|info
parameter_list|)
function_decl|;
comment|/**    * Process the received Journal record    * @param service {@link JournalService} making the callback    * @param firstTxnId first transaction Id in the journal    * @param numTxns number of records    * @param records journal records    * @throws IOException on error    *     * Any IOException thrown from the listener is thrown back in     * {@link JournalProtocol#journal}    */
DECL|method|journal (JournalService service, long firstTxnId, int numTxns, byte[] records)
specifier|public
name|void
name|journal
parameter_list|(
name|JournalService
name|service
parameter_list|,
name|long
name|firstTxnId
parameter_list|,
name|int
name|numTxns
parameter_list|,
name|byte
index|[]
name|records
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Roll the editlog    * @param service {@link JournalService} making the callback    * @param txid transaction ID to roll at    *     * Any IOException thrown from the listener is thrown back in     * {@link JournalProtocol#startLogSegment}    */
DECL|method|rollLogs (JournalService service, long txid)
specifier|public
name|void
name|rollLogs
parameter_list|(
name|JournalService
name|service
parameter_list|,
name|long
name|txid
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

