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
name|store
operator|.
name|StoreOp
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
name|NodeToAttributes
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
name|HashMap
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
comment|/**  * Defines all FileSystem editlog operation. All node label and attribute  * store write or read operation will be defined in this class.  *  * @param<M> Manager used for each operation.  */
end_comment

begin_class
DECL|class|FSNodeStoreLogOp
specifier|public
specifier|abstract
class|class
name|FSNodeStoreLogOp
parameter_list|<
name|M
parameter_list|>
implements|implements
name|StoreOp
argument_list|<
name|OutputStream
argument_list|,
name|InputStream
argument_list|,
name|M
argument_list|>
block|{
DECL|method|getOpCode ()
specifier|public
specifier|abstract
name|int
name|getOpCode
parameter_list|()
function_decl|;
DECL|method|getNodeToAttributesMap ( NodesToAttributesMappingRequest request)
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|getNodeToAttributesMap
parameter_list|(
name|NodesToAttributesMappingRequest
name|request
parameter_list|)
block|{
name|List
argument_list|<
name|NodeToAttributes
argument_list|>
name|attributes
init|=
name|request
operator|.
name|getNodesToAttributes
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Set
argument_list|<
name|NodeAttribute
argument_list|>
argument_list|>
name|nodeToAttrMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|attributes
operator|.
name|forEach
argument_list|(
parameter_list|(
name|v
parameter_list|)
lambda|->
name|nodeToAttrMap
operator|.
name|put
argument_list|(
name|v
operator|.
name|getNode
argument_list|()
argument_list|,
operator|new
name|HashSet
argument_list|<>
argument_list|(
name|v
operator|.
name|getNodeAttributes
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|nodeToAttrMap
return|;
block|}
block|}
end_class

end_unit

