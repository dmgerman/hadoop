begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.streaming
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|streaming
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStreamReader
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
name|List
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
name|FileUtil
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
name|MiniMRCluster
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
name|Utils
import|;
end_import

begin_comment
comment|/**  * This test case tests the symlink creation  * utility provided by distributed caching   */
end_comment

begin_class
DECL|class|TestMultipleCachefiles
specifier|public
class|class
name|TestMultipleCachefiles
block|{
DECL|field|INPUT_FILE
name|String
name|INPUT_FILE
init|=
literal|"/testing-streaming/input.txt"
decl_stmt|;
DECL|field|OUTPUT_DIR
name|String
name|OUTPUT_DIR
init|=
literal|"/testing-streaming/out"
decl_stmt|;
DECL|field|CACHE_FILE
name|String
name|CACHE_FILE
init|=
literal|"/testing-streaming/cache.txt"
decl_stmt|;
DECL|field|CACHE_FILE_2
name|String
name|CACHE_FILE_2
init|=
literal|"/testing-streaming/cache2.txt"
decl_stmt|;
DECL|field|input
name|String
name|input
init|=
literal|"check to see if we can read this none reduce"
decl_stmt|;
DECL|field|map
name|String
name|map
init|=
name|TestStreaming
operator|.
name|XARGS_CAT
decl_stmt|;
DECL|field|reduce
name|String
name|reduce
init|=
name|TestStreaming
operator|.
name|CAT
decl_stmt|;
DECL|field|mapString
name|String
name|mapString
init|=
literal|"testlink"
decl_stmt|;
DECL|field|mapString2
name|String
name|mapString2
init|=
literal|"testlink2"
decl_stmt|;
DECL|field|cacheString
name|String
name|cacheString
init|=
literal|"This is just the cache string"
decl_stmt|;
DECL|field|cacheString2
name|String
name|cacheString2
init|=
literal|"This is just the second cache string"
decl_stmt|;
DECL|field|job
name|StreamJob
name|job
decl_stmt|;
DECL|method|TestMultipleCachefiles ()
specifier|public
name|TestMultipleCachefiles
parameter_list|()
throws|throws
name|IOException
block|{   }
annotation|@
name|Test
DECL|method|testMultipleCachefiles ()
specifier|public
name|void
name|testMultipleCachefiles
parameter_list|()
throws|throws
name|Exception
block|{
name|boolean
name|mayExit
init|=
literal|false
decl_stmt|;
name|MiniMRCluster
name|mr
init|=
literal|null
decl_stmt|;
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
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
name|FileSystem
name|fileSys
init|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
decl_stmt|;
name|String
name|namenode
init|=
name|fileSys
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|mr
operator|=
operator|new
name|MiniMRCluster
argument_list|(
literal|1
argument_list|,
name|namenode
argument_list|,
literal|3
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|mr
operator|.
name|createJobConf
argument_list|()
control|)
block|{
name|args
operator|.
name|add
argument_list|(
literal|"-jobconf"
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
operator|+
literal|"="
operator|+
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|argv
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"-input"
block|,
name|INPUT_FILE
block|,
literal|"-output"
block|,
name|OUTPUT_DIR
block|,
literal|"-mapper"
block|,
name|map
block|,
literal|"-reducer"
block|,
name|reduce
block|,
literal|"-jobconf"
block|,
literal|"stream.tmpdir="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"/tmp"
argument_list|)
block|,
literal|"-jobconf"
block|,
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
operator|+
literal|"="
operator|+
literal|"-Dcontrib.name="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"contrib.name"
argument_list|)
operator|+
literal|" "
operator|+
literal|"-Dbuild.test="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"build.test"
argument_list|)
operator|+
literal|" "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_MAP_TASK_JAVA_OPTS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|,
literal|""
argument_list|)
argument_list|)
block|,
literal|"-jobconf"
block|,
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
operator|+
literal|"="
operator|+
literal|"-Dcontrib.name="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"contrib.name"
argument_list|)
operator|+
literal|" "
operator|+
literal|"-Dbuild.test="
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"build.test"
argument_list|)
operator|+
literal|" "
operator|+
name|conf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_REDUCE_TASK_JAVA_OPTS
argument_list|,
name|conf
operator|.
name|get
argument_list|(
name|JobConf
operator|.
name|MAPRED_TASK_JAVA_OPTS
argument_list|,
literal|""
argument_list|)
argument_list|)
block|,
literal|"-cacheFile"
block|,
name|fileSys
operator|.
name|getUri
argument_list|()
operator|+
name|CACHE_FILE
operator|+
literal|"#"
operator|+
name|mapString
block|,
literal|"-cacheFile"
block|,
name|fileSys
operator|.
name|getUri
argument_list|()
operator|+
name|CACHE_FILE_2
operator|+
literal|"#"
operator|+
name|mapString2
block|,
literal|"-jobconf"
block|,
literal|"mapred.jar="
operator|+
name|TestStreaming
operator|.
name|STREAMING_JAR
block|,       }
decl_stmt|;
for|for
control|(
name|String
name|arg
range|:
name|argv
control|)
block|{
name|args
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
name|argv
operator|=
name|args
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|args
operator|.
name|size
argument_list|()
index|]
argument_list|)
expr_stmt|;
name|fileSys
operator|.
name|delete
argument_list|(
operator|new
name|Path
argument_list|(
name|OUTPUT_DIR
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DataOutputStream
name|file
init|=
name|fileSys
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|INPUT_FILE
argument_list|)
argument_list|)
decl_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|mapString
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|mapString2
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
name|fileSys
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|CACHE_FILE
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|cacheString
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|file
operator|=
name|fileSys
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|CACHE_FILE_2
argument_list|)
argument_list|)
expr_stmt|;
name|file
operator|.
name|writeBytes
argument_list|(
name|cacheString2
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|file
operator|.
name|close
argument_list|()
expr_stmt|;
name|job
operator|=
operator|new
name|StreamJob
argument_list|(
name|argv
argument_list|,
name|mayExit
argument_list|)
expr_stmt|;
name|job
operator|.
name|go
argument_list|()
expr_stmt|;
name|fileSys
operator|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|String
name|line
init|=
literal|null
decl_stmt|;
name|String
name|line2
init|=
literal|null
decl_stmt|;
name|Path
index|[]
name|fileList
init|=
name|FileUtil
operator|.
name|stat2Paths
argument_list|(
name|fileSys
operator|.
name|listStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|OUTPUT_DIR
argument_list|)
argument_list|,
operator|new
name|Utils
operator|.
name|OutputFileUtils
operator|.
name|OutputFilesFilter
argument_list|()
argument_list|)
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
name|fileList
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|fileList
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|BufferedReader
name|bread
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|fileSys
operator|.
name|open
argument_list|(
name|fileList
index|[
name|i
index|]
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|line
operator|=
name|bread
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|line2
operator|=
name|bread
operator|.
name|readLine
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|line2
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|cacheString
operator|+
literal|"\t"
argument_list|,
name|line
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|cacheString2
operator|+
literal|"\t"
argument_list|,
name|line2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
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
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
DECL|method|main (String[]args)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
operator|new
name|TestMultipleCachefiles
argument_list|()
operator|.
name|testMultipleCachefiles
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

