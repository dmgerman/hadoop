begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with this  * work for additional information regarding copyright ownership.  The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.federation.store.records.impl.pb
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
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
name|classification
operator|.
name|InterfaceStability
operator|.
name|Unstable
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
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterIdProto
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
name|federation
operator|.
name|proto
operator|.
name|YarnServerFederationProtos
operator|.
name|SubClusterIdProtoOrBuilder
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
name|server
operator|.
name|federation
operator|.
name|store
operator|.
name|records
operator|.
name|SubClusterId
import|;
end_import

begin_comment
comment|/**  * Protocol buffer based implementation of {@link SubClusterId}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|SubClusterIdPBImpl
specifier|public
class|class
name|SubClusterIdPBImpl
extends|extends
name|SubClusterId
block|{
DECL|field|proto
specifier|private
name|SubClusterIdProto
name|proto
init|=
name|SubClusterIdProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|SubClusterIdProto
operator|.
name|Builder
name|builder
init|=
literal|null
decl_stmt|;
DECL|field|viaProto
specifier|private
name|boolean
name|viaProto
init|=
literal|false
decl_stmt|;
DECL|method|SubClusterIdPBImpl ()
specifier|public
name|SubClusterIdPBImpl
parameter_list|()
block|{
name|builder
operator|=
name|SubClusterIdProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|SubClusterIdPBImpl (SubClusterIdProto proto)
specifier|public
name|SubClusterIdPBImpl
parameter_list|(
name|SubClusterIdProto
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
name|SubClusterIdProto
name|getProto
parameter_list|()
block|{
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
name|SubClusterIdProto
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
annotation|@
name|Override
DECL|method|getId ()
specifier|public
name|String
name|getId
parameter_list|()
block|{
name|SubClusterIdProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
return|return
name|p
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|setId (String subClusterId)
specifier|protected
name|void
name|setId
parameter_list|(
name|String
name|subClusterId
parameter_list|)
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
if|if
condition|(
name|subClusterId
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearId
argument_list|()
expr_stmt|;
return|return;
block|}
name|builder
operator|.
name|setId
argument_list|(
name|subClusterId
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

