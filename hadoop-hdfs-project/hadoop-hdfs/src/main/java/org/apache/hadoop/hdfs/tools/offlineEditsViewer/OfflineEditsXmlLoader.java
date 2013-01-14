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
name|File
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
operator|.
name|InvalidXmlException
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
name|hdfs
operator|.
name|server
operator|.
name|namenode
operator|.
name|FSEditLogOpCodes
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
operator|.
name|OpInstanceCache
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
name|offlineEditsViewer
operator|.
name|OfflineEditsViewer
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
operator|.
name|Stanza
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
name|Attributes
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
name|InputSource
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
name|SAXParseException
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
name|XMLReader
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
name|DefaultHandler
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
name|XMLReaderFactory
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Charsets
import|;
end_import

begin_comment
comment|/**  * OfflineEditsXmlLoader walks an EditsVisitor over an OEV XML file  */
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
DECL|class|OfflineEditsXmlLoader
class|class
name|OfflineEditsXmlLoader
extends|extends
name|DefaultHandler
implements|implements
name|OfflineEditsLoader
block|{
DECL|field|fixTxIds
specifier|private
specifier|final
name|boolean
name|fixTxIds
decl_stmt|;
DECL|field|visitor
specifier|private
specifier|final
name|OfflineEditsVisitor
name|visitor
decl_stmt|;
DECL|field|fileReader
specifier|private
specifier|final
name|InputStreamReader
name|fileReader
decl_stmt|;
DECL|field|state
specifier|private
name|ParseState
name|state
decl_stmt|;
DECL|field|stanza
specifier|private
name|Stanza
name|stanza
decl_stmt|;
DECL|field|stanzaStack
specifier|private
name|Stack
argument_list|<
name|Stanza
argument_list|>
name|stanzaStack
decl_stmt|;
DECL|field|opCode
specifier|private
name|FSEditLogOpCodes
name|opCode
decl_stmt|;
DECL|field|cbuf
specifier|private
name|StringBuffer
name|cbuf
decl_stmt|;
DECL|field|nextTxId
specifier|private
name|long
name|nextTxId
decl_stmt|;
DECL|field|opCache
specifier|private
specifier|final
name|OpInstanceCache
name|opCache
init|=
operator|new
name|OpInstanceCache
argument_list|()
decl_stmt|;
DECL|enum|ParseState
specifier|static
enum|enum
name|ParseState
block|{
DECL|enumConstant|EXPECT_EDITS_TAG
name|EXPECT_EDITS_TAG
block|,
DECL|enumConstant|EXPECT_VERSION
name|EXPECT_VERSION
block|,
DECL|enumConstant|EXPECT_RECORD
name|EXPECT_RECORD
block|,
DECL|enumConstant|EXPECT_OPCODE
name|EXPECT_OPCODE
block|,
DECL|enumConstant|EXPECT_DATA
name|EXPECT_DATA
block|,
DECL|enumConstant|HANDLE_DATA
name|HANDLE_DATA
block|,
DECL|enumConstant|EXPECT_END
name|EXPECT_END
block|,   }
DECL|method|OfflineEditsXmlLoader (OfflineEditsVisitor visitor, File inputFile, OfflineEditsViewer.Flags flags)
specifier|public
name|OfflineEditsXmlLoader
parameter_list|(
name|OfflineEditsVisitor
name|visitor
parameter_list|,
name|File
name|inputFile
parameter_list|,
name|OfflineEditsViewer
operator|.
name|Flags
name|flags
parameter_list|)
throws|throws
name|FileNotFoundException
block|{
name|this
operator|.
name|visitor
operator|=
name|visitor
expr_stmt|;
name|this
operator|.
name|fileReader
operator|=
operator|new
name|InputStreamReader
argument_list|(
operator|new
name|FileInputStream
argument_list|(
name|inputFile
argument_list|)
argument_list|,
name|Charsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|this
operator|.
name|fixTxIds
operator|=
name|flags
operator|.
name|getFixTxIds
argument_list|()
expr_stmt|;
block|}
comment|/**    * Loads edits file, uses visitor to process all elements    */
annotation|@
name|Override
DECL|method|loadEdits ()
specifier|public
name|void
name|loadEdits
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
name|XMLReader
name|xr
init|=
name|XMLReaderFactory
operator|.
name|createXMLReader
argument_list|()
decl_stmt|;
name|xr
operator|.
name|setContentHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setErrorHandler
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|xr
operator|.
name|setDTDHandler
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|xr
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
name|fileReader
argument_list|)
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|close
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXParseException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"XML parsing error: "
operator|+
literal|"\n"
operator|+
literal|"Line:    "
operator|+
name|e
operator|.
name|getLineNumber
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"URI:     "
operator|+
name|e
operator|.
name|getSystemId
argument_list|()
operator|+
literal|"\n"
operator|+
literal|"Message: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|visitor
operator|.
name|close
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|toString
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
name|visitor
operator|.
name|close
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|visitor
operator|.
name|close
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|fileReader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
DECL|method|startDocument ()
specifier|public
name|void
name|startDocument
parameter_list|()
block|{
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_EDITS_TAG
expr_stmt|;
name|stanza
operator|=
literal|null
expr_stmt|;
name|stanzaStack
operator|=
operator|new
name|Stack
argument_list|<
name|Stanza
argument_list|>
argument_list|()
expr_stmt|;
name|opCode
operator|=
literal|null
expr_stmt|;
name|cbuf
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
name|nextTxId
operator|=
operator|-
literal|1
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|endDocument ()
specifier|public
name|void
name|endDocument
parameter_list|()
block|{
if|if
condition|(
name|state
operator|!=
name|ParseState
operator|.
name|EXPECT_END
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expecting</EDITS>"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|startElement (String uri, String name, String qName, Attributes atts)
specifier|public
name|void
name|startElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|qName
parameter_list|,
name|Attributes
name|atts
parameter_list|)
block|{
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|EXPECT_EDITS_TAG
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"EDITS"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"you must put "
operator|+
literal|"<EDITS> at the top of the XML file! "
operator|+
literal|"Got tag "
operator|+
name|name
operator|+
literal|" instead"
argument_list|)
throw|;
block|}
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_VERSION
expr_stmt|;
break|break;
case|case
name|EXPECT_VERSION
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"EDITS_VERSION"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"you must put "
operator|+
literal|"<EDITS_VERSION> at the top of the XML file! "
operator|+
literal|"Got tag "
operator|+
name|name
operator|+
literal|" instead"
argument_list|)
throw|;
block|}
break|break;
case|case
name|EXPECT_RECORD
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"RECORD"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected a<RECORD> tag"
argument_list|)
throw|;
block|}
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_OPCODE
expr_stmt|;
break|break;
case|case
name|EXPECT_OPCODE
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"OPCODE"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected an<OPCODE> tag"
argument_list|)
throw|;
block|}
break|break;
case|case
name|EXPECT_DATA
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"DATA"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected a<DATA> tag"
argument_list|)
throw|;
block|}
name|stanza
operator|=
operator|new
name|Stanza
argument_list|()
expr_stmt|;
name|state
operator|=
name|ParseState
operator|.
name|HANDLE_DATA
expr_stmt|;
break|break;
case|case
name|HANDLE_DATA
case|:
name|Stanza
name|parent
init|=
name|stanza
decl_stmt|;
name|Stanza
name|child
init|=
operator|new
name|Stanza
argument_list|()
decl_stmt|;
name|stanzaStack
operator|.
name|push
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|stanza
operator|=
name|child
expr_stmt|;
name|parent
operator|.
name|addChild
argument_list|(
name|name
argument_list|,
name|child
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXPECT_END
case|:
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"not expecting anything after</EDITS>"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|endElement (String uri, String name, String qName)
specifier|public
name|void
name|endElement
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|qName
parameter_list|)
block|{
name|String
name|str
init|=
name|cbuf
operator|.
name|toString
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
name|cbuf
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
switch|switch
condition|(
name|state
condition|)
block|{
case|case
name|EXPECT_EDITS_TAG
case|:
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected<EDITS/>"
argument_list|)
throw|;
case|case
name|EXPECT_VERSION
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"EDITS_VERSION"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected</EDITS_VERSION>"
argument_list|)
throw|;
block|}
try|try
block|{
name|int
name|version
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|str
argument_list|)
decl_stmt|;
name|visitor
operator|.
name|start
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Can't throw IOException from a SAX method, sigh.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_RECORD
expr_stmt|;
break|break;
case|case
name|EXPECT_RECORD
case|:
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|"EDITS"
argument_list|)
condition|)
block|{
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_END
expr_stmt|;
block|}
elseif|else
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"RECORD"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected</EDITS> or</RECORD>"
argument_list|)
throw|;
block|}
break|break;
case|case
name|EXPECT_OPCODE
case|:
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"OPCODE"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected</OPCODE>"
argument_list|)
throw|;
block|}
name|opCode
operator|=
name|FSEditLogOpCodes
operator|.
name|valueOf
argument_list|(
name|str
argument_list|)
expr_stmt|;
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_DATA
expr_stmt|;
break|break;
case|case
name|EXPECT_DATA
case|:
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected<DATA/>"
argument_list|)
throw|;
case|case
name|HANDLE_DATA
case|:
name|stanza
operator|.
name|setValue
argument_list|(
name|str
argument_list|)
expr_stmt|;
if|if
condition|(
name|stanzaStack
operator|.
name|empty
argument_list|()
condition|)
block|{
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
literal|"DATA"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"expected</DATA>"
argument_list|)
throw|;
block|}
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_RECORD
expr_stmt|;
name|FSEditLogOp
name|op
init|=
name|opCache
operator|.
name|get
argument_list|(
name|opCode
argument_list|)
decl_stmt|;
name|opCode
operator|=
literal|null
expr_stmt|;
try|try
block|{
name|op
operator|.
name|decodeXml
argument_list|(
name|stanza
argument_list|)
expr_stmt|;
name|stanza
operator|=
literal|null
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|stanza
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"fromXml error decoding opcode "
operator|+
name|opCode
operator|+
literal|"\n"
operator|+
name|stanza
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stanza
operator|=
literal|null
expr_stmt|;
block|}
block|}
if|if
condition|(
name|fixTxIds
condition|)
block|{
if|if
condition|(
name|nextTxId
operator|<=
literal|0
condition|)
block|{
name|nextTxId
operator|=
name|op
operator|.
name|getTransactionId
argument_list|()
expr_stmt|;
if|if
condition|(
name|nextTxId
operator|<=
literal|0
condition|)
block|{
name|nextTxId
operator|=
literal|1
expr_stmt|;
block|}
block|}
name|op
operator|.
name|setTransactionId
argument_list|(
name|nextTxId
argument_list|)
expr_stmt|;
name|nextTxId
operator|++
expr_stmt|;
block|}
try|try
block|{
name|visitor
operator|.
name|visitOp
argument_list|(
name|op
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// Can't throw IOException from a SAX method, sigh.
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|state
operator|=
name|ParseState
operator|.
name|EXPECT_RECORD
expr_stmt|;
block|}
else|else
block|{
name|stanza
operator|=
name|stanzaStack
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
break|break;
case|case
name|EXPECT_END
case|:
throw|throw
operator|new
name|InvalidXmlException
argument_list|(
literal|"not expecting anything after</EDITS>"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
DECL|method|characters (char ch[], int start, int length)
specifier|public
name|void
name|characters
parameter_list|(
name|char
name|ch
index|[]
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|length
parameter_list|)
block|{
name|cbuf
operator|.
name|append
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

