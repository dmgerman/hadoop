begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.io.file.tfile
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|io
operator|.
name|file
operator|.
name|tfile
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_class
DECL|class|TestCompression
specifier|public
class|class
name|TestCompression
block|{
comment|/**    * Regression test for HADOOP-11418.    * Verify we can set a LZO codec different from default LZO codec.    */
annotation|@
name|Test
DECL|method|testConfigureLZOCodec ()
specifier|public
name|void
name|testConfigureLZOCodec
parameter_list|()
throws|throws
name|IOException
block|{
comment|// Dummy codec
name|String
name|defaultCodec
init|=
literal|"org.apache.hadoop.io.compress.DefaultCodec"
decl_stmt|;
name|Compression
operator|.
name|Algorithm
operator|.
name|conf
operator|.
name|set
argument_list|(
name|Compression
operator|.
name|Algorithm
operator|.
name|CONF_LZO_CLASS
argument_list|,
name|defaultCodec
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|defaultCodec
argument_list|,
name|Compression
operator|.
name|Algorithm
operator|.
name|LZO
operator|.
name|getCodec
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

