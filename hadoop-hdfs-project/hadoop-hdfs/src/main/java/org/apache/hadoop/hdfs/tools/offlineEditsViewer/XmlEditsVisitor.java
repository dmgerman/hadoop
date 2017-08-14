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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|SAXTransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
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
name|util
operator|.
name|XMLUtils
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
name|server
operator|.
name|namenode
operator|.
name|FSEditLogOp
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
implements|implements
name|OfflineEditsVisitor
block|{
DECL|field|out
specifier|private
specifier|final
name|OutputStream
name|out
decl_stmt|;
DECL|field|contentHandler
specifier|private
name|ContentHandler
name|contentHandler
decl_stmt|;
DECL|field|factory
specifier|private
specifier|final
name|SAXTransformerFactory
name|factory
decl_stmt|;
DECL|field|XML_INDENTATION_PROP
specifier|private
specifier|final
specifier|static
name|String
name|XML_INDENTATION_PROP
init|=
literal|"{http://xml.apache.org/"
operator|+
literal|"xslt}indent-amount"
decl_stmt|;
DECL|field|XML_INDENTATION_NUM
specifier|private
specifier|final
specifier|static
name|String
name|XML_INDENTATION_NUM
init|=
literal|"2"
decl_stmt|;
comment|/**    * Create a processor that writes to the file named and may or may not    * also output to the screen, as specified.    *    * @param out output stream to write    * @throws IOException on any error    */
DECL|method|XmlEditsVisitor (OutputStream out)
specifier|public
name|XmlEditsVisitor
parameter_list|(
name|OutputStream
name|out
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|factory
operator|=
operator|(
name|SAXTransformerFactory
operator|)
name|SAXTransformerFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
try|try
block|{
name|TransformerHandler
name|handler
init|=
name|factory
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|XML_INDENTATION_PROP
argument_list|,
name|XML_INDENTATION_NUM
argument_list|)
expr_stmt|;
name|handler
operator|.
name|getTransformer
argument_list|()
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|STANDALONE
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|handler
operator|.
name|setResult
argument_list|(
operator|new
name|StreamResult
argument_list|(
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|contentHandler
operator|=
name|handler
expr_stmt|;
name|contentHandler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|contentHandler
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|"EDITS"
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SAXTransformer error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SAX error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**    * Start visitor (initialization)    */
annotation|@
name|Override
DECL|method|start (int version)
specifier|public
name|void
name|start
parameter_list|(
name|int
name|version
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|contentHandler
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|"EDITS_VERSION"
argument_list|,
operator|new
name|AttributesImpl
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|bld
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|bld
operator|.
name|append
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|addString
argument_list|(
name|bld
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|"EDITS_VERSION"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SAX error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
DECL|method|addString (String str)
specifier|public
name|void
name|addString
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|SAXException
block|{
name|int
name|slen
init|=
name|str
operator|.
name|length
argument_list|()
decl_stmt|;
name|char
name|arr
index|[]
init|=
operator|new
name|char
index|[
name|slen
index|]
decl_stmt|;
name|str
operator|.
name|getChars
argument_list|(
literal|0
argument_list|,
name|slen
argument_list|,
name|arr
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|contentHandler
operator|.
name|characters
argument_list|(
name|arr
argument_list|,
literal|0
argument_list|,
name|slen
argument_list|)
expr_stmt|;
block|}
comment|/**    * Finish visitor    */
annotation|@
name|Override
DECL|method|close (Throwable error)
specifier|public
name|void
name|close
parameter_list|(
name|Throwable
name|error
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|contentHandler
operator|.
name|endElement
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|,
literal|"EDITS"
argument_list|)
expr_stmt|;
if|if
condition|(
name|error
operator|!=
literal|null
condition|)
block|{
name|String
name|msg
init|=
name|error
operator|.
name|getMessage
argument_list|()
decl_stmt|;
name|XMLUtils
operator|.
name|addSaxString
argument_list|(
name|contentHandler
argument_list|,
literal|"ERROR"
argument_list|,
operator|(
name|msg
operator|==
literal|null
operator|)
condition|?
literal|"null"
else|:
name|msg
argument_list|)
expr_stmt|;
block|}
name|contentHandler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SAX error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
name|out
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|visitOp (FSEditLogOp op)
specifier|public
name|void
name|visitOp
parameter_list|(
name|FSEditLogOp
name|op
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|op
operator|.
name|outputToXml
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"SAX error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

