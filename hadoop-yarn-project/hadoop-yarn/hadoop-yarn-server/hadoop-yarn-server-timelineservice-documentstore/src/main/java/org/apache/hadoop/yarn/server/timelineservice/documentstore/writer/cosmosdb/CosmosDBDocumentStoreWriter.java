begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.writer.cosmosdb
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
operator|.
name|cosmosdb
package|;
end_package

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|AccessCondition
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|AccessConditionType
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|DocumentClient
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|DocumentClientException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|DocumentCollection
import|;
end_import

begin_import
import|import
name|com
operator|.
name|microsoft
operator|.
name|azure
operator|.
name|documentdb
operator|.
name|RequestOptions
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
name|util
operator|.
name|Time
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
name|metrics
operator|.
name|PerNodeAggTimelineCollectorMetrics
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
name|entity
operator|.
name|TimelineEntityDocument
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
name|flowactivity
operator|.
name|FlowActivityDocument
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
name|flowrun
operator|.
name|FlowRunDocument
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
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_comment
comment|/**  * This is the Document Store Writer implementation for  * {@link DocumentStoreVendor#COSMOS_DB}.  */
end_comment

begin_class
DECL|class|CosmosDBDocumentStoreWriter
specifier|public
class|class
name|CosmosDBDocumentStoreWriter
parameter_list|<
name|TimelineDoc
extends|extends
name|TimelineDocument
parameter_list|>
implements|implements
name|DocumentStoreWriter
argument_list|<
name|TimelineDoc
argument_list|>
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|CosmosDBDocumentStoreWriter
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|client
specifier|private
specifier|static
name|DocumentClient
name|client
decl_stmt|;
DECL|field|databaseName
specifier|private
specifier|final
name|String
name|databaseName
decl_stmt|;
DECL|field|METRICS
specifier|private
specifier|static
specifier|final
name|PerNodeAggTimelineCollectorMetrics
name|METRICS
init|=
name|PerNodeAggTimelineCollectorMetrics
operator|.
name|getInstance
argument_list|()
decl_stmt|;
DECL|field|DATABASE_LINK
specifier|private
specifier|static
specifier|final
name|String
name|DATABASE_LINK
init|=
literal|"/dbs/%s"
decl_stmt|;
DECL|field|COLLECTION_LINK
specifier|private
specifier|static
specifier|final
name|String
name|COLLECTION_LINK
init|=
name|DATABASE_LINK
operator|+
literal|"/colls/%s"
decl_stmt|;
DECL|field|DOCUMENT_LINK
specifier|private
specifier|static
specifier|final
name|String
name|DOCUMENT_LINK
init|=
name|COLLECTION_LINK
operator|+
literal|"/docs/%s"
decl_stmt|;
DECL|method|CosmosDBDocumentStoreWriter (Configuration conf)
specifier|public
name|CosmosDBDocumentStoreWriter
parameter_list|(
name|Configuration
name|conf
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Cosmos DB DocumentStoreWriter..."
argument_list|)
expr_stmt|;
name|databaseName
operator|=
name|DocumentStoreUtils
operator|.
name|getCosmosDBDatabaseName
argument_list|(
name|conf
argument_list|)
expr_stmt|;
comment|// making CosmosDB Client Singleton
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Cosmos DB Client..."
argument_list|)
expr_stmt|;
name|client
operator|=
name|DocumentStoreUtils
operator|.
name|createCosmosDBClient
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createDatabase ()
specifier|public
name|void
name|createDatabase
parameter_list|()
block|{
try|try
block|{
name|client
operator|.
name|readDatabase
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|DATABASE_LINK
argument_list|,
name|databaseName
argument_list|)
argument_list|,
operator|new
name|RequestOptions
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Database {} already exists."
argument_list|,
name|databaseName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentClientException
name|docExceptionOnRead
parameter_list|)
block|{
if|if
condition|(
name|docExceptionOnRead
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|404
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating new Database : {}"
argument_list|,
name|databaseName
argument_list|)
expr_stmt|;
name|Database
name|databaseDefinition
init|=
operator|new
name|Database
argument_list|()
decl_stmt|;
name|databaseDefinition
operator|.
name|setId
argument_list|(
name|databaseName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|createDatabase
argument_list|(
name|databaseDefinition
argument_list|,
operator|new
name|RequestOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentClientException
name|docExceptionOnCreate
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create new Database : {}"
argument_list|,
name|databaseName
argument_list|,
name|docExceptionOnCreate
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while reading Database : {}"
argument_list|,
name|databaseName
argument_list|,
name|docExceptionOnRead
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|createCollection (final String collectionName)
specifier|public
name|void
name|createCollection
parameter_list|(
specifier|final
name|String
name|collectionName
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating Timeline Collection : {} for Database : {}"
argument_list|,
name|collectionName
argument_list|,
name|databaseName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|readCollection
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|COLLECTION_LINK
argument_list|,
name|databaseName
argument_list|,
name|collectionName
argument_list|)
argument_list|,
operator|new
name|RequestOptions
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Collection {} already exists."
argument_list|,
name|collectionName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentClientException
name|docExceptionOnRead
parameter_list|)
block|{
if|if
condition|(
name|docExceptionOnRead
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|404
condition|)
block|{
name|DocumentCollection
name|collection
init|=
operator|new
name|DocumentCollection
argument_list|()
decl_stmt|;
name|collection
operator|.
name|setId
argument_list|(
name|collectionName
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating collection {} under Database {}"
argument_list|,
name|collectionName
argument_list|,
name|databaseName
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|createCollection
argument_list|(
name|String
operator|.
name|format
argument_list|(
name|DATABASE_LINK
argument_list|,
name|databaseName
argument_list|)
argument_list|,
name|collection
argument_list|,
operator|new
name|RequestOptions
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentClientException
name|docExceptionOnCreate
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to create Collection : {} under Database : {}"
argument_list|,
name|collectionName
argument_list|,
name|databaseName
argument_list|,
name|docExceptionOnCreate
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while reading Collection : {} under Database : {}"
argument_list|,
name|collectionName
argument_list|,
name|databaseName
argument_list|,
name|docExceptionOnRead
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
DECL|method|writeDocument (final TimelineDoc timelineDoc, final CollectionType collectionType)
specifier|public
name|void
name|writeDocument
parameter_list|(
specifier|final
name|TimelineDoc
name|timelineDoc
parameter_list|,
specifier|final
name|CollectionType
name|collectionType
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Upserting document under collection : {} with  entity type : "
operator|+
literal|"{} under Database {}"
argument_list|,
name|databaseName
argument_list|,
name|timelineDoc
operator|.
name|getType
argument_list|()
argument_list|,
name|collectionType
operator|.
name|getCollectionName
argument_list|()
argument_list|)
expr_stmt|;
name|boolean
name|succeeded
init|=
literal|false
decl_stmt|;
name|long
name|startTime
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
decl_stmt|;
try|try
block|{
name|upsertDocument
argument_list|(
name|collectionType
argument_list|,
name|timelineDoc
argument_list|)
expr_stmt|;
name|succeeded
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to perform upsert for Document Id : {} under "
operator|+
literal|"Collection : {} under Database {}"
argument_list|,
name|timelineDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|collectionType
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|databaseName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|long
name|latency
init|=
name|Time
operator|.
name|monotonicNow
argument_list|()
operator|-
name|startTime
decl_stmt|;
name|METRICS
operator|.
name|addPutEntitiesLatency
argument_list|(
name|latency
argument_list|,
name|succeeded
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|upsertDocument (final CollectionType collectionType, final TimelineDoc timelineDoc)
specifier|private
name|void
name|upsertDocument
parameter_list|(
specifier|final
name|CollectionType
name|collectionType
parameter_list|,
specifier|final
name|TimelineDoc
name|timelineDoc
parameter_list|)
block|{
specifier|final
name|String
name|collectionLink
init|=
name|String
operator|.
name|format
argument_list|(
name|COLLECTION_LINK
argument_list|,
name|databaseName
argument_list|,
name|collectionType
operator|.
name|getCollectionName
argument_list|()
argument_list|)
decl_stmt|;
name|RequestOptions
name|requestOptions
init|=
operator|new
name|RequestOptions
argument_list|()
decl_stmt|;
name|AccessCondition
name|accessCondition
init|=
operator|new
name|AccessCondition
argument_list|()
decl_stmt|;
name|StringBuilder
name|eTagStrBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|TimelineDoc
name|updatedTimelineDoc
init|=
name|applyUpdatesOnPrevDoc
argument_list|(
name|collectionType
argument_list|,
name|timelineDoc
argument_list|,
name|eTagStrBuilder
argument_list|)
decl_stmt|;
name|accessCondition
operator|.
name|setCondition
argument_list|(
name|eTagStrBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|accessCondition
operator|.
name|setType
argument_list|(
name|AccessConditionType
operator|.
name|IfMatch
argument_list|)
expr_stmt|;
name|requestOptions
operator|.
name|setAccessCondition
argument_list|(
name|accessCondition
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|upsertDocument
argument_list|(
name|collectionLink
argument_list|,
name|updatedTimelineDoc
argument_list|,
name|requestOptions
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Successfully wrote doc with id : {} and type : {} under "
operator|+
literal|"Database : {}"
argument_list|,
name|timelineDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|timelineDoc
operator|.
name|getType
argument_list|()
argument_list|,
name|databaseName
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DocumentClientException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getStatusCode
argument_list|()
operator|==
literal|409
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"There was a conflict while upserting, hence retrying..."
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|upsertDocument
argument_list|(
name|collectionType
argument_list|,
name|updatedTimelineDoc
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"Error while upserting Collection : {} with Doc Id : {} under"
operator|+
literal|" Database : {}"
argument_list|,
name|collectionType
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|updatedTimelineDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|databaseName
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|applyUpdatesOnPrevDoc (CollectionType collectionType, TimelineDoc timelineDoc, StringBuilder eTagStrBuilder)
specifier|private
name|TimelineDoc
name|applyUpdatesOnPrevDoc
parameter_list|(
name|CollectionType
name|collectionType
parameter_list|,
name|TimelineDoc
name|timelineDoc
parameter_list|,
name|StringBuilder
name|eTagStrBuilder
parameter_list|)
block|{
name|TimelineDoc
name|prevDocument
init|=
name|fetchLatestDoc
argument_list|(
name|collectionType
argument_list|,
name|timelineDoc
operator|.
name|getId
argument_list|()
argument_list|,
name|eTagStrBuilder
argument_list|)
decl_stmt|;
if|if
condition|(
name|prevDocument
operator|!=
literal|null
condition|)
block|{
name|prevDocument
operator|.
name|merge
argument_list|(
name|timelineDoc
argument_list|)
expr_stmt|;
name|timelineDoc
operator|=
name|prevDocument
expr_stmt|;
block|}
return|return
name|timelineDoc
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|fetchLatestDoc (final CollectionType collectionType, final String documentId, StringBuilder eTagStrBuilder)
specifier|private
name|TimelineDoc
name|fetchLatestDoc
parameter_list|(
specifier|final
name|CollectionType
name|collectionType
parameter_list|,
specifier|final
name|String
name|documentId
parameter_list|,
name|StringBuilder
name|eTagStrBuilder
parameter_list|)
block|{
specifier|final
name|String
name|documentLink
init|=
name|String
operator|.
name|format
argument_list|(
name|DOCUMENT_LINK
argument_list|,
name|databaseName
argument_list|,
name|collectionType
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|documentId
argument_list|)
decl_stmt|;
try|try
block|{
name|Document
name|latestDocument
init|=
name|client
operator|.
name|readDocument
argument_list|(
name|documentLink
argument_list|,
operator|new
name|RequestOptions
argument_list|()
argument_list|)
operator|.
name|getResource
argument_list|()
decl_stmt|;
name|TimelineDoc
name|timelineDoc
decl_stmt|;
switch|switch
condition|(
name|collectionType
condition|)
block|{
case|case
name|FLOW_RUN
case|:
name|timelineDoc
operator|=
operator|(
name|TimelineDoc
operator|)
name|latestDocument
operator|.
name|toObject
argument_list|(
name|FlowRunDocument
operator|.
name|class
argument_list|)
expr_stmt|;
break|break;
case|case
name|FLOW_ACTIVITY
case|:
name|timelineDoc
operator|=
operator|(
name|TimelineDoc
operator|)
name|latestDocument
operator|.
name|toObject
argument_list|(
name|FlowActivityDocument
operator|.
name|class
argument_list|)
expr_stmt|;
break|break;
default|default:
name|timelineDoc
operator|=
operator|(
name|TimelineDoc
operator|)
name|latestDocument
operator|.
name|toObject
argument_list|(
name|TimelineEntityDocument
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
name|eTagStrBuilder
operator|.
name|append
argument_list|(
name|latestDocument
operator|.
name|getETag
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|timelineDoc
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No previous Document found with id : {} for Collection"
operator|+
literal|" : {} under Database : {}"
argument_list|,
name|documentId
argument_list|,
name|collectionType
operator|.
name|getCollectionName
argument_list|()
argument_list|,
name|databaseName
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|close ()
specifier|public
specifier|synchronized
name|void
name|close
parameter_list|()
block|{
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Closing Cosmos DB Client..."
argument_list|)
expr_stmt|;
name|client
operator|.
name|close
argument_list|()
expr_stmt|;
name|client
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

