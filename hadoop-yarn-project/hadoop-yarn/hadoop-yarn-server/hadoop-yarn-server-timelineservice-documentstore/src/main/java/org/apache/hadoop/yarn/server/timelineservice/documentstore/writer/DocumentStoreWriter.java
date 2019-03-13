begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.writer
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
name|writer
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
name|server
operator|.
name|timelineservice
operator|.
name|documentstore
operator|.
name|collection
operator|.
name|CollectionType
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
name|timelineservice
operator|.
name|documentstore
operator|.
name|lib
operator|.
name|DocumentStoreVendor
import|;
end_import

begin_comment
comment|/**  * Every {@link DocumentStoreVendor} have to implement this for creating  * writer to its backend.  */
end_comment

begin_interface
DECL|interface|DocumentStoreWriter
specifier|public
interface|interface
name|DocumentStoreWriter
parameter_list|<
name|Document
parameter_list|>
extends|extends
name|AutoCloseable
block|{
DECL|method|createDatabase ()
name|void
name|createDatabase
parameter_list|()
function_decl|;
DECL|method|createCollection (String collectionName)
name|void
name|createCollection
parameter_list|(
name|String
name|collectionName
parameter_list|)
function_decl|;
DECL|method|writeDocument (Document document, CollectionType collectionType)
name|void
name|writeDocument
parameter_list|(
name|Document
name|document
parameter_list|,
name|CollectionType
name|collectionType
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

