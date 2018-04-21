begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.server.federation.store.protocol.impl.pb
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|impl
operator|.
name|pb
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnableNameserviceResponseProto
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnableNameserviceResponseProto
operator|.
name|Builder
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
name|federation
operator|.
name|protocol
operator|.
name|proto
operator|.
name|HdfsServerFederationProtos
operator|.
name|EnableNameserviceResponseProtoOrBuilder
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
name|federation
operator|.
name|store
operator|.
name|protocol
operator|.
name|EnableNameserviceResponse
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
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|PBRecord
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
name|Message
import|;
end_import

begin_comment
comment|/**  * Protobuf implementation of the state store API object  * EnableNameserviceResponse.  */
end_comment

begin_class
DECL|class|EnableNameserviceResponsePBImpl
specifier|public
class|class
name|EnableNameserviceResponsePBImpl
extends|extends
name|EnableNameserviceResponse
implements|implements
name|PBRecord
block|{
specifier|private
name|FederationProtocolPBTranslator
argument_list|<
name|EnableNameserviceResponseProto
argument_list|,
DECL|field|translator
name|Builder
argument_list|,
name|EnableNameserviceResponseProtoOrBuilder
argument_list|>
name|translator
init|=
operator|new
name|FederationProtocolPBTranslator
argument_list|<>
argument_list|(
name|EnableNameserviceResponseProto
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|EnableNameserviceResponsePBImpl ()
specifier|public
name|EnableNameserviceResponsePBImpl
parameter_list|()
block|{   }
DECL|method|EnableNameserviceResponsePBImpl (EnableNameserviceResponseProto proto)
specifier|public
name|EnableNameserviceResponsePBImpl
parameter_list|(
name|EnableNameserviceResponseProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|setProto
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|EnableNameserviceResponseProto
name|getProto
parameter_list|()
block|{
return|return
name|translator
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setProto (Message proto)
specifier|public
name|void
name|setProto
parameter_list|(
name|Message
name|proto
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|setProto
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|readInstance (String base64String)
specifier|public
name|void
name|readInstance
parameter_list|(
name|String
name|base64String
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|translator
operator|.
name|readInstance
argument_list|(
name|base64String
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getStatus ()
specifier|public
name|boolean
name|getStatus
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getStatus
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setStatus (boolean status)
specifier|public
name|void
name|setStatus
parameter_list|(
name|boolean
name|status
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|setStatus
argument_list|(
name|status
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

