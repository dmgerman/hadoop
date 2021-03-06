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
name|NodeId
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
name|NodeLabel
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
name|ReplaceLabelsOnNodeRequest
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
name|AddToClusterNodeLabelsRequestPBImpl
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
name|ReplaceLabelsOnNodeRequestPBImpl
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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

begin_comment
comment|/**  * NodeLabel Mirror Op class.  */
end_comment

begin_class
DECL|class|NodeLabelMirrorOp
specifier|public
class|class
name|NodeLabelMirrorOp
extends|extends
name|FSNodeStoreLogOp
argument_list|<
name|CommonNodeLabelsManager
argument_list|>
block|{
DECL|method|NodeLabelMirrorOp ()
specifier|public
name|NodeLabelMirrorOp
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
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
name|AddToClusterNodeLabelsRequestPBImpl
operator|)
name|AddToClusterNodeLabelsRequestPBImpl
operator|.
name|newInstance
argument_list|(
name|mgr
operator|.
name|getClusterNodeLabels
argument_list|()
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
if|if
condition|(
name|mgr
operator|.
name|isCentralizedConfiguration
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ReplaceLabelsOnNodeRequestPBImpl
operator|)
name|ReplaceLabelsOnNodeRequest
operator|.
name|newInstance
argument_list|(
name|mgr
operator|.
name|getNodeLabels
argument_list|()
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
name|List
argument_list|<
name|NodeLabel
argument_list|>
name|labels
init|=
operator|new
name|AddToClusterNodeLabelsRequestPBImpl
argument_list|(
name|YarnServerResourceManagerServiceProtos
operator|.
name|AddToClusterNodeLabelsRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
operator|.
name|getNodeLabels
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addToCluserNodeLabels
argument_list|(
name|labels
argument_list|)
expr_stmt|;
if|if
condition|(
name|mgr
operator|.
name|isCentralizedConfiguration
argument_list|()
condition|)
block|{
comment|// Only load node to labels mapping while using centralized
comment|// configuration
name|Map
argument_list|<
name|NodeId
argument_list|,
name|Set
argument_list|<
name|String
argument_list|>
argument_list|>
name|nodeToLabels
init|=
operator|new
name|ReplaceLabelsOnNodeRequestPBImpl
argument_list|(
name|YarnServerResourceManagerServiceProtos
operator|.
name|ReplaceLabelsOnNodeRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
operator|.
name|getNodeToLabels
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|replaceLabelsOnNode
argument_list|(
name|nodeToLabels
argument_list|)
expr_stmt|;
block|}
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
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

