begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.conf
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|conf
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
name|util
operator|.
name|Map
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
name|CommonConfigurationKeys
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_class
DECL|class|TestDeprecatedKeys
specifier|public
class|class
name|TestDeprecatedKeys
extends|extends
name|TestCase
block|{
comment|//Tests a deprecated key
DECL|method|testDeprecatedKeys ()
specifier|public
name|void
name|testDeprecatedKeys
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"topology.script.file.name"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"topology.script.file.name"
argument_list|,
literal|"xyz"
argument_list|)
expr_stmt|;
name|String
name|scriptFile
init|=
name|conf
operator|.
name|get
argument_list|(
name|CommonConfigurationKeys
operator|.
name|NET_TOPOLOGY_SCRIPT_FILE_NAME_KEY
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|scriptFile
operator|.
name|equals
argument_list|(
literal|"xyz"
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//Tests reading / writing a conf file with deprecation after setting
DECL|method|testReadWriteWithDeprecatedKeys ()
specifier|public
name|void
name|testReadWriteWithDeprecatedKeys
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|conf
operator|.
name|setBoolean
argument_list|(
literal|"old.config.yet.to.be.deprecated"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Configuration
operator|.
name|addDeprecation
argument_list|(
literal|"old.config.yet.to.be.deprecated"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"new.conf.to.replace.deprecated.conf"
block|}
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|String
name|fileContents
decl_stmt|;
try|try
block|{
name|conf
operator|.
name|writeXml
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|fileContents
operator|=
name|out
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"old.config.yet.to.be.deprecated"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|fileContents
operator|.
name|contains
argument_list|(
literal|"new.conf.to.replace.deprecated.conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testIteratorWithDeprecatedKeysMappedToMultipleNewKeys ()
specifier|public
name|void
name|testIteratorWithDeprecatedKeysMappedToMultipleNewKeys
parameter_list|()
block|{
name|Configuration
name|conf
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|Configuration
operator|.
name|addDeprecation
argument_list|(
literal|"dK"
argument_list|,
operator|new
name|String
index|[]
block|{
literal|"nK1"
block|,
literal|"nK2"
block|}
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"k"
argument_list|,
literal|"v"
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"dK"
argument_list|,
literal|"V"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"V"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"dK"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"V"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"nK1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"V"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"nK2"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"nK1"
argument_list|,
literal|"VV"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"VV"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"dK"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"VV"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"nK1"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"VV"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"nK2"
argument_list|)
argument_list|)
expr_stmt|;
name|conf
operator|.
name|set
argument_list|(
literal|"nK2"
argument_list|,
literal|"VVV"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"VVV"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"dK"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"VVV"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"nK2"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"VVV"
argument_list|,
name|conf
operator|.
name|get
argument_list|(
literal|"nK1"
argument_list|)
argument_list|)
expr_stmt|;
name|boolean
name|kFound
init|=
literal|false
decl_stmt|;
name|boolean
name|dKFound
init|=
literal|false
decl_stmt|;
name|boolean
name|nK1Found
init|=
literal|false
decl_stmt|;
name|boolean
name|nK2Found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|conf
control|)
block|{
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"k"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"v"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|kFound
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"dK"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"VVV"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|dKFound
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"nK1"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"VVV"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|nK1Found
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|equals
argument_list|(
literal|"nK2"
argument_list|)
condition|)
block|{
name|assertEquals
argument_list|(
literal|"VVV"
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|nK2Found
operator|=
literal|true
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
literal|"regular Key not found"
argument_list|,
name|kFound
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"deprecated Key not found"
argument_list|,
name|dKFound
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new Key 1 not found"
argument_list|,
name|nK1Found
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"new Key 2 not found"
argument_list|,
name|nK2Found
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

