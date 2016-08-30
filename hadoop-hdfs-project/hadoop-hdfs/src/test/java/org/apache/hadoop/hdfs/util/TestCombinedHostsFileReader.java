begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|util
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
name|FileWriter
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|protocol
operator|.
name|DatanodeAdminProperties
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
name|test
operator|.
name|GenericTestUtils
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
name|After
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
name|assertEquals
import|;
end_import

begin_comment
comment|/*  * Test for JSON based HostsFileReader  */
end_comment

begin_class
DECL|class|TestCombinedHostsFileReader
specifier|public
class|class
name|TestCombinedHostsFileReader
block|{
comment|// Using /test/build/data/tmp directory to store temporary files
DECL|field|HOSTS_TEST_DIR
specifier|static
specifier|final
name|String
name|HOSTS_TEST_DIR
init|=
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|NEW_FILE
name|File
name|NEW_FILE
init|=
operator|new
name|File
argument_list|(
name|HOSTS_TEST_DIR
argument_list|,
literal|"dfs.hosts.new.json"
argument_list|)
decl_stmt|;
DECL|field|TEST_CACHE_DATA_DIR
specifier|static
specifier|final
name|String
name|TEST_CACHE_DATA_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.cache.data"
argument_list|,
literal|"build/test/cache"
argument_list|)
decl_stmt|;
DECL|field|EXISTING_FILE
name|File
name|EXISTING_FILE
init|=
operator|new
name|File
argument_list|(
name|TEST_CACHE_DATA_DIR
argument_list|,
literal|"dfs.hosts.json"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{   }
annotation|@
name|After
DECL|method|tearDown ()
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Delete test file after running tests
name|NEW_FILE
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/*    * Load the existing test json file    */
annotation|@
name|Test
DECL|method|testLoadExistingJsonFile ()
specifier|public
name|void
name|testLoadExistingJsonFile
parameter_list|()
throws|throws
name|Exception
block|{
name|Set
argument_list|<
name|DatanodeAdminProperties
argument_list|>
name|all
init|=
name|CombinedHostsFileReader
operator|.
name|readFile
argument_list|(
name|EXISTING_FILE
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|7
argument_list|,
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test empty json config file    */
annotation|@
name|Test
DECL|method|testEmptyCombinedHostsFileReader ()
specifier|public
name|void
name|testEmptyCombinedHostsFileReader
parameter_list|()
throws|throws
name|Exception
block|{
name|FileWriter
name|hosts
init|=
operator|new
name|FileWriter
argument_list|(
name|NEW_FILE
argument_list|)
decl_stmt|;
name|hosts
operator|.
name|write
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|hosts
operator|.
name|close
argument_list|()
expr_stmt|;
name|Set
argument_list|<
name|DatanodeAdminProperties
argument_list|>
name|all
init|=
name|CombinedHostsFileReader
operator|.
name|readFile
argument_list|(
name|NEW_FILE
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|all
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

