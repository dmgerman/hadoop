begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.crypto.key
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|crypto
operator|.
name|key
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
name|ByteArrayOutputStream
import|;
end_import

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
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
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
name|After
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
DECL|class|TestKeyShell
specifier|public
class|class
name|TestKeyShell
block|{
DECL|field|outContent
specifier|private
specifier|final
name|ByteArrayOutputStream
name|outContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|errContent
specifier|private
specifier|final
name|ByteArrayOutputStream
name|errContent
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
DECL|field|tmpDir
specifier|private
specifier|static
name|File
name|tmpDir
decl_stmt|;
DECL|field|initialStdOut
specifier|private
name|PrintStream
name|initialStdOut
decl_stmt|;
DECL|field|initialStdErr
specifier|private
name|PrintStream
name|initialStdErr
decl_stmt|;
annotation|@
name|Before
DECL|method|setup ()
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|Exception
block|{
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|errContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|tmpDir
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"target"
argument_list|)
argument_list|,
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|tmpDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|initialStdOut
operator|=
name|System
operator|.
name|out
expr_stmt|;
name|initialStdErr
operator|=
name|System
operator|.
name|err
expr_stmt|;
name|System
operator|.
name|setOut
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|outContent
argument_list|)
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
operator|new
name|PrintStream
argument_list|(
name|errContent
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|cleanUp ()
specifier|public
name|void
name|cleanUp
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|setOut
argument_list|(
name|initialStdOut
argument_list|)
expr_stmt|;
name|System
operator|.
name|setErr
argument_list|(
name|initialStdErr
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testKeySuccessfulKeyLifecycle ()
specifier|public
name|void
name|testKeySuccessfulKeyLifecycle
parameter_list|()
throws|throws
name|Exception
block|{
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has been successfully "
operator|+
literal|"created."
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args2
init|=
block|{
literal|"list"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args2a
init|=
block|{
literal|"list"
block|,
literal|"--metadata"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args2a
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"description"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"created"
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args3
init|=
block|{
literal|"roll"
block|,
literal|"key1"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has been successfully "
operator|+
literal|"rolled."
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args4
init|=
block|{
literal|"delete"
block|,
literal|"key1"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args4
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has been successfully "
operator|+
literal|"deleted."
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args5
init|=
block|{
literal|"list"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args5
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidKeySize ()
specifier|public
name|void
name|testInvalidKeySize
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|,
literal|"--size"
block|,
literal|"56"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has NOT been created."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidCipher ()
specifier|public
name|void
name|testInvalidCipher
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|,
literal|"--cipher"
block|,
literal|"LJM"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has NOT been created."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testInvalidProvider ()
specifier|public
name|void
name|testInvalidProvider
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|,
literal|"--cipher"
block|,
literal|"AES"
block|,
literal|"--provider"
block|,
literal|"sdff://file/tmp/keystore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There are no valid "
operator|+
literal|"KeyProviders configured."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransientProviderWarning ()
specifier|public
name|void
name|testTransientProviderWarning
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|,
literal|"--cipher"
block|,
literal|"AES"
block|,
literal|"--provider"
block|,
literal|"user:///"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"WARNING: you are modifying a "
operator|+
literal|"transient provider."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testTransientProviderOnlyConfig ()
specifier|public
name|void
name|testTransientProviderOnlyConfig
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|config
operator|.
name|set
argument_list|(
name|KeyProviderFactory
operator|.
name|KEY_PROVIDER_PATH
argument_list|,
literal|"user:///"
argument_list|)
expr_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"There are no valid "
operator|+
literal|"KeyProviders configured."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFullCipher ()
specifier|public
name|void
name|testFullCipher
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|args1
init|=
block|{
literal|"create"
block|,
literal|"key1"
block|,
literal|"--cipher"
block|,
literal|"AES/CBC/pkcs5Padding"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|int
name|rc
init|=
literal|0
decl_stmt|;
name|KeyShell
name|ks
init|=
operator|new
name|KeyShell
argument_list|()
decl_stmt|;
name|ks
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has been successfully "
operator|+
literal|"created."
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|String
index|[]
name|args2
init|=
block|{
literal|"delete"
block|,
literal|"key1"
block|,
literal|"--provider"
block|,
literal|"jceks://file"
operator|+
name|tmpDir
operator|+
literal|"/keystore.jceks"
block|}
decl_stmt|;
name|rc
operator|=
name|ks
operator|.
name|run
argument_list|(
name|args2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
literal|"key1 has been successfully "
operator|+
literal|"deleted."
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

