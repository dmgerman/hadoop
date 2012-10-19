begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.nativeio
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|nativeio
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
name|FileDescriptor
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
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
name|Assume
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
name|util
operator|.
name|NativeCodeLoader
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
name|Time
import|;
end_import

begin_class
DECL|class|TestNativeIO
specifier|public
class|class
name|TestNativeIO
block|{
DECL|field|LOG
specifier|static
specifier|final
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TestNativeIO
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_DIR
specifier|static
specifier|final
name|File
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
argument_list|)
argument_list|,
literal|"testnativeio"
argument_list|)
decl_stmt|;
annotation|@
name|Before
DECL|method|checkLoaded ()
specifier|public
name|void
name|checkLoaded
parameter_list|()
block|{
name|assumeTrue
argument_list|(
name|NativeCodeLoader
operator|.
name|isNativeCodeLoaded
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
DECL|method|setupTestDir ()
specifier|public
name|void
name|setupTestDir
parameter_list|()
block|{
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|TEST_DIR
argument_list|)
expr_stmt|;
name|TEST_DIR
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFstat ()
specifier|public
name|void
name|testFstat
parameter_list|()
throws|throws
name|Exception
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testfstat"
argument_list|)
argument_list|)
decl_stmt|;
name|NativeIO
operator|.
name|Stat
name|stat
init|=
name|NativeIO
operator|.
name|fstat
argument_list|(
name|fos
operator|.
name|getFD
argument_list|()
argument_list|)
decl_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Stat: "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|stat
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|stat
operator|.
name|getGroup
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Stat mode field should indicate a regular file"
argument_list|,
name|NativeIO
operator|.
name|Stat
operator|.
name|S_IFREG
argument_list|,
name|stat
operator|.
name|getMode
argument_list|()
operator|&
name|NativeIO
operator|.
name|Stat
operator|.
name|S_IFMT
argument_list|)
expr_stmt|;
block|}
comment|/**    * Test for races in fstat usage    *    * NOTE: this test is likely to fail on RHEL 6.0 which has a non-threadsafe    * implementation of getpwuid_r.    */
annotation|@
name|Test
DECL|method|testMultiThreadedFstat ()
specifier|public
name|void
name|testMultiThreadedFstat
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testfstat"
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
name|thrown
init|=
operator|new
name|AtomicReference
argument_list|<
name|Throwable
argument_list|>
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|Thread
argument_list|>
name|statters
init|=
operator|new
name|ArrayList
argument_list|<
name|Thread
argument_list|>
argument_list|()
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
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|Thread
name|statter
init|=
operator|new
name|Thread
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|long
name|et
init|=
name|Time
operator|.
name|now
argument_list|()
operator|+
literal|5000
decl_stmt|;
while|while
condition|(
name|Time
operator|.
name|now
argument_list|()
operator|<
name|et
condition|)
block|{
try|try
block|{
name|NativeIO
operator|.
name|Stat
name|stat
init|=
name|NativeIO
operator|.
name|fstat
argument_list|(
name|fos
operator|.
name|getFD
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.name"
argument_list|)
argument_list|,
name|stat
operator|.
name|getOwner
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|stat
operator|.
name|getGroup
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
operator|!
name|stat
operator|.
name|getGroup
argument_list|()
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Stat mode field should indicate a regular file"
argument_list|,
name|NativeIO
operator|.
name|Stat
operator|.
name|S_IFREG
argument_list|,
name|stat
operator|.
name|getMode
argument_list|()
operator|&
name|NativeIO
operator|.
name|Stat
operator|.
name|S_IFMT
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|t
parameter_list|)
block|{
name|thrown
operator|.
name|set
argument_list|(
name|t
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
name|statters
operator|.
name|add
argument_list|(
name|statter
argument_list|)
expr_stmt|;
name|statter
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|Thread
name|t
range|:
name|statters
control|)
block|{
name|t
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|thrown
operator|.
name|get
argument_list|()
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|thrown
operator|.
name|get
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
DECL|method|testFstatClosedFd ()
specifier|public
name|void
name|testFstatClosedFd
parameter_list|()
throws|throws
name|Exception
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testfstat2"
argument_list|)
argument_list|)
decl_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
try|try
block|{
name|NativeIO
operator|.
name|Stat
name|stat
init|=
name|NativeIO
operator|.
name|fstat
argument_list|(
name|fos
operator|.
name|getFD
argument_list|()
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|nioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|nioe
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Errno
operator|.
name|EBADF
argument_list|,
name|nioe
operator|.
name|getErrno
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOpenMissingWithoutCreate ()
specifier|public
name|void
name|testOpenMissingWithoutCreate
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Open a missing file without O_CREAT and it should fail"
argument_list|)
expr_stmt|;
try|try
block|{
name|FileDescriptor
name|fd
init|=
name|NativeIO
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"doesntexist"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|NativeIO
operator|.
name|O_WRONLY
argument_list|,
literal|0700
argument_list|)
decl_stmt|;
name|fail
argument_list|(
literal|"Able to open a new file without O_CREAT"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|nioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception"
argument_list|,
name|nioe
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Errno
operator|.
name|ENOENT
argument_list|,
name|nioe
operator|.
name|getErrno
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
DECL|method|testOpenWithCreate ()
specifier|public
name|void
name|testOpenWithCreate
parameter_list|()
throws|throws
name|Exception
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Test creating a file with O_CREAT"
argument_list|)
expr_stmt|;
name|FileDescriptor
name|fd
init|=
name|NativeIO
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testWorkingOpen"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|NativeIO
operator|.
name|O_WRONLY
operator||
name|NativeIO
operator|.
name|O_CREAT
argument_list|,
literal|0700
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fd
operator|.
name|valid
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|fd
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertFalse
argument_list|(
name|fd
operator|.
name|valid
argument_list|()
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Test exclusive create"
argument_list|)
expr_stmt|;
try|try
block|{
name|fd
operator|=
name|NativeIO
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testWorkingOpen"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|NativeIO
operator|.
name|O_WRONLY
operator||
name|NativeIO
operator|.
name|O_CREAT
operator||
name|NativeIO
operator|.
name|O_EXCL
argument_list|,
literal|0700
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Was able to create existing file with O_EXCL"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|nioe
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Got expected exception for failed exclusive create"
argument_list|,
name|nioe
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Errno
operator|.
name|EEXIST
argument_list|,
name|nioe
operator|.
name|getErrno
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**    * Test that opens and closes a file 10000 times - this would crash with    * "Too many open files" if we leaked fds using this access pattern.    */
annotation|@
name|Test
DECL|method|testFDDoesntLeak ()
specifier|public
name|void
name|testFDDoesntLeak
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|FileDescriptor
name|fd
init|=
name|NativeIO
operator|.
name|open
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testNoFdLeak"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|NativeIO
operator|.
name|O_WRONLY
operator||
name|NativeIO
operator|.
name|O_CREAT
argument_list|,
literal|0700
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fd
operator|.
name|valid
argument_list|()
argument_list|)
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|fd
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**    * Test basic chmod operation    */
annotation|@
name|Test
DECL|method|testChmod ()
specifier|public
name|void
name|testChmod
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|NativeIO
operator|.
name|chmod
argument_list|(
literal|"/this/file/doesnt/exist"
argument_list|,
literal|777
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Chmod of non-existent file didn't fail"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|nioe
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Errno
operator|.
name|ENOENT
argument_list|,
name|nioe
operator|.
name|getErrno
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|File
name|toChmod
init|=
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testChmod"
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"Create test subject"
argument_list|,
name|toChmod
operator|.
name|exists
argument_list|()
operator|||
name|toChmod
operator|.
name|mkdir
argument_list|()
argument_list|)
expr_stmt|;
name|NativeIO
operator|.
name|chmod
argument_list|(
name|toChmod
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|toChmod
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
name|NativeIO
operator|.
name|chmod
argument_list|(
name|toChmod
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|0000
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|toChmod
argument_list|,
literal|0000
argument_list|)
expr_stmt|;
name|NativeIO
operator|.
name|chmod
argument_list|(
name|toChmod
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
literal|0644
argument_list|)
expr_stmt|;
name|assertPermissions
argument_list|(
name|toChmod
argument_list|,
literal|0644
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPosixFadvise ()
specifier|public
name|void
name|testPosixFadvise
parameter_list|()
throws|throws
name|Exception
block|{
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
literal|"/dev/zero"
argument_list|)
decl_stmt|;
try|try
block|{
name|NativeIO
operator|.
name|posix_fadvise
argument_list|(
name|fis
operator|.
name|getFD
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|,
name|NativeIO
operator|.
name|POSIX_FADV_SEQUENTIAL
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|// we should just skip the unit test on machines where we don't
comment|// have fadvise support
name|assumeTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|NativeIO
operator|.
name|posix_fadvise
argument_list|(
name|fis
operator|.
name|getFD
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|,
name|NativeIO
operator|.
name|POSIX_FADV_SEQUENTIAL
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw on bad file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|nioe
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Errno
operator|.
name|EBADF
argument_list|,
name|nioe
operator|.
name|getErrno
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|NativeIO
operator|.
name|posix_fadvise
argument_list|(
literal|null
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|,
name|NativeIO
operator|.
name|POSIX_FADV_SEQUENTIAL
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw on null file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NullPointerException
name|npe
parameter_list|)
block|{
comment|// expected
block|}
block|}
annotation|@
name|Test
DECL|method|testSyncFileRange ()
specifier|public
name|void
name|testSyncFileRange
parameter_list|()
throws|throws
name|Exception
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
operator|new
name|File
argument_list|(
name|TEST_DIR
argument_list|,
literal|"testSyncFileRange"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|fos
operator|.
name|write
argument_list|(
literal|"foo"
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|NativeIO
operator|.
name|sync_file_range
argument_list|(
name|fos
operator|.
name|getFD
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|,
name|NativeIO
operator|.
name|SYNC_FILE_RANGE_WRITE
argument_list|)
expr_stmt|;
comment|// no way to verify that this actually has synced,
comment|// but if it doesn't throw, we can assume it worked
block|}
catch|catch
parameter_list|(
name|UnsupportedOperationException
name|uoe
parameter_list|)
block|{
comment|// we should just skip the unit test on machines where we don't
comment|// have fadvise support
name|assumeTrue
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
try|try
block|{
name|NativeIO
operator|.
name|sync_file_range
argument_list|(
name|fos
operator|.
name|getFD
argument_list|()
argument_list|,
literal|0
argument_list|,
literal|1024
argument_list|,
name|NativeIO
operator|.
name|SYNC_FILE_RANGE_WRITE
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Did not throw on bad file"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NativeIOException
name|nioe
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|Errno
operator|.
name|EBADF
argument_list|,
name|nioe
operator|.
name|getErrno
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|assertPermissions (File f, int expected)
specifier|private
name|void
name|assertPermissions
parameter_list|(
name|File
name|f
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
name|FileSystem
name|localfs
init|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
decl_stmt|;
name|FsPermission
name|perms
init|=
name|localfs
operator|.
name|getFileStatus
argument_list|(
operator|new
name|Path
argument_list|(
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getPermission
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|perms
operator|.
name|toShort
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

