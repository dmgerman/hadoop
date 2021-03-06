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
name|java
operator|.
name|util
operator|.
name|HashSet
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
name|Set
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
name|FederationNamespaceInfoProto
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
name|GetNamespaceInfoResponseProto
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
name|GetNamespaceInfoResponseProtoOrBuilder
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
name|resolver
operator|.
name|FederationNamespaceInfo
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
name|GetNamespaceInfoResponse
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
comment|/**  * Protobuf implementation of the state store API object  * GetNamespaceInfoResponse.  */
end_comment

begin_class
DECL|class|GetNamespaceInfoResponsePBImpl
specifier|public
class|class
name|GetNamespaceInfoResponsePBImpl
extends|extends
name|GetNamespaceInfoResponse
implements|implements
name|PBRecord
block|{
specifier|private
name|FederationProtocolPBTranslator
argument_list|<
name|GetNamespaceInfoResponseProto
argument_list|,
name|GetNamespaceInfoResponseProto
operator|.
name|Builder
argument_list|,
DECL|field|translator
name|GetNamespaceInfoResponseProtoOrBuilder
argument_list|>
name|translator
init|=
operator|new
name|FederationProtocolPBTranslator
argument_list|<
name|GetNamespaceInfoResponseProto
argument_list|,
name|GetNamespaceInfoResponseProto
operator|.
name|Builder
argument_list|,
name|GetNamespaceInfoResponseProtoOrBuilder
argument_list|>
argument_list|(
name|GetNamespaceInfoResponseProto
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|method|GetNamespaceInfoResponsePBImpl ()
specifier|public
name|GetNamespaceInfoResponsePBImpl
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|getProto ()
specifier|public
name|GetNamespaceInfoResponseProto
name|getProto
parameter_list|()
block|{
return|return
name|this
operator|.
name|translator
operator|.
name|build
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setProto (Message protocol)
specifier|public
name|void
name|setProto
parameter_list|(
name|Message
name|protocol
parameter_list|)
block|{
name|this
operator|.
name|translator
operator|.
name|setProto
argument_list|(
name|protocol
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
DECL|method|getNamespaceInfo ()
specifier|public
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|getNamespaceInfo
parameter_list|()
block|{
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|ret
init|=
operator|new
name|HashSet
argument_list|<
name|FederationNamespaceInfo
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FederationNamespaceInfoProto
argument_list|>
name|namespaceList
init|=
name|this
operator|.
name|translator
operator|.
name|getProtoOrBuilder
argument_list|()
operator|.
name|getNamespaceInfosList
argument_list|()
decl_stmt|;
for|for
control|(
name|FederationNamespaceInfoProto
name|ns
range|:
name|namespaceList
control|)
block|{
name|FederationNamespaceInfo
name|info
init|=
operator|new
name|FederationNamespaceInfo
argument_list|(
name|ns
operator|.
name|getBlockPoolId
argument_list|()
argument_list|,
name|ns
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|ns
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
decl_stmt|;
name|ret
operator|.
name|add
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
name|ret
return|;
block|}
annotation|@
name|Override
DECL|method|setNamespaceInfo (Set<FederationNamespaceInfo> namespaceInfo)
specifier|public
name|void
name|setNamespaceInfo
parameter_list|(
name|Set
argument_list|<
name|FederationNamespaceInfo
argument_list|>
name|namespaceInfo
parameter_list|)
block|{
name|int
name|index
init|=
literal|0
decl_stmt|;
for|for
control|(
name|FederationNamespaceInfo
name|item
range|:
name|namespaceInfo
control|)
block|{
name|FederationNamespaceInfoProto
operator|.
name|Builder
name|itemBuilder
init|=
name|FederationNamespaceInfoProto
operator|.
name|newBuilder
argument_list|()
decl_stmt|;
name|itemBuilder
operator|.
name|setClusterId
argument_list|(
name|item
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
name|itemBuilder
operator|.
name|setBlockPoolId
argument_list|(
name|item
operator|.
name|getBlockPoolId
argument_list|()
argument_list|)
expr_stmt|;
name|itemBuilder
operator|.
name|setNameserviceId
argument_list|(
name|item
operator|.
name|getNameserviceId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|translator
operator|.
name|getBuilder
argument_list|()
operator|.
name|addNamespaceInfos
argument_list|(
name|index
argument_list|,
name|itemBuilder
operator|.
name|build
argument_list|()
argument_list|)
expr_stmt|;
name|index
operator|++
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

