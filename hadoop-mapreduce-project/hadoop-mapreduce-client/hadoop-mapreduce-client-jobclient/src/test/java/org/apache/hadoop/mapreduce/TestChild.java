begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.mapreduce
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|mapreduce
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
name|PathFilter
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
name|HadoopTestCase
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
name|log4j
operator|.
name|Level
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
name|assertFalse
import|;
end_import

begin_class
DECL|class|TestChild
specifier|public
class|class
name|TestChild
extends|extends
name|HadoopTestCase
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
name|String
name|TEST_ROOT_DIR
init|=
operator|new
name|File
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
name|toURI
argument_list|()
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
DECL|field|inDir
specifier|private
specifier|final
name|Path
name|inDir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"./wc/input"
argument_list|)
decl_stmt|;
DECL|field|outDir
specifier|private
specifier|final
name|Path
name|outDir
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"./wc/output"
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
DECL|method|TestChild ()
specifier|public
name|TestChild
parameter_list|()
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|HadoopTestCase
operator|.
name|CLUSTER_MR
argument_list|,
name|HadoopTestCase
operator|.
name|LOCAL_FS
argument_list|,
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
DECL|class|MyMapper
specifier|static
class|class
name|MyMapper
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
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|boolean
name|oldConfigs
init|=
name|conf
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
name|conf
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
name|conf
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
name|Level
name|logLevel
init|=
name|Level
operator|.
name|toLevel
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_LOG_LEVEL
argument_list|,
name|Level
operator|.
name|INFO
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_LOG_LEVEL
operator|+
literal|"has value of "
operator|+
name|logLevel
argument_list|,
name|logLevel
argument_list|,
name|Level
operator|.
name|OFF
argument_list|)
expr_stmt|;
block|}
block|}
DECL|class|MyReducer
specifier|static
class|class
name|MyReducer
extends|extends
name|Reducer
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
name|Configuration
name|conf
init|=
name|context
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|boolean
name|oldConfigs
init|=
name|conf
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
name|conf
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
name|conf
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
name|Level
name|logLevel
init|=
name|Level
operator|.
name|toLevel
argument_list|(
name|conf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_LOG_LEVEL
argument_list|,
name|Level
operator|.
name|INFO
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_LOG_LEVEL
operator|+
literal|"has value of "
operator|+
name|logLevel
argument_list|,
name|logLevel
argument_list|,
name|Level
operator|.
name|OFF
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|submitAndValidateJob (JobConf conf, int numMaps, int numReds, boolean oldConfigs)
specifier|private
name|Job
name|submitAndValidateJob
parameter_list|(
name|JobConf
name|conf
parameter_list|,
name|int
name|numMaps
parameter_list|,
name|int
name|numReds
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
name|conf
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
name|conf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|,
name|TASK_OPTS_VAL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|conf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
argument_list|,
name|MAP_OPTS_VAL
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
argument_list|,
name|REDUCE_OPTS_VAL
argument_list|)
expr_stmt|;
block|}
name|conf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_LOG_LEVEL
argument_list|,
name|Level
operator|.
name|OFF
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_LOG_LEVEL
argument_list|,
name|Level
operator|.
name|OFF
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Job
name|job
init|=
name|MapReduceTestUtil
operator|.
name|createJob
argument_list|(
name|conf
argument_list|,
name|inDir
argument_list|,
name|outDir
argument_list|,
name|numMaps
argument_list|,
name|numReds
argument_list|)
decl_stmt|;
name|job
operator|.
name|setMapperClass
argument_list|(
name|MyMapper
operator|.
name|class
argument_list|)
expr_stmt|;
name|job
operator|.
name|setReducerClass
argument_list|(
name|MyReducer
operator|.
name|class
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"Job already has a job tracker connection, before it's submitted"
argument_list|,
name|job
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|submit
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
literal|"Job doesn't have a job tracker connection, even though it's been submitted"
argument_list|,
name|job
operator|.
name|isConnected
argument_list|()
argument_list|)
expr_stmt|;
name|job
operator|.
name|waitForCompletion
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|job
operator|.
name|isSuccessful
argument_list|()
argument_list|)
expr_stmt|;
comment|// Check output directory
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
name|assertTrue
argument_list|(
literal|"Job output directory doesn't exit!"
argument_list|,
name|fs
operator|.
name|exists
argument_list|(
name|outDir
argument_list|)
argument_list|)
expr_stmt|;
name|FileStatus
index|[]
name|list
init|=
name|fs
operator|.
name|listStatus
argument_list|(
name|outDir
argument_list|,
operator|new
name|OutputFilter
argument_list|()
argument_list|)
decl_stmt|;
name|int
name|numPartFiles
init|=
name|numReds
operator|==
literal|0
condition|?
name|numMaps
else|:
name|numReds
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Number of part-files is "
operator|+
name|list
operator|.
name|length
operator|+
literal|" and not "
operator|+
name|numPartFiles
argument_list|,
name|list
operator|.
name|length
operator|==
name|numPartFiles
argument_list|)
expr_stmt|;
return|return
name|job
return|;
block|}
annotation|@
name|Test
DECL|method|testChild ()
specifier|public
name|void
name|testChild
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|submitAndValidateJob
argument_list|(
name|createJobConf
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|submitAndValidateJob
argument_list|(
name|createJobConf
argument_list|()
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
DECL|class|OutputFilter
specifier|private
specifier|static
class|class
name|OutputFilter
implements|implements
name|PathFilter
block|{
DECL|method|accept (Path path)
specifier|public
name|boolean
name|accept
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
return|return
operator|!
operator|(
name|path
operator|.
name|getName
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"_"
argument_list|)
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

