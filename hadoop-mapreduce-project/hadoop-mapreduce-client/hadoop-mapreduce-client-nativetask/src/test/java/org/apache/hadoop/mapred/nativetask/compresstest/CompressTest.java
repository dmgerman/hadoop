begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.compresstest
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
operator|.
name|nativetask
operator|.
name|compresstest
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
name|io
operator|.
name|Text
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
name|nativetask
operator|.
name|NativeRuntime
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
name|nativetask
operator|.
name|kvtest
operator|.
name|TestInputFile
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
name|nativetask
operator|.
name|testutil
operator|.
name|ResultVerifier
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
name|nativetask
operator|.
name|testutil
operator|.
name|ScenarioConfiguration
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
name|nativetask
operator|.
name|testutil
operator|.
name|TestConstants
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
name|MRJobConfig
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
name|apache
operator|.
name|hadoop
operator|.
name|util
operator|.
name|NativeCodeLoader
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
DECL|class|CompressTest
specifier|public
class|class
name|CompressTest
block|{
DECL|field|nativeConf
specifier|private
specifier|static
specifier|final
name|Configuration
name|nativeConf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
DECL|field|hadoopConf
specifier|private
specifier|static
specifier|final
name|Configuration
name|hadoopConf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
static|static
block|{
name|nativeConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|COMPRESS_TEST_CONF_PATH
argument_list|)
expr_stmt|;
name|hadoopConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|COMPRESS_TEST_CONF_PATH
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testSnappyCompress ()
specifier|public
name|void
name|testSnappyCompress
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|snappyCodec
init|=
literal|"org.apache.hadoop.io.compress.SnappyCodec"
decl_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|,
name|snappyCodec
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nativeOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_NATIVE_OUTPUTDIR
operator|+
literal|"/snappy"
decl_stmt|;
specifier|final
name|Job
name|job
init|=
name|CompressMapper
operator|.
name|getCompressJob
argument_list|(
literal|"nativesnappy"
argument_list|,
name|nativeConf
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|,
name|nativeOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|,
name|snappyCodec
argument_list|)
expr_stmt|;
specifier|final
name|String
name|hadoopOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_NORMAL_OUTPUTDIR
operator|+
literal|"/snappy"
decl_stmt|;
specifier|final
name|Job
name|hadoopjob
init|=
name|CompressMapper
operator|.
name|getCompressJob
argument_list|(
literal|"hadoopsnappy"
argument_list|,
name|hadoopConf
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hadoopjob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|compareRet
init|=
name|ResultVerifier
operator|.
name|verify
argument_list|(
name|nativeOutputPath
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"file compare result: if they are the same ,then return true"
argument_list|,
literal|true
argument_list|,
name|compareRet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGzipCompress ()
specifier|public
name|void
name|testGzipCompress
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|gzipCodec
init|=
literal|"org.apache.hadoop.io.compress.GzipCodec"
decl_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|,
name|gzipCodec
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nativeOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_NATIVE_OUTPUTDIR
operator|+
literal|"/gzip"
decl_stmt|;
specifier|final
name|Job
name|job
init|=
name|CompressMapper
operator|.
name|getCompressJob
argument_list|(
literal|"nativegzip"
argument_list|,
name|nativeConf
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|,
name|nativeOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|,
name|gzipCodec
argument_list|)
expr_stmt|;
specifier|final
name|String
name|hadoopOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_NORMAL_OUTPUTDIR
operator|+
literal|"/gzip"
decl_stmt|;
specifier|final
name|Job
name|hadoopjob
init|=
name|CompressMapper
operator|.
name|getCompressJob
argument_list|(
literal|"hadoopgzip"
argument_list|,
name|hadoopConf
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hadoopjob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|compareRet
init|=
name|ResultVerifier
operator|.
name|verify
argument_list|(
name|nativeOutputPath
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"file compare result: if they are the same ,then return true"
argument_list|,
literal|true
argument_list|,
name|compareRet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testLz4Compress ()
specifier|public
name|void
name|testLz4Compress
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|lz4Codec
init|=
literal|"org.apache.hadoop.io.compress.Lz4Codec"
decl_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|,
name|lz4Codec
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nativeOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_NATIVE_OUTPUTDIR
operator|+
literal|"/lz4"
decl_stmt|;
specifier|final
name|Job
name|nativeJob
init|=
name|CompressMapper
operator|.
name|getCompressJob
argument_list|(
literal|"nativelz4"
argument_list|,
name|nativeConf
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|,
name|nativeOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nativeJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|hadoopConf
operator|.
name|set
argument_list|(
name|MRJobConfig
operator|.
name|MAP_OUTPUT_COMPRESS_CODEC
argument_list|,
name|lz4Codec
argument_list|)
expr_stmt|;
specifier|final
name|String
name|hadoopOutputPath
init|=
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_NORMAL_OUTPUTDIR
operator|+
literal|"/lz4"
decl_stmt|;
specifier|final
name|Job
name|hadoopJob
init|=
name|CompressMapper
operator|.
name|getCompressJob
argument_list|(
literal|"hadooplz4"
argument_list|,
name|hadoopConf
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|hadoopJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|compareRet
init|=
name|ResultVerifier
operator|.
name|verify
argument_list|(
name|nativeOutputPath
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"file compare result: if they are the same ,then return true"
argument_list|,
literal|true
argument_list|,
name|compareRet
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|startUp ()
specifier|public
name|void
name|startUp
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeRuntime
operator|.
name|isNativeLibraryLoaded
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|ScenarioConfiguration
name|conf
init|=
operator|new
name|ScenarioConfiguration
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
name|path
argument_list|)
condition|)
block|{
operator|new
name|TestInputFile
argument_list|(
name|hadoopConf
operator|.
name|getInt
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_FILESIZE
argument_list|,
literal|100000
argument_list|)
argument_list|,
name|Text
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|Text
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|conf
argument_list|)
operator|.
name|createSequenceTestFile
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_INPUTDIR
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|cleanUp ()
specifier|public
specifier|static
name|void
name|cleanUp
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|ScenarioConfiguration
argument_list|()
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_COMPRESS_TEST_DIR
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

