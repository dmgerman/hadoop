begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce.v2.hs.protocolPB
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|hs
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
name|classification
operator|.
name|InterfaceAudience
operator|.
name|Private
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshAdminAclsResponseProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshAdminAclsRequestProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshLoadedJobCacheRequestProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshLoadedJobCacheResponseProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshJobRetentionSettingsRequestProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshJobRetentionSettingsResponseProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshLogRetentionSettingsRequestProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|proto
operator|.
name|HSAdminRefreshProtocolProtos
operator|.
name|RefreshLogRetentionSettingsResponseProto
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
name|mapreduce
operator|.
name|v2
operator|.
name|hs
operator|.
name|protocol
operator|.
name|HSAdminRefreshProtocol
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
annotation|@
name|Private
DECL|class|HSAdminRefreshProtocolServerSideTranslatorPB
specifier|public
class|class
name|HSAdminRefreshProtocolServerSideTranslatorPB
implements|implements
name|HSAdminRefreshProtocolPB
block|{
DECL|field|impl
specifier|private
specifier|final
name|HSAdminRefreshProtocol
name|impl
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|RefreshAdminAclsResponseProto
DECL|field|VOID_REFRESH_ADMIN_ACLS_RESPONSE
name|VOID_REFRESH_ADMIN_ACLS_RESPONSE
init|=
name|RefreshAdminAclsResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|RefreshLoadedJobCacheResponseProto
DECL|field|VOID_REFRESH_LOADED_JOB_CACHE_RESPONSE
name|VOID_REFRESH_LOADED_JOB_CACHE_RESPONSE
init|=
name|RefreshLoadedJobCacheResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|RefreshJobRetentionSettingsResponseProto
DECL|field|VOID_REFRESH_JOB_RETENTION_SETTINGS_RESPONSE
name|VOID_REFRESH_JOB_RETENTION_SETTINGS_RESPONSE
init|=
name|RefreshJobRetentionSettingsResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|RefreshLogRetentionSettingsResponseProto
DECL|field|VOID_REFRESH_LOG_RETENTION_SETTINGS_RESPONSE
name|VOID_REFRESH_LOG_RETENTION_SETTINGS_RESPONSE
init|=
name|RefreshLogRetentionSettingsResponseProto
operator|.
name|newBuilder
argument_list|()
operator|.
name|build
argument_list|()
decl_stmt|;
DECL|method|HSAdminRefreshProtocolServerSideTranslatorPB ( HSAdminRefreshProtocol impl)
specifier|public
name|HSAdminRefreshProtocolServerSideTranslatorPB
parameter_list|(
name|HSAdminRefreshProtocol
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
DECL|method|refreshAdminAcls ( RpcController controller, RefreshAdminAclsRequestProto request)
specifier|public
name|RefreshAdminAclsResponseProto
name|refreshAdminAcls
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshAdminAclsRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|refreshAdminAcls
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
name|VOID_REFRESH_ADMIN_ACLS_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|refreshLoadedJobCache ( RpcController controller, RefreshLoadedJobCacheRequestProto request)
specifier|public
name|RefreshLoadedJobCacheResponseProto
name|refreshLoadedJobCache
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshLoadedJobCacheRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|refreshLoadedJobCache
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
name|VOID_REFRESH_LOADED_JOB_CACHE_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|refreshJobRetentionSettings ( RpcController controller, RefreshJobRetentionSettingsRequestProto request)
specifier|public
name|RefreshJobRetentionSettingsResponseProto
name|refreshJobRetentionSettings
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshJobRetentionSettingsRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|refreshJobRetentionSettings
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
name|VOID_REFRESH_JOB_RETENTION_SETTINGS_RESPONSE
return|;
block|}
annotation|@
name|Override
DECL|method|refreshLogRetentionSettings ( RpcController controller, RefreshLogRetentionSettingsRequestProto request)
specifier|public
name|RefreshLogRetentionSettingsResponseProto
name|refreshLogRetentionSettings
parameter_list|(
name|RpcController
name|controller
parameter_list|,
name|RefreshLogRetentionSettingsRequestProto
name|request
parameter_list|)
throws|throws
name|ServiceException
block|{
try|try
block|{
name|impl
operator|.
name|refreshLogRetentionSettings
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
name|VOID_REFRESH_LOG_RETENTION_SETTINGS_RESPONSE
return|;
block|}
block|}
end_class

end_unit

