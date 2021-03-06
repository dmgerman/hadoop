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
name|combinertest
operator|.
name|WordCount
operator|.
name|IntSumReducer
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
name|combinertest
operator|.
name|WordCount
operator|.
name|TokenizerMapper
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
name|lib
operator|.
name|input
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
name|mapreduce
operator|.
name|lib
operator|.
name|input
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
name|mapreduce
operator|.
name|lib
operator|.
name|output
operator|.
name|FileOutputFormat
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
DECL|class|CombinerTest
specifier|public
class|class
name|CombinerTest
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
DECL|field|nativeoutputpath
specifier|private
name|String
name|nativeoutputpath
decl_stmt|;
DECL|field|hadoopoutputpath
specifier|private
name|String
name|hadoopoutputpath
decl_stmt|;
annotation|@
name|Test
DECL|method|testWordCountCombiner ()
specifier|public
name|void
name|testWordCountCombiner
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
name|Job
name|nativejob
init|=
name|getJob
argument_list|(
literal|"nativewordcount"
argument_list|,
name|nativeConf
argument_list|,
name|inputpath
argument_list|,
name|nativeoutputpath
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|commonConf
init|=
name|ScenarioConfiguration
operator|.
name|getNormalConfiguration
argument_list|()
decl_stmt|;
name|commonConf
operator|.
name|addResource
argument_list|(
name|TestConstants
operator|.
name|COMBINER_CONF_PATH
argument_list|)
expr_stmt|;
specifier|final
name|Job
name|normaljob
init|=
name|getJob
argument_list|(
literal|"normalwordcount"
argument_list|,
name|commonConf
argument_list|,
name|inputpath
argument_list|,
name|hadoopoutputpath
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|nativejob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|normaljob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|assertThat
argument_list|(
name|ResultVerifier
operator|.
name|verify
argument_list|(
name|nativeoutputpath
argument_list|,
name|hadoopoutputpath
argument_list|)
argument_list|)
operator|.
name|isTrue
argument_list|()
expr_stmt|;
name|ResultVerifier
operator|.
name|verifyCounters
argument_list|(
name|normaljob
argument_list|,
name|nativejob
argument_list|,
literal|true
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
name|this
operator|.
name|nativeoutputpath
operator|=
name|TestConstants
operator|.
name|NATIVETASK_COMBINER_TEST_NATIVE_OUTPUTDIR
operator|+
literal|"/nativewordcount"
expr_stmt|;
name|this
operator|.
name|hadoopoutputpath
operator|=
name|TestConstants
operator|.
name|NATIVETASK_COMBINER_TEST_NORMAL_OUTPUTDIR
operator|+
literal|"/normalwordcount"
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
DECL|method|getJob (String jobname, Configuration inputConf, String inputpath, String outputpath)
specifier|protected
specifier|static
name|Job
name|getJob
parameter_list|(
name|String
name|jobname
parameter_list|,
name|Configuration
name|inputConf
parameter_list|,
name|String
name|inputpath
parameter_list|,
name|String
name|outputpath
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|(
name|inputConf
argument_list|)
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"fileoutputpath"
argument_list|,
name|outputpath
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|outputpath
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
name|outputpath
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
specifier|final
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|,
name|jobname
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|WordCount
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|TokenizerMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setCombinerClass
argument_list|(
name|IntSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|IntSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setInputFormatClass
argument_list|(
name|SequenceFileInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|inputpath
argument_list|)
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
operator|new
name|Path
argument_list|(
name|outputpath
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
block|}
end_class

end_unit

