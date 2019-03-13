begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.documentstore.reader
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
name|reader
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
name|documentstore
operator|.
name|DocumentStoreTestUtils
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
name|ArrayList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Collectors
import|;
end_import

begin_comment
comment|/**  * Dummy Document Store Reader for mocking backend calls for unit test.  */
end_comment

begin_class
DECL|class|DummyDocumentStoreReader
specifier|public
class|class
name|DummyDocumentStoreReader
parameter_list|<
name|TimelineDoc
extends|extends
name|TimelineDocument
parameter_list|>
implements|implements
name|DocumentStoreReader
argument_list|<
name|TimelineDoc
argument_list|>
block|{
DECL|field|entityDoc
specifier|private
specifier|final
name|TimelineEntityDocument
name|entityDoc
decl_stmt|;
DECL|field|entityDocs
specifier|private
specifier|final
name|List
argument_list|<
name|TimelineEntityDocument
argument_list|>
name|entityDocs
decl_stmt|;
DECL|field|flowRunDoc
specifier|private
specifier|final
name|FlowRunDocument
name|flowRunDoc
decl_stmt|;
DECL|field|flowActivityDoc
specifier|private
specifier|final
name|FlowActivityDocument
name|flowActivityDoc
decl_stmt|;
DECL|method|DummyDocumentStoreReader ()
specifier|public
name|DummyDocumentStoreReader
parameter_list|()
block|{
try|try
block|{
name|entityDoc
operator|=
name|DocumentStoreTestUtils
operator|.
name|bakeTimelineEntityDoc
argument_list|()
expr_stmt|;
name|entityDocs
operator|=
name|DocumentStoreTestUtils
operator|.
name|bakeYarnAppTimelineEntities
argument_list|()
expr_stmt|;
name|flowRunDoc
operator|=
name|DocumentStoreTestUtils
operator|.
name|bakeFlowRunDoc
argument_list|()
expr_stmt|;
name|flowActivityDoc
operator|=
name|DocumentStoreTestUtils
operator|.
name|bakeFlowActivityDoc
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to create "
operator|+
literal|"DummyDocumentStoreReader : "
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readDocument (String collectionName, TimelineReaderContext context, Class<TimelineDoc> docClass)
specifier|public
name|TimelineDoc
name|readDocument
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|TimelineReaderContext
name|context
parameter_list|,
name|Class
argument_list|<
name|TimelineDoc
argument_list|>
name|docClass
parameter_list|)
block|{
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
return|return
operator|(
name|TimelineDoc
operator|)
name|flowActivityDoc
return|;
case|case
name|YARN_FLOW_RUN
case|:
return|return
operator|(
name|TimelineDoc
operator|)
name|flowRunDoc
return|;
default|default:
return|return
operator|(
name|TimelineDoc
operator|)
name|entityDoc
return|;
block|}
block|}
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
DECL|method|readDocumentList (String collectionName, TimelineReaderContext context, Class<TimelineDoc> docClass, long size)
specifier|public
name|List
argument_list|<
name|TimelineDoc
argument_list|>
name|readDocumentList
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|TimelineReaderContext
name|context
parameter_list|,
name|Class
argument_list|<
name|TimelineDoc
argument_list|>
name|docClass
parameter_list|,
name|long
name|size
parameter_list|)
block|{
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
name|List
argument_list|<
name|FlowActivityDocument
argument_list|>
name|flowActivityDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|flowActivityDocs
operator|.
name|add
argument_list|(
name|flowActivityDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
name|flowActivityDocs
operator|.
name|size
argument_list|()
condition|)
block|{
name|size
operator|=
name|flowActivityDocs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|List
argument_list|<
name|TimelineDoc
argument_list|>
operator|)
name|flowActivityDocs
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|size
argument_list|)
return|;
case|case
name|YARN_FLOW_RUN
case|:
name|List
argument_list|<
name|FlowRunDocument
argument_list|>
name|flowRunDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|flowRunDocs
operator|.
name|add
argument_list|(
name|flowRunDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
name|flowRunDocs
operator|.
name|size
argument_list|()
condition|)
block|{
name|size
operator|=
name|flowRunDocs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|List
argument_list|<
name|TimelineDoc
argument_list|>
operator|)
name|flowRunDocs
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|size
argument_list|)
return|;
case|case
name|YARN_APPLICATION
case|:
name|List
argument_list|<
name|TimelineEntityDocument
argument_list|>
name|applicationEntities
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|applicationEntities
operator|.
name|add
argument_list|(
name|entityDoc
argument_list|)
expr_stmt|;
if|if
condition|(
name|size
operator|>
name|applicationEntities
operator|.
name|size
argument_list|()
condition|)
block|{
name|size
operator|=
name|applicationEntities
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|List
argument_list|<
name|TimelineDoc
argument_list|>
operator|)
name|applicationEntities
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|size
argument_list|)
return|;
default|default:
if|if
condition|(
name|size
operator|>
name|entityDocs
operator|.
name|size
argument_list|()
operator|||
name|size
operator|==
operator|-
literal|1
condition|)
block|{
name|size
operator|=
name|entityDocs
operator|.
name|size
argument_list|()
expr_stmt|;
block|}
return|return
operator|(
name|List
argument_list|<
name|TimelineDoc
argument_list|>
operator|)
name|entityDocs
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
operator|(
name|int
operator|)
name|size
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
DECL|method|fetchEntityTypes (String collectionName, TimelineReaderContext context)
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|fetchEntityTypes
parameter_list|(
name|String
name|collectionName
parameter_list|,
name|TimelineReaderContext
name|context
parameter_list|)
block|{
return|return
name|entityDocs
operator|.
name|stream
argument_list|()
operator|.
name|map
argument_list|(
name|TimelineEntityDocument
operator|::
name|getType
argument_list|)
operator|.
name|collect
argument_list|(
name|Collectors
operator|.
name|toSet
argument_list|()
argument_list|)
return|;
block|}
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

