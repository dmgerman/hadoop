begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocolPB
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
name|protocol
operator|.
name|proto
operator|.
name|RefreshAuthorizationPolicyProtocolProtos
operator|.
name|RefreshServiceAclRequestProto
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
name|proto
operator|.
name|RefreshAuthorizationPolicyProtocolProtos
operator|.
name|RefreshServiceAclResponseProto
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
name|authorize
operator|.
name|RefreshAuthorizationPolicyProtocol
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

begin_class
DECL|class|RefreshAuthorizationPolicyProtocolServerSideTranslatorPB
specifier|public
class|class
name|RefreshAuthorizationPolicyProtocolServerSideTranslatorPB
implements|implements
name|RefreshAuthorizationPolicyProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|RefreshAuthorizationPolicyProtocol
name|impl
decl_stmt|;
DECL|method|RefreshAuthorizationPolicyProtocolServerSideTranslatorPB ( RefreshAuthorizationPolicyProtocol impl)
specifier|public
name|RefreshAuthorizationPolicyProtocolServerSideTranslatorPB
parameter_list|(
name|RefreshAuthorizationPolicyProtocol
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
DECL|method|refreshServiceAcl ( RpcController controller, RefreshServiceAclRequestProto request)
specifier|public
name|RefreshServiceAclResponseProto
name|refreshServiceAcl
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshServiceAclRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|refreshServiceAcl
argument_list|()
expr_stmt|;
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
return|return
name|RefreshServiceAclResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
return|;
block|}
block|}
end_class

end_unit

