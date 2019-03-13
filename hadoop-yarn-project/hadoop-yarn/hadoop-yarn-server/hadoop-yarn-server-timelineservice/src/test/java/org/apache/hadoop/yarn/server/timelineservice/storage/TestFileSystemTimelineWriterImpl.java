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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|io
operator|.
name|InputStreamReader
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
name|HashMap
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
name|Map
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
name|fs
operator|.
name|FileStatus
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
name|fs
operator|.
name|FileSystem
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
name|fs
operator|.
name|Path
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
name|security
operator|.
name|UserGroupInformation
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
name|TimelineEntities
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
name|TimelineMetric
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
name|TimelineMetricOperation
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
name|conf
operator|.
name|YarnConfiguration
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
name|collector
operator|.
name|TimelineCollectorContext
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
name|util
operator|.
name|timeline
operator|.
name|TimelineUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_class
DECL|class|TestFileSystemTimelineWriterImpl
specifier|public
class|class
name|TestFileSystemTimelineWriterImpl
block|{
annotation|@
name|Rule
DECL|field|tmpFolder
specifier|public
name|TemporaryFolder
name|tmpFolder
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
comment|/**    * Unit test for PoC YARN 3264.    *    * @throws Exception    */
annotation|@
name|Test
DECL|method|testWriteEntityToFile ()
specifier|public
name|void
name|testWriteEntityToFile
parameter_list|()
throws|throws
name|Exception
block|{
name|TimelineEntities
name|te
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|String
name|id
init|=
literal|"hello"
decl_stmt|;
name|String
name|type
init|=
literal|"world"
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
literal|1425016501000L
argument_list|)
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|TimelineMetric
name|metric
init|=
operator|new
name|TimelineMetric
argument_list|()
decl_stmt|;
name|String
name|metricId
init|=
literal|"CPU"
decl_stmt|;
name|metric
operator|.
name|setId
argument_list|(
name|metricId
argument_list|)
expr_stmt|;
name|metric
operator|.
name|setType
argument_list|(
name|TimelineMetric
operator|.
name|Type
operator|.
name|SINGLE_VALUE
argument_list|)
expr_stmt|;
name|metric
operator|.
name|setRealtimeAggregationOp
argument_list|(
name|TimelineMetricOperation
operator|.
name|SUM
argument_list|)
expr_stmt|;
name|metric
operator|.
name|addValue
argument_list|(
literal|1425016501000L
argument_list|,
literal|1234567L
argument_list|)
expr_stmt|;
name|TimelineEntity
name|entity2
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|String
name|id2
init|=
literal|"metric"
decl_stmt|;
name|String
name|type2
init|=
literal|"app"
decl_stmt|;
name|entity2
operator|.
name|setId
argument_list|(
name|id2
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|setType
argument_list|(
name|type2
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|setCreatedTime
argument_list|(
literal|1425016503000L
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|addMetric
argument_list|(
name|metric
argument_list|)
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entity2
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|TimelineMetric
argument_list|>
name|aggregatedMetrics
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|TimelineMetric
argument_list|>
argument_list|()
decl_stmt|;
name|aggregatedMetrics
operator|.
name|put
argument_list|(
name|metricId
argument_list|,
name|metric
argument_list|)
expr_stmt|;
name|FileSystemTimelineWriterImpl
name|fsi
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fsi
operator|=
operator|new
name|FileSystemTimelineWriterImpl
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|outputRoot
init|=
name|tmpFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|outputRoot
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|start
argument_list|()
expr_stmt|;
name|fsi
operator|.
name|write
argument_list|(
operator|new
name|TimelineCollectorContext
argument_list|(
literal|"cluster_id"
argument_list|,
literal|"user_id"
argument_list|,
literal|"flow_name"
argument_list|,
literal|"flow_version"
argument_list|,
literal|12345678L
argument_list|,
literal|"app_id"
argument_list|)
argument_list|,
name|te
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"user_id"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|outputRoot
operator|+
name|File
operator|.
name|separator
operator|+
literal|"entities"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"cluster_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"user_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_name"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_version"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"12345678"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"app_id"
operator|+
name|File
operator|.
name|separator
operator|+
name|type
operator|+
name|File
operator|.
name|separator
operator|+
name|id
operator|+
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_EXTENSION
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path("
operator|+
name|fileName
operator|+
literal|") should exist: "
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path should be a file"
argument_list|,
operator|!
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|data
init|=
name|readFromFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
comment|// ensure there's only one entity + 1 new line
name|assertTrue
argument_list|(
literal|"data size is:"
operator|+
name|data
operator|.
name|size
argument_list|()
argument_list|,
name|data
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|String
name|d
init|=
name|data
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// confirm the contents same as what was written
name|assertEquals
argument_list|(
name|d
argument_list|,
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
comment|// verify aggregated metrics
name|String
name|fileName2
init|=
name|fsi
operator|.
name|getOutputRoot
argument_list|()
operator|+
name|File
operator|.
name|separator
operator|+
literal|"entities"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"cluster_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"user_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_name"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_version"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"12345678"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"app_id"
operator|+
name|File
operator|.
name|separator
operator|+
name|type2
operator|+
name|File
operator|.
name|separator
operator|+
name|id2
operator|+
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_EXTENSION
decl_stmt|;
name|Path
name|path2
init|=
operator|new
name|Path
argument_list|(
name|fileName2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path("
operator|+
name|fileName
operator|+
literal|") should exist: "
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|path2
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus2
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path2
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path should be a file"
argument_list|,
operator|!
name|fileStatus2
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|data2
init|=
name|readFromFile
argument_list|(
name|fs
argument_list|,
name|path2
argument_list|)
decl_stmt|;
comment|// ensure there's only one entity + 1 new line
name|assertTrue
argument_list|(
literal|"data size is:"
operator|+
name|data2
operator|.
name|size
argument_list|()
argument_list|,
name|data2
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|String
name|metricToString
init|=
name|data2
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// confirm the contents same as what was written
name|assertEquals
argument_list|(
name|metricToString
argument_list|,
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fsi
operator|!=
literal|null
condition|)
block|{
name|fsi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteMultipleEntities ()
specifier|public
name|void
name|testWriteMultipleEntities
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
literal|"appId"
decl_stmt|;
name|String
name|type
init|=
literal|"app"
decl_stmt|;
name|TimelineEntities
name|te1
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
literal|1425016501000L
argument_list|)
expr_stmt|;
name|te1
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|TimelineEntities
name|te2
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entity2
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity2
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|entity2
operator|.
name|setCreatedTime
argument_list|(
literal|1425016503000L
argument_list|)
expr_stmt|;
name|te2
operator|.
name|addEntity
argument_list|(
name|entity2
argument_list|)
expr_stmt|;
name|FileSystemTimelineWriterImpl
name|fsi
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fsi
operator|=
operator|new
name|FileSystemTimelineWriterImpl
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|outputRoot
init|=
name|tmpFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|outputRoot
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|start
argument_list|()
expr_stmt|;
name|fsi
operator|.
name|write
argument_list|(
operator|new
name|TimelineCollectorContext
argument_list|(
literal|"cluster_id"
argument_list|,
literal|"user_id"
argument_list|,
literal|"flow_name"
argument_list|,
literal|"flow_version"
argument_list|,
literal|12345678L
argument_list|,
literal|"app_id"
argument_list|)
argument_list|,
name|te1
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"user_id"
argument_list|)
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|write
argument_list|(
operator|new
name|TimelineCollectorContext
argument_list|(
literal|"cluster_id"
argument_list|,
literal|"user_id"
argument_list|,
literal|"flow_name"
argument_list|,
literal|"flow_version"
argument_list|,
literal|12345678L
argument_list|,
literal|"app_id"
argument_list|)
argument_list|,
name|te2
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"user_id"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|outputRoot
operator|+
name|File
operator|.
name|separator
operator|+
literal|"entities"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"cluster_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"user_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_name"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_version"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"12345678"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"app_id"
operator|+
name|File
operator|.
name|separator
operator|+
name|type
operator|+
name|File
operator|.
name|separator
operator|+
name|id
operator|+
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_EXTENSION
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path("
operator|+
name|fileName
operator|+
literal|") should exist: "
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path should be a file"
argument_list|,
operator|!
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|data
init|=
name|readFromFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"data size is:"
operator|+
name|data
operator|.
name|size
argument_list|()
argument_list|,
name|data
operator|.
name|size
argument_list|()
operator|==
literal|3
argument_list|)
expr_stmt|;
name|String
name|d
init|=
name|data
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// confirm the contents same as what was written
name|assertEquals
argument_list|(
name|d
argument_list|,
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|metricToString
init|=
name|data
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
comment|// confirm the contents same as what was written
name|assertEquals
argument_list|(
name|metricToString
argument_list|,
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fsi
operator|!=
literal|null
condition|)
block|{
name|fsi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testWriteEntitiesWithEmptyFlowName ()
specifier|public
name|void
name|testWriteEntitiesWithEmptyFlowName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|id
init|=
literal|"appId"
decl_stmt|;
name|String
name|type
init|=
literal|"app"
decl_stmt|;
name|TimelineEntities
name|te
init|=
operator|new
name|TimelineEntities
argument_list|()
decl_stmt|;
name|TimelineEntity
name|entity
init|=
operator|new
name|TimelineEntity
argument_list|()
decl_stmt|;
name|entity
operator|.
name|setId
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|entity
operator|.
name|setCreatedTime
argument_list|(
literal|1425016501000L
argument_list|)
expr_stmt|;
name|te
operator|.
name|addEntity
argument_list|(
name|entity
argument_list|)
expr_stmt|;
name|FileSystemTimelineWriterImpl
name|fsi
init|=
literal|null
decl_stmt|;
try|try
block|{
name|fsi
operator|=
operator|new
name|FileSystemTimelineWriterImpl
argument_list|()
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
name|String
name|outputRoot
init|=
name|tmpFolder
operator|.
name|newFolder
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_DIR_ROOT
argument_list|,
name|outputRoot
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|fsi
operator|.
name|start
argument_list|()
expr_stmt|;
name|fsi
operator|.
name|write
argument_list|(
operator|new
name|TimelineCollectorContext
argument_list|(
literal|"cluster_id"
argument_list|,
literal|"user_id"
argument_list|,
literal|""
argument_list|,
literal|"flow_version"
argument_list|,
literal|12345678L
argument_list|,
literal|"app_id"
argument_list|)
argument_list|,
name|te
argument_list|,
name|UserGroupInformation
operator|.
name|createRemoteUser
argument_list|(
literal|"user_id"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|outputRoot
operator|+
name|File
operator|.
name|separator
operator|+
literal|"entities"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"cluster_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"user_id"
operator|+
name|File
operator|.
name|separator
operator|+
literal|""
operator|+
name|File
operator|.
name|separator
operator|+
literal|"flow_version"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"12345678"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"app_id"
operator|+
name|File
operator|.
name|separator
operator|+
name|type
operator|+
name|File
operator|.
name|separator
operator|+
name|id
operator|+
name|FileSystemTimelineWriterImpl
operator|.
name|TIMELINE_SERVICE_STORAGE_EXTENSION
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path("
operator|+
name|fileName
operator|+
literal|") should exist: "
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
name|fileStatus
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Specified path should be a file"
argument_list|,
operator|!
name|fileStatus
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|data
init|=
name|readFromFile
argument_list|(
name|fs
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"data size is:"
operator|+
name|data
operator|.
name|size
argument_list|()
argument_list|,
name|data
operator|.
name|size
argument_list|()
operator|==
literal|2
argument_list|)
expr_stmt|;
name|String
name|d
init|=
name|data
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// confirm the contents same as what was written
name|assertEquals
argument_list|(
name|d
argument_list|,
name|TimelineUtils
operator|.
name|dumpTimelineRecordtoJSON
argument_list|(
name|entity
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|fsi
operator|!=
literal|null
condition|)
block|{
name|fsi
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|readFromFile (FileSystem fs, Path path)
specifier|private
name|List
argument_list|<
name|String
argument_list|>
name|readFromFile
parameter_list|(
name|FileSystem
name|fs
parameter_list|,
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|BufferedReader
name|br
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fs
operator|.
name|open
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|data
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|String
name|line
init|=
name|br
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|data
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
name|line
operator|=
name|br
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
block|}
end_class

end_unit

