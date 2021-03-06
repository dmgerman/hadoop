begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *     http://www.apache.org/licenses/LICENSE-2.0  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.yarn.submarine.client.cli
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
package|;
end_package

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
name|FileUtils
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
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|runjob
operator|.
name|RunJobParameters
operator|.
name|UnderscoreConverterPropertyUtils
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
name|yarn
operator|.
name|submarine
operator|.
name|client
operator|.
name|cli
operator|.
name|param
operator|.
name|yaml
operator|.
name|YamlConfigFile
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|Yaml
import|;
end_import

begin_import
import|import
name|org
operator|.
name|yaml
operator|.
name|snakeyaml
operator|.
name|constructor
operator|.
name|Constructor
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
name|IOException
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

begin_comment
comment|/**  * Test utility class for test code that deals with YAML configuration parsing.  */
end_comment

begin_class
DECL|class|YamlConfigTestUtils
specifier|public
specifier|final
class|class
name|YamlConfigTestUtils
block|{
DECL|method|YamlConfigTestUtils ()
specifier|private
name|YamlConfigTestUtils
parameter_list|()
block|{}
DECL|method|deleteFile (File file)
specifier|public
specifier|static
name|void
name|deleteFile
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
name|file
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
block|}
DECL|method|readYamlConfigFile (String filename)
specifier|public
specifier|static
name|YamlConfigFile
name|readYamlConfigFile
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|Constructor
name|constructor
init|=
operator|new
name|Constructor
argument_list|(
name|YamlConfigFile
operator|.
name|class
argument_list|)
decl_stmt|;
name|constructor
operator|.
name|setPropertyUtils
argument_list|(
operator|new
name|UnderscoreConverterPropertyUtils
argument_list|()
argument_list|)
expr_stmt|;
name|Yaml
name|yaml
init|=
operator|new
name|Yaml
argument_list|(
name|constructor
argument_list|)
decl_stmt|;
name|InputStream
name|inputStream
init|=
name|YamlConfigTestUtils
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
return|return
name|yaml
operator|.
name|loadAs
argument_list|(
name|inputStream
argument_list|,
name|YamlConfigFile
operator|.
name|class
argument_list|)
return|;
block|}
DECL|method|createTempFileWithContents (String filename)
specifier|public
specifier|static
name|File
name|createTempFileWithContents
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
name|InputStream
name|inputStream
init|=
name|YamlConfigTestUtils
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|filename
argument_list|)
decl_stmt|;
name|File
name|targetFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|".yaml"
argument_list|)
decl_stmt|;
name|FileUtils
operator|.
name|copyInputStreamToFile
argument_list|(
name|inputStream
argument_list|,
name|targetFile
argument_list|)
expr_stmt|;
return|return
name|targetFile
return|;
block|}
DECL|method|createEmptyTempFile ()
specifier|public
specifier|static
name|File
name|createEmptyTempFile
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|File
operator|.
name|createTempFile
argument_list|(
literal|"test"
argument_list|,
literal|".yaml"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

