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
comment|/**  * TokenizerFactory for different implementations of Tokenizer  *  */
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
DECL|class|TokenizerFactory
specifier|public
class|class
name|TokenizerFactory
block|{
comment|/**    * Factory function that creates a Tokenizer object, the input format    * is set based on filename (*.xml is XML, otherwise binary)    *    * @param filename input filename    */
DECL|method|getTokenizer (String filename)
specifier|static
specifier|public
name|Tokenizer
name|getTokenizer
parameter_list|(
name|String
name|filename
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|filename
operator|.
name|toLowerCase
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|"xml"
argument_list|)
condition|)
block|{
return|return
operator|new
name|XmlTokenizer
argument_list|(
name|filename
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|new
name|BinaryTokenizer
argument_list|(
name|filename
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

