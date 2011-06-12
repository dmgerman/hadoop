begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.raid
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|raid
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|raid
operator|.
name|protocol
operator|.
name|PolicyInfo
import|;
end_import

begin_class
DECL|class|TestRaidFilter
specifier|public
class|class
name|TestRaidFilter
extends|extends
name|TestCase
block|{
DECL|field|TEST_DIR
specifier|final
specifier|static
name|String
name|TEST_DIR
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
literal|"build/contrib/raid/test/data"
argument_list|)
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|LOG
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
literal|"org.apache.hadoop.raid.TestRaidFilter"
argument_list|)
decl_stmt|;
DECL|field|conf
name|Configuration
name|conf
decl_stmt|;
DECL|field|dfs
name|MiniDFSCluster
name|dfs
init|=
literal|null
decl_stmt|;
DECL|field|fs
name|FileSystem
name|fs
init|=
literal|null
decl_stmt|;
DECL|method|mySetup ()
specifier|private
name|void
name|mySetup
parameter_list|()
throws|throws
name|Exception
block|{
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|)
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// Make sure data directory exists
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|dfs
operator|=
operator|new
name|MiniDFSCluster
argument_list|(
name|conf
argument_list|,
literal|2
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dfs
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|dfs
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
name|String
name|namenode
init|=
name|fs
operator|.
name|getUri
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|FileSystem
operator|.
name|setDefaultUri
argument_list|(
name|conf
argument_list|,
name|namenode
argument_list|)
expr_stmt|;
block|}
DECL|method|myTearDown ()
specifier|private
name|void
name|myTearDown
parameter_list|()
throws|throws
name|Exception
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
block|}
DECL|method|testLayeredPolicies ()
specifier|public
name|void
name|testLayeredPolicies
parameter_list|()
throws|throws
name|Exception
block|{
name|mySetup
argument_list|()
expr_stmt|;
name|Path
name|src1
init|=
operator|new
name|Path
argument_list|(
literal|"/user/foo"
argument_list|)
decl_stmt|;
name|Path
name|src2
init|=
operator|new
name|Path
argument_list|(
literal|"/user/foo/bar"
argument_list|)
decl_stmt|;
name|PolicyInfo
name|info1
init|=
operator|new
name|PolicyInfo
argument_list|(
literal|"p1"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|info1
operator|.
name|setSrcPath
argument_list|(
name|src1
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|info1
operator|.
name|setErasureCode
argument_list|(
literal|"xor"
argument_list|)
expr_stmt|;
name|info1
operator|.
name|setDescription
argument_list|(
literal|"test policy"
argument_list|)
expr_stmt|;
name|info1
operator|.
name|setProperty
argument_list|(
literal|"targetReplication"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|info1
operator|.
name|setProperty
argument_list|(
literal|"metaReplication"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|info1
operator|.
name|setProperty
argument_list|(
literal|"modTimePeriod"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|PolicyInfo
name|info2
init|=
operator|new
name|PolicyInfo
argument_list|(
literal|"p2"
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|info2
operator|.
name|setSrcPath
argument_list|(
name|src2
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|info2
operator|.
name|setErasureCode
argument_list|(
literal|"xor"
argument_list|)
expr_stmt|;
name|info2
operator|.
name|setDescription
argument_list|(
literal|"test policy"
argument_list|)
expr_stmt|;
name|info2
operator|.
name|setProperty
argument_list|(
literal|"targetReplication"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|info2
operator|.
name|setProperty
argument_list|(
literal|"metaReplication"
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|info2
operator|.
name|setProperty
argument_list|(
literal|"modTimePeriod"
argument_list|,
literal|"0"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|PolicyInfo
argument_list|>
name|all
init|=
operator|new
name|ArrayList
argument_list|<
name|PolicyInfo
argument_list|>
argument_list|()
decl_stmt|;
name|all
operator|.
name|add
argument_list|(
name|info1
argument_list|)
expr_stmt|;
name|all
operator|.
name|add
argument_list|(
name|info2
argument_list|)
expr_stmt|;
try|try
block|{
name|long
name|blockSize
init|=
literal|1024
decl_stmt|;
name|byte
index|[]
name|bytes
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|blockSize
index|]
decl_stmt|;
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|src1
argument_list|,
literal|"f1"
argument_list|)
decl_stmt|;
name|Path
name|f2
init|=
operator|new
name|Path
argument_list|(
name|src2
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm1
init|=
name|fs
operator|.
name|create
argument_list|(
name|f1
argument_list|,
literal|false
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|FSDataOutputStream
name|stm2
init|=
name|fs
operator|.
name|create
argument_list|(
name|f2
argument_list|,
literal|false
argument_list|,
literal|4096
argument_list|,
operator|(
name|short
operator|)
literal|1
argument_list|,
name|blockSize
argument_list|)
decl_stmt|;
name|FSDataOutputStream
index|[]
name|stms
init|=
operator|new
name|FSDataOutputStream
index|[]
block|{
name|stm1
block|,
name|stm2
block|}
decl_stmt|;
for|for
control|(
name|FSDataOutputStream
name|stm
range|:
name|stms
control|)
block|{
name|stm
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|stm
operator|.
name|write
argument_list|(
name|bytes
argument_list|)
expr_stmt|;
name|stm
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
name|FileStatus
name|stat1
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f1
argument_list|)
decl_stmt|;
name|FileStatus
name|stat2
init|=
name|fs
operator|.
name|getFileStatus
argument_list|(
name|f2
argument_list|)
decl_stmt|;
name|RaidFilter
operator|.
name|Statistics
name|stats
init|=
operator|new
name|RaidFilter
operator|.
name|Statistics
argument_list|()
decl_stmt|;
name|RaidFilter
operator|.
name|TimeBasedFilter
name|filter
init|=
operator|new
name|RaidFilter
operator|.
name|TimeBasedFilter
argument_list|(
name|conf
argument_list|,
name|RaidNode
operator|.
name|xorDestinationPath
argument_list|(
name|conf
argument_list|)
argument_list|,
name|info1
argument_list|,
name|all
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
name|stats
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Stats "
operator|+
name|stats
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|filter
operator|.
name|check
argument_list|(
name|stat1
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|filter
operator|.
name|check
argument_list|(
name|stat2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|myTearDown
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

