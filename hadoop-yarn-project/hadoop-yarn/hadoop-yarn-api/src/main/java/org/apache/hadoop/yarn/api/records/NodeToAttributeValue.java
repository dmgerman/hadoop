begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.api.records
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
name|records
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
name|Public
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
name|util
operator|.
name|Records
import|;
end_import

begin_comment
comment|/**  *<p>  * Mapping of Attribute Value to a Node.  *</p>  */
end_comment

begin_class
annotation|@
name|Public
annotation|@
name|Unstable
DECL|class|NodeToAttributeValue
specifier|public
specifier|abstract
class|class
name|NodeToAttributeValue
block|{
DECL|method|newInstance (String hostname, String attributeValue)
specifier|public
specifier|static
name|NodeToAttributeValue
name|newInstance
parameter_list|(
name|String
name|hostname
parameter_list|,
name|String
name|attributeValue
parameter_list|)
block|{
name|NodeToAttributeValue
name|nodeToAttributeValue
init|=
name|Records
operator|.
name|newRecord
argument_list|(
name|NodeToAttributeValue
operator|.
name|class
argument_list|)
decl_stmt|;
name|nodeToAttributeValue
operator|.
name|setAttributeValue
argument_list|(
name|attributeValue
argument_list|)
expr_stmt|;
name|nodeToAttributeValue
operator|.
name|setHostname
argument_list|(
name|hostname
argument_list|)
expr_stmt|;
return|return
name|nodeToAttributeValue
return|;
block|}
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getAttributeValue ()
specifier|public
specifier|abstract
name|String
name|getAttributeValue
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setAttributeValue (String attributeValue)
specifier|public
specifier|abstract
name|void
name|setAttributeValue
parameter_list|(
name|String
name|attributeValue
parameter_list|)
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|getHostname ()
specifier|public
specifier|abstract
name|String
name|getHostname
parameter_list|()
function_decl|;
annotation|@
name|Public
annotation|@
name|Unstable
DECL|method|setHostname (String hostname)
specifier|public
specifier|abstract
name|void
name|setHostname
parameter_list|(
name|String
name|hostname
parameter_list|)
function_decl|;
block|}
end_class

end_unit

