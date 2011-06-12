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
name|FileOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutputStream
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
comment|/**  * BinaryEditsVisitor implements a binary EditsVisitor  */
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
DECL|class|BinaryEditsVisitor
specifier|public
class|class
name|BinaryEditsVisitor
extends|extends
name|EditsVisitor
block|{
DECL|field|out
specifier|final
specifier|private
name|DataOutputStream
name|out
decl_stmt|;
comment|/**    * Create a processor that writes to a given file and    * reads using a given Tokenizer    *    * @param filename Name of file to write output to    * @param tokenizer Input tokenizer    */
DECL|method|BinaryEditsVisitor (String filename, Tokenizer tokenizer)
specifier|public
name|BinaryEditsVisitor
parameter_list|(
name|String
name|filename
parameter_list|,
name|Tokenizer
name|tokenizer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
argument_list|(
name|filename
argument_list|,
name|tokenizer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a processor that writes to a given file and reads using    * a given Tokenizer, may also print to screen    *    * @param filename Name of file to write output to    * @param tokenizer Input tokenizer    * @param printToScreen Mirror output to screen? (ignored for binary)    */
DECL|method|BinaryEditsVisitor (String filename, Tokenizer tokenizer, boolean printToScreen)
specifier|public
name|BinaryEditsVisitor
parameter_list|(
name|String
name|filename
parameter_list|,
name|Tokenizer
name|tokenizer
parameter_list|,
name|boolean
name|printToScreen
parameter_list|)
throws|throws
name|IOException
block|{
name|super
argument_list|(
name|tokenizer
argument_list|)
expr_stmt|;
name|out
operator|=
operator|new
name|DataOutputStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|filename
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start the visitor (initialization)    */
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do for binary format
block|}
comment|/**    * Finish the visitor    */
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Finish the visitor and indicate an error    */
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Error processing EditLog file.  Exiting."
argument_list|)
expr_stmt|;
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Close output stream and prevent further writing    */
DECL|method|close ()
specifier|private
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|/**    * Visit a enclosing element (element that has other elements in it)    */
annotation|@
name|Override
DECL|method|visitEnclosingElement (Tokenizer.Token value)
name|void
name|visitEnclosingElement
parameter_list|(
name|Tokenizer
operator|.
name|Token
name|value
parameter_list|)
throws|throws
name|IOException
block|{
comment|// nothing to do for binary format
block|}
comment|/**    * End of eclosing element    */
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{
comment|// nothing to do for binary format
block|}
comment|/**    * Visit a Token    */
annotation|@
name|Override
DECL|method|visit (Tokenizer.Token value)
name|Tokenizer
operator|.
name|Token
name|visit
parameter_list|(
name|Tokenizer
operator|.
name|Token
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|value
operator|.
name|toBinary
argument_list|(
name|out
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

