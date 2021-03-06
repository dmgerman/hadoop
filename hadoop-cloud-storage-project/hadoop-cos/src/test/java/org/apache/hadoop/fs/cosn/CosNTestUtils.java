begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *<p>  * http://www.apache.org/licenses/LICENSE-2.0  *<p>  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.fs.cosn
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|fs
operator|.
name|cosn
package|;
end_package

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
name|lang3
operator|.
name|StringUtils
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
name|internal
operator|.
name|AssumptionViolatedException
import|;
end_import

begin_comment
comment|/**  * Utilities for the CosN tests.  */
end_comment

begin_class
DECL|class|CosNTestUtils
specifier|public
specifier|final
class|class
name|CosNTestUtils
block|{
DECL|method|CosNTestUtils ()
specifier|private
name|CosNTestUtils
parameter_list|()
block|{   }
comment|/**    * Create the file system for test.    *    * @param configuration hadoop's configuration    * @return The file system for test    * @throws IOException If fail to create or initialize the file system.    */
DECL|method|createTestFileSystem ( Configuration configuration)
specifier|public
specifier|static
name|CosNFileSystem
name|createTestFileSystem
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|fsName
init|=
name|configuration
operator|.
name|getTrimmed
argument_list|(
name|CosNTestConfigKey
operator|.
name|TEST_COS_FILESYSTEM_CONF_KEY
argument_list|,
name|CosNTestConfigKey
operator|.
name|DEFAULT_TEST_COS_FILESYSTEM_CONF_VALUE
argument_list|)
decl_stmt|;
name|boolean
name|liveTest
init|=
name|StringUtils
operator|.
name|isNotEmpty
argument_list|(
name|fsName
argument_list|)
decl_stmt|;
name|URI
name|testUri
decl_stmt|;
if|if
condition|(
name|liveTest
condition|)
block|{
name|testUri
operator|=
name|URI
operator|.
name|create
argument_list|(
name|fsName
argument_list|)
expr_stmt|;
name|liveTest
operator|=
name|testUri
operator|.
name|getScheme
argument_list|()
operator|.
name|equals
argument_list|(
name|CosNFileSystem
operator|.
name|SCHEME
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|AssumptionViolatedException
argument_list|(
literal|"no test file system in "
operator|+
name|fsName
argument_list|)
throw|;
block|}
name|CosNFileSystem
name|cosFs
init|=
operator|new
name|CosNFileSystem
argument_list|()
decl_stmt|;
name|cosFs
operator|.
name|initialize
argument_list|(
name|testUri
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
return|return
name|cosFs
return|;
block|}
comment|/**    * Create a dir path for test.    * The value of {@link CosNTestConfigKey#TEST_UNIQUE_FORK_ID_KEY}    * will be used if it is set.    *    * @param defVal default value    * @return The test path    */
DECL|method|createTestPath (Path defVal)
specifier|public
specifier|static
name|Path
name|createTestPath
parameter_list|(
name|Path
name|defVal
parameter_list|)
block|{
name|String
name|testUniqueForkId
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|CosNTestConfigKey
operator|.
name|TEST_UNIQUE_FORK_ID_KEY
argument_list|)
decl_stmt|;
return|return
name|testUniqueForkId
operator|==
literal|null
condition|?
name|defVal
else|:
operator|new
name|Path
argument_list|(
literal|"/"
operator|+
name|testUniqueForkId
argument_list|,
literal|"test"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

