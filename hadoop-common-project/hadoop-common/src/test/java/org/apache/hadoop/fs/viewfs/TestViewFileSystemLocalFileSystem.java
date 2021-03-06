begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.viewfs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|viewfs
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
name|IOException
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
name|FSDataInputStream
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

begin_comment
comment|/**  *   * Test the ViewFileSystemBaseTest using a viewfs with authority:   *    viewfs://mountTableName/  *    ie the authority is used to load a mount table.  *    The authority name used is "default"  *  */
end_comment

begin_class
DECL|class|TestViewFileSystemLocalFileSystem
specifier|public
class|class
name|TestViewFileSystemLocalFileSystem
extends|extends
name|ViewFileSystemBaseTest
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
name|TestViewFileSystemLocalFileSystem
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
annotation|@
name|Before
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// create the test root on local_fs
name|fsTarget
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testNflyWriteSimple ()
specifier|public
name|void
name|testNflyWriteSimple
parameter_list|()
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting testNflyWriteSimple"
argument_list|)
expr_stmt|;
specifier|final
name|URI
index|[]
name|testUris
init|=
operator|new
name|URI
index|[]
block|{
name|URI
operator|.
name|create
argument_list|(
name|targetTestRoot
operator|+
literal|"/nfwd1"
argument_list|)
block|,
name|URI
operator|.
name|create
argument_list|(
name|targetTestRoot
operator|+
literal|"/nfwd2"
argument_list|)
block|}
decl_stmt|;
specifier|final
name|String
name|testFileName
init|=
literal|"test.txt"
decl_stmt|;
specifier|final
name|Configuration
name|testConf
init|=
operator|new
name|Configuration
argument_list|(
name|conf
argument_list|)
decl_stmt|;
specifier|final
name|String
name|testString
init|=
literal|"Hello Nfly!"
decl_stmt|;
specifier|final
name|Path
name|nflyRoot
init|=
operator|new
name|Path
argument_list|(
literal|"/nflyroot"
argument_list|)
decl_stmt|;
name|ConfigUtil
operator|.
name|addLinkNfly
argument_list|(
name|testConf
argument_list|,
name|nflyRoot
operator|.
name|toString
argument_list|()
argument_list|,
name|testUris
argument_list|)
expr_stmt|;
specifier|final
name|FileSystem
name|nfly
init|=
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"viewfs:///"
argument_list|)
argument_list|,
name|testConf
argument_list|)
decl_stmt|;
specifier|final
name|FSDataOutputStream
name|fsDos
init|=
name|nfly
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|nflyRoot
argument_list|,
literal|"test.txt"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fsDos
operator|.
name|writeUTF
argument_list|(
name|testString
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsDos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|FileStatus
index|[]
name|statuses
init|=
name|nfly
operator|.
name|listStatus
argument_list|(
name|nflyRoot
argument_list|)
decl_stmt|;
name|FileSystem
name|lfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|URI
name|testUri
range|:
name|testUris
control|)
block|{
specifier|final
name|Path
name|testFile
init|=
operator|new
name|Path
argument_list|(
operator|new
name|Path
argument_list|(
name|testUri
argument_list|)
argument_list|,
name|testFileName
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|testFile
operator|+
literal|" should exist!"
argument_list|,
name|lfs
operator|.
name|exists
argument_list|(
name|testFile
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|FSDataInputStream
name|fsdis
init|=
name|lfs
operator|.
name|open
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
try|try
block|{
name|assertEquals
argument_list|(
literal|"Wrong file content"
argument_list|,
name|testString
argument_list|,
name|fsdis
operator|.
name|readUTF
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fsdis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testNflyInvalidMinReplication ()
specifier|public
name|void
name|testNflyInvalidMinReplication
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Starting testNflyInvalidMinReplication"
argument_list|)
expr_stmt|;
specifier|final
name|URI
index|[]
name|testUris
init|=
operator|new
name|URI
index|[]
block|{
name|URI
operator|.
name|create
argument_list|(
name|targetTestRoot
operator|+
literal|"/nfwd1"
argument_list|)
block|,
name|URI
operator|.
name|create
argument_list|(
name|targetTestRoot
operator|+
literal|"/nfwd2"
argument_list|)
block|}
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|ConfigUtil
operator|.
name|addLinkNfly
argument_list|(
name|conf
argument_list|,
literal|"mt"
argument_list|,
literal|"/nflyroot"
argument_list|,
literal|"minReplication=4"
argument_list|,
name|testUris
argument_list|)
expr_stmt|;
try|try
block|{
name|FileSystem
operator|.
name|get
argument_list|(
name|URI
operator|.
name|create
argument_list|(
literal|"viewfs://mt/"
argument_list|)
argument_list|,
name|conf
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected bad minReplication exception."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|assertTrue
argument_list|(
literal|"No minReplication message"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
operator|.
name|contains
argument_list|(
literal|"Minimum replication"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
name|fsTarget
operator|.
name|delete
argument_list|(
name|fileSystemTestHelper
operator|.
name|getTestRootPath
argument_list|(
name|fsTarget
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

