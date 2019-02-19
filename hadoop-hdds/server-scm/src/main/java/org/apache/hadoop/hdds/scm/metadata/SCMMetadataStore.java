begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.ââSee the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.ââThe ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  *  with the License.ââYou may obtain a copy of the License at  *  * ââââ http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdds.scm.metadata
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
name|metadata
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|cert
operator|.
name|X509Certificate
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|annotations
operator|.
name|VisibleForTesting
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
name|hdds
operator|.
name|security
operator|.
name|x509
operator|.
name|certificate
operator|.
name|authority
operator|.
name|CertificateStore
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
name|utils
operator|.
name|db
operator|.
name|DBStore
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
name|utils
operator|.
name|db
operator|.
name|Table
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|utils
operator|.
name|db
operator|.
name|TableIterator
import|;
end_import

begin_comment
comment|/**  * Generic interface for data stores for SCM.  * This is similar to the OMMetadataStore class,  * where we write classes into some underlying storage system.  */
end_comment

begin_interface
DECL|interface|SCMMetadataStore
specifier|public
interface|interface
name|SCMMetadataStore
block|{
comment|/**    * Start metadata manager.    *    * @param configuration - Configuration    * @throws IOException - Unable to start metadata store.    */
DECL|method|start (OzoneConfiguration configuration)
name|void
name|start
parameter_list|(
name|OzoneConfiguration
name|configuration
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**    * Stop metadata manager.    */
DECL|method|stop ()
name|void
name|stop
parameter_list|()
throws|throws
name|Exception
function_decl|;
comment|/**    * Get metadata store.    *    * @return metadata store.    */
annotation|@
name|VisibleForTesting
DECL|method|getStore ()
name|DBStore
name|getStore
parameter_list|()
function_decl|;
comment|/**    * A Table that keeps the deleted blocks lists and transactions.    *    * @return Table    */
DECL|method|getDeletedBlocksTXTable ()
name|Table
argument_list|<
name|Long
argument_list|,
name|DeletedBlocksTransaction
argument_list|>
name|getDeletedBlocksTXTable
parameter_list|()
function_decl|;
comment|/**    * Returns the current TXID for the deleted blocks.    *    * @return Long    */
DECL|method|getCurrentTXID ()
name|Long
name|getCurrentTXID
parameter_list|()
function_decl|;
comment|/**    * Returns the next TXID for the Deleted Blocks.    *    * @return Long.    */
DECL|method|getNextDeleteBlockTXID ()
name|Long
name|getNextDeleteBlockTXID
parameter_list|()
function_decl|;
comment|/**    * A table that maintains all the valid certificates issued by the SCM CA.    *    * @return Table    */
DECL|method|getValidCertsTable ()
name|Table
argument_list|<
name|BigInteger
argument_list|,
name|X509Certificate
argument_list|>
name|getValidCertsTable
parameter_list|()
function_decl|;
comment|/**    * A Table that maintains all revoked certificates until they expire.    *    * @return Table.    */
DECL|method|getRevokedCertsTable ()
name|Table
argument_list|<
name|BigInteger
argument_list|,
name|X509Certificate
argument_list|>
name|getRevokedCertsTable
parameter_list|()
function_decl|;
comment|/**    * Returns the list of Certificates of a specific type.    *    * @param certType - CertType.    * @return Iterator<X509Certificate>    */
DECL|method|getAllCerts (CertificateStore.CertType certType)
name|TableIterator
name|getAllCerts
parameter_list|(
name|CertificateStore
operator|.
name|CertType
name|certType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

