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
name|java
operator|.
name|util
operator|.
name|LinkedList
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
name|hdfs
operator|.
name|tools
operator|.
name|offlineImageViewer
operator|.
name|DepthCounter
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
comment|/**  * An XmlEditsVisitor walks over an EditLog structure and writes out  * an equivalent XML document that contains the EditLog's components.  */
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
DECL|class|XmlEditsVisitor
specifier|public
class|class
name|XmlEditsVisitor
extends|extends
name|TextEditsVisitor
block|{
DECL|field|tagQ
specifier|final
specifier|private
name|LinkedList
argument_list|<
name|EditsElement
argument_list|>
name|tagQ
init|=
operator|new
name|LinkedList
argument_list|<
name|EditsElement
argument_list|>
argument_list|()
decl_stmt|;
DECL|field|depthCounter
specifier|final
specifier|private
name|DepthCounter
name|depthCounter
init|=
operator|new
name|DepthCounter
argument_list|()
decl_stmt|;
comment|/**    * Create a processor that writes to the file named and may or may not    * also output to the screen, as specified.    *    * @param filename Name of file to write output to    * @param tokenizer Input tokenizer    */
DECL|method|XmlEditsVisitor (String filename, Tokenizer tokenizer)
specifier|public
name|XmlEditsVisitor
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
name|super
argument_list|(
name|filename
argument_list|,
name|tokenizer
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**    * Create a processor that writes to the file named and may or may not    * also output to the screen, as specified.    *    * @param filename Name of file to write output to    * @param tokenizer Input tokenizer    * @param printToScreen Mirror output to screen? (ignored for binary)    */
DECL|method|XmlEditsVisitor (String filename, Tokenizer tokenizer, boolean printToScreen)
specifier|public
name|XmlEditsVisitor
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
name|filename
argument_list|,
name|tokenizer
argument_list|,
name|printToScreen
argument_list|)
expr_stmt|;
block|}
comment|/**    * Start visitor (initialization)    */
annotation|@
name|Override
DECL|method|start ()
name|void
name|start
parameter_list|()
throws|throws
name|IOException
block|{
name|write
argument_list|(
literal|"<?xml version=\"1.0\"?>\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish visitor    */
annotation|@
name|Override
DECL|method|finish ()
name|void
name|finish
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
comment|/**    * Finish with error    */
annotation|@
name|Override
DECL|method|finishAbnormally ()
name|void
name|finishAbnormally
parameter_list|()
throws|throws
name|IOException
block|{
name|write
argument_list|(
literal|"\n<!-- Error processing EditLog file.  Exiting -->\n"
argument_list|)
expr_stmt|;
name|super
operator|.
name|finishAbnormally
argument_list|()
expr_stmt|;
block|}
comment|/**    * Visit a Token    *    * @param value a Token to visit    */
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
name|writeTag
argument_list|(
name|value
operator|.
name|getEditsElement
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|value
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|value
return|;
block|}
comment|/**    * Visit an enclosing element (element that cntains other elements)    *    * @param value a Token to visit    */
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
name|printIndents
argument_list|()
expr_stmt|;
name|write
argument_list|(
literal|"<"
operator|+
name|value
operator|.
name|getEditsElement
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
name|tagQ
operator|.
name|push
argument_list|(
name|value
operator|.
name|getEditsElement
argument_list|()
argument_list|)
expr_stmt|;
name|depthCounter
operator|.
name|incLevel
argument_list|()
expr_stmt|;
block|}
comment|/**    * Leave enclosing element    */
annotation|@
name|Override
DECL|method|leaveEnclosingElement ()
name|void
name|leaveEnclosingElement
parameter_list|()
throws|throws
name|IOException
block|{
name|depthCounter
operator|.
name|decLevel
argument_list|()
expr_stmt|;
if|if
condition|(
name|tagQ
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Tried to exit non-existent enclosing element "
operator|+
literal|"in EditLog file"
argument_list|)
throw|;
name|EditsElement
name|element
init|=
name|tagQ
operator|.
name|pop
argument_list|()
decl_stmt|;
name|printIndents
argument_list|()
expr_stmt|;
name|write
argument_list|(
literal|"</"
operator|+
name|element
operator|.
name|toString
argument_list|()
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
block|}
comment|/**    * Write an XML tag    *    * @param tag a tag name    * @param value a tag value    */
DECL|method|writeTag (String tag, String value)
specifier|private
name|void
name|writeTag
parameter_list|(
name|String
name|tag
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
name|printIndents
argument_list|()
expr_stmt|;
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|write
argument_list|(
literal|"<"
operator|+
name|tag
operator|+
literal|">"
operator|+
name|value
operator|+
literal|"</"
operator|+
name|tag
operator|+
literal|">\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|write
argument_list|(
literal|"<"
operator|+
name|tag
operator|+
literal|"/>\n"
argument_list|)
expr_stmt|;
block|}
block|}
comment|// prepared values that printIndents is likely to use
DECL|field|indents
specifier|final
specifier|private
specifier|static
name|String
index|[]
name|indents
init|=
block|{
literal|""
block|,
literal|"  "
block|,
literal|"    "
block|,
literal|"      "
block|,
literal|"        "
block|,
literal|"          "
block|,
literal|"            "
block|}
decl_stmt|;
comment|/**    * Prints the leading spaces based on depth level    */
DECL|method|printIndents ()
specifier|private
name|void
name|printIndents
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|write
argument_list|(
name|indents
index|[
name|depthCounter
operator|.
name|getLevel
argument_list|()
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IndexOutOfBoundsException
name|e
parameter_list|)
block|{
comment|// unlikely needed so can be slow
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|depthCounter
operator|.
name|getLevel
argument_list|()
condition|;
name|i
operator|++
control|)
name|write
argument_list|(
literal|"  "
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

