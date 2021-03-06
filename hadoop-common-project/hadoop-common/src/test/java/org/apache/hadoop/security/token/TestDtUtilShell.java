begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.security.token
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|security
operator|.
name|token
package|;
end_package

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
name|DataInputStream
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
name|IOException
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
name|io
operator|.
name|Text
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
name|security
operator|.
name|Credentials
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
name|assertFalse
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

begin_import
import|import
name|org
operator|.
name|mockito
operator|.
name|Mockito
import|;
end_import

begin_class
DECL|class|TestDtUtilShell
specifier|public
class|class
name|TestDtUtilShell
block|{
DECL|field|IDENTIFIER
specifier|private
specifier|static
name|byte
index|[]
name|IDENTIFIER
init|=
block|{
literal|0x69
block|,
literal|0x64
block|,
literal|0x65
block|,
literal|0x6e
block|,
literal|0x74
block|,
literal|0x69
block|,
literal|0x66
block|,
literal|0x69
block|,
literal|0x65
block|,
literal|0x72
block|}
decl_stmt|;
DECL|field|PASSWORD
specifier|private
specifier|static
name|byte
index|[]
name|PASSWORD
init|=
block|{
literal|0x70
block|,
literal|0x61
block|,
literal|0x73
block|,
literal|0x73
block|,
literal|0x77
block|,
literal|0x6f
block|,
literal|0x72
block|,
literal|0x64
block|}
decl_stmt|;
DECL|field|KIND
specifier|private
specifier|static
name|Text
name|KIND
init|=
operator|new
name|Text
argument_list|(
literal|"testTokenKind"
argument_list|)
decl_stmt|;
DECL|field|SERVICE
specifier|private
specifier|static
name|Text
name|SERVICE
init|=
operator|new
name|Text
argument_list|(
literal|"testTokenService"
argument_list|)
decl_stmt|;
DECL|field|SERVICE2
specifier|private
specifier|static
name|Text
name|SERVICE2
init|=
operator|new
name|Text
argument_list|(
literal|"ecivreSnekoTtset"
argument_list|)
decl_stmt|;
DECL|field|defaultConf
specifier|private
specifier|static
name|Configuration
name|defaultConf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
DECL|field|localFs
specifier|private
specifier|static
name|FileSystem
name|localFs
init|=
literal|null
decl_stmt|;
DECL|field|alias
specifier|private
specifier|final
name|String
name|alias
init|=
literal|"proxy_ip:1234"
decl_stmt|;
DECL|field|getUrl
specifier|private
specifier|final
name|String
name|getUrl
init|=
name|SERVICE_GET
operator|.
name|toString
argument_list|()
operator|+
literal|"://localhost:9000/"
decl_stmt|;
DECL|field|getUrl2
specifier|private
specifier|final
name|String
name|getUrl2
init|=
literal|"http://localhost:9000/"
decl_stmt|;
DECL|field|SERVICE_GET
specifier|public
specifier|static
name|Text
name|SERVICE_GET
init|=
operator|new
name|Text
argument_list|(
literal|"testTokenServiceGet"
argument_list|)
decl_stmt|;
DECL|field|KIND_GET
specifier|public
specifier|static
name|Text
name|KIND_GET
init|=
operator|new
name|Text
argument_list|(
literal|"testTokenKindGet"
argument_list|)
decl_stmt|;
DECL|field|MOCK_TOKEN
specifier|public
specifier|static
name|Token
argument_list|<
name|?
argument_list|>
name|MOCK_TOKEN
init|=
operator|new
name|Token
argument_list|(
name|IDENTIFIER
argument_list|,
name|PASSWORD
argument_list|,
name|KIND_GET
argument_list|,
name|SERVICE_GET
argument_list|)
decl_stmt|;
DECL|field|SERVICE_IMPORT
specifier|private
specifier|static
specifier|final
name|Text
name|SERVICE_IMPORT
init|=
operator|new
name|Text
argument_list|(
literal|"testTokenServiceImport"
argument_list|)
decl_stmt|;
DECL|field|KIND_IMPORT
specifier|private
specifier|static
specifier|final
name|Text
name|KIND_IMPORT
init|=
operator|new
name|Text
argument_list|(
literal|"testTokenKindImport"
argument_list|)
decl_stmt|;
DECL|field|IMPORT_TOKEN
specifier|private
specifier|static
specifier|final
name|Token
argument_list|<
name|?
argument_list|>
name|IMPORT_TOKEN
init|=
operator|new
name|Token
argument_list|(
name|IDENTIFIER
argument_list|,
name|PASSWORD
argument_list|,
name|KIND_IMPORT
argument_list|,
name|SERVICE_IMPORT
argument_list|)
decl_stmt|;
static|static
block|{
try|try
block|{
name|defaultConf
operator|.
name|set
argument_list|(
literal|"fs.defaultFS"
argument_list|,
literal|"file:///"
argument_list|)
expr_stmt|;
name|localFs
operator|=
name|FileSystem
operator|.
name|getLocal
argument_list|(
name|defaultConf
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"init failure"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
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
DECL|field|workDir
specifier|private
specifier|final
name|Path
name|workDir
init|=
operator|new
name|Path
argument_list|(
name|GenericTestUtils
operator|.
name|getTestDir
argument_list|(
literal|"TestDtUtilShell"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
decl_stmt|;
DECL|field|tokenFile
specifier|private
specifier|final
name|Path
name|tokenFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"testPrintTokenFile"
argument_list|)
decl_stmt|;
DECL|field|tokenFile2
specifier|private
specifier|final
name|Path
name|tokenFile2
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"testPrintTokenFile2"
argument_list|)
decl_stmt|;
DECL|field|tokenLegacyFile
specifier|private
specifier|final
name|Path
name|tokenLegacyFile
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"testPrintTokenFile3"
argument_list|)
decl_stmt|;
DECL|field|tokenFileGet
specifier|private
specifier|final
name|Path
name|tokenFileGet
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"testGetTokenFile"
argument_list|)
decl_stmt|;
DECL|field|tokenFileImport
specifier|private
specifier|final
name|Path
name|tokenFileImport
init|=
operator|new
name|Path
argument_list|(
name|workDir
argument_list|,
literal|"testImportTokenFile"
argument_list|)
decl_stmt|;
DECL|field|tokenFilename
specifier|private
specifier|final
name|String
name|tokenFilename
init|=
name|tokenFile
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|tokenFilename2
specifier|private
specifier|final
name|String
name|tokenFilename2
init|=
name|tokenFile2
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|tokenFilenameGet
specifier|private
specifier|final
name|String
name|tokenFilenameGet
init|=
name|tokenFileGet
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|tokenFilenameImport
specifier|private
specifier|final
name|String
name|tokenFilenameImport
init|=
name|tokenFileImport
operator|.
name|toString
argument_list|()
decl_stmt|;
DECL|field|args
specifier|private
name|String
index|[]
name|args
init|=
literal|null
decl_stmt|;
DECL|field|dt
specifier|private
name|DtUtilShell
name|dt
init|=
literal|null
decl_stmt|;
DECL|field|rc
specifier|private
name|int
name|rc
init|=
literal|0
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
name|localFs
operator|.
name|mkdirs
argument_list|(
name|localFs
operator|.
name|makeQualified
argument_list|(
name|workDir
argument_list|)
argument_list|)
expr_stmt|;
name|makeTokenFile
argument_list|(
name|tokenFile
argument_list|,
literal|false
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|makeTokenFile
argument_list|(
name|tokenFile2
argument_list|,
literal|false
argument_list|,
name|SERVICE2
argument_list|)
expr_stmt|;
name|makeTokenFile
argument_list|(
name|tokenLegacyFile
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|dt
operator|=
operator|new
name|DtUtilShell
argument_list|()
expr_stmt|;
name|dt
operator|.
name|setConf
argument_list|(
operator|new
name|Configuration
argument_list|()
argument_list|)
expr_stmt|;
name|dt
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
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|rc
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|After
DECL|method|teardown ()
specifier|public
name|void
name|teardown
parameter_list|()
throws|throws
name|Exception
block|{
name|localFs
operator|.
name|delete
argument_list|(
name|localFs
operator|.
name|makeQualified
argument_list|(
name|workDir
argument_list|)
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
DECL|method|makeTokenFile (Path tokenPath, boolean legacy, Text service)
specifier|public
name|void
name|makeTokenFile
parameter_list|(
name|Path
name|tokenPath
parameter_list|,
name|boolean
name|legacy
parameter_list|,
name|Text
name|service
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|service
operator|==
literal|null
condition|)
block|{
name|service
operator|=
name|SERVICE
expr_stmt|;
block|}
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
name|tok
init|=
operator|(
name|Token
argument_list|<
name|?
extends|extends
name|TokenIdentifier
argument_list|>
operator|)
operator|new
name|Token
argument_list|(
name|IDENTIFIER
argument_list|,
name|PASSWORD
argument_list|,
name|KIND
argument_list|,
name|service
argument_list|)
decl_stmt|;
name|creds
operator|.
name|addToken
argument_list|(
name|tok
operator|.
name|getService
argument_list|()
argument_list|,
name|tok
argument_list|)
expr_stmt|;
name|Credentials
operator|.
name|SerializedFormat
name|format
init|=
name|Credentials
operator|.
name|SerializedFormat
operator|.
name|PROTOBUF
decl_stmt|;
if|if
condition|(
name|legacy
condition|)
block|{
name|format
operator|=
name|Credentials
operator|.
name|SerializedFormat
operator|.
name|WRITABLE
expr_stmt|;
block|}
name|creds
operator|.
name|writeTokenStorageFile
argument_list|(
name|tokenPath
argument_list|,
name|defaultConf
argument_list|,
name|format
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testPrint ()
specifier|public
name|void
name|testPrint
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilename
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple print exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple print output kind:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple print output service:\n"
operator|+
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
name|SERVICE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenLegacyFile
operator|.
name|toString
argument_list|()
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test legacy print exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple print output kind:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple print output service:\n"
operator|+
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
name|SERVICE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
literal|"-alias"
block|,
name|SERVICE
operator|.
name|toString
argument_list|()
block|,
name|tokenFilename
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test alias print exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple print output kind:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple print output service:\n"
operator|+
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
name|SERVICE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|outContent
operator|.
name|reset
argument_list|()
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
literal|"-alias"
block|,
literal|"not-a-serivce"
block|,
name|tokenFilename
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test no alias print exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"test no alias print output kind:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"test no alias print output service:\n"
operator|+
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
name|SERVICE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testEdit ()
specifier|public
name|void
name|testEdit
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|oldService
init|=
name|SERVICE2
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|newAlias
init|=
literal|"newName:12345"
decl_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"edit"
block|,
literal|"-service"
block|,
name|oldService
block|,
literal|"-alias"
block|,
name|newAlias
block|,
name|tokenFilename2
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple edit exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
literal|"-alias"
block|,
name|oldService
block|,
name|tokenFilename2
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple edit print old exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple edit output kind old:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple edit output service old:\n"
operator|+
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
name|oldService
argument_list|)
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
literal|"-alias"
block|,
name|newAlias
block|,
name|tokenFilename2
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple edit print new exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple edit output kind new:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple edit output service new:\n"
operator|+
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
name|newAlias
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testAppend ()
specifier|public
name|void
name|testAppend
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"append"
block|,
name|tokenFilename
block|,
name|tokenFilename2
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple append exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilename2
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple append print exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple append output kind:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple append output service:\n"
operator|+
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
name|SERVICE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test simple append output service:\n"
operator|+
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
name|SERVICE2
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testRemove ()
specifier|public
name|void
name|testRemove
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"remove"
block|,
literal|"-alias"
block|,
name|SERVICE
operator|.
name|toString
argument_list|()
block|,
name|tokenFilename
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple remove exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilename
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple remove print exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"test simple remove output kind:\n"
operator|+
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
name|KIND
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"test simple remove output service:\n"
operator|+
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
name|SERVICE
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGet ()
specifier|public
name|void
name|testGet
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"get"
block|,
name|getUrl
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test mocked get exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|oc
init|=
name|outContent
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test print after get exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after get output kind:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|KIND_GET
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after get output service:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|SERVICE_GET
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetWithServiceFlag ()
specifier|public
name|void
name|testGetWithServiceFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"get"
block|,
name|getUrl2
block|,
literal|"-service"
block|,
name|SERVICE_GET
operator|.
name|toString
argument_list|()
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test mocked get with service flag exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|oc
init|=
name|outContent
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test print after get with service flag exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after get with service flag output kind:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|KIND_GET
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after get with service flag output service:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|SERVICE_GET
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testGetWithAliasFlag ()
specifier|public
name|void
name|testGetWithAliasFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"get"
block|,
name|getUrl
block|,
literal|"-alias"
block|,
name|alias
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test mocked get with alias flag exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|String
name|oc
init|=
name|outContent
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"test print after get with alias flag exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after get with alias flag output kind:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|KIND_GET
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after get with alias flag output alias:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|alias
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"test print after get with alias flag output old service:\n"
operator|+
name|oc
argument_list|,
name|oc
operator|.
name|contains
argument_list|(
name|SERVICE_GET
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFormatJavaFlag ()
specifier|public
name|void
name|testFormatJavaFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"get"
block|,
name|getUrl
block|,
literal|"-format"
block|,
literal|"java"
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test mocked get with java format flag exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Credentials
name|spyCreds
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|creds
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tokenFilenameGet
argument_list|)
argument_list|)
decl_stmt|;
name|spyCreds
operator|.
name|readTokenStorageStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyCreds
argument_list|)
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testFormatProtoFlag ()
specifier|public
name|void
name|testFormatProtoFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"get"
block|,
name|getUrl
block|,
literal|"-format"
block|,
literal|"protobuf"
block|,
name|tokenFilenameGet
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test mocked get with protobuf format flag exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|Credentials
name|creds
init|=
operator|new
name|Credentials
argument_list|()
decl_stmt|;
name|Credentials
name|spyCreds
init|=
name|Mockito
operator|.
name|spy
argument_list|(
name|creds
argument_list|)
decl_stmt|;
name|DataInputStream
name|in
init|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|tokenFilenameGet
argument_list|)
argument_list|)
decl_stmt|;
name|spyCreds
operator|.
name|readTokenStorageStream
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|Mockito
operator|.
name|verify
argument_list|(
name|spyCreds
argument_list|,
name|Mockito
operator|.
name|never
argument_list|()
argument_list|)
operator|.
name|readFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testImport ()
specifier|public
name|void
name|testImport
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|base64
init|=
name|IMPORT_TOKEN
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"import"
block|,
name|base64
block|,
name|tokenFilenameImport
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple import print old exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilenameImport
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple import print old exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after import output:\n"
operator|+
name|outContent
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|KIND_IMPORT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after import output:\n"
operator|+
name|outContent
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|SERVICE_IMPORT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after simple import output:\n"
operator|+
name|outContent
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|base64
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testImportWithAliasFlag ()
specifier|public
name|void
name|testImportWithAliasFlag
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|base64
init|=
name|IMPORT_TOKEN
operator|.
name|encodeToUrlString
argument_list|()
decl_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"import"
block|,
name|base64
block|,
literal|"-alias"
block|,
name|alias
block|,
name|tokenFilenameImport
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test import with alias print old exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|args
operator|=
operator|new
name|String
index|[]
block|{
literal|"print"
block|,
name|tokenFilenameImport
block|}
expr_stmt|;
name|rc
operator|=
name|dt
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"test simple import print old exit code"
argument_list|,
literal|0
argument_list|,
name|rc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after import output:\n"
operator|+
name|outContent
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|KIND_IMPORT
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"test print after import with alias output:\n"
operator|+
name|outContent
argument_list|,
name|outContent
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|alias
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

