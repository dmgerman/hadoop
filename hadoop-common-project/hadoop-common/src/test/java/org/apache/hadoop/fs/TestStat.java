begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
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
name|BufferedReader
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
name|StringReader
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

begin_class
DECL|class|TestStat
specifier|public
class|class
name|TestStat
block|{
DECL|field|stat
specifier|private
specifier|static
name|Stat
name|stat
decl_stmt|;
annotation|@
name|BeforeClass
DECL|method|setup ()
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|stat
operator|=
operator|new
name|Stat
argument_list|(
operator|new
name|Path
argument_list|(
literal|"/dummypath"
argument_list|)
argument_list|,
literal|4096l
argument_list|,
literal|false
argument_list|,
name|FileSystem
operator|.
name|get
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|class|StatOutput
specifier|private
class|class
name|StatOutput
block|{
DECL|field|doesNotExist
specifier|final
name|String
name|doesNotExist
decl_stmt|;
DECL|field|directory
specifier|final
name|String
name|directory
decl_stmt|;
DECL|field|file
specifier|final
name|String
name|file
decl_stmt|;
DECL|field|symlink
specifier|final
name|String
name|symlink
decl_stmt|;
DECL|method|StatOutput (String doesNotExist, String directory, String file, String symlink)
name|StatOutput
parameter_list|(
name|String
name|doesNotExist
parameter_list|,
name|String
name|directory
parameter_list|,
name|String
name|file
parameter_list|,
name|String
name|symlink
parameter_list|)
block|{
name|this
operator|.
name|doesNotExist
operator|=
name|doesNotExist
expr_stmt|;
name|this
operator|.
name|directory
operator|=
name|directory
expr_stmt|;
name|this
operator|.
name|file
operator|=
name|file
expr_stmt|;
name|this
operator|.
name|symlink
operator|=
name|symlink
expr_stmt|;
block|}
DECL|method|test ()
name|void
name|test
parameter_list|()
throws|throws
name|Exception
block|{
name|BufferedReader
name|br
decl_stmt|;
name|FileStatus
name|status
decl_stmt|;
try|try
block|{
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|doesNotExist
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|parseExecResult
argument_list|(
name|br
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|directory
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|parseExecResult
argument_list|(
name|br
argument_list|)
expr_stmt|;
name|status
operator|=
name|stat
operator|.
name|getFileStatusForTesting
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|isDirectory
argument_list|()
argument_list|)
expr_stmt|;
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|file
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|parseExecResult
argument_list|(
name|br
argument_list|)
expr_stmt|;
name|status
operator|=
name|stat
operator|.
name|getFileStatusForTesting
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|isFile
argument_list|()
argument_list|)
expr_stmt|;
name|br
operator|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|StringReader
argument_list|(
name|symlink
argument_list|)
argument_list|)
expr_stmt|;
name|stat
operator|.
name|parseExecResult
argument_list|(
name|br
argument_list|)
expr_stmt|;
name|status
operator|=
name|stat
operator|.
name|getFileStatusForTesting
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|status
operator|.
name|isSymlink
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testStatLinux ()
specifier|public
name|void
name|testStatLinux
parameter_list|()
throws|throws
name|Exception
block|{
name|StatOutput
name|linux
init|=
operator|new
name|StatOutput
argument_list|(
literal|"stat: cannot stat `watermelon': No such file or directory"
argument_list|,
literal|"4096,directory,1373584236,1373586485,755,andrew,root,`.'"
argument_list|,
literal|"0,regular empty file,1373584228,1373584228,644,andrew,andrew,`target'"
argument_list|,
literal|"6,symbolic link,1373584236,1373584236,777,andrew,andrew,`link' -> `target'"
argument_list|)
decl_stmt|;
name|linux
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testStatFreeBSD ()
specifier|public
name|void
name|testStatFreeBSD
parameter_list|()
throws|throws
name|Exception
block|{
name|StatOutput
name|freebsd
init|=
operator|new
name|StatOutput
argument_list|(
literal|"stat: symtest/link: stat: No such file or directory"
argument_list|,
literal|"512,Directory,1373583695,1373583669,40755,awang,awang,`link' -> `'"
argument_list|,
literal|"0,Regular File,1373508937,1373508937,100644,awang,awang,`link' -> `'"
argument_list|,
literal|"6,Symbolic Link,1373508941,1373508941,120755,awang,awang,`link' -> `target'"
argument_list|)
decl_stmt|;
name|freebsd
operator|.
name|test
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testStatFileNotFound ()
specifier|public
name|void
name|testStatFileNotFound
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|stat
operator|.
name|getFileStatus
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Expected FileNotFoundException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
comment|// expected
block|}
block|}
block|}
end_class

end_unit

