begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.qjournal.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|qjournal
operator|.
name|protocolPB
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|RpcController
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|protobuf
operator|.
name|ServiceException
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
name|qjournal
operator|.
name|protocol
operator|.
name|InterQJournalProtocol
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|GetEditLogManifestRequestProto
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
name|qjournal
operator|.
name|protocol
operator|.
name|QJournalProtocolProtos
operator|.
name|GetEditLogManifestResponseProto
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

begin_comment
comment|/**  * Implementation for protobuf service that forwards requests  * received on {@link InterQJournalProtocolPB} to the  * {@link InterQJournalProtocol} server implementation.  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
DECL|class|InterQJournalProtocolServerSideTranslatorPB
specifier|public
class|class
name|InterQJournalProtocolServerSideTranslatorPB
implements|implements
name|InterQJournalProtocolPB
block|{
comment|/* Server side implementation to delegate the requests to. */
DECL|field|impl
specifier|private
specifier|final
name|InterQJournalProtocol
name|impl
decl_stmt|;
DECL|method|InterQJournalProtocolServerSideTranslatorPB (InterQJournalProtocol impl)
specifier|public
name|InterQJournalProtocolServerSideTranslatorPB
parameter_list|(
name|InterQJournalProtocol
name|impl
parameter_list|)
block|{
name|this
operator|.
name|impl
operator|=
name|impl
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getEditLogManifestFromJournal ( RpcController controller, GetEditLogManifestRequestProto request)
specifier|public
name|GetEditLogManifestResponseProto
name|getEditLogManifestFromJournal
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|GetEditLogManifestRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
return|return
name|impl
operator|.
name|getEditLogManifestFromJournal
argument_list|(
name|request
operator|.
name|getJid
argument_list|()
operator|.
name|getIdentifier
argument_list|()
argument_list|,
name|request
operator|.
name|hasNameServiceId
argument_list|()
condition|?
name|request
operator|.
name|getNameServiceId
argument_list|()
else|:
literal|null
argument_list|,
name|request
operator|.
name|getSinceTxId
argument_list|()
argument_list|,
name|request
operator|.
name|getInProgressOk
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServiceException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

