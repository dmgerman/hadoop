begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
DECL|package|org.apache.hadoop.hdfs.tools.offlineEditsViewer
package|package
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|hdfs
operator|.
name|tools
operator|.
name|offlineEditsViewer
package|;
end_package

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
name|DataInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|org
operator|.
name|apache
operator|.
name|hadoop
operator|.
name|classification
operator|.
name|InterfaceAudience
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
name|classification
operator|.
name|InterfaceStability
import|;
end_import

begin_comment
comment|/**  * Tokenizer that reads tokens from a binary file  *  */
end_comment

begin_class
annotation|@
name|InterfaceAudience
operator|.
name|Private
annotation|@
name|InterfaceStability
operator|.
name|Unstable
DECL|class|BinaryTokenizer
specifier|public
class|class
name|BinaryTokenizer
implements|implements
name|Tokenizer
block|{
DECL|field|in
specifier|private
name|DataInputStream
name|in
decl_stmt|;
comment|/**    * BinaryTokenizer constructor    *    * @param filename input filename    */
DECL|method|BinaryTokenizer (String filename)
specifier|public
name|BinaryTokenizer
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|in
operator|=
operator|new
name|DataInputStream
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|filename
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * BinaryTokenizer constructor    *    * @param in input stream    */
DECL|method|BinaryTokenizer (DataInputStream in)
specifier|public
name|BinaryTokenizer
parameter_list|(
name|DataInputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
block|}
comment|/**    * @see org.apache.hadoop.hdfs.tools.offlineEditsViewer.Tokenizer#read    *    * @param t a Token to read    * @return token that was just read    */
annotation|@
name|Override
DECL|method|read (Token t)
specifier|public
name|Token
name|read
parameter_list|(
name|Token
name|t
parameter_list|)
throws|throws
name|IOException
block|{
name|t
operator|.
name|fromBinary
argument_list|(
name|in
argument_list|)
expr_stmt|;
return|return
name|t
return|;
block|}
block|}
end_class

end_unit

