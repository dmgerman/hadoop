begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.slider.server.appmaster.web.rest.registry
package|package
name|org
operator|.
name|apache
operator|.
name|slider
operator|.
name|server
operator|.
name|appmaster
operator|.
name|web
operator|.
name|rest
operator|.
name|registry
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
name|registry
operator|.
name|client
operator|.
name|types
operator|.
name|ServiceRecord
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|annotate
operator|.
name|JsonIgnoreProperties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jackson
operator|.
name|map
operator|.
name|annotate
operator|.
name|JsonSerialize
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

begin_comment
comment|/**  * Representation of a path entry  */
end_comment

begin_class
annotation|@
name|JsonIgnoreProperties
argument_list|(
name|ignoreUnknown
operator|=
literal|true
argument_list|)
annotation|@
name|JsonSerialize
argument_list|(
name|include
operator|=
name|JsonSerialize
operator|.
name|Inclusion
operator|.
name|NON_NULL
argument_list|)
DECL|class|PathEntryResource
specifier|public
class|class
name|PathEntryResource
block|{
comment|/**    * Child nodes: as the short path to each element    */
DECL|field|nodes
specifier|public
name|List
argument_list|<
name|String
argument_list|>
name|nodes
decl_stmt|;
comment|/**    * Service record: if null âthere is no resolvable service    * record at this node.    */
DECL|field|service
specifier|public
name|ServiceRecord
name|service
decl_stmt|;
block|}
end_class

end_unit

