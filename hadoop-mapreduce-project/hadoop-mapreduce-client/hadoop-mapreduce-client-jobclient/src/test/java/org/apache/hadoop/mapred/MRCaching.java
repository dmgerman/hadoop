begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapred
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapred
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
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
name|*
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
name|mapred
operator|.
name|OutputCollector
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
name|Reducer
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
name|Reporter
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
name|*
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
name|MapReduceBase
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
name|filecache
operator|.
name|DistributedCache
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
name|java
operator|.
name|net
operator|.
name|URI
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

begin_class
DECL|class|MRCaching
specifier|public
class|class
name|MRCaching
block|{
DECL|field|testStr
specifier|static
name|String
name|testStr
init|=
literal|"This is a test file "
operator|+
literal|"used for testing caching "
operator|+
literal|"jars, zip and normal files."
decl_stmt|;
comment|/**    * Using the wordcount example and adding caching to it. The cache    * archives/files are set and then are checked in the map if they have been    * localized or not.    */
DECL|class|MapClass
specifier|public
specifier|static
class|class
name|MapClass
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|LongWritable
argument_list|,
name|Text
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|field|conf
name|JobConf
name|conf
decl_stmt|;
DECL|field|one
specifier|private
specifier|final
specifier|static
name|IntWritable
name|one
init|=
operator|new
name|IntWritable
argument_list|(
literal|1
argument_list|)
decl_stmt|;
DECL|field|word
specifier|private
name|Text
name|word
init|=
operator|new
name|Text
argument_list|()
decl_stmt|;
DECL|method|configure (JobConf jconf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|jconf
parameter_list|)
block|{
name|conf
operator|=
name|jconf
expr_stmt|;
try|try
block|{
name|Path
index|[]
name|localArchives
init|=
name|DistributedCache
operator|.
name|getLocalCacheArchives
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|Path
index|[]
name|localFiles
init|=
name|DistributedCache
operator|.
name|getLocalCacheFiles
argument_list|(
name|conf
argument_list|)
decl_stmt|;
comment|// read the cached files (unzipped, unjarred and text)
comment|// and put it into a single file TEST_ROOT_DIR/test.txt
name|String
name|TEST_ROOT_DIR
init|=
name|jconf
operator|.
name|get
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"file:///"
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Path
name|fileOut
init|=
operator|new
name|Path
argument_list|(
name|file
argument_list|,
literal|"test.txt"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|fileOut
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|fileOut
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
name|localArchives
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// read out the files from these archives
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|localArchives
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|File
name|txt
init|=
operator|new
name|File
argument_list|(
name|f
argument_list|,
literal|"test.txt"
argument_list|)
decl_stmt|;
name|FileInputStream
name|fin
init|=
operator|new
name|FileInputStream
argument_list|(
name|txt
argument_list|)
decl_stmt|;
name|DataInputStream
name|din
init|=
operator|new
name|DataInputStream
argument_list|(
name|fin
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|din
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|din
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|localFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// read out the files from these archives
name|File
name|txt
init|=
operator|new
name|File
argument_list|(
name|localFiles
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
name|FileInputStream
name|fin
init|=
operator|new
name|FileInputStream
argument_list|(
name|txt
argument_list|)
decl_stmt|;
name|DataInputStream
name|din
init|=
operator|new
name|DataInputStream
argument_list|(
name|fin
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|din
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|map (LongWritable key, Text value, OutputCollector<Text, IntWritable> output, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|LongWritable
name|key
parameter_list|,
name|Text
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|IntWritable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|line
init|=
name|value
operator|.
name|toString
argument_list|()
decl_stmt|;
name|StringTokenizer
name|itr
init|=
operator|new
name|StringTokenizer
argument_list|(
name|line
argument_list|)
decl_stmt|;
while|while
condition|(
name|itr
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|word
operator|.
name|set
argument_list|(
name|itr
operator|.
name|nextToken
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|collect
argument_list|(
name|word
argument_list|,
name|one
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * Using the wordcount example and adding caching to it. The cache    * archives/files are set and then are checked in the map if they have been    * symlinked or not.    */
DECL|class|MapClass2
specifier|public
specifier|static
class|class
name|MapClass2
extends|extends
name|MapClass
block|{
DECL|field|conf
name|JobConf
name|conf
decl_stmt|;
DECL|method|configure (JobConf jconf)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|jconf
parameter_list|)
block|{
name|conf
operator|=
name|jconf
expr_stmt|;
try|try
block|{
comment|// read the cached files (unzipped, unjarred and text)
comment|// and put it into a single file TEST_ROOT_DIR/test.txt
name|String
name|TEST_ROOT_DIR
init|=
name|jconf
operator|.
name|get
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
decl_stmt|;
name|Path
name|file
init|=
operator|new
name|Path
argument_list|(
literal|"file:///"
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
decl_stmt|;
name|FileSystem
name|fs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|file
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|file
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|Path
name|fileOut
init|=
operator|new
name|Path
argument_list|(
name|file
argument_list|,
literal|"test.txt"
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|fileOut
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DataOutputStream
name|out
init|=
name|fs
operator|.
name|create
argument_list|(
name|fileOut
argument_list|)
decl_stmt|;
name|String
index|[]
name|symlinks
init|=
operator|new
name|String
index|[
literal|6
index|]
decl_stmt|;
name|symlinks
index|[
literal|0
index|]
operator|=
literal|"."
expr_stmt|;
name|symlinks
index|[
literal|1
index|]
operator|=
literal|"testjar"
expr_stmt|;
name|symlinks
index|[
literal|2
index|]
operator|=
literal|"testzip"
expr_stmt|;
name|symlinks
index|[
literal|3
index|]
operator|=
literal|"testtgz"
expr_stmt|;
name|symlinks
index|[
literal|4
index|]
operator|=
literal|"testtargz"
expr_stmt|;
name|symlinks
index|[
literal|5
index|]
operator|=
literal|"testtar"
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|symlinks
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
comment|// read out the files from these archives
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|symlinks
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|File
name|txt
init|=
operator|new
name|File
argument_list|(
name|f
argument_list|,
literal|"test.txt"
argument_list|)
decl_stmt|;
name|FileInputStream
name|fin
init|=
operator|new
name|FileInputStream
argument_list|(
name|txt
argument_list|)
decl_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fin
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|str
init|=
name|reader
operator|.
name|readLine
argument_list|()
decl_stmt|;
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|out
operator|.
name|writeBytes
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ie
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|StringUtils
operator|.
name|stringifyException
argument_list|(
name|ie
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**    * A reducer class that just emits the sum of the input values.    */
DECL|class|ReduceClass
specifier|public
specifier|static
class|class
name|ReduceClass
extends|extends
name|MapReduceBase
implements|implements
name|Reducer
argument_list|<
name|Text
argument_list|,
name|IntWritable
argument_list|,
name|Text
argument_list|,
name|IntWritable
argument_list|>
block|{
DECL|method|reduce (Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|Text
name|key
parameter_list|,
name|Iterator
argument_list|<
name|IntWritable
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|Text
argument_list|,
name|IntWritable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|sum
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|values
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|sum
operator|+=
name|values
operator|.
name|next
argument_list|()
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|output
operator|.
name|collect
argument_list|(
name|key
argument_list|,
operator|new
name|IntWritable
argument_list|(
name|sum
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|TestResult
specifier|public
specifier|static
class|class
name|TestResult
block|{
DECL|field|job
specifier|public
name|RunningJob
name|job
decl_stmt|;
DECL|field|isOutputOk
specifier|public
name|boolean
name|isOutputOk
decl_stmt|;
DECL|method|TestResult (RunningJob job, boolean isOutputOk)
name|TestResult
parameter_list|(
name|RunningJob
name|job
parameter_list|,
name|boolean
name|isOutputOk
parameter_list|)
block|{
name|this
operator|.
name|job
operator|=
name|job
expr_stmt|;
name|this
operator|.
name|isOutputOk
operator|=
name|isOutputOk
expr_stmt|;
block|}
block|}
DECL|method|setupCache (String cacheDir, FileSystem fs)
specifier|static
name|void
name|setupCache
parameter_list|(
name|String
name|cacheDir
parameter_list|,
name|FileSystem
name|fs
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|localPath
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|txtPath
init|=
operator|new
name|Path
argument_list|(
name|localPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test.txt"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|jarPath
init|=
operator|new
name|Path
argument_list|(
name|localPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test.jar"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|zipPath
init|=
operator|new
name|Path
argument_list|(
name|localPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test.zip"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|tarPath
init|=
operator|new
name|Path
argument_list|(
name|localPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test.tgz"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|tarPath1
init|=
operator|new
name|Path
argument_list|(
name|localPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test.tar.gz"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|tarPath2
init|=
operator|new
name|Path
argument_list|(
name|localPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"test.tar"
argument_list|)
argument_list|)
decl_stmt|;
name|Path
name|cachePath
init|=
operator|new
name|Path
argument_list|(
name|cacheDir
argument_list|)
decl_stmt|;
name|fs
operator|.
name|delete
argument_list|(
name|cachePath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|cachePath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|cachePath
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|txtPath
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|jarPath
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|zipPath
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|tarPath
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|tarPath1
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
name|fs
operator|.
name|copyFromLocalFile
argument_list|(
name|tarPath2
argument_list|,
name|cachePath
argument_list|)
expr_stmt|;
block|}
DECL|method|launchMRCache (String indir, String outdir, String cacheDir, JobConf conf, String input)
specifier|public
specifier|static
name|TestResult
name|launchMRCache
parameter_list|(
name|String
name|indir
parameter_list|,
name|String
name|outdir
parameter_list|,
name|String
name|cacheDir
parameter_list|,
name|JobConf
name|conf
parameter_list|,
name|String
name|input
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|TEST_ROOT_DIR
init|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|replace
argument_list|(
literal|' '
argument_list|,
literal|'+'
argument_list|)
decl_stmt|;
comment|//if (TEST_ROOT_DIR.startsWith("C:")) TEST_ROOT_DIR = "/tmp";
name|conf
operator|.
name|set
argument_list|(
literal|"test.build.data"
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|indir
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|outdir
argument_list|)
decl_stmt|;
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
name|fs
operator|.
name|delete
argument_list|(
name|outDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|fs
operator|.
name|mkdirs
argument_list|(
name|inDir
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Mkdirs failed to create "
operator|+
name|inDir
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"HERE:"
operator|+
name|inDir
argument_list|)
expr_stmt|;
name|DataOutputStream
name|file
init|=
name|fs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|inDir
argument_list|,
literal|"part-0"
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|input
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|conf
operator|.
name|setJobName
argument_list|(
literal|"cachetest"
argument_list|)
expr_stmt|;
comment|// the keys are words (strings)
name|conf
operator|.
name|setOutputKeyClass
argument_list|(
name|Text
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// the values are counts (ints)
name|conf
operator|.
name|setOutputValueClass
argument_list|(
name|IntWritable
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setCombinerClass
argument_list|(
name|MRCaching
operator|.
name|ReduceClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|MRCaching
operator|.
name|ReduceClass
operator|.
name|class
argument_list|)
expr_stmt|;
name|FileInputFormat
operator|.
name|setInputPaths
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|)
expr_stmt|;
name|FileOutputFormat
operator|.
name|setOutputPath
argument_list|(
name|conf
argument_list|,
name|outDir
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumMapTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setNumReduceTasks
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setSpeculativeExecution
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|URI
index|[]
name|uris
init|=
operator|new
name|URI
index|[
literal|6
index|]
decl_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|MRCaching
operator|.
name|MapClass2
operator|.
name|class
argument_list|)
expr_stmt|;
name|uris
index|[
literal|0
index|]
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|cacheDir
operator|+
literal|"/test.txt"
argument_list|)
expr_stmt|;
name|uris
index|[
literal|1
index|]
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|cacheDir
operator|+
literal|"/test.jar"
argument_list|)
expr_stmt|;
name|uris
index|[
literal|2
index|]
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|cacheDir
operator|+
literal|"/test.zip"
argument_list|)
expr_stmt|;
name|uris
index|[
literal|3
index|]
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|cacheDir
operator|+
literal|"/test.tgz"
argument_list|)
expr_stmt|;
name|uris
index|[
literal|4
index|]
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|cacheDir
operator|+
literal|"/test.tar.gz"
argument_list|)
expr_stmt|;
name|uris
index|[
literal|5
index|]
operator|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|resolve
argument_list|(
name|cacheDir
operator|+
literal|"/test.tar"
argument_list|)
expr_stmt|;
name|DistributedCache
operator|.
name|addCacheFile
argument_list|(
name|uris
index|[
literal|0
index|]
argument_list|,
name|conf
argument_list|)
expr_stmt|;
comment|// Save expected file sizes
name|long
index|[]
name|fileSizes
init|=
operator|new
name|long
index|[
literal|1
index|]
decl_stmt|;
name|fileSizes
index|[
literal|0
index|]
operator|=
name|fs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|uris
index|[
literal|0
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getLen
argument_list|()
expr_stmt|;
name|long
index|[]
name|archiveSizes
init|=
operator|new
name|long
index|[
literal|5
index|]
decl_stmt|;
comment|// track last 5
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|6
condition|;
name|i
operator|++
control|)
block|{
name|DistributedCache
operator|.
name|addCacheArchive
argument_list|(
name|uris
index|[
name|i
index|]
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|archiveSizes
index|[
name|i
operator|-
literal|1
index|]
operator|=
comment|// starting with second archive
name|fs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|uris
index|[
name|i
index|]
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getLen
argument_list|()
expr_stmt|;
block|}
name|RunningJob
name|job
init|=
name|JobClient
operator|.
name|runJob
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
comment|// after the job ran check to see if the input from the localized cache
comment|// match the real string. check if there are 3 instances or not.
name|Path
name|result
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
operator|+
literal|"/test.txt"
argument_list|)
decl_stmt|;
block|{
name|BufferedReader
name|file
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
operator|.
name|open
argument_list|(
name|result
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
init|=
name|file
operator|.
name|readLine
argument_list|()
decl_stmt|;
while|while
condition|(
name|line
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|testStr
operator|.
name|equals
argument_list|(
name|line
argument_list|)
condition|)
return|return
operator|new
name|TestResult
argument_list|(
name|job
argument_list|,
literal|false
argument_list|)
return|;
name|count
operator|++
expr_stmt|;
name|line
operator|=
name|file
operator|.
name|readLine
argument_list|()
expr_stmt|;
block|}
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|count
operator|!=
literal|6
condition|)
return|return
operator|new
name|TestResult
argument_list|(
name|job
argument_list|,
literal|false
argument_list|)
return|;
comment|// Check to ensure the filesizes of files in DC were correctly saved.
comment|// Note, the underlying job clones the original conf before determine
comment|// various stats (timestamps etc.), so we have to getConfiguration here.
name|validateCacheFileSizes
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|fileSizes
argument_list|,
name|MRJobConfig
operator|.
name|CACHE_FILES_SIZES
argument_list|)
expr_stmt|;
name|validateCacheFileSizes
argument_list|(
name|job
operator|.
name|getConfiguration
argument_list|()
argument_list|,
name|archiveSizes
argument_list|,
name|MRJobConfig
operator|.
name|CACHE_ARCHIVES_SIZES
argument_list|)
expr_stmt|;
return|return
operator|new
name|TestResult
argument_list|(
name|job
argument_list|,
literal|true
argument_list|)
return|;
block|}
DECL|method|validateCacheFileSizes (Configuration job, long[] expectedSizes, String configKey)
specifier|private
specifier|static
name|void
name|validateCacheFileSizes
parameter_list|(
name|Configuration
name|job
parameter_list|,
name|long
index|[]
name|expectedSizes
parameter_list|,
name|String
name|configKey
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|configValues
init|=
name|job
operator|.
name|get
argument_list|(
name|configKey
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|configKey
operator|+
literal|" -> "
operator|+
name|configValues
argument_list|)
expr_stmt|;
name|String
index|[]
name|realSizes
init|=
name|StringUtils
operator|.
name|getStrings
argument_list|(
name|configValues
argument_list|)
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"Number of files for "
operator|+
name|configKey
argument_list|,
name|expectedSizes
operator|.
name|length
argument_list|,
name|realSizes
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expectedSizes
operator|.
name|length
condition|;
operator|++
name|i
control|)
block|{
name|long
name|actual
init|=
name|Long
operator|.
name|valueOf
argument_list|(
name|realSizes
index|[
name|i
index|]
argument_list|)
decl_stmt|;
name|long
name|expected
init|=
name|expectedSizes
index|[
name|i
index|]
decl_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"File "
operator|+
name|i
operator|+
literal|" for "
operator|+
name|configKey
argument_list|,
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

