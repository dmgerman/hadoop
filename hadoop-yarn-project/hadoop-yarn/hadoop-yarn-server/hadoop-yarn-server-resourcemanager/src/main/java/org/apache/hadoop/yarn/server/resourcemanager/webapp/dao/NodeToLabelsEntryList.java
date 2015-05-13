begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.resourcemanager.webapp.dao
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
name|resourcemanager
operator|.
name|webapp
operator|.
name|dao
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"nodeToLabelsName"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
DECL|class|NodeToLabelsEntryList
specifier|public
class|class
name|NodeToLabelsEntryList
block|{
DECL|field|nodeToLabels
specifier|protected
name|ArrayList
argument_list|<
name|NodeToLabelsEntry
argument_list|>
name|nodeToLabels
init|=
operator|new
name|ArrayList
argument_list|<
name|NodeToLabelsEntry
argument_list|>
argument_list|()
decl_stmt|;
DECL|method|NodeToLabelsEntryList ()
specifier|public
name|NodeToLabelsEntryList
parameter_list|()
block|{
comment|// JAXB needs this
block|}
DECL|method|getNodeToLabels ()
specifier|public
name|ArrayList
argument_list|<
name|NodeToLabelsEntry
argument_list|>
name|getNodeToLabels
parameter_list|()
block|{
return|return
name|nodeToLabels
return|;
block|}
block|}
end_class

end_unit

