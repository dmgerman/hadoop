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
name|ImmutableSet
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
name|NodeAttribute
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
name|NodeAttributesManager
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
name|AttributeMappingOperationType
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
name|NodesToAttributesMappingRequest
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
name|NodesToAttributesMappingRequestPBImpl
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

begin_comment
comment|/**  * File System Node Attribute Mirror read and write operation.  */
end_comment

begin_class
DECL|class|NodeAttributeMirrorOp
specifier|public
class|class
name|NodeAttributeMirrorOp
extends|extends
name|FSNodeStoreLogOp
argument_list|<
name|NodeAttributesManager
argument_list|>
block|{
annotation|@
name|Override
DECL|method|write (OutputStream os, NodeAttributesManager mgr)
specifier|public
name|void
name|write
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|NodeAttributesManager
name|mgr
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|(
name|NodesToAttributesMappingRequestPBImpl
operator|)
name|NodesToAttributesMappingRequest
operator|.
name|newInstance
argument_list|(
name|AttributeMappingOperationType
operator|.
name|REPLACE
argument_list|,
name|mgr
operator|.
name|getNodeToAttributes
argument_list|(
name|ImmutableSet
operator|.
name|of
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_CENTRALIZED
argument_list|)
argument_list|)
argument_list|,
literal|false
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
DECL|method|recover (InputStream is, NodeAttributesManager mgr)
specifier|public
name|void
name|recover
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|NodeAttributesManager
name|mgr
parameter_list|)
throws|throws
name|IOException
block|{
name|NodesToAttributesMappingRequest
name|request
init|=
operator|new
name|NodesToAttributesMappingRequestPBImpl
argument_list|(
name|YarnServerResourceManagerServiceProtos
operator|.
name|NodesToAttributesMappingRequestProto
operator|.
name|parseDelimitedFrom
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|replaceNodeAttributes
argument_list|(
name|NodeAttribute
operator|.
name|PREFIX_CENTRALIZED
argument_list|,
name|getNodeToAttributesMap
argument_list|(
name|request
argument_list|)
argument_list|)
expr_stmt|;
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

