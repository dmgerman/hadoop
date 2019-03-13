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
name|collection
operator|.
name|document
operator|.
name|TimelineDocument
import|;
end_import

begin_comment
comment|/**  * Dummy Document Store Writer for mocking backend calls for unit test.  */
end_comment

begin_class
DECL|class|DummyDocumentStoreWriter
specifier|public
class|class
name|DummyDocumentStoreWriter
parameter_list|<
name|Document
extends|extends
name|TimelineDocument
parameter_list|>
implements|implements
name|DocumentStoreWriter
argument_list|<
name|Document
argument_list|>
block|{
annotation|@
name|Override
DECL|method|createDatabase ()
specifier|public
name|void
name|createDatabase
parameter_list|()
block|{   }
annotation|@
name|Override
DECL|method|createCollection (String collectionName)
specifier|public
name|void
name|createCollection
parameter_list|(
name|String
name|collectionName
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|writeDocument (Document timelineDocument, CollectionType collectionType)
specifier|public
name|void
name|writeDocument
parameter_list|(
name|Document
name|timelineDocument
parameter_list|,
name|CollectionType
name|collectionType
parameter_list|)
block|{   }
annotation|@
name|Override
DECL|method|close ()
specifier|public
name|void
name|close
parameter_list|()
block|{   }
block|}
end_class

end_unit

