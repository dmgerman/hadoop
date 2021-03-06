begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.lib
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
name|lib
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
name|conf
operator|.
name|Configuration
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
name|exceptions
operator|.
name|YarnException
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
name|reader
operator|.
name|DocumentStoreReader
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
name|reader
operator|.
name|cosmosdb
operator|.
name|CosmosDBDocumentStoreReader
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
name|writer
operator|.
name|cosmosdb
operator|.
name|CosmosDBDocumentStoreWriter
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
name|DocumentStoreUtils
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
name|writer
operator|.
name|DocumentStoreWriter
import|;
end_import

begin_import
import|import static
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
name|DocumentStoreUtils
operator|.
name|getStoreVendor
import|;
end_import

begin_comment
comment|/**  * Factory methods for instantiating a timeline Document Store reader or  * writer. Based on the {@link DocumentStoreVendor} that is configured,  * appropriate reader or writer would be instantiated.  */
end_comment

begin_class
DECL|class|DocumentStoreFactory
specifier|public
specifier|final
class|class
name|DocumentStoreFactory
block|{
comment|// making factory class not instantiable
DECL|method|DocumentStoreFactory ()
specifier|private
name|DocumentStoreFactory
parameter_list|()
block|{   }
comment|/**    * Creates a DocumentStoreWriter for a {@link DocumentStoreVendor}.    * @param conf    *              for creating client connection    * @param<Document> type of Document for which the writer has to be created,    *                  i.e TimelineEntityDocument, FlowActivityDocument etc    * @return document store writer    * @throws DocumentStoreNotSupportedException if there is no implementation    *         for a configured {@link DocumentStoreVendor} or unknown    *         {@link DocumentStoreVendor} is configured.    * @throws YarnException if the required configs for DocumentStore is missing.    */
specifier|public
specifier|static
parameter_list|<
name|Document
extends|extends
name|TimelineDocument
parameter_list|>
DECL|method|createDocumentStoreWriter ( Configuration conf)
name|DocumentStoreWriter
argument_list|<
name|Document
argument_list|>
name|createDocumentStoreWriter
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
specifier|final
name|DocumentStoreVendor
name|storeType
init|=
name|getStoreVendor
argument_list|(
name|conf
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|storeType
condition|)
block|{
case|case
name|COSMOS_DB
case|:
name|DocumentStoreUtils
operator|.
name|validateCosmosDBConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
operator|new
name|CosmosDBDocumentStoreWriter
argument_list|<>
argument_list|(
name|conf
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|DocumentStoreNotSupportedException
argument_list|(
literal|"Unable to create DocumentStoreWriter for type : "
operator|+
name|storeType
argument_list|)
throw|;
block|}
block|}
comment|/**  * Creates a DocumentStoreReader for a {@link DocumentStoreVendor}.  * @param conf  *            for creating client connection  * @param<Document> type of Document for which the writer has to be created,  *                  i.e TimelineEntityDocument, FlowActivityDocument etc  * @return document store reader  * @throws DocumentStoreNotSupportedException if there is no implementation  *         for a configured {@link DocumentStoreVendor} or unknown  *         {@link DocumentStoreVendor} is configured.  * @throws YarnException if the required configs for DocumentStore is missing.  * */
specifier|public
specifier|static
parameter_list|<
name|Document
extends|extends
name|TimelineDocument
parameter_list|>
DECL|method|createDocumentStoreReader ( Configuration conf)
name|DocumentStoreReader
argument_list|<
name|Document
argument_list|>
name|createDocumentStoreReader
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|YarnException
block|{
specifier|final
name|DocumentStoreVendor
name|storeType
init|=
name|getStoreVendor
argument_list|(
name|conf
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|storeType
condition|)
block|{
case|case
name|COSMOS_DB
case|:
name|DocumentStoreUtils
operator|.
name|validateCosmosDBConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
return|return
operator|new
name|CosmosDBDocumentStoreReader
argument_list|<>
argument_list|(
name|conf
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|DocumentStoreNotSupportedException
argument_list|(
literal|"Unable to create DocumentStoreReader for type : "
operator|+
name|storeType
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

