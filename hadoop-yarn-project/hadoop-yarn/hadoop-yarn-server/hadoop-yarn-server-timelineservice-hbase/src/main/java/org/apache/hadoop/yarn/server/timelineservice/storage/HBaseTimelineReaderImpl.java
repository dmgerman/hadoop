begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.server.timelineservice.storage
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
name|storage
package|;
end_package

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
name|Set
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
name|hbase
operator|.
name|client
operator|.
name|Connection
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
name|hbase
operator|.
name|client
operator|.
name|ConnectionFactory
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
name|common
operator|.
name|HBaseTimelineStorageUtils
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
name|reader
operator|.
name|EntityTypeReader
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
name|reader
operator|.
name|TimelineEntityReader
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
name|reader
operator|.
name|TimelineEntityReaderFactory
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
comment|/**  * HBase based implementation for {@link TimelineReader}.  */
end_comment

begin_class
DECL|class|HBaseTimelineReaderImpl
specifier|public
class|class
name|HBaseTimelineReaderImpl
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
name|HBaseTimelineReaderImpl
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|hbaseConf
specifier|private
name|Configuration
name|hbaseConf
init|=
literal|null
decl_stmt|;
DECL|field|conn
specifier|private
name|Connection
name|conn
decl_stmt|;
DECL|method|HBaseTimelineReaderImpl ()
specifier|public
name|HBaseTimelineReaderImpl
parameter_list|()
block|{
name|super
argument_list|(
name|HBaseTimelineReaderImpl
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
name|super
operator|.
name|serviceInit
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|hbaseConf
operator|=
name|HBaseTimelineStorageUtils
operator|.
name|getTimelineServiceHBaseConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|conn
operator|=
name|ConnectionFactory
operator|.
name|createConnection
argument_list|(
name|hbaseConf
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|serviceStop ()
specifier|protected
name|void
name|serviceStop
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|conn
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"closing the hbase Connection"
argument_list|)
expr_stmt|;
name|conn
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|serviceStop
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
name|TimelineEntityReader
name|reader
init|=
name|TimelineEntityReaderFactory
operator|.
name|createSingleEntityReader
argument_list|(
name|context
argument_list|,
name|dataToRetrieve
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readEntity
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|TimelineEntityReader
name|reader
init|=
name|TimelineEntityReaderFactory
operator|.
name|createMultipleEntitiesReader
argument_list|(
name|context
argument_list|,
name|filters
argument_list|,
name|dataToRetrieve
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readEntities
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
return|;
block|}
annotation|@
name|Override
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
throws|throws
name|IOException
block|{
name|EntityTypeReader
name|reader
init|=
operator|new
name|EntityTypeReader
argument_list|(
name|context
argument_list|)
decl_stmt|;
return|return
name|reader
operator|.
name|readEntityTypes
argument_list|(
name|hbaseConf
argument_list|,
name|conn
argument_list|)
return|;
block|}
block|}
end_class

end_unit

