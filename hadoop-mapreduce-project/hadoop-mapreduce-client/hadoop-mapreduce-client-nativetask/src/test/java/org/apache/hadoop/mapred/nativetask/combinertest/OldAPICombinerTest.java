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
name|assertj
operator|.
name|core
operator|.
name|api
operator|.
name|Assertions
operator|.
name|assertThat
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
name|FileInputFormat
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
name|FileOutputFormat
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
name|RunningJob
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
name|SequenceFileInputFormat
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
name|TextOutputFormat
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
name|TaskCounter
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
DECL|class|OldAPICombinerTest
specifier|public
class|class
name|OldAPICombinerTest
block|{
DECL|field|fs
specifier|private
name|FileSystem
name|fs
decl_stmt|;
DECL|field|inputpath
specifier|private
name|String
name|inputpath
decl_stmt|;
annotation|@
name|Test
DECL|method|testWordCountCombinerWithOldAPI ()
specifier|public
name|void
name|testWordCountCombinerWithOldAPI
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
name|COMBINER_CONF_PATH
argument_list|)
expr_stmt|;
specifier|final
name|String
name|nativeoutput
init|=
name|TestConstants
operator|.
name|NATIVETASK_OLDAPI_COMBINER_TEST_NATIVE_OUTPUTPATH
decl_stmt|;
specifier|final
name|JobConf
name|nativeJob
init|=
name|getOldAPIJobconf
argument_list|(
name|nativeConf
argument_list|,
literal|"nativeCombinerWithOldAPI"
argument_list|,
name|inputpath
argument_list|,
name|nativeoutput
argument_list|)
decl_stmt|;
name|RunningJob
name|nativeRunning
init|=
name|JobClient
operator|.
name|runJob
argument_list|(
name|nativeJob
argument_list|)
decl_stmt|;
name|Counter
name|nativeReduceGroups
init|=
name|nativeRunning
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
name|TaskCounter
operator|.
name|REDUCE_INPUT_RECORDS
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|normalConf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
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
specifier|final
name|String
name|normaloutput
init|=
name|TestConstants
operator|.
name|NATIVETASK_OLDAPI_COMBINER_TEST_NORMAL_OUTPUTPATH
decl_stmt|;
specifier|final
name|JobConf
name|normalJob
init|=
name|getOldAPIJobconf
argument_list|(
name|normalConf
argument_list|,
literal|"normalCombinerWithOldAPI"
argument_list|,
name|inputpath
argument_list|,
name|normaloutput
argument_list|)
decl_stmt|;
name|RunningJob
name|normalRunning
init|=
name|JobClient
operator|.
name|runJob
argument_list|(
name|normalJob
argument_list|)
decl_stmt|;
name|Counter
name|normalReduceGroups
init|=
name|normalRunning
operator|.
name|getCounters
argument_list|()
operator|.
name|findCounter
argument_list|(
name|TaskCounter
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
name|nativeoutput
argument_list|,
name|normaloutput
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|compareRet
argument_list|)
operator|.
name|withFailMessage
argument_list|(
literal|"file compare result: if they are the same ,then return true"
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|nativeReduceGroups
operator|.
name|getValue
argument_list|()
argument_list|)
operator|.
name|withFailMessage
argument_list|(
literal|"The input reduce record count must be same"
argument_list|)
operator|.
name|isEqualTo
argument_list|(
name|normalReduceGroups
operator|.
name|getValue
argument_list|()
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
name|conf
operator|.
name|addcombinerConf
argument_list|()
expr_stmt|;
name|this
operator|.
name|fs
operator|=
name|FileSystem
operator|.
name|get
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|this
operator|.
name|inputpath
operator|=
name|TestConstants
operator|.
name|NATIVETASK_COMBINER_TEST_INPUTDIR
operator|+
literal|"/wordcount"
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|inputpath
argument_list|)
argument_list|)
condition|)
block|{
operator|new
name|TestInputFile
argument_list|(
name|conf
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
name|inputpath
argument_list|,
literal|1
argument_list|,
call|(
name|byte
call|)
argument_list|(
literal|'a'
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|NATIVETASK_COMBINER_TEST_DIR
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
DECL|method|getOldAPIJobconf (Configuration configuration, String name, String input, String output)
specifier|private
specifier|static
name|JobConf
name|getOldAPIJobconf
parameter_list|(
name|Configuration
name|configuration
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|input
parameter_list|,
name|String
name|output
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|JobConf
name|jobConf
init|=
operator|new
name|JobConf
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
specifier|final
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|output
argument_list|)
argument_list|)
condition|)
block|{
name|fs
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|output
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|jobConf
operator|.
name|setJobName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setMapperClass
argument_list|(
name|WordCountWithOldAPI
operator|.
name|TokenizerMapperWithOldAPI
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setCombinerClass
argument_list|(
name|WordCountWithOldAPI
operator|.
name|IntSumReducerWithOldAPI
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setReducerClass
argument_list|(
name|WordCountWithOldAPI
operator|.
name|IntSumReducerWithOldAPI
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setInputFormat
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|setOutputFormat
argument_list|(
name|TextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|jobConf
argument_list|,
operator|new
name|Path
argument_list|(
name|input
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|jobConf
argument_list|,
operator|new
name|Path
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|jobConf
return|;
block|}
block|}
end_class

end_unit

