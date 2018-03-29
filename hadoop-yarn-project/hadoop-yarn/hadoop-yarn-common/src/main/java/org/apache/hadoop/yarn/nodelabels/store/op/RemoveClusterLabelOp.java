begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.nodelabels.store.op
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|nodelabels
operator|.
name|store
operator|.
name|op
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|nodelabels
operator|.
name|CommonNodeLabelsManager
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
name|YarnServerResourceManagerServiceProtos
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
name|api
operator|.
name|protocolrecords
operator|.
name|RemoveFromClusterNodeLabelsRequest
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
name|api
operator|.
name|protocolrecords
operator|.
name|impl
operator|.
name|pb
operator|.
name|RemoveFromClusterNodeLabelsRequestPBImpl
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_comment
comment|/**  * Remove label from cluster log store operation.  */
end_comment

begin_class
DECL|class|RemoveClusterLabelOp
specifier|public
class|class
name|RemoveClusterLabelOp
extends|extends
name|FSNodeStoreLogOp
argument_list|<
name|CommonNodeLabelsManager
argument_list|>
block|{
DECL|field|labels
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
decl_stmt|;
DECL|field|OPCODE
specifier|public
specifier|static
specifier|final
name|int
name|OPCODE
init|=
literal|2
decl_stmt|;
annotation|@
name|Override
DECL|method|write (OutputStream os, CommonNodeLabelsManager mgr)
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|CommonNodeLabelsManager
name|mgr
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|RemoveFromClusterNodeLabelsRequestPBImpl
operator|)
name|RemoveFromClusterNodeLabelsRequest
operator|.
name|newInstance
argument_list|(
name|Sets
operator|.
name|newHashSet
argument_list|(
name|labels
operator|.
name|iterator
argument_list|()
argument_list|)
argument_list|)
operator|)
operator|.
name|getProto
argument_list|()
operator|.
name|writeDelimitedTo
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|recover (InputStream is, CommonNodeLabelsManager mgr)
specifier|public
name|void
name|recover
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|CommonNodeLabelsManager
name|mgr
parameter_list|)
throws|throws
name|IOException
block|{
name|labels
operator|=
name|YarnServerResourceManagerServiceProtos
operator|.
name|RemoveFromClusterNodeLabelsRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
operator|.
name|getNodeLabelsList
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|removeFromClusterNodeLabels
argument_list|(
name|labels
argument_list|)
expr_stmt|;
block|}
DECL|method|setLabels (Collection<String> labels)
specifier|public
name|RemoveClusterLabelOp
name|setLabels
parameter_list|(
name|Collection
argument_list|<
name|String
argument_list|>
name|labels
parameter_list|)
block|{
name|this
operator|.
name|labels
operator|=
name|labels
expr_stmt|;
return|return
name|this
return|;
block|}
DECL|method|getLabels ()
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getLabels
parameter_list|()
block|{
return|return
name|labels
return|;
block|}
annotation|@
name|Override
DECL|method|getOpCode ()
specifier|public
name|int
name|getOpCode
parameter_list|()
block|{
return|return
name|OPCODE
return|;
block|}
block|}
end_class

end_unit

