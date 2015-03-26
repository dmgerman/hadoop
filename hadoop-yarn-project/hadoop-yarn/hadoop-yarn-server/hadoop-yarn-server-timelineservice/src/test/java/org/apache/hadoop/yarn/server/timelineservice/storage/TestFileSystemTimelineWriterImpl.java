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
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
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
name|Test
import|;
end_import

begin_class
DECL|class|TestFileSystemTimelineWriterImpl
specifier|public
class|class
name|TestFileSystemTimelineWriterImpl
block|{
comment|/**    * Unit test for PoC YARN 3264    * @throws Exception    */
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
name|entity
operator|.
name|setModifiedTime
argument_list|(
literal|1425016502000L
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
name|fsi
operator|.
name|init
argument_list|(
operator|new
name|YarnConfiguration
argument_list|()
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
literal|"cluster_id"
argument_list|,
literal|"user_id"
argument_list|,
literal|"flow_id"
argument_list|,
literal|"flow_run_id"
argument_list|,
literal|"app_id"
argument_list|,
name|te
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|fsi
operator|.
name|getOutputRoot
argument_list|()
operator|+
literal|"/entities/cluster_id/user_id/flow_id/flow_run_id/app_id/"
operator|+
name|type
operator|+
literal|"/"
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
name|Paths
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|f
operator|.
name|exists
argument_list|()
operator|&&
operator|!
name|f
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
name|Files
operator|.
name|readAllLines
argument_list|(
name|path
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
comment|// ensure there's only one entity + 1 new line
name|assertTrue
argument_list|(
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
comment|// delete the directory
name|File
name|outputDir
init|=
operator|new
name|File
argument_list|(
name|fsi
operator|.
name|getOutputRoot
argument_list|()
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
name|outputDir
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
operator|(
name|f
operator|.
name|exists
argument_list|()
operator|)
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
name|stop
argument_list|()
expr_stmt|;
name|FileUtils
operator|.
name|deleteDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|fsi
operator|.
name|getOutputRoot
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

