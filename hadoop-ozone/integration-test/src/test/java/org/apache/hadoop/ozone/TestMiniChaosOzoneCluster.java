begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.ozone
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|ozone
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|RandomStringUtils
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
name|hdds
operator|.
name|conf
operator|.
name|OzoneConfiguration
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
name|ozone
operator|.
name|client
operator|.
name|ObjectStore
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
name|ozone
operator|.
name|client
operator|.
name|OzoneBucket
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
name|ozone
operator|.
name|client
operator|.
name|OzoneVolume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
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
name|picocli
operator|.
name|CommandLine
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|picocli
operator|.
name|CommandLine
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
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_comment
comment|/**  * Test Read Write with Mini Ozone Chaos Cluster.  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|description
operator|=
literal|"Starts IO with MiniOzoneChaosCluster"
argument_list|,
name|name
operator|=
literal|"chaos"
argument_list|,
name|mixinStandardHelpOptions
operator|=
literal|true
argument_list|)
DECL|class|TestMiniChaosOzoneCluster
specifier|public
class|class
name|TestMiniChaosOzoneCluster
implements|implements
name|Runnable
block|{
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-d"
block|,
literal|"--numDatanodes"
block|}
argument_list|,
name|description
operator|=
literal|"num of datanodes"
argument_list|)
DECL|field|numDatanodes
specifier|private
specifier|static
name|int
name|numDatanodes
init|=
literal|20
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-t"
block|,
literal|"--numThreads"
block|}
argument_list|,
name|description
operator|=
literal|"num of IO threads"
argument_list|)
DECL|field|numThreads
specifier|private
specifier|static
name|int
name|numThreads
init|=
literal|10
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-b"
block|,
literal|"--numBuffers"
block|}
argument_list|,
name|description
operator|=
literal|"num of IO buffers"
argument_list|)
DECL|field|numBuffers
specifier|private
specifier|static
name|int
name|numBuffers
init|=
literal|16
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-m"
block|,
literal|"--numMinutes"
block|}
argument_list|,
name|description
operator|=
literal|"total run time"
argument_list|)
DECL|field|numMinutes
specifier|private
specifier|static
name|int
name|numMinutes
init|=
literal|1440
decl_stmt|;
comment|// 1 day by default
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-n"
block|,
literal|"--numClients"
block|}
argument_list|,
name|description
operator|=
literal|"no of clients writing to OM"
argument_list|)
DECL|field|numClients
specifier|private
specifier|static
name|int
name|numClients
init|=
literal|3
decl_stmt|;
annotation|@
name|Option
argument_list|(
name|names
operator|=
block|{
literal|"-i"
block|,
literal|"--failureInterval"
block|}
argument_list|,
name|description
operator|=
literal|"time between failure events in seconds"
argument_list|)
DECL|field|failureInterval
specifier|private
specifier|static
name|int
name|failureInterval
init|=
literal|300
decl_stmt|;
comment|// 5 second period between failures.
DECL|field|cluster
specifier|private
specifier|static
name|MiniOzoneChaosCluster
name|cluster
decl_stmt|;
DECL|field|loadGenerator
specifier|private
specifier|static
name|MiniOzoneLoadGenerator
name|loadGenerator
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|init ()
specifier|public
specifier|static
name|void
name|init
parameter_list|()
throws|throws
name|Exception
block|{
name|cluster
operator|=
operator|new
name|MiniOzoneChaosCluster
operator|.
name|Builder
argument_list|(
operator|new
name|OzoneConfiguration
argument_list|()
argument_list|)
operator|.
name|setNumDatanodes
argument_list|(
name|numDatanodes
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitForClusterToBeReady
argument_list|()
expr_stmt|;
name|String
name|volumeName
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|String
name|bucketName
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|ObjectStore
name|store
init|=
name|cluster
operator|.
name|getRpcClient
argument_list|()
operator|.
name|getObjectStore
argument_list|()
decl_stmt|;
name|store
operator|.
name|createVolume
argument_list|(
name|volumeName
argument_list|)
expr_stmt|;
name|OzoneVolume
name|volume
init|=
name|store
operator|.
name|getVolume
argument_list|(
name|volumeName
argument_list|)
decl_stmt|;
name|volume
operator|.
name|createBucket
argument_list|(
name|bucketName
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|OzoneBucket
argument_list|>
name|ozoneBuckets
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numClients
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numClients
condition|;
name|i
operator|++
control|)
block|{
name|ozoneBuckets
operator|.
name|add
argument_list|(
name|volume
operator|.
name|getBucket
argument_list|(
name|bucketName
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|agedBucketName
init|=
name|RandomStringUtils
operator|.
name|randomAlphabetic
argument_list|(
literal|10
argument_list|)
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
name|volume
operator|.
name|createBucket
argument_list|(
name|agedBucketName
argument_list|)
expr_stmt|;
name|OzoneBucket
name|agedLoadBucket
init|=
name|volume
operator|.
name|getBucket
argument_list|(
name|agedBucketName
argument_list|)
decl_stmt|;
name|loadGenerator
operator|=
operator|new
name|MiniOzoneLoadGenerator
argument_list|(
name|ozoneBuckets
argument_list|,
name|agedLoadBucket
argument_list|,
name|numThreads
argument_list|,
name|numBuffers
argument_list|)
expr_stmt|;
block|}
comment|/**    * Shutdown MiniDFSCluster.    */
annotation|@
name|AfterClass
DECL|method|shutdown ()
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
if|if
condition|(
name|loadGenerator
operator|!=
literal|null
condition|)
block|{
name|loadGenerator
operator|.
name|shutdownLoadGenerator
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|run ()
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
name|init
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|startChaos
argument_list|(
name|failureInterval
argument_list|,
name|failureInterval
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|loadGenerator
operator|.
name|startIO
argument_list|(
name|numMinutes
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{     }
finally|finally
block|{
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|main (String... args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
modifier|...
name|args
parameter_list|)
block|{
name|CommandLine
operator|.
name|run
argument_list|(
operator|new
name|TestMiniChaosOzoneCluster
argument_list|()
argument_list|,
name|System
operator|.
name|err
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testReadWriteWithChaosCluster ()
specifier|public
name|void
name|testReadWriteWithChaosCluster
parameter_list|()
block|{
name|cluster
operator|.
name|startChaos
argument_list|(
literal|5
argument_list|,
literal|10
argument_list|,
name|TimeUnit
operator|.
name|SECONDS
argument_list|)
expr_stmt|;
name|loadGenerator
operator|.
name|startIO
argument_list|(
literal|1
argument_list|,
name|TimeUnit
operator|.
name|MINUTES
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

