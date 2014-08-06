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

begin_class
DECL|class|CompressTest
specifier|public
class|class
name|CompressTest
block|{
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
name|Configuration
name|conf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|SNAPPY_COMPRESS_CONF_PATH
argument_list|)
expr_stmt|;
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
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|hadoopconf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
name|hadoopconf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|SNAPPY_COMPRESS_CONF_PATH
argument_list|)
expr_stmt|;
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
name|hadoopconf
argument_list|)
decl_stmt|;
name|hadoopjob
operator|.
name|waitForCompletion
argument_list|(
literal|true
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
name|CompressMapper
operator|.
name|outputFileDir
operator|+
literal|"nativesnappy"
argument_list|,
name|CompressMapper
operator|.
name|outputFileDir
operator|+
literal|"hadoopsnappy"
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
name|Configuration
name|conf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|GZIP_COMPRESS_CONF_PATH
argument_list|)
expr_stmt|;
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
name|conf
argument_list|)
decl_stmt|;
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|hadoopconf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
name|hadoopconf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|GZIP_COMPRESS_CONF_PATH
argument_list|)
expr_stmt|;
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
name|hadoopconf
argument_list|)
decl_stmt|;
name|hadoopjob
operator|.
name|waitForCompletion
argument_list|(
literal|true
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
name|CompressMapper
operator|.
name|outputFileDir
operator|+
literal|"nativegzip"
argument_list|,
name|CompressMapper
operator|.
name|outputFileDir
operator|+
literal|"hadoopgzip"
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
name|Configuration
name|nativeConf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
name|nativeConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|LZ4_COMPRESS_CONF_PATH
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
name|nativeJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|hadoopConf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
name|hadoopConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|LZ4_COMPRESS_CONF_PATH
argument_list|)
expr_stmt|;
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
argument_list|)
decl_stmt|;
name|hadoopJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
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
name|CompressMapper
operator|.
name|outputFileDir
operator|+
literal|"nativelz4"
argument_list|,
name|CompressMapper
operator|.
name|outputFileDir
operator|+
literal|"hadooplz4"
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
name|CompressMapper
operator|.
name|inputFile
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
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
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
name|CompressMapper
operator|.
name|inputFile
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

