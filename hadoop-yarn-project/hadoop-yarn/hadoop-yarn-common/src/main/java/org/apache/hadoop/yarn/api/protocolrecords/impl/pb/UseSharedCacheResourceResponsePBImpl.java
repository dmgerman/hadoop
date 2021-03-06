begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.protocolrecords.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
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
name|yarn
operator|.
name|api
operator|.
name|protocolrecords
operator|.
name|UseSharedCacheResourceResponse
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|UseSharedCacheResourceResponseProto
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
name|yarn
operator|.
name|proto
operator|.
name|YarnServiceProtos
operator|.
name|UseSharedCacheResourceResponseProtoOrBuilder
import|;
end_import

begin_class
DECL|class|UseSharedCacheResourceResponsePBImpl
specifier|public
class|class
name|UseSharedCacheResourceResponsePBImpl
extends|extends
name|UseSharedCacheResourceResponse
block|{
DECL|field|proto
name|UseSharedCacheResourceResponseProto
name|proto
init|=
name|UseSharedCacheResourceResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|UseSharedCacheResourceResponseProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|UseSharedCacheResourceResponsePBImpl ()
specifier|public
name|UseSharedCacheResourceResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|UseSharedCacheResourceResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|UseSharedCacheResourceResponsePBImpl ( UseSharedCacheResourceResponseProto proto)
specifier|public
name|UseSharedCacheResourceResponsePBImpl
parameter_list|(
name|UseSharedCacheResourceResponseProto
name|proto
parameter_list|)
block|{
name|this
operator|.
name|proto
operator|=
name|proto
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|UseSharedCacheResourceResponseProto
name|getProto
parameter_list|()
block|{
name|mergeLocalToProto
argument_list|()
expr_stmt|;
name|proto
operator|=
name|viaProto
condition|?
name|proto
else|:
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
return|return
name|proto
return|;
block|}
annotation|@
name|Override
DECL|method|getPath ()
specifier|public
name|String
name|getPath
parameter_list|()
block|{
name|UseSharedCacheResourceResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
operator|(
name|p
operator|.
name|hasPath
argument_list|()
operator|)
condition|?
name|p
operator|.
name|getPath
argument_list|()
else|:
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|setPath (String path)
specifier|public
name|void
name|setPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearPath
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|mergeLocalToProto ()
specifier|private
name|void
name|mergeLocalToProto
parameter_list|()
block|{
if|if
condition|(
name|viaProto
condition|)
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|proto
operator|=
name|builder
operator|.
name|build
argument_list|()
expr_stmt|;
name|viaProto
operator|=
literal|true
expr_stmt|;
block|}
DECL|method|maybeInitBuilder ()
specifier|private
name|void
name|maybeInitBuilder
parameter_list|()
block|{
if|if
condition|(
name|viaProto
operator|||
name|builder
operator|==
literal|null
condition|)
block|{
name|builder
operator|=
name|UseSharedCacheResourceResponseProto
operator|.
name|newBuilder
argument_list|(
name|proto
argument_list|)
expr_stmt|;
block|}
name|viaProto
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

