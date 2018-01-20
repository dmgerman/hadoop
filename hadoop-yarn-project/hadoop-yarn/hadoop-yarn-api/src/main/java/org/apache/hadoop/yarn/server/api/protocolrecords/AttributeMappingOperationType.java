begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.api.protocolrecords
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
name|api
operator|.
name|protocolrecords
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

begin_comment
comment|/**  *<p>  * Type of node to attribute mapping operation.  *</p>  *  */
end_comment

begin_enum
annotation|@
name|Public
annotation|@
name|Unstable
DECL|enum|AttributeMappingOperationType
specifier|public
enum|enum
name|AttributeMappingOperationType
block|{
comment|/** Replaces the existing node to attribute mapping with new mapping.*/
DECL|enumConstant|REPLACE
name|REPLACE
block|,
comment|/** Add attribute(s) to a node and if it already exists will update the    *  value.*/
DECL|enumConstant|ADD
name|ADD
block|,
comment|/** Removes attribute(s) mapped to a node. */
DECL|enumConstant|REMOVE
name|REMOVE
block|}
end_enum

end_unit

