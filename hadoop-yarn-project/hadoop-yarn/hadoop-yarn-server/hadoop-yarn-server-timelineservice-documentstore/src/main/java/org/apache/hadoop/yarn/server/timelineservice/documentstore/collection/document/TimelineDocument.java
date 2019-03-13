begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.collection.document
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|document
package|;
end_package

begin_comment
comment|/**  * This is an interface for all the Timeline Documents. Any new document that  * has to be persisted in the document store should implement this.  */
end_comment

begin_interface
DECL|interface|TimelineDocument
specifier|public
interface|interface
name|TimelineDocument
parameter_list|<
name|Document
parameter_list|>
block|{
DECL|method|getId ()
name|String
name|getId
parameter_list|()
function_decl|;
DECL|method|getType ()
name|String
name|getType
parameter_list|()
function_decl|;
DECL|method|getCreatedTime ()
name|long
name|getCreatedTime
parameter_list|()
function_decl|;
DECL|method|setCreatedTime (long time)
name|void
name|setCreatedTime
parameter_list|(
name|long
name|time
parameter_list|)
function_decl|;
DECL|method|merge (Document timelineDocument)
name|void
name|merge
parameter_list|(
name|Document
name|timelineDocument
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

