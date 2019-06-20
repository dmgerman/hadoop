begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.s3a.commit
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
name|commit
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
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
name|Collections
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
name|Set
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|rules
operator|.
name|TemporaryFolder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
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
name|S3AUtils
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
name|commit
operator|.
name|files
operator|.
name|SuccessData
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
name|Mapper
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
name|TextInputFormat
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
name|util
operator|.
name|DurationInfo
import|;
end_import

begin_import
import|import static
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
name|commit
operator|.
name|InternalCommitterConstants
operator|.
name|FS_S3A_COMMITTER_STAGING_UUID
import|;
end_import

begin_comment
comment|/**  * Test for an MR Job with all the different committers.  */
end_comment

begin_class
DECL|class|AbstractITCommitMRJob
specifier|public
specifier|abstract
class|class
name|AbstractITCommitMRJob
extends|extends
name|AbstractYarnClusterITest
block|{
DECL|field|LOG
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|AbstractITCommitMRJob
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Rule
DECL|field|temp
specifier|public
specifier|final
name|TemporaryFolder
name|temp
init|=
operator|new
name|TemporaryFolder
argument_list|()
decl_stmt|;
annotation|@
name|Test
DECL|method|testMRJob ()
specifier|public
name|void
name|testMRJob
parameter_list|()
throws|throws
name|Exception
block|{
name|describe
argument_list|(
literal|"Run a simple MR Job"
argument_list|)
expr_stmt|;
name|S3AFileSystem
name|fs
init|=
name|getFileSystem
argument_list|()
decl_stmt|;
comment|// final dest is in S3A
name|Path
name|outputPath
init|=
name|path
argument_list|(
name|getMethodName
argument_list|()
argument_list|)
decl_stmt|;
comment|// create and delete to force in a tombstone marker -see HADOOP-16207
name|fs
operator|.
name|mkdirs
argument_list|(
name|outputPath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|outputPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|String
name|commitUUID
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|suffix
init|=
name|isUniqueFilenames
argument_list|()
condition|?
operator|(
literal|"-"
operator|+
name|commitUUID
operator|)
else|:
literal|""
decl_stmt|;
name|int
name|numFiles
init|=
name|getTestFileCount
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|expectedFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|numFiles
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|expectedKeys
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
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
name|numFiles
condition|;
name|i
operator|+=
literal|1
control|)
block|{
name|File
name|file
init|=
name|temp
operator|.
name|newFile
argument_list|(
name|i
operator|+
literal|".text"
argument_list|)
decl_stmt|;
try|try
init|(
name|FileOutputStream
name|out
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
init|)
block|{
name|out
operator|.
name|write
argument_list|(
operator|(
literal|"file "
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|filename
init|=
name|String
operator|.
name|format
argument_list|(
literal|"part-m-%05d%s"
argument_list|,
name|i
argument_list|,
name|suffix
argument_list|)
decl_stmt|;
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|outputPath
argument_list|,
name|filename
argument_list|)
decl_stmt|;
name|expectedFiles
operator|.
name|add
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|expectedKeys
operator|.
name|add
argument_list|(
literal|"/"
operator|+
name|fs
operator|.
name|pathToKey
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|expectedFiles
argument_list|)
expr_stmt|;
name|Job
name|mrJob
init|=
name|createJob
argument_list|()
decl_stmt|;
name|JobConf
name|jobConf
init|=
operator|(
name|JobConf
operator|)
name|mrJob
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|mrJob
operator|.
name|setOutputFormatClass
argument_list|(
name|LoggingTextOutputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|mrJob
argument_list|,
name|outputPath
argument_list|)
expr_stmt|;
name|File
name|mockResultsFile
init|=
name|temp
operator|.
name|newFile
argument_list|(
literal|"committer.bin"
argument_list|)
decl_stmt|;
name|mockResultsFile
operator|.
name|delete
argument_list|()
expr_stmt|;
name|String
name|committerPath
init|=
literal|"file:"
operator|+
name|mockResultsFile
decl_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
literal|"mock-results-file"
argument_list|,
name|committerPath
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
name|FS_S3A_COMMITTER_STAGING_UUID
argument_list|,
name|commitUUID
argument_list|)
expr_stmt|;
name|mrJob
operator|.
name|setInputFormatClass
argument_list|(
name|TextInputFormat
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|addInputPath
argument_list|(
name|mrJob
argument_list|,
operator|new
name|Path
argument_list|(
name|temp
operator|.
name|getRoot
argument_list|()
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|mrJob
operator|.
name|setMapperClass
argument_list|(
name|MapClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|mrJob
operator|.
name|setNumReduceTasks
argument_list|(
literal|0
argument_list|)
expr_stmt|;
comment|// an attempt to set up log4j properly, which clearly doesn't work
name|URL
name|log4j
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"log4j.properties"
argument_list|)
decl_stmt|;
if|if
condition|(
name|log4j
operator|!=
literal|null
operator|&&
name|log4j
operator|.
name|getProtocol
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
name|Path
name|log4jPath
init|=
operator|new
name|Path
argument_list|(
name|log4j
operator|.
name|toURI
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using log4j path {}"
argument_list|,
name|log4jPath
argument_list|)
expr_stmt|;
name|mrJob
operator|.
name|addFileToClassPath
argument_list|(
name|log4jPath
argument_list|)
expr_stmt|;
name|String
name|sysprops
init|=
name|String
operator|.
name|format
argument_list|(
literal|"-Xmx256m -Dlog4j.configuration=%s"
argument_list|,
name|log4j
argument_list|)
decl_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
argument_list|,
name|sysprops
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
argument_list|,
name|sysprops
argument_list|)
expr_stmt|;
name|jobConf
operator|.
name|set
argument_list|(
literal|"yarn.app.mapreduce.am.command-opts"
argument_list|,
name|sysprops
argument_list|)
expr_stmt|;
block|}
name|applyCustomConfigOptions
argument_list|(
name|jobConf
argument_list|)
expr_stmt|;
comment|// fail fast if anything goes wrong
name|mrJob
operator|.
name|setMaxMapAttempts
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|mrJob
operator|.
name|submit
argument_list|()
expr_stmt|;
try|try
init|(
name|DurationInfo
name|ignore
init|=
operator|new
name|DurationInfo
argument_list|(
name|LOG
argument_list|,
literal|"Job Execution"
argument_list|)
init|)
block|{
name|boolean
name|succeeded
init|=
name|mrJob
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"MR job failed"
argument_list|,
name|succeeded
argument_list|)
expr_stmt|;
block|}
name|waitForConsistency
argument_list|()
expr_stmt|;
name|assertIsDirectory
argument_list|(
name|outputPath
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|results
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|outputPath
argument_list|,
name|S3AUtils
operator|.
name|HIDDEN_FILE_FILTER
argument_list|)
decl_stmt|;
name|int
name|fileCount
init|=
name|results
operator|.
name|length
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|actualFiles
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|fileCount
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No files in output directory"
argument_list|,
name|fileCount
operator|!=
literal|0
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Found {} files"
argument_list|,
name|fileCount
argument_list|)
expr_stmt|;
for|for
control|(
name|FileStatus
name|result
range|:
name|results
control|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"result: {}"
argument_list|,
name|result
argument_list|)
expr_stmt|;
name|actualFiles
operator|.
name|add
argument_list|(
name|result
operator|.
name|getPath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|actualFiles
argument_list|)
expr_stmt|;
name|SuccessData
name|successData
init|=
name|validateSuccessFile
argument_list|(
name|fs
argument_list|,
name|outputPath
argument_list|,
name|committerName
argument_list|()
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|successFiles
init|=
name|successData
operator|.
name|getFilenames
argument_list|()
decl_stmt|;
name|String
name|commitData
init|=
name|successData
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
literal|"No filenames in "
operator|+
name|commitData
argument_list|,
operator|!
name|successFiles
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Should commit the expected files"
argument_list|,
name|expectedFiles
argument_list|,
name|actualFiles
argument_list|)
expr_stmt|;
name|Set
argument_list|<
name|String
argument_list|>
name|summaryKeys
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|summaryKeys
operator|.
name|addAll
argument_list|(
name|successFiles
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Summary keyset doesn't list the the expected paths "
operator|+
name|commitData
argument_list|,
name|expectedKeys
argument_list|,
name|summaryKeys
argument_list|)
expr_stmt|;
name|assertPathDoesNotExist
argument_list|(
literal|"temporary dir"
argument_list|,
operator|new
name|Path
argument_list|(
name|outputPath
argument_list|,
name|CommitConstants
operator|.
name|TEMPORARY
argument_list|)
argument_list|)
expr_stmt|;
name|customPostExecutionValidation
argument_list|(
name|outputPath
argument_list|,
name|successData
argument_list|)
expr_stmt|;
block|}
comment|/**    *  Test Mapper.    *  This is executed in separate process, and must not make any assumptions    *  about external state.    */
DECL|class|MapClass
specifier|public
specifier|static
class|class
name|MapClass
extends|extends
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|LongWritable
argument_list|,
name|Text
argument_list|>
block|{
DECL|field|operations
specifier|private
name|int
name|operations
decl_stmt|;
DECL|field|id
specifier|private
name|String
name|id
init|=
literal|""
decl_stmt|;
DECL|field|l
specifier|private
name|LongWritable
name|l
init|=
operator|new
name|LongWritable
argument_list|()
decl_stmt|;
DECL|field|t
specifier|private
name|Text
name|t
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
annotation|@
name|Override
DECL|method|setup (Context context)
specifier|protected
name|void
name|setup
parameter_list|(
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
name|super
operator|.
name|setup
argument_list|(
name|context
argument_list|)
expr_stmt|;
comment|// force in Log4J logging
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|BasicConfigurator
operator|.
name|configure
argument_list|()
expr_stmt|;
name|boolean
name|scaleMap
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getBoolean
argument_list|(
name|KEY_SCALE_TESTS_ENABLED
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|operations
operator|=
name|scaleMap
condition|?
name|SCALE_TEST_KEYS
else|:
name|BASE_TEST_KEYS
expr_stmt|;
name|id
operator|=
name|context
operator|.
name|getTaskAttemptID
argument_list|()
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|map (LongWritable key, Text value, Context context)
specifier|protected
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|Context
name|context
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|operations
condition|;
name|i
operator|++
control|)
block|{
name|l
operator|.
name|set
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|t
operator|.
name|set
argument_list|(
name|String
operator|.
name|format
argument_list|(
literal|"%s:%05d"
argument_list|,
name|id
argument_list|,
name|i
argument_list|)
argument_list|)
expr_stmt|;
name|context
operator|.
name|write
argument_list|(
name|l
argument_list|,
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

