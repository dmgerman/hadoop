begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.util
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
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
name|junit
operator|.
name|*
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

begin_comment
comment|/*  * Test for HostsFileReader.java  *   */
end_comment

begin_class
DECL|class|TestHostsFileReader
specifier|public
class|class
name|TestHostsFileReader
block|{
comment|// Using /test/build/data/tmp directory to store temprory files
DECL|field|HOSTS_TEST_DIR
specifier|final
name|String
name|HOSTS_TEST_DIR
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
name|getAbsolutePath
argument_list|()
decl_stmt|;
DECL|field|EXCLUDES_FILE
name|File
name|EXCLUDES_FILE
init|=
operator|new
name|File
argument_list|(
name|HOSTS_TEST_DIR
argument_list|,
literal|"dfs.exclude"
argument_list|)
decl_stmt|;
DECL|field|INCLUDES_FILE
name|File
name|INCLUDES_FILE
init|=
operator|new
name|File
argument_list|(
name|HOSTS_TEST_DIR
argument_list|,
literal|"dfs.include"
argument_list|)
decl_stmt|;
DECL|field|excludesFile
name|String
name|excludesFile
init|=
name|HOSTS_TEST_DIR
operator|+
literal|"/dfs.exclude"
decl_stmt|;
DECL|field|includesFile
name|String
name|includesFile
init|=
name|HOSTS_TEST_DIR
operator|+
literal|"/dfs.include"
decl_stmt|;
DECL|field|includes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|includes
decl_stmt|;
DECL|field|excludes
specifier|private
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
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
comment|// Delete test files after running tests
name|EXCLUDES_FILE
operator|.
name|delete
argument_list|()
expr_stmt|;
name|INCLUDES_FILE
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/*    * 1.Create dfs.exclude,dfs.include file    * 2.Write host names per line    * 3.Write comments starting with #    * 4.Close file    * 5.Compare if number of hosts reported by HostsFileReader    *   are equal to the number of hosts written    */
annotation|@
name|Test
DECL|method|testHostsFileReader ()
specifier|public
name|void
name|testHostsFileReader
parameter_list|()
throws|throws
name|Exception
block|{
name|FileWriter
name|efw
init|=
operator|new
name|FileWriter
argument_list|(
name|excludesFile
argument_list|)
decl_stmt|;
name|FileWriter
name|ifw
init|=
operator|new
name|FileWriter
argument_list|(
name|includesFile
argument_list|)
decl_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"#DFS-Hosts-excluded\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"somehost1\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"#This-is-comment\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"somehost2\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"somehost3 # host3\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"somehost4\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"somehost4 somehost5\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"#Hosts-in-DFS\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"somehost1\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"somehost2\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"somehost3\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"#This-is-comment\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"somehost4 # host4\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"somehost4 somehost5\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|close
argument_list|()
expr_stmt|;
name|HostsFileReader
name|hfp
init|=
operator|new
name|HostsFileReader
argument_list|(
name|includesFile
argument_list|,
name|excludesFile
argument_list|)
decl_stmt|;
name|int
name|includesLen
init|=
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|excludesLen
init|=
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|includesLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|5
argument_list|,
name|excludesLen
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"host3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"host4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test for null file    */
annotation|@
name|Test
DECL|method|testHostFileReaderWithNull ()
specifier|public
name|void
name|testHostFileReaderWithNull
parameter_list|()
throws|throws
name|Exception
block|{
name|FileWriter
name|efw
init|=
operator|new
name|FileWriter
argument_list|(
name|excludesFile
argument_list|)
decl_stmt|;
name|FileWriter
name|ifw
init|=
operator|new
name|FileWriter
argument_list|(
name|includesFile
argument_list|)
decl_stmt|;
name|efw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ifw
operator|.
name|close
argument_list|()
expr_stmt|;
name|HostsFileReader
name|hfp
init|=
operator|new
name|HostsFileReader
argument_list|(
name|includesFile
argument_list|,
name|excludesFile
argument_list|)
decl_stmt|;
name|int
name|includesLen
init|=
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|excludesLen
init|=
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
comment|// TestCase1: Check if lines beginning with # are ignored
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|includesLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|excludesLen
argument_list|)
expr_stmt|;
comment|// TestCase2: Check if given host names are reported by getHosts and
comment|// getExcludedHosts
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Check if only comments can be written to hosts file    */
annotation|@
name|Test
DECL|method|testHostFileReaderWithCommentsOnly ()
specifier|public
name|void
name|testHostFileReaderWithCommentsOnly
parameter_list|()
throws|throws
name|Exception
block|{
name|FileWriter
name|efw
init|=
operator|new
name|FileWriter
argument_list|(
name|excludesFile
argument_list|)
decl_stmt|;
name|FileWriter
name|ifw
init|=
operator|new
name|FileWriter
argument_list|(
name|includesFile
argument_list|)
decl_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"#DFS-Hosts-excluded\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"#Hosts-in-DFS\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|close
argument_list|()
expr_stmt|;
name|HostsFileReader
name|hfp
init|=
operator|new
name|HostsFileReader
argument_list|(
name|includesFile
argument_list|,
name|excludesFile
argument_list|)
decl_stmt|;
name|int
name|includesLen
init|=
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|excludesLen
init|=
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|includesLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|excludesLen
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test if spaces are allowed in host names    */
annotation|@
name|Test
DECL|method|testHostFileReaderWithSpaces ()
specifier|public
name|void
name|testHostFileReaderWithSpaces
parameter_list|()
throws|throws
name|Exception
block|{
name|FileWriter
name|efw
init|=
operator|new
name|FileWriter
argument_list|(
name|excludesFile
argument_list|)
decl_stmt|;
name|FileWriter
name|ifw
init|=
operator|new
name|FileWriter
argument_list|(
name|includesFile
argument_list|)
decl_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"#DFS-Hosts-excluded\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"   somehost somehost2"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"   somehost3 # somehost4"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"#Hosts-in-DFS\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"   somehost somehost2"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"   somehost3 # somehost4"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|close
argument_list|()
expr_stmt|;
name|HostsFileReader
name|hfp
init|=
operator|new
name|HostsFileReader
argument_list|(
name|includesFile
argument_list|,
name|excludesFile
argument_list|)
decl_stmt|;
name|int
name|includesLen
init|=
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|excludesLen
init|=
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|includesLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|excludesLen
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost4"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost3"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost4"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/*    * Test if spaces , tabs and new lines are allowed    */
annotation|@
name|Test
DECL|method|testHostFileReaderWithTabs ()
specifier|public
name|void
name|testHostFileReaderWithTabs
parameter_list|()
throws|throws
name|Exception
block|{
name|FileWriter
name|efw
init|=
operator|new
name|FileWriter
argument_list|(
name|excludesFile
argument_list|)
decl_stmt|;
name|FileWriter
name|ifw
init|=
operator|new
name|FileWriter
argument_list|(
name|includesFile
argument_list|)
decl_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"#DFS-Hosts-excluded\n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"     \n"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"   somehost \t somehost2 \n somehost4"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|write
argument_list|(
literal|"   somehost3 \t # somehost5"
argument_list|)
expr_stmt|;
name|efw
operator|.
name|close
argument_list|()
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"#Hosts-in-DFS\n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"     \n"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"   somehost \t  somehost2 \n somehost4"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|write
argument_list|(
literal|"   somehost3 \t # somehost5"
argument_list|)
expr_stmt|;
name|ifw
operator|.
name|close
argument_list|()
expr_stmt|;
name|HostsFileReader
name|hfp
init|=
operator|new
name|HostsFileReader
argument_list|(
name|includesFile
argument_list|,
name|excludesFile
argument_list|)
decl_stmt|;
name|int
name|includesLen
init|=
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|int
name|excludesLen
init|=
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|size
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|includesLen
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|excludesLen
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|hfp
operator|.
name|getExcludedHosts
argument_list|()
operator|.
name|contains
argument_list|(
literal|"somehost5"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

