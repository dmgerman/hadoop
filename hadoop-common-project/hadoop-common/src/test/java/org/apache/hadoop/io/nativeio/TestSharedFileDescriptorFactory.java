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
name|org
operator|.
name|junit
operator|.
name|Assert
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Assume
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
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang
operator|.
name|SystemUtils
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

begin_class
DECL|class|TestSharedFileDescriptorFactory
specifier|public
class|class
name|TestSharedFileDescriptorFactory
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
name|TestSharedFileDescriptorFactory
operator|.
name|class
argument_list|)
decl_stmt|;
DECL|field|TEST_BASE
specifier|private
specifier|static
specifier|final
name|File
name|TEST_BASE
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
decl_stmt|;
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|10000
argument_list|)
DECL|method|testReadAndWrite ()
specifier|public
name|void
name|testReadAndWrite
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeIO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|SystemUtils
operator|.
name|IS_OS_UNIX
argument_list|)
expr_stmt|;
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|TEST_BASE
argument_list|,
literal|"testReadAndWrite"
argument_list|)
decl_stmt|;
name|path
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|SharedFileDescriptorFactory
name|factory
init|=
operator|new
name|SharedFileDescriptorFactory
argument_list|(
literal|"woot_"
argument_list|,
name|path
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
name|FileInputStream
name|inStream
init|=
name|factory
operator|.
name|createDescriptor
argument_list|(
literal|4096
argument_list|)
decl_stmt|;
name|FileOutputStream
name|outStream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|inStream
operator|.
name|getFD
argument_list|()
argument_list|)
decl_stmt|;
name|outStream
operator|.
name|write
argument_list|(
literal|101
argument_list|)
expr_stmt|;
name|inStream
operator|.
name|getChannel
argument_list|()
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|101
argument_list|,
name|inStream
operator|.
name|read
argument_list|()
argument_list|)
expr_stmt|;
name|inStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|outStream
operator|.
name|close
argument_list|()
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
DECL|method|createTempFile (String path)
specifier|static
specifier|private
name|void
name|createTempFile
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|Exception
block|{
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|fos
operator|.
name|write
argument_list|(
literal|101
argument_list|)
expr_stmt|;
name|fos
operator|.
name|close
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
DECL|method|testCleanupRemainders ()
specifier|public
name|void
name|testCleanupRemainders
parameter_list|()
throws|throws
name|Exception
block|{
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|NativeIO
operator|.
name|isAvailable
argument_list|()
argument_list|)
expr_stmt|;
name|Assume
operator|.
name|assumeTrue
argument_list|(
name|SystemUtils
operator|.
name|IS_OS_UNIX
argument_list|)
expr_stmt|;
name|File
name|path
init|=
operator|new
name|File
argument_list|(
name|TEST_BASE
argument_list|,
literal|"testCleanupRemainders"
argument_list|)
decl_stmt|;
name|path
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|String
name|remainder1
init|=
name|path
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"woot2_remainder1"
decl_stmt|;
name|String
name|remainder2
init|=
name|path
operator|.
name|getAbsolutePath
argument_list|()
operator|+
name|Path
operator|.
name|SEPARATOR
operator|+
literal|"woot2_remainder2"
decl_stmt|;
name|createTempFile
argument_list|(
name|remainder1
argument_list|)
expr_stmt|;
name|createTempFile
argument_list|(
name|remainder2
argument_list|)
expr_stmt|;
operator|new
name|SharedFileDescriptorFactory
argument_list|(
literal|"woot2_"
argument_list|,
name|path
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
comment|// creating the SharedFileDescriptorFactory should have removed
comment|// the remainders
name|Assert
operator|.
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|remainder1
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertFalse
argument_list|(
operator|new
name|File
argument_list|(
name|remainder2
argument_list|)
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtil
operator|.
name|fullyDelete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

