begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore
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
name|service
operator|.
name|AbstractService
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
name|api
operator|.
name|records
operator|.
name|timeline
operator|.
name|TimelineHealth
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntity
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
name|api
operator|.
name|records
operator|.
name|timelineservice
operator|.
name|TimelineEntityType
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
name|reader
operator|.
name|TimelineCollectionReader
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
name|reader
operator|.
name|TimelineDataToRetrieve
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
name|reader
operator|.
name|TimelineEntityFilters
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
name|reader
operator|.
name|TimelineReaderContext
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
name|storage
operator|.
name|TimelineReader
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Set
import|;
end_import

begin_comment
comment|/**  * This is a generic document store timeline reader for reading the timeline  * entity information. Based on the {@link DocumentStoreVendor} that is  * configured, the  documents are read from that backend.  */
end_comment

begin_class
DECL|class|DocumentStoreTimelineReaderImpl
specifier|public
class|class
name|DocumentStoreTimelineReaderImpl
extends|extends
name|AbstractService
implements|implements
name|TimelineReader
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
name|DocumentStoreTimelineReaderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|collectionReader
specifier|private
name|TimelineCollectionReader
name|collectionReader
decl_stmt|;
DECL|method|DocumentStoreTimelineReaderImpl ()
specifier|public
name|DocumentStoreTimelineReaderImpl
parameter_list|()
block|{
name|super
argument_list|(
name|DocumentStoreTimelineReaderImpl
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceInit (Configuration conf)
specifier|public
name|void
name|serviceInit
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|Exception
block|{
name|DocumentStoreVendor
name|storeType
init|=
name|DocumentStoreUtils
operator|.
name|getStoreVendor
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Initializing Document Store Reader for : "
operator|+
name|storeType
argument_list|)
expr_stmt|;
name|collectionReader
operator|=
operator|new
name|TimelineCollectionReader
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|public
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stopping Document Timeline Store reader..."
argument_list|)
expr_stmt|;
name|collectionReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getEntity (TimelineReaderContext context, TimelineDataToRetrieve dataToRetrieve)
specifier|public
name|TimelineEntity
name|getEntity
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|,
name|TimelineDataToRetrieve
name|dataToRetrieve
parameter_list|)
throws|throws
name|IOException
block|{
name|TimelineEntityDocument
name|timelineEntityDoc
decl_stmt|;
switch|switch
condition|(
name|TimelineEntityType
operator|.
name|valueOf
argument_list|(
name|context
operator|.
name|getEntityType
argument_list|()
argument_list|)
condition|)
block|{
case|case
name|YARN_FLOW_ACTIVITY
case|:
case|case
name|YARN_FLOW_RUN
case|:
name|timelineEntityDoc
operator|=
name|collectionReader
operator|.
name|readDocument
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|DocumentStoreUtils
operator|.
name|createEntityToBeReturned
argument_list|(
name|timelineEntityDoc
argument_list|,
name|dataToRetrieve
operator|.
name|getConfsToRetrieve
argument_list|()
argument_list|,
name|dataToRetrieve
operator|.
name|getMetricsToRetrieve
argument_list|()
argument_list|)
return|;
default|default:
name|timelineEntityDoc
operator|=
name|collectionReader
operator|.
name|readDocument
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
return|return
name|DocumentStoreUtils
operator|.
name|createEntityToBeReturned
argument_list|(
name|timelineEntityDoc
argument_list|,
name|dataToRetrieve
argument_list|)
return|;
block|}
DECL|method|getEntities (TimelineReaderContext context, TimelineEntityFilters filters, TimelineDataToRetrieve dataToRetrieve)
specifier|public
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|getEntities
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|,
name|TimelineEntityFilters
name|filters
parameter_list|,
name|TimelineDataToRetrieve
name|dataToRetrieve
parameter_list|)
throws|throws
name|IOException
block|{
name|List
argument_list|<
name|TimelineEntityDocument
argument_list|>
name|entityDocs
init|=
name|collectionReader
operator|.
name|readDocuments
argument_list|(
name|context
argument_list|,
name|filters
operator|.
name|getLimit
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|applyFilters
argument_list|(
name|filters
argument_list|,
name|dataToRetrieve
argument_list|,
name|entityDocs
argument_list|)
return|;
block|}
DECL|method|getEntityTypes (TimelineReaderContext context)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getEntityTypes
parameter_list|(
name|TimelineReaderContext
name|context
parameter_list|)
block|{
return|return
name|collectionReader
operator|.
name|fetchEntityTypes
argument_list|(
name|context
argument_list|)
return|;
block|}
annotation|@
name|Override
DECL|method|getHealthStatus ()
specifier|public
name|TimelineHealth
name|getHealthStatus
parameter_list|()
block|{
if|if
condition|(
name|collectionReader
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|TimelineHealth
argument_list|(
name|TimelineHealth
operator|.
name|TimelineHealthStatus
operator|.
name|RUNNING
argument_list|,
literal|""
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|TimelineHealth
argument_list|(
name|TimelineHealth
operator|.
name|TimelineHealthStatus
operator|.
name|READER_CONNECTION_FAILURE
argument_list|,
literal|"Timeline store reader not initialized."
argument_list|)
return|;
block|}
block|}
comment|// for honoring all filters from {@link TimelineEntityFilters}
DECL|method|applyFilters (TimelineEntityFilters filters, TimelineDataToRetrieve dataToRetrieve, List<TimelineEntityDocument> entityDocs)
specifier|private
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|applyFilters
parameter_list|(
name|TimelineEntityFilters
name|filters
parameter_list|,
name|TimelineDataToRetrieve
name|dataToRetrieve
parameter_list|,
name|List
argument_list|<
name|TimelineEntityDocument
argument_list|>
name|entityDocs
parameter_list|)
throws|throws
name|IOException
block|{
name|Set
argument_list|<
name|TimelineEntity
argument_list|>
name|timelineEntities
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|TimelineEntityDocument
name|entityDoc
range|:
name|entityDocs
control|)
block|{
specifier|final
name|TimelineEntity
name|timelineEntity
init|=
name|entityDoc
operator|.
name|fetchTimelineEntity
argument_list|()
decl_stmt|;
if|if
condition|(
name|DocumentStoreUtils
operator|.
name|isFilterNotMatching
argument_list|(
name|filters
argument_list|,
name|timelineEntity
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|TimelineEntity
name|entityToBeReturned
init|=
name|DocumentStoreUtils
operator|.
name|createEntityToBeReturned
argument_list|(
name|entityDoc
argument_list|,
name|dataToRetrieve
argument_list|)
decl_stmt|;
name|timelineEntities
operator|.
name|add
argument_list|(
name|entityToBeReturned
argument_list|)
expr_stmt|;
block|}
return|return
name|timelineEntities
return|;
block|}
block|}
end_class

end_unit

