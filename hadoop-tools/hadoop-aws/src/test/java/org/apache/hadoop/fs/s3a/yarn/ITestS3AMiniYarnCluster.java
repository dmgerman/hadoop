begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements. See the NOTICE file distributed with this  * work for additional information regarding copyright ownership. The ASF  * licenses this file to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT  * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the  * License for the specific language governing permissions and limitations under  * the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.yarn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|s3a
operator|.
name|yarn
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|EnumSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|examples
operator|.
name|WordCount
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
name|CreateFlag
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
name|FSDataOutputStream
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
name|FileContext
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
name|contract
operator|.
name|ContractTestUtils
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
name|s3a
operator|.
name|AbstractS3ATestBase
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
name|s3a
operator|.
name|S3AFileSystem
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
name|s3a
operator|.
name|S3ATestUtils
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
name|server
operator|.
name|MiniYARNCluster
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
comment|/**  * Tests that S3A is usable through a YARN application.  */
end_comment

begin_class
DECL|class|ITestS3AMiniYarnCluster
specifier|public
class|class
name|ITestS3AMiniYarnCluster
extends|extends
name|AbstractS3ATestBase
block|{
DECL|field|conf
specifier|private
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|YarnConfiguration
argument_list|()
decl_stmt|;
DECL|field|fs
specifier|private
name|S3AFileSystem
name|fs
decl_stmt|;
DECL|field|yarnCluster
specifier|private
name|MiniYARNCluster
name|yarnCluster
decl_stmt|;
DECL|field|rootPath
specifier|private
name|Path
name|rootPath
decl_stmt|;
annotation|@
name|Override
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setup
argument_list|()
expr_stmt|;
name|fs
operator|=
name|S3ATestUtils
operator|.
name|createTestFileSystem
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|rootPath
operator|=
name|path
argument_list|(
literal|"MiniClusterWordCount"
argument_list|)
expr_stmt|;
name|Path
name|workingDir
init|=
name|path
argument_list|(
literal|"working"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|setWorkingDirectory
argument_list|(
name|workingDir
argument_list|)
expr_stmt|;
name|fs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
literal|"input/"
argument_list|)
argument_list|)
expr_stmt|;
name|yarnCluster
operator|=
operator|new
name|MiniYARNCluster
argument_list|(
literal|"MiniClusterWordCount"
argument_list|,
comment|// testName
literal|1
argument_list|,
comment|// number of node managers
literal|1
argument_list|,
comment|// number of local log dirs per node manager
literal|1
argument_list|)
expr_stmt|;
comment|// number of hdfs dirs per node manager
name|yarnCluster
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|yarnCluster
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|yarnCluster
operator|!=
literal|null
condition|)
block|{
name|yarnCluster
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
name|super
operator|.
name|teardown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testWithMiniCluster ()
specifier|public
name|void
name|testWithMiniCluster
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|input
init|=
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
literal|"input/in.txt"
argument_list|)
decl_stmt|;
name|input
operator|=
name|input
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|Path
name|output
init|=
operator|new
name|Path
argument_list|(
name|rootPath
argument_list|,
literal|"output/"
argument_list|)
decl_stmt|;
name|output
operator|=
name|output
operator|.
name|makeQualified
argument_list|(
name|fs
operator|.
name|getUri
argument_list|()
argument_list|,
name|fs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|writeStringToFile
argument_list|(
name|input
argument_list|,
literal|"first line\nsecond line\nthird line"
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|conf
argument_list|,
literal|"word count"
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
name|WordCount
operator|.
name|TokenizerMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setCombinerClass
argument_list|(
name|WordCount
operator|.
name|IntSumReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|WordCount
operator|.
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
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|job
argument_list|,
name|input
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|job
argument_list|,
name|output
argument_list|)
expr_stmt|;
name|int
name|exitCode
init|=
operator|(
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
condition|?
literal|0
else|:
literal|1
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Returned error code."
argument_list|,
literal|0
argument_list|,
name|exitCode
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
name|output
argument_list|,
literal|"_SUCCESS"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|outputAsStr
init|=
name|readStringFromFile
argument_list|(
operator|new
name|Path
argument_list|(
name|output
argument_list|,
literal|"part-r-00000"
argument_list|)
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|resAsMap
init|=
name|getResultAsMap
argument_list|(
name|outputAsStr
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|resAsMap
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|resAsMap
operator|.
name|get
argument_list|(
literal|"first"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|resAsMap
operator|.
name|get
argument_list|(
literal|"second"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
operator|(
name|int
operator|)
name|resAsMap
operator|.
name|get
argument_list|(
literal|"third"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
operator|(
name|int
operator|)
name|resAsMap
operator|.
name|get
argument_list|(
literal|"line"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * helper method.    */
DECL|method|getResultAsMap (String outputAsStr)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|getResultAsMap
parameter_list|(
name|String
name|outputAsStr
parameter_list|)
throws|throws
name|IOException
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|line
range|:
name|outputAsStr
operator|.
name|split
argument_list|(
literal|"\n"
argument_list|)
control|)
block|{
name|String
index|[]
name|tokens
init|=
name|line
operator|.
name|split
argument_list|(
literal|"\t"
argument_list|)
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
name|tokens
index|[
literal|0
index|]
argument_list|,
name|Integer
operator|.
name|parseInt
argument_list|(
name|tokens
index|[
literal|1
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
comment|/**    * helper method.    */
DECL|method|writeStringToFile (Path path, String string)
specifier|private
name|void
name|writeStringToFile
parameter_list|(
name|Path
name|path
parameter_list|,
name|String
name|string
parameter_list|)
throws|throws
name|IOException
block|{
name|FileContext
name|fc
init|=
name|S3ATestUtils
operator|.
name|createTestFileContext
argument_list|(
name|conf
argument_list|)
decl_stmt|;
try|try
init|(
name|FSDataOutputStream
name|file
init|=
name|fc
operator|.
name|create
argument_list|(
name|path
argument_list|,
name|EnumSet
operator|.
name|of
argument_list|(
name|CreateFlag
operator|.
name|CREATE
argument_list|)
argument_list|)
init|)
block|{
name|file
operator|.
name|write
argument_list|(
name|string
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * helper method.    */
DECL|method|readStringFromFile (Path path)
specifier|private
name|String
name|readStringFromFile
parameter_list|(
name|Path
name|path
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|ContractTestUtils
operator|.
name|readBytesToString
argument_list|(
name|fs
argument_list|,
name|path
argument_list|,
operator|(
name|int
operator|)
name|fs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
operator|.
name|getLen
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

