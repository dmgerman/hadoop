begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.combinertest
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
name|combinertest
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
name|IntWritable
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
name|Task
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
name|Counter
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

begin_class
DECL|class|LargeKVCombinerTest
specifier|public
class|class
name|LargeKVCombinerTest
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
name|LargeKVCombinerTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
block|}
annotation|@
name|Test
DECL|method|testLargeValueCombiner ()
specifier|public
name|void
name|testLargeValueCombiner
parameter_list|()
block|{
specifier|final
name|Configuration
name|normalConf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|nativeConf
init|=
name|ScenarioConfiguration
operator|.
name|getNativeConfiguration
argument_list|()
decl_stmt|;
name|normalConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|COMBINER_CONF_PATH
argument_list|)
expr_stmt|;
name|nativeConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|COMBINER_CONF_PATH
argument_list|)
expr_stmt|;
specifier|final
name|int
name|deafult_KVSize_Maximum
init|=
literal|1
operator|<<
literal|22
decl_stmt|;
comment|// 4M
specifier|final
name|int
name|KVSize_Maximu
init|=
name|normalConf
operator|.
name|getInt
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MAX_LARGEKV_TEST
argument_list|,
name|deafult_KVSize_Maximum
argument_list|)
decl_stmt|;
specifier|final
name|String
name|inputPath
init|=
name|normalConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_TEST_COMBINER_INPUTPATH_KEY
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_TEST_COMBINER_INPUTPATH_DEFAULTV
argument_list|)
operator|+
literal|"/largeKV"
decl_stmt|;
specifier|final
name|String
name|nativeOutputPath
init|=
name|normalConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_TEST_COMBINER_OUTPUTPATH
argument_list|,
name|TestConstants
operator|.
name|NATIVETASK_TEST_COMBINER_OUTPUTPATH_DEFAULTV
argument_list|)
operator|+
literal|"/nativeLargeKV"
decl_stmt|;
specifier|final
name|String
name|hadoopOutputPath
init|=
name|normalConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NORMAL_TEST_COMBINER_OUTPUTPATH
argument_list|,
name|TestConstants
operator|.
name|NORMAL_TEST_COMBINER_OUTPUTPATH_DEFAULTV
argument_list|)
operator|+
literal|"/normalLargeKV"
decl_stmt|;
try|try
block|{
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|normalConf
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|65536
init|;
name|i
operator|<=
name|KVSize_Maximu
condition|;
name|i
operator|*=
literal|4
control|)
block|{
name|int
name|max
init|=
name|i
decl_stmt|;
name|int
name|min
init|=
name|Math
operator|.
name|max
argument_list|(
name|i
operator|/
literal|4
argument_list|,
name|max
operator|-
literal|10
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"===KV Size Test: min size: "
operator|+
name|min
operator|+
literal|", max size: "
operator|+
name|max
argument_list|)
expr_stmt|;
name|normalConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MIN
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|normalConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MAX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MIN
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|min
argument_list|)
argument_list|)
expr_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_KVSIZE_MAX
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|max
argument_list|)
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|inputPath
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
operator|new
name|TestInputFile
argument_list|(
name|normalConf
operator|.
name|getInt
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_COMBINER_WORDCOUNT_FILESIZE
argument_list|,
literal|1000000
argument_list|)
argument_list|,
name|IntWritable
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
name|normalConf
argument_list|)
operator|.
name|createSequenceTestFile
argument_list|(
name|inputPath
argument_list|,
literal|1
argument_list|)
expr_stmt|;
specifier|final
name|Job
name|normaljob
init|=
name|CombinerTest
operator|.
name|getJob
argument_list|(
literal|"normalwordcount"
argument_list|,
name|normalConf
argument_list|,
name|inputPath
argument_list|,
name|hadoopOutputPath
argument_list|)
decl_stmt|;
specifier|final
name|Job
name|nativejob
init|=
name|CombinerTest
operator|.
name|getJob
argument_list|(
literal|"nativewordcount"
argument_list|,
name|nativeConf
argument_list|,
name|inputPath
argument_list|,
name|nativeOutputPath
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|nativejob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Counter
name|nativeReduceGroups
init|=
name|nativejob
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
name|Task
operator|.
name|Counter
operator|.
name|REDUCE_INPUT_RECORDS
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|normaljob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|Counter
name|normalReduceGroups
init|=
name|normaljob
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
name|Task
operator|.
name|Counter
operator|.
name|REDUCE_INPUT_RECORDS
argument_list|)
decl_stmt|;
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
specifier|final
name|String
name|reason
init|=
literal|"LargeKVCombinerTest failed with, min size: "
operator|+
name|min
operator|+
literal|", max size: "
operator|+
name|max
operator|+
literal|", normal out: "
operator|+
name|hadoopOutputPath
operator|+
literal|", native Out: "
operator|+
name|nativeOutputPath
decl_stmt|;
name|assertEquals
argument_list|(
name|reason
argument_list|,
literal|true
argument_list|,
name|compareRet
argument_list|)
expr_stmt|;
comment|//        assertEquals("Native Reduce reduce group counter should equal orignal reduce group counter",
comment|//            nativeReduceGroups.getValue(), normalReduceGroups.getValue());
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"run exception"
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

