begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|GetSubClustersInfoResponseProto
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
name|GetSubClustersInfoResponseProtoOrBuilder
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
name|SubClusterInfoProto
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
name|GetSubClustersInfoResponse
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
name|SubClusterInfo
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
name|TextFormat
import|;
end_import

begin_comment
comment|/**  * Protocol buffer based implementation of {@link GetSubClustersInfoResponse}.  */
end_comment

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetSubClustersInfoResponsePBImpl
specifier|public
class|class
name|GetSubClustersInfoResponsePBImpl
extends|extends
name|GetSubClustersInfoResponse
block|{
DECL|field|proto
specifier|private
name|GetSubClustersInfoResponseProto
name|proto
init|=
name|GetSubClustersInfoResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
specifier|private
name|GetSubClustersInfoResponseProto
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
DECL|field|subClusterInfos
specifier|private
name|List
argument_list|<
name|SubClusterInfo
argument_list|>
name|subClusterInfos
decl_stmt|;
DECL|method|GetSubClustersInfoResponsePBImpl ()
specifier|public
name|GetSubClustersInfoResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetSubClustersInfoResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetSubClustersInfoResponsePBImpl ( GetSubClustersInfoResponseProto proto)
specifier|public
name|GetSubClustersInfoResponsePBImpl
parameter_list|(
name|GetSubClustersInfoResponseProto
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
name|GetSubClustersInfoResponseProto
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
DECL|method|mergeLocalToBuilder ()
specifier|private
name|void
name|mergeLocalToBuilder
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|subClusterInfos
operator|!=
literal|null
condition|)
block|{
name|addSubClusterInfosToProto
argument_list|()
expr_stmt|;
block|}
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
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
block|}
name|mergeLocalToBuilder
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
name|GetSubClustersInfoResponseProto
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
DECL|method|getSubClusters ()
specifier|public
name|List
argument_list|<
name|SubClusterInfo
argument_list|>
name|getSubClusters
parameter_list|()
block|{
name|initSubClustersInfoList
argument_list|()
expr_stmt|;
return|return
name|subClusterInfos
return|;
block|}
annotation|@
name|Override
DECL|method|setSubClusters (List<SubClusterInfo> subClusters)
specifier|public
name|void
name|setSubClusters
parameter_list|(
name|List
argument_list|<
name|SubClusterInfo
argument_list|>
name|subClusters
parameter_list|)
block|{
if|if
condition|(
name|subClusters
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearSubClusterInfos
argument_list|()
expr_stmt|;
return|return;
block|}
name|this
operator|.
name|subClusterInfos
operator|=
name|subClusters
expr_stmt|;
block|}
DECL|method|initSubClustersInfoList ()
specifier|private
name|void
name|initSubClustersInfoList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|subClusterInfos
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetSubClustersInfoResponseProtoOrBuilder
name|p
init|=
name|viaProto
condition|?
name|proto
else|:
name|builder
decl_stmt|;
name|List
argument_list|<
name|SubClusterInfoProto
argument_list|>
name|subClusterInfosList
init|=
name|p
operator|.
name|getSubClusterInfosList
argument_list|()
decl_stmt|;
name|subClusterInfos
operator|=
operator|new
name|ArrayList
argument_list|<
name|SubClusterInfo
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|SubClusterInfoProto
name|r
range|:
name|subClusterInfosList
control|)
block|{
name|subClusterInfos
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|r
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addSubClusterInfosToProto ()
specifier|private
name|void
name|addSubClusterInfosToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearSubClusterInfos
argument_list|()
expr_stmt|;
if|if
condition|(
name|subClusterInfos
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|Iterable
argument_list|<
name|SubClusterInfoProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|SubClusterInfoProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|SubClusterInfoProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|SubClusterInfoProto
argument_list|>
argument_list|()
block|{
specifier|private
name|Iterator
argument_list|<
name|SubClusterInfo
argument_list|>
name|iter
init|=
name|subClusterInfos
operator|.
name|iterator
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|iter
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|SubClusterInfoProto
name|next
parameter_list|()
block|{
return|return
name|convertToProtoFormat
argument_list|(
name|iter
operator|.
name|next
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
block|}
return|;
block|}
block|}
decl_stmt|;
name|builder
operator|.
name|addAllSubClusterInfos
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|convertToProtoFormat (SubClusterInfo r)
specifier|private
name|SubClusterInfoProto
name|convertToProtoFormat
parameter_list|(
name|SubClusterInfo
name|r
parameter_list|)
block|{
return|return
operator|(
operator|(
name|SubClusterInfoPBImpl
operator|)
name|r
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
DECL|method|convertFromProtoFormat (SubClusterInfoProto r)
specifier|private
name|SubClusterInfoPBImpl
name|convertFromProtoFormat
parameter_list|(
name|SubClusterInfoProto
name|r
parameter_list|)
block|{
return|return
operator|new
name|SubClusterInfoPBImpl
argument_list|(
name|r
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|hashCode ()
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|getProto
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
annotation|@
name|Override
DECL|method|equals (Object other)
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|.
name|isAssignableFrom
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|this
operator|.
name|getProto
argument_list|()
operator|.
name|equals
argument_list|(
name|this
operator|.
name|getClass
argument_list|()
operator|.
name|cast
argument_list|(
name|other
argument_list|)
operator|.
name|getProto
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
DECL|method|toString ()
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|TextFormat
operator|.
name|shortDebugString
argument_list|(
name|getProto
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

