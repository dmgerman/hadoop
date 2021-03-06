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
name|io
operator|.
name|file
operator|.
name|tfile
operator|.
name|Compression
operator|.
name|Algorithm
import|;
end_import

begin_class
DECL|class|TestTFileLzoCodecsStreams
specifier|public
class|class
name|TestTFileLzoCodecsStreams
extends|extends
name|TestTFileStreams
block|{
comment|/**    * Test LZO compression codec, using the same test cases as in the ByteArrays.    */
annotation|@
name|Override
DECL|method|setUp ()
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|IOException
block|{
name|skip
operator|=
operator|!
operator|(
name|Algorithm
operator|.
name|LZO
operator|.
name|isSupported
argument_list|()
operator|)
expr_stmt|;
if|if
condition|(
name|skip
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Skipped"
argument_list|)
expr_stmt|;
block|}
name|init
argument_list|(
name|Compression
operator|.
name|Algorithm
operator|.
name|LZO
operator|.
name|getName
argument_list|()
argument_list|,
literal|"memcmp"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|skip
condition|)
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

