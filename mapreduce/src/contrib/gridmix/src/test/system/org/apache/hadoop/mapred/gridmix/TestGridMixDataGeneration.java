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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
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
name|logging
operator|.
name|LogFactory
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
name|test
operator|.
name|system
operator|.
name|MRCluster
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
name|test
operator|.
name|system
operator|.
name|JTProtocol
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
name|test
operator|.
name|system
operator|.
name|JTClient
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
name|JobClient
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
name|JobStatus
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
name|RoundRobinUserResolver
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
name|EchoUserResolver
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
name|SubmitterUserResolver
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
name|test
operator|.
name|system
operator|.
name|UtilsForGridmix
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
name|test
operator|.
name|system
operator|.
name|GridMixRunMode
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
name|test
operator|.
name|system
operator|.
name|GridMixConfig
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
name|ContentSummary
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
name|org
operator|.
name|junit
operator|.
name|Assert
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

begin_comment
comment|/**  * Verify the Gridmix data generation with various submission policies and   * user resolver modes.  */
end_comment

begin_class
DECL|class|TestGridMixDataGeneration
specifier|public
class|class
name|TestGridMixDataGeneration
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestGridMixDataGeneration
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MRCluster
name|cluster
decl_stmt|;
DECL|field|jtClient
specifier|private
specifier|static
name|JTClient
name|jtClient
decl_stmt|;
DECL|field|rtClient
specifier|private
specifier|static
name|JTProtocol
name|rtClient
decl_stmt|;
DECL|field|gridmixDir
specifier|private
specifier|static
name|Path
name|gridmixDir
decl_stmt|;
DECL|field|cSize
specifier|private
specifier|static
name|int
name|cSize
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|before ()
specifier|public
specifier|static
name|void
name|before
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|excludeExpList
init|=
block|{
literal|"java.net.ConnectException"
block|,
literal|"java.io.IOException"
block|}
decl_stmt|;
name|cluster
operator|=
name|MRCluster
operator|.
name|createCluster
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setExcludeExpList
argument_list|(
name|excludeExpList
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|cSize
operator|=
name|cluster
operator|.
name|getTTClients
argument_list|()
operator|.
name|size
argument_list|()
expr_stmt|;
name|jtClient
operator|=
name|cluster
operator|.
name|getJTClient
argument_list|()
expr_stmt|;
name|rtClient
operator|=
name|jtClient
operator|.
name|getProxy
argument_list|()
expr_stmt|;
name|gridmixDir
operator|=
operator|new
name|Path
argument_list|(
literal|"herriot-gridmix"
argument_list|)
expr_stmt|;
name|UtilsForGridmix
operator|.
name|createDirs
argument_list|(
name|gridmixDir
argument_list|,
name|rtClient
operator|.
name|getDaemonConf
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|after ()
specifier|public
specifier|static
name|void
name|after
parameter_list|()
throws|throws
name|Exception
block|{
name|UtilsForGridmix
operator|.
name|cleanup
argument_list|(
name|gridmixDir
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|cluster
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
comment|/**    * Generate the data in a STRESS submission policy with SubmitterUserResolver     * mode and verify whether the generated data matches with given     * input size or not.    * @throws IOException    */
annotation|@
name|Test
DECL|method|testGenerateDataWithSTRESSSubmission ()
specifier|public
name|void
name|testGenerateDataWithSTRESSSubmission
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
name|rtClient
operator|.
name|getDaemonConf
argument_list|()
expr_stmt|;
specifier|final
name|long
name|inputSizeInMB
init|=
name|cSize
operator|*
literal|128
decl_stmt|;
name|String
index|[]
name|runtimeValues
init|=
block|{
literal|"LOADJOB"
block|,
name|SubmitterUserResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"STRESS"
block|,
name|inputSizeInMB
operator|+
literal|"m"
block|,
literal|"file:///dev/null"
block|}
decl_stmt|;
name|String
index|[]
name|otherArgs
init|=
block|{
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_DISTCACHE_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_COMPRESSION_ENABLE
operator|+
literal|"=false"
block|}
decl_stmt|;
name|int
name|exitCode
init|=
name|UtilsForGridmix
operator|.
name|runGridmixJob
argument_list|(
name|gridmixDir
argument_list|,
name|conf
argument_list|,
name|GridMixRunMode
operator|.
name|DATA_GENERATION
operator|.
name|getValue
argument_list|()
argument_list|,
name|runtimeValues
argument_list|,
name|otherArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Data generation has failed."
argument_list|,
literal|0
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|checkGeneratedDataAndJobStatus
argument_list|(
name|inputSizeInMB
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generate the data in a REPLAY submission policy with RoundRobinUserResolver    * mode and verify whether the generated data matches with the given     * input size or not.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testGenerateDataWithREPLAYSubmission ()
specifier|public
name|void
name|testGenerateDataWithREPLAYSubmission
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
name|rtClient
operator|.
name|getDaemonConf
argument_list|()
expr_stmt|;
specifier|final
name|long
name|inputSizeInMB
init|=
name|cSize
operator|*
literal|300
decl_stmt|;
name|String
index|[]
name|runtimeValues
init|=
block|{
literal|"LOADJOB"
block|,
name|RoundRobinUserResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"REPLAY"
block|,
name|inputSizeInMB
operator|+
literal|"m"
block|,
literal|"file://"
operator|+
name|UtilsForGridmix
operator|.
name|getProxyUsersFile
argument_list|(
name|conf
argument_list|)
block|,
literal|"file:///dev/null"
block|}
decl_stmt|;
name|String
index|[]
name|otherArgs
init|=
block|{
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_DISTCACHE_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_COMPRESSION_ENABLE
operator|+
literal|"=false"
block|}
decl_stmt|;
name|int
name|exitCode
init|=
name|UtilsForGridmix
operator|.
name|runGridmixJob
argument_list|(
name|gridmixDir
argument_list|,
name|conf
argument_list|,
name|GridMixRunMode
operator|.
name|DATA_GENERATION
operator|.
name|getValue
argument_list|()
argument_list|,
name|runtimeValues
argument_list|,
name|otherArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Data generation has failed."
argument_list|,
literal|0
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|checkGeneratedDataAndJobStatus
argument_list|(
name|inputSizeInMB
argument_list|)
expr_stmt|;
block|}
comment|/**    * Generate the data in a SERIAL submission policy with EchoUserResolver    * mode and also set the no.of bytes per file in the data.Verify whether each     * file size matches with given per file size or not and also     * verify the overall size of generated data.    * @throws Exception    */
annotation|@
name|Test
DECL|method|testGenerateDataWithSERIALSubmission ()
specifier|public
name|void
name|testGenerateDataWithSERIALSubmission
parameter_list|()
throws|throws
name|Exception
block|{
name|conf
operator|=
name|rtClient
operator|.
name|getDaemonConf
argument_list|()
expr_stmt|;
name|long
name|perNodeSizeInMB
init|=
literal|500
decl_stmt|;
comment|// 500 mb per node data
specifier|final
name|long
name|inputSizeInMB
init|=
name|cSize
operator|*
name|perNodeSizeInMB
decl_stmt|;
name|String
index|[]
name|runtimeValues
init|=
block|{
literal|"LOADJOB"
block|,
name|EchoUserResolver
operator|.
name|class
operator|.
name|getName
argument_list|()
block|,
literal|"SERIAL"
block|,
name|inputSizeInMB
operator|+
literal|"m"
block|,
literal|"file:///dev/null"
block|}
decl_stmt|;
name|long
name|bytesPerFile
init|=
literal|200
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|// 200 mb per file of data
name|String
index|[]
name|otherArgs
init|=
block|{
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_BYTES_PER_FILE
operator|+
literal|"="
operator|+
name|bytesPerFile
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_DISTCACHE_ENABLE
operator|+
literal|"=false"
block|,
literal|"-D"
block|,
name|GridMixConfig
operator|.
name|GRIDMIX_COMPRESSION_ENABLE
operator|+
literal|"=false"
block|}
decl_stmt|;
name|int
name|exitCode
init|=
name|UtilsForGridmix
operator|.
name|runGridmixJob
argument_list|(
name|gridmixDir
argument_list|,
name|conf
argument_list|,
name|GridMixRunMode
operator|.
name|DATA_GENERATION
operator|.
name|getValue
argument_list|()
argument_list|,
name|runtimeValues
argument_list|,
name|otherArgs
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Data generation has failed."
argument_list|,
literal|0
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Verify the eache file size in a generate data."
argument_list|)
expr_stmt|;
name|verifyEachNodeSize
argument_list|(
operator|new
name|Path
argument_list|(
name|gridmixDir
argument_list|,
literal|"input"
argument_list|)
argument_list|,
name|perNodeSizeInMB
argument_list|)
expr_stmt|;
name|verifyNumOfFilesGeneratedInEachNode
argument_list|(
operator|new
name|Path
argument_list|(
name|gridmixDir
argument_list|,
literal|"input"
argument_list|)
argument_list|,
name|perNodeSizeInMB
argument_list|,
name|bytesPerFile
argument_list|)
expr_stmt|;
name|checkGeneratedDataAndJobStatus
argument_list|(
name|inputSizeInMB
argument_list|)
expr_stmt|;
block|}
DECL|method|checkGeneratedDataAndJobStatus (long inputSize)
specifier|private
name|void
name|checkGeneratedDataAndJobStatus
parameter_list|(
name|long
name|inputSize
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Verify the generated data size."
argument_list|)
expr_stmt|;
name|long
name|dataSizeInMB
init|=
name|getDataSizeInMB
argument_list|(
operator|new
name|Path
argument_list|(
name|gridmixDir
argument_list|,
literal|"input"
argument_list|)
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"Generate data has not matched with given size"
argument_list|,
name|dataSizeInMB
operator|+
literal|0.1
operator|>
name|inputSize
operator|||
name|dataSizeInMB
operator|-
literal|0.1
operator|<
name|inputSize
argument_list|)
expr_stmt|;
name|JobClient
name|jobClient
init|=
name|jtClient
operator|.
name|getClient
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|jobClient
operator|.
name|getAllJobs
argument_list|()
operator|.
name|length
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Verify the job status after completion of job."
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Job has not succeeded."
argument_list|,
name|JobStatus
operator|.
name|SUCCEEDED
argument_list|,
name|jobClient
operator|.
name|getAllJobs
argument_list|()
index|[
name|len
operator|-
literal|1
index|]
operator|.
name|getRunState
argument_list|()
argument_list|)
expr_stmt|;
block|}
DECL|method|verifyEachNodeSize (Path inputDir, long dataSizePerNode)
specifier|private
name|void
name|verifyEachNodeSize
parameter_list|(
name|Path
name|inputDir
parameter_list|,
name|long
name|dataSizePerNode
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|inputDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|fstatus
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|inputDir
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|fstat
range|:
name|fstatus
control|)
block|{
if|if
condition|(
name|fstat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|long
name|fileSize
init|=
name|getDataSizeInMB
argument_list|(
name|fstat
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertTrue
argument_list|(
literal|"The Size has not matched with given "
operator|+
literal|"per node file size("
operator|+
name|dataSizePerNode
operator|+
literal|"MB)"
argument_list|,
name|fileSize
operator|+
literal|0.1
operator|>
name|dataSizePerNode
operator|||
name|fileSize
operator|-
literal|0.1
operator|<
name|dataSizePerNode
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|verifyNumOfFilesGeneratedInEachNode (Path inputDir, long nodeSize, long fileSize)
specifier|private
name|void
name|verifyNumOfFilesGeneratedInEachNode
parameter_list|(
name|Path
name|inputDir
parameter_list|,
name|long
name|nodeSize
parameter_list|,
name|long
name|fileSize
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|fileCount
init|=
name|nodeSize
operator|/
name|fileSize
decl_stmt|;
name|long
name|expFileCount
init|=
name|Math
operator|.
name|round
argument_list|(
name|fileCount
argument_list|)
decl_stmt|;
name|expFileCount
operator|=
name|expFileCount
operator|+
operator|(
operator|(
name|nodeSize
operator|%
name|fileSize
operator|!=
literal|0
operator|)
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|FileSystem
name|fs
init|=
name|inputDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileStatus
index|[]
name|fstatus
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|inputDir
argument_list|)
decl_stmt|;
for|for
control|(
name|FileStatus
name|fstat
range|:
name|fstatus
control|)
block|{
if|if
condition|(
name|fstat
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|FileSystem
name|nodeFs
init|=
name|fstat
operator|.
name|getPath
argument_list|()
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|long
name|actFileCount
init|=
name|nodeFs
operator|.
name|getContentSummary
argument_list|(
name|fstat
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|getFileCount
argument_list|()
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File count has not matched."
argument_list|,
name|expFileCount
argument_list|,
name|actFileCount
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|getDataSizeInMB (Path inputDir)
specifier|private
specifier|static
name|long
name|getDataSizeInMB
parameter_list|(
name|Path
name|inputDir
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|fs
init|=
name|inputDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|ContentSummary
name|csmry
init|=
name|fs
operator|.
name|getContentSummary
argument_list|(
name|inputDir
argument_list|)
decl_stmt|;
name|long
name|dataSize
init|=
name|csmry
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|dataSize
operator|=
name|dataSize
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
expr_stmt|;
return|return
name|dataSize
return|;
block|}
block|}
end_class

end_unit

