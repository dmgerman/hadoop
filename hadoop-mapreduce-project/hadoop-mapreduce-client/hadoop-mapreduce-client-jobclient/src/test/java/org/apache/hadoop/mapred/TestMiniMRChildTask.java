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
name|assertNotNull
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
import|;
end_import

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|fs
operator|.
name|permission
operator|.
name|FsPermission
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
name|hdfs
operator|.
name|MiniDFSCluster
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
name|io
operator|.
name|Writable
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
name|WritableComparable
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
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
operator|.
name|v2
operator|.
name|MiniMRYarnCluster
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
name|Shell
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
name|BeforeClass
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
comment|/**  * Class to test mapred task's   *   - temp directory  *   - child env  */
end_comment

begin_class
DECL|class|TestMiniMRChildTask
specifier|public
class|class
name|TestMiniMRChildTask
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
name|TestMiniMRChildTask
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|OLD_CONFIGS
specifier|private
specifier|final
specifier|static
name|String
name|OLD_CONFIGS
init|=
literal|"test.old.configs"
decl_stmt|;
DECL|field|TASK_OPTS_VAL
specifier|private
specifier|final
specifier|static
name|String
name|TASK_OPTS_VAL
init|=
literal|"-Xmx200m"
decl_stmt|;
DECL|field|MAP_OPTS_VAL
specifier|private
specifier|final
specifier|static
name|String
name|MAP_OPTS_VAL
init|=
literal|"-Xmx200m"
decl_stmt|;
DECL|field|REDUCE_OPTS_VAL
specifier|private
specifier|final
specifier|static
name|String
name|REDUCE_OPTS_VAL
init|=
literal|"-Xmx300m"
decl_stmt|;
DECL|field|mr
specifier|private
specifier|static
name|MiniMRYarnCluster
name|mr
decl_stmt|;
DECL|field|dfs
specifier|private
specifier|static
name|MiniDFSCluster
name|dfs
decl_stmt|;
DECL|field|fileSys
specifier|private
specifier|static
name|FileSystem
name|fileSys
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
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
decl_stmt|;
static|static
block|{
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|io
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"problem getting local fs"
argument_list|,
name|io
argument_list|)
throw|;
block|}
block|}
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|Path
name|TEST_ROOT_DIR
init|=
name|localFs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
literal|"target"
argument_list|,
name|TestMiniMRChildTask
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"-tmpDir"
argument_list|)
argument_list|)
decl_stmt|;
DECL|field|APP_JAR
specifier|static
name|Path
name|APP_JAR
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"MRAppJar.jar"
argument_list|)
decl_stmt|;
comment|/**    * Map class which checks whether temp directory exists    * and check the value of java.io.tmpdir    * Creates a tempfile and checks whether that is created in     * temp directory specified.    */
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
DECL|field|tmpDir
name|Path
name|tmpDir
decl_stmt|;
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
if|if
condition|(
name|localFs
operator|.
name|exists
argument_list|(
name|tmpDir
argument_list|)
condition|)
block|{         }
else|else
block|{
name|fail
argument_list|(
literal|"Temp directory "
operator|+
name|tmpDir
operator|+
literal|" doesnt exist."
argument_list|)
expr_stmt|;
block|}
name|File
name|tmpFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
block|}
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|tmpDir
operator|=
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|job
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"IOException in getting localFS"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// configure a job
DECL|method|configure (JobConf conf, Path inDir, Path outDir, String input, Class<? extends Mapper> map, Class<? extends Reducer> reduce)
specifier|private
name|void
name|configure
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|Path
name|inDir
parameter_list|,
name|Path
name|outDir
parameter_list|,
name|String
name|input
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Mapper
argument_list|>
name|map
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Reducer
argument_list|>
name|reduce
parameter_list|)
throws|throws
name|IOException
block|{
comment|// set up the input file system and write input text.
name|FileSystem
name|inFs
init|=
name|inDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|FileSystem
name|outFs
init|=
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|outFs
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
name|inFs
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
comment|// write input into input file
name|DataOutputStream
name|file
init|=
name|inFs
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
comment|// configure the mapred Job which creates a tempfile in map.
name|conf
operator|.
name|setJobName
argument_list|(
literal|"testmap"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setMapperClass
argument_list|(
name|map
argument_list|)
expr_stmt|;
name|conf
operator|.
name|setReducerClass
argument_list|(
name|reduce
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
literal|0
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
name|conf
operator|.
name|set
argument_list|(
literal|"test.build.data"
argument_list|,
name|TEST_ROOT_DIR
argument_list|)
expr_stmt|;
block|}
DECL|method|checkEnv (String envName, String expValue, String mode)
specifier|private
specifier|static
name|void
name|checkEnv
parameter_list|(
name|String
name|envName
parameter_list|,
name|String
name|expValue
parameter_list|,
name|String
name|mode
parameter_list|)
block|{
name|String
name|envValue
init|=
name|System
operator|.
name|getenv
argument_list|(
name|envName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"append"
operator|.
name|equals
argument_list|(
name|mode
argument_list|)
condition|)
block|{
if|if
condition|(
name|envValue
operator|==
literal|null
operator|||
operator|!
name|envValue
operator|.
name|contains
argument_list|(
name|File
operator|.
name|pathSeparator
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Missing env variable"
argument_list|)
throw|;
block|}
else|else
block|{
name|String
index|[]
name|parts
init|=
name|envValue
operator|.
name|trim
argument_list|()
operator|.
name|split
argument_list|(
name|File
operator|.
name|pathSeparator
argument_list|)
decl_stmt|;
comment|// check if the value is appended
if|if
condition|(
operator|!
name|parts
index|[
name|parts
operator|.
name|length
operator|-
literal|1
index|]
operator|.
name|equals
argument_list|(
name|expValue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Wrong env variable in append mode"
argument_list|)
throw|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|envValue
operator|==
literal|null
operator|||
operator|!
name|envValue
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
name|expValue
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Wrong env variable in noappend mode"
argument_list|)
throw|;
block|}
block|}
block|}
comment|// Mappers that simply checks if the desired user env are present or not
DECL|class|EnvCheckMapper
specifier|private
specifier|static
class|class
name|EnvCheckMapper
extends|extends
name|MapReduceBase
implements|implements
name|Mapper
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|,
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
block|{
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|boolean
name|oldConfigs
init|=
name|job
operator|.
name|getBoolean
argument_list|(
name|OLD_CONFIGS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldConfigs
condition|)
block|{
name|String
name|javaOpts
init|=
name|job
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
operator|+
literal|" is null!"
argument_list|,
name|javaOpts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
operator|+
literal|" has value of: "
operator|+
name|javaOpts
argument_list|,
name|javaOpts
argument_list|,
name|TASK_OPTS_VAL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|mapJavaOpts
init|=
name|job
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
operator|+
literal|" is null!"
argument_list|,
name|mapJavaOpts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
operator|+
literal|" has value of: "
operator|+
name|mapJavaOpts
argument_list|,
name|mapJavaOpts
argument_list|,
name|MAP_OPTS_VAL
argument_list|)
expr_stmt|;
block|}
comment|// check if X=y works for an already existing parameter
name|checkEnv
argument_list|(
literal|"LANG"
argument_list|,
literal|"en_us_8859_1"
argument_list|,
literal|"noappend"
argument_list|)
expr_stmt|;
comment|// check if X=/tmp for a new env variable
name|checkEnv
argument_list|(
literal|"MY_PATH"
argument_list|,
literal|"/tmp"
argument_list|,
literal|"noappend"
argument_list|)
expr_stmt|;
comment|// check if X=$X:/tmp works for a new env var and results into :/tmp
name|checkEnv
argument_list|(
literal|"NEW_PATH"
argument_list|,
name|File
operator|.
name|pathSeparator
operator|+
literal|"/tmp"
argument_list|,
literal|"noappend"
argument_list|)
expr_stmt|;
name|String
name|jobLocalDir
init|=
name|job
operator|.
name|get
argument_list|(
name|MRJobConfig
operator|.
name|JOB_LOCAL_DIR
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|MRJobConfig
operator|.
name|JOB_LOCAL_DIR
operator|+
literal|" is null"
argument_list|,
name|jobLocalDir
argument_list|)
expr_stmt|;
block|}
DECL|method|map (WritableComparable key, Writable value, OutputCollector<WritableComparable, Writable> out, Reporter reporter)
specifier|public
name|void
name|map
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Writable
name|value
parameter_list|,
name|OutputCollector
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
name|out
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
DECL|class|EnvCheckReducer
specifier|private
specifier|static
class|class
name|EnvCheckReducer
extends|extends
name|MapReduceBase
implements|implements
name|Reducer
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|,
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
block|{
annotation|@
name|Override
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|configure (JobConf job)
specifier|public
name|void
name|configure
parameter_list|(
name|JobConf
name|job
parameter_list|)
block|{
name|boolean
name|oldConfigs
init|=
name|job
operator|.
name|getBoolean
argument_list|(
name|OLD_CONFIGS
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|oldConfigs
condition|)
block|{
name|String
name|javaOpts
init|=
name|job
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
operator|+
literal|" is null!"
argument_list|,
name|javaOpts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
operator|+
literal|" has value of: "
operator|+
name|javaOpts
argument_list|,
name|javaOpts
argument_list|,
name|TASK_OPTS_VAL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|reduceJavaOpts
init|=
name|job
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
operator|+
literal|" is null!"
argument_list|,
name|reduceJavaOpts
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
operator|+
literal|" has value of: "
operator|+
name|reduceJavaOpts
argument_list|,
name|reduceJavaOpts
argument_list|,
name|REDUCE_OPTS_VAL
argument_list|)
expr_stmt|;
block|}
comment|// check if X=y works for an already existing parameter
name|checkEnv
argument_list|(
literal|"LANG"
argument_list|,
literal|"en_us_8859_1"
argument_list|,
literal|"noappend"
argument_list|)
expr_stmt|;
comment|// check if X=/tmp for a new env variable
name|checkEnv
argument_list|(
literal|"MY_PATH"
argument_list|,
literal|"/tmp"
argument_list|,
literal|"noappend"
argument_list|)
expr_stmt|;
comment|// check if X=$X:/tmp works for a new env var and results into :/tmp
name|checkEnv
argument_list|(
literal|"NEW_PATH"
argument_list|,
name|File
operator|.
name|pathSeparator
operator|+
literal|"/tmp"
argument_list|,
literal|"noappend"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|reduce (WritableComparable key, Iterator<Writable> values, OutputCollector<WritableComparable, Writable> output, Reporter reporter)
specifier|public
name|void
name|reduce
parameter_list|(
name|WritableComparable
name|key
parameter_list|,
name|Iterator
argument_list|<
name|Writable
argument_list|>
name|values
parameter_list|,
name|OutputCollector
argument_list|<
name|WritableComparable
argument_list|,
name|Writable
argument_list|>
name|output
parameter_list|,
name|Reporter
name|reporter
parameter_list|)
throws|throws
name|IOException
block|{     }
block|}
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|IOException
block|{
comment|// create configuration, dfs, file system and mapred cluster
name|dfs
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
operator|new
name|File
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
operator|)
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"MRAppJar "
operator|+
name|MiniMRYarnCluster
operator|.
name|APPJAR
operator|+
literal|" not found. Not running test."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|mr
operator|==
literal|null
condition|)
block|{
name|mr
operator|=
operator|new
name|MiniMRYarnCluster
argument_list|(
name|TestMiniMRChildTask
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|mr
operator|.
name|init
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|mr
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
comment|// Copy MRAppJar and make it private. TODO: FIXME. This is a hack to
comment|// workaround the absent public discache.
name|localFs
operator|.
name|copyFromLocalFile
argument_list|(
operator|new
name|Path
argument_list|(
name|MiniMRYarnCluster
operator|.
name|APPJAR
argument_list|)
argument_list|,
name|APP_JAR
argument_list|)
expr_stmt|;
name|localFs
operator|.
name|setPermission
argument_list|(
name|APP_JAR
argument_list|,
operator|new
name|FsPermission
argument_list|(
literal|"700"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
DECL|method|tearDown ()
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
block|{
comment|// close file system and shut down dfs and mapred cluster
try|try
block|{
if|if
condition|(
name|fileSys
operator|!=
literal|null
condition|)
block|{
name|fileSys
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|dfs
operator|!=
literal|null
condition|)
block|{
name|dfs
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|mr
operator|!=
literal|null
condition|)
block|{
name|mr
operator|.
name|stop
argument_list|()
expr_stmt|;
name|mr
operator|=
literal|null
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"IO exception in closing file system)"
argument_list|)
expr_stmt|;
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test to test if the user set env variables reflect in the child    * processes. Mainly    *   - x=y (x can be a already existing env variable or a new variable)    */
annotation|@
name|Test
DECL|method|testTaskEnv ()
specifier|public
name|void
name|testTaskEnv
parameter_list|()
block|{
try|try
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|baseDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
comment|// initialize input, output directories
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|baseDir
operator|+
literal|"/testing/wc/input1"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|baseDir
operator|+
literal|"/testing/wc/output1"
argument_list|)
decl_stmt|;
name|FileSystem
name|outFs
init|=
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|runTestTaskEnv
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|outFs
operator|.
name|delete
argument_list|(
name|outDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Exception in testing child env"
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test to test if the user set *old* env variables reflect in the child    * processes. Mainly    *   - x=y (x can be a already existing env variable or a new variable)    */
annotation|@
name|Test
DECL|method|testTaskOldEnv ()
specifier|public
name|void
name|testTaskOldEnv
parameter_list|()
block|{
try|try
block|{
name|JobConf
name|conf
init|=
operator|new
name|JobConf
argument_list|(
name|mr
operator|.
name|getConfig
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|baseDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data"
argument_list|)
decl_stmt|;
comment|// initialize input, output directories
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|baseDir
operator|+
literal|"/testing/wc/input1"
argument_list|)
decl_stmt|;
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|baseDir
operator|+
literal|"/testing/wc/output1"
argument_list|)
decl_stmt|;
name|FileSystem
name|outFs
init|=
name|outDir
operator|.
name|getFileSystem
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|runTestTaskEnv
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|outFs
operator|.
name|delete
argument_list|(
name|outDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Exception in testing child env"
argument_list|)
expr_stmt|;
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"deprecation"
argument_list|)
DECL|method|runTestTaskEnv (JobConf config, Path inDir, Path outDir, boolean oldConfigs)
specifier|private
name|void
name|runTestTaskEnv
parameter_list|(
name|JobConf
name|config
parameter_list|,
name|Path
name|inDir
parameter_list|,
name|Path
name|outDir
parameter_list|,
name|boolean
name|oldConfigs
parameter_list|)
throws|throws
name|IOException
throws|,
name|InterruptedException
throws|,
name|ClassNotFoundException
block|{
name|String
name|input
init|=
literal|"The input"
decl_stmt|;
name|configure
argument_list|(
name|config
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
name|input
argument_list|,
name|EnvCheckMapper
operator|.
name|class
argument_list|,
name|EnvCheckReducer
operator|.
name|class
argument_list|)
expr_stmt|;
comment|// test
comment|//  - new SET of new var (MY_PATH)
comment|//  - set of old var (LANG)
comment|//  - append to a new var (NEW_PATH)
name|String
name|mapTaskEnvKey
init|=
name|JobConf
operator|.
name|MAPRED_MAP_TASK_ENV
decl_stmt|;
name|String
name|reduceTaskEnvKey
init|=
name|JobConf
operator|.
name|MAPRED_MAP_TASK_ENV
decl_stmt|;
name|String
name|mapTaskJavaOptsKey
init|=
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
decl_stmt|;
name|String
name|reduceTaskJavaOptsKey
init|=
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
decl_stmt|;
name|String
name|mapTaskJavaOpts
init|=
name|MAP_OPTS_VAL
decl_stmt|;
name|String
name|reduceTaskJavaOpts
init|=
name|REDUCE_OPTS_VAL
decl_stmt|;
name|config
operator|.
name|setBoolean
argument_list|(
name|OLD_CONFIGS
argument_list|,
name|oldConfigs
argument_list|)
expr_stmt|;
if|if
condition|(
name|oldConfigs
condition|)
block|{
name|mapTaskEnvKey
operator|=
name|reduceTaskEnvKey
operator|=
name|JobConf
operator|.
name|MAPRED_TASK_ENV
expr_stmt|;
name|mapTaskJavaOptsKey
operator|=
name|reduceTaskJavaOptsKey
operator|=
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
expr_stmt|;
name|mapTaskJavaOpts
operator|=
name|reduceTaskJavaOpts
operator|=
name|TASK_OPTS_VAL
expr_stmt|;
block|}
name|config
operator|.
name|set
argument_list|(
name|mapTaskEnvKey
argument_list|,
name|Shell
operator|.
name|WINDOWS
condition|?
literal|"MY_PATH=/tmp,LANG=en_us_8859_1,NEW_PATH=%MY_PATH%;/tmp"
else|:
literal|"MY_PATH=/tmp,LANG=en_us_8859_1,NEW_PATH=$NEW_PATH:/tmp"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|reduceTaskEnvKey
argument_list|,
name|Shell
operator|.
name|WINDOWS
condition|?
literal|"MY_PATH=/tmp,LANG=en_us_8859_1,NEW_PATH=%MY_PATH%;/tmp"
else|:
literal|"MY_PATH=/tmp,LANG=en_us_8859_1,NEW_PATH=$NEW_PATH:/tmp"
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|mapTaskJavaOptsKey
argument_list|,
name|mapTaskJavaOpts
argument_list|)
expr_stmt|;
name|config
operator|.
name|set
argument_list|(
name|reduceTaskJavaOptsKey
argument_list|,
name|reduceTaskJavaOpts
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|Job
operator|.
name|getInstance
argument_list|(
name|config
argument_list|)
decl_stmt|;
name|job
operator|.
name|addFileToClassPath
argument_list|(
name|APP_JAR
argument_list|)
expr_stmt|;
name|job
operator|.
name|setJarByClass
argument_list|(
name|TestMiniMRChildTask
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setMaxMapAttempts
argument_list|(
literal|1
argument_list|)
expr_stmt|;
comment|// speed up failures
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|boolean
name|succeeded
init|=
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"The environment checker job failed."
argument_list|,
name|succeeded
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

