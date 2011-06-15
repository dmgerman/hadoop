begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.gridmix
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|gridmix
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
name|*
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
name|mapred
operator|.
name|JobConf
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
name|mapred
operator|.
name|gridmix
operator|.
name|DebugJobProducer
operator|.
name|MockJob
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
name|mapreduce
operator|.
name|Job
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
name|mapreduce
operator|.
name|MRConfig
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
name|mapreduce
operator|.
name|MRJobConfig
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
name|mapreduce
operator|.
name|server
operator|.
name|jobtracker
operator|.
name|JTConfig
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
name|tools
operator|.
name|rumen
operator|.
name|JobStory
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

begin_comment
comment|/**  * Test if Gridmix correctly configures the simulated job's configuration for  * high ram job properties.  */
end_comment

begin_class
DECL|class|TestHighRamJob
specifier|public
class|class
name|TestHighRamJob
block|{
comment|/**    * A dummy {@link GridmixJob} that opens up the simulated job for testing.    */
DECL|class|DummyGridmixJob
specifier|protected
specifier|static
class|class
name|DummyGridmixJob
extends|extends
name|GridmixJob
block|{
DECL|method|DummyGridmixJob (Configuration conf, JobStory desc)
specifier|public
name|DummyGridmixJob
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|JobStory
name|desc
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|conf
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|desc
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test"
argument_list|)
argument_list|,
name|UserGroupInformation
operator|.
name|getCurrentUser
argument_list|()
argument_list|,
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|/**      * Do nothing since this is a dummy gridmix job.      */
annotation|@
name|Override
DECL|method|call ()
specifier|public
name|Job
name|call
parameter_list|()
throws|throws
name|Exception
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
DECL|method|canEmulateCompression ()
specifier|protected
name|boolean
name|canEmulateCompression
parameter_list|()
block|{
comment|// return false as we don't need compression
return|return
literal|false
return|;
block|}
DECL|method|getJob ()
specifier|protected
name|Job
name|getJob
parameter_list|()
block|{
comment|// open the simulated job for testing
return|return
name|job
return|;
block|}
block|}
DECL|method|testHighRamConfig (long jobMapMB, long jobReduceMB, long clusterMapMB, long clusterReduceMB, long simulatedClusterMapMB, long simulatedClusterReduceMB, long expectedMapMB, long expectedReduceMB, Configuration gConf)
specifier|private
specifier|static
name|void
name|testHighRamConfig
parameter_list|(
name|long
name|jobMapMB
parameter_list|,
name|long
name|jobReduceMB
parameter_list|,
name|long
name|clusterMapMB
parameter_list|,
name|long
name|clusterReduceMB
parameter_list|,
name|long
name|simulatedClusterMapMB
parameter_list|,
name|long
name|simulatedClusterReduceMB
parameter_list|,
name|long
name|expectedMapMB
parameter_list|,
name|long
name|expectedReduceMB
parameter_list|,
name|Configuration
name|gConf
parameter_list|)
throws|throws
name|IOException
block|{
name|Configuration
name|simulatedJobConf
init|=
operator|new
name|Configuration
argument_list|(
name|gConf
argument_list|)
decl_stmt|;
name|simulatedJobConf
operator|.
name|setLong
argument_list|(
name|MRConfig
operator|.
name|MAPMEMORY_MB
argument_list|,
name|simulatedClusterMapMB
argument_list|)
expr_stmt|;
name|simulatedJobConf
operator|.
name|setLong
argument_list|(
name|MRConfig
operator|.
name|REDUCEMEMORY_MB
argument_list|,
name|simulatedClusterReduceMB
argument_list|)
expr_stmt|;
comment|// define a source conf
name|Configuration
name|sourceConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// configure the original job
name|sourceConf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MEMORY_MB
argument_list|,
name|jobMapMB
argument_list|)
expr_stmt|;
name|sourceConf
operator|.
name|setLong
argument_list|(
name|MRConfig
operator|.
name|MAPMEMORY_MB
argument_list|,
name|clusterMapMB
argument_list|)
expr_stmt|;
name|sourceConf
operator|.
name|setLong
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MEMORY_MB
argument_list|,
name|jobReduceMB
argument_list|)
expr_stmt|;
name|sourceConf
operator|.
name|setLong
argument_list|(
name|MRConfig
operator|.
name|REDUCEMEMORY_MB
argument_list|,
name|clusterReduceMB
argument_list|)
expr_stmt|;
comment|// define a mock job
name|MockJob
name|story
init|=
operator|new
name|MockJob
argument_list|(
name|sourceConf
argument_list|)
decl_stmt|;
name|GridmixJob
name|job
init|=
operator|new
name|DummyGridmixJob
argument_list|(
name|simulatedJobConf
argument_list|,
name|story
argument_list|)
decl_stmt|;
name|Job
name|simulatedJob
init|=
name|job
operator|.
name|getJob
argument_list|()
decl_stmt|;
name|Configuration
name|simulatedConf
init|=
name|simulatedJob
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
comment|// check if the high ram properties are not set
name|assertEquals
argument_list|(
name|expectedMapMB
argument_list|,
name|simulatedConf
operator|.
name|getLong
argument_list|(
name|MRJobConfig
operator|.
name|MAP_MEMORY_MB
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedReduceMB
argument_list|,
name|simulatedConf
operator|.
name|getLong
argument_list|(
name|MRJobConfig
operator|.
name|REDUCE_MEMORY_MB
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests high ram job properties configuration.    */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
annotation|@
name|Test
DECL|method|testHighRamFeatureEmulation ()
specifier|public
name|void
name|testHighRamFeatureEmulation
parameter_list|()
throws|throws
name|IOException
block|{
comment|// define the gridmix conf
name|Configuration
name|gridmixConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
comment|// test : check high ram emulation disabled
name|gridmixConf
operator|.
name|setBoolean
argument_list|(
name|GridmixJob
operator|.
name|GRIDMIX_HIGHRAM_EMULATION_ENABLE
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|,
name|JobConf
operator|.
name|DISABLED_MEMORY_LIMIT
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
comment|// test : check with high ram enabled (default) and no scaling
name|gridmixConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|// set the deprecated max memory limit
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JobConf
operator|.
name|UPPER_LIMIT_ON_TASK_VMEM_PROPERTY
argument_list|,
literal|20
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|20
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|,
literal|5
argument_list|,
literal|10
argument_list|,
literal|10
argument_list|,
literal|20
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
comment|// test : check with high ram enabled and scaling
name|gridmixConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
comment|// set the new max map/reduce memory limits
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_MAX_MAPMEMORY_MB
argument_list|,
literal|100
argument_list|)
expr_stmt|;
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_MAX_REDUCEMEMORY_MB
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|45
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|,
literal|50
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
literal|300
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
comment|// test : check with high ram enabled and map memory scaling mismatch
comment|//        (deprecated)
name|gridmixConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JobConf
operator|.
name|UPPER_LIMIT_ON_TASK_VMEM_PROPERTY
argument_list|,
literal|70
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|Boolean
name|failed
init|=
literal|null
decl_stmt|;
try|try
block|{
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|45
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|,
literal|50
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
literal|300
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Exception expected for exceeding map memory limit "
operator|+
literal|"(deprecation)!"
argument_list|,
name|failed
argument_list|)
expr_stmt|;
comment|// test : check with high ram enabled and reduce memory scaling mismatch
comment|//        (deprecated)
name|gridmixConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JobConf
operator|.
name|UPPER_LIMIT_ON_TASK_VMEM_PROPERTY
argument_list|,
literal|150
operator|*
literal|1024
operator|*
literal|1024
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|45
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|,
literal|50
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
literal|300
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Exception expected for exceeding reduce memory limit "
operator|+
literal|"(deprecation)!"
argument_list|,
name|failed
argument_list|)
expr_stmt|;
comment|// test : check with high ram enabled and scaling mismatch on map limits
name|gridmixConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_MAX_MAPMEMORY_MB
argument_list|,
literal|70
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|45
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|,
literal|50
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
literal|300
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Exception expected for exceeding map memory limit!"
argument_list|,
name|failed
argument_list|)
expr_stmt|;
comment|// test : check with high ram enabled and scaling mismatch on reduce
comment|//        limits
name|gridmixConf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|gridmixConf
operator|.
name|setLong
argument_list|(
name|JTConfig
operator|.
name|JT_MAX_REDUCEMEMORY_MB
argument_list|,
literal|200
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|testHighRamConfig
argument_list|(
literal|10
argument_list|,
literal|45
argument_list|,
literal|5
argument_list|,
literal|15
argument_list|,
literal|50
argument_list|,
literal|100
argument_list|,
literal|100
argument_list|,
literal|300
argument_list|,
name|gridmixConf
argument_list|)
expr_stmt|;
name|failed
operator|=
literal|false
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|failed
operator|=
literal|true
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|failed
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Exception expected for exceeding reduce memory limit!"
argument_list|,
name|failed
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

