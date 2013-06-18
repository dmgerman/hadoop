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
name|api
operator|.
name|protocolrecords
operator|.
name|GetClusterNodesResponse
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
name|api
operator|.
name|records
operator|.
name|NodeReport
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
name|api
operator|.
name|records
operator|.
name|impl
operator|.
name|pb
operator|.
name|NodeReportPBImpl
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
name|YarnProtos
operator|.
name|NodeReportProto
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
name|GetClusterNodesResponseProto
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
name|GetClusterNodesResponseProtoOrBuilder
import|;
end_import

begin_class
annotation|@
name|Private
annotation|@
name|Unstable
DECL|class|GetClusterNodesResponsePBImpl
specifier|public
class|class
name|GetClusterNodesResponsePBImpl
extends|extends
name|GetClusterNodesResponse
block|{
DECL|field|proto
name|GetClusterNodesResponseProto
name|proto
init|=
name|GetClusterNodesResponseProto
operator|.
name|getDefaultInstance
argument_list|()
decl_stmt|;
DECL|field|builder
name|GetClusterNodesResponseProto
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
DECL|field|nodeManagerInfoList
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeManagerInfoList
decl_stmt|;
DECL|method|GetClusterNodesResponsePBImpl ()
specifier|public
name|GetClusterNodesResponsePBImpl
parameter_list|()
block|{
name|builder
operator|=
name|GetClusterNodesResponseProto
operator|.
name|newBuilder
argument_list|()
expr_stmt|;
block|}
DECL|method|GetClusterNodesResponsePBImpl (GetClusterNodesResponseProto proto)
specifier|public
name|GetClusterNodesResponsePBImpl
parameter_list|(
name|GetClusterNodesResponseProto
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
annotation|@
name|Override
DECL|method|getNodeReports ()
specifier|public
name|List
argument_list|<
name|NodeReport
argument_list|>
name|getNodeReports
parameter_list|()
block|{
name|initLocalNodeManagerInfosList
argument_list|()
expr_stmt|;
return|return
name|this
operator|.
name|nodeManagerInfoList
return|;
block|}
annotation|@
name|Override
DECL|method|setNodeReports (List<NodeReport> nodeManagers)
specifier|public
name|void
name|setNodeReports
parameter_list|(
name|List
argument_list|<
name|NodeReport
argument_list|>
name|nodeManagers
parameter_list|)
block|{
if|if
condition|(
name|nodeManagers
operator|==
literal|null
condition|)
block|{
name|builder
operator|.
name|clearNodeReports
argument_list|()
expr_stmt|;
block|}
name|this
operator|.
name|nodeManagerInfoList
operator|=
name|nodeManagers
expr_stmt|;
block|}
DECL|method|getProto ()
specifier|public
name|GetClusterNodesResponseProto
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
return|return
literal|false
return|;
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
name|getProto
argument_list|()
operator|.
name|toString
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|", "
argument_list|)
operator|.
name|replaceAll
argument_list|(
literal|"\\s+"
argument_list|,
literal|" "
argument_list|)
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
name|nodeManagerInfoList
operator|!=
literal|null
condition|)
block|{
name|addLocalNodeManagerInfosToProto
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
name|maybeInitBuilder
argument_list|()
expr_stmt|;
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
name|GetClusterNodesResponseProto
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
comment|// Once this is called. containerList will never be null - until a getProto
comment|// is called.
DECL|method|initLocalNodeManagerInfosList ()
specifier|private
name|void
name|initLocalNodeManagerInfosList
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|nodeManagerInfoList
operator|!=
literal|null
condition|)
block|{
return|return;
block|}
name|GetClusterNodesResponseProtoOrBuilder
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
name|NodeReportProto
argument_list|>
name|list
init|=
name|p
operator|.
name|getNodeReportsList
argument_list|()
decl_stmt|;
name|nodeManagerInfoList
operator|=
operator|new
name|ArrayList
argument_list|<
name|NodeReport
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|NodeReportProto
name|a
range|:
name|list
control|)
block|{
name|nodeManagerInfoList
operator|.
name|add
argument_list|(
name|convertFromProtoFormat
argument_list|(
name|a
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|addLocalNodeManagerInfosToProto ()
specifier|private
name|void
name|addLocalNodeManagerInfosToProto
parameter_list|()
block|{
name|maybeInitBuilder
argument_list|()
expr_stmt|;
name|builder
operator|.
name|clearNodeReports
argument_list|()
expr_stmt|;
if|if
condition|(
name|nodeManagerInfoList
operator|==
literal|null
condition|)
return|return;
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
name|iterable
init|=
operator|new
name|Iterable
argument_list|<
name|NodeReportProto
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|NodeReportProto
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Iterator
argument_list|<
name|NodeReportProto
argument_list|>
argument_list|()
block|{
name|Iterator
argument_list|<
name|NodeReport
argument_list|>
name|iter
init|=
name|nodeManagerInfoList
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
name|NodeReportProto
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
name|addAllNodeReports
argument_list|(
name|iterable
argument_list|)
expr_stmt|;
block|}
DECL|method|convertFromProtoFormat (NodeReportProto p)
specifier|private
name|NodeReportPBImpl
name|convertFromProtoFormat
parameter_list|(
name|NodeReportProto
name|p
parameter_list|)
block|{
return|return
operator|new
name|NodeReportPBImpl
argument_list|(
name|p
argument_list|)
return|;
block|}
DECL|method|convertToProtoFormat (NodeReport t)
specifier|private
name|NodeReportProto
name|convertToProtoFormat
parameter_list|(
name|NodeReport
name|t
parameter_list|)
block|{
return|return
operator|(
operator|(
name|NodeReportPBImpl
operator|)
name|t
operator|)
operator|.
name|getProto
argument_list|()
return|;
block|}
block|}
end_class

end_unit

