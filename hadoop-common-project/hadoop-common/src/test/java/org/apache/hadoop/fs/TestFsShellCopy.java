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
name|*
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
DECL|class|TestFsShellCopy
specifier|public
class|class
name|TestFsShellCopy
block|{
DECL|field|conf
specifier|static
name|Configuration
name|conf
decl_stmt|;
DECL|field|shell
specifier|static
name|FsShell
name|shell
decl_stmt|;
DECL|field|lfs
specifier|static
name|LocalFileSystem
name|lfs
decl_stmt|;
DECL|field|testRootDir
DECL|field|srcPath
DECL|field|dstPath
specifier|static
name|Path
name|testRootDir
decl_stmt|,
name|srcPath
decl_stmt|,
name|dstPath
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
name|conf
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|shell
operator|=
operator|new
name|FsShell
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|lfs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|testRootDir
operator|=
name|lfs
operator|.
name|makeQualified
argument_list|(
operator|new
name|Path
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"test/build/data"
argument_list|)
argument_list|,
literal|"testShellCopy"
argument_list|)
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|mkdirs
argument_list|(
name|testRootDir
argument_list|)
expr_stmt|;
name|srcPath
operator|=
operator|new
name|Path
argument_list|(
name|testRootDir
argument_list|,
literal|"srcFile"
argument_list|)
expr_stmt|;
name|dstPath
operator|=
operator|new
name|Path
argument_list|(
name|testRootDir
argument_list|,
literal|"dstFile"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|prepFiles ()
specifier|public
name|void
name|prepFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|lfs
operator|.
name|setVerifyChecksum
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|setWriteChecksum
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|srcPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|dstPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|FSDataOutputStream
name|out
init|=
name|lfs
operator|.
name|create
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeChars
argument_list|(
literal|"hi"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|lfs
operator|.
name|exists
argument_list|(
name|lfs
operator|.
name|getChecksumFile
argument_list|(
name|srcPath
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyNoCrc ()
specifier|public
name|void
name|testCopyNoCrc
parameter_list|()
throws|throws
name|Exception
block|{
name|shellRun
argument_list|(
literal|0
argument_list|,
literal|"-get"
argument_list|,
name|srcPath
operator|.
name|toString
argument_list|()
argument_list|,
name|dstPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|dstPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyCrc ()
specifier|public
name|void
name|testCopyCrc
parameter_list|()
throws|throws
name|Exception
block|{
name|shellRun
argument_list|(
literal|0
argument_list|,
literal|"-get"
argument_list|,
literal|"-crc"
argument_list|,
name|srcPath
operator|.
name|toString
argument_list|()
argument_list|,
name|dstPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|dstPath
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorruptedCopyCrc ()
specifier|public
name|void
name|testCorruptedCopyCrc
parameter_list|()
throws|throws
name|Exception
block|{
name|FSDataOutputStream
name|out
init|=
name|lfs
operator|.
name|getRawFileSystem
argument_list|()
operator|.
name|create
argument_list|(
name|srcPath
argument_list|)
decl_stmt|;
name|out
operator|.
name|writeChars
argument_list|(
literal|"bang"
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
name|shellRun
argument_list|(
literal|1
argument_list|,
literal|"-get"
argument_list|,
name|srcPath
operator|.
name|toString
argument_list|()
argument_list|,
name|dstPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCorruptedCopyIgnoreCrc ()
specifier|public
name|void
name|testCorruptedCopyIgnoreCrc
parameter_list|()
throws|throws
name|Exception
block|{
name|shellRun
argument_list|(
literal|0
argument_list|,
literal|"-get"
argument_list|,
literal|"-ignoreCrc"
argument_list|,
name|srcPath
operator|.
name|toString
argument_list|()
argument_list|,
name|dstPath
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|checkPath
argument_list|(
name|dstPath
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPath (Path p, boolean expectChecksum)
specifier|private
name|void
name|checkPath
parameter_list|(
name|Path
name|p
parameter_list|,
name|boolean
name|expectChecksum
parameter_list|)
throws|throws
name|IOException
block|{
name|assertTrue
argument_list|(
name|lfs
operator|.
name|exists
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|hasChecksum
init|=
name|lfs
operator|.
name|exists
argument_list|(
name|lfs
operator|.
name|getChecksumFile
argument_list|(
name|p
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectChecksum
argument_list|,
name|hasChecksum
argument_list|)
expr_stmt|;
block|}
DECL|method|shellRun (int n, String ... args)
specifier|private
name|void
name|shellRun
parameter_list|(
name|int
name|n
parameter_list|,
name|String
modifier|...
name|args
parameter_list|)
throws|throws
name|Exception
block|{
name|assertEquals
argument_list|(
name|n
argument_list|,
name|shell
operator|.
name|run
argument_list|(
name|args
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyFileFromLocal ()
specifier|public
name|void
name|testCopyFileFromLocal
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testRoot
init|=
operator|new
name|Path
argument_list|(
name|testRootDir
argument_list|,
literal|"testPutFile"
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|testRoot
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|mkdirs
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
name|Path
name|targetDir
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"target"
argument_list|)
decl_stmt|;
name|Path
name|filePath
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
operator|new
name|Path
argument_list|(
literal|"srcFile"
argument_list|)
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|create
argument_list|(
name|filePath
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkPut
argument_list|(
name|filePath
argument_list|,
name|targetDir
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyDirFromLocal ()
specifier|public
name|void
name|testCopyDirFromLocal
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|testRoot
init|=
operator|new
name|Path
argument_list|(
name|testRootDir
argument_list|,
literal|"testPutDir"
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|testRoot
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|mkdirs
argument_list|(
name|testRoot
argument_list|)
expr_stmt|;
name|Path
name|targetDir
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
literal|"target"
argument_list|)
decl_stmt|;
name|Path
name|dirPath
init|=
operator|new
name|Path
argument_list|(
name|testRoot
argument_list|,
operator|new
name|Path
argument_list|(
literal|"srcDir"
argument_list|)
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|mkdirs
argument_list|(
name|dirPath
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|create
argument_list|(
operator|new
name|Path
argument_list|(
name|dirPath
argument_list|,
literal|"srcFile"
argument_list|)
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|checkPut
argument_list|(
name|dirPath
argument_list|,
name|targetDir
argument_list|)
expr_stmt|;
block|}
DECL|method|checkPut (Path srcPath, Path targetDir)
specifier|private
name|void
name|checkPut
parameter_list|(
name|Path
name|srcPath
parameter_list|,
name|Path
name|targetDir
parameter_list|)
throws|throws
name|Exception
block|{
name|lfs
operator|.
name|delete
argument_list|(
name|targetDir
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|mkdirs
argument_list|(
name|targetDir
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|setWorkingDirectory
argument_list|(
name|targetDir
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|dstPath
init|=
operator|new
name|Path
argument_list|(
literal|"path"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|childPath
init|=
operator|new
name|Path
argument_list|(
name|dstPath
argument_list|,
literal|"childPath"
argument_list|)
decl_stmt|;
name|lfs
operator|.
name|setWorkingDirectory
argument_list|(
name|targetDir
argument_list|)
expr_stmt|;
comment|// copy to new file, then again
name|prepPut
argument_list|(
name|dstPath
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|0
argument_list|,
name|srcPath
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|lfs
operator|.
name|isFile
argument_list|(
name|srcPath
argument_list|)
condition|)
block|{
name|checkPut
argument_list|(
literal|1
argument_list|,
name|srcPath
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// directory works because it copies into the dir
comment|// clear contents so the check won't think there are extra paths
name|prepPut
argument_list|(
name|dstPath
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|0
argument_list|,
name|srcPath
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
block|}
comment|// copy to non-existent subdir
name|prepPut
argument_list|(
name|childPath
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|1
argument_list|,
name|srcPath
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
comment|// copy into dir, then with another name
name|prepPut
argument_list|(
name|dstPath
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|0
argument_list|,
name|srcPath
argument_list|,
name|dstPath
argument_list|)
expr_stmt|;
name|prepPut
argument_list|(
name|childPath
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|0
argument_list|,
name|srcPath
argument_list|,
name|childPath
argument_list|)
expr_stmt|;
comment|// try to put to pwd with existing dir
name|prepPut
argument_list|(
name|targetDir
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|0
argument_list|,
name|srcPath
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|prepPut
argument_list|(
name|targetDir
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|0
argument_list|,
name|srcPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
comment|// try to put to pwd with non-existent cwd
name|prepPut
argument_list|(
name|dstPath
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|setWorkingDirectory
argument_list|(
name|dstPath
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|1
argument_list|,
name|srcPath
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|prepPut
argument_list|(
name|dstPath
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|checkPut
argument_list|(
literal|1
argument_list|,
name|srcPath
argument_list|,
operator|new
name|Path
argument_list|(
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|prepPut (Path dst, boolean create, boolean isDir)
specifier|private
name|void
name|prepPut
parameter_list|(
name|Path
name|dst
parameter_list|,
name|boolean
name|create
parameter_list|,
name|boolean
name|isDir
parameter_list|)
throws|throws
name|IOException
block|{
name|lfs
operator|.
name|delete
argument_list|(
name|dst
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|lfs
operator|.
name|exists
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|create
condition|)
block|{
if|if
condition|(
name|isDir
condition|)
block|{
name|lfs
operator|.
name|mkdirs
argument_list|(
name|dst
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lfs
operator|.
name|isDirectory
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lfs
operator|.
name|mkdirs
argument_list|(
operator|new
name|Path
argument_list|(
name|dst
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|lfs
operator|.
name|create
argument_list|(
name|dst
argument_list|)
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|lfs
operator|.
name|isFile
argument_list|(
name|dst
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
DECL|method|checkPut (int exitCode, Path src, Path dest)
specifier|private
name|void
name|checkPut
parameter_list|(
name|int
name|exitCode
parameter_list|,
name|Path
name|src
parameter_list|,
name|Path
name|dest
parameter_list|)
throws|throws
name|Exception
block|{
name|String
name|argv
index|[]
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|dest
operator|!=
literal|null
condition|)
block|{
name|argv
operator|=
operator|new
name|String
index|[]
block|{
literal|"-put"
block|,
name|src
operator|.
name|toString
argument_list|()
block|,
name|pathAsString
argument_list|(
name|dest
argument_list|)
block|}
expr_stmt|;
block|}
else|else
block|{
name|argv
operator|=
operator|new
name|String
index|[]
block|{
literal|"-put"
block|,
name|src
operator|.
name|toString
argument_list|()
block|}
expr_stmt|;
name|dest
operator|=
operator|new
name|Path
argument_list|(
name|Path
operator|.
name|CUR_DIR
argument_list|)
expr_stmt|;
block|}
name|Path
name|target
decl_stmt|;
if|if
condition|(
name|lfs
operator|.
name|exists
argument_list|(
name|dest
argument_list|)
condition|)
block|{
if|if
condition|(
name|lfs
operator|.
name|isDirectory
argument_list|(
name|dest
argument_list|)
condition|)
block|{
name|target
operator|=
operator|new
name|Path
argument_list|(
name|pathAsString
argument_list|(
name|dest
argument_list|)
argument_list|,
name|src
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|target
operator|=
name|dest
expr_stmt|;
block|}
block|}
else|else
block|{
name|target
operator|=
operator|new
name|Path
argument_list|(
name|lfs
operator|.
name|getWorkingDirectory
argument_list|()
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
name|boolean
name|targetExists
init|=
name|lfs
operator|.
name|exists
argument_list|(
name|target
argument_list|)
decl_stmt|;
name|Path
name|parent
init|=
name|lfs
operator|.
name|makeQualified
argument_list|(
name|target
argument_list|)
operator|.
name|getParent
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"COPY src["
operator|+
name|src
operator|.
name|getName
argument_list|()
operator|+
literal|"] -> ["
operator|+
name|dest
operator|+
literal|"] as ["
operator|+
name|target
operator|+
literal|"]"
argument_list|)
expr_stmt|;
name|String
name|lsArgv
index|[]
init|=
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
literal|"-R"
block|,
name|pathAsString
argument_list|(
name|parent
argument_list|)
block|}
decl_stmt|;
name|shell
operator|.
name|run
argument_list|(
name|lsArgv
argument_list|)
expr_stmt|;
name|int
name|gotExit
init|=
name|shell
operator|.
name|run
argument_list|(
name|argv
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"copy exit:"
operator|+
name|gotExit
argument_list|)
expr_stmt|;
name|lsArgv
operator|=
operator|new
name|String
index|[]
block|{
literal|"-ls"
block|,
literal|"-R"
block|,
name|pathAsString
argument_list|(
name|parent
argument_list|)
block|}
expr_stmt|;
name|shell
operator|.
name|run
argument_list|(
name|lsArgv
argument_list|)
expr_stmt|;
if|if
condition|(
name|exitCode
operator|==
literal|0
condition|)
block|{
name|assertTrue
argument_list|(
name|lfs
operator|.
name|exists
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|lfs
operator|.
name|isFile
argument_list|(
name|src
argument_list|)
operator|==
name|lfs
operator|.
name|isFile
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|lfs
operator|.
name|listStatus
argument_list|(
name|lfs
operator|.
name|makeQualified
argument_list|(
name|target
argument_list|)
operator|.
name|getParent
argument_list|()
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|assertEquals
argument_list|(
name|targetExists
argument_list|,
name|lfs
operator|.
name|exists
argument_list|(
name|target
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
name|exitCode
argument_list|,
name|gotExit
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCopyMerge ()
specifier|public
name|void
name|testCopyMerge
parameter_list|()
throws|throws
name|Exception
block|{
name|Path
name|root
init|=
operator|new
name|Path
argument_list|(
name|testRootDir
argument_list|,
literal|"TestMerge"
argument_list|)
decl_stmt|;
name|Path
name|f1
init|=
operator|new
name|Path
argument_list|(
name|root
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
name|root
argument_list|,
literal|"f2"
argument_list|)
decl_stmt|;
name|Path
name|f3
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"f3"
argument_list|)
decl_stmt|;
name|Path
name|fnf
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"fnf"
argument_list|)
decl_stmt|;
name|Path
name|d
init|=
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"dir"
argument_list|)
decl_stmt|;
name|Path
name|df1
init|=
operator|new
name|Path
argument_list|(
name|d
argument_list|,
literal|"df1"
argument_list|)
decl_stmt|;
name|Path
name|df2
init|=
operator|new
name|Path
argument_list|(
name|d
argument_list|,
literal|"df2"
argument_list|)
decl_stmt|;
name|Path
name|df3
init|=
operator|new
name|Path
argument_list|(
name|d
argument_list|,
literal|"df3"
argument_list|)
decl_stmt|;
name|createFile
argument_list|(
name|f1
argument_list|,
name|f2
argument_list|,
name|f3
argument_list|,
name|df1
argument_list|,
name|df2
argument_list|,
name|df3
argument_list|)
expr_stmt|;
name|int
name|exit
decl_stmt|;
comment|// one file, kind of silly
name|exit
operator|=
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
name|f1
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
name|exit
operator|=
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
name|fnf
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|lfs
operator|.
name|exists
argument_list|(
operator|new
name|Path
argument_list|(
literal|"out"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// two files
name|exit
operator|=
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
name|f1
operator|.
name|toString
argument_list|()
block|,
name|f2
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1f2"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
comment|// two files, preserves order
name|exit
operator|=
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
name|f2
operator|.
name|toString
argument_list|()
block|,
name|f1
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f2f1"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
comment|// two files
name|exit
operator|=
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
literal|"-nl"
block|,
name|f1
operator|.
name|toString
argument_list|()
block|,
name|f2
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1\nf2\n"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
comment|// glob three files
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
literal|"-nl"
block|,
operator|new
name|Path
argument_list|(
name|root
argument_list|,
literal|"f*"
argument_list|)
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1\nf2\nf3\n"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
comment|// directory with 3 files, should skip subdir
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
literal|"-nl"
block|,
name|root
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1\nf2\nf3\n"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
comment|// subdir
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
literal|"-nl"
block|,
name|d
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"df1\ndf2\ndf3\n"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
comment|// file, dir, file
name|shell
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"-getmerge"
block|,
literal|"-nl"
block|,
name|f1
operator|.
name|toString
argument_list|()
block|,
name|d
operator|.
name|toString
argument_list|()
block|,
name|f2
operator|.
name|toString
argument_list|()
block|,
literal|"out"
block|}
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|exit
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"f1\ndf1\ndf2\ndf3\nf2\n"
argument_list|,
name|readFile
argument_list|(
literal|"out"
argument_list|)
argument_list|)
expr_stmt|;
block|}
DECL|method|createFile (Path .... paths)
specifier|private
name|void
name|createFile
parameter_list|(
name|Path
modifier|...
name|paths
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Path
name|path
range|:
name|paths
control|)
block|{
name|FSDataOutputStream
name|out
init|=
name|lfs
operator|.
name|create
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|out
operator|.
name|write
argument_list|(
name|path
operator|.
name|getName
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readFile (String out)
specifier|private
name|String
name|readFile
parameter_list|(
name|String
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|Path
name|path
init|=
operator|new
name|Path
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|FileStatus
name|stat
init|=
name|lfs
operator|.
name|getFileStatus
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|FSDataInputStream
name|in
init|=
name|lfs
operator|.
name|open
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|stat
operator|.
name|getLen
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|readFully
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|lfs
operator|.
name|delete
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buffer
argument_list|)
return|;
block|}
comment|// path handles "." rather oddly
DECL|method|pathAsString (Path p)
specifier|private
name|String
name|pathAsString
parameter_list|(
name|Path
name|p
parameter_list|)
block|{
name|String
name|s
init|=
operator|(
name|p
operator|==
literal|null
operator|)
condition|?
name|Path
operator|.
name|CUR_DIR
else|:
name|p
operator|.
name|toString
argument_list|()
decl_stmt|;
return|return
name|s
operator|.
name|isEmpty
argument_list|()
condition|?
name|Path
operator|.
name|CUR_DIR
else|:
name|s
return|;
block|}
block|}
end_class

end_unit

