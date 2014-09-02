begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.shell
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|shell
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
name|File
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
name|InputStream
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
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|io
operator|.
name|IOUtils
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
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * This class tests the logic for displaying the binary formats supported  * by the Text command.  */
end_comment

begin_class
DECL|class|TestTextCommand
specifier|public
class|class
name|TestTextCommand
block|{
DECL|field|TEST_ROOT_DIR
specifier|private
specifier|static
specifier|final
name|String
name|TEST_ROOT_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"test.build.data"
argument_list|,
literal|"build/test/data/"
argument_list|)
operator|+
literal|"/testText"
decl_stmt|;
DECL|field|AVRO_FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|AVRO_FILENAME
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"weather.avro"
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
DECL|field|TEXT_FILENAME
specifier|private
specifier|static
specifier|final
name|String
name|TEXT_FILENAME
init|=
operator|new
name|Path
argument_list|(
name|TEST_ROOT_DIR
argument_list|,
literal|"testtextfile.txt"
argument_list|)
operator|.
name|toUri
argument_list|()
operator|.
name|getPath
argument_list|()
decl_stmt|;
comment|/**    * Tests whether binary Avro data files are displayed correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testDisplayForAvroFiles ()
specifier|public
name|void
name|testDisplayForAvroFiles
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|expectedOutput
init|=
literal|"{\"station\":\"011990-99999\",\"time\":-619524000000,\"temp\":0}"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"{\"station\":\"011990-99999\",\"time\":-619506000000,\"temp\":22}"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"{\"station\":\"011990-99999\",\"time\":-619484400000,\"temp\":-11}"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"{\"station\":\"012650-99999\",\"time\":-655531200000,\"temp\":111}"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
operator|+
literal|"{\"station\":\"012650-99999\",\"time\":-655509600000,\"temp\":78}"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"line.separator"
argument_list|)
decl_stmt|;
name|String
name|output
init|=
name|readUsingTextCommand
argument_list|(
name|AVRO_FILENAME
argument_list|,
name|generateWeatherAvroBinaryData
argument_list|()
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expectedOutput
argument_list|,
name|output
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a zero-length file is displayed correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testEmptyTextFil ()
specifier|public
name|void
name|testEmptyTextFil
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|emptyContents
init|=
block|{ }
decl_stmt|;
name|String
name|output
init|=
name|readUsingTextCommand
argument_list|(
name|TEXT_FILENAME
argument_list|,
name|emptyContents
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|""
operator|.
name|equals
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a one-byte file is displayed correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testOneByteTextFil ()
specifier|public
name|void
name|testOneByteTextFil
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|oneByteContents
init|=
block|{
literal|'x'
block|}
decl_stmt|;
name|String
name|output
init|=
name|readUsingTextCommand
argument_list|(
name|TEXT_FILENAME
argument_list|,
name|oneByteContents
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|oneByteContents
argument_list|)
operator|.
name|equals
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Tests that a one-byte file is displayed correctly.    */
annotation|@
name|Test
argument_list|(
name|timeout
operator|=
literal|30000
argument_list|)
DECL|method|testTwoByteTextFil ()
specifier|public
name|void
name|testTwoByteTextFil
parameter_list|()
throws|throws
name|Exception
block|{
name|byte
index|[]
name|twoByteContents
init|=
block|{
literal|'x'
block|,
literal|'y'
block|}
decl_stmt|;
name|String
name|output
init|=
name|readUsingTextCommand
argument_list|(
name|TEXT_FILENAME
argument_list|,
name|twoByteContents
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
operator|new
name|String
argument_list|(
name|twoByteContents
argument_list|)
operator|.
name|equals
argument_list|(
name|output
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// Create a file on the local file system and read it using
comment|// the Display.Text class.
DECL|method|readUsingTextCommand (String fileName, byte[] fileContents)
specifier|private
name|String
name|readUsingTextCommand
parameter_list|(
name|String
name|fileName
parameter_list|,
name|byte
index|[]
name|fileContents
parameter_list|)
throws|throws
name|Exception
block|{
name|createFile
argument_list|(
name|fileName
argument_list|,
name|fileContents
argument_list|)
expr_stmt|;
comment|// Prepare and call the Text command's protected getInputStream method
comment|// using reflection.
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|URI
name|localPath
init|=
operator|new
name|URI
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|PathData
name|pathData
init|=
operator|new
name|PathData
argument_list|(
name|localPath
argument_list|,
name|conf
argument_list|)
decl_stmt|;
name|Display
operator|.
name|Text
name|text
init|=
operator|new
name|Display
operator|.
name|Text
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|(
name|PathData
name|item
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|super
operator|.
name|getInputStream
argument_list|(
name|item
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|text
operator|.
name|setConf
argument_list|(
name|conf
argument_list|)
expr_stmt|;
name|InputStream
name|stream
init|=
operator|(
name|InputStream
operator|)
name|text
operator|.
name|getInputStream
argument_list|(
name|pathData
argument_list|)
decl_stmt|;
return|return
name|inputStreamToString
argument_list|(
name|stream
argument_list|)
return|;
block|}
DECL|method|inputStreamToString (InputStream stream)
specifier|private
name|String
name|inputStreamToString
parameter_list|(
name|InputStream
name|stream
parameter_list|)
throws|throws
name|IOException
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|IOUtils
operator|.
name|copy
argument_list|(
name|stream
argument_list|,
name|writer
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|createFile (String fileName, byte[] contents)
specifier|private
name|void
name|createFile
parameter_list|(
name|String
name|fileName
parameter_list|,
name|byte
index|[]
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
operator|(
operator|new
name|File
argument_list|(
name|TEST_ROOT_DIR
argument_list|)
operator|)
operator|.
name|mkdir
argument_list|()
expr_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|fileName
argument_list|)
decl_stmt|;
name|file
operator|.
name|createNewFile
argument_list|()
expr_stmt|;
name|FileOutputStream
name|stream
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|stream
operator|.
name|write
argument_list|(
name|contents
argument_list|)
expr_stmt|;
name|stream
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
DECL|method|generateWeatherAvroBinaryData ()
specifier|private
name|byte
index|[]
name|generateWeatherAvroBinaryData
parameter_list|()
block|{
comment|// The contents of a simple binary Avro file with weather records.
name|byte
index|[]
name|contents
init|=
block|{
operator|(
name|byte
operator|)
literal|0x4f
block|,
operator|(
name|byte
operator|)
literal|0x62
block|,
operator|(
name|byte
operator|)
literal|0x6a
block|,
operator|(
name|byte
operator|)
literal|0x1
block|,
operator|(
name|byte
operator|)
literal|0x4
block|,
operator|(
name|byte
operator|)
literal|0x14
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x76
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x2e
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x64
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x8
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x75
block|,
operator|(
name|byte
operator|)
literal|0x6c
block|,
operator|(
name|byte
operator|)
literal|0x6c
block|,
operator|(
name|byte
operator|)
literal|0x16
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x76
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x2e
block|,
operator|(
name|byte
operator|)
literal|0x73
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x68
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0xf2
block|,
operator|(
name|byte
operator|)
literal|0x2
block|,
operator|(
name|byte
operator|)
literal|0x7b
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x79
block|,
operator|(
name|byte
operator|)
literal|0x70
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x64
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x57
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x68
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x73
block|,
operator|(
name|byte
operator|)
literal|0x70
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x73
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x66
block|,
operator|(
name|byte
operator|)
literal|0x69
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x6c
block|,
operator|(
name|byte
operator|)
literal|0x64
block|,
operator|(
name|byte
operator|)
literal|0x73
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x5b
block|,
operator|(
name|byte
operator|)
literal|0x7b
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x73
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x69
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x79
block|,
operator|(
name|byte
operator|)
literal|0x70
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x73
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x69
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x67
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x7d
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x7b
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x69
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x79
block|,
operator|(
name|byte
operator|)
literal|0x70
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x6c
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x67
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x7d
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x7b
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x6d
block|,
operator|(
name|byte
operator|)
literal|0x70
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x79
block|,
operator|(
name|byte
operator|)
literal|0x70
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x69
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x7d
block|,
operator|(
name|byte
operator|)
literal|0x5d
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x64
block|,
operator|(
name|byte
operator|)
literal|0x6f
block|,
operator|(
name|byte
operator|)
literal|0x63
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x3a
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x41
block|,
operator|(
name|byte
operator|)
literal|0x20
block|,
operator|(
name|byte
operator|)
literal|0x77
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x74
block|,
operator|(
name|byte
operator|)
literal|0x68
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x20
block|,
operator|(
name|byte
operator|)
literal|0x72
block|,
operator|(
name|byte
operator|)
literal|0x65
block|,
operator|(
name|byte
operator|)
literal|0x61
block|,
operator|(
name|byte
operator|)
literal|0x64
block|,
operator|(
name|byte
operator|)
literal|0x69
block|,
operator|(
name|byte
operator|)
literal|0x6e
block|,
operator|(
name|byte
operator|)
literal|0x67
block|,
operator|(
name|byte
operator|)
literal|0x2e
block|,
operator|(
name|byte
operator|)
literal|0x22
block|,
operator|(
name|byte
operator|)
literal|0x7d
block|,
operator|(
name|byte
operator|)
literal|0x0
block|,
operator|(
name|byte
operator|)
literal|0xb0
block|,
operator|(
name|byte
operator|)
literal|0x81
block|,
operator|(
name|byte
operator|)
literal|0xb3
block|,
operator|(
name|byte
operator|)
literal|0xc4
block|,
operator|(
name|byte
operator|)
literal|0xa
block|,
operator|(
name|byte
operator|)
literal|0xc
block|,
operator|(
name|byte
operator|)
literal|0xf6
block|,
operator|(
name|byte
operator|)
literal|0x62
block|,
operator|(
name|byte
operator|)
literal|0xfa
block|,
operator|(
name|byte
operator|)
literal|0xc9
block|,
operator|(
name|byte
operator|)
literal|0x38
block|,
operator|(
name|byte
operator|)
literal|0xfd
block|,
operator|(
name|byte
operator|)
literal|0x7e
block|,
operator|(
name|byte
operator|)
literal|0x52
block|,
operator|(
name|byte
operator|)
literal|0x0
block|,
operator|(
name|byte
operator|)
literal|0xa7
block|,
operator|(
name|byte
operator|)
literal|0xa
block|,
operator|(
name|byte
operator|)
literal|0xcc
block|,
operator|(
name|byte
operator|)
literal|0x1
block|,
operator|(
name|byte
operator|)
literal|0x18
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x2d
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xa3
block|,
operator|(
name|byte
operator|)
literal|0x90
block|,
operator|(
name|byte
operator|)
literal|0xe8
block|,
operator|(
name|byte
operator|)
literal|0x87
block|,
operator|(
name|byte
operator|)
literal|0x24
block|,
operator|(
name|byte
operator|)
literal|0x0
block|,
operator|(
name|byte
operator|)
literal|0x18
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x2d
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0x81
block|,
operator|(
name|byte
operator|)
literal|0xfb
block|,
operator|(
name|byte
operator|)
literal|0xd6
block|,
operator|(
name|byte
operator|)
literal|0x87
block|,
operator|(
name|byte
operator|)
literal|0x24
block|,
operator|(
name|byte
operator|)
literal|0x2c
block|,
operator|(
name|byte
operator|)
literal|0x18
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x2d
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xa5
block|,
operator|(
name|byte
operator|)
literal|0xae
block|,
operator|(
name|byte
operator|)
literal|0xc2
block|,
operator|(
name|byte
operator|)
literal|0x87
block|,
operator|(
name|byte
operator|)
literal|0x24
block|,
operator|(
name|byte
operator|)
literal|0x15
block|,
operator|(
name|byte
operator|)
literal|0x18
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x32
block|,
operator|(
name|byte
operator|)
literal|0x36
block|,
operator|(
name|byte
operator|)
literal|0x35
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x2d
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xb7
block|,
operator|(
name|byte
operator|)
literal|0xa2
block|,
operator|(
name|byte
operator|)
literal|0x8b
block|,
operator|(
name|byte
operator|)
literal|0x94
block|,
operator|(
name|byte
operator|)
literal|0x26
block|,
operator|(
name|byte
operator|)
literal|0xde
block|,
operator|(
name|byte
operator|)
literal|0x1
block|,
operator|(
name|byte
operator|)
literal|0x18
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x31
block|,
operator|(
name|byte
operator|)
literal|0x32
block|,
operator|(
name|byte
operator|)
literal|0x36
block|,
operator|(
name|byte
operator|)
literal|0x35
block|,
operator|(
name|byte
operator|)
literal|0x30
block|,
operator|(
name|byte
operator|)
literal|0x2d
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0x39
block|,
operator|(
name|byte
operator|)
literal|0xff
block|,
operator|(
name|byte
operator|)
literal|0xdb
block|,
operator|(
name|byte
operator|)
literal|0xd5
block|,
operator|(
name|byte
operator|)
literal|0xf6
block|,
operator|(
name|byte
operator|)
literal|0x93
block|,
operator|(
name|byte
operator|)
literal|0x26
block|,
operator|(
name|byte
operator|)
literal|0x9c
block|,
operator|(
name|byte
operator|)
literal|0x1
block|,
operator|(
name|byte
operator|)
literal|0xb0
block|,
operator|(
name|byte
operator|)
literal|0x81
block|,
operator|(
name|byte
operator|)
literal|0xb3
block|,
operator|(
name|byte
operator|)
literal|0xc4
block|,
operator|(
name|byte
operator|)
literal|0xa
block|,
operator|(
name|byte
operator|)
literal|0xc
block|,
operator|(
name|byte
operator|)
literal|0xf6
block|,
operator|(
name|byte
operator|)
literal|0x62
block|,
operator|(
name|byte
operator|)
literal|0xfa
block|,
operator|(
name|byte
operator|)
literal|0xc9
block|,
operator|(
name|byte
operator|)
literal|0x38
block|,
operator|(
name|byte
operator|)
literal|0xfd
block|,
operator|(
name|byte
operator|)
literal|0x7e
block|,
operator|(
name|byte
operator|)
literal|0x52
block|,
operator|(
name|byte
operator|)
literal|0x0
block|,
operator|(
name|byte
operator|)
literal|0xa7
block|,     }
decl_stmt|;
return|return
name|contents
return|;
block|}
block|}
end_class

end_unit

