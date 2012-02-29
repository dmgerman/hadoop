begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.protocol
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
name|protocol
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
name|classification
operator|.
name|InterfaceAudience
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
name|ipc
operator|.
name|VersionedProtocol
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
name|KerberosInfo
import|;
end_import

begin_comment
comment|/**  * Protocol used to journal edits to a remote node. Currently,  * this is used to publish edits from the NameNode to a BackupNode.  */
end_comment

begin_interface
annotation|@
name|KerberosInfo
argument_list|(
name|serverPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|,
name|clientPrincipal
operator|=
name|DFSConfigKeys
operator|.
name|DFS_NAMENODE_USER_NAME_KEY
argument_list|)
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|interface|JournalProtocol
specifier|public
interface|interface
name|JournalProtocol
block|{
comment|/**    *     * This class is used by both the Namenode (client) and BackupNode (server)     * to insulate from the protocol serialization.    *     * If you are adding/changing DN's interface then you need to     * change both this class and ALSO related protocol buffer    * wire protocol definition in JournalProtocol.proto.    *     * For more details on protocol buffer wire protocol, please see     * .../org/apache/hadoop/hdfs/protocolPB/overview.html    */
DECL|field|versionID
specifier|public
specifier|static
specifier|final
name|long
name|versionID
init|=
literal|1L
decl_stmt|;
comment|/**    * Journal edit records.    * This message is sent by the active name-node to the backup node    * via {@code EditLogBackupOutputStream} in order to synchronize meta-data    * changes with the backup namespace image.    *     * @param registration active node registration    * @param firstTxnId the first transaction of this batch    * @param numTxns number of transactions    * @param records byte array containing serialized journal records    */
DECL|method|journal (NamenodeRegistration registration, long firstTxnId, int numTxns, byte[] records)
specifier|public
name|void
name|journal
parameter_list|(
name|NamenodeRegistration
name|registration
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
comment|/**    * Notify the BackupNode that the NameNode has rolled its edit logs    * and is now writing a new log segment.    * @param registration the registration of the active NameNode    * @param txid the first txid in the new log    */
DECL|method|startLogSegment (NamenodeRegistration registration, long txid)
specifier|public
name|void
name|startLogSegment
parameter_list|(
name|NamenodeRegistration
name|registration
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

