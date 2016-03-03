begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|protocol
operator|.
name|BlockStoragePolicy
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
name|server
operator|.
name|blockmanagement
operator|.
name|BlockStoragePolicySuite
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
DECL|class|TestApplyingStoragePolicy
specifier|public
class|class
name|TestApplyingStoragePolicy
block|{
DECL|field|REPL
specifier|private
specifier|static
specifier|final
name|short
name|REPL
init|=
literal|1
decl_stmt|;
DECL|field|SIZE
specifier|private
specifier|static
specifier|final
name|int
name|SIZE
init|=
literal|128
decl_stmt|;
DECL|field|conf
specifier|private
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|cluster
specifier|private
specifier|static
name|MiniDFSCluster
name|cluster
decl_stmt|;
DECL|field|fs
specifier|private
specifier|static
name|DistributedFileSystem
name|fs
decl_stmt|;
annotation|@
name|Before
DECL|method|clusterSetUp ()
specifier|public
name|void
name|clusterSetUp
parameter_list|()
throws|throws
name|IOException
block|{
name|conf
operator|=
operator|new
name|HdfsConfiguration
argument_list|()
expr_stmt|;
name|cluster
operator|=
operator|new
name|MiniDFSCluster
operator|.
name|Builder
argument_list|(
name|conf
argument_list|)
operator|.
name|numDataNodes
argument_list|(
name|REPL
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
name|cluster
operator|.
name|waitActive
argument_list|()
expr_stmt|;
name|fs
operator|=
name|cluster
operator|.
name|getFileSystem
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|clusterShutdown ()
specifier|public
name|void
name|clusterShutdown
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|fs
operator|!=
literal|null
condition|)
block|{
name|fs
operator|.
name|close
argument_list|()
expr_stmt|;
name|fs
operator|=
literal|null
expr_stmt|;
block|}
if|if
condition|(
name|cluster
operator|!=
literal|null
condition|)
block|{
name|cluster
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|cluster
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testStoragePolicyByDefault ()
specifier|public
name|void
name|testStoragePolicyByDefault
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|wow
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"wow"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|fooz
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"/fooz"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|wow
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|BlockStoragePolicySuite
name|suite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|hot
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"HOT"
argument_list|)
decl_stmt|;
comment|/*      * test: storage policy is HOT by default or inherited from nearest      * ancestor, if not explicitly specified for newly created dir/file.      */
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|wow
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSetAndUnsetStoragePolicy ()
specifier|public
name|void
name|testSetAndUnsetStoragePolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|wow
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"wow"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|fooz
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"/fooz"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|wow
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|BlockStoragePolicySuite
name|suite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|warm
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"WARM"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|cold
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"COLD"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|hot
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"HOT"
argument_list|)
decl_stmt|;
comment|/*      * test: set storage policy      */
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|foo
argument_list|,
name|warm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|bar
argument_list|,
name|cold
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|wow
argument_list|,
name|hot
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|fooz
argument_list|,
name|warm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: get storage policy after set      */
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|warm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|cold
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|wow
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: unset storage policy      */
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|foo
argument_list|)
expr_stmt|;
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|bar
argument_list|)
expr_stmt|;
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|wow
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: get storage policy after unset      */
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|wow
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testNestedStoragePolicy ()
specifier|public
name|void
name|testNestedStoragePolicy
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|wow
init|=
operator|new
name|Path
argument_list|(
name|bar
argument_list|,
literal|"wow"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|fooz
init|=
operator|new
name|Path
argument_list|(
literal|"/foos"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|wow
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|BlockStoragePolicySuite
name|suite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|warm
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"WARM"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|cold
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"COLD"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|hot
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"HOT"
argument_list|)
decl_stmt|;
comment|/*      * test: set storage policy      */
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|foo
argument_list|,
name|warm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|bar
argument_list|,
name|cold
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|wow
argument_list|,
name|hot
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|fooz
argument_list|,
name|warm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: get storage policy after set      */
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|warm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|cold
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|wow
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: unset storage policy in the case of being nested      */
comment|// unset wow
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|wow
argument_list|)
expr_stmt|;
comment|// inherit storage policy from wow's nearest ancestor
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|wow
argument_list|)
argument_list|,
name|cold
argument_list|)
expr_stmt|;
comment|// unset bar
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|bar
argument_list|)
expr_stmt|;
comment|// inherit storage policy from bar's nearest ancestor
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|warm
argument_list|)
expr_stmt|;
comment|// unset foo
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|foo
argument_list|)
expr_stmt|;
comment|// default storage policy is applied, since no more available ancestors
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
comment|// unset fooz
try|try
block|{
name|fs
operator|.
name|unsetStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: default storage policy is applied, since no explicit policies from      * ancestors are available      */
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|wow
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testSetAndGetStoragePolicy ()
specifier|public
name|void
name|testSetAndGetStoragePolicy
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|foo
init|=
operator|new
name|Path
argument_list|(
literal|"/foo"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|bar
init|=
operator|new
name|Path
argument_list|(
name|foo
argument_list|,
literal|"bar"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|fooz
init|=
operator|new
name|Path
argument_list|(
literal|"/fooz"
argument_list|)
decl_stmt|;
name|DFSTestUtil
operator|.
name|createFile
argument_list|(
name|fs
argument_list|,
name|bar
argument_list|,
name|SIZE
argument_list|,
name|REPL
argument_list|,
literal|0
argument_list|)
expr_stmt|;
specifier|final
name|BlockStoragePolicySuite
name|suite
init|=
name|BlockStoragePolicySuite
operator|.
name|createDefaultSuite
argument_list|()
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|warm
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"WARM"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|cold
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"COLD"
argument_list|)
decl_stmt|;
specifier|final
name|BlockStoragePolicy
name|hot
init|=
name|suite
operator|.
name|getPolicy
argument_list|(
literal|"HOT"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|hot
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: set storage policy      */
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|foo
argument_list|,
name|warm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|bar
argument_list|,
name|cold
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|setStoragePolicy
argument_list|(
name|fooz
argument_list|,
name|warm
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
comment|/*      * test: get storage policy after set      */
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|foo
argument_list|)
argument_list|,
name|warm
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|bar
argument_list|)
argument_list|,
name|cold
argument_list|)
expr_stmt|;
try|try
block|{
name|fs
operator|.
name|getStoragePolicy
argument_list|(
name|fooz
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|assertTrue
argument_list|(
name|e
operator|instanceof
name|FileNotFoundException
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

