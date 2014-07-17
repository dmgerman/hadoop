begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred.nativetask.nonsorttest
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
name|nonsorttest
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
name|LongWritable
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
name|TextOutputFormat
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
DECL|class|NonSortTest
specifier|public
class|class
name|NonSortTest
block|{
annotation|@
name|Test
DECL|method|nonSortTest ()
specifier|public
name|void
name|nonSortTest
parameter_list|()
throws|throws
name|Exception
block|{
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
name|NONSORT_TEST_CONF
argument_list|)
expr_stmt|;
name|nativeConf
operator|.
name|set
argument_list|(
name|TestConstants
operator|.
name|NATIVETASK_MAP_OUTPUT_SORT
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|String
name|inputpath
init|=
name|nativeConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_INPUTDIR
argument_list|)
decl_stmt|;
name|String
name|outputpath
init|=
name|nativeConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_NATIVE_OUTPUT
argument_list|)
decl_stmt|;
specifier|final
name|Job
name|nativeNonSort
init|=
name|getJob
argument_list|(
name|nativeConf
argument_list|,
literal|"NativeNonSort"
argument_list|,
name|inputpath
argument_list|,
name|outputpath
argument_list|)
decl_stmt|;
name|nativeNonSort
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|NONSORT_TEST_CONF
argument_list|)
expr_stmt|;
name|inputpath
operator|=
name|normalConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_INPUTDIR
argument_list|)
expr_stmt|;
name|outputpath
operator|=
name|normalConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_NORMAL_OUTPUT
argument_list|)
expr_stmt|;
specifier|final
name|Job
name|hadoopWithSort
init|=
name|getJob
argument_list|(
name|normalConf
argument_list|,
literal|"NormalJob"
argument_list|,
name|inputpath
argument_list|,
name|outputpath
argument_list|)
decl_stmt|;
name|hadoopWithSort
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
name|nativeConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_NATIVE_OUTPUT
argument_list|)
argument_list|,
name|normalConf
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_NORMAL_OUTPUT
argument_list|)
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
name|configuration
init|=
operator|new
name|ScenarioConfiguration
argument_list|()
decl_stmt|;
name|configuration
operator|.
name|addNonSortTestConf
argument_list|()
expr_stmt|;
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
specifier|final
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|configuration
operator|.
name|get
argument_list|(
name|TestConstants
operator|.
name|NONSORT_TEST_INPUTDIR
argument_list|)
argument_list|)
decl_stmt|;
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
name|configuration
operator|.
name|getInt
argument_list|(
literal|"nativetask.nonsorttest.filesize"
argument_list|,
literal|10000000
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
name|configuration
argument_list|)
operator|.
name|createSequenceTestFile
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|getJob (Configuration conf, String jobName, String inputpath, String outputpath)
specifier|private
name|Job
name|getJob
parameter_list|(
name|Configuration
name|conf
parameter_list|,
name|String
name|jobName
parameter_list|,
name|String
name|inputpath
parameter_list|,
name|String
name|outputpath
parameter_list|)
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
operator|new
name|Job
argument_list|(
name|conf
argument_list|,
name|jobName
argument_list|)
decl_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|NonSortTestMR
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|NonSortTestMR
operator|.
name|Map
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|NonSortTestMR
operator|.
name|KeyHashSumReduce
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
name|setMapOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setOutputValueClass
argument_list|(
name|LongWritable
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
name|job
operator|.
name|setOutputFormatClass
argument_list|(
name|TextOutputFormat
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

